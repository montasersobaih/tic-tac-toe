package com.mj.tic.tac.toe.javafx.java.util;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents the outcome of a Tic-Tac-Toe game by capturing the winning
 * player and the board coordinates that constitute the winning line.
 *
 * <p>A {@code Winner} object is produced by the game logic once a winning
 * condition is detected &mdash; either a row, column, or diagonal is fully
 * occupied by the same player.  The {@link #getPlayer()} method identifies
 * which player won, while {@link #getLocations()} returns the exact cells
 * (ordered from one end of the line to the other) that form the winning
 * three-in-a-row.
 *
 * <p>This class is immutable and relies on Lombok-generated
 * {@code equals()}, {@code hashCode()}, {@code toString()}, and a
 * constructor accepting both fields via {@link lombok.Data @Data} and
 * {@link lombok.AllArgsConstructor @AllArgsConstructor}.
 *
 * <p><b>Usage example:</b>
 * <pre>{@code
 * Winner result = new Winner(Player.HUMAN, List.of(
 *     new Coordinates(0, 0),
 *     new Coordinates(0, 1),
 *     new Coordinates(0, 2)
 * ));
 *
 * System.out.println("Winner: " + result.getPlayer());
 * System.out.println("Winning cells: " + result.getLocations());
 * }</pre>
 *
 * @author Montaser Jamal
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @see Player
 * @see Coordinates
 * @since 22-01-2023
 */

@Data
@AllArgsConstructor
public final class Winner {

    private final Player player;

    private final List<Coordinates> locations;
}
