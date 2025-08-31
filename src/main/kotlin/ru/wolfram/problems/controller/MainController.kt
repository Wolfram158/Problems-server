package ru.wolfram.problems.controller

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import ru.wolfram.problems.exception.NullTaskException
import ru.wolfram.problems.model.Task
import ru.wolfram.problems.model.TaskDbo
import ru.wolfram.problems.model.User
import ru.wolfram.problems.service.TaskService
import ru.wolfram.problems.service.UserService
import java.nio.file.Paths
import kotlin.io.path.createParentDirectories
import kotlin.io.path.writeText
import kotlin.jvm.optionals.getOrElse

@RestController
@RequestMapping("/api/v1")
class MainController(
    private val userService: UserService,
    private val taskService: TaskService
) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    @PostMapping("/register")
    fun register(@RequestBody user: User): ResponseEntity<String> {
        val username = user.username
        val password = user.password
        if (username != null && password != null) {
            return if (userService.create(username, password)) {
                ResponseEntity.ok("Success")
            } else {
                ResponseEntity.ok("User already exists")
            }
        }
        return ResponseEntity.badRequest().body("Failure! Have you specified username and password?")
    }

    @GetMapping("/tasks")
    fun getTasks(): ResponseEntity<List<Task>> {
        return ResponseEntity.ok(
            taskService.getTasks().map { taskDbo ->
                TaskDbo.toTask(taskDbo) ?: throw NullTaskException()
            }
        )
    }

    @PostMapping("/solve")
    fun solveTask(
        @RequestParam("name") taskName: String,
        @RequestBody solution: Solution
    ): ResponseEntity<String> {
        val username =
            (SecurityContextHolder.getContext().authentication?.principal as? UserDetails)?.username
                ?: return ResponseEntity.ok("Who are you?")
        val task = taskService.getTaskByName(taskName).getOrElse { throw NullTaskException() }
        val fileTaskName = taskName.replace(" ", "_")
        val pathToSolution = Paths.get("solutions/$username/$fileTaskName.java")
        pathToSolution.createParentDirectories()
        pathToSolution.writeText(solution.solution)
        when (solution.language.lowercase()) {
            "java" -> {
                return scope.solve(
                    fileTaskName = fileTaskName,
                    username = username,
                    task = task,
                    taskName = taskName,
                    compiler = "javac",
                    runner = "java",
                    ext = "java",
                    userService = userService
                )
            }

            "scala" -> {
                return scope.solve(
                    fileTaskName = fileTaskName,
                    username = username,
                    task = task,
                    taskName = taskName,
                    compiler = "scalac",
                    runner = "scala",
                    ext = "scala",
                    userService = userService
                )
            }

            else -> {
                return ResponseEntity.ok("Language ${solution.language} is not supported!")
            }
        }
    }
}