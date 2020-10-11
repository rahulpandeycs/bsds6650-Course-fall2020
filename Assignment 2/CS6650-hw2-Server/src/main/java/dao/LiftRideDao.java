package dao;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

import model.LiftRide;
import model.SkierVertical;
import utils.ConnectionUtil;

public class LiftRideDao {

  public void saveLiftRide(LiftRide liftRide) {
    Transaction transaction = null;
    try (Session session = ConnectionUtil.getSessionFactory(LiftRide.class).openSession()) {
      transaction = session.beginTransaction();
      session.save(liftRide);
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      e.printStackTrace();
    }
  }

  public void updateLiftRide(LiftRide liftRide) {
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
    }
  }


  public void deleteLiftRide(int id) {
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
    }
  }

  public LiftRide getLiftRide(int id) {

    Transaction transaction = null;
    LiftRide liftRide = null;

    try (Session session = ConnectionUtil.getSessionFactory(LiftRide.class).openSession()) {
      transaction = session.beginTransaction();
      liftRide = session.get(LiftRide.class, id);
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      e.printStackTrace();
    }
    return liftRide;
  }

  public List<LiftRide> getAllLiftRide(int id) {

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
    }
    return listOfLiftRide;
  }
}
