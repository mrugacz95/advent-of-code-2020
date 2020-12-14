package pl.mrugacz95.aoc.day14

import java.lang.RuntimeException

sealed class Command
data class Mem(val addr: Long, val value: Long) : Command()
data class Mask(val mask: String) : Command()

fun generateValues(floatingValue: String): Sequence<String> = sequence {
    when {
        floatingValue.isEmpty() -> yield("") // break recursion
        floatingValue[0] == 'X' -> { // split
            yieldAll(generateValues('1' + floatingValue.substring(1, floatingValue.length)))
            yieldAll(generateValues('0' + floatingValue.substring(1, floatingValue.length)))
        }
        else                    -> { // continue
            yieldAll(generateValues(floatingValue.substring(1, floatingValue.length))
                .map { floatingValue[0] + it })
        }

    }

}

fun applyMask(value: String, mask: String): String {
    return value.padStart(36, '0').zip(mask).map {
        when (it.second) {
            '0' -> it.first
            else -> it.second
        }
    }.joinToString("")
}

fun solve(commands: List<Command>, simpleMem: Boolean = true): Long {
    val mem = hashMapOf<Long, Long>()
    var mask: String? = null
    for (command in commands) {
        when (command) {
            is Mask -> {
                mask = command.mask
            }
            is Mem  -> {
                val currentMask = mask ?: throw RuntimeException("Mask not initialized")
                when {
                    simpleMem -> {
                        val zeroMask = currentMask.replace('X', '1').toLong(2)
                        val oneMask = currentMask.replace('X', '0').toLong(2)
                        mem[command.addr] = (command.value or oneMask) and zeroMask
                    }
                    else      -> {
                        val maskedAddr = applyMask(command.addr.toString(2), currentMask)
                        for (addr in generateValues(maskedAddr)) {
                            mem[addr.toLong(2)] = command.value
                        }
                    }
                }
            }
        }
    }
    return mem.values.sum()
}

fun parseLine(line: String): Command {
    return when (line.substring(0, 4)) {
        "mem[" -> {
            val regex = "mem\\[(?<addr>\\d+)] = (?<value>\\d+)".toRegex()
            val groups = regex.matchEntire(line)?.groups ?: throw RuntimeException("Unrecognized mem line: $line")
            val mem = groups["addr"]?.value?.toLong() ?: throw RuntimeException("Didn't found addr in memory line")
            val value = groups["value"]?.value?.toLong() ?: throw RuntimeException("Didn't found value in memory line")
            Mem(mem, value)
        }
        "mask" -> {
            val mask = line.replace("mask = ", "")
            Mask(mask)
        }
        else   -> throw RuntimeException("Unrecognized line: $line")
    }
}

fun main() {
    val commands = {}::class.java.getResource("/day14.in")
        .readText()
        .split("\n")
        .map { parseLine(it) }
    println("Answer part 1: ${solve(commands)}")
    println("Answer part 2: ${solve(commands, false)}")
}