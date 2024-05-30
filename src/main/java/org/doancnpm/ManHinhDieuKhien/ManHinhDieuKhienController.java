package org.doancnpm.ManHinhDieuKhien;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.doancnpm.SQLUltilities.CalculateSQL;

import java.net.URL;
import java.time.LocalDate;
import java.util.Map;
import java.util.ResourceBundle;

public class ManHinhDieuKhienController implements Initializable {

    private Stage stage;

    public void setPrimaryStage(Stage primaryStage) {
        this.stage = primaryStage;
    }

    @FXML
    private LineChart<String, Number> mixlineChart;
    @FXML
    private PieChart tonkhoPieChart;
    @FXML
    private Text nvText,dlText,tongGiaTriKhoHangText;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initLineChart();
        initPieChart();
    }

    private void initLineChart() {
        mixlineChart.setTitle("Doanh thu và tổng nợ đại lý");
        CalculateSQL calculateSQL = CalculateSQL.getInstance();
        LocalDate localDate = LocalDate.now();
        int currentYear = localDate.getYear();

        // Tạo dữ liệu cho tổng doanh số
        XYChart.Series<String, Number> totalSalesSeries = createSalesDataSeries(2024);
        totalSalesSeries.setName("Tổng doanh số");
        mixlineChart.getData().add(totalSalesSeries);

        // Tạo dữ liệu cho tổng nợ của các đại lý
        Map<String, Double> totalDebts = calculateSQL.calculateTotalDebtUntilMonth(2024);
        XYChart.Series<String, Number> totalDebtsSeries = createDebtsDataSeries(totalDebts);
        totalDebtsSeries.setName("Tổng nợ của các đại lý");
        mixlineChart.getData().add(totalDebtsSeries);
    }

    private XYChart.Series<String, Number> createSalesDataSeries(int year) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        if (year == now.getYear()) {
            for (int month = 1; month <= currentMonth; month++) {
                series.getData().add(new XYChart.Data<>(months[month - 1], CalculateSQL.getInstance().calculateTotalValueExportsForMonth(month, year)));
            }
        } else {
            for (int month = 1; month <= 12; month++) {
                series.getData().add(new XYChart.Data<>(months[month - 1], CalculateSQL.getInstance().calculateTotalValueExportsForMonth(month, year)));
            }
        }
        return series;
    }


    private XYChart.Series<String, Number> createDebtsDataSeries(Map<String, Double> totalDebts) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();
        if (totalDebts.containsKey(currentYear + "-1")) {
            for (int month = 1; month <= currentMonth; month++) {
                String monthKey = currentYear + "-" + month;
                series.getData().add(new XYChart.Data<>(months[month - 1], totalDebts.getOrDefault(monthKey, 0.0)));
            }
        } else {
            for (int month = 1; month <= 12; month++) {
                String monthKey = currentYear + "-" + month;
                series.getData().add(new XYChart.Data<>(months[month - 1], totalDebts.getOrDefault(monthKey, 0.0)));
            }
        }
        return series;
    }


    private void initPieChart() {
        CalculateSQL calculateSQL = CalculateSQL.getInstance();
        int soMatHangTonKho = calculateSQL.calSoMatHangTonKho();
        int tongSoMatHang = calculateSQL.calTongSoMatHang();

        double percentageTonKho = 0;
        double percentageHetHang = 0;

        if (tongSoMatHang != 0) {
            percentageTonKho = ((double) soMatHangTonKho / tongSoMatHang) * 100;
            percentageHetHang = 100 - percentageTonKho;
        }

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        // Thêm mục "Tồn kho" vào danh sách dữ liệu của biểu đồ
        if (percentageTonKho > 0) {
            pieChartData.add(new PieChart.Data("Tồn kho", percentageTonKho));
        }

        // Thêm mục "Hết hàng" vào danh sách dữ liệu của biểu đồ
        if (percentageHetHang > 0) {
            pieChartData.add(new PieChart.Data("Hết hàng", percentageHetHang));
        }

        tonkhoPieChart.setData(pieChartData);
        tonkhoPieChart.setTitle("Kho hàng");
        tonkhoPieChart.setLabelsVisible(false);
    }



}
