package org.doancnpm;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.doancnpm.DAO.MatHangDao;
import org.doancnpm.Models.MatHang;
import org.doancnpm.main.DirectAddDialogController;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AppStart extends Application {
    @Override
    public void start(Stage stage) throws IOException {
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