package com.mj.tic.tac.toe.javafx.java.util;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents a player's mark placed on a Tic-Tac-Toe board cell, consisting
 * of a {@code char} symbol ({@code 'X'} or {@code 'O'}) and the
 * {@link Coordinates} of the cell where it was placed.
 * <p>
 * Instances are created via {@link #Mark(char, Coordinates)} and are
 * immutable, making them safe to use as map keys or in collections.
 *
 * @author Montaser Jamal
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 22-01-2023
 */

@Data
@AllArgsConstructor
public final class Mark {

    private final char value;

    private final Coordinates coordinates;
}
