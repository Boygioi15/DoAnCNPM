package org.doancnpm.ManHinhPhieuThu;

import io.github.palexdev.materialfx.controls.MFXComboBox;
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

import javafx.stage.FileChooser;
import javafx.util.Callback;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.doancnpm.DAO.DaiLyDAO;
import org.doancnpm.DAO.NhanVienDAO;
import org.doancnpm.DAO.PhieuThuDao;

import org.doancnpm.DAO.QuanDAO;
import org.doancnpm.Filters.DaiLyFilter;
import org.doancnpm.Filters.PhieuThuFilter;
import org.doancnpm.ManHinhDaiLy.DirectAddDialog;
import org.doancnpm.Models.*;
import org.doancnpm.Ultilities.DayFormat;
import org.doancnpm.Ultilities.PopDialog;

import java.io.*;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class ManHinhPhieuThuController implements Initializable {
    public MenuItem addDirectButton;
    @FXML Node manHinhPhieuThu;
    @FXML private TableView mainTableView;
    @FXML private Button refreshButton;
    @FXML private MFXTextField maPhieuThuTextField;
    @FXML private MFXTextField maDaiLyTextField;
    @FXML private MFXTextField maNhanVienTextField;
    @FXML private MenuItem addExcelButton;


    private final ObservableList<PhieuThu> dsPhieuThu = FXCollections.observableArrayList();
    private final ObservableList<PhieuThu> dsPhieuThuFiltered = FXCollections.observableArrayList();
    private final PhieuThuFilter filter = new PhieuThuFilter();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initTableView();
        initEvent();
        initDatabaseBinding();
        initUIDataBinding();

        updateListFromDatabase();
        //init data

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
    }

    private void OpenDirectAddDialog() {
        try {
            new LapPhieuThuDialog().showAndWait().ifPresent(
                    phieuThuAdded -> {
                        try {
                            PhieuThuDao.getInstance().Insert(phieuThuAdded);
                            PopDialog.popSuccessDialog("Thêm mới thành công");
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

    public void initTableView() {
        // Tạo các cột cho TableView
        TableColumn<PhieuThu, String> maPTCol = new TableColumn<>("Mã Phiếu Thu");
        maPTCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMaPhieuThu()));

        TableColumn<PhieuThu, String> maDLCol = new TableColumn<>("Mã đại lý");
        maDLCol.setCellValueFactory(data -> {
            DaiLy daiLy = null;
            try {
                daiLy = DaiLyDAO.getInstance().QueryID(data.getValue().getMaDaiLi());
            } catch (SQLException _){

            }
            return new SimpleObjectProperty<>(daiLy.getMaDaiLy());
        });


        TableColumn<PhieuThu, String> maNVCol = new TableColumn<>("Mã Nhân Viên");
        maNVCol.setCellValueFactory(data -> {
            NhanVien nhanVien = null;
            try{
                nhanVien = NhanVienDAO.getInstance().QueryID(data.getValue().getMaNhanVien());
            } catch(SQLException _){

            }
            return new SimpleObjectProperty<>(nhanVien.getMaNhanVien());
        });

        TableColumn<PhieuThu, String> ngayCol = new TableColumn<>("Ngày lập phiếu ");
        ngayCol.setCellValueFactory(data -> {
            Date ngay = data.getValue().getNgayLap();
            return new SimpleObjectProperty<>(DayFormat.GetDayStringFormatted(ngay));
        });

        TableColumn<PhieuThu, Integer> tongTienThuCol = new TableColumn<>("Tổng tiền thu");
        tongTienThuCol.setCellValueFactory(new PropertyValueFactory<>("SoTienThu"));

        TableColumn<PhieuThu, String> ghiChuCol = new TableColumn<>("Ghi chú");
        ghiChuCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGhiChu()));

        TableColumn<PhieuThu, Boolean> selectedCol = new TableColumn<>("Selected");
        selectedCol.setCellValueFactory(new PropertyValueFactory<>("selected"));
        selectedCol.setCellFactory(tc -> new CheckBoxTableCell<>());

        //action column
        TableColumn actionCol = new TableColumn("Action");
        //actionCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

        Callback<TableColumn<PhieuThu, String>, TableCell<PhieuThu, String>> cellFactory
                = //
                new Callback<>() {
                    @Override
                    public TableCell call(final TableColumn<PhieuThu, String> param) {
                        final TableCell<PhieuThu, String> cell = new TableCell<PhieuThu, String>() {
                            final Button suaBtn = new javafx.scene.control.Button("Sửa");
                            final Button xoaBtn = new javafx.scene.control.Button("Xóa");

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    xoaBtn.setOnAction(event -> {
                                        PhieuThu pt = getTableView().getItems().get(getIndex());
                                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                        alert.setTitle("Xóa phiếu thu");

                                        Optional<ButtonType> result = alert.showAndWait();
                                        if (result.get() == ButtonType.OK) {
                                            try {
                                                PhieuThuDao.getInstance().Delete(pt.getID());
                                                PopDialog.popSuccessDialog("Xóa phiếu thu" + " thành công");
                                            } catch (SQLException e) {
                                                PopDialog.popErrorDialog("Xóa phiếu thu" + " thất bại", e.toString());
                                            }
                                        }
                                    });
                                    suaBtn.setOnAction(_ -> {
                                        try {
                                            PhieuThu phieuThu = getTableView().getItems().get(getIndex());
                                            new LapPhieuThuDialog(phieuThu).showAndWait().ifPresent(_ -> {
                                                try {
                                                    PhieuThuDao.getInstance().Update(phieuThu.getID(),phieuThu);
                                                    PopDialog.popSuccessDialog("Cập nhật phiếu thu tiền "+phieuThu.getMaPhieuThu()+" thành công");
                                                } catch (SQLException e) {
                                                    PopDialog.popErrorDialog("Cập nhật phiếu thu tiền "+phieuThu.getMaPhieuThu()+" thất bại",
                                                            e.toString());
                                                }
                                                //mainTableView.getItems().set(selectedIndex, response);
                                            });
                                        } catch(IOException exc) {
                                            exc.printStackTrace();
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
                maPTCol,
                maDLCol,
                maNVCol,
                ngayCol,
                tongTienThuCol,
                ghiChuCol,
                actionCol
        );
        mainTableView.setEditable(true);
        mainTableView.setPrefWidth(1100);
    }

    public void setVisibility(boolean visibility) {
        manHinhPhieuThu.setVisible(visibility);
    }

    private void initDatabaseBinding() {
        PhieuThuDao.getInstance().AddDatabaseListener(_ -> updateListFromDatabase());

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


        Date ngayLapPhieu = new Date(System.currentTimeMillis());

        for (int i = 1; i <= sheet.getLastRowNum()-1; i++) {
            Row row = sheet.getRow(i);
            if (row != null) { // Kiểm tra xem dòng có tồn tại hay không
                org.apache.poi.ss.usermodel.Cell maDaiLyCell = row.getCell(0);
                org.apache.poi.ss.usermodel.Cell tienThuCell = row.getCell(1);
                org.apache.poi.ss.usermodel.Cell ghiChuCell = row.getCell(2);

                PhieuThu phieuThu = new PhieuThu();

                phieuThu.setSoTienThu((int) tienThuCell.getNumericCellValue());
                phieuThu.setGhiChu(ghiChuCell.getStringCellValue());


                phieuThu.setNgayLap(ngayLapPhieu);
                try {
                    PhieuThuDao.getInstance().Insert(phieuThu); // Thêm đối tượng vào cơ sở dữ liệu
                }
                catch (SQLException e) {
                    PopDialog.popErrorDialog("Có lỗi trong quá trình thêm mới phiếu thu", e.toString());
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
            PopDialog.popErrorDialog("Có lỗi trong quá trình thực hiện", e.toString());
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
        for (PhieuThu phieuThu : dsPhieuThuFiltered) {
            Row row = sheet.createRow(rownum++);
            cellnum = 0;
            row.createCell(cellnum++).setCellValue(phieuThu.getMaPhieuThu());

            DaiLy daiLy = null;
            try {
                daiLy = DaiLyDAO.getInstance().QueryID(phieuThu.getMaDaiLi());
            } catch (SQLException e) {
                // Handle exception if necessary
            }
            if (daiLy != null) {
                row.createCell(cellnum++).setCellValue(daiLy.getMaDaiLy());
            } else {
                row.createCell(cellnum++).setCellValue(""); // Or handle the null case appropriately
            }

            NhanVien nhanVien = null;
            try {
                nhanVien = NhanVienDAO.getInstance().QueryID(phieuThu.getMaNhanVien());
            } catch (SQLException e) {
                // Handle exception if necessary
            }
            if (nhanVien != null) {
                row.createCell(cellnum++).setCellValue(nhanVien.getMaNhanVien());
            } else {
                row.createCell(cellnum++).setCellValue(""); // Or handle the null case appropriately
            }

            row.createCell(cellnum++).setCellValue(phieuThu.getSoTienThu());

            // Định dạng ngày
            Cell dateCell = row.createCell(cellnum++);
            if (phieuThu.getNgayLap() != null) {
                dateCell.setCellValue(phieuThu.getNgayLap());
                dateCell.setCellStyle(dateCellStyle);
            } else {
                dateCell.setCellValue(""); // Or handle the null case appropriately
            }
        }

        // Lưu tệp Excel
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            workbook.write(fos);
            workbook.close();
        } catch (IOException e) {
            PopDialog.popErrorDialog("Xuất file excel thất bại", e.toString());
        }
    }
    private void initUIDataBinding() {
        mainTableView.setItems(dsPhieuThuFiltered);
        initFilterBinding();
    }
    private void initFilterBinding(){
        filter.setInput(dsPhieuThu);
        maPhieuThuTextField.textProperty().addListener(_ -> {
            filter.setMaPhieuThu(maPhieuThuTextField.getText());
            filterList();
        });
        maDaiLyTextField.textProperty().addListener(_ -> {
            filter.setMaDaiLy(Integer.parseInt(maDaiLyTextField.getText()));
            filterList();
        });
        maNhanVienTextField.textProperty().addListener(_ -> {
            filter.setMaNhanVien(Integer.parseInt(maNhanVienTextField.getText()));
            filterList();
        });
    }
    private void updateListFromDatabase() {
        dsPhieuThu.clear();
        try {
            dsPhieuThu.addAll(PhieuThuDao.getInstance().QueryAll());
            filterList();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void filterList(){
        dsPhieuThuFiltered.clear();
        dsPhieuThuFiltered.addAll(filter.Filter());
    }
    private void resetFilter(){
        maPhieuThuTextField.clear();
        maDaiLyTextField.clear();
        maNhanVienTextField.clear();
    }
}
