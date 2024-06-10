package org.doancnpm.ManHinhPhieuNhap;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.doancnpm.DAO.DaiLyDAO;
import org.doancnpm.DAO.MatHangDAO;
import org.doancnpm.DAO.NhanVienDAO;
import org.doancnpm.Models.*;
import org.doancnpm.Ultilities.CheckExist;
import org.doancnpm.Ultilities.ChiTietPhieu.ChiTietPhieuNhapRow;
import org.doancnpm.Ultilities.DayFormat;
import org.doancnpm.Ultilities.PopDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class LapPhieuNhapDialogController implements Initializable {
    @FXML
    private MFXTextField ngayLapPhieuTextField;
    @FXML
    private MFXTextField nhanVienTextField;
    @FXML
    private TextField nccTextField;
    @FXML
    Text tongTienText;

    @FXML
    private TextArea ghiChuTextArea;

    @FXML
    Button loadExcelBtn;
    @FXML
    Button xuatExcelBtn;

    @FXML
    Button themCTPNButton;
    @FXML
    VBox ctpnContainer;

    NhanVien nvLapPhieu = null;
    PhieuNhap phieuNhapGoc;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        themCTPNButton.setOnAction(ob -> {
            themCTPN();
        });
        loadExcelBtn.setOnAction(ob -> {
            try {
                OpenImportDialog();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        themCTPN();
    }

    public void themCTPN() {
        ChiTietPhieuNhapRow temp = new ChiTietPhieuNhapRow(ctpnContainer);
        ctpnContainer.getChildren().add(ctpnContainer.getChildren().size() - 2, temp);
        temp.SetOnXoa(event -> {
            if (ctpnContainer.getChildren().size() <= 3) {
                event.consume();
            } else {
                ctpnContainer.getChildren().remove(temp);
            }
        });
        temp.thanhTienTextField.textProperty().addListener(ob -> {
            capNhatTongTien();
        });
    }

    private void capNhatTongTien() {
        double tt = 0;
        for (int i = 0; i < ctpnContainer.getChildren().size(); i++) {
            if (ctpnContainer.getChildren().get(i) instanceof ChiTietPhieuNhapRow temp) {
                tt += temp.getThanhTienDouble();
            }
        }
        tongTienText.setText(Double.toString(tt));
    }

    public void setInitialValue(PhieuNhap phieuNhap, NhanVien nvLoggedIn) {
        if (phieuNhap == null) {
            nhanVienTextField.setText(nvLoggedIn.getHoTen());
            this.nvLapPhieu = nvLoggedIn;

            Date ngayTiepNhan = new Date(System.currentTimeMillis());
            ngayLapPhieuTextField.setText(DayFormat.GetDayStringFormatted(ngayTiepNhan));
            return;
        }
        phieuNhapGoc = phieuNhap;
        ngayLapPhieuTextField.setText(DayFormat.GetDayStringFormatted(phieuNhap.getNgayLapPhieu()));
        try {
            NhanVien nv = NhanVienDAO.getInstance().QueryID(phieuNhap.getMaNhanVien());
            nhanVienTextField.setText(nv.getHoTen());

        } catch (SQLException _) {
        }

        nccTextField.setText(phieuNhap.getNhaCungCap());
        ghiChuTextArea.setText(phieuNhap.getGhiChu());


        ngayLapPhieuTextField.setDisable(true);
        nhanVienTextField.setDisable(true);
        nccTextField.setDisable(true);
    }

    public PhieuNhap getPhieuNhap() {
        if (phieuNhapGoc == null) {
            Date ngayTiepNhan = new Date(System.currentTimeMillis());
            PhieuNhap phieuNhap = new PhieuNhap();

            phieuNhap.setMaNhanVien(nvLapPhieu.getID());
            phieuNhap.setNhaCungCap(nccTextField.getText());
            phieuNhap.setNgayLapPhieu(ngayTiepNhan);
            phieuNhap.setGhiChu(ghiChuTextArea.getText());
            return phieuNhap;
        } else {
            phieuNhapGoc.setGhiChu(ghiChuTextArea.getText());
            return phieuNhapGoc;
        }
    }

    public List<ChiTietPhieuNhap> getChiTietPhieuNhap() {
        List<ChiTietPhieuNhap> chiTietPhieuNhapList = new ArrayList<>();
        for (int i = 0; i < ctpnContainer.getChildren().size(); i++) {
            if (ctpnContainer.getChildren().get(i) instanceof ChiTietPhieuNhapRow temp) {
                chiTietPhieuNhapList.add(temp.getChiTietPhieuNhap());
            }
        }
        return chiTietPhieuNhapList;
    }

    public void OpenImportDialog() throws SQLException {
        // Hiển thị hộp thoại chọn tệp Excel
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx", "*.xls")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        // Kiểm tra nếu người dùng đã chọn một tệp Excel
        if (selectedFile != null) {
            // Gọi hàm importFromExcel và truyền đường dẫn tệp Excel đã chọn
            importFromExcel(selectedFile.getAbsolutePath());
        }
    }

    public void themCTPNExcel(ChiTietPhieuNhap chiTietPhieuNhap) {
        ChiTietPhieuNhapRow temp = new ChiTietPhieuNhapRow(ctpnContainer);
        temp.loadFromChiTietPhieuNhap(chiTietPhieuNhap);
        ctpnContainer.getChildren().add(ctpnContainer.getChildren().size() - 2, temp);
        temp.SetOnXoa(event -> {
            if (ctpnContainer.getChildren().size() <= 3) {
                event.consume();
            } else {
                ctpnContainer.getChildren().remove(temp);
            }
        });
    }

    private void importFromExcel(String filePath) {
        File file = new File(filePath);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            PopDialog.popErrorDialog("Không tìm thấy file excel");
            return;
        }

        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(fis);
        } catch (IOException e) {
            PopDialog.popErrorDialog("Có lỗi trong quá trình thực hiện", e.getMessage());
            return;
        }
        XSSFSheet sheet = workbook.getSheetAt(0); // Giả sử dữ liệu ở sheet đầu tiên

        boolean hasError = true; // Biến để theo dõi nếu có lỗi xảy ra

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) { // Kiểm tra xem dòng có tồn tại hay không
                Cell matHangCell = row.getCell(0);
                Cell soLuongCell = row.getCell(1);

                ChiTietPhieuNhap chiTietPhieuNhap = new ChiTietPhieuNhap();
                String maMatHang = matHangCell.getStringCellValue().trim();
                String numericalPart = null;
                int maMatHangID = 0;
                try {
                    if (!CheckExist.checkMatHang(maMatHang)) {
                        PopDialog.popErrorDialog("Không tồn tại mặt hàng " + maMatHang);
                        continue;
                    } else {
                        hasError = false;
                        // Tách phần số từ maMatHang
                        numericalPart = maMatHang.replaceAll("[^0-9]", "");
                        maMatHangID = Integer.parseInt(numericalPart);
                        chiTietPhieuNhap.setMaMatHang(maMatHangID);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                Integer sl = (int) soLuongCell.getNumericCellValue();
                chiTietPhieuNhap.setSoLuong(sl);
                MatHang matHang;
                try {
                    matHang = MatHangDAO.getInstance().QueryID(maMatHangID);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                try {
                    double thanhTien = sl * matHang.getDonGiaNhap();
                    chiTietPhieuNhap.setThanhTien(thanhTien);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                themCTPNExcel(chiTietPhieuNhap);
            }
        }
        capNhatTongTien();
        // Xóa hàng đầu tiên sau khi nhập
        if (!hasError) {
            ctpnContainer.getChildren().remove(0);
        }

        try {
            workbook.close();
            fis.close();
            if (!hasError) { // Chỉ hiển thị dialog thành công nếu không có lỗi nào
                PopDialog.popSuccessDialog("Thêm danh sách mặt hàng từ file excel thành công");
            }
            else{
                PopDialog.popErrorDialog("Thêm danh sách mặt hàng từ file excel không thành công");
            }
        } catch (IOException e) {
            PopDialog.popErrorDialog("Có lỗi trong quá trình thực hiện", e.getMessage());
        }
    }
}
