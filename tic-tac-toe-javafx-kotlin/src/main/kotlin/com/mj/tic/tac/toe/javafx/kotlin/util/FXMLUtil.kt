package com.mj.tic.tac.toe.javafx.kotlin.util

import com.mj.tic.tac.toe.javafx.kotlin.util.FXMLUtil.getFXMLLoader
import com.mj.tic.tac.toe.javafx.kotlin.util.FXMLUtil.loadInterface
import java.util.Optional
import java.util.ResourceBundle
import javafx.fxml.FXMLLoader
import javafx.scene.layout.Pane

/**
 * Singleton utility for loading JavaFX FXML layouts.
 *
 * Centralizes all FXML loading logic in one place, reducing code
 * duplication and ensuring consistent resource bundle attachment
 * across the application. All FXML paths are resolved relative to
 * the classpath using the resource paths defined in [ResourcePath].
 *
 * @author Montaser Jamal
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 25-01-2023
 */

object FXMLUtil {

    /**
     * Creates an [FXMLLoader] for the given FXML interface constant.
     *
     * Resolves the FXML resource path from [FXInterface.toString],
     * creates a new [FXMLLoader], and attaches the `"controls"`
     * [ResourceBundle] for i18n support.
     *
     * @param fxInterface The FXML interface enum constant whose
     *   [toString] returns the FXML resource path.
     * @return A fully configured [FXMLLoader] ready to load the layout.
     */
    fun getFXMLLoader(fxInterface: FXInterface): FXMLLoader {
        return fxInterface
            .toString()
            .let(FXMLUtil.javaClass::getResource)
            .let(::FXMLLoader)
            .also { loader ->
                ResourceBundle.getBundle("controls")?.let(loader::setResources)
            }
    }

    /**
     * Loads an FXML layout from a pre-configured [FXMLLoader].
     *
     * @param loader The [FXMLLoader] to invoke [FXMLLoader.load] on.
     * @return The root [Pane] of the loaded layout, or null if the
     *   loader is null.
     */
    fun loadInterface(loader: FXMLLoader?): Pane? = loader!!.load()

    /**
     * Convenience method that chains [getFXMLLoader] and [loadInterface].
     *
     * @param fxInterface The FXML interface constant to load.
     * @return The root [Pane] of the loaded layout, or null if loading
     *   fails.
     */
    fun loadInterface(fxInterface: FXInterface): Pane? {
        return Optional.of(fxInterface)
            .map(::getFXMLLoader)
            .map(::loadInterface)
            .orElse(null)
    }
}