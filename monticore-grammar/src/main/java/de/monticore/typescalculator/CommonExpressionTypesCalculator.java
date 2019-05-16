package de.monticore.typescalculator;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.EMethodSymbol;
import de.monticore.expressions.prettyprint2.CommonExpressionsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mcbasictypes._symboltable.MCTypeSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.*;

public class CommonExpressionTypesCalculator extends ExpressionsBasisTypesCalculator implements CommonExpressionsVisitor {

  private CommonExpressionsVisitor realThis;

  @Override
  public void setRealThis(CommonExpressionsVisitor realThis){
    this.realThis=realThis;
  }

  @Override
  public CommonExpressionsVisitor getRealThis(){
    return realThis;
  }

  public CommonExpressionTypesCalculator(){
    realThis=this;
  }

  @Override
  public void endVisit(ASTPlusExpression expr){
    ASTMCType result = calculateTypeArithmetic(expr.getLeft(),expr.getRight());
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error("0xA0188 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTMultExpression expr){
    ASTMCType result = calculateTypeArithmetic(expr.getLeft(),expr.getRight());
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error("0xA0189 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTDivideExpression expr){
    ASTMCType result = calculateTypeArithmetic(expr.getLeft(),expr.getRight());
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error("0xA0190 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTMinusExpression expr){
    ASTMCType result = calculateTypeArithmetic(expr.getLeft(),expr.getRight());
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error("0xA0191 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTModuloExpression expr){
    ASTMCType result = calculateTypeArithmetic(expr.getLeft(),expr.getRight());
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error("0xA0192 The resulting type cannot be calculated");
    }
  }
  
  @Override
  public void endVisit(ASTLessEqualExpression expr){
    ASTMCType result = calculateTypeCompare(expr.getLeft(),expr.getRight());
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error("0xA0193 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTGreaterEqualExpression expr){
    ASTMCType result = calculateTypeCompare(expr.getLeft(),expr.getRight());
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error("0xA0194 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTLessThanExpression expr){
    ASTMCType result = calculateTypeCompare(expr.getLeft(),expr.getRight());
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error("0xA0195 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTGreaterThanExpression expr){
    ASTMCType result = calculateTypeCompare(expr.getLeft(),expr.getRight());
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error("0xA0196 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTEqualsExpression expr){
    ASTMCType result = calculateTypeLogical(expr.getLeft(),expr.getRight());
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error("0xA0197 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTNotEqualsExpression expr){
    ASTMCType result = calculateTypeLogical(expr.getLeft(),expr.getRight());
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error("0xA0198 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTBooleanAndOpExpression expr){
    ASTMCType result = null;
    if(types.containsKey(expr.getLeft())&&types.containsKey(expr.getRight())) {
      if (types.get(expr.getLeft()).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build()) && types.get(expr.getRight()).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build())) {
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build();
      }
    }
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error("0xA0199 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTBooleanOrOpExpression expr){
    ASTMCType result = null;
    if(types.containsKey(expr.getLeft())&&types.containsKey(expr.getRight())) {
      if (types.get(expr.getLeft()).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build()) && types.get(expr.getRight()).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build())) {
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build();
      }
    }
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error("0xA0200 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTLogicalNotExpression expr) {
    ASTMCType result = null;
    if(types.containsKey(expr.getExpression())){
      if (types.get(expr.getExpression()).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build())) {
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build();
      }
    }
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error("0xA0201 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTBracketExpression expr){
    ASTMCType result = null;
    if(types.containsKey(expr.getExpression())){
      result=types.get(expr.getExpression()).getASTMCType();
    }
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error("0xA0202 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTSimpleAssignmentExpression expr){
    ASTMCType result = null;
    if(types.containsKey(expr.getLeft())&&types.containsKey(expr.getRight())){
      if(types.get(expr.getLeft()).deepEquals(types.get(expr.getRight()))){
        result=types.get(expr.getRight()).getASTMCType();
      }else if(types.get(expr.getLeft()).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build())&&types.get(expr.getRight()).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build())){
        result=MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build();
      }
    }
    if(result!=null){
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr,sym);
      this.result=result;
    }else{
      Log.error("0xA0203 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTConditionalExpression expr){
    ASTMCType result = null;
    if(types.containsKey(expr.getTrueExpression())&&types.containsKey(expr.getFalseExpression())){
      if(types.containsKey(expr.getCondition())&&types.get(expr.getCondition()).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build())){
        result = calculateTypeArithmetic(expr.getTrueExpression(),expr.getFalseExpression());
      }
    }
    if(result!=null){
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      this.result=result;
      types.put(expr,sym);
    }else{
      Log.error("0xA0204 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTBooleanNotExpression expr){
    ASTMCType result = null;
    if(types.containsKey(expr.getExpression())){
      if(types.get(expr.getExpression()).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build())){
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build();
      }else if(types.get(expr.getExpression()).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.LONG).build())){
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.LONG).build();
      }
    }
    if(result!=null){
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      this.result=result;
      types.put(expr,sym);
    }else{
      Log.error("0xA0205 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTCallExpression expr){
    if(types.containsKey(expr.getExpression())) {
      CommonExpressionsPrettyPrinter printer = new CommonExpressionsPrettyPrinter(new IndentPrinter());
      String exprString = printer.prettyprint(expr);
      Collection<EMethodSymbol> methodcollection = scope.resolveEMethodMany(exprString);
      List<EMethodSymbol> methodlist = new ArrayList<>(methodcollection);
      for (EMethodSymbol method : methodlist) {
        if (expr.getArguments().getExpressionList().size()==method.getArguments().size()){
          boolean success = true;
          for(int i=0;i<method.getArguments().size();i++){
            if(!method.getArguments().get(i).getASTMCType().deepEquals(types.get(expr.getArguments().getExpressionList().get(i)))){
              success = false;
            }
          }
          if(success){
            String nameString = printer.prettyprint(expr.getExpression());
            if(method.getReturnType().isPresentMCType()&& (scope.resolveEType(nameString).isPresent()||scope.resolveEVariable(nameString).isPresent())){
              ASTMCType result=method.getReturnType().getMCType();
              this.result=result;
              MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
              sym.setASTMCType(result);
              types.put(expr,sym);
            }else if(method.getReturnType().isPresentMCVoidType()&&(scope.resolveEVariable(nameString).isPresent()||scope.resolveEType(nameString).isPresent())){
              List<String> name = new ArrayList<>();
              name.add("void");
              ASTMCType result = MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build();
              this.result=result;
              MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
              sym.setASTMCType(result);
              types.put(expr,sym);
            }else{
              Log.error("0xA209 the resulting type cannot be resolved");
            }
          }else{
            Log.error("0xA209 the resulting type cannot be resolved");
          }
        }
      }
    }else{
      Log.info("package suspected","CommonExpressionTypesCalculator");
    }
  }

  private ASTMCType calculateTypeArithmetic(ASTExpression left, ASTExpression right){
    ASTMCType result = null;
    if(types.containsKey(left)&&types.containsKey(right)) {
      if (types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build()) && types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build())) {
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build();
      }else if(types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build()) && types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build())){
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build();
      }else if(types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.FLOAT).build()) && types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.FLOAT).build())){
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.FLOAT).build();
      }else if(types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.LONG).build()) && types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.LONG).build())){
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.LONG).build();
      }else if(types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build()) && types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build())
            || types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build()) && types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build())){
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build();
      }
    }
    return result;
  }

  private ASTMCType calculateTypeCompare(ASTExpression left, ASTExpression right){
    ASTMCType result = null;
    if(types.containsKey(left)&&types.containsKey(right)) {
      if (types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build()) && types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build())) {
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build();
      }else if (types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build()) && types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build())) {
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build();
      }else if (types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.FLOAT).build()) && types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.FLOAT).build())) {
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build();
      }else if (types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.LONG).build()) && types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.LONG).build())) {
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build();
      }else if(types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build()) && types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build())
            || types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build()) && types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build())) {
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build();
      }
    }
    return result;
  }

  private ASTMCType calculateTypeLogical(ASTExpression left, ASTExpression right) {
    ASTMCType result = null;

    if(types.containsKey(left)&&types.containsKey(right)) {
      if (types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build()) && types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build())) {
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build();
      }else if (types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build()) && types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build())) {
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build();
      }else if (types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.FLOAT).build()) && types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.FLOAT).build())) {
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build();
      }else if (types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.LONG).build()) && types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.LONG).build())) {
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build();
      }else if(types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build()) && types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build())
          || types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build()) && types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build())) {
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build();
      }
      else if (types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build()) && types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build())) {
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build();
      }
    }
    return result;
  }

  public ASTMCType calculateType(ASTExpression expr){
    expr.accept(realThis);
    return types.get(expr).getASTMCType();
  }

  public void setTypes(Map<ASTNode,MCTypeSymbol> types){
    this.types=types;
  }
  //TODO: es fehlen noch LiteralExpr, CallExpr, NameExpr und QualifiedNameExpr, bisher nur fuer double und int alles implementiert
}
