/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java;

import de.monticore.ast.ASTNode;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd.facade.CDModifier;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class DecoratorAssert {

  private static final int DEFAULT_ARRAY_DIMENSION = 1;

  private static final MCTypeFacade CD_TYPE_FACADE = MCTypeFacade.getInstance();

  private DecoratorAssert() {
  }

  public static void assertDeepEquals(ASTNode expected, ASTNode actual) {
    assertTrue(String.format("Expected: [%s], Actual: [%s]", getAsString(expected), getAsString(actual)), expected.deepEquals(actual));
  }

  public static void assertDeepEquals(ASTMCType expected, ASTMCType actual) {
    CD4CodeFullPrettyPrinter p = new CD4CodeFullPrettyPrinter();
    assertEquals(p.prettyprint(expected), p.prettyprint(actual));
  }

  private static String getAsString(ASTNode node) {
    return node instanceof ASTMCType ? ((ASTMCType) node).printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()) : node.toString();
  }

  public static void assertDeepEquals(CDModifier expected, ASTNode actual) {
    assertTrue(actual instanceof ASTModifier);
    ASTModifier actualMod = (ASTModifier) actual;
    ASTModifier expectedMod = expected.build();
    assertEquals(expectedMod.isAbstract(), actualMod.isAbstract());
    assertEquals(expectedMod.isProtected(), actualMod.isProtected());
    assertEquals(expectedMod.isPrivate(), actualMod.isPrivate());
    assertEquals(expectedMod.isPublic(), actualMod.isPublic());
    assertEquals(expectedMod.isDerived(), actualMod.isDerived());
    assertEquals(expectedMod.isFinal(), actualMod.isFinal());
    assertEquals(expectedMod.isStatic(), actualMod.isStatic());
    assertEquals(expectedMod.isPresentStereotype(), actualMod.isPresentStereotype());
  }

  public static void assertDeepEquals(Class<?> expected, ASTNode actual) {
    assertTrue(actual instanceof ASTMCType);
    assertEquals(expected.getSimpleName(), (new CD4CodeFullPrettyPrinter()).prettyprint((ASTMCType) actual));
  }

  public static void assertDeepEquals(String name, ASTNode actual) {
    assertTrue(actual instanceof ASTMCType);
    assertEquals(name, (new CD4CodeFullPrettyPrinter()).prettyprint((ASTMCType) actual));
  }

  public static void assertBoolean(ASTNode actual) {
    assertTrue(actual instanceof ASTMCPrimitiveType);
    assertTrue(((ASTMCPrimitiveType) actual).isBoolean());
  }

  public static void assertInt(ASTNode actual) {
    assertTrue(actual instanceof ASTMCPrimitiveType);
    assertTrue(((ASTMCPrimitiveType) actual).isInt());
  }

  public static void assertFloat(ASTNode actual){
    assertTrue(actual instanceof ASTMCPrimitiveType);
    assertTrue(((ASTMCPrimitiveType) actual).isFloat());
  }

  public static void assertVoid(ASTNode acutal) {
    assertDeepEquals(CD_TYPE_FACADE.createVoidType(), acutal);
  }

  public static void assertOptionalOf(Class<?> clazz, ASTNode actual) {
    String type = "Optional<" + clazz.getSimpleName() + ">";
    assertTrue(actual instanceof ASTMCType);
    assertEquals(type,(new CD4CodeFullPrettyPrinter()).prettyprint((ASTMCType) actual));
  }

  public static void assertOptionalOf(String name, ASTNode actual) {
    String type = "Optional<" + name + ">";
    assertTrue(actual instanceof ASTMCType);
    assertEquals(type,(new CD4CodeFullPrettyPrinter()).prettyprint((ASTMCType) actual));
  }

  public static void assertListOf(Class<?> clazz, ASTNode actual) {
    String type = "List<" + clazz.getSimpleName() + ">";
    assertTrue(actual instanceof ASTMCType);
    assertEquals(type,(new CD4CodeFullPrettyPrinter()).prettyprint((ASTMCType) actual));
  }

  public static void assertListOf(String name, ASTNode actual) {
    assertDeepEquals(CD_TYPE_FACADE.createListTypeOf(name), actual);
  }
  public static void assertArrayOf(Class<?> clazz, ASTNode actual) {
    assertDeepEquals(CD_TYPE_FACADE.createArrayType(clazz, DEFAULT_ARRAY_DIMENSION), actual);
  }

  public static void assertArrayOf(String name, ASTNode actual) {
    assertDeepEquals(CD_TYPE_FACADE.createArrayType(name, DEFAULT_ARRAY_DIMENSION), actual);
  }
}
