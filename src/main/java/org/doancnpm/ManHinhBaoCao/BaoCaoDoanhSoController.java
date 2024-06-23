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
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import org.doancnpm.DAO.DaiLyDAO;
import org.doancnpm.Models.BaoCaoDoanhSo;
import org.doancnpm.Models.DaiLy;
import org.doancnpm.SQLUltilities.CalculateSQL;
import org.doancnpm.Ultilities.PopDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BaoCaoDoanhSoController {
    protected TableView<BaoCaoDoanhSo> createTableViewForMonth(Map<Integer, Integer> soPhieuXuatData, Map<Integer, Double> tongGiaTriData) {
        ObservableList<BaoCaoDoanhSo> baoCaoDoanhSoItems = FXCollections.observableArrayList();
        int stt = 0;
        int totalSoPhieuXuat = 0;
        double totalTongGiaTri = 0.0;

        for (Map.Entry<Integer, Integer> entry : soPhieuXuatData.entrySet()) {
            stt++;
            int maDaiLy = entry.getKey();
            int soPhieuXuat = entry.getValue();
            double tongGiaTri = tongGiaTriData.getOrDefault(maDaiLy, 0.0);
            totalSoPhieuXuat += soPhieuXuat;
            totalTongGiaTri += tongGiaTri;
            BaoCaoDoanhSo item = new BaoCaoDoanhSo(stt, maDaiLy, new Date(), soPhieuXuat, tongGiaTri, 0);
            baoCaoDoanhSoItems.add(item);
        }

        TableView<BaoCaoDoanhSo> tableView = new TableView<>();
        tableView.setMaxHeight(300.0);
        tableView.setMaxWidth(500.0);
        tableView.setMinHeight(300.0);
        tableView.setMinWidth(500.0);
        tableView.setPrefHeight(300.0);
        tableView.setPrefWidth(500.0);

        TableColumn<BaoCaoDoanhSo, String> sttCol = new TableColumn<>("STT");
        sttCol.setCellValueFactory(data -> {
            Integer sttValue = data.getValue().getSTT();
            return new SimpleObjectProperty<>(sttValue == 0 ? "" : sttValue.toString());
        });
        sttCol.setPrefWidth(37.6);

        TableColumn<BaoCaoDoanhSo, String> tenDaiLyCol = new TableColumn<>("Đại lý");
        tenDaiLyCol.setCellValueFactory(data -> {
            if (data.getValue().getMaDaiLy() == 0) {
                return new SimpleObjectProperty<>("");
            } else {
                DaiLy daiLy = null;
                try {
                    daiLy = DaiLyDAO.getInstance().QueryID(data.getValue().getMaDaiLy());
                } catch (SQLException _) {
                }
                return new SimpleObjectProperty<>(daiLy != null ? daiLy.getTenDaiLy() : "");
            }
        });
        tenDaiLyCol.setPrefWidth(152.0);

        TableColumn<BaoCaoDoanhSo, String> soPhieuXuatCol = new TableColumn<>("Số phiếu xuất");
        soPhieuXuatCol.setCellValueFactory(data -> {
            Integer sttValue = data.getValue().getSTT();
            if (sttValue != 0) {
                return new SimpleObjectProperty<>(String.valueOf(data.getValue().getSoPhieuXuat()));
            } else {
                return new SimpleObjectProperty<>(""); // Return an empty string for STT == 0
            }
        });
        soPhieuXuatCol.setPrefWidth(96.8);


        TableColumn<BaoCaoDoanhSo, String> tongTriGiaCol = new TableColumn<>("Tổng trị giá");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("#,##0", symbols);
        df.setMaximumFractionDigits(8);
        tongTriGiaCol.setCellValueFactory(data -> {
            if (data.getValue().getSTT() == 0) {
                return new SimpleObjectProperty<>("");
            } else {
                return new SimpleObjectProperty<>(df.format(data.getValue().getTongTriGia()));
            }
        });
        tongTriGiaCol.setPrefWidth(119.2);


        TableColumn<BaoCaoDoanhSo, String> tyLeCol = new TableColumn<>("Tỷ lệ");
        tyLeCol.setCellValueFactory(data -> {
            if (data.getValue().getSTT() == 0) {
                return new SimpleObjectProperty<>(""); // Trả về chuỗi rỗng nếu STT = 0
            } else {
                double tongGiaTri = 0.0;
                for (BaoCaoDoanhSo item : baoCaoDoanhSoItems) {
                    if (item.getSTT() != 0) {
                        tongGiaTri += item.getTongTriGia();
                    }
                }
                if (data.getValue().getSTT() != baoCaoDoanhSoItems.size()) {
                    double tyLe = data.getValue().getTongTriGia() / tongGiaTri;
                    tyLe = Math.round(tyLe * 100.0) / 100.0;
                    data.getValue().setTyLe(tyLe);
                    return new SimpleObjectProperty<>(String.format("%.2f", tyLe)); // Định dạng lại thành chuỗi với 2 chữ số thập phân
                } else {
                    double tongtyLe = 0;
                    for (int i = 0; i < data.getValue().getSTT() - 1; i++) {
                        if (baoCaoDoanhSoItems.get(i).getSTT() != 0) {
                            tongtyLe += baoCaoDoanhSoItems.get(i).getTyLe();
                        }
                    }
                    double tyLe = 1 - tongtyLe;
                    tyLe = Math.round(tyLe * 100.0) / 100.0;
                    data.getValue().setTyLe(tyLe);
                    return new SimpleObjectProperty<>(String.format("%.2f", tyLe)); // Định dạng lại thành chuỗi với 2 chữ số thập phân
                }
            }
        });
        tyLeCol.setPrefWidth(92.8);
        tableView.getColumns().addAll(sttCol, tenDaiLyCol, soPhieuXuatCol, tongTriGiaCol, tyLeCol);
        tableView.setItems(baoCaoDoanhSoItems);

        return tableView;
    }


    protected ObservableList<TitledPane> createTitledPanesForMonths(int selectedMonth, int year) {
        ObservableList<TitledPane> titledPanes = FXCollections.observableArrayList();
        if (selectedMonth != 0) {
            Map<Integer, Integer> soPhieuXuatData = CalculateSQL.getInstance().calSoPhieuXuatVoiDaiLyTheoThang(selectedMonth, year);
            Map<Integer, Double> tongGiaTriData = CalculateSQL.getInstance().calTongGiaTriPhieuXuatVoiDaiLyTheoThang(selectedMonth, year);

            if (!soPhieuXuatData.isEmpty() || !tongGiaTriData.isEmpty()) {
                VBox container = new VBox(10);
                container.setAlignment(Pos.CENTER);

                ScrollPane scrollPane = new ScrollPane();
                scrollPane.setFitToWidth(true);

                VBox layout = new VBox(10);
                layout.setAlignment(Pos.CENTER);
                layout.setPadding(new Insets(10));
                layout.setPrefWidth(600);

                TableView<BaoCaoDoanhSo> tableView = createTableViewForMonth(soPhieuXuatData, tongGiaTriData);

                // Create the summary box
                VBox summaryBox = createSummaryBox(soPhieuXuatData, tongGiaTriData);
                summaryBox.prefWidthProperty().bind(tableView.widthProperty());
                summaryBox.setMaxWidth(Region.USE_PREF_SIZE);

                // Add components to the layout
                layout.getChildren().addAll(tableView,summaryBox);
                Button exportButton = new Button("Xuất PDF");
                exportButton.setOnAction(event -> {
                    exportBaoCaoDoanhSoToPDF(tableView.getItems(), selectedMonth, year);
                });
                HBox buttonContainer = new HBox(10);
                buttonContainer.setAlignment(Pos.CENTER_RIGHT);
                buttonContainer.getChildren().add(exportButton);
                layout.getChildren().add(buttonContainer);

                scrollPane.setContent(layout);
                container.getChildren().add(scrollPane);

                TitledPane titledPane = new TitledPane();
                titledPane.setText("Tháng " + selectedMonth);
                titledPane.setContent(container);
                titledPanes.add(titledPane);
            }
        } else {
            for (int month = 1; month <= 12; month++) {
                int monthValue = month;
                Map<Integer, Integer> soPhieuXuatData = CalculateSQL.getInstance().calSoPhieuXuatVoiDaiLyTheoThang(monthValue, year);
                Map<Integer, Double> tongGiaTriData = CalculateSQL.getInstance().calTongGiaTriPhieuXuatVoiDaiLyTheoThang(monthValue, year);

                if (!soPhieuXuatData.isEmpty() || !tongGiaTriData.isEmpty()) {
                    VBox container = new VBox(10);
                    container.setAlignment(Pos.CENTER);

                    ScrollPane scrollPane = new ScrollPane();
                    scrollPane.setFitToWidth(true);

                    VBox layout = new VBox(10);
                    layout.setAlignment(Pos.CENTER);
                    layout.setPadding(new Insets(10));
                    layout.setPrefWidth(600);

                    TableView<BaoCaoDoanhSo> tableView = createTableViewForMonth(soPhieuXuatData, tongGiaTriData);

                    // Create the summary box
                    VBox summaryBox = createSummaryBox(soPhieuXuatData, tongGiaTriData);
                    summaryBox.prefWidthProperty().bind(tableView.widthProperty());
                    summaryBox.setMaxWidth(Region.USE_PREF_SIZE);

                    // Add components to the layout
                    layout.getChildren().addAll(tableView,summaryBox);

                    Button exportButton = new Button("Xuất PDF");
                    exportButton.setOnAction(event -> {
                        exportBaoCaoDoanhSoToPDF(tableView.getItems(), monthValue, year);
                    });
                    HBox buttonContainer = new HBox(10);
                    buttonContainer.setAlignment(Pos.CENTER_RIGHT);
                    buttonContainer.getChildren().add(exportButton);
                    layout.getChildren().add(buttonContainer);

                    scrollPane.setContent(layout);
                    container.getChildren().add(scrollPane);

                    TitledPane titledPane = new TitledPane();
                    titledPane.setText("Tháng " + month);
                    titledPane.setContent(container);
                    titledPane.setExpanded(false);
                    titledPanes.add(titledPane);
                }
            }
        }
        return titledPanes;
    }
    private VBox createSummaryBox(Map<Integer, Integer> soPhieuXuatData, Map<Integer, Double> tongGiaTriData) {
        VBox summaryBox = new VBox(5);
        summaryBox.setAlignment(Pos.CENTER_LEFT);
        summaryBox.setPadding(new Insets(10, 0, 10, 0));

        Label lblTongDaiLy = new Label("Tổng số đại lý có hoạt động:");
        Label lblTongPhieuXuat = new Label("Tổng số phiếu xuất:");
        Label lblTongTriGia = new Label("Tổng trị giá:");

        for (Label lbl : new Label[]{lblTongDaiLy, lblTongPhieuXuat, lblTongTriGia}) {
            lbl.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        }

        int tongDaiLy = soPhieuXuatData.size();
        int tongPhieuXuat = soPhieuXuatData.values().stream().mapToInt(Integer::intValue).sum();
        double tongTriGia = tongGiaTriData.values().stream().mapToDouble(Double::doubleValue).sum();

        lblTongDaiLy.setText(lblTongDaiLy.getText() + " " + tongDaiLy);
        lblTongPhieuXuat.setText(lblTongPhieuXuat.getText() + " " + tongPhieuXuat);
        lblTongTriGia.setText(lblTongTriGia.getText() + " " + String.format("%,.0f", tongTriGia));

        summaryBox.getChildren().addAll(lblTongDaiLy, lblTongPhieuXuat, lblTongTriGia);
        return summaryBox;
    }
    private static void exportBaoCaoDoanhSoToPDF(List<BaoCaoDoanhSo> data, int month, int year) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu báo cáo doanh số");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        String initialFileName = String.format("BaoCaoDoanhSo_%02d_%d.pdf", month, year);
        fileChooser.setInitialFileName(initialFileName);

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
                addHeader(document, titleFont, contentFont, month, year);

                // Add company details
                addDetails(document, boldFont, contentFont);

                // Create and add table
                PdfPTable table = createTable(data, contentFont, boldFont);
                document.add(table);

                document.close();
                PopDialog.popSuccessDialog("Xuất báo cáo doanh số tháng " + month + " năm " + year + " thành công.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void addHeader(Document document, Font titleFont, Font contentFont, int month, int year) throws DocumentException {
        // Add title
        Paragraph reportTitle = new Paragraph("BÁO CÁO DOANH SỐ THÁNG " + month + " NĂM " + year, titleFont);
        reportTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(reportTitle);

        // Add creation date
        Paragraph dateParagraph = new Paragraph("Ngày tạo: " + LocalDate.now().toString(), contentFont);
        dateParagraph.setAlignment(Element.ALIGN_RIGHT);
        document.add(dateParagraph);

        document.add(Chunk.NEWLINE);
    }

    private static void addDetails(Document document, Font boldFont, Font contentFont) throws DocumentException {
        Paragraph company = new Paragraph();
        company.setFont(contentFont);
        company.add(Chunk.NEWLINE);
        company.add(new Phrase("Thông tin liên hệ\n", contentFont));
        company.add(new Phrase("Nhóm 27\n", boldFont));
        company.add(new Phrase("SE104.O27\n", contentFont));
        company.add(new Phrase("Địa chỉ: 123 Đường ABC, Thành phố DEF\n", contentFont));
        company.setAlignment(Element.ALIGN_LEFT);

        document.add(company);
        document.add(Chunk.NEWLINE);
    }

    private static PdfPTable createTable(List<BaoCaoDoanhSo> data, Font contentFont, Font boldFont) throws DocumentException {
        PdfPTable table = new PdfPTable(5); // 5 columns
        table.setWidthPercentage(100);
        float[] columnWidths = {1, 3, 2, 2, 2};
        table.setWidths(columnWidths);

        // Add table headers
        table.addCell(createCell("STT", boldFont));
        table.addCell(createCell("Đại lý", boldFont));
        table.addCell(createCell("Số phiếu xuất", boldFont));
        table.addCell(createCell("Tổng trị giá", boldFont));
        table.addCell(createCell("Tỷ lệ", boldFont));

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("#,##0", symbols);
        df.setMaximumFractionDigits(8);

        // Add table data
        for (BaoCaoDoanhSo item : data) {
            if (item.getSTT() != 0) {
                table.addCell(createCell(String.valueOf(item.getSTT()), contentFont));
            } else {
                table.addCell(createCell("", contentFont));
            }

            if (item.getSTT() == 0) {
                table.addCell(createCell("Tổng doanh số", contentFont));
            } else {
                DaiLy daiLy = null;
                try {
                    daiLy = DaiLyDAO.getInstance().QueryID(item.getMaDaiLy());
                } catch (SQLException _) {
                }
                table.addCell(createCell(daiLy != null ? daiLy.getTenDaiLy() : "", contentFont));
            }
            table.addCell(createCell(String.valueOf(item.getSoPhieuXuat()), contentFont));
            table.addCell(createCell(df.format(item.getTongTriGia()), contentFont));
            if (item.getSTT() != 0) {
                table.addCell(createCell(String.valueOf(item.getTyLe()), contentFont));
            } else {
                table.addCell(createCell("", contentFont)); // Add an empty cell for the total row
            }
        }

        return table;
    }

    private static PdfPCell createCell(String content, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }

    private static void addHeaderNam(Document document, Font titleFont, Font contentFont, int year) throws DocumentException {
        // Add title
        Paragraph reportTitle = new Paragraph("BÁO CÁO DOANH SỐ NĂM " + year, titleFont);
        reportTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(reportTitle);

        // Add creation date
        Paragraph dateParagraph = new Paragraph("Ngày tạo: " + LocalDate.now().toString(), contentFont);
        dateParagraph.setAlignment(Element.ALIGN_RIGHT);
        document.add(dateParagraph);

        document.add(Chunk.NEWLINE);
    }

    protected static void exportBaoCaoDoanhSoNamPDF(List<BaoCaoDoanhSo> data, int year) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu báo cáo doanh số");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        String initialFileName = String.format("BaoCaoDoanhSo_%d.pdf", year);
        fileChooser.setInitialFileName(initialFileName);

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
                addHeaderNam(document, titleFont, contentFont, year);

                // Add company details
                addDetails(document, boldFont, contentFont);

                // Create and add table
                PdfPTable table = createTable(data, contentFont, boldFont);
                document.add(table);


                document.close();
                PopDialog.popSuccessDialog("Xuất báo cáo doanh số năm " + year + " thành công.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
