package ru.wolfram.problems.config

import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Configuration
import ru.wolfram.problems.exception.DescriptionIsEmptyException
import ru.wolfram.problems.model.TaskDbo
import ru.wolfram.problems.service.TaskService
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.io.path.readText

@Configuration
class OnStartListener(
    private var taskService: TaskService
) : ApplicationListener<ApplicationReadyEvent> {

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        Files.walk(Paths.get("tasks"), 1).use {
            it.filter { it1 -> it1.isDirectory() }.forEach { path ->
                var input = listOf<String>()
                var output = listOf<String>()
                var description: String? = null
                var name = ""
                Files.list(path).use { it2 ->
                    it2.filter { it3 -> it3.isDirectory() }.forEach { path1 ->
                        Files.list(path1).use { it3 ->
                            it3.forEach { path2 ->
                                val fileName = path2.fileName.toString()
                                if (fileName == "input.txt") {
                                    input = path2.getIO()
                                } else if (fileName == "output.txt") {
                                    output = path2.getIO()
                                } else if (fileName.endsWith(".md")) {
                                    name = path2.fileName.toString().substringBefore(".").replace("_", " ")
                                    description = path2.readText()
                                }
                            }
                        }
                        if (description == null) {
                            throw DescriptionIsEmptyException()
                        }
                        taskService.addTask(
                            TaskDbo(
                                taskName = name,
                                descriptionMarkdown = description,
                                input = input,
                                output = output,
                            )
                        )
                    }
                }
            }
        }
    }

    private fun Path.getIO() = readText().split("-".repeat(10))

}