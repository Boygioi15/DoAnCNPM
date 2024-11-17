package org.doancnpm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PopupControl;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.doancnpm.DAO.NhanVienDAO;
import org.doancnpm.ManHinhBaoCao.ManHinhBaoCaoController;
import org.doancnpm.ManHinhDieuKhien.ManHinhDieuKhienController;
import org.doancnpm.Models.DatabaseDriver;

import java.io.IOException;
import java.sql.SQLException;

public class AppStart extends Application {
    @Override
    public void start(Stage stage) throws IOException, SQLException {
        Scene scene = new Scene(new StackPane());

        NavController loginManager = new NavController(scene,stage);
        loginManager.showLoginScreen();

        DatabaseDriver.initializeDatabase(DatabaseDriver.getConnect());

     System.out.println(  NhanVienDAO.getInstance().QueryAll());

        stage.setTitle("Quản lý đại lý");
        stage.getIcons().add(new Image(getClass().getResource("/image/deal.png").toExternalForm()));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}