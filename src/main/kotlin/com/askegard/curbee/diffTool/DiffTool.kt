package com.askegard.curbee.diffTool

import com.askegard.curbee.annotations.AuditKey
import com.askegard.curbee.exceptions.MissingAuditKeyException
import com.askegard.curbee.models.ChangeType
import com.askegard.curbee.models.ListUpdate
import com.askegard.curbee.models.PropertyUpdate
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlin.reflect.cast
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation

class DiffTool(
    private val objectMapper: ObjectMapper = jacksonObjectMapper()
) {
    fun diff(previous: Any, current: Any): List<ChangeType> {
        if (previous == current) {
            return emptyList()
        }
        else if (previous::class != current::class) {
            return listOf(PropertyUpdate(
                property = "class",
                previous = previous::class.toString(),
                current = previous::class.toString(),
            ))
        }
        else if (previous::class.javaPrimitiveType != null) {
            return listOf(PropertyUpdate(
                property = "primitive value",
                previous = previous.toString(),
                current = current.toString(),
            ))
        }

        return getDiffSerialization(previous, current).toList()
    }

    private fun getDiffSerialization(previous: Any, current: Any, parentPath: String = "", isParentList: Boolean = false): MutableList<ChangeType> {
        val previousSerialized = objectMapper.writeValueAsString(previous)
        val previousAsMap = objectMapper.readValue<Map<String,Any?>>(previousSerialized)
        val currentSerialized = objectMapper.writeValueAsString(current)
        val currentAsMap = objectMapper.readValue<Map<String,Any?>>(currentSerialized)

        val diffs = mutableListOf<ChangeType>()
        for ((key, previousValue) in previousAsMap) {
            val currentValue = currentAsMap[key]

            val propertyPath = if (parentPath.isEmpty()){
                key
            } else if (isParentList && previousValue != null){
                val auditKey = getAuditKey(previousValue, parentPath)
                "$parentPath[$auditKey]"
            }
            else if (isParentList && currentValue != null){
                val auditKey = getAuditKey(currentValue, parentPath)
                "$parentPath[$auditKey]"
            }
            else {
                "$parentPath.$key"
            }

            when {
                previousValue == null && currentValue != null -> diffs.add(PropertyUpdate(
                    property = propertyPath,
                    previous = null,
                    current = currentValue.toString(),
                ))
                previousValue != null && currentValue == null -> diffs.add(PropertyUpdate(
                    property = propertyPath,
                    previous = previousValue.toString(),
                    current = null,
                ))
                previousValue is Map<*, *> && currentValue is Map<*, *> -> {
                    val nestedPropertyDiffs = getDiffSerialization(
                        previous = previousValue as Map<String, Any>,
                        current = currentValue as Map<String, Any>,
                        parentPath = parentPath,
                    )
                    diffs.addAll(nestedPropertyDiffs)
                }
                previousValue is List<*> && currentValue is List<*> -> {
                    diffs.addAll(getListDiffs(previousValue as List<Any>, currentValue as List<Any>, propertyPath))
                }
                previousValue != currentValue -> diffs.add(PropertyUpdate(
                    property = propertyPath,
                    previous = previousValue.toString(),
                    current = currentValue.toString(),
                ))
            }
        }

        return diffs
    }

    private fun getListDiffs(previous: List<Any>, current: List<Any>, propertyPath: String): MutableList<ChangeType> {
        if (previous.size != current.size) {
            return mutableListOf(
                ListUpdate(
                    property = propertyPath,
                    added = current.filter { !previous.contains(it) }.map { it.toString() },
                    removed = previous.filter { !current.contains(it) }.map { it.toString() },
                )
            )
        }

        val diffs = mutableListOf<ChangeType>()
        previous.forEachIndexed { index, item ->
            diffs.addAll(getDiffSerialization(item, current[index], propertyPath, true))
        }

        return diffs
    }

    private inline fun <reified E: Any> getAuditKey(item: E, parentPath: String): String {
        val itemCast = E::class.cast(item)
        var id: String? = null
        for (member in E::class.declaredMemberProperties) {
            if (member.findAnnotation<AuditKey>() != null) {
                return member.get(itemCast).toString()
            }
            if (member.name == "id") {
                id = member.get(itemCast).toString()
            }
        }
        if (id != null) {
            return id
        }
        throw MissingAuditKeyException("Unable to determine audit key for field in $parentPath")
    }
}