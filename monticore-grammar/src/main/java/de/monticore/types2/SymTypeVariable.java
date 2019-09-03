/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types2;

public class SymTypeVariable extends SymTypeExpression {

  /**
   * A typeVariable has a name
   */
  protected String varName;

  // TODO BR: The type variable may be bounded; then it probably helps to connect to
  // the Var-Symbol that contains the bound
  
  public SymTypeVariable(String varName) {
    this.varName = varName;
  }

  /**
   * We could connect the Variable to a symbol carrying that variable
   * (clarify if that is really needed)
   */
  // protected TypeVarSymbol typeVarSymbol;

  public String getVarName() {
    return varName;
  }
  
  public void setVarName(String name) {
    this.varName = name;
  }

  /**
   * print: Umwandlung in einen kompakten String
   */
  public String print() {
    return getVarName();
  }
  
  /**
   * Am I primitive? (such as "int")
   */
  public boolean isPrimitiveType() {
    return false;
    // TODO: ?sometimes the var is, sometimes not ...
    // Unless we always assume boxed implementations then return false is correct
  }

  // --------------------------------------------------------------------------


  @Override @Deprecated
  public boolean deepEquals(SymTypeExpression symTypeExpression) {
    if(!(symTypeExpression instanceof SymTypeVariable)){
      return false;
    }
    if(!this.name.equals(symTypeExpression.name)){
      return false;
    }
    for(int i = 0; i<this.superTypes.size();i++){
      if(!this.superTypes.get(i).deepEquals(symTypeExpression.superTypes.get(i))){
        return false;
      }
    }
    return true;
  }

  @Override @Deprecated
  public SymTypeExpression deepClone() {
    SymTypeVariable clone = new SymTypeVariable();
    clone.setName(this.name);
    clone.setEnclosingScope(this.enclosingScope);
    for(SymTypeExpression expr: superTypes){
      clone.addSuperType(expr.deepClone());
    }
    return clone;
  }
  
  public SymTypeVariable() {
  }
  
}
