package org.doancnpm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PopupControl;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;
import org.doancnpm.ManHinhBaoCao.ManHinhBaoCaoController;
import org.doancnpm.ManHinhDieuKhien.ManHinhDieuKhienController;

public class AppStart extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        Scene scene = new Scene(new StackPane());
        NavController loginManager = new NavController(scene, stage);
        loginManager.showLoginScreen();

        stage.setTitle("Quản lý đại lý");
        stage.getIcons().add(new Image(getClass().getResource("/image/deal.png").toExternalForm()));
        stage.setScene(scene);
        stage.show();
    }
    public void startTest(Stage stage) throws IOException {
        Mode.TestMode = true;
        start(stage);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch();
    }
}
