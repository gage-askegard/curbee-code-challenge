package com.askegard.curbee.models

sealed class ChangeType(
    open val property: String,
)

data class PropertyUpdate(
    override val property: String,
    val previous: String?,
    val current: String?,
): ChangeType(property)

data class ListUpdate(
    override val property: String,
    val added: List<String>,
    val removed: List<String>,
): ChangeType(property)