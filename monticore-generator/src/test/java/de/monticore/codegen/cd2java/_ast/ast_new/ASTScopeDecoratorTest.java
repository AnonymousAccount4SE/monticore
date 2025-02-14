/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.ast_new;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.ASTScopeDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static org.junit.Assert.*;

public class ASTScopeDecoratorTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private List<ASTCDAttribute> attributes;

  private static final String AST_I_SCOPE = "de.monticore.codegen.ast.ast._symboltable.IASTScope";

  private static final String SUPER_I_SCOPE= "de.monticore.codegen.ast.supercd._symboltable.ISuperCDScope";

  @Before
  public void setup() {
    ASTCDCompilationUnit ast = this.parse("de", "monticore", "codegen", "ast", "AST");

    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("service", new AbstractService(ast));

    ASTScopeDecorator decorator = new ASTScopeDecorator(this.glex, new SymbolTableService(ast));
    ASTCDClass clazz = getClassBy("A", ast);
    this.attributes = decorator.decorate(clazz);
  }

  @Test
  public void testAttributes() {
    assertFalse(attributes.isEmpty());
    assertEquals(3, attributes.size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSpannedScopeAttribute() {
    Optional<ASTCDAttribute> symbolAttribute = attributes.stream().filter(x -> x.getName().equals("spannedScope")).findFirst();
    assertTrue(symbolAttribute.isPresent());
    assertDeepEquals(PROTECTED, symbolAttribute.get().getModifier());
    assertDeepEquals(AST_I_SCOPE, symbolAttribute.get().getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSpannedScope2Attribute() {
    Optional<ASTCDAttribute> symbolAttribute = attributes.stream().filter(x -> x.getName().equals("spannedScope")).findFirst();
    assertTrue(symbolAttribute.isPresent());
    assertDeepEquals(PROTECTED, symbolAttribute.get().getModifier());
    assertDeepEquals(AST_I_SCOPE, symbolAttribute.get().getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testEnclosingScopeAttributeInherited() {
    List<ASTCDAttribute> enclosingScope = attributes.stream().filter(x -> x.getName().equals("enclosingScope")).collect(Collectors.toList());
    assertFalse(enclosingScope.isEmpty());
    assertEquals(2, enclosingScope.size());
    ASTCDAttribute scope = enclosingScope.get(1);
    ASTModifier astModifier= PROTECTED.build();
    TransformationHelper.addStereotypeValue(astModifier, MC2CDStereotypes.INHERITED.toString());
    assertDeepEquals(astModifier, scope.getModifier());
    assertDeepEquals(SUPER_I_SCOPE, scope.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testEnclosingScopeAttribute() {
    List<ASTCDAttribute> enclosingScope = attributes.stream().filter(x -> x.getName().equals("enclosingScope")).collect(Collectors.toList());
    assertFalse(enclosingScope.isEmpty());
    assertEquals(2, enclosingScope.size());
    ASTCDAttribute scope = enclosingScope.get(0);
    assertDeepEquals(PROTECTED, scope.getModifier());
    assertDeepEquals(AST_I_SCOPE, scope.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
