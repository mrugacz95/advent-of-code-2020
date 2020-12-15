package pl.mrugacz95.aoc.day15

import java.util.LinkedList

fun findSpokenNumber(turns: List<Int>, toFind: Int): Int {
    var lastFound: Int? = null
    for (i in turns.indices.reversed()) {
        val turn = turns[i]
        if (turn == toFind) {
            if (lastFound != null) {
                return lastFound - i
            } else {
                lastFound = i
            }
        }
    }
    return 0
}

fun part1(numbers: List<Int>): Int {
    val turns = numbers.toMutableList()
    var lastNum = numbers.last()
    for (i in 0 until 2020 - numbers.size) {
        val spelledNum = findSpokenNumber(turns, lastNum)
        turns.add(spelledNum)
        lastNum = spelledNum
    }
    return lastNum
}

fun <E> LinkedList<E>.addAndLimit(e: E, limit: Int) {
    addLast(e)
    while (size > limit) {
        removeFirst()
    }
}

fun part2(numbers: List<Int>): Int {
    val lastOccurrences = numbers
        .withIndex()
        .associateBy({ it.value }, { LinkedList(listOf(it.index)) })
        .toMutableMap()
    var lastNum = numbers.last()
    for (i in numbers.size until 30000000) {
        val lastOccur = lastOccurrences[lastNum]
        val spelledNum = if (lastOccur?.size == 2) lastOccur[1] - lastOccur[0] else 0
        lastOccurrences.putIfAbsent(spelledNum, LinkedList())
        lastOccurrences[spelledNum]?.addAndLimit(i, 2)
        lastNum = spelledNum
    }
    return lastNum
}

fun main() {
    val numbers = {}::class.java.getResource("/day15.in")
        .readText()
        .split(",")
        .map { it.toInt() }
    println("Answer part 1: ${part1(numbers)}")
    println("Answer part 2: ${part2(numbers)}")
}
