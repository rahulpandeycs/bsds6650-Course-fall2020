import com.google.gson.Gson;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.http.HttpStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;

import PubSubQueue.SkiDataPublisher;
import RabbitMQConnectionPool.RBMQChannelFactory;
import RabbitMQConnectionPool.RBMQChannelPool;
import dao.LiftRideDao;
import dao.SkierVerticalDao;
import exception.SkierServerException;
import model.LiftRide;
import model.ResponseMsg;
import model.SkierVertical;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@WebServlet(name = "SkierServlet")
public class SkiersServlet extends javax.servlet.http.HttpServlet {

  private Gson gson = new Gson();
  private static final long serialVersionUID = 1L;
  private LiftRideDao liftRideDao;
  private SkierVerticalDao skierVerticalDao;
  //  private RBMQConnectionUtil rbmqConnectionUtil;
  private RBMQChannelPool rbmqChannelPool;
  public static GenericObjectPoolConfig defaultConfig;
  private Connection connection;

  final static JedisPoolConfig poolConfig = buildPoolConfig();
  static JedisPool jedisPool;
  static Map<String, String> redisMap;

  static {
    defaultConfig = new GenericObjectPoolConfig();
    defaultConfig.setMaxTotal(800);
    defaultConfig.setMinIdle(16);
    defaultConfig.setMaxIdle(800);
    defaultConfig.setBlockWhenExhausted(false);
  }


