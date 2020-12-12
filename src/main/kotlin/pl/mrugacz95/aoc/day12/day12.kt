import kotlin.math.abs

private operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>): Pair<Int, Int> {
    return Pair(this.first + other.first, this.second + other.second)
}

open class Point(var pos: Pair<Int, Int> = Pair(0, 0)) {
    var angle = 90

    open fun apply(command: Pair<Char, Int>) {
        when (command.first) {
            'N' -> pos += Pair(-command.second, 0)
            'S' -> pos += Pair(command.second, 0)
            'E' -> pos += Pair(0, command.second)
            'W' -> pos += Pair(0, -command.second)
            'L' -> angle = (angle - command.second + 360) % 360 // counter- clockwise
            'R' -> angle = (angle + command.second + 360) % 360 // clockwise
            'F' -> when (angle) {
                0   -> apply(Pair('N', command.second))
                90  -> apply(Pair('E', command.second))
                180 -> apply(Pair('S', command.second))
                270 -> apply(Pair('W', command.second))
            }
        }
    }

    fun distance(): Int {
        return abs(pos.first) + abs(pos.second)
    }

    override fun toString(): String {
        return "<${printPos()}, angle: $angle>"
    }

    fun printPos(): String {
        return "pos: ${abs(pos.first)}${if (pos.first > 0) 'S' else 'N'} ${abs(pos.second)}${if (pos.second > 0) 'E' else 'W'}"
    }
}

class PointWithWaypoint : Point() {
    private val waypoint = Point(Pair(-1, 10))
    override fun apply(command: Pair<Char, Int>) {
        when (command.first) {
            'N' -> waypoint.apply(command)
            'S' -> waypoint.apply(command)
            'E' -> waypoint.apply(command)
            'W' -> waypoint.apply(command)
            'L' -> waypoint.pos = when (command.second) { // counter-clockwise
                0    -> waypoint.pos
                90   -> {
                    Pair(-waypoint.pos.second, waypoint.pos.first)
                }
                180  -> {
                    Pair(-waypoint.pos.first, -waypoint.pos.second)
                }
                270  -> {
                    Pair(waypoint.pos.second, -waypoint.pos.first)
                }
                else -> throw RuntimeException("Wrong angle: ${command.second}")
            }
            'R' -> apply(Pair('L', (-command.second + 360) % 360)) // clockwise
            'F' -> pos += Pair(waypoint.pos.first * command.second, waypoint.pos.second * command.second)
        }
    }

    override fun toString(): String {
        return "<${printPos()}, waypoint: ${waypoint.printPos()}>"
    }
}

fun solve(commands: List<Pair<Char, Int>>, ferry: Point): Int {
    for (command in commands) {
        ferry.apply(command)
    }
    return ferry.distance()
}

fun main() {
    val commands = {}::class.java.getResource("/day12.in")
        .readText()
        .split("\n")
        .map {
            Pair(it[0], it.substring(1, it.length).toInt())
        }
    println("Answer part 1: ${solve(commands, Point())}")
    println("Answer part 2: ${solve(commands, PointWithWaypoint())}")
}

