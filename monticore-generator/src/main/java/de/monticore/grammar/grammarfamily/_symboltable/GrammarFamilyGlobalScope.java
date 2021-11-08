/* generated from model Grammar_WithConcepts */
/* generated by template core.Class*/

/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.grammarfamily._symboltable;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar_withconcepts._parser.Grammar_WithConceptsParser;
import de.monticore.grammar.grammar_withconcepts._symboltable.IGrammar_WithConceptsArtifactScope;
import de.monticore.io.FileReaderWriter;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Optional;


public class GrammarFamilyGlobalScope extends GrammarFamilyGlobalScopeTOP {
  public GrammarFamilyGlobalScope(MCPath symbolPath, String modelFileExtension) {
    super(symbolPath, modelFileExtension);
  }

  public GrammarFamilyGlobalScope() {
    super();
  }

  @Override
  public GrammarFamilyGlobalScope getRealThis() {
    return this;
  }

  @Override
  public  void loadMCGrammar (String name) {
    for (String modelName : calculateModelNamesForMCGrammar(name)) {
      loadGrammarFileForModelName(modelName);
    }
  }

  public  void loadGrammarFileForModelName (String modelName)  {
    // 1. call super implementation to start with employing the DeSer
    // super.loadFileForModelName(modelName);

    String filePath = Paths
            .get(Names.getPathFromPackage(modelName) + ".mc4").toString();

    if (!isFileLoaded(filePath)) {

      // 2. calculate potential location of model file and try to find it in model path
      Optional<URL> url = getSymbolPath().find(Names.getPathFromPackage(modelName)+".mc4");

      // 3. if the file was found, parse the model and create its symtab
      if (url.isPresent()) {
        ASTMCGrammar ast = parse(url.get());
        IGrammar_WithConceptsArtifactScope artScope = new GrammarFamilyPhasedSTC().createFromAST(ast);
        addSubScope(artScope);
        addLoadedFile(filePath);
      }
    }
  }

  protected ASTMCGrammar parse(URL url){
    try {
      Reader reader = FileReaderWriter.getReader(url);
      Optional<ASTMCGrammar> optAST = new Grammar_WithConceptsParser().parse(reader);
      if(optAST.isPresent()){
        return optAST.get();
      }
    }
    catch (IOException e) {
      Log.error("0x1A235 Error while parsing model", e);
    }
    return null;
  }

}
