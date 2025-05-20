package org.example.hssv1.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

import org.example.hssv1.model.CustomUser;
import org.example.hssv1.model.Department;
import org.example.hssv1.model.Major;
import org.example.hssv1.model.QuestionCategory;
import org.example.hssv1.model.Question;
import org.example.hssv1.model.Answer;
import org.example.hssv1.model.AdvisorProfile;


import java.util.Properties;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();

                Properties settings = new Properties();
                settings.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
                // Database URL from previous DatabaseConnection, adjust if needed
                settings.put(Environment.URL, "jdbc:mysql://localhost:3306/hssv?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
                settings.put(Environment.USER, "root"); // Default user, adjust if needed
                settings.put(Environment.PASS, "123456"); // Default password, adjust if needed
                settings.put(Environment.DIALECT, "org.hibernate.dialect.MySQLDialect"); // Updated dialect
                settings.put(Environment.SHOW_SQL, "true");
                settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
                settings.put(Environment.HBM2DDL_AUTO, "update"); // or "create-drop" for dev, "validate" for prod

                configuration.setProperties(settings);

                // Add annotated classes
                configuration.addAnnotatedClass(CustomUser.class);
                configuration.addAnnotatedClass(Department.class);
                configuration.addAnnotatedClass(Major.class);
                configuration.addAnnotatedClass(QuestionCategory.class);
                configuration.addAnnotatedClass(Question.class);
                configuration.addAnnotatedClass(Answer.class);
                configuration.addAnnotatedClass(AdvisorProfile.class);
                // Add other models here as they are created/updated

                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties()).build();

                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("There was an error building the Hibernate session factory", e);
            }
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
} 