/*
 * Copyright (c) 2018 Juniper Networks, Inc. All rights reserved.
 */

package net.juniper.contrail.vro.workflows.model

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlType

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "inputType",
    propOrder = ["parameters"]
)
class Input {
    @XmlElement(name = "param")
    var parameters: MutableList<Parameter> = mutableListOf()

    fun addParameters(vararg parameter: Parameter) =
        this.parameters.addAll(parameter)
}
