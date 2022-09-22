/* (c) https://github.com/MontiCore/monticore */
import automata.AutomataMill;
import automata._symboltable.IAutomataGlobalScope;
import automata._symboltable.MyStateDeSer;
import automata._symboltable.MyStateResolver;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class GlobalScopeTest {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testGS() {

    // tests availability of the configuration methods
    IAutomataGlobalScope gs = AutomataMill.globalScope();
    gs.setSymbolPath(new MCPath(Paths.get("src/models")));
    gs.setFileExt("autsym");
    gs.addAdaptedStateSymbolResolver(new MyStateResolver());
    gs.putSymbolDeSer("automata._symboltable.StateSymbol", 
        new MyStateDeSer());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
