package org.doancnpm.ManHinhPhieuNhap;

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
import javafx.scene.text.Text;
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
import org.doancnpm.Filters.PhieuNhapFilter;
import org.doancnpm.Models.*;
import org.doancnpm.Ultilities.DayFormat;
import org.doancnpm.Ultilities.PopDialog;

import java.io.*;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class ManHinhPhieuNhapController implements Initializable {

    @FXML
    private Node manHinhPhieuNhap;
    @FXML
    private TableView mainTableView;
    @FXML
    private TableView detailTableView;
    @FXML
    private Button refreshButton;
    @FXML
    private MFXTextField nccTextField;
    @FXML
    private MFXTextField maPNTextField;
    @FXML
    private MFXTextField nvTextField;

    @FXML
    private MenuItem addExcelButton;
    @FXML
    private MenuItem exportExcelButton;
    @FXML
    private MenuItem addDirectButton;

    @FXML
    private Text maPNText;
    @FXML
    private Text maNVText;
    @FXML
    private Text tenNVText;
    @FXML
    private Text nccText;
    @FXML
    private Text ngayLapPhieuText;
    @FXML
    private Text tongTienText;

    @FXML
    private MasterDetailPane masterDetailPane;

    @FXML
    private Region masterPane;
    @FXML
    private Button toggleDetailButton;
    @FXML
    private Region detailPane;

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
    private void initDetailPane() {
        masterDetailPane.setDetailNode(detailPane);
        masterDetailPane.setMasterNode(masterPane);

        masterDetailPane.widthProperty().addListener(ob -> {
            detailPane.setMinWidth(masterDetailPane.getWidth() * 0.3);
            detailPane.setMaxWidth(masterDetailPane.getWidth() * 0.3);
        });
        mainTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, phieuNhap) -> {
            UpdateDetailPane((PhieuNhap) phieuNhap);
        });
    }

    private void initDetailTableView() {
        // Tạo các cột cho TableView

        TableColumn<ChiTietPhieuNhap, String> mhCol = new TableColumn<>("Mặt hàng");
        mhCol.setCellValueFactory(data -> {
            MatHang mh = null;
            try {
                mh = MatHangDAO.getInstance().QueryID(data.getValue().getMaMatHang());
            } catch (SQLException _) {
            }
            return new SimpleObjectProperty<>(mh.getMaMatHang() + " - " + mh.getTenMatHang());
        });

        TableColumn<ChiTietPhieuNhap, String> dvtCol = new TableColumn<>("Đơn vị tính");
        dvtCol.setCellValueFactory(data -> {
            MatHang mh = null;
            try {
                mh = MatHangDAO.getInstance().QueryID(data.getValue().getMaMatHang());
            } catch (SQLException _) {
            }

            DonViTinh dvt = null;
            try {
                dvt = DonViTinhDAO.getInstance().QueryID(mh.getMaDVT());
            } catch (SQLException _) {
            }
            return new SimpleObjectProperty<>(dvt.getTenDVT());
        });

        TableColumn<ChiTietPhieuNhap, Integer> slCol = new TableColumn<>("Số lượng");
        slCol.setCellValueFactory(new PropertyValueFactory<>("soLuong"));

        TableColumn<ChiTietPhieuNhap, Double> donGiaNhapCol = new TableColumn<>("Đơn giá");
        donGiaNhapCol.setCellValueFactory(data -> {
            MatHang mh = null;
            try {
                mh = MatHangDAO.getInstance().QueryID(data.getValue().getMaMatHang());
            } catch (SQLException _) {
            }
            return new SimpleObjectProperty<>(mh.getDonGiaNhap());
        });

        TableColumn<MatHang, Integer> thanhTienCol = new TableColumn<>("Thành tiền");
        thanhTienCol.setCellValueFactory(new PropertyValueFactory<>("thanhTien"));

        detailTableView.getColumns().addAll(
                mhCol, dvtCol, slCol, donGiaNhapCol, thanhTienCol
        );
        detailTableView.setEditable(true);
        detailTableView.widthProperty().addListener(ob -> {
            double width = detailTableView.getWidth();
            mhCol.setPrefWidth(width * 0.3);
            dvtCol.setPrefWidth(width * 0.15);
            slCol.setPrefWidth(width * 0.15);
            donGiaNhapCol.setPrefWidth(width * 0.2);
            thanhTienCol.setPrefWidth(width * 0.2);
        });
        detailTableView.setEditable(true);
    }

    private void initEvent() {
        addDirectButton.setOnAction(_ -> {
            OpenDirectAddDialog();
        });
        refreshButton.setOnAction(_ -> {
            resetFilter();
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
        addExcelButton.setOnAction(_ ->{
            importDialog();
        });
    }

    private void initDatabaseBinding() {
        PhieuNhapDAO.getInstance().AddDatabaseListener(_ -> updateListFromDatabase());

    }

    private void initUIDataBinding() {
        mainTableView.setItems(dsPhieuNhapFiltered);
    }

    private void initFilterBinding() {
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
                                            new LapPhieuNhapDialog(phieuNhap, nhanVienLoggedIn).showAndWait();
                                        } catch (IOException exc) {
                                            exc.printStackTrace();
                                        }
                                    });

                                    xuatBtn.setOnAction(_ -> {
                                        PhieuNhap phieuNhap = getTableView().getItems().get(getIndex());
                                        List<ChiTietPhieuNhap> chiTietPhieuNhapList;
                                        try {
                                            chiTietPhieuNhapList = CTPNDAO.getInstance().QueryByPhieuNhapID(phieuNhap.getID());
                                        } catch (SQLException e) {
                                            throw new RuntimeException(e);
                                        }
                                        exportToPDF(chiTietPhieuNhapList, phieuNhap);
                                    });
                                    HBox hbox = new HBox();
                                    hbox.getChildren().addAll(suaBtn, xuatBtn);
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
            selectedCol.setPrefWidth(width * 0.1);
            maPNCol.setPrefWidth(width * 0.1);
            maNVCol.setPrefWidth(width * 0.1);
            nccCol.setPrefWidth(width * 0.3);
            tongTienCol.setPrefWidth(width * 0.2);
            actionCol.setPrefWidth(width * 0.2);
        });
        mainTableView.setEditable(true);
        mainTableView.setPrefWidth(1100);

    }

    //detail pane
    public void UpdateDetailPane(PhieuNhap phieuNhap) {
        if (phieuNhap == null) {
            CloseDetailPanel();
            return;
        }
        maPNText.setText(phieuNhap.getMaPhieuNhap());
        try {
            NhanVien nv = NhanVienDAO.getInstance().QueryID(phieuNhap.getMaNhanVien());
            maNVText.setText(nv.getMaNhanVien());
            tenNVText.setText(nv.getHoTen());
        } catch (SQLException _) {
        }
        nccText.setText(phieuNhap.getNhaCungCap());
        ngayLapPhieuText.setText(DayFormat.GetDayStringFormatted(phieuNhap.getNgayLapPhieu()));
        tongTienText.setText(Double.toString(phieuNhap.getTongTien()));

        //item
        try {
            List<ChiTietPhieuNhap> chiTietPhieuNhapList = CTPNDAO.getInstance().QueryByPhieuNhapID(phieuNhap.getID());
            detailTableView.getItems().clear();
            ObservableList<ChiTietPhieuNhap> observableChiTietPhieuNhapList = FXCollections.observableArrayList(chiTietPhieuNhapList);
            detailTableView.setItems(observableChiTietPhieuNhapList);
        } catch (SQLException _) {
        }
    }

    public void OpenDetailPanel() {
        toggleDetailButton.setText(">");
        masterDetailPane.setShowDetailNode(true);

    }

    public void CloseDetailPanel() {
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

    public void importFromExcel(String filePath) {
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy");
        String fileName = "DsPhieuNhap_" + dateFormat.format(ngayHienTai) + ".xlsx";

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
        XSSFSheet sheet = workbook.createSheet("PhieuNhapData"); // Tạo một sheet mới hoặc sử dụng sheet hiện có

        // Tạo hàng đầu tiên với các tiêu đề cột
        Row headerRow = sheet.createRow(0);
        String[] columnTitles = {"Mã phiếu nhập","Nhân viên", "Nhà cung cấp","Ngày lập phiếu","Tổng tiền","Ghi chú"};
        int cellnum = 0;
        for (String title : columnTitles) {
            Cell cell = headerRow.createCell(cellnum++);
            cell.setCellValue(title);
        }

        int rownum = 1; // Bắt đầu từ hàng thứ 2 sau tiêu đề
        for (PhieuNhap phieuNhap : dsPhieuNhapFiltered) {
            Row row = sheet.createRow(rownum++);
            cellnum = 0;
            row.createCell(cellnum++).setCellValue(phieuNhap.getMaPhieuNhap());

            NhanVien nhanVien = null;
            try {
                nhanVien = NhanVienDAO.getInstance().QueryID(phieuNhap.getMaNhanVien());
            } catch (SQLException _) {}
            if (nhanVien != null) {
                row.createCell(cellnum++).setCellValue(nhanVien.getHoTen());
            } else {
                row.createCell(cellnum++).setCellValue("???"); // Or handle the null case appropriately
            }

            row.createCell(cellnum++).setCellValue(phieuNhap.getNhaCungCap());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            row.createCell(cellnum++).setCellValue(dateFormat.format(phieuNhap.getNgayLapPhieu()));
            row.createCell(cellnum++).setCellValue(phieuNhap.getTongTien());
            row.createCell(cellnum++).setCellValue(phieuNhap.getGhiChu());
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

    public void exportToPDF(List<ChiTietPhieuNhap> data, PhieuNhap phieuNhap) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu phiếu nhập");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        fileChooser.setInitialFileName("PhieuNhap_"+phieuNhap.getMaPhieuNhap()+".pdf");

        File selectedFile = fileChooser.showSaveDialog(null);

        if (selectedFile != null) {
            Document document = new Document();
            try {
                BaseFont baseFont = BaseFont.createFont("src/main/resources/vuArial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                Font titleFont = new Font(baseFont, 28, Font.BOLD);
                Font contentFont = new Font(baseFont, 12);
                Font boldFont = new Font(baseFont, 16, Font.BOLD);
                Font tittleTableFont = new Font(baseFont, 12, Font.BOLD);
                Font NhaCungCapFont = new Font(baseFont, 18);
                PdfWriter.getInstance(document, new FileOutputStream(selectedFile.getAbsolutePath()));
                document.open();

                // Add header
                addHeader(document, boldFont, contentFont, phieuNhap);
                document.add(Chunk.NEWLINE);
                document.add(new Paragraph("\n"));
                document.add(new Paragraph("\n"));
                document.add(new Paragraph("\n"));
                addTittle(document,titleFont,NhaCungCapFont,contentFont,phieuNhap);
                document.add(Chunk.NEWLINE);
                document.add(new Paragraph("\n"));


                // Create and add table
                PdfPTable table = createTable(data, contentFont, tittleTableFont);
                document.add(table);
                document.add(new Paragraph("\n"));
                // Add total revenue
                addTotalRevenue(document, phieuNhap.getTongTien(), boldFont);

                // Add footer
                addFooter(document, contentFont,phieuNhap);

                document.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void addHeader(Document document, Font boldFont, Font contentFont, PhieuNhap phieuNhap) throws DocumentException {
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
        String formattedDate = dateFormat.format(phieuNhap.getNgayLapPhieu());

        Paragraph dateTimeP = new Paragraph();
        dateTimeP.add(new Phrase("PHIẾU NHẬP #" + phieuNhap.getMaPhieuNhap() + "\n", boldFont));
        dateTimeP.add(new Phrase(formattedDate + "\n", contentFont));
        dateTimeP.setAlignment(Element.ALIGN_RIGHT);
        dateTime.addElement(dateTimeP);

        detailsTable.addCell(brand);
        detailsTable.addCell(dateTime);
        document.add(detailsTable);
        document.add(Chunk.NEWLINE);
    }

    private void addTittle(Document document, Font titleFont,Font NhaCungCapFont,Font contentFont, PhieuNhap phieuNhap) throws DocumentException {
        document.add(Chunk.NEWLINE);
        Paragraph tittle = new Paragraph();
        tittle.setFont(contentFont);
        tittle.add(new Phrase("PHIẾU NHẬP\n", titleFont));
        tittle.add(Chunk.NEWLINE);
        tittle.add(new Phrase("Nhà cung cấp: "+phieuNhap.getNhaCungCap()+"\n", NhaCungCapFont));
        tittle.setAlignment(Element.ALIGN_LEFT);
        document.add(tittle);
        document.add(Chunk.NEWLINE);
    }

    private static PdfPTable createTable(List<ChiTietPhieuNhap> data, Font contentFont, Font tittleTableFont) throws DocumentException {
        PdfPTable table = new PdfPTable(6); // 5 columns
        table.setWidthPercentage(100);
        float[] columnWidths = {1, 3, 1.2F, 1.5F, 1.5F, 1.8F};
        table.setWidths(columnWidths);

        // Add table headers
        table.addCell(createCell("STT", tittleTableFont));
        table.addCell(createCell("Mặt hàng", tittleTableFont));
        table.addCell(createCell("ĐVT", tittleTableFont));
        table.addCell(createCell("Số lượng", tittleTableFont));
        table.addCell(createCell("Đơn giá", tittleTableFont));
        table.addCell(createCell("Thành tiền", tittleTableFont));

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("#,##0", symbols);
        df.setMaximumFractionDigits(8);
        int STT = 0;
        // Add table data
        for (ChiTietPhieuNhap item : data) {
            STT++;
            table.addCell(createCell(String.valueOf(STT), contentFont));

            MatHang mh = null;
            try {
                mh = MatHangDAO.getInstance().QueryID(item.getMaMatHang());
            } catch (SQLException _) {
            }
            table.addCell(createCell(mh != null ? mh.getMaMatHang() + " - " + mh.getTenMatHang() : "", contentFont));

            DonViTinh dvt = null;
            try {
                dvt = DonViTinhDAO.getInstance().QueryID(mh.getMaDVT());
            } catch (SQLException _) {
            }
            table.addCell(createCell(dvt.getTenDVT(), contentFont));

            table.addCell(createCell(String.valueOf(item.getSoLuong()), contentFont));

            table.addCell(createCell(df.format(mh.getDonGiaNhap()), contentFont));

            table.addCell(createCell(df.format(item.getThanhTien()), contentFont));
        }

        return table;
    }

    private static void addTotalRevenue(Document document, Double tongTien, Font boldFont) throws DocumentException {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("#,##0", symbols);
        df.setMaximumFractionDigits(8);
        Paragraph totalRevenueParagraph = new Paragraph("TỔNG THANH TOÁN: " + df.format(tongTien) + " VNĐ", boldFont);
        totalRevenueParagraph.setAlignment(Element.ALIGN_RIGHT);
        document.add(totalRevenueParagraph);

        document.add(Chunk.NEWLINE);
    }

    private static void addFooter(Document document, Font contentFont,PhieuNhap phieuNhap) throws DocumentException {
        if(phieuNhap.getGhiChu()!= null) {
            Paragraph footer = new Paragraph();
            footer.setFont(contentFont);
            footer.add(new Phrase("Ghi chú: \n", contentFont));
            footer.add(new Phrase(phieuNhap.getGhiChu(), contentFont));
            document.add(footer);
        }
    }

    private static PdfPCell createCell(String content, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setFixedHeight(20);
        return cell;
    }

    //functionalities
    public void OpenDirectAddDialog() {
        try {
            new LapPhieuNhapDialog(nhanVienLoggedIn).showAndWait();
        } catch (IOException e) {
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

    private void filterList() {
        dsPhieuNhapFiltered.clear();
        dsPhieuNhapFiltered.addAll(filter.Filter());
    }

    private void resetFilter() {
        maPNTextField.clear();
        nvTextField.clear();
    }
}
