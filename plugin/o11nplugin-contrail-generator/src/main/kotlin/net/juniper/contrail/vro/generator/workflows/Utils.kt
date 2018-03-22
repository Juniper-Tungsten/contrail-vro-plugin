/*
 * Copyright (c) 2018 Juniper Networks, Inc. All rights reserved.
 */

package net.juniper.contrail.vro.generator.workflows

import com.google.common.hash.Hashing
import net.juniper.contrail.vro.generator.scriptPath
import java.io.File

fun generateID(packageName: String, displayName: String) =
    Hashing.md5().newHasher()
        .putString("$packageName.$displayName", Charsets.UTF_8)
        .hash().toString()

fun loadFile(root: String, name: String): String {
    return File("$root/$scriptPath/$name.js").readText()
}