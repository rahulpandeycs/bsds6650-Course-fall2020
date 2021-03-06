
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.sql.DataSource;

public class JDBConnection {
  // JDBC Driver Name & Database URL
//  static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
//  static final String JDBC_DB_URL = "jdbc:mysql://localhost:3306/mysqlslap";
//
//  // JDBC Database Credentials
//  static final String JDBC_USER = "root";
//  static final String JDBC_PASS = "password";

  private String JDBC_DRIVER;
  private String JDBC_DB_URL;

  // JDBC Database Credentials
  private String JDBC_USER;
  private String JDBC_PASS;

  private static GenericObjectPool gPool = null;

  public JDBConnection() {
    try {
      Properties configParameters = loadHibernateConfigFile();

      this.JDBC_DB_URL = configParameters.getProperty("cmd.url");
      this.JDBC_DRIVER = configParameters.getProperty("cmd.driver");
      this.JDBC_USER = configParameters.getProperty("cmd.user");
      this.JDBC_PASS = configParameters.getProperty("cmd.password");

    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }

  public DataSource setUpPool() throws Exception {
    Class.forName(JDBC_DRIVER);

    // Creates an Instance of GenericObjectPool That Holds Our Pool of Connections Object!
    gPool = new GenericObjectPool();
    gPool.setMaxActive(10);

    // Creates a ConnectionFactory Object Which Will Be Use by the Pool to Create the Connection Object!
    ConnectionFactory cf = new DriverManagerConnectionFactory(JDBC_DB_URL, JDBC_USER, JDBC_PASS);

    // Creates a PoolableConnectionFactory That Will Wraps the Connection Object Created by the ConnectionFactory to Add Object Pooling Functionality!
    PoolableConnectionFactory pcf = new PoolableConnectionFactory(cf, gPool, null, null, false, true);
    return new PoolingDataSource(gPool);
  }

  public GenericObjectPool getConnectionPool() {
    return gPool;
  }

  // This Method Is Used To Print The Connection Pool Status
  void printDbStatus() {
//    System.out.println("Max.: " + getConnectionPool().getMaxActive() + "; Active: " + getConnectionPool().getNumActive() + "; Idle: " + getConnectionPool().getNumIdle());
  }

  private static Properties loadHibernateConfigFile() throws IOException {
    System.out.println("Reading default config.properties");
    InputStream input = ConnectionUtil.class.getClassLoader().getResourceAsStream("config.properties");
    Properties prop = new Properties();
    if (input == null) {
      System.out.println("Sorry, unable to find config.properties");
      return null;
    }
    //load a properties file from class path, inside static method
    prop.load(input);
    return prop;
  }
}
