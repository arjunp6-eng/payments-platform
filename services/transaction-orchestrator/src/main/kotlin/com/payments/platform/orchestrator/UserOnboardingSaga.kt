package com.payments.platform.orchestrator

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.payments.platform.events.UserRegisteredEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class UserOnboardingSaga(
    private val ledgerServiceClient: LedgerServiceClient
) {
    private val objectMapper = jacksonObjectMapper().findAndRegisterModules()

    companion object {
        private val logger = LoggerFactory.getLogger(UserOnboardingSaga::class.java)
    }

    @KafkaListener(topics = ["user-events"], groupId = "orchestrator-group")
    fun handleUserRegisteredEvent(message: String) {
        logger.info("--> Received raw message string: $message")
        try {
            // Manually parse the JSON string
            val event = objectMapper.readValue<UserRegisteredEvent>(message)
            logger.info("--> Successfully deserialized UserRegisteredEvent: $event")
            // Delegate the parsed object to the client
            ledgerServiceClient.createWalletForUser(event)
        } catch (e: Exception) {
            logger.error("!!! Failed to deserialize message, cannot start saga: $message", e)
        }
    }
}
