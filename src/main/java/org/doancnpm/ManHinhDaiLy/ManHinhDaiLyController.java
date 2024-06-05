package org.doancnpm.ManHinhDaiLy;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import org.apache.poi.ss.usermodel.Cell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.controlsfx.control.MasterDetailPane;
import org.doancnpm.DAO.DaiLyDAO;
import org.doancnpm.DAO.LoaiDaiLyDAO;
import org.doancnpm.DAO.QuanDAO;
import org.doancnpm.Filters.DaiLyFilter;
import org.doancnpm.Models.DaiLy;
import org.doancnpm.Models.LoaiDaiLy;
import org.doancnpm.Models.Quan;
import org.doancnpm.Ultilities.DayFormat;
import org.doancnpm.Ultilities.MoneyFormatter;
import org.doancnpm.Ultilities.PopDialog;

import java.io.*;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

/** Controls the main application screen */
public class ManHinhDaiLyController implements Initializable {

    @FXML private Region manHinhDaiLy;
    @FXML private Button filterButton;
    @FXML private MenuItem addDirectButton;
    @FXML private MenuItem addExcelButton;
    @FXML private Button lapPhieuThuTienButton;
    @FXML private MenuItem deleteSelectedButton;
    @FXML private MenuItem exportExcelButton;

    @FXML private TextField maDaiLyTextField;
    @FXML private TextField tenDaiLyTextField;
    @FXML private ComboBox<Quan> quanComboBox;
    @FXML private ComboBox<LoaiDaiLy> loaiDaiLyCombobox;


    @FXML private TableView mainTableView;

    //MasterDetailPane
    @FXML private MasterDetailPane masterDetailPane;

    @FXML private Region masterPane;
    @FXML private Button toggleDetailButton;
    @FXML private Region detailPane;

    @FXML private Text tenDaiLyText;
    @FXML private Text maDaiLyText;
    @FXML private Text maQuanText;
    @FXML private Text maLoaiText;
    @FXML private Text sdtText;
    @FXML private Text emailText;
    @FXML private Text diaChiText;
    @FXML private Text ngayTiepNhanText;
    @FXML private Text noHienTaiText;

    @FXML private TextArea ghiChuTextArea;

