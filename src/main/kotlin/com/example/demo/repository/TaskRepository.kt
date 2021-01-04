package com.example.demo.repository

import com.example.demo.Dbmodel.Task
import com.example.demo.Dbmodel.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TaskRepository : JpaRepository<Task, Long>{
    fun findByTaskName(taskNAme: String): Task?

    @Query("select * from Task t where create_date between ?1 and ?2 order by create_date ASC", nativeQuery = true)
    fun findTasksByCreateDateBetween(fromDate: String, toDate: String): List<Task>
}