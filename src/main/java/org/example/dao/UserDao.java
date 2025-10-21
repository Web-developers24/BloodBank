package org.example.dao;

import org.example.config.HibernateUtil;
import org.example.model.User;
import org.hibernate.Session;

public class UserDao {

    public User getUserByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User WHERE username = :u", User.class)
                    .setParameter("u", username)
                    .uniqueResult();
        }
    }
}

