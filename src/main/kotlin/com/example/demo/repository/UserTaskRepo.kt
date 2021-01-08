package com.example.demo.repository

import com.example.demo.Dbmodel.UserTask
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface UserTaskRepo : JpaRepository<UserTask, Long> {



}