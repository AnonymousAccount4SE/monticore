/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import de.monticore.ast.ASTNode;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesException;
import de.monticore.symboltable.resolving.ResolvingFilter;
import de.monticore.symboltable.resolving.ResolvingInfo;
import de.monticore.symboltable.visibility.IsShadowedBySymbol;
import de.se_rwth.commons.Splitters;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Predicate;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.ImmutableList.copyOf;
import static de.monticore.symboltable.Scopes.getLocalSymbolsAsCollection;
import static de.monticore.symboltable.modifiers.AccessModifier.ALL_INCLUSION;
import static de.monticore.symboltable.resolving.ResolvingFilter.getFiltersForTargetKind;
import static de.se_rwth.commons.Joiners.DOT;
import static de.se_rwth.commons.logging.Log.*;
import static java.util.Collections.emptySet;
import static java.util.Optional.*;
import static java.util.stream.Collectors.toSet;

public class CommonScope implements MutableScope {

  private final Map<String, Collection<Symbol>> symbols = new LinkedHashMap<>();
  private final List<MutableScope> subScopes = new ArrayList<>();

  private Boolean exportsSymbols = null;
  private Boolean isShadowingScope;

  private String name;

  protected MutableScope enclosingScope;

  private ScopeSpanningSymbol spanningSymbol;
  private ASTNode astNode;
  private Set<ResolvingFilter<? extends Symbol>> resolvingFilters = new LinkedHashSet<>();


  public CommonScope() {

  }

  public CommonScope(boolean isShadowingScope) {
    this(empty(), isShadowingScope);
  }

  public CommonScope(Optional<MutableScope> enclosingScope) {
    this(enclosingScope, false);
  }

  public CommonScope(Optional<MutableScope> enclosingScope, boolean isShadowingScope) {
    errorIfNull(enclosingScope);

    this.isShadowingScope = isShadowingScope;

    if (enclosingScope.isPresent()) {
      setEnclosingScope(enclosingScope.get());
    }
  }

  public void add(Symbol symbol) {
    errorIfNull(symbol);

    final String symbolName = symbol.getName();
    if (!symbols.containsKey(symbolName)) {
      symbols.put(symbolName, new ArrayList<>());
    }

    symbols.get(symbolName).add(symbol);

    symbol.setEnclosingScope(this);
  }

  public void addResolver(ResolvingFilter<? extends Symbol> resolvingFilter) {
    this.resolvingFilters.add(resolvingFilter);
  }


  public void addSubScope(MutableScope subScope) {
    if (!subScopes.contains(subScope)) {
      subScopes.add(subScope);
      subScope.setEnclosingScope(this);
    }
  }


  /**
   * Continues (top-down) resolving with this sub scope
   *
   * @param resolvingInfo contains resolving information, such as, the already involved scopes.
   * @param symbolName    the name of the searched symbol
   * @param kind          the kind of the searched symbol
   */
  @Override
  public <T extends Symbol> Collection<T> continueAsSubScope(ResolvingInfo resolvingInfo,
                                                             String symbolName, SymbolKind kind, AccessModifier modifier, Predicate<Symbol> predicate) {
    if (checkIfContinueAsSubScope(symbolName, kind)) {
      final String remainingSymbolName = getRemainingNameForResolveDown(symbolName);

      return this.resolveDownMany(resolvingInfo, remainingSymbolName, kind, modifier, predicate);
    }

    return emptySet();
  }

  @Override
  public boolean exportsSymbols() {
    if (exportsSymbols == null) {
      return getName().isPresent();
    }

    return exportsSymbols;
  }

  @Override
  public MutableScope getAsMutableScope() {
    return this;
  }

  @Override
  public Optional<ASTNode> getAstNode() {
    return ofNullable(astNode);
  }


  @Override
  public Optional<MutableScope> getEnclosingScope() {
    return ofNullable(enclosingScope);
  }

  @Override
  public Map<String, Collection<Symbol>> getLocalSymbols() {
    return ImmutableMap.copyOf(symbols);
  }

