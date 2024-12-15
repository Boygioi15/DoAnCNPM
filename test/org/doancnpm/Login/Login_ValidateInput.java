package org.doancnpm.Login;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.doancnpm.DAO.NhanVienDAO;
import org.doancnpm.Models.NhanVien;
import org.doancnpm.SetUp;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

class Login_ValidateInput extends ApplicationTest {
    private LoginController controller;
    Connection connection;
    @BeforeAll
    static void initFX() {
        SetUp.initFX();
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login/LoginUI.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
    }

    @BeforeEach
    void setUp() throws ParseException, SQLException {

    }


    @Test
    void UTC002() {
        Platform.runLater(() -> {
            controller.user.setText("");
            controller.password.setText("phong123466");
            boolean isValid = controller.validateInput();
            assertFalse(isValid);
        });
    }


    @Test
    void UTC003() {
        Platform.runLater(() -> {
            controller.user.setText("admin1");
            controller.password.setText("");
            boolean isValid = controller.validateInput();
            assertFalse(isValid);
        });
    }


    @Test
    void UTC001() {
        Platform.runLater(() -> {
            controller.user.setText("admin1");
            controller.password.setText("phong123466");
            boolean isValid = controller.validateInput();
            assertTrue(isValid);
        });
    }

}
