import java.util.Collections;
import java.util.List;

public class PerformanceMetrics {

  List<ExecutionResponseData> responseDataList;

  public PerformanceMetrics(List<ExecutionResponseData> responseDataList) {
    this.responseDataList = responseDataList;
    Collections.sort(responseDataList);
  }

  long getMeanResponseTime() {
    long sum = 0;
    for (ExecutionResponseData executionResponseData : responseDataList)
      sum += executionResponseData.getLatency();
    return sum / (long)responseDataList.size();
  }

  long getMedianResponseTime() {
    long median;
    if (responseDataList.size() % 2 != 0)
      median = responseDataList.get(responseDataList.size() / 2).getLatency();
    else {
      median = (responseDataList.get(responseDataList.size() / 2).getLatency() + responseDataList.get(responseDataList.size() / 2 + 1).getLatency())/2;
    }
    return median;
  }

  long getP99Percentile() {
    return responseDataList.get((int)Math.ceil(responseDataList.size()*0.99)).getLatency();
  }

  long getMaxResponse() {
    return responseDataList.get(responseDataList.size()-1).getLatency();
  }

}
