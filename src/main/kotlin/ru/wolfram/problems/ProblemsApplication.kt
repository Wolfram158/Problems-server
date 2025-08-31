package ru.wolfram.problems

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class ProblemsApplication

fun main(args: Array<String>) {
    val app = SpringApplication(ProblemsApplication::class.java)
    app.run(*args)
}