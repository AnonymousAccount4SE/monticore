/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcfullgenerictypes._ast;

import com.google.common.collect.ImmutableList;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;

import java.util.List;

public class ASTMCMultipleGenericType extends ASTMCMultipleGenericTypeTOP {
  public ASTMCMultipleGenericType() {
  }

  protected  ASTMCMultipleGenericType (/* generated by template ast.ConstructorParametersDeclaration*/
          de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType mCBasicGenericType
          ,
          java.util.List<de.monticore.types.mcfullgenerictypes._ast.ASTMCInnerType> mCInnerTypes

  )
    /* generated by template ast.ConstructorAttributesSetter*/
  {
    setMCBasicGenericType(mCBasicGenericType);
    setMCInnerTypeList(mCInnerTypes);
  }

  // TODO RE: Diese Klasse ist wohl unfertig. Sie könnte auch entfernbar sein?

  @Override
  public String getBaseName() {
    return getNameList().get(getNameList().size()-1);
  }

  // TODO RE: Dieser Rumpf ist Illegal
  @Override
  public List<String> getNameList() {
    ImmutableList.Builder<String> nameList = new ImmutableList.Builder<String>();

    nameList.addAll(getMCBasicGenericType().getNameList());

    for(ASTMCInnerType inner : getMCInnerTypeList()) {
      nameList.add(inner.getName());
    }

    return null;
  }

  // TODO RE: Dieser Rumpf ist Illegal
  public void setNameList(List<String> names) {

  }

  // TODO RE: Dieser Rumpf ist Illegal
  @Override
  public List<ASTMCTypeArgument> getMCTypeArgumentList() {
    return null;
  }

  // TODO RE: Dieser Rumpf ist Illegal
  public void setMCTypeArgumentList(List<ASTMCTypeArgument> arguments) {

  }

}
