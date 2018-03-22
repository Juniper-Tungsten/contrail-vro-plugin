/*
 * Copyright (c) 2018 Juniper Networks, Inc. All rights reserved.
 */

package net.juniper.contrail.vro.generator

import net.juniper.contrail.api.ApiObjectBase

fun generateFindersModel(): FindersModel {
    val objectClasses = ApiObjectBase::class.java.nonAbstractSubclassesIn(apiPackageName)
    return FindersModel(objectClasses)
}