package uk.ac.soton.comp2211.g16.ad.charts;
// TODO: add code for trimToSize
// TODO: add bigInt for most of the data. This replaces bitwise sorting and is more ineff. but it
// TODO: modify the HasMap to be an Array. Not necessary to have it as HM.
// works with big num

// TODO: @abe Make functions that use getDataForCPC() from DbConnector class
// TODO: Change constructor of class so that when it is called for the first time it gets all
// information needed
// Information needed is double[] cost long[] userID and LocalDateTime[] dateTime. Aka the data
// shown for each of these
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;

import java.math.BigInteger;

public class LineChartClass extends LineChart  {

  // Class Variables

  int size;
  // DEBUG: Error may come out of having this variable
  int minSize = size + 1;
  double[] cost;
  long[] userID;
  // Could use Hashmap & avoid having a search algorithm
  // TODO: Use this object for datetime
  int[] days;
  short[] userData;

  //private static final NumberAxis x = new NumberAxis();
  //private static final NumberAxis y = new NumberAxis();

  // private static final ObservableList<XYChart.Series<x,y>> ;
  // Will need to create a Series to store dateTime and Cost together
  // Constructor

  // TODO: Create Series 
  public LineChartClass(int startSize, double[] cost) {
    super(new NumberAxis(), new NumberAxis());

    if (startSize >= 0) {
      // Maybe? this.size = startSize;
      this.cost = new double[startSize];
      this.userID = new long[startSize];
      this.userData = new short[startSize];
      this.days = new int[startSize];
    } else
      throw new IllegalArgumentException("Illegal starting capacity: value is below 0" + startSize);
  }

  // Checks if the new size of the chart is too large and adjusts it to avoid an overflow.
  private static int sizeTooLarge(int minSize) {
    if (minSize < 0) // This means overflow
      throw new OutOfMemoryError("The minimum size of the chart has escaped bounds");

    // Making two bigintegers to store the values
    BigInteger maxArraySize =
        BigInteger.valueOf(
            Integer.MAX_VALUE); // setting Max Array Size variable to max Integer value possible
    BigInteger capacity = BigInteger.valueOf(minSize); // Setting capacity as value of minsize

    // If the capacity is greater than Integer Max Value return integer max value
    // Otherwise, return the value of the new capacity
    if (capacity.compareTo(maxArraySize) > 0) return Integer.MAX_VALUE;
    else return capacity.intValue();
  }
}
