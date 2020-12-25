package pl.mrugacz95.aoc.day25

const val reminder = 20201227L

fun getSecretLoopSize(subjectNumber: Int, pubKey: Long): Int {
    var value = 1L
    var loopSize = 0
    while (value != pubKey) {
        loopSize += 1
        value = (value * subjectNumber) % reminder
    }
    return loopSize
}

fun transformSubjectNumber(subjectNumber: Long, loopSize: Int): Long {
    var value = 1L
    for (i in 1..loopSize) {
        value = (value * subjectNumber) % reminder
    }
    return value
}

fun part1(subjectNumbers: List<Long>): Long {
    val doorsSecretLoop = getSecretLoopSize(7, subjectNumbers[1])
    return transformSubjectNumber(subjectNumbers[0], doorsSecretLoop)
}

fun main() {
    val paths = {}::class.java.getResource("/day25.in")
        .readText()
        .split("\n")
        .map { it.toLong() }
    println("Answer part 1: ${part1(paths)}")
}