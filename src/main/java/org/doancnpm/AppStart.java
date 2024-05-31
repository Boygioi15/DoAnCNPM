package org.doancnpm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PopupControl;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.doancnpm.ManHinhBaoCao.ManHinhBaoCaoController;
import org.doancnpm.ManHinhDieuKhien.ManHinhDieuKhienController;

import java.io.IOException;

public class AppStart extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        /*Scene scene = new Scene(new StackPane());

        NavController loginManager = new NavController(scene);
        loginManager.showLoginScreen();

        stage.setTitle("Quản lý đại lý");
        stage.setScene(scene);
        stage.show();*/
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main/ManHinhBaoCao/MainUI.fxml"));
        Scene myScene = new Scene(loader.load());
        stage.setScene(myScene);
        stage.setTitle("Graph drawer");
        stage.show();

        ManHinhBaoCaoController openController = loader.getController();
        openController.setPrimaryStage(stage); // Inject primaryStage into OpenController
    }

    public static void main(String[] args) {
        launch();
    }
}