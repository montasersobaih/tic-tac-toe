package com.mj.tic.tac.toe.javafx.kotlin.dialog

import com.mj.tic.tac.toe.javafx.kotlin.constant.DInterface
import com.mj.tic.tac.toe.javafx.kotlin.util.FXMLUtil
import java.net.URL
import java.util.ResourceBundle
import java.util.stream.Stream
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color

/**
 * @author Montaser Sobaih
 * @version 1.0
 * @since 13-06-2021
 */

class ConfirmDialog : BaseDialog<BorderPane, Boolean> {

    @FXML
    private lateinit var rootPane: BorderPane

    @FXML
    private lateinit var dialogTitle: Label

    @FXML
    private lateinit var dialogMessage: Label

    private var confirm: Runnable? = null

    private var cancel: Runnable? = null

    @Suppress("ConvertSecondaryConstructorToPrimary")
    private constructor(container: StackPane) : super(container) {
        super.setParentBackground(Color.TRANSPARENT)
        super.setTransitionType(DialogTransition.CENTER)
    }

    override fun initialize(location: URL, resources: ResourceBundle) {}

    override fun initializeLayout(): BorderPane {
        return Stream
            .of(DInterface.CONFIRM_DIALOG)
            .map(FXMLUtil::getFXMLLoader)
            .peek { it?.setController(this@ConfirmDialog) }
            .map(FXMLUtil::loadInterface)
            .findFirst()
            .map(BorderPane::class.java::cast)
            .orElse(null)
    }

    override fun onDialogKeyPressed(event: KeyEvent) {
        if (event.code == KeyCode.ESCAPE) {
            onCancel(ActionEvent(event, this))
        }
    }

    @FXML
    private fun onCancel(event: ActionEvent): Unit {
        updateValueAndClose(false)
        cancel?.run()
    }

    @FXML
    private fun onConfirm(event: ActionEvent): Unit {
        let { updateValueAndClose(true) }.also { confirm?.run() }
    }

    companion object {
        fun showAndWait(container: StackPane, message: String): Boolean {
            return builder(container)
                .setMessage(message)
                .build()
                .showAndWait() ?: false
        }


        fun show(container: StackPane, message: String, onConfirm: Runnable?, onCancel: Runnable? = null) {
            builder(container)
                .setMessage(message)
                .setOnConfirmListener(onConfirm)
                .setOnCancelListener(onCancel)
                .buildAndShow()
        }

        fun builder(container: StackPane) = Builder(ConfirmDialog(container))
    }

    class Builder(private val dialog: ConfirmDialog) {

        fun setMessage(message: String): Builder {
            dialog.dialogMessage.text = message
            return this
        }

        fun setOnConfirmListener(confirmation: Runnable?): Builder {
            dialog.confirm = confirmation
            return this
        }

        fun setOnCancelListener(cancellation: Runnable?): Builder {
            dialog.cancel = cancellation
            return this
        }

        fun build(): ConfirmDialog = dialog

        fun buildAndShow(): Unit = build().show()
    }
}