package com.mj.tic.tac.toe.javafx.java.util;

/**
 * Defines the states of a tic-tac-toe game session.
 * <p>
 * This enum represents the high-level lifecycle states of the game, controlling
 * transitions between the start of a new session, individual match resets,
 * session termination, and the option to replay.
 * </p>
 *
 * @author Montaser Jamal
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @created 28-05-2026
 * @since 1.0.0
 */
public enum GameState {

    RESET_GAME,
    NEW_GAME,
    GAME_OVER;
}
