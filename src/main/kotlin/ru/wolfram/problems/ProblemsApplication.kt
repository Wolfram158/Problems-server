package ru.wolfram.problems

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class ProblemsApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val app = SpringApplication(ProblemsApplication::class.java)
            app.run(*args)
        }
    }
}