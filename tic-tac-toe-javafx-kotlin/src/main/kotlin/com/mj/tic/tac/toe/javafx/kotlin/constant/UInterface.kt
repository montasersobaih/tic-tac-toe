package com.mj.tic.tac.toe.javafx.kotlin.constant

import com.mj.tic.tac.toe.javafx.kotlin.util.FXInterface

/**
 * Enum mapping main application view names to their FXML resource paths.
 *
 * Implements [FXInterface] so that instances can be used directly
 * with [FXMLUtil] for loading the main view layout. Each constant's
 * [toString] returns the full classpath resource path to the
 * corresponding FXML file.
 *
 * @property value The full FXML resource path for this view.
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 15-10-2022
 */

enum class UInterface(private val value: String) : FXInterface {

    /** The main application window layout. */
    APPLICATION_PAGE("${ResourcePath.INTERFACE}view.fxml");

    /**
     * Returns the full FXML resource path for this view.
     *
     * @return The FXML path string used by [FXMLUtil] to load the layout.
     */
    override fun toString(): String = value
}