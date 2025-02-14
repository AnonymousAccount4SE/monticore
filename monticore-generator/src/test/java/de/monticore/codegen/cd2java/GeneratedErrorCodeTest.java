/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * test that error codes are generated deterministic
 * if same string is passed, the same error code is generated
 */
public class GeneratedErrorCodeTest extends DecoratorTestCase {

  @Test
  public void testDeterministic() {
    ASTCDCompilationUnit compilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    ASTService service = new ASTService(compilationUnit);
    String codeTest = "Foo";
    String generatedErrorCode = service.getGeneratedErrorCode(codeTest);

    ASTService service2 = new ASTService(compilationUnit);
    String codeTest2 = "Foo";
    String generatedErrorCode2 = service2.getGeneratedErrorCode(codeTest2);

    assertEquals(generatedErrorCode, generatedErrorCode2);
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
