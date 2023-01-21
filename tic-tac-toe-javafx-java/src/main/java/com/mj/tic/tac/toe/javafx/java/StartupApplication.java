package com.mj.tic.tac.toe.javafx.java;

import com.mj.tic.tac.toe.javafx.java.constant.UInterface;
import com.mj.tic.tac.toe.javafx.java.util.FXMLUtil;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 20-01-2023
 */

public class StartupApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Pane pane = FXMLUtil.loadInterface(UInterface.APPLICATION_PAGE);

        Scene scene = new Scene(pane, 600, 400);
        scene.setFill(Color.TRANSPARENT);

        stage.setTitle("Tic Tac Toe");
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
