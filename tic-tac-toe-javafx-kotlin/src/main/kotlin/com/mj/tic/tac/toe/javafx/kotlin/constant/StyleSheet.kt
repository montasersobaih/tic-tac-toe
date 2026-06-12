package com.mj.tic.tac.toe.javafx.kotlin.constant

/**
 * Enum mapping logical style names to their CSS file paths.
 *
 * Each constant represents a CSS stylesheet used by the application.
 * The [toString] method returns the full classpath resource path,
 * which can be passed directly to JavaFX's
 * [javafx.scene.Scene.getStylesheets] or
 * [javafx.scene.control.Control.getStylesheets] for application.
 *
 * @property value The full CSS file path for this stylesheet.
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 15-10-2022
 */

enum class StyleSheet(private val value: String) {

    /** Global CSS custom property definitions (colors, fonts, spacing). */
    GLOBAL_COLORS("${ResourcePath.STYLE_SHEET}global-config.css"),

    /** Button styles including hover, pressed, disabled, and winner pseudo-class. */
    BUTTON("${ResourcePath.STYLE_CONTROL}button.css"),

    /** Base dialog window styles. */
    DIALOG("${ResourcePath.STYLE_CONTROL}dialog.css"),

    /** Label typography and colour styles. */
    LABEL("${ResourcePath.STYLE_CONTROL}label.css"),

    /** Dialog variant with additional padding. */
    PADDED_DIALOG("${ResourcePath.STYLE_CONTROL}padded-dialog.css"),

    /** Pane background and layout styles. */
    PANE("${ResourcePath.STYLE_CONTROL}pane.css"),

    /** Custom scroll bar appearance styles. */
    SCROLL_BAR("${ResourcePath.STYLE_CONTROL}scroll-bar.css"),

    /** Text field input styles. */
    TEXT_FIELD("${ResourcePath.STYLE_CONTROL}text-field.css");

    /**
     * Returns the full CSS file path for this stylesheet.
     *
     * @return The CSS path string for use with JavaFX stylesheet APIs.
     */
    override fun toString(): String = value
}