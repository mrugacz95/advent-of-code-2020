package pl.mrugacz95.aoc.day19

import java.lang.RuntimeException

fun Iterable<String>.cartesianProduct(other: Iterable<String>): List<String> {
    return this.flatMap { lhsElem -> other.map { rhsElem -> lhsElem + rhsElem } }
}

const val debug = true

fun println(str: String) {
    if (debug)
        print("$str\n")
}

fun evalRule(index: Int, rules: Map<Int, String>, result: MutableMap<Int, List<String>>): List<String> {
    result[index]?.let {
        return it
    }
    val rule = rules[index] ?: throw RuntimeException("Rule definition not found: $index")
    val eval = if (rule.isTerminalRule()) { // "x"
        listOf(rule[1].toString())
    } else {
        if ("|" in rule) { // X [X] | Y [Y]
            val (firstAlt, secondAlt) = rule.split(" | ")
                .map { it.split(" ").map { rId -> rId.toInt() } }
                .map { it.map { rIdx -> evalRule(rIdx, rules, result) } }
            firstAlt.reduce { acc, i -> acc.cartesianProduct(i) } + secondAlt.reduce { acc, i -> acc.cartesianProduct(i) }
        } else { // X X [X]
            val evaluated = rule.split(" | ", " ")
                .map { it.toInt() }
                .map { evalRule(it, rules, result) }
            evaluated.reduce { acc, i -> acc.cartesianProduct(i) }
        }
    }
    result[index] = eval
    return eval
}

fun String.isTerminalRule() = length == 3 && get(0) == '"' && get(2) == '"'

fun part1(rules: Map<Int, String>, messages: List<String>): Int {
    val evaluated = mutableMapOf<Int, List<String>>()
    for (key in rules.keys) {
        evalRule(key, rules, evaluated)
    }
    val zeroRule = evaluated[0] ?: throw RuntimeException("Zero rule not found")
    return messages.filter { msg -> msg in zeroRule }.count()
}

sealed class Rule(open val id: Int) {
    abstract val length: IntRange
    abstract fun match(message: List<Int>, rules: Map<Int, Rule>): Boolean
    override fun equals(other: Any?): Boolean {
        if(other !is Rule) return false
        return other.id != this.id
    }

    override fun hashCode(): Int {
        return id
    }
}

data class Term(override val id: Int, val term: String) : Rule(id) {
    override val length = 1..1
    override fun match(message: List<Int>, rules: Map<Int, Rule>) = throw RuntimeException("")
}

data class RuleList(override val id: Int, val prod: List<Int>) : Rule(id) {
    override val length = prod.size..prod.size
    override fun match(message: List<Int>, rules: Map<Int, Rule>) = prod == message.subList(0, prod.size)
}

data class Alternative(override val id: Int, val alts: List<List<Int>>) : Rule(id) {
    override val length: IntRange by lazy {
        val min = alts.map { it.size }.minByOrNull { it }!!
        val max = alts.map { it.size }.maxByOrNull { it }!!
        min..max
    }

    override fun match(message: List<Int>, rules: Map<Int, Rule>): Boolean {
        for (pair in alts) {
            if (message.take(pair.size) == pair) {
                return true
            }
        }
        return false
    }
}
val rules = mutableMapOf<Int, Rule>()
fun parseRules(unparsedRules: Map<Int, String>){
    for ((rId, rule) in unparsedRules.entries.withIndex()) {
        rules[rule.key] = if (rule.value.isTerminalRule()) { // "x"
            Term(rId, rule.value[1].toString())
        } else {
            if ("|" in rule.value) { // X [X] | Y [Y]
                Alternative(rId, rule.value.split(" | ")
                    .map { it.split(" ").map { it.toInt() } })
            } else { // X X [X]
                RuleList(rId, rule.value.split(" ")
                    .map { it.toInt() })
            }
        }
    }
}

