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
    name = "paramType",
    propOrder = ["description"]
)
class Parameter(
    name: String,
    type: ParameterType<Any>,
    description: String? = null
) {
    @XmlAttribute(name = "name")
    val name: String = name

    @XmlAttribute(name = "type")
    val type: String = type.name

    @XmlElement(required = true)
    val description: String? = description.CDATA
}

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "parametersSetType",
    propOrder = ["parameters"]
)
class ParameterSet {
    @XmlElement(name = "param")
    private val parameters: MutableList<Parameter> = mutableListOf()

    fun addParameter(parameter: Parameter) =
        this.parameters.add(parameter)
}

sealed class ParameterType<out Type : Any> {
    abstract val name: String
    override fun toString() =
        name
}

object boolean : ParameterType<Boolean>() {
    override val name get() =
        "boolean"
}

object number : ParameterType<Int>() {
    override val name get() =
        "number"
}

object string : ParameterType<String>() {
    override val name get() =
        "string"
}

object SecureString : ParameterType<String>() {
    override val name get() =
        "SecureString"
}

class Reference(val simpleName: String) : ParameterType<Reference>() {
    constructor(clazz: Class<*>): this(clazz.simpleName)

    override val name: String get() =
        "Contrail:$simpleName"
}

val String.reference get() =
    Reference(this)

val <T> Class<T>.reference get() =
    Reference(this)

inline fun <reified T> reference() =
    Reference(T::class.java)

