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

fun part2(departures: List<Int?>): Long {
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

fun main() {
    val notes = {}::class.java.getResource("/day13.in")
        .readText()
        .split("\n")
    val arrival = notes[0].toInt()
    val departures = notes[1].split(",").map { if (it == "x") null else it.toInt() }
    println("Answer part 1: ${part1(arrival, departures)}")
    println("Answer part 2: ${part2(departures)}")
}


