import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.io.IOException;

public class RBMQChannelFactory extends BasePooledObjectFactory<Channel> {

  private Connection connection;

  public RBMQChannelFactory(Connection connection) {
    this.connection = connection;
  }

  @Override
  public Channel create() throws IOException {
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
//    pooledObject.getObject();
  }

  // for all other methods, the no-op implementation
  // in BasePooledObjectFactory will suffice
}
