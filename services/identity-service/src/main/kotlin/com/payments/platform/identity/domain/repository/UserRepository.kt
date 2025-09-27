package com.payments.platform.identity.domain.repository

import com.payments.platform.identity.domain.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Spring Data JPA repository for the User entity.
 *
 * By extending JpaRepository, we get a full set of CRUD (Create, Read, Update, Delete)
 * operations for our User entity without writing any implementation code.
 *
 * @see com.payments.platform.identity.domain.entity.User
 */
@Repository
interface UserRepository : JpaRepository<User, UUID>