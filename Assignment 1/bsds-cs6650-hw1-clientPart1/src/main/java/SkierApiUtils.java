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
  static ApiResponse<SkierVertical> callSkierApiGetWithParameters(SkiersApi apiInstance, String resortID, String dayId, String skierId) throws ApiException {
    ApiResponse<SkierVertical> skierVertical = apiInstance.getSkierDayVerticalWithHttpInfo(resortID, dayId, skierId);
    return skierVertical;
  }

  // /skiers/{skierID}/vertical
  static SkierVertical callSkierResortTotalsWithOneParameters(SkiersApi apiInstance, String skierId) throws ApiException {
    List<String> resort = Arrays.asList("resort_example");
    ApiResponse<SkierVertical>  skierResortTotalsResponse = apiInstance.getSkierResortTotalsWithHttpInfo(skierId,resort);
    return skierResortTotalsResponse.getData();
  }

  // /skiers/{skierID}/vertical
  public static ApiResponse<Void> callWriteNewLiftRide(SkiersApi apiInstance, LiftRide body) throws ApiException {
    List<String> resort = Arrays.asList("resort_example");
    ApiResponse<Void> response = apiInstance.writeNewLiftRideWithHttpInfo(body);
    return response;
  }

  public static LiftRide getSampleLiftRide(){
    final LiftRide liftRide = new LiftRide();
    liftRide.setResortID("Mission Ridge");
    liftRide.setDayID("23");
    liftRide.setSkierID("7889");
    liftRide.setTime("217");
    liftRide.setLiftID("21");
    return liftRide;
  }
}
