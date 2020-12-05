package pl.mrugacz95.aoc.day3

import java.lang.RuntimeException

enum class Field(val c: Char) {
    TREE('#'),
    FREE('.');

    companion object {
        fun getEnum(c: Char) = when (c) {
            '#'  -> TREE
            '.'  -> FREE
            else -> throw RuntimeException("Unknown char: $c")
        }
    }
}

val map = {}::class.java.getResource("/day3.in")
    .readText()
    .split("\n")
    .map { it.toList() }
    .map { list ->
        list.map { c -> Field.getEnum(c) }
    }

val width = map[0].size
val height = map.size

fun getFiled(y: Int, x: Int): Field {
    return map[y][x % width]
}

fun countTreesForSlope(deltaY: Int, deltaX: Int): Int {
    var y = 0
    var x = 0
    var counter = 0
    while (y < height) {
        if (getFiled(y, x) == Field.TREE) {
            counter += 1
        }
        y += deltaY
        x += deltaX
    }
    return counter
}

fun part2(): Long {
    val slopes = arrayListOf(Pair(1, 1), Pair(1, 3), Pair(1, 5), Pair(1, 7), Pair(2, 1))
    var trees = 1L
    for (slope in slopes) {
        trees *= countTreesForSlope(slope.first, slope.second)
    }
    return trees
}

fun main() {
    println("Answer part 1: ${countTreesForSlope(1, 3)}")
    println("Answer part 2: ${part2()}")
}