package org.doancnpm.ManHinhNhanVien;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.controlsfx.control.MasterDetailPane;
import org.doancnpm.DAO.ChucVuDAO;
import org.doancnpm.DAO.NhanVienDAO;
import org.doancnpm.DAO.QuanDAO;
import org.doancnpm.Filters.NhanVienFilter;
import org.doancnpm.Models.ChucVu;
import org.doancnpm.Models.NhanVien;
import org.doancnpm.Models.Quan;
import org.doancnpm.Ultilities.DayFormat;
import org.doancnpm.Ultilities.PopDialog;

import java.io.*;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;

/** Controls the main application screen */
public class ManHinhNhanVienController implements Initializable {

    @FXML private Region manHinhNhanVien;
    @FXML private Button refreshButton;
    @FXML private MenuItem addDirectButton;
    @FXML private MenuItem addExcelButton;
    @FXML private MenuItem deleteSelectedButton;
    @FXML private MenuItem exportExcelButton;

    @FXML private MFXTextField maNVTextField;
    @FXML private MFXTextField tenNVTextField;
    @FXML private MFXComboBox<ChucVu> chucVuComboBox;


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
        luongCol.setCellValueFactory(new PropertyValueFactory<>("luong"));

        TableColumn<NhanVien, String> chucVuCol = new TableColumn<>("Chức vụ");
        chucVuCol.setCellValueFactory(data -> {
            ChucVu chucVu = null;
            try {
                chucVu = ChucVuDAO.getInstance().QueryID(data.getValue().getMaChucVu());
            } catch (SQLException _) {}
            return new SimpleObjectProperty<>(chucVu.getTenCV());
        });
        //selected collumn:
        TableColumn<NhanVien, Boolean> selectedCol = new TableColumn<>( "Selected" );
        selectedCol.setCellValueFactory( new PropertyValueFactory<>( "selected" ));
        selectedCol.setCellFactory( tc -> new CheckBoxTableCell<>());

        //action column
        TableColumn actionCol = new TableColumn("Action");

