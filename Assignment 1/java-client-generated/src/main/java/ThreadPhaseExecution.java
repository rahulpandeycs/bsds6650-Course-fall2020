import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.LiftRide;
import io.swagger.client.model.SkierVertical;

public class ThreadPhaseExecution implements Runnable {

  private ConfigParameters parameters;
  private SharedGlobalCount successCount;
  private SharedGlobalCount failCount;
  private ThreadManager threadManager;
  private CountDownLatch latch;
  private PhaseExecutionParameter phaseExecutionParameter;
  private Random random = new Random();

  private static Logger logger = LoggerFactory.getLogger(ThreadPhaseExecution.class);

  public ThreadPhaseExecution(ConfigParameters configParameters, PhaseExecutionParameter phaseExecutionParameter, ThreadManager threadManager, CountDownLatch latch) {
    this.parameters = configParameters;
    this.successCount = new SharedGlobalCount();
    this.failCount = new SharedGlobalCount();
    this.threadManager = threadManager;
    this.latch = latch;
    this.phaseExecutionParameter = phaseExecutionParameter;
  }

  @Override
  public void run() {
    SkiersApi skiersApi = new SkiersApi();

    //Do numPost POST Calls
    for (int threadCount = 0; threadCount < phaseExecutionParameter.getThreadsToExecute(); threadCount++) {

      int randomSkierId = ThreadLocalRandom.current().nextInt(phaseExecutionParameter.startSkierId, phaseExecutionParameter.endSkierId + 1);
      int dayId = ThreadLocalRandom.current().nextInt(phaseExecutionParameter.getStartTime(), phaseExecutionParameter.endTime + 1);
      int randomLiftNum = ThreadLocalRandom.current().nextInt(phaseExecutionParameter.getNumLifts());
      LiftRide liftRide = SkierApiUtils.getSampleLiftRide();
      liftRide.dayID(String.valueOf(dayId));
      liftRide.setLiftID(String.valueOf(randomLiftNum));
      liftRide.setTime(String.valueOf(dayId));

      executePostCall(skiersApi, liftRide);
      // Do numGet Get Calls:
      for (int i = 0; i < phaseExecutionParameter.numGet; i++) {
        try {
          ApiResponse<SkierVertical> skierResponse = SkierApiUtils.callSkierApiGetWithParameters(skiersApi, parameters.resortId, String.valueOf(dayId), String.valueOf(randomSkierId));
          if (skierResponse.getStatusCode() == 200)
            successCount.incrementCounter();
          else {
            logger.error("The GET request failed with response code: " + skierResponse.getStatusCode());
            failCount.incrementCounter();
          }
        } catch (ApiException e) {
          e.printStackTrace();
          logger.error("The get request to Resort API failed with error: " + e.getMessage() + "Reason being: " + e.getCause());
          failCount.incrementCounter();
        }
      }
      latch.countDown();
    }
    threadManager.globalCountSuccess.incrementCounterBy(this.successCount.counter);
    threadManager.globalCountFail.incrementCounterBy(this.failCount.counter);
  }

  void executePostCall(SkiersApi skiersApi, LiftRide liftRide) {
    ApiClient client = skiersApi.getApiClient();
    client.setBasePath(parameters.getAddressPort());

    for (int i = 0; i < phaseExecutionParameter.numPost; i++) {
      try {
        ApiResponse<Void> writeResponse = SkierApiUtils.callWriteNewLiftRide(skiersApi, liftRide);
        if (writeResponse.getStatusCode() == 200) {
          successCount.incrementCounter();
          logger.info("Record successfully created!");
        } else {
          logger.error("The GET request failed with response code: " + writeResponse.getStatusCode());
          failCount.incrementCounter();
        }
      } catch (ApiException ex) {
        failCount.incrementCounter();
        logger.error("Creation of Lift failed : " + ex.getMessage() + " With reason: " + ex.getCause());
      }
    }
  }
}
