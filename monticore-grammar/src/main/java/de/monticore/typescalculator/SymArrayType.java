package de.monticore.typescalculator;

import de.monticore.typescalculator.SymTypeExpression;

/**
 * Arrays of a certain dimension (>= 1)
 */
public class SymArrayType extends SymTypeExpression {
  
  /**
   * An arrayType has a dimension
   */
  protected int dim;
  
  /**
   * An Array has an argument Type
   */
  protected SymTypeExpression argument;
  
  public SymArrayType(int dim, SymTypeExpression argument) {
    this.dim = dim;
    this.argument = argument;
  }
  
  public int getDim() {
    return dim;
  }
  
  public void setDim(int dim) {
    this.dim = dim;
  }
  
  public SymTypeExpression getArgument() {
    return argument;
  }
  
  public void setArgument(SymTypeExpression argument) {
    this.argument = argument;
  }
  
  /**
   * print: Umwandlung in einen kompakten String
   */
  public String print() {
    StringBuffer r = new StringBuffer(getArgument().print());
    for(int i = 1; i<=dim;i++){
      r.append("[]");
    }
    return r.toString();
  }
  
  
  // --------------------------------------------------------------------------
  
  @Override @Deprecated // and not implemented yet
  public boolean deepEquals(SymTypeExpression symTypeExpression) {
    return false;
  }
  
  @Override @Deprecated // and not implemented yet
  public SymTypeExpression deepClone() {
    return null;
  }
  
}
