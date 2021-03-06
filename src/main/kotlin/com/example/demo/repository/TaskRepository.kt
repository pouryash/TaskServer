package com.example.demo.repository

import com.example.demo.Dbmodel.Task
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface TaskRepository : JpaRepository<Task, Long>{
    fun findByTaskName(taskNAme: String): Task?

    @Query("select * from Task t where create_date between ?1 and ?2 order by create_date ASC", nativeQuery = true)
    fun findTasksByCreateDateBetween(fromDate: String, toDate: String): List<Task>

    @Query(
        "SELECT T.id, T.task_name, T.description, T.reporter, T.priority, T.status, T.logged_time, T.create_date, T.end_date, T.is_deleted" +
                " FROM user A INNER JOIN user_task B ON B.user_id = :userId And A.id = B.user_id INNER JOIN task T ON B.task_id = T.id", nativeQuery = true
    )
    fun findUserTasks(userId: Long): ArrayList<Task>
}