<#ftl strip_whitespace=true>
package ${packageName};

import com.vmware.o11n.sdk.modeldriven.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import net.juniper.contrail.vro.ContrailPluginFactory;
import org.springframework.beans.factory.BeanFactory;
import net.juniper.contrail.api.*;
import net.juniper.contrail.api.types.*;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;



<@compress single_line=true>
public class ${className}
<#if fullSuperClassName??>
    extends ${fullSuperClassName}
<#else>
    extends AbstractWrapper
</#if>
    implements ModelWrapper <#if findable>, Findable<#elseif rootIdPropagated>, RootIdPropagator</#if>  {
</@compress>

    private static final long serialVersionUID = 1L;
    @Override
    public void setContext(PluginContext ctx) {
        <#if interceptor??>
        _ctx = new WrapperContext(ctx, ${interceptor.canonicalName}.class);
        <#else>
		_ctx = new WrapperContext(ctx, null);
		</#if>
    }

    <#if findable>
    private Sid _internalId;

    public Sid getInternalId() {
        return _internalId;
    }

    public void setInternalId(Sid id) {
        this._internalId = id;
    }
    </#if>

	@Override
    public ${modelClass.canonicalName} __getTarget() {
        return (${modelClass.canonicalName}) _target;
    }

	<#list constructors as c> <#if !c.extensionClass??>
	<#if !(c.params?has_content)><#assign defaultConstructor = true></#if>
    <@compress single_line=true>public ${className}(<@params c />)<@thrown c /> {</@compress>
	    setContext(AnonymousPluginContext.get());

        <@locals c />

        __setTarget(new ${c.declaringClass.canonicalName}(<@localNames c />));
	}
	</#if></#list>

	<#list constructors as c> <#if c.extensionClass??>
	<#if !(c.params?has_content)><#assign defaultConstructor = true></#if>
    <@compress single_line=true>public ${className}(<@params c />)<@thrown c /> {</@compress>
        setContext(AnonymousPluginContext.get());

        <@locals c />

        __setTarget(_ctx.constructWith(${c.extensionClass.canonicalName}.class).${c.extensionMethod}(<@localNames c />));
	}
	</#if></#list>

	<#if !defaultConstructor??>
	public ${className}() {
		// Empty default constructor
	}</#if>

    <#list methods as m> <#if !m.extensionClass?? && !interceptor?? && !m.inheritedWrapperMethod>
    <@compress single_line=true>public ${m.returns.returnFriendlyClassName} ${m.name}(<@params m />)<@thrown m /> {</@compress>
        <@locals m />

        <#if m.returns.returnFriendlyClassName != 'void'>
        Object _res$ = <#else>        </#if><#if m.originalName == 'setModel'>__getTarget().${m.originalName}((__getTarget().getModel()).getClass().cast(<@localNames m />));
        <#else>__getTarget().${m.originalName}(<@localNames m />);</#if>

        <#if m.returns.returnFriendlyClassName != 'void'>
        ${m.returns.returnFriendlyClassName} _res$pl = _ctx.createPluginObject(_res$, ${m.returns.convertFriendlyClassName});
        <#if findable>
        _ctx.assignId(${m.returns.convertFriendlyClassName}, _res$pl, getInternalId());
        <#elseif rootIdPropagated>
        _ctx.assignId(${m.returns.convertFriendlyClassName}, _res$pl, getRootId());
        </#if>
        return _res$pl;
        </#if>
    }
	</#if></#list>

    <#list methods as m> <#if !m.extensionClass?? && interceptor??>
    <@compress single_line=true>public ${m.returns.returnFriendlyClassName} ${m.name} (<@params m />) throws Throwable {</@compress>
        <@locals m />

        <@compress single_line=true><#if m.returns.returnFriendlyClassName != 'void'>Object _res$ = <#else> </#if>
		_ctx.intercept(__getTarget(), <#if findable>getInternalId()<#else>null</#if>, "${m.originalName}", new Class<?>[] {<@types m />}<#if m.params?has_content>, new Object[]{<@localNames m />}</#if>);
        </@compress>

        <#if m.returns.returnFriendlyClassName != 'void'>
        ${m.returns.returnFriendlyClassName} _res$pl = _ctx.createPluginObject(_res$, ${m.returns.convertFriendlyClassName});
        <#if findable>
        _ctx.assignId(${m.returns.convertFriendlyClassName}, _res$pl, getInternalId());
        <#elseif rootIdPropagated>
        _ctx.assignId(${m.returns.convertFriendlyClassName}, _res$pl, getRootId());
        </#if>
        return _res$pl;
        </#if>
    }
	</#if></#list>

    <#list methods as m> <#if m.extensionClass??>
    <@compress single_line=true>public ${m.returns.returnFriendlyClassName} ${m.name} (<@params m />)<@thrown m /> {</@compress>
        <@locals m />
        <@compress single_line=true><#if m.returns.returnFriendlyClassName != 'void'>Object _res$ = <#else> </#if>
        _ctx.delegateTo(${m.extensionClass.canonicalName}.class, <#if findable>getInternalId()<#else>null</#if>, __getTarget()).${m.originalName}(<@localNames m />);
        </@compress>

        <#if m.returns.returnFriendlyClassName != 'void'>
        ${m.returns.returnFriendlyClassName} _res$pl = _ctx.createPluginObject(_res$, ${m.returns.convertFriendlyClassName});
        <#if findable>
        _ctx.assignId(${m.returns.convertFriendlyClassName}, _res$pl, getInternalId());
        <#elseif rootIdPropagated>
        _ctx.assignId(${m.returns.convertFriendlyClassName}, _res$pl, getRootId());
        </#if>
        return _res$pl;
        </#if>
    }
	</#if></#list>

	<#if singleton >
	    <@singletonMethod />
	</#if>

    <#list refsFields as field>
    public String get${field.methodName}() {
        List<${field.returnTypeName}> ref_list = __getTarget().get${field.methodName}();

        return getRefString(ref_list, "${field.refObjectType}");
    }

    </#list>
    <#if findable>
    private <T extends ApiPropertyBase>String getRefString(List<ObjectReference<T>> ref_list, String type) {
       if( ref_list == null) {
            return null;
        }

        BeanFactory beanFactory = _ctx.getPluginContext().getApplicationContext().getAutowireCapableBeanFactory();
        ContrailPluginFactory factory = beanFactory.getBean(ContrailPluginFactory.class);
        StringBuilder builder = new StringBuilder();
        String prefix = "";
        Sid wrapperSid = this.getInternalId();

        try {
            for( ObjectReference<T> ref : ref_list ) {
                Sid sid = wrapperSid.with(type, ref.getUuid());
                ModelWrapper element = (ModelWrapper)factory.find(type, sid.toString());
                if( element != null ) {
                    builder.append(prefix);
                    prefix = "\n";
                    builder.append(((ApiObjectBase)element.__getTarget()).getName());
                }
            }
        }
        catch (IllegalArgumentException e) {
            return null;
        }
        return builder.toString();
    }
    </#if>


    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        setContext(AnonymousPluginContext.get());
    }
}

