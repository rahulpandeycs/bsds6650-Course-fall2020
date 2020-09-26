import java.util.Arrays;
import java.util.List;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.ResortsApi;
import io.swagger.client.model.ResortsList;
import io.swagger.client.model.TopTen;

public class RestApiClient {

  public static void main(String[] args) {

    ResortsApi apiInstance = new ResortsApi();
    ApiClient client = apiInstance.getApiClient();
    String basePath = "";
    client.setBasePath(basePath);

    List<String> resort = Arrays.asList("resort_example"); // List<String> | resort to query by
    List<String> dayID = Arrays.asList("dayID_example"); // List<String> | day number in the season
    try {
      TopTen result = apiInstance.getTopTenVert(resort, dayID);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ResortsApi#getTopTenVert");
      e.printStackTrace();
    }
  }
}
