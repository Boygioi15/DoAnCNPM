package org.doancnpm.ManHinhPhieuXuat;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.doancnpm.Models.DaiLy;
import org.doancnpm.Models.MatHang;
import org.doancnpm.Ultilities.ChiTietPhieu.ChiTietPhieuXuatRow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

class Export_record_Insert_Validate_Test extends ApplicationTest {

    private ChiTietPhieuXuatRow chiTietPhieuXuatRow;
    private LapPhieuXuatDialogController controller;

    @Override
    public void start(Stage stage) throws Exception {
        // Gọi FXMLLoader để load giao diện FXML của LapPhieuXuatDialogController
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main/ManHinhPhieuXuat/LapPhieuXuat.fxml"));
        Parent root = loader.load();

        // Lấy controller từ FXML đã load
        controller = loader.getController();

        // Tiếp tục với phần container cũ cho việc kiểm tra ChiTietPhieuXuatRow
        VBox container = new VBox();
        chiTietPhieuXuatRow = new ChiTietPhieuXuatRow(container); // Tạo instance của ChiTietPhieuXuatRow

        // Đặt container làm root của Scene
        Scene scene = new Scene(container, 600, 400);
        stage.setScene(scene);
    }

    @BeforeEach
    void setUp() {
        assertNotNull(chiTietPhieuXuatRow.mhComboBox, "Mặt hàng ComboBox không được khởi tạo");
        assertNotNull(chiTietPhieuXuatRow.slTextField, "Số lượng TextField không được khởi tạo");
        assertNotNull(chiTietPhieuXuatRow.dvtTextField, "Đơn vị tính TextField không được khởi tạo");
        assertNotNull(chiTietPhieuXuatRow.thanhTienTextField, "Thành tiền TextField không được khởi tạo");
    }

    @Test
    void testGetValidWithTestCases() {
        Platform.runLater(() -> {
            // Clear the container to ensure no duplicates
            controller.ctpxContainer.getChildren().clear();

            UTCID01();
            UTCID02();
            UTCID03();
            UTCID04();
            UTCID05();
        });
    }

    private void UTCID01() {
        // Test Case 1: Đại lý trống
        controller.dlComboBox.setValue(null);
        chiTietPhieuXuatRow.mhComboBox.setValue(null);
        chiTietPhieuXuatRow.slTextField.setText("5");

        // Clear the container before adding again
        controller.ctpxContainer.getChildren().clear();
        controller.ctpxContainer.getChildren().add(chiTietPhieuXuatRow);

        String result = controller.GetValid();
        assertTrue(result.contains("Đại lý không được để trống"), "Test Case 1 thất bại");
    }

    private void UTCID02() {
        // Test Case 2: Mặt hàng trống
        controller.dlComboBox.setValue(new DaiLy());
        chiTietPhieuXuatRow.mhComboBox.setValue(null); // Mặt hàng trống
        chiTietPhieuXuatRow.slTextField.setText("5");

        // Clear the container before adding again
        controller.ctpxContainer.getChildren().clear();
        controller.ctpxContainer.getChildren().add(chiTietPhieuXuatRow);

        String result = controller.GetValid();
        assertTrue(result.contains("Mặt hàng không được để trống"), "Test Case 2 thất bại");
    }

    private void UTCID03() {
        // Test Case 3: Số lượng trống
        controller.dlComboBox.setValue(new DaiLy());
        MatHang matHang1 = new MatHang("MH001", "Sữa Milo", 7000L);
        matHang1.setMaDVT(1); // Thiết lập MaDVT cho MatHang
        chiTietPhieuXuatRow.mhComboBox.setValue(matHang1);
        chiTietPhieuXuatRow.slTextField.setText("");

        // Clear the container before adding again
        controller.ctpxContainer.getChildren().clear();
        controller.ctpxContainer.getChildren().add(chiTietPhieuXuatRow);

        String result = controller.GetValid();

        assertTrue(result.contains("Số lượng không được để trống"), "Test Case 3 thất bại");
    }

    private void UTCID04() {
        // Test Case 4: Định dạng số lượng sai
        controller.dlComboBox.setValue(new DaiLy() );
        MatHang matHang2 = new MatHang("MH002", "Bánh Oreo", 5000L);
        matHang2.setMaDVT(2); // Thiết lập MaDVT cho MatHang
        chiTietPhieuXuatRow.mhComboBox.setValue(matHang2);
        chiTietPhieuXuatRow.slTextField.setText("abc"); // Định dạng sai

        // Clear the container before adding again
        controller.ctpxContainer.getChildren().clear();
        controller.ctpxContainer.getChildren().add(chiTietPhieuXuatRow);

        String result = controller.GetValid();
        assertTrue(result.contains("Định dạng số lượng sai"), "Test Case 4 thất bại");
    }

    private void UTCID05() {
        // Test Case 5: Tất cả hợp lệ
        controller.dlComboBox.setValue(new DaiLy());
        MatHang matHang3 = new MatHang("MH003", "Nước ngọt", 10000L);
        matHang3.setMaDVT(3); // Thiết lập MaDVT cho MatHang
        chiTietPhieuXuatRow.mhComboBox.setValue(matHang3);
        chiTietPhieuXuatRow.slTextField.setText("10");

        // Clear the container before adding again
        controller.ctpxContainer.getChildren().clear();
        controller.ctpxContainer.getChildren().add(chiTietPhieuXuatRow);

        String result = controller.GetValid();
        assertFalse(result.contains("Mặt hàng không được để trống"));
        assertFalse(result.contains("Số lượng không được để trống"));
        assertFalse(result.contains("Định dạng số lượng sai"));
    }
}
