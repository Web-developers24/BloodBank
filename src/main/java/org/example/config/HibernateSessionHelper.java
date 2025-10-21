package org.example.config;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class HibernateSessionHelper {

    public interface HibernateWork<T> {
        T execute(Session session);
    }

    public static <T> T doInTransaction(HibernateWork<T> work) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            T result = work.execute(session);
            tx.commit();
            return result;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            throw new RuntimeException("❌ Hibernate operation failed: " + e.getMessage());
        }
    }
}
