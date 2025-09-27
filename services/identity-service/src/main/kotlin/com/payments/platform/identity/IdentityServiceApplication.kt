package com.payments.platform.identity

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class IdentityServiceApplication

fun  main(args: Array<String>) {
    runApplication<IdentityServiceApplication>(*args)
}
