/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_INTERFACE;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.I_SCOPE;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.I_SYMBOL;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.END_VISIT;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.GET_REAL_THIS;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.HANDLE;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.HANDLE_TEMPLATE;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.REAL_THIS;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.SET_REAL_THIS;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.TRAVERSE;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.TRAVERSE_SCOPE_TEMPLATE;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.TRAVERSE_TEMPLATE;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.VISIT;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.VISITOR_METHODS_TRAVERSER_DELEGATING_TEMPLATE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDDefinition;
import de.monticore.cd.cd4analysis._ast.ASTCDEnum;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.cd.cd4analysis._ast.ASTCDParameter;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4code.CD4CodeMill;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType;
import de.monticore.types.prettyprint.MCSimpleGenericTypesPrettyPrinter;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;

/**
 * creates a Visitor interface from a grammar
 */
public class TraverserInterfaceDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDInterface> {
  
  protected final VisitorService visitorService;
  
  protected final SymbolTableService symbolTableService;
  
  protected boolean isTop;
  
  public TraverserInterfaceDecorator(final GlobalExtensionManagement glex,
                             final VisitorService visitorService,
                             final SymbolTableService symbolTableService) {
    super(glex);
    this.visitorService = visitorService;
    this.symbolTableService = symbolTableService;
  }

  @Override
  public ASTCDInterface decorate(ASTCDCompilationUnit ast) {
    ASTCDCompilationUnit compilationUnit = visitorService.calculateCDTypeNamesWithASTPackage(ast);
    
    String traverserSimpleName = visitorService.getTraverserInterfaceSimpleName();
    ASTMCQualifiedType traverserType = visitorService.getTraverserInterfaceType();
    
    // get visitor types and names of super cds and own cd
    List<CDDefinitionSymbol> superCDsTransitive = visitorService.getSuperCDsTransitive();
    List<String> visitorFullNameList = superCDsTransitive.stream()
        .map(visitorService::getVisitor2FullName)
        .collect(Collectors.toList());
    visitorFullNameList.add(visitorService.getVisitor2FullName());
    
    // create list of cdDefinitions from superclass and own class
    List<ASTCDDefinition> definitionList = new ArrayList<>();
    definitionList.add(compilationUnit.getCDDefinition());
    definitionList.addAll(superCDsTransitive
        .stream()
        .map(visitorService::calculateCDTypeNamesWithASTPackage)
        .collect(Collectors.toList()));
    
    List<String> visitorSimpleNameList =new ArrayList<>();
    visitorSimpleNameList.addAll(superCDsTransitive.stream()
        .map(visitorService::getVisitorSimpleName)
        .collect(Collectors.toList()));
    
    ASTCDInterface visitorInterface = CD4CodeMill.cDInterfaceBuilder()
        .setName(traverserSimpleName)
        .addAllInterface(this.visitorService.getSuperTraverserInterfaces())
        .setModifier(PUBLIC.build())
        .addCDMethod(addGetRealThisMethods(traverserType))
        .addCDMethod(addSetRealThisMethods(traverserType))
        .addAllCDMethods(addVisitor2Methods(definitionList))
        .addAllCDMethods(createTraverserDelegatingMethods(compilationUnit.getCDDefinition()))
        .addAllCDMethods(addDefaultVisitorMethods(visitorSimpleNameList))
        .build();
    
    return visitorInterface;
  }

  /**
   * Adds the getRealThis method with respect to the TOP mechanism.
   * 
   * @param visitorType The return type of the method
   * @return The decorated getRealThis method
   */
  protected ASTCDMethod addGetRealThisMethods(ASTMCType visitorType) {
    String hookPoint;
    if (!isTop()) {
      hookPoint = "return this;";
    } else {
      hookPoint = "return (" + visitorService.getTraverserInterfaceSimpleName() + ")this;";
    }
    ASTCDMethod getRealThisMethod = this.getCDMethodFacade().createMethod(PUBLIC, visitorType, GET_REAL_THIS);
    this.replaceTemplate(EMPTY_BODY, getRealThisMethod, new StringHookPoint(hookPoint));
    return getRealThisMethod;
  }

