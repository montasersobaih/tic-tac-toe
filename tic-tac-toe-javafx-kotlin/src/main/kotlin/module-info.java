/**
 * Module descriptor for the Tic Tac Toe JavaFX + Kotlin application.
 *
 * Declares the module's dependencies and opens the necessary packages
 * to JavaFX's FXML loader for reflective access (required for @FXML
 * field injection, controller instantiation, and event handler wiring).
 *
 * <p><b>Project:</b> tic-tac-toe</p>
 *
 * @author Montaser Jamal
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @created 12-06-2026
 * @since 1.0.0
 */

module com.mj.tic.tac.toe.javafx.kotlin {
    /* JavaFX module dependencies */
    requires javafx.controls;
    requires javafx.fxml;

    /* Kotlin standard library */
    requires kotlin.stdlib;

    requires com.jfoenix;

    /* Open packages to JavaFX for FXML reflective access */
    opens com.mj.tic.tac.toe.javafx.kotlin to javafx.graphics;
    opens com.mj.tic.tac.toe.javafx.kotlin.util to javafx.fxml;
    opens com.mj.tic.tac.toe.javafx.kotlin.dialog to javafx.fxml;
    opens com.mj.tic.tac.toe.javafx.kotlin.controller to javafx.fxml;
    opens com.mj.tic.tac.toe.javafx.kotlin.controller.view to javafx.fxml;
    opens com.mj.tic.tac.toe.javafx.kotlin.controller.layout to javafx.fxml;
}