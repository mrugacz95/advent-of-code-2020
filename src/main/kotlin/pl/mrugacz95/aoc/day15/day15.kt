package pl.mrugacz95.aoc.day15

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

fun part2(numbers: List<Int>): Int {
    val lastOccurrences = numbers
        .withIndex()
        .associateBy({ it.value }, { it.index })
        .toMutableMap()
    var nextNum = 0
    for (turn in numbers.size until 30000000 - 1) {
        val lastOccur = lastOccurrences[nextNum]
        val spelledNum = if (lastOccur != null) turn - lastOccur else 0
        lastOccurrences[nextNum] = turn
        nextNum = spelledNum
    }
    return nextNum
}

fun main() {
    val numbers = {}::class.java.getResource("/day15.in")
        .readText()
        .split(",")
        .map { it.toInt() }
    println("Answer part 1: ${part1(numbers)}")
    println("Answer part 2: ${part2(numbers)}")
}
