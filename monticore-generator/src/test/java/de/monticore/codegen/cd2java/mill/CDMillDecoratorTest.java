/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.mill;

import com.google.common.collect.Lists;
import de.monticore.MontiCoreScript;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java._ast.builder.BuilderDecorator;
import de.monticore.codegen.cd2java._parser.ParserService;
import de.monticore.codegen.cd2java._symboltable.SymbolTableCDDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._symboltable.language.LanguageBuilderDecorator;
import de.monticore.codegen.cd2java._symboltable.language.LanguageDecorator;
import de.monticore.codegen.cd2java._symboltable.modelloader.ModelLoaderBuilderDecorator;
import de.monticore.codegen.cd2java._symboltable.modelloader.ModelLoaderDecorator;
import de.monticore.codegen.cd2java._symboltable.scope.*;
import de.monticore.codegen.cd2java._symboltable.symbol.*;
import de.monticore.codegen.cd2java._symboltable.symbol.symbolloadermutator.MandatoryMutatorSymbolLoaderDecorator;
import de.monticore.codegen.cd2java._symboltable.symboltablecreator.*;
import de.monticore.codegen.cd2java._visitor.*;
import de.monticore.codegen.cd2java._visitor.builder.DelegatorVisitorBuilderDecorator;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsGlobalScope;
import de.monticore.io.paths.IterablePath;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_PACKAGE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CDMillDecoratorTest extends DecoratorTestCase {

  private ASTCDCompilationUnit millCD;

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit originalCompilationUnit;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit decoratedSymbolCompilationUnit;

  private ASTCDCompilationUnit decoratedScopeCompilationUnit;

  @Before
  public void setUp() {
    LogStub.init();
    LogStub.enableFailQuick(false);
    this.glex = new GlobalExtensionManagement();

    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "Automaton");
    decoratedScopeCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "AutomatonScopeCD");
    decoratedSymbolCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "AutomatonSymbolCD");

    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new VisitorService(decoratedCompilationUnit));

    ASTService astService = new ASTService(decoratedCompilationUnit);
    MillForSuperDecorator forSuperDecorator = new MillForSuperDecorator(this.glex, new ASTService(decoratedCompilationUnit));
    MillDecorator millDecorator = new MillDecorator(this.glex, astService);

    // create ast package
    ASTCDCompilationUnit astCD = decoratedCompilationUnit.deepClone();
    astCD.addPackage(astCD.getCDDefinition().getName().toLowerCase());
    astCD.addPackage(AST_PACKAGE);

    CDMillDecorator cdMillDecorator = new CDMillDecorator(this.glex, millDecorator, forSuperDecorator);
    this.millCD = cdMillDecorator.decorate(Lists.newArrayList(astCD, getVisitorCD(), getSymbolCD()));
  }

  protected ASTCDCompilationUnit getSymbolCD() {
    SymbolTableService symbolTableService = new SymbolTableService(decoratedCompilationUnit);
    VisitorService visitorService = new VisitorService(decoratedCompilationUnit);
    ParserService parserService = new ParserService(decoratedCompilationUnit);
    MethodDecorator methodDecorator = new MethodDecorator(glex, symbolTableService);
    AccessorDecorator accessorDecorator = new AccessorDecorator(glex, symbolTableService);

    SymbolDecorator symbolDecorator = new SymbolDecorator(glex, symbolTableService, visitorService, methodDecorator);
    BuilderDecorator builderDecorator = new BuilderDecorator(glex, accessorDecorator, symbolTableService);
    SymbolBuilderDecorator symbolBuilderDecorator = new SymbolBuilderDecorator(glex, builderDecorator);
    ScopeClassDecorator scopeClassDecorator = new ScopeClassDecorator(glex, symbolTableService, visitorService, methodDecorator);
    ScopeClassBuilderDecorator scopeClassBuilderDecorator = new ScopeClassBuilderDecorator(glex, builderDecorator);
    ScopeInterfaceDecorator scopeInterfaceDecorator = new ScopeInterfaceDecorator(glex, symbolTableService, visitorService, methodDecorator);
    GlobalScopeClassDecorator globalScopeClassDecorator = new GlobalScopeClassDecorator(glex, symbolTableService, methodDecorator);
    GlobalScopeClassBuilderDecorator globalScopeClassBuilderDecorator = new GlobalScopeClassBuilderDecorator(glex, symbolTableService, builderDecorator);
    ArtifactScopeDecorator artifactScopeDecorator = new ArtifactScopeDecorator(glex, symbolTableService, visitorService, methodDecorator);
    ArtifactScopeBuilderDecorator artifactScopeBuilderDecorator = new ArtifactScopeBuilderDecorator(glex, symbolTableService, builderDecorator, accessorDecorator);
    SymbolLoaderDecorator symbolReferenceDecorator = new SymbolLoaderDecorator(glex, symbolTableService, methodDecorator, new MandatoryMutatorSymbolLoaderDecorator(glex));
    SymbolLoaderBuilderDecorator symbolReferenceBuilderDecorator = new SymbolLoaderBuilderDecorator(glex, symbolTableService, accessorDecorator);
    CommonSymbolInterfaceDecorator commonSymbolInterfaceDecorator = new CommonSymbolInterfaceDecorator(glex, symbolTableService, visitorService, methodDecorator);
    LanguageDecorator languageDecorator = new LanguageDecorator(glex, symbolTableService, parserService, accessorDecorator);
    LanguageBuilderDecorator languageBuilderDecorator = new LanguageBuilderDecorator(glex, builderDecorator);
    ModelLoaderDecorator modelLoaderDecorator = new ModelLoaderDecorator(glex, symbolTableService, accessorDecorator);
    ModelLoaderBuilderDecorator modelLoaderBuilderDecorator = new ModelLoaderBuilderDecorator(glex, builderDecorator);
    SymbolResolvingDelegateInterfaceDecorator symbolResolvingDelegateInterfaceDecorator = new SymbolResolvingDelegateInterfaceDecorator(glex, symbolTableService);
    SymbolTableCreatorDecorator symbolTableCreatorDecorator = new SymbolTableCreatorDecorator(glex, symbolTableService, visitorService, methodDecorator);
    SymbolTableCreatorBuilderDecorator symbolTableCreatorBuilderDecorator = new SymbolTableCreatorBuilderDecorator(glex, symbolTableService);
    SymbolTableCreatorDelegatorDecorator symbolTableCreatorDelegatorDecorator = new SymbolTableCreatorDelegatorDecorator(glex, symbolTableService, visitorService);
    SymbolTableCreatorForSuperTypes symbolTableCreatorForSuperTypes = new SymbolTableCreatorForSuperTypes(glex, symbolTableService);
    SymbolTableCreatorDelegatorBuilderDecorator symbolTableCreatorDelegatorBuilderDecorator = new SymbolTableCreatorDelegatorBuilderDecorator(glex, builderDecorator);
    SymbolTableCreatorForSuperTypesBuilder symbolTableCreatorForSuperTypesBuilder = new SymbolTableCreatorForSuperTypesBuilder(glex, builderDecorator, symbolTableService);

    IterablePath targetPath = Mockito.mock(IterablePath.class);

    SymbolTableCDDecorator symbolTableCDDecorator = new SymbolTableCDDecorator(glex, targetPath, symbolTableService, symbolDecorator,
        symbolBuilderDecorator, symbolReferenceDecorator, symbolReferenceBuilderDecorator,
        scopeClassDecorator, scopeClassBuilderDecorator, scopeInterfaceDecorator,
        globalScopeClassDecorator, globalScopeClassBuilderDecorator, artifactScopeDecorator, artifactScopeBuilderDecorator,
        commonSymbolInterfaceDecorator, languageDecorator, languageBuilderDecorator, modelLoaderDecorator, modelLoaderBuilderDecorator,
        symbolResolvingDelegateInterfaceDecorator, symbolTableCreatorDecorator, symbolTableCreatorBuilderDecorator,
        symbolTableCreatorDelegatorDecorator, symbolTableCreatorForSuperTypes, symbolTableCreatorDelegatorBuilderDecorator,
        symbolTableCreatorForSuperTypesBuilder);

    // cd with no handcoded classes
    return symbolTableCDDecorator.decorate(decoratedCompilationUnit, decoratedSymbolCompilationUnit, decoratedScopeCompilationUnit);
  }

  protected ASTCDCompilationUnit getVisitorCD() {
    IterablePath targetPath = Mockito.mock(IterablePath.class);
    VisitorService visitorService = new VisitorService(decoratedCompilationUnit);
    SymbolTableService symbolTableService = new SymbolTableService(decoratedCompilationUnit);

    VisitorDecorator astVisitorDecorator = new VisitorDecorator(this.glex, visitorService, symbolTableService);
    DelegatorVisitorDecorator delegatorVisitorDecorator = new DelegatorVisitorDecorator(this.glex, visitorService, symbolTableService);
    ParentAwareVisitorDecorator parentAwareVisitorDecorator = new ParentAwareVisitorDecorator(this.glex, visitorService);
    InheritanceVisitorDecorator inheritanceVisitorDecorator = new InheritanceVisitorDecorator(this.glex, visitorService, symbolTableService);
    DelegatorVisitorBuilderDecorator delegatorVisitorBuilderDecorator = new DelegatorVisitorBuilderDecorator(this.glex, visitorService, symbolTableService);


    CDVisitorDecorator decorator = new CDVisitorDecorator(this.glex, targetPath, visitorService,
        astVisitorDecorator, delegatorVisitorDecorator, inheritanceVisitorDecorator,
        parentAwareVisitorDecorator, delegatorVisitorBuilderDecorator);
    return decorator.decorate(decoratedCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testPackageName() {
    assertEquals(5, millCD.sizePackages());
    assertEquals("de", millCD.getPackage(0));
    assertEquals("monticore", millCD.getPackage(1));
    assertEquals("codegen", millCD.getPackage(2));
    assertEquals("symboltable", millCD.getPackage(3));
    assertEquals("automaton", millCD.getPackage(4));
  }

  @Test
  public void testDefinitionName() {
    assertEquals("Automaton", millCD.getCDDefinition().getName());
  }

  @Test
  public void testClassSize() {
    assertEquals(2, millCD.getCDDefinition().sizeCDClasss());
  }

  @Test
  public void testMillClass() {
    ASTCDClass automatonMill = getClassBy("AutomatonMill", millCD);
  }

  @Test
  public void testMillForSuperClass() {
    ASTCDClass automatonMill = getClassBy("LexicalsMillForAutomaton", millCD);
  }
}