  @Override
  public Optional<String> getName() {
    if (!isNullOrEmpty(name)) {
      return of(name);
    }

    if (getSpanningSymbol().isPresent()) {
      return of(getSpanningSymbol().get().getName());
    }

    return empty();
  }

  @Override
  public Set<ResolvingFilter<? extends Symbol>> getResolvingFilters() {
    return ImmutableSet.copyOf(resolvingFilters);
  }

  @Override
  public Optional<? extends ScopeSpanningSymbol> getSpanningSymbol() {
    return ofNullable(spanningSymbol);
  }

  @Override
  public List<MutableScope> getSubScopes() {
    return copyOf(subScopes);
  }

  @Override
  public int getSymbolsSize() {
    int size = 0;
    for (Entry<String, Collection<Symbol>> entry : symbols.entrySet()) {
      size += entry.getValue().size();
    }
    return size;
  }

  @Override
  public boolean isShadowingScope() {
    if (isShadowingScope == null) {
      return getName().isPresent();
    }
    return isShadowingScope;
  }

  @Override
  public boolean isSpannedBySymbol() {
    return getSpanningSymbol().isPresent();
  }

  @Override
  public void remove(Symbol symbol) {
    if (symbols.containsKey(symbol.getName())) {
      final boolean symbolRemoved = symbols.get(symbol.getName()).remove(symbol);
      if (symbolRemoved) {
        symbol.setEnclosingScope(null);
      }
    }
  }

  /**
   * Removes the sub scope <code>subScope</code>.
   *
   * @param subScope the sub scope to be removed
   */
  public void removeSubScope(MutableScope subScope) {
    if (subScopes.contains(subScope)) {
      subScopes.remove(subScope);
      subScope.setEnclosingScope(null);
    }
  }

  public <T extends Symbol> Optional<T> resolve(ResolvingInfo resolvingInfo, String name, SymbolKind kind, AccessModifier modifier) {
    return getResolvedOrThrowException(resolveMany(resolvingInfo, name, kind, modifier));
  }

  @Override
  public <T extends Symbol> Optional<T> resolve(String symbolName, SymbolKind kind) {
    return getResolvedOrThrowException(resolveMany(symbolName, kind));
  }

  @Override
  public <T extends Symbol> Optional<T> resolve(String name, SymbolKind kind, AccessModifier modifier) {
    return getResolvedOrThrowException(resolveMany(name, kind, modifier));
  }

  @Override
  public <T extends Symbol> Optional<T> resolve(String name, SymbolKind kind, AccessModifier modifier, Predicate<Symbol> predicate) {
    return getResolvedOrThrowException(resolveMany(name, kind, modifier, predicate));
  }


  /**
   * @see MutableScope#resolveDown(String, SymbolKind)
   */
  @Override
  public <T extends Symbol> Optional<T> resolveDown(String name, SymbolKind kind) {
    return getResolvedOrThrowException(this.resolveDownMany(name, kind));
  }

  @Override
  public <T extends Symbol> Optional<T> resolveDown(String name, SymbolKind kind, AccessModifier modifier) {
    return getResolvedOrThrowException(resolveDownMany(name, kind, modifier));
  }

  @Override
  public <T extends Symbol> Optional<T> resolveDown(String name, SymbolKind kind, AccessModifier modifier, Predicate<Symbol> predicate) {
    return getResolvedOrThrowException(resolveDownMany(name, kind, modifier, predicate));
  }

