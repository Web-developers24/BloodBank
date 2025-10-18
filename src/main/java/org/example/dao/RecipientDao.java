package org.example.dao;

import org.example.config.HibernateUtil;
import org.example.model.Recipient;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class RecipientDao {

    public void saveRecipient(Recipient recipient) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(recipient);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    public void updateRecipient(Recipient recipient) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(recipient);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    public List<Recipient> getAllRecipients() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Recipient", Recipient.class).list();
        }
    }
}
