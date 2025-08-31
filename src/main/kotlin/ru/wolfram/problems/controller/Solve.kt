package ru.wolfram.problems.controller

import org.springframework.http.ResponseEntity
import ru.wolfram.problems.extensions.Result
import ru.wolfram.problems.extensions.runCommand
import ru.wolfram.problems.model.TaskDbo
import ru.wolfram.problems.service.UserService
import java.io.File

fun solve(
    fileTaskName: String,
    username: String,
    task: TaskDbo,
    taskName: String,
    compiler: String,
    runner: String,
    ext: String,
    userService: UserService
): ResponseEntity<String> {
    val compile =
        "$compiler $fileTaskName.$ext".runCommand(20, File("solutions/$username"))
    when (compile) {
        is Result.Failure -> {
            return ResponseEntity.ok(compile.error)
        }

        else -> {}
    }
    task.input!!.forEachIndexed { index, test ->
        val lines = buildString {
            append("echo ")
            append(test.trim().split("\n").joinToString(separator = "&echo.&"))
        }
        val result =
            "cmd.exe /C $lines | $runner $fileTaskName.$ext".runCommand(20, File("solutions/$username"))
        when (result) {
            is Result.Success -> {
                if (result.result.trim() != task.output!![index].trim()) {
                    return ResponseEntity.ok("Failed on test ${index + 1}!")
                }
            }

            is Result.Failure -> {
                return ResponseEntity.ok("Failed on test ${index + 1}! Error occurred:${System.lineSeparator()}${result.error}")
            }

            else -> {}
        }
    }
    val solvedTasks = userService.getSolvedTasks(username)?.toMutableSet() ?: mutableSetOf()
    solvedTasks.remove<String?>(null)
    solvedTasks.add(taskName)
    userService.updateSolvedTasks(username, solvedTasks)
    return ResponseEntity.ok("Successfully passed all ${task.output!!.size} tests!")
}