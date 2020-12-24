package pl.mrugacz95.aoc.day24

import pl.mrugacz95.aoc.day19.first

operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>): Pair<Int, Int> {
    return Pair(first + other.first, second + other.second)
}

enum class Direction(val delta: Pair<Int, Int>) {
    E(Pair(+1, 0)),
    SE(Pair(0, 1)),
    SW(Pair(-1, 1)),
    W(Pair(-1, 0)),
    NW(Pair(0, -1)),
    NE(Pair(1, -1));

    companion object {
        fun fromString(direction: String): Direction {
            return when (direction) {
                "e"  -> E
                "se" -> SE
                "sw" -> SW
                "w"  -> W
                "nw" -> NW
                "ne" -> NE
                else -> throw RuntimeException("Unknown direction : $direction")
            }
        }
    }
}

fun getInitialState(paths: List<List<Direction>>): Set<Pair<Int, Int>> {
    val floor = mutableSetOf<Pair<Int, Int>>() // only black tiles
    for (path in paths) {
        var tile = Pair(0, 0)
        for (dir in path) {
            tile += dir.delta
        }
        if (tile in floor) {
            floor.remove(tile)
        } else {
            floor.add(tile)
        }
    }
    return floor
}

fun part1(paths: List<List<Direction>>): Int {
    return getInitialState(paths).size
}

fun countNeighbours(pos: Pair<Int, Int>, floor: Set<Pair<Int, Int>>): Int {
    var counter = 0
    for (n in Direction.values()) {
        if (pos + n.delta in floor) {
            counter += 1
        }
    }
    return counter
}

fun getBounds(floor: Set<Pair<Int, Int>>): Sequence<Pair<Int, Int>> = sequence {
    val qValues = floor.map { it.first }
    val minQ = qValues.minOrNull()!!
    val maxQ = qValues.maxOrNull()!!

    val rValues = floor.map { it.second }
    val minR = rValues.minOrNull()!!
    val maxR = rValues.maxOrNull()!!

    for (q in minQ - 1..maxQ + 1) {
        for (r in minR - 1..maxR + 1) {
            yield(Pair(q, r))
        }
    }
}

fun part2(paths: List<List<Direction>>): Int {
    var state = getInitialState(paths)
    for (day in 1..100) {
        val newState = mutableSetOf<Pair<Int, Int>>()
        for (tile in getBounds(state)) {
            val isBlack = tile in state
            when (countNeighbours(tile, state)) {
                1 -> if(isBlack) newState.add(tile)
                2    -> newState.add(tile)
                else -> { }
            }
        }
        state = newState
    }
    return state.size
}

fun main() {
    val paths = {}::class.java.getResource("/day24.in")
        .readText()
        .split("\n")
        .map { path ->
            "e|se|sw|w|nw|ne".toRegex()
                .findAll(path)
                .map { it.groupValues[0] }
                .map { Direction.fromString(it) }
                .toList()
        }
    println("Answer part 1: ${part1(paths)}")
    println("Answer part 2: ${part2(paths)}")
}