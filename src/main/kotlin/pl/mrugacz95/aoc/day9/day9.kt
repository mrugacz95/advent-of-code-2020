package pl.mrugacz95.aoc.day9

import java.lang.ArithmeticException
import java.lang.RuntimeException

const val preambleLength = 25

val numbers = {}::class.java.getResource("/day9.in")
    .readText()
    .split("\n")
    .map { it.toLong() }

fun findInvalidNumber(): Long {
    for (nextNumId in preambleLength until numbers.size) {
        val nextNumber = numbers[nextNumId]
        var membersFound = false
        outer@
        for (i in nextNumId - preambleLength..nextNumId) {
            val firstNumber = numbers[i]
            for (j in i..nextNumId) {
                val secondNumber = numbers[j]
                if (firstNumber + secondNumber == nextNumber) {
                    membersFound = true
                    break@outer
                }
            }
        }
        if (!membersFound) {
            return nextNumber
        }
    }
    throw RuntimeException("No invalid number found")
}

fun findWeakness(invalidNumber: Long): Long {
    var left = 0
    var right = 0
    var sum = 0L
    while (left != numbers.size) {
        if (sum < invalidNumber && right < numbers.size) {
            sum += numbers[right]
            right += 1
        } else if (sum > invalidNumber) {
            sum -= numbers[left]
            left += 1
        } else {
            val range = numbers.subList(left, right)
            val min = range.minOrNull() ?: throw ArithmeticException()
            val max = range.maxOrNull() ?: throw ArithmeticException()
            return min + max
        }
    }
    throw RuntimeException("No subarray found")
}

fun main() {
    val invalidNumber = findInvalidNumber()
    println("Answer part 1: $invalidNumber")
    println("Answer part 2: ${findWeakness(invalidNumber)}")
}