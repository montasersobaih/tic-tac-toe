package com.mj.tic.tac.toe.javafx.kotlin.util

import javafx.fxml.FXMLLoader
import javafx.scene.layout.Pane
import java.util.Optional
import java.util.ResourceBundle
import java.util.stream.Stream

/**
 * @author Montaser Jamal
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 25-01-2023
 */

object FXMLUtil {

    fun getFXMLLoader(fxInterface: FXInterface): FXMLLoader? {
        return Stream.of(fxInterface)
            .map(FXInterface::toString)
            .map(FXMLUtil.javaClass::getResource)
            .map(::FXMLLoader)
            .peek {
                Optional
                    .of("controls")
                    .map(ResourceBundle::getBundle)
                    .ifPresent(it!!::setResources)
            }
            .findFirst()
            .orElse(null)
    }

    fun loadInterface(loader: FXMLLoader?): Pane? = loader!!.load()

    fun loadInterface(fxInterface: FXInterface): Pane? {
        return Optional.of(fxInterface)
            .map(::getFXMLLoader)
            .map(::loadInterface)
            .orElse(null)
    }
}