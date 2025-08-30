package ru.wolfram.problems.service

import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import ru.wolfram.problems.model.User.Companion.toUserDbo
import ru.wolfram.problems.model.UserDbo
import ru.wolfram.problems.repository.UserRepository
import ru.wolfram.problems.model.User as UserEntity

@Service
class UserService(
    private val userRepository: UserRepository,
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        return userRepository.findByUsername(username)?.let {
            User.builder()
                .username(it.username)
                .password(it.password)
                .authorities(it.authorities)
                .build()
        }
            ?: throw UsernameNotFoundException("User not found")
    }

    fun updateSolvedTasks(username: String, new: Set<String>) {
        return userRepository.updateSolvedTasks(username, new)
    }

    fun getSolvedTasks(username: String): Set<String> {
        return userRepository.getSolvedTasks(username)
    }

    fun create(username: String, password: String): Boolean {
        val user = User.builder()
            .username(username)
            .password(BCryptPasswordEncoder().encode(password))
            .authorities("USER")
            .build()

        if (userRepository.findByUsername(username) != null) {
            return false
        }

        userRepository.save<UserDbo>(
            UserEntity(
                username = user.username,
                password = user.password,
                authorities = user.authorities.toList(),
                solvedTasks = setOf()
            ).toUserDbo()
        )

        return true
    }
}