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

public class SkiDataConsumer1 {

  private final static String QUEUE_NAME = "LiftRideWriteQueue";
  private static LiftRideDao liftRideDao = new LiftRideDao();

  public static void main(String[] argv) {

    final RBMQChannelPool rbmqChannelPool = new RBMQChannelPool();
    Runnable runnable = new Runnable() {

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
              e.printStackTrace();
            }
            System.out.println("Data saved to DB:" + liftRide.toString());
          };
          // process messages
          channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> {
          });
        } catch (IOException ex) {
          Logger.getLogger(SkiDataConsumer1.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    };

    // start threads and block to receive messages
//    Thread recv1 = new Thread(runnable);
//    Thread recv2 = new Thread(runnable);
    for(int i=0; i < 20; i++){
      new Thread(runnable).start();
    }
//
//    recv1.start();
//    recv2.start();
  }
}
