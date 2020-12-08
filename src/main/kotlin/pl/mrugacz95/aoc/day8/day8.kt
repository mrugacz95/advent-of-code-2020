package pl.mrugacz95.aoc.day8

import java.lang.RuntimeException

enum class Op {
    NOP, JMP, ACC
}

fun part1(code: List<Pair<Op, Int>>): Pair<Int, Int> {
    var acc = 0
    val visited = mutableMapOf<Int, Boolean>()
    var pointer = 0
    while (!visited.getOrDefault(pointer, false)) {
        if (pointer >= code.size || pointer < 0) {
            return Pair(pointer, acc)
        }
        val op = code[pointer]
        visited[pointer] = true
        when (op.first) {
            Op.NOP -> pointer += 1
            Op.JMP -> pointer += op.second
            Op.ACC -> {
                acc += op.second
                pointer += 1
            }
        }
    }
    return Pair(pointer, acc)
}

fun part2(code: List<Pair<Op, Int>>): Pair<Int, Int> {
    for (i in 0..code.size) {
            val newCode = code.toMutableList()
            val oldOp = code[i]
            val newOp = when(oldOp.first){
                Op.NOP -> Op.JMP
                Op.JMP -> Op.NOP
                Op.ACC -> continue
            }
            newCode[i] = Pair(newOp, oldOp.second)
            val result = part1(newCode)
            if (result.first == code.size) {
                return result
        }
    }
    throw RuntimeException("Changing operation didn't fixed the code")
}

fun main() {
    val code = {}::class.java.getResource("/day8.in")
        .readText()
        .split("\n")
        .map {
            val op = it.split(" ")
            Pair(Op.valueOf(op[0].toUpperCase()), op[1].toInt())
        }
    println("Answer part 1: ${part1(code).second}")
    println("Answer part 2: ${part2(code)}")
}