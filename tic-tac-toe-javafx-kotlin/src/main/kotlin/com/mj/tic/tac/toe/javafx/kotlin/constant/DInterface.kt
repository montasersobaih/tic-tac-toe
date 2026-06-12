package com.mj.tic.tac.toe.javafx.kotlin.constant

import com.mj.tic.tac.toe.javafx.kotlin.util.FXInterface

/**
 * Enum mapping dialog names to their FXML resource paths.
 *
 * Implements [FXInterface] so that instances can be used directly
 * with [FXMLUtil] for loading dialog layouts. Each constant's
 * [toString] returns the full classpath resource path to the
 * corresponding FXML file.
 *
 * @property value The full FXML resource path for this dialog.
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 15-10-2022
 */

enum class DInterface(private val value: String) : FXInterface {

    /** Generic confirmation dialog with confirm and cancel buttons. */
    CONFIRM_DIALOG("${ResourcePath.DIALOG}confirm_dialog.fxml"),

    /** Difficulty selection dialog with Easy, Medium, and Hard options. */
    DIFFICULTY_DIALOG("${ResourcePath.DIALOG}difficulty_dialog.fxml");

    /**
     * Returns the full FXML resource path for this dialog.
     *
     * @return The FXML path string used by [FXMLUtil] to load the layout.
     */
    override fun toString(): String = value
}