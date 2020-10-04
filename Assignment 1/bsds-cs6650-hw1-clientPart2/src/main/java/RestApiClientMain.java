

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

    try (InputStream input = RestApiClientMain.class.getClassLoader().getResourceAsStream("config.properties")) {
      Properties prop = new Properties();
      if (input == null) {
        logger.error("Sorry, unable to find config.properties");
        return;
      }
      //load a properties file from class path, inside static method
      prop.load(input);
      ConfigParameters parameters = new ConfigParameters(prop);
      ThreadManager threadManager = new ThreadManager(parameters);
      Thread mainThread = new Thread(threadManager);
      mainThread.start();
    } catch (IOException ex) {
      logger.error("The config.properties not present");
    }
  }
}
