package pl.mrugacz95.aoc.day20

import java.lang.RuntimeException
import kotlin.math.sqrt

data class Tile(val id: Int, val sides: Map<Side, String>, val raw: List<String>) {

    companion object {
        const val EMPTY = '.'
        const val FULL = '#'

        fun fromRaw(definition: String): Tile {
            val rows = definition.split("\n")
            val id = rows[0].replace("Tile ", "").replace(":", "").toInt()
            val rawImage = rows.subList(1, rows.size)
            val sides = Side.values().associateBy({ it }, { getEdge(it, rawImage) })
            return Tile(id, sides, rawImage)
        }

        private fun getEdge(side: Side, image: List<String>): String {
            val edge = when (side) {
                Side.TOP -> image.first().toList()
                Side.BOTTOM -> image.last().toList()
                Side.LEFT -> image.map { it.first() }
                Side.RIGHT -> image.map { it.last() }
            }
            return edge.joinToString("")
        }
    }

    enum class Side {
        TOP, BOTTOM, LEFT, RIGHT;
    }

    override fun toString(): String {
        return "<id:$id sides:${sides()}>"
    }

    fun rotateRight(): Sequence<Tile> = sequence {
        var tile = this@Tile
        for (r in 0..4) {
            val newSides = mapOf(
                Side.TOP to tile.sides[Side.LEFT]!!,
                Side.BOTTOM to tile.sides[Side.RIGHT]!!,
                Side.LEFT to tile.sides[Side.BOTTOM]!!,
                Side.RIGHT to tile.sides[Side.TOP]!!,
            )
            val newRaw = tile.raw.withIndex().map { row -> tile.raw.map { it[row.index] }.reversed().joinToString("") }
            tile = Tile(id, newSides, newRaw)
            yield(tile)
        }
    }

    fun flipAcross(): Sequence<Tile> = sequence {
        yield(this@Tile)
        yield(
            Tile(
                id, mapOf(
                    Side.TOP to sides[Side.TOP]!!.reversed(),
                    Side.BOTTOM to sides[Side.BOTTOM]!!.reversed(),
                    Side.LEFT to sides[Side.RIGHT]!!,
                    Side.RIGHT to sides[Side.LEFT]!!
                ),
                raw.map { it.reversed() }
            )
        )
        yield(
            Tile(
                id, mapOf(
                    Side.TOP to sides[Side.BOTTOM]!!,
                    Side.BOTTOM to sides[Side.TOP]!!,
                    Side.LEFT to sides[Side.LEFT]!!.reversed(),
                    Side.RIGHT to sides[Side.RIGHT]!!.reversed()
                ),
                raw.reversed()
            )
        )
    }

    private fun matchSide(other: Tile?, side: Side): Boolean {
        if (other == null) return true
        val sideToMatch = this.sides[side]!!
        return sideToMatch in other.sides.values ||
                sideToMatch in other.sides.values.map { it.reversed() }
    }

    fun matchAnyEdge(other: Tile): Boolean {
        return sides.values.any { it in other.sides.values } ||
                sides.values.any { it.reversed() in other.sides.values }
    }

    fun countMatchingEdges(others: List<Tile>): Int {
        var counter = 0
        for (other in others) {
            if (this.id == other.id) continue
            if (this.matchAnyEdge(other)) {
                counter += 1
            }
        }
        return counter
    }

    fun isCorner(others: List<Tile>) = countMatchingEdges(others) == 2
    fun isBorder(others: List<Tile>) = countMatchingEdges(others) == 3

    override fun equals(other: Any?): Boolean {
        if (other !is Tile) return false
        return this.id == other.id
    }

    override fun hashCode(): Int {
        return id
    }

    fun orientToMatch(neighbours: Map<Side, Tile?>): Tile {
        for (flipped in flipAcross()) {
            for (rotated in flipped.rotateRight()) {
                if (neighbours.all { rotated.matchSide(it.value, it.key) }) {
                    return rotated
                }
            }
        }
        throw RuntimeException("No matching to neighbours found")
    }

    private fun top() = raw.first().toList()
    private fun bottom() = raw.last().toList()
    private fun left() = raw.map { it.first() }
    private fun right() = raw.map { it.last() }

    fun sides() = listOf(top(), bottom(), left(), right())
}

fun part1(tiles: List<Tile>): Long {
    return tiles.filter { it.isCorner(tiles) }.fold(1L, { acc, tile -> acc * tile.id })
}

