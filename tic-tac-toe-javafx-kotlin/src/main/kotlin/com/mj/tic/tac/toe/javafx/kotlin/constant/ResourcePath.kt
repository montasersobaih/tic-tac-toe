package com.mj.tic.tac.toe.javafx.kotlin.constant

/**
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 15-10-2022
 */

internal object ResourcePath {

    //==================================================={Interfaces}===================================================
    const val INTERFACE = "/interface/"

    const val DIALOG = "${INTERFACE}dialog/"

    const val LAYOUT = "${INTERFACE}layout/"

    //====================================================={Assist}=====================================================
    private const val ASSETS = "/assets/"

    const val STYLE_SHEET = "${ASSETS}css/"

    const val STYLE_CONTROL = "${STYLE_SHEET}control/"

    const val STYLE_USER_INTERFACE = "${STYLE_SHEET}interface/"

    const val STYLE_DIALOG_INTERFACE = "${STYLE_USER_INTERFACE}dialog/"
}