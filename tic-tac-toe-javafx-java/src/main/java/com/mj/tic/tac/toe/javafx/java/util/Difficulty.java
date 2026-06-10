package com.mj.tic.tac.toe.javafx.java.util;

/**
 * Represents the three AI difficulty levels available in the game.
 * <p>
 * Each constant is associated with a resource-bundle key that resolves to a
 * localized display label (e.g. {@code "Easy"}, {@code "Moyen"}, etc.),
 * enabling the UI to render the difficulty name in the user's locale.
 * </p>
 *
 * <p>The difficulty level controls the computer's move-selection strategy:</p>
 * <ul>
 *   <li>{@link #EASY} — picks a random empty cell.</li>
 *   <li>{@link #MEDIUM} — randomly chooses between a random move and the
 *       optimal minimax move (50/50).</li>
 *   <li>{@link #HARD} — always plays the optimal move via the full minimax
 *       algorithm.</li>
 * </ul>
 *
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 20-01-2023
 */
public enum Difficulty {

    EASY("control.label.difficulty.easy"),
    MEDIUM("control.label.difficulty.medium"),
    HARD("control.label.difficulty.hard");

    /**
     * Resource-bundle key for the localized display name of this difficulty.
     */
    private final String resourceKey;

    /**
     * Constructs a difficulty level constant.
     *
     * @param resourceKey the resource-bundle key used to look up the localized label.
     */
    Difficulty(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    /**
     * Returns the resource-bundle key used to look up this difficulty's localized label.
     *
     * @return the resource-bundle key (e.g. {@code "control.label.difficulty.easy"}).
     */
    public String getResourceKey() {
        return resourceKey;
    }

    /**
     * Returns the resource-bundle key for this difficulty's localized display name.
     *
     * @return the resource-bundle key (e.g. {@code "control.label.difficulty.easy"}).
     */
    @Override
    public String toString() {
        return this.resourceKey;
    }
}
