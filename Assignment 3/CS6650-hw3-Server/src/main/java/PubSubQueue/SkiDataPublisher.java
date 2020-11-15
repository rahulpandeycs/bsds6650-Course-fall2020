package PubSubQueue;

import com.rabbitmq.client.Channel;

import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import RabbitMQConnectionPool.RBMQChannelPool;
import model.LiftRide;

public class SkiDataPublisher implements Runnable {

  LiftRide liftRide;
  final RBMQChannelPool rbmqChannelPool;
  private final static String QUEUE_NAME = "LiftRideWriteQueue";

  public SkiDataPublisher(LiftRide liftRide) {
    this.liftRide = liftRide;
    this.rbmqChannelPool = new RBMQChannelPool();
  }

  @Override
  public void run() {
    try {
      // channel per thread
      Channel channel = rbmqChannelPool.getChannel();

      //Second parameter to make queue durable
      channel.queueDeclare(QUEUE_NAME, true, false, false, null);
      byte[] yourBytes = SerializationUtils.serialize(liftRide);
      channel.basicPublish("", QUEUE_NAME, null, yourBytes);
      channel.close();
      System.out.println(" [All Messages  Sent '");
    } catch (TimeoutException | IOException ex) {
      Logger.getLogger(SkiDataPublisher.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}
