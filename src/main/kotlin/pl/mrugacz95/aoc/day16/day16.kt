package pl.mrugacz95.aoc.day16

import pl.mrugacz95.aoc.day2.toInt

class Ticket(description: String) {
    private val groups = description.split("\n\n")
    private val fields = groups[0].split("\n").map {
        val field = it.split(": ")
        val values = field[1].split(" or ")
            .map { range -> range.split("-") }
            .map { value -> value[0].toInt()..value[1].toInt() }
        Pair(field[0], values)
    }.toList()
    private val myTicket = groups[1].replace("your ticket:\n", "").split(",").map { it.toInt() }
    private val nearbyTickets = groups[2].replace("nearby tickets:\n", "")
        .split("\n")
        .map { ticket -> ticket.split(",").map { it.toInt() } }

    override fun toString(): String {
        return "fields:\n${fields}\nmy ticket:\n${myTicket}\nnearby tickets:\n${nearbyTickets}"
    }

    private fun isValueValidForRanges(fieldValue: Int, ranges: List<IntRange>): Boolean {
        return ranges.any { range -> fieldValue in range }
    }

    private fun isValueValidForAllRanges(fieldValue: Int): Boolean {
        return fields.map { it.second }.any { isValueValidForRanges(fieldValue, it) }
    }

    private fun ticketValuesInAllRanges(ticket: List<Int>): Boolean {
        return ticket.all { isValueValidForAllRanges(it) }
    }

    fun sumInvalidFields(): Int {
        return nearbyTickets.flatten().filter { !isValueValidForAllRanges(it) }.sum()
    }

    private fun discardInvalidTickets(): List<List<Int>> {
        return nearbyTickets.filter { ticketValuesInAllRanges(it) }
    }

    private fun guessFieldsNames(validTickets: List<List<Int>>, solver: MarriageSolver<String, Int>): Map<String, Int> {
        val preferences = fields
            .withIndex()
            .associateBy({ it.value.first },
                { field ->
                    fields.indices
                        .filter { index -> // matches all ticket values
                            validTickets
                                .map { it[index] }
                                .withIndex()
                                .all { fieldValue -> isValueValidForRanges(fieldValue.value, field.value.second) }
                        }
                        .toSet()
                })
        return solver.solve(preferences)
    }

    fun mulDepartureFields(solver: MarriageSolver<String, Int>): Long {
        val validTickets = discardInvalidTickets()
        val fieldsMatch = guessFieldsNames(validTickets, solver)
        return fieldsMatch.entries
            .filter { it.key.startsWith("departure") }
            .map { myTicket[it.value].toLong() }
            .reduce { acc, i -> acc * i }
    }
}

abstract class MarriageSolver<K, V> {
    abstract fun solve(preferences: Map<K, Set<V>>): Map<K, V>
}

class GreedySolver<K, V> : MarriageSolver<K, V>() {
    inner class ExclusiveMatrix(private val rows: Set<K>, private val cols: Set<V>) {
        init {
            if (rows.size != cols.size) {
                throw RuntimeException("Cols size must match rows size")
            }
        }

        private val mat = Array(rows.size) { BooleanArray(rows.size) { true } } // all fields match all labels

        fun match(rowElement: K, colElement: V) {
            val rowId = rows.indexOf(rowElement)
            val colId = cols.indexOf(colElement)
            for (i in mat.indices) {
                mat[rowId][i] = false
                mat[i][colId] = false
            }
            mat[rowId][colId] = true
        }

        fun unmatch(rowElement: K, colElement: V) {
            val rowId = rows.indexOf(rowElement)
            val colId = cols.indexOf(colElement)
            mat[rowId][colId] = false
        }

        fun determined(): Boolean {
            return mat.map { row -> row.filter { it }.count() }.all { it == 1 }
        }

        fun getMapping(): Map<K, V> {
            if (!determined()) {
                throw RuntimeException("Mapping is not determined yet")
            }
            return mat.withIndex()
                .associateBy(
                    { rows.elementAt(it.index) },
                    { row ->
                        cols.elementAt(row.value
                            .withIndex()
                            .single { it.value }
                            .index)
                    })
        }

        fun getMatches(rowElement: K): List<V> {
            return mat[rows.indexOf(rowElement)]
                .withIndex()
                .filter { it.value }
                .map { cols.elementAt(it.index) }
        }

        override fun toString(): String {
            val longestLabel = (rows.map { it.toString().length }.maxByOrNull { it }
                ?: throw RuntimeException("No max label length found")) + 1
            return rows.zip(mat).joinToString("\n", postfix = "\n") { pair ->
                val matches = getMatches(pair.first)
                "${pair.first.toString().padEnd(longestLabel)} ${
                    pair.second.joinToString(" ",
                        transform = { value -> value.toInt().toString() })
                }  ${pair.second.filter { it }.count()} ${if (matches.size == 1) matches.single().toString() else "?"}"
            }
        }
    }

    override fun solve(preferences: Map<K, Set<V>>): Map<K, V> {
        val guesses = ExclusiveMatrix(
            preferences.keys,
            preferences.values.flatten().toSet()
        )
        while (!guesses.determined()) {
            for (preference in preferences) {
                val matches = guesses.getMatches(preference.key)
                if (matches.size == 1) { // has match
                    guesses.match(preference.key, matches.first())
                    continue
                }
                for (candidate in preferences.values.flatten()) {
                    preferences[preference.key]?.let {
                        if (candidate !in it) {
                            guesses.unmatch(preference.key, candidate)
                        }
                    }
                }
            }
        }
        return guesses.getMapping()
    }
}

