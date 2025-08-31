package ru.wolfram.problems.controller

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.springframework.http.ResponseEntity
import ru.wolfram.problems.extensions.Result
import ru.wolfram.problems.extensions.runCommand
import ru.wolfram.problems.model.TaskDbo
import ru.wolfram.problems.service.UserService
import java.io.File

fun CoroutineScope.solve(
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
    val results = mutableListOf<Deferred<ResponseEntity<String>?>>()
    task.input!!.forEachIndexed { index, test ->
        val lines = buildString {
            append("echo ")
            append(test.trim().split("\n").joinToString(separator = "&echo.&"))
        }
        results.add(async {
            val result =
                "cmd.exe /C $lines | $runner $fileTaskName.$ext".runCommand(20, File("solutions/$username"))
            return@async when (result) {
                is Result.Success -> {
                    if (result.result.trim() != task.output!![index].trim()) {
                        ResponseEntity.ok("Failed on test ${index + 1}!")
                    } else {
                        null
                    }
                }

                is Result.Failure -> {
                    ResponseEntity.ok("Failed on test ${index + 1}! Error occurred:${System.lineSeparator()}${result.error}")
                }

                else -> {
                    null
                }
            }
        })
    }
    val ret = runBlocking {
        for (result in results) {
            val res = result.await()
            if (res != null) {
                return@runBlocking res
            }
        }
        return@runBlocking null
    }
    if (ret != null) {
        return ret
    }
    val solvedTasks = userService.getSolvedTasks(username)?.toMutableSet() ?: mutableSetOf()
    solvedTasks.remove<String?>(null)
    solvedTasks.add(taskName)
    userService.updateSolvedTasks(username, solvedTasks)
    return ResponseEntity.ok("Successfully passed all ${task.output!!.size} tests!")
}