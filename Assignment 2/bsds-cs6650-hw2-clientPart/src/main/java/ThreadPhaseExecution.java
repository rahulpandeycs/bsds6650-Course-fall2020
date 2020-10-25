import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.LiftRide;
import io.swagger.client.model.SkierVertical;

public class ThreadPhaseExecution implements Callable<List<ExecutionResponseData>> {

  private ConfigParameters parameters;
  private SharedGlobalCount successCount;
  private SharedGlobalCount failCount;
  private ThreadManager threadManager;
  private CountDownLatch latch;
  private PhaseExecutionParameter phaseExecutionParameter;
  private Random random = new Random();
  private List<ExecutionResponseData> responseDataList;

  private static Logger logger = LoggerFactory.getLogger(ThreadPhaseExecution.class);

  public ThreadPhaseExecution(ConfigParameters configParameters, PhaseExecutionParameter phaseExecutionParameter, ThreadManager threadManager, CountDownLatch latch) {
    this.parameters = configParameters;
    this.successCount = new SharedGlobalCount();
    this.failCount = new SharedGlobalCount();
    this.threadManager = threadManager;
    this.latch = latch;
    this.phaseExecutionParameter = phaseExecutionParameter;
    this.responseDataList = new ArrayList<>();
  }

  @Override
  public List<ExecutionResponseData> call() {

    SkiersApi skiersApi = new SkiersApi();

    //Random for GET Call
    int dayId = ThreadLocalRandom.current().nextInt(phaseExecutionParameter.getStartTime(), phaseExecutionParameter.getEndTime() + 1);

    //Create liftRide for write API
    //  ResortID ="Mission Ridge", DayID ="23", SkierID="7889", Time="217", LiftID="21";
    LiftRide liftRide = SkierApiUtils.getSampleLiftRide();

    //Execute Get call to get totalVerticalPerSkierDay
    executePostCall(skiersApi, liftRide);

    //Execute Post call to create a Lift Ride.
    executeGetCallTotalVertForSkiDay(skiersApi, dayId);
    executeGetCallTotalVertForResort(skiersApi, dayId);

    latch.countDown();
    threadManager.getGlobalCountSuccess().incrementCounterBy(this.successCount.getCounter());
    threadManager.getGlobalCountFail().incrementCounterBy(this.failCount.getCounter());
    return this.responseDataList;
  }

  private void executeGetCallTotalVertForSkiDay(SkiersApi skiersApi, int dayId) {

    // Do numGet Get Calls:
    for (int i = 0; i < phaseExecutionParameter.getNumGet(); i++) {
      int randomGetSkierId = ThreadLocalRandom.current().nextInt(phaseExecutionParameter.getStartSkierId(), phaseExecutionParameter.getEndSkierId() + 1);
      long startTime = System.currentTimeMillis();
      try {
        ApiResponse<SkierVertical> skierResponse = SkierApiUtils.callSkierApiGetWithParameters(skiersApi, parameters.getResortId(), String.valueOf(dayId), String.valueOf(randomGetSkierId));
        if (skierResponse.getStatusCode() == 200)
          successCount.incrementCounter();
        else {
          logger.error("The GET request failed with response code: " + skierResponse.getStatusCode());
          failCount.incrementCounter();
        }
        this.responseDataList.add(new ExecutionResponseData(startTime, "GET", System.currentTimeMillis() - startTime, skierResponse.getStatusCode()));
      } catch (ApiException e) {
        logger.error("The get request to Resort API failed with error: " + e.getMessage() + "Reason being: " + e.getCause());
        failCount.incrementCounter();
        this.responseDataList.add(new ExecutionResponseData(startTime, "GET: Reason -> " + e.getCause(), System.currentTimeMillis() - startTime, e.getCode()));
        e.printStackTrace();
      }
    }
  }


