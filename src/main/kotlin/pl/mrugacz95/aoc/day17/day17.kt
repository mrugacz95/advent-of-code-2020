package pl.mrugacz95.aoc.day17

fun <T, U> Iterable<T>.cartesianProduct(other: Iterable<U>): Iterable<Pair<T, U>> {
    return this.flatMap { lhsElem -> other.map { rhsElem -> lhsElem to rhsElem } }
}

typealias Cube = Triple<Int, Int, Int>

val Cube.x: Int
    get() = first

val Cube.y: Int
    get() = second

val Cube.z: Int
    get() = third

val Cube.w: Int
    get() = third


const val ACTIVE = '#'
const val INACTIVE = '.'
var state = {}::class.java.getResource("/day17.in")
    .readText()
    .split("\n")
    .withIndex()
    .map { row ->
        row.value.withIndex()
            .filter { field -> field.value == ACTIVE }
            .map { Triple(it.index, row.index, 0) }
    }
    .flatten()
    .toSet()

var xRange = 0..(state.maxByOrNull { it.first }?.first ?: throw RuntimeException("Wrong input"))
var yRange = 0..(state.maxByOrNull { it.second }?.second ?: throw RuntimeException("Wrong input"))
var zRange = 0..0
var neighbourhood = (-1..1)
    .cartesianProduct(-1..1)
    .cartesianProduct(-1..1)
    .filterNot { it.first.first == 0 && it.second == 0 && it.first.second == 0 }
    .map { Triple(it.first.first, it.first.second, it.second) }

operator fun Cube.plus(other: Cube): Cube {
    return Cube(first + other.first, second + other.second, third + other.third)
}

fun countNeighbours(cube: Cube, state: Set<Cube>): Int {
    var count = 0
    for (neighbour in neighbourhood) {
        if (cube + neighbour in state) {
            count += 1
        }
    }
    return count
}

fun IntRange.update(value: Int): IntRange {
    return minOf(first, value)..maxOf(last, value)
}

fun IntRange.increase(): IntRange {
    return (first - 1)..(last + 1)
}

fun iterValues() = sequence {
    for (z in zRange.increase()) {
        for (y in yRange.increase()) {
            for (x in xRange.increase()) {
                yield(Cube(x, y, z))
            }
        }
    }
}

fun step(state: Set<Cube>): HashSet<Cube> {
    val nextCubes = HashSet<Cube>()
    var nextXRange = xRange
    var nextYRange = yRange
    var nextZRange = zRange
    for (cube in iterValues()) {
        val active = when (countNeighbours(cube, state)) {
            2    -> cube in pl.mrugacz95.aoc.day17.state
            3    -> true
            else -> false
        }
        if (active) {
            nextCubes.add(cube)
            nextXRange = nextXRange.update(cube.x)
            nextYRange = nextYRange.update(cube.y)
            nextZRange = nextZRange.update(cube.z)
        }
    }
    xRange = nextXRange
    yRange = nextYRange
    zRange = nextZRange
    return nextCubes
}

fun printState() {
    for (z in zRange) {
        println("z=$z")
        for (y in yRange) {
            for (x in xRange) {
                print(if (Triple(x, y, z) in state) ACTIVE else INACTIVE)
            }
            println()
        }
    }
    println()
}

fun part1(): Int {
    for (i in 1..6) {
        state = step(state)
    }
    return state.count()
}

fun main() {
    println("Answer part 1: ${part1()}")
}