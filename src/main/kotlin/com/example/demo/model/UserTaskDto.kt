package com.example.demo.model


data class UserTaskDto(
        var id: Long = 0,
        val taskName: String = "",
        val description: String = "",
        val reporter: String = "",
        var priority: String = Priority.Low.name,
        var status: String = Status.ToDo.name,
        var loggedTime: String = "",
        var createDate: String = "",
        var endDate: String = "",
        val userName: String = "",
        val email: String = "",

)
