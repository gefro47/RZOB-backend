package com.gefro.springbootkotlinRZOBbackend.repository

import com.gefro.springbootkotlinRZOBbackend.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import javax.transaction.Transactional

@Repository
@Transactional
interface UserRepository: JpaRepository<User, String> {
}