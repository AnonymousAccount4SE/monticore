/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.data;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.cd.facade.CDModifier.PUBLIC_ABSTRACT;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertBoolean;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getInterfaceBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class DataInterfaceDecoratorTest extends DecoratorTestCase {
  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDInterface dataInterface;

  @Before
  public void setUp() {
    ASTCDCompilationUnit cd = this.parse("de", "monticore", "codegen", "data", "DataInterface");
    ASTCDInterface clazz = getInterfaceBy("ASTA", cd);
    this.glex.setGlobalValue("service", new AbstractService(cd));
    this.glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());

    MethodDecorator methodDecorator = new MethodDecorator(glex, new ASTService(cd));
    InterfaceDecorator dataDecorator = new InterfaceDecorator(this.glex, new DataDecoratorUtil(), methodDecorator, new ASTService(cd));
    ASTCDInterface changeInterface = CD4AnalysisMill.cDInterfaceBuilder().setName(clazz.getName())
        .setModifier(clazz.getModifier())
        .build();
    this.dataInterface = dataDecorator.decorate(clazz, changeInterface);

    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
  }

  @Test
  public void testClassSignature() {
    assertEquals("ASTA", dataInterface.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAttributesCount() {
    assertTrue(dataInterface.getCDAttributeList().isEmpty());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMethodCount() {
    assertEquals(51, dataInterface.getCDMethodList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDeepEquals() {
    ASTCDMethod method = getMethodBy("deepEquals", 1, dataInterface);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());

    assertFalse(method.isEmptyCDParameters());
    assertEquals(1, method.sizeCDParameters());

    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(Object.class, parameter.getMCType());
    assertEquals("o", parameter.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDeepEqualsForceSameOrder() {
    ASTCDMethod method = getMethodBy("deepEquals", 2, dataInterface);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());

    assertFalse(method.isEmptyCDParameters());
    assertEquals(2, method.sizeCDParameters());

    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(Object.class, parameter.getMCType());
    assertEquals("o", parameter.getName());

    parameter = method.getCDParameter(1);
    assertBoolean(parameter.getMCType());
    assertEquals("forceSameOrder", parameter.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDeepEqualsWithComments() {
    ASTCDMethod method = getMethodBy("deepEqualsWithComments", 1, dataInterface);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());

    assertFalse(method.isEmptyCDParameters());
    assertEquals(1, method.sizeCDParameters());

    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(Object.class, parameter.getMCType());
    assertEquals("o", parameter.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDeepEqualsWithCommentsForceSameOrder() {
    ASTCDMethod method = getMethodBy("deepEqualsWithComments", 2, dataInterface);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());

    assertFalse(method.isEmptyCDParameters());
    assertEquals(2, method.sizeCDParameters());

    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(Object.class, parameter.getMCType());
    assertEquals("o", parameter.getName());

    parameter = method.getCDParameter(1);
    assertBoolean(parameter.getMCType());
    assertEquals("forceSameOrder", parameter.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testEqualAttributes() {
    ASTCDMethod method = getMethodBy("equalAttributes", dataInterface);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());

    assertFalse(method.isEmptyCDParameters());
    assertEquals(1, method.sizeCDParameters());

    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(Object.class, parameter.getMCType());
    assertEquals("o", parameter.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testEqualsWithComments() {
    ASTCDMethod method = getMethodBy("equalsWithComments", dataInterface);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());

    assertFalse(method.isEmptyCDParameters());
    assertEquals(1, method.sizeCDParameters());

    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(Object.class, parameter.getMCType());
    assertEquals("o", parameter.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDeepClone() {
    ASTCDMethod method = getMethodBy("deepClone", 0, dataInterface);
    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(dataInterface.getName(), method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    CD4C.init(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CD2JavaTemplates.INTERFACE, dataInterface, packageDir);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  
    assertTrue(Log.getFindings().isEmpty());
  }

}
