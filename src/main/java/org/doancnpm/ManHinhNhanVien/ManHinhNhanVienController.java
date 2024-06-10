package org.doancnpm.ManHinhNhanVien;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
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
import javafx.util.StringConverter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.controlsfx.control.MasterDetailPane;
import org.doancnpm.DAO.ChucVuDAO;
import org.doancnpm.DAO.DonViTinhDAO;
import org.doancnpm.DAO.NhanVienDAO;
import org.doancnpm.DAO.QuanDAO;
import org.doancnpm.Filters.NhanVienFilter;
import org.doancnpm.Models.ChucVu;
import org.doancnpm.Models.DaiLy;
import org.doancnpm.Models.DonViTinh;
import org.doancnpm.Models.NhanVien;
import org.doancnpm.Models.Quan;
import org.doancnpm.Ultilities.CheckExist;
import org.doancnpm.Ultilities.DayFormat;
import org.doancnpm.Ultilities.MoneyFormatter;
import org.doancnpm.Ultilities.PopDialog;

import java.io.*;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;

/** Controls the main application screen */
public class ManHinhNhanVienController implements Initializable {

    @FXML private Region manHinhNhanVien;
    @FXML private Button filterButton;
    @FXML private MenuItem addDirectButton;
    @FXML private MenuItem addExcelButton;
    @FXML private MenuItem deleteSelectedButton;
    @FXML private MenuItem exportExcelButton;

    @FXML private MFXTextField maNVTextField;
    @FXML private MFXTextField tenNVTextField;
    @FXML private MFXComboBox<ChucVu> chucVuComboBox;
    @FXML private Region filterPane;
    @FXML private Region filterPaneContainer;
    @FXML private Button toggleFilterButton;

    @FXML private TableView mainTableView;

    //MasterDetailPane
    @FXML private MasterDetailPane masterDetailPane;

    @FXML private Region masterPane;
    @FXML private Button toggleDetailButton;
    @FXML private Region detailPane;

    @FXML private Text maNVText;
    @FXML private Text tenNVText;
    @FXML private Text gioiTinhText;
    @FXML private Text ngaySinhText;
    @FXML private Text sdtText;
    @FXML private Text emailText;
    @FXML private Text chucVuText;
    @FXML private Text luongText;
    @FXML private TextArea ghiChuTextArea;

    @FXML private FlowPane emptySelectionPane;

    //model part
    private final ObservableList<NhanVien> dsNhanVien = FXCollections.observableArrayList();
    private final ObservableList<NhanVien> dsNhanVienFiltered = FXCollections.observableArrayList();
    private final NhanVienFilter filter = new NhanVienFilter();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //init UI and event
        initTableView();
        initEvent();

        //init binding
        initDatabaseBinding();
        initUIDataBinding();

