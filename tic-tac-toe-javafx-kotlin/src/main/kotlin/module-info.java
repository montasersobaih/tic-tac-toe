module com.mj.tictactoejavafxkotlin {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;

    requires com.jfoenix;

    opens com.mj.tic.tac.toe.javafx.kotlin to javafx.fxml;
    opens com.mj.tic.tac.toe.javafx.kotlin.dialog to javafx.fxml;
    opens com.mj.tic.tac.toe.javafx.kotlin.controller.view to javafx.fxml;
    opens com.mj.tic.tac.toe.javafx.kotlin.controller.layout to javafx.fxml;

    exports com.mj.tic.tac.toe.javafx.kotlin;
}