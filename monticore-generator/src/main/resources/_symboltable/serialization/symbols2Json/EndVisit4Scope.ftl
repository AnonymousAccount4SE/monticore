<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("rteScope", "symbols2Json")}
if (node.isPresentSpanningSymbol() && node.isExportingSymbols()) {
  getJsonPrinter().endArray();
  scopeDeSer.serializeAddons(node, getRealThis());
  getJsonPrinter().endObject();
}
