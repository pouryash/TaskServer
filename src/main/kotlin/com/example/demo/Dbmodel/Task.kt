package com.example.demo.Dbmodel

import com.example.demo.model.Priority
import com.example.demo.model.Status
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Task(
        @Id
        val id: Long = 0,
        val taskName: String = "",
        val description: String = "",
        val reporter: String = "",
        val priority: String = Priority.Low.name,
        val status: String = Status.ToDo.name,
        val loggedTime: String = "",
        val createDate: Date,
        val endDate: Date? = null,
        val isDeleted: Int = 0

)
