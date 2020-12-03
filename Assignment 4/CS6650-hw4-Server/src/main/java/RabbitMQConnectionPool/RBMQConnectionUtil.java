package RabbitMQConnectionPool;

import com.rabbitmq.client.Connection;

import org.apache.commons.pool2.ObjectPool;

import java.io.IOException;

public class RBMQConnectionUtil {

  private final ObjectPool<Connection> pool;

  public RBMQConnectionUtil(ObjectPool<Connection> pool) {
    this.pool = pool;
  }

  public Connection getConnection() throws IOException {
    Connection connection = null;
    try {
      connection = pool.borrowObject();
      return connection;
    } catch (IOException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Unable to borrow buffer from pool" + e.toString());
    }
  }

  public void returnConnection(Connection connection) throws Exception {
    if (null != connection) {
      pool.returnObject(connection);
    }
  }
}
