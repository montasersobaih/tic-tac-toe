package com.mj.tic.tac.toe.javafx.kotlin.constant

/**
 * Central repository for all resource directory path constants.
 *
 * This internal object defines every resource path used across the
 * application, preventing hardcoded path duplication and making
 * resource layout changes straightforward. Paths are organized into
 * two categories:
 * - **Interfaces:** FXML layout files for the UI and dialogs.
 * - **Assist:** CSS stylesheet files for controls and layouts.
 *
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 15-10-2022
 */

internal object ResourcePath {

    //==================================================={Interfaces}===================================================
    /** Root directory for all FXML layout files. */
    const val INTERFACE = "/interface/"

    /** Subdirectory for dialog FXML files. */
    const val DIALOG = "${INTERFACE}dialog/"

    /** Subdirectory for layout panel FXML files. */
    const val LAYOUT = "${INTERFACE}layout/"

    //====================================================={Assist}=====================================================
    /** Root directory for all asset files. */
    private const val ASSETS = "/assets/"

    /** Root directory for all CSS stylesheet files. */
    const val STYLE_SHEET = "${ASSETS}css/"

    /** Subdirectory for CSS files targeting reusable controls. */
    const val STYLE_CONTROL = "${STYLE_SHEET}control/"

    /** Subdirectory for CSS files targeting interface layouts. */
    const val STYLE_USER_INTERFACE = "${STYLE_SHEET}interface/"

    /** Subdirectory for CSS files targeting dialog interfaces. */
    const val STYLE_DIALOG_INTERFACE = "${STYLE_USER_INTERFACE}dialog/"
}