  @Override
  public <T extends Symbol> Collection<T> resolveDownMany(ResolvingInfo resolvingInfo, String name, SymbolKind kind, AccessModifier modifier,
                                                          Predicate<Symbol> predicate) {
    // 1. Conduct search locally in the current scope
    final Set<T> resolved = this.resolveManyLocally(resolvingInfo, name, kind, modifier, predicate);

    final String resolveCall = "resolveDownMany(\"" + name + "\", \"" + kind.getName()
            + "\") in scope \"" + getName() + "\"";
    trace("START " + resolveCall + ". Found #" + resolved.size() + " (local)", "");
    // If no matching symbols have been found...
    if (resolved.isEmpty()) {
      // 2. Continue search in sub scopes and ...
      for (MutableScope subScope : getSubScopes()) {
        final Collection<T> resolvedFromSub = subScope.continueAsSubScope(resolvingInfo, name, kind, modifier, predicate);
        // 3. unify results
        resolved.addAll(resolvedFromSub);
      }
    }
    trace("END " + resolveCall + ". Found #" + resolved.size(), "");

    return resolved;
  }

  @Override
  public <T extends Symbol> Collection<T> resolveDownMany(String name, SymbolKind kind) {
    return this.resolveDownMany(new ResolvingInfo(getResolvingFilters()), name, kind, ALL_INCLUSION, x -> true);
  }

  @Override
  public <T extends Symbol> Collection<T> resolveDownMany(String name, SymbolKind kind, AccessModifier modifier) {
    return resolveDownMany(new ResolvingInfo(getResolvingFilters()), name, kind, modifier, x -> true);
  }

  @Override
  public <T extends Symbol> Collection<T> resolveDownMany(String name, SymbolKind kind, AccessModifier modifier, Predicate<Symbol> predicate) {
    return resolveDownMany(new ResolvingInfo(getResolvingFilters()), name, kind, modifier, predicate);
  }

  @Override
  public <T extends Symbol> Optional<T> resolveImported(String name, SymbolKind kind, AccessModifier modifier) {
    return this.resolveLocally(name, kind);
  }

  @Override
  public <T extends Symbol> Optional<T> resolveLocally(String name, SymbolKind kind) {
    return getResolvedOrThrowException(
            this.<T>resolveManyLocally(new ResolvingInfo(getResolvingFilters()), name, kind, ALL_INCLUSION, x -> true));
  }

  /**
   * @see Scope#resolveLocally(SymbolKind)
   */
  @Override
  public <T extends Symbol> List<T> resolveLocally(SymbolKind kind) {
    final Collection<ResolvingFilter<? extends Symbol>> resolversForKind =
            getResolvingFiltersForTargetKind(resolvingFilters, kind);

    final Collection<T> resolvedSymbols = new LinkedHashSet<>();

    final Collection<Symbol> symbolsAsList = getLocalSymbolsAsCollection(this);

    for (ResolvingFilter<? extends Symbol> resolvingFilter : resolversForKind) {
      final ResolvingInfo resolvingInfo = new ResolvingInfo(getResolvingFilters());
      resolvingInfo.addInvolvedScope(this);
      Collection<T> filtered = (Collection<T>) resolvingFilter.filter(resolvingInfo, symbolsAsList);
      resolvedSymbols.addAll(filtered);
    }

    return copyOf(resolvedSymbols);
  }

  @Override
  public <T extends Symbol> Collection<T> resolveMany(ResolvingInfo resolvingInfo, String name, SymbolKind kind, AccessModifier modifier) {
    return resolveMany(resolvingInfo, name, kind, modifier, x -> true);
  }

  public <T extends Symbol> Collection<T> resolveMany(ResolvingInfo resolvingInfo, String name, SymbolKind kind, AccessModifier modifier,
                                                      Predicate<Symbol> predicate) {
    final Set<T> resolvedSymbols = this.resolveManyLocally(resolvingInfo, name, kind, modifier, predicate);

    final Collection<T> resolvedFromEnclosing = continueWithEnclosingScope(resolvingInfo, name, kind, modifier, predicate);
    resolvedSymbols.addAll(resolvedFromEnclosing);

    return resolvedSymbols;
  }

  @Override
  public <T extends Symbol> Collection<T> resolveMany(final String name, final SymbolKind kind) {
    return resolveMany(name, kind, ALL_INCLUSION);
  }

  @Override
  public <T extends Symbol> Collection<T> resolveMany(String name, SymbolKind kind, AccessModifier modifier) {
    return resolveMany(name, kind, modifier, x -> true);
  }