fun arrangeImage(tiles: List<Tile>): List<List<Tile>> {
    val tilesLeft = tiles.toMutableList()
    val imageSize = sqrt(tiles.size.toDouble()).toInt()

    val firstCorner = tilesLeft.first { it.isCorner(tiles) }
    tilesLeft.remove(firstCorner)

    // collect first column
    val firstColumn = mutableListOf(firstCorner)
    for (y in 1 until imageSize) {
        for (tile in tilesLeft) {
            if (tile.isBorder(tiles) && tile.matchAnyEdge(firstColumn[y - 1]) && !firstColumn.contains(tile)) {
                firstColumn.add(tile)
                tilesLeft.remove(tile)
                break
            }
        }
    }
    for (tile in tilesLeft) {
        if (tile.isCorner(tiles) && tile.matchAnyEdge(firstColumn.last()) && !firstColumn.contains(tile)) {
            firstColumn.add(tile)
            tilesLeft.remove(tile)
            break
        }
    }

    // collect next columns left-to-right using other tiles
    val fullImage = firstColumn.map { mutableListOf(it) }
    for (x in 1 until imageSize) {
        for (y in 0 until imageSize) {
            val leftNeighbour = fullImage[y][x - 1]
            for (tile in tilesLeft) {
                if (tile.matchAnyEdge(leftNeighbour)) {
                    fullImage[y].add(tile)
                    tilesLeft.remove(tile)
                    break
                }
            }
        }
    }

//    println(fullImage.joinToString("\n") { row -> row.joinToString { it.id.toString() } })
    return fullImage
}

fun orientTiles(arrangedImage: List<List<Tile>>): MutableList<MutableList<Tile>> {
    val result = MutableList(arrangedImage.size) { mutableListOf<Tile>() }
    for ((rowId, row) in arrangedImage.withIndex()) {
        for ((colId, tile) in row.withIndex()) {
            val neighbours = mapOf(
                Tile.Side.TOP to if (rowId == 0) null else arrangedImage[rowId - 1][colId],
                Tile.Side.BOTTOM to if (rowId == arrangedImage.lastIndex) null else arrangedImage[rowId + 1][colId],
                Tile.Side.LEFT to if (colId == 0) null else arrangedImage[rowId][colId - 1],
                Tile.Side.RIGHT to if (colId == arrangedImage[0].lastIndex) null else arrangedImage[rowId][colId + 1]
            )
            val oriented = tile.orientToMatch(neighbours)
            result[rowId].add(oriented)
        }
    }
    return result
}

class Image(private val orientTiles: MutableList<MutableList<Tile>>) {

    companion object {
        val monster = listOf(
            "                  # ",
            "#    ##    ##    ###",
            " #  #  #  #  #  #   "
        )

    }

    val tileSize = orientTiles.first().first().raw.size
    private val withoutBorders = orientTiles.flatMap { tilesRow ->
        val row = mutableListOf<String>()
        for (y in 1 until tileSize - 1) {
            row.add(tilesRow.joinToString("") { tile -> tile.raw[y].drop(1).dropLast(1) })
        }
        row
    }

    fun printWithBorders() {
        for (row in orientTiles) {
            for (y in row.first().raw.indices) {
                println(row.joinToString(" ") { tile -> tile.raw[y] })
            }
            println()
        }
    }

    fun printWithoutBorders() {
        println(withoutBorders.joinToString("\n"))
    }

    private fun countMonsters(image: List<String>): Int {
        var counter = 0
        for (y in 0 until  image.size - monster.size){
            for (x in 0 until image.first().length - monster.first().length){
                var found = true
                monsterLoop@
                for (my in monster.indices){
                    for (mx in monster.first().indices){
                        if(image[y + my][x + mx] != '#' && monster[my][mx] == '#'){
                            found = false
                            break@monsterLoop
                        }
                    }
                }
                if (found){
                    counter += 1
                }
            }
        }
        return counter
    }



    fun countMonstersWithTransformations(): Int {
        val imageTile = Tile(0, mapOf(Tile.Side.TOP to "",Tile.Side.BOTTOM to "",Tile.Side.LEFT to "",Tile.Side.RIGHT to ""), withoutBorders)
        for (flipped in imageTile.flipAcross()) {
            for (rotated in flipped.rotateRight()) {
                val count = countMonsters(rotated.raw)
                if (count > 0) {
                    return count
                }
            }
        }
        return 0
    }

    fun countHashes(img : List<String>): Int {
        return img.joinToString("").count { it == '#' }
    }

    fun countSafeWaters() : Int{
        return countHashes(withoutBorders) - countHashes(monster) * countMonstersWithTransformations()

    }
}

fun part2(tiles: List<Tile>): Int {
    val arrangedImage = arrangeImage(tiles)
    val image = Image(orientTiles(arrangedImage))
//    image.printWithoutBorders()
    return image.countSafeWaters()

}

fun main() {
    val tiles = {}::class.java.getResource("/day20.in")
        .readText()
        .split("\n\n")
        .map { Tile.fromRaw(it) }
    println("Answer part 1: ${part1(tiles)}")
    println("Answer part 2: ${part2(tiles)}")
}