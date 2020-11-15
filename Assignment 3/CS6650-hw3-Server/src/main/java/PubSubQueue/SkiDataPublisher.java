package PubSubQueue;

import com.rabbitmq.client.Channel;

import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import RabbitMQConnectionPool.RBMQChannelPool;
import model.LiftRide;

public class SkiDataPublisher {

  LiftRide liftRide;
  private final static String QUEUE_NAME = "LiftRideWriteQueue";
  private final static int NUM_MESSAGES_PER_THREAD = 10;


  SkiDataPublisher(LiftRide liftRide) {
    this.liftRide = liftRide;
  }

  public static void main(String[] argv) {
    final RBMQChannelPool rbmqChannelPool = new RBMQChannelPool();
    try {
      Runnable runnable = new Runnable() {
        @Override
        public void run() {
          try {
            // channel per thread
            Channel channel = rbmqChannelPool.getChannel();
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            for (int i = 0; i < NUM_MESSAGES_PER_THREAD; i++) {
              LiftRide liftRide = new LiftRide("ThreadResort22", 1, 2, 33, 5);
              byte[] yourBytes = SerializationUtils.serialize(liftRide);
              channel.basicPublish("", QUEUE_NAME, null, yourBytes);
            }
            channel.close();
            System.out.println(" [All Messages  Sent '");
          } catch (TimeoutException | IOException ex) {
            Logger.getLogger(SkiDataPublisher.class.getName()).log(Level.SEVERE, null, ex);
          }
        }
      };

      // start threads and wait for completion
      Thread t1 = new Thread(runnable);
      Thread t2 = new Thread(runnable);
      t1.start();
      t2.start();

      t1.join();
      t2.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
