package com.bbms.config;

import com.bbms.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.InputStream;
import java.util.Properties;

public class HibernateUtil {

    private static final Logger logger = LogManager.getLogger(HibernateUtil.class);
    private static SessionFactory sessionFactory;

    static {
        try {
            Configuration configuration = new Configuration();
            
            // Load hibernate.cfg.xml
            configuration.configure("hibernate.cfg.xml");
            
            // Override with environment variables if present
            overrideFromEnvironment(configuration);
            
            // Add annotated classes
            configuration.addAnnotatedClass(User.class);
            configuration.addAnnotatedClass(Donor.class);
            configuration.addAnnotatedClass(Recipient.class);
            configuration.addAnnotatedClass(BloodStock.class);
            configuration.addAnnotatedClass(Donation.class);
            configuration.addAnnotatedClass(BloodRequest.class);
            configuration.addAnnotatedClass(Transfusion.class);
            
            sessionFactory = configuration.buildSessionFactory();
            logger.info("Hibernate SessionFactory initialized successfully");
            
        } catch (Exception e) {
            logger.error("Failed to initialize Hibernate SessionFactory", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    private static void overrideFromEnvironment(Configuration configuration) {
        String dbUrl = System.getenv("DB_URL");
        String dbUser = System.getenv("DB_USER");
        String dbPass = System.getenv("DB_PASS");
        
        if (dbUrl != null && !dbUrl.isEmpty()) {
            configuration.setProperty("hibernate.connection.url", dbUrl);
            logger.info("Using DB_URL from environment");
        }
        if (dbUser != null && !dbUser.isEmpty()) {
            configuration.setProperty("hibernate.connection.username", dbUser);
        }
        if (dbPass != null && !dbPass.isEmpty()) {
            configuration.setProperty("hibernate.connection.password", dbPass);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
            logger.info("Hibernate SessionFactory closed");
        }
    }
}
