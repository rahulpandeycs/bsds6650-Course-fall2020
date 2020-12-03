
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import model.LiftRide;
import model.SkierVertical;

public class ConnectionUtil {

  private static Logger logger = LoggerFactory.getLogger(ConnectionUtil.class);

  private static SessionFactory sessionFactory;

  public static SessionFactory getSessionFactory(Class entityClass) throws IOException {
    if (sessionFactory == null) {
      HibernateConfigParameters configParameters = loadHibernateConfigFile();
      try {
        Configuration configuration = new Configuration();
        // Hibernate settings equivalent to hibernate.cfg.xml's properties
        Properties settings = new Properties();
        settings.put(Environment.DRIVER, configParameters.getDRIVER());
        settings.put(Environment.URL, configParameters.getURL());
        settings.put(Environment.USER, configParameters.getUSER());
        settings.put(Environment.PASS, configParameters.getPASS());

        settings.put(Environment.DIALECT, "org.hibernate.dialect.MySQL5Dialect");
        settings.put(Environment.SHOW_SQL, "true");
        settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
        settings.put(Environment.HBM2DDL_AUTO, configParameters.getHBM2DDL_AUTO());
        settings.put(Environment.C3P0_MAX_SIZE, 10);

        configuration.setProperties(settings);
        configuration.addAnnotatedClass(LiftRide.class);
        configuration.addAnnotatedClass(SkierVertical.class);

        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties()).build();

        System.out.println("Hibernate Java Config serviceRegistry created");
        sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        return sessionFactory;

      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return sessionFactory;
  }


  private static HibernateConfigParameters loadHibernateConfigFile() throws IOException {
    java.io.File propertiesFile = null;
    System.out.println("Reading default config.properties");
    InputStream input = ConnectionUtil.class.getClassLoader().getResourceAsStream("config.properties");
    Properties prop = new Properties();
    if (input == null) {
      System.out.println("Sorry, unable to find config.properties");
      logger.error("Sorry, unable to find config.properties");
      return null;
    }
    //load a properties file from class path, inside static method
    prop.load(input);
    return new HibernateConfigParameters(prop);
  }
}
