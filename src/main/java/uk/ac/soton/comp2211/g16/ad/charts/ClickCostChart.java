package uk.ac.soton.comp2211.g16.ad.charts;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;


public class ClickCostChart extends LineChart{

  private String startDate;
  private String endDate;

  public ClickCostChart(String startDate, String endDate) {
    super(new NumberAxis(), new NumberAxis());
    this.startDate = startDate;
    this.endDate = endDate;
  }

  // Populates Chart with average Cost for every Day
  // TODO: changes name depending on what series is being displayed
  // TODO: change method so x axis displays day/month format.
//  private  setChartData(String startDate, String endDate, ArrayList<Double> cost) {
//    // Define the date format to be used
//    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//
//    // Parse the input strings to LocalDate objects
//    LocalDate start = LocalDate.parse(startDate, formatter);
//    LocalDate end = LocalDate.parse(endDate, formatter);
//
//    // Check if StartDate is Before endDate
//    if (start.isAfter(end) || end.isBefore(start)) {
//      throw new IllegalArgumentException("The chosen dates are conflicting");
//    }
//
//    int i = 0;
//    int timePeriod = (int) ChronoUnit.DAYS.between(start, end) + 1;
//
//    // Rearranging x axis bounds
//    // rearraging y axis bounds
//    //y.setLowerBound(0);
//    //y.setUpperBound(cost.max);
//    //Populates data observableArrayList with while loop
//    while (i < timePeriod) {
//      LocalDate date = start.plusDays(i);
//      //add(new XYChart.Data<Integer, Double>((int)date.toEpochDay(), cost.get(i)));
//      i++;
//    }
//    // Adds data to the series
//    //this.getData().add(new XYChart.Series<Integer, Double>("Click Cost for " + XYChart.Series.class.getName(), data));
//  }

  public String getStartDate() {
  return startDate;
}
  public String getEndDate() {
    return endDate;
  }

}
