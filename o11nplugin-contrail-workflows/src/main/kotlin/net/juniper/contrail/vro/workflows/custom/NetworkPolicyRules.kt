/*
 * Copyright (c) 2018 Juniper Networks, Inc. All rights reserved.
 */

package net.juniper.contrail.vro.workflows.custom

import net.juniper.contrail.api.types.ActionListType
import net.juniper.contrail.api.types.AddressType
import net.juniper.contrail.api.types.NetworkPolicy
import net.juniper.contrail.api.types.PolicyRuleType
import net.juniper.contrail.api.types.Project
import net.juniper.contrail.api.types.SecurityGroup
import net.juniper.contrail.api.types.ServiceInstance
import net.juniper.contrail.api.types.VirtualNetwork
import net.juniper.contrail.vro.config.constants.parent
import net.juniper.contrail.vro.config.networkPolicyRules
import net.juniper.contrail.vro.workflows.dsl.WorkflowDefinition
import net.juniper.contrail.vro.workflows.dsl.ConditionAlternative
import net.juniper.contrail.vro.workflows.dsl.ConditionConjunction
import net.juniper.contrail.vro.workflows.dsl.FromBooleanParameter
import net.juniper.contrail.vro.workflows.dsl.FromStringParameter
import net.juniper.contrail.vro.workflows.dsl.WhenNonNull
import net.juniper.contrail.vro.workflows.dsl.actionCallTo
import net.juniper.contrail.vro.workflows.model.array
import net.juniper.contrail.vro.workflows.model.boolean
import net.juniper.contrail.vro.workflows.model.number
import net.juniper.contrail.vro.workflows.model.reference
import net.juniper.contrail.vro.workflows.model.string
import net.juniper.contrail.vro.workflows.schema.Schema
import net.juniper.contrail.vro.workflows.schema.propertyDescription
import net.juniper.contrail.vro.workflows.schema.simpleTypeConstraints
import net.juniper.contrail.vro.workflows.util.extractPropertyDescription
import net.juniper.contrail.vro.workflows.util.extractRelationDescription

private val sourceAddressTypeParameterName = "srcAddressType"
private val destinationAddressTypeParameterName = "dstAddressType"
private val mirrorShowParameterName = "mirror"
private val mirrorTypeParameterName = "mirrorType"
// There is no information about protocols in the schema
private val defaultPort = "any"
private val defaultProtocol = "any"
private val allowedProtocols = listOf("any", "tcp", "udp", "icmp", "icmp6")
private val defaultAddressType = "Network"
private val allowedAddressTypes = listOf("CIDR", "Network", "Policy", "Security Group")
private val defaultDirection = "<>"
private val allowedDirections = listOf("<>", ">")
private val defaultMirrorType = "Analyzer Instance"
private val allowedMirrorTypes = listOf("Analyzer Instance", "NIC Assisted", "Analyzer IP")
private val defaultJuniperHeaderOption = "enabled"
private val allowedJuniperHeaderOptions = listOf("enabled", "disabled")
private val defaultNexthopMode = "dynamic"
private val allowedNexthopModes = listOf("dynamic", "static")
private val defaultNetworkType = "any"
private val allowedNetworkTypes = listOf("any", "local", "reference")

