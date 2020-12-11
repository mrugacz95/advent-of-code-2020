package pl.mrugacz95.aoc.day11

class Ferry(private var seats: List<String>, private val tolerance: Int, closeNeighbourhood: Boolean = true) {
    companion object {
        const val EMPTY = 'L'
        const val OCCUPIED = '#'
    }

    private val checker = when (closeNeighbourhood) {
        true  -> CloseNeighbourChecker()
        false -> FarNeighbourChecker()
    }

    fun getSeat(y: Int, x: Int): Char {
        return seats[y][x]
    }

    abstract inner class NeighbourChecker {
        abstract fun check(y: Int, x: Int, direction: Pair<Int, Int>): Boolean
    }

    inner class CloseNeighbourChecker : NeighbourChecker() {
        override fun check(y: Int, x: Int, direction: Pair<Int, Int>): Boolean {
            val ny = y + direction.first
            val nx = x + direction.second
            return ny >= 0 && ny < seats.size && nx >= 0 && nx < seats[0].length && getSeat(ny, nx) == OCCUPIED
        }
    }

    inner class FarNeighbourChecker : NeighbourChecker() {
        override fun check(y: Int, x: Int, direction: Pair<Int, Int>): Boolean {
            var ny = y + direction.first
            var nx = x + direction.second
            while (ny >= 0 && ny < seats.size && nx >= 0 && nx < seats[0].length) {
                when (getSeat(ny, nx)) {
                    OCCUPIED -> return true
                    EMPTY    -> return false
                }
                ny += direction.first
                nx += direction.second
            }
            return false
        }
    }

    private fun countNeighbours(y: Int, x: Int): Int {
        val directions = generateDirections()
        var count = 0
        for (dir in directions) {
            if (checker.check(y, x, dir)) {
                count += 1
            }
        }
        return count
    }

    private fun generateDirections(): MutableList<Pair<Int, Int>> {
        val directions = mutableListOf<Pair<Int, Int>>()
        for (dy in -1..1) {
            for (dx in -1..1) {
                if (dy == 0 && dx == 0) continue
                directions.add(Pair(dy, dx))
            }
        }
        return directions
    }

    fun step(): Boolean {
        val newSeats = seats.mapIndexed { rowId, row ->
            row.mapIndexed { colId, seat ->
                val occupied = countNeighbours(rowId, colId)
                when (seat) {
                    EMPTY    -> if (occupied == 0) OCCUPIED else EMPTY
                    OCCUPIED -> if (occupied >= tolerance) EMPTY else OCCUPIED
                    else     -> seat
                }
            }.joinToString("")
        }.toList()
        val changed = newSeats == seats
        seats = newSeats
        return changed
    }

    fun countOccupied(): Int {
        return seats.map { it.filter { seat -> seat == OCCUPIED }.count() }.sum()
    }

    override fun toString(): String {
        return "count : ${countOccupied()}\n${seats.joinToString("\n")}\n"
    }
}

fun solve(ferry: Ferry): Int {
    var afterStep = ferry.countOccupied()
    var beforeStep: Int
    do {
        beforeStep = afterStep
        ferry.step()
        afterStep = ferry.countOccupied()
    } while (beforeStep != afterStep)
    return beforeStep
}

fun main() {
    val seats = {}::class.java.getResource("/day11.in")
        .readText()
        .split("\n")
    println("Answer part 1: ${solve(Ferry(seats, 4))}")
    println("Answer part 2: ${solve(Ferry(seats, 5, false))}")
}