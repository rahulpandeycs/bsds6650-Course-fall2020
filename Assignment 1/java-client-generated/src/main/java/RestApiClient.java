

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.client.ApiClient;
import io.swagger.client.api.ResortsApi;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.LiftRide;
import io.swagger.client.model.SkierVertical;

public class RestApiClient {

  final static String basePath = "http://localhost:8081/CS6650Assignment1Server_war_exploded";
  private static Logger logger = LoggerFactory.getLogger(RestApiClient.class);
  private static SkierVertical Resorts;

  public static void main(String[] args) {

    final int MAX_THREADS = 256;
    final int NUM_SKIERS = 50000;
    final int NUM_LIFTS = 40;
    final int SKI_DAY = 1;
    final String RESORT_NAME = "SilverMt";
    final String SERVER_ADDRESS = "";
    final int SKI_DAY_MAX = 420;

    final LiftRide liftRide = new LiftRide();
    liftRide.setResortID("Mission Ridge");
    liftRide.setDayID("23");
    liftRide.setSkierID("7889");
    liftRide.setTime("217");
    liftRide.setLiftID("21");


    logger.info("Started with Execution of Phase 1 at: " + System.currentTimeMillis());
    //Server At: http://localhost:8081/CS6650Assignment1Server_war_exploded/skiers/1/vertical
    ResortsApi resortsApi = new ResortsApi();
    SkiersApi skiersApi = new SkiersApi();

    String resortId = "11", dayId = "111", skierId = "1";

    //Execute Phase 1:
    //Do 100 POST Calls
    for (int threadCount = 0; threadCount < MAX_THREADS; threadCount++) {
      ApiClient client = skiersApi.getApiClient();
      client.setBasePath(basePath);

      for (int i = 0; i < 100; i++) {
        try {
          SkierApiUtils.callWriteNewLiftRide(skiersApi, liftRide);
          logger.info("Record successfully created!");
        } catch (Exception ex) {
          logger.error("Creation of Lift failed : " + ex.getMessage() + " With reason: " + ex.getCause());
        }
      }

      // Do 5 Get Calls:
      for (int i = 0; i < 5; i++) {
        SkierVertical skierResponse = SkierApiUtils.callSkierApiGetWithParameters(skiersApi, resortId, dayId, skierId);
      }
    }



    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~`
    //Execution Phase 2:
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //Do 100 POST Calls
    for (int threadCount = 0; threadCount < MAX_THREADS; threadCount++) {
      ApiClient client = skiersApi.getApiClient();
      client.setBasePath(basePath);

      for (int i = 0; i < 100; i++) {
        try {
          SkierApiUtils.callWriteNewLiftRide(skiersApi, liftRide);
          logger.info("Record successfully created!");
        } catch (Exception ex) {
          logger.error("Creation of Lift failed : " + ex.getMessage() + " With reason: " + ex.getCause());
        }
      }

      // Do 5 Get Calls:
      for (int i = 0; i < 5; i++) {
        SkierVertical skierResponse = SkierApiUtils.callSkierApiGetWithParameters(skiersApi, resortId, dayId, skierId);
      }
    }

    logger.info("Ended with Execution of Phase 1 at: " + System.currentTimeMillis());

  }
}
