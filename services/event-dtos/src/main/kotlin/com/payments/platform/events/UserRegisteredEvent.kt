package com.payments.platform.events

import java.util.UUID

/**
 * A structured event DTO for when a new user is registered.
 * This will be serialized to JSON and sent via Kafka.
 */
data class UserRegisteredEvent(
    val eventId: UUID = UUID.randomUUID(),
    val userId: UUID,
    val email: String
)
