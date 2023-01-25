module tic.tac.toe.javafx.java {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.jfoenix;
    requires lombok;

    opens com.mj.tic.tac.toe.javafx.java to javafx.graphics;
    opens com.mj.tic.tac.toe.javafx.java.dialog to javafx.fxml;
    opens com.mj.tic.tac.toe.javafx.java.controller to javafx.fxml;
    opens com.mj.tic.tac.toe.javafx.java.controller.view to javafx.fxml;
    opens com.mj.tic.tac.toe.javafx.java.controller.layout to javafx.fxml;
}