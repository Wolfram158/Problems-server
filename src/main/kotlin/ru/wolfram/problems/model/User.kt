package ru.wolfram.problems.model

import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import ru.wolfram.problems.exception.NullAuthoritiesException

@Entity
@Table(name = "users")
class UserDbo(
    @Column(name = "username", nullable = false)
    var username: String? = null,

    @Column(name = "password", nullable = false)
    var password: String? = null,

    @Column(name = "authorities", nullable = false)
    var authorities: String? = null,

    @Column(name = "id", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
) {
    companion object {
        fun UserDbo.toUser() = User(
            username = username,
            password = password,
            authorities = authorities?.split(User.AUTHORITIES_SEPARATOR)
                ?.map { role -> SimpleGrantedAuthority(role) }
        )
    }
}

data class User(
    private val username: String? = null,
    private val password: String? = null,
    private val authorities: List<GrantedAuthority>? = null,
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority?> {
        return authorities ?: throw NullAuthoritiesException()
    }

    override fun getPassword(): String? {
        return password
    }

    override fun getUsername(): String? {
        return username
    }

    companion object {
        const val AUTHORITIES_SEPARATOR = ","

        fun User.toUserDbo() = UserDbo(
            username, password,
            authorities?.toList()?.joinToString(separator = AUTHORITIES_SEPARATOR) { role ->
                role.authority ?: throw NullAuthoritiesException()
            })
    }
}