/*
 * Copyright (c) 2018 Juniper Networks, Inc. All rights reserved.
 */

package net.juniper.contrail.vro.config

import java.util.Properties

val globalProjectInfo = readProjectInfo()

fun readProjectInfo(): ProjectInfo {
    val props = Properties()
    props.load(ProjectInfo::class.java.getResourceAsStream("/maven.properties"))
    val configRoot = props["project.dir"] as String
    val configPattern = "-config$".toRegex()
    val finalProjectRoot = configRoot.replace(configPattern, "")
    val generatorRoot = configRoot.replace(configPattern, "-core")
    val coreRoot = configRoot.replace(configPattern, "-core")
    val customRoot = configRoot.replace(configPattern, "-custom")
    val packageRoot = configRoot.replace(configPattern, "-package")
    val version = props["project.version"] as String
    val buildNumber = props["build.number"] as String
    val workflowPackage = props["workflow.package"] as String
    val baseVersion = version.replace("-SNAPSHOT", "")

    return ProjectInfo(
        finalProjectRoot = finalProjectRoot,
        configRoot = configRoot,
        generatorRoot = generatorRoot,
        coreRoot = coreRoot,
        customRoot = customRoot,
        packageRoot = packageRoot,
        version = version,
        baseVersion = baseVersion,
        buildNumber = buildNumber,
        workflowPackage = workflowPackage)
}

data class ProjectInfo(
    val finalProjectRoot: String,
    val configRoot: String,
    val generatorRoot: String,
    val coreRoot: String,
    val customRoot: String,
    val packageRoot: String,
    val version: String,
    val baseVersion: String,
    val buildNumber: String,
    val workflowPackage: String)