        //init data
        updateListFromDatabase();
        initDetailPane();
        initFilterPane();
    }
    public void setVisibility(boolean visibility){
        manHinhNhanVien.setVisible(visibility);
    }

    //init
    private void initDetailPane(){
        masterDetailPane.setDetailNode(detailPane);
        masterDetailPane.setMasterNode(masterPane);

        masterDetailPane.widthProperty().addListener(ob ->{
            detailPane.setMinWidth(masterDetailPane.getWidth()*0.3);
            detailPane.setMaxWidth(masterDetailPane.getWidth()*0.3);
        });
        mainTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, nhanVien) -> {
            UpdateDetailPane((NhanVien) nhanVien);
        });
        manHinhNhanVien.widthProperty().addListener(ob -> {
            if(manHinhNhanVien.getWidth()>1030){
                toggleDetailButton.setDisable(false);
                OpenDetailPanel();
            }else{
                toggleDetailButton.setDisable(true);
                CloseDetailPanel();
            }
        });
    }
    private void initTableView(){
        // Tạo các cột cho TableView
        TableColumn<NhanVien, String> maNVCol = new TableColumn<>("Mã");
        maNVCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMaNhanVien()));

        TableColumn<NhanVien, String> tenNVCol = new TableColumn<>("Họ tên");
        tenNVCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getHoTen()));

        TableColumn<NhanVien, String> gioiTinhCol = new TableColumn<>("Giới tính");
        gioiTinhCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGioiTinh()));

        TableColumn<NhanVien, String> luongCol = new TableColumn<>("Lương");
        luongCol.setCellValueFactory(data->{
            return new SimpleStringProperty(MoneyFormatter.convertLongToString(data.getValue().getLuong()));
        });

        TableColumn<NhanVien, String> chucVuCol = new TableColumn<>("Chức vụ");
        chucVuCol.setCellValueFactory(data -> {
            ChucVu chucVu = null;
            try {
                chucVu = ChucVuDAO.getInstance().QueryID(data.getValue().getMaChucVu());
            } catch (SQLException _) {}
            return new SimpleObjectProperty<>(chucVu.getTenCV());
        });
        //selected collumn:
        TableColumn<NhanVien, Boolean> selectedCol = new TableColumn<>( );
        HBox headerBox = new HBox();
        CheckBox headerCheckBox = new CheckBox();
        headerBox.getChildren().add(headerCheckBox);
        headerBox.setAlignment(Pos.CENTER); // Center align the content
        headerCheckBox.setDisable(true);
        selectedCol.setGraphic(headerBox);
        selectedCol.setSortable(false);
        selectedCol.setCellValueFactory( new PropertyValueFactory<>( "selected" ));
        selectedCol.setCellFactory(new Callback<TableColumn<NhanVien, Boolean>, TableCell<NhanVien, Boolean>>() {
            @Override
            public TableCell<NhanVien, Boolean> call(TableColumn<NhanVien, Boolean> param) {
                TableCell<NhanVien, Boolean> cell = new TableCell<NhanVien, Boolean>() {
                    @Override
                    protected void updateItem(Boolean item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                        } else {
                            CheckBox checkBox = new CheckBox();
                            checkBox.selectedProperty().bindBidirectional(((NhanVien) getTableRow().getItem()).selectedProperty());
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

        Callback<TableColumn<NhanVien, String>, TableCell<NhanVien, String>> cellFactory
                = //
                new Callback<>() {
                    @Override
                    public TableCell call(final TableColumn<NhanVien, String> param) {
                        final TableCell<NhanVien, String> cell = new TableCell<NhanVien, String>() {
                            final Button suaBtn = new Button();
                            final Button xoaBtn = new Button();

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
                                        NhanVien nv = getTableView().getItems().get(getIndex());
                                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                        alert.setTitle("Xóa nhân viên");
                                        alert.setHeaderText("Bạn có chắc chắn muốn xóa nhân viên " + nv.getMaNhanVien() + " ?");

                                        Optional<ButtonType> result = alert.showAndWait();
                                        if (result.get() == ButtonType.OK){
                                            try {
                                                NhanVienDAO.getInstance().Delete(nv.getID());
                                                PopDialog.popSuccessDialog("Xóa nhân viên "+ nv.getMaNhanVien() + " thành công");
                                            }
                                            catch (SQLException e) {
                                                PopDialog.popErrorDialog("Xóa nhân viên "+ nv.getMaNhanVien() + " thất bại",e.getMessage());
                                            }
                                        }
                                    });
                                    suaBtn.setOnAction(_ -> {
                                        try {
                                            NhanVien nhanVien = getTableView().getItems().get(getIndex());
                                            new TiepNhanNhanVienDialog(nhanVien).showAndWait();
                                        } catch(IOException exc) {
                                            exc.printStackTrace();
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
        mainTableView.getColumns().addAll(selectedCol,
                maNVCol,
                tenNVCol,
                gioiTinhCol,
                luongCol,
                chucVuCol,
                actionCol
        );
        selectedCol.setResizable(false);
        maNVCol.setResizable(false);
        tenNVCol.setResizable(false);
        gioiTinhCol.setResizable(false);
        luongCol.setResizable(false);
        chucVuCol.setResizable(false);
        actionCol.setResizable(false);

        maNVCol.getStyleClass().add("column-header-left");
        tenNVCol.getStyleClass().add("column-header-left");
        gioiTinhCol.getStyleClass().add("column-header-left");
        luongCol.getStyleClass().add("column-header-left");
        chucVuCol.getStyleClass().add("column-header-left");

        selectedCol.getStyleClass().add("column-header-center");
        actionCol.getStyleClass().add("column-header-center");

        mainTableView.widthProperty().addListener(ob -> {
            double width = mainTableView.getWidth();
            selectedCol.setPrefWidth(width*0.1);
            maNVCol.setPrefWidth(width*0.13);
            tenNVCol.setPrefWidth(width*0.24);
            gioiTinhCol.setPrefWidth(width*0.1);
            chucVuCol.setPrefWidth(width*0.13);
            luongCol.setPrefWidth(width*0.15);
            actionCol.setPrefWidth(width*0.15);
        });
        mainTableView.setEditable( true );
        mainTableView.setPrefWidth(1100);
    }
    private void initEvent(){
        addDirectButton.setOnAction(_ -> {
            OpenDirectAddDialog();
        });
        deleteSelectedButton.setOnAction(_ -> DeleteSelectedRow());

        addExcelButton.setOnAction(_ -> {
            OpenImportDialog();
        });
        exportExcelButton.setOnAction(_ -> {
            OpenExportDialog();
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
    private void initDatabaseBinding(){
        NhanVienDAO.getInstance().AddDatabaseListener(_ -> updateListFromDatabase());
    }
    private void initUIDataBinding(){
        mainTableView.setItems(dsNhanVienFiltered);
        initFilterBinding();
        initFilterComboboxData();
    }
    private void initFilterBinding(){
        filter.setInput(dsNhanVien);
        maNVTextField.textProperty().addListener(_ -> {
            filter.setMaNhanVien(maNVTextField.getText());
            filterList();
        });
        tenNVTextField.textProperty().addListener(_ -> {
            filter.setTenNhanVien(tenNVTextField.getText());
            filterList();
        });
        chucVuComboBox.valueProperty().addListener(_ -> {
            if(chucVuComboBox.getValue() == null){
                filter.setMaChucVu(null);
            }
            else {
                filter.setMaChucVu(chucVuComboBox.getValue().getId());
            }
            filterList();
        });
    }
    private void initFilterComboboxData(){
        try {
            ObservableList<ChucVu> chucVus = FXCollections.observableArrayList(ChucVuDAO.getInstance().QueryAll());

            // Sử dụng StringConverter để hiển thị dữ liệu trong ComboBox
            StringConverter<ChucVu> quanStringConverter = new StringConverter<ChucVu>() {
                @Override
                public String toString(ChucVu chucVu) {
                    return chucVu == null ? null : chucVu.getTenCV(); //lên hình
                }

                @Override
                public ChucVu fromString(String string) {
                    return null; //Từ hình xuống data
                }
            };
            // Đặt StringConverter cho ComboBox
            chucVuComboBox.setConverter(quanStringConverter);

            // Đặt DataSource cho ComboBox
            chucVuComboBox.setItems(chucVus);
        }
        catch (SQLException e) {
            PopDialog.popErrorDialog("Lấy dữ liệu chức vụ thất bại",e.getMessage());
        }
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
    public void UpdateDetailPane(NhanVien nhanVien){
        if(nhanVien==null){
            emptySelectionPane.setVisible(true);
            return;
        }
        try{
            emptySelectionPane.setVisible(false);
            ChucVu chucVu = ChucVuDAO.getInstance().QueryID(nhanVien.getMaChucVu());

            tenNVText.setText(nhanVien.getHoTen());
            maNVText.setText(nhanVien.getMaNhanVien());
            chucVuText.setText(chucVu.getTenCV());
            sdtText.setText(nhanVien.getSDT());
            emailText.setText(nhanVien.getEmail());
            luongText.setText(MoneyFormatter.convertLongToString(nhanVien.getLuong()));
            ngaySinhText.setText(DayFormat.GetDayStringFormatted(nhanVien.getNgaySinh()));
            gioiTinhText.setText(nhanVien.getGioiTinh());
            ghiChuTextArea.setText(nhanVien.getGhiChu());
        }
        catch (SQLException _){}

    }
    public void OpenDetailPanel(){
        masterDetailPane.setShowDetailNode(true);

    }
    public void CloseDetailPanel(){
        masterDetailPane.setShowDetailNode(false);
    }


    //open import dialog
    public void OpenImportDialog() {
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
    private void importFromExcel(String filePath)  {
        File file = new File(filePath);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        }
        catch (FileNotFoundException e) {
            PopDialog.popErrorDialog("Không tìm thấy file excel");
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

        boolean hasError = true; // Biến để theo dõi nếu có lỗi xảy ra
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) { // Kiểm tra xem dòng có tồn tại hay không
                Cell hoTenCell = row.getCell(0);
                Cell gioiTinhCell = row.getCell(1);
                Cell ngaySinhCell = row.getCell(2);
                Cell SDTCell = row.getCell(3);
                Cell emailCell = row.getCell(4);
                Cell chucVuCell = row.getCell(5);
                Cell luongCell = row.getCell(6);
                Cell ghiChuCell = row.getCell(7);

                NhanVien nhanVien = new NhanVien();
                nhanVien.setHoTen(hoTenCell.getStringCellValue());
                nhanVien.setGioiTinh(gioiTinhCell.getStringCellValue());
                // Assuming ngaySinhCell is already defined and contains a date string
                String ngaySinh = ngaySinhCell.getStringCellValue();

                // Define the date format according to the format in the Excel file
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");// Adjust format as needed

                try {
                    // Parse the string into a java.util.Date object
                    java.util.Date utilDate = dateFormat.parse(ngaySinh);

                    // Convert the java.util.Date object into a java.sql.Date object
                    java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

                    // Set the date to the nhanVien object
                    nhanVien.setNgaySinh(sqlDate);

                } catch (ParseException e) {
                    // Handle the exception if the date format is incorrect
                    e.printStackTrace();
                }
                nhanVien.setSDT(SDTCell.getStringCellValue());
                nhanVien.setEmail(emailCell.getStringCellValue());

                String chucVuName = chucVuCell.getStringCellValue().trim();
                Integer chucVuID = handleChucVu(chucVuName);
                if (chucVuID == null) {
                    continue;
                }
                else{
                    hasError = false;
                }
                nhanVien.setMaChucVu(chucVuID);

                nhanVien.setLuong(luongCell.getNumericCellValue());
                if (ghiChuCell != null) {
                    nhanVien.setGhiChu(ghiChuCell.getStringCellValue());
                } else {
                    nhanVien.setGhiChu(null);
                }
                try {
                    NhanVienDAO.getInstance().Insert(nhanVien); // Thêm đối tượng vào cơ sở dữ liệu
                }
                catch (SQLException e) {
                    PopDialog.popErrorDialog("Thêm mới nhân viên thất bại", e.getMessage());
                    return;
                }
            }
        }
        try {
            workbook.close();
            fis.close();
            if (!hasError) { // Chỉ hiển thị dialog thành công nếu không có lỗi nào
                PopDialog.popSuccessDialog("Thêm danh sách nhân viên từ file excel thành công");
            }
            else{
                PopDialog.popErrorDialog("Thêm danh sách nhân viên từ file excel không thành công");
            }
        }
        catch (IOException e) {
            PopDialog.popErrorDialog("Có lỗi trong quá trình thực hiện", e.getMessage());
        }
    }
    private Integer handleChucVu(String chucVuName) {
        try {
            if (!CheckExist.checkChucVu(chucVuName)) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Xác nhận");
                alert.setHeaderText(null);
                alert.setContentText("Chức vụ '"+chucVuName + "' không tồn tại. Bạn có muốn thêm chức vụ mới không?");

                ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);

                if (result == ButtonType.OK) {
                    ChucVu CV = new ChucVu();
                    CV.setTenCV(chucVuName);// Tạo một đối tượng mới cho đơn vị tính
                    ChucVuDAO.getInstance().Insert(CV); // Thêm đơn vị tính mới vào cơ sở dữ liệu
                    Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
                    infoAlert.setTitle("Thông báo");
                    infoAlert.setHeaderText(null);
                    infoAlert.setContentText("Chức vụ '" + chucVuName + "' đã được thêm thành công!");
                    infoAlert.showAndWait();
                    return ChucVuDAO.getInstance().QueryMostRecent().getId(); // Trả về ID của đơn vị tính mới
                } else {
                    PopDialog.popErrorDialog("Chức vụ '" + chucVuName + "' không tồn tại");
                    return null;
                }
            } else {
                return ChucVuDAO.getInstance().QueryName(chucVuName).getId(); // Trả về ID của đơn vị tính đã tồn tại
            }
        } catch (SQLException e) {
            PopDialog.popErrorDialog("Có lỗi trong quá trình xử lý chức vụ", e.getMessage());
            return null;
        }
    }
    public void OpenExportDialog() {
        // Hiển thị hộp thoại lưu tệp Excel
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));

        // Tạo tên file với định dạng "Export_ngay_thang_nam.xlsx"
        Date ngayHienTai = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy");
        String fileName = "DsNhanVien_" + dateFormat.format(ngayHienTai) + ".xlsx";

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
        XSSFSheet sheet = workbook.createSheet("NhanVienData"); // Tạo một sheet mới hoặc sử dụng sheet hiện có

        // Tạo hàng đầu tiên với các tiêu đề cột
        Row headerRow = sheet.createRow(0);
        String[] columnTitles = {"Mã nhân viên", "Họ tên", "Giới tính", "Ngày sinh", "Số điện thoại", "Email", "Chức vụ", "Lương", "Ghi chú"};
        int cellnum = 0;
        for (String title : columnTitles) {
            Cell cell = headerRow.createCell(cellnum++);
            cell.setCellValue(title);
        }

        // Duyệt qua danh sách dsNhanVienFiltered và ghi dữ liệu vào tệp Excel
        int rownum = 1; // Bắt đầu từ hàng thứ 2 sau tiêu đề
        for (NhanVien nhanVien : dsNhanVienFiltered) {
            Row row = sheet.createRow(rownum++);
            cellnum = 0;
            row.createCell(cellnum++).setCellValue(nhanVien.getMaNhanVien());
            row.createCell(cellnum++).setCellValue(nhanVien.getHoTen());
            row.createCell(cellnum++).setCellValue(nhanVien.getGioiTinh());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            row.createCell(cellnum++).setCellValue(dateFormat.format(nhanVien.getNgaySinh()));
            row.createCell(cellnum++).setCellValue(nhanVien.getSDT());
            row.createCell(cellnum++).setCellValue(nhanVien.getEmail());
            ChucVu chucVu;
            try{
                chucVu=ChucVuDAO.getInstance().QueryID(nhanVien.getMaChucVu());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if (chucVu!=null){
                row.createCell(cellnum++).setCellValue(chucVu.getTenCV());
            }
            else{
                row.createCell(cellnum++).setCellValue("???");
            }
            row.createCell(cellnum++).setCellValue(nhanVien.getLuong());
            row.createCell(cellnum++).setCellValue(nhanVien.getGhiChu());
        }
        // Tự động điều chỉnh độ rộng của các cột
        for (int i = 0; i < columnTitles.length; i++) {
            sheet.autoSizeColumn(i);
        }
        // Lưu tệp Excel
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            workbook.write(fos);
            workbook.close();
        }
        catch (IOException e){
            PopDialog.popErrorDialog("Xuất file excel thất bại",e.getMessage());
        }
    }

    //functionalities
    public void OpenDirectAddDialog(){
        try {
            new TiepNhanNhanVienDialog().showAndWait();
        }
        catch (IOException e) {
            PopDialog.popErrorDialog("Không thể mở dialog thêm nhân viên");
        }
    }
    public void DeleteSelectedRow()  {
        final Set<NhanVien> del = new HashSet<>();
        for( Object o : mainTableView.getItems()) {
            NhanVien dl = (NhanVien) o;
            if( dl.isSelected()) {
                del.add( dl );
            }
        }
        if(del.isEmpty()){
            PopDialog.popSuccessDialog("Bạn chưa chọn các nhân viên cần xóa!");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xóa nhân viên");
        alert.setHeaderText("Bạn có chắc chắn muốn xóa " +del.size()+" nhân viên?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            for(NhanVien dl : del){
                int ID = dl.getID();
                try {
                    NhanVienDAO.getInstance().Delete(ID);
                } catch (SQLException e) {
                    PopDialog.popErrorDialog("Xóa nhân viên " + dl.getMaNhanVien() + " thất bại",e.getMessage());
                    return;
                }
            }
        }
        PopDialog.popSuccessDialog("Xóa "+del.size()+" nhân viên thành công");
    }

    private void updateListFromDatabase() {
        dsNhanVien.clear();
        try {
            dsNhanVien.addAll(NhanVienDAO.getInstance().QueryAll());
            filterList();
        } catch (SQLException e) {
            PopDialog.popErrorDialog("Load dữ liệu nhân viên thất bại",e.getMessage());
        }
    }
    private void filterList(){
        dsNhanVienFiltered.clear();
        dsNhanVienFiltered.addAll(filter.Filter());
    }
    private void resetFilter(){
        chucVuComboBox.clearSelection();
        chucVuComboBox.clear();
        maNVTextField.clear();
        tenNVTextField.clear();
    }
}
