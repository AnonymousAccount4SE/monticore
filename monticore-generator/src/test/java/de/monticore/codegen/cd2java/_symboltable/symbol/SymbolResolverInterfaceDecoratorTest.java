/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.symbol;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.MCTypeFacade;
import de.se_rwth.commons.logging.*;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.cd.facade.CDModifier.PUBLIC_ABSTRACT;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertBoolean;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SymbolResolverInterfaceDecoratorTest extends DecoratorTestCase {

  private ASTCDInterface symbolClassAutomaton;

  private GlobalExtensionManagement glex;

  private MCTypeFacade mcTypeFacade;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  private static final String AUTOMATON_SYMBOL = "de.monticore.codegen.ast.automaton._symboltable.AutomatonSymbol";

  private static final String PREDICATE = "java.util.function.Predicate<de.monticore.codegen.ast.automaton._symboltable.AutomatonSymbol>";

  private static final String ACCESS_MODIFIER = "de.monticore.symboltable.modifiers.AccessModifier";

  @Before
  public void setUp() {
    this.mcTypeFacade = MCTypeFacade.getInstance();
    this.glex = new GlobalExtensionManagement();

    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));


    SymbolResolverInterfaceDecorator decorator = new SymbolResolverInterfaceDecorator(this.glex, new SymbolTableService(decoratedCompilationUnit));
    //creates ScopeSpanningSymbol
    ASTCDClass automatonClass = getClassBy("ASTAutomaton", decoratedCompilationUnit);
    this.symbolClassAutomaton = decorator.decorate(automatonClass);

  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  // ScopeSpanningSymbol

  @Test
  public void testClassNameAutomatonSymbol() {
    assertEquals("IAutomatonSymbolResolver", symbolClassAutomaton.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testNoSuperInterfacesl() {
    assertFalse(symbolClassAutomaton.isPresentCDExtendUsage());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testNoAttribute() {
    assertTrue( symbolClassAutomaton.getCDAttributeList().isEmpty());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMethods() {
    assertEquals(1, symbolClassAutomaton.getCDMethodList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testResolveAdaptedLocallyManyMethod() {
    ASTCDMethod method = getMethodBy("resolveAdaptedAutomatonSymbol", symbolClassAutomaton);

    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertDeepEquals(mcTypeFacade.createListTypeOf(AUTOMATON_SYMBOL), method.getMCReturnType().getMCType());
    assertEquals(4, method.sizeCDParameters());
    assertBoolean(method.getCDParameter(0).getMCType());
    assertEquals("foundSymbols", method.getCDParameter(0).getName());
    assertDeepEquals(String.class, method.getCDParameter(1).getMCType());
    assertEquals("name", method.getCDParameter(1).getName());
    assertDeepEquals(ACCESS_MODIFIER, method.getCDParameter(2).getMCType());
    assertEquals("modifier", method.getCDParameter(2).getName());
    assertDeepEquals(PREDICATE, method.getCDParameter(3).getMCType());
    assertEquals("predicate", method.getCDParameter(3).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
