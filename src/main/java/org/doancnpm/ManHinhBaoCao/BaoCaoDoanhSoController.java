package org.doancnpm.ManHinhBaoCao;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
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
import org.doancnpm.Models.BaoCaoDoanhSo;
import org.doancnpm.Models.DaiLy;
import org.doancnpm.SQLUltilities.CalculateSQL;

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
        for (Map.Entry<Integer, Integer> entry : soPhieuXuatData.entrySet()) {
            stt++;
            int maDaiLy = entry.getKey();
            int soPhieuXuat = entry.getValue();
            double tongGiaTri = tongGiaTriData.getOrDefault(maDaiLy, 0.0);
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

        TableColumn<BaoCaoDoanhSo, Integer> sttCol = new TableColumn<>("STT");
        sttCol.setCellValueFactory(new PropertyValueFactory<>("STT"));
        sttCol.setPrefWidth(37.599966645240784);


        TableColumn<BaoCaoDoanhSo, String> tenDaiLyCol = new TableColumn<>("Đại lý");
        tenDaiLyCol.setCellValueFactory(data -> {
            DaiLy daiLy = null;
            try {
                daiLy = DaiLyDAO.getInstance().QueryID(data.getValue().getMaDaiLy());
            } catch (SQLException _) {

            }
            return new SimpleObjectProperty<>(daiLy.getTenDaiLy());
        });
        tenDaiLyCol.setPrefWidth(152.00006484985352);

        TableColumn<BaoCaoDoanhSo, Integer> soPhieuXuatCol = new TableColumn<>("Số phiếu xuất");
        soPhieuXuatCol.setCellValueFactory(new PropertyValueFactory<>("soPhieuXuat"));
        soPhieuXuatCol.setPrefWidth(96.80000305175781);

        TableColumn<BaoCaoDoanhSo, String> tongTriGiaCol = new TableColumn<>("Tổng trị giá");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("#,##0", symbols);
        df.setMaximumFractionDigits(8);
        tongTriGiaCol.setCellValueFactory(data -> {
            return new SimpleObjectProperty<>(df.format(data.getValue().getTongTriGia()));
        });

        tongTriGiaCol.setPrefWidth(119.20001220703125);


        TableColumn<BaoCaoDoanhSo, Double> tyLeCol = new TableColumn<>("Tỷ lệ");
        tyLeCol.setCellValueFactory(data -> {
            double tongGiaTri = 0.0;
            for (BaoCaoDoanhSo item : baoCaoDoanhSoItems) {
                tongGiaTri += item.getTongTriGia();
            }
            if (data.getValue().getSTT() != baoCaoDoanhSoItems.size()) {
                double tyLe = data.getValue().getTongTriGia() / tongGiaTri;
                tyLe = Math.round(tyLe * 100.0) / 100.0;
                data.getValue().setTyLe(tyLe);

                return new SimpleObjectProperty<>(tyLe);
            } else {
                double tongtyLe = 0;
                for (int i = 0; i < data.getValue().getSTT() - 1; i++) {
                    tongtyLe += baoCaoDoanhSoItems.get(i).getTyLe();
                }
                double tyLe = 1 - tongtyLe;
                tyLe = Math.round(tyLe * 100.0) / 100.0;
                data.getValue().setTyLe(tyLe);
                return new SimpleObjectProperty<>(tyLe);
            }
        });
        tyLeCol.setPrefWidth(92.79998779296875);

        tableView.getColumns().addAll(sttCol, tenDaiLyCol, soPhieuXuatCol, tongTriGiaCol, tyLeCol);

        tableView.setItems(baoCaoDoanhSoItems);

        return tableView;
    }

    protected ObservableList<TitledPane> createTitledPanesForMonths(int year) {
        ObservableList<TitledPane> titledPanes = FXCollections.observableArrayList();
        for (int month = 1; month <= 12; month++) {
            int monthValue = month;
            Map<Integer, Integer> soPhieuXuatData = CalculateSQL.getInstance().calSoPhieuXuatVoiDaiLyTheoThang(monthValue, year);
            Map<Integer, Double> tongGiaTriData = CalculateSQL.getInstance().calTongGiaTriPhieuXuatVoiDaiLyTheoThang(monthValue, year);

            if (!soPhieuXuatData.isEmpty() || !tongGiaTriData.isEmpty()) {
                VBox container = new VBox(10);
                container.setAlignment(Pos.CENTER);

                ScrollPane scrollPane = new ScrollPane();
                scrollPane.setFitToWidth(true); // Ensure ScrollPane fits its content width

                VBox layout = new VBox(10);
                layout.setAlignment(Pos.CENTER);
                layout.setPadding(new Insets(10));
                layout.setPrefWidth(600); // Set a preferred width for the layout

                TableView<BaoCaoDoanhSo> tableView = createTableViewForMonth(soPhieuXuatData, tongGiaTriData);
                layout.getChildren().add(tableView);

                Button exportButton = new Button("Xuất PDF");
                exportButton.setOnAction(event -> {
                    exportBaoCaoDoanhSoToPDF(tableView.getItems());
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
                titledPanes.add(titledPane);
            }
        }
        return titledPanes;
    }

    private void exportBaoCaoDoanhSoToPDF(List<BaoCaoDoanhSo> data) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu báo cáo doanh số");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        fileChooser.setInitialFileName("BaoCaoDoanhSo.pdf"); // Đặt tên file mặc định

        // Hiển thị hộp thoại để người dùng chọn vị trí lưu file
        File selectedFile = fileChooser.showSaveDialog(null);

        if (selectedFile != null) {
            Document document = new Document();
            try {
                PdfWriter.getInstance(document, new FileOutputStream(selectedFile.getAbsolutePath()));
                document.open();

                // Tiêu đề của báo cáo
                Paragraph reportTitle = new Paragraph("BÁO CÁO DOANH SỐ", new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD));
                reportTitle.setAlignment(Element.ALIGN_CENTER);
                document.add(reportTitle);

                // Ngày tạo báo cáo
                Paragraph dateParagraph = new Paragraph("Ngày tạo: " + LocalDate.now().toString(), new Font(Font.FontFamily.HELVETICA, 12));
                dateParagraph.setAlignment(Element.ALIGN_RIGHT);
                document.add(dateParagraph);

                document.add(Chunk.NEWLINE); // Thêm khoảng trắng giữa tiêu đề và bảng

                // Tạo bảng PDF

                // Tạo bảng PDF
                PdfPTable table = new PdfPTable(5); // 5 cột cho STT, Đại lý, Số phiếu xuất, Tổng trị giá, Tỷ lệ

                // Thiết lập chiều rộng cho các cột
                float[] columnWidths = {1, 3, 2, 2, 2}; // Tổng chiều rộng là 10
                table.setWidths(columnWidths);

                // Đặt font cho các tiêu đề cột và dữ liệu
                Font font = new Font(BaseFont.createFont("src/main/resources/VNTIME.TTF", BaseFont.IDENTITY_H, BaseFont.EMBEDDED));

                // Tạo tiêu đề cho các cột
                table.addCell(new com.itextpdf.text.Phrase("STT", font));
                table.addCell(new com.itextpdf.text.Phrase("Đại lý", font));
                table.addCell(new com.itextpdf.text.Phrase("Số phiếu xuất", font));
                table.addCell(new com.itextpdf.text.Phrase("Tổng trị giá", font));
                table.addCell(new com.itextpdf.text.Phrase("Tỷ lệ", font));

                double totalTongTriGia = 0.0;
                DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
                symbols.setGroupingSeparator('.');
                DecimalFormat df = new DecimalFormat("#,##0", symbols);
                df.setMaximumFractionDigits(8);
                // Thêm dữ liệu từ TableView vào PDF
                for (BaoCaoDoanhSo item : data) {
                    table.addCell(new com.itextpdf.text.Phrase(String.valueOf(item.getSTT()), font));
                    DaiLy daiLy = null;
                    try {
                        daiLy = DaiLyDAO.getInstance().QueryID(item.getMaDaiLy());
                    } catch (SQLException _) {
                    }
                    table.addCell(new com.itextpdf.text.Phrase(daiLy != null ? daiLy.getTenDaiLy() : "", font));
                    table.addCell(new com.itextpdf.text.Phrase(String.valueOf(item.getSoPhieuXuat()), font));

                    table.addCell(new com.itextpdf.text.Phrase(df.format(item.getTongTriGia()), font));
                    table.addCell(new com.itextpdf.text.Phrase(String.valueOf(item.getTyLe()), font));
                    totalTongTriGia += item.getTongTriGia();
                }


                document.add(table);
                document.add(Chunk.NEWLINE);
                double totalRevenue = data.stream().mapToDouble(BaoCaoDoanhSo::getTongTriGia).sum();
                Paragraph totalRevenueParagraph = new Paragraph("Tổng doanh số: " + df.format(totalRevenue) + "VNĐ", font);
                totalRevenueParagraph.setAlignment(Element.ALIGN_RIGHT); // Căn lề phải
                document.add(totalRevenueParagraph);

                document.add(Chunk.NEWLINE);
                Paragraph contactInfo = new Paragraph();
                contactInfo.setFont(font);
                contactInfo.add(new Phrase("Email: your@email.com", font));
                contactInfo.add(Chunk.NEWLINE); // Xuống dòng
                contactInfo.add(new Phrase("Địa chỉ: Your Address", font));
                contactInfo.add(Chunk.NEWLINE); // Xuống dòng
                contactInfo.add(new Phrase("Hotline: Your Hotline", font));
                contactInfo.setAlignment(Element.ALIGN_LEFT);

                document.add(contactInfo);

                document.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
