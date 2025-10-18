package org.example.dao;

import org.example.config.HibernateUtil;
import org.example.model.BloodStock;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class BloodStockDao {

    public void saveOrUpdateStock(BloodStock stock) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(stock); // merge = insert or update
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    public BloodStock getStockByGroup(String bloodGroup) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from BloodStock where bloodGroup = :bg", BloodStock.class)
                    .setParameter("bg", bloodGroup)
                    .uniqueResult();
        }
    }

    public List<BloodStock> getAllStocks() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from BloodStock", BloodStock.class).list();
        }
    }
}
