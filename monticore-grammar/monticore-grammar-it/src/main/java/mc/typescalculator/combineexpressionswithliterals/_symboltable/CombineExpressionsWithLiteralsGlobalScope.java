/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator.combineexpressionswithliterals._symboltable;

import de.monticore.io.paths.ModelPath;

public class CombineExpressionsWithLiteralsGlobalScope extends CombineExpressionsWithLiteralsGlobalScopeTOP {

  public CombineExpressionsWithLiteralsGlobalScope(ModelPath mp){
    super(mp,"cex");
  }

  @Override
  public CombineExpressionsWithLiteralsGlobalScope getRealThis() {
    return this;
  }

}
