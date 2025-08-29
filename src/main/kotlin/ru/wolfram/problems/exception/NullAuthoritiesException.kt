package ru.wolfram.problems.exception

class NullAuthoritiesException: RuntimeException() {
    override val message: String = "Unexpected behaviour: authorities is null"
}