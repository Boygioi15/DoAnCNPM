package org.doancnpm.ManHinhPhieuXuat;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.controlsfx.control.SearchableComboBox;
import org.doancnpm.DAO.*;
import org.doancnpm.Models.*;
import org.doancnpm.Ultilities.CheckExist;
import org.doancnpm.Ultilities.ChiTietPhieu.ChiTietPhieuNhapRow;
import org.doancnpm.Ultilities.ChiTietPhieu.ChiTietPhieuXuatRow;
import org.doancnpm.Ultilities.DayFormat;
import org.doancnpm.Ultilities.MoneyFormatter;
import org.doancnpm.Ultilities.PopDialog;

import java.io.*;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class LapPhieuXuatDialogController implements Initializable {
    @FXML private TextField ngayLapPhieuTextField;
    @FXML private TextField nhanVienTextField;
    @FXML private SearchableComboBox<DaiLy> dlComboBox;
    @FXML Text tongTienText;
    @FXML
    Label title;
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
        xuatExcelBtn.setOnAction(ob->{
            OpenExportDialog();
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
        ctpxContainer.getChildren().add(ctpxContainer.getChildren().size()-1,temp);
        temp.SetOnXoa(event -> {
            if(ctpxContainer.getChildren().size()<=2){
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
        Long tt = 0L;
        for(int i = 0; i< ctpxContainer.getChildren().size(); i++){
            if(ctpxContainer.getChildren().get(i) instanceof ChiTietPhieuXuatRow temp){
                tt += temp.getThanhTien();
            }
        }
        tongTienText.setText(MoneyFormatter.convertLongToString(tt));
    }
    public void setInitialValue(PhieuXuat phieuXuat, NhanVien nvLoggedIn) {
        if(phieuXuat==null){
            nhanVienTextField.setText(nvLoggedIn.getHoTen());
            this.nvLapPhieu = nvLoggedIn;

            Date ngayTiepNhan = new Date(System.currentTimeMillis());
            ngayLapPhieuTextField.setText(DayFormat.GetDayStringFormatted(ngayTiepNhan));
            xuatExcelBtn.setDisable(true);
            return;
        }
        loadExcelBtn.setDisable(true);
        title.setText("Cập nhật phiếu xuất");
        phieuXuatGoc = phieuXuat;
        ngayLapPhieuTextField.setText(DayFormat.GetDayStringFormatted(phieuXuat.getNgayLapPhieu()));
        try{
            NhanVien nv = NhanVienDAO.getInstance().QueryID(phieuXuat.getMaNhanVien());
            nhanVienTextField.setText(nv.getHoTen());

            DaiLy dl = DaiLyDAO.getInstance().QueryID(phieuXuat.getMaDaiLy());

            dlComboBox.setValue(dl);
        }catch (SQLException _){}

        ghiChuTextArea.setText(phieuXuat.getGhiChu());

        ctpxContainer.setDisable(true);
        ngayLapPhieuTextField.setDisable(true);
        nhanVienTextField.setDisable(true);
        dlComboBox.setDisable(true);
        loadCTPXs();
    }
    private void loadCTPXs(){
        try {
            List<ChiTietPhieuXuat> list = CTPXDAO.getInstance().QueryByPhieuXuatID(phieuXuatGoc.getID());
            for(ChiTietPhieuXuat chiTietPhieuXuat : list){
                themCTPXExcel(chiTietPhieuXuat);
            }
            if(ctpxContainer.getChildren().size()>=2){
                ctpxContainer.getChildren().remove(0);
            }
            capNhatTongTien();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
        ctpxContainer.getChildren().add(ctpxContainer.getChildren().size() - 1, temp);
        temp.SetOnXoa(event -> {
            if (ctpxContainer.getChildren().size() <= 2) {
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
                    long thanhTien = sl * matHang.getDonGiaNhap();
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
            if(ctpxContainer.getChildren().size()>=2){
                ctpxContainer.getChildren().remove(0);
            }
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
    public void OpenExportDialog() {
        // Hiển thị hộp thoại lưu tệp Excel
        if (phieuXuatGoc == null) {
            PopDialog.popErrorDialog("Không có phiếu xuất để xuất Excel");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));

        // Tạo tên file với định dạng "Export_ngay_thang_nam.xlsx"
        Date ngayHienTai = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy");
        String fileName = "ChiTietPhieuXuat_"+phieuXuatGoc.getMaPhieuXuat()+"_"+ dateFormat.format(ngayHienTai) + ".xlsx";

        // Thiết lập tên file mặc định cho hộp thoại lưu
        File initialDirectory = new File(System.getProperty("user.home"));
        File defaultFile = new File(initialDirectory, fileName);
        fileChooser.setInitialFileName(fileName);
        fileChooser.setInitialDirectory(initialDirectory);
        fileChooser.setInitialFileName(fileName);

        File selectedFile = fileChooser.showSaveDialog(null);

        // Kiểm tra nếu người dùng đã chọn vị trí lưu tệp Excel
        if (selectedFile != null) {
            // Gọi hàm exportFromExcel và truyền đường dẫn tệp Excel được chọn
            exportToExcel(selectedFile.getAbsolutePath());
        }

    }
    public void exportToExcel(String filePath) {
        // Tạo hoặc mở tệp Excel
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("ChiTietPhieuXuatData"); // Tạo một sheet mới hoặc sử dụng sheet hiện có

        // Tạo hàng đầu tiên với các tiêu đề cột
        Row headerRow = sheet.createRow(0);
        String[] columnTitles = {"Mã mặt hàng", "Số lượng", "Thành tiền"};
        int cellnum = 0;
        for (String title : columnTitles) {
            Cell cell = headerRow.createCell(cellnum++);
            cell.setCellValue(title);
        }

        List<ChiTietPhieuXuat> chiTietPhieuXuats = null;
        // Lấy danh sách chi tiết phiếu nhập từ cơ sở dữ liệu
        try {
            chiTietPhieuXuats = CTPXDAO.getInstance().QueryByPhieuXuatID(phieuXuatGoc.getID());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Ghi dữ liệu vào các dòng của tệp Excel
        int rownum = 1; // Bắt đầu từ hàng thứ 2 sau tiêu đề
        for (ChiTietPhieuXuat chiTiet : chiTietPhieuXuats) {
            Row row = sheet.createRow(rownum++);
            cellnum = 0;
            MatHang matHang = null;
            try {
                matHang = MatHangDAO.getInstance().QueryID(chiTiet.getMaMatHang());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            row.createCell(cellnum++).setCellValue(matHang.getMaMatHang());
            row.createCell(cellnum++).setCellValue(chiTiet.getSoLuong());
            row.createCell(cellnum++).setCellValue(chiTiet.getThanhTien());
        }

        // Tự động điều chỉnh độ rộng của các cột
        for (int i = 0; i < columnTitles.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Lưu tệp Excel
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            workbook.write(fos);
            workbook.close();
            PopDialog.popSuccessDialog("Xuất file excel thành công");
        } catch (IOException e) {
            PopDialog.popErrorDialog("Xuất file excel thất bại", e.getMessage());
        }
    }
}
