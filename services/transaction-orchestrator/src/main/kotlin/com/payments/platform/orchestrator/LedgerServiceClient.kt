package com.payments.platform.orchestrator

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.payments.platform.events.UserRegisteredEvent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Recover
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException

@Component
open class LedgerServiceClient(
    private val webClient: WebClient,
    private val kafkaTemplate: KafkaTemplate<String, String>,
    @param:Value("\${ledger.service.url}") private val ledgerServiceUrl: String,
    @param:Value("\${saga.user-onboarding.dlq-topic}") private val dlqTopic: String
) {
    companion object {
        private val logger = LoggerFactory.getLogger(LedgerServiceClient::class.java)
    }

    @Retryable(
        retryFor = [WebClientRequestException::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 1000, multiplier = 2.0)
    )
    open fun createWalletForUser(event: UserRegisteredEvent) {
        logger.info("--> Attempting to create wallet for user: ${event.userId} (Attempt)...")

        val createAccountRequest = mapOf(
            "user_id" to event.userId.toString(),
            "account_type" to "CUSTOMER_WALLET"
        )

        webClient.post()
            .uri("$ledgerServiceUrl/v1/accounts")
            .bodyValue(createAccountRequest)
            .retrieve()
            .toBodilessEntity()
            .block()

        logger.info("Successfully created wallet for user: ${event.userId}")
    }

    @Recover
    open fun recover(error: WebClientRequestException, event: UserRegisteredEvent) {
        logger.error("All retry attempts failed for user: ${event.userId}. Error: ${error.message}")
        logger.info("Publishing failed event to DLQ topic: $dlqTopic")
        // Manually serialize to JSON string before sending to DLQ
        val jsonEvent = jacksonObjectMapper().findAndRegisterModules().writeValueAsString(event)
        // Correct the typo in the line below
        kafkaTemplate.send(dlqTopic, jsonEvent)
    }
}

