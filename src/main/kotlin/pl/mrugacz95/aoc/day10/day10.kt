package pl.mrugacz95.aoc.day10

fun part1(joltages: List<Int>): Int {
    val differences = joltages.sorted().windowed(2, 1).map { it[1] - it[0] }.groupingBy { it }.eachCount()
    return differences[1]!! * differences[3]!!
}

val cache = HashMap<Int, Long>()

fun findPermutations(startIndex: Int, list: List<Int>): Long {
    cache[startIndex]?.let {
        return it
    }
    if (startIndex == list.size - 1) {
        return 1
    }
    val first = list[startIndex]
    var count = 0L
    for (i in startIndex + 1 until list.size) {
        val nextValue = list[i]
        if (nextValue - first <= 3) {
            count += findPermutations(i, list)
        } else {
            break
        }
    }
    cache[startIndex] = count
    return count
}

fun main() {
    val joltages = {}::class.java.getResource("/day10.in")
        .readText()
        .split("\n")
        .map { it.toInt() }
        .sorted()
        .toMutableList()
    joltages.add(0, 0)
    joltages.add(joltages.size, joltages.maxOrNull()!! + 3)

    println("Answer part 1: ${part1(joltages)}")
    println("Answer part 2: ${findPermutations(0, joltages)}")
}