package com.mj.tic.tac.toe.javafx.kotlin.task

import javafx.application.Platform
import javafx.css.PseudoClass
import javafx.scene.control.Button
import javafx.scene.layout.Pane
import java.util.Arrays

/**
 * @author Montaser Jamal
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 22-01-2023
 */

class ResetPlayAreaTask : BaseTask<Void?> {

    private val matrix: Array<ByteArray>

    private val playAreaPane: Pane

    @Suppress("ConvertSecondaryConstructorToPrimary")
    constructor(matrix: Array<ByteArray>, playAreaPane: Pane) : super() {
        this.matrix = matrix
        this.playAreaPane = playAreaPane
    }

    override fun call(): Void? {
        for (line in matrix) {
            Arrays.fill(line, 0.toByte())
        }

        for (node in playAreaPane.children) {
            Platform.runLater { resetButton(node as Button) }
        }

        return null
    }

    private fun resetButton(button: Button) {
        button.text = null
        button.isDisable = false
        button.pseudoClassStateChanged(PseudoClass.getPseudoClass("winner"), false)
    }
}