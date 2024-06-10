package org.doancnpm.ManHinhBaoCao;

import io.github.palexdev.materialfx.controls.MFXComboBox;

import javafx.collections.FXCollections;
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

import org.doancnpm.Models.BaoCaoCongNo;
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
    private ComboBox<Integer> CbYear;
    @FXML
    private VBox accorditionDoanhSo;
    @FXML
    private VBox accorditionCongNo;
    @FXML
    Region manHinhBaoCao;
    public void setVisibility(boolean visibility) {
        manHinhBaoCao.setVisible(visibility);
    }

    BaoCaoDoanhSoController baoCaoDoanhSoController = new BaoCaoDoanhSoController();
    BaoCaoCongNoController baoCaoCongNoController =new BaoCaoCongNoController();
    BieuDoController bieuDoController = new BieuDoController();

    public int currentYear = LocalDate.now().getYear();

    public void setPrimaryStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initLineChart(currentYear);
        initComboBox();
        initAccordion(currentYear);
    }

    private void initComboBox() {

        for (int year = currentYear; year >= currentYear - 5; year--) {
            CbYear.getItems().add(year);
        }

        CbYear.setValue(currentYear);

        CbYear.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                int selectedYear = newValue;
                initLineChart(selectedYear);
                initAccordion(selectedYear);
            }
        });

    }

    private void initAccordion(int year) {
        accorditionDoanhSo.getChildren().clear();
        accorditionDoanhSo.getChildren().addAll(baoCaoDoanhSoController.createTitledPanesForMonths(year));
        accorditionCongNo.getChildren().clear();
        accorditionCongNo.getChildren().addAll(baoCaoCongNoController.createTitledPanesForMonths(year));
    }


    private void initLineChart(int selectedYear) {
        mixlineChart.setTitle("Doanh thu và tổng nợ đại lý");
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

}
