package de.monticore.statements.mcvardeclarationstatements._symboltable;

import com.google.common.collect.Lists;
import de.monticore.statements.mccommonstatements._ast.ASTJavaModifier;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCModifier;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTLocalVariableDeclaration;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTVariableDeclarator;
import de.monticore.statements.mcvardeclarationstatements._visitor.MCVarDeclarationStatementsVisitor;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbolSurrogate;
import de.monticore.types.check.SymTypeArray;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfNull;
import de.monticore.types.check.SynthesizeSymTypeFromMCFullGenericTypes;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.List;

import static de.monticore.statements.mccommonstatements._ast.ASTConstantsMCCommonStatements.*;

public class MCVarDeclarationStatementsSTCompleteTypes implements MCVarDeclarationStatementsVisitor {

  private MCVarDeclarationStatementsVisitor realThis;

  public MCVarDeclarationStatementsSTCompleteTypes(){
    this.realThis = this;
  }

  @Override
  public MCVarDeclarationStatementsVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(MCVarDeclarationStatementsVisitor realThis) {
    this.realThis = realThis;
  }

  public void endVisit(ASTLocalVariableDeclaration ast) {
    List<FieldSymbol> symbols = Lists.newArrayList();
    for (ASTVariableDeclarator v : ast.getVariableDeclaratorList()) {
      SymTypeExpression simpleType = createTypeLoader(ast.getMCType());
      v.getDeclarator().getSymbol().setType(simpleType);
      symbols.add(v.getDeclarator().getSymbol());
    }
    addModifiersToVariables(symbols, ast.getMCModifierList());
  }

  protected void addModifiersToVariables(List<FieldSymbol> symbols, Iterable<? extends ASTMCModifier> modifiers) {
    for (FieldSymbol symbol : symbols) {
      for (ASTMCModifier modifier : modifiers) {
        if (modifier instanceof ASTJavaModifier) {
          // visibility
          switch (((ASTJavaModifier) modifier).getModifier()) {
            case PUBLIC:
              symbol.setIsPublic(true);
              break;
            case PROTECTED:
              symbol.setIsProtected(true);
              break;
            case PRIVATE:
              symbol.setIsPrivate(true);
              // other variable modifiers as in jls7 8.3.1 Field Modifiers
              break;
            case STATIC:
              symbol.setIsStatic(true);
              break;
            case FINAL:
              symbol.setIsFinal(true);
              break;
            default:
              break;
          }
        }
      }
    }
  }

  private SymTypeExpression createTypeLoader(ASTMCType ast) {
    SynthesizeSymTypeFromMCFullGenericTypes syn = new SynthesizeSymTypeFromMCFullGenericTypes();
    // Start visitor
    ast.accept(syn);
    return syn.getResult().orElse(new SymTypeOfNull());
  }


}
