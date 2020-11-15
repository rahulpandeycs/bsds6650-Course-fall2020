package RabbitMQConnectionPool;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import exception.RBMQChannelException;

public class RBMQChannelFactory extends BasePooledObjectFactory<Channel> {

  @Override
  public Channel create() throws IOException {
    Connection connection;
    try {
      ConnectionFactory factory = new ConnectionFactory();
      factory.setHost("localhost");
      connection = factory.newConnection();
    } catch (Exception e) {
      throw new RBMQChannelException("Exception Occurred", e);
    }
    return connection.createChannel();
  }

  /**
   * Use the default PooledObject implementation.
   */
  @Override
  public PooledObject<Channel> wrap(Channel channel) {
    return new DefaultPooledObject<Channel>(channel);
  }

  /**
   * When an object is returned to the pool, clear the buffer.
   */
  @Override
  public void passivateObject(PooledObject<Channel> pooledObject) throws IOException {
    pooledObject.getObject().abort();
  }

  // for all other methods, the no-op implementation
  // in BasePooledObjectFactory will suffice
}
