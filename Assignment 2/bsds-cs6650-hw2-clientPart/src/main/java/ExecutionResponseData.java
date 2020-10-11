import java.util.Comparator;

public class ExecutionResponseData implements Comparable<ExecutionResponseData> {

  private long startTime;
  private String requestType;
  private long latency;
  private int responseCode;

  public ExecutionResponseData(long startTime, String requestType, long latency, int responseCode) {
    this.startTime = startTime;
    this.requestType = requestType;
    this.latency = latency;
    this.responseCode = responseCode;
  }

  public long getStartTime() {
    return startTime;
  }

  public String getRequestType() {
    return requestType;
  }

  public long getLatency() {
    return latency;
  }

  public int getResponseCode() {
    return responseCode;
  }

//TODO:
  @Override
  public int compareTo(ExecutionResponseData executionResponseData) {
    return (int) (this.latency - executionResponseData.latency);
  }
}
