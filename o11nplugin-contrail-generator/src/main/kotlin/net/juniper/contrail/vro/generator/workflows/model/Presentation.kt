/*
 * Copyright (c) 2018 Juniper Networks, Inc. All rights reserved.
 */

package net.juniper.contrail.vro.generator.workflows.model

import net.juniper.contrail.vro.generator.util.CDATA
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlType

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "presentationType",
    propOrder = ["presentationSteps", "presentationParameters"]
)
class Presentation(
    presentationSteps: List<PresentationStep> = emptyList(),
    presentationParameters: List<PresentationParameter> = emptyList()
) {
    @XmlElement(name = "p-step")
    val presentationSteps: List<PresentationStep> =
        presentationSteps.toList()

    @XmlElement(name = "p-param")
    val presentationParameters: List<PresentationParameter> =
        presentationParameters.toList()
}

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "p-stepType",
    propOrder = ["title", "presentationParameters", "presentationGroups"]
)
class PresentationStep private constructor(
    title: String,
    presentationParameters: List<PresentationParameter>?,
    presentationGroups: List<PresentationGroup>?
) {
    companion object {
        fun fromParameters(
            title: String,
            presentationParameters: List<PresentationParameter>
        ) = PresentationStep(title, presentationParameters, null)

        fun fromGroups(
            title: String,
            presentationGroups: List<PresentationGroup>
        ) = PresentationStep(title, null, presentationGroups)
    }

    @XmlElement
    val title: String = title

    @XmlElement(name = "p-param")
    val presentationParameters: List<PresentationParameter>? =
        presentationParameters?.toList()

    @XmlElement(name = "p-group")
    val presentationGroups: List<PresentationGroup>? =
        presentationGroups?.toList()
}

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "p-groupType",
    propOrder = ["title", "presentationParameters"]
)
class PresentationGroup(
    title: String,
    presentationParameters: List<PresentationParameter>
) {
    @XmlElement
    val title: String = title

    @XmlElement(name = "p-param")
    val presentationParameters: List<PresentationParameter> =
        presentationParameters.toList()
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
    val parameterQualifiers: List<ParameterQualifier> =
        qualifiers?.toMutableList() ?: mutableListOf()
}

