/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduleapplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Duy Hua
 */
public class DBConnection {
    
    // JDBC URL parts
    private static final String protocol = "jdbc";
    private static final String vendorName = ":mysql:";
    private static final String ipAddress = "//wgudb.ucertify.com:3306/";
    private static final String dbName = "WJ08mRY";
    
    // Concat the URL
    private static final String jdbcURL = protocol + vendorName + ipAddress + dbName;
    
    private static final String MYSQLJDBCDriver = "com.mysql.jdbc.Driver";
    
    private static final String username = "U08mRY";
    private static final String password = "53689334586";
    private static Connection conn = null;
    
    public static Connection startConnection() {
        try {
            Class.forName(MYSQLJDBCDriver);
            conn = DriverManager.getConnection(jdbcURL, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return conn;
    }
    
    //Need to get the connection
    public static Connection getConnection() {
        return conn;
    }
    
    public static void closeConnection() {
        try {
            conn.close();
        } catch (Exception e) {
            
        }
    }
            
}
