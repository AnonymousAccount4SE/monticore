/* (c) https://github.com/MontiCore/monticore */
package de.monticore.simplecd;

import de.monticore.simplecd._ast.ASTCDCompilationUnit;
import de.monticore.simplecd._parser.SimpleCDParser;
import de.monticore.simplecd._symboltable.*;
import de.monticore.simplecd._visitor.SimpleCDTraverser;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesForSymbolException;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ResolvingTest {

  protected Optional<ASTCDCompilationUnit> parseAndTransform(String model){
    SimpleCDParser parser = SimpleCDMill.parser();
    try {
      Optional<ASTCDCompilationUnit> unit = parser.parse(model);
      SimpleCDTraverser traverser = SimpleCDMill.traverser();
      if(unit.isPresent()){
        return Optional.of(unit.get());
      }
      return Optional.empty();
    } catch (IOException e) {
      return Optional.empty();
    }
  }

  protected ISimpleCDArtifactScope buildSymbolTable(ASTCDCompilationUnit ast){
    SimpleCDScopesGenitorDelegator scopesGenitorDelegator = SimpleCDMill.scopesGenitorDelegator();
    ISimpleCDArtifactScope as = scopesGenitorDelegator.createFromAST(ast);

    SimpleCDTraverser traverser = SimpleCDMill.traverser();
    traverser.add4SimpleCD(new SimpleCDSymbolTableCompleter());
    ast.accept(traverser);

    return as;
  }

  @Test
  public void testValidExample(){
    Optional<ASTCDCompilationUnit> a = parseAndTransform("src/test/resources/de/monticore/simplecd/valid/A.cd");
    Optional<ASTCDCompilationUnit> b = parseAndTransform("src/test/resources/de/monticore/simplecd/valid/B.cd");

    assertTrue(a.isPresent());
    assertTrue(b.isPresent());

    ISimpleCDArtifactScope asB = buildSymbolTable(b.get());
    ISimpleCDArtifactScope asA = buildSymbolTable(a.get());

    Optional<CDClassSymbol> fooSymbol = asA.resolveCDClass("Foo");
    assertTrue(fooSymbol.isPresent());

    ISimpleCDScope fooScope = fooSymbol.get().getSpannedScope();
    Optional<CDClassSymbol> type = fooScope.resolveCDClass("B.Bar");
    assertTrue(type.isPresent());
  }

  @Test
  public void testSimpleInvalid(){
    Optional<ASTCDCompilationUnit> c = parseAndTransform("src/test/resources/de/monticore/simplecd/invalid/C.cd");

    assertTrue(c.isPresent());

    ISimpleCDArtifactScope asC = buildSymbolTable(c.get());

    Optional<CDClassSymbol> fooSymbol = asC.resolveCDClass("Foo");
    assertTrue(fooSymbol.isPresent());

    ISimpleCDScope fooScope = fooSymbol.get().getSpannedScope();
    try {
      Optional<CDClassSymbol> type = fooScope.resolveCDClass("Bar");
      //if a type could be resolved: Test fails because Bar should be ambiguous
      assertFalse(type.isPresent());
    } catch(ResolvedSeveralEntriesForSymbolException e) {
      assertTrue(e.getMessage().startsWith("0xA4095"));
    }
  }

  @Test
  public void testInterModelInvalid(){
    Optional<ASTCDCompilationUnit> a = parseAndTransform("src/test/resources/de/monticore/simplecd/invalid/A.cd");
    Optional<ASTCDCompilationUnit> b = parseAndTransform("src/test/resources/de/monticore/simplecd/invalid/B.cd");

    assertTrue(a.isPresent());
    assertTrue(b.isPresent());

    ISimpleCDArtifactScope asB = buildSymbolTable(b.get());
    ISimpleCDArtifactScope asA = buildSymbolTable(a.get());

    Optional<CDClassSymbol> fooSymbol = asA.resolveCDClass("Foo");
    assertTrue(fooSymbol.isPresent());

    ISimpleCDScope fooScope = fooSymbol.get().getSpannedScope();
    try {
      Optional<CDClassSymbol> type = fooScope.resolveCDClass("B.Bar");
      //if a type could be resolved: Test fails because B.Bar should be ambiguous
      assertFalse(type.isPresent());
    } catch(ResolvedSeveralEntriesForSymbolException e) {
      assertTrue(e.getMessage().startsWith("0xA4095"));
    }
  }


}
