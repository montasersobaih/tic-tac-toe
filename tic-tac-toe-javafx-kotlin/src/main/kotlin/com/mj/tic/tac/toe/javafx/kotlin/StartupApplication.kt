package com.mj.tic.tac.toe.javafx.kotlin

import com.mj.tic.tac.toe.javafx.kotlin.constant.UInterface
import com.mj.tic.tac.toe.javafx.kotlin.util.FXMLUtil
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.stage.StageStyle

/**
 * The JavaFX [Application] entry point for the Tic Tac Toe application.
 *
 * Launches the main application window with the following configuration:
 * - **Size:** 600 × 400 pixels.
 * - **Style:** Undecorated ([StageStyle.TRANSPARENT]) — the custom title
 *   bar ([com.mj.tic.tac.toe.javafx.kotlin.controller.layout.ApplicationBarController]) replaces OS-native window chrome.
 * - **Background:** Transparent scene fill, enabling the custom-shaped
 *   window appearance.
 * - **Title:** "Tic Tac Toe".
 *
 * The main FXML layout is loaded via [FXMLUtil.loadInterface] using
 * the [UInterface.APPLICATION_PAGE] resource constant.
 *
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 20-01-2023
 */

class StartupApplication : Application() {

    /**
     * Called by the JavaFX runtime to start the application.
     *
     * Loads the main FXML view, configures the scene and stage, and
     * displays the window.
     *
     * @param stage The primary stage for this application.
     * @throws Exception if the FXML layout cannot be loaded.
     */
    @Throws(Exception::class)
    override fun start(stage: Stage) {
        val pane = FXMLUtil.loadInterface(UInterface.APPLICATION_PAGE)!!

        val scene = Scene(pane, 600.0, 400.0)
        scene.fill = Color.TRANSPARENT

        stage.title = "Tic Tac Toe"
        stage.scene = scene
        stage.initStyle(StageStyle.TRANSPARENT)
        stage.show()
    }
}

/**
 * Application entry point.
 *
 * Called when the JAR is executed directly. Launches the JavaFX
 * application lifecycle with [StartupApplication] as the main class.
 */
fun main() {
    Application.launch(StartupApplication::class.java)
}