package ru.wolfram.problems.exception

class NullTaskException: RuntimeException() {
    override val message = "Unexpected behaviour: task is null"
}