package com.example.demo.Dbmodel

import com.example.demo.model.Priority
import com.example.demo.model.Status
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Task(
        @Id
        val id: Long = 0,
        val taskName: String = "",
        val reporter: String = "",
        val priority: String = Priority.Low.name,
        val status: String = Status.ToDo.name,
        val loggedTime: String = "",
        val createDate: String = "",
        val endDate: String = ""

)
