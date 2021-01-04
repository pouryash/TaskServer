package com.example.demo.model


data class TaskDto(
        var id: Long = 0,
        val taskName: String = "",
        val reporter: String = "",
        var priority: String = Priority.Low.name,
        var status: String = Status.ToDo.name,
        var loggedTime: String = "",
        var createDate: String = "",
        var endDate: String = ""

)
