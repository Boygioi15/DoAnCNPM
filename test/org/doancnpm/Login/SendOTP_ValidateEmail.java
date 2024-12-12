package org.doancnpm.Login;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.doancnpm.DAO.NhanVienDAO;
import org.doancnpm.Models.NhanVien;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
class SendOTP_ValidateEmail extends ApplicationTest {
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
    void setUp() throws ParseException, SQLException {
        connection = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=QUANLYDAILY;user=sa;password=123456789;");
        Statement stmt = connection.createStatement();

        stmt.execute("DELETE FROM NhanVien WHERE email = 'existPhong@gmail.com';");
        stmt.execute("DELETE FROM NhanVien WHERE email = 'notExistPhong@gmail.com';");

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        java.sql.Date ngaySinh = new java.sql.Date(formatter.parse("13/04/2024").getTime());
        NhanVien nv = new NhanVien(1, false, 1000000L, "existPhong@gmail.com", ngaySinh, "Hoang phong", "nam", "0848761709", "cc");
        NhanVienDAO.getInstance().Insert(nv);
    }


    @Test
    void UTC001() {
        Platform.runLater(() -> {
            controller.emailText.setText("existPhong@gmail.com");
            boolean isValid = controller.validateEmailInput();
            assertTrue(isValid,"EMAIL OK");
        });
    }

    @Test
    void UTC002() {
        Platform.runLater(() -> {
            controller.emailText.setText("");
            boolean isValid = controller.validateEmailInput();
            assertFalse(isValid,"EMAIL TRONG");
        });
    }

    @Test
    void UTC003() {
        Platform.runLater(() -> {
            controller.emailText.setText("phong@gmail,com");
            boolean isValid = controller.validateEmailInput();
            assertFalse(isValid, "EMAIL SAI ĐINH DANG");
        });
    }

    @Test
    void UTC004() {
        Platform.runLater(() -> {
            controller.emailText.setText("notExistPhong@gmail.com");
            boolean isValid = controller.validateEmailInput();
            assertFalse(isValid, "EMAIL KHONG TON TAI");
        });
    }



}
