

import com.opencsv.CSVWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class RestApiClientMain {

  //  final static String basePath = "http://localhost:8081/CS6650_hw2_Server_deploy";
  private static Logger logger = LoggerFactory.getLogger(RestApiClientMain.class);
  // private static SkierVertical Resorts;

  public static void main(String[] args) {

    try {
      java.io.File propertiesFile = null;
      InputStream input = null;
      if (args.length > 1 && args[0].equals("-f")) {
        propertiesFile = new java.io.File(args[1]);
        System.out.println("Reading file passed as input: " + args[1]);
        input = new java.io.FileInputStream(propertiesFile);
      } else {
        System.out.println("Reading default config.properties");
        input = RestApiClientMain.class.getClassLoader().getResourceAsStream("config.properties");
      }
      Properties prop = new Properties();
      if (input == null) {
        System.out.println("Sorry, unable to find config.properties");
        logger.error("Sorry, unable to find config.properties");
        return;
      }
      //load a properties file from class path, inside static method
      prop.load(input);
      ConfigParameters parameters = new ConfigParameters(prop);
      if (!(parameters.getNumLifts() >= 5 && parameters.getNumLifts() <= 60)) {
        System.out.println("Execution abort! Number of Lifts should be in range 5-60");
        return;
      }

      // Starting the producer
      BlockingQueue blockingQueue = new LinkedBlockingDeque();
      ExecutionResponseDataProducer executionResponseDataProducer = new ExecutionResponseDataProducer(blockingQueue, null, parameters);
      Thread producerThread = new Thread(executionResponseDataProducer);
      producerThread.start();

      //Create the CSV file that will be used later for appending the data
      File csvFile = new File(Constants.PERFORMANCE_METRICS_CSV);
      String uri = csvFile.getAbsoluteFile().getAbsolutePath();
      CSVWriter csvWriter = new CSVWriter(new FileWriter(csvFile));
      csvWriter.writeNext(new String[]{"latency", "requestType", "responseCode", "startTime"});
      csvWriter.close();

      //Starting the consumer
      CSVWriterConsumer csvWriterConsumer = new CSVWriterConsumer(blockingQueue, CompletableFuture.completedFuture(null), Paths.get(uri));
      ExecutorService ConsumerThreadPool = Executors.newFixedThreadPool(10);
      ConsumerThreadPool.submit(csvWriterConsumer);

      ConsumerThreadPool.shutdown();

      try {
        if (!ConsumerThreadPool.awaitTermination(1, TimeUnit.HOURS)) {
          ConsumerThreadPool.shutdownNow();
        }
      } catch (InterruptedException ex) {
        ConsumerThreadPool.shutdownNow();
        Thread.currentThread().interrupt();
      }


      // Thread consumerThread = new Thread(csvWriterConsumer);
      // consumerThread.start();
      System.out.println("Control came back to main");
    } catch (IOException ex) {
      System.out.println("The config.properties not present");
      logger.error("The config.properties not present");
    }
  }
}
