/*
 * Copyright (c) 2018 Juniper Networks, Inc. All rights reserved.
 */

package net.juniper.contrail.vro.generator

import net.juniper.contrail.api.ApiObjectBase

class CustomMappingModel (
    val nestedClasses: List<NestedClassInfo>,
    val proxyClasses: List<ClassInfo>,
    val converters: List<ConverterInfo>,
    val findableClasses: List<Class<*>>,
    val rootClasses: List<ClassInfo>,
    val relations: List<Relation>,
    val referenceRelations: List<RefRelation>,
    val nestedRelations: List<NestedRelation>
) : GenericModel()

fun generateCustomMappingModel(
    propertyClasses: List<Class<*>>,
    objectClasses: List<Class<out ApiObjectBase>>,
    rootClasses: List<Class<out ApiObjectBase>>,
    nestedClasses: NestedClasses,
    relationsModel: RelationsModel
): CustomMappingModel {
    val rootClassesInfo = rootClasses.toClassInfo()
    val innerClassesInfo = nestedClasses.nonAliasClasses.toNestedClassInfo()
    val proxyClassesInfo = nestedClasses.aliasClasses.keySet().map { ClassInfo(it) }.toList()
    val converterInfo = nestedClasses.aliasClasses.values().toConverterInfo()

    return CustomMappingModel(
        innerClassesInfo,
        proxyClassesInfo,
        converterInfo,
        objectClasses,
        rootClassesInfo,
        relationsModel.relations,
        relationsModel.referenceRelations,
        relationsModel.nestedRelations
    )
}
