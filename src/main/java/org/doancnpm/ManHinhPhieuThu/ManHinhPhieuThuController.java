package org.doancnpm.ManHinhPhieuThu;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
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
import javafx.stage.FileChooser;
import javafx.util.Callback;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.controlsfx.control.MasterDetailPane;
import org.doancnpm.DAO.*;

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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class ManHinhPhieuThuController implements Initializable {

    @FXML private Node manHinhPhieuThu;
    @FXML private TableView mainTableView;
    @FXML private Button refreshButton;
    @FXML private MFXTextField maPhieuThuTextField;
    @FXML private MFXTextField maDaiLyTextField;
    @FXML private MFXTextField maNhanVienTextField;

    @FXML private MenuItem addExcelButton;
    @FXML private MenuItem addDirectButton;
    @FXML private MenuItem exportExcelButton;
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
    }
    private void initEvent() {
        addDirectButton.setOnAction(_ -> {
            OpenDirectAddDialog();
        });
        refreshButton.setOnAction(_ -> {
            resetFilter();
        });
        exportExcelButton.setOnAction(_ ->{
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
        addExcelButton.setOnAction(_ ->{
            importDialog();
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
        TableColumn<PhieuThu, String> maPTCol = new TableColumn<>("Mã Phiếu Thu");
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


        TableColumn<PhieuThu, String> maNVCol = new TableColumn<>("Nhân Viên");
        maNVCol.setCellValueFactory(data -> {
            NhanVien nhanVien = null;
            try{
                nhanVien = NhanVienDAO.getInstance().QueryID(data.getValue().getMaNhanVien());
            } catch(SQLException _){}
            return new SimpleObjectProperty<>(nhanVien.getMaNhanVien());
        });

        TableColumn<PhieuThu, Integer> tongTienThuCol = new TableColumn<>("Tổng tiền thu");
        tongTienThuCol.setCellValueFactory(new PropertyValueFactory<>("SoTienThu"));

        TableColumn<PhieuThu, Boolean> selectedCol = new TableColumn<>("Selected");
        selectedCol.setCellValueFactory(new PropertyValueFactory<>("selected"));
        selectedCol.setCellFactory(tc -> new CheckBoxTableCell<>());

        TableColumn actionCol = new TableColumn("Action");

        Callback<TableColumn<PhieuThu, String>, TableCell<PhieuThu, String>> cellFactory
                = //
                new Callback<>() {
                    @Override
                    public TableCell call(final TableColumn<PhieuThu, String> param) {
                        final TableCell<PhieuThu, String> cell = new TableCell<PhieuThu, String>() {
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
                                            PhieuThu phieuThu = getTableView().getItems().get(getIndex());
                                            new LapPhieuThuDialog(phieuThu, nhanVienLoggedIn).showAndWait();
                                        } catch(IOException exc) {
                                            exc.printStackTrace();
                                        }
                                    });

                                    xuatBtn.setOnAction(_ -> {
                                        PhieuThu phieuThu = getTableView().getItems().get(getIndex());
                                        exportToPDF(phieuThu);
                                    });
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
        mainTableView.setEditable(true);
        mainTableView.widthProperty().addListener(ob -> {
            double width = mainTableView.getWidth();
            selectedCol.setPrefWidth(width*0.1);
            maPTCol.setPrefWidth(width*0.1);
            maDLCol.setPrefWidth(width*0.1);
            maNVCol.setPrefWidth(width*0.1);
            tongTienThuCol.setPrefWidth(width*0.45);
            actionCol.setPrefWidth(width*0.15);
        });
        mainTableView.setEditable( true );
        mainTableView.setPrefWidth(1100);

    }

    //detail pane
    public void UpdateDetailPane(PhieuThu phieuThu){
        if(phieuThu==null){
            CloseDetailPanel();
            return;
        }
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy");
        String fileName = "DsPhieuThu_" + dateFormat.format(ngayHienTai) + ".xlsx";

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
        String[] columnTitles = {"Mã phiếu thu", "Mã đại lý", "Mã nhân viên","Ngày lập phiếu","Số tiền thu","Ghi chú"};
        int cellnum = 0;
        for (String title : columnTitles) {
            Cell cell = headerRow.createCell(cellnum++);
            cell.setCellValue(title);
        }


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
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            row.createCell(cellnum++).setCellValue(dateFormat.format(phieuThu.getNgayLap()));
            row.createCell(cellnum++).setCellValue(phieuThu.getSoTienThu());
            row.createCell(cellnum++).setCellValue(phieuThu.getGhiChu());
        }
        // Tự động điều chỉnh độ rộng của các cột
        for (int i = 0; i < columnTitles.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Lưu tệp Excel
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            workbook.write(fos);
            workbook.close();
        } catch (IOException e) {
            PopDialog.popErrorDialog("Xuất file excel thất bại", e.getMessage());
        }
    }
    public void exportToPDF(PhieuThu phieuThu) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu phiếu thu");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        fileChooser.setInitialFileName("PhieuThu_"+phieuThu.getMaPhieuThu()+".pdf");

        File selectedFile = fileChooser.showSaveDialog(null);

        if (selectedFile != null) {
            Document document = new Document();
            try {
                BaseFont baseFont = BaseFont.createFont("src/main/resources/vuArial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                Font titleFont = new Font(baseFont, 28, Font.BOLD);
                Font contentFont = new Font(baseFont, 12);
                Font boldFont = new Font(baseFont, 16, Font.BOLD);
                Font contentTienThu = new Font(baseFont, 18);
                Font DaiLyFont = new Font(baseFont, 18);
                PdfWriter.getInstance(document, new FileOutputStream(selectedFile.getAbsolutePath()));
                document.open();

                // Add header
                addHeader(document, boldFont, contentFont, phieuThu);
                document.add(Chunk.NEWLINE);
                document.add(new Paragraph("\n"));
                document.add(new Paragraph("\n"));
                document.add(new Paragraph("\n"));
                addTittle(document,titleFont,DaiLyFont,contentFont,phieuThu);
                document.add(Chunk.NEWLINE);
                document.add(new Paragraph("\n"));

                // Add detail
                addDetail(document,contentTienThu,phieuThu);
                // Add footer
                addFooter(document, contentFont,phieuThu);

                document.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void addHeader(Document document, Font boldFont, Font contentFont, PhieuThu phieuThu) throws DocumentException {
        PdfPTable detailsTable = new PdfPTable(2);
        detailsTable.setWidthPercentage(100);
        detailsTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        PdfPCell brand = new PdfPCell();
        brand.setBorder(Rectangle.NO_BORDER);
        brand.setHorizontalAlignment(Element.ALIGN_CENTER);
        brand.setVerticalAlignment(Element.ALIGN_CENTER);
        brand.setPadding(5);
        Paragraph brandP = new Paragraph();
        brandP.add(new Phrase("NHÓM 27\n", boldFont));
        brandP.add(new Phrase("SE104.O27\n", contentFont));
        brand.addElement(brandP);

        PdfPCell dateTime = new PdfPCell();
        dateTime.setBorder(Rectangle.NO_BORDER);
        dateTime.setHorizontalAlignment(Element.ALIGN_RIGHT);
        // Format date for "ngày ... tháng ... năm ..."
        SimpleDateFormat dateFormat = new SimpleDateFormat("'Ngày' dd 'tháng' MM 'năm' yyyy");
        String formattedDate = dateFormat.format(phieuThu.getNgayLap());

        Paragraph dateTimeP = new Paragraph();
        dateTimeP.add(new Phrase("PHIẾU THU #" + phieuThu.getMaPhieuThu() + "\n", boldFont));
        dateTimeP.add(new Phrase(formattedDate + "\n", contentFont));
        dateTimeP.setAlignment(Element.ALIGN_RIGHT);
        dateTime.addElement(dateTimeP);

        detailsTable.addCell(brand);
        detailsTable.addCell(dateTime);
        document.add(detailsTable);
        document.add(Chunk.NEWLINE);
    }

    private void addTittle(Document document, Font titleFont,Font DaiLyFont,Font contentFont, PhieuThu phieuThu) throws DocumentException {
        document.add(Chunk.NEWLINE);
        Paragraph tittle = new Paragraph();
        tittle.setFont(contentFont);
        tittle.add(new Phrase("PHIẾU THU\n", titleFont));
        tittle.add(new Phrase("\n",titleFont));
        tittle.add(Chunk.NEWLINE);
        DaiLy daiLy = null;
        try {
            daiLy = DaiLyDAO.getInstance().QueryID(phieuThu.getMaDaiLy());
        } catch (SQLException _) {
        }
        tittle.add(new Phrase(daiLy.getTenDaiLy()+"\n", DaiLyFont));
        tittle.add(new Phrase("Điện thoại: "+daiLy.getDienThoai()+"\n", contentFont));
        tittle.add(new Phrase("Địa chỉ: "+daiLy.getDiaChi()+"\n", contentFont));
        tittle.add(new Phrase("Email: "+daiLy.getEmail()+"\n", contentFont));
        tittle.setAlignment(Element.ALIGN_LEFT);
        document.add(tittle);
        document.add(Chunk.NEWLINE);
    }
    private void addDetail(Document document,Font contentTienThu, PhieuThu phieuThu) throws DocumentException {
        document.add(Chunk.NEWLINE);
        Paragraph detail = new Paragraph();
        detail.setFont(contentTienThu);
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("#,##0", symbols);
        df.setMaximumFractionDigits(8);
        detail.add(new Phrase("Số tiền thu: "+df.format(phieuThu.getSoTienThu())+" VNĐ\n", contentTienThu));
        detail.setAlignment(Element.ALIGN_LEFT);
        document.add(detail);
        document.add(Chunk.NEWLINE);
    }

    private static void addFooter(Document document, Font contentFont,PhieuThu phieuThu) throws DocumentException {
        if(phieuThu.getGhiChu()!= null) {
            Paragraph footer = new Paragraph();
            footer.setFont(contentFont);
            footer.add(new Phrase("Ghi chú: \n", contentFont));
            footer.add(new Phrase(phieuThu.getGhiChu(), contentFont));
            document.add(footer);
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
    private void resetFilter(){
        maPhieuThuTextField.clear();
        maDaiLyTextField.clear();
        maNhanVienTextField.clear();
    }
}