fun main() {
    val ticket = Ticket({}::class.java.getResource("/day16.in").readText())
    println("Answer part 1: ${ticket.sumInvalidFields()}")
    println("Answer part 2: ${ticket.mulDepartureFields(HopcroftKarpSolver())}")
}

class HopcroftKarpSolver<U, V> : MarriageSolver<U, V>() {

    sealed class NodeWrapper<U, V> {
        class UWrapper<U, V>(val value: U) : NodeWrapper<U, V>() {
            override fun toString(): String {
                return value.toString()
            }
        }

        class VWrapper<U, V>(val value: V) : NodeWrapper<U, V>() {
            override fun toString(): String {
                return value.toString()
            }
        }

        override fun equals(other: Any?): Boolean {
            when (other) {
                is UWrapper<*, *> -> {
                    if (this is UWrapper)
                        return other.value == this.value
                }
                is VWrapper<*, *> -> {
                    if (this is VWrapper)
                        return other.value == this.value
                }
            }
            return false
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }

    override fun solve(preferences: Map<U, Set<V>>): Map<U, V> {
        val uMatching = mutableMapOf<U, V>()
        val vMatching = mutableMapOf<V, U>()

        for (u in preferences.keys) { // all nodes
            if (u !in uMatching.keys) { // not matched u node
                val queue = ArrayDeque<NodeWrapper<U, V>>()
                val visited = mutableSetOf<NodeWrapper<U, V>>()
                val previous = mutableMapOf<NodeWrapper<U, V>, NodeWrapper<U, V>?>()

                val startNode = NodeWrapper.UWrapper<U, V>(u)
                queue.add(startNode) // start BFS
                visited.add(startNode)
                previous[startNode] = null
                while (queue.isNotEmpty()) {
                    when (val current = queue.removeFirst()) {
                        is NodeWrapper.UWrapper -> { // u node
                            for (v in preferences[current.value]!!) { // all neighbours
                                val neighbour = NodeWrapper.VWrapper<U, V>(v)
                                if (neighbour !in visited) { // only not visited
                                    visited.add(neighbour) // set visited
                                    previous[neighbour] = current // set previous
                                    queue.add(neighbour) // queue up node
                                }
                            }
                        }
                        is NodeWrapper.VWrapper -> { // v node
                            if (current.value !in vMatching) { // without matching
                                var node = current
                                while (previous[node] != null) { // iterate path to startNode, pseudo DFS
                                    if (node is NodeWrapper.VWrapper) {
                                        val newMatching = (previous[node]!! as NodeWrapper.UWrapper).value
                                        vMatching[node.value] = newMatching
                                        uMatching[newMatching] = node.value
                                    }
                                    node = previous[node]!!
                                }
                                break // found unmatched v node, end loop
                            } else { // has matching
                                val matching = NodeWrapper.UWrapper<U, V>(vMatching[current.value]!!)
                                visited.add(matching) // set visited
                                previous[matching] = current // set previous
                                queue.add(matching) // queue up node
                            }
                        }
                    }
                }
            }
        }
        return uMatching
    }
}