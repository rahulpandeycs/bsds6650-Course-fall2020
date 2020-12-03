import java.util.Properties;

public class ConfigParameters {

  private int maxThreads = 256;
  private int numSkiers = 50000;
  private int numLifts = 40;
  private int skiDay = 1;
  private String resortId = "SilverMt";
  private String addressPort;

  public ConfigParameters(Properties properties) {
    if(properties.getProperty("cmd.maxThreads") != null){
      this.maxThreads = Integer.valueOf(properties.getProperty("cmd.maxThreads"));
    }

    if(properties.getProperty("cmd.numSkiers") != null) {
      this.numSkiers = Integer.valueOf(properties.getProperty("cmd.numSkiers"));
    }

    if(properties.getProperty("cmd.numLifts") != null){
      this.numLifts = Integer.valueOf(properties.getProperty("cmd.numLifts"));
    }
    if(properties.getProperty("cmd.skiDay") != null){
      this.skiDay = Integer.valueOf(properties.getProperty("cmd.skiDay"));
    }
    if(properties.getProperty("cmd.resortId") != null){
      this.resortId = properties.getProperty("cmd.resortId");
    }
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
