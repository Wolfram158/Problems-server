package ru.wolfram.problems.exception

class DescriptionIsEmptyException : RuntimeException() {
    override val message = "Description is empty!"
}