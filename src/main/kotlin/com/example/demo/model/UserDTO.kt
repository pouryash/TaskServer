package com.example.demo.model

import javax.persistence.Entity
import javax.persistence.Id


data class UserDTO(
    var id: Long = 0,
    val userName: String = "",
    val password: String = "",
    val email: String = "",
    val role: String = "user",
    var createDate: String = "",
    var token: String = ""
)
