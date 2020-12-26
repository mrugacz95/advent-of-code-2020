package pl.mrugacz95.aoc.day23

fun <T> List<T>.cycle(): Sequence<T> {
    var i = 0
    if (isEmpty()) return emptySequence()
    return generateSequence {
        this[i++ % this.size]
    }
}

const val cupsToPick = 3

fun makeMove(state: List<Int>): List<Int> {
    val current = state.first()
    var cups = state
    val picked = cups.subList(1, cupsToPick + 1)
    cups = listOf(current) + cups.subList(cupsToPick + 1, cups.size).toList()
    var destination = current
    do {
        destination = if (destination - 1 > 0) {
            destination - 1
        } else {
            cups.maxOrNull()!!
        }
    }while (destination in picked)

    val destinationIdx = cups.indexOf(destination)
    val newState = cups.subList(0, destinationIdx + 1) + picked + cups.subList(destinationIdx + 1, cups.size)
    return newState.subList(1, newState.size) + listOf(newState.first())
}


fun part1(cups: List<Int>): String {
    var state = cups
    for (move in 1..100) {
        state = makeMove(state)
    }
    return state.cycle().dropWhile { it != 1 }.drop(1).take(cups.size - 1).joinToString("")
}


fun printCups(nextCup: List<Int>, currentCup: Int){
    println("Cups: ")
    var current = 1
    for(i in 1 until nextCup.size){
        if (nextCup[current] == currentCup){
            print("(${nextCup[current]}) ")
        }
        else {
            print("${nextCup[current]} ")
        }
        current = nextCup[current]
    }
    println()
}

const val debug = false
const val numberOfMoves = 10000000
const val numberOfCups = 1000000

fun part2(cups: List<Int>): Long {
    val numberOfCups = numberOfCups  + 1
    val nextCup = MutableList(numberOfCups){ it + 1 }
    nextCup[0] = -1 // empty
    for(i in 0 until cups.size - 1){
            nextCup[cups[i]] = cups[i + 1]
    }
    nextCup[cups.last()] = cups.size + 1
    nextCup[nextCup.lastIndex] = cups[0]
    var current = cups[0]
    for (move in 0..numberOfMoves) {
        val picked = mutableListOf(nextCup[current])
        for(pick in 0 until cupsToPick - 1){
            picked.add(nextCup[picked.last()])
        }

        var destination = current - 1
        while (destination in picked || destination <= 0){
            destination -= 1
            if(destination <= 0){
                destination = numberOfCups - 1
            }
        }
        if(debug) {
            println("-- move $move --")
            printCups(nextCup, current)
            println("Picked: $picked")
            println("destination: $destination\n")
        }
        nextCup[current] = nextCup[picked.last()] // skip picked

        nextCup[picked.last()] = nextCup[destination] // insert tail
        nextCup[destination] = picked.first() // insert head

        current = nextCup[current]
    }
    val first = nextCup[1]
    val second = nextCup[first]
    return first.toLong() * second.toLong()
}

fun main() {
    val cups = {}::class.java.getResource("/day23.in")
        .readText()
        .map { it.toString().toInt() }
    println("Answer part 1: ${part1(cups)}")
    println("Answer part 2: ${part2(cups)}")
}