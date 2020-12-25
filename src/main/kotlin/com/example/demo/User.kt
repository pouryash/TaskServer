package com.example.demo

import org.jetbrains.annotations.NotNull
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class User(
        @Id
        val id: Long = 0,
        val userName: String = "",
        val password: String = "",
        val email: String = "",
        val role: String = "",
        val createDate: String = ""

)
