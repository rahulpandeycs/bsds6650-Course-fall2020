
import java.util.Properties;

public class HibernateConfigParameters {
  private String DRIVER;
  private String URL;
  private String USER;
  private String PASS;
  private String HBM2DDL_AUTO;

  public HibernateConfigParameters(Properties properties) {
    if (properties.getProperty("cmd.driver") != null) {
      this.DRIVER = properties.getProperty("cmd.driver");
    }

//    if (properties.getProperty("cmd.url") != null) {
//      this.URL = properties.getProperty("cmd.url");
//    }

    if (properties.getProperty("cmd.replicaUrl") != null) {
      this.URL = properties.getProperty("cmd.replicaUrl");
    }

    if (properties.getProperty("cmd.user") != null) {
      this.USER = properties.getProperty("cmd.user");
    }
    if (properties.getProperty("cmd.password") != null) {
      this.PASS = properties.getProperty("cmd.password");
    }
    if (properties.getProperty("cmd.ddl") != null) {
      this.HBM2DDL_AUTO = properties.getProperty("cmd.ddl");
    }
  }

  public String getDRIVER() {
    return DRIVER;
  }

  public String getURL() {
    return URL;
  }

  public String getUSER() {
    return USER;
  }

  public String getPASS() {
    return PASS;
  }

  public String getHBM2DDL_AUTO() {
    return HBM2DDL_AUTO;
  }
}
