

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RestApiClient {

  //  final static String basePath = "http://localhost:8081/CS6650Assignment1Server_war_exploded";
  private static Logger logger = LoggerFactory.getLogger(RestApiClient.class);
  // private static SkierVertical Resorts;

  public static void main(String[] args) {

    try (InputStream input = RestApiClient.class.getClassLoader().getResourceAsStream("config.properties")) {
      Properties prop = new Properties();
      if (input == null) {
        System.out.println("Sorry, unable to find config.properties");
        return;
      }
      //load a properties file from class path, inside static method
      prop.load(input);
      ConfigParameters parameters = new ConfigParameters(prop);
      ThreadManager threadManager = new ThreadManager(parameters);
      threadManager.run();

    } catch (IOException ex) {
      logger.error("The config.properties not present");
    }
    logger.info("Started with Execution of Phase 1 at: " + System.currentTimeMillis());


    logger.info("Ended with Execution of Phase 1 at: " + System.currentTimeMillis());

  }
}