<@compress single_line=true>
<#macro params m>
<#list m.params as p>
${p.returnFriendlyClassName} ${p.name}<#if p_has_next>, </#if>
</#list>
</#macro>
</@compress>

<@compress single_line=true>
<#macro locals m>
<#list m.params as p>
        ${p.modelType.canonicalName} _local$${p.name} = _ctx.extractModelObject(${p.name}, ${p.modelType.canonicalName}.class);
</#list>
</#macro>
</@compress>

<@compress single_line=true>
<#macro types m>
<#list m.params as p>
${p.modelType.canonicalName}.class<#if p_has_next>, </#if>
</#list>
</#macro>
</@compress>


<@compress single_line=true>
<#macro signature m>
<#list m.params as p>
${p.modelType.canonicalName}.class<#if p_has_next>, </#if>
</#list>
</#macro>
</@compress>

<@compress single_line=true>
<#macro localNames m>
<#list m.params as p>
_local$${p.name}<#if p_has_next>, </#if></#list></#macro>
</@compress>

<#macro singletonMethod>
    public static ${className} createScriptingSingleton(ch.dunes.vso.sdk.api.IPluginFactory f) {
        return ((AbstractModelDrivenFactory) f).createScriptingSingleton(${modelClass.canonicalName}.class);
    }
</#macro>

<#macro thrown c><#if c.thrown?has_content> throws <#list c.thrown as e>${e.canonicalName}<#if e_has_next>, <#else> </#if></#list><#else> </#if></#macro>