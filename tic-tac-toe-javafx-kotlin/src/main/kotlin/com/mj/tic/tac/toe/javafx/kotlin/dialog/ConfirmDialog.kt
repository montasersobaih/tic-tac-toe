package com.mj.tic.tac.toe.javafx.kotlin.dialog

import com.mj.tic.tac.toe.javafx.kotlin.constant.DInterface
import com.mj.tic.tac.toe.javafx.kotlin.util.FXMLUtil
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import java.net.URL
import java.util.Optional
import java.util.ResourceBundle
import java.util.stream.Stream

/**
 * @author Montaser Sobaih
 * @version 1.0
 * @since 13-06-2021
 */

class ConfirmDialog : BaseDialog<BorderPane?> {

    @FXML
    private lateinit var confirmPane: BorderPane

    @FXML
    private lateinit var confirmTitle: Label

    @FXML
    private lateinit var confirmBody: Label

    private var confirm: ButtonListener? = null

    private var decline: ButtonListener? = null

    @Suppress("ConvertSecondaryConstructorToPrimary")
    private constructor(container: StackPane) : super(container) {
        super.setParentBackground(Color.TRANSPARENT)
        super.setTransitionType(DialogTransition.CENTER)
    }

    companion object {
        fun getInstance(container: StackPane): Builder {
            return Builder(ConfirmDialog(container))
        }
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

    @FXML
    private fun onConfirmButton(event: ActionEvent) {
        Optional.ofNullable(confirm).ifPresent(ButtonListener::doAction).also { close() }
    }

    @FXML
    private fun onDeclineButton(event: ActionEvent) {
        Optional.ofNullable(decline).ifPresent(ButtonListener::doAction).also { close() }
    }

    /*=================================================={Builder}=====================================================*/
    class Builder(private val dialog: ConfirmDialog) {

        fun setMessage(message: String): Builder {
            dialog.confirmBody.text = message
            return this
        }

        fun setOnConfirmListener(listener: ButtonListener): Builder {
            dialog.confirm = listener
            return this
        }

        fun setOnDeclineListener(listener: ButtonListener): Builder {
            dialog.decline = listener
            return this
        }

        fun build(): ConfirmDialog = dialog
    }

    /*=================================================={Listener}====================================================*/
    fun interface ButtonListener {
        fun doAction()
    }
}