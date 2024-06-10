package org.doancnpm.ManHinhPhieuXuat;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.controlsfx.control.SearchableComboBox;
import org.doancnpm.DAO.DaiLyDAO;
import org.doancnpm.DAO.MatHangDAO;
import org.doancnpm.DAO.NhanVienDAO;
import org.doancnpm.Models.*;
import org.doancnpm.Ultilities.CheckExist;
import org.doancnpm.Ultilities.ChiTietPhieu.ChiTietPhieuNhapRow;
import org.doancnpm.Ultilities.ChiTietPhieu.ChiTietPhieuXuatRow;
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

public class LapPhieuXuatDialogController implements Initializable {
    @FXML private MFXTextField ngayLapPhieuTextField;
    @FXML private MFXTextField nhanVienTextField;
    @FXML private SearchableComboBox<DaiLy> dlComboBox;
    @FXML Text tongTienText;

    @FXML private TextArea ghiChuTextArea;

    @FXML Button loadExcelBtn;
    @FXML Button xuatExcelBtn;

    @FXML Button themCTPXButton;
    @FXML VBox ctpxContainer;

    NhanVien nvLapPhieu = null;
    PhieuXuat phieuXuatGoc;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        themCTPXButton.setOnAction(ob -> {
            themCTPX();
        });
        themCTPX();
        initDaiLyComboBox();
        loadExcelBtn.setOnAction(ob ->{
            try {
                OpenImportDialog();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
    private void initDaiLyComboBox() {
        try {
            ObservableList<DaiLy> daiLy = FXCollections.observableArrayList(DaiLyDAO.getInstance().QueryAll());

            // Sử dụng StringConverter để hiển thị dữ liệu trong ComboBox
            StringConverter<DaiLy> quanStringConverter = new StringConverter<DaiLy>() {
                @Override
                public String toString(DaiLy dl) {
                    return dl == null ? null : (dl.getMaDaiLy() + "-" + dl.getTenDaiLy()); //lên hình
                }

                @Override
                public DaiLy fromString(String string) {
                    return null; //Từ hình xuống data
                }
            };
            // Đặt StringConverter cho ComboBox
            dlComboBox.setConverter(quanStringConverter);

            // Đặt DataSource cho ComboBox
            dlComboBox.setItems(daiLy);
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
    public void themCTPX(){
        ChiTietPhieuXuatRow temp = new ChiTietPhieuXuatRow(ctpxContainer);
        ctpxContainer.getChildren().add(ctpxContainer.getChildren().size()-2,temp);
        temp.SetOnXoa(event -> {
            if(ctpxContainer.getChildren().size()<=3){
                event.consume();
            }else{
                ctpxContainer.getChildren().remove(temp);
            }
        });
        temp.thanhTienTextField.textProperty().addListener(ob ->{
            capNhatTongTien();
        });
    }
    private void capNhatTongTien(){
        double tt = 0;
        for(int i = 0; i< ctpxContainer.getChildren().size(); i++){
            if(ctpxContainer.getChildren().get(i) instanceof ChiTietPhieuXuatRow temp){
                tt += temp.getThanhTienDouble();
            }
        }
        tongTienText.setText(Double.toString(tt));
    }
    public void setInitialValue(PhieuXuat phieuXuat, NhanVien nvLoggedIn) {
        if(phieuXuat==null){
            nhanVienTextField.setText(nvLoggedIn.getHoTen());
            this.nvLapPhieu = nvLoggedIn;

            Date ngayTiepNhan = new Date(System.currentTimeMillis());
            ngayLapPhieuTextField.setText(DayFormat.GetDayStringFormatted(ngayTiepNhan));
            return;
        }
        phieuXuatGoc = phieuXuat;
        ngayLapPhieuTextField.setText(DayFormat.GetDayStringFormatted(phieuXuat.getNgayLapPhieu()));
        try{
            NhanVien nv = NhanVienDAO.getInstance().QueryID(phieuXuat.getMaNhanVien());
            nhanVienTextField.setText(nv.getHoTen());

            DaiLy dl = DaiLyDAO.getInstance().QueryID(phieuXuat.getMaDaiLy());

            dlComboBox.setValue(dl);
        }catch (SQLException _){}

        ghiChuTextArea.setText(phieuXuat.getGhiChu());


        ngayLapPhieuTextField.setDisable(true);
        nhanVienTextField.setDisable(true);
        dlComboBox.setDisable(true);
    }
    public PhieuXuat getPhieuXuat(){
        if(phieuXuatGoc ==null){
            Date ngayTiepNhan = new Date(System.currentTimeMillis());
            PhieuXuat phieuXuat = new PhieuXuat();

            phieuXuat.setMaNhanVien(nvLapPhieu.getID());
            phieuXuat.setMaDaiLy(dlComboBox.getValue().getID());
            phieuXuat.setNgayLapPhieu(ngayTiepNhan);
            phieuXuat.setGhiChu(ghiChuTextArea.getText());
            return phieuXuat;
        }
        else{
            phieuXuatGoc.setGhiChu(ghiChuTextArea.getText());
            return phieuXuatGoc;
        }
    }
    public List<ChiTietPhieuXuat> getChiTietPhieuXuat(){
        List<ChiTietPhieuXuat> chiTietPhieuXuatList = new ArrayList<>();
        for(int i = 0; i< ctpxContainer.getChildren().size(); i++){
            if(ctpxContainer.getChildren().get(i) instanceof ChiTietPhieuXuatRow temp){
                chiTietPhieuXuatList.add(temp.getChiTietPhieuXuat());
            }
        }
        return chiTietPhieuXuatList;
    }

    public String GetValid(){
        Boolean emptyDaiLy = dlComboBox.getValue()==null;
        String validRows = "";
        if(emptyDaiLy){
            validRows = validRows.concat("Đại lý không được để trống. \n");
        }
        for(int i = 0; i< ctpxContainer.getChildren().size(); i++){
            if(ctpxContainer.getChildren().get(i) instanceof ChiTietPhieuXuatRow temp){
                String validRow = temp.GetValid();
                if(!validRow.isEmpty()){
                    validRows = validRows.concat("Hàng "+(i+1)+": "+validRow).concat("\n");
                }
            }
        }
        return validRows;
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

    public void themCTPXExcel(ChiTietPhieuXuat chiTietPhieuXuat) {
        ChiTietPhieuXuatRow temp = new ChiTietPhieuXuatRow(ctpxContainer);
        temp.loadFromChiTietPhieuXuat(chiTietPhieuXuat);
        ctpxContainer.getChildren().add(ctpxContainer.getChildren().size() - 2, temp);
        temp.SetOnXoa(event -> {
            if (ctpxContainer.getChildren().size() <= 3) {
                event.consume();
            } else {
                ctpxContainer.getChildren().remove(temp);
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

                ChiTietPhieuXuat chiTietPhieuXuat = new ChiTietPhieuXuat();
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
                        chiTietPhieuXuat.setMaMatHang(maMatHangID);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                Integer sl = (int) soLuongCell.getNumericCellValue();
                chiTietPhieuXuat.setSoLuong(sl);
                MatHang matHang;
                try {
                    matHang = MatHangDAO.getInstance().QueryID(maMatHangID);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                try {
                    double thanhTien = sl * matHang.getDonGiaNhap();
                    chiTietPhieuXuat.setThanhTien(thanhTien);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                themCTPXExcel(chiTietPhieuXuat);
            }
        }
        capNhatTongTien();
        // Xóa hàng đầu tiên sau khi nhập
        if (!hasError) {
            ctpxContainer.getChildren().remove(0);
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
