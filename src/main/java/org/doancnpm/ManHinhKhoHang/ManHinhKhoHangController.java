package org.doancnpm.ManHinhKhoHang;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleDoubleProperty;
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
import javafx.stage.FileChooser;
import javafx.util.Callback;

import javafx.util.Duration;
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
import org.doancnpm.Ultilities.CheckExist;
import org.doancnpm.Ultilities.DayFormat;
import org.doancnpm.Ultilities.MoneyFormatter;
import org.doancnpm.Ultilities.PopDialog;
import javafx.scene.text.Text;

import java.io.*;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.ResourceBundle;

public class ManHinhKhoHangController implements Initializable {
    @FXML private Region manHinhKhoHang;
    @FXML private Button filterButton;

    @FXML private TableView mainTableView;
    @FXML private MFXTextField maMHTextField;
    @FXML private MFXTextField tenMHTextField;
    @FXML private MFXComboBox<DonViTinh> dvtComboBox;
    @FXML private MFXComboBox<String> soLuongComboBox;
    @FXML private Region filterPane;
    @FXML private Region filterPaneContainer;
    @FXML private Button toggleFilterButton;

    @FXML
    private MenuItem addDirectButton;
    @FXML
    private MenuItem addExcelButton;
    @FXML
    private MenuItem exportExcelButton;

    @FXML
    private Text maMHText;
    @FXML
    private Text tenMHText;
    @FXML
    private Text dvtText;
    @FXML
    private Text donGiaNhapText;
    @FXML
    private Text donGiaXuatText;
    @FXML
    private Text soLuongText;
    @FXML
    private TextArea ghiChuTextArea;

    @FXML private FlowPane emptySelectionPane;

    @FXML private MasterDetailPane masterDetailPane;

    @FXML
    private Region masterPane;
    @FXML
    private Button toggleDetailButton;
    @FXML
    private Region detailPane;

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

