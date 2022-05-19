<#-- (c) https://github.com/MontiCore/monticore -->

<#-- Defines freemarker functions as aliases of Java methods.
     These functions are injected in all templates.
-->


<#function include templ>
	<#return tc.include(templ)>
</#function>

<#-- TODO: overload checked until here -->

<#function include2 templ ast>
        <#return tc.include(templ,ast)>
</#function>

<#function includeArgs templ param...>
	<#return tc.includeArgs(templ, param)>
</#function>


<#function signature param...>
	<#return tc.signature(param)>
</#function>


<#-- Aliases for logging methods -->

<#function trace msg logger>
	<#return tc.trace(msg, logger)>
</#function>

<#function debug msg logger>
	<#return tc.debug(msg, logger)>
</#function>

<#function info msg logger>
	<#return tc.info(msg, logger)>
</#function>

<#function warn msg>
	<#return tc.warn(msg)>
</#function>

<#function error msg>
	<#return tc.error(msg)>
</#function>

<#-- Aliases for global vars methods -->

<#function defineGlobalVar name value>
	<#return glex.defineGlobalVar(name, value)>
</#function>

<#function defineGlobalVars name...>
	<#return glex.defineGlobalVars(name)>
</#function>

<#function changeGlobalVar name value>
	<#return glex.changeGlobalVar(name, value)>
</#function>

<#function addToGlobalVar name value>
	<#return glex.addToGlobalVar(name, value)>
</#function>

<#function getGlobalVar name>
	<#return glex.getGlobalVar(name)>
</#function>

<#function requiredGlobalVar name>
	<#return glex.requiredGlobalVar(name)>
</#function>

<#function requiredGlobalVars name...>
	<#return glex.requiredGlobalVars(name)>
</#function>

<#-- Aliases for hook points methods -->
<#function bindHookPoint name hp>
	<#return glex.bindHookPoint(name, hp)>
</#function>

<#function defineHookPoint name ast="">
	<#if ast?has_content>
		<#return glex.defineHookPoint(tc, name, ast)>
	<#else>
		<#return glex.defineHookPoint(tc, name)>
	</#if>
</#function>

<#function defineHookPointWithDefault name default>
	<#return glex.defineHookPointWithDefault(tc, name, default)>
</#function>

<#function defineHookPointWithDefault3 name ast default>
	<#return glex.defineHookPointWithDefault(tc, name, ast, default)>
</#function>


<#function existsHookPoint name>
	<#return glex.existsHookPoint(name)>
</#function>
