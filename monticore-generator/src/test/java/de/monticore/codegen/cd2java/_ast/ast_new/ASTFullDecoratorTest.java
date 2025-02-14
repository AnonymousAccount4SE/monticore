/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.ast_new;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.*;
import de.monticore.codegen.cd2java._ast.ast_class.reference.ASTReferenceDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.data.DataDecorator;
import de.monticore.codegen.cd2java.data.DataDecoratorUtil;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static org.junit.Assert.*;

public class ASTFullDecoratorTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDClass astClass;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;


  @Before
  public void setup() {
    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "AST");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    ASTService astService = new ASTService(decoratedCompilationUnit);
    SymbolTableService symbolTableService = new SymbolTableService(decoratedCompilationUnit);
    VisitorService visitorService = new VisitorService(decoratedCompilationUnit);
    ASTSymbolDecorator astSymbolDecorator = new ASTSymbolDecorator(glex, symbolTableService);
    ASTScopeDecorator astScopeDecorator = new ASTScopeDecorator(glex,  symbolTableService);
    DataDecorator dataDecorator = new DataDecorator(glex, new MethodDecorator(glex, astService), new ASTService(decoratedCompilationUnit), new DataDecoratorUtil());
    ASTDecorator astDecorator = new ASTDecorator(glex, astService, visitorService,
        astSymbolDecorator, astScopeDecorator, new MethodDecorator(glex, astService), symbolTableService);
    ASTReferenceDecorator astReferencedSymbolDecorator = new ASTReferenceDecorator(glex, symbolTableService);
    ASTFullDecorator fullDecorator = new ASTFullDecorator(dataDecorator, astDecorator, astReferencedSymbolDecorator);

    ASTCDClass clazz = getClassBy("A", decoratedCompilationUnit);
    ASTCDClass changedClass = CD4AnalysisMill.cDClassBuilder().setName(clazz.getName())
        .setModifier(clazz.getModifier())
        .build();
    this.astClass = fullDecorator.decorate(clazz, changedClass);
  }

  @Test
  public void testCompilationCopy() {
    assertNotEquals(originalCompilationUnit, decoratedCompilationUnit);
  
    assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testClassName() {
    assertEquals("A", astClass.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testClassModifier() {
    // because it defines a symbol but has no name attribute or a getName method
    assertTrue(astClass.getModifier().isAbstract());
  
    assertTrue(Log.getFindings().isEmpty());
  }
  @Test
  public void testAttributeSize() {
    assertEquals(3, astClass.getCDAttributeList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testConstructorSize() {
    assertEquals(1, astClass.getCDConstructorList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMethodSize() {
    assertFalse(astClass.getCDMethodList().isEmpty());
    assertEquals(22, astClass.getCDMethodList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

}
