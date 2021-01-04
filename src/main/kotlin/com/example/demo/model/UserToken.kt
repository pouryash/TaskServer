package com.example.demo.model

import javax.persistence.Entity
import javax.persistence.Id


data class UserToken(
    var id: Long = 0,
    val userName: String = "",
    val role: String = "user",
    var token: String = ""
)
