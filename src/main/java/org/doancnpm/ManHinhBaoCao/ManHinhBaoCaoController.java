package org.doancnpm.ManHinhBaoCao;

import io.github.palexdev.materialfx.controls.MFXComboBox;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.Stage;

import org.doancnpm.SQLUltilities.CalculateSQL;

import java.net.URL;
import java.time.LocalDate;
import java.util.Map;
import java.util.ResourceBundle;

public class ManHinhBaoCaoController implements Initializable {
    private Stage stage;
    @FXML
    private LineChart<String, Number> mixlineChart;
    @FXML
    private MFXComboBox<Integer> CbYear;
    @FXML
    private ScrollPane ScrollDoanhSo;
    @FXML
    private Accordion accorditionDoanhSo;
    BaoCaoDoanhSoController baoCaoDoanhSoController = new BaoCaoDoanhSoController();
    BieuDoController bieuDoController = new BieuDoController();

    public int currentYear = LocalDate.now().getYear();

    public void setPrimaryStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initLineChart(currentYear);
        initComboBox();
        initAccordionDoanhSo(currentYear);
    }

    private void initComboBox() {

        for (int year = currentYear; year >= currentYear - 5; year--) {
            CbYear.getItems().add(year);
        }

        CbYear.setText(String.valueOf(currentYear));

        CbYear.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                int selectedYear = newValue;
                initLineChart(selectedYear);
                initAccordionDoanhSo(selectedYear);
            }
        });

    }

    private void initAccordionDoanhSo(int year) {
        accorditionDoanhSo.getPanes().clear();
        accorditionDoanhSo.getPanes().addAll(baoCaoDoanhSoController.createTitledPanesForMonths(year));
        ScrollDoanhSo.setContent(accorditionDoanhSo);
    }


    private void initLineChart(int selectedYear) {
        mixlineChart.setTitle("Doanh thu và tổng nợ đại lý");
        CalculateSQL calculateSQL = CalculateSQL.getInstance();

        mixlineChart.getData().clear(); // Xóa dữ liệu cũ trước khi vẽ lại
        // Tạo dữ liệu cho tổng doanh số
        XYChart.Series<String, Number> totalSalesSeries = bieuDoController.createSalesDataSeries(selectedYear);
        totalSalesSeries.setName("Tổng doanh số");
        mixlineChart.getData().add(totalSalesSeries);

        // Tạo dữ liệu cho tổng nợ của các đại lý
        Map<String, Double> totalDebts = calculateSQL.calculateTotalDebtUntilMonth(selectedYear);
        XYChart.Series<String, Number> totalDebtsSeries = bieuDoController.createDebtsDataSeries(totalDebts);
        totalDebtsSeries.setName("Tổng nợ của các đại lý");
        mixlineChart.getData().add(totalDebtsSeries);
    }
}
