package com.payments.platform.notification

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

    @KafkaListener(topics = ["user-events"], groupId = "notification-group")
    fun listenToUserEvents(message: String) {
        logger.info("--> Received message from user-events topic: $message")
        // In a real application, you would parse this message and send an email/SMS.
    }
}
