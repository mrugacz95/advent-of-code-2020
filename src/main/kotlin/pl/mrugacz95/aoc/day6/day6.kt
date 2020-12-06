package pl.mrugacz95.aoc.day6

fun countAnswers(answers: List<String>, countCondition: (Int, Int) -> Boolean): Int {
    val counter = mutableMapOf<Char, Int>()
    for (answer in answers) {
        for (char in answer) {
            counter[char] = counter.getOrDefault(char, 0) + 1
        }
    }
    return counter.values.map {
        if (countCondition(it, answers.size)) 1 else 0
    }.sum()
}

fun main() {
    val answers = {}::class.java.getResource("/day6.in")
        .readText()
        .split("\n\n")
        .map { it.split("\n") }
    println("Answer part 1: ${answers.map { group -> countAnswers(group) { it, _ -> it > 0 } }.sum()}")
    println("Answer part 2: ${answers.map { group -> countAnswers(group) { it, n -> it == n } }.sum()}")
}