package com.mj.tic.tac.toe.javafx.kotlin.util

import java.util.Optional
import java.util.ResourceBundle
import javafx.fxml.FXMLLoader
import javafx.scene.layout.Pane

/**
 * @author Montaser Jamal
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 25-01-2023
 */

object FXMLUtil {

    fun getFXMLLoader(fxInterface: FXInterface): FXMLLoader {
        return fxInterface
            .toString()
            .let(FXMLUtil.javaClass::getResource)
            .let(::FXMLLoader)
            .also { loader ->
                ResourceBundle.getBundle("controls")?.let(loader::setResources)
            }
    }

    fun loadInterface(loader: FXMLLoader?): Pane? = loader!!.load()

    fun loadInterface(fxInterface: FXInterface): Pane? {
        return Optional.of(fxInterface)
            .map(::getFXMLLoader)
            .map(::loadInterface)
            .orElse(null)
    }
}