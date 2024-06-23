package org.doancnpm.ManHinhBaoCao;

import io.github.palexdev.materialfx.controls.MFXComboBox;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javafx.util.Pair;
import org.doancnpm.DAO.DaiLyDAO;
import org.doancnpm.DAO.PhieuThuDAO;
import org.doancnpm.DAO.PhieuXuatDAO;
import org.doancnpm.Models.BaoCaoCongNo;
import org.doancnpm.Models.BaoCaoDoanhSo;
import org.doancnpm.Models.DaiLy;
import org.doancnpm.SQLUltilities.CalculateSQL;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class ManHinhBaoCaoController implements Initializable {
    private Stage stage;
    @FXML
    private LineChart<String, Number> mixlineChart;
    @FXML
    private ComboBox<Integer> CbYear;
    @FXML
    private MFXComboBox<Integer> thangComboBox;
    @FXML
    private VBox accorditionDoanhSo;
    @FXML
    private VBox accorditionCongNo;
    @FXML
    Region manHinhBaoCao;
    @FXML
    Button exportBaoCaoDSNam, exportBaoCaoCNNam;
    @FXML
    Tab chartTab, DSTab, CNTab;
    @FXML
    TabPane BaoCaoTabPane;

    public void setVisibility(boolean visibility) {
        manHinhBaoCao.setVisible(visibility);
    }

    BaoCaoDoanhSoController baoCaoDoanhSoController = new BaoCaoDoanhSoController();
    BaoCaoCongNoController baoCaoCongNoController = new BaoCaoCongNoController();
    BieuDoController bieuDoController = new BieuDoController();

    public int currentYear = LocalDate.now().getYear();
    public int currentMonth = LocalDate.now().getMonthValue();

    public void setPrimaryStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initLineChart(currentYear);
        initComboBox();
        initAccordion(0, currentYear);
        exportBaoCaoDSNam.setOnAction(actionEvent -> {
            handleXuatBaoCaoDSNam(CbYear.getValue());
        });
        exportBaoCaoCNNam.setOnAction(actionEvent -> {
            handleXuatBaoCaoCNNam(CbYear.getValue());
        });

        PhieuXuatDAO.getInstance().AddDatabaseListener(observable -> {
            initLineChart(CbYear.getValue());
            initAccordion(thangComboBox.getValue(), CbYear.getValue());
        });
        PhieuThuDAO.getInstance().AddDatabaseListener(observable -> {
            initLineChart(CbYear.getValue());
            initAccordion(thangComboBox.getValue(), CbYear.getValue());
        });
    }

    private void initComboBox() {

        for (int year = currentYear; year >= currentYear - 5; year--) {
            CbYear.getItems().add(year);
        }

        CbYear.setValue(currentYear);

        for (int i = 1; i <= currentMonth - 1; i++) {
            thangComboBox.getItems().add(i);
        }

        CbYear.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                int selectedYear = newValue;
                thangComboBox.getItems().clear();
                Set<Integer> activeMonths = CalculateSQL.getInstance().findActiveMonths(selectedYear);
                if (selectedYear == currentYear) {
                    for (int i = 1; i <= currentMonth-1; i++) {
                        if(activeMonths.contains(i)) {
                            thangComboBox.getItems().add(i);
                        }
                    }
                }
                else{
                    for (int i = 1; i <= 12; i++) {
                        if(activeMonths.contains(i)) {
                            thangComboBox.getItems().add(i);
                        }
                    }
                }
                initLineChart(selectedYear);
                initAccordion(0, selectedYear);
            }
        });
        thangComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                int selectedMonth = newValue;
                initAccordion(selectedMonth, CbYear.getValue());

                if (BaoCaoTabPane != null) {
                    Tab selectedTab = BaoCaoTabPane.getSelectionModel().getSelectedItem();
                    if (selectedTab == chartTab) {
                        BaoCaoTabPane.getSelectionModel().select(DSTab);
                    }
                }
            }
        });
    }

    private void initAccordion(int month, int year) {
        accorditionDoanhSo.getChildren().clear();
        accorditionDoanhSo.getChildren().addAll(baoCaoDoanhSoController.createTitledPanesForMonths(month, year));
        accorditionCongNo.getChildren().clear();
        accorditionCongNo.getChildren().addAll(baoCaoCongNoController.createTitledPanesForMonths(month, year));
    }


    private void initLineChart(int selectedYear) {
        mixlineChart.setTitle("Hoạt động doanh thu và nợ hàng tháng trong năm "+selectedYear);
        CalculateSQL calculateSQL = CalculateSQL.getInstance();

        mixlineChart.getData().clear();
        // Tạo dữ liệu cho tổng doanh số
        XYChart.Series<String, Number> totalSalesSeries = bieuDoController.createSalesDataSeries(selectedYear);
        totalSalesSeries.setName("Tổng doanh số");
        mixlineChart.getData().add(totalSalesSeries);


        // Tạo dữ liệu cho tổng nợ của các đại lý
        XYChart.Series<String, Number> totalReceiptsSeries = bieuDoController.createReceiptsSeries(selectedYear);
        totalReceiptsSeries.setName("Tổng giá trị phiếu thu");
        mixlineChart.getData().add(totalReceiptsSeries);


        // Đảm bảo trục X hiển thị tất cả các tháng
        CategoryAxis xAxis = (CategoryAxis) mixlineChart.getXAxis();
        xAxis.setCategories(FXCollections.observableArrayList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"));
    }

    private void handleXuatBaoCaoDSNam(int year) {
        Map<Integer, BaoCaoDoanhSo> baoCaoNamMap = new HashMap<>();
        int stt = 0;
        double totalSoPhieuXuat = 0;
        double totalTongGiaTri = 0;

        // Lặp qua từng tháng trong năm
        int currentMonth = LocalDate.now().getMonthValue(); // Lấy tháng hiện tại
        int endMonth = (year == LocalDate.now().getYear()) ? currentMonth : 12; // Nếu năm truyền vào là năm nay, chỉ xét đến tháng hiện tại
        for (int month = 1; month <= endMonth; month++) {
            int monthValue = month;
            Map<Integer, Integer> soPhieuXuatData = CalculateSQL.getInstance().calSoPhieuXuatVoiDaiLyTheoThang(monthValue, year);
            Map<Integer, Double> tongGiaTriData = CalculateSQL.getInstance().calTongGiaTriPhieuXuatVoiDaiLyTheoThang(monthValue, year);

            // Lặp qua từng đại lý trong dữ liệu của mỗi tháng
            for (Map.Entry<Integer, Integer> entry : soPhieuXuatData.entrySet()) {
                int maDaiLy = entry.getKey();
                int soPhieuXuat = entry.getValue();
                double tongGiaTri = tongGiaTriData.getOrDefault(maDaiLy, 0.0);
                stt++;

                // Kiểm tra xem đã có BaoCaoDoanhSo cho đại lý này trong map chưa
                if (baoCaoNamMap.containsKey(maDaiLy)) {
                    // Nếu đã có, cập nhật thông tin tổng số phiếu xuất và tổng giá trị
                    BaoCaoDoanhSo baoCaoDoanhSo = baoCaoNamMap.get(maDaiLy);
                    baoCaoDoanhSo.setSoPhieuXuat(baoCaoDoanhSo.getSoPhieuXuat() + soPhieuXuat);
                    baoCaoDoanhSo.setTongTriGia(baoCaoDoanhSo.getTongTriGia() + tongGiaTri);
                } else {
                    // Nếu chưa có, tạo mới BaoCaoDoanhSo và thêm vào map
                    BaoCaoDoanhSo item = new BaoCaoDoanhSo(stt, maDaiLy, new Date(), soPhieuXuat, tongGiaTri, 0);
                    baoCaoNamMap.put(maDaiLy, item);
                }

                // Cập nhật tổng số phiếu xuất và tổng giá trị cả năm
                totalSoPhieuXuat += soPhieuXuat;
                totalTongGiaTri += tongGiaTri;
            }
        }
        List<BaoCaoDoanhSo> baoCaoNamList = new ArrayList<>(baoCaoNamMap.values());
        // Lưu tổng số phiếu xuất và tổng giá trị của tất cả các đại lý cộng lại
        int totalPhieuXuatNam = baoCaoNamList.stream().mapToInt(BaoCaoDoanhSo::getSoPhieuXuat).sum();
        double totalTongGiaTriNam = baoCaoNamList.stream().mapToDouble(BaoCaoDoanhSo::getTongTriGia).sum();
        // Thêm dòng tổng vào BaoCaoDoanhSo
        BaoCaoDoanhSo totalItem = new BaoCaoDoanhSo();
        totalItem.setSoPhieuXuat(totalPhieuXuatNam);
        totalItem.setTongTriGia(totalTongGiaTriNam);
        totalItem.setTyLe(1.0); // 100% của tổng
        totalItem.setSTT(0); // Đánh dấu là dòng tổng
        baoCaoNamList.add(totalItem); // Thêm dòng tổng vào danh sách baoCaoNamList

        for (BaoCaoDoanhSo item : baoCaoNamList) {
            if (item.getSTT() != 0 && item.getSTT() != baoCaoNamList.size()) {
                double tyLe = item.getTongTriGia() / totalTongGiaTriNam;
                tyLe = Math.round(tyLe * 100.0) / 100.0;
                item.setTyLe(tyLe);
            } else if (item.getSTT() != 0) {
                double tongtyLe = 0;
                for (BaoCaoDoanhSo items : baoCaoNamList) {
                    if (items.getSTT() != 0) {
                        tongtyLe += items.getTyLe();
                    }
                }
                double tyLe = 1 - tongtyLe;
                tyLe = Math.round(tyLe * 100.0) / 100.0;
                item.setTyLe(tyLe);
            }
        }
        BaoCaoDoanhSoController.exportBaoCaoDoanhSoNamPDF(baoCaoNamList, year);
    }

    private void handleXuatBaoCaoCNNam(int year) {
        ObservableList<BaoCaoCongNo> baoCaoCongNoItems = FXCollections.observableArrayList();
        Map<Integer, Map<String, Pair<Double, Double>>> totalDebtsData = null;
        Set<Integer> activeDaiLyIDs = null;
        int currentYear = LocalDate.now().getYear();

        try {
            activeDaiLyIDs = CalculateSQL.getInstance().filterDaiLyIDsNam(year);
            totalDebtsData = CalculateSQL.getInstance().calculateDebtUntilMonthWithDaiLy(year);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        int stt = 0;
        for (Map.Entry<Integer, Map<String, Pair<Double, Double>>> outerEntry : totalDebtsData.entrySet()) {
            int maDaiLy = outerEntry.getKey();
            if (activeDaiLyIDs.contains(maDaiLy)) {
                stt++;
                String keyNoCuoi = year + "-" + 12;
                String keyNoDau = year + "-" + 1;
                Map<String, Pair<Double, Double>> debtDetailsMap = outerEntry.getValue();
                Pair<Double, Double> debtDetailsDau = debtDetailsMap.getOrDefault(keyNoDau, new Pair<>(0.0, 0.0));
                Pair<Double, Double> debtDetailsCuoi = debtDetailsMap.getOrDefault(keyNoCuoi, new Pair<>(0.0, 0.0));
                double noDau = debtDetailsDau.getKey();
                double noCuoi = debtDetailsCuoi.getValue();

                if (year == currentYear) {
                    try {
                        DaiLy daiLy = DaiLyDAO.getInstance().QueryID(maDaiLy);
                        noCuoi = daiLy.getNoHienTai();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }

                BaoCaoCongNo item = new BaoCaoCongNo(stt, maDaiLy, new Date(), noDau, noCuoi, 0);
                baoCaoCongNoItems.add(item);
            }
        }
        BaoCaoCongNoController.exportBaoCaoCongNoNamPDF(baoCaoCongNoItems, year);
    }

}
