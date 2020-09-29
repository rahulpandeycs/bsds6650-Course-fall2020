import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadManager implements Runnable {

  ConfigParameters parameters;
  SharedGlobalCount globalCountFail;
  SharedGlobalCount globalCountSuccess;

  private static Logger logger = LoggerFactory.getLogger(ThreadManager.class);

  public ThreadManager(ConfigParameters parameters) {
    this.parameters = parameters;
    this.globalCountFail = new SharedGlobalCount();
    this.globalCountSuccess = new SharedGlobalCount();
  }

  private void submitToThreadPhaseExecution(ExecutorService threadPool, PhaseExecutionParameter phaseExecutionParameter,
                                           double countDownThreshold) throws InterruptedException {

    CountDownLatch latch = new CountDownLatch((int)(phaseExecutionParameter.getThreadsToExecute()*countDownThreshold));

    int startSkierId = 1;
    int range = parameters.numSkiers/phaseExecutionParameter.getThreadsToExecute();

    for (int i = 0; i < phaseExecutionParameter.getThreadsToExecute(); i++) {
      phaseExecutionParameter.setStartSkierId(startSkierId);
      phaseExecutionParameter.setEndSkierId(startSkierId+range-1);
      Runnable phaseThread = new ThreadPhaseExecution(parameters, phaseExecutionParameter, this, latch);
//      threadPool.execute(() -> {
//          new ThreadPhaseExecution(parameters, phaseExecutionParameter, this, latch);
//      });
      threadPool.submit(phaseThread);
      startSkierId+=range;
    }
    // wait for the latch to be decremented
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
    } catch (InterruptedException e) {
      logger.error("Thread execution failed : " + e.getMessage() + "with reason : " + e.getCause());
    }


    //Execute Phase 2
    try {
      PhaseExecutionParameter phase1ExecutionParameter2  = new PhaseExecutionParameter(5,100,0,
              parameters.getNumSkiers()*4/parameters.getMaxThreads(),91,360,parameters.numLifts, parameters.getMaxThreads());
      submitToThreadPhaseExecution(WORKER_THREAD_POOL, phase1ExecutionParameter2, 1/10);
    } catch (InterruptedException e) {
      logger.error("Thread execution failed : " + e.getMessage() + "with reason : " + e.getCause());
    }

    //Execute Phase 3
    try {
      PhaseExecutionParameter phase1ExecutionParameter3  = new PhaseExecutionParameter(10,100,0,
              parameters.getNumSkiers()*4/parameters.getMaxThreads(),361,420,parameters.numLifts, parameters.getMaxThreads()/4);
      submitToThreadPhaseExecution(WORKER_THREAD_POOL, phase1ExecutionParameter3, 1/10);
    } catch (InterruptedException e) {
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

    System.out.println("Number of successful requests sent : " + globalCountSuccess.counter);
    System.out.println("Number of unsuccessful requests :" + globalCountFail.counter);
    System.out.println("The total run time (wall time) :" + (endTime - startTime));

    int totalRequests = globalCountSuccess.counter + globalCountFail.counter;
    System.out.println("Throughput: " + totalRequests/(endTime - startTime));
  }
}
