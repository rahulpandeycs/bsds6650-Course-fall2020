package RabbitMQConnectionPool;

import com.rabbitmq.client.Channel;

import org.apache.commons.pool2.ObjectPool;

import java.io.IOException;

public class RBMQChannelUtil {

  private final ObjectPool<Channel> pool;

  public RBMQChannelUtil(ObjectPool<Channel> pool) {
    this.pool = pool;
  }

  public Channel getChannel() throws IOException {
    Channel channel = null;
    try {
      channel = pool.borrowObject();
      return channel;
    } catch (IOException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Unable to borrow buffer from pool" + e.toString());
    }
  }

  public void returnChannel(Channel channel) throws Exception {
    if (null != channel) {
      pool.returnObject(channel);
    }
  }
}
