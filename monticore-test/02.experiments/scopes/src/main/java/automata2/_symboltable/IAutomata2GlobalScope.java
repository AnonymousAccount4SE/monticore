/* generated from model Automata */
/* generated by template core.Interface*/

/* (c) https://github.com/MontiCore/monticore */
package automata2._symboltable;

/* generated by template core.Imports*/

import de.se_rwth.commons.Names;

import java.util.HashSet;
import java.util.Set;

public interface IAutomata2GlobalScope extends IAutomata2GlobalScopeTOP {

  @Override
  default Set<String> calculateModelNamesForState(String name) {
    Set<String> names = new HashSet<>();
    // calculate all prefixes
    while (name.contains(".")) {
      name = Names.getQualifier(name);
      names.add(name);
    }
    return names;
  }

}
