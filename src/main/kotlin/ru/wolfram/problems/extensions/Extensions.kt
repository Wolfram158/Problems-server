package ru.wolfram.problems.extensions

import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

fun String.runCommand(waitTimeSeconds: Long, workingDir: File): Result? {
    try {
        val parts = split("\\s".toRegex())
        val proc = ProcessBuilder(*parts.toTypedArray())
            .directory(workingDir)
            .redirectInput(ProcessBuilder.Redirect.PIPE)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()
        proc.waitFor(waitTimeSeconds, TimeUnit.SECONDS)
        val error = proc.errorStream.bufferedReader().readText()
        if (error.isNotEmpty()) {
            return Result.Failure(error)
        }
        return Result.Success(proc.inputStream.bufferedReader().readText())
    } catch (_: IOException) {
        return null
    }
}

sealed interface Result {
    class Success(val result: String) : Result
    class Failure(val error: String) : Result
}