import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

  public void submitToThreadPhaseExecution(ExecutorService threadPool, PhaseExecutionParameter phaseExecutionParameter, int currentThreads,
                                           double countDownThreshold) throws InterruptedException {
    CountDownLatch latch = new CountDownLatch((int)(phaseExecutionParameter.getThreadsToExecute()*countDownThreshold));

    for (int i = 0; i < phaseExecutionParameter.getThreadsToExecute(); i++) {
      threadPool.submit(() -> {
          new ThreadPhaseExecution(parameters, phaseExecutionParameter, this, latch);
      });
    }
    // wait for the latch to be decremented
    latch.await();
  }

  @Override
  public void run() {

    //Create all threads
    ExecutorService WORKER_THREAD_POOL
            = Executors.newFixedThreadPool(parameters.maxThreads/4 + parameters.maxThreads + parameters.maxThreads/4);

    //Execute Phase 1
    try {
      PhaseExecutionParameter phase1ExecutionParameter  = new PhaseExecutionParameter(5,100,0,
              parameters.getNumSkiers()*4/parameters.getMaxThreads(),0,90,parameters.numLifts, parameters.getMaxThreads()/4);
      submitToThreadPhaseExecution(WORKER_THREAD_POOL, phase1ExecutionParameter, parameters.getMaxThreads()/4, 1/10);
    } catch (InterruptedException e) {
      logger.error("Thread execution failed : " + e.getMessage() + "with reason : " + e.getCause());
    }


    //Execute Phase 2
    try {
      PhaseExecutionParameter phase1ExecutionParameter2  = new PhaseExecutionParameter(5,100,0,
              parameters.getNumSkiers()*4/parameters.getMaxThreads(),91,360,parameters.numLifts, parameters.getMaxThreads());
      submitToThreadPhaseExecution(WORKER_THREAD_POOL, phase1ExecutionParameter2, parameters.getMaxThreads()/4, 1/10);
    } catch (InterruptedException e) {
      logger.error("Thread execution failed : " + e.getMessage() + "with reason : " + e.getCause());
    }

    //Execute Phase 3
    try {
      PhaseExecutionParameter phase1ExecutionParameter3  = new PhaseExecutionParameter(10,100,0,
              parameters.getNumSkiers()*4/parameters.getMaxThreads(),361,420,parameters.numLifts, parameters.getMaxThreads()/4);
      submitToThreadPhaseExecution(WORKER_THREAD_POOL, phase1ExecutionParameter3, parameters.getMaxThreads()/4, 1/10);
    } catch (InterruptedException e) {
      logger.error("Thread execution failed : " + e.getMessage() + "with reason : " + e.getCause());
    }
  }
}
