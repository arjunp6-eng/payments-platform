package com.payments.platform.identity.adapter.`in`.web

import com.payments.platform.identity.application.port.`in`.dto.RegisterUserRequest
import com.payments.platform.identity.application.port.`in`.dto.UserResponse
import com.payments.platform.identity.application.service.UserService
import com.payments.platform.identity.domain.entity.User
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService
) {
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun registerUser(@RequestBody request: RegisterUserRequest): UserResponse {
        return userService.registerUser(request)
    }
}