        Callback<TableColumn<NhanVien, String>, TableCell<NhanVien, String>> cellFactory
                = //
                new Callback<>() {
                    @Override
                    public TableCell call(final TableColumn<NhanVien, String> param) {
                        final TableCell<NhanVien, String> cell = new TableCell<NhanVien, String>() {
                            final Button suaBtn = new Button("Sửa");
                            final Button xoaBtn = new Button("Xóa");

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                }
                                else {
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
                                            new TiepNhanNhanVienDialog(nhanVien).showAndWait().ifPresent(nhanVienInfo -> {
                                                //set "database" info
                                                nhanVienInfo.setID(nhanVien.getID());
                                                nhanVienInfo.setMaNhanVien(nhanVien.getMaNhanVien());
                                                try {
                                                    NhanVienDAO.getInstance().Update(nhanVien.getID(),nhanVienInfo);
                                                    PopDialog.popSuccessDialog("Cập nhật nhân viên "+nhanVienInfo.getMaNhanVien()+" thành công");
                                                } catch (SQLException e) {
                                                    PopDialog.popErrorDialog("Cập nhật nhân viên "+nhanVienInfo.getMaNhanVien()+" thất bại",
                                                            e.getMessage());
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

        mainTableView.widthProperty().addListener(ob -> {
            double width = mainTableView.getWidth();
            selectedCol.setPrefWidth(width*0.1);
            maNVCol.setPrefWidth(width*0.1);
            tenNVCol.setPrefWidth(width*0.3);
            gioiTinhCol.setPrefWidth(width*0.1);
            chucVuCol.setPrefWidth(width*0.1);
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
        refreshButton.setOnAction(_ -> {
            resetFilter();
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

    //detail pane
    public void UpdateDetailPane(NhanVien nhanVien){
        if(nhanVien==null){
            CloseDetailPanel();
            return;
        }
        try{
            ChucVu chucVu = ChucVuDAO.getInstance().QueryID(nhanVien.getMaChucVu());

            tenNVText.setText(nhanVien.getHoTen());
            maNVText.setText(nhanVien.getMaNhanVien());
            chucVuText.setText(chucVu.getTenCV());
            sdtText.setText(nhanVien.getSDT());
            emailText.setText(nhanVien.getEmail());
            luongText.setText(Double.toString(nhanVien.getLuong()));
            ngaySinhText.setText(DayFormat.GetDayStringFormatted(nhanVien.getNgaySinh()));
            gioiTinhText.setText(nhanVien.getGioiTinh());
            ghiChuTextArea.setText(nhanVien.getGhiChu());
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
        /*
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
                Cell loaiNhanVienCell = row.getCell(1);
                Cell tenNhanVienCell = row.getCell(2);
                Cell dienThoaiCell = row.getCell(3);
                Cell emailCell = row.getCell(4);
                Cell diaChiCell = row.getCell(5);
                Cell ghiChuCell = row.getCell(6);

                NhanVien nhanVien = new NhanVien();
                nhanVien.setMaQuan((int) quanCell.getNumericCellValue());
                nhanVien.setMaLoaiNhanVien((int) loaiNhanVienCell.getNumericCellValue());
                nhanVien.setTenNhanVien(tenNhanVienCell.getStringCellValue());
                nhanVien.setDienThoai(dienThoaiCell.getStringCellValue());
                nhanVien.setEmail(emailCell.getStringCellValue());
                nhanVien.setDiaChi(diaChiCell.getStringCellValue());
                nhanVien.setGhiChu(ghiChuCell.getStringCellValue());
                nhanVien.setNgayTiepNhan(ngayTiepNhan);
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
            PopDialog.popSuccessDialog("Thêm danh sách nhân viên từ file excel thành công");
        }
        catch (IOException e) {
            PopDialog.popErrorDialog("Có lỗi trong quá trình thực hiện", e.getMessage());
        }

         */
    }

    public void OpenExportDialog() {
        // Hiển thị hộp thoại lưu tệp Excel
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));

        // Tạo tên file với định dạng "Export_ngay_thang_nam.xlsx"
        Date ngayHienTai = new Date(System.currentTimeMillis());
        String fileName = "DsNhanVien_" + DayFormat.GetDayStringFormatted(ngayHienTai) + ".xlsx";

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
        XSSFSheet sheet = workbook.createSheet("NhanVienData"); // Tạo một sheet mới hoặc sử dụng sheet hiện có

        // Tạo hàng đầu tiên với các tiêu đề cột
        Row headerRow = sheet.createRow(0);
        String[] columnTitles = {"Mã nhân viên", "Quận", "Loại nhân viên", "Tên nhân viên", "Số điện thoại", "Email", "Địa chỉ", "Nợ hiện tại", "Ghi chú"};
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
            row.createCell(cellnum++).setCellValue(nhanVien.getMaQuan());
            row.createCell(cellnum++).setCellValue(nhanVien.getMaLoaiNhanVien());
            row.createCell(cellnum++).setCellValue(nhanVien.getTenNhanVien());
            row.createCell(cellnum++).setCellValue(nhanVien.getDienThoai());
            row.createCell(cellnum++).setCellValue(nhanVien.getEmail());
            row.createCell(cellnum++).setCellValue(nhanVien.getDiaChi());
            row.createCell(cellnum++).setCellValue(nhanVien.getNoHienTai());
            row.createCell(cellnum++).setCellValue(nhanVien.getGhiChu());
        }

        // Lưu tệp Excel
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            workbook.write(fos);
            workbook.close();
        }
        catch (IOException e){
            PopDialog.popErrorDialog("Xuất file excel thất bại",e.getMessage());
        }

         */
    }

    //functionalities
    public void OpenDirectAddDialog(){
        try {
            new TiepNhanNhanVienDialog().showAndWait().ifPresent(
                    nhanVienAdded -> {
                        try {
                            NhanVienDAO.getInstance().Insert(nhanVienAdded);
                            PopDialog.popSuccessDialog("Thêm mới nhân viên "+nhanVienAdded.getHoTen()+" thành công");
                        }
                        catch (SQLException e) {
                            PopDialog.popErrorDialog("Thêm mới nhân viên thất bại", e.getMessage());
                        }
                    }
            );
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
