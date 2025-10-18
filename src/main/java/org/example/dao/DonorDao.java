package org.example.dao;

import org.example.config.HibernateUtil;
import org.example.model.Donor;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class DonorDao {

    // Save a new donor
    public void saveDonor(Donor donor) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(donor);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    // Update an existing donor
    public void updateDonor(Donor donor) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(donor);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    // Retrieve all donors
    public List<Donor> getAllDonors() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Donor", Donor.class).list();
        }
    }
}
