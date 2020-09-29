
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import io.swagger.client.ApiException;
import io.swagger.client.api.ResortsApi;
import io.swagger.client.model.TopTen;

public class ResortApiUtils {

  private static Logger logger = LoggerFactory.getLogger(ResortApiUtils.class);

  static TopTen callResortApiGet(ResortsApi apiInstance){
    List<String> resort = Arrays.asList("resort_example"); // List<String> | resort to query by
    List<String> dayID = Arrays.asList("dayID_example"); // List<String> | day number in the season
    TopTen topTen = null;
    try {
      topTen = apiInstance.getTopTenVert(resort, dayID);
    } catch (ApiException e) {
      System.err.println("Exception when calling ResortsApi#getTopTenVert");
      e.printStackTrace();
      logger.error("The get request to Resort API failed with error: " + e.getMessage() + "Reason being: " +e.getCause());
    }
    return topTen;
  }

}
