package com.example.demo.Dbmodel

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.IdClass

@Entity
@IdClass(UserTaskId::class)
data class UserTask(
        @Id
        val userId: Long = 0,
        @Id
        val taskId: Long = 0,
)
