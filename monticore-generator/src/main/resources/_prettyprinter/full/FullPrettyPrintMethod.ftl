<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Convenient method for pretty printing
-->
getPrinter().clearBuffer();
getTraverser().clearTraversedElements();
node.accept(getTraverser());
return getPrinter().getContent().stripTrailing();
