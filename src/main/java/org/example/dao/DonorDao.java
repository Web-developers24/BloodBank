package org.example.dao;

import org.example.config.HibernateSessionHelper;
import org.example.model.Donor;
import org.hibernate.Session;
import java.util.List;

public class DonorDao {

    public void saveDonor(Donor donor) {
        HibernateSessionHelper.doInTransaction(session -> {
            session.persist(donor);
            System.out.println("✅ Donor saved: " + donor.getFullName());
            return null;
        });
    }

    public void updateDonor(Donor donor) {
        HibernateSessionHelper.doInTransaction(session -> {
            session.merge(donor);
            System.out.println("♻️ Donor updated: " + donor.getFullName());
            return null;
        });
    }

    public List<Donor> getAllDonors() {
        return HibernateSessionHelper.doInTransaction(session ->
                session.createQuery("from Donor", Donor.class).list());
    }
}
