import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.LiftRide;
import io.swagger.client.model.SkierVertical;

public class APICallRetry {
  public static final int DEFAULT_RETRIES = 5;
  public static final long DEFAULT_WAIT_TIME_IN_MILLI = 1000;

  private int numberOfRetries;
  private int numberOfTriesLeft;
  private long timeToWait;

  public APICallRetry() {
    this(DEFAULT_RETRIES, DEFAULT_WAIT_TIME_IN_MILLI);
  }

  public APICallRetry(int numberOfRetries,
                      long timeToWait) {
    this.numberOfRetries = numberOfRetries;
    numberOfTriesLeft = numberOfRetries;
    this.timeToWait = timeToWait;
  }

  /**
   * @return true if there are tries left
   */
  public boolean shouldRetry() {
    return numberOfTriesLeft > 0;
  }

  public ApiResponse<Void> errorOccurredInPost(SkiersApi skiersApi, LiftRide liftRide) throws ApiException {
    if (!shouldRetry()) {
      throw new ApiException("Retry Failed: Total " + numberOfRetries
              + " attempts made at interval " + getTimeToWait()
              + "ms");
    }

    ApiResponse<Void> retryApiResponse = null;
    try {
      retryApiResponse = doPostCallToCreateLiftRide(liftRide, skiersApi);
    } catch (ApiException ex) {
    }

    numberOfTriesLeft--;

    if (retryApiResponse != null && retryApiResponse.getStatusCode() == 201)
      return retryApiResponse;

    waitUntilNextTry();
    retryApiResponse = errorOccurredInPost(skiersApi, liftRide);
    return retryApiResponse;
  }

  public ApiResponse<SkierVertical> errorOccurredInGet(SkiersApi skiersApi, int skierId) throws ApiException {

    if (!shouldRetry()) {
      throw new ApiException("Retry Failed: Total " + numberOfRetries
              + " attempts made at interval " + getTimeToWait()
              + "ms");
    }

    ApiResponse<SkierVertical> retryApiResponse = null;
    try {
      retryApiResponse = doGetVerticalWithSkierId(skiersApi, skierId);
    } catch (ApiException ex) {
    }

    numberOfTriesLeft--;

    if (retryApiResponse != null && retryApiResponse.getStatusCode() == 200)
      return retryApiResponse;

    waitUntilNextTry();
    retryApiResponse = errorOccurredInGet(skiersApi, skierId);
    return retryApiResponse;
  }

  public ApiResponse<SkierVertical> errorOccurredInGetWithManyParameters(SkiersApi skiersApi, int skierId, String dayId, String resortId) throws ApiException {

    if (!shouldRetry()) {
      throw new ApiException("Retry Failed: Total " + numberOfRetries
              + " attempts made at interval " + getTimeToWait()
              + "ms");
    }

    ApiResponse<SkierVertical> retryApiResponse = null;
    try {
      retryApiResponse = doGetVerticalWithSkierIdDayIdAndResort(skiersApi, skierId, dayId, resortId);
    } catch (ApiException ex) {
    }

    numberOfTriesLeft--;

    if (retryApiResponse != null && retryApiResponse.getStatusCode() == 200)
      return retryApiResponse;

    waitUntilNextTry();
    retryApiResponse = errorOccurredInGetWithManyParameters(skiersApi, skierId, dayId, resortId);
    return retryApiResponse;
  }

  private ApiResponse<SkierVertical> doGetVerticalWithSkierId(SkiersApi skiersApi, int skierId) throws ApiException {
    ApiResponse<SkierVertical> skierResponse = SkierApiUtils.callSkierResortTotalsWithOneParameters(skiersApi, String.valueOf(skierId));
    return skierResponse;
  }

  private ApiResponse<SkierVertical> doGetVerticalWithSkierIdDayIdAndResort(SkiersApi skiersApi, int skierId, String dayId, String resortId) throws ApiException {
    ApiResponse<SkierVertical> skierResponse = SkierApiUtils.callSkierApiGetWithParameters(skiersApi, resortId, String.valueOf(dayId), String.valueOf(skierId));
    return skierResponse;
  }

  private ApiResponse<Void> doPostCallToCreateLiftRide(LiftRide liftRide, SkiersApi skiersApi) throws ApiException {
    ApiResponse<Void> writeResponse = SkierApiUtils.callWriteNewLiftRide(skiersApi, liftRide);
    return writeResponse;
  }

  public long getTimeToWait() {
    return timeToWait;
  }

  private void waitUntilNextTry() {
    try {
      Thread.sleep(getTimeToWait());
    } catch (InterruptedException ignored) {
    }
  }
}
