import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.LiftRide;
import io.swagger.client.model.SkierVertical;

public class SkierApiUtils {

  private static Logger logger = LoggerFactory.getLogger(SkierApiUtils.class);

  // /skiers/{resortID}/days/{dayID}/skiers/{skierID}
  static SkierVertical callSkierApiGetWithParameters(SkiersApi apiInstance, String resortID, String dayId, String skierId){
    SkierVertical skierVertical = null;
    try {
      skierVertical = apiInstance.getSkierDayVertical(resortID,dayId,skierId);
    } catch (ApiException e) {
      System.err.println("Exception when calling SkierApi#/skiers/{resortID}/days/{dayID}/skiers/{skierID}");
      e.printStackTrace();
      logger.error("The get request to Resort API failed with error: " + e.getMessage() + "Reason being: " +e.getCause());
    }
    return skierVertical;
  }

  // /skiers/{skierID}/vertical
  static SkierVertical callSkierResortTotalsWithOneParameters(SkiersApi apiInstance, String skierId){
    SkierVertical skierVerticalR = null;
    ApiResponse<SkierVertical> skierResortTotalsResponse = null;
    List<String> resort = Arrays.asList("resort_example");
    try {
      skierResortTotalsResponse = apiInstance.getSkierResortTotalsWithHttpInfo(skierId,resort);
    } catch (ApiException e) {
      System.err.println("Exception when calling SkierApi#getSkierResortTotalsWithHttpInfo");
      e.printStackTrace();
      logger.error("The get request to Resort API failed with error: " + e.getMessage() + "Reason being: " +e.getCause());
    }
    return skierResortTotalsResponse.getData();
  }

  // /skiers/{skierID}/vertical
  public static void callWriteNewLiftRide(SkiersApi apiInstance, LiftRide body){
    List<String> resort = Arrays.asList("resort_example");
    try {
      apiInstance.writeNewLiftRide(body);
    } catch (ApiException e) {
      System.err.println("Exception when calling SkierApi#getSkierResortTotalsWithHttpInfo");
      e.printStackTrace();
      logger.error("The get request to Resort API failed with error: " + e.getMessage() + "Reason being: " +e.getCause());
    }
  }
}
