package PubSubQueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import RabbitMQConnectionPool.RBMQChannelFactory;
import RabbitMQConnectionPool.RBMQChannelPool;
import dao.LiftRideDao;
import exception.SkierServerException;
import model.LiftRide;

public class SkiDataConsumerNotUsed {

  private final static String QUEUE_NAME = "LiftRideWriteQueue";
  private static LiftRideDao liftRideDao = new LiftRideDao();
  public static GenericObjectPoolConfig defaultConfig;

  static {
    defaultConfig = new GenericObjectPoolConfig();
    defaultConfig.setMaxTotal(20);
    defaultConfig.setMinIdle(2);
    defaultConfig.setMaxIdle(5);
    defaultConfig.setBlockWhenExhausted(false);
  }

  public static void main(String[] argv) throws IOException, TimeoutException {

    //Connection to RabbitMQ
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    Connection connection = factory.newConnection();

    RBMQChannelPool rbmqConnectionUtil = new RBMQChannelPool(new GenericObjectPool<Channel>(new RBMQChannelFactory(connection), defaultConfig));
//    RBMQConnectionUtil rbmqConnectionUtil = new RBMQConnectionUtil(new GenericObjectPool<Channel>(new RBMQConnectionFactory()));
    Runnable runnable = new Runnable() {

      @Override
      public void run() {
        try {
          final Channel channel = rbmqConnectionUtil.getChannel();
          channel.queueDeclare(QUEUE_NAME, true, false, false, null);

          // max one message per receiver
          channel.basicQos(1);
          System.out.println(" [*] Thread waiting for messages. To exit press CTRL+C");

          DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            LiftRide liftRide = SerializationUtils.deserialize(delivery.getBody());
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            try {
              liftRideDao.saveLiftRide(liftRide);
            } catch (SkierServerException e) {
              Logger.getLogger(SkiDataConsumerNotUsed.class.getName()).log(Level.SEVERE, "Retry will be attempted", e);
              int retryCount = 2;
              try {
                Thread.sleep(1000);
                retryCount--;
                retrySaveToDB(liftRide);
              } catch (SkierServerException | InterruptedException skierServerException) {
                Logger.getLogger(SkiDataConsumerNotUsed.class.getName()).log(Level.SEVERE, "Retry will be attempted, count: " + retryCount, e);
                if (retryCount > 0) {
                  retryCount--;
                  try {
                    Thread.sleep(1000);
                    retrySaveToDB(liftRide);
                  } catch (SkierServerException | InterruptedException serverException) {
                    Logger.getLogger(SkiDataConsumerNotUsed.class.getName()).log(Level.SEVERE, null, serverException);
                    serverException.printStackTrace();
                  }
                }
                skierServerException.printStackTrace();
              }
              Logger.getLogger(SkiDataConsumerNotUsed.class.getName()).log(Level.SEVERE, "No Retry remains", e);
              e.printStackTrace();
            }
            System.out.println("Data saved to DB:" + liftRide.toString());
          };

          // process messages
          channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> {
          });

          //  channel.close();
        } catch (IOException ex) {
          Logger.getLogger(SkiDataConsumerNotUsed.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    };

    // start threads and block to receive messages
    for (int i = 0; i < 5; i++) {
      new Thread(runnable).start();
    }
  }

  private static void retrySaveToDB(LiftRide liftRide) throws SkierServerException {
    liftRideDao.saveLiftRide(liftRide);
  }
}
