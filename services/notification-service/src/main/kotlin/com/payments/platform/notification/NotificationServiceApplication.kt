package com.payments.platform.notification

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.payments.platform.events.UserRegisteredEvent
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@SpringBootApplication
open class NotificationServiceApplication

fun main(args: Array<String>) {
    runApplication<NotificationServiceApplication>(*args)
}

@Component
class UserEventsListener {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val objectMapper = jacksonObjectMapper().findAndRegisterModules()

    @KafkaListener(topics = ["user-events"], groupId = "notification-group")
    fun listenToUserEvents(message: String) { // 1. Receive the raw String
        logger.info("--> Received raw message string: $message")
        try {
            // 2. Manually parse the JSON string into our object
            val event = objectMapper.readValue<UserRegisteredEvent>(message)
            logger.info("--> Successfully deserialized UserRegisteredEvent: $event")
        } catch (e: Exception) {
            // 3. If parsing fails, log the exact message that caused the error
            logger.error("!!! Failed to deserialize message: $message", e)
        }
    }
}

