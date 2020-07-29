/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.typecd2java;

import de.monticore.MontiCoreScript;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsGlobalScope;
import de.monticore.io.paths.ModelPath;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.*;

public class TypeCD2JavaTest {

  private ASTCDCompilationUnit cdCompilationUnit;

  @Before
  public void setUp() {
    //create grammar from ModelPath
    Path modelPathPath = Paths.get("src/test/resources");
    ModelPath modelPath = new ModelPath(modelPathPath);
    Optional<ASTMCGrammar> grammar = new MontiCoreScript()
        .parseGrammar(Paths.get(new File(
            "src/test/resources/Automaton.mc4").getAbsolutePath()));
    assertTrue(grammar.isPresent());

    CD4AnalysisGlobalScope cd4AnalysisGlobalScope = new CD4AnalysisGlobalScope(modelPath, "cd");
    Grammar_WithConceptsGlobalScope grammar_withConceptsGlobalScope = new Grammar_WithConceptsGlobalScope(modelPath, "mc4");

    //create ASTCDDefinition from MontiCoreScript
    MontiCoreScript script = new MontiCoreScript();
    script.createSymbolsFromAST(grammar_withConceptsGlobalScope, grammar.get());
    cdCompilationUnit = script.deriveCD(grammar.get(), new GlobalExtensionManagement(),
        cd4AnalysisGlobalScope);

    cdCompilationUnit.setEnclosingScope(cd4AnalysisGlobalScope);
    //make types java compatible
    TypeCD2JavaDecorator decorator = new TypeCD2JavaDecorator(cd4AnalysisGlobalScope);
    decorator.decorate(cdCompilationUnit);
  }

  @Test
  public void testTypeJavaConformList() {
    assertTrue(cdCompilationUnit.getCDDefinition().getCDClasss(0).getCDAttributes(1).getMCType() instanceof ASTMCGenericType);
    ASTMCGenericType simpleReferenceType = (ASTMCGenericType) cdCompilationUnit.getCDDefinition().getCDClasss(0).getCDAttributes(1).getMCType();
    assertFalse(simpleReferenceType.getNamesList().isEmpty());
    assertEquals(3, simpleReferenceType.getNamesList().size());
    assertEquals("java", simpleReferenceType.getNamesList().get(0));
    assertEquals("util", simpleReferenceType.getNamesList().get(1));
    assertEquals("List", simpleReferenceType.getNamesList().get(2));
  }

  @Test
  public void testTypeJavaConformASTPackage() {
    //test that for AST classes the package is now java conform
    assertTrue(cdCompilationUnit.getCDDefinition().getCDClasss(0).getCDAttributes(1).getMCType() instanceof ASTMCGenericType);
    ASTMCGenericType listType = (ASTMCGenericType) cdCompilationUnit.getCDDefinition().getCDClasss(0).getCDAttributes(1).getMCType();
    assertEquals(1, listType.getMCTypeArgumentsList().size());
    assertTrue(listType.getMCTypeArgumentsList().get(0).getMCTypeOpt().isPresent());
    assertTrue(listType.getMCTypeArgumentsList().get(0).getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    ASTMCQualifiedType typeArgument = (ASTMCQualifiedType) listType.getMCTypeArgumentsList().get(0).getMCTypeOpt().get();
    assertEquals(3, typeArgument.getNameList().size());
    assertEquals("automaton", typeArgument.getNameList().get(0));
    assertEquals("_ast", typeArgument.getNameList().get(1));
    assertEquals("ASTState", typeArgument.getNameList().get(2));
  }

  @Test
  public void testStringType() {
    //test that types like String are not changed
    assertTrue(cdCompilationUnit.getCDDefinition().getCDClasss(0).getCDAttributes(0).getMCType() instanceof ASTMCQualifiedType);
    ASTMCQualifiedType simpleReferenceType = (ASTMCQualifiedType) cdCompilationUnit.getCDDefinition().getCDClasss(0).getCDAttributes(0).getMCType();
    assertFalse(simpleReferenceType.getNameList().isEmpty());
    assertEquals(1, simpleReferenceType.getNameList().size());
    assertEquals("String", simpleReferenceType.getNameList().get(0));
  }
}
