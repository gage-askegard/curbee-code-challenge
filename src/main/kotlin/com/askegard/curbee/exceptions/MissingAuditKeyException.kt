package com.askegard.curbee.exceptions

class MissingAuditKeyException(
    override val message: String,
): Exception(message = message) {
}