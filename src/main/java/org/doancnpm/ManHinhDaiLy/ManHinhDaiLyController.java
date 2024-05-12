package org.doancnpm.ManHinhDaiLy;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.StringConverter;
import org.apache.poi.ss.usermodel.Cell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.doancnpm.DAO.DaiLyDAO;
import org.doancnpm.DAO.LoaiDaiLyDAO;
import org.doancnpm.DAO.QuanDAO;
import org.doancnpm.Filters.DaiLyFilter;
import org.doancnpm.Models.DaiLy;
import org.doancnpm.Models.LoaiDaiLy;
import org.doancnpm.Models.Quan;
import org.doancnpm.Ultilities.DayFormat;
import org.doancnpm.Ultilities.PopDialog;

import java.io.*;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.util.*;

/** Controls the main application screen */
public class ManHinhDaiLyController implements Initializable {

    @FXML private Node manHinhDaiLy;
    @FXML private Button refreshButton, filterButton;
    @FXML private MenuItem addDirectButton;
    @FXML private MenuItem addExcelButton;
    @FXML private Button lapPhieuThuTienButton;
    @FXML private MenuItem deleteSelectedButton;
    @FXML private MenuItem exportExcelButton;

    @FXML private MFXTextField maDaiLyTextField;
    @FXML private MFXTextField tenDaiLyTextField;
    @FXML private MFXComboBox<Quan> quanComboBox;
    @FXML private MFXComboBox<LoaiDaiLy> loaiDaiLyCombobox;

