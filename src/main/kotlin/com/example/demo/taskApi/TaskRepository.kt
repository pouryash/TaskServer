package com.example.demo.taskApi

import com.example.demo.Dbmodel.Task
import com.example.demo.Dbmodel.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface TaskRepository : JpaRepository<Task, Long>{
    fun findByTaskName(taskNAme: String): Task?
}