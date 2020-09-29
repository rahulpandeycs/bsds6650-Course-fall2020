import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ThreadManager implements Runnable {

  ConfigParameters parameters;
  SharedGlobalCount globalCountFail;
  SharedGlobalCount globalCountSuccess;
  List<ExecutionResponseData> globalExecutionResponseData;

  private static Logger logger = LoggerFactory.getLogger(ThreadManager.class);

  public ThreadManager(ConfigParameters parameters) {
    this.parameters = parameters;
    this.globalCountFail = new SharedGlobalCount();
    this.globalCountSuccess = new SharedGlobalCount();
    this.globalExecutionResponseData = new ArrayList<>();
  }

  private void submitToThreadPhaseExecution(ExecutorService threadPool, PhaseExecutionParameter phaseExecutionParameter,
                                           double countDownThreshold) throws InterruptedException, ExecutionException {

    CountDownLatch latch = new CountDownLatch((int)(phaseExecutionParameter.getThreadsToExecute()*countDownThreshold));

    int startSkierId = 1;
    int range = parameters.numSkiers/phaseExecutionParameter.getThreadsToExecute();

    for (int i = 0; i < phaseExecutionParameter.getThreadsToExecute(); i++) {
      phaseExecutionParameter.setStartSkierId(startSkierId);
      phaseExecutionParameter.setEndSkierId(startSkierId+range-1);
      Callable<List<ExecutionResponseData>> phaseThread = new ThreadPhaseExecution(parameters, phaseExecutionParameter, this, latch);
      Future<List<ExecutionResponseData>> futureExecutionResponseData = threadPool.submit(phaseThread);
      List<ExecutionResponseData> executionResponseData = futureExecutionResponseData.get();
      globalExecutionResponseData.addAll(executionResponseData);
      startSkierId+=range;
    }
    // wait for the latch to be decremented for to threshold value.
    latch.await();
  }

  @Override
  public void run() {

    long startTime = System.currentTimeMillis();
    //Create all threads
    ExecutorService WORKER_THREAD_POOL
            = Executors.newFixedThreadPool(parameters.maxThreads/4 + parameters.maxThreads + parameters.maxThreads/4);
    //Execute Phase 1
    try {
      PhaseExecutionParameter phase1ExecutionParameter  = new PhaseExecutionParameter(5,100,0,
              parameters.getNumSkiers()*4/parameters.getMaxThreads(),0,90,parameters.numLifts, parameters.getMaxThreads()/4);
      submitToThreadPhaseExecution(WORKER_THREAD_POOL, phase1ExecutionParameter, 1/10);
    } catch (InterruptedException | ExecutionException e) {
      logger.error("Thread execution failed : " + e.getMessage() + "with reason : " + e.getCause());
    }


    //Execute Phase 2
    try {
      PhaseExecutionParameter phase1ExecutionParameter2  = new PhaseExecutionParameter(5,100,0,
              parameters.getNumSkiers()*4/parameters.getMaxThreads(),91,360,parameters.numLifts, parameters.getMaxThreads());
      submitToThreadPhaseExecution(WORKER_THREAD_POOL, phase1ExecutionParameter2, 1/10);
    } catch (InterruptedException | ExecutionException e) {
      logger.error("Thread execution failed : " + e.getMessage() + "with reason : " + e.getCause());
    }

    //Execute Phase 3
    try {
      PhaseExecutionParameter phase1ExecutionParameter3  = new PhaseExecutionParameter(10,100,0,
              parameters.getNumSkiers()*4/parameters.getMaxThreads(),361,420,parameters.numLifts, parameters.getMaxThreads()/4);
      submitToThreadPhaseExecution(WORKER_THREAD_POOL, phase1ExecutionParameter3, 1/10);
    } catch (InterruptedException | ExecutionException e) {
      logger.error("Thread execution failed : " + e.getMessage() + "with reason : " + e.getCause());
    }

    WORKER_THREAD_POOL.shutdown();

    try {
      if (!WORKER_THREAD_POOL.awaitTermination(1, TimeUnit.HOURS)) {
        WORKER_THREAD_POOL.shutdownNow();
      }
    } catch (InterruptedException ex) {
      WORKER_THREAD_POOL.shutdownNow();
      Thread.currentThread().interrupt();
    }

    long endTime = System.currentTimeMillis();

    PerformanceMetrics performanceMetrics = new PerformanceMetrics(globalExecutionResponseData);
    int totalRequests = globalExecutionResponseData.size();

    System.out.println("Mean responseTime:" + performanceMetrics.getMeanResponseTime());
    System.out.println("Mean responseTime:" + performanceMetrics.getMedianResponseTime());
    System.out.println("The total run time (wall time) :" + (endTime - startTime));
    System.out.println("Throughput: " + totalRequests/(endTime - startTime));
    System.out.println("p99 (99th percentile) response time :" + performanceMetrics.getP99Percentile());
    System.out.println("Max response time:" + performanceMetrics.getMaxResponse());


  }
}
