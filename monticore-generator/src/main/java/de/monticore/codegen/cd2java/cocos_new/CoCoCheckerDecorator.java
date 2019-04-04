package de.monticore.codegen.cd2java.cocos_new;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.ast_new.ASTService;
import de.monticore.codegen.cd2java.factories.SuperSymbolHelper;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.codegen.cd2java.visitor_new.VisitorService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.types.TypesHelper;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java.cocos_new.CoCoConstants.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;
import static de.monticore.codegen.cd2java.visitor_new.VisitorConstants.VISIT;

public class CoCoCheckerDecorator extends AbstractDecorator<ASTCDCompilationUnit, ASTCDClass> {

  private static final String REAL_THIS = "realThis";

  private static final String COCOS = "CoCos";

  private static final String NODE = "node";

  private static final String COCO = "coco";

  private static final String CHECKER = "checker";

  private final MethodDecorator methodDecorator;

  private final CoCoService cocoService;

  private final VisitorService visitorService;

  public CoCoCheckerDecorator(final GlobalExtensionManagement glex, final MethodDecorator methodDecorator,
      final CoCoService cocoService,
      final VisitorService visitorService) {
    super(glex);
    this.methodDecorator = methodDecorator;
    this.cocoService = cocoService;
    this.visitorService = visitorService;

  }

  @Override
  public ASTCDClass decorate(ASTCDCompilationUnit compilationUnit) {
    ASTCDAttribute realThisAttribute = getCDAttributeFactory().createAttribute(PRIVATE, visitorService.getVisitorType(), REAL_THIS);
    this.replaceTemplate(VALUE, realThisAttribute, new StringHookPoint("= this"));
    List<ASTCDMethod> realThisMethods = methodDecorator.decorate(realThisAttribute);

    ASTCDConstructor constructor = this.getCDConstructorFactory().createConstructor(PUBLIC, cocoService.getCheckerSimpleTypeName());

    ASTCDClass cocoChecker = CD4AnalysisMill.cDClassBuilder()
        .setName(cocoService.getCheckerSimpleTypeName())
        .addInterface(visitorService.getVisitorReferenceType())
        .addCDAttribute(realThisAttribute)
        .addCDConstructor(constructor)
        .addAllCDMethods(realThisMethods)
        .build();

    CDSymbol cdSymbol = cocoService.getCDSymbol();
    for (CDSymbol currentCDSymbol : cocoService.getAllCDs()) {
      CoCoService cocoService = CoCoService.createCoCoService(currentCDSymbol);
      ASTService astService = ASTService.createASTService(currentCDSymbol);

      ASTType checkerType = cocoService.getCheckerType();
      String checkerName = TypesHelper.printType(checkerType).replaceAll("\\.", "_");
      boolean isCurrentDiagram = cdSymbol.getFullName().equals(currentCDSymbol.getFullName());

      cocoChecker.addCDAttribute(createCheckerAttribute(checkerType, checkerName, isCurrentDiagram));
      cocoChecker.addCDMethod(createAddCheckerMethod(checkerType, checkerName));

      ASTType astBaseInterfaceType = astService.getASTBaseInterface();
      ASTType cocoNodeType = cocoService.getCoCoType();
      String cocoNodeCollectionName = TypesHelper.printType(astBaseInterfaceType).replaceAll("\\.", "_") + COCOS;
      cocoChecker.addCDAttribute(createCoCoCollectionAttribute(cocoNodeType, cocoNodeCollectionName));

      ASTCDMethod addNodeCoCo = createAddCoCoMethod(cocoNodeType, checkerType);
      this.replaceTemplate(EMPTY_BODY, addNodeCoCo, createAddCoCoImpl(true, cocoNodeCollectionName, checkerName));

      ASTCDMethod visitNode = createVisitMethod(astBaseInterfaceType);
      this.replaceTemplate(EMPTY_BODY, visitNode, createVisitImpl(true, cocoNodeType, cocoNodeCollectionName, checkerName));

      ASTCDMethod checkAll = createCheckAllMethod(astBaseInterfaceType);
      this.replaceTemplate(EMPTY_BODY, checkAll, new StringHookPoint(NODE + ".accept(getRealThis());"));

      cocoChecker.addCDMethod(addNodeCoCo);
      cocoChecker.addCDMethod(visitNode);
      cocoChecker.addCDMethod(checkAll);


      for (CDTypeSymbol cdTypeSymbol : currentCDSymbol.getTypes()) {
        if (!cdTypeSymbol.isClass() && !cdTypeSymbol.isInterface()) {
          continue;
        }

        ASTType cocoType = cocoService.getCoCoType((ASTCDType) cdTypeSymbol.getAstNode().get());
        ASTType astType = astService.getASTType((ASTCDType) cdTypeSymbol.getAstNode().get());
        String cocoCollectionName = TypesHelper.printType(astType).replaceAll("\\.", "_") + COCOS;

        if (isCurrentDiagram) {
          cocoChecker.addCDAttribute(createCoCoCollectionAttribute(cocoType, cocoCollectionName));
        }

        ASTCDMethod addCoCo = createAddCoCoMethod(cocoType, checkerType);
        this.replaceTemplate(EMPTY_BODY, addCoCo, createAddCoCoImpl(isCurrentDiagram, cocoCollectionName, checkerName));

        ASTCDMethod visit = createVisitMethod(astType);
        this.replaceTemplate(EMPTY_BODY, visit, createVisitImpl(isCurrentDiagram, cocoType, cocoCollectionName, checkerName));

        cocoChecker.addCDMethod(addCoCo);
        cocoChecker.addCDMethod(visit);
      }
    }

    return cocoChecker;
  }

