package com.mj.tic.tac.toe.javafx.java.constant;

import com.mj.tic.tac.toe.javafx.java.util.FXInterface;

/**
 * Enumeration of all dialog FXML resources used in the application.
 * <p>
 * Each constant maps a logical dialog name to its corresponding FXML layout
 * file path, built by concatenating the shared {@link ResourcePath#DIALOG}
 * base directory with the specific file name. All members implement the
 * {@link FXInterface} marker interface to enable uniform resource handling
 * across the UI layer.
 * </p>
 *
 * <pre>{@code
 * // Load the difficulty dialog FXML:
 * String path = DInterface.DIFFICULTY_DIALOG.toString();
 * }</pre>
 *
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 15-10-2022
 */
public enum DInterface implements FXInterface {

    /**
     * Confirmation dialog (e.g. "Play again?"). Maps to {@code confirm_dialog.fxml}.
     */
    CONFIRM_DIALOG(ResourcePath.DIALOG.concat("confirm_dialog.fxml")),

    /**
     * Difficulty-selection dialog (Easy / Medium / Hard). Maps to {@code difficulty_dialog.fxml}.
     */
    DIFFICULTY_DIALOG(ResourcePath.DIALOG.concat("difficulty_dialog.fxml"));

    /**
     * The fully-qualified FXML resource path for this dialog.
     */
    private final String value;

    /**
     * Constructs a new dialog interface constant.
     *
     * @param value the fully-qualified path to the FXML resource.
     */
    DInterface(String value) {
        this.value = value;
    }

    /**
     * Returns the fully-qualified FXML resource path.
     *
     * @return the resource path string.
     */
    @Override
    public String toString() {
        return value;
    }
}
