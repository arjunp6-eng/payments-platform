package com.payments.platform.identity.application.port.`in`.dto

import java.time.Instant
import java.util.UUID

/**
 * A DTO representing the data sent back to the client after a successful user operation.
 * It purposefully excludes sensitive information like the password hash.
 */
data class UserResponse(
    val id: UUID,
    val email: String,
    val status: String,
    val createdAt: Instant
)