  /**
   * Adds the setRealThis method.
   * 
   * @param visitorType The input parameter type
   * @return The decorated setRealThis method
   */
  protected ASTCDMethod addSetRealThisMethods(ASTMCType visitorType) {
    ASTCDParameter visitorParameter = getCDParameterFacade().createParameter(visitorType, REAL_THIS);
    ASTCDMethod setRealThis = this.getCDMethodFacade().createMethod(PUBLIC, SET_REAL_THIS, visitorParameter);
    String generatedErrorCode = visitorService.getGeneratedErrorCode(visitorType.printType(
        new MCSimpleGenericTypesPrettyPrinter(new IndentPrinter())) + SET_REAL_THIS);
    this.replaceTemplate(EMPTY_BODY, setRealThis, new StringHookPoint(
        "    throw new UnsupportedOperationException(\"0xA7012"+generatedErrorCode+" The setter for realThis is " +
            "not implemented. You might want to implement a wrapper class to allow setting/getting realThis.\");\n"));
    return setRealThis;
  }

  /**
   * Adds the non-delegating handle method.
   * 
   * @param astType Type of the handled node
   * @param traverse Flag if the node should be traversed
   * @return The decorated handle method
   */
  protected ASTCDMethod addHandleMethod(ASTMCType astType, boolean traverse) {
    ASTCDMethod handleMethod = visitorService.getVisitorMethod(HANDLE, astType);
    this.replaceTemplate(EMPTY_BODY, handleMethod, new TemplateHookPoint(HANDLE_TEMPLATE, traverse));
    return handleMethod;
  }

  /**
   * Adds the non-delegating traverse method.
   * 
   * @param astType Type of the handled node
   * @param astcdClass The class, which attributes are traversed
   * @return The decorated traverse method
   */
  protected ASTCDMethod addTraversMethod(ASTMCType astType, ASTCDClass astcdClass) {
    ASTCDMethod traverseMethod = visitorService.getVisitorMethod(TRAVERSE, astType);
    boolean isScopeSpanningSymbol = symbolTableService.hasScopeStereotype(astcdClass.getModifier()) ||
        symbolTableService.hasInheritedScopeStereotype(astcdClass.getModifier());
    this.replaceTemplate(EMPTY_BODY, traverseMethod, new TemplateHookPoint(TRAVERSE_TEMPLATE, astcdClass, isScopeSpanningSymbol));
    return traverseMethod;
  }
  
