/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This DeSer reailizes serialization and deserialization of SymTypeExpressions.
 */
public class SymTypeExpressionDeSer {

  /**
   * The singleton that DeSerializes all SymTypeExpressions.
   * It is stateless and can be reused recursively.
   */
  protected static SymTypeExpressionDeSer instance;
  // not realized as static delegator, but only as singleton

  protected SymTypeArrayDeSer symTypeArrayDeSer;

  protected SymTypePrimitiveDeSer symTypePrimitiveDeSer;

  protected SymTypeOfGenericsDeSer symTypeOfGenericsDeSer;

  protected SymTypeOfIntersectionDeSer symTypeOfIntersectionDeSer;

  protected SymTypeOfObjectDeSer symTypeOfObjectDeSer;

  protected SymTypeOfUnionDeSer symTypeOfUnionDeSer;

  protected SymTypeVariableDeSer symTypeVariableDeSer;

  protected SymTypeOfWildcardDeSer symTypeOfWildcardDeSer;

  protected SymTypeOfFunctionDeSer symTypeOfFunctionDeSer;

  protected SymTypeExpressionDeSer() {
    //this is a singleton, do not use constructor
    this.symTypeArrayDeSer = new SymTypeArrayDeSer();
    this.symTypePrimitiveDeSer = new SymTypePrimitiveDeSer();
    this.symTypeOfGenericsDeSer = new SymTypeOfGenericsDeSer();
    this.symTypeOfIntersectionDeSer = new SymTypeOfIntersectionDeSer();
    this.symTypeOfObjectDeSer = new SymTypeOfObjectDeSer();
    this.symTypeOfUnionDeSer = new SymTypeOfUnionDeSer();
    this.symTypeVariableDeSer = new SymTypeVariableDeSer();
    this.symTypeOfWildcardDeSer = new SymTypeOfWildcardDeSer();
    this.symTypeOfFunctionDeSer = new SymTypeOfFunctionDeSer();
  }

  public static void serializeMember(JsonPrinter printer, String memberName,
      SymTypeExpression member) {
    printer.memberJson(memberName, member.printAsJson());
  }

  public static void serializeMember(JsonPrinter printer, String memberName,
      Optional<SymTypeExpression> member) {
    if (member.isPresent()) {
      printer.memberJson(memberName, member.get().printAsJson());
    }
  }

  public static void serializeMember(JsonPrinter printer, String memberName,
      List<SymTypeExpression> member) {
    printer.array(memberName, member, SymTypeExpression::printAsJson);
  }

  public static SymTypeExpression deserializeMember(String memberName, JsonObject json) {
    return getInstance().deserialize(json.getMember(memberName));
  }

  public static Optional<SymTypeExpression> deserializeOptionalMember(String memberName,
      JsonObject json) {
    if (json.hasMember(memberName)) {
      return Optional.of(getInstance().deserialize(json.getMember(memberName)));
    }
    else {
      return Optional.empty();
    }
  }

  public static List<SymTypeExpression> deserializeListMember(String memberName, JsonObject json) {
    List<SymTypeExpression> result = new ArrayList<>();
    if (json.hasMember(memberName)) {
      for (JsonElement e : json.getArrayMember(memberName)) {
        result.add(getInstance().deserialize(e));
      }
    }
    return result;
  }

  public static SymTypeExpressionDeSer getInstance() {
    if (null == instance) {
      instance = new SymTypeExpressionDeSer();
    }
    return instance;
  }

  /**
   * This method can be used to set the instance of the SymTypeExpressionDeSer to a custom suptype
   *
   * @param theInstance
   */
  public static void setInstance(SymTypeExpressionDeSer theInstance) {
    if (null == theInstance) {  //in this case, "reset" to default type
      instance = new SymTypeExpressionDeSer();
    }
    else {
      instance = theInstance;
    }
  }

