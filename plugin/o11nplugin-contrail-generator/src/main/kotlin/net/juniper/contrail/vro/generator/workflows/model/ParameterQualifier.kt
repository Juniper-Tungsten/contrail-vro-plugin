/*
 * Copyright (c) 2018 Juniper Networks, Inc. All rights reserved.
 */

package net.juniper.contrail.vro.generator.workflows.model

import net.juniper.contrail.vro.config.CDATA
import net.juniper.contrail.vro.generator.workflows.model.QualifierKind.ognl
import net.juniper.contrail.vro.generator.workflows.model.QualifierKind.static
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlType
import javax.xml.bind.annotation.XmlValue

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "p-qualType",
    propOrder = ["value"]
)
class ParameterQualifier(
    kind: QualifierKind,
    name: String,
    type: String? = null,
    value: String? = null
) {
    @XmlAttribute(name = "kind")
    val kind: String = kind.name

    @XmlAttribute(name = "name")
    val name: String = name

    @XmlAttribute(name = "type")
    val type: String? = type

    @XmlValue
    val value: String? = value.CDATA

}

enum class QualifierKind {
    static,
    ognl;
}

//TODO convert strings to sealed classes
val mandatoryQualifierName = "mandatory"
val visibleQualifierName = "visible"
val defaultValueQualifierName = "defaultValue"
val numberFormatQualifierName = "numberFormat"
val minNumberValueQualifierName = "minNumberValue"
val maxNumberValueQualifierName = "maxNumberValue"
val showInInventoryQualifierName = "contextualParameter"
val genericEnumerationQualifierName = "genericEnumeration"
val linkedEnumerationQualifierName = "linkedEnumeration"
val dataBindingQualifierName = "dataBinding"
val sdkRootObjectQualifierName = "sdkRootObject"
val selectAsQualifierName = "show-select-as"
val beforeDateQualifierName = "beforeDate"
val afterDateQualifierName = "afterDate"

val voidValue = "__NULL__"

val showInInventoryQualifier = staticQualifier(showInInventoryQualifierName, void, voidValue)
val mandatoryQualifier = staticQualifier(mandatoryQualifierName, boolean, true)
val selectAsListQualifier = staticQualifier(selectAsQualifierName, string, "list")
val selectAsTreeQualifier = staticQualifier(selectAsQualifierName, string, "tree")
fun <T : Any> defaultValueQualifier(type: ParameterType<T>, value: T) = staticQualifier(defaultValueQualifierName, type, value)
fun <T : Any> predefinedAnswersQualifier(type: ParameterType<T>, values: List<T>): ParameterQualifier {
    val simpleType = type.unArrayed
    return staticQualifier(genericEnumerationQualifierName, array(simpleType), cDATAListFormat(simpleType, values))
}
fun numberFormatQualifier(value: String) = staticQualifier(numberFormatQualifierName, string, value)
fun minNumberValueQualifier(value: Long) = staticQualifier(minNumberValueQualifierName, number, value)
fun maxNumberValueQualifier(value: Long) = staticQualifier(maxNumberValueQualifierName, number, value)
fun visibleWhenNonNullQualifier(otherName: String) =
    ognlQualifier(visibleQualifierName, boolean, "#$otherName != null")
fun visibleWhenVariableHasValueQualifier(otherName: String, requiredValue: String) =
    ognlQualifier(visibleQualifierName, boolean, "#$otherName == \"$requiredValue\"")
fun visibleWhenBooleanSwitchedQualifier(otherName: String) =
    ognlQualifier(visibleQualifierName, boolean, "#$otherName")
fun listFromAction(action: Action) =
    ognlQualifier(linkedEnumerationQualifierName, action.resultType, action.ognl)
fun childOf(parent: String) =
    ognlQualifier(sdkRootObjectQualifierName, any, "#$parent")
fun bindDataTo(parameter: String) =
    ognlQualifier(dataBindingQualifierName, string, "#$parameter")

private val Action.ognl get() =
    """GetAction("$packageName","$name").call($call)"""
private val Action.call get() =
    parameters.joinToString(",") { "#${it.name}" }

private fun <T : Any> staticQualifier(name: String, type: ParameterType<T>, value: T) =
    ParameterQualifier(static, name, type.name, value.toString())

private fun ognlQualifier(name: String, type: ParameterType<Any>, value: String) =
    ParameterQualifier(ognl, name, type.name, value)

private fun <T : Any> cDATAListFormat(type: ParameterType<T>, values: List<T>): String {
    val elements = values.joinToString(";") { "#$type#$it#" }
    return "#{$elements}#"
}

fun wrapConstraints(xsdConstraint: String, constraintValue: Any): ParameterQualifier? =
    when (xsdConstraint) {
        "default" -> {
            ParameterQualifier(
                static,
                defaultValueQualifierName,
                constraintValue.javaClass.parameterType.name,
                constraintValue.toString()
            )
        }
        "minInclusive" -> minNumberValueQualifier(constraintValue.toString().toLong())
        "maxInclusive" -> maxNumberValueQualifier(constraintValue.toString().toLong())
        "pattern" -> {
            ParameterQualifier(
                static,
                "regexp",
                "Regexp",
                constraintValue.toString()
            )
        }
        "enumerations" -> predefinedAnswersQualifier(string, constraintValue as List<String>)
        "required" -> mandatoryQualifier
        else -> null
    }

