package ru.wolfram.problems.service

import org.springframework.stereotype.Service
import ru.wolfram.problems.model.TaskDbo
import ru.wolfram.problems.repository.TaskRepository
import java.util.*

@Service
class TaskService(
    private val taskRepository: TaskRepository
) {
    fun getTasks(): List<TaskDbo> = taskRepository.findAll()

    fun getTaskByName(taskName: String): Optional<TaskDbo> = taskRepository.findById(taskName)

    fun addTask(task: TaskDbo): TaskDbo = taskRepository.save(task)
}