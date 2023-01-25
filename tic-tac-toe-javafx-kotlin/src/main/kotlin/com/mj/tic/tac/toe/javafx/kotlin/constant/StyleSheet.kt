package com.mj.tic.tac.toe.javafx.kotlin.constant

/**
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 15-10-2022
 */

enum class StyleSheet(private val value: String) {

    GLOBAL_COLORS("${ResourcePath.STYLE_SHEET}global-config.css"),
    BUTTON("${ResourcePath.STYLE_CONTROL}button.css"),
    DIALOG("${ResourcePath.STYLE_CONTROL}dialog.css"),
    LABEL("${ResourcePath.STYLE_CONTROL}label.css"),
    PADDED_DIALOG("${ResourcePath.STYLE_CONTROL}padded-dialog.css"),
    PANE("${ResourcePath.STYLE_CONTROL}pane.css"),
    SCROLL_BAR("${ResourcePath.STYLE_CONTROL}scroll-bar.css"),
    TEXT_FIELD("${ResourcePath.STYLE_CONTROL}text-field.css");

    override fun toString(): String = value
}