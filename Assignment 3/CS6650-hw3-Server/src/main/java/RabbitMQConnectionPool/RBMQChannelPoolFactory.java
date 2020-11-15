package RabbitMQConnectionPool;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import exception.RBMQChannelException;

public class RBMQChannelPoolFactory implements PooledObjectFactory<Channel> {

  private Connection connection;

  public RBMQChannelPoolFactory() {
    this(null);
  }

  public RBMQChannelPoolFactory(String uri) {
    try {
      ConnectionFactory factory = new ConnectionFactory();
      if (uri != null) {
        //factory.setUri(uri);
        factory.setHost(uri);
      }
      connection = factory.newConnection();
    } catch (Exception e) {
      throw new RBMQChannelException("Exception Occurred", e);
    }
  }

  public PooledObject<Channel> makeObject() throws Exception {
    return new DefaultPooledObject<Channel>(connection.createChannel());
  }

  public void destroyObject(PooledObject<Channel> pooledObject) throws Exception {
    final Channel channel = pooledObject.getObject();
    if (channel.isOpen()) {
      try {
        channel.close();
      } catch (Exception e) {
      }
    }
  }

  public boolean validateObject(PooledObject<Channel> pooledObject) {
    final Channel channel = pooledObject.getObject();
    return channel.isOpen();
  }

  public void activateObject(PooledObject<Channel> pooledObject) throws Exception {
  }

  public void passivateObject(PooledObject<Channel> pooledObject) throws Exception {
  }
}
