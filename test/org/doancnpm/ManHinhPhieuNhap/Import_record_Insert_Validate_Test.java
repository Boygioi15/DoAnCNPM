package org.doancnpm.ManHinhPhieuNhap;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.doancnpm.SetUp;
import org.doancnpm.Ultilities.ChiTietPhieu.ChiTietPhieuNhapRow;
import org.doancnpm.Models.MatHang;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

class Import_record_Insert_Validate_Test extends ApplicationTest {

    private ChiTietPhieuNhapRow chiTietPhieuNhapRow;
    private LapPhieuNhapDialogController controller;
    @BeforeAll
    static void initFX() {
        SetUp.initFX();
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Gọi FXMLLoader để load giao diện FXML của LapPhieuNhapDialogController
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main/ManHinhPhieuNhap/LapPhieuNhap.fxml"));
        Parent root = loader.load();

        // Lấy controller từ FXML đã load
        controller = loader.getController();

        // Tiếp tục với phần container cũ cho việc kiểm tra ChiTietPhieuNhapRow
        VBox container = new VBox();
        chiTietPhieuNhapRow = new ChiTietPhieuNhapRow(container); // Tạo instance của ChiTietPhieuNhapRow

        // Đặt container làm root của Scene
        Scene scene = new Scene(container, 600, 400);
        stage.setScene(scene);
    }


    @BeforeEach
    void setUp() {
        assertNotNull(controller.nccTextField_add, "TextField không được khởi tạo");
        assertNotNull(chiTietPhieuNhapRow.mhComboBox, "Mặt hàng ComboBox không được khởi tạo");
        assertNotNull(chiTietPhieuNhapRow.slTextField, "Số lượng TextField không được khởi tạo");
        assertNotNull(chiTietPhieuNhapRow.dvtTextField, "Đơn vị tính TextField không được khởi tạo");
        assertNotNull(chiTietPhieuNhapRow.thanhTienTextField, "Thành tiền TextField không được khởi tạo");
    }

    @Test
    void testGetValidWithTestCases() {
        Platform.runLater(() -> {
            // Clear the container to ensure no duplicates
            controller.ctpnContainer.getChildren().clear();

            UTCID01();
            UTCID02();
            UTCID03();
            UTCID04();
            UTCID05();
        });
    }

    private void UTCID01() {
        // Test Case 1: Nhà cung cấp trống
        controller.nccTextField_add.setText("");
        chiTietPhieuNhapRow.mhComboBox.setValue(null); // Mặt hàng trống
        chiTietPhieuNhapRow.slTextField.setText("5");

        // Clear the container before adding again
        controller.ctpnContainer.getChildren().clear();
        controller.ctpnContainer.getChildren().add(chiTietPhieuNhapRow);

        String result = controller.GetValid();
        assertTrue(result.contains("Nhà cung cấp không được để trống"), "Test Case 1 thất bại");
    }

    private void UTCID02() {
        // Test Case 2: Mặt hàng trống
        controller.nccTextField_add.setText("Nhà cung cấp Thủ Đức");
        chiTietPhieuNhapRow.mhComboBox.setValue(null); // Mặt hàng trống
        chiTietPhieuNhapRow.slTextField.setText("5");

        // Clear the container before adding again
        controller.ctpnContainer.getChildren().clear();
        controller.ctpnContainer.getChildren().add(chiTietPhieuNhapRow);

        String result = controller.GetValid();
        assertTrue(result.contains("Mặt hàng không được để trống"), "Test Case 2 thất bại");
    }

    private void UTCID03() {
        // Test Case 3: Số lượng trống
        controller.nccTextField_add.setText("Nhà cung cấp Thủ Đức");
        MatHang matHang1 = new MatHang("MH001", "Sữa Milo", 7000L);
        matHang1.setMaDVT(1); // Thiết lập MaDVT cho MatHang
        chiTietPhieuNhapRow.mhComboBox.setValue(matHang1);
        chiTietPhieuNhapRow.slTextField.setText("");

        // Clear the container before adding again
        controller.ctpnContainer.getChildren().clear();
        controller.ctpnContainer.getChildren().add(chiTietPhieuNhapRow);

        String result = controller.GetValid();

        assertTrue(result.contains("Số lượng không được để trống"), "Test Case 3 thất bại");
    }

    private void UTCID04() {
        // Test Case 4: Định dạng số lượng sai
        controller.nccTextField_add.setText("Nhà cung cấp Thủ Đức");
        MatHang matHang2 = new MatHang("MH002", "Bánh Oreo", 5000L);
        matHang2.setMaDVT(2); // Thiết lập MaDVT cho MatHang
        chiTietPhieuNhapRow.mhComboBox.setValue(matHang2);
        chiTietPhieuNhapRow.slTextField.setText("abc"); // Định dạng sai

        // Clear the container before adding again
        controller.ctpnContainer.getChildren().clear();
        controller.ctpnContainer.getChildren().add(chiTietPhieuNhapRow);

        String result = controller.GetValid();
        assertTrue(result.contains("Định dạng số lượng sai"), "Test Case 4 thất bại");
    }

    private void UTCID05() {
        // Test Case 5: Tất cả hợp lệ
        controller.nccTextField_add.setText("Nhà cung cấp Thủ Đức");
        MatHang matHang3 = new MatHang("MH003", "Nước ngọt", 10000L);
        matHang3.setMaDVT(3); // Thiết lập MaDVT cho MatHang
        chiTietPhieuNhapRow.mhComboBox.setValue(matHang3);
        chiTietPhieuNhapRow.slTextField.setText("10");

        // Clear the container before adding again
        controller.ctpnContainer.getChildren().clear();
        controller.ctpnContainer.getChildren().add(chiTietPhieuNhapRow);

        String result = controller.GetValid();
        assertFalse(result.contains("Mặt hàng không được để trống"));
        assertFalse(result.contains("Số lượng không được để trống"));
        assertFalse(result.contains("Định dạng số lượng sai"));
    }
}
