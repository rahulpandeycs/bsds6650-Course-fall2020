package dao;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

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

    try (Session session = ConnectionUtil.getSessionFactory(SkierVertical.class).openSession()) {
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
}
