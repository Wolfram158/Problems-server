package ru.wolfram.problems.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.wolfram.problems.model.TaskDbo

@Repository
interface TaskRepository: JpaRepository<TaskDbo, String>