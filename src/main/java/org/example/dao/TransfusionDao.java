package org.example.dao;

import org.example.config.HibernateUtil;
import org.example.model.Transfusion;
import org.example.model.BloodStock;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class TransfusionDao {

    public void saveTransfusion(Transfusion transfusion) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            // Save transfusion
            session.persist(transfusion);

            // Decrease stock
            BloodStock stock = session.createQuery("from BloodStock where bloodGroup = :bg", BloodStock.class)
                    .setParameter("bg", transfusion.getBloodGroupUsed())
                    .uniqueResult();

            if (stock != null && stock.getUnitsAvailable() >= transfusion.getUnits()) {
                stock.setUnitsAvailable(stock.getUnitsAvailable() - transfusion.getUnits());
                stock.setLastUpdated(java.time.LocalDateTime.now());
                session.merge(stock);
            } else {
                System.out.println("⚠️ Not enough stock available for this blood group!");
            }

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    public List<Transfusion> getAllTransfusions() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Transfusion", Transfusion.class).list();
        }
    }
}
