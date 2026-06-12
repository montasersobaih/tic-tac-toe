package com.mj.tic.tac.toe.javafx.kotlin.util

/**
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 20-01-2023
 */

class Board(val dimension: Int) {

    private val cells: Array<ByteArray> = Array(dimension) { ByteArray(dimension) }

    private val capacity = dimension * dimension

    var markedCellsCount: Int = 0
        private set

    companion object {
        const val MINIMUM_DIMENSION = 3
    }

    init {
        require(dimension >= MINIMUM_DIMENSION) {
            "Dimension must not be less than three, passed value: $dimension"
        }

        require(dimension % 2 == 1) {
            "Dimension must be an odd value, passed value: $dimension"
        }
    }

    constructor() : this(MINIMUM_DIMENSION)

    fun reset() {
        for (row in cells) {
            row.fill(0)
        }

        markedCellsCount = 0
    }

    fun mark(coordinates: Coordinates, player: Player): Unit = mark(coordinates.x, coordinates.y, player)

    fun mark(x: Int, y: Int, player: Player): Unit {
        require(cells[x][y] == 0.toByte()) { "Cell is already marked." }
        cells[x][y] = player.value
        markedCellsCount++
    }

    fun unmark(coordinates: Coordinates): Unit = unmark(coordinates.x, coordinates.y)

    fun unmark(x: Int, y: Int): Unit {
        require(cells[x][y] != 0.toByte()) { "Cell is already empty." }
        cells[x][y] = 0
        markedCellsCount--
    }

    fun get(coordinates: Coordinates): Byte = get(coordinates.x, coordinates.y)

    fun get(x: Int, y: Int): Byte = cells[x][y]

    fun isEmpty(coordinates: Coordinates): Boolean = get(coordinates) == 0.toByte()

    fun isFull(): Boolean = markedCellsCount == capacity

    fun getEmptyCells(): List<Coordinates> {
        val moves = mutableListOf<Coordinates>()

        for (x in 0 until dimension) {
            for (y in 0 until dimension) {
                val c = Coordinates(x, y)
                if (isEmpty(c)) {
                    moves.add(c)
                }
            }
        }

        return moves
    }

    fun deepCopy(): Board {
        val copy = Board(dimension)

        for (i in 0 until dimension) {
            System.arraycopy(cells[i], 0, copy.cells[i], 0, dimension)
        }

        copy.markedCellsCount = markedCellsCount
        return copy
    }

    fun toArray(): Array<ByteArray> = Array(dimension) { i -> cells[i].clone() }

    override fun toString(): String {
        return cells.joinToString(System.lineSeparator()) { row -> row.contentToString() }
    }
}
