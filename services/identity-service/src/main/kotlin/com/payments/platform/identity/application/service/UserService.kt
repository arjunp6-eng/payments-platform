package com.payments.platform.identity.application.service

import com.payments.platform.identity.application.port.`in`.dto.RegisterUserRequest
import com.payments.platform.identity.application.port.`in`.dto.UserResponse
import com.payments.platform.identity.domain.entity.User
import com.payments.platform.identity.domain.repository.UserRepository
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
    private val passwordEncoder: PasswordEncoder
) {
    fun registerUser(request: RegisterUserRequest): UserResponse {
        // Here you would add validation logic (e.g., check if email already exists)

        val newUser = User(
            email = request.email,
            passwordHash = passwordEncoder.encode(request.password), // Securely hash the password
            status = "PENDING_VERIFICATION", // A more realistic initial status
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        val savedUser = userRepository.save(newUser)

        // Map the saved User entity to the UserResponse DTO before returning
        return savedUser.toUserResponse()
    }

    /**
     * A private extension function to map a User entity to a UserResponse DTO.
     * This keeps the mapping logic clean and reusable.
     */
    private fun User.toUserResponse(): UserResponse = UserResponse(
        id = this.id,
        email = this.email,
        status = this.status,
        createdAt = this.createdAt
    )
}

