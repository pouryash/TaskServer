package com.example.demo.model


data class ResponseModel(
        var status: Int = 0,
        var message: String = "",
        var data: Any = Unit
)
