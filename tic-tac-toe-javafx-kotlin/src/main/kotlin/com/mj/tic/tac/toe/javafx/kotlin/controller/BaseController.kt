package com.mj.tic.tac.toe.javafx.kotlin.controller

import com.mj.tic.tac.toe.javafx.kotlin.util.ResourceBundleUtil
import java.net.URL
import java.util.ResourceBundle
import javafx.fxml.Initializable

/**
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 20-01-2023
 */

abstract class BaseController : Initializable {

    protected var mediator: ControllerMediator? = null
        private set

    abstract override fun initialize(url: URL, resources: ResourceBundle)

    fun setMediator(mediator: ControllerMediator?) {
        this.mediator = mediator
    }

    protected fun getLocalizedText(key: String): String = ResourceBundleUtil.getString(key)
}