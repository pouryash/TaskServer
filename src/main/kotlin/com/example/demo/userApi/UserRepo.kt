package com.example.demo.userApi

import com.example.demo.Dbmodel.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepo : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
    fun findByToken(token: String): User?
    fun findByUserNameAndPassword(userName: String, password: String): User?
}