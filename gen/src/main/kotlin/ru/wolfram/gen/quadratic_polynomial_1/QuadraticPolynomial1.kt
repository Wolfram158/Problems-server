package ru.wolfram.gen.quadratic_polynomial_1

import ru.wolfram.gen.Test
import java.nio.file.Paths
import kotlin.io.path.writeText
import kotlin.random.Random

class QuadraticPolynomial1 {
    private val plus = { x: Long, y: Long -> x + y }
    private val mul = { x: Long, y: Long -> x * y }
    private val mod = { x: Long -> x % 998244353 }
    private val toLong = { x: Long -> x }

    private fun generateTest(p: Long? = null, q: Long? = null, n: Long? = null): Test {
        val p = p ?: Random.nextLong()
        val q = q ?: Random.nextLong()
        val n = n ?: Random.nextLong()
        val answer = Main.solve(
            p,
            q,
            n,
            -1,
            0,
            1,
            2,
            plus,
            mul,
            mod,
            toLong,
        )
        return Test("$p $q $n", "$answer")
    }

    private fun specialTest1() = generateTest(3, 4, 0)

    private fun specialTest2() = generateTest(17, -9, 1)

    fun generateTests(count: Int) {
        val tests = listOf(specialTest1(), specialTest2()) + IntRange(1, count - 2).map { generateTest() }
        val inputs = tests.joinToString(separator = "\n$IO_SEPARATOR\n") { it.input }
        val outputs = tests.joinToString(separator = "\n$IO_SEPARATOR\n") { it.output }
        Paths.get("tasks/quadratic_polynomial_1/input.txt").writeText(inputs)
        Paths.get("tasks/quadratic_polynomial_1/output.txt").writeText(outputs)
    }

    companion object {
        val IO_SEPARATOR = "-".repeat(10)
    }
}

fun main() {
    QuadraticPolynomial1().generateTests(100)
}

