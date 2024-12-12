package org.doancnpm.ManHinhNhanVien;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.doancnpm.Models.ChucVu;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class Employee_Insert_Validate_Test extends ApplicationTest {

    TiepNhanNhanVienDialogController controller;

    @BeforeAll
    static void initFX() {
        // Khởi động JavaFX một lần
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main/ManHinhNhanVien/TiepNhanNhanVien2.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
    }

    @BeforeEach
    void setUp() {
        // Đảm bảo các thành phần giao diện đã được khởi tạo
        assertNotNull(controller.chucVuComboBox, "Chức vụ ComboBox không được khởi tạo");
        assertNotNull(controller.gioiTinhComboBox, "Giới tính ComboBox không được khởi tạo");
        assertNotNull(controller.emailTextField, "Email TextField không được khởi tạo");
        assertNotNull(controller.luongTextField, "Lương TextField không được khởi tạo");
    }

    @Test
    void testGetValidateData() {
        Platform.runLater(() -> {
            // Test Case 1: Chức vụ null
            controller.chucVuComboBox.setValue(null); // Chức vụ trống
            controller.gioiTinhComboBox.setValue("Nam");
            controller.emailTextField.setText("nhanvien@gmail.com");
            controller.luongTextField.setText("5000000");
            String result = controller.GetValid();
            assertTrue(result.contains("Chức vụ không được để trống"), "Test Case 1 thất bại");

            // Test Case 2: Giới tính null
            controller.chucVuComboBox.setValue(new ChucVu());
            controller.gioiTinhComboBox.setValue(null); // Giới tính trống
            result = controller.GetValid();
            assertTrue(result.contains("Giới tính không được để trống"), "Test Case 2 thất bại");

            // Test Case 3: Email sai định dạng
            controller.gioiTinhComboBox.setValue("Nam");
            controller.emailTextField.setText("nhanvien.com"); // Email sai định dạng
            result = controller.GetValid();
            assertTrue(result.contains("Email không đúng định dạng"), "Test Case 3 thất bại");

            // Test Case 4: Email null
            controller.emailTextField.setText(""); // Email trống
            result = controller.GetValid();
            assertTrue(result.contains("Email không được để trống"), "Test Case 4 thất bại");

            // Test Case 5: Lương null
            controller.emailTextField.setText("nhanvien@gmail.com");
            controller.luongTextField.setText(""); // Lương trống
            result = controller.GetValid();
            assertTrue(result.contains("Lương không được để trống"), "Test Case 5 thất bại");

            // Test Case 6: Tất cả hợp lệ
            controller.chucVuComboBox.setValue(new ChucVu());
            controller.gioiTinhComboBox.setValue("Nam");
            controller.emailTextField.setText("nhanvien@gmail.com");
            controller.luongTextField.setText("5000000");
            result = controller.GetValid();
            assertFalse(result.contains("Chức vụ không được để trống"));
            assertFalse(result.contains("Giới tính không được để trống"));
            assertFalse(result.contains("Email không đúng định dạng"));
            assertFalse(result.contains("Email không được để trống"));
            assertFalse(result.contains("Lương không được để trống"));


            // Test Case 7: Email đã tồn tại
            // Giả lập email đã tồn tại trong hệ thống
            controller.chucVuComboBox.setValue(new ChucVu());
            controller.gioiTinhComboBox.setValue("Nam");
            controller.emailTextField.setText("daTonTai@gmail.com"); // Email đã tồn tại
            controller.luongTextField.setText("5000000");

            result = controller.GetValid();
            assertTrue(result.contains("Email đã tồn tại"), "Test Case 7 thất bại");
        });
    }
}
