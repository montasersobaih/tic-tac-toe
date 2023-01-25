package com.mj.tic.tac.toe.javafx.kotlin.dialog

import com.mj.tic.tac.toe.javafx.kotlin.constant.DInterface
import com.mj.tic.tac.toe.javafx.kotlin.util.FXMLUtil
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import java.net.URL
import java.util.ResourceBundle
import java.util.stream.Stream

/**
 * @author Montaser Sobaih
 * @version 1.0
 * @since 13-06-2021
 */

class AlertDialog : BaseDialog<BorderPane?> {

    @FXML
    private lateinit var alertPane: BorderPane

    @FXML
    private lateinit var alertBody: Label

    @Suppress("ConvertSecondaryConstructorToPrimary")
    private constructor(container: StackPane, body: String) : super(container) {
        alertBody!!.text = body
        super.setParentBackground(Color.TRANSPARENT)
        super.setTransitionType(DialogTransition.CENTER)
    }

    companion object {
        fun show(pane: StackPane, message: String) = AlertDialog(pane, message).show()
    }

    override fun initialize(location: URL, resources: ResourceBundle) {}

    override fun initializeLayout(): BorderPane {
        return Stream
            .of(DInterface.ALERT_DIALOG)
            .map(FXMLUtil::getFXMLLoader)
            .peek { it?.setController(this@AlertDialog) }
            .map(FXMLUtil::loadInterface)
            .findFirst()
            .map(BorderPane::class.java::cast)
            .orElse(null)
    }

    override fun onDialogKeyPressed(event: KeyEvent) {
        if (event.code == KeyCode.ENTER) {
            close()
        } else {
            super.onDialogKeyPressed(event)
        }
    }

    @FXML
    private fun onButtonClick(event: ActionEvent) = close()
}