package org.doancnpm.ManHinhBaoCao;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.doancnpm.DAO.DaiLyDAO;
import org.doancnpm.Models.BaoCaoCongNo;
import org.doancnpm.Models.DaiLy;
import org.doancnpm.SQLUltilities.CalculateSQL;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

public class BaoCaoCongNoController {
    protected TableView<BaoCaoCongNo> createTableViewForMonth(Map<Integer, Map<String, Map<Double, Double>>> totalDebtsData, int month, int year) throws SQLException {
        ObservableList<BaoCaoCongNo> baoCaoCongNoItems = FXCollections.observableArrayList();

        Set<Integer> activeDaiLyIDs = CalculateSQL.getInstance().filterDaiLyIDs(month, year);
        int stt = 0;
        for (Map.Entry<Integer, Map<String, Map<Double, Double>>> outerEntry : totalDebtsData.entrySet()) {
            int maDaiLy = outerEntry.getKey();
            if (activeDaiLyIDs.contains(maDaiLy)) {
                stt++;
                String key = year + "-" + month;
                Map<String, Map<Double, Double>> debtDetailsMap = outerEntry.getValue();
                Map<Double, Double> debtDetails = debtDetailsMap.getOrDefault(key, new HashMap<>());
                double noDau = 0.0;
                double noCuoi = 0.0;

                if (!debtDetails.isEmpty()) {
                    noDau = debtDetails.keySet().iterator().next();
                    noCuoi = debtDetails.get(noDau);
                }

                BaoCaoCongNo item = new BaoCaoCongNo(stt, maDaiLy, new Date(), noDau, noCuoi);
                baoCaoCongNoItems.add(item);
            }
        }

        TableView<BaoCaoCongNo> tableView = new TableView<>();
        tableView.setMaxHeight(300.0);
        tableView.setMaxWidth(500.0);
        tableView.setMinHeight(300.0);
        tableView.setMinWidth(500.0);
        tableView.setPrefHeight(300.0);
        tableView.setPrefWidth(500.0);

        TableColumn<BaoCaoCongNo, Integer> sttCol = new TableColumn<>("STT");
        sttCol.setCellValueFactory(new PropertyValueFactory<>("STT"));
        sttCol.setPrefWidth(37.599966645240784);

        TableColumn<BaoCaoCongNo, String> tenDaiLyCol = new TableColumn<>("Đại lý");
        tenDaiLyCol.setCellValueFactory(data -> {
            DaiLy daiLy = null;
            try {
                daiLy = DaiLyDAO.getInstance().QueryID(data.getValue().getMaDaiLy());
            } catch (SQLException _) {
            }
            return new SimpleObjectProperty<>(daiLy.getTenDaiLy());
        });
        tenDaiLyCol.setPrefWidth(150);

        TableColumn<BaoCaoCongNo, String> noDauCol = new TableColumn<>("Nợ Đầu");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("#,##0", symbols);
        df.setMaximumFractionDigits(8);
        noDauCol.setCellValueFactory(data -> {
            return new SimpleObjectProperty<>(df.format(data.getValue().getNoDau()));
        });
        noDauCol.setPrefWidth(150);

        TableColumn<BaoCaoCongNo, String> noCuoiCol = new TableColumn<>("Nợ Cuối");
        noCuoiCol.setCellValueFactory(data -> {
            return new SimpleObjectProperty<>(df.format(data.getValue().getNoCuoi()));
        });
        noCuoiCol.setPrefWidth(150);

        tableView.getColumns().addAll(sttCol, tenDaiLyCol, noDauCol, noCuoiCol);

        tableView.setItems(baoCaoCongNoItems);

        return tableView;
    }
    protected ObservableList<TitledPane> createTitledPanesForMonths(int year) {
        ObservableList<TitledPane> titledPanes = FXCollections.observableArrayList();
        Set<Integer> activeMonths = CalculateSQL.getInstance().findActiveMonths(year);

        for (int monthValue = 1; monthValue <= 12; monthValue++) {

            if (activeMonths.contains(monthValue)) {
                try {
                    Map<Integer, Map<String, Map<Double, Double>>> totalDebtsData = CalculateSQL.getInstance().calculateDebtUntilMonthWithDaiLy(year);
                    VBox container = new VBox(10);
                    container.setAlignment(Pos.CENTER);

                    ScrollPane scrollPane = new ScrollPane();
                    scrollPane.setFitToWidth(true); // Ensure ScrollPane fits its content width

                    VBox layout = new VBox(10);
                    layout.setAlignment(Pos.CENTER);
                    layout.setPadding(new Insets(10));
                    layout.setPrefWidth(600); // Set a preferred width for the layout

                    TableView<BaoCaoCongNo> tableView = createTableViewForMonth(totalDebtsData, monthValue, year);
                    layout.getChildren().add(tableView);

                    Button exportButton = new Button("Xuất PDF");
                    exportButton.setOnAction(event -> {
                        exportBaoCaoCongNoToPDF(tableView.getItems());
                    });
                    HBox buttonContainer = new HBox(10);
                    buttonContainer.setAlignment(Pos.CENTER_RIGHT);
                    buttonContainer.getChildren().add(exportButton);
                    layout.getChildren().add(buttonContainer);

                    scrollPane.setContent(layout);
                    container.getChildren().add(scrollPane);

                    TitledPane titledPane = new TitledPane();
                    titledPane.setText("Tháng " + monthValue);
                    titledPane.setContent(container);
                    titledPanes.add(titledPane);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return titledPanes;
    }


    private static void exportBaoCaoCongNoToPDF(List<BaoCaoCongNo> data) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu báo cáo doanh số");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        fileChooser.setInitialFileName("BaoCaoDoanhSo.pdf");

        File selectedFile = fileChooser.showSaveDialog(null);

        if (selectedFile != null) {
            Document document = new Document();
            try {
                BaseFont baseFont = BaseFont.createFont("src/main/resources/vuArial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                Font titleFont = new Font(baseFont, 16, Font.BOLD);
                Font contentFont = new Font(baseFont, 12);
                Font boldFont = new Font(baseFont, 12, Font.BOLD);
                PdfWriter.getInstance(document, new FileOutputStream(selectedFile.getAbsolutePath()));
                document.open();

                // Add header
                addHeader(document, titleFont, contentFont);

                // Add customer and company details
                addCustomerAndCompanyDetails(document, boldFont, contentFont);

                // Create and add table
                PdfPTable table = createTable(data, contentFont, boldFont);
                document.add(table);

                // Add footer
                addFooter(document, contentFont);

                document.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void addHeader(Document document, Font titleFont, Font contentFont) throws DocumentException {
        // Add title
        Paragraph reportTitle = new Paragraph("BÁO CÁO CÔNG NỢ", titleFont);
        reportTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(reportTitle);

        // Add creation date
        Paragraph dateParagraph = new Paragraph("Ngày tạo: " + LocalDate.now().toString(), contentFont);
        dateParagraph.setAlignment(Element.ALIGN_RIGHT);
        document.add(dateParagraph);

        document.add(Chunk.NEWLINE);
    }

    private static void addCustomerAndCompanyDetails(Document document, Font boldFont, Font contentFont) throws DocumentException {
        PdfPTable detailsTable = new PdfPTable(2);
        detailsTable.setWidthPercentage(100);
        detailsTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        // Customer details
        PdfPCell customerDetailsCell = new PdfPCell();
        customerDetailsCell.setBorder(Rectangle.NO_BORDER);
        customerDetailsCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        Paragraph customerDetails = new Paragraph();
        customerDetails.add(new Phrase("KHÁCH SẠN TRE XANH\n", boldFont));
        customerDetails.add(new Phrase("Số điện thoại khách hàng: +84 912 345 678\n", contentFont));
        customerDetails.add(new Phrase("Địa chỉ khách hàng: 148 Hồ Tùng Mậu, Bắc Từ Liêm, Hà Nội\n", contentFont));
        customerDetails.add(new Phrase("Hóa đơn #12345\n", contentFont));
        customerDetails.add(new Phrase("Ngày: 01/06/2025\n", contentFont));
        customerDetailsCell.addElement(customerDetails);

        // Company details
        PdfPCell companyDetailsCell = new PdfPCell();
        companyDetailsCell.setBorder(Rectangle.NO_BORDER);
        companyDetailsCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        Paragraph companyDetails = new Paragraph();
        companyDetails.add(new Phrase("Công ty XNK Thanh Hà\n", boldFont));
        companyDetails.add(new Phrase("Email: xinchao@trangwebhay.vn\n", contentFont));
        companyDetails.add(new Phrase("Địa chỉ: 123 Đường ABC, Thành phố DEF\n", contentFont));
        companyDetails.add(new Phrase("Hotline: +84 912 345 678\n", contentFont));
        companyDetailsCell.addElement(companyDetails);

        detailsTable.addCell(customerDetailsCell);
        detailsTable.addCell(companyDetailsCell);

        document.add(detailsTable);
        document.add(Chunk.NEWLINE);
    }

    private static PdfPTable createTable(List<BaoCaoCongNo> data, Font contentFont, Font boldFont) throws DocumentException {
        PdfPTable table = new PdfPTable(4); // 5 columns
        table.setWidthPercentage(100);
        float[] columnWidths = {1, 3, 3, 3};
        table.setWidths(columnWidths);

        // Add table headers
        table.addCell(createCell("STT", boldFont));
        table.addCell(createCell("Đại lý", boldFont));
        table.addCell(createCell("Nợ đầu", boldFont));
        table.addCell(createCell("Nợ cuối", boldFont));

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("#,##0", symbols);
        df.setMaximumFractionDigits(8);

        // Add table data
        for (BaoCaoCongNo item : data) {
            table.addCell(createCell(String.valueOf(item.getSTT()), contentFont));
            DaiLy daiLy = null;
            try {
                daiLy = DaiLyDAO.getInstance().QueryID(item.getMaDaiLy());
            } catch (SQLException _) {
            }
            table.addCell(createCell(daiLy != null ? daiLy.getTenDaiLy() : "", contentFont));
            table.addCell(createCell(df.format(item.getNoDau()), contentFont));
            table.addCell(createCell(df.format(item.getNoCuoi()), contentFont));
        }

        return table;
    }

    private static void addFooter(Document document, Font contentFont) throws DocumentException {
        Paragraph footer = new Paragraph();
        footer.setFont(contentFont);
        footer.add(new Phrase("Thông tin thanh toán\n", contentFont));
        footer.add(new Phrase("Ngân hàng VIB\n", contentFont));
        footer.add(new Phrase("Tên tài khoản: Công ty XNK Thanh Hà\n", contentFont));
        footer.add(new Phrase("Số tài khoản: 123-456-7890\n", contentFont));
        footer.add(new Phrase("Hạn thanh toán: 01/07/2025\n", contentFont));
        footer.add(Chunk.NEWLINE);
        footer.add(new Phrase("Thông tin liên hệ\n", contentFont));
        footer.add(new Phrase("Email: xinchao@trangwebhay.vn\n", contentFont));
        footer.add(new Phrase("Địa chỉ: 123 Đường ABC, Thành phố DEF\n", contentFont));
        footer.add(new Phrase("Hotline: +84 912 345 678\n", contentFont));
        footer.setAlignment(Element.ALIGN_LEFT);

        document.add(footer);
    }

    private static PdfPCell createCell(String content, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }
}