    @FXML private TableView mainTableView;
    @FXML private TableView detailTableView;

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
    }
    public void setVisibility(boolean visibility){
        manHinhDaiLy.setVisible(visibility);
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
            } catch (SQLException _) {

            }

            return new SimpleObjectProperty<>(quan.getTenQuan());
        });

        TableColumn<DaiLy, String> loaiDLCol = new TableColumn<>("Loại đại lý");
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

        TableColumn<DaiLy, String> SDTCol = new TableColumn<>("SDT");
        SDTCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDienThoai()));

        TableColumn<DaiLy, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));

        TableColumn<DaiLy, String> diaChiCol = new TableColumn<>("Địa chỉ");
        diaChiCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDiaChi()));

        TableColumn<DaiLy, Integer> noHienTaiCol = new TableColumn<>("Nợ hiện tại");
        noHienTaiCol.setCellValueFactory( new PropertyValueFactory<>("noHienTai"));

        TableColumn<DaiLy, String> ghiChuCol = new TableColumn<>("Ghi chú");
        ghiChuCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGhiChu()));


        TableColumn<DaiLy, String> ngayCol = new TableColumn<>("Ngày tiếp nhận");
        ngayCol.setCellValueFactory(data -> {
            Date ngay = data.getValue().getNgayTiepNhan();
            return new SimpleObjectProperty<>(DayFormat.GetDayStringFormatted(ngay));
        });

        //selected collumn:
        TableColumn<DaiLy, Boolean> selectedCol = new TableColumn<>( "Selected" );
        selectedCol.setCellValueFactory( new PropertyValueFactory<>( "selected" ));
        selectedCol.setCellFactory( tc -> new CheckBoxTableCell<>());

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
                                        alert.setHeaderText(dl.getTenDaiLy() + " đã đồng hành với bạn trong 3 tháng\nBạn qua cầu rút ván hả?");

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
                                            new DirectAddDialog(daily).showAndWait().ifPresent(daiLyInfo -> {
                                                //set "database" info
                                                daiLyInfo.setID(daily.getID());
                                                daiLyInfo.setNoHienTai(daily.getNoHienTai());
                                                daiLyInfo.setNgayTiepNhan(daily.getNgayTiepNhan());
                                                daiLyInfo.setMaDaiLy(daily.getMaDaiLy());
                                                try {
                                                    DaiLyDAO.getInstance().Update(daily.getID(),daiLyInfo);
                                                    PopDialog.popSuccessDialog("Cập nhật đại lý "+daiLyInfo.getMaDaiLy()+" thành công");
                                                } catch (SQLException e) {
                                                    PopDialog.popErrorDialog("Cập nhật đại lý "+daiLyInfo.getMaDaiLy()+" thất bại",
                                                            e.toString());
                                                }
                                                //mainTableView.getItems().set(selectedIndex, response);
                                            });
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
                maDLCol,
                quanCol,
                loaiDLCol,
                tenDLCol,
                SDTCol,
                emailCol,
                diaChiCol,
                ngayCol,
                noHienTaiCol,
                ghiChuCol,
                actionCol
        );
        mainTableView.setEditable( true );
        mainTableView.setPrefWidth(1100);
    }
    private void initEvent(){
        addDirectButton.setOnAction(_ -> {
            OpenDirectAddDialog();
        });
        refreshButton.setOnAction(_ -> {
            resetFilter();
        });
        deleteSelectedButton.setOnAction(_ -> DeleteSelectedRow());

        addExcelButton.setOnAction(_ -> {
            importDialog();
        });
        exportExcelButton.setOnAction(_ -> {
            exportDialog();
        });
    }

    //open import dialog
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
            PopDialog.popErrorDialog("Không thể mở file excel", e.toString());
            return;
        }

        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(fis);
        }
        catch (IOException e) {
            PopDialog.popErrorDialog("Có lỗi trong quá trình thực hiện", e.toString());
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
                    PopDialog.popErrorDialog("Có lỗi trong quá trình thêm mới đại lý", e.toString());
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
            PopDialog.popErrorDialog("Có lỗi trong quá trình thực hiện", e.toString());
        }

    }

    public void exportDialog() {
        // Hiển thị hộp thoại lưu tệp Excel
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));

        // Tạo tên file với định dạng "Export_ngay_thang_nam.xlsx"
        Date ngayHienTai = new Date(System.currentTimeMillis());
        String fileName = "DsDaiLy_" + DayFormat.GetDayStringFormatted(ngayHienTai) + ".xlsx";

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
        String[] columnTitles = {"Mã đại lý", "Quận", "Loại đại lý", "Tên đại lý", "Số điện thoại", "Email", "Địa chỉ", "Nợ hiện tại", "Ghi chú"};
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
            row.createCell(cellnum++).setCellValue(daiLy.getGhiChu());
        }

        // Lưu tệp Excel
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            workbook.write(fos);
            workbook.close();
        }
        catch (IOException e){
            PopDialog.popErrorDialog("Xuất file excel thất bại", e.toString());
        }
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

    public void OpenDirectAddDialog(){
        try {
            new DirectAddDialog().showAndWait().ifPresent(
                    daiLyAdded -> {
                        try {
                            DaiLyDAO.getInstance().Insert(daiLyAdded);
                            PopDialog.popSuccessDialog("Thêm mới đại lý "+daiLyAdded.getTenDaiLy()+" thành công");
                        }
                        catch (SQLException e) {
                            PopDialog.popErrorDialog("Thêm mới đại lý thất bại", e.toString());
                        }
                    }
            );
        }
        catch (IOException e) {
            PopDialog.popErrorDialog("Không thể mở dialog thêm đại lý", e.toString());
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
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xóa đại lý");
        alert.setHeaderText("Bạn có chắc chắn muốn xóa " +del.size()+" đại lý?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            //System.out.println(del.size());
            for(DaiLy dl : del){
                int ID = dl.getID();
                try {
                    DaiLyDAO.getInstance().Delete(ID);
                } catch (SQLException e) {
                    PopDialog.popErrorDialog("Xóa đại lý " + dl.getMaDaiLy() + " thất bại",e.toString());
                    return;
                }
            }
        }
    }

    private void updateListFromDatabase() {
        dsDaiLy.clear();
        try {
            dsDaiLy.addAll(DaiLyDAO.getInstance().QueryAll());
            filterList();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void filterList(){
        dsDaiLyFiltered.clear();
        dsDaiLyFiltered.addAll(filter.Filter());
    }
    private void resetFilter(){
        quanComboBox.clearSelection();
        quanComboBox.clear();
        maDaiLyTextField.clear();
        tenDaiLyTextField.clear();
        loaiDaiLyCombobox.clearSelection();
        loaiDaiLyCombobox.clear();
    }
}