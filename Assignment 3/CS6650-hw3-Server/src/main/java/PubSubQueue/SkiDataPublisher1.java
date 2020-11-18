//package PubSubQueue;
//
//import com.rabbitmq.client.Channel;
//
//import org.apache.commons.lang3.SerializationUtils;
//import org.apache.commons.pool2.impl.GenericObjectPool;
//import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
//
//import java.io.IOException;
//import java.util.concurrent.TimeoutException;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//import RabbitMQConnectionPool.RBMQChannelFactory;
//import RabbitMQConnectionPool.RBMQChannelPool;
//import model.LiftRide;
//
//public class SkiDataPublisher1 {
//
//  LiftRide liftRide;
//  private final static String QUEUE_NAME = "LiftRideWriteQueue";
//  private final static int NUM_MESSAGES_PER_THREAD = 10;
//  public static GenericObjectPoolConfig defaultConfig;
//
//  static {
//    defaultConfig = new GenericObjectPoolConfig();
//    defaultConfig.setMaxTotal(20);
//    defaultConfig.setMinIdle(2);
//    defaultConfig.setMaxIdle(5);
//    defaultConfig.setBlockWhenExhausted(false);
//  }
//
//
//  SkiDataPublisher1(LiftRide liftRide) {
//    this.liftRide = liftRide;
//  }
//
//  public static void main(String[] argv) {
////    final RBMQChannelPool rbmqChannelPool = new RBMQChannelPool();
//    RBMQChannelPool rbmqConnectionUtil = new RBMQChannelPool(new GenericObjectPool<Channel>(new RBMQChannelFactory(),defaultConfig));
//    try {
//      Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//          try {
//            // channel per thread
////            Channel channel = rbmqChannelPool.getChannel();
//            Channel channel = rbmqConnectionUtil.getChannel();
//            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
//            for (int i = 0; i < NUM_MESSAGES_PER_THREAD; i++) {
//              LiftRide liftRide = new LiftRide("ThreadResort22", 1, 2, 33, 5);
//              byte[] yourBytes = SerializationUtils.serialize(liftRide);
//              channel.basicPublish("", QUEUE_NAME, null, yourBytes);
//            }
//            channel.close();
//            rbmqConnectionUtil.returnChannel(channel);
//            System.out.println(" [All Messages  Sent '");
//          } catch (TimeoutException | IOException ex) {
//            Logger.getLogger(SkiDataPublisher1.class.getName()).log(Level.SEVERE, null, ex);
//          } catch (Exception e) {
//            Logger.getLogger(SkiDataPublisher1.class.getName()).log(Level.SEVERE, null, e);
//          }
//        }
//      };
//
//      // start threads and wait for completion
//      Thread t1 = new Thread(runnable);
//      Thread t2 = new Thread(runnable);
//      t1.start();
//      t2.start();
//
//      t1.join();
//      t2.join();
//    } catch (InterruptedException e) {
//      e.printStackTrace();
//    }
//  }
//}
