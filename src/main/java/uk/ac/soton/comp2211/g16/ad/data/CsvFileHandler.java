package uk.ac.soton.comp2211.g16.ad.data;


import com.alibaba.druid.pool.DruidDataSource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import uk.ac.soton.comp2211.g16.ad.SqliteUtil;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;


public class CsvFileHandler {

    private static final Logger LOGGER = Logger.getLogger(CsvFileHandler.class.getName());
    private static final String CLICK_LOG = "click_log.csv";
    private static final String IMPRESSION_LOG = "impression_log.csv";
    private static final String SERVER_LOG = "server_log.csv";

    public String clickLogPath;
    public String impressionLogPath;
    public String serverLogPath;


    DbConnector dbc = new DbConnector();
    public File path;
    long startTime;
    private long clickHandleTime = 0;
    private long impressionHandleTime = 0;
    private long serverHandleTime = 0;

    private double clickLines = 1;
    private double impressionLines = 1;
    private double serverLines = 1;
    double speed;

    public static void main(String[] args) {
        //for debug use only
        //CsvFileHandler debug = new CsvFileHandler("2_month_BIGFILE");
        /*CsvFileHandler debug = new CsvFileHandler("2_week_campaign_2");

        try {
            debug.processCsvFiles();
        } catch (Exception e) {
            e.printStackTrace();
        }

         */

    }

    /**
     * path: The folder for storing campaign data, should contain "click_log.csv", "impression_log.csv" and "server_log.csv"
     */
    public CsvFileHandler(String path) {
        this.path = new File(path);
    }

    public CsvFileHandler() {
    }

