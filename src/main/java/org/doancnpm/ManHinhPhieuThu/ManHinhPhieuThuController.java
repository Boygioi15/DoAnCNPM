package org.doancnpm.ManHinhPhieuThu;

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
import javafx.stage.FileChooser;
import javafx.util.Callback;

import javafx.util.Duration;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.controlsfx.control.MasterDetailPane;
import org.doancnpm.DAO.DaiLyDAO;
import org.doancnpm.DAO.NhanVienDAO;
import org.doancnpm.DAO.PhieuThuDAO;

import org.doancnpm.Filters.PhieuThuFilter;
import org.doancnpm.ManHinhPhieuXuat.LapPhieuXuatDialog;
import org.doancnpm.Models.*;
import org.doancnpm.Ultilities.DayFormat;
import org.doancnpm.Ultilities.PopDialog;
import javafx.scene.text.Text;

import java.io.*;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;

public class ManHinhPhieuThuController implements Initializable {

    @FXML private Region manHinhPhieuThu;
    @FXML private TableView mainTableView;

    @FXML private MFXTextField maPhieuThuTextField;
    @FXML private MFXTextField maDaiLyTextField;
    @FXML private MFXTextField maNhanVienTextField;

    @FXML private Region filterPane;
    @FXML private Region filterPaneContainer;
    @FXML private Button toggleFilterButton;

    @FXML private MenuItem addExcelButton;
    @FXML private MenuItem addDirectButton;

    @FXML private Text maPhieuThuText;
    @FXML private Text maDLText;
    @FXML private Text tenDLText;
    @FXML private Text maNVText;
    @FXML private Text tenNVText;
    @FXML private Text ngayLapPhieuText;
    @FXML private Text soTienThuText;
    @FXML private TextArea ghiChuTextArea;

    @FXML private MasterDetailPane masterDetailPane;

    @FXML private Region masterPane;
    @FXML private Button toggleDetailButton;
    @FXML private Region detailPane;

    @FXML private FlowPane emptySelectionPane;

    private final ObservableList<PhieuThu> dsPhieuThu = FXCollections.observableArrayList();
    private final ObservableList<PhieuThu> dsPhieuThuFiltered = FXCollections.observableArrayList();
    private final PhieuThuFilter filter = new PhieuThuFilter();

    NhanVien nhanVienLoggedIn = null;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initTableView();
        initEvent();
        initDatabaseBinding();
        initFilterBinding();

        initUIDataBinding();

