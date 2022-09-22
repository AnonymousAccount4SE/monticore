/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import de.se_rwth.commons.logging.Log;

public class ExternalNTOnlyInComponentGrammarTest extends CocoTest{

  private final String MESSAGE = " The external nonterminal A must not be used in a grammar not marked " +
          "as a grammar component.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "de.monticore.grammar.cocos.invalid.A0276.A0276";
  private final String grammar2 = "de.monticore.grammar.cocos.invalid.A0276.A0276b";
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @BeforeClass
  public static void disableFailQuick() {
    checker.addCoCo(new ExternalNTOnlyInComponentGrammar());
  }

  @Test
  public void testInvalid(){
    testInvalidGrammar(grammar, ExternalNTOnlyInComponentGrammar.ERROR_CODE, MESSAGE, checker);
  }

  @Test
  public void testInvalid2(){
    testInvalidGrammar(grammar2, ExternalNTOnlyInComponentGrammar.ERROR_CODE, MESSAGE, checker);
  }


  @Test
  public void testCorrect(){
    testValidGrammar("de.monticore.grammar.cocos.valid.Component", checker);
  }


}
