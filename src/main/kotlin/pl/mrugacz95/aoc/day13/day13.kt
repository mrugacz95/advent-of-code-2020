package pl.mrugacz95.aoc.day13

import java.lang.RuntimeException

fun part1(arrival: Int, buses: List<Int?>): Int {
    val departures = buses.filterNotNull()
    val longestBus = departures.maxOrNull() ?: throw RuntimeException("Wrong departures list")
    for (i in arrival until arrival + longestBus) {
        for (departure in departures) {
            if ((i).rem(departure) == 0) {
                return (i - arrival) * departure
            }
        }
    }
    throw RuntimeException("No bus found")
}

fun part2(departures: List<Int?>): Long { // ETA 3h 40m 52s
    val maxDeparture = departures.withIndex().filter { it.value != null }.maxByOrNull { it.value!! }
        ?: throw RuntimeException("No max departure found")
    var timestamp = maxDeparture.value!!.toLong() - maxDeparture.index
    var found: Boolean
    while (true) {
        found = true
        for (i in departures.indices) {
            val departure = departures[i] ?: continue
            if ((timestamp + i).rem(departure) != 0L) {
                timestamp += maxDeparture.value!!
                found = false
                break
            }
        }
        if (found) {
            break
        }
    }
    return timestamp
}

inline fun <T> Iterable<T>.sumByLong(selector: (T) -> Long): Long {
    var sum = 0L
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

data class GCD(
    var a: Long,
    var x: Long,
    var b: Long,
    var y: Long
)

fun gcdExtended(a: Long, b: Long): GCD {
    if (a == 0L) {
        return GCD(a, 0, b, 1)
    }
    val gcd = gcdExtended(b % a, a)

    val x = gcd.y - b / a * gcd.x
    val y = gcd.x
    return GCD(a, x, b, y)
}

/***
 * Based on http://ww2.ii.uj.edu.pl/~wilczak/ilo/pdf/twierdzenie_chinskie.pdf
 */
fun chineseRemainderTheorem(a: List<Long>, n: List<Int>): Long {
    val N = a.reduce { acc, i -> acc * i }
    val Ni = a.map { N / it }
    val x = a.zip(Ni)
        .map { gcdExtended(it.first, it.second) }
        .zip(n)
        .sumByLong { it.first.b * it.first.y * it.second }
        .rem(N)
    return if (x < 0) x + N else x
}

fun fastPart2(departures: List<Long?>): Long {
    val values = departures.mapIndexedNotNull { index, i -> if (i != null) IndexedValue(index, i) else i }
    return chineseRemainderTheorem(values.map { it.value }, values.map { -it.index })
}

fun main() {
    val notes = {}::class.java.getResource("/day13.in")
        .readText()
        .split("\n")
    val arrival = notes[0].toInt()
    val departures = notes[1].split(",").map { if (it == "x") null else it.toInt() }
    println("Answer part 1: ${part1(arrival, departures)}")
//    println("Answer part 2: ${part2(departures)}") // Just don't
    println("Answer part 2: ${fastPart2(departures.map { it?.toLong() })}")
}


