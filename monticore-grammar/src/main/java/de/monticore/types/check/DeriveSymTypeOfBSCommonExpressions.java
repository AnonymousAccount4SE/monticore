/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsHandler;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.*;
import de.monticore.symboltable.ISymbol;
import de.se_rwth.commons.logging.Log;

import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.types.check.TypeCheck.*;

public class DeriveSymTypeOfBSCommonExpressions extends AbstractDeriveFromExpression implements CommonExpressionsVisitor2, CommonExpressionsHandler {

  protected CommonExpressionsTraverser traverser;

  @Override
  public CommonExpressionsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(CommonExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void traverse(ASTPlusPrefixExpression expr) {
    SymTypeExpression innerResult = acceptThisAndReturnSymTypeExpression(expr.getExpression());
    if(!innerResult.isObscureType()){
      SymTypeExpression wholeResult = calculatePlusPrefixExpression(innerResult);
      storeResultOrLogError(wholeResult, expr, "0xA0174");
    }
  }

  protected SymTypeExpression calculatePlusPrefixExpression(SymTypeExpression innerResult) {
    return getBitUnaryNumericPromotionType(innerResult);
  }

  @Override
  public void traverse(ASTMinusPrefixExpression expr) {
    SymTypeExpression innerResult = acceptThisAndReturnSymTypeExpression(expr.getExpression());
    if(!innerResult.isObscureType()){
      SymTypeExpression wholeResult = calculateMinusPrefixExpression(innerResult);
      storeResultOrLogError(wholeResult, expr, "0xA0175");
    }
  }

  protected SymTypeExpression calculateMinusPrefixExpression(SymTypeExpression innerResult) {
    return getBitUnaryNumericPromotionType(innerResult);
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTPlusExpression expr) {
    //the "+"-operator also allows String
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getLeft(), expr.getRight());
    if(checkNotObscure(innerTypes)){
      //calculate
      SymTypeExpression wholeResult = calculatePlusExpression(expr, innerTypes.get(0), innerTypes.get(1));
      storeResultOrLogError(wholeResult, expr, "0xA0210");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected SymTypeExpression calculatePlusExpression(ASTPlusExpression expr, SymTypeExpression left, SymTypeExpression right) {
    return getBinaryNumericPromotionWithString(right, left);
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTMultExpression expr) {
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getLeft(), expr.getRight());
    if(checkNotObscure(innerTypes)){
      //calculate
      SymTypeExpression wholeResult = calculateMultExpression(expr, innerTypes.get(0), innerTypes.get(1));
      storeResultOrLogError(wholeResult, expr, "0xA0211");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected SymTypeExpression calculateMultExpression(ASTMultExpression expr, SymTypeExpression left, SymTypeExpression right) {
    return getBinaryNumericPromotion(right, left);
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTDivideExpression expr) {
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getLeft(), expr.getRight());
    if(checkNotObscure(innerTypes)){
      //calculate
      SymTypeExpression wholeResult = calculateDivideExpression(expr, innerTypes.get(0), innerTypes.get(1));
      storeResultOrLogError(wholeResult, expr, "0xA0212");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected SymTypeExpression calculateDivideExpression(ASTDivideExpression expr, SymTypeExpression left, SymTypeExpression right) {
    return getBinaryNumericPromotion(right, left);
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void endVisit(ASTMinusExpression expr) {
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getLeft(), expr.getRight());
    if(checkNotObscure(innerTypes)){
      //calculate
      SymTypeExpression wholeResult = calculateMinusExpression(expr, innerTypes.get(0), innerTypes.get(1));
      storeResultOrLogError(wholeResult, expr, "0xA0213");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected SymTypeExpression calculateMinusExpression(ASTMinusExpression expr, SymTypeExpression left, SymTypeExpression right) {
    return getBinaryNumericPromotion(right, left);
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void endVisit(ASTModuloExpression expr) {
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getLeft(), expr.getRight());
    if(checkNotObscure(innerTypes)){
      //calculate
      SymTypeExpression wholeResult = calculateModuloExpression(expr, innerTypes.get(0), innerTypes.get(1));
      storeResultOrLogError(wholeResult, expr, "0xA0214");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected SymTypeExpression calculateModuloExpression(ASTModuloExpression expr, SymTypeExpression left, SymTypeExpression right) {
    return getBinaryNumericPromotion(right, left);
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTLessEqualExpression expr) {
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getLeft(), expr.getRight());
    if(checkNotObscure(innerTypes)){
      //calculate
      SymTypeExpression wholeResult = calculateLessEqualExpression(expr, innerTypes.get(0), innerTypes.get(1));
      storeResultOrLogError(wholeResult, expr, "0xA0215");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected SymTypeExpression calculateLessEqualExpression(ASTLessEqualExpression expr, SymTypeExpression left, SymTypeExpression right) {
    return calculateTypeCompare(expr, right, left);
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTGreaterEqualExpression expr) {
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getLeft(), expr.getRight());
    if(checkNotObscure(innerTypes)){
      //calculate
      SymTypeExpression wholeResult = calculateGreaterEqualExpression(expr, innerTypes.get(0), innerTypes.get(1));
      storeResultOrLogError(wholeResult, expr, "0xA0216");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected SymTypeExpression calculateGreaterEqualExpression(ASTGreaterEqualExpression expr, SymTypeExpression left, SymTypeExpression right) {
    return calculateTypeCompare(expr, right, left);
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTLessThanExpression expr) {
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getLeft(), expr.getRight());
    if(checkNotObscure(innerTypes)){
      //calculate
      SymTypeExpression wholeResult = calculateLessThanExpression(expr, innerTypes.get(0), innerTypes.get(1));
      storeResultOrLogError(wholeResult, expr, "0xA0217");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected SymTypeExpression calculateLessThanExpression(ASTLessThanExpression expr, SymTypeExpression left, SymTypeExpression right) {
    return calculateTypeCompare(expr, right, left);
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTGreaterThanExpression expr) {
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getLeft(), expr.getRight());
    if(checkNotObscure(innerTypes)){
      //calculate
      SymTypeExpression wholeResult = calculateGreaterThanExpression(expr, innerTypes.get(0), innerTypes.get(1));
      storeResultOrLogError(wholeResult, expr, "0xA0218");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected SymTypeExpression calculateGreaterThanExpression(ASTGreaterThanExpression expr, SymTypeExpression left, SymTypeExpression right) {
    return calculateTypeCompare(expr, right, left);
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTEqualsExpression expr) {
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getLeft(), expr.getRight());
    if(checkNotObscure(innerTypes)){
      //calculate
      SymTypeExpression wholeResult = calculateEqualsExpression(expr, innerTypes.get(0), innerTypes.get(1));
      storeResultOrLogError(wholeResult, expr, "0xA0219");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected SymTypeExpression calculateEqualsExpression(ASTEqualsExpression expr, SymTypeExpression left, SymTypeExpression right) {
    return calculateTypeLogical(expr, right, left);
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTNotEqualsExpression expr) {
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getLeft(), expr.getRight());
    if(checkNotObscure(innerTypes)){
      //calculate
      SymTypeExpression wholeResult = calculateNotEqualsExpression(expr, innerTypes.get(0), innerTypes.get(1));
      storeResultOrLogError(wholeResult, expr, "0xA0220");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected SymTypeExpression calculateNotEqualsExpression(ASTNotEqualsExpression expr, SymTypeExpression left, SymTypeExpression right) {
    return calculateTypeLogical(expr, right, left);
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTBooleanAndOpExpression expr) {
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getLeft(), expr.getRight());
    if(checkNotObscure(innerTypes)){
      //calculate
      SymTypeExpression wholeResult = calculateBooleanAndOpExpression(innerTypes.get(0), innerTypes.get(1));
      storeResultOrLogError(wholeResult, expr, "0xA0223");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected SymTypeExpression calculateBooleanAndOpExpression(SymTypeExpression leftResult, SymTypeExpression rightResult) {
    return calculateLogicalOrOpAndOp(leftResult, rightResult);
  }

  @Override
  public void endVisit(ASTBooleanOrOpExpression expr) {
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getLeft(), expr.getRight());
    if(checkNotObscure(innerTypes)){
      //calculate
      SymTypeExpression wholeResult = calculateBooleanOrOpExpression(innerTypes.get(0), innerTypes.get(1));
      storeResultOrLogError(wholeResult, expr, "0xA0226");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected SymTypeExpression calculateBooleanOrOpExpression(SymTypeExpression leftResult, SymTypeExpression rightResult) {
    return calculateLogicalOrOpAndOp(leftResult, rightResult);
  }

  protected SymTypeExpression calculateLogicalOrOpAndOp(SymTypeExpression leftResult, SymTypeExpression rightResult) {
    SymTypeExpression wholeResult = SymTypeExpressionFactory.createObscureType();
    if (isBoolean(leftResult) && isBoolean(rightResult)) {
      wholeResult = SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN);
    }
    return wholeResult;
  }

  /**
   * We use traverse to collect the result of the inner part of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTLogicalNotExpression expr) {
    SymTypeExpression innerResult = acceptThisAndReturnSymTypeExpression(expr.getExpression());
    if(!innerResult.isObscureType()) {
      SymTypeExpression wholeResult = calculateLogicalNotExpression(innerResult);
      storeResultOrLogError(wholeResult, expr, "0xA0228");
    }
  }

  protected SymTypeExpression calculateLogicalNotExpression(SymTypeExpression innerResult) {
    SymTypeExpression wholeResult = SymTypeExpressionFactory.createObscureType();
    if (isBoolean(innerResult)) {
      wholeResult = SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN);
    }
    return wholeResult;
  }

  /**
   * We use traverse to collect the results of the three parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTConditionalExpression expr) {
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getCondition(), expr.getTrueExpression(), expr.getFalseExpression());
    if(checkNotObscure(innerTypes)){
      SymTypeExpression wholeResult = calculateConditionalExpressionType(innerTypes.get(0), innerTypes.get(1), innerTypes.get(2));
      storeResultOrLogError(wholeResult, expr, "0xA0234");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected SymTypeExpression calculateConditionalExpressionType(SymTypeExpression conditionResult,
                                                                           SymTypeExpression trueResult,
                                                                           SymTypeExpression falseResult) {
    SymTypeExpression wholeResult = SymTypeExpressionFactory.createObscureType();
    //condition has to be boolean
    if (isBoolean(conditionResult)) {
      //check if "then" and "else" are either from the same type or are in sub-supertype relation
      if (compatible(trueResult, falseResult)) {
        wholeResult = trueResult;
      } else if (compatible(falseResult, trueResult)) {
        wholeResult = falseResult;
      } else {
        // first argument can be null since it should not be relevant to the type calculation
        wholeResult = getBinaryNumericPromotion(trueResult, falseResult);
      }
    }
    return wholeResult;
  }

  /**
   * We use traverse to collect the result of the inner part of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTBooleanNotExpression expr) {
    SymTypeExpression innerResult = acceptThisAndReturnSymTypeExpression(expr.getExpression());
    if(!innerResult.isObscureType()) {
      SymTypeExpression wholeResult = calculateBooleanNotExpression(innerResult);
      storeResultOrLogError(wholeResult, expr, "0xA0236");
    }
  }

  protected SymTypeExpression calculateBooleanNotExpression(SymTypeExpression innerResult) {
    SymTypeExpression wholeResult = SymTypeExpressionFactory.createObscureType();
    //the inner result has to be an integral type
    if (isIntegralType(innerResult)) {
      wholeResult = getUnaryIntegralPromotionType(getTypeCheckResult().getResult());
    }
    return wholeResult;
  }

  /**
   * Checks whether the expression has the form of a valid qualified java name.
   */
  protected boolean isQualifiedName(ASTFieldAccessExpression expr) {
    ASTExpression currentExpr = expr;

    // Iterate over subexpressions to check whether they are of the form NameExpression ("." FieldAccessExpression)*
    // Therefore, NameExpression will terminate the traversal, indicating that the expression is a valid name.
    // If the pattern is broken by another expression, then the expression is no valid name, and we terminate.
    while(!(currentExpr instanceof ASTNameExpression)) {
      if(currentExpr instanceof ASTFieldAccessExpression) {
        currentExpr = ((ASTFieldAccessExpression) currentExpr).getExpression();
      } else {
        return false;
      }
    }
    return true;
  }

  /**
   * Checks whether the expression has the form of a valid qualified, or unqualified, java name.
   */
  protected boolean isName(ASTExpression expr) {
    return expr instanceof ASTNameExpression ||
      (expr instanceof ASTFieldAccessExpression && isQualifiedName((ASTFieldAccessExpression) expr));
  }

  /**
   * We use traverse to collect the result of the inner part of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTFieldAccessExpression expr) {
    if(isQualifiedName(expr) && expr.getEnclosingScope() instanceof IBasicSymbolsScope) {
      calculateNamingChainFieldAccess(expr);
    } else {
      calculateArithmeticFieldAccessExpression(expr);
    }
  }

  /**
   * Calculate the type result of FieldAccessExpressions that represent qualified names and cascading field accesses.
   * E.g., pac.kage.Type.staticMember, or, localField.innerField.furtherNestedField.
   * But not: pac.kage.Type.staticMethod().innerField, as here <i>innerField</i> is not only qualified by names, but
   * it is based on the access of a value returned by a CallExpression.
   * @param expr The only valid sub expressions of the FieldAccessExpression are other FieldAccessExpressions, and
   *             a {@link ASTNameExpression} that is the end of the field access chain.
   */
  protected void calculateNamingChainFieldAccess(ASTFieldAccessExpression expr) {
    Optional<List<ASTExpression>> astNamePartsOpt = collectSubExpressions(expr);

    if (!astNamePartsOpt.isPresent()) {
      Log.error("0x0xA2310 (Internal error) The qualified name parts of a FieldAccessExpression can not be " +
        "calculated as the field access expression is no qualified name. " + expr.get_SourcePositionStart().toString());
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());

    } else {
      // We will incrementally try to build our result:
      // We will start with the first name part and check whether it resolves to an entity.
      // Then we will check whether further field accesses are (nested) accesses on that entity's members.
      // When there is no such entity, or it does not have a member we were looking for, then we try to resolve the
      // qualified name up to the part of the name where we currently are.
      List<ASTExpression> astNameParts = astNamePartsOpt.get();

      getTypeCheckResult().reset();
      for(int i = 0; i < astNameParts.size(); i++) {
        if(getTypeCheckResult().isPresentResult() && !getTypeCheckResult().getResult().isObscureType()) {
          calculateFieldAccess((ASTFieldAccessExpression) astNameParts.get(i), true);
        } else {
          calculatedQualifiedEntity(astNameParts.subList(0, i + 1));
        }
      }

      if(!getTypeCheckResult().isPresentResult() || getTypeCheckResult().getResult().isObscureType()) {
        logError("0xA0241", expr.get_SourcePositionStart());
      }
    }
  }

  /**
   * Calculate the type result of FieldAccessExpressions that do not represent qualified names.
   */
  protected void calculateArithmeticFieldAccessExpression(ASTFieldAccessExpression expr) {
    expr.getExpression().accept(getTraverser());
    if (getTypeCheckResult().isPresentResult() && !getTypeCheckResult().getResult().isObscureType()) {
      calculateFieldAccess(expr, false);
    } // else do nothing (as the type check result is already absent or obscure).
  }

  /**
   * Calculates the type result of the field access expression, given that the type result of the accessed entity's
   * owner has already been computed (and is accessible via getTypeCheckResult()).
   * @param quiet Prevents the logging of errors if no entity is found that could be accessed, i.e., if the field access
   *              is invalid and the calculation of a result is not possible.
   */
  protected void calculateFieldAccess(ASTFieldAccessExpression expr,
                                                 boolean quiet) {
    TypeCheckResult fieldOwner = getTypeCheckResult().copy();
    SymTypeExpression fieldOwnerExpr = fieldOwner.getResult();
    TypeSymbol fieldOwnerSymbol = fieldOwnerExpr.getTypeInfo();
    if (fieldOwnerSymbol instanceof TypeVarSymbol && !quiet) {
      Log.error("0xA0321 The type " + fieldOwnerSymbol.getName() + " is a type variable and cannot have methods and attributes");
    }
    //search for a method, field or type in the scope of the type of the inner expression
    List<VariableSymbol> fieldSymbols = getCorrectFieldsFromInnerType(fieldOwnerExpr, expr);
    Optional<TypeSymbol> typeSymbolOpt = fieldOwnerSymbol.getSpannedScope().resolveType(expr.getName());
    Optional<TypeVarSymbol> typeVarOpt = fieldOwnerSymbol.getSpannedScope().resolveTypeVar(expr.getName());

    if (!fieldSymbols.isEmpty()) {
      //cannot be a method, test variable first
      //durch AST-Umbau kann ASTFieldAccessExpression keine Methode sein
      //if the last result is a type then filter for static field symbols
      if (fieldOwner.isType()) {
        fieldSymbols = filterModifiersVariables(fieldSymbols);
      }
      if (fieldSymbols.size() != 1) {
        getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
        if(!quiet) {
          logError("0xA1236", expr.get_SourcePositionStart());
        }
      }
      if (!fieldSymbols.isEmpty()) {
        VariableSymbol var = fieldSymbols.get(0);
        expr.setDefiningSymbol(var);
        SymTypeExpression type = var.getType();
        getTypeCheckResult().setField();
        getTypeCheckResult().setResult(type);
      }
    } else if (typeVarOpt.isPresent()) {
      //test for type var first
      TypeVarSymbol typeVar = typeVarOpt.get();
      if(checkModifierType(typeVar)){
        SymTypeExpression wholeResult = SymTypeExpressionFactory.createTypeVariable(typeVar);
        expr.setDefiningSymbol(typeVar);
        getTypeCheckResult().setType();
        getTypeCheckResult().setResult(wholeResult);
      } else{
        getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
        if(!quiet) {
          logError("0xA1306", expr.get_SourcePositionStart());
        }
      }
    } else if (typeSymbolOpt.isPresent()) {
      //no variable found, test type
      TypeSymbol typeSymbol = typeSymbolOpt.get();
      if (checkModifierType(typeSymbol)) {
        SymTypeExpression wholeResult = SymTypeExpressionFactory.createTypeExpression(typeSymbol);
        expr.setDefiningSymbol(typeSymbol);
        getTypeCheckResult().setType();
        getTypeCheckResult().setResult(wholeResult);
      } else {
        getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
        if(!quiet) {
          logError("0xA1303", expr.get_SourcePositionStart());
        }
      }
    } else {
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
      if(!quiet) {
        logError("0xA1317", expr.get_SourcePositionStart());
      }
    }
  }

  /**
   * Hookpoint for object oriented languages to get the correct variables/fields from a type based on their modifiers
   */
  protected List<VariableSymbol> getCorrectFieldsFromInnerType(SymTypeExpression innerResult, ASTFieldAccessExpression expr) {
    return innerResult.getFieldList(expr.getName(), getTypeCheckResult().isType(), true);
  }

  /**
   * Hookpoint for object oriented languages that offer modifiers like static, public, private, ...
   */
  protected boolean checkModifierType(TypeSymbol typeSymbol){
    return true;
  }

  /**
   * Hookpoint for object oriented languages that offer modifiers like static, public, private, ...
   */
  protected List<VariableSymbol> filterModifiersVariables(List<VariableSymbol> variableSymbols) {
    return variableSymbols;
  }

  /**
   * Transforms {@link ASTNameExpression}s and {@link ASTFieldAccessExpression}s into the names they represent (for
   * field access expressions it takes the name of the accessed field/entity).
   */
  protected String astNameToString(ASTExpression expression) {
    if(expression instanceof ASTNameExpression) {
      return ((ASTNameExpression) expression).getName();
    } else if (expression instanceof ASTFieldAccessExpression) {
      return ((ASTFieldAccessExpression) expression).getName();
    } else {
      throw new IllegalArgumentException();
    }
  }

  /**
   * If the FieldAccessExpression represents a qualified name, then this method returns its name parts.
   * Else an empty optional is returned.
   */
  protected Optional<List<ASTExpression>> collectSubExpressions(ASTFieldAccessExpression expr) {
    ASTExpression currentExpr = expr;
    List<ASTExpression> nameParts = new LinkedList<>();

    while(!(currentExpr instanceof ASTNameExpression)) {
      if(currentExpr instanceof ASTFieldAccessExpression) {
        ASTFieldAccessExpression curExpr = (ASTFieldAccessExpression) currentExpr;
        nameParts.add(0, curExpr);

        currentExpr = curExpr.getExpression();  // Advance iteration
      } else {
        return Optional.empty();
      }
    }

    // Do not forget to add the terminal NameExpression
    nameParts.add(0, currentExpr);
    return Optional.of(nameParts);
  }

  /**
   * Tries to resolve the given name parts to a variable, type variable, or type and if a symbol is found, then
   * it(s type) is set as the current type check result.
   * If no symbol is found, then nothing happens (no error logged, no altering of the type check result).
   * If multiple fields are found, then the result is set to obscure, and an error is logged.
   * Variables take precedence over types variables that take precedence over
   * types.
   * @param astNameParts Expressions that represent a qualified identification of a {@link VariableSymbol},
   *                  {@link TypeVarSymbol}, or {@link TypeSymbol}. Therefore, the list that must contain a
   *                  {@code NameExpression} at the beginning, followed only by {@code FieldAccessExpression}s.
   */
  protected void calculatedQualifiedEntity(List<ASTExpression> astNameParts) {
    List<String> nameParts = astNameParts.stream().map(this::astNameToString).collect(Collectors.toList());
    String qualName = String.join(".", nameParts);
    ASTExpression lastExpr = astNameParts.get(astNameParts.size() - 1);

    List<VariableSymbol> fieldSymbols = getScope(lastExpr.getEnclosingScope()).resolveVariableMany(qualName);
    Optional<TypeSymbol> typeSymbolOpt = getScope(lastExpr.getEnclosingScope()).resolveType(qualName);
    Optional<TypeVarSymbol> typeVarOpt = getScope(lastExpr.getEnclosingScope()).resolveTypeVar(qualName);

    if (!fieldSymbols.isEmpty()) {
      if (fieldSymbols.size() != 1) {
        logError("0xA1236", lastExpr.get_SourcePositionStart());
        getTypeCheckResult().reset();
        getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
      } else {
        VariableSymbol var = fieldSymbols.get(0);
        trySetDefiningSymbolOrError(lastExpr, var);
        SymTypeExpression type = var.getType();
        getTypeCheckResult().setField();
        getTypeCheckResult().setResult(type);
      }

    } else if (typeVarOpt.isPresent()) {
      TypeVarSymbol typeVar = typeVarOpt.get();
      SymTypeExpression type = SymTypeExpressionFactory.createTypeVariable(typeVar);
      trySetDefiningSymbolOrError(lastExpr, typeVar);
      getTypeCheckResult().setType();
      getTypeCheckResult().setResult(type);

    } else if (typeSymbolOpt.isPresent()) {
      TypeSymbol typeSymbol = typeSymbolOpt.get();
      SymTypeExpression type = SymTypeExpressionFactory.createTypeExpression(typeSymbol);
      trySetDefiningSymbolOrError(lastExpr, typeSymbol);
      getTypeCheckResult().setType();
      getTypeCheckResult().setResult(type);
    }
  }

  /**
   * If {@code expr} is of type {@link ASTNameExpression}, {@link ASTFieldAccessExpression}, or
   * {@link ASTCallExpression}, then {@code definingSymbol} is set as its defining symbol. Else, an error is logged.
   */
  protected void trySetDefiningSymbolOrError(ASTExpression expr, ISymbol definingSymbol) {
    if(expr instanceof ASTNameExpression) {
      ((ASTNameExpression) expr).setDefiningSymbol(definingSymbol);
    } else if(expr instanceof ASTFieldAccessExpression) {
      ((ASTFieldAccessExpression) expr).setDefiningSymbol(definingSymbol);
    } else if (expr instanceof ASTCallExpression) {
      ((ASTCallExpression) expr).setDefiningSymbol(definingSymbol);
    } else {
      Log.error("0xA2306 (Internal error) tried to set the symbol on an Expression that is none of the following:" +
        "ASTNameExpression, ASTFieldAccessExpression, ASTCallExpression.");
    }
  }

  /**
   * We use traverse to collect the result of the inner part of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTCallExpression expr) {
    if (isName(expr.getExpression()) && expr.getEnclosingScope() instanceof IBasicSymbolsScope) {
      calculateNamingChainCallExpression(expr);
    } else {
      calculateArithmeticCallExpression(expr);
    }
  }

  /**
   * Calculate the type result of call expressions that represent fully qualified method calls (like when calling a
   * static method: {@code pac.kage.Type.staticMethod()}) and methodCalls on cascading field accesses (e.g.,
   * {@code localField.innerField.instanceMethod()}). But not:
   * {@code pac.kage.Type.staticMethod().innerField.instanceMethod()}, as here <i>instanceMethod</i> is not only
   * qualified by names, but it is based on the access of a value returned by a CallExpression.
   * @param expr The only valid sub expressions of the CallExpression are FieldAccessExpressions, or a
   *             {@link ASTNameExpression} that is the leaf of the field access chain, or alternatively defines a local
   *             method call ({@code localMethod("foo")}.
   */
  protected void calculateNamingChainCallExpression(ASTCallExpression expr) {
    Optional<List<ASTExpression>> astNamePartsOpt = Optional.empty();

    if(expr.getExpression() instanceof ASTFieldAccessExpression) {
      ASTFieldAccessExpression qualExpr = (ASTFieldAccessExpression) expr.getExpression();
      astNamePartsOpt = collectSubExpressions(qualExpr);
    } else if(expr.getExpression() instanceof ASTNameExpression){
      ASTNameExpression nameExpr = (ASTNameExpression) expr.getExpression();
      astNamePartsOpt = Optional.of(Collections.singletonList(nameExpr));
    }

    if(astNamePartsOpt.isEmpty()){
      Log.error("0x0xA2312 (Internal error) The (qualified) name parts of a CallExpression can not be " +
        "calculated as the call expression is not defined by a (qualified) name. "
        + expr.get_SourcePositionStart().toString());
      this.getTypeCheckResult().reset();
      this.getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
      return;
    }

    List<ASTExpression> astNameParts = astNamePartsOpt.get();
    List<String> nameParts = astNameParts.stream().map(this::astNameToString).collect(Collectors.toList());
    List<SymTypeExpression> args = calculateArguments(expr, nameParts.get(nameParts.size() - 1));

    // We will incrementally try to build our result:
    // We will start with the first name part and check whether it resolves to an entity.
    // Then we will check whether further field accesses are (nested) accesses on that entity's members.
    // When there is no such entity, or it does not have a member we were looking for, then we try to resolve the
    // qualified name up to the part of the name where we currently are.
    // We terminate *before* the last name part, as the last name part must resolve to a method and not a type or field.

    getTypeCheckResult().reset();
    for(int i = 0; i < astNameParts.size() - 1; i++) {
      if(getTypeCheckResult().isPresentResult() && !getTypeCheckResult().getResult().isObscureType()) {
        calculateFieldAccess((ASTFieldAccessExpression) astNameParts.get(i), true);
      } else {
        calculatedQualifiedEntity(astNameParts.subList(0, i + 1));
      }
    }

    if(getTypeCheckResult().isPresentResult() && !getTypeCheckResult().getResult().isObscureType() && checkNotObscure(args)) {
      calculateCallExpression(expr, args);
    } else {
      // Check whether we have a fully qualified static method.
      String qualNamePrefix = String.join(".", nameParts);
      List<FunctionSymbol> funcSymbols = getScope(expr.getEnclosingScope()).resolveFunctionMany(qualNamePrefix);

      List<FunctionSymbol> methodlist = new ArrayList<>(funcSymbols);
      //count how many methods can be found with the correct arguments and return type
      List<FunctionSymbol> fittingMethods = getFittingMethods(methodlist, expr, args);
      //there can only be one method with the correct arguments and return type
      if (fittingMethods.size() == 1 && checkNotObscure(args)) {
        expr.setDefiningSymbol(fittingMethods.get(0));
        Optional<SymTypeExpression> wholeResult = Optional.of(fittingMethods.get(0).getType());
        getTypeCheckResult().setMethod();
        getTypeCheckResult().setResult(wholeResult.get());
      } else {
        getTypeCheckResult().reset();
        getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
        logError("0xA1242", expr.get_SourcePositionStart());
      }
    }
  }

  /**
   * Calculates the type result of the expression, given that it is not a simple, or qualified method access (e.g.,
   * {@code pac.kage.Owner.staticMethod()}, or {@code isInt()} (a local Method name)).
   */
  protected void calculateArithmeticCallExpression(ASTCallExpression expr) {

    List<SymTypeExpression> args = calculateArguments(expr, astNameToString(expr.getExpression()));

    //make sure that the type of the last argument is not stored in the TypeCheckResult anymore
    getTypeCheckResult().reset();
    expr.getExpression().accept(getTraverser());

    calculateCallExpression(expr, args);
  }

  /**
   * Calculates the type result of the call expression, given that the type result of the method owner has already been
   * computed (and is accessible via getTypeCheckResult()), and that the type of its arguments has already been
   * computed.
   */
  protected void calculateCallExpression(ASTCallExpression expr, List<SymTypeExpression> args) {
    if(!getTypeCheckResult().isPresentResult()
      || getTypeCheckResult().getResult().isObscureType()
      || !checkNotObscure(args)) {
      return;
    }

    String methodName = astNameToString(expr.getExpression());
    SymTypeExpression methodOwnerExpr = getTypeCheckResult().getResult();

    // Filter based on the method modifiers
    List<FunctionSymbol> methodList = getCorrectMethodsFromInnerType(methodOwnerExpr, expr, methodName);
    // Filter based on a compatible signature
    List<FunctionSymbol> fittingMethods = getFittingMethods(methodList, expr, args);
    // If the last result is static then filter for static methods
    if (getTypeCheckResult().isType()) {
      fittingMethods = filterModifiersFunctions(fittingMethods);
    }

    // There can only be one method with the correct arguments and return type
    if (!fittingMethods.isEmpty()) {
      if (fittingMethods.size() > 1) {
        checkForReturnType(expr, fittingMethods);
      }
      expr.setDefiningSymbol(fittingMethods.get(0));
      SymTypeExpression result = fittingMethods.get(0).getType();
      getTypeCheckResult().setMethod();
      getTypeCheckResult().setResult(result);
    } else {
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
      logError("0xA2239", expr.get_SourcePositionStart());
    }
  }

  protected List<SymTypeExpression> calculateArguments(ASTCallExpression expr, String methodName){
    List<SymTypeExpression> returnList = new ArrayList<>();
    for(int i = 0; i < expr.getArguments().sizeExpressions(); i++){
      getTypeCheckResult().reset();
      expr.getArguments().getExpression(i).accept(getTraverser());
      if(getTypeCheckResult().isPresentResult() && !getTypeCheckResult().isType()){
        returnList.add(getTypeCheckResult().getResult());
      }else{
        //Placeholder as no function can have a parameter of type void and so that the correct number of
        //SymTypeExpressions is in the list
        returnList.add(SymTypeExpressionFactory.createObscureType());
      }
    }
    return returnList;
  }

  protected void checkForReturnType(ASTCallExpression expr, List<FunctionSymbol> fittingMethods){
    SymTypeExpression returnType = fittingMethods.get(0).getType();
    for (FunctionSymbol method : fittingMethods) {
      if (!returnType.deepEquals(method.getType())) {
        getTypeCheckResult().reset();
        getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
        logError("0xA1239", expr.get_SourcePositionStart());
      }
    }
  }

  /**
   * Hookpoint for object oriented languages to get the correct functions/methods from a type based on their modifiers
   */
  protected List<FunctionSymbol> getCorrectMethodsFromInnerType(SymTypeExpression innerResult, ASTCallExpression expr, String name) {
    return innerResult.getMethodList(name, getTypeCheckResult().isType(), true);
  }

  protected List<FunctionSymbol> getFittingMethods(List<FunctionSymbol> methodlist, ASTCallExpression expr, List<SymTypeExpression> args) {
    List<FunctionSymbol> fittingMethods = new ArrayList<>();
    for (FunctionSymbol method : methodlist) {
      //for every method found check if the arguments are correct
      if ((!method.isIsElliptic() &&
          args.size() == method.getParameterList().size())
          || (method.isIsElliptic() &&
          args.size() >= method.getParameterList().size() - 1)) {
        boolean success = true;
        for (int i = 0; i < args.size(); i++) {
          //test if every single argument is correct
          //if an argument is void type then it could not be calculated correctly -> see calculateArguments
          SymTypeExpression paramType = method.getParameterList().get(Math.min(i, method.getParameterList().size() - 1)).getType();
          if (!paramType.deepEquals(args.get(i)) &&
            !compatible(paramType, args.get(i)) || args.get(i).isVoidType()) {
            success = false;
          }
        }
        if (success) {
          //method has the correct arguments and return type
          fittingMethods.add(method);
        }
      }
    }
    return fittingMethods;
  }

  /**
   * Hookpoint for object oriented languages that offer modifiers like static, public, private, ...
   */
  protected List<FunctionSymbol> filterModifiersFunctions(List<FunctionSymbol> functionSymbols) {
    return functionSymbols;
  }

  /**
   * helper method for <=, >=, <, > -> calculates the result of these expressions
   */
  protected SymTypeExpression calculateTypeCompare(ASTInfixExpression expr, SymTypeExpression rightResult, SymTypeExpression leftResult) {
    // if the left and the right part of the expression are numerics,
    // then the whole expression is a boolean
    if (isNumericType(leftResult) && isNumericType(rightResult)) {
      return SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN);
    }
    //should never happen, no valid result, error will be handled in traverse
    return SymTypeExpressionFactory.createObscureType();
  }

  /**
   * helper method for the calculation of the ASTEqualsExpression and the ASTNotEqualsExpression
   */
  protected SymTypeExpression calculateTypeLogical(ASTInfixExpression expr, SymTypeExpression rightResult, SymTypeExpression leftResult) {
    //Option one: they are both numeric types
    if (isNumericType(leftResult) && isNumericType(rightResult)
        || isBoolean(leftResult) && isBoolean(rightResult)) {
      return SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN);
    }
    //Option two: none of them is a primitive type and they are either the same type or in a super/sub type relation
    if (!leftResult.isPrimitive() && !rightResult.isPrimitive() &&
        (compatible(leftResult, rightResult) || compatible(rightResult, leftResult))
    ) {
      return SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN);
    }
    //should never happen, no valid result, error will be handled in traverse
    return SymTypeExpressionFactory.createObscureType();
  }

  /**
   * return the result for the five basic arithmetic operations (+,-,*,/,%)
   */
  protected SymTypeExpression getBinaryNumericPromotion(SymTypeExpression leftResult, SymTypeExpression rightResult) {
    //if one part of the expression is a double and the other is another numeric type then the result is a double
    if ((isDouble(leftResult) && isNumericType(rightResult)) ||
        (isDouble(rightResult) && isNumericType(leftResult))) {
      return SymTypeExpressionFactory.createPrimitive("double");
      //no part of the expression is a double -> try again with float
    } else if ((isFloat(leftResult) && isNumericType(rightResult)) ||
        (isFloat(rightResult) && isNumericType(leftResult))) {
      return SymTypeExpressionFactory.createPrimitive("float");
      //no part of the expression is a float -> try again with long
    } else if ((isLong(leftResult) && isNumericType(rightResult)) ||
        (isLong(rightResult) && isNumericType(leftResult))) {
      return SymTypeExpressionFactory.createPrimitive("long");
      //no part of the expression is a long -> if both parts are numeric types then the result is a int
    } else if (isIntegralType(leftResult) && isIntegralType(rightResult)
    ) {
      return SymTypeExpressionFactory.createPrimitive("int");
    }
    //should never happen, no valid result, error will be handled in traverse
    return SymTypeExpressionFactory.createObscureType();
  }

  /**
   * return the result for the "+"-operation if Strings
   */
  protected SymTypeExpression getBinaryNumericPromotionWithString(SymTypeExpression rightResult, SymTypeExpression leftResult) {
    //if one part of the expression is a String then the whole expression is a String
    if(isString(leftResult)) {
      return SymTypeExpressionFactory.createTypeObject(leftResult.getTypeInfo());
    }
    if (isString(rightResult)) {
      return SymTypeExpressionFactory.createTypeObject(rightResult.getTypeInfo());
    }
    //no String in the expression -> use the normal calculation for the basic arithmetic operators
    return getBinaryNumericPromotion(leftResult, rightResult);
  }

  /**
   * helper method for the calculation of the ASTBooleanNotExpression
   */
  protected SymTypeExpression getUnaryIntegralPromotionType(SymTypeExpression type) {
    if (type.isPrimitive() && isIntegralType(type)) {
      if (isLong(type)) {
        return SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.LONG);
      } else {
        return SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT);
      }
    }
    return SymTypeExpressionFactory.createObscureType();
  }
}