  /**
   * Adds the getter and setter methods for the attached visitors.
   * 
   * @param definitionList List of class diagrams to retrieve available visitors
   * @return The decorated getter and setter methods
   */
  protected List<ASTCDMethod> addVisitor2Methods(List<ASTCDDefinition> definitionList) {
    // add setter and getter for created attribute in 'getVisitorAttributes'
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDDefinition cd : definitionList) {
      String simpleName = Names.getSimpleName(visitorService.getVisitorSimpleName(cd.getSymbol()));
      //add setter for visitor attribute
      //e.g. public void setAutomataVisitor(automata._visitor.AutomataVisitor AutomataVisitor)
      ASTMCQualifiedType visitorType = getMCTypeFacade().createQualifiedType(visitorService.getVisitor2FullName(cd.getSymbol()));
      ASTCDParameter visitorParameter = getCDParameterFacade().createParameter(visitorType, StringTransformations.uncapitalize(simpleName));
      ASTCDMethod setVisitorMethod = getCDMethodFacade().createMethod(PUBLIC, "set" + simpleName, visitorParameter);
      methodList.add(setVisitorMethod);

      //add getter for visitor attribute
      // e.g. public Optional<automata._visitor.AutomataVisitor> getAutomataVisitor()
      ASTMCOptionalType optionalVisitorType = getMCTypeFacade().createOptionalTypeOf(visitorType);
      ASTCDMethod getVisitorMethod = getCDMethodFacade().createMethod(PUBLIC, optionalVisitorType, "get" + simpleName);
      this.replaceTemplate(EMPTY_BODY, getVisitorMethod, new StringHookPoint("return Optional.empty();"));
      methodList.add(getVisitorMethod);
    }
    return methodList;
  }
  
  /**
   * Controls the creation of all visitor related methods, such as visit,
   * endVisit, handle, and traverse for all visitable entities.
   * 
   * @param cdDefinition The input class diagram from which all visitable
   *          entities are derived
   * @return The decorated visitor methods
   */
  protected List<ASTCDMethod> createTraverserDelegatingMethods(ASTCDDefinition cdDefinition) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    String simpleVisitorName = visitorService.getVisitorSimpleName(cdDefinition.getSymbol());
    
    // add methods for classes, interfaces, enumerations, symbols, and scopes
    visitorMethods.addAll(createVisitorDelegatorClassMethods(cdDefinition.getCDClassList(), simpleVisitorName));
    visitorMethods.addAll(createVisitorDelegatorInterfaceMethods(cdDefinition.getCDInterfaceList(), simpleVisitorName));
    visitorMethods.addAll(createVisitorDelegatorEnumMethods(cdDefinition.getCDEnumList(), simpleVisitorName, cdDefinition.getName()));
    visitorMethods.addAll(createVisitorDelegatorSymbolMethods(cdDefinition, simpleVisitorName));
    visitorMethods.addAll(createVisitorDelegatorScopeMethods(cdDefinition, simpleVisitorName));
    
    return visitorMethods;
  }

  /**
   * Creates visit, endVisit, handle, and traverse methods for a list of
   * classes.
   * 
   * @param astcdClassList The input list of classes
   * @param simpleVisitorName The name of the visited entity
   * @return The decorated visitor methods
   */
  protected List<ASTCDMethod> createVisitorDelegatorClassMethods(List<ASTCDClass> astcdClassList, String simpleVisitorName) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    for (ASTCDClass astcdClass : astcdClassList) {
      visitorMethods.addAll(createVisitorDelegatorClassMethod(astcdClass, simpleVisitorName));
    }
    return visitorMethods;
  }
  
  /**
   * Creates visit, endVisit, handle, and traverse methods for a given class.
   * 
   * @param astcdClass The input class
   * @param simpleVisitorName The name of the visited entity
   * @return The decorated visitor methods
   */
  protected List<ASTCDMethod> createVisitorDelegatorClassMethod(ASTCDClass astcdClass, String simpleVisitorName) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    boolean doTraverse = !(astcdClass.isPresentModifier() && astcdClass.getModifier().isAbstract());
    ASTMCType classType = getMCTypeFacade().createQualifiedType(astcdClass.getName());
    
    // delegating visitor methods
    visitorMethods.add(addDelegatingMethod(classType, simpleVisitorName, VISIT));
    visitorMethods.add(addDelegatingMethod(classType, simpleVisitorName, END_VISIT));
    
    // non-delegating traverser methods
    visitorMethods.add(addHandleMethod(classType, doTraverse));
    if (doTraverse) {
      visitorMethods.add(addTraversMethod(classType, astcdClass));
    }
    
    return visitorMethods;
  }

  /**
   * Creates visit, endVisit, handle, and traverse methods for a list of
   * interfaces.
   * 
   * @param astcdInterfaceList The input list of interfaces
   * @param simpleVisitorName The name of the visited entity
   * @return The decorated visitor methods
   */
  protected List<ASTCDMethod> createVisitorDelegatorInterfaceMethods(List<ASTCDInterface> astcdInterfaceList, String simpleVisitorName) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    for (ASTCDInterface astcdInterface : astcdInterfaceList) {
      visitorMethods.addAll(createVisitorDelegatorInterfaceMethod(astcdInterface, simpleVisitorName));
    }
    return visitorMethods;
  }

  /**
   * Creates visit, endVisit, handle, and traverse methods for a given
   * interface.
   * 
   * @param astcdClass The input interface
   * @param simpleVisitorName The name of the visited entity
   * @return The decorated visitor methods
   */
  protected List<ASTCDMethod> createVisitorDelegatorInterfaceMethod(ASTCDInterface astcdInterface, String simpleVisitorName) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    ASTMCType interfaceType = getMCTypeFacade().createQualifiedType(astcdInterface.getName());
    
    // delegating visitor methods
    visitorMethods.add(addDelegatingMethod(interfaceType, simpleVisitorName, VISIT));
    visitorMethods.add(addDelegatingMethod(interfaceType, simpleVisitorName, END_VISIT));
    
    // non-delegating traverser methods
    visitorMethods.add(addHandleMethod(interfaceType, false));
    
    return visitorMethods;
  }
  
  /**
   * Creates visit, endVisit, handle, and traverse methods for a list of
   * enumerations.
   * 
   * @param astcdInterfaceList The input list of enumerations
   * @param simpleVisitorName The name of the visited entity
   * @return The decorated visitor methods
   */
  protected List<ASTCDMethod> createVisitorDelegatorEnumMethods(List<ASTCDEnum> astcdEnumList, String simpleVisitorName,  String definitionName) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    for (ASTCDEnum astcdEnum : astcdEnumList) {
      if (!visitorService.isLiteralsEnum(astcdEnum, definitionName)) {
        visitorMethods.addAll(createVisitorDelegatorEnumMethod(astcdEnum, simpleVisitorName));
      }
    }
    return visitorMethods;
  }

  /**
   * Creates visit, endVisit, handle, and traverse methods for a given
   * enumeration.
   * 
   * @param astcdClass The input enumeration
   * @param simpleVisitorName The name of the visited entity
   * @return The decorated visitor methods
   */
  protected List<ASTCDMethod> createVisitorDelegatorEnumMethod(ASTCDEnum astcdEnum, String simpleVisitorName) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    ASTMCType enumType = getMCTypeFacade().createQualifiedType(astcdEnum.getName());
    
    // delegating visitor methods
    visitorMethods.add(addDelegatingMethod(enumType, simpleVisitorName, VISIT));
    visitorMethods.add(addDelegatingMethod(enumType, simpleVisitorName, END_VISIT));
    
    // non-delegating traverser methods
    visitorMethods.add(addHandleMethod(enumType, false));
    
    return visitorMethods;
  }

  /**
   * Iterates over all defined symbols and creates corresponding visit,
   * endVisit, handle, and traverse methods.
   * 
   * @param astcdDefinition The class diagram that contains the symbol
   *          definitions.
   * @param simpleVisitorName The name of the delegated visitor
   * @return The corresponding visitor methods for all symbols
   */
  protected List<ASTCDMethod> createVisitorDelegatorSymbolMethods(ASTCDDefinition astcdDefinition, String simpleVisitorName) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    Set<String> symbolNames = symbolTableService.retrieveSymbolNamesFromCD(astcdDefinition.getSymbol());
    for (String symbolName : symbolNames) {
      visitorMethods.addAll(createVisitorDelegatorSymbolMethod(symbolName, simpleVisitorName));
    }
    return visitorMethods;
  }

  /**
   * Creates corresponding visit, endVisit, handle, and traverse methods for a
   * given symbol name.
   * 
   * @param symbolName The qualified name of the input symbol
   * @param simpleVisitorName The name of the delegated visitor
   * @return The corresponding visitor methods for the given symbol
   */
  protected List<ASTCDMethod> createVisitorDelegatorSymbolMethod(String symbolName, String simpleVisitorName) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    ASTMCQualifiedType symbolType = getMCTypeFacade().createQualifiedType(symbolName);
    
    // delegating visitor methods
    visitorMethods.add(addDelegatingMethod(symbolType, simpleVisitorName, VISIT));
    visitorMethods.add(addDelegatingMethod(symbolType, simpleVisitorName, END_VISIT));
    
    // non-delegating traverser methods
    ASTCDMethod handleMethod = visitorService.getVisitorMethod(HANDLE, symbolType);
    this.replaceTemplate(EMPTY_BODY, handleMethod, new TemplateHookPoint(HANDLE_TEMPLATE, true));
    visitorMethods.add(handleMethod);
    visitorMethods.add(visitorService.getVisitorMethod(TRAVERSE, symbolType));
    
    return visitorMethods;
  }
  
  /**
   * Iterates over all defined scopes and creates corresponding visit, endVisit,
   * handle, and traverse methods.
   * 
   * @param astcdDefinition The class diagram that contains the scope
   *          definitions.
   * @param simpleVisitorName The name of the delegated visitor
   * @return The corresponding visitor methods for all scopes
   */
  protected List<ASTCDMethod> createVisitorDelegatorScopeMethods(ASTCDDefinition astcdDefinition, String simpleVisitorName) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    CDDefinitionSymbol cdSymbol = astcdDefinition.getSymbol();
    ASTMCQualifiedType scopeType = getMCTypeFacade().createQualifiedType(symbolTableService.getScopeInterfaceFullName(cdSymbol));
    ASTMCQualifiedType artifactScopeType = getMCTypeFacade().createQualifiedType(symbolTableService.getArtifactScopeInterfaceFullName(cdSymbol));
    
    TemplateHookPoint traverseSymbolsBody = new TemplateHookPoint(TRAVERSE_SCOPE_TEMPLATE, getSymbolsTransitive());
    StringHookPoint traverseDelegationBody = new StringHookPoint(TRAVERSE + "(("
        + symbolTableService.getScopeInterfaceFullName() + ") node);");
    
    visitorMethods.addAll(createVisitorDelegatorScopeMethod(scopeType, simpleVisitorName, traverseSymbolsBody));

    // only create artifact scope methods if grammar contains productions or
    // refers to a starting production of a super grammar
    if (symbolTableService.hasProd(astcdDefinition) || symbolTableService.hasStartProd(astcdDefinition)) {
      visitorMethods.addAll(createVisitorDelegatorScopeMethod(artifactScopeType, simpleVisitorName, traverseDelegationBody));
    }
    return visitorMethods;
  }

  /**
   * Creates corresponding visit, endVisit, handle, and traverse methods for a
   * given scope name.
   * 
   * @param scopeType The qualified type of the input scope
   * @param simpleVisitorName The name of the delegated visitor
   * @param traverseBody body of the traverse method, provided in form of
   *          hookpoint
   * @return The corresponding visitor methods for the given scope
   */
  protected List<ASTCDMethod> createVisitorDelegatorScopeMethod(ASTMCType scopeType, String simpleVisitorName, HookPoint traverseBody) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    
    // delegating visitor methods
    visitorMethods.add(addDelegatingMethod(scopeType, simpleVisitorName, VISIT));
    visitorMethods.add(addDelegatingMethod(scopeType, simpleVisitorName, END_VISIT));
    
    // non-delegating traverser methods
    ASTCDMethod handleMethod = visitorService.getVisitorMethod(HANDLE, scopeType);
    this.replaceTemplate(EMPTY_BODY, handleMethod, new TemplateHookPoint(HANDLE_TEMPLATE, true));
    visitorMethods.add(handleMethod);
    ASTCDMethod traverseMethod = visitorService.getVisitorMethod(TRAVERSE, scopeType);
    visitorMethods.add(traverseMethod);
    this.replaceTemplate(EMPTY_BODY, traverseMethod, traverseBody);
    
    return visitorMethods;
  }

  /**
   * Creates a visitor method (e.g., visit and endVisit) that delegates to the
   * corresponding attached sub-visitor for the actual computation. Works for
   * all types of visitor methods as long as available in the target visitor.
   * 
   * @param astType The qualified type of the input entity
   * @param simpleVisitorName The name of the visitor
   * @param methodName The name of the method to create
   * @return The decorated method
   */
  protected ASTCDMethod addDelegatingMethod(ASTMCType astType, String simpleVisitorName, String methodName) {
    return addDelegatingMethod(astType, new ArrayList<>(Arrays.asList(simpleVisitorName)), methodName);
  }
  
  /**
   * Creates a visitor method (e.g., visit and endVisit) that delegates to the
   * corresponding attached sub-visitor for the actual computation. Works for
   * all types of visitor methods as long as available in the target visitor.
   * 
   * @param astType The qualified type of the input entity
   * @param simpleVisitorName A list of names for the visitors to generate
   * @param methodName The name of the method to create
   * @return The decorated method
   */
  protected ASTCDMethod addDelegatingMethod(ASTMCType astType, List<String> simpleVisitorName, String methodName) {
    ASTCDMethod visitorMethod = visitorService.getVisitorMethod(methodName, astType);
    this.replaceTemplate(EMPTY_BODY, visitorMethod, new TemplateHookPoint(
        VISITOR_METHODS_TRAVERSER_DELEGATING_TEMPLATE, simpleVisitorName, methodName));
    return visitorMethod;
  }
  
  /**
   * Creates visit and endVisit methods for ASTNode, ISymbol, and IScope.
   * 
   * @param simpleVisitorNameList The list of all qualified (super) visitors
   * @return The corresponding visitor methods for default elements
   */
  protected List<ASTCDMethod> addDefaultVisitorMethods(List<String> simpleVisitorNameList) {
    // only visit and endVisit
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    ArrayList<String> reversedList = new ArrayList<>(simpleVisitorNameList);
    Collections.reverse(reversedList);
    
    // ASTNode methods
    ASTMCQualifiedType astInterfaceType = getMCTypeFacade().createQualifiedType(AST_INTERFACE);
    visitorMethods.add(addDelegatingMethod(astInterfaceType, simpleVisitorNameList, VISIT));
    visitorMethods.add(addDelegatingMethod(astInterfaceType, reversedList, END_VISIT));
    
    // ISymbol methods
    ASTMCQualifiedType symoblInterfaceType = getMCTypeFacade().createQualifiedType(I_SYMBOL);
    visitorMethods.add(addDelegatingMethod(symoblInterfaceType, simpleVisitorNameList, VISIT));
    visitorMethods.add(addDelegatingMethod(symoblInterfaceType, reversedList, END_VISIT));
    
    // IScope methods
    ASTMCQualifiedType scopeInterfaceType = getMCTypeFacade().createQualifiedType(I_SCOPE);
    visitorMethods.add(addDelegatingMethod(scopeInterfaceType, simpleVisitorNameList, VISIT));
    visitorMethods.add(addDelegatingMethod(scopeInterfaceType, reversedList, END_VISIT));
    
    return visitorMethods;
  }
  
  /**
   * Returns a set of qualified symbol names. Considers the complete inheritance
   * hierarchy and thus, contains local symbols as well as inherited symbols.
   * 
   * @return The set of all qualified symbol names
   */
  protected Set<String> getSymbolsTransitive() {
    Set<String> superSymbolNames = new HashSet<String>();
    // add local symbols
    superSymbolNames.addAll(symbolTableService.retrieveSymbolNamesFromCD(visitorService.getCDSymbol()));
    
    // add symbols of super CDs
    List<CDDefinitionSymbol> superCDsTransitive = visitorService.getSuperCDsTransitive();
    for (CDDefinitionSymbol cdSymbol : superCDsTransitive) {
      superSymbolNames.addAll(symbolTableService.retrieveSymbolNamesFromCD(cdSymbol));
    }
    return superSymbolNames;
  }
  
  public boolean isTop() {
    return isTop;
  }
  
  public void setTop(boolean top) {
    isTop = top;
  }
}
