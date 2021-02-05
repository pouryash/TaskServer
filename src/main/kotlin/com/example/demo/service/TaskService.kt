package com.example.demo.service

import com.example.demo.Dbmodel.Task
import com.example.demo.Dbmodel.UserTask
import com.example.demo.model.*
import com.example.demo.repository.TaskRepository
import com.example.demo.repository.UserRepo
import com.example.demo.repository.UserTaskDetailRepo
import com.example.demo.repository.UserTaskRepo
import com.example.demo.utils.DateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.*
import javax.persistence.EntityManagerFactory
import javax.persistence.Query


@Service
class TaskService(
    private val taskRepository: TaskRepository, private var userRepo: UserRepo,
    private var userTaskRepo: UserTaskRepo
) : UserTaskDetailRepo {

    @Autowired
    lateinit var emf: EntityManagerFactory


    override fun filterTAsks(filterModel: FilterModel): ResponseEntity<ResponseModel> {

        var dateFilter = ""
        var statusFilter = ""
        var priorityFilter = ""
        var endDateFilter = ""
        var userFilter = ""
        val fields =
            "T.id, T.taskName, T.description, T.reporter, T.priority, T.status, T.loggedTime, T.createDate, T.endDate, U.userName, U.email"

        if (filterModel.status != "" && getValidStatus(filterModel.status) != "") {
            statusFilter += "And T.status = '${getValidStatus(filterModel.status)}'"
        }
        if (filterModel.priority != "" && getValidPriority(filterModel.priority) != "") {
            priorityFilter += "And T.priority = '${getValidPriority(filterModel.priority)}'"
        }
        if (filterModel.fromDate != "" && filterModel.toDate != "") {
            dateFilter += "And T.createDate between '${filterModel.fromDate}' and '${filterModel.toDate}'"
        }
        if (filterModel.isEnded) {
            endDateFilter += "And T.endDate IS NOT NULL"
        }
        if (filterModel.userId != -1L) {
            userFilter += "And U.id = '${filterModel.userId}'"
        }

        val em = emf.createEntityManager()

        val query: Query = em.createQuery(
            "SELECT $fields FROM User U INNER JOIN UserTask B ON U.id = B.userId INNER JOIN Task T ON B.taskId = T.id And T.isDeleted != 1" +
                    " $dateFilter $statusFilter $priorityFilter $endDateFilter $userFilter "
        )
        val list = query.resultList
        em.close()

        return return ResponseEntity(
            ResponseModel(
                HttpStatus.OK.value(),
                HttpStatus.OK.reasonPhrase,
                list
            ), HttpStatus.OK
        )

    }

    fun getUserTasksById(userId: Long): ResponseEntity<ResponseModel> {

        return ResponseEntity(
            ResponseModel(
                HttpStatus.OK.value(),
                HttpStatus.OK.reasonPhrase,
                taskRepository.findUserTasks(userId)
            ), HttpStatus.OK
        )
    }

    fun getUserTasksByToken(Authorization: String): ResponseEntity<ResponseModel> {
        userRepo.findByToken(Authorization)?.let {
            return ResponseEntity(
                ResponseModel(
                    HttpStatus.OK.value(),
                    HttpStatus.OK.reasonPhrase,
                    taskRepository.findUserTasks(it.id)
                ), HttpStatus.OK
            )
        }

        return ResponseEntity(
            ResponseModel(
                HttpStatus.NOT_FOUND.value(),
                "User not found"
            ), HttpStatus.NOT_FOUND
        )

    }


    fun getAllTasksBetweenDate(Authorization: String, fromDate: String, toDate: String): ResponseEntity<ResponseModel> {

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


    fun searchTask(Authorization: String, taskDto: TaskDto): ResponseEntity<ResponseModel> {

        userRepo.findByToken(Authorization)?.let {
            if (it.role == "admin")
                return ResponseEntity(
                    ResponseModel(
                        HttpStatus.OK.value(),
                        HttpStatus.OK.reasonPhrase,
                        convertTaskListToTaskDtoList(taskRepository.searchAllTask(taskDto.taskName))
                    ), HttpStatus.OK
                )

            return ResponseEntity(
                ResponseModel(
                    HttpStatus.OK.value(),
                    HttpStatus.OK.reasonPhrase,
                    convertTaskListToTaskDtoList(taskRepository.searchUserTasks(it.id, taskDto.taskName))
                ), HttpStatus.OK
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

            if (taskDto.taskName.isNotEmpty() && taskDto.priority.isNotEmpty()
                && taskDto.reporter.isNotEmpty() && taskDto.userId != -1L
            ) {

                taskRepository.findByTaskName(taskDto.taskName)?.let {
                    return ResponseEntity(
                        ResponseModel(
                            HttpStatus.CONFLICT.value(),
                            HttpStatus.CONFLICT.reasonPhrase
                        ), HttpStatus.CONFLICT
                    )

                }

                taskDto.loggedTime = ""
                taskDto.status = getValidStatus(taskDto.status)
                taskDto.priority = getValidPriority(taskDto.priority)
                taskDto.createDate = DateUtils.convertDateToString(Date())
                taskDto.endDate = ""
                taskDto.id = 0

                taskRepository.save(convertTaskDtoToTask(taskDto))

                var newTaskDto = convertTaskToTaskDto(taskRepository.findByTaskName(taskDto.taskName)!!)

                userTaskRepo.save(UserTask(userId = taskDto.userId, taskId = newTaskDto.id))
                return ResponseEntity(
                    ResponseModel(
                        HttpStatus.OK.value(),
                        HttpStatus.OK.reasonPhrase,
                        newTaskDto.copy(userId = taskDto.userId)
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
                        taskDto.endDate = DateUtils.convertDateToString(existingTask.endDate)

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
                            priority = getValidPriority(taskDto.priority),
                            endDate = DateUtils.convertStringToDate(taskDto.endDate),
                            loggedTime = taskDto.loggedTime, description = taskDto.description
                        )
                    taskRepository.save(updatedTaskEntity)
                    return ResponseEntity(
                        ResponseModel(
                            HttpStatus.OK.value(),
                            HttpStatus.OK.reasonPhrase,
                            convertTaskToTaskDto(updatedTaskEntity)
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
//                    taskRepository.delete(it)
                    taskRepository.save(it.copy(isDeleted = 1))
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
            description = task.description,
            priority = task.priority,
            createDate = DateUtils.convertDateToString(task.createDate),
            endDate = DateUtils.convertDateToString(task.endDate),
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
            description = taskDto.description,
            priority = taskDto.priority,
            createDate = DateUtils.convertStringToDate(taskDto.createDate)!!,
            endDate = DateUtils.convertStringToDate(taskDto.endDate),
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
        val logFormat = "0w-0d-0h-0m"
        val formatList = logFormat.split('-')

        if (!isLoggedTimeValid(current) || !isLoggedTimeValid(previous))
            return ""

        var currentList = current.split(" ")
        var previousList = previous.replace(" ", "").split('-')

        previousList = formatList.map {
            if (previous.contains(it.last()))
                previousList.first { data -> data.contains(it.last()) }.toString()
            else
                it
        }

        val resultList = previousList.map {
            if (current.contains(it.last())) {
                val suffix = currentList.first { data -> data.contains(it.last()) }.filter { it.isLetter() }
                val firstNum = currentList.first { data -> data.contains(it.last()) }.filter { it.isDigit() }.toInt()
                val secondNum = it.filter { it.isDigit() }.toInt()
                "${(firstNum + secondNum)}$suffix"
            } else
                it
        }

        var result = ""

        resultList.forEach {
            if (it.first().toString() != "0")
                result += "$it-"
        }

        return result.removeSuffix("-")

//        var newLoggedTime = ""
//        if (previousList[0].isNotEmpty() && currentList[0].isNotEmpty()) {
//            for (i in currentList.indices) {
//                for (j in previousList.indices) {
//                    if (currentList[i].last() == previousList[j].last()) {
//                        var suffix = currentList[i].filter { it.isLetter() }
//                        var firstNum = currentList[i].filter { it.isDigit() }.toInt()
//                        var secondNum = previousList[j].filter { it.isDigit() }.toInt()
//                        newLoggedTime += "${(firstNum + secondNum)}$suffix-"
//                        break
//                    } else {
//                        if (currentList.size > previousList.size) {
//                            if (j < previousList.size - 1)
//                                continue
//                            newLoggedTime += "${currentList[i]}-"
//                        } else {
//                            if (i == 0 && newLoggedTime.isEmpty()) {
//                                newLoggedTime += "${previousList[j]}-"
//                                continue
//                            }
//                            if (i == currentList.size - 1 && j > currentList.size - 1) {
//                                newLoggedTime += "${previousList[j]}-"
//                            }
//                        }
//                    }
//                }
//
//            }
//        } else {
//            newLoggedTime = if (currentList[0].isNotEmpty())
//                current.replace(" ", "")
//            else
//                previous.replace(" ", "")
//        }
//        return newLoggedTime.removeSuffix("-")
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