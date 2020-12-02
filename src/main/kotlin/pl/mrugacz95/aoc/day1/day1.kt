package pl.mrugacz95.aoc.day1

fun solution(numbers: List<Int>) {
    for (i in numbers.indices) {
        val a = numbers[i]
        for (j in i until numbers.size) {
            val b = numbers[j]
            if (a + b == 2020) {
                println("Part 1 answer: $a*$b=${a * b}")
            }
            for (k in j until numbers.size) {
                val c = numbers[k]
                if (a + b + c == 2020) {
                    println("Part 2 answer: $a*$b*$c=${a * b * c}")
                }
            }
        }
    }
}

fun main() {
    val numbers = {}::class.java.getResource("/day1.in")
        .readText()
        .split("\n")
        .map { x -> x.toInt() }
    solution(numbers)
}