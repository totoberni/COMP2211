package uk.ac.soton.comp2211.g16.ad.data;

import java.sql.*;

import java.util.*;
import java.util.logging.Logger;

import com.alibaba.druid.pool.DruidPooledConnection;
import uk.ac.soton.comp2211.g16.ad.SqliteUtil;

import static java.lang.Integer.valueOf;

public class DbConnector {
    static Connection conn;

    private static Hashtable<String, ResultSet> pCache = new Hashtable<>();


    public DbConnector() {
        this.conn = connect();
    }

    private static final Logger LOGGER = Logger.getLogger(DbConnector.class.getName());

    private static String timeGranularityQuerySnippet = "\'%Y-%m-%d\'";

    // TODO: need someone to make this call work I am not able D:

    public static ArrayList<String> getDates() {
        try {
            Statement stmt = connect().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT DATE(Date) As 'Dates' FROM ClickLog GROUP BY DATE(date)");
            ArrayList<String> dates = new ArrayList();
            while (rs.next()) {
                dates.add(rs.getString("Dates"));
            }
            return dates;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
    }



    public static String getDateRange(String startDate, String endDate) {
        if (startDate.isEmpty() && endDate.isEmpty())
            return "";
        else if (startDate.isEmpty())
            return "<= \"" + endDate + "\")";
        else if (endDate.isEmpty())
            return ">= \"" + startDate + "\")";
        else
            return "BETWEEN \"" + startDate + "\" AND \"" + endDate + "\")";
    }

    private static String getJoinOnImpression(String table) {
        return "INNER JOIN ImpressionLog ON " + table + ".ID = ImpressionLog.ID ";
    }

    //COULD HAVE A CHECK THING THAT SEES IF THIS COMBO OF BOOLS HAS BEEN DONE BEFORE
    public static String getWHEREOnFilter(Boolean male, Boolean female, Boolean lessThan25, Boolean range25To34, Boolean range35To44, Boolean range45To54, Boolean greaterThan54, Boolean lowIncome, Boolean mediumIncome, Boolean highIncome, Boolean blog, Boolean news, Boolean shopping, Boolean socialMedia, Boolean hobbies, Boolean travel) {
        StringBuilder clause = new StringBuilder();
        if (male && female && lessThan25 && range25To34 && range35To44 && range45To54 && greaterThan54 && lowIncome && mediumIncome && highIncome && blog && news && socialMedia && shopping && hobbies && travel) {
            return "";
        }

        if (!(male && female)) {
            if (male) {
                clause.append("WHERE Gender = \"Male\" ");
            } else if (female) {
                clause.append("WHERE Gender = \"Female\" ");
            }
            clause.append("AND ");
        }

        if (clause.isEmpty()) {
            clause.append("WHERE ");
        }

        if (!(lessThan25 && range25To34 && range35To44 && range45To54 && greaterThan54)) {
            StringBuilder ageClause = new StringBuilder();
            ageClause.append("(");
            if (lessThan25) {
                ageClause.append("(Age = \"<25\") OR ");
            }
            if (range25To34) {
                ageClause.append("(Age = \"25-34\") OR ");
            }
            if (range35To44) {
                ageClause.append("(Age = \"35-44\") OR ");
            }
            if (range45To54) {
                ageClause.append("(Age = \"45-54\") OR ");
            }
            if (greaterThan54) {
                ageClause.append("(Age = \">54\") OR ");
            }
            if (ageClause.length() > 4) {
                ageClause.delete(ageClause.length() - 4, ageClause.length()); //Deletes last OR
            }
            ageClause.append(") ");
            clause.append(ageClause);
            clause.append("AND ");
        }

        if (clause.isEmpty()) {
            clause.append("WHERE ");
        }

        if (!(lowIncome && mediumIncome && highIncome)) {
            StringBuilder incomeClause = new StringBuilder();
            incomeClause.append("(");
            if (lowIncome) {
                incomeClause.append("(Income = \"Low\") OR ");
            }
            if (mediumIncome) {
                incomeClause.append("(Income = \"Medium\") OR ");
            }
            if (highIncome) {
                incomeClause.append("(Income = \"High\") OR ");
            }

            incomeClause.delete(incomeClause.length() - 4, incomeClause.length()); //Deletes last OR
            incomeClause.append(") ");
            clause.append(incomeClause);
            clause.append("AND ");
        }

        if (clause.isEmpty()) {
            clause.append("WHERE ");
        }

        if (!(blog && news && socialMedia && shopping && hobbies && travel)) {
            StringBuilder contextClause = new StringBuilder();
            contextClause.append("(");
            if (blog) {
                contextClause.append("(Context = \"Blog\") OR ");
            }
            if (news) {
                contextClause.append("(Context = \"News\") OR ");
            }
            if (socialMedia) {
                contextClause.append("(Context = \"Social Media\") OR ");
            }
            if (shopping) {
                contextClause.append("(Context = \"Shopping\") OR ");
            }
            if (hobbies) {
                contextClause.append("(Context = \"Hobbies\") OR ");
            }
            if (travel) {
                contextClause.append("(Context = \"Travel\") OR ");
            }
            contextClause.delete(contextClause.length() - 4, contextClause.length()); //Deletes last OR
            contextClause.append(") ");
            clause.append(contextClause);
            clause.append("AND ");
        }

        clause.delete(clause.length() - 5, clause.length()); //Deletes last AND (including spaces)
        return clause.toString();
    }

    public static ArrayList<Integer> getCostOverTime() {
        ArrayList<Integer> costsInOrder = new ArrayList<>();
        //ArrayList<DateTime> datesInOrder = new ArrayList<>();
        try (
                Statement stmt = connect().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT SUM(ImpressionCost) AS Cost, Date FROM ImpressionLog ORDER BY Date ASC;" +
                        "or this SELECT SUM(ImpressionCost) AS Cost FROM ImpressionLog ORDER BY Date DESC;\n" +
                        "\n" +
                        " or t his SELECT TotalCost, Date FROM (Select ClickCost As TotalCost, Date FROM ClickLog JOIN SELECT ImpressionCost AS TotalCost, Date FROM ImpressionLog);\n" +
                        "\n" +
                        "or this SELECT DateA, SUM(ClickCost) AS RunningClickCost FROM (SELECT Date AS DateA, COUNT(DISTINCT(ClickCost)) FROM ClickLog GROUP BY Date ORDER BY Date), (SELECT Date AS DateB, COUNT(DISTINCT(ClickCost)) FROM ClickLog GROUP BY Date ORDER BY Date) WHERE DateA >= DateB GROUP BY DateA ORDER BY DateB;\n" +
                        "\n" +
                        "\n" +
                        "maybe this CREATE VIEW costView AS SELECT Date, COUNT(ClickCost) FROM ClickLog GROUP BY Date ORDER BY Date; \n" +
                        "\n" +
                        "SELECT Date, SUM(ClickCost) FROM \n" +
                        "  a.order_date\n" +
                        ", sum(b.order_count) RunningTotal\n" +
                        "from \n" +
                        "  order_view a\n" +
                        ", order_view b\n" +
                        "where a.order_date >= b.order_date\n" +
                        "group by a.order_date\n" +
                        "order by a.order_date;\n" +
                        "\n")) {
            //ResultSet rs = stmt.executeQuery("SELECT SUM(TotalCost) AS Cost FROM (Select ClickCost As TotalCost, Date FROM ClickLog UNION ALL SELECT ImpressionCost AS TotalCost, Date FROM ImpressionLog);")) {
            while (rs.next()) {
                costsInOrder.add(rs.getInt("Cost"));
                //datesInOrder.add(rs.getInt("Cost"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return costsInOrder;
    }

    public static float getBouncesViaTimeSpent(String filter, String startDate, String endDate) {
        try {
            try {
                if (!startDate.toString().isEmpty() && !endDate.toString().isEmpty()) {
                    if (filter.isEmpty())
                        return getNumberOfStatistic("*", "ServerLog", "WHERE (ExitDate - EntryDate) <= 10 AND EntryDate >= \"" + startDate.toString() + "\" AND ExitDate <= \"" + endDate.toString() + "\"", "");
                    else
                        return getNumberOfStatistic("*", "ServerLog", filter + " AND (ExitDate - EntryDate) <= 10 AND EntryDate >= \"" + startDate.toString() + "\" AND ExitDate <= \"" + endDate.toString() + "\"", "");
                }
            } catch (NullPointerException e) {
            }
            try {
                if (!endDate.toString().isEmpty()) {
                    if (filter.isEmpty())
                        return getNumberOfStatistic("*", "ServerLog", "WHERE (ExitDate - EntryDate) <= 10 AND ExitDate <= \"" + endDate.toString() + "\"", "");
                    else
                        return getNumberOfStatistic("*", "ServerLog", filter + " AND (ExitDate - EntryDate) <= 10 AND ExitDate <= \"" + endDate.toString() + "\"", "");
                }
            } catch (NullPointerException e) {
            }
            try {
                if (!startDate.toString().isEmpty()) {
                    if (filter.isEmpty())
                        return getNumberOfStatistic("*", "ServerLog", "WHERE (ExitDate - EntryDate) <= 10 AND EntryDate >= \"" + startDate.toString() + "\"", "");
                    else
                        return getNumberOfStatistic("*", "ServerLog", filter + " AND (ExitDate - EntryDate) <= 10 AND EntryDate >= \"" + startDate.toString() + "\"", "");
                }
            } catch (NullPointerException e) {
            }
            if (filter.isEmpty())
                return getNumberOfStatistic("*", "ServerLog", "WHERE (ExitDate - EntryDate) <= 10", "");
            else
                return getNumberOfStatistic("*", "ServerLog", filter + " AND (ExitDate - EntryDate) <= 10", "");
        } catch (Exception e) {
            return 0; //one is 0
        }
    }

    public static float getBouncesViaPagesVisited(String filter, String startDate, String endDate) {
        try {
            try {
                if (!startDate.toString().isEmpty() && !endDate.toString().isEmpty()) {
                    if (filter.isEmpty())
                        return getNumberOfStatistic("*", "ServerLog", "WHERE PagesViewed <= 1 AND EntryDate >= \"" + startDate.toString() + "\" AND ExitDate <= \"" + endDate.toString() + "\"", "");
                    else
                        return getNumberOfStatistic("*", "ServerLog", filter + " AND PagesViewed <= 1 AND EntryDate >= \"" + startDate.toString() + "\" AND ExitDate <= \"" + endDate.toString() + "\"", "");
                }
            } catch (NullPointerException e) {
            }
            try {
                if (!endDate.toString().isEmpty()) {
                    if (filter.isEmpty())
                        return getNumberOfStatistic("*", "ServerLog", "WHERE PagesViewed <= 1 AND ExitDate <= \"" + endDate.toString() + "\"", "");
                    else
                        return getNumberOfStatistic("*", "ServerLog", filter + " AND PagesViewed <= 1 AND ExitDate <= \"" + endDate.toString() + "\"", "");
                }
            } catch (NullPointerException e) {
            }
            try {
                if (!startDate.toString().isEmpty()) {
                    if (filter.isEmpty())
                        return getNumberOfStatistic("*", "ServerLog", "WHERE PagesViewed <= 1 AND EntryDate >= \"" + startDate.toString() + "\"", "");
                    else
                        return getNumberOfStatistic("*", "ServerLog", filter + " AND PagesViewed <= 1 AND EntryDate >= \"" + startDate.toString() + "\"", "");
                }
            } catch (NullPointerException e) {
            }
            if (filter.isEmpty())
                return getNumberOfStatistic("*", "ServerLog", "WHERE PagesViewed <= 1", "");
            else
                return getNumberOfStatistic("*", "ServerLog", filter + " AND PagesViewed <= 1", "");
        } catch (Exception e2) {
            return 0; //one is 0
        }
    }

    public static float getBounceRateViaTimeSpent(String filter, String startDate, String endDate) {
        try {
            return getBouncesViaTimeSpent(filter, startDate, endDate) / getNumberOfStatistic("*", "ClickLog", filter, getDateRange(startDate, endDate));
        } catch (Exception e) {
            return 0; //one is 0
        }
    }

    public static float getBounceRateViaPagesVisited(String filter, String startDate, String endDate) {
        try {
            return (float) getBouncesViaPagesVisited(filter, startDate, endDate) / (float) getNumberOfStatistic("*", "ClickLog", filter, getDateRange(startDate, endDate));
        } catch (Exception e) {
            return 0; //one is 0
        }
    }

    private static float executeReturnTotalQuery(String sql) {
        System.out.println(sql);
        long startTime = System.currentTimeMillis();
        DruidPooledConnection connection = null;
        try {
            connection = SqliteUtil.getConn();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try  {

            ResultSet rs = stmt.executeQuery(sql);
            long endTime = System.currentTimeMillis();
            System.out.println("Executing time: " + (endTime - startTime) + " ms");
            while (rs.next()) {
                return rs.getFloat("Total");
            }
        } catch (SQLException e) {

            e.printStackTrace();
        }finally {
            try {
                SqliteUtil.closeConnection(connection);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            try {
                SqliteUtil.closeStatement(stmt);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return 0;
    }

    public static float getCTR(String filter, String date) {
        try {
            return ((float) getNumberOfStatistic("*", "ClickLog", filter, date) / (float) getNumberOfStatistic("*", "ImpressionLog", filter, date)) * 100;
        } catch (Exception e) {
            return 0; //one is 0
        }
    }

    public static float getCPC(String filter, String date) {
        try {
            return (((float) getSum("ClickCost", "ClickLog", filter, date) + (float) getSum("ImpressionCost", "ImpressionLog", filter, date)) / 100 / (float) getNumberOfStatistic("ClickCost", "ClickLog", filter, date));
        } catch (Exception e) {
            return 0; //one is 0
        }
    }

    public static float getCPA(String filter, String startDate, String endDate) {
        String date;
        try {
            date = getDateRange(startDate, endDate);
            if (!startDate.toString().isEmpty() && !endDate.toString().isEmpty()) {
                if (filter.isEmpty())
                    return ((float) getTotalCost(filter, "")) / ((float) getNumberOfStatistic("*", "ServerLog WHERE Conversion = \"Yes\" AND EntryDate >= \"" + startDate.toString() + "\" AND ExitDate <= \"" + endDate.toString() + "\"", filter, ""));
                else
                    return ((float) getTotalCost(filter, "")) / ((float) getNumberOfStatistic("*", "ServerLog", filter + " AND Conversion = \"Yes\" AND EntryDate >= \"" + startDate.toString() + "\" AND ExitDate <= \"" + endDate.toString() + "\"", ""));
            }
            try {
                if (!endDate.toString().isEmpty()) {
                    if (filter.isEmpty())
                        return ((float) getTotalCost(filter, date)) / ((float) getNumberOfStatistic("*", "ServerLog WHERE Conversion = \"Yes\" AND ExitDate <= \"" + endDate.toString() + "\"", "", ""));
                    else
                        return ((float) getTotalCost(filter, date)) / ((float) getNumberOfStatistic("*", "ServerLog", filter + " AND Conversion = \"Yes\" AND ExitDate <= \"" + endDate.toString() + "\"", ""));
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            try {
                if (!startDate.toString().isEmpty()) {
                    if (filter.isEmpty())
                        return ((float) getTotalCost(filter, "")) / ((float) getNumberOfStatistic("*", "ServerLog WHERE Conversion = \"Yes\" AND EntryDate >= \"" + startDate.toString() + "\"", "", ""));
                    else
                        return ((float) getTotalCost(filter, "")) / ((float) getNumberOfStatistic("*", "ServerLog", filter + " AND Conversion = \"Yes\" AND EntryDate >= \"" + startDate.toString() + "\"", ""));
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            if (filter.isEmpty())
                return ((float) getTotalCost(filter, "")) / ((float) getNumberOfStatistic("*", "ServerLog WHERE Conversion = \"Yes\"", "", ""));
            else
                return ((float) getTotalCost(filter, "")) / ((float) getNumberOfStatistic("*", "ServerLog", filter + " AND Conversion = \"Yes\"", ""));
        } catch (Exception e) {
            return ((float) getTotalCost(filter, "")) / ((float) getNumberOfStatistic("*", "ServerLog WHERE Conversion = \"Yes\"", filter, ""));
        }
    }

    public static float getCPM(String filter, String date) {
        try {
            return (((float) getSum("ImpressionCost", "ImpressionLog", filter, date) / 100 / (float) (getNumberOfStatistic("*", "ImpressionLog", filter, date)) * 1000));
        } catch (Exception e) {

            return 0; //one is 0
        }
    }


//fake
//  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
//  public static float getCPC(String filter, String date) {
//    try {
//      return (((float) getSum("ClickCost", "ClickLog", filter, date) + (float) getSum("ImpressionCost", "ImpressionLog", filter, date)) / 100 / (float) getNumberOfStatistic("ClickCost", "ClickLog", filter, date));
//    }
//    catch (Exception e) {
//      return 0; //one is 0
//    }

//  }

// Using DateTimeFormatter to handle data


    public static HashMap<String,String> getCampaignDetail() {


        try {
            Statement stmt = connect().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MIN(Date) AS min_date, MAX(Date) AS max_date FROM ClickLog;");


            HashMap<String,String> dates =new HashMap<>();
            //System.out.println(rs.getString("max_date"));
            if (rs.next()) {
                dates.put("StartDate", rs.getString("min_date"));
                System.out.println(rs.getString("min_date"));
                dates.put("EndDate", rs.getString("max_date"));
                System.out.println(rs.getString("max_date"));
            }
            return dates;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
    }
//SELECT MIN(click_date) AS min_date, MAX(click_date) AS max_date
//FROM Click;


    public static float getNumberOfStatisticFromServerLog(String statistic, String filter, String startDate, String endDate) {
        //If empty throw an error

        try {
            if (!startDate.toString().isEmpty() && !endDate.toString().isEmpty()) {
                if (!filter.isEmpty())
                    return executeReturnTotalQuery("SELECT COUNT(" + statistic + ") AS Total FROM ServerLog " + getJoinOnImpression("ServerLog") + filter + " AND EntryDate >= \"" + startDate.toString() + "\" AND ExitDate <= \"" + endDate.toString() + "\";");
                else
                    return executeReturnTotalQuery("SELECT COUNT(" + statistic + ") AS Total FROM ServerLog WHERE EntryDate >= \"" + startDate.toString() + "\" AND ExitDate <= \"" + endDate.toString() + "\";");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (!startDate.toString().isEmpty()) {
                if (!filter.isEmpty())
                    return executeReturnTotalQuery("SELECT COUNT(" + statistic + ") AS Total FROM ServerLog " + getJoinOnImpression("ServerLog") + filter + " AND EntryDate >= \"" + startDate.toString() + "\";");
                else
                    return executeReturnTotalQuery("SELECT COUNT(" + statistic + ") AS Total FROM ServerLog WHERE EntryDate >= \"" + startDate.toString() + "\";");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (!endDate.toString().isEmpty()) {
                if (!filter.isEmpty())
                    return executeReturnTotalQuery("SELECT COUNT(" + statistic + ") AS Total FROM ServerLog " + getJoinOnImpression("ServerLog") + filter + " AND ExitDate <= \"" + endDate.toString() + "\";");
                else
                    return executeReturnTotalQuery("SELECT COUNT(" + statistic + ") AS Total FROM ServerLog WHERE ExitDate <= \"" + endDate.toString() + "\";");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!filter.isEmpty())
            return executeReturnTotalQuery("SELECT COUNT(" + statistic + ") AS Total FROM ServerLog " + getJoinOnImpression("ServerLog") + filter + ";");
        else
            return executeReturnTotalQuery("SELECT COUNT(" + statistic + ") AS Total FROM ServerLog;");
    }


    public static float getNumberOfStatistic(String statistic, String table, String filter, String date) {
        if (date.isEmpty()) {
            if (!filter.isEmpty() && !table.equals("ImpressionLog"))
                return executeReturnTotalQuery("SELECT COUNT(" + statistic + ") AS Total FROM " + table + " " + getJoinOnImpression(table) + filter + ";");
            else if (filter.isEmpty())
                return executeReturnTotalQuery("SELECT COUNT(" + statistic + ") AS Total FROM " + table + ";");
            else
                return executeReturnTotalQuery("SELECT COUNT(" + statistic + ") AS Total FROM " + table + " " + filter + ";");
        } else {
            if (!filter.isEmpty() && !table.equals("ImpressionLog"))
                return executeReturnTotalQuery("SELECT COUNT(" + statistic + ") AS Total FROM " + table + " " + getJoinOnImpression(table) + filter + " AND ("+ table + ".Date " + date + ";");
            else if (filter.isEmpty())
                return executeReturnTotalQuery("SELECT COUNT(" + statistic + ") AS Total FROM " + table + " WHERE (Date " + date + ";");
            else
                return executeReturnTotalQuery("SELECT COUNT(" + statistic + ") AS Total FROM " + table + " " + filter + " AND (Date " + date + ";");
        }
    }

    public static float getSum(String statistic, String table, String filter, String date) {
        if (date.isEmpty()) {
            if (!filter.isEmpty() && !table.equals("ImpressionLog"))
                return executeReturnTotalQuery("SELECT SUM(" + statistic + ") AS Total FROM " + table + " " + getJoinOnImpression(table) + filter + ";");
            else if (filter.isEmpty())
                return executeReturnTotalQuery("SELECT SUM(" + statistic + ") AS Total FROM " + table + ";");
            else
                return executeReturnTotalQuery("SELECT SUM(" + statistic + ") AS Total FROM " + table + " " + filter + ";");
        } else {
            if (!filter.isEmpty() && !table.equals("ImpressionLog"))
                return executeReturnTotalQuery("SELECT SUM(" + statistic + ") AS Total FROM " + table + " " + getJoinOnImpression(table) + filter + " AND ("+ table + ".Date " + date + ";");
            else if (filter.isEmpty())
                return executeReturnTotalQuery("SELECT SUM(" + statistic + ") AS Total FROM " + table + " WHERE (Date " + date + ";");
            else
                return executeReturnTotalQuery("SELECT SUM(" + statistic + ") AS Total FROM " + table + " " + filter + " AND (Date " + date + ";");
        }
    }

    public static float getTotalCost(String filter, String date) {
        if (date.isEmpty()) {
            if (filter.isEmpty())
                return (executeReturnTotalQuery("SELECT SUM(ImpressionCost) AS Total FROM ImpressionLog;") + executeReturnTotalQuery("SELECT SUM(ClickCost) AS Total FROM ClickLog;")) / 100;
            else
                return (executeReturnTotalQuery("SELECT SUM(ImpressionCost) AS Total FROM ImpressionLog " + filter + ";") + executeReturnTotalQuery("SELECT SUM(ClickCost) AS Total FROM ClickLog " + getJoinOnImpression("ClickLog") + filter + ";")) / 100;
        } else {
            if (filter.isEmpty())
                return (executeReturnTotalQuery("SELECT SUM(ImpressionCost) AS Total FROM ImpressionLog WHERE (Date " + date + ";") + executeReturnTotalQuery("SELECT SUM(ClickCost) AS Total FROM ClickLog WHERE (Date " + date + ";")) / 100;
            else
                return (executeReturnTotalQuery("SELECT SUM(ImpressionCost) AS Total FROM ImpressionLog " + filter + " AND (Date " + date + ";") + executeReturnTotalQuery("SELECT SUM(ClickCost) AS Total FROM ClickLog " + getJoinOnImpression("ClickLog") + filter + " AND (ClickLog.Date " + date + ";")) / 100;
        }
    }

//    public static Connection connect() {
////        Connection connection = null;
////        SQLiteDataSource dataSource = new SQLiteDataSource();
////        //dataSource.getConnection().
////        dataSource.setUrl("jdbc:sqlite:dashboard.db");
////        //dataSource.
////        //dataSource.setUrl("jdbc:sqlite::memory:");
////        //jdbc:sqlite::memory:
////        try {
////            connection = dataSource.getConnection();
////            connection.createStatement().execute("PRAGMA synchronous = OFF");
////            connection.createStatement().execute("PRAGMA cache_size = 10240");
////            LOGGER.fine("Connected to DB");
////
////
////        } catch (SQLException e) {
////            e.printStackTrace(System.err);
////        }
//        Connection connection = null;
//        try {
//            connection = SqliteUtil.getConnection("dashboard");
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//        return connection;
//

//    }
    public static Connection connect() {
        try {
            return SqliteUtil.getConn();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static void enactStatement(String query) {
        Connection connection=null;
        try {
            connection = SqliteUtil.getConn();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Statement statement = null;
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try  {
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                SqliteUtil.closeConnection(connection);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            try {
                SqliteUtil.closeStatement(statement);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void enactStatements(List<String> queries) {

        try (//Connection conn = connect();
             Statement statement = conn.createStatement()) {
            int i = 0;
            conn.setAutoCommit(false);

            for (String query : queries
            ) {

                statement.addBatch(query);
                if ((i++ % 10000000) == 0) {
                    // Do an execute now and again, don't send too many at once
                    statement.executeBatch();
                    conn.commit();
                    statement.clearBatch();
                }
            }
            statement.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static String getDateRangeForServerLog(String startDate, String endDate) {
        if (startDate.isEmpty() && endDate.isEmpty())
            return "";
        else if (startDate.isEmpty())
            return "WHERE ExitDate <= \"" + endDate + "\")";
        else if (endDate.isEmpty())
            return "WHERE EntryDate >= \"" + startDate + "\")";
        else
            return "WHERE EntryDate >= \'" + startDate + "\' AND ExitDate <= \'" + endDate + "\'";
    }

    public static String getDateRangeForClickLog(String startDate, String endDate) {
        if (startDate.isEmpty() && endDate.isEmpty())
            return "";
        else if (startDate.isEmpty())
            return "WHERE ClickLog.Date <= \"" + endDate + "\")";
        else if (endDate.isEmpty())
            return "WHERE ClickLog.Date >= \"" + startDate + "\")";
        else
            return "WHERE ClickLog.Date BETWEEN \'" + startDate + "\' AND \'" + endDate + "\'";
    }

    public static HashMap<Integer, Float> getDataForServerYesNoConversion(String fullFilterString, String YesNo) {
        String sql = "SELECT * FROM (SELECT PagesViewed,\n" +
                "                      Conversion,\n" +
                "                      COUNT(*) * 100.0 / SUM(COUNT(*)) OVER (PARTITION BY PagesViewed) AS Percentage\n" +
                "               FROM ServerLog\n" + getJoinOnImpression("ServerLog") +
                fullFilterString +
                "               GROUP BY PagesViewed, Conversion) \n" +
                "               WHERE Conversion == \'" + YesNo + "\'";






        //ArrayList<Integer, Float> convPerView = new ArrayList<>();
        HashMap<Integer, Float> convPerView = new HashMap<>();


        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                convPerView.put(rs.getInt("PagesViewed"), rs.getFloat("Percentage"));

            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return convPerView;



    }

    public static ArrayList<Integer> getDataForUserGroupChart(String filter) {
        String sql = "SELECT COUNT(DISTINCT ClickLog.ID) AS Uniques, strftime (" + timeGranularityQuerySnippet + ", ClickLog.Date) as ymd\n" +
            "FROM ClickLog\n" + getJoinOnImpression("ClickLog") +
            filter + "\n" +
            "GROUP BY ymd\n" +
            "LIMIT -1 OFFSET 1";

        ArrayList<Integer> uniquesOverTime = new ArrayList<>();


        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                uniquesOverTime.add(rs.getInt("Uniques"));

            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return uniquesOverTime;
    }

    public static ArrayList<Number> getCTRChart(String impressionFilter) {

        ArrayList<Number> data = new ArrayList<>();

        String sql = "SELECT CAST(clickUniques AS REAL)/impressionUniques as CTR, ymd1\n" +
            "FROM (\n" +
            "SELECT COUNT(DISTINCT ID) as impressionUniques, strftime (" + timeGranularityQuerySnippet + ", Date) as ymd1\n" +
            "FROM ImpressionLog\n" +
            impressionFilter +
            "GROUP BY ymd1\n" +
            "LIMIT -1 OFFSET 1\n" +
            ")\n" +
            "\n" +
            "LEFT JOIN (\n" +
            "SELECT COUNT(DISTINCT ID) as clickUniques, strftime (" + timeGranularityQuerySnippet + ", Date) as ymd2\n" +
            "FROM ClickLog\n" +
            "GROUP BY ymd2\n" +
            "LIMIT -1 OFFSET 1\n" +
            ")\n" +
            "\n" +
            "ON ymd1==ymd2";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                data.add((rs.getFloat("CTR")));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return data;
    }

    public static ArrayList<Number> getBouncesChart(String clickFilter, String criteria) {
        ArrayList<Number> data = new ArrayList<>();
        String sql = "SELECT COUNT(*) as Bounces, strftime (" + timeGranularityQuerySnippet + ", ClickLog.Date) as ymd\n" +
            "FROM ServerLog\n" +
            "JOIN ClickLog\n" +
            "ON ServerLog.ID == ClickLog.ID\n" + getJoinOnImpression("ClickLog") +
            clickFilter +
            " AND Conversion == 'No' AND " + criteria + "\n" +
            "GROUP BY ymd";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                data.add((rs.getFloat("Bounces")));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return data;
    }

    public static ArrayList<Number> getBounceRateChart(String serverFilter, String criteria) {
        ArrayList<Number> data = new ArrayList<>();
        String sql = "SELECT AVG(CAST(Bounces AS REAL)/UniqueClicks) as BounceRate, ymd\n" +
            "FROM (SELECT COUNT(*) as Bounces, strftime ("+ timeGranularityQuerySnippet + ", ClickLog.Date) as ymd\n" +
            "FROM ServerLog\n" +
            "JOIN ClickLog\n" +
            "ON ServerLog.ID == ClickLog.ID\n" +getJoinOnImpression("ServerLog") +
            serverFilter +
                " AND Conversion == 'No' AND " + criteria + "\n" +
            "GROUP BY ymd)\n" +
            "\n" +
            "\n" +
            "JOIN (SELECT COUNT(*) as UniqueClicks, strftime(" + timeGranularityQuerySnippet + ", ClickLog.Date) as ymd2\n" +
            "      FROM ClickLog\n" +
            "      GROUP BY ymd2\n" +
            "      LIMIT -1 OFFSET 1)\n" +
            "GROUP BY ymd";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                data.add((rs.getFloat("BounceRate")));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return data;
    }

    public static ArrayList<Number> getDateForCPCChart(String filter) {
        ArrayList<Number> data = new ArrayList<>();
        String sql = "SELECT SUM(ClickCost)/COUNT(DISTINCT ClickLog.ID) AS \"CPC\", strftime(" + timeGranularityQuerySnippet + ", ClickLog.Date) as ymd\n" +
            "FROM ClickLog\n" +
            getJoinOnImpression("ClickLog") +
            filter + "\n" +
            "GROUP BY ymd\n" +
            "LIMIT -1 OFFSET 1\n";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                data.add((rs.getFloat("CPC")));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return data;
    }


    public void executeBatch(List<PreparedStatement> statements) {
        try (Connection conn = connect()) {
            conn.setAutoCommit(false);

            for (PreparedStatement pstmt : statements) {
                pstmt.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static ArrayList<Double> getAverageCPC() {
        ArrayList<Double> averageCPCList = new ArrayList<>();

        try (
                Statement stmt = connect().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT SUM(ClickCost)/100 as ClickCostPerDay, strftime ('%Y-%m-%d', ClickLog.Date) ymd\n" +
                        "from ClickLog\n" +
                        "GROUP BY ymd\n" +
                        "LIMIT -1 OFFSET 1")) {
            while (rs.next()) {
                double averageCPC = (double) rs.getDouble("ClickCostPerDay");
                averageCPCList.add(averageCPC);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return averageCPCList;
    }

    public static String generateFilterString(String filter, String date) {
        if (date.isEmpty()) {
            if (filter.isEmpty())
                return "";
            else
                return filter;
        } else {
            if (filter.isEmpty())
                return "WHERE (ImpressionLog.Date " + date;// AND (Date " + date;
            else
                return "" + filter + " AND (ImpressionLog.Date " + date;//  + " AND (Date " + date ;
        }
    }

    public static ArrayList<Number> getDataForCPTIChart(String s, String impressionLog, String filter) {
        ArrayList<Number> cptiOverTime = new ArrayList<>();
        String sql = "SELECT SUM(ImpressionCost)/1000 AS Cost, strftime (" + timeGranularityQuerySnippet + ", ImpressionLog.Date) as ymd\n" +
            "FROM ImpressionLog\n" +
                filter + "\n" +
                "GROUP BY ymd\n";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                cptiOverTime.add((int) ((rs.getFloat("Cost")) * 100));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return cptiOverTime;
    }

    public static ArrayList<Number> getDataForCPAChart(String allFilters) {
        ArrayList<Number> cpaPerDay = new ArrayList<>();
        String sql = "SELECT SUM(ClickLog.ClickCost) AS Cost, strftime (" + timeGranularityQuerySnippet + ", ClickLog.Date) ymd\n" +
            "FROM ClickLog\n" +
            "JOIN ServerLog\n" +
            "ON ClickLog.ID == ServerLog.ID\n" + getJoinOnImpression("ClickLog") +
            allFilters + "AND ServerLog.Conversion == \"Yes\"\n" +
            "GROUP BY ymd";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                cpaPerDay.add((rs.getInt("Cost")));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return cpaPerDay;
    }
    public static ArrayList<Integer> getDataForClicksPerDayChart(String data, String table, String allFilters) {
        //System.out.println("Impressions per day is working");

        ArrayList<Integer> clicksPerDay = new ArrayList<>();
        String sql = "";

        if (allFilters.isEmpty()) {
            sql = "SELECT COUNT(*) as TotalClicks, strftime (" + timeGranularityQuerySnippet + ", Date) ymd\n" +
                    "FROM ClickLog\n" +
                    "GROUP BY ymd\n" +
                    "LIMIT -1 OFFSET 1";
        } else {
            sql = "SELECT COUNT(*) as TotalClicks, strftime (" + timeGranularityQuerySnippet + ", ClickLog.Date) ymd\n" +
                    "FROM ClickLog\n" +
                    getJoinOnImpression("ClickLog") +
                    allFilters +
                    "GROUP BY ymd\n" +
                    "LIMIT -1 OFFSET 1";
        }


        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                clicksPerDay.add((rs.getInt("TotalClicks")));
                //System.out.println(rs.getString(whatData) + " : " + rs.getInt("counted"));
            }
            //rs.beforeFirst(); //Resets the resultset

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return clicksPerDay;
    }

    public static ArrayList<Number> getDataForCostPerDayChart(String data, String table, String allFilters) {
        //System.out.println("Impressions per day is working");

        ArrayList<Number> costPerDay = new ArrayList<>();
        String sql = "SELECT SUM(ImpressionCost) as TotalCost, strftime (" + timeGranularityQuerySnippet + ", Date) ymd\n" +
                "FROM ImpressionLog\n" +
                allFilters +
                "GROUP BY ymd\n" +
                "LIMIT -1 OFFSET 1";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                costPerDay.add((int) rs.getFloat("TotalCost"));
                //System.out.println(rs.getString(whatData) + " : " + rs.getInt("counted"));
            }
            //rs.beforeFirst(); //Resets the resultset

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return costPerDay;
    }

    public static ArrayList<Integer> getDataForImpressionPerDayChart(String data, String table, String allFilters) {
        //System.out.println("Impressions per day is working");

        ArrayList<Integer> impPerDay = new ArrayList<>();
        String sql = "SELECT COUNT(*) as TotalImp, strftime (" + timeGranularityQuerySnippet + ", Date) ymd\n" +
                "FROM ImpressionLog\n" +
                allFilters +
                " GROUP BY ymd;";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                impPerDay.add(rs.getInt("TotalImp"));
                //System.out.println(rs.getString(whatData) + " : " + rs.getInt("counted"));
            }
            //rs.beforeFirst(); //Resets the resultset

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return impPerDay;
    }
    public static HashMap<String, Integer> getDataForPieCharts(String whatData, String whatTable, String allFilters) {
        HashMap<String, Integer> pcMap = new HashMap<>();
    /*
    ResultSet rsC = checkCacheForResultSet("PieCharts" + whatData + whatTable, allFilters);

    try {
      if (rsC != null) {
        while (rsC.next()) {
          pcMap.put(rsC.getString(whatData), rsC.getInt("counted"));
          System.out.println(rsC.getString(whatData) + " : " + rsC.getInt("counted"));
          return pcMap;
        }
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    */


        String sql = String.format("SELECT %s, COUNT(%s) as counted\n" +
                "FROM %s\n" +
                allFilters +
                " GROUP BY %s", whatData, whatData, whatTable, whatData);
        //System.out.println(sql);

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                pcMap.put(rs.getString(whatData), rs.getInt("counted"));
                //System.out.println(rs.getString(whatData) + " : " + rs.getInt("counted"));
            }
            //rs.beforeFirst(); //Resets the resultset

        } catch (SQLException e) {
            e.printStackTrace();
            //System.out.println(e.getMessage());
        }
        return pcMap;
    }

    public static String insertImpressionFormat(String date, String iD, String gender, String age, String income, String context, String impressionCost) {
        //add a counter that identifies the campaign generated by first imported second imported etc can do this in csv then upload to db with a campaign id
        return (String.format("INSERT INTO ImpressionLog VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s')", date, iD, gender, age, income, context, impressionCost));
    }

    public static String insertImpressionFormat2(String date, String iD, String gender, String age, String income, String context, String impressionCost) {
        //add a counter that identifies the campaign generated by first imported second imported etc can do this in csv then upload to db with a campaign id
        return (String.format(",('%s', '%s', '%s', '%s', '%s', '%s', '%s')", date, iD, gender, age, income, context, impressionCost));
    }

    public static String insertClickFormat(String date, String iD, String clickCost) {
        return (String.format("INSERT INTO ClickLog VALUES('%s', '%s', '%s')", date, iD, clickCost));
    }

    public static String insertClickFormat2(String date, String iD, String clickCost) {
        return (String.format(",('%s', '%s', '%s')", date, iD, clickCost));
    }

    public static String insertServerFormat(String entryDate, String iD, String exitDate, String pagesViewed, String conversion) {
        return (String.format("INSERT INTO ServerLog VALUES('%s', '%s', '%s', '%s', '%s')", entryDate, iD, exitDate, pagesViewed, conversion));
    }

    private static void purgeTables() {
        enactStatement("DROP TABLE IF EXISTS ImpressionLog;");
        enactStatement("DROP TABLE IF EXISTS ClickLog;");
        enactStatement("DROP TABLE IF EXISTS ServerLog;");
    }

    static String sada = """
            CREATE INDEX "sw"
            ON "ImpressionLog" (
              "ID" ASC
            );""";
    static String dew = """
            CREATE INDEX "xx"
            ON "ServerLog" (
              "ID" ASC
            );""";

    static String grtgrt = """
             CREATE INDEX "sss"
            ON "ImpressionLog" (
              "ImpressionCost" ASC
            );""";

    public static void createTables() {
        //Probably should separate out these and then the clears so that if one table is gone then it remakes or I can just make it drop table instead
        String createImpressionLog = """
                CREATE TABLE ImpressionLog(
                    Date DateTime NOT NULL,
                    ID int NOT NULL,
                    Gender VarChar(6) NOT NULL,
                    Age VarChar(10) NOT NULL,
                    Income VarChar(6) NOT NULL,
                    Context VarChar(10) NOT NULL,
                    ImpressionCost real NOT NULL);""";
        String createClickLog = """
                CREATE TABLE ClickLog( 
                    Date DateTime NOT NULL,
                    ID int NOT NULL,
                    ClickCost real NOT NULL);""";
        String createServerLog = """
                CREATE TABLE ServerLog(
                    EntryDate DateTime NOT NULL,
                    ID int NOT NULL,
                    ExitDate DateTime NOT NULL,
                    PagesViewed int NOT NULL,
                    Conversion VarChar(3) NOT NULL);""";
//        String sada = """
//                CREATE INDEX "sw"
//                ON "ImpressionLog" (
//                  "ID" ASC
//                );""";
//        String dew = """
//                CREATE INDEX "xx"
//                ON "ServerLog" (
//                  "ID" ASC
//                );""";
//        String grtgrt = """
//                CREATE TABLE ServerLog(
//                    EntryDate DateTime NOT NULL,
//                    ID int NOT NULL,
//                    ExitDate DateTime NOT NULL,
//                    PagesViewed int NOT NULL,
//                    Conversion VarChar(3) NOT NULL);""";
        purgeTables(); //Purges them if they exist
        DruidPooledConnection connection = null;
        try {
            connection = SqliteUtil.getConn();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Statement statement = null;
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try  {
            statement.execute(createImpressionLog);
            statement.execute(createClickLog);
            statement.execute(createServerLog);
//            statement.execute(sada);
//            statement.execute(dew);
//            statement.execute(grtgrt);
//      statement.execute(sada);
//      statement.execute(dew);
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }finally {
            try {
                SqliteUtil.closeConnection(connection);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            try {
                SqliteUtil.closeStatement(statement);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void createTables11() {
        //Probably should separate out these and then the clears so that if one table is gone then it remakes or I can just make it drop table instead
        DruidPooledConnection connection = null;
        try {
            connection = SqliteUtil.getConn();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Statement statement = null;
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // purgeTables(); //Purges them if they exist
        try {
            statement.execute(sada);
            statement.execute(dew);
            statement.execute(grtgrt);
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }finally {
            try {
                SqliteUtil.closeConnection(connection);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            try {
                SqliteUtil.closeStatement(statement);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void setTimeGranularityQuerySnippet(String querySnippet) {
        timeGranularityQuerySnippet = querySnippet;
    }
}
