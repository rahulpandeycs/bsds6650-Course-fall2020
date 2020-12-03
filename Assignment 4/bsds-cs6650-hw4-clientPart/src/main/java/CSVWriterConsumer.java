import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class CSVWriterConsumer implements Runnable {

  private BlockingQueue<Future<List<ExecutionResponseData>>> blockingQueue;
  private final Future<List<ExecutionResponseData>> processTerminationIndicator;
  private final Path path;

  public CSVWriterConsumer(BlockingQueue<Future<List<ExecutionResponseData>>> blockingQueue,
                           Future<List<ExecutionResponseData>> processTerminationIndicator,
                           Path path) {
    this.blockingQueue = blockingQueue;
    this.processTerminationIndicator = processTerminationIndicator;
    this.path = path;
  }

  @Override
  public void run() {
    try {
      System.out.println("Entered into Consumer for writing file!");
      while (true) {
        if (!blockingQueue.isEmpty()) {
          Future<List<ExecutionResponseData>> responseData = blockingQueue.take();
          boolean exitConsumer = writeDataToCSVAndExit(responseData.get(), path);
          if (exitConsumer) {
            System.out.println("Exiting now: Data stored, Results are stored at: " + path);
            return;
          }
        }
      }
    } catch (ExecutionException e) {
      e.printStackTrace();
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  private boolean writeDataToCSVAndExit(List<ExecutionResponseData> executionResponseData, Path path) throws IOException {
    boolean exitConsumer = false;
    CSVWriter writer = new CSVWriter(new FileWriter(path.toString(), true));
    for (ExecutionResponseData data : executionResponseData) {
      if (!data.getRequestType().equals("EXIT")) {
        String[] dataArray = new String[]{String.valueOf(data.getLatency()), data.getRequestType(),
                String.valueOf(data.getResponseCode()), String.valueOf(data.getStartTime())};
        writer.writeNext(dataArray);
      } else {
        exitConsumer = true;
      }
    }
    writer.close();
    return exitConsumer;
  }
}
