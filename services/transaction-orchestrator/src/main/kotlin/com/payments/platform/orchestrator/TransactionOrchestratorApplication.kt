package com.payments.platform.orchestrator

import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.retry.annotation.EnableRetry

@SpringBootApplication
@EnableRetry
open class TransactionOrchestratorApplication

fun main(args: Array<String>) {
    runApplication<TransactionOrchestratorApplication>(*args) {
        webApplicationType = WebApplicationType.NONE
    }
}
