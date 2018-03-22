/*
 * Copyright (c) 2018 Juniper Networks, Inc. All rights reserved.
 */

package net.juniper.contrail.vro.generator.model

import net.juniper.contrail.vro.generator.ProjectInfo
import net.juniper.contrail.vro.generator.generatedPackageName
import net.juniper.contrail.vro.generator.generatedSourcesRoot
import net.juniper.contrail.vro.generator.templatePath
import net.juniper.contrail.vro.generator.util.div

fun generateModel(
    info: ProjectInfo,
    definition: RelationDefinition,
    objectClasses: List<ObjectClass>,
    rootClasses: List<ObjectClass>,
    propertyClasses: List<PropertyClass>
) {
    val relations = definition.relations
    val forwardRelations = definition.forwardRelations
    val backwardRelations = definition.backwardRelations
    val nestedRelations = definition.nestedRelations
    val referenceWrappers = generateReferenceWrappers(objectClasses)

    val relationsModel = generateRelationsModel(relations, forwardRelations, backwardRelations, nestedRelations, rootClasses)
    val customMappingModel = generateCustomMappingModel(objectClasses, rootClasses, propertyClasses, relations, forwardRelations, backwardRelations, nestedRelations)
    val wrappersModel = generateWrappersModel(referenceWrappers, nestedRelations)
    val findersModel = generateFindersModel(objectClasses, referenceWrappers, nestedRelations)
    val executorModel = generateExecutorModel(objectClasses, rootClasses, relations, forwardRelations)

    val customMappingConfig = GeneratorConfig(
        baseDir = info.customRoot / generatedSourcesRoot,
        packageName = generatedPackageName)
    val customMappingGenerator = GeneratorEngine(customMappingConfig, templatePath)
    customMappingGenerator.generate(customMappingModel, "CustomMapping.kt")

    val coreGeneratorConfig = GeneratorConfig(
        baseDir = info.coreRoot / generatedSourcesRoot,
        packageName = generatedPackageName)

    val coreGenerator = GeneratorEngine(coreGeneratorConfig, templatePath)
    coreGenerator.generate(relationsModel, "Relations.kt")
    coreGenerator.generate(findersModel, "Finders.kt")
    coreGenerator.generate(executorModel, "Executor.kt")
    coreGenerator.generate(wrappersModel, "Wrappers.kt")
}