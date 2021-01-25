package com.example.demo.repository

import com.example.demo.Dbmodel.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepo : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
    fun findByToken(token: String): User?
    fun findByEmailAndPassword(email: String, password: String): User?
}