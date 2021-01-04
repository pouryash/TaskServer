package com.example.demo.taskApi

import com.example.demo.model.ResponseModel
import com.example.demo.model.TaskDto
import com.example.demo.utils.DateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api")
class TaskController(private val taskRepository: TaskRepository) {

    @Autowired
    lateinit var taskService: TaskService




    @PostMapping("/createTask")
    fun createNewUser(@RequestBody taskDto: TaskDto?): ResponseEntity<ResponseModel> {
        taskDto?.let {
            if (taskDto.taskName.isNotEmpty() && taskDto.priority.isNotEmpty() && taskDto.reporter.isNotEmpty()) {

                taskRepository.findByTaskName(taskDto.taskName)?.let {
                    return ResponseEntity(
                        ResponseModel(
                            HttpStatus.CONFLICT.value(),
                            HttpStatus.CONFLICT.reasonPhrase
                        ), HttpStatus.CONFLICT
                    )
                }
                val validLog = taskService.getValidLoggedTime(taskDto.loggedTime, "")
                if (validLog.isNotEmpty())
                    taskDto.loggedTime = validLog
                else
                    return ResponseEntity(
                        ResponseModel(
                            HttpStatus.UNPROCESSABLE_ENTITY.value(),
                            HttpStatus.UNPROCESSABLE_ENTITY.reasonPhrase
                        ), HttpStatus.UNPROCESSABLE_ENTITY
                    )
                taskDto.status = taskService.getValidStatus(taskDto.status)
                taskDto.priority = taskService.getValidPriority(taskDto.priority)
                taskDto.createDate = DateUtils.getCurrentDate()
                taskDto.endDate = ""
                taskDto.id = 0
                var task = taskService.convertTaskDtoToTask(taskDto)

                taskRepository.save(task).let {
                    taskDto.id = task.id
                }
                return ResponseEntity(
                    ResponseModel(
                        HttpStatus.OK.value(),
                        HttpStatus.OK.reasonPhrase,
                        taskDto
                    ), HttpStatus.OK
                )
            }
        }

        return ResponseEntity(
            ResponseModel(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.reasonPhrase,
            ), HttpStatus.BAD_REQUEST
        )

    }


//    @PutMapping("/updateTask")
//    fun updateTAsk(
//        @RequestHeader Authorization: String,
//        @RequestBody taskDto: TaskDto
//    ): ResponseEntity<ResponseModel> {
//        if (taskDto.taskName.isNotEmpty() && taskDto.reporter.isNotEmpty() && taskDto.priority.isNotEmpty()) {
//
//            taskRepository.findByToken(Authorization)?.let { existingTask ->
//                val updatedUserEntity: User =
//                    existingTask.copy(
//                        taskName = taskDto.taskName, reporter = taskDto.reporter,
//                        password = userDTO.password, createDate = DateUtils.getCurrentDate()
//                    )
//                userRepository.save(updatedUserEntity)
//                return ResponseEntity(
//                    ResponseModel(
//                        HttpStatus.OK.value(),
//                        HttpStatus.OK.reasonPhrase
//                    ), HttpStatus.OK
//                )
//            }
//            return ResponseEntity(
//                ResponseModel(
//                    HttpStatus.NOT_FOUND.value(),
//                    "User not found"
//                ), HttpStatus.NOT_FOUND
//            )
//
//        }
//        return ResponseEntity(
//            ResponseModel(
//                HttpStatus.BAD_REQUEST.value(),
//                HttpStatus.BAD_REQUEST.reasonPhrase,
//            ), HttpStatus.BAD_REQUEST
//        )
//    }

}
