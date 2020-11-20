package RabbiMQConsumer;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import PubSubQueue.SkiDataPublisher;
import model.LiftRide;

public class RabbitMQConsumerDao {
  JDBConnection jdbcObj;
  DataSource dataSource;

  public RabbitMQConsumerDao() {
    // Performing Database Operation!
    System.out.println("\n=====Making A New Connection Object For Db Transaction=====\n");
    jdbcObj = new JDBConnection();
    try {
      dataSource = jdbcObj.setUpPool();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public boolean updateLiftRide(LiftRide liftRide) throws SQLException {
    int rsObj;
    Connection connObj = null;
    PreparedStatement pstmtObj = null;

    try {
      jdbcObj.printDbStatus();
      connObj = dataSource.getConnection();
      jdbcObj.printDbStatus();

      //bsdsCS6650
      pstmtObj = connObj.prepareStatement("INSERT INTO bsdsCS6650.liftRide (dayId, resortID, skierID, time, liftID) " +
              "VALUES (?, ?, ?, ?, ?) " +
              "ON DUPLICATE KEY UPDATE " +
              "dayId = VALUES(dayId), " +
              "resortID = VALUES(resortID), " +
              "skierID = VALUES(skierID), " +
              "time = VALUES(time), " +
              "liftID = VALUES(liftID) ");

      pstmtObj.setString(1, String.valueOf(liftRide.getDayID()));
      pstmtObj.setString(2, String.valueOf(liftRide.getResortID()));
      pstmtObj.setString(3, String.valueOf(liftRide.getSkierID()));
      pstmtObj.setString(4, String.valueOf(liftRide.getTime()));
      pstmtObj.setString(5, String.valueOf(liftRide.getLiftID()));

      rsObj = pstmtObj.executeUpdate(); //save or update lift ride
      Logger.getLogger(RabbitMQConsumerDao.class.getName()).log(Level.INFO, "Record inserted successfully ");

    } finally {
      try {
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
    return true;
  }

  public boolean selectRecords() {
    ResultSet rsObj = null;
    Connection connObj = null;
    PreparedStatement pstmtObj = null;

    try {
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
    return true;
  }
}