    public void setPath(String path){

    }
    public static void changeSource(String databaseFileName){
        SqliteUtil.changeSource(databaseFileName);

    }
    /**
     * Read the three files, and call database class to import data.
     */
    public void processCsvFiles() throws SQLException, InterruptedException {
        startTime = System.currentTimeMillis();
        dbc.createTables();

        long clickStartTime = System.currentTimeMillis();

        Connection connectidon = DbConnector.conn;
        PreparedStatement statementdsee = connectidon.prepareStatement("INSERT INTO ClickLog VALUES(?, ?, ?)");
        connectidon.setAutoCommit(false);
        LineIterator dewdew = null;
        try {
            dewdew = FileUtils.lineIterator(new File(clickLogPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
// Initialise variables

            dewdew.next();
            while (dewdew.hasNext()) {
                String[] values = dewdew.next().split(",");
//                String[] values = line.split(",");
                for (int i = 0; i < values.length; i++) {
                    statementdsee.setString(i + 1, values[i]);
                }
                statementdsee.executeUpdate();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        connectidon.commit();
            long clickEndTime = System.currentTimeMillis();
            clickHandleTime = clickEndTime - clickStartTime;
            System.out.println("Click DONE in: " + clickHandleTime);



        long impressionStartTime = System.currentTimeMillis();
        Connection connection = DbConnector.conn;
        PreparedStatement statement = connection.prepareStatement("INSERT INTO ImpressionLog VALUES(?, ?, ?, ?, ?, ?, ?)");
        connection.setAutoCommit(false);
        LineIterator iterator = null;
        try {
            iterator = FileUtils.lineIterator(new File(impressionLogPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
// Initialise variables

            iterator.next();
            while (iterator.hasNext()) {
                String[] values = iterator.next().split(",");
//                String[] values = line.split(",");
                for (int i = 0; i < values.length; i++) {
                    statement.setString(i + 1, values[i]);
                }
                statement.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        connection.commit();



        long impressionEndTime = System.currentTimeMillis();
        impressionHandleTime = impressionEndTime - impressionStartTime;
        System.out.println("Impression DONE in: " + impressionHandleTime);

long a=System.currentTimeMillis();
        Connection defe = null;
        try {
            defe = SqliteUtil.getConn();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        PreparedStatement ferfre = defe.prepareStatement("INSERT INTO ServerLog VALUES(?,?, ?, ?, ?)");
        defe.setAutoCommit(false);
        LineIterator iteratodewdwr = null;
        try {
            iteratodewdwr = FileUtils.lineIterator(new File(serverLogPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
// Initialise variables

//            boolean flag=false;
            iteratodewdwr.next();
            while (iteratodewdwr.hasNext()) {
                String[] values = iteratodewdwr.next().split(",");
//                String[] values = line.split(",");
//                if(flag){
                    for (int i = 0; i < values.length; i++) {
                        ferfre.setString(i + 1, values[i]);
                    }
                        ferfre.executeUpdate();
//                }else{
//                    flag=true;
//                }


            }
        }catch (Exception e){
            e.printStackTrace();
        }
        defe.commit();


//        f = new File(serverLogPath);
//        entries = new ArrayList<>();
//        try {
//            BufferedReader br = new BufferedReader(new FileReader(f));
//            String line;
//            while ((line = br.readLine()) != null) {
//                String[] values = line.split(",");
//                StringBuilder builder=new StringBuilder("INSERT INTO ServerLog VALUES('");
//                builder.append(values[0]).append("',");
//                builder.append("'").append(values[1]).append("',");
//                builder.append("'").append(values[2]).append("',");
//                builder.append("'").append(values[3]).append("',");
//                builder.append("'").append(values[4]).append("')");
////                System.out.println(builder.toString());
//                entries.add(builder.toString());
//                //entries.add(dbc.insertServerFormat(values[0], values[1], values[2], values[3], values[4]));
//                //System.out.println(String.format("Date:%s ID:%s Gender:%s Age:%s Income:%s Context:%s   Impression Cost:%s",values[0],values[1],values[2],values[3],values[4],values[5],values[6]));
//            }
//            br.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        dbc.enactStatements(entries);
        System.out.println("serverlog"+(System.currentTimeMillis()-a));
        dbc.createTables11();

        long endTime = System.currentTimeMillis();
        System.out.println("Executing time: " + (endTime - startTime)/1000 + " s");
    }

    private static ByteBuffer reAllocate(ByteBuffer stringBuffer) {
        final int capacity = stringBuffer.capacity();
        byte[] newBuffer = new byte[capacity * 2];
        System.arraycopy(stringBuffer.array(), 0, newBuffer, 0, capacity);
        return (ByteBuffer) ByteBuffer.wrap(newBuffer).position(capacity);
    }

    /**
     *
     */
    public boolean csvHeaderChecker(String filePath, String header) {
        try {
            File file = new File(filePath);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            //System.out.println(line);
            if (line != null && line.equals(header)) {
                br.close();
                try {
                    if (header.equals("Date,ID,Click Cost")) {
                        clickLines = Files.lines(file.toPath()).count();
                        clickLogPath = file.getAbsolutePath();
                    } else if (header.equals("Date,ID,Gender,Age,Income,Context,Impression Cost")) {
                        impressionLines = Files.lines(file.toPath()).count();
                        impressionLogPath = file.getAbsolutePath();
                    } else if (header.equals("Entry Date,ID,Exit Date,Pages Viewed,Conversion")) {
                        serverLines = Files.lines(file.toPath()).count();
                        serverLogPath = file.getAbsolutePath();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<Boolean> folderChecker() {

        Boolean clickLogExsist = false;
        Boolean impressionLogExsist = false;
        Boolean serverLogExsist = false;

        for (File f : path.listFiles()) {
            if (f.isFile()) {
                if (f.getName().equals(CLICK_LOG) && csvHeaderChecker(f.getAbsolutePath(), "Date,ID,Click Cost")) {
                    clickLogExsist = true;
                    clickLogPath = f.getAbsolutePath();
                }
                if (f.getName().equals(IMPRESSION_LOG) && csvHeaderChecker(f.getAbsolutePath(), "Date,ID,Gender,Age,Income,Context,Impression Cost")) {
                    impressionLogExsist = true;
                    impressionLogPath = f.getAbsolutePath();
                }
                if (f.getName().equals(SERVER_LOG) && csvHeaderChecker(f.getAbsolutePath(), "Entry Date,ID,Exit Date,Pages Viewed,Conversion")) {
                    serverLogExsist = true;
                    serverLogPath = f.getAbsolutePath();
                }
            }
        }
        ArrayList<Boolean> result = new ArrayList<>();
        result.add(clickLogExsist);
        result.add(impressionLogExsist);
        result.add(serverLogExsist);
        return result;
    }

    public double getProgress() {
        double clickShare = clickLines / (clickLines + impressionLines + serverLines);
        double impressionShare = impressionLines / (clickLines + impressionLines + serverLines);
        double serverShare = serverLines / (clickLines + impressionLines + serverLines);


        //System.out.println("Click share:" + clickShare + "   clicklines:" + clickLines +"   fm:"+ (clickLines+impressionLines+serverLines)+"   result:" + (double)clickLines/(double)(clickLines+impressionLines+serverLines));
        if (clickHandleTime == 0 && impressionHandleTime == 0 && serverHandleTime == 0) {
            return Math.min(1, clickShare);
        } else if (clickHandleTime != 0 && impressionHandleTime == 0 && serverHandleTime == 0) {
            speed = clickLines / (double) clickHandleTime * 0.3;
            long timeTaken = System.currentTimeMillis() - startTime - clickHandleTime;
            return Math.min(clickShare + impressionShare * ((timeTaken * speed) / impressionLines), clickShare + impressionShare);
        } else if (impressionHandleTime != 0 && serverHandleTime == 0) {
            long timeTaken = System.currentTimeMillis() - (startTime + clickHandleTime);
            return Math.min(clickShare + impressionShare + serverShare * ((timeTaken * speed) / serverLines), 1);
        } else {
            return 1;
        }
    }


}