internal fun addRuleToPolicyWorkflow(schema: Schema): WorkflowDefinition {

    val workflowName = "Add rule to network policy"

    return customWorkflow<NetworkPolicy>(workflowName).withScriptFile("addRuleToPolicy") {
        step("Parent policy") {
            parameter("parent", reference<NetworkPolicy>()) {
                extractRelationDescription<Project, NetworkPolicy>(schema)
                mandatory = true
            }
        }
        step("Basic attributes") {
            visibility = WhenNonNull("parent")
            parameter("simpleAction", string) {
                extractPropertyDescription<ActionListType>(schema)
                additionalQualifiers += schema.simpleTypeConstraints<ActionListType>("simpleAction")
            }
            parameter("protocol", string) {
                extractPropertyDescription<PolicyRuleType>(schema)
                mandatory = true
                defaultValue = defaultProtocol
                predefinedAnswers = allowedProtocols
            }
            parameter("direction", string) {
                // direction has no description in the schema
                description = "Direction"
                mandatory = true
                additionalQualifiers += schema.simpleTypeConstraints<PolicyRuleType>("direction")
            }
        }
        step("Addresses") {
            visibility = WhenNonNull("parent")
            parameter(sourceAddressTypeParameterName, string) {
                description = "Traffic Source"
                mandatory = true
                defaultValue = defaultAddressType
                predefinedAnswers = allowedAddressTypes
            }
            parameter("srcAddressCidr", string) {
                description = schema.propertyDescription<AddressType>("subnet")
                mandatory = true
                visibility = FromStringParameter(sourceAddressTypeParameterName, "CIDR")
            }
            parameter("srcAddressNetworkType", string) {
                description = "Type of source network address"
                mandatory = true
                visibility = FromStringParameter(sourceAddressTypeParameterName, "Network")
                defaultValue = defaultNetworkType
                predefinedAnswers = allowedNetworkTypes
            }
            parameter("srcAddressNetwork", reference<VirtualNetwork>()) {
                description = schema.propertyDescription<AddressType>("virtual_network")
                mandatory = true
                visibility = FromStringParameter("srcAddressNetworkType", "reference")
            }
            parameter("srcAddressPolicy", reference<NetworkPolicy>()) {
                description = schema.propertyDescription<AddressType>("network-policy")
                mandatory = true
                visibility = FromStringParameter(sourceAddressTypeParameterName, "Policy")
            }
            parameter("srcAddressSecurityGroup", reference<SecurityGroup>()) {
                description = schema.propertyDescription<AddressType>("security-group")
                mandatory = true
                visibility = FromStringParameter(sourceAddressTypeParameterName, "Security Group")
            }
            parameter("srcPorts", string) {
                extractPropertyDescription<PolicyRuleType>(schema)
                mandatory = true
                defaultValue = defaultPort
            }
            parameter(destinationAddressTypeParameterName, string) {
                description = "Traffic Destination"
                mandatory = true
                defaultValue = defaultAddressType
                predefinedAnswers = allowedAddressTypes
            }
            parameter("dstAddressCidr", string) {
                description = schema.propertyDescription<AddressType>("subnet")
                mandatory = true
                visibility = FromStringParameter(destinationAddressTypeParameterName, "CIDR")
            }
            parameter("dstAddressNetworkType", string) {
                description = "Type of destination network address"
                mandatory = true
                visibility = FromStringParameter(destinationAddressTypeParameterName, "Network")
                defaultValue = defaultNetworkType
                predefinedAnswers = allowedNetworkTypes
            }
            parameter("dstAddressNetwork", reference<VirtualNetwork>()) {
                description = schema.propertyDescription<AddressType>("virtual_network")
                mandatory = true
                visibility = FromStringParameter("dstAddressNetworkType", "reference")
            }
            parameter("dstAddressPolicy", reference<NetworkPolicy>()) {
                description = schema.propertyDescription<AddressType>("network-policy")
                mandatory = true
                visibility = FromStringParameter(destinationAddressTypeParameterName, "Policy")
            }
            parameter("dstAddressSecurityGroup", reference<SecurityGroup>()) {
                description = schema.propertyDescription<AddressType>("security-group")
                mandatory = true
                visibility = FromStringParameter(destinationAddressTypeParameterName, "Security Group")
            }
            parameter("dstPorts", string) {
                extractPropertyDescription<PolicyRuleType>(schema)
                mandatory = true
                defaultValue = defaultPort
            }
        }
        step("Advanced Options") {
            visibility = WhenNonNull("parent")
            parameter("log", boolean) {
                extractPropertyDescription<ActionListType>(schema)
                mandatory = true
                defaultValue = false
            }
            parameter("services", boolean) {
                description = "Services"
                mandatory = true
                defaultValue = false
            }
            parameter("serviceInstances", array(reference<ServiceInstance>())) {
                description = "Service instances"
                mandatory = true
                visibility = FromBooleanParameter("services")
            }

            parameter(mirrorShowParameterName, boolean) {
                description = "Mirror"
                mandatory = true
                defaultValue = false
            }
            parameter(mirrorTypeParameterName, string) {
                description = "Mirror Type"
                mandatory = true
                visibility = FromBooleanParameter(mirrorShowParameterName)
                defaultValue = defaultMirrorType
                predefinedAnswers = allowedMirrorTypes
            }
            parameter("analyzerInstance", reference<ServiceInstance>()) {
                description = "Analyzer Instance"
                mandatory = true
                visibility = ConditionConjunction(
                    FromStringParameter(mirrorTypeParameterName, "Analyzer Instance"),
                    FromBooleanParameter(mirrorShowParameterName)
                )
            }
            parameter("analyzerName", string) {
                description = "Analyzer Name"
                mandatory = true
                visibility = ConditionConjunction(
                    ConditionAlternative(
                        FromStringParameter(mirrorTypeParameterName, "NIC Assisted"),
                        FromStringParameter(mirrorTypeParameterName, "Analyzer IP")),
                    FromBooleanParameter(mirrorShowParameterName)
                )
            }
            parameter("nicAssistedVlan", number) {
                description = "NIC Assisted VLAN"
                mandatory = true
                min = 1
                max = 4094
                visibility = ConditionConjunction(
                    FromStringParameter(mirrorTypeParameterName, "NIC Assisted"),
                    FromBooleanParameter(mirrorShowParameterName)
                )
            }
            parameter("analyzerIP", string) {
                description = "Analyzer IP"
                mandatory = true
                visibility = ConditionConjunction(
                    FromStringParameter(mirrorTypeParameterName, "Analyzer IP"),
                    FromBooleanParameter(mirrorShowParameterName)
                )
            }
            parameter("analyzerMac", string) {
                description = "Analyzer MAC"
                mandatory = true
                visibility = ConditionConjunction(
                    FromStringParameter(mirrorTypeParameterName, "Analyzer IP"),
                    FromBooleanParameter(mirrorShowParameterName)
                )
            }
            parameter("udpPort", number) {
                description = "UDP Port"
                mandatory = true
                visibility = ConditionConjunction(
                    FromStringParameter(mirrorTypeParameterName, "Analyzer IP"),
                    FromBooleanParameter(mirrorShowParameterName)
                )
            }
            parameter("juniperHeader", string) {
                description = "Juniper Header"
                mandatory = true
                defaultValue = defaultJuniperHeaderOption
                predefinedAnswers = allowedJuniperHeaderOptions
                visibility = ConditionConjunction(
                    FromStringParameter(mirrorTypeParameterName, "Analyzer IP"),
                    FromBooleanParameter(mirrorShowParameterName)
                )
            }
            parameter("routingInstance", reference<VirtualNetwork>()) {
                description = "Routing Instance"
                mandatory = true
                visibility = ConditionConjunction(
                    FromStringParameter(mirrorTypeParameterName, "Analyzer IP"),
                    FromBooleanParameter(mirrorShowParameterName),
                    FromStringParameter("juniperHeader", "disabled")
                )
            }
            parameter("nexthopMode", string) {
                description = "Nexthop Mode"
                mandatory = true
                defaultValue = defaultNexthopMode
                predefinedAnswers = allowedNexthopModes
                visibility = ConditionConjunction(
                    FromStringParameter(mirrorTypeParameterName, "Analyzer IP"),
                    FromBooleanParameter(mirrorShowParameterName)
                )
            }
            parameter("vtepDestIp", string) {
                description = "VTEP Dest IP"
                mandatory = true
                visibility = ConditionConjunction(
                    FromStringParameter(mirrorTypeParameterName, "Analyzer IP"),
                    FromBooleanParameter(mirrorShowParameterName),
                    FromStringParameter("nexthopMode", "static")
                )
            }
            parameter("vtepDestMac", string) {
                description = "VTEP Dest MAC"
                mandatory = true
                visibility = ConditionConjunction(
                    FromStringParameter(mirrorTypeParameterName, "Analyzer IP"),
                    FromBooleanParameter(mirrorShowParameterName),
                    FromStringParameter("nexthopMode", "static")
                )
            }
            parameter("vni", number) {
                description = "VxLAN ID"
                mandatory = true
                visibility = ConditionConjunction(
                    FromStringParameter(mirrorTypeParameterName, "Analyzer IP"),
                    FromBooleanParameter(mirrorShowParameterName),
                    FromStringParameter("nexthopMode", "static")
                )
            }
        }
    }
}

internal fun removePolicyRuleWorkflow(schema: Schema): WorkflowDefinition {
    val workflowName = "Remove network policy rule"

    return customWorkflow<NetworkPolicy>(workflowName).withScriptFile("removeRuleFromPolicy") {
        parameter(parent, reference<NetworkPolicy>()) {
            extractRelationDescription<Project, NetworkPolicy>(schema)
            mandatory = true
        }
        parameter("rule", string) {
            visibility = WhenNonNull(parent)
            description = "Rule to remove"
            mandatory = true
            predefinedAnswersFrom = actionCallTo(networkPolicyRules).parameter(parent)
        }
    }
}