    //model part
    private final ObservableList<DaiLy> dsDaiLy = FXCollections.observableArrayList();
    private final ObservableList<DaiLy> dsDaiLyFiltered = FXCollections.observableArrayList();
    private final DaiLyFilter filter = new DaiLyFilter();

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
    }
    public void setVisibility(boolean visibility){
        manHinhDaiLy.setVisible(visibility);
    }

    //init
    private void initDetailPane(){
        masterDetailPane.setDetailNode(detailPane);
        masterDetailPane.setMasterNode(masterPane);

        masterDetailPane.widthProperty().addListener(ob ->{
            detailPane.setMinWidth(masterDetailPane.getWidth()*0.3);
            detailPane.setMaxWidth(masterDetailPane.getWidth()*0.3);
        });
        mainTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, daiLy) -> {
            UpdateDetailPane((DaiLy) daiLy);
        });
    }
    private void initTableView(){
        // Tạo các cột cho TableView
        TableColumn<DaiLy, String> maDLCol = new TableColumn<>("Mã đại lý");
        maDLCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMaDaiLy()));


        TableColumn<DaiLy, String> quanCol = new TableColumn<>("Quận");
        quanCol.setCellValueFactory(data -> {
            Quan quan = null;
            try {
                quan = QuanDAO.getInstance().QueryID(data.getValue().getMaQuan());
            } catch (SQLException _) {}

            return new SimpleObjectProperty<>(quan.getTenQuan());
        });

        TableColumn<DaiLy, String> loaiDLCol = new TableColumn<>("Loại");
        loaiDLCol.setCellValueFactory(data -> {
            LoaiDaiLy loaiDaiLy = null;
            try {
                loaiDaiLy = LoaiDaiLyDAO.getInstance().QueryID(data.getValue().getMaLoaiDaiLy());
            } catch (SQLException _) {

            }
            return new SimpleObjectProperty<>(loaiDaiLy.getTenLoai());
        });

        TableColumn<DaiLy, String> tenDLCol = new TableColumn<>("Tên đại lý");
        tenDLCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTenDaiLy()));

        TableColumn<DaiLy, String> noHienTaiCol = new TableColumn<>("Nợ hiện tại");
        noHienTaiCol.setCellValueFactory(data -> {
            return new SimpleStringProperty(MoneyFormatter.ConvertLongToString(data.getValue().getNoHienTai()));
        });

        //selected collumn:

        TableColumn<DaiLy, Boolean> selectedCol = new TableColumn<>( );
        HBox headerBox = new HBox();
        CheckBox headerCheckBox = new CheckBox();
        headerBox.getChildren().add(headerCheckBox);
        headerBox.setAlignment(Pos.CENTER); // Center align the content
        headerCheckBox.setDisable(true);
        selectedCol.setGraphic(headerBox);
        selectedCol.setSortable(false);
        selectedCol.setCellValueFactory( new PropertyValueFactory<>( "selected" ));
        selectedCol.setCellFactory(new Callback<TableColumn<DaiLy, Boolean>, TableCell<DaiLy, Boolean>>() {
            @Override
            public TableCell<DaiLy, Boolean> call(TableColumn<DaiLy, Boolean> param) {
                TableCell<DaiLy, Boolean> cell = new TableCell<DaiLy, Boolean>() {
                    @Override
                    protected void updateItem(Boolean item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                        } else {
                            CheckBox checkBox = new CheckBox();
                            checkBox.selectedProperty().bindBidirectional(((DaiLy) getTableRow().getItem()).selectedProperty());
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
        //actionCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

        Callback<TableColumn<DaiLy, String>, TableCell<DaiLy, String>> cellFactory
                = //
                new Callback<>() {
                    @Override
                    public TableCell call(final TableColumn<DaiLy, String> param) {
                        final TableCell<DaiLy, String> cell = new TableCell<DaiLy, String>() {
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
                                        DaiLy dl = getTableView().getItems().get(getIndex());
                                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                        alert.setTitle("Xóa đại lý");
                                        alert.setHeaderText("Bạn có chắc chắn muốn xóa đại lý " + dl.getTenDaiLy() + " ?");

                                        Optional<ButtonType> result = alert.showAndWait();
                                        if (result.get() == ButtonType.OK){
                                            try {
                                                DaiLyDAO.getInstance().Delete(dl.getID());
                                                PopDialog.popSuccessDialog("Xóa đại lý "+ dl.getMaDaiLy() + " thành công");
                                            }
                                            catch (SQLException e) {
                                                PopDialog.popErrorDialog("Xóa đại lý "+ dl.getMaDaiLy() + " thất bại",e.toString());
                                            }
                                        }
                                    });
                                    suaBtn.setOnAction(_ -> {
                                        try {
                                            DaiLy daily = getTableView().getItems().get(getIndex());
                                            new TiepNhanDaiLyDialog(daily).showAndWait();
                                        } catch(IOException exc) {
                                            exc.printStackTrace();
                                        }
                                    });
                                    HBox hbox = new HBox();
                                    hbox.getChildren().addAll(suaBtn,xoaBtn);
                                    hbox.setAlignment(Pos.CENTER);
                                    hbox.setMaxHeight(1000);
                                    hbox.setSpacing(5);
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
                maDLCol,
                tenDLCol,
                quanCol,
                loaiDLCol,
                noHienTaiCol,
                actionCol
        );
        maDLCol.setResizable(false);
        quanCol.setResizable(false);
        loaiDLCol.setResizable(false);
        tenDLCol.setResizable(false);
        noHienTaiCol.setResizable(false);
        actionCol.setResizable(false);

        maDLCol.getStyleClass().add("column-header-left");
        quanCol.getStyleClass().add("column-header-left");
        loaiDLCol.getStyleClass().add("column-header-left");
        tenDLCol.getStyleClass().add("column-header-left");
        noHienTaiCol.getStyleClass().add("column-header-left");
        selectedCol.getStyleClass().add("column-header-center");
        actionCol.getStyleClass().add("column-header-center");


        selectedCol.setPrefWidth(50);
        maDLCol.setPrefWidth(80);
        tenDLCol.setPrefWidth(150);

        quanCol.setPrefWidth(80);
        loaiDLCol.setPrefWidth(80);
        noHienTaiCol.setPrefWidth(120);
        actionCol.setPrefWidth(100);

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
    }
    private void initDatabaseBinding(){
        DaiLyDAO.getInstance().AddDatabaseListener(_ -> updateListFromDatabase());
    }
    private void initUIDataBinding(){
        mainTableView.setItems(dsDaiLyFiltered);
        initFilterBinding();
        initFilterComboboxData();
    }
    private void initFilterBinding(){
        filter.setInput(dsDaiLy);
        maDaiLyTextField.textProperty().addListener(_ -> {
            filter.setMaDaiLy(maDaiLyTextField.getText());
            filterList();
        });
        tenDaiLyTextField.textProperty().addListener(_ -> {
            filter.setTenDaiLy(tenDaiLyTextField.getText());
            filterList();
        });
        quanComboBox.valueProperty().addListener(_ -> {
            if(quanComboBox.getValue() == null){
                filter.setMaQuan(null);
            }
            else {
                filter.setMaQuan(quanComboBox.getValue().getId());
            }
            filterList();
        });
        loaiDaiLyCombobox.valueProperty().addListener(_ -> {
            if(loaiDaiLyCombobox.getValue() == null){
                filter.setMaLoaiDaiLy(null);
            }
            else {
                filter.setMaLoaiDaiLy(loaiDaiLyCombobox.getValue().getId());
            }
            filterList();
        });
    }
    private void initFilterComboboxData(){
        try {
            ObservableList<Quan> quans = FXCollections.observableArrayList(QuanDAO.getInstance().QueryAll());
            ObservableList<LoaiDaiLy> loaiDaiLys = FXCollections.observableArrayList(LoaiDaiLyDAO.getInstance().QueryAll());

            // Sử dụng StringConverter để hiển thị dữ liệu trong ComboBox
            StringConverter<Quan> quanStringConverter = new StringConverter<Quan>() {
                @Override
                public String toString(Quan quan) {
                    return quan == null ? null : quan.getTenQuan(); //lên hình
                }

                @Override
                public Quan fromString(String string) {
                    return null; //Từ hình xuống data
                }
            };

            StringConverter<LoaiDaiLy> loaiDaiLyStringConverter = new StringConverter<LoaiDaiLy>() {
                @Override
                public String toString(LoaiDaiLy loaiDaiLy) {
                    return loaiDaiLy == null ? null : loaiDaiLy.getTenLoai();
                }

                @Override
                public LoaiDaiLy fromString(String string) {
                    return null; // Bạn có thể cần triển khai nếu cần
                }
            };

            // Đặt StringConverter cho ComboBox
            quanComboBox.setConverter(quanStringConverter);
            loaiDaiLyCombobox.setConverter(loaiDaiLyStringConverter);

            // Đặt DataSource cho ComboBox
            quanComboBox.setItems(quans);
            loaiDaiLyCombobox.setItems(loaiDaiLys);
        }
        catch (SQLException e) {
            PopDialog.popErrorDialog("Lấy dữ liệu các quận/ loại đại lý thất bại",e.toString());
        }
    }

    //detail pane
    public void UpdateDetailPane(DaiLy daiLy){
        if(daiLy==null){
            CloseDetailPanel();
            return;
        }
        try{
            Quan quan = QuanDAO.getInstance().QueryID(daiLy.getMaQuan());
            LoaiDaiLy loaiDaiLy = LoaiDaiLyDAO.getInstance().QueryID(daiLy.getMaLoaiDaiLy());

            tenDaiLyText.setText(daiLy.getTenDaiLy());
            maDaiLyText.setText(daiLy.getMaDaiLy());
            maQuanText.setText(quan.getTenQuan());
            maLoaiText.setText(loaiDaiLy.getMaLoai());
            sdtText.setText(daiLy.getDienThoai());
            emailText.setText(daiLy.getEmail());
            diaChiText.setText(daiLy.getDiaChi());
            ngayTiepNhanText.setText(DayFormat.GetDayStringFormatted(daiLy.getNgayTiepNhan()));
            noHienTaiText.setText(MoneyFormatter.ConvertLongToString(daiLy.getNoHienTai()));
            ghiChuTextArea.setText(daiLy.getGhiChu());
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


        Date ngayTiepNhan = new Date(System.currentTimeMillis());

        for (int i = 1; i <= sheet.getLastRowNum()-1; i++) {
            Row row = sheet.getRow(i);
            if (row != null) { // Kiểm tra xem dòng có tồn tại hay không
                Cell quanCell = row.getCell(0);
                Cell loaiDaiLyCell = row.getCell(1);
                Cell tenDaiLyCell = row.getCell(2);
                Cell dienThoaiCell = row.getCell(3);
                Cell emailCell = row.getCell(4);
                Cell diaChiCell = row.getCell(5);
                Cell ghiChuCell = row.getCell(6);

                DaiLy daiLy = new DaiLy();
                daiLy.setMaQuan((int) quanCell.getNumericCellValue());
                daiLy.setMaLoaiDaiLy((int) loaiDaiLyCell.getNumericCellValue());
                daiLy.setTenDaiLy(tenDaiLyCell.getStringCellValue());
                daiLy.setDienThoai(dienThoaiCell.getStringCellValue());
                daiLy.setEmail(emailCell.getStringCellValue());
                daiLy.setDiaChi(diaChiCell.getStringCellValue());
                daiLy.setGhiChu(ghiChuCell.getStringCellValue());
                daiLy.setNgayTiepNhan(ngayTiepNhan);
                try {
                    DaiLyDAO.getInstance().Insert(daiLy); // Thêm đối tượng vào cơ sở dữ liệu
                }
                catch (SQLException e) {
                    PopDialog.popErrorDialog("Thêm mới đại lý thất bại", e.getMessage());
                    return;
                }
            }
        }

        try {
            workbook.close();
            fis.close();
            PopDialog.popSuccessDialog("Thêm danh sách đại lý từ file excel thành công");
        }
        catch (IOException e) {
            PopDialog.popErrorDialog("Có lỗi trong quá trình thực hiện", e.getMessage());
        }

    }

    public void OpenExportDialog() {
        // Hiển thị hộp thoại lưu tệp Excel
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));

        // Tạo tên file với định dạng "Export_ngay_thang_nam.xlsx"
        Date ngayHienTai = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy");
        String fileName = "DsDaiLy_" + dateFormat.format(ngayHienTai) + ".xlsx";

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
        XSSFSheet sheet = workbook.createSheet("DaiLyData"); // Tạo một sheet mới hoặc sử dụng sheet hiện có

        // Tạo hàng đầu tiên với các tiêu đề cột
        Row headerRow = sheet.createRow(0);
        String[] columnTitles = {"Mã đại lý", "Quận", "Loại đại lý", "Tên đại lý", "Số điện thoại", "Email", "Địa chỉ", "Nợ hiện tại","Ngày tiếp nhận", "Ghi chú"};
        int cellnum = 0;
        for (String title : columnTitles) {
            Cell cell = headerRow.createCell(cellnum++);
            cell.setCellValue(title);
        }

        // Duyệt qua danh sách dsDaiLyFiltered và ghi dữ liệu vào tệp Excel
        int rownum = 1; // Bắt đầu từ hàng thứ 2 sau tiêu đề
        for (DaiLy daiLy : dsDaiLyFiltered) {
            Row row = sheet.createRow(rownum++);
            cellnum = 0;
            row.createCell(cellnum++).setCellValue(daiLy.getMaDaiLy());
            row.createCell(cellnum++).setCellValue(daiLy.getMaQuan());
            row.createCell(cellnum++).setCellValue(daiLy.getMaLoaiDaiLy());
            row.createCell(cellnum++).setCellValue(daiLy.getTenDaiLy());
            row.createCell(cellnum++).setCellValue(daiLy.getDienThoai());
            row.createCell(cellnum++).setCellValue(daiLy.getEmail());
            row.createCell(cellnum++).setCellValue(daiLy.getDiaChi());
            row.createCell(cellnum++).setCellValue(daiLy.getNoHienTai());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            row.createCell(cellnum++).setCellValue(dateFormat.format(daiLy.getNgayTiepNhan()));
            row.createCell(cellnum++).setCellValue(daiLy.getGhiChu());
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
            new TiepNhanDaiLyDialog().showAndWait();
        }
        catch (IOException e) {
            PopDialog.popErrorDialog("Không thể mở dialog thêm đại lý");
        }
    }
    public void DeleteSelectedRow()  {
        final Set<DaiLy> del = new HashSet<>();
        for( Object o : mainTableView.getItems()) {
            DaiLy dl = (DaiLy) o;
            if( dl.isSelected()) {
                del.add( dl );
            }
        }
        if(del.isEmpty()){
            PopDialog.popSuccessDialog("Bạn chưa chọn các đại lý cần xóa!");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xóa đại lý");
        alert.setHeaderText("Bạn có chắc chắn muốn xóa " +del.size()+" đại lý?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            for(DaiLy dl : del){
                int ID = dl.getID();
                try {
                    DaiLyDAO.getInstance().Delete(ID);
                } catch (SQLException e) {
                    PopDialog.popErrorDialog("Xóa đại lý " + dl.getMaDaiLy() + " thất bại",e.getMessage());
                    return;
                }
            }
        }
        PopDialog.popSuccessDialog("Xóa "+del.size()+" đại lý thành công");
    }

    private void updateListFromDatabase() {
        dsDaiLy.clear();
        try {
            dsDaiLy.addAll(DaiLyDAO.getInstance().QueryAll());
            filterList();
        } catch (SQLException e) {
            PopDialog.popErrorDialog("Load dữ liệu đại lý thất bại",e.getMessage());
        }
    }
    private void filterList(){
        dsDaiLyFiltered.clear();
        dsDaiLyFiltered.addAll(filter.Filter());
    }
    private void resetFilter(){
        quanComboBox.getSelectionModel().clearSelection();
        quanComboBox.setValue(null);

        maDaiLyTextField.clear();
        tenDaiLyTextField.clear();

        loaiDaiLyCombobox.getSelectionModel().clearSelection();
        loaiDaiLyCombobox.setValue(null);
    }
}
