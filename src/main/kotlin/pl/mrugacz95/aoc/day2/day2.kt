package pl.mrugacz95.aoc.day2

import java.lang.RuntimeException

fun Boolean.toInt() = if (this) 1 else 0

data class Policy(val policy: String) {
    companion object {
        val regex = "(?<least>\\d+)-(?<most>\\d+) (?<char>.): (?<pass>[a-z]+)".toRegex()
    }

    private val groups = regex.matchEntire(policy)?.groups ?: throw RuntimeException("Regex doesn't match")
    val first = groups["least"]?.value?.toInt() ?: throw RuntimeException("First field not found")
    val second = groups["most"]?.value?.toInt() ?: throw RuntimeException("Second field not found")
    val char = groups["char"]?.value?.get(0) ?: throw RuntimeException("Char field not found")
    val pass = groups["pass"]?.value ?: throw RuntimeException("Pass field not found")
}

fun validPolicyPart1(policy: Policy): Boolean {
    val count = policy.pass
        .toList()
        .groupingBy { it }
        .eachCount()[policy.char] ?: return false
    return count in policy.first..policy.second
}

fun part1(policies: List<Policy>) {
    println("Answer part 1: ${policies.map { validPolicyPart1(it).toInt() }.sum()}")
}

fun validPolicyPart2(policy: Policy): Boolean {
    val characters = policy.pass.toList()
    return (characters[policy.first - 1] == policy.char).xor(characters[policy.second - 1] == policy.char)
}

fun part2(policies: List<Policy>) {
    println("Answer part 2: ${policies.map { validPolicyPart2(it).toInt() }.sum()}")
}

fun main() {
    val policies: List<Policy> = {}::class.java.getResource("/day2.in")
        .readText()
        .split("\n")
        .map { Policy(it) }
    part1(policies)
    part2(policies)
}