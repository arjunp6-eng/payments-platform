package com.payments.platform.identity.application.service

import com.payments.platform.identity.application.port.`in`.dto.RegisterUserRequest
import com.payments.platform.identity.application.port.`in`.dto.UserResponse
import com.payments.platform.identity.domain.entity.User
import com.payments.platform.identity.domain.repository.UserRepository
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Instant

/**
 * Contains the core business logic for user-related operations.
 * The service layer is responsible for orchestrating data access and business rules.
 */
@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val kafkaTemplate: KafkaTemplate<String, String>
) {
    fun registerUser(request: RegisterUserRequest): UserResponse {
        // Create a new User entity from the incoming request data
        val newUser = User(
            email = request.email,
            // Securely hash the plain-text password before saving
            passwordHash = passwordEncoder.encode(request.password),
            status = "PENDING_VERIFICATION",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        // Save the new user to the database
        val savedUser = userRepository.save(newUser)

        // Publish an event to the "user-events" Kafka topic
        kafkaTemplate.send("user-events", "UserRegistered:${savedUser.id}")

        // Convert the saved user entity to a safe response DTO and return it
        return savedUser.toUserResponse()
    }

    /**
     * A private helper function to map the internal User entity to the public
     * UserResponse DTO, ensuring sensitive data like the password hash is not exposed.
     */
    private fun User.toUserResponse(): UserResponse = UserResponse(
        id = this.id,
        email = this.email,
        status = this.status,
        createdAt = this.createdAt
    )
}

