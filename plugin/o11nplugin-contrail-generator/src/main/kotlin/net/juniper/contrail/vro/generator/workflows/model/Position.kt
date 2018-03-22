/*
 * Copyright (c) 2018 Juniper Networks, Inc. All rights reserved.
 */

package net.juniper.contrail.vro.generator.workflows.model

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlType
import javax.xml.bind.annotation.XmlValue

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "positionType",
    propOrder = ["value"]
)
class Position (
    @XmlAttribute(name = "x")
    val x: String? = null,

    @XmlAttribute(name = "y")
    val y: String? = null,

    @XmlValue
    val value: String? = null
) {
    constructor(x: Float, y: Float) : this(x = x.toString(), y = y.toString())
}
