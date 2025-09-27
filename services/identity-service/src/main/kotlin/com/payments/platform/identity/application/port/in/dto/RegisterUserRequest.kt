package com.payments.platform.identity.application.port.`in`.dto

/**
 * A Data Transfer Object (DTO) that represents the request body for registering a new user.
 * Using a DTO prevents exposing our internal domain entities directly through the API.
 */
data class RegisterUserRequest(
    val email: String,
    val password: String
)
