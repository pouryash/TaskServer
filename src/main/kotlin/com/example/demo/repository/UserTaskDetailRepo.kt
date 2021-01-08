package com.example.demo.repository

import com.example.demo.Dbmodel.Task
import com.example.demo.model.FilterModel
import com.example.demo.model.ResponseModel
import com.example.demo.model.UserTaskDto
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Repository


interface UserTaskDetailRepo {

    fun filterTAsks(filterModel: FilterModel): ResponseEntity<ResponseModel>

}