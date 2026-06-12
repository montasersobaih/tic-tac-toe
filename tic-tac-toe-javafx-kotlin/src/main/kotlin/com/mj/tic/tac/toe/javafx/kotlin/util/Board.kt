package com.mj.tic.tac.toe.javafx.kotlin.util

/**
 * A mutable NxN game board backed by a 2D byte array.
 *
 * The board stores each cell as a byte value:
 * - `0` = empty cell
 * - `1` = human mark ([Player.HUMAN])
 * - `2` = computer mark ([Player.COMPUTER])
 *
 * The dimension must be an odd integer >= 3, enforced by the [require]
 * checks in the constructor. This supports standard 3x3 boards as well
 * as larger odd-dimensioned boards for extended gameplay.
 *
 * The class provides methods for marking and unmarking cells (used by
 * [com.mj.tic.tac.toe.javafx.kotlin.engine.MinimaxMoveStrategy] for simulating moves during search), querying
 * cell state, enumerating empty cells, and creating deep copies for
 * AI computation without mutating the original game state.
 *
 * @property dimension The width and height of the board (must be odd and >= 3).
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 20-01-2023
 */

class Board(val dimension: Int) {

    /** The 2D byte array backing the board state. Initialised to all zeros. */
    private val cells: Array<ByteArray> = Array(dimension) { ByteArray(dimension) }

    /** Pre-computed total cell count for O(1) full-board checks. */
    private val capacity = dimension * dimension

    /**
     * Tracks how many cells are currently occupied.
     *
     * Updated atomically by [mark] and [unmark]. The setter is private;
     * external code can only read this value.
     */
    var markedCellsCount: Int = 0
        private set

    companion object {
        /**
         * The minimum allowed board dimension.
         *
         * A board smaller than 3x3 cannot produce a meaningful
         * Tic Tac Toe game.
         */
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

    /**
     * Creates a standard 3x3 board.
     */
    constructor() : this(MINIMUM_DIMENSION)

    /**
     * Clears every cell to `0` and resets [markedCellsCount].
     *
     * Used when starting a new game or resetting the current game.
     */
    fun reset() {
        for (row in cells) {
            row.fill(0)
        }

        markedCellsCount = 0
    }

    /**
     * Marks a cell for a player at the given [Coordinates].
     *
     * Delegates to [mark] with individual x and y values.
     *
     * @param coordinates The board position to mark.
     * @param player The player whose mark is placed.
     * @throws IllegalArgumentException if the cell is already occupied.
     */
    fun mark(coordinates: Coordinates, player: Player): Unit = mark(coordinates.x, coordinates.y, player)

    /**
     * Marks the cell at (x, y) for the given player.
     *
     * Stores [Player.value] in the cell array and increments
     * [markedCellsCount].
     *
     * @param x The row index (0-based).
     * @param y The column index (0-based).
     * @param player The player making the move.
     * @throws IllegalArgumentException if the cell is already occupied
     *   (its value is not zero).
     */
    fun mark(x: Int, y: Int, player: Player): Unit {
        require(cells[x][y] == 0.toByte()) { "Cell is already marked." }
        cells[x][y] = player.value
        markedCellsCount++
    }

    /**
     * Undoes a mark at the given [Coordinates].
     *
     * Delegates to [unmark] with individual x and y values.
     *
     * @param coordinates The board position to clear.
     * @throws IllegalArgumentException if the cell was already empty.
     */
    fun unmark(coordinates: Coordinates): Unit = unmark(coordinates.x, coordinates.y)

    /**
     * Clears the cell at (x, y) back to zero.
     *
     * Decrements [markedCellsCount]. Used by AI search algorithms to
     * undo simulated moves.
     *
     * @param x The row index (0-based).
     * @param y The column index (0-based).
     * @throws IllegalArgumentException if the cell was already empty.
     */
    fun unmark(x: Int, y: Int): Unit {
        require(cells[x][y] != 0.toByte()) { "Cell is already empty." }
        cells[x][y] = 0
        markedCellsCount--
    }

    /**
     * Returns the byte value at the given [Coordinates].
     *
     * @param coordinates The position to query.
     * @return `0` (empty), `1` ([Player.HUMAN]), or `2` ([Player.COMPUTER]).
     */
    fun get(coordinates: Coordinates): Byte = get(coordinates.x, coordinates.y)

    /**
     * Returns the byte value at the given (x, y) position.
     *
     * @param x The row index (0-based).
     * @param y The column index (0-based).
     * @return `0` (empty), `1` ([Player.HUMAN]), or `2` ([Player.COMPUTER]).
     */
    fun get(x: Int, y: Int): Byte = cells[x][y]

    /**
     * Checks whether the cell at the given [Coordinates] is empty.
     *
     * @param coordinates The position to check.
     * @return `true` if the cell value is `0` (empty), `false` otherwise.
     */
    fun isEmpty(coordinates: Coordinates): Boolean = get(coordinates) == 0.toByte()

    /**
     * Checks whether the board is completely full.
     *
     * @return `true` when [markedCellsCount] equals the board's total
     *   capacity (dimension * dimension).
     */
    fun isFull(): Boolean = markedCellsCount == capacity

    /**
     * Returns a list of all empty cell positions on the board.
     *
     * Iterates every cell and collects those with value `0`.
     * Used by [RandomMoveStrategy] to select random moves and by
     * [MinimaxMoveStrategy] to enumerate candidate moves.
     *
     * @return A list of [Coordinates] for all empty cells.
     */
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

    /**
     * Creates an independent deep copy of this board.
     *
     * Each row array is copied via [System.arraycopy] to ensure the
     * copy has no shared references with the original. Used by
     * [MinimaxMoveStrategy] for simulating moves during search without
     * modifying the live game board.
     *
     * @return A new [Board] instance with the same dimension and
     *   identical cell state.
     */
    fun deepCopy(): Board {
        val copy = Board(dimension)

        for (i in 0 until dimension) {
            System.arraycopy(cells[i], 0, copy.cells[i], 0, dimension)
        }

        copy.markedCellsCount = markedCellsCount
        return copy
    }

    /**
     * Returns a cloned snapshot of the internal 2D byte array.
     *
     * @return A new [Array] of [ByteArray] containing the current
     *   board state.
     */
    fun toArray(): Array<ByteArray> = Array(dimension) { i -> cells[i].clone() }

    /**
     * Formats the board as a multi-line string for debugging.
     *
     * @return A string with one row per line, using Kotlin's
     *   [ByteArray.contentToString] for each row.
     */
    override fun toString(): String {
        return cells.joinToString(System.lineSeparator()) { row -> row.contentToString() }
    }
}