        initFilterPane();
        //init data
    }

    public void setVisibility(boolean visibility) {
        manHinhKhoHang.setVisible(visibility);
    }

    public void setNhanVienLoggedIn(NhanVien nhanVienLoggedIn) {
        this.nhanVienLoggedIn = nhanVienLoggedIn;
    }

    //init
    private void initDetailPane() {
        masterDetailPane.setDetailNode(detailPane);
        masterDetailPane.setMasterNode(masterPane);

        masterDetailPane.widthProperty().addListener(ob -> {
            detailPane.setMinWidth(masterDetailPane.getWidth() * 0.3);
            detailPane.setMaxWidth(masterDetailPane.getWidth() * 0.3);
        });
        mainTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, matHang) -> {
            UpdateDetailPane((MatHang) matHang);
        });

        manHinhKhoHang.widthProperty().addListener(ob -> {
            if(manHinhKhoHang.getWidth()>1030){
                toggleDetailButton.setDisable(false);
                OpenDetailPanel();
            }else{
                toggleDetailButton.setDisable(true);
                CloseDetailPanel();
            }
        });
    }

    private void initEvent() {
        addDirectButton.setOnAction(_ -> {
            OpenDirectAddDialog();
        });
        addExcelButton.setOnAction(_ -> {
            importDialog();
        });
        exportExcelButton.setOnAction(_ -> {
            exportDialog();
        });
        toggleDetailButton.setOnAction(ob -> {
            if (masterDetailPane.isShowDetailNode()) {
                CloseDetailPanel();
            } else {
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
        MatHangDAO.getInstance().AddDatabaseListener(_ -> updateListFromDatabase());
    }

    private void initUIDataBinding() {
        mainTableView.setItems(dsMatHangFiltered);
        initFilterBinding();
    }

    private void initFilterBinding() {
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
            if (dvtComboBox.getValue() == null) {
                filter.setMaDVT(null);
            } else {
                filter.setMaDVT(dvtComboBox.getValue().getId());
            }
            filterList();
        });
        soLuongComboBox.valueProperty().addListener(_ -> {
            if (soLuongComboBox.getValue() == null) {
                filter.setTonKho(null);
            } else {
                if (soLuongComboBox.getValue().equals("Hết hàng")) {
                    filter.setTonKho(true);
                } else {
                    filter.setTonKho(false);
                }
            }
            filterList();

        });
    }

    private void initFilterComboboxData() {
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
        } catch (SQLException e) {
            PopDialog.popErrorDialog("Lấy dữ liệu các đơn vị tính thất bại", e.getMessage());
        }
        soLuongComboBox.getItems().addAll("Hết hàng", "Còn hàng");
    }
    private void initFilterPane(){
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(filterPaneContainer.widthProperty());
        clip.heightProperty().bind(filterPaneContainer.heightProperty());
        filterPaneContainer.setClip(clip);
    }
    private void initTableView() {
        // Tạo các cột cho TableView
        TableColumn<MatHang, String> maMHCol = new TableColumn<>("Mã");
        maMHCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMaMatHang()));

        TableColumn<MatHang, String> tenMHCol = new TableColumn<>("Tên mặt hàng");
        tenMHCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTenMatHang()));

        TableColumn<MatHang, String> dvtCol = new TableColumn<>("Đơn vị tính");
        dvtCol.setCellValueFactory(data -> {
            DonViTinh dvt = null;
            try {
                dvt = DonViTinhDAO.getInstance().QueryID(data.getValue().getMaDVT());
            } catch (SQLException _) {
            }
            return new SimpleObjectProperty<>(dvt.getTenDVT());
        });

        TableColumn<MatHang, String> donGiaNhapCol = new TableColumn<>("Đơn giá nhập");
        donGiaNhapCol.setCellValueFactory(data ->{
            return  new SimpleStringProperty(MoneyFormatter.convertLongToString(data.getValue().getDonGiaNhap()));
        });
        TableColumn<MatHang, String> donGiaXuatCol = new TableColumn<>("Đơn giá xuất");
        donGiaXuatCol.setCellValueFactory(data ->{
            return  new SimpleStringProperty(MoneyFormatter.convertLongToString(data.getValue().getDonGiaXuat()));
        });

        TableColumn<MatHang, Integer> soLuongCol = new TableColumn<>("Số lượng");
        soLuongCol.setCellValueFactory(data ->{
            return new SimpleObjectProperty<>(data.getValue().getSoLuong());
        });

        TableColumn<MatHang, Boolean> selectedCol = new TableColumn<>( );
        HBox headerBox = new HBox();
        CheckBox headerCheckBox = new CheckBox();
        headerBox.getChildren().add(headerCheckBox);
        headerBox.setAlignment(Pos.CENTER); // Center align the content
        headerCheckBox.setDisable(true);
        selectedCol.setGraphic(headerBox);
        selectedCol.setSortable(false);
        selectedCol.setCellValueFactory( new PropertyValueFactory<>( "selected" ));
        selectedCol.setCellFactory(new Callback<TableColumn<MatHang, Boolean>, TableCell<MatHang, Boolean>>() {
            @Override
            public TableCell<MatHang, Boolean> call(TableColumn<MatHang, Boolean> param) {
                TableCell<MatHang, Boolean> cell = new TableCell<MatHang, Boolean>() {
                    @Override
                    protected void updateItem(Boolean item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                        } else {
                            CheckBox checkBox = new CheckBox();
                            checkBox.selectedProperty().bindBidirectional(((MatHang) getTableRow().getItem()).selectedProperty());
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

        Callback<TableColumn<MatHang, String>, TableCell<MatHang, String>> cellFactory
                = //
                new Callback<>() {
                    @Override
                    public TableCell call(final TableColumn<MatHang, String> param) {
                        final TableCell<MatHang, String> cell = new TableCell<MatHang, String>() {
                            final Button suaBtn = new javafx.scene.control.Button();
                            final Button xoaBtn = new javafx.scene.control.Button();

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                }
                                else {
                                    Image trashCan = new Image(getClass().getResourceAsStream("/image/trash_can.png"));
                                    ImageView trashImage = new ImageView(trashCan);
                                    Image edit = new Image(getClass().getResourceAsStream("/image/edit.png"));
                                    ImageView editImage = new ImageView(edit);
                                    trashImage.setFitWidth(20);
                                    trashImage.setFitHeight(20);

                                    editImage.setFitWidth(20);
                                    editImage.setFitHeight(20);

                                    xoaBtn.setGraphic(trashImage);
                                    suaBtn.setGraphic(editImage);

                                    xoaBtn.setOnAction(event -> {
                                        MatHang mh = getTableView().getItems().get(getIndex());
                                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                        alert.setTitle("Xóa đại lý");
                                        alert.setHeaderText("Xác nhận xóa mặt hàng " + mh.getTenMatHang() + " ?");

                                        Optional<ButtonType> result = alert.showAndWait();
                                        if (result.get() == ButtonType.OK) {
                                            try {
                                                MatHangDAO.getInstance().Delete(mh.getID());
                                                PopDialog.popSuccessDialog("Xóa mặt hàng "+ mh.getTenMatHang() + " thành công");
                                            }
                                            catch (SQLException e) {
                                                PopDialog.popErrorDialog("Xóa mặt hàng "+ mh.getTenMatHang() + " thất bại",e.getMessage());
                                            }
                                        }
                                    });
                                    suaBtn.setOnAction(_ -> {
                                        try {
                                            MatHang matHang = getTableView().getItems().get(getIndex());
                                            new ThemMoiMatHangDialog(matHang).showAndWait();
                                        } catch(IOException exc) {
                                            PopDialog.popErrorDialog("Không thể mở dialog thêm mặt hàng");
                                        }
                                    });
                                    HBox hbox = new HBox();
                                    hbox.getChildren().addAll(suaBtn, xoaBtn);
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

        maMHCol.getStyleClass().add("column-header-left");
        tenMHCol.getStyleClass().add("column-header-left");
        dvtCol.getStyleClass().add("column-header-left");
        donGiaNhapCol.getStyleClass().add("column-header-left");
        soLuongCol.getStyleClass().add("column-header-left");

        selectedCol.getStyleClass().add("column-header-center");
        actionCol.getStyleClass().add("column-header-center");

        mainTableView.setEditable(true);
        mainTableView.widthProperty().addListener(ob -> {
            double width = mainTableView.getWidth();
            selectedCol.setPrefWidth(width*0.1);
            maMHCol.setPrefWidth(width*0.12);
            dvtCol.setPrefWidth(width*0.12);
            tenMHCol.setPrefWidth(width*0.24);
            donGiaNhapCol.setPrefWidth(width*0.15);
            soLuongCol.setPrefWidth(width*0.12);
            actionCol.setPrefWidth(width*0.15);
        });
        mainTableView.setEditable(true);
        mainTableView.setPrefWidth(1100);

    }

    //detail pane
    public void UpdateDetailPane(MatHang matHang){
        if(matHang==null){
            emptySelectionPane.setVisible(true);
            return;
        }
        emptySelectionPane.setVisible(false);
        maMHText.setText(matHang.getMaMatHang());
        tenMHText.setText(matHang.getTenMatHang());
        try {
            DonViTinh dvt = DonViTinhDAO.getInstance().QueryID(matHang.getMaDVT());
            dvtText.setText(dvt.getTenDVT());
            donGiaNhapText.setText(MoneyFormatter.convertLongToString(matHang.getDonGiaNhap()));
            donGiaXuatText.setText(MoneyFormatter.convertLongToString(matHang.getDonGiaXuat()));
            soLuongText.setText(Integer.toString(matHang.getSoLuong()));
            ghiChuTextArea.setText(matHang.getGhiChu());
        } catch (SQLException _) {
        }
    }
    public void OpenDetailPanel(){
        masterDetailPane.setShowDetailNode(true);

    }

    public void CloseDetailPanel() {
        masterDetailPane.setShowDetailNode(false);
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

    public void importFromExcel(String filePath) {
        File file = new File(filePath);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            PopDialog.popErrorDialog("Không thể mở file excel");
            return;
        }

        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(fis);
        } catch (IOException e) {
            PopDialog.popErrorDialog("Có lỗi trong quá trình thực hiện", e.getMessage());
            return;
        }
        XSSFSheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet
        boolean hasError = true; // Biến để theo dõi nếu có lỗi xảy ra
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) { // Kiểm tra xem dòng có tồn tại hay không
                Cell tenMatHangCell = row.getCell(0);
                Cell donViTinhCell = row.getCell(1);
                Cell donGiaNhapCell = row.getCell(2);
                Cell ghiChuCell = row.getCell(3);

                MatHang matHang = new MatHang();
                String donViTinh = donViTinhCell.getStringCellValue().trim();

                Integer dvtID = handleDVT(donViTinh);
                if (dvtID == null) {
                   continue;
                }
                else{
                    hasError = false;
                }
                matHang.setMaDVT(dvtID);

                matHang.setTenMatHang(tenMatHangCell.getStringCellValue());
                matHang.setDonGiaNhap((long)(donGiaNhapCell.getNumericCellValue()));
                if (ghiChuCell != null) {
                    matHang.setGhiChu(ghiChuCell.getStringCellValue());
                } else {
                    matHang.setGhiChu(null);
                }
                matHang.setSoLuong(0);
                try {
                    MatHangDAO.getInstance().Insert(matHang); // Thêm đối tượng vào cơ sở dữ liệu
                } catch (SQLException e) {
                    PopDialog.popErrorDialog("Thêm mới mặt hàng thất bại", e.getMessage());
                    return;
                }
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

    private Integer handleDVT(String dvtName) {
        try {
            if (!CheckExist.checkDVT(dvtName)) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Xác nhận");
                alert.setHeaderText(null);
                alert.setContentText("Loại đơn vị tính: " + dvtName + " không tồn tại. Bạn có muốn thêm đơn vị tính mới không?");

                ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);

                if (result == ButtonType.OK) {
                    DonViTinh dvt = new DonViTinh();
                    dvt.setTenDVT(dvtName);// Tạo một đối tượng mới cho đơn vị tính
                    DonViTinhDAO.getInstance().Insert(dvt); // Thêm đơn vị tính mới vào cơ sở dữ liệu
                    Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
                    infoAlert.setTitle("Thông báo");
                    infoAlert.setHeaderText(null);
                    infoAlert.setContentText("Đơn vị tính " + dvtName + " đã được thêm thành công!");
                    infoAlert.showAndWait();
                    return DonViTinhDAO.getInstance().QueryMostRecent().getId(); // Trả về ID của đơn vị tính mới
                } else {
                    return null;
                }
            } else {
                return DonViTinhDAO.getInstance().QueryName(dvtName).getId(); // Trả về ID của đơn vị tính đã tồn tại
            }
        } catch (SQLException e) {
            PopDialog.popErrorDialog("Có lỗi trong quá trình xử lý đơn vị tính", e.getMessage());
            return null;
        }
    }

    public void exportDialog() {
        // Hiển thị hộp thoại lưu tệp Excel
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));

        // Tạo tên file với định dạng "Export_ngay_thang_nam.xlsx"
        Date ngayHienTai = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy");
        String fileName = "DsMatHang_" + dateFormat.format(ngayHienTai) + ".xlsx";

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
        // Tạo hoặc mở tệp Excel
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("MatHangData"); // Tạo một sheet mới hoặc sử dụng sheet hiện có

        // Tạo hàng đầu tiên với các tiêu đề cột
        Row headerRow = sheet.createRow(0);
        String[] columnTitles = {"Mã mặt hàng", "Tên mặt hàng", "Đơn vị tính", "Đơn giá nhập", "Đơn giá xuất", "Số lượng", "Ghi chú"};
        int cellnum = 0;
        for (String title : columnTitles) {
            Cell cell = headerRow.createCell(cellnum++);
            cell.setCellValue(title);
        }

        int rownum = 1; // Bắt đầu từ hàng thứ 2 sau tiêu đề
        for (MatHang matHang : dsMatHangFiltered) {
            Row row = sheet.createRow(rownum++);
            cellnum = 0;
            row.createCell(cellnum++).setCellValue(matHang.getMaMatHang());
            row.createCell(cellnum++).setCellValue(matHang.getTenMatHang());

            DonViTinh donViTinh = new DonViTinh();
            try {
                donViTinh = DonViTinhDAO.getInstance().QueryID(matHang.getMaDVT());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if (donViTinh != null) {
                row.createCell(cellnum++).setCellValue(donViTinh.getTenDVT());
            } else {
                row.createCell(cellnum++).setCellValue("???");
            }

            row.createCell(cellnum++).setCellValue(matHang.getDonGiaNhap());
            row.createCell(cellnum++).setCellValue(matHang.getDonGiaXuat());
            row.createCell(cellnum++).setCellValue(matHang.getSoLuong());
            row.createCell(cellnum++).setCellValue(matHang.getGhiChu());
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

    //functionalities
    public void OpenDirectAddDialog() {
        try {
            new ThemMoiMatHangDialog().showAndWait();
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

    private void filterList() {
        dsMatHangFiltered.clear();
        dsMatHangFiltered.addAll(filter.Filter());
    }

    private void resetFilter() {
        maMHTextField.clear();
        tenMHTextField.clear();
        dvtComboBox.clearSelection();
        dvtComboBox.clear();
        soLuongComboBox.clearSelection();
        dvtComboBox.clear();
    }
}
