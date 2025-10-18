package org.example.dao;

import org.example.config.HibernateUtil;
import org.example.model.Donation;
import org.example.model.BloodStock;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class DonationDao {

    public void saveDonation(Donation donation) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            // Save donation
            session.persist(donation);

            // Update stock
            BloodStock stock = session.createQuery("from BloodStock where bloodGroup = :bg", BloodStock.class)
                    .setParameter("bg", donation.getBloodGroup())
                    .uniqueResult();

            if (stock == null) {
                stock = new BloodStock();
                stock.setBloodGroup(donation.getBloodGroup());
                stock.setUnitsAvailable(donation.getUnits());
            } else {
                stock.setUnitsAvailable(stock.getUnitsAvailable() + donation.getUnits());
            }

            stock.setLastUpdated(java.time.LocalDateTime.now());
            session.merge(stock);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    public List<Donation> getAllDonations() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Donation", Donation.class).list();
        }
    }
}
