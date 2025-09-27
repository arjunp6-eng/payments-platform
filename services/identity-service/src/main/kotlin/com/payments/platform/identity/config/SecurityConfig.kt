package com.payments.platform.identity.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

/**
 * Basic security configuration.
 * We define the PasswordEncoder bean here and disable CSRF for our stateless API.
 * This class and its @Bean methods are marked 'open' to allow Spring to create proxies.
 */
@Configuration
open class SecurityConfig {

    @Bean
    open fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    open fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() } // Disable CSRF protection for stateless REST APIs
            authorizeRequests {
                // Allow public access to the registration endpoint
                authorize("/api/v1/users/register", permitAll)
                // Secure all other endpoints by default
                authorize(anyRequest, authenticated)
            }
        }
        return http.build()
    }
}