  private void executeGetCallTotalVertForResort(SkiersApi skiersApi, int dayId) {
    // Do numGet Get Calls:
    for (int i = 0; i < phaseExecutionParameter.getNumGet(); i++) {
      int randomGetSkierId = ThreadLocalRandom.current().nextInt(phaseExecutionParameter.getStartSkierId(), phaseExecutionParameter.getEndSkierId() + 1);
      long startTime = System.currentTimeMillis();
      try {
        ApiResponse<SkierVertical> skierResponse = SkierApiUtils.callSkierResortTotalsWithOneParameters(skiersApi, String.valueOf(randomGetSkierId));
        if (skierResponse.getStatusCode() == 200)
          successCount.incrementCounter();
        else {
          logger.error("The GET request failed with response code: " + skierResponse.getStatusCode());
          failCount.incrementCounter();
        }
        this.responseDataList.add(new ExecutionResponseData(startTime, "GET", System.currentTimeMillis() - startTime, skierResponse.getStatusCode()));
      } catch (ApiException e) {
        logger.error("The get request to Resort API failed with error: " + e.getMessage() + "Reason being: " + e.getCause());
        failCount.incrementCounter();
        this.responseDataList.add(new ExecutionResponseData(startTime, "GET : Reason = " + e.getCause(), System.currentTimeMillis() - startTime, e.getCode()));
        e.printStackTrace();
      }
    }
  }


//  private Function<Void, ApiResponse<SkierVertical>> getAPICallFunctionWithSkiDay(SkiersApi skiersApi) {
//    Function<Void, ApiResponse<SkierVertical>> getCallWithSkiDay = new Function<Void, ApiResponse<SkierVertical>>() {
//      ApiResponse<SkierVertical> response;
//
//      @Override
//      public ApiResponse<SkierVertical> apply(Void inputResponseClass) {
//        int randomGetSkierId = ThreadLocalRandom.current().nextInt(phaseExecutionParameter.getStartSkierId(), phaseExecutionParameter.getEndSkierId() + 1);
//        try {
//          response = SkierApiUtils.callSkierApiGetWithParameters(skiersApi, parameters.getResortId(), String.valueOf(dayId), String.valueOf(randomGetSkierId));
//        } catch (ApiException e) {
//          e.printStackTrace();
//          logger.error("The get request to Resort API failed with error: " + e.getMessage() + "Reason being: " + e.getCause());
//          failCount.incrementCounter();
//          this.responseDataList.add(new ExecutionResponseData(startTime, "GET", System.currentTimeMillis() - startTime, e.getCode()));
//        }
//        return response;
//      }
//    };
//    return getCallWithSkiDay;
//  }

//  private Function<Void, ApiResponse<SkierVertical>> getAPICallFunction(SkiersApi skiersApi) {
//    Function<Void, ApiResponse<SkierVertical>> getCallWithSkiDay = new Function<Void, ApiResponse<SkierVertical>>() {
//      ApiResponse<SkierVertical> response;
//
//      @Override
//      public ApiResponse<SkierVertical> apply(Void inputResponseClass) {
//        int randomGetSkierId = ThreadLocalRandom.current().nextInt(phaseExecutionParameter.getStartSkierId(), phaseExecutionParameter.getEndSkierId() + 1);
//        try {
//          response = SkierApiUtils.callSkierApiGetWithParameters(skiersApi, parameters.getResortId(), String.valueOf(dayId), String.valueOf(randomGetSkierId));
//        } catch (ApiException e) {
//          e.printStackTrace();
//        }
//        return response;
//      }
//    };
//    return getCallWithSkiDay;
//  }


  private void executePostCall(SkiersApi skiersApi, LiftRide liftRide) {
    ApiClient client = skiersApi.getApiClient();
    client.setBasePath(parameters.getAddressPort());

    for (int i = 0; i < phaseExecutionParameter.getNumPost(); i++) {

      int randomSkierId = ThreadLocalRandom.current().nextInt(phaseExecutionParameter.getStartSkierId(), phaseExecutionParameter.getEndSkierId() + 1);
      int randomTime = ThreadLocalRandom.current().nextInt(phaseExecutionParameter.getStartTime(), phaseExecutionParameter.getEndTime() + 1);
      int randomLiftNum = ThreadLocalRandom.current().nextInt(1, phaseExecutionParameter.getNumLifts());

      liftRide.setSkierID(String.valueOf(randomSkierId));
      liftRide.setLiftID(String.valueOf(randomLiftNum));
      liftRide.setTime(String.valueOf(randomTime));

      long startTime = System.currentTimeMillis();
      try {
        ApiResponse<Void> writeResponse = SkierApiUtils.callWriteNewLiftRide(skiersApi, liftRide);
        if (writeResponse.getStatusCode() == 201) {
          successCount.incrementCounter();
          logger.info("Record successfully created!");
        } else {
          logger.error("The POST request failed with response code: " + writeResponse.getStatusCode());
          failCount.incrementCounter();
        }
        this.responseDataList.add(new ExecutionResponseData(startTime, "POST", System.currentTimeMillis() - startTime, writeResponse.getStatusCode()));
      } catch (ApiException ex) {
        ex.printStackTrace();
        this.responseDataList.add(new ExecutionResponseData(startTime, "POST : Reason: " + ex.getCause(), System.currentTimeMillis() - startTime, ex.getCode()));
        failCount.incrementCounter();
        logger.error("Creation of Lift failed : " + ex.getMessage() + " With reason: " + ex.getCause());
      }
    }
  }
}
