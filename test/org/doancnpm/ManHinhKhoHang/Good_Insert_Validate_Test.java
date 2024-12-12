package org.doancnpm.ManHinhKhoHang;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.doancnpm.Models.DonViTinh;
import org.doancnpm.SetUp;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

class Good_Insert_Validate_Test extends ApplicationTest {

    private ThemMoiMatHangDialogController controller;

    @BeforeAll
    static void initFX() {
        SetUp.initFX();
    }


    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main/ManHinhKhoHang/ThemMoiMatHang.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
    }

    @BeforeEach
    void setUp() {
        // Đảm bảo các thành phần giao diện đã được khởi tạo
        assertNotNull(controller.tenMHTextField, "Tên mặt hàng TextField không được khởi tạo");
        assertNotNull(controller.dvtComboBox, "Đơn vị tính ComboBox không được khởi tạo");
        assertNotNull(controller.donGiaNhapTextField, "Đơn giá nhập TextField không được khởi tạo");
    }

    @Test
    void testValidateDataWithTestCases() {
        Platform.runLater(() -> {
            // Test Case 1: Tên mặt hàng trống
            controller.tenMHTextField.setText(""); // Tên mặt hàng trống
            controller.dvtComboBox.setValue(new DonViTinh());
            controller.donGiaNhapTextField.setText("7000");
            String result = controller.getValidData();
            assertTrue(result.contains("Tên mặt hàng không được để trống"), "Test Case 1 thất bại");

            // Test Case 2: Đơn vị tính trống
            controller.tenMHTextField.setText("Sữa Milo"); // Tên mặt hàng hợp lệ
            controller.dvtComboBox.setValue(null); // Đơn vị tính trống
            controller.donGiaNhapTextField.setText("7000");
            result = controller.getValidData();
            assertTrue(result.contains("Đơn vị tính không được để trống"), "Test Case 2 thất bại");

            // Test Case 3: Đơn giá nhập trống
            controller.tenMHTextField.setText("Sữa Milo");
            controller.dvtComboBox.setValue(new DonViTinh());
            controller.donGiaNhapTextField.setText(""); // Đơn giá nhập trống
            result = controller.getValidData();
            assertTrue(result.contains("Đơn giá nhập không được để trống"), "Test Case 3 thất bại");

            // Test Case 4: Tất cả thông tin hợp lệ
            controller.tenMHTextField.setText("Sữa Milo");
            controller.dvtComboBox.setValue(new DonViTinh());
            controller.donGiaNhapTextField.setText("7000");
            result = controller.getValidData();
            assertFalse(result.contains("Tên mặt hàng không được để trống"));
            assertFalse(result.contains("Đơn vị tính không được để trống"));
            assertFalse(result.contains("Đơn giá nhập không được để trống"));
        });
    }
}
