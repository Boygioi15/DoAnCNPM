package org.doancnpm.ManHinhQuyDinh;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.doancnpm.DAO.ThamSoDAO;
import org.doancnpm.SetUp;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

class ChangeParam_capNhatDLToida extends ApplicationTest {
    private ManHinhQuyDinhController controller;
    Connection connection;

    @BeforeAll
    static void initFX() {
        SetUp.initFX();
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
        controller.slDLToiDaTextField = new TextField();
        controller.slDLCommitButton = new Button();
    }

    @Test
    void UTC01() throws SQLException {
        Platform.runLater(() -> {
            controller.slDLToiDaTextField.setText("2");

            controller.capNhatDLToiDa();

            double updatedValue = 0;
            try {
                updatedValue = ThamSoDAO.getInstance().GetDLToiDaMoiQuan();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            assertEquals(2, updatedValue, "So dai ly được cập nhật không khớp");

        });

    }

    @Test
    void UTC02() throws SQLException {
        Platform.runLater(() -> {
            controller.slDLToiDaTextField.setText("-1");

            controller.capNhatDLToiDa();

            double updatedValue = 0;
            try {
                updatedValue = ThamSoDAO.getInstance().GetDLToiDaMoiQuan();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            assertNotEquals(controller.slDLToiDaTextField.getText(), updatedValue);

        });

    }

    @Test
    void UTC03() throws SQLException {
        Platform.runLater(() -> {
            controller.slDLToiDaTextField.setText("absădac");

            controller.capNhatDLToiDa();

            double updatedValue = 0;
            try {
                updatedValue = ThamSoDAO.getInstance().GetDLToiDaMoiQuan();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            assertNotEquals(controller.slDLToiDaTextField.getText(), updatedValue);

        });
    }

    @Test
    void UTC04() throws SQLException {
        Platform.runLater(() -> {
            controller.slDLToiDaTextField.setText("");

            controller.capNhatDLToiDa();

            double updatedValue = 0;
            try {
                updatedValue = ThamSoDAO.getInstance().GetDLToiDaMoiQuan();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            assertNotEquals(controller.slDLToiDaTextField.getText(), updatedValue);
        });


    }

}