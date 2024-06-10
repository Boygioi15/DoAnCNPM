package org.doancnpm.ManHinhBaoCao;

import javafx.scene.chart.XYChart;
import org.doancnpm.SQLUltilities.CalculateSQL;

import java.time.LocalDate;
import java.util.Map;

public class BieuDoController {
    protected XYChart.Series<String, Number> createReceiptsSeries(int year) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        if (year == now.getYear()) {
            for (int month = 1; month <= currentMonth; month++) {
                series.getData().add(new XYChart.Data<>(months[month - 1], CalculateSQL.getInstance().calculateTotalValueReceiptsForMonth(month, year)));
            }
        } else {
            for (int month = 1; month <= 12; month++) {
                series.getData().add(new XYChart.Data<>(months[month - 1], CalculateSQL.getInstance().calculateTotalValueReceiptsForMonth(month, year)));
            }
        }
        return series;
    }
    protected XYChart.Series<String, Number> createSalesDataSeries(int year) {
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
}
