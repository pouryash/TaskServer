package com.example.demo.taskApi

import com.example.demo.Dbmodel.Task
import com.example.demo.model.Priority
import com.example.demo.model.Status
import com.example.demo.model.TaskDto
import org.springframework.stereotype.Service

@Service
class TaskService {


    fun convertTaskListToTaskDtoList(taskList: List<Task>): List<TaskDto> {
        return taskList.map {
            convertTaskToTaskDto(it)
        }
    }

    fun convertTaskToTaskDto(task: Task): TaskDto {
        return TaskDto(
            id = task.id, taskName = task.taskName, priority = task.priority,
            createDate = task.createDate, endDate = task.endDate, reporter = task.reporter,
            status = task.status, loggedTime = task.loggedTime
        )
    }

    fun convertTaskDtoListToTaskList(taskDtoList: List<TaskDto>): List<Task> {
        return taskDtoList.map {
            convertTaskDtoToTask(it)
        }
    }

    fun convertTaskDtoToTask(taskDto: TaskDto): Task {
        return Task(
            id = taskDto.id, taskName = taskDto.taskName, priority = taskDto.priority,
            createDate = taskDto.createDate, endDate = taskDto.endDate, reporter = taskDto.reporter,
            status = taskDto.status, loggedTime = taskDto.loggedTime
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
                var num = list[i].filter { it.isDigit() }.toInt()
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        }
        return true
    }
}