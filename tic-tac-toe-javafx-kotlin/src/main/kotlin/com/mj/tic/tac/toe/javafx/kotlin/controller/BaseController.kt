package com.mj.tic.tac.toe.javafx.kotlin.controller

import com.mj.tic.tac.toe.javafx.kotlin.util.ResourceBundleUtil
import javafx.fxml.Initializable
import java.net.URL
import java.util.ResourceBundle
import java.util.concurrent.Executors

/**
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 20-01-2023
 */

abstract class BaseController : Initializable {

    protected var count: Byte = 0

    protected val turn: Byte
        get() = (count % 2).toByte()

    abstract override fun initialize(url: URL, resources: ResourceBundle)

    protected fun getString(key: String): String = ResourceBundleUtil.getString(key)

    companion object {

        @JvmStatic
        protected val executor = Executors.newSingleThreadExecutor()

        @JvmStatic
        protected val matrix = Array(3) { ByteArray(3) }
    }
}