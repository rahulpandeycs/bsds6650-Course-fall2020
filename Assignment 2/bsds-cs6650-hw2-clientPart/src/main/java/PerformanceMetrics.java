import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class PerformanceMetrics {

  List<ExecutionResponseData> responseDataList;

  public PerformanceMetrics(Collection<Future<List<ExecutionResponseData>>> futureResponseDataList) throws ExecutionException, InterruptedException {
    this.responseDataList = new ArrayList<>();
    for (Future<List<ExecutionResponseData>> futureDataList : futureResponseDataList) {
      this.responseDataList.addAll(futureDataList.get());
//      for(ExecutionResponseData executionResponseData: futureDataList.get()){
//        this.responseDataList.add(executionResponseData);
//      }
    }
    Collections.sort(responseDataList);
  }

  long getMeanResponseTime() {
    long sum = 0;
    for (ExecutionResponseData executionResponseData : responseDataList)
      sum += executionResponseData.getLatency();
    return sum / (long) responseDataList.size();
  }

  long getMedianResponseTime() {
    long median;
    if (responseDataList.size() % 2 != 0)
      median = responseDataList.get(responseDataList.size() / 2).getLatency();
    else {
      median = (responseDataList.get(responseDataList.size() / 2).getLatency() + responseDataList.get(responseDataList.size() / 2 + 1).getLatency()) / 2;
    }
    return median;
  }

  long getPercentile(int percentile) {
    int indexValue = (int) Math.ceil(responseDataList.size() * percentile / (double) 100);
    return responseDataList.get(indexValue - 1).getLatency();
  }

  long getMaxResponse() {
    return responseDataList.get(responseDataList.size() - 1).getLatency();
  }

  int getTotalRequests() {
    return responseDataList.size();
  }

  List<ExecutionResponseData> getExecutionResponseList() {
    return responseDataList;
  }
}
