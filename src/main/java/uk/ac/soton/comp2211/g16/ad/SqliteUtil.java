package uk.ac.soton.comp2211.g16.ad;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SqliteUtil {

//    private static final String CLASS_NAME = "org.sqlite.JDBC";
//    private static final String DB_URL_PREFIX = "jdbc:sqlite:";



   static SqliteUtil sqliteUtil=new SqliteUtil();
   public static  SqliteUtil xx(){
        return sqliteUtil;
    }

    private static DruidDataSource dbPoll;
    static {


        try {
            dbPoll = new DruidDataSource();
            dbPoll.setDriverClassName("org.sqlite.JDBC");
            dbPoll.setUrl("jdbc:sqlite:dashboard.db");
            dbPoll.setInitialSize(5);
            dbPoll.setMinIdle(5);
            dbPoll.setMaxActive(8);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static DruidPooledConnection getConn() throws Exception {
        return dbPoll.getConnection();
    }

    public static void changeSource(String databaseFileName){
        dbPoll = new DruidDataSource();
        dbPoll.setDriverClassName("org.sqlite.JDBC");
        dbPoll.setUrl("jdbc:sqlite:"+databaseFileName);
        dbPoll.setInitialSize(5);
        dbPoll.setMinIdle(5);
        dbPoll.setMaxActive(8);

    }



    public static void closeStatement(Statement statement) throws SQLException {
        if (null != statement && !statement.isClosed()) {
            Connection conn = statement.getConnection();
            statement.close();
            closeConnection(conn);
        }
    }

    public static void closeConnection(Connection conn) throws SQLException {
        if (null != conn && !conn.isClosed()) {
            conn.close();
        }
    }
}
