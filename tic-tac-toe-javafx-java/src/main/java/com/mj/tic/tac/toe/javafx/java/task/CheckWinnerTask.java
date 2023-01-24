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

    private final byte[][] matrix;

    private final Mark mark;

    public CheckWinnerTask(byte[][] matrix, Mark mark) {
        this.matrix = matrix;
        this.mark = mark;
    }

    @Override
    protected Winner call() {
        List<Coordinates> locations = this.check();

        if (locations.size() == 3) {
            return new Winner(mark.getValue(), locations);
        }

        return null;
    }

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
