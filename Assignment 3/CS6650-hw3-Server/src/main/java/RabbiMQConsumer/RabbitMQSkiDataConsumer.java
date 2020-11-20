package RabbiMQConsumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import RabbitMQConnectionPool.RBMQChannelFactory;
import RabbitMQConnectionPool.RBMQChannelPool;
import model.LiftRide;

public class RabbitMQSkiDataConsumer {

//  private final static String QUEUE_NAME = "LiftRideWriteQueue";
  private final static String QUEUE_NAME = "LiftRideWriteNonDurableQueue";
  private static RabbitMQConsumerDao liftRideDao = new RabbitMQConsumerDao();
  public static GenericObjectPoolConfig defaultConfig;

  static {
    defaultConfig = new GenericObjectPoolConfig();
    defaultConfig.setMaxTotal(20);
    defaultConfig.setMinIdle(2);
    defaultConfig.setMaxIdle(5);
    defaultConfig.setBlockWhenExhausted(false);
  }

  public static void main(String[] argv) throws IOException, TimeoutException {

    //Connection to RabbitMQrabbitmqctl set_permissions -p "custom-vhost" "username" ".*" ".*" ".*"
    ConnectionFactory factory = new ConnectionFactory();
    factory.setUsername("ubuntu");
    factory.setPassword("password");
    factory.setVirtualHost("/");
    factory.setHost("ec2-52-91-132-255.compute-1.amazonaws.com");
    factory.setPort(5672);

//    // Local
//    factory.setHost("localhost");
    Connection connection = factory.newConnection();

    RBMQChannelPool rbmqConnectionUtil = new RBMQChannelPool(new GenericObjectPool<Channel>(new RBMQChannelFactory(connection), defaultConfig));
    Runnable runnable = new Runnable() {

      @Override
      public void run() {
        try {
          final Channel channel = rbmqConnectionUtil.getChannel();
          channel.queueDeclare(QUEUE_NAME, false, false, false, null);

          // max one message per receiver
          channel.basicQos(1);
          System.out.println(" [*] Thread waiting for messages. To exit press CTRL+C");

          DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            LiftRide liftRide = SerializationUtils.deserialize(delivery.getBody());
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            try {
              liftRideDao.updateLiftRide(liftRide);
            } catch (SQLException throwables) {
              retrySaveToDB(liftRide, 1);
            }
            System.out.println(" Data saved!");
          };

          // process messages
          channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> {
          });

          //  channel.close();
        } catch (IOException ex) {
          Logger.getLogger(RabbitMQSkiDataConsumer.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    };

    // start threads and block to receive messages
    for (int i = 0; i < 18; i++) {
      new Thread(runnable).start();
    }
  }

  private static void retrySaveToDB(LiftRide liftRide, int count) {
    if (count > 3) return;
    try {
      liftRideDao.updateLiftRide(liftRide);
    } catch (SQLException throwables) {
      retrySaveToDB(liftRide, count + 1);
    }
  }
}
