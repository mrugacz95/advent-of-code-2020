package pl.mrugacz95.aoc.day21

import pl.mrugacz95.aoc.day16.GreedySolver

class Food(raw: String){
    private val groups = raw.split(" (contains ")
    val ingredients = groups[0].split(" ").toSet()
    val allergens = groups[1].dropLast(1).split(", ")
}

fun solve(foodList: List<Food>): Map<String, String> {
    val allAllergens = foodList.flatMap { it.allergens }.toSet()
    val mayBeIn = mutableMapOf<String, Set<String>>()
    for (allergen in allAllergens) {
        val foodContaining = foodList.filter { allergen in it.allergens }
        val possibleIngredients = foodContaining.map { it.ingredients }.reduce { acc, i -> acc.intersect(i) }
        mayBeIn[allergen] = possibleIngredients
    }
    return GreedySolver<String, String>().solve(mayBeIn.toMap())
}

fun main() {
    val food = {}::class.java.getResource("/day21.in")
        .readText()
        .split("\n")
        .map { Food(it) }
    val matching= solve(food)
    println("Answer part 1: ${food.map { f -> f.ingredients.filter { ingredient -> ingredient !in matching.values }.count() }.sum()}")
    println("Answer part 2: ${matching.entries.sortedBy { it.key }.joinToString(",") { it.value }}")
}