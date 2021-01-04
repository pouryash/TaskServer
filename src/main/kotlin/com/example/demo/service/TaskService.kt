package com.example.demo.service

import com.example.demo.Dbmodel.Task
import com.example.demo.model.Priority
import com.example.demo.model.ResponseModel
import com.example.demo.model.Status
import com.example.demo.model.TaskDto
import com.example.demo.repository.TaskRepository
import com.example.demo.repository.UserRepo
import com.example.demo.utils.DateUtils
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.*

@Service
class TaskService(private val taskRepository: TaskRepository, private var userRepo: UserRepo) {


    fun getAllTasksBetweenDate(Authorization: String, fromDate: String, toDate: String ): ResponseEntity<ResponseModel> {

        if (fromDate.isNotEmpty() && toDate.isNotEmpty()) {

            userRepo.findByToken(Authorization)?.let {
                if (it.role == "admin")
                    return ResponseEntity(
                        ResponseModel(
                            HttpStatus.OK.value(),
                            HttpStatus.OK.reasonPhrase,
                            convertTaskListToTaskDtoList(taskRepository.findTasksByCreateDateBetween(fromDate, toDate))
                        ), HttpStatus.OK
                    )

                return ResponseEntity(
                    ResponseModel(
                        HttpStatus.FORBIDDEN.value(),
                        "authorization not correct",
                    ), HttpStatus.FORBIDDEN
                )
            }

            return ResponseEntity(
                ResponseModel(
                    HttpStatus.NOT_FOUND.value(),
                    "User not found"
                ), HttpStatus.NOT_FOUND
            )
        }
        return ResponseEntity(
            ResponseModel(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.reasonPhrase,
            ), HttpStatus.BAD_REQUEST
        )
    }


    fun getAllTasks(Authorization: String): ResponseEntity<ResponseModel> {

        userRepo.findByToken(Authorization)?.let {
            if (it.role == "admin")
                return ResponseEntity(
                    ResponseModel(
                        HttpStatus.OK.value(),
                        HttpStatus.OK.reasonPhrase,
                        convertTaskListToTaskDtoList(taskRepository.findAll())
                    ), HttpStatus.OK
                )

            return ResponseEntity(
                ResponseModel(
                    HttpStatus.FORBIDDEN.value(),
                    "authorization not correct",
                ), HttpStatus.FORBIDDEN
            )
        }

        return ResponseEntity(
            ResponseModel(
                HttpStatus.NOT_FOUND.value(),
                "User not found"
            ), HttpStatus.NOT_FOUND
        )

    }

