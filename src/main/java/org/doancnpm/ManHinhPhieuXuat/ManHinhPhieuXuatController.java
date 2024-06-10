package org.doancnpm.ManHinhPhieuXuat;

import io.github.palexdev.materialfx.controls.MFXTextField;
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
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.controlsfx.control.MasterDetailPane;
import org.doancnpm.DAO.*;
import org.doancnpm.Filters.PhieuXuatFilter;
import org.doancnpm.Models.*;
import org.doancnpm.Ultilities.DayFormat;
import org.doancnpm.Ultilities.MoneyFormatter;
import org.doancnpm.Ultilities.PopDialog;

import java.io.*;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class ManHinhPhieuXuatController implements Initializable {

    @FXML private Node manHinhPhieuXuat;
    @FXML private TableView mainTableView;
    @FXML private TableView detailTableView;
    @FXML private Button refreshButton;
    @FXML private MFXTextField dlTextField;
    @FXML private MFXTextField maPXTextField;
    @FXML private MFXTextField nvTextField;

    @FXML private MenuItem addExcelButton;
    @FXML private MenuItem addDirectButton;

    @FXML private Text maPXText;
    @FXML private Text maNVText;
    @FXML private Text tenNVText;
    @FXML private Text maDLText,tenDLText;
    @FXML private Text ngayLapPhieuText;
    @FXML private Text tongTienText;

    @FXML private MasterDetailPane masterDetailPane;

    @FXML private Region masterPane;
    @FXML private Button toggleDetailButton;
    @FXML private Region detailPane;

    private final ObservableList<PhieuXuat> dsPhieuXuat = FXCollections.observableArrayList();
    private final ObservableList<PhieuXuat> dsPhieuXuatFiltered = FXCollections.observableArrayList();
    private final PhieuXuatFilter filter = new PhieuXuatFilter();

    NhanVien nhanVienLoggedIn = null;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initMainTableView();
        initDetailTableView();
        initEvent();
        initDatabaseBinding();
        initFilterBinding();

        initUIDataBinding();

        updateListFromDatabase();
        initDetailPane();
        //init data
    }
    public void setVisibility(boolean visibility) {
        manHinhPhieuXuat.setVisible(visibility);
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
        mainTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, phieuXuat) -> {
            UpdateDetailPane((PhieuXuat) phieuXuat);
        });
    }
    private void initDetailTableView(){
        // Tạo các cột cho TableView

        TableColumn<ChiTietPhieuXuat, String> mhCol = new TableColumn<>("Mặt hàng");
        mhCol.setCellValueFactory(data -> {
            MatHang mh = null;
            try {
                mh = MatHangDAO.getInstance().QueryID(data.getValue().getMaMatHang());
            } catch (SQLException _) {}
            return new SimpleObjectProperty<>(mh.getMaMatHang()+ " - "+mh.getTenMatHang());
        });

        TableColumn<ChiTietPhieuXuat, String> dvtCol = new TableColumn<>("Đơn vị tính");
        dvtCol.setCellValueFactory(data -> {
            MatHang mh = null;
            try {
                mh = MatHangDAO.getInstance().QueryID(data.getValue().getMaMatHang());
            } catch (SQLException _) {}

            DonViTinh dvt = null;
            try {
                dvt = DonViTinhDAO.getInstance().QueryID(mh.getMaDVT());
            } catch (SQLException _) {}
            return new SimpleObjectProperty<>(dvt.getTenDVT());
        });

        TableColumn<ChiTietPhieuXuat, Integer> slCol = new TableColumn<>("Số lượng");
        slCol.setCellValueFactory( new PropertyValueFactory<>("soLuong"));

        TableColumn<ChiTietPhieuXuat, String> donGiaNhapCol = new TableColumn<>("Đơn giá");
        donGiaNhapCol.setCellValueFactory(data -> {
            return new SimpleObjectProperty<>(MoneyFormatter.convertLongToString(data.getValue().getDonGiaXuat()));
        });

        TableColumn<ChiTietPhieuXuat, String> thanhTienCol = new TableColumn<>("Thành tiền ");
        thanhTienCol.setCellValueFactory(data->{
            return new SimpleStringProperty(MoneyFormatter.convertLongToString(data.getValue().getThanhTien()));
        });
        detailTableView.getColumns().addAll(
                mhCol,dvtCol,slCol,donGiaNhapCol,thanhTienCol
        );
        detailTableView.setEditable(true);
        detailTableView.widthProperty().addListener(ob -> {
            double width = detailTableView.getWidth();
            mhCol.setPrefWidth(width*0.3);
            dvtCol.setPrefWidth(width*0.15);
            slCol.setPrefWidth(width*0.15);
            donGiaNhapCol.setPrefWidth(width*0.2);
            thanhTienCol.setPrefWidth(width*0.2);
        });
        detailTableView.setEditable( true );
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
        PhieuXuatDAO.getInstance().AddDatabaseListener(_ -> updateListFromDatabase());

    }
    private void initUIDataBinding() {
        mainTableView.setItems(dsPhieuXuatFiltered);
    }
    private void initFilterBinding(){
        filter.setInput(dsPhieuXuat);

        maPXTextField.textProperty().addListener(_ -> {
            filter.setMaPhieuXuat(maPXTextField.getText());
            filterList();
        });
        dlTextField.textProperty().addListener(_ -> {
            filter.setMaDaiLy(Integer.parseInt(dlTextField.getText()));
            filterList();
        });
        nvTextField.textProperty().addListener(_ -> {
            filter.setMaPhieuXuat(nvTextField.getText());
            filterList();
        });
    }
    private void initMainTableView() {
        // Tạo các cột cho TableView
        TableColumn<PhieuXuat, String> maPXCol = new TableColumn<>("Mã Phiếu Xuất");
        maPXCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMaPhieuXuat()));

        TableColumn<PhieuXuat, String> maNVCol = new TableColumn<>("Nhân viên");
        maNVCol.setCellValueFactory(new PropertyValueFactory<>("maNhanVien"));

        TableColumn<PhieuXuat, Integer> nccCol = new TableColumn<>("Đại lý");
        nccCol.setCellValueFactory(new PropertyValueFactory<>("maDaiLy"));

        TableColumn<PhieuXuat, String> tongTienCol = new TableColumn<>("Tổng tiền");
        tongTienCol.setCellValueFactory(data->{
            return new SimpleStringProperty(MoneyFormatter.convertLongToString(data.getValue().getTongTien()));
        });

        TableColumn<PhieuXuat, Boolean> selectedCol = new TableColumn<>("Selected");
        selectedCol.setCellValueFactory(new PropertyValueFactory<>("selected"));
        selectedCol.setCellFactory(tc -> new CheckBoxTableCell<>());

        //action column
        TableColumn actionCol = new TableColumn("Action");

        Callback<TableColumn<PhieuXuat, String>, TableCell<PhieuXuat, String>> cellFactory
                = //
                new Callback<>() {
                    @Override
                    public TableCell call(final TableColumn<PhieuXuat, String> param) {
                        final TableCell<PhieuXuat, String> cell = new TableCell<PhieuXuat, String>() {
                            final Button suaBtn = new Button("Sửa");
                            final Button xuatBtn = new Button("Xuất");

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    suaBtn.setOnAction(_ -> {
                                        try {
                                            PhieuXuat phieuXuat = getTableView().getItems().get(getIndex());
                                            new LapPhieuXuatDialog(phieuXuat, nhanVienLoggedIn).showAndWait();
                                        } catch(IOException exc) {
                                            exc.printStackTrace();
                                        }
                                    });

                                    xuatBtn.setOnAction(_ -> {});
                                    HBox hbox = new HBox();
                                    hbox.getChildren().addAll(suaBtn,xuatBtn);
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
                maPXCol,
                maNVCol,
                nccCol,
                tongTienCol,
                actionCol
        );
        mainTableView.setEditable(true);
        mainTableView.widthProperty().addListener(ob -> {
            double width = mainTableView.getWidth();
            selectedCol.setPrefWidth(width*0.1);
            maPXCol.setPrefWidth(width*0.1);
            maNVCol.setPrefWidth(width*0.1);
            nccCol.setPrefWidth(width*0.3);
            tongTienCol.setPrefWidth(width*0.2);
            actionCol.setPrefWidth(width*0.2);
        });
        mainTableView.setEditable( true );
        mainTableView.setPrefWidth(1100);

    }

    //detail pane
    public void UpdateDetailPane(PhieuXuat phieuXuat){
        if(phieuXuat==null){
            CloseDetailPanel();
            return;
        }
        maPXText.setText(phieuXuat.getMaPhieuXuat());
        try{
            NhanVien nv = NhanVienDAO.getInstance().QueryID(phieuXuat.getMaNhanVien());
            maNVText.setText(nv.getMaNhanVien());
            tenNVText.setText(nv.getHoTen());

            DaiLy dl = DaiLyDAO.getInstance().QueryID(phieuXuat.getMaDaiLy());
            maDLText.setText(dl.getMaDaiLy());
            tenDLText.setText(dl.getTenDaiLy());
        }
        catch (SQLException _){}
        ngayLapPhieuText.setText(DayFormat.GetDayStringFormatted(phieuXuat.getNgayLapPhieu()));
        tongTienText.setText(MoneyFormatter.convertLongToString(phieuXuat.getTongTien()));

        //item
        try{
            List<ChiTietPhieuXuat> chiTietPhieuXuatList = CTPXDAO.getInstance().QueryByPhieuXuatID(phieuXuat.getID());
            detailTableView.getItems().clear();
            ObservableList<ChiTietPhieuXuat> observableChiTietPhieuXuatList = FXCollections.observableArrayList(chiTietPhieuXuatList);
            detailTableView.setItems(observableChiTietPhieuXuatList);
        }
        catch(SQLException _){}
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
        /*
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

                PhieuXuat phieuXuat = new PhieuXuat();
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

                phieuXuat.setMaDaiLy(idDL);
                phieuXuat.setMaNhanVien(idNV);
                phieuXuat.setSoTienThu((int) tienThuCell.getNumericCellValue());
                phieuXuat.setGhiChu(ghiChuCell.getStringCellValue());

                phieuXuat.setNgayLap(ngayLapPhieu);
                try {
                    PhieuXuatDAO.getInstance().Insert(phieuXuat); // Thêm đối tượng vào cơ sở dữ liệu
                }
                catch (SQLException e) {
                    PopDialog.popErrorDialog("Thêm mới phiếu xuất thất bại", e.getMessage());
                    return;
                }
            }
        }

        try {
            workbook.close();
            fis.close();
            PopDialog.popSuccessDialog("Thêm danh sách phiếu xuất từ file excel thành công");
        }
        catch (IOException e) {
            PopDialog.popErrorDialog("Có lỗi trong quá trình thực hiện", e.getMessage());
        }

         */
    }

    public void exportDialog() {
        // Hiển thị hộp thoại lưu tệp Excel
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));

        // Tạo tên file với định dạng "Export_ngay_thang_nam.xlsx"
        Date ngayHienTai = new Date(System.currentTimeMillis());
        String fileName = "DsPhieuXuat_" + DayFormat.GetDayStringFormatted(ngayHienTai) + ".xlsx";

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
        XSSFSheet sheet = workbook.createSheet("PhieuXuatData"); // Tạo một sheet mới hoặc sử dụng sheet hiện có

        // Tạo hàng đầu tiên với các tiêu đề cột
        Row headerRow = sheet.createRow(0);
        String[] columnTitles = {"Mã phiếu xuất", "Mã đại lý", "Mã nhân viên", "Số tiền thu", "Ngày lập phiếu"};
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
        for (PhieuXuat phieuXuat : dsPhieuXuatFiltered) {
            Row row = sheet.createRow(rownum++);
            cellnum = 0;
            row.createCell(cellnum++).setCellValue(phieuXuat.getMaPhieuXuat());

            DaiLy daiLy = null;
            try {
                daiLy = DaiLyDAO.getInstance().QueryID(phieuXuat.getMaDaiLy());
            } catch (SQLException _) {}
            if (daiLy != null) {
                row.createCell(cellnum++).setCellValue(daiLy.getMaDaiLy());
            } else {
                row.createCell(cellnum++).setCellValue("???"); // Or handle the null case appropriately
            }

            NhanVien nhanVien = null;
            try {
                nhanVien = NhanVienDAO.getInstance().QueryID(phieuXuat.getMaNhanVien());
            } catch (SQLException _) {}
            if (nhanVien != null) {
                row.createCell(cellnum++).setCellValue(nhanVien.getMaNhanVien());
            } else {
                row.createCell(cellnum++).setCellValue("???"); // Or handle the null case appropriately
            }

            row.createCell(cellnum++).setCellValue(phieuXuat.getSoTienThu());

            // Định dạng ngày
            Cell dateCell = row.createCell(cellnum++);
            if (phieuXuat.getNgayLap() != null) {
                dateCell.setCellValue(phieuXuat.getNgayLap());
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
            new LapPhieuXuatDialog(nhanVienLoggedIn).showAndWait();
            updateListFromDatabase();
        }
        catch (IOException e) {
            e.printStackTrace();
            PopDialog.popErrorDialog("Không thể mở dialog thêm phiếu xuất", e.getMessage());
        }
    }

    private void updateListFromDatabase() {
        dsPhieuXuat.clear();
        try {
            dsPhieuXuat.addAll(PhieuXuatDAO.getInstance().QueryAll());
            filterList();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void filterList(){
        dsPhieuXuatFiltered.clear();
        dsPhieuXuatFiltered.addAll(filter.Filter());
    }
    private void resetFilter(){
        maPXTextField.clear();
        nvTextField.clear();
    }
}
