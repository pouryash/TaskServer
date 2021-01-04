package com.example.demo.controller

import com.example.demo.Dbmodel.Task
import com.example.demo.Dbmodel.User
import com.example.demo.model.CustomeDate
import com.example.demo.model.ResponseModel
import com.example.demo.model.Status
import com.example.demo.model.TaskDto
import com.example.demo.repository.TaskRepository
import com.example.demo.repository.UserRepo
import com.example.demo.service.TaskService
import com.example.demo.utils.DateUtils
import com.fasterxml.jackson.databind.node.TextNode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api")
class TaskController() {

    @Autowired
    lateinit var taskService: TaskService


    @GetMapping("/tasks")
    fun getAllTasks(@RequestHeader Authorization: String): ResponseEntity<ResponseModel> {
        return taskService.getAllTasks(Authorization)
    }

    @GetMapping("/tasksBetweenDate")
    fun getAllTasksBetweenDate(@RequestHeader Authorization: String, @RequestBody date: CustomeDate): ResponseEntity<ResponseModel> {
        return taskService.getAllTasksBetweenDate(Authorization, date.fromDate, date.toDate)
    }


    @PostMapping("/createTask")
    fun createNewTask(@RequestBody taskDto: TaskDto?): ResponseEntity<ResponseModel> {

        return taskService.createNewTask(taskDto)

    }


    @PutMapping("/updateTask")
    fun updateTask(
        @RequestHeader Authorization: String,
        @RequestBody taskDto: TaskDto
    ): ResponseEntity<ResponseModel> {

        return taskService.updateTAsk(Authorization, taskDto)
    }


    @DeleteMapping("/tasks")
    fun deleteTaskByName(
        @RequestHeader Authorization: String,
        @RequestBody task: TaskDto
    ): ResponseEntity<ResponseModel> {
        return taskService.deleteTaskByName(Authorization, task.taskName)
    }

}
