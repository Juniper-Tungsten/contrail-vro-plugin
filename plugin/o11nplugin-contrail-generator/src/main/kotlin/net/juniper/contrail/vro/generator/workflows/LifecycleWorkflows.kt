/*
 * Copyright (c) 2018 Juniper Networks, Inc. All rights reserved.
 */

package net.juniper.contrail.vro.generator.workflows

import net.juniper.contrail.vro.config.constants.Connection
import net.juniper.contrail.vro.config.ObjectClass
import net.juniper.contrail.vro.config.bold
import net.juniper.contrail.vro.config.constants.item
import net.juniper.contrail.vro.config.constants.parent
import net.juniper.contrail.vro.config.isApiTypeClass
import net.juniper.contrail.vro.config.parameterName
import net.juniper.contrail.vro.config.pluginName
import net.juniper.contrail.vro.generator.model.Property
import net.juniper.contrail.vro.workflows.dsl.WorkflowDefinition
import net.juniper.contrail.vro.workflows.dsl.withScript
import net.juniper.contrail.vro.workflows.dsl.workflow
import net.juniper.contrail.vro.workflows.model.reference
import net.juniper.contrail.vro.workflows.model.string
import net.juniper.contrail.vro.workflows.schema.Schema
import net.juniper.contrail.vro.workflows.schema.objectDescription
import net.juniper.contrail.vro.workflows.schema.relationDescription

fun createWorkflow(clazz: ObjectClass, parentClazz: ObjectClass?, refs: List<ObjectClass>, schema: Schema): WorkflowDefinition {

    val workflowName = "Create ${clazz.allLowerCase}"
    val parentName = parentClazz?.pluginName ?: Connection

    return workflow(workflowName).withScript(createScriptBody(clazz, parentClazz, refs)) {
        description = schema.createWorkflowDescription(clazz)
        parameter("name", string) {
            description = "${clazz.allCapitalized} name"
            mandatory = true

        }
        parameter(parent, parentName.reference) {
            description = "Parent ${parentName.allCapitalized}"
            mandatory = true

        }

        for (ref in refs) {
            parameter(ref.parameterName, ref.reference) {
                description = schema.relationInCreateWorkflowDescription(clazz, ref)
                mandatory = true
            }
        }
    }
}

fun editWorkflow(clazz: ObjectClass, schema: Schema): WorkflowDefinition {

    val workflowName = "Edit ${clazz.allLowerCase}"

    return workflow(workflowName).withScript(editScriptBody(clazz)) {
        description = schema.createWorkflowDescription(clazz)
        parameter(item, clazz.reference) {
            description = "${clazz.allCapitalized} to edit"
            mandatory = true
            showInInventory = true
        }

        addProperties(clazz, schema, editMode = true)
    }
}

fun deleteWorkflow(clazz: ObjectClass) =
    deleteWorkflow(clazz.pluginName, deleteScriptBody(clazz.pluginName))

private fun Schema.createWorkflowDescription(clazz: ObjectClass) : String? {
    val objectDescription = objectDescription(clazz) ?: return null
    return """
        ${clazz.allCapitalized.bold}
        $objectDescription
    """.trimIndent()
}

private fun Schema.relationInCreateWorkflowDescription(parentClazz: ObjectClass, clazz: ObjectClass) = """
${clazz.allCapitalized}
${relationDescription(parentClazz, clazz)}
""".trim()

private fun createScriptBody(clazz: Class<*>, parentClazz: ObjectClass?, references: List<ObjectClass>) = """
${parent.retrieveExecutor}
var $item = new Contrail${clazz.pluginName}();
$item.setName(name);
${references.addAllReferences}
$executor.create${clazz.pluginName}($item${if (parentClazz == null) "" else ", $parent"});
""".trimIndent()

private fun editScriptBody(clazz: Class<*>) = """
${clazz.editPropertiesCode(item)}
${item.retrieveExecutor}
${item.updateAsClass(clazz.pluginName)}
""".trimIndent()

private fun deleteScriptBody(className: String) = """
${item.retrieveExecutor}
${item.deleteAsClass(className)}
""".trimIndent()

fun Class<*>.editPropertiesCode(item: String, level: Int = 0) =
    workflowEditableProperties.joinToString("\n") { it.editCode(item, level) }

fun Property.editCode(item: String, level: Int) = when {
    clazz.isApiTypeClass && level <= maxComplexLevel -> complexEditCode(item, level)
    !clazz.isApiTypeClass && level <= maxPrimitiveLevel -> primitiveEditCode(item)
    else -> ""
}

fun Property.primitiveEditCode(item: String) =
    "$item.set${propertyName.capitalize()}($propertyName);"

fun Property.complexEditCode(item: String, level: Int): String = """
var $propertyName = $item.get${propertyName.capitalize()}();
if (${propertyName.condition}) {
    if (!$propertyName) $propertyName = new Contrail${clazz.pluginName}();
${clazz.editPropertiesCode(propertyName, level + 1).prependIndent(tab)}
} else {
    $propertyName = null;
}
$item.set${propertyName.capitalize()}($propertyName);
""".trim()