  private static JedisPoolConfig buildPoolConfig() {
    final JedisPoolConfig poolConfig = new JedisPoolConfig();
    poolConfig.setMaxTotal(128);
    poolConfig.setMaxIdle(128);
    poolConfig.setMinIdle(16);
    poolConfig.setTestOnBorrow(true);
    poolConfig.setTestOnReturn(true);
    poolConfig.setTestWhileIdle(true);
    poolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(60).toMillis());
    poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis());
    poolConfig.setNumTestsPerEvictionRun(3);
    poolConfig.setBlockWhenExhausted(true);
    return poolConfig;
  }

  public void init() {
    this.liftRideDao = new LiftRideDao();
    this.skierVerticalDao = new SkierVerticalDao();
//    this.jedisPool = new JedisPool(poolConfig, "localhost", 6379, 4000);
    this.redisMap = new HashMap<>();

    //Creating connection factory to be used
    ConnectionFactory factory = new ConnectionFactory();
    try {

//      localhost
//      factory.setHost("localhost");

      factory.setUsername("ubuntu");
      factory.setPassword("password");
      factory.setVirtualHost("/");
      factory.setHost("ec2-52-91-132-255.compute-1.amazonaws.com");
      factory.setPort(5672);

      connection = factory.newConnection();
    } catch (IOException exception) {
      exception.printStackTrace();
    } catch (TimeoutException e) {
      e.printStackTrace();
    }

    this.rbmqChannelPool = new RBMQChannelPool(new GenericObjectPool<Channel>(new RBMQChannelFactory(connection), defaultConfig));
    //this.rbmqConnectionUtil = new RBMQConnectionUtil(new GenericObjectPool<Connection>(new RBMQConnectionFactory(),defaultConfig));
  }

  protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

    String URI = request.getPathInfo();
    ResponseMsg message = new ResponseMsg("");

    if (!validatePOSTRequest(URI)) {
      response.setStatus(HttpStatus.SC_BAD_REQUEST);
      message.setMessage("Invalid input");
    }

    String requestBody = "";
    LiftRide liftRide = null;
    if ("POST".equalsIgnoreCase(request.getMethod())) {
      BufferedReader reader = request.getReader();
      requestBody = reader.lines().collect(Collectors.joining(System.lineSeparator()));

      try {
        liftRide = new Gson().fromJson(requestBody, LiftRide.class);
      } catch (Exception ex) {
        response.setStatus(HttpStatus.SC_BAD_REQUEST);
        message.setMessage("Incorrect data provided in request!");
      }

      int result = validatePOSTRequestData(liftRide);
      if (result < 0) {
        response.setStatus(HttpStatus.SC_BAD_REQUEST);
        message.setMessage("Complete data not provided!");
      } else {
        try {

          new SkiDataPublisher(liftRide, rbmqChannelPool).doPublish();

        } catch (TimeoutException e) {
          response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
          message.setMessage("Server failed to process request, with reason " + e.getMessage());
        } catch (Exception e) {
          response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
          message.setMessage("Server failed to process request, with reason " + e.getMessage());
        }

//        try (Jedis jedis = jedisPool.getResource()) { //Delete cache on update
//          String recordKey1 = getResortDaySkierIDRedisKey(liftRide.getResortID(), String.valueOf(liftRide.getSkierID()),
//                  String.valueOf(liftRide.getDayID()));
//          String recordKey2 = getSkierIdResortIdRedisKey(liftRide.getResortID(), String.valueOf(liftRide.getSkierID()));
//          if (jedis.exists(recordKey1)) {
//            jedis.del(recordKey1);
//            System.out.println("Cache updated");
//          }
//          if (jedis.exists(recordKey2)) {
//            jedis.del(recordKey2);
//            System.out.println("Cache updated");
//          }
//        }

        String recordKey1 = getResortDaySkierIDRedisKey(liftRide.getResortID(), String.valueOf(liftRide.getSkierID()),
                String.valueOf(liftRide.getDayID()));
        String recordKey2 = getSkierIdResortIdRedisKey(liftRide.getResortID(), String.valueOf(liftRide.getSkierID()));
        if (redisMap.containsKey(recordKey1)) {
          redisMap.remove(recordKey1);
          System.out.println("Cache updated");
        }
        if (redisMap.containsKey(recordKey2)) {
          redisMap.remove(recordKey2);
          System.out.println("Cache updated");
        }

        response.setStatus(HttpStatus.SC_CREATED);
      }
    } else if ("PUT".equalsIgnoreCase(request.getMethod()) || "DELETE".equalsIgnoreCase(request.getMethod())) {
      response.setStatus(HttpStatus.SC_NOT_IMPLEMENTED);
    }

    String jsonMessage = new Gson().toJson(message);
    response.setContentType("application/json");
    PrintWriter out = response.getWriter();

    if (!message.toString().equals(""))
      out.println(jsonMessage);
    else
      out.println(this.gson.toJson(liftRide));
  }

  protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

    String URI = request.getPathInfo();
    ResponseMsg message = new ResponseMsg("");

    //If truncate database
    if (URI.split("/").length == 2 && URI.split("/")[1].equals("truncate")) {
      try {
        liftRideDao.truncateLiftRide();
        response.setStatus(HttpStatus.SC_OK);
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        response.setCharacterEncoding("UTF-8");
        message.setMessage("LiftRide truncated successfully");
        String jsonMessage = new Gson().toJson(message);
        out.println(jsonMessage);
        return;
      } catch (SkierServerException e) {
        response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        message.setMessage("Server failed to process request, with reason " + e.getMessage());
      }
    }

    int validatedResult = validateGetRequest(URI);

    if (validatedResult > 0) {
      response.setStatus(HttpStatus.SC_OK);
    } else if (validatedResult < 0) {
      response.setStatus(HttpStatus.SC_BAD_REQUEST);
      message.setMessage("Invalid inputs supplied for: " + URI);
    } else {
      response.setStatus(HttpStatus.SC_NOT_FOUND);
      message.setMessage("Data not found");
    }

    String jsonMessage = new Gson().toJson(message);
    response.setContentType("application/json");
    PrintWriter out = response.getWriter();
    response.setCharacterEncoding("UTF-8");

    if (!message.toString().equals(""))
      out.println(jsonMessage);
    else {
      String outputJson = "";
      String[] urlSplit = URI.split("/");
      try (Jedis jedis = jedisPool.getResource()) {
        if (urlSplit.length > 3 && urlSplit.length == 6) {
          // Get the total vertical for the skier for the specified ski day
          String skierID = urlSplit[5];
          String resortID = urlSplit[1];
          String dayID = urlSplit[3];
          int totalVert = 0;
          try {
            jedis.isConnected();
            jedis.connect();
            String recordKey = getResortDaySkierIDRedisKey(resortID, skierID, dayID);
//            if (jedis.get(recordKey) != null) { //Get from the cache
//              totalVert = Integer.valueOf(jedis.get(recordKey));
//              System.out.println("GET Served from cache");
//            }
            if (redisMap.containsKey(recordKey)) {
              totalVert = Integer.valueOf(redisMap.get(recordKey));
              System.out.println("GET Served from cache");
            } else {
              totalVert = skierVerticalDao.getTotalVertByResortDaySkierID(resortID, Integer.valueOf(skierID), Integer.valueOf(dayID));
//              jedis.set(recordKey, String.valueOf(totalVert));
              redisMap.put(recordKey,String.valueOf(totalVert));
              System.out.println("GET Saved to cache");
            }
            outputJson = this.gson.toJson(new SkierVertical(resortID, totalVert));
          } catch (SkierServerException e) {
            response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            message.setMessage("Server failed to process request, with reason " + e.getMessage());
          }
        } else if (urlSplit.length == 3) {
          // Get the total vertical for the skier the specified resort.
          String query = request.getQueryString();
          String skierID = urlSplit[1];
          String resortID = query.split("=")[1];
          int totalVert = 0;
          try {
            String recordKey = getSkierIdResortIdRedisKey(resortID, skierID);
            if (jedis.get(recordKey) != null) {
              totalVert = Integer.valueOf(jedis.get(recordKey));
              System.out.println("GET Served from cache");
            } else {
              totalVert = skierVerticalDao.getTotalVertBySkierIdResortId(resortID, Integer.valueOf(skierID));
              jedis.set(recordKey, String.valueOf(totalVert));
              System.out.println("GET Saved to the cache");
            }
            outputJson = this.gson.toJson(new SkierVertical(resortID, totalVert));
          } catch (SkierServerException e) {
            response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            message.setMessage("Server failed to process request, with reason " + e.getMessage());
          }
        }
      } catch (Exception e) {
        System.out.println("Jedis connection failed!");
        e.printStackTrace();
      }
      out.println(outputJson);
    }
  }

  private String getResortDaySkierIDRedisKey(String resortId, String skierID, String DayId) {
    StringBuilder sb = new StringBuilder();
    return sb.append(resortId).append(":").append(skierID).append(":").append(DayId).toString();
  }

  private String getSkierIdResortIdRedisKey(String resortId, String skierId) {
    StringBuilder sb = new StringBuilder();
    return sb.append(resortId).append(":").append(skierId).toString();
  }

  int validateGetRequest(String URI) {
    // /skiers/{resortID}/days/{dayID}/skiers/{skierID}
    String[] splitURI = URI.split("/");
    if (splitURI.length == 3) {
      if (!(splitURI[2].equals("vertical"))) return 0;
      String skierID = splitURI[1];
      try {
        Integer.parseInt(skierID);
      } catch (NumberFormatException ex) {
        return 0;
      }
    } else if (splitURI.length == 6) {
      if (!(splitURI[2].equals("days") && splitURI[4].equals("skiers"))) return 0;
      String dayID = splitURI[3];
      String skierID = splitURI[5];
      try {
        Integer.parseInt(skierID);
        Integer.parseInt(dayID);
      } catch (NumberFormatException ex) {
        return 0;
      }
    } else
      return -1;
    return 1;
  }

  boolean validatePOSTRequest(String URI) {
    if (!URI.equals("/liftrides")) return false;
    return true;
  }

  int validatePOSTRequestData(LiftRide liftRideModel) {
    if (Objects.isNull(liftRideModel.getSkierID()) || Objects.isNull(liftRideModel.getDayID())
            || Objects.isNull(liftRideModel.getDayID()) || Objects.isNull(liftRideModel.getResortID())
            || Objects.isNull(liftRideModel.getTime()) || Integer.valueOf(liftRideModel.getSkierID()) == 0
            || Integer.valueOf(liftRideModel.getTime()) == 0 || Integer.valueOf(liftRideModel.getLiftID()) == 0
            || Integer.valueOf(liftRideModel.getDayID()) == 0 || liftRideModel.getResortID().equals("")) {
      return -1;
    }
    return 1;
  }


  @Override
  public void destroy() {
    super.destroy();
    jedisPool.getResource().flushAll();
    jedisPool.destroy();
    System.out.println("Destroy called");
  }
}
