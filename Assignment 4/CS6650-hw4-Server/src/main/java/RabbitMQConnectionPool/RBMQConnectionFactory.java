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

public class RBMQConnectionFactory extends BasePooledObjectFactory<Connection> {

  @Override
  public Connection create() throws IOException, TimeoutException {
    ConnectionFactory factory;
    try {
      factory = new ConnectionFactory();
      factory.setHost("localhost");
    } catch (Exception e) {
      throw new RBMQChannelException("Exception Occurred", e);
    }
    return factory.newConnection();
  }

  /**
   * Use the default PooledObject implementation.
   */
  @Override
  public PooledObject<Connection> wrap(Connection channel) {
    return new DefaultPooledObject<Connection>(channel);
  }

  /**
   * When an object is returned to the pool, clear the buffer.
   */
  @Override
  public void passivateObject(PooledObject<Connection> pooledObject) throws IOException {
    pooledObject.getObject().abort();
  }

  // for all other methods, the no-op implementation
  // in BasePooledObjectFactory will suffice
}
