package com.example.demo.Dbmodel

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class User(
        @Id
        val id: Long = 0,
        val userName: String = "",
        val password: String = "",
        @Column(name = "email", nullable = false, unique = true)
        val email: String = "",
        val role: String = "user",
        val createDate: String = "",
        val token: String = ""

)
