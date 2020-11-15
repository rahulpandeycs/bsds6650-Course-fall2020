package PubSubQueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import RabbitMQConnectionPool.RBMQChannelPool;
import dao.LiftRideDao;
import exception.SkierServerException;
import model.LiftRide;

public class SkiDataConsumer implements Runnable {

  private final static String QUEUE_NAME = "LiftRideWriteQueue";
  private static LiftRideDao liftRideDao = new LiftRideDao();
  final RBMQChannelPool rbmqChannelPool = new RBMQChannelPool();

  public SkiDataConsumer(){
  }

  @Override
  public void run() {
    try {
      final Channel channel = rbmqChannelPool.getChannel();
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
          //Logging the save exceptions
          Logger.getLogger(SkiDataConsumer.class.getName()).log(Level.SEVERE, null, e);
        }
        System.out.println("Data saved to DB:" + liftRide.toString());
      };

      // process messages
      channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> {
      });
    } catch (IOException ex) {
      Logger.getLogger(SkiDataConsumer.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}