  @Override
  public <T extends Symbol> Collection<T> resolveMany(String name, SymbolKind kind, AccessModifier modifier, Predicate<Symbol> predicate) {
    return resolveMany(new ResolvingInfo(getResolvingFilters()), name, kind, modifier, predicate);
  }

  @Override
  public <T extends Symbol> Collection<T> resolveMany(String name, SymbolKind kind, Predicate<Symbol> predicate) {
    return resolveMany(new ResolvingInfo(getResolvingFilters()), name, kind, ALL_INCLUSION, predicate);
  }

  public void setAstNode(ASTNode astNode) {
    this.astNode = astNode;
  }

  public void setEnclosingScope(MutableScope newEnclosingScope) {
    if ((this.enclosingScope != null) && (newEnclosingScope != null)) {
      if (this.enclosingScope == newEnclosingScope) {
        return;
      }
      warn("0xA1042 Scope \"" + getName() + "\" has already an enclosing scope.");
    }

    // remove this scope from current (old) enclosing scope, if exists.
    if (this.enclosingScope != null) {
      this.enclosingScope.removeSubScope(this);
    }

    // add this scope to new enclosing scope, if exists.
    if (newEnclosingScope != null) {
      newEnclosingScope.addSubScope(this);
    }

    // set new enclosing scope (or null)
    this.enclosingScope = newEnclosingScope;
  }

  public void setExportsSymbols(boolean exportsSymbols) {
    this.exportsSymbols = exportsSymbols;
  }

  @Override
  public void setName(final String name) {
    this.name = nullToEmpty(name);
  }

  public void setResolvingFilters(Collection<ResolvingFilter<? extends Symbol>> resolvingFilters) {
    this.resolvingFilters = new LinkedHashSet<>(resolvingFilters);
  }

  public void setSpanningSymbol(ScopeSpanningSymbol symbol) {
    this.spanningSymbol = symbol;
  }

  @Override
  public String toString() {
    return symbols.toString();
  }

  protected boolean checkIfContinueAsSubScope(String symbolName, SymbolKind kind) {
    if (this.exportsSymbols()) {
      final List<String> nameParts = getNameParts(symbolName).toList();

      if (nameParts.size() > 1) {
        final String firstNamePart = nameParts.get(0);
        // A scope that exports symbols usually has a name.
        return firstNamePart.equals(this.getName().orElse(""));
      }
    }

    return false;
  }

  /**
   * Returns true, if current scope should continue with resolving. By default,
   * if symbols are already found and the current scope is a shadowing scope,
   * the resolving process is not continued.
   *
   * @param foundSymbols states whether symbols have already been found during
   *                     the current resolving process.
   * @return true, if resolving should continue
   */
  protected boolean checkIfContinueWithEnclosingScope(boolean foundSymbols) {
    // If this scope shadows its enclosing scope and already some symbols are found,
    // there is no need to continue searching.
    return !(foundSymbols && isShadowingScope());
  }

  /**
   * Continues resolving with the enclosing scope.
   */
  protected <T extends Symbol> Collection<T> continueWithEnclosingScope(ResolvingInfo resolvingInfo, String name, SymbolKind kind, AccessModifier modifier,
                                                                        Predicate<Symbol> predicate) {

    if (checkIfContinueWithEnclosingScope(resolvingInfo.areSymbolsFound()) && (getEnclosingScope().isPresent())) {
      return getEnclosingScope().get().resolveMany(resolvingInfo, name, kind, modifier, predicate);
    }

    return emptySet();
  }

  /**
   * Creates the predicate that checks whether the <code>shadowedSymbol</code> (usually a symbol of
   * the enclosing scope) is shadowed by the (applied) symbols (usually the symbols of the current
   * scope).
   *
   * @return the predicate that checks symbol hiding.
   */
  protected IsShadowedBySymbol createIsShadowingByPredicate(Symbol shadowedSymbol) {
    return new IsShadowedBySymbol(shadowedSymbol);
  }

