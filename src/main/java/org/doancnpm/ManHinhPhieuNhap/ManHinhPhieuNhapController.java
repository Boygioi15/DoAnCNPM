package org.doancnpm.ManHinhPhieuNhap;

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
import org.doancnpm.Filters.PhieuNhapFilter;
import org.doancnpm.ManHinhPhieuThu.LapPhieuThuDialog;
import org.doancnpm.Models.*;
import org.doancnpm.Ultilities.DayFormat;
import org.doancnpm.Ultilities.PopDialog;

import java.io.*;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class ManHinhPhieuNhapController implements Initializable {

    @FXML private Node manHinhPhieuNhap;
    @FXML private TableView mainTableView;
    @FXML private TableView detailTableView;
    @FXML private Button refreshButton;
    @FXML private MFXTextField nccTextField;
    @FXML private MFXTextField maPNTextField;
    @FXML private MFXTextField nvTextField;

    @FXML private MenuItem addExcelButton;
    @FXML private MenuItem addDirectButton;

    @FXML private Text maPNText;
    @FXML private Text maNVText;
    @FXML private Text tenNVText;
    @FXML private Text nccText;
    @FXML private Text ngayLapPhieuText;
    @FXML private Text tongTienText;

    @FXML private MasterDetailPane masterDetailPane;

    @FXML private Region masterPane;
    @FXML private Button toggleDetailButton;
    @FXML private Region detailPane;

    private final ObservableList<PhieuNhap> dsPhieuNhap = FXCollections.observableArrayList();
    private final ObservableList<PhieuNhap> dsPhieuNhapFiltered = FXCollections.observableArrayList();
    private final PhieuNhapFilter filter = new PhieuNhapFilter();

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
        manHinhPhieuNhap.setVisible(visibility);
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
        mainTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, phieuNhap) -> {
            UpdateDetailPane((PhieuNhap) phieuNhap);
        });
    }
    private void initDetailTableView(){
        // Tạo các cột cho TableView

        TableColumn<ChiTietPhieuNhap, String> mhCol = new TableColumn<>("Mặt hàng");
        mhCol.setCellValueFactory(data -> {
            MatHang mh = null;
            try {
                mh = MatHangDAO.getInstance().QueryID(data.getValue().getMaMatHang());
            } catch (SQLException _) {}
            return new SimpleObjectProperty<>(mh.getMaMatHang()+ " - "+mh.getTenMatHang());
        });

        TableColumn<ChiTietPhieuNhap, String> dvtCol = new TableColumn<>("Đơn vị tính");
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

        TableColumn<ChiTietPhieuNhap, Integer> slCol = new TableColumn<>("Số lượng");
        slCol.setCellValueFactory( new PropertyValueFactory<>("soLuong"));

        TableColumn<ChiTietPhieuNhap, Double> donGiaNhapCol = new TableColumn<>("Đơn giá");
        donGiaNhapCol.setCellValueFactory(data -> {
            MatHang mh = null;
            try {
                mh = MatHangDAO.getInstance().QueryID(data.getValue().getMaMatHang());
            } catch (SQLException _) {}
            return new SimpleObjectProperty<>(mh.getDonGiaNhap());
        });

        TableColumn<MatHang, Integer> thanhTienCol = new TableColumn<>("Thành tiền");
        thanhTienCol.setCellValueFactory(new PropertyValueFactory<>("thanhTien"));

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
        PhieuNhapDAO.getInstance().AddDatabaseListener(_ -> updateListFromDatabase());

    }
    private void initUIDataBinding() {
        mainTableView.setItems(dsPhieuNhapFiltered);
    }
    private void initFilterBinding(){
        filter.setInput(dsPhieuNhap);

        maPNTextField.textProperty().addListener(_ -> {
            filter.setMaPhieuNhap(maPNTextField.getText());
            filterList();
        });
        nccTextField.textProperty().addListener(_ -> {
            filter.setNhaCungCap(nccTextField.getText());
            filterList();
        });
        nvTextField.textProperty().addListener(_ -> {
            filter.setMaPhieuNhap(nvTextField.getText());
            filterList();
        });
    }
    private void initMainTableView() {
        // Tạo các cột cho TableView
        TableColumn<PhieuNhap, String> maPNCol = new TableColumn<>("Mã Phiếu Nhập");
        maPNCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMaPhieuNhap()));

        TableColumn<PhieuNhap, String> maNVCol = new TableColumn<>("Nhân viên");
        maNVCol.setCellValueFactory(new PropertyValueFactory<>("maNhanVien"));

        TableColumn<PhieuNhap, Integer> nccCol = new TableColumn<>("Nhà cung cấp");
        nccCol.setCellValueFactory(new PropertyValueFactory<>("nhaCungCap"));

        TableColumn<PhieuNhap, Integer> tongTienCol = new TableColumn<>("Tổng tiền");
        tongTienCol.setCellValueFactory(new PropertyValueFactory<>("tongTien"));

        TableColumn<PhieuNhap, Boolean> selectedCol = new TableColumn<>("Selected");
        selectedCol.setCellValueFactory(new PropertyValueFactory<>("selected"));
        selectedCol.setCellFactory(tc -> new CheckBoxTableCell<>());

        //action column
        TableColumn actionCol = new TableColumn("Action");

        Callback<TableColumn<PhieuNhap, String>, TableCell<PhieuNhap, String>> cellFactory
                = //
                new Callback<>() {
                    @Override
                    public TableCell call(final TableColumn<PhieuNhap, String> param) {
                        final TableCell<PhieuNhap, String> cell = new TableCell<PhieuNhap, String>() {
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
                                            PhieuNhap phieuNhap = getTableView().getItems().get(getIndex());
                                            new LapPhieuNhapDialog(phieuNhap, nhanVienLoggedIn).showAndWait().ifPresent(_ -> {
                                                try {
                                                    PhieuNhapDAO.getInstance().Update(phieuNhap.getID(),phieuNhap);
                                                    PopDialog.popSuccessDialog("Cập nhật phiếu nhập hàng "+phieuNhap.getMaPhieuNhap()+" thành công");
                                                }
                                                catch (SQLException e) {
                                                    PopDialog.popErrorDialog("Cập nhật phiếu nhập hàng "+phieuNhap.getMaPhieuNhap()+" thất bại",
                                                            e.getMessage());
                                                }
                                                //mainTableView.getItems().set(selectedIndex, response);
                                            });
                                        } catch(IOException exc) {
                                            exc.printStackTrace();
                                        }
                                    });

                                    xuatBtn.setOnAction(_ -> {});
                                    HBox hbox = new HBox();
                                    hbox.getChildren().addAll(xuatBtn);
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
                maPNCol,
                maNVCol,
                nccCol,
                tongTienCol,
                actionCol
        );
        mainTableView.setEditable(true);
        mainTableView.widthProperty().addListener(ob -> {
            double width = mainTableView.getWidth();
            selectedCol.setPrefWidth(width*0.1);
            maPNCol.setPrefWidth(width*0.1);
            maNVCol.setPrefWidth(width*0.1);
            nccCol.setPrefWidth(width*0.3);
            tongTienCol.setPrefWidth(width*0.2);
            actionCol.setPrefWidth(width*0.2);
        });
        mainTableView.setEditable( true );
        mainTableView.setPrefWidth(1100);

    }

    //detail pane
    public void UpdateDetailPane(PhieuNhap phieuNhap){
        if(phieuNhap==null){
            CloseDetailPanel();
            return;
        }
        maPNText.setText(phieuNhap.getMaPhieuNhap());
        try{
            NhanVien nv = NhanVienDAO.getInstance().QueryID(phieuNhap.getMaNhanVien());
            maNVText.setText(nv.getMaNhanVien());
            tenNVText.setText(nv.getHoTen());
        }
        catch (SQLException _){}
        nccText.setText(phieuNhap.getNhaCungCap());
        ngayLapPhieuText.setText(DayFormat.GetDayStringFormatted(phieuNhap.getNgayLapPhieu()));
        tongTienText.setText(Double.toString(phieuNhap.getTongTien()));

        //item
        try{
            List<ChiTietPhieuNhap> chiTietPhieuNhapList = CTPNDAO.getInstance().QueryByPhieuNhapID(phieuNhap.getID());
            detailTableView.getItems().clear();
            ObservableList<ChiTietPhieuNhap> observableChiTietPhieuNhapList = FXCollections.observableArrayList(chiTietPhieuNhapList);
            detailTableView.setItems(observableChiTietPhieuNhapList);
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

                PhieuNhap phieuNhap = new PhieuNhap();
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

                phieuNhap.setMaDaiLy(idDL);
                phieuNhap.setMaNhanVien(idNV);
                phieuNhap.setSoTienThu((int) tienThuCell.getNumericCellValue());
                phieuNhap.setGhiChu(ghiChuCell.getStringCellValue());

                phieuNhap.setNgayLap(ngayLapPhieu);
                try {
                    PhieuNhapDAO.getInstance().Insert(phieuNhap); // Thêm đối tượng vào cơ sở dữ liệu
                }
                catch (SQLException e) {
                    PopDialog.popErrorDialog("Thêm mới phiếu nhập thất bại", e.getMessage());
                    return;
                }
            }
        }

        try {
            workbook.close();
            fis.close();
            PopDialog.popSuccessDialog("Thêm danh sách phiếu nhập từ file excel thành công");
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
        String fileName = "DsPhieuNhap_" + DayFormat.GetDayStringFormatted(ngayHienTai) + ".xlsx";

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
        XSSFSheet sheet = workbook.createSheet("PhieuNhapData"); // Tạo một sheet mới hoặc sử dụng sheet hiện có

        // Tạo hàng đầu tiên với các tiêu đề cột
        Row headerRow = sheet.createRow(0);
        String[] columnTitles = {"Mã phiếu nhập", "Mã đại lý", "Mã nhân viên", "Số tiền thu", "Ngày lập phiếu"};
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
        for (PhieuNhap phieuNhap : dsPhieuNhapFiltered) {
            Row row = sheet.createRow(rownum++);
            cellnum = 0;
            row.createCell(cellnum++).setCellValue(phieuNhap.getMaPhieuNhap());

            DaiLy daiLy = null;
            try {
                daiLy = DaiLyDAO.getInstance().QueryID(phieuNhap.getMaDaiLy());
            } catch (SQLException _) {}
            if (daiLy != null) {
                row.createCell(cellnum++).setCellValue(daiLy.getMaDaiLy());
            } else {
                row.createCell(cellnum++).setCellValue("???"); // Or handle the null case appropriately
            }

            NhanVien nhanVien = null;
            try {
                nhanVien = NhanVienDAO.getInstance().QueryID(phieuNhap.getMaNhanVien());
            } catch (SQLException _) {}
            if (nhanVien != null) {
                row.createCell(cellnum++).setCellValue(nhanVien.getMaNhanVien());
            } else {
                row.createCell(cellnum++).setCellValue("???"); // Or handle the null case appropriately
            }

            row.createCell(cellnum++).setCellValue(phieuNhap.getSoTienThu());

            // Định dạng ngày
            Cell dateCell = row.createCell(cellnum++);
            if (phieuNhap.getNgayLap() != null) {
                dateCell.setCellValue(phieuNhap.getNgayLap());
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
            new LapPhieuNhapDialog(nhanVienLoggedIn).showAndWait();
        }
        catch (IOException e) {
            e.printStackTrace();
            PopDialog.popErrorDialog("Không thể mở dialog thêm phiếu nhập", e.getMessage());
        }
    }

    private void updateListFromDatabase() {
        dsPhieuNhap.clear();
        try {
            dsPhieuNhap.addAll(PhieuNhapDAO.getInstance().QueryAll());
            filterList();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void filterList(){
        dsPhieuNhapFiltered.clear();
        dsPhieuNhapFiltered.addAll(filter.Filter());
    }
    private void resetFilter(){
        maPNTextField.clear();
        nvTextField.clear();
    }
}
