package org.example;

import org.example.config.HibernateUtil;
import org.example.model.Donor;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;

public class TestHibernate {
    public static void main(String[] args) {
        // Open a session
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        // Create a sample Donor record
        Donor d = new Donor();
        d.setFullName("Priya Raman");
        d.setBloodGroup("O+");
        d.setPhone("99999 00000");
        d.setDateOfBirth(LocalDate.of(1995, 5, 10));
        d.setLastDonationDate(LocalDate.of(2025, 9, 1));

        // Save into DB
        session.persist(d);
        tx.commit();

        session.close();
        System.out.println("✅ Hibernate connected and donor saved successfully!");
    }
}
