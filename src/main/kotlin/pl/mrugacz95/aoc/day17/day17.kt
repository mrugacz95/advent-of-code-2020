package pl.mrugacz95.aoc.day17

fun <T> Iterable<Iterable<T>>.cartesianProduct(other: Iterable<Iterable<T>>): List<List<T>> {
    return this.flatMap { lhsElem -> other.map { rhsElem -> lhsElem + rhsElem } }
}

fun mixRanges(vararg ranges: IntRange): List<List<Int>> {
    return ranges.map { range -> range.map { listOf(it) } }
        .reduce { acc, i -> acc.cartesianProduct(i) }
}

typealias Cube = List<Int>

fun Cube(vararg values: Int): Cube {
    return values.toList()
}

operator fun Cube.plus(other: Cube): Cube {
    return zip(other).map { it.first + it.second }
}

class GameOfLife(private val dimensions: Int, input: List<List<Boolean>>) {

    companion object {
        const val ACTIVE = '#'
        const val INACTIVE = '.'
    }

    private var state = input
        .withIndex()
        .map { row ->
            row.value.withIndex()
                .filter { field -> field.value }
                .map { Cube(it.index, row.index, *IntArray(dimensions - 2) { 0 }) }
        }
        .flatten()
        .toSet()

    private var ranges = List(dimensions) { dim ->
        when (dim) {
            0    -> 0..(state.maxByOrNull { it[dim] }?.get(dim) ?: throw RuntimeException("Wrong input"))
            1    -> 0..(state.maxByOrNull { it[dim] }?.get(dim) ?: throw RuntimeException("Wrong input"))
            else -> 0..0
        }
    }

    var neighbourhood = mixRanges(*Array(dimensions) { (-1..1) })
        .filterNot { it.all { v -> v == 0 } }

    private fun countNeighbours(cube: Cube, state: Set<Cube>): Int {
        var count = 0
        for (neighbour in neighbourhood) {
            if (cube + neighbour in state) {
                count += 1
            }
        }
        return count
    }

    private fun IntRange.update(value: Int): IntRange {
        return minOf(first, value)..maxOf(last, value)
    }

    private fun IntRange.increase(): IntRange {
        return (first - 1)..(last + 1)
    }

    private fun iterValues() = sequence {
        for (cube in mixRanges(*ranges.map { it.increase() }.toTypedArray())) {
            yield(cube)
        }
    }

    fun step() {
        val nextCubes = HashSet<Cube>()
        var nextRanges = ranges
        for (cube in iterValues()) {
            val active = when (countNeighbours(cube, state)) {
                2    -> cube in state
                3    -> true
                else -> false
            }
            if (active) {
                nextCubes.add(cube)
                nextRanges = nextRanges.zip(cube).map {
                    it.first.update(it.second)
                }
            }
        }
        ranges = nextRanges
        state = nextCubes
    }

    fun printState() {
        if (dimensions > 4) return // skip
        val outerRange = ranges.getOrElse(3) { 0..0 }
        for (w in outerRange) {
            for (z in ranges[2]) {
                println("z=$z, w=$w")
                for (y in ranges[1]) {
                    for (x in ranges[0]) {
                        if (dimensions > 3)
                            print(if (listOf(x, y, z, w) in state) ACTIVE else INACTIVE)
                        else
                            print(if (listOf(x, y, z) in state) ACTIVE else INACTIVE)
                    }
                    println()
                }
            }
        }
        println()
    }

    fun activeCells(): Int {
        return state.count()
    }
}

fun solve(dimensions: Int, input: List<List<Boolean>>): Int {
    val simulation = GameOfLife(dimensions, input)
    for (i in 1..6) {
//        simulation.printState()
        simulation.step()
    }
    return simulation.activeCells()
}

fun main() {
    val input = {}::class.java.getResource("/day17.in")
        .readText()
        .split("\n")
        .map { row ->
            row.map { it == GameOfLife.ACTIVE }
        }
    println("Answer part 1: ${solve(3, input)}")
    println("Answer part 2: ${solve(4, input)}")
}