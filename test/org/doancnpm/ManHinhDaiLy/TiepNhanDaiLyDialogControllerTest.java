package org.doancnpm.ManHinhDaiLy;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.doancnpm.Models.DaiLy;
import org.doancnpm.Models.LoaiDaiLy;
import org.doancnpm.Models.Quan;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

class TiepNhanDaiLyDialogControllerTest extends ApplicationTest {

    private TiepNhanDaiLyDialogController controller;

    @BeforeAll
    static void initFX() {
        // Khởi động JavaFX chỉ một lần
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main/ManHinhDaiLy/TiepNhanDaiLyUI2.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
    }

    @BeforeEach
    void setUp() {
        // Đảm bảo các thành phần giao diện đã được khởi tạo
        assertNotNull(controller.quanComboBox, "Quận ComboBox không được khởi tạo");
        assertNotNull(controller.loaiDaiLyComboBox, "Loại Đại Lý ComboBox không được khởi tạo");
        assertNotNull(controller.diaChiTextField, "Địa chỉ TextField không được khởi tạo");
        assertNotNull(controller.tenDaiLyTextField, "Tên Đại Lý TextField không được khởi tạo");
        assertNotNull(controller.emailTextField, "Email TextField không được khởi tạo");
        assertNotNull(controller.dienThoaiTextField, "Điện thoại TextField không được khởi tạo");
        assertNotNull(controller.ghiChuTextArea, "Ghi chú TextArea không được khởi tạo");
    }

    @Test
    void testGetValidateData() {
        Platform.runLater(() -> {
            // Test Case 1: Quận null
            controller.quanComboBox.setValue(null); // Quận trống
            controller.loaiDaiLyComboBox.setValue(new LoaiDaiLy());
            controller.diaChiTextField.setText("14 Tân Vạn");
            controller.tenDaiLyTextField.setText("Đại lý Văn An");
            controller.emailTextField.setText("vanan@gmail.com");
            controller.dienThoaiTextField.setText("123456789");
            String result = controller.getValidateData();
            assertTrue(result.contains("Quận không được để trống"), "Test Case 1 thất bại");

            // Test Case 2: Loại đại lý null
            controller.quanComboBox.setValue(new Quan());
            controller.loaiDaiLyComboBox.setValue(null); // Loại đại lý trống
            result = controller.getValidateData();
            assertTrue(result.contains("Loại đại lý không được để trống"), "Test Case 2 thất bại");

            // Test Case 3: Địa chỉ null
            controller.loaiDaiLyComboBox.setValue(new LoaiDaiLy());
            controller.diaChiTextField.setText(""); // Địa chỉ trống
            result = controller.getValidateData();
            assertTrue(result.contains("Địa chỉ không được để trống"), "Test Case 3 thất bại");

            // Test Case 4: Tên đại lý null
            controller.diaChiTextField.setText("14 Tân Vạn");
            controller.tenDaiLyTextField.setText(""); // Tên đại lý trống
            result = controller.getValidateData();
            assertTrue(result.contains("Tên đại lý không được để trống"), "Test Case 4 thất bại");

            // Test Case 5: Email không hợp lệ
            controller.tenDaiLyTextField.setText("Đại lý Văn An");
            controller.emailTextField.setText("invalid-email"); // Email sai định dạng
            result = controller.getValidateData();
            assertTrue(result.contains("Email không đúng định dạng"), "Test Case 5 thất bại");

            // Test Case 6: Email hợp lệ, tất cả hợp lệ
            controller.emailTextField.setText("vanan@gmail.com");
            result = controller.getValidateData();
            assertFalse(result.contains("Quận không được để trống"));
            assertFalse(result.contains("Loại đại lý không được để trống"));
            assertFalse(result.contains("Địa chỉ không được để trống"));
            assertFalse(result.contains("Tên đại lý không được để trống"));
            assertFalse(result.contains("Email không đúng định dạng"));
        });
    }
}
