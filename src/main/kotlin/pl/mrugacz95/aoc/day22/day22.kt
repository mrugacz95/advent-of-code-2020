package pl.mrugacz95.aoc.day22

fun <T : Comparable<T>> Iterable<T>.argmax(): Int? {
    return withIndex().maxByOrNull { it.value }?.index
}

const val withCaching = false // cache slow down execution
val cache = mutableMapOf<Pair<Boolean, Int>, Pair<Int, List<Int>>>()

const val debug = false
fun log(message: String) {
    if (debug) {
        println(message)
    }
}

var games = 1

fun play(
    decks: List<List<Int>>,
    recursiveVersion: Boolean = false,
    parentGame: Int? = null
): Pair<Int, List<Int>> {
    val game = games++
    if (withCaching)
        cache[Pair(recursiveVersion, decks.hashCode())]?.let {
            log("Cache hit, gameplay skipped")
            log("The winner of game $game is player ${it.first + 1}!\n")
            log("...anyway, back to game ${parentGame}.")
            return it
        }
    val gameStates = mutableSetOf<Int>()
    val cards = decks.map { it.toMutableList() }
    var roundNumber = 1
    log("=== Game $game ===")
    while (!cards.any { it.isEmpty() }) {
        log("\n-- Round $roundNumber (Game $game) --")
        if (cards.hashCode() in gameStates) return Pair(0, cards[0])
        gameStates.add(cards.hashCode())
        log("Player 1's deck: ${cards[0].joinToString(", ")}")
        log("Player 2's deck: ${cards[1].joinToString(", ")}")
        val desk = cards.map { it.removeAt(0) }
        log("Player 1 plays: ${desk[0]}")
        log("Player 2 plays: ${desk[1]}")
        val wins = if (!recursiveVersion) {
            desk.argmax() ?: throw RuntimeException("No winner found")
        } else {
            when {
                desk.zip(cards).all { it.first <= it.second.size } -> {
                    log("Playing a sub-game to determine the winner...\n")
                    val newDecks = cards.zip(desk).map { it.first.take(it.second) }.toMutableList()
                    val (winner, _) = play(newDecks, true, game)
                    winner
                }
                else                                               -> {
                    desk.argmax() ?: throw RuntimeException("No winner found")
                }
            }
        }
        log("Player ${wins + 1} wins round $roundNumber of game $game!")
        roundNumber += 1
        cards[wins].addAll(
            if (wins == 0) {
                desk
            } else {
                desk.reversed()
            }
        )
    }
    val winner = cards.withIndex().first { it.value.isNotEmpty() }.index
    log("The winner of game $game is player ${winner + 1}!\n")
    if (parentGame != null)
        log("...anyway, back to game $parentGame.")
    val result = Pair(winner, cards[winner])
    if (withCaching) {
        cache[Pair(recursiveVersion, decks.hashCode())] = result
    }
    return result
}

fun calculatePlayerScore(cards: List<Int>): Int {
    return cards.reversed().withIndex().map { it.value * (it.index + 1) }.sum()
}

fun main() {
    val decks = {}::class.java.getResource("/day22.in")
        .readText()
        .split("\n\n")
        .map { deck ->
            deck.split("\n").drop(1).map { it.toInt() }
        }
    println("Answer part 1: ${calculatePlayerScore(play(decks).second)}")
    println("Answer part 2: ${calculatePlayerScore(play(decks, true).second)}")
}