package ru.wolfram.problems.repository

import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import ru.wolfram.problems.model.UserDbo

@Repository
interface UserRepository: JpaRepository<UserDbo, Long> {
    fun findByUsername(username: String): UserDbo?

    @Transactional
    @Modifying
    @Query("update UserDbo set solvedTasks = :new where username = :username")
    fun updateSolvedTasks(username: String, new: Set<String>)

    @Query("select solvedTasks from UserDbo where username = :username")
    fun getSolvedTasks(username: String): Set<String>
}