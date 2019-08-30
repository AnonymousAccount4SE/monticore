/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import de.monticore.types.typesymbols._symboltable.TypeSymbol;

/**
 * An objectType is a full qualified class name.
 * Therefore, we have the fullName, the baseName and the
 * Symbol behind that full qualified class name to retrieve
 */
public class SymObjectType extends SymTypeExpression {
  
  /**
   * An SymObjectType has a name.
   * This is always the full qualified name (i.e. including package)
   */
  protected String objname;
  
  /**
   * Symbol corresponding to the type's name (if loaded)
   */
  // XXX BR: unklar, ob das optional sein muss, wenn schon der Name
  // immer gesetzt ist; man könnte das Symbol gleich beim initialisieren mit setzen lassen
  protected TypeSymbol objTypeSymbol;
  
  public SymObjectType(String objname) {
    this.objname = objname;
  }
  
  public String getObjName() {
    return objname;
  }
  
  public void setObjName(String objname) {
    this.objname = objname;
  }
  
  public TypeSymbol getObjTypeSymbol() {
    return objTypeSymbol;
  }
  
  public void setObjTypeSymbol(TypeSymbol objTypeSymbol) {
    this.objTypeSymbol = objTypeSymbol;
  }
  
  /**
   * print: Umwandlung in einen kompakten String
   */
  public String print() {
    return getObjName();
  }
  
  /**
   * getFullName: get the Qualified Name including Package
   */
  public String getFullName() {
    return getObjName();
  }
  
  /**
   * getBaseName: get the unqualified Name (no ., no Package)
   */
  public String getBaseName() {
    String[] parts = getObjName().split("\\.");
    return parts[parts.length - 1];
  }
  
  // --------------------------------------------------------------------------
  
  @Override @Deprecated
  public boolean deepEquals(SymTypeExpression symTypeExpression) {
    if(!(symTypeExpression instanceof SymObjectType)){
      return false;
    }
    if(!this.name.equals(symTypeExpression.name)){
      return false;
    }
    if(!this.typeSymbol.equals(symTypeExpression.typeSymbol)){
      return false;
    }
    // TODO RE: supertypen muss man doch nicht klonen?
    for(int i = 0; i<this.superTypes.size();i++){
      if(!this.superTypes.get(i).deepEquals(symTypeExpression.superTypes.get(i))){
        return false;
      }
    }
    return true;
  }

  @Override @Deprecated
  public SymTypeExpression deepClone() {
    SymObjectType clone = new SymObjectType(objname);
    clone.setName(this.name);
    clone.setEnclosingScope(this.enclosingScope);

    for(SymTypeExpression expr: superTypes){
      clone.addSuperType(expr.deepClone());
    }
    clone.typeSymbol = this.typeSymbol;
    return clone;
  }
  
  @Deprecated
  public SymObjectType() {
  }
  
  
}
