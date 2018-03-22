/*
 * Copyright (c) 2018 Juniper Networks, Inc. All rights reserved.
 */

package net.juniper.contrail.vro.generator

import net.juniper.contrail.vro.workflows.runWorkflowGenerator
import java.util.Properties

object Generator {

    private val generatedSourcesRoot = "/target/generated-sources"
    private val templatePath = "/templates"

    @JvmStatic fun main(args: Array<String>) {
        val projectInfo = readProjectInfo()

        val objectClasses = objectClasses()
        val rootClasses = objectClasses.rootClasses()

        val relations = generateRelations(objectClasses)
        val refRelations = generateReferenceRelations(objectClasses)
        val nestedRelations = generateNestedRelations(objectClasses)
        val referenceWrappers = generateReferenceWrappers(objectClasses)

        val relationsModel = generateRelationsModel(relations, refRelations, nestedRelations, rootClasses)
        val customMappingModel = generateCustomMappingModel(objectClasses, rootClasses, relations, refRelations, nestedRelations)
        val wrappersModel = generateWrappersModel(referenceWrappers, nestedRelations)
        val findersModel = generateFindersModel(objectClasses, referenceWrappers, nestedRelations)

        val customMappingConfig = GeneratorConfig(
            baseDir = projectInfo.customRoot / generatedSourcesRoot,
            packageName = generatedPackageName)
        val customMappingGenerator = GeneratorEngine(customMappingConfig, templatePath)
        customMappingGenerator.generate(customMappingModel, "CustomMapping.kt")

        val coreGeneratorConfig = GeneratorConfig(
            baseDir = projectInfo.coreRoot / generatedSourcesRoot,
            packageName = generatedPackageName)

        val coreGenerator = GeneratorEngine(coreGeneratorConfig, templatePath)
        coreGenerator.generate(relationsModel, "Relations.kt")
        coreGenerator.generate(findersModel, "Finders.kt")
        coreGenerator.generate(customMappingModel, "Executor.kt")
        coreGenerator.generate(wrappersModel, "Wrappers.kt")
        
        runWorkflowGenerator(projectInfo, objectClasses)
    }
}

fun readProjectInfo(): ProjectInfo {
    val props = Properties()
    props.load(Generator::class.java.getResourceAsStream("/maven.properties"))
    val generatorRoot = props["project.dir"] as String
    val generatorPattern = "-generator$".toRegex()
    val staticRoot = "$generatorRoot/src/main/static"
    val finalProjectRoot = generatorRoot.replace(generatorPattern, "")
    val coreRoot = generatorRoot.replace(generatorPattern, "-core")
    val customRoot = generatorRoot.replace(generatorPattern, "-custom")
    val packageRoot = generatorRoot.replace(generatorPattern, "-package")
    val version = props["project.version"] as String
    val buildNumber = props["build.number"] as String
    val baseVersion = version.replace("-SNAPSHOT", "")

    return ProjectInfo(
        generatorRoot = generatorRoot,
        finalProjectRoot = finalProjectRoot,
        coreRoot = coreRoot,
        customRoot = customRoot,
        packageRoot = packageRoot,
        staticRoot = staticRoot,
        version = version,
        baseVersion = baseVersion,
        buildNumber = buildNumber)
}

data class ProjectInfo(
    val generatorRoot: String,
    val finalProjectRoot: String,
    val coreRoot: String,
    val customRoot: String,
    val packageRoot: String,
    val staticRoot: String,
    val version: String,
    val baseVersion: String,
    val buildNumber: String)