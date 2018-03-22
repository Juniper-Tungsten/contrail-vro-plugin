/*
 * Copyright (c) 2018 Juniper Networks, Inc. All rights reserved.
 */

package net.juniper.contrail.vro.generator.util

import java.nio.file.Path
import java.nio.file.Paths

val CAMEL_CASE_REGEX = "(?<=[a-z])(?=[A-Z])".toRegex()
val ES_SUFFIXES = ".*(s|x|z|ch|sh)$".toRegex()
val NON_IES_SUFFIXES_REGEX = "(ay|ey|iy|oy|uy)$".toRegex()

val String.typeToClassName: String get() =
    split("-").joinToString("") { it.toLowerCase().capitalize() }

fun String.underscoredPropertyToCamelCase(): String {
    val elements = split("_")
    val first = elements[0]
    val rest = elements.slice(1 until elements.size)
    return first + rest.joinToString("") { it.toLowerCase().capitalize() }
}

fun String.splitCamel(): String =
    split(CAMEL_CASE_REGEX).joinToString(" ")

fun String.folderName() : String = when (this) {
    "BgpAsAService" -> "BGPs As Services"
    "Bgpvpn" -> "BGP VPNs"
    "VirtualDns" -> "Virtual DNSes"
    else -> pluralizeCamelCases(this)
}

fun pluralizeCamelCases(name: String) : String {
    val nameParts = name.split(CAMEL_CASE_REGEX)
    val uppercasedWords = nameParts.map { uppercaseAcronyms(it) }.run {
        if (last() == "List") dropLast(1) else this
    }
    val pluralWord = uppercasedWords.last().pluralize()
    return (uppercasedWords.dropLast(1).plus(pluralWord)).joinToString(" ")
}

fun uppercaseAcronyms(name: String): String = when (name) {
    "Ip", "ip" -> "IP"
    "Bgp", "bgp" -> "BGP"
    "Vpn", "vpn" -> "VPN"
    "Src" -> "Source"
    "Dst" -> "Destination"
    "Entries" -> "Rules"
    "mgmt" -> "Management"
    else -> name
}

private val String.alreadyPlural get() = when (this) {
    "Pairs", "Details", "Fields", "Type2", "Subnets", "Pools", "Routes", "Rules",
    "Ports", "Annotations", "Addresses", "Entries" -> true
    else -> false
}

fun String.pluralize(): String = when {
    alreadyPlural -> this
    matches(ES_SUFFIXES) -> this + "es"
    endsWith("y") && !matches(NON_IES_SUFFIXES_REGEX) -> dropLast(1) + "ies"
    endsWith("list", true) -> this
    else -> this + "s"
}

fun String.packageToPath() =
    replace('.', '/')

fun Path.append(subpath: String): Path =
    Paths.get(toString(), subpath)

operator fun String.div(subpath: String): Path =
    Paths.get(this, subpath)

val String.isCDATA get() =
    startsWith("<![CDATA[") && endsWith("]]>")

val String.CDATA get() = when {
    this.isBlank() -> this
    this.isCDATA -> this
    else -> "<![CDATA[$this]]>"
}

val String?.CDATA
    @JvmName("getCDATANullable")
    get() = when {
    this == null -> null
    else -> this.CDATA
}