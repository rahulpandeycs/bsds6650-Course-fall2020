import java.util.Properties;

public class ConfigParameters {

  int maxThreads;
  int numSkiers;
  int numLifts;
  int skiDay;
  String resortId;
  String addressPort;

  public ConfigParameters(Properties properties) {
    this.maxThreads = Integer.valueOf(properties.getProperty("cmd.maxThreads"));
    this.numSkiers = Integer.valueOf(properties.getProperty("cmd.numSkiers"));
    this.numLifts = Integer.valueOf(properties.getProperty("cmd.numLifts"));
    this.skiDay = Integer.valueOf(properties.getProperty("cmd.skiDay"));
    this.resortId = properties.getProperty("cmd.resortId");
    this.addressPort = properties.getProperty("cmd.addressPort");
  }



  public int getMaxThreads() {
    return maxThreads;
  }

  public int getNumSkiers() {
    return numSkiers;
  }

  public int getNumLifts() {
    return numLifts;
  }

  public int getSkiDay() {
    return skiDay;
  }

  public String getResortId() {
    return resortId;
  }

  public String getAddressPort() {
    return addressPort;
  }
}