    fun createNewTask(taskDto: TaskDto?): ResponseEntity<ResponseModel> {

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
                val validLog = getValidLoggedTime(taskDto.loggedTime, "")
                if (validLog.isNotEmpty())
                    taskDto.loggedTime = validLog
                else
                    return ResponseEntity(
                        ResponseModel(
                            HttpStatus.UNPROCESSABLE_ENTITY.value(),
                            HttpStatus.UNPROCESSABLE_ENTITY.reasonPhrase
                        ), HttpStatus.UNPROCESSABLE_ENTITY
                    )
                taskDto.status = getValidStatus(taskDto.status)
                taskDto.priority = getValidPriority(taskDto.priority)
                taskDto.createDate = DateUtils.convertDateToString(Date())
                taskDto.endDate = ""
                taskDto.id = 0
                var task = convertTaskDtoToTask(taskDto)

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


    fun updateTAsk(
        Authorization: String,
        taskDto: TaskDto
    ): ResponseEntity<ResponseModel> {
        if (taskDto.taskName.isNotEmpty() && taskDto.reporter.isNotEmpty() && taskDto.priority.isNotEmpty()) {

            userRepo.findByToken(Authorization)?.let { existingUser ->

                taskRepository.findByTaskName(taskDto.taskName)?.let { existingTask ->
                    if (taskDto.status.equals(Status.Done.name, ignoreCase = true))
                        taskDto.endDate = DateUtils.convertDateToString(Date())
                    else
                        taskDto.endDate = ""

                    val validLog = getValidLoggedTime(taskDto.loggedTime, existingTask.loggedTime)
                    if (validLog.isNotEmpty())
                        taskDto.loggedTime = validLog
                    else
                        return ResponseEntity(
                            ResponseModel(
                                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                                HttpStatus.UNPROCESSABLE_ENTITY.reasonPhrase
                            ), HttpStatus.UNPROCESSABLE_ENTITY
                        )

                    val updatedTaskEntity: Task =
                        existingTask.copy(
                            taskName = taskDto.taskName, status = getValidStatus(taskDto.status),
                            priority = getValidPriority(taskDto.priority), endDate = taskDto.endDate,
                            loggedTime = taskDto.loggedTime
                        )
                    taskRepository.save(updatedTaskEntity)
                    return ResponseEntity(
                        ResponseModel(
                            HttpStatus.OK.value(),
                            HttpStatus.OK.reasonPhrase
                        ), HttpStatus.OK
                    )
                }

                return ResponseEntity(
                    ResponseModel(
                        HttpStatus.NOT_FOUND.value(),
                        "Task not found"
                    ), HttpStatus.NOT_FOUND
                )
            }
            return ResponseEntity(
                ResponseModel(
                    HttpStatus.NOT_FOUND.value(),
                    "User not found"
                ), HttpStatus.NOT_FOUND
            )
        }

        return ResponseEntity(
            ResponseModel(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.reasonPhrase,
            ), HttpStatus.BAD_REQUEST
        )
    }


    fun deleteTaskByName(Authorization: String, name: String): ResponseEntity<ResponseModel> {

        if (name.isNotEmpty()) {

            userRepo.findByToken(Authorization)?.let { existingUser ->
                if (existingUser.role != "admin")
                    return ResponseEntity(
                        ResponseModel(
                            HttpStatus.FORBIDDEN.value(),
                            "authorization not correct",
                        ), HttpStatus.FORBIDDEN
                    )
                taskRepository.findByTaskName(name)?.let {
                    taskRepository.delete(it)
                    return ResponseEntity(
                        ResponseModel(
                            HttpStatus.OK.value(),
                            HttpStatus.OK.reasonPhrase
                        ), HttpStatus.OK
                    )
                }
                return ResponseEntity(
                    ResponseModel(
                        HttpStatus.NOT_FOUND.value(),
                        "Task not found"
                    ), HttpStatus.NOT_FOUND
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


    fun convertTaskListToTaskDtoList(taskList: List<Task>): List<TaskDto> {
        return taskList.map {
            convertTaskToTaskDto(it)
        }
    }

    fun convertTaskToTaskDto(task: Task): TaskDto {
        return TaskDto(
            id = task.id,
            taskName = task.taskName,
            priority = task.priority,
            createDate = DateUtils.convertDateToString(task.createDate),
            endDate = task.endDate,
            reporter = task.reporter,
            status = task.status,
            loggedTime = task.loggedTime
        )
    }

    fun convertTaskDtoListToTaskList(taskDtoList: List<TaskDto>): List<Task> {
        return taskDtoList.map {
            convertTaskDtoToTask(it)
        }
    }

    fun convertTaskDtoToTask(taskDto: TaskDto): Task {
        return Task(
            id = taskDto.id,
            taskName = taskDto.taskName,
            priority = taskDto.priority,
            createDate = DateUtils.convertStringToDate(taskDto.createDate),
            endDate = taskDto.endDate,
            reporter = taskDto.reporter,
            status = taskDto.status,
            loggedTime = taskDto.loggedTime
        )
    }

    fun getValidPriority(value: String): String {
        when (value.toLowerCase()) {
            "low" -> {
                return Priority.Low.name
            }
            "medium" -> {
                return Priority.Medium.name
            }
            "high" -> {
                return Priority.High.name
            }
            else -> {
                return Priority.Low.name
            }
        }
    }

    fun getValidStatus(value: String): String {
        when (value.toLowerCase()) {
            "todo" -> {
                return Status.ToDo.name
            }
            "inprogress" -> {
                return Status.InProgress.name
            }
            "testing" -> {
                return Status.Testing.name
            }
            "done" -> {
                return Status.Done.name
            }
            else -> {
                return Status.ToDo.name
            }
        }
    }

    fun getValidLoggedTime(current: String, previous: String): String {
        if (!isLoggedTimeValid(current) || !isLoggedTimeValid(previous))
            return ""
        val currentList = current.replace(" ", "").split('-')
        val previousList = previous.replace(" ", "").split('-')
        var newLoggedTime = ""
        if (previousList[0].isNotEmpty() && currentList[0].isNotEmpty()) {
            for (i in currentList.indices) {
                for (j in previousList.indices) {
                    if (currentList[i].last() == previousList[j].last()) {
                        var suffix = currentList[i].filter { it.isLetter() }
                        var firstNum = currentList[i].filter { it.isDigit() }.toInt()
                        var secondNum = previousList[j].filter { it.isDigit() }.toInt()
                        newLoggedTime += "${(firstNum + secondNum)}$suffix-"
                        break
                    } else {
                        if (currentList.size > previousList.size) {
                            if (j < previousList.size - 1)
                                continue
                            newLoggedTime += "${currentList[i]}-"
                        } else {
                            if (i == 0)
                                newLoggedTime += "${previousList[j]}-"
                            continue
                        }
                    }
                }

            }
        } else {
            newLoggedTime = if (currentList[0].isNotEmpty())
                current.replace(" ", "")
            else
                previous.replace(" ", "")
        }
        return newLoggedTime.removeSuffix("-")
    }

    fun isLoggedTimeValid(value: String): Boolean {
        if (value.isEmpty())
            return true
        val list = value.replace(" ", "").split('-')
        for (i in list.indices) {
            try {
                var suffix = list[i].filter { it.isLetter() }
                var num = list[i].filter { it.isDigit() }
                if (suffix.isEmpty() || num.isEmpty())
                    return false
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        }
        return true
    }
}