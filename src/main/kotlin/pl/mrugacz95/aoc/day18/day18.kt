import pl.mrugacz95.aoc.day13.sumByLong
import java.lang.RuntimeException
import java.util.Stack

const val MUL = "*"
const val ADD = "+"
const val LBR = "("
const val RBR = ")"

fun evaluateRPN(expression: List<String>): Long {
    val stack = Stack<String>()
    for (term in expression) {
        when (term) {
            MUL, ADD -> {
                val v1 = stack.pop().toLong()
                val v2 = stack.pop().toLong()
                when (term) {
                    ADD -> stack.push((v1 + v2).toString())
                    MUL -> stack.push((v1 * v2).toString())
                }
            }
            else     -> stack.push(term) // number
        }
    }
    return stack.pop().toLong()
}

fun expressionToRPN(
    expression: List<String>,
    precedence: Map<String, Int>,
): List<String> {
    val result = mutableListOf<String>()
    val operators = Stack<String>()
    for (term in expression) {
        when (term) {
            LBR      ->
                operators.push(term)
            RBR      -> {
                while (operators.peek() != LBR)
                    result.add(operators.pop())
                operators.pop()
            }
            MUL, ADD -> {
                val currentPrecedence = precedence[term] ?: throw RuntimeException("Precedence for $term not defined")
                while (operators.isNotEmpty()
                    && operators.peek() != LBR
                    && precedence[operators.peek()]!! <= currentPrecedence
                ) {
                    result.add(operators.pop())
                }
                operators.add(term)
            }
            else     -> result.add(term)
        }
    }
    while (!operators.empty()) {
        result.add(operators.pop())
    }
    return result
}

fun main() {
    val equations = {}::class.java.getResource("/day18.in")
        .readText()
        .split("\n")
        .map {
            it.replace(")", " )")
                .replace("(", "( ")
                .split(" ") // split and keep brackets separated
        }
    println("Answer part 1 : ${equations.sumByLong { evaluateRPN(expressionToRPN(it, mapOf("+" to 0, "*" to 0))) }}")
    println("Answer part 2 : ${equations.sumByLong { evaluateRPN(expressionToRPN(it, mapOf("+" to 0, "*" to 1))) }}")
}

