package com.mj.tic.tac.toe.javafx.kotlin

import com.mj.tic.tac.toe.javafx.kotlin.constant.UInterface
import com.mj.tic.tac.toe.javafx.kotlin.util.FXMLUtil
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.stage.StageStyle

class HelloApplication : Application() {

    override fun start(stage: Stage) {
        val pane = FXMLUtil.loadInterface(UInterface.APPLICATION_PAGE);

        val scene = Scene(pane, 600.0, 400.0)
        scene.fill = Color.TRANSPARENT

        stage.title = "Tic Tac Toe"
        stage.scene = scene
        stage.initStyle(StageStyle.TRANSPARENT)
        stage.show()
    }
}

fun main() {
    Application.launch(HelloApplication::class.java)
}