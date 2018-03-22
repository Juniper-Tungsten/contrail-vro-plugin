/*
 * Copyright (c) 2018 Juniper Networks, Inc. All rights reserved.
 */

package net.juniper.contrail.vro.generator.model

import net.juniper.contrail.api.ApiObjectBase
import net.juniper.contrail.api.ApiPropertyBase
import net.juniper.contrail.vro.generator.ProjectInfo
import net.juniper.contrail.vro.generator.generatedPackageName
import net.juniper.contrail.vro.generator.generatedSourcesRoot
import net.juniper.contrail.vro.generator.templatePath
import net.juniper.contrail.vro.generator.util.div
import net.juniper.contrail.vro.generator.util.rootClasses

fun generateModel(
    info: ProjectInfo,
    objectClasses: List<Class<out ApiObjectBase>>,
    propertyClasses: List<Class<out ApiPropertyBase>>
): RelationsModel {

    val rootClasses = objectClasses.rootClasses()

    val relations = generateRelations(objectClasses)
    val refRelations = generateReferenceRelations(objectClasses)
    val nestedRelations = generateNestedRelations(objectClasses)
    val referenceWrappers = generateReferenceWrappers(objectClasses)

    val relationsModel = generateRelationsModel(relations, refRelations, nestedRelations, rootClasses)
    val customMappingModel = generateCustomMappingModel(objectClasses, rootClasses, propertyClasses, relations, refRelations, nestedRelations)
    val wrappersModel = generateWrappersModel(referenceWrappers, nestedRelations)
    val findersModel = generateFindersModel(objectClasses, referenceWrappers, nestedRelations)

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
    coreGenerator.generate(customMappingModel, "Executor.kt")
    coreGenerator.generate(wrappersModel, "Wrappers.kt")

    return relationsModel
}