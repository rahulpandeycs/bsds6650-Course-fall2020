package utils;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

public class JDBCTestClass {
  public static void main(String[] args) {
    ResultSet rsObj = null;
    Connection connObj = null;
    PreparedStatement pstmtObj = null;
    JDBConnection jdbcObj = new JDBConnection();
    try {
      DataSource dataSource = jdbcObj.setUpPool();
      jdbcObj.printDbStatus();

      // Performing Database Operation!
      System.out.println("\n=====Making A New Connection Object For Db Transaction=====\n");
      connObj = dataSource.getConnection();
      jdbcObj.printDbStatus();

      pstmtObj = connObj.prepareStatement("SELECT * FROM mysqlslap.Records;");
      rsObj = pstmtObj.executeQuery();
      while (rsObj.next()) {
        System.out.println("Student Name: " + rsObj.getString("Student"));
      }
      System.out.println("\n=====Releasing Connection Object To Pool=====\n");
    } catch (Exception sqlException) {
      sqlException.printStackTrace();
    } finally {
      try {
        // Closing ResultSet Object
        if (rsObj != null) {
          rsObj.close();
        }
        // Closing PreparedStatement Object
        if (pstmtObj != null) {
          pstmtObj.close();
        }
        // Closing Connection Object
        if (connObj != null) {
          connObj.close();
        }
      } catch (Exception sqlException) {
        sqlException.printStackTrace();
      }
    }
    jdbcObj.printDbStatus();
  }
}