fun first(ruleId: Int): Set<Char> {
    return when (val rule = rules[ruleId]!!) {
        is Term        -> return setOf(rule.term.first())
        is RuleList    -> return first(rule.prod.first())
        is Alternative -> rule.alts.map { first(it.first()) }.reduce { acc, i -> acc.union(i) }
    }
}

val determined = mutableMapOf<Int,Boolean>()
fun isDetermined(ruleId:Int): Boolean {
    determined[ruleId]?.let {
        return it
    }
    determined[ruleId] = false
    val result = when(val rule = rules[ruleId]!!){
        is Term        -> true
        is RuleList    -> rule.prod.all { isDetermined(it) }
        is Alternative -> rule.alts.all { prod -> prod.all { isDetermined(it) } }
    }
    determined[ruleId] = result
    return result
}

fun isTerminal(ruleId:Int): String?{
    return when(val rule = rules[ruleId]!!){
        is Term        ->  rule.term
        is RuleList    -> {
            val terms = rule.prod.map { isTerminal(it) }
            if(terms.any { it == null }){
                return null
            }
            terms.joinToString("")
        }
        is Alternative -> null
    }
}


fun ruleListMatch(productions: List<Int>, message: String): Sequence<String> = sequence {
    val firstProd = productions.first()
    val matches = matches(firstProd, message)
    if(productions.size == 1){
        yieldAll(matches)
        return@sequence
    }
    for(prodMatch in matches) {
        val currentMatch  = ruleListMatch( productions.drop(1), message.removePrefix(prodMatch))
        for(curr in currentMatch) {
            if (message.startsWith(prodMatch + curr)) {
                yield(prodMatch + curr)
            }
        }
    }
}

fun matches(ruleId: Int, message: String): Sequence<String> = sequence {
    if(message.isEmpty()){
        return@sequence
    }
    if(message.first() !in first[ruleId]!!){ // first char check
        return@sequence
    }
    when(val rule = rules[ruleId]){
        is Term        -> {
            if (message.startsWith(rule.term)) { // check again
                yield(rule.term)
            }
            return@sequence
        }
        is RuleList    -> {
            val prefixMatch = ruleListMatch(rule.prod, message)
            yieldAll(prefixMatch)
            return@sequence
        }
        is Alternative -> { // return longest match
            for(alt in rule.alts){
                ruleListMatch(alt, message).let { someMatches ->
                    for (match in someMatches) {
                        if (message.startsWith(match)) {
                            yield(match)
                        }
                    }
                }
            }
            return@sequence
        }
    }
}

val first = mutableMapOf<Int, Set<Char>>()

fun part2OnceAgain(unparsedRules: Map<Int, String>, messages: List<String>): Int {
    parseRules(unparsedRules)
    for(ruleId in rules.keys){
        isDetermined(ruleId)
    }
    println("${determined.entries.filter { it.value }.count()}/${rules.size} rules are determined")
    println("Only ${determined.entries.filterNot { it.value }.joinToString (", "){ it.key.toString() }} are not determined")
    for(ruleId in rules.keys){
        val term = isTerminal(ruleId)
        if(term != null){
            rules[ruleId] = Term(ruleId,term)
        }
    }
    for(ruleId in rules.keys){
            first[ruleId] = first(ruleId)
    }
    var counter = 0
    for(message in messages) {
       val matches = matches(0, message)
        for(match in matches) {
            if (match == message) {
                counter += 1
            }
        }
    }
    return counter
}

fun main() {
    val input = {}::class.java.getResource("/day19.in")
        .readText()
        .split("\n\n")
    val rules = input[0]
        .split("\n")
        .map { rule -> rule.split(": ") }
        .associateBy({ it[0].toInt() }, { it[1] })
        .toMutableMap()
    val messages = input[1]
        .split("\n")
    println("Answer part 1: ${part1(rules, messages)}")
    rules[8] = "42 | 42 8"
    rules[11] = "42 31 | 42 11 31"
    println("Answer part 2: ${part2OnceAgain(rules, messages)}")
}