  public String serialize(SymTypeExpression toSerialize) {
    // this may not be the most optimal implementation,
    // however, we currently do not need more
    // void and null are stored as strings
    if(toSerialize.isNullType()) {
      return "\""+BasicSymbolsMill.NULL +"\"";
    }
    if(toSerialize.isVoidType()) {
      return "\""+BasicSymbolsMill.VOID +"\"";
    }
    if(toSerialize.isArrayType()) {
      return symTypeArrayDeSer.serialize((SymTypeArray)toSerialize);
    }
    if(toSerialize.isFunctionType()) {
      return symTypeOfFunctionDeSer.serialize((SymTypeOfFunction) toSerialize);
    }
    if(toSerialize.isGenericType()) {
      return symTypeOfGenericsDeSer.serialize((SymTypeOfGenerics) toSerialize);
    }
    if(toSerialize.isIntersectionType()) {
      return symTypeOfIntersectionDeSer.serialize((SymTypeOfIntersection) toSerialize);
    }
    if(toSerialize.isObjectType()) {
      return symTypeOfObjectDeSer.serialize((SymTypeOfObject) toSerialize);
    }
    if(toSerialize.isUnionType()) {
      return symTypeOfUnionDeSer.serialize((SymTypeOfUnion)toSerialize);
    }
    if(toSerialize.isPrimitive()) {
      return symTypePrimitiveDeSer.serialize((SymTypePrimitive)toSerialize);
    }
    if(toSerialize.isTypeVariable()) {
      return symTypeVariableDeSer.serialize((SymTypeVariable) toSerialize);
    }
    if(toSerialize.isWildcard()) {
      return symTypeOfWildcardDeSer.serialize((SymTypeOfWildcard) toSerialize);
    }
    Log.error("0x823FD Internal error: Loading ill-structured SymTab: No way to serialize SymType;");
    return null;
  }

  /**
   * This method is a shortcut, as there are many symbolrules indicating that a symbol has a
   * a List of SymTypeExpressions as member.
   *
   * @param serializedMember
   * @return
   */
  public List<SymTypeExpression> deserializeList(JsonElement serializedMember) {
    List<SymTypeExpression> result = new ArrayList<>();
    for (JsonElement e : serializedMember.getAsJsonArray().getValues()) {
      result.add(deserialize(e));
    }
    return result;
  }

  public SymTypeExpression deserialize(String serialized) {
    return deserialize(JsonParser.parse(serialized));
  }

  public SymTypeExpression deserialize(JsonElement serialized) {

    // void and null are stored as strings
    if (serialized.isJsonString()) {
      switch(serialized.getAsJsonString().getValue()){
        case BasicSymbolsMill.NULL:
          return SymTypeExpressionFactory.createTypeOfNull();
        case BasicSymbolsMill.VOID:
          return SymTypeExpressionFactory.createTypeVoid();
      }
    }

    // all other serialized SymTypeExrpressions are json objects with a kind
    if (serialized.isJsonObject()) {
      JsonObject o = serialized.getAsJsonObject();
      switch (JsonDeSers.getKind(o)) {
        case SymTypeArrayDeSer.SERIALIZED_KIND:
          return symTypeArrayDeSer.deserialize(o);
        case SymTypePrimitiveDeSer.SERIALIZED_KIND:
          return symTypePrimitiveDeSer.deserialize(o);
        case SymTypeOfGenericsDeSer.SERIALIZED_KIND:
          return symTypeOfGenericsDeSer.deserialize(o);
        case SymTypeOfIntersectionDeSer.SERIALIZED_KIND:
          return symTypeOfIntersectionDeSer.deserialize(o);
        case SymTypeOfObjectDeSer.SERIALIZED_KIND:
          return symTypeOfObjectDeSer.deserialize(o);
        case SymTypeOfUnionDeSer.SERIALIZED_KIND:
          return symTypeOfUnionDeSer.deserialize(o);
        case SymTypeVariableDeSer.SERIALIZED_KIND:
          return symTypeVariableDeSer.deserialize(o);
        case SymTypeOfWildcardDeSer.SERIALIZED_KIND:
          return symTypeOfWildcardDeSer.deserialize(o);
        case SymTypeOfFunctionDeSer.SERIALIZED_KIND:
          return symTypeOfFunctionDeSer.deserialize(o);
      }
    }

    Log.error(
        "0x823FE Internal error: Loading ill-structured SymTab: Unknown serialization of SymTypeExpression: "
            + serialized);
    return null;
  }

}
