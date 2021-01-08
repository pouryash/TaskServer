package com.example.demo.Dbmodel

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.Entity
import javax.persistence.Id

@Embeddable
data class UserTaskId(  @Column(name = "userId", nullable = false)
                        val userId: Long = 0,
                        @Column(name = "taskId", nullable = false)
                        val taskId: Long = 0,) : Serializable
