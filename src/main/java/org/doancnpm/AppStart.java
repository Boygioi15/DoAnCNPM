package org.doancnpm;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.PopupControl;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class AppStart extends Application {
    @Override
    public void start(Stage stage){
        Scene scene = new Scene(new StackPane());

        NavController loginManager = new NavController(scene);
        loginManager.showLoginScreen();

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}