/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

public class ProdAndOverriddenProdUseSameAttrNameForDiffNTsTest extends CocoTest{

  private final String MESSAGE = " The overriding production QualifiedName must not use " +
          "the name part for the nonterminal StringLiteral as the overridden production uses this name for the nonterminal Name";
          private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A4025.A4025";

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.init();
    LogStub.enableFailQuick(false);
    checker.addCoCo(new ProdAndOverriddenProdUseSameAttrNameForDiffNTs());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, ProdAndOverriddenProdUseSameAttrNameForDiffNTs.ERROR_CODE, MESSAGE,
        checker);
  }


  @Test
  public void testCorrect(){
    testValidGrammar("cocos.valid.Attributes", checker);
  }

  @Test
  public void testCorrect2(){
    testValidGrammar("mc.grammars.types.TestTypes", checker);
  }

}
