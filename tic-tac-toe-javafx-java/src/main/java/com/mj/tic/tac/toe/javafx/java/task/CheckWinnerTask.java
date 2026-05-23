package com.mj.tic.tac.toe.javafx.java.task;

import com.mj.tic.tac.toe.javafx.java.util.Coordinates;
import com.mj.tic.tac.toe.javafx.java.util.Mark;
import com.mj.tic.tac.toe.javafx.java.util.Winner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 20-01-2023
 */

public final class CheckWinnerTask extends BaseTask<Winner> {

    /**
     * The 3×3 game board matrix to inspect.
     */
    private final byte[][] matrix;

    /**
     * The most recent {@link Mark} placed by a player — used as the starting point for win checks.
     */
    private final Mark mark;

    /**
     * Constructs a new check-winner task.
     *
     * @param matrix The 3×3 game board matrix to inspect.
     * @param mark   The most recent mark placed; its coordinates determine which row, column,
     *               and diagonals are checked.
     */
    public CheckWinnerTask(byte[][] matrix, Mark mark) {
        this.matrix = matrix;
        this.mark = mark;
    }

    /**
     * Invoked on a background thread. Inspects the row, column, and diagonals intersecting
     * the last-placed mark. If three consecutive matching cells are found, a {@link Winner}
     * is returned; otherwise {@code null}.
     *
     * @return A {@link Winner} containing the player symbol and the three winning
     * {@link Coordinates}, or {@code null} if there is no winner.
     */
    @Override
    protected Winner call() {
        List<Coordinates> locations = this.check();

        if (locations.size() == 3) {
            return new Winner(mark.getValue(), locations);
        }

        return null;
    }

    /**
     * Performs the actual win check by scanning the row, column, main diagonal, and anti-diagonal
     * that pass through the last-placed mark's coordinates. Returns the list of matching
     * coordinates as soon as all three cells in any line are found, or an empty list if none of
     * the four lines are fully occupied by the same player.
     *
     * @return A list of three {@link Coordinates} if a winning line exists, otherwise an empty list.
     */
    private List<Coordinates> check() {
        Coordinates location = mark.getCoordinates();
        byte value = matrix[location.getX()][location.getY()];
        List<Coordinates> coordinates = new ArrayList<>();

        //check row
        for (int i = 0; i < matrix[location.getX()].length; i++) {
            if (matrix[location.getX()][i] == value) {
                coordinates.add(new Coordinates(location.getX(), i));
                if (coordinates.size() == 3) {
                    return coordinates;
                }
            }
        }

        //check column
        coordinates.clear();
        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i][location.getY()] == value) {
                coordinates.add(new Coordinates(i, location.getY()));
                if (coordinates.size() == 3) {
                    return coordinates;
                }
            }
        }

        //check diagonal
        coordinates.clear();
        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i][i] == value) {
                coordinates.add(new Coordinates(i, i));
                if (coordinates.size() == 3) {
                    return coordinates;
                }
            }
        }

        //check reverse diagonal
        coordinates.clear();
        for (int i = matrix.length - 1; i >= 0; i--) {
            int lastIndex = matrix.length - 1;
            if (matrix[lastIndex - i][i] == value) {
                coordinates.add(new Coordinates(lastIndex - i, i));
                if (coordinates.size() == 3) {
                    return coordinates;
                }
            }
        }

        return Collections.emptyList();
    }
}
