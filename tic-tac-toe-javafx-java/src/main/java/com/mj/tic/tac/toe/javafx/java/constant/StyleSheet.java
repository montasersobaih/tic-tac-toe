package com.mj.tic.tac.toe.javafx.java.constant;

/**
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 15-10-2022
 */

public enum StyleSheet {

    GLOBAL_COLORS(ResourcePath.STYLE_SHEET.concat("global-config.css")),

    BUTTON(ResourcePath.STYLE_CONTROL.concat("button.css")),
    DIALOG(ResourcePath.STYLE_CONTROL.concat("dialog.css")),
    LABEL(ResourcePath.STYLE_CONTROL.concat("label.css")),
    PADDED_DIALOG(ResourcePath.STYLE_CONTROL.concat("padded-dialog.css")),
    PADDED_PANE(ResourcePath.STYLE_CONTROL.concat("padded-pane.css")),
    PANE(ResourcePath.STYLE_CONTROL.concat("pane.css")),
    TEXT_FIELD(ResourcePath.STYLE_CONTROL.concat("text-field.css"));

    private final String value;

    StyleSheet(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
