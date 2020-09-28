import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.SkierVertical;

public class ThreadPhaseExecution {

  ConfigParameters parameters;
  int numGet;

  private static Logger logger = LoggerFactory.getLogger(ThreadPhaseExecution.class);

  public ThreadPhaseExecution(ConfigParameters configParameters, int numGet) {
    this.parameters = configParameters;
    this.numGet = numGet;
  }


  //Execute Phase 1
  void executePhase() {
    SkiersApi skiersApi = new SkiersApi();
    //Do 100 POST Calls
    for (int threadCount = 0; threadCount < parameters.maxThreads; threadCount++) {
      executePostCall(skiersApi);
      // Do 5 Get Calls:
      for (int i = 0; i < 5; i++) {
        try {
          SkierVertical skierResponse = SkierApiUtils.callSkierApiGetWithParameters(skiersApi, parameters.resortId, String.valueOf(parameters.skiDay), String.valueOf(parameters.numSkiers));
        } catch (ApiException e) {
          e.printStackTrace();
          logger.error("The get request to Resort API failed with error: " + e.getMessage() + "Reason being: " + e.getCause());
        }
      }
    }
  }

  //Execute Phase 2
  void executePhase2() {
    SkiersApi skiersApi = new SkiersApi();
    //Do 100 POST Calls
    for (int threadCount = 0; threadCount < parameters.maxThreads; threadCount++) {
      executePostCall(skiersApi);
      // Do 5 Get Calls:
      for (int i = 0; i < 5; i++) {
        try {
          SkierVertical skierResponse = SkierApiUtils.callSkierApiGetWithParameters(skiersApi, parameters.resortId, String.valueOf(parameters.skiDay), String.valueOf(parameters.numSkiers));
        } catch (ApiException e) {
          logger.error("The get request to Resort API failed with error: " + e.getMessage() + "Reason being: " + e.getCause());
        }
      }
    }
  }

  //Execute Phase 3
  void executePhase3() {
    SkiersApi skiersApi = new SkiersApi();
    //Do 100 POST Calls
    for (int threadCount = 0; threadCount < parameters.maxThreads; threadCount++) {
      executePostCall(skiersApi);
      // Do 10 Get Calls:
      for (int i = 0; i < 10; i++) {
        try {
          SkierVertical skierResponse = SkierApiUtils.callSkierApiGetWithParameters(skiersApi, parameters.resortId, String.valueOf(parameters.skiDay), String.valueOf(parameters.numSkiers));
        } catch (ApiException e) {
          logger.error("The get request to Resort API failed with error: " + e.getMessage() + "Reason being: " + e.getCause());
        }
      }
    }
  }

  void executePostCall(SkiersApi skiersApi) {
    ApiClient client = skiersApi.getApiClient();
    client.setBasePath(parameters.getAddressPort());

    for (int i = 0; i < 100; i++) {
      try {
        SkierApiUtils.callWriteNewLiftRide(skiersApi, SkierApiUtils.getSampleLiftRide());
        logger.info("Record successfully created!");
      } catch (Exception ex) {
        logger.error("Creation of Lift failed : " + ex.getMessage() + " With reason: " + ex.getCause());
      }
    }
  }
}
