package com.askegard.curbee.models

sealed class ChangeType(
    open val property: String,
)

class PropertyUpdate(
    override val property: String,
    val previous: String?,
    val current: String?,
): ChangeType(property)

class ListUpdate(
    override val property: String,
    val added: Array<String>,
    val removed: Array<String>,
): ChangeType(property)