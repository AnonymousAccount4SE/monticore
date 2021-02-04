// (c) https://github.com/MontiCore/monticore
package de.monticore.types.check;

import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.monticore.types.mcfullgenerictypes._visitor.MCFullGenericTypesTraverser;

import java.util.Optional;

public class FullSynthesizeFromMCFullGenericTypes implements ISynthesize {

  private MCFullGenericTypesTraverser traverser;

  protected TypeCheckResult typeCheckResult;

  public FullSynthesizeFromMCFullGenericTypes(){
    init();
  }

  @Override
  public Optional<SymTypeExpression> getResult() {
    if(typeCheckResult.isPresentCurrentResult()){
      return Optional.of(typeCheckResult.getCurrentResult());
    }else{
      return Optional.empty();
    }
  }

  @Override
  public void init() {
    traverser = MCFullGenericTypesMill.traverser();
    typeCheckResult = new TypeCheckResult();

    SynthesizeSymTypeFromMCFullGenericTypes synFromFull = new SynthesizeSymTypeFromMCFullGenericTypes();
    synFromFull.setTypeCheckResult(typeCheckResult);
    SynthesizeSymTypeFromMCSimpleGenericTypes synFromSimple = new SynthesizeSymTypeFromMCSimpleGenericTypes();
    synFromSimple.setTypeCheckResult(typeCheckResult);
    SynthesizeSymTypeFromMCCollectionTypes synFromCollection = new SynthesizeSymTypeFromMCCollectionTypes();
    synFromCollection.setTypeCheckResult(typeCheckResult);
    SynthesizeSymTypeFromMCBasicTypes synFromBasic = new SynthesizeSymTypeFromMCBasicTypes();
    synFromBasic.setTypeCheckResult(typeCheckResult);

    traverser.add4MCFullGenericTypes(synFromFull);
    traverser.setMCFullGenericTypesHandler(synFromFull);
    traverser.add4MCSimpleGenericTypes(synFromSimple);
    traverser.setMCSimpleGenericTypesHandler(synFromSimple);
    traverser.add4MCCollectionTypes(synFromCollection);
    traverser.setMCCollectionTypesHandler(synFromCollection);
    traverser.add4MCBasicTypes(synFromBasic);
    traverser.setMCBasicTypesHandler(synFromBasic);
  }

  public MCFullGenericTypesTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(MCFullGenericTypesTraverser traverser) {
    this.traverser = traverser;
  }
}
