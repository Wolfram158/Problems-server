package ru.wolfram.problems.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.wolfram.problems.model.User
import ru.wolfram.problems.service.UserService

@RestController
@RequestMapping("/api/v1")
class QuoteController @Autowired constructor(val userService: UserService) {

    val quotes = mutableListOf<String>()

    @PostMapping("/register")
    fun login(@RequestBody user: User): ResponseEntity<String> {
        val username = user.username
        val password = user.password
        if (username != null && password != null) {
            if (userService.create(username, password)) {
                return ResponseEntity.ok("Success")
            }
        }
        return ResponseEntity.ok("User already exists")
    }

    @GetMapping("/quotes")
    fun loadQuotes(
        @RequestParam("q", required = false) query: String?
    ): List<String> {
        return if (query != null) {
            quotes.filter {
                it.contains(query, ignoreCase = true)
            }
        } else quotes
    }

    @PostMapping("/quotes")
    fun postQuote(
        @RequestBody quoteDto: String
    ): String {
        quotes.add(quoteDto)
        return quoteDto
    }

    @PutMapping("/quotes")
    fun putQuote(
        @RequestBody quoteDto: String
    ): String {
        val index = quotes.indexOfFirst { it == quoteDto }
        quotes[index] = quoteDto
        return quoteDto
    }

    @DeleteMapping("/quotes/{id}")
    fun deleteQuote(
        @PathVariable("id") id: String
    ) {
        val quoteToDelete = quotes.find { it == id }
        if (quoteToDelete != null) {
            quotes.remove(quoteToDelete)
        } else {
            throw RuntimeException("Quote not found")
        }
    }
}