  protected <T extends Symbol> Set<T> filterSymbolsByAccessModifier(AccessModifier modifier, Set<T> resolvedUnfiltered) {
    return Scopes.filterSymbolsByAccessModifier(modifier, resolvedUnfiltered);
  }

  protected FluentIterable<String> getNameParts(String symbolName) {
    return from(Splitters.DOT.split(symbolName));
  }


  protected <T extends Symbol> Collection<T> getNotShadowedSymbols(Collection<T> shadowingSymbols, Collection<T> symbols) {
    final Collection<T> result = new LinkedHashSet<>();

    for (T resolvedSymbol : symbols) {
      if (isNotSymbolShadowed(shadowingSymbols, resolvedSymbol)) {
        result.add(resolvedSymbol);
      }
    }

    return result;
  }

  protected String getRemainingNameForResolveDown(String symbolName) {
    final FluentIterable<String> nameParts = getNameParts(symbolName);
    return (nameParts.size() > 1) ? DOT.join(nameParts.skip(1)) : symbolName;
  }

  protected <T extends Symbol> Optional<T> getResolvedOrThrowException(final Collection<T> resolved) {
    return ResolvingFilter.getResolvedOrThrowException(resolved);
  }

  /**
   * @param targetKind the symbol targetKind
   * @return all resolvers of this scope that can resolve symbols of <code>targetKind</code>.
   */
  protected Collection<ResolvingFilter<? extends Symbol>> getResolvingFiltersForTargetKind
  (final Collection<ResolvingFilter<? extends Symbol>> resolvingFilters, final SymbolKind
          targetKind) {

    final Collection<ResolvingFilter<? extends Symbol>> resolversForKind = getFiltersForTargetKind(resolvingFilters, targetKind);

    if (resolversForKind.isEmpty()) {
      debug("No resolver found for symbol targetKind \"" + targetKind.getName() + "\" in scope \"" +
              getName() + "\"", CommonScope.class.getSimpleName());
    }

    return resolversForKind;
  }

  protected <T extends Symbol> boolean isNotSymbolShadowed(Collection<T> shadowingSymbols, T symbol) {
    // Does any local symbol shadow the symbol of the enclosing scope?
    return shadowingSymbols.stream().noneMatch(createIsShadowingByPredicate(symbol));
  }

  protected <T extends Symbol> Set<T> resolveManyLocally(ResolvingInfo resolvingInfo, String name, SymbolKind kind, AccessModifier modifier,
                                                         Predicate<Symbol> predicate) {
    errorIfNull(resolvingInfo);
    resolvingInfo.addInvolvedScope(this);
    Collection<ResolvingFilter<? extends Symbol>> resolversForKind =
            getResolvingFiltersForTargetKind(resolvingInfo.getResolvingFilters(), kind);

    final Set<T> resolvedSymbols = new LinkedHashSet<>();

    for (ResolvingFilter<? extends Symbol> resolvingFilter : resolversForKind) {

      try {
        Optional<T> resolvedSymbol = (Optional<T>) resolvingFilter.filter(resolvingInfo, name, symbols);
        if (resolvedSymbol.isPresent()) {
          if (resolvedSymbols.contains(resolvedSymbol.get())) {
            debug("The symbol " + resolvedSymbol.get().getName() + " has already been resolved.",
                    CommonScope.class.getSimpleName());
          }
          resolvedSymbols.add(resolvedSymbol.get());
        }
      } catch (ResolvedSeveralEntriesException e) {
        resolvedSymbols.addAll((Collection<? extends T>) e.getSymbols());
      }
    }

    // filter out symbols that are not included within the access modifier
    Set<T> filteredSymbols = filterSymbolsByAccessModifier(modifier, resolvedSymbols);
    filteredSymbols = new LinkedHashSet<>(filteredSymbols.stream().filter(predicate).collect(toSet()));

    resolvingInfo.updateSymbolsFound(!filteredSymbols.isEmpty());

    return filteredSymbols;
  }
}
