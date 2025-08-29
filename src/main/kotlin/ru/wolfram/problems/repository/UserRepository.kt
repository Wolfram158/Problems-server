package ru.wolfram.problems.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.wolfram.problems.model.UserDbo

@Repository
interface UserRepository: JpaRepository<UserDbo, Long> {
    fun findByUsername(username: String): UserDbo?
}