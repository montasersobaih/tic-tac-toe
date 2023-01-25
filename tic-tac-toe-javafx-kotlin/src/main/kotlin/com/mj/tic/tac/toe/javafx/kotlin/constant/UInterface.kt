package com.mj.tic.tac.toe.javafx.kotlin.constant

import com.mj.tic.tac.toe.javafx.kotlin.util.FXInterface

/**
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 15-10-2022
 */

enum class UInterface(private val value: String) : FXInterface {

    APPLICATION_PAGE("${ResourcePath.INTERFACE}view.fxml");

    override fun toString(): String = value
}