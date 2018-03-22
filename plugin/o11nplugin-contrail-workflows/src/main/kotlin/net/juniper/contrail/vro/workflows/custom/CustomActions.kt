/*
 * Copyright (c) 2018 Juniper Networks, Inc. All rights reserved.
 */

package net.juniper.contrail.vro.workflows.custom

import net.juniper.contrail.vro.workflows.model.Action

fun loadCustomActions(version: String, packageName: String): List<Action> = mutableListOf<Action>().apply {
    this += propertyRetrievalAction(version, packageName)
    this += propertyNotNullAction(version, packageName)
    this += cidrValidationAction(version, packageName)
    this += extractListPropertyAction(version, packageName)
    this += getNetworkPolicyRulesAction(version, packageName)
}