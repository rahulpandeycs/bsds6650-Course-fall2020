import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;

public class ExecutionResponseDataProducer implements Runnable {

  private BlockingQueue<Future<List<ExecutionResponseData>>> blockingQueue;
  private final ExecutionResponseData processTerminationIndicator;
  private ConfigParameters parameters;

  public ExecutionResponseDataProducer(BlockingQueue<Future<List<ExecutionResponseData>>> blockingQueue,
                                       ExecutionResponseData processTerminationIndicator, ConfigParameters parameters) {
    this.blockingQueue = blockingQueue;
    this.processTerminationIndicator = processTerminationIndicator;
    this.parameters = parameters;
  }

  @Override
  public void run() {
    ThreadManager threadManager = new ThreadManager(parameters, blockingQueue);
    Thread producerThread = new Thread(threadManager);
    producerThread.start();
  }
}
