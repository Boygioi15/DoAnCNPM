package org.doancnpm.ManHinhBaoCao;

import javafx.scene.chart.XYChart;
import org.doancnpm.SQLUltilities.CalculateSQL;

import java.time.LocalDate;
import java.util.Map;

public class BieuDoController {
    protected XYChart.Series<String, Number> createDebtsDataSeries(Map<String, Double> totalDebts) {
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
