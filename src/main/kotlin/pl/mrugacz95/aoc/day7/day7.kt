package pl.mrugacz95.aoc.day7
const val goldBag = "shiny gold"
val bags: Map<String, List<Pair<Int, String>>> = {}::class.java.getResource("/day7.in")
    .readText()
    .split("\n")
    .associateBy(
        { it.split(" bags contain ")[0] },
        { content ->
            content.split(" bags contain ")[1]
                .split(", ")
                .filter { !it.startsWith("no") }
                .map {
                    val bag = it.split(" ")
                    Pair(bag[0].toInt(), bag.subList(1, bag.size - 1).joinToString(" "))
                }
        })

fun canContainShinyGold(color: String): Boolean {
    val contains = bags[color] ?: return false
    for (bag in contains) {
        if (bag.second == goldBag || canContainShinyGold(bag.second)) {
            return true
        }
    }
    return false
}

fun countBags(color: String) : Int{
    val content = bags[color] ?: return 1
    return content.map { it.first * countBags(it.second) }.sum() + 1
}

fun main() {
    println("Answer part 1: ${bags.keys.filter { bag -> canContainShinyGold(bag) }.count()}")
    println("Answer part 2: ${countBags(goldBag) - 1}")
}