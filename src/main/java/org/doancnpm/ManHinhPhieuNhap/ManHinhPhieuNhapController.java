package org.doancnpm.ManHinhPhieuNhap;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.Duration;
import org.controlsfx.control.MasterDetailPane;
import org.doancnpm.DAO.*;
import org.doancnpm.Filters.PhieuNhapFilter;
import org.doancnpm.ManHinhPhieuThu.LapPhieuThuDialog;
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

public class ManHinhPhieuNhapController implements Initializable {

    @FXML private Region manHinhPhieuNhap;
    @FXML private TableView mainTableView;
    @FXML private TableView detailTableView;
    @FXML private Button filterButton;
    @FXML private MFXTextField nccTextField;
    @FXML private MFXTextField maPNTextField;
    @FXML private MFXTextField nvTextField;
    @FXML private Region filterPane;
    @FXML private Region filterPaneContainer;
    @FXML private Button toggleFilterButton;

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
    @FXML private FlowPane emptySelectionPane;

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
        initFilterPane();
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
        manHinhPhieuNhap.widthProperty().addListener(ob -> {
            if(manHinhPhieuNhap.getWidth()>1030){
                toggleDetailButton.setDisable(false);
                OpenDetailPanel();
            }else{
                toggleDetailButton.setDisable(true);
                CloseDetailPanel();
            }
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

        TableColumn<ChiTietPhieuNhap, String> donGiaNhapCol = new TableColumn<>("Đơn giá");
        donGiaNhapCol.setCellValueFactory(data -> {
            System.out.println("data.getValue().getDonGiaNhap() " + data.getValue().getDonGiaNhap());
            return new SimpleObjectProperty<>(MoneyFormatter.convertLongToString(data.getValue().getDonGiaNhap()));
        });

        TableColumn<ChiTietPhieuNhap, String> thanhTienCol = new TableColumn<>("Thành tiền ");
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
        toggleFilterButton.setOnAction(ob ->{
            if(filterPane.isVisible()){
                CloseFilterPanel();
            }
            else{
                OpenFilterPanel();
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
        TableColumn<PhieuNhap, String> maPNCol = new TableColumn<>("Mã");
        maPNCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMaPhieuNhap()));

        TableColumn<PhieuNhap, String> maNVCol = new TableColumn<>("Nhân viên");
        maNVCol.setCellValueFactory(new PropertyValueFactory<>("maNhanVien"));

        TableColumn<PhieuNhap, Integer> nccCol = new TableColumn<>("Nhà cung cấp");
        nccCol.setCellValueFactory(new PropertyValueFactory<>("nhaCungCap"));

        TableColumn<PhieuNhap, String> tongTienCol = new TableColumn<>("Tổng tiền");
        tongTienCol.setCellValueFactory(data->{
            return new SimpleStringProperty(MoneyFormatter.convertLongToString(data.getValue().getTongTien()));
        });

        TableColumn<PhieuNhap, Boolean> selectedCol = new TableColumn<>( );
        HBox headerBox = new HBox();
        CheckBox headerCheckBox = new CheckBox();
        headerBox.getChildren().add(headerCheckBox);
        headerBox.setAlignment(Pos.CENTER); // Center align the content
        headerCheckBox.setDisable(true);
        selectedCol.setGraphic(headerBox);
        selectedCol.setSortable(false);
        selectedCol.setCellValueFactory( new PropertyValueFactory<>( "selected" ));
        selectedCol.setCellFactory(new Callback<TableColumn<PhieuNhap, Boolean>, TableCell<PhieuNhap, Boolean>>() {
            @Override
            public TableCell<PhieuNhap, Boolean> call(TableColumn<PhieuNhap, Boolean> param) {
                TableCell<PhieuNhap, Boolean> cell = new TableCell<PhieuNhap, Boolean>() {
                    @Override
                    protected void updateItem(Boolean item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                        } else {
                            CheckBox checkBox = new CheckBox();
                            checkBox.selectedProperty().bindBidirectional(((PhieuNhap) getTableRow().getItem()).selectedProperty());
                            checkBox.getStyleClass().add("cell-center");
                            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                            setGraphic(checkBox);
                        }
                    }
                };
                cell.getStyleClass().add("cell-center");
                return cell;
            }
        });

        //action column
        TableColumn actionCol = new TableColumn("Action");

        Callback<TableColumn<PhieuNhap, String>, TableCell<PhieuNhap, String>> cellFactory
                = //
                new Callback<>() {
                    @Override
                    public TableCell call(final TableColumn<PhieuNhap, String> param) {
                        final TableCell<PhieuNhap, String> cell = new TableCell<PhieuNhap, String>() {
                            final Button suaBtn = new Button();
                            final Button xuatBtn = new Button();

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    Image edit = new Image(getClass().getResourceAsStream("/image/edit.png"));
                                    ImageView editImage = new ImageView(edit);
                                    Image xuat = new Image(getClass().getResourceAsStream("/image/exportPDF.png"));
                                    ImageView xuatImage = new ImageView(xuat);
                                    editImage.setFitWidth(20);
                                    editImage.setFitHeight(20);

                                    xuatImage.setFitWidth(20);
                                    xuatImage.setFitHeight(20);

                                    suaBtn.setGraphic(editImage);
                                    xuatBtn.setGraphic(xuatImage);

                                    suaBtn.setOnAction(_ -> {
                                        try {
                                            PhieuNhap phieuNhap = getTableView().getItems().get(getIndex());
                                            new LapPhieuNhapDialog(phieuNhap, nhanVienLoggedIn).showAndWait();
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
                maPNCol,
                maNVCol,
                nccCol,
                tongTienCol,
                actionCol
        );

        maPNCol.getStyleClass().add("column-header-left");
        maNVCol.getStyleClass().add("column-header-left");
        nccCol.getStyleClass().add("column-header-left");
        tongTienCol.getStyleClass().add("column-header-left");

        selectedCol.getStyleClass().add("column-header-center");
        actionCol.getStyleClass().add("column-header-center");

        mainTableView.setEditable(true);
        mainTableView.widthProperty().addListener(ob -> {
            double width = mainTableView.getWidth();
            selectedCol.setPrefWidth(width*0.1);
            maPNCol.setPrefWidth(width*0.13);
            maNVCol.setPrefWidth(width*0.13);
            nccCol.setPrefWidth(width*0.24);
            tongTienCol.setPrefWidth(width*0.25);
            actionCol.setPrefWidth(width*0.15);
        });
        mainTableView.setEditable( true );
        mainTableView.setPrefWidth(1100);

    }
    private void initFilterPane(){
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(filterPaneContainer.widthProperty());
        clip.heightProperty().bind(filterPaneContainer.heightProperty());
        filterPaneContainer.setClip(clip);
    }

    public void OpenFilterPanel(){
        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.2), filterPane);
        tt.setToX(0);
        tt.play();
        filterPane.setVisible(true);
        tt.setOnFinished(e -> {
        });
    }
    public void CloseFilterPanel(){
        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.2), filterPane);
        tt.setToX(-filterPane.getWidth());
        tt.play();

        tt.setOnFinished(e -> {
            filterPane.setVisible(false);
        });
    }
    //detail pane
    public void UpdateDetailPane(PhieuNhap phieuNhap){
        if(phieuNhap==null){
            emptySelectionPane.setVisible(true);
            return;
        }
        emptySelectionPane.setVisible(false);
        maPNText.setText(phieuNhap.getMaPhieuNhap());
        try{
            NhanVien nv = NhanVienDAO.getInstance().QueryID(phieuNhap.getMaNhanVien());
            maNVText.setText(nv.getMaNhanVien());
            tenNVText.setText(nv.getHoTen());
        }
        catch (SQLException _){}
        nccText.setText(phieuNhap.getNhaCungCap());
        ngayLapPhieuText.setText(DayFormat.GetDayStringFormatted(phieuNhap.getNgayLapPhieu()));
        tongTienText.setText(MoneyFormatter.convertLongToString(phieuNhap.getTongTien()));

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
        masterDetailPane.setShowDetailNode(true);

    }
    public void CloseDetailPanel(){
        masterDetailPane.setShowDetailNode(false);
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
            updateListFromDatabase();
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
