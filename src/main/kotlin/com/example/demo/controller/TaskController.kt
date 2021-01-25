package com.example.demo.controller

import com.example.demo.model.FilterModel
import com.example.demo.model.ResponseModel
import com.example.demo.model.TaskDto
import com.example.demo.service.TaskService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api")
class TaskController() {

    @Autowired
    lateinit var taskService: TaskService

    @GetMapping("/tasks/{id}")
    fun getUserTasksById(@PathVariable(value = "id") userId: Long): ResponseEntity<ResponseModel>{

        return taskService.getUserTasksById(userId)
    }

    @GetMapping("/userTasks")
    fun getUserTasksByToken(@RequestHeader Authorization: String): ResponseEntity<ResponseModel>{

        return taskService.getUserTasksByToken(Authorization)
    }

    @PostMapping("/searchTask")
    fun searchTask(@RequestHeader Authorization: String, @RequestBody taskDto: TaskDto): ResponseEntity<ResponseModel>{

        return taskService.searchTask(Authorization, taskDto)
    }

    @GetMapping("/getAllTasks")
    fun getAllTasks(@RequestHeader Authorization: String): ResponseEntity<ResponseModel> {
        return taskService.getAllTasks(Authorization)
    }

    @GetMapping("/tasksBetweenDate")
    fun getAllTasksBetweenDate(@RequestHeader Authorization: String, @RequestBody date: FilterModel): ResponseEntity<ResponseModel> {
        return taskService.getAllTasksBetweenDate(Authorization, date.fromDate, date.toDate)
    }


    @PostMapping("/createTask")
    fun createNewTask(@RequestBody taskDto: TaskDto?): ResponseEntity<ResponseModel> {

        return taskService.createNewTask(taskDto)

    }

    @PostMapping("/filterTasks")
    fun filterTasks(@RequestBody filterModel: FilterModel): ResponseEntity<ResponseModel> {

        return taskService.filterTAsks(filterModel)

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