  protected ASTCDAttribute createCheckerAttribute(ASTType checkerType, String checkerName, boolean isCurrentDiagram) {
    ASTType checkerListType = getCDTypeFactory().createListTypeOf(checkerType);
    ASTCDAttribute checker = getCDAttributeFactory().createAttribute(PRIVATE, checkerListType, checkerName);
    HookPoint hp = isCurrentDiagram ? new StringHookPoint("= new ArrayList<>(Arrays.asList(new " + TypesHelper.printType(checkerType) + "()))")
        : new StringHookPoint("= new ArrayList<>()");
    this.replaceTemplate(VALUE, checker, hp);
    return checker;
  }

  protected ASTCDMethod createAddCheckerMethod(ASTType checkerType, String checkerName) {
    ASTCDParameter parameter = getCDParameterFactory().createParameter(checkerType, CHECKER);
    ASTCDMethod addCheckerMethod = getCDMethodFactory().createMethod(PUBLIC, ADD_CHECKER, parameter);
    this.replaceTemplate(EMPTY_BODY, addCheckerMethod, new StringHookPoint("this." + checkerName + ".add(" + CHECKER + ");"));
    return addCheckerMethod;
  }

  protected ASTCDAttribute createCoCoCollectionAttribute(ASTType cocoType, String cocoCollectionName) {
    ASTType cocoCollectionType = getCDTypeFactory().createCollectionTypeOf(cocoType);
    ASTCDAttribute cocoCollectionAttribute = getCDAttributeFactory().createAttribute(PRIVATE, cocoCollectionType, cocoCollectionName);
    this.replaceTemplate(VALUE, cocoCollectionAttribute, new StringHookPoint("= new LinkedHashSet<>()"));
    return cocoCollectionAttribute;
  }

  protected ASTCDMethod createAddCoCoMethod(ASTType cocoType, ASTType checkerType) {
    ASTCDParameter parameter = getCDParameterFactory().createParameter(cocoType, COCO);
    return getCDMethodFactory().createMethod(PUBLIC, checkerType, ADD_COCO, parameter);
  }

  protected HookPoint createAddCoCoImpl(boolean isCurrentDiagram, String cocoCollectionName, String checkerName) {
    String impl;
    if (isCurrentDiagram) {
      impl = cocoCollectionName + ".add(" + COCO + ");\n";
    }
    else {
      impl = checkerName + ".stream().findFirst().get()." + ADD_COCO + "(" + COCO + ");\n";
    }
    return new StringHookPoint(impl + "return this;");
  }

  protected ASTCDMethod createVisitMethod(ASTType astType) {
    ASTCDParameter parameter = getCDParameterFactory().createParameter(astType, NODE);
    return getCDMethodFactory().createMethod(PUBLIC, VISIT, parameter);
  }

  protected HookPoint createVisitImpl(boolean isCurrentDiagram, ASTType cocoType, String cocoCollectionName, String checkerName) {
    if (isCurrentDiagram) {
      return new StringHookPoint(
          "for (" + TypesHelper.printType(cocoType) + " " + COCO + " : " + cocoCollectionName + ") {\n" +
              COCO + "." + CHECK + "(" + NODE + ");\n" +
              "}\n" +
              "// and delegate to all registered checkers of the same language as well\n" +
              checkerName + ".stream().forEach(c -> c.visit(" + NODE + "));");
    }
    else {
      return new StringHookPoint( checkerName + ".stream().forEach(c -> c.visit(" + NODE + "));");
    }
  }

  protected ASTCDMethod createCheckAllMethod(ASTType astType) {
    ASTCDParameter parameter = getCDParameterFactory().createParameter(astType, NODE);
    return getCDMethodFactory().createMethod(PUBLIC, CHECK_ALL, parameter);
  }
}
