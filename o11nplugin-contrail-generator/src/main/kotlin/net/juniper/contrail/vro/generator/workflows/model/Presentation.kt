/*
 * Copyright (c) 2018 Juniper Networks, Inc. All rights reserved.
 */

package net.juniper.contrail.vro.generator.workflows.model

import net.juniper.contrail.vro.generator.util.CDATA
import net.juniper.contrail.vro.generator.util.addIfAbsent
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlType

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "presentationType",
    propOrder = ["presentationSteps"]
)
class Presentation {
    @XmlElement(name = "p-step")
    private val presentationSteps: MutableList<PresentationStep> = mutableListOf()

    fun addStep(step: PresentationStep) {
        presentationSteps.add(step)
    }

    fun step(title: String? = null, setup: PresentationStep.() -> Unit) {
        val step = PresentationStep(title)
        step.setup()
        presentationSteps.add(step)
    }
}

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "p-stepType",
    propOrder = ["title", "presentationParameters"]
)
class PresentationStep (
    @XmlElement
    val title: String? = null
) {
    @XmlElement(name = "p-param")
    private val presentationParameters: MutableList<PresentationParameter> = mutableListOf()

    fun addParameter(parameter: PresentationParameter) {
        presentationParameters.add(parameter)
    }

    fun parameter(name: String, description: String, setup: PresentationParameter.() -> Unit = {}) {
        val parameter = PresentationParameter(name, description)
        parameter.setup()
        presentationParameters.add(parameter)
    }
}

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "p-paramType",
    propOrder = ["description", "parameterQualifiers"]
)
class PresentationParameter(
    name: String,
    description: String? = null,
    qualifiers: List<ParameterQualifier>? = null
) {
    @XmlAttribute(name = "name")
    val name: String = name

    @XmlElement(name = "desc", required = true)
    val description: String? = description.CDATA ?: name

    @XmlElement(name = "p-qual")
    private val parameterQualifiers: MutableList<ParameterQualifier> =
        qualifiers?.toMutableList() ?: mutableListOf()

    var mandatory: Boolean
        get() = parameterQualifiers.contains(mandatoryQualifier)
        set(value) =
            addOrRemove(value, mandatoryQualifier)

    var showInInventory: Boolean
        get() = parameterQualifiers.contains(showInInventoryQualifier)
        set(value) =
            addOrRemove(value, showInInventoryQualifier)

    private fun <T : Any> setDefaultValue(type: ParameterType<T>, value: T) =
        addOrReplace(defaultValueQualifier(type, value))

    fun setDefaultValue(value: String) =
        setDefaultValue(string, value)

    fun setDefaultValue(value: Int) =
        setDefaultValue(number, value)

    var numberFormat: String?
        get() = parameterQualifiers.find { it.name == numberFormatQualifierName }?.value
        set(value) {
            addOrReplace(numberFormatQualifier(value ?: return))
        }

    var minNumberValue: Int?
        get() = parameterQualifiers.find { it.name == minNumberValueQualifierName }?.value?.toInt()
        set(value) {
            addOrReplace(minNumberValueQualifier(value ?: return))
        }

    var maxNumberValue: Int?
        get() = parameterQualifiers.find { it.name == maxNumberValueQualifierName }?.value?.toInt()
        set(value) {
            addOrReplace(maxNumberValueQualifier(value ?: return))
        }

    private fun addOrRemove(add: Boolean, parameter: ParameterQualifier) {
        if (add)
            parameterQualifiers.addIfAbsent(parameter)
        else
            parameterQualifiers.remove(parameter)
    }

    private fun addOrReplace(parameter: ParameterQualifier) {
        parameterQualifiers.removeIf { it.name == parameter.name }
        parameterQualifiers.add(parameter)
    }
}

