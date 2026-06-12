/**
 * <p><b>Project:</b> tic-tac-toe</p>
 *
 * @author Montaser Jamal
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @created 12-06-2026
 * @since 1.0.0
 */

module com.mj.tic.tac.toe.javafx.kotlin {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;

    requires com.jfoenix;

    opens com.mj.tic.tac.toe.javafx.kotlin to javafx.graphics;
    opens com.mj.tic.tac.toe.javafx.kotlin.util to javafx.fxml;
    opens com.mj.tic.tac.toe.javafx.kotlin.dialog to javafx.fxml;
    opens com.mj.tic.tac.toe.javafx.kotlin.controller to javafx.fxml;
    opens com.mj.tic.tac.toe.javafx.kotlin.controller.view to javafx.fxml;
    opens com.mj.tic.tac.toe.javafx.kotlin.controller.layout to javafx.fxml;
}