package com.mj.tic.tac.toe.javafx.java.engine;

import com.mj.tic.tac.toe.javafx.java.util.Board;
import com.mj.tic.tac.toe.javafx.java.util.Coordinates;
import com.mj.tic.tac.toe.javafx.java.util.Player;
import com.mj.tic.tac.toe.javafx.java.util.Winner;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * Detects a winning condition on a Tic-Tac-Toe {@link Board} by scanning rows,
 * columns, and both diagonals.
 * <p>
 * The three scan axes are executed concurrently via {@link CompletableFuture}
 * and the first to find a complete line of matching non-empty marks wins the
 * race via {@link CompletableFuture#anyOf(CompletableFuture[])}. If all three
 * complete without finding a winner, an empty {@link Optional} is returned.
 * <p>
 * <strong>Board encoding expected:</strong>
 * <ul>
 *   <li>{@code 0} &mdash; empty cell</li>
 *   <li>{@code 1} &mdash; cell marked by the human player</li>
 *   <li>{@code 2} &mdash; cell marked by the computer player</li>
 * </ul>
 * <p>
 * Each detection method returns a {@link Winner} on success or {@code null}
 * on failure. The public {@link #detect(Board)} method converts {@code null}
 * results into {@link Optional#empty()} via the
 * {@link CompletableFuture#exceptionally exceptionally} callback, ensuring
 * callers never deal with {@code null}.
 * <p>
 * <strong>Thread safety:</strong> This class is stateless and thread-safe.
 * The private detection methods perform only reads on the board, so multiple
 * futures can safely share the same {@code Board} instance without
 * synchronization.
 *
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 20-01-2023
 */

public final class WinnerDetector {

    /**
     * Scans the given board for a winning line across rows, columns, and
     * diagonals in parallel.
     * <p>
     * Three asynchronous tasks are launched — one per scan axis — and the
     * first to discover a complete line of matching non-empty marks wins
     * the race. If an axis scan throws an exception or returns {@code null}
     * (meaning no winner was found on that axis), it is treated as an
     * empty result.
     * <p>
     * Thread interruption during the blocking {@link CompletableFuture#get()}
     * call restores the interrupt flag and returns {@link Optional#empty()}.
     *
     * @param board the board state to inspect; must not be {@code null}.
     * @return an {@link Optional} containing the {@link Winner} if a winning
     * line exists, or {@link Optional#empty()} if the game is still
     * in progress or the board is filled with no winner (draw).
     */
    @SuppressWarnings("DuplicatedCode")
    public static Optional<Winner> detect(Board board) {
        CompletableFuture<Winner> rowFut = CompletableFuture.supplyAsync(() -> winningRowDetection(board));
        CompletableFuture<Winner> colFut = CompletableFuture.supplyAsync(() -> winningColumnDetection(board));
        CompletableFuture<Winner> diagFut = CompletableFuture.supplyAsync(() -> winningDiagonalDetection(board));

        return CompletableFuture
                .allOf(rowFut, colFut, diagFut)
                .thenApply(ignored -> Stream.of(rowFut, colFut, diagFut)
                        .map(CompletableFuture::join)
                        .filter(Objects::nonNull)
                        .findFirst())
                .exceptionally(ex -> Optional.empty())
                .join();
    }

    /**
     * Scans the board for a winning line, optimized for a move just made at
     * the given coordinates.
     *
     * <p>Unlike {@link #detect(Board)}, this overload only checks the row,
     * column, and diagonal(s) that intersect the specified coordinates rather
     * than scanning every row and column on the board. This reduces the scan
     * complexity from O(n&sup2;) to O(3n) for axis-aligned scan evaluations
     * plus the two diagonals.</p>
     *
     * <p>This method is intended to be called immediately after a player
     * marks a cell, because only the line that includes the new mark can
     * be a newly completed winning line.</p>
     *
     * @param board       the board state to inspect; must not be {@code null}
     * @param coordinates the coordinates of the most recently placed mark;
     *                    must not be {@code null}
     * @return an {@link Optional} containing the {@link Winner} if the
     * move at the given coordinates completed a winning line, or
     * {@link Optional#empty()} otherwise
     */
    @SuppressWarnings("DuplicatedCode")
    public static Optional<Winner> detect(Board board, Coordinates coordinates) {
        CompletableFuture<Winner> rowFut = CompletableFuture.supplyAsync(() -> winningRowDetection(board, coordinates));
        CompletableFuture<Winner> colFut = CompletableFuture.supplyAsync(() -> winningColumnDetection(board, coordinates));
        CompletableFuture<Winner> diagFut = CompletableFuture.supplyAsync(() -> winningDiagonalDetection(board));

        return CompletableFuture
                .allOf(rowFut, colFut, diagFut)
                .thenApply(ignored -> Stream.of(rowFut, colFut, diagFut)
                        .map(CompletableFuture::join)
                        .filter(Objects::nonNull)
                        .findFirst())
                .exceptionally(ex -> Optional.empty())
                .join();
    }

    /**
     * Scans each row (constant {@code x}, varying {@code y}) for a complete
     * line of marks by the same player.
     * <p>
     * Iterates row by row (top to bottom). For each row, the first cell's
     * value is captured and compared against the remaining cells in that row.
     * If any cell is empty ({@code 0}) or belongs to a different player, the
     * row is abandoned and scanning continues to the next row.
     *
     * @param board the board state to inspect.
     * @return a {@link Winner} containing the player and the three coordinates
     * of the winning row, or {@code null} if no row is complete.
     */
    @SuppressWarnings("DuplicatedCode")
    private static Winner winningRowDetection(Board board) {
        for (int x = 0; x < board.getDimension(); x++) {
            List<Coordinates> winningLine = new ArrayList<>(List.of(new Coordinates(x, 0)));
            byte pValue = board.get(winningLine.get(0));
            for (int y = 1; y < board.getDimension(); y++) {
                if (pValue != 0 && pValue == board.get(new Coordinates(x, y))) {
                    winningLine.add(new Coordinates(x, y));
                } else {
                    break;
                }

                if (winningLine.size() == board.getDimension()) {
                    return new Winner(Player.from(pValue), winningLine);
                }
            }
        }

        return null;
    }

    @SuppressWarnings("DuplicatedCode")
    private static Winner winningRowDetection(Board board, Coordinates coordinates) {
        List<Coordinates> winningLine = new ArrayList<>(List.of(coordinates.withY(0)));

        byte pValue = board.get(winningLine.get(0));
        for (int y = 1; y < board.getDimension(); y++) {
            var yCoordinates = coordinates.withY(y);
            if (board.get(yCoordinates) == pValue) {
                winningLine.add(yCoordinates);
            } else {
                return null;
            }

            if (winningLine.size() == board.getDimension()) {
                return new Winner(Player.from(pValue), winningLine);
            }
        }

        return null;
    }

    /**
     * Scans each column (constant {@code y}, varying {@code x}) for a
     * complete line of marks by the same player.
     * <p>
     * Iterates column by column (left to right). For each column, the first
     * cell's value is captured and compared against the remaining cells in
     * that column. If any cell is empty ({@code 0}) or belongs to a different
     * player, the column is abandoned and scanning continues to the next
     * column.
     *
     * @param board the board state to inspect.
     * @return a {@link Winner} containing the player and the three coordinates
     * of the winning column, or {@code null} if no column is complete.
     */
    @SuppressWarnings("DuplicatedCode")
    private static Winner winningColumnDetection(Board board) {
        for (int y = 0; y < board.getDimension(); y++) {
            List<Coordinates> winningLine = new ArrayList<>(List.of(new Coordinates(0, y)));
            byte pValue = board.get(winningLine.get(0));
            for (int x = 1; x < board.getDimension(); x++) {
                if (pValue != 0 && pValue == board.get(new Coordinates(x, y))) {
                    winningLine.add(new Coordinates(x, y));
                } else {
                    break;
                }

                if (winningLine.size() == board.getDimension()) {
                    return new Winner(Player.from(pValue), winningLine);
                }
            }
        }

        return null;
    }

    /**
     * Scans only the column intersecting the given coordinates for a
     * complete line of marks by the same player.
     *
     * <p>This is an optimized variant of {@link #winningColumnDetection(Board)}
     * that checks a single column rather than every column on the board.
     * It is intended for use after a move has been placed at the specified
     * coordinates.</p>
     *
     * @param board       the board state to inspect
     * @param coordinates the coordinates whose column should be scanned
     * @return a {@link Winner} if the entire column is filled by the same
     * player, or {@code null} otherwise
     */
    private static Winner winningColumnDetection(Board board, Coordinates coordinates) {
        List<Coordinates> winningLine = new ArrayList<>(List.of(coordinates.withX(0)));

        byte pValue = board.get(winningLine.get(0));
        for (int x = 1; x < board.getDimension(); x++) {
            var xCoordinates = coordinates.withX(x);
            if (pValue == board.get(xCoordinates)) {
                winningLine.add(xCoordinates);
            } else {
                return null;
            }

            if (winningLine.size() == board.getDimension()) {
                return new Winner(Player.from(pValue), winningLine);
            }
        }

        return null;
    }

    /**
     * Scans both diagonals for a complete line of marks by the same player.
     * <p>
     * Checks the top-left to bottom-right diagonal (cells where {@code x == y})
     * first. If that diagonal is incomplete, it checks the top-right to
     * bottom-left diagonal (cells where {@code x + y == SIZE - 1}). An empty
     * cell at the starting position immediately skips that diagonal.
     *
     * @param board the board state to inspect.
     * @return a {@link Winner} containing the player and the three coordinates
     * of the winning diagonal, or {@code null} if neither diagonal is
     * complete.
     */
    @SuppressWarnings("DuplicatedCode")
    private static Winner winningDiagonalDetection(Board board) {
        // top-left to bottom-right
        List<Coordinates> winningLine = new ArrayList<>();
        byte pValue = board.get(new Coordinates(0, 0));
        if (pValue != 0) {
            for (int i = 0; i < board.getDimension(); i++) {
                if (pValue == board.get(new Coordinates(i, i))) {
                    winningLine.add(new Coordinates(i, i));
                } else {
                    break;
                }
            }

            if (winningLine.size() == board.getDimension()) {
                return new Winner(Player.from(pValue), winningLine);
            }
        }

        // top-right to bottom-left
        winningLine.clear();
        pValue = board.get(new Coordinates(0, board.getDimension() - 1));
        if (pValue != 0) {
            for (int i = 0; i < board.getDimension(); i++) {
                int y = board.getDimension() - 1 - i;
                if (pValue == board.get(new Coordinates(i, y))) {
                    winningLine.add(new Coordinates(i, y));
                } else {
                    break;
                }
            }
            if (winningLine.size() == board.getDimension()) {
                return new Winner(Player.from(pValue), winningLine);
            }
        }

        return null;
    }
}
