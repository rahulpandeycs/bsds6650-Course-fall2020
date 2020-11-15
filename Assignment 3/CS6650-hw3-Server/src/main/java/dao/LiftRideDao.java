package dao;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

import exception.SkierServerException;
import model.LiftRide;
import model.SkierCompositeKey;
import utils.ConnectionUtil;

public class LiftRideDao {

  public void saveLiftRide(LiftRide liftRide) throws SkierServerException {
    Transaction transaction = null;
    try (Session session = ConnectionUtil.getSessionFactory(LiftRide.class).openSession()) {
      transaction = session.beginTransaction();
      session.saveOrUpdate(liftRide);
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      System.out.println("The error has occurred");
      throw new SkierServerException("Database save to skier Api failed with reason", e);
    }
  }

  public void truncateLiftRide() throws SkierServerException {
    Transaction transaction = null;
    try (Session session = ConnectionUtil.getSessionFactory(LiftRide.class).openSession()) {
      transaction = session.beginTransaction();
      session.createSQLQuery("truncate table liftRide").executeUpdate();
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      e.printStackTrace();
      throw new SkierServerException("LiftRide Database trucate failed with reason", e);
    }
  }

  public void updateLiftRide(LiftRide liftRide) throws SkierServerException {
    Transaction transaction = null;
    try (Session session = ConnectionUtil.getSessionFactory(LiftRide.class).openSession()) {
      transaction = session.beginTransaction();
      session.update(liftRide);
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      e.printStackTrace();
      throw new SkierServerException("Database update to skier Api failed with reason", e);
    }
  }


  public void deleteLiftRide(int id) throws SkierServerException {
    Transaction transaction = null;
    try (Session session = ConnectionUtil.getSessionFactory(LiftRide.class).openSession()) {

      transaction = session.beginTransaction();
      LiftRide liftRide = session.get(LiftRide.class, id);
      if (liftRide != null) {
        session.delete(liftRide);
      }
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      e.printStackTrace();
      throw new SkierServerException("Database delete in skier Api failed with reason", e);
    }
  }

  public LiftRide getLiftRide(SkierCompositeKey ride) throws SkierServerException {

    Transaction transaction = null;
    LiftRide liftRide = null;

    try (Session session = ConnectionUtil.getSessionFactory(LiftRide.class).openSession()) {
      transaction = session.beginTransaction();
      liftRide = session.get(LiftRide.class, ride);
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      e.printStackTrace();
      throw new SkierServerException("Get call from skier Api failed with reason", e);
    }
    return liftRide;
  }

  public List<LiftRide> getAllLiftRide(int id) throws SkierServerException {

    Transaction transaction = null;
    List<LiftRide> listOfLiftRide = null;

    try (Session session = ConnectionUtil.getSessionFactory(LiftRide.class).openSession()) {
      transaction = session.beginTransaction();
      listOfLiftRide = session.createQuery("from LiftRide").getResultList();
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      e.printStackTrace();
      throw new SkierServerException("Get call from skier Api failed with reason", e);
    }
    return listOfLiftRide;
  }
}
