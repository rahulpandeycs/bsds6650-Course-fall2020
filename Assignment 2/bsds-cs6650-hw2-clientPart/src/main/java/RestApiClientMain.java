

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RestApiClientMain {

  //  final static String basePath = "http://localhost:8081/CS6650Assignment1Server_war_exploded";
  private static Logger logger = LoggerFactory.getLogger(RestApiClientMain.class);
  // private static SkierVertical Resorts;

  public static void main(String[] args) {

    try {
      java.io.File propertiesFile = null;
      InputStream input = null;
      if (args.length > 1 && args[0].equals("-f")) {
        propertiesFile = new java.io.File(args[1]);
        System.out.println("Reading file: " + args[1]);
        input = new java.io.FileInputStream(propertiesFile);
      } else {
        System.out.println("Reading default config.properties");
        input = RestApiClientMain.class.getClassLoader().getResourceAsStream("config.properties");
      }
      Properties prop = new Properties();
      if (input == null) {
        System.out.println("Sorry, unable to find config.properties");
        logger.error("Sorry, unable to find config.properties");
        return;
      }
      //load a properties file from class path, inside static method
      prop.load(input);
      ConfigParameters parameters = new ConfigParameters(prop);
      if (!(parameters.getNumLifts() >= 5 && parameters.getNumLifts() <= 60)){
        System.out.println("Execution abort! Number of Lifts should be in range 5-60");
        return;
      }
      ThreadManager threadManager = new ThreadManager(parameters);
      Thread mainThread = new Thread(threadManager);
      mainThread.start();
    } catch (IOException ex) {
      System.out.println("The config.properties not present");
      logger.error("The config.properties not present");
    }
  }
}
