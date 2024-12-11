package org.doancnpm.Login;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.doancnpm.Models.NhanVien;
import org.doancnpm.Models.TaiKhoan;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

class ChangePassword_Validate extends ApplicationTest {
    private LoginController controller;
    Connection connection;

    @BeforeAll
    static void initFX() {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(latch::countDown);
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login/LoginUI.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
    }
    @BeforeEach
    void setUp() throws SQLException {
//        connection = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=QUANLYDAILY;user=sa;password=123456;");
//        NhanVien nv = new NhanVien();
//        TaiKhoan acc = new TaiKhoan();

    }

    @Test
    void UTC001 (){
        Platform.runLater(()-> {
            controller.enterPasswordText.setText("null");
            controller.reEnterPasswordText.setText("123456");
            Boolean bl = controller.validatePassword();
            assertFalse(bl);
        });
    }

    @Test
    void UTC002 (){
        Platform.runLater(()-> {
            controller.enterPasswordText.setText("passpass123");
            controller.reEnterPasswordText.setText("");
            Boolean bl = controller.validatePassword();
            assertFalse(bl);
        });
    }

    @Test
    void UTC003 (){
        Platform.runLater(()-> {
            controller.enterPasswordText.setText("passpass123");
            controller.reEnterPasswordText.setText("123456");
            Boolean bl = controller.validatePassword();
            assertFalse(bl);
        });
    }


    @Test
    void UTC004 (){
        Platform.runLater(()-> {
            controller.enterPasswordText.setText("123456");
            controller.reEnterPasswordText.setText("123456");
            Boolean bl = controller.validatePassword();
            assertFalse(bl);
        });
    }


    @Test
    void UTC005 (){
        Platform.runLater(()-> {
            controller.enterPasswordText.setText("passpass123");
            controller.reEnterPasswordText.setText("passpass123");
            Boolean bl = controller.validatePassword();
            assertTrue(bl);
        });
    }

}