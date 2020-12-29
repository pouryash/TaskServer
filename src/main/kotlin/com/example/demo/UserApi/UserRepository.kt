package com.example.demo.UserApi

import com.example.demo.Dbmodel.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, Long>{
    fun findByEmail(email: String): User?
    fun findByToken(token: String): User?
    fun findByUserNameAndPassword(userName: String, password: String): User?
}