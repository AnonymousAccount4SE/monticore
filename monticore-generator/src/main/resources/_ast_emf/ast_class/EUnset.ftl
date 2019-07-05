<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeList", "packageName", "className")}
  <#assign genHelper = glex.getGlobalVar("astHelper")>
    switch (featureID) {
    <#list attributeList as attribute>
      <#assign setter = astHelper.getPlainSetter(attribute)>
      case ${packageName}.${className}_${attribute.getName()?cap_first}:
      <#if genHelper.isListType(attribute)>
        ${attribute.getName()}.clear();
      <#elseif genHelper.isOptional(attribute.getType())>
        ${setter}Absent();
      <#else>
        <#-- TODO GV: not optionals! -->
        ${setter}(${genHelper.getDefaultValue(attribute)});
      </#if>
      return;
    </#list>
    }
    eDynamicUnset(featureID);
