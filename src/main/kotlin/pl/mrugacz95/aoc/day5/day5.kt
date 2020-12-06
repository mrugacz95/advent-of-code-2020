package pl.mrugacz95.aoc.day5

import kotlin.math.floor

class Range(var lowerRange: Int, var upperRange: Int) {
    fun select(upper: Boolean) {
        val mid: Int = floor((lowerRange + upperRange) / 2.0).toInt()
        if (upper) {
            lowerRange = mid + 1
        } else {
            upperRange = mid
        }
    }

    fun closed(): Boolean {
        return lowerRange == upperRange
    }
}

fun getSeatId(seat: String): Int {
    val rowRange = Range(0, 127)
    for (i in 0..7) {
        when (seat[i]) {
            'F' -> rowRange.select(false)
            'B' -> rowRange.select(true)
        }
    }
    val colRange = Range(0, 7)
    for (i in 7..9) {
        when (seat[i]) {
            'L' -> colRange.select(false)
            'R' -> colRange.select(true)
        }
    }
    assert(rowRange.closed())
    assert(colRange.closed())
    return rowRange.lowerRange * 8 + colRange.lowerRange
}

fun part2(seatsIds: List<Int>): Int? {
    val seats = seatsIds.sorted()
    seats.asSequence().windowed(2,1).forEach {
        if(it[0] + 1 != it[1]){
            return it[0]+1
        }
    }
    return null
}

fun main() {
    val seats = {}::class.java.getResource("/day5.in")
        .readText()
        .split("\n")
    assert(getSeatId("BFFFBBFRRR") == 567)
    assert(getSeatId("FFFBBBFRRR") == 119)
    assert(getSeatId("BBFFBBFRLL") == 820)
    val seatsIds = seats.map { getSeatId(it) }
    println("Answer part 1: ${seatsIds.maxOrNull()}")
    println("Answer part 2: ${part2(seatsIds)}")
}