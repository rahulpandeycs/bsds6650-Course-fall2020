import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.SkierVertical;

public class ThreadPhaseExecution implements Runnable{

  private ConfigParameters parameters;
  private int numGet;
  private int numPost;
  private SharedGlobalCount successCount ;
  private SharedGlobalCount failCount ;
  private ThreadManager threadManager;
  private CountDownLatch latch;
  private PhaseExecutionParameter phaseExecutionParameter;

  private static Logger logger = LoggerFactory.getLogger(ThreadPhaseExecution.class);

  public ThreadPhaseExecution(ConfigParameters configParameters, PhaseExecutionParameter phaseExecutionParameter, ThreadManager threadManager, CountDownLatch latch) {
    this.parameters = configParameters;
    this.numGet = numGet;
    this.numPost = numPost;
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
    for (int threadCount = 0; threadCount < this.parameters.maxThreads; threadCount++) {
      executePostCall(skiersApi);
      // Do numGet Get Calls:
      for (int i = 0; i < this.numGet; i++) {
        try {
          SkierVertical skierResponse = SkierApiUtils.callSkierApiGetWithParameters(skiersApi, parameters.resortId, String.valueOf(parameters.skiDay), String.valueOf(parameters.numSkiers));
          successCount.incrementCounter();
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

  void executePostCall(SkiersApi skiersApi) {
    ApiClient client = skiersApi.getApiClient();
    client.setBasePath(parameters.getAddressPort());

    for (int i = 0; i < numPost; i++) {
      try {
        SkierApiUtils.callWriteNewLiftRide(skiersApi, SkierApiUtils.getSampleLiftRide());
        logger.info("Record successfully created!");
        successCount.incrementCounter();
      } catch (Exception ex) {
        failCount.incrementCounter();
        logger.error("Creation of Lift failed : " + ex.getMessage() + " With reason: " + ex.getCause());
      }
    }
  }
}
