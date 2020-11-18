package PubSubQueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import RabbitMQConnectionPool.RBMQChannelPool;
import exception.RBMQChannelException;
import model.LiftRide;

//public class SkiDataPublisher implements Runnable {
public class SkiDataPublisher {

  private LiftRide liftRide;
  private final static String QUEUE_NAME = "LiftRideWriteQueue";
  private Connection connection;
  RBMQChannelPool channelPool;
//  private RBMQConnectionUtil rbmqConnectionPool;

  public static GenericObjectPoolConfig defaultConfig;

  static {
    defaultConfig = new GenericObjectPoolConfig();
    defaultConfig.setMaxTotal(20);
    defaultConfig.setMinIdle(2);
    defaultConfig.setMaxIdle(5);
    defaultConfig.setBlockWhenExhausted(false);
  }

//  public SkiDataPublisher(LiftRide liftRide, Connection connection) {
//    this.liftRide = liftRide;
//    this.connection = connection;
//  }

  public SkiDataPublisher(LiftRide liftRide, RBMQChannelPool channelPool) {
    this.liftRide = liftRide;
    this.channelPool = channelPool;
  }

  //  @Override
  public void doPublish() throws TimeoutException, IOException {
    try {
      // channel per thread
      Channel channel = channelPool.getChannel();

      //Second parameter to make queue durable
      channel.queueDeclare(QUEUE_NAME, true, false, false, null);
      byte[] yourBytes = SerializationUtils.serialize(liftRide);
      channel.basicPublish("", QUEUE_NAME, null, yourBytes);
      Logger.getLogger(SkiDataPublisher.class.getName()).log(Level.INFO, "Message sent");
      channelPool.returnChannel(channel);
    } catch (TimeoutException | IOException ex) {
      Logger.getLogger(SkiDataPublisher.class.getName()).log(Level.SEVERE, null, ex);
      throw new RBMQChannelException("The publish operation failed with reason: ", ex);
    } catch (Exception e) {
      Logger.getLogger(SkiDataPublisher.class.getName()).log(Level.SEVERE, null, e);
      throw new RBMQChannelException("The publish operation failed with reason: ", e);
    }
  }
}