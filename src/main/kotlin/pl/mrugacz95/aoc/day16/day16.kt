package pl.mrugacz95.aoc.day16

import pl.mrugacz95.aoc.day2.toInt
import java.lang.RuntimeException
import java.security.InvalidParameterException

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

    class ExclusiveMatrix(private val labels: List<String>) {
        private val mat = Array(labels.size) { BooleanArray(labels.size) { true } } // all fields match all labels

        fun checkIndex(index: Int) {
            if (index > labels.size) {
                throw InvalidParameterException("Index must be less than numbers of labels")
            }
        }

        fun matchLabel(label: String, index: Int) {
            checkIndex(index)
            val labelId = labels.indexOf(label)
            for (i in labels.indices) {
                mat[labelId][i] = false
                mat[i][index] = false
            }
            mat[labelId][index] = true
        }

        fun unmatchLabel(label: String, index: Int) {
            checkIndex(index)
            val labelId = labels.indexOf(label)
            mat[labelId][index] = false
        }

        fun determined(): Boolean {
            return mat.map { row -> row.filter { it }.count() }.all { it == 1 }
        }

        fun getMapping(): Map<String, Int> {
            if (!determined()) {
                throw RuntimeException("Mapping is not determined yet")
            }
            return mat.withIndex()
                .associateBy({ labels[it.index] },
                    { row ->
                        row.value
                            .withIndex()
                            .single { it.value }
                            .index
                    })
        }

        fun getLeftMatches(label: String): List<Int> {
            return mat[labels.indexOf(label)].withIndex().filter { it.value }.map { it.index }
        }

        override fun toString(): String {
            val longestLabel =
                (labels.map { it.length }.maxByOrNull { it } ?: throw RuntimeException("No max label length found")) + 1
            return labels.zip(mat).joinToString("\n", postfix = "\n") { pair ->
                "${pair.first.padEnd(longestLabel)} ${
                    pair.second.joinToString(" ",
                        transform = { value -> value.toInt().toString() })
                }  ${pair.second.filter { it }.count()}"
            }
        }
    }

    private fun guessFieldsNames(validTickets: List<List<Int>>): Map<String, Int> {
        val guesses = ExclusiveMatrix(fields.map { it.first })
        while (!guesses.determined()) {
            for (label in fields) {
                val labelMatches = guesses.getLeftMatches(label.first)
                if (labelMatches.size == 1) { // has match
                    guesses.matchLabel(label.first, labelMatches.first())
                    continue
                }
                val ranges = label.second
                for (ticket in validTickets) {
                    for ((fieldId, fieldValue) in ticket.withIndex()) {
                        if (!isValueValidForRanges(fieldValue, ranges)) {
                            guesses.unmatchLabel(label.first, fieldId)
                        }
                    }
                }
            }
        }
        return guesses.getMapping()
    }

    fun mulDepartureFields(): Long {
        val validTickets = discardInvalidTickets()
        val associatedFields = guessFieldsNames(validTickets)
        return associatedFields.entries
            .filter { it.key.startsWith("departure") }
            .map { myTicket[it.value].toLong() }
            .reduce { acc, i -> acc * i }
    }
}

fun main() {
    val ticket = Ticket({}::class.java.getResource("/day16.in").readText())
    println("Answer part 1: ${ticket.sumInvalidFields()}")
    println("Answer part 2: ${ticket.mulDepartureFields()}")
}