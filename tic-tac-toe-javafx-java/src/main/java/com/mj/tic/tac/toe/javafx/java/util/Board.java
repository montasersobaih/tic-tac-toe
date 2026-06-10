package com.mj.tic.tac.toe.javafx.java.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents the Tic-Tac-Toe board as a square grid of byte-encoded cells.
 *
 * <p>The board stores cell values as {@code byte} in a 2D array, where each
 * value corresponds to a {@link Player} constant:
 * <ul>
 *   <li>{@code 0} &mdash; empty / unmarked</li>
 *   <li>{@code 1} &mdash; {@link Player#HUMAN}</li>
 *   <li>{@code 2} &mdash; {@link Player#COMPUTER}</li>
 * </ul>
 *
 * <p>Coordinates are zero-based and accessed through {@link Coordinates}
 * objects where {@code x} is the row index and {@code y} is the column index.
 * The minimum board dimension is 3 &times; 3, enforced by the constructor.</p>
 *
 * <p>Instances are mutable through the {@link #mark(Coordinates, Player)} and
 * {@link #unmark(Coordinates)} methods, but a snapshot can be obtained via
 * {@link #deepCopy()} or {@link #toArray()} for algorithms (such as minimax)
 * that need to explore hypothetical board states without mutating the original.
 * The class implements {@link Cloneable} in support of the
 * {@link #deepCopy()} method.</p>
 *
 * <p><b>Thread safety:</b> This class is <b>not</b> thread-safe. All access
 * from multiple threads must be externally synchronized.</p>
 *
 * @author Montaser Jamal
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @see Player
 * @see Coordinates
 * @since 22-01-2023
 */

public final class Board implements Cloneable {

    /**
     * The minimum allowed board dimension (3 &times; 3).
     */
    public static final int MINIMUM_DIMENSION = 3;

    /**
     * Backing store for the board cells. {@code cells[x][y]} holds the byte
     * value of the cell at row {@code x}, column {@code y}: {@code 0} for
     * empty, {@code 1} for {@link Player#HUMAN}, {@code 2} for
     * {@link Player#COMPUTER}.
     */
    private final byte[][] cells;

    /**
     * Total number of cells on the board, equal to
     * {@code dimension * dimension}. Used to determine if the board is full.
     */
    private final int capacity;

    /**
     * Current number of marked (non-empty) cells. Incremented by
     * {@link #mark(Coordinates, Player)} and decremented by
     * {@link #unmark(Coordinates)}.
     */
    private int markedCellsCount;

    /**
     * Constructs a default 3 &times; 3 board.
     */
    public Board() {
        this(MINIMUM_DIMENSION);
    }

    /**
     * Constructs a square board with the given dimension.
     *
     * @param dimension the number of rows and columns (must be &ge; 3)
     * @throws IllegalArgumentException if {@code dimension < 3}
     */
    public Board(int dimension) {
        if (dimension < MINIMUM_DIMENSION) {
            throw new IllegalArgumentException("Dimension must not be less than three, passed value: " + dimension);
        } else if (dimension % 2 == 0) {
            throw new IllegalArgumentException("Dimension must be an odd value, passed value: " + dimension);
        }

        this.cells = new byte[dimension][dimension];
        this.capacity = dimension * dimension;
    }

    /**
     * Deep-copy constructor used internally by {@link #deepCopy()}.
     *
     * @param other the board to copy from
     */
    private Board(Board other) {
        this.cells = new byte[other.cells.length][];
        for (int i = 0; i < other.cells.length; i++) {
            this.cells[i] = other.cells[i].clone();
        }
        this.capacity = other.capacity;
        this.markedCellsCount = other.markedCellsCount;
    }

    /**
     * Returns the dimension (number of rows/columns) of this square board.
     *
     * @return the board dimension
     */
    public int getDimension() {
        return cells.length;
    }

    /**
     * Clears all cells and resets the marked-cell counter to zero.
     *
     * <p>This is equivalent to resetting the board to its initial empty
     * state, ready for a new game.</p>
     */
    public void reset() {
        for (byte[] row : cells) {
            Arrays.fill(row, (byte) 0);
        }

        markedCellsCount = 0;
    }

    /**
     * Marks the cell at the given coordinates with the specified player.
     *
     * @param coordinates the cell position (must not be {@code null})
     * @param player      the player making the mark (must not be
     *                    {@code null})
     * @throws NullPointerException  if either argument is {@code null}
     * @throws IllegalStateException if the cell is already non-empty
     */
    public void mark(Coordinates coordinates, Player player) {
        Objects.requireNonNull(coordinates, "coordinates must not be null");
        mark(coordinates.getX(), coordinates.getY(), player);
    }

    /**
     * Fast-path variant that avoids creating a {@link Coordinates}
     * object.  Behaves identically to
     * {@link #mark(Coordinates, Player)}.
     *
     * @throws IllegalStateException if the cell is already non-empty
     */
    public void mark(int x, int y, Player player) {
        Objects.requireNonNull(player, "player must not be null");

        if (cells[x][y] != 0) {
            throw new IllegalStateException("Cell is already marked.");
        }

        cells[x][y] = player.getValue();
        markedCellsCount++;
    }

    /**
     * Clears the cell at the given coordinates, undoing a previous
     * {@link #mark(Coordinates, Player)}.
     *
     * <p>This is primarily used by AI algorithms (e.g. minimax) that
     * explore hypothetical board configurations and need to backtrack.</p>
     *
     * @param coordinates the cell position to clear (must not be
     *                    {@code null})
     * @throws NullPointerException  if {@code coordinates} is {@code null}
     * @throws IllegalStateException if the cell is already empty
     */
    public void unmark(Coordinates coordinates) {
        Objects.requireNonNull(coordinates, "coordinates must not be null");
        unmark(coordinates.getX(), coordinates.getY());
    }

    /**
     * Fast-path variant that avoids creating a {@link Coordinates}
     * object.  Behaves identically to
     * {@link #unmark(Coordinates)}.
     *
     * @throws IllegalStateException if the cell is already empty
     */
    public void unmark(int x, int y) {
        if (cells[x][y] == 0) {
            throw new IllegalStateException("Cell is already empty.");
        }

        cells[x][y] = 0;
        markedCellsCount--;
    }

    /**
     * Returns the raw byte value at the given coordinates.
     *
     * @param coordinates the cell position (must not be {@code null})
     * @return {@code 0} if empty, {@code 1} for {@link Player#HUMAN},
     * {@code 2} for {@link Player#COMPUTER}
     * @throws NullPointerException if {@code coordinates} is {@code null}
     */
    public byte get(Coordinates coordinates) {
        Objects.requireNonNull(coordinates, "coordinates must not be null");
        return get(coordinates.getX(), coordinates.getY());
    }

    /**
     * Fast-path variant that avoids creating a {@link Coordinates}
     * object.  Behaves identically to {@link #get(Coordinates)}.
     */
    public byte get(int x, int y) {
        return cells[x][y];
    }

    /**
     * Checks whether the cell at the given coordinates is empty.
     *
     * @param coordinates the cell position (must not be {@code null})
     * @return {@code true} if the cell value is {@code 0}
     * @throws NullPointerException if {@code coordinates} is {@code null}
     */
    public boolean isEmpty(Coordinates coordinates) {
        return get(coordinates) == 0;
    }

    /**
     * Checks whether every cell on the board has been marked.
     *
     * @return {@code true} if the board is full (draw condition)
     */
    public boolean isFull() {
        return markedCellsCount == capacity;
    }

    /**
     * Returns the number of currently marked (non-empty) cells.
     *
     * @return the count of marked cells
     */
    public int getMarkedCellsCount() {
        return markedCellsCount;
    }

    /**
     * Returns an unmodifiable list of all empty cell coordinates on the
     * board.
     *
     * <p>This is a convenience method commonly used by AI algorithms to
     * enumerate available moves.</p>
     *
     * @return unmodifiable list of empty {@link Coordinates}
     */
    public List<Coordinates> getEmptyCells() {
        List<Coordinates> moves = new ArrayList<>(capacity);
        int n = cells.length;

        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                Coordinates coordinates = new Coordinates(x, y);
                if (isEmpty(coordinates)) {
                    moves.add(coordinates);
                }
            }
        }

        return Collections.unmodifiableList(moves);
    }

    /**
     * Creates an independent deep copy of this board.
     *
     * <p>The returned board shares no mutable state with the original.
     * This is essential for algorithms that simulate future moves without
     * altering the actual game state.</p>
     *
     * @return a deep copy of this board
     */
    public Board deepCopy() {
        return new Board(this);
    }

    /**
     * Returns a defensive copy of the internal cell array.
     *
     * <p>Modifications to the returned array do not affect this board.</p>
     *
     * @return a new 2D {@code byte} array with the current cell values
     */
    public byte[][] toArray() {
        int n = cells.length;
        byte[][] copy = new byte[n][];
        for (int i = 0; i < n; i++) {
            copy[i] = this.cells[i].clone();
        }
        return copy;
    }

    /**
     * Returns a string representation of the board, one row per line.
     *
     * @return a multi-line string showing each row as an array of bytes
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (byte[] row : cells) {
            sb.append(Arrays.toString(row)).append(System.lineSeparator());
        }
        return sb.toString();
    }
}
