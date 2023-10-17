package uk.ac.soton.comp2211.g16.ad;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import uk.ac.soton.comp2211.g16.ad.data.DbConnector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataUtil {
    public static Map<String, Object> getdata1(String allFilters) {
        Map<String, Object> map = new HashMap<>();
        HashMap<String, Integer> genderMap = DbConnector.getDataForPieCharts("Gender", "ImpressionLog", allFilters);
        ObservableList<PieChart.Data> genderDataList = FXCollections.observableArrayList();
        genderMap.remove("Gender");
        for (String key : genderMap.keySet()) {
            genderDataList.add(new PieChart.Data(key, genderMap.get(key)));
        }

        map.put("a", genderDataList);
        HashMap<String, Integer> incomeMap = DbConnector.getDataForPieCharts("Income", "ImpressionLog", allFilters);
        ObservableList<PieChart.Data> incomeDataList = FXCollections.observableArrayList();
        incomeMap.remove("Income");
        for (String key : incomeMap.keySet()) {
            incomeDataList.add(new PieChart.Data(key, incomeMap.get(key)));

        }
        map.put("b", incomeDataList);

        HashMap<String, Integer> ageMap = DbConnector.getDataForPieCharts("Age", "ImpressionLog", allFilters);
        ageMap.remove("Age");
        ObservableList<PieChart.Data> ageDataList = FXCollections.observableArrayList();
        for (String key : ageMap.keySet()) {
            ageDataList.add(new PieChart.Data(key, ageMap.get(key)));
        }
        map.put("c", ageDataList);
        HashMap<String, Integer> contextMap = DbConnector.getDataForPieCharts("Context", "ImpressionLog", allFilters);
        ObservableList<PieChart.Data> contextDataList = FXCollections.observableArrayList();
        contextMap.remove("Context");
        for (String key : contextMap.keySet()) {
            contextDataList.add(new PieChart.Data(key, contextMap.get(key)));
        }
        map.put("d", contextDataList);

        return map;
    }

    public static Map<String, Object> getData2(String startDate, String endDate, String impressionFilter, String currentAdvertChartItem, boolean select) {
        String clickFilter = DbConnector.getDateRangeForClickLog(startDate, endDate);
        String serverFilter = DbConnector.getDateRangeForServerLog(startDate, endDate);
        ArrayList<Number> data = null;

        switch (currentAdvertChartItem) {
            case "CTR":
                data = DbConnector.getCTRChart(impressionFilter);
                break;

            case "Bounces":
                if (select) {
                    data = DbConnector.getBouncesChart(clickFilter, getBouncesCriteriaForChart("Pages"));
                } else {
                    data = DbConnector.getBouncesChart(clickFilter, getBouncesCriteriaForChart("Time"));
                }
                //advertMenu.show();
                break;
            case "BounceRate":
                if (select) {
                    data = DbConnector.getBounceRateChart(serverFilter, getBouncesCriteriaForChart("Pages"));
                } else {
                    data = DbConnector.getBounceRateChart(serverFilter, getBouncesCriteriaForChart("Time"));
                }
                //advertMenu.show();
                break;
            case "ConvRate":
                break;
            default:
                break;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("e",data);
        return map;


    }

    private static String getBouncesCriteriaForChart(String pagesOrTime) {
        if (pagesOrTime.equals("Pages")) {
            return "ServerLog.PagesViewed < 2";
        } else if (pagesOrTime.equals("Time")) {
            return "(ExitDate - EntryDate <= 10)";
        } else {
            return "1==1"; //SQL Injection much?
        }
    }
}
