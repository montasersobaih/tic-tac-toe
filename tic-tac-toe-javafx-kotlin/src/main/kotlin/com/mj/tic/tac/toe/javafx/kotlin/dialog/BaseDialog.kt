package com.mj.tic.tac.toe.javafx.kotlin.dialog

import com.jfoenix.controls.JFXDialog
import com.jfoenix.controls.events.JFXDialogEvent
import com.mj.tic.tac.toe.javafx.kotlin.util.ResourceBundleUtil
import java.net.URL
import java.util.ResourceBundle
import javafx.application.Platform
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.fxml.Initializable
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color

/**
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 15-10-2022
 */

abstract class BaseDialog<R : Pane, V> : JFXDialog, Initializable {

    private val dialogValue: ObjectProperty<V> = SimpleObjectProperty()

    @Suppress("ConvertSecondaryConstructorToPrimary")
    protected constructor(container: StackPane?) : super() {
        super.setDialogContainer(container)
        super.setOverlayClose(false)
        super.setContent(@Suppress("LeakingThis") initializeLayout())
        super.setEventHandler(KeyEvent.ANY, ::onDialogKeyPressed)
        super.addEventHandler(JFXDialogEvent.OPENED) { requestFocus() }
    }

    abstract override fun initialize(location: URL, resources: ResourceBundle)

    protected abstract fun initializeLayout(): R

    protected fun setParentBackground(@Suppress("SameParameterValue") color: Color?) {
        val pane = content.parent as Pane
        pane.background = Background(BackgroundFill(color, null, null))
    }

    protected fun getLocalizedText(key: String): String = ResourceBundleUtil.getString(key)

    protected open fun onDialogKeyPressed(event: KeyEvent) {
        if (event.code == KeyCode.ESCAPE) {
            close()
        }
    }

    protected fun getDialogValue(): V? = dialogValue.get()

    protected fun updateValueAndClose(value: V) {
        dialogValue.set(value)
        if (Platform.isFxApplicationThread()) {
            super.close()
        } else {
            Platform.runLater { super.close() }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun showAndWait(): V? {
        require(Platform.isFxApplicationThread()) {
            "showAndWait() must be called from the JavaFX Application Thread."
        }

        val handler = { _: JFXDialogEvent ->
            Platform.exitNestedEventLoop(this@BaseDialog, dialogValue.get())
        }

        super.addEventHandler(JFXDialogEvent.CLOSED, handler)

        try {
            super.show()
            return Platform.enterNestedEventLoop(this) as V?
        } finally {
            super.removeEventHandler(JFXDialogEvent.CLOSED, handler)
        }
    }
}