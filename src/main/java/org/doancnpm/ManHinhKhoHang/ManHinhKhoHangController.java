package org.doancnpm.ManHinhKhoHang;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import javafx.util.StringConverter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.controlsfx.control.MasterDetailPane;
import org.doancnpm.DAO.*;

import org.doancnpm.Filters.MatHangFilter;
import org.doancnpm.Filters.PhieuThuFilter;
import org.doancnpm.ManHinhDaiLy.TiepNhanDaiLyDialog;
import org.doancnpm.Models.*;
import org.doancnpm.Ultilities.DayFormat;
import org.doancnpm.Ultilities.PopDialog;
import javafx.scene.text.Text;

import java.io.*;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class ManHinhKhoHangController implements Initializable {
    @FXML private Region manHinhKhoHang;
    @FXML private TableView mainTableView;
    @FXML private Button refreshButton;
    @FXML private MFXTextField maMHTextField;
    @FXML private MFXTextField tenMHTextField;
    @FXML private MFXComboBox<DonViTinh> dvtComboBox;
    @FXML private MFXComboBox<String> soLuongComboBox;

    @FXML private MenuItem addDirectButton;
    @FXML private MenuItem addExcelButton;

    @FXML private Text maMHText;
    @FXML private Text tenMHText;
    @FXML private Text dvtText;
    @FXML private Text donGiaNhapText;
    @FXML private Text donGiaXuatText;
    @FXML private Text soLuongText;
    @FXML private TextArea ghiChuTextArea;

    @FXML private MasterDetailPane masterDetailPane;

    @FXML private Region masterPane;
    @FXML private Button toggleDetailButton;
    @FXML private Region detailPane;

    private final ObservableList<MatHang> dsMatHang = FXCollections.observableArrayList();
    private final ObservableList<MatHang> dsMatHangFiltered = FXCollections.observableArrayList();
    private final MatHangFilter filter = new MatHangFilter();

    NhanVien nhanVienLoggedIn = null;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initTableView();
        initEvent();
        initDatabaseBinding();
        initFilterBinding();
        initFilterComboboxData();

        initUIDataBinding();

        updateListFromDatabase();
        initDetailPane();
        //init data
    }
    public void setVisibility(boolean visibility) {
        manHinhKhoHang.setVisible(visibility);
    }
    public void setNhanVienLoggedIn(NhanVien nhanVienLoggedIn) {
        this.nhanVienLoggedIn = nhanVienLoggedIn;
    }
    //init
    private void initDetailPane(){
        masterDetailPane.setDetailNode(detailPane);
        masterDetailPane.setMasterNode(masterPane);

        masterDetailPane.widthProperty().addListener(ob ->{
            detailPane.setMinWidth(masterDetailPane.getWidth()*0.3);
            detailPane.setMaxWidth(masterDetailPane.getWidth()*0.3);
        });
        mainTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, matHang) -> {
            UpdateDetailPane((MatHang) matHang);
        });
    }
    private void initEvent() {
        addDirectButton.setOnAction(_ -> {
            OpenDirectAddDialog();
        });
        refreshButton.setOnAction(_ -> {
            resetFilter();
        });
        addExcelButton.setOnAction(_ ->{
            exportDialog();
        });
        toggleDetailButton.setOnAction(ob ->{
            if(masterDetailPane.isShowDetailNode()){
                CloseDetailPanel();
            }
            else{
                OpenDetailPanel();
            }
        });
    }
    private void initDatabaseBinding() {
        MatHangDAO.getInstance().AddDatabaseListener(_ -> updateListFromDatabase());

    }
    private void initUIDataBinding() {
        mainTableView.setItems(dsMatHangFiltered);
        initFilterBinding();
    }
    private void initFilterBinding(){
        filter.setInput(dsMatHang);

        maMHTextField.textProperty().addListener(_ -> {
            filter.setMaMH(maMHTextField.getText());
            filterList();
        });
        tenMHTextField.textProperty().addListener(_ -> {
            filter.setTenMH(tenMHTextField.getText());
            filterList();
        });
        dvtComboBox.valueProperty().addListener(_ -> {
            if(dvtComboBox.getValue() == null){
                filter.setMaDVT(null);
            }
            else {
                filter.setMaDVT(dvtComboBox.getValue().getId());
            }
            filterList();
        });
        soLuongComboBox.valueProperty().addListener(_ ->{
            if(soLuongComboBox.getValue() == null){
                filter.setTonKho(null);
            }else{
                if(soLuongComboBox.getValue().equals("Hết hàng")){
                    filter.setTonKho(true);
                }
                else{
                    filter.setTonKho(false);
                }
            }

        });
    }
    private void initFilterComboboxData(){
        try {
            ObservableList<DonViTinh> donViTinhs = FXCollections.observableArrayList(DonViTinhDAO.getInstance().QueryAll());

            // Sử dụng StringConverter để hiển thị dữ liệu trong ComboBox
            StringConverter<DonViTinh> quanStringConverter = new StringConverter<DonViTinh>() {
                @Override
                public String toString(DonViTinh donViTinh) {
                    return donViTinh == null ? null : donViTinh.getTenDVT(); //lên hình
                }

                @Override
                public DonViTinh fromString(String string) {
                    return null; //Từ hình xuống data
                }
            };

            // Đặt StringConverter cho ComboBox
            dvtComboBox.setConverter(quanStringConverter);
            // Đặt DataSource cho ComboBox
            dvtComboBox.setItems(donViTinhs);
        }
        catch (SQLException e) {
            PopDialog.popErrorDialog("Lấy dữ liệu các đơn vị tính thất bại",e.getMessage());
        }
        soLuongComboBox.getItems().addAll("Hết hàng", "Còn hàng");
    }
    private void initTableView() {
        // Tạo các cột cho TableView
        TableColumn<MatHang, String> maMHCol = new TableColumn<>("Mã mặt hàng");
        maMHCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMaMatHang()));

        TableColumn<MatHang, String> tenMHCol = new TableColumn<>("Mã mặt hàng");
        tenMHCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTenMatHang()));

        TableColumn<MatHang, String> dvtCol = new TableColumn<>("Đơn vị tính");
        dvtCol.setCellValueFactory(data -> {
            DonViTinh dvt = null;
            try {
                dvt = DonViTinhDAO.getInstance().QueryID(data.getValue().getMaDVT());
            } catch (SQLException _) {}
            return new SimpleObjectProperty<>(dvt.getTenDVT());
        });

        TableColumn<MatHang, Double> donGiaNhapCol = new TableColumn<>("Đơn giá nhập");
        donGiaNhapCol.setCellValueFactory(new PropertyValueFactory<>("donGiaNhap"));

        TableColumn<MatHang, Integer> soLuongCol = new TableColumn<>("Số lượng");
        soLuongCol.setCellValueFactory(new PropertyValueFactory<>("soLuong"));

        TableColumn<PhieuThu, Boolean> selectedCol = new TableColumn<>("Selected");
        selectedCol.setCellValueFactory(new PropertyValueFactory<>("selected"));
        selectedCol.setCellFactory(tc -> new CheckBoxTableCell<>());

        //action column
        TableColumn actionCol = new TableColumn("Action");

        Callback<TableColumn<MatHang, String>, TableCell<MatHang, String>> cellFactory
                = //
                new Callback<>() {
                    @Override
                    public TableCell call(final TableColumn<MatHang, String> param) {
                        final TableCell<MatHang, String> cell = new TableCell<MatHang, String>() {
                            final Button suaBtn = new javafx.scene.control.Button("Sửa");
                            final Button xoaBtn = new javafx.scene.control.Button("Xóa");

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                }
                                else {
                                    xoaBtn.setOnAction(event -> {
                                        MatHang mh = getTableView().getItems().get(getIndex());
                                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                        alert.setTitle("Xóa đại lý");
                                        alert.setHeaderText("Xác nhận xóa mặt hàng " + mh.getTenMatHang()+ " ?");

                                        Optional<ButtonType> result = alert.showAndWait();
                                        if (result.get() == ButtonType.OK){
                                            try {
                                                MatHangDAO.getInstance().Delete(mh.getID());
                                                PopDialog.popSuccessDialog("Xóa mặt hàng "+ mh.getTenMatHang() + " thành công");
                                            }
                                            catch (SQLException e) {
                                                PopDialog.popErrorDialog("Xóa mặt hàng "+ mh.getTenMatHang() + " thành công",e.getMessage());
                                            }
                                        }
                                    });
                                    suaBtn.setOnAction(_ -> {
                                        try {
                                            MatHang matHang = getTableView().getItems().get(getIndex());
                                            new ThemMoiMatHangDialog(matHang).showAndWait().ifPresent(matHangInfo -> {
                                                //set "database" info
                                                matHangInfo.setID(matHang.getID());
                                                matHangInfo.setDonGiaXuat(matHang.getDonGiaXuat());
                                                matHangInfo.setSoLuong(matHang.getSoLuong());
                                                try {
                                                    MatHangDAO.getInstance().Update(matHang.getID(),matHangInfo);
                                                    PopDialog.popSuccessDialog("Cập nhật mặt hàng "+matHangInfo.getMaMatHang()+" thành công");
                                                } catch (SQLException e) {
                                                    PopDialog.popErrorDialog("Cập nhật mặt hàng "+matHangInfo.getMaMatHang()+" thất bại",
                                                            e.getMessage());
                                                }
                                                //mainTableView.getItems().set(selectedIndex, response);
                                            });
                                        } catch(IOException exc) {
                                            PopDialog.popErrorDialog("Không thể mở dialog thêm mặt hàng");
                                        }
                                    });
                                    HBox hbox = new HBox();
                                    hbox.getChildren().addAll(suaBtn,xoaBtn);
                                    hbox.setSpacing(5);
                                    hbox.setPrefWidth(USE_COMPUTED_SIZE);
                                    hbox.setPrefHeight(USE_COMPUTED_SIZE);
                                    setGraphic(hbox);
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }
                };

        actionCol.setCellFactory(cellFactory);

        // Thêm các cột vào TableView
        mainTableView.getColumns().addAll(
                selectedCol,
                maMHCol,
                tenMHCol,
                dvtCol,
                donGiaNhapCol,
                soLuongCol,
                actionCol
        );
        mainTableView.setEditable(true);
        mainTableView.widthProperty().addListener(ob -> {
            double width = mainTableView.getWidth();
            selectedCol.setPrefWidth(width*0.1);
            maMHCol.setPrefWidth(width*0.1);
            dvtCol.setPrefWidth(width*0.1);
            tenMHCol.setPrefWidth(width*0.3);
            donGiaNhapCol.setPrefWidth(width*0.15);
            soLuongCol.setPrefWidth(width*0.1);
            actionCol.setPrefWidth(width*0.15);
        });
        mainTableView.setEditable( true );
        mainTableView.setPrefWidth(1100);

    }

    //detail pane
    public void UpdateDetailPane(MatHang matHang){
        if(matHang==null){
            CloseDetailPanel();
            return;
        }
        maMHText.setText(matHang.getMaMatHang());
        tenMHText.setText(matHang.getTenMatHang());
        try{
            DonViTinh dvt = DonViTinhDAO.getInstance().QueryID(matHang.getMaDVT());
            dvtText.setText(dvt.getTenDVT());
            donGiaNhapText.setText(Double.toString(matHang.getDonGiaNhap()));
            donGiaXuatText.setText(Double.toString(matHang.getDonGiaXuat()));
            soLuongText.setText(Integer.toString(matHang.getSoLuong()));
            ghiChuTextArea.setText(matHang.getGhiChu());
        }
        catch (SQLException _){}
    }
    public void OpenDetailPanel(){
        toggleDetailButton.setText(">");
        masterDetailPane.setShowDetailNode(true);

    }
    public void CloseDetailPanel(){
        masterDetailPane.setShowDetailNode(false);
        toggleDetailButton.setText("<");
    }

    //import - export
    public void importDialog() {
        // Hiển thị hộp thoại chọn tệp Excel
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx", "*.xls")
        );
        File selectedFile = fileChooser.showOpenDialog(mainTableView.getScene().getWindow()); // primaryStage là cửa sổ chính của ứng dụng, bạn cần thay thế nó bằng Stage thích hợp

        // Kiểm tra nếu người dùng đã chọn một tệp Excel
        if (selectedFile != null) {
            // Gọi hàm importFromExcel và truyền đường dẫn tệp Excel đã chọn
            importFromExcel(selectedFile.getAbsolutePath());
        }
    }
    public void importFromExcel(String filePath)  {
        File file = new File(filePath);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        }
        catch (FileNotFoundException e) {
            PopDialog.popErrorDialog("Không thể mở file excel");
            return;
        }

        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(fis);
        }
        catch (IOException e) {
            PopDialog.popErrorDialog("Có lỗi trong quá trình thực hiện", e.getMessage());
            return;
        }
        XSSFSheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

        Date ngayLapPhieu = new Date(System.currentTimeMillis());

        for (int i = 1; i <= sheet.getLastRowNum()-1; i++) {
            Row row = sheet.getRow(i);
            if (row != null) { // Kiểm tra xem dòng có tồn tại hay không
                Cell maDaiLyCell = row.getCell(0);
                Cell maNhanVienCell = row.getCell(1);
                Cell tienThuCell = row.getCell(2);
                Cell ghiChuCell = row.getCell(3);

                PhieuThu phieuThu = new PhieuThu();
                String maDaiLy = maDaiLyCell.getStringCellValue();
                String maNhanVien = maNhanVienCell.getStringCellValue();
                int idDL, idNV;

                try{
                    idNV = Integer.parseInt(maNhanVien.substring(2));
                }
                catch (NumberFormatException e){
                    PopDialog.popErrorDialog("Định dạng mã nhân viên không đúng");
                    return;
                }
                try{
                    idDL = Integer.parseInt(maDaiLy.substring(2));
                }
                catch (NumberFormatException e){
                    PopDialog.popErrorDialog("Định dạng mã đại lý không đúng");
                    return;
                }

                phieuThu.setMaDaiLy(idDL);
                phieuThu.setMaNhanVien(idNV);
                //phieuThu.setSoTienThu((int) tienThuCell.getNumericCellValue());
                phieuThu.setGhiChu(ghiChuCell.getStringCellValue());

                phieuThu.setNgayLap(ngayLapPhieu);
                try {
                    PhieuThuDAO.getInstance().Insert(phieuThu); // Thêm đối tượng vào cơ sở dữ liệu
                }
                catch (SQLException e) {
                    PopDialog.popErrorDialog("Thêm mới phiếu thu thất bại", e.getMessage());
                    return;
                }
            }
        }

        try {
            workbook.close();
            fis.close();
            PopDialog.popSuccessDialog("Thêm danh sách phiếu thu từ file excel thành công");
        }
        catch (IOException e) {
            PopDialog.popErrorDialog("Có lỗi trong quá trình thực hiện", e.getMessage());
        }

    }

    public void exportDialog() {
        // Hiển thị hộp thoại lưu tệp Excel
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));

        // Tạo tên file với định dạng "Export_ngay_thang_nam.xlsx"
        Date ngayHienTai = new Date(System.currentTimeMillis());
        String fileName = "DsPhieuThu_" + DayFormat.GetDayStringFormatted(ngayHienTai) + ".xlsx";

        // Thiết lập tên file mặc định cho hộp thoại lưu
        File initialDirectory = new File(System.getProperty("user.home"));
        File defaultFile = new File(initialDirectory, fileName);
        fileChooser.setInitialFileName(fileName);
        fileChooser.setInitialDirectory(initialDirectory);
        fileChooser.setInitialFileName(fileName);

        File selectedFile = fileChooser.showSaveDialog(mainTableView.getScene().getWindow());

        // Kiểm tra nếu người dùng đã chọn vị trí lưu tệp Excel
        if (selectedFile != null) {
            // Gọi hàm exportFromExcel và truyền đường dẫn tệp Excel được chọn
            exportToExcel(selectedFile.getAbsolutePath());
        }

    }
    public void exportToExcel(String filePath) {
        /*
        // Tạo hoặc mở tệp Excel
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("PhieuThuData"); // Tạo một sheet mới hoặc sử dụng sheet hiện có

        // Tạo hàng đầu tiên với các tiêu đề cột
        Row headerRow = sheet.createRow(0);
        String[] columnTitles = {"Mã phiếu thu", "Mã đại lý", "Mã nhân viên", "Số tiền thu", "Ngày lập phiếu"};
        int cellnum = 0;
        for (String title : columnTitles) {
            Cell cell = headerRow.createCell(cellnum++);
            cell.setCellValue(title);
        }

        // Tạo CellStyle cho định dạng ngày
        CreationHelper createHelper = workbook.getCreationHelper();
        CellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));

        int rownum = 1; // Bắt đầu từ hàng thứ 2 sau tiêu đề
        for (PhieuThu phieuThu : dsMatHangFiltered) {
            Row row = sheet.createRow(rownum++);
            cellnum = 0;
            row.createCell(cellnum++).setCellValue(phieuThu.getMaPhieuThu());

            DaiLy daiLy = null;
            try {
                daiLy = DaiLyDAO.getInstance().QueryID(phieuThu.getMaDaiLy());
            } catch (SQLException _) {}
            if (daiLy != null) {
                row.createCell(cellnum++).setCellValue(daiLy.getMaDaiLy());
            } else {
                row.createCell(cellnum++).setCellValue("???"); // Or handle the null case appropriately
            }

            NhanVien nhanVien = null;
            try {
                nhanVien = NhanVienDAO.getInstance().QueryID(phieuThu.getMaNhanVien());
            } catch (SQLException _) {}
            if (nhanVien != null) {
                row.createCell(cellnum++).setCellValue(nhanVien.getMaNhanVien());
            } else {
                row.createCell(cellnum++).setCellValue("???"); // Or handle the null case appropriately
            }

            row.createCell(cellnum++).setCellValue(phieuThu.getSoTienThu());

            // Định dạng ngày
            Cell dateCell = row.createCell(cellnum++);
            if (phieuThu.getNgayLap() != null) {
                dateCell.setCellValue(phieuThu.getNgayLap());
                dateCell.setCellStyle(dateCellStyle);
            } else {
                dateCell.setCellValue("???"); // Or handle the null case appropriately
            }
        }

        // Lưu tệp Excel
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            workbook.write(fos);
            workbook.close();
        } catch (IOException e) {
            PopDialog.popErrorDialog("Xuất file excel thất bại", e.getMessage());
        }

         */
    }

    //functionalities
    public void OpenDirectAddDialog() {
        try {
            new ThemMoiMatHangDialog().showAndWait().ifPresent(
                    matHangAdded -> {
                        try {
                            MatHangDAO.getInstance().Insert(matHangAdded);
                            PopDialog.popSuccessDialog("Thêm mới mặt hàng thành công");
                        }
                        catch (SQLException e) {
                            PopDialog.popErrorDialog("Thêm mới mặt hàng thất bại", e.getMessage());
                        }
                    }
            );
        }
        catch (IOException e) {
            e.printStackTrace();
            PopDialog.popErrorDialog("Không thể mở dialog thêm mặt hàng", e.getMessage());
        }


    }

    private void updateListFromDatabase() {
        dsMatHang.clear();
        try {
            dsMatHang.addAll(MatHangDAO.getInstance().QueryAll());
            filterList();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void filterList(){
        dsMatHangFiltered.clear();
        dsMatHangFiltered.addAll(filter.Filter());
    }
    private void resetFilter(){
        maMHTextField.clear();
        tenMHTextField.clear();
        dvtComboBox.clearSelection();
        dvtComboBox.clear();
        soLuongComboBox.clearSelection();
        dvtComboBox.clear();
    }
}