        updateListFromDatabase();
        initDetailPane();
        //init data
        initFilterPane();
    }
    public void setVisibility(boolean visibility) {
        manHinhPhieuThu.setVisible(visibility);
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
        mainTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, phieuThu) -> {
            UpdateDetailPane((PhieuThu) phieuThu);
        });

        manHinhPhieuThu.widthProperty().addListener(ob -> {
            if(manHinhPhieuThu.getWidth()>1030){
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
        PhieuThuDAO.getInstance().AddDatabaseListener(_ -> updateListFromDatabase());

    }
    private void initUIDataBinding() {
        mainTableView.setItems(dsPhieuThuFiltered);
    }
    private void initFilterBinding(){
        filter.setInput(dsPhieuThu);

        maPhieuThuTextField.textProperty().addListener(_ -> {
            filter.setMaPhieuThu(maPhieuThuTextField.getText());
            filterList();
        });
        maDaiLyTextField.textProperty().addListener(_ -> {
            try{
                filter.setMaDaiLy(Integer.parseInt(maDaiLyTextField.getText()));
            }
            catch (NumberFormatException e){
                filter.setMaDaiLy(null);
            }
            filterList();
        });
        maNhanVienTextField.textProperty().addListener(_ -> {
            try{
                filter.setMaNhanVien(Integer.parseInt(maNhanVienTextField.getText()));
            }
            catch (NumberFormatException e){
                filter.setMaNhanVien(null);
            }
            filterList();
        });
    }
    private void initTableView() {
        // Tạo các cột cho TableView
        TableColumn<PhieuThu, String> maPTCol = new TableColumn<>("Mã");
        maPTCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMaPhieuThu()));

        TableColumn<PhieuThu, String> maDLCol = new TableColumn<>("Đại lý");
        maDLCol.setCellValueFactory(data -> {
            DaiLy daiLy = null;
            try {
                daiLy = DaiLyDAO.getInstance().QueryID(data.getValue().getMaDaiLy());
            }
            catch (SQLException _){}
            return new SimpleObjectProperty<>(daiLy.getMaDaiLy());
        });


        TableColumn<PhieuThu, String> maNVCol = new TableColumn<>("Nhân viên");
        maNVCol.setCellValueFactory(data -> {
            NhanVien nhanVien = null;
            try{
                nhanVien = NhanVienDAO.getInstance().QueryID(data.getValue().getMaNhanVien());
            } catch(SQLException _){}
            return new SimpleObjectProperty<>(nhanVien.getMaNhanVien());
        });

        TableColumn<PhieuThu, Integer> tongTienThuCol = new TableColumn<>("Tổng tiền thu");
        tongTienThuCol.setCellValueFactory(new PropertyValueFactory<>("SoTienThu"));

        TableColumn<PhieuThu, Boolean> selectedCol = new TableColumn<>( );
        HBox headerBox = new HBox();
        CheckBox headerCheckBox = new CheckBox();
        headerBox.getChildren().add(headerCheckBox);
        headerBox.setAlignment(Pos.CENTER); // Center align the content
        headerCheckBox.setDisable(true);
        selectedCol.setGraphic(headerBox);
        selectedCol.setSortable(false);
        selectedCol.setCellValueFactory( new PropertyValueFactory<>( "selected" ));
        selectedCol.setCellFactory(new Callback<TableColumn<PhieuThu, Boolean>, TableCell<PhieuThu, Boolean>>() {
            @Override
            public TableCell<PhieuThu, Boolean> call(TableColumn<PhieuThu, Boolean> param) {
                TableCell<PhieuThu, Boolean> cell = new TableCell<PhieuThu, Boolean>() {
                    @Override
                    protected void updateItem(Boolean item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                        } else {
                            CheckBox checkBox = new CheckBox();
                            checkBox.selectedProperty().bindBidirectional(((PhieuThu) getTableRow().getItem()).selectedProperty());
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

        TableColumn actionCol = new TableColumn("Action");

        Callback<TableColumn<PhieuXuat, String>, TableCell<PhieuXuat, String>> cellFactory
                = //
                new Callback<>() {
                    @Override
                    public TableCell call(final TableColumn<PhieuXuat, String> param) {
                        final TableCell<PhieuXuat, String> cell = new TableCell<PhieuXuat, String>() {
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
                maPTCol,
                maDLCol,
                maNVCol,
                tongTienThuCol,
                actionCol
        );
        maPTCol.getStyleClass().add("column-header-left");
        maDLCol.getStyleClass().add("column-header-left");
        maNVCol.getStyleClass().add("column-header-left");
        tongTienThuCol.getStyleClass().add("column-header-left");
        selectedCol.getStyleClass().add("column-header-center");
        actionCol.getStyleClass().add("column-header-center");

        mainTableView.setEditable(true);
        mainTableView.widthProperty().addListener(ob -> {
            double width = mainTableView.getWidth();
            selectedCol.setPrefWidth(width*0.1);
            maPTCol.setPrefWidth(width*0.15);
            maDLCol.setPrefWidth(width*0.15);
            maNVCol.setPrefWidth(width*0.15);
            tongTienThuCol.setPrefWidth(width*0.30);
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
    public void UpdateDetailPane(PhieuThu phieuThu){
        if(phieuThu==null){
            emptySelectionPane.setVisible(true);
            return;
        }
        emptySelectionPane.setVisible(false);
        maPhieuThuText.setText(phieuThu.getMaPhieuThu());
        try{
            DaiLy dl = DaiLyDAO.getInstance().QueryID(phieuThu.getMaDaiLy());
            NhanVien nv = NhanVienDAO.getInstance().QueryID(phieuThu.getMaNhanVien());
            maDLText.setText(dl.getMaDaiLy());
            tenDLText.setText(dl.getTenDaiLy());
            maNVText.setText(nv.getMaNhanVien());
            tenNVText.setText(nv.getHoTen());

            ngayLapPhieuText.setText(DayFormat.GetDayStringFormatted(phieuThu.getNgayLap()));
            soTienThuText.setText(Double.toString(phieuThu.getSoTienThu()));
            ghiChuTextArea.setText(phieuThu.getGhiChu());
        }
        catch (SQLException _){}
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
    }

    //functionalities
    public void OpenDirectAddDialog() {
        try {
            new LapPhieuThuDialog(nhanVienLoggedIn).showAndWait().ifPresent(
                    phieuThuAdded -> {
                        try {
                            PhieuThuDAO.getInstance().Insert(phieuThuAdded);
                            PopDialog.popSuccessDialog("Thêm mới phiếu thu thành công");
                        }
                        catch (SQLException e) {
                            PopDialog.popErrorDialog("Thêm mới phiếu thu thất bại", e.toString());
                        }
                    }
            );
        }
        catch (IOException e) {
            e.printStackTrace();
            PopDialog.popErrorDialog("Không thể mở dialog thêm phiếu thu", e.toString());
        }
    }

    private void updateListFromDatabase() {
        dsPhieuThu.clear();
        try {
            dsPhieuThu.addAll(PhieuThuDAO.getInstance().QueryAll());
            filterList();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void filterList(){
        dsPhieuThuFiltered.clear();
        dsPhieuThuFiltered.addAll(filter.Filter());
    }
}
