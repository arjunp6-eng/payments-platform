package com.payments.platform.identity.application.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.payments.platform.events.UserRegisteredEvent
import com.payments.platform.identity.application.port.`in`.dto.RegisterUserRequest
import com.payments.platform.identity.application.port.`in`.dto.UserResponse
import com.payments.platform.identity.domain.entity.User
import com.payments.platform.identity.domain.repository.UserRepository
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val kafkaTemplate: KafkaTemplate<String, String>, // Ensure this is <String, String>
    private val objectMapper: ObjectMapper
) {
    fun registerUser(request: RegisterUserRequest): UserResponse {
        val newUser = User(
            email = request.email,
            passwordHash = passwordEncoder.encode(request.password),
            status = "PENDING_VERIFICATION",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        val savedUser = userRepository.save(newUser)

        val event = UserRegisteredEvent(
            userId = savedUser.id,
            email = savedUser.email
        )

        // Manually convert the event object to a JSON String
        val jsonEvent = objectMapper.writeValueAsString(event)

        // Send the JSON String to Kafka
        kafkaTemplate.send("user-events", jsonEvent)

        return savedUser.toUserResponse()
    }

    private fun User.toUserResponse(): UserResponse = UserResponse(
        id = this.id,
        email = this.email,
        status = this.status,
        createdAt = this.createdAt
    )
}