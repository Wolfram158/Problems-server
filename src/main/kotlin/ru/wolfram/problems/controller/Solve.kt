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
    extCompile: String,
    extRun: String,
    userService: UserService,
    suffix: String = ""
): ResponseEntity<String> {
    val compile =
        "$compiler $fileTaskName.$extCompile".runCommand(20, File("solutions/$username"))
    when (compile) {
        is Result.Failure -> {
            return ResponseEntity.ok(compile.error)
        }

        else -> {}
    }
    val results = mutableListOf<Deferred<MutableList<ResponseEntity<String>?>>>()
    val step = 10
    task.input!!.windowed(step, step, true).forEachIndexed { numOfGroup, group ->
        results.add(async {
            val subresults = mutableListOf<ResponseEntity<String>?>()
            group.forEachIndexed { index, test ->
                val lines = buildString {
                    append("echo ")
                    append(test.trim().split("\n").joinToString(separator = "&echo.&"))
                }
                val result =
                    if (extRun.isNotBlank()) {
                        "cmd.exe /C $lines | $runner $fileTaskName$suffix.$extRun".runCommand(20, File("solutions/$username"))
                    } else {
                        "cmd.exe /C $lines | $runner".runCommand(20, File("solutions/$username"))
                    }
                subresults.add(
                    when (result) {
                        is Result.Success -> {
                            if (result.result.trim() != task.output!![numOfGroup * step + index].trim()) {
                                ResponseEntity.ok("Failed on test ${numOfGroup * step + index + 1}!")
                            } else {
                                null
                            }
                        }

                        is Result.Failure -> {
                            ResponseEntity.ok("Failed on test ${numOfGroup * step + index + 1}! Error occurred:${System.lineSeparator()}${result.error}")
                        }

                        else -> {
                            null
                        }
                    }
                )
            }
            subresults
        })
    }
    val ret = runBlocking {
        for (result in results) {
            for (subresult in result.await()) {
                if (subresult != null) {
                    return@runBlocking subresult
                }
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