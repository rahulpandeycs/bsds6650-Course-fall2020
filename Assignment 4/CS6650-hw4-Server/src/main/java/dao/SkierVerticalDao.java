package dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

import exception.SkierServerException;
import model.LiftRide;
import model.SkierVertical;
import utils.ConnectionUtil;

public class SkierVerticalDao {

  public void saveSkierVertical(SkierVertical skierVertical) {
    Transaction transaction = null;
    try (Session session = ConnectionUtil.getSessionFactory(SkierVertical.class).openSession()) {
      transaction = session.beginTransaction();
      session.save(skierVertical);
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      e.printStackTrace();
    }
  }

  public void updateLiftRide(SkierVertical skierVertical) {
    Transaction transaction = null;
    try (Session session = ConnectionUtil.getSessionFactory(SkierVertical.class).openSession()) {
      transaction = session.beginTransaction();
      session.update(skierVertical);
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      e.printStackTrace();
    }
  }


  public void deleteSkierVertical(int id) {
    Transaction transaction = null;
    try (Session session = ConnectionUtil.getSessionFactory(SkierVertical.class).openSession()) {

      transaction = session.beginTransaction();
      SkierVertical skierVertical = session.get(SkierVertical.class, id);
      if (skierVertical != null) {
        session.delete(skierVertical);
      }
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      e.printStackTrace();
    }
  }

  public SkierVertical getSkierVertical(String id) {

    Transaction transaction = null;
    SkierVertical skierVertical = null;

    try (Session session = ConnectionUtil.getSessionFactory(LiftRide.class).openSession()) {
      transaction = session.beginTransaction();
      skierVertical = session.get(SkierVertical.class, id);
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      e.printStackTrace();
    }
    return skierVertical;
  }

  public List<SkierVertical> getAllSkierVertical(String id) {

    Transaction transaction = null;
    List<SkierVertical> listOfSkierVertical = null;

    try (Session session = ConnectionUtil.getSessionFactory(SkierVertical.class).openSession()) {
      transaction = session.beginTransaction();
      listOfSkierVertical = session.createQuery("from SkierVertical").getResultList();
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      e.printStackTrace();
    }
    return listOfSkierVertical;
  }

  public int getTotalVertBySkierIdResortId(String resortId, int skierId) throws SkierServerException {

    Transaction transaction = null;
    List<LiftRide> listOfLiftRides = null;
    int totalVert = 0;
    try (Session session = ConnectionUtil.getSessionFactory(LiftRide.class).openSession()) {
      transaction = session.beginTransaction();
      Query query = session.createQuery("select sum (liftID*10) from LiftRide where resortID = :resortId and skierID = :skierId");
      query.setParameter("resortId", resortId);
      query.setParameter("skierId", skierId);
      Object queryResult = query.uniqueResult();
      if (queryResult != null)
        totalVert = (int) (long) queryResult;
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      System.out.println("The error has occurred");
      throw new SkierServerException("Skier Api failed to fetch data from database", e);
    }
    return totalVert;
  }

  public int getTotalVertByResortDaySkierID(String resortId, int skierID, int DayId) throws SkierServerException {

    Transaction transaction = null;
    List<LiftRide> listOfLiftRides = null;
    int totalVert = 0;
    try (Session session = ConnectionUtil.getSessionFactory(LiftRide.class).openSession()) {
      transaction = session.beginTransaction();
      Query query = session.createQuery("select sum(liftID*10) from LiftRide where resortID = :resortId and skierID = :skierId and dayID = :dayId");
      query.setParameter("resortId", resortId);
      query.setParameter("skierId", skierID);
      query.setParameter("dayId", DayId);
      Object queryResult = query.uniqueResult();
      if (queryResult != null)
        totalVert = (int) (long) queryResult;
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      System.out.println("The error has occurred");
      throw new SkierServerException("Skier Api failed to fetch data from database", e);
    }

    return totalVert;
  }
}
