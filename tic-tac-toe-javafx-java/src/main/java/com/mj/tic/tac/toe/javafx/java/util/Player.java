package com.mj.tic.tac.toe.javafx.java.util;

import java.util.Arrays;

/**
 * Represents the two players in a Tic-Tac-Toe game.
 *
 * <p>Each {@code Player} constant is backed by a {@code byte} value that
 * corresponds to the encoding used in the game's {@link Board} cell matrix:
 * <ul>
 *   <li>{@link #HUMAN} &rarr; {@code 1}</li>
 *   <li>{@link #COMPUTER} &rarr; {@code 2}</li>
 * </ul>
 *
 * <p>Convenience methods provide access to the backing value
 * ({@link #getValue()}), the opposing player ({@link #opponent()}), and
 * reverse-lookup from a raw byte ({@link #from(byte)}).
 *
 * @author Montaser Jamal
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @created 27-05-2026
 * @since 1.0.0
 */

public enum Player {

    HUMAN((byte) 1),
    COMPUTER((byte) 2);

    private final byte value;

    Player(byte value) {
        this.value = value;
    }

    /**
     * Resolves a player from its raw board-encoding byte.
     *
     * @param value the byte value to look up ({@code 1} or {@code 2})
     * @return the matching {@code Player} constant
     * @throws IllegalArgumentException if {@code value} is not {@code 1} or {@code 2}
     */
    public static Player from(byte value) {
        return Arrays.stream(Player.values())
                .filter(player -> player.value == value)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown player value: " + value));
    }

    /**
     * Returns the byte value used to represent this player on the board.
     *
     * @return {@code 1} for {@link #HUMAN}, {@code 2} for {@link #COMPUTER}
     */
    public byte getValue() {
        return value;
    }

    /**
     * Returns the opposing player.
     *
     * <p>This is equivalent to toggling between the two players:
     * {@link #HUMAN} &harr; {@link #COMPUTER}.
     *
     * @return the other player
     */
    public Player opponent() {
        return this == HUMAN ? COMPUTER : HUMAN;
    }
}
