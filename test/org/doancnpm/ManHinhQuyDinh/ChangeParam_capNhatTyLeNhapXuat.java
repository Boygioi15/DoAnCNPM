package org.doancnpm.ManHinhQuyDinh;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.doancnpm.DAO.ThamSoDAO;
import org.doancnpm.Ultilities.PopDialog;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationTest;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChangeParam_capNhatTyLeNhapXuat extends ApplicationTest {
    private ManHinhQuyDinhController controller;
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
    }
    @BeforeEach
    void setUp() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main/ManHinhQuyDinh/MainUI.fxml"));
        Parent root = loader.load();
        controller = loader.getController();

        // Kết nối cơ sở dữ liệu thật (nếu cần)
        connection = DriverManager.getConnection(
                "jdbc:sqlserver://localhost:1433;databaseName=QUANLYDAILY;user=sa;password=123456789;"
        );

        // Gắn các thành phần giao diện
        controller.tyleTextField = new TextField();
        controller.tyLeCommitButton = new Button();
    }

    @Test
    void UTC1() throws SQLException {
        controller.tyleTextField.setText("2");

        controller.capNhatTyLeNhapXuat();

        double updatedValue = ThamSoDAO.getInstance().GetTyLeDonGiaXuat();
        assertEquals(2, updatedValue, "Tỷ lệ được cập nhật không khớp");

        assertFalse(controller.tyLeCommitButton.isVisible());}

    @Test
    void UTC2() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                controller.tyleTextField.setText("-1");
                controller.capNhatTyLeNhapXuat();
                double updatedValue = ThamSoDAO.getInstance().GetTyLeDonGiaXuat();
                assertNotEquals(controller.tyleTextField.getText(), updatedValue, "Tỷ lệ được cập nhật không khớp");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    void UTC3() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                controller.tyleTextField.setText("abc");
                controller.capNhatTyLeNhapXuat();

                double updatedValue = ThamSoDAO.getInstance().GetTyLeDonGiaXuat();
                assertNotEquals(controller.tyleTextField.getText(), updatedValue, "Tỷ lệ được cập nhật không khớp");

            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    void UTC4() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                controller.tyleTextField.setText("");
                controller.capNhatTyLeNhapXuat();

                double updatedValue = ThamSoDAO.getInstance().GetTyLeDonGiaXuat();
                assertNotEquals(controller.tyleTextField.getText(), updatedValue, "Tỷ lệ được cập nhật không khớp");

            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

}