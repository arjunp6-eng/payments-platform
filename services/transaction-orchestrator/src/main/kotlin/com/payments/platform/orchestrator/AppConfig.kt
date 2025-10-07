package com.payments.platform.orchestrator

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder
import org.springframework.web.reactive.function.client.WebClient

@Configuration
open class AppConfig {

    @Bean
    open fun webClient(): WebClient = WebClient.builder().build()

    @Bean
    open fun userEventsTopic(): NewTopic {
        return TopicBuilder.name("user-events")
            .partitions(1)
            .replicas(1)
            .build()
    }

    @Bean
    open fun userOnboardingDlqTopic(@Value("\${saga.user-onboarding.dlq-topic}") dlqTopic: String): NewTopic {
        return TopicBuilder.name(dlqTopic)
            .partitions(1)
            .replicas(1)
            .build()
    }
}
