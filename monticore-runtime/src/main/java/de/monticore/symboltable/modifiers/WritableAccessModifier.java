/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.modifiers;

import java.util.Map;

public enum WritableAccessModifier implements AccessModifier {


  WRITABLE {
    @Override
    public boolean includes(AccessModifier modifier) {
      AccessModifier writeable = modifier.getDimensionToModifierMap().get("Writable");
      if(writeable != null){
        return writeable.equals(WRITABLE);
      }
      return true;
    }

    @Override
    public Map<String, AccessModifier> getDimensionToModifierMap() {
      return Map.of("Writable", this);
    }
  },

  NON_WRITABLE {
    @Override
    public boolean includes(AccessModifier modifier) {
      AccessModifier writeable = modifier.getDimensionToModifierMap().get("Writable");
      if(writeable != null){
        return writeable.equals(NON_WRITABLE);
      }
      return true;
    }

    @Override
    public Map<String, AccessModifier> getDimensionToModifierMap() {
      return Map.of("Writable", this);
    }
  }
}
