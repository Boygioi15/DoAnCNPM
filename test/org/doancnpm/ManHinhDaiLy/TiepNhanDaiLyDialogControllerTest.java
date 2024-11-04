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
        // Khởi động nền tảng JavaFX
        Platform.startup(() -> {});
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main/ManHinhDaiLy/TiepNhanDaiLyUI2.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
    }

    @BeforeEach
    void setUp() {
        // Đảm bảo các thành phần giao diện của controller đã được khởi tạo đúng cách qua FXML loader
        assertNotNull(controller.quanComboBox);
        assertNotNull(controller.loaiDaiLyComboBox);
        assertNotNull(controller.diaChiTextField);
        assertNotNull(controller.tenDaiLyTextField);
        assertNotNull(controller.emailTextField);
        assertNotNull(controller.dienThoaiTextField);
        assertNotNull(controller.ghiChuTextArea);
        assertNotNull(controller.title);  // Kiểm tra label 'title' có được khởi tạo không
    }

    @Test
    void testSetInitialValue() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                DaiLy daiLy = new DaiLy();
                daiLy.setMaQuan(1);
                daiLy.setMaLoaiDaiLy(1);
                daiLy.setTenDaiLy("Dai Ly Test");
                daiLy.setDiaChi("123 Test St");
                daiLy.setDienThoai("0909123456");
                daiLy.setEmail("test@example.com");
                daiLy.setGhiChu("Ghi chu test");

                controller.setInitialValue(daiLy);

                // Kiểm tra giá trị
                assertEquals("Dai Ly Test", controller.tenDaiLyTextField.getText());
                assertEquals("123 Test St", controller.diaChiTextField.getText());
                assertEquals("0909123456", controller.dienThoaiTextField.getText());
                assertEquals("test@example.com", controller.emailTextField.getText());
                assertEquals("Ghi chu test", controller.ghiChuTextArea.getText());
                if (controller.title != null) {
                    assertEquals("Cập nhật đại lý", controller.title.getText());
                }
            } finally {
                latch.countDown();
            }
        });

        latch.await();
    }

    @Test
    void testGetValidateData() {
        Platform.runLater(() -> {
            // Thiết lập các trường không hợp lệ để kiểm tra
            controller.quanComboBox.setValue(null);
            controller.loaiDaiLyComboBox.setValue(null); // Loại đại lý không hợp lệ
            controller.diaChiTextField.setText("123 Test St");
            controller.tenDaiLyTextField.setText("Dai Ly Test");

            // Kiểm tra lỗi khi quận không được để trống
            String result = controller.getValidateData();
            assertTrue(result.contains("Quận không được để trống"));
            //assertTrue(result.contains("Hợp lệ"));

            // Cập nhật quận và kiểm tra loại đại lý
            controller.quanComboBox.setValue(new Quan());
            controller.loaiDaiLyComboBox.setValue(null); // Loại đại lý không được để trống

            result = controller.getValidateData();
            assertTrue(result.contains("Loại đại lý không được để trống"));

            // Cập nhật loại đại lý và kiểm tra địa chỉ
            controller.loaiDaiLyComboBox.setValue(new LoaiDaiLy());
            controller.diaChiTextField.setText(""); // Địa chỉ không hợp lệ

            result = controller.getValidateData();
            assertTrue(result.contains("Địa chỉ không được để trống"));

            // Cập nhật địa chỉ và kiểm tra tên đại lý
            controller.diaChiTextField.setText("123 Test St");
            controller.tenDaiLyTextField.setText(""); // Tên đại lý không hợp lệ

            result = controller.getValidateData();
            assertTrue(result.contains("Tên đại lý không được để trống"));

            // Kiểm tra email không hợp lệ
            controller.tenDaiLyTextField.setText("Dai Ly Test");
            controller.emailTextField.setText("invalid-email"); // Email không hợp lệ

            result = controller.getValidateData();
            assertTrue(result.contains("Email không đúng định dạng"));

            // Kiểm tra trường hợp tất cả thông tin hợp lệ
            controller.emailTextField.setText("test@example.com");
            result = controller.getValidateData();
            assertFalse(result.contains("Quận không được để trống"));
            assertFalse(result.contains("Loại đại lý không được để trống"));
            assertFalse(result.contains("Địa chỉ không được để trống"));
            assertFalse(result.contains("Tên đại lý không được để trống"));
            assertFalse(result.contains("Email không đúng định dạng")); // Không có lỗi
        });
    }



    @Test
    void testIsValidEmailFormat() {
        assertTrue(controller.isValidEmailFormat("test@example.com"));
        //assertTrue(controller.isValidEmailFormat("test@example.com"));
        assertFalse(controller.isValidEmailFormat("plainaddress"));
        assertFalse(controller.isValidEmailFormat("missing-at-sign.com"));
        assertFalse(controller.isValidEmailFormat("name@domain@domain.com"));
    }
}
