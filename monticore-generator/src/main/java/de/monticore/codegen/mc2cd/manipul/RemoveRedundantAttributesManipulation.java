/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.manipul;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.codegen.mc2cd.AttributeCategory;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static de.monticore.codegen.mc2cd.AttributeCategory.determineCategory;

/**
 * Removes duplicate attributes that may result from rules having multiple nonterminals referencing
 * <p>
 * the same rule.
 */
final class RemoveRedundantAttributesManipulation implements UnaryOperator<ASTCDCompilationUnit> {

  @Override
  public ASTCDCompilationUnit apply(ASTCDCompilationUnit cdCompilationUnit) {
    for (ASTCDClass cdClass : cdCompilationUnit.getCDDefinition().getCDClassesList()) {
      removeRedundantAttributes(cdClass.getCDAttributeList());
    }
    for (ASTCDInterface cdClass : cdCompilationUnit.getCDDefinition().getCDInterfacesList()) {
      removeRedundantAttributes(cdClass.getCDAttributeList());
    }
    return cdCompilationUnit;
  }

  /**
   * @param cdAttributes the list of all the attributes in the class
   */
  void removeRedundantAttributes(List<ASTCDAttribute> cdAttributes) {
    Iterator<ASTCDAttribute> iterator = cdAttributes.iterator();
    while (iterator.hasNext()) {
      ASTCDAttribute inspectedAttribute = iterator.next();
      List<ASTCDAttribute> remainingAttributes = cdAttributes
          .stream()
          .filter(attribute -> !attribute.equals(inspectedAttribute))
          .collect(Collectors.toList());
      boolean isRedundant = remainingAttributes
          .stream()
          .anyMatch(a -> isRedundant(inspectedAttribute, a));
      if (isRedundant) {
        iterator.remove();
      }
    }
  }

  /**
   * Checks if the remaining attributes contain an attribute that makes the inspected attribute
   * redundant.
   *
   * @return true if another attribute with the same variable name, the same original type and an
   * equal or higher category exists
   */
  private static boolean isRedundant(ASTCDAttribute inspectedAttribute,
                                     ASTCDAttribute remainingAttribute) {
    String inspectedName = inspectedAttribute.getName();
    String inspectedType = getOriginalTypeName(inspectedAttribute);
    AttributeCategory inspectedCategory = determineCategory(inspectedAttribute);

    boolean sameName = inspectedName.equalsIgnoreCase(remainingAttribute.getName());

    boolean sameType = inspectedType.equals(getOriginalTypeName(remainingAttribute));

    boolean sameOrHigherCategory = inspectedCategory
        .compareTo(AttributeCategory.determineCategory(remainingAttribute)) < 1;

    return sameName && sameType && sameOrHigherCategory;
  }

  private static String getOriginalTypeName(ASTCDAttribute cdAttribute) {
    AttributeCategory category = AttributeCategory.determineCategory(cdAttribute);
    if (category == AttributeCategory.GENERICLIST || category == AttributeCategory.OPTIONAL) {
      Optional<String> firstArgument = getFirstTypeArgument(cdAttribute);
      if (firstArgument.isPresent()) {
        return firstArgument.get();
      }
    }
    return TransformationHelper.typeToString(cdAttribute.getMCType());
  }

  private static Optional<String> getFirstTypeArgument(ASTCDAttribute cdAttribute) {
    // the 'List' in 'List<String>'
    if (cdAttribute.getMCType() instanceof ASTMCGenericType) {
      List<ASTMCTypeArgument> argList = ((ASTMCGenericType) cdAttribute.getMCType()).getMCTypeArgumentList();
      if (!argList.isEmpty()) {
        String simpleTypeName = argList.get(0).getMCTypeOpt().get().printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter()));
        return Optional.of(simpleTypeName);
      }
    }
    return Optional.empty();
  }

}
