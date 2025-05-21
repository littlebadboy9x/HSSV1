package org.example.hssv1.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import org.example.hssv1.model.CustomUser;
import org.example.hssv1.model.Department;
import org.example.hssv1.model.Major;
import org.example.hssv1.model.QuestionCategory;
import org.example.hssv1.model.Question;
import org.example.hssv1.model.Answer;
import org.example.hssv1.model.AdvisorProfile;

import java.util.HashMap;
import java.util.Map;

public class HibernateUtil {
    private static StandardServiceRegistry registry;
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                // Tạo registry với các cấu hình
                StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();
                
                // Cấu hình Hibernate
                Map<String, Object> settings = new HashMap<>();
                settings.put("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
                settings.put("hibernate.connection.url", "jdbc:mysql://localhost:3306/hssv?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
                settings.put("hibernate.connection.username", "root");
                settings.put("hibernate.connection.password", "123456");
                settings.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
                settings.put("hibernate.show_sql", "true");
                settings.put("hibernate.format_sql", "true");
                settings.put("hibernate.current_session_context_class", "thread");
                settings.put("hibernate.hbm2ddl.auto", "update");
                
                // Áp dụng cấu hình
                registryBuilder.applySettings(settings);
                registry = registryBuilder.build();
                
                // Tạo MetadataSources và thêm các entity classes
                MetadataSources sources = new MetadataSources(registry)
                        .addAnnotatedClass(CustomUser.class)
                        .addAnnotatedClass(Department.class)
                        .addAnnotatedClass(Major.class)
                        .addAnnotatedClass(QuestionCategory.class)
                        .addAnnotatedClass(Question.class)
                        .addAnnotatedClass(Answer.class)
                        .addAnnotatedClass(AdvisorProfile.class);
                
                // Tạo Metadata
                Metadata metadata = sources.getMetadataBuilder().build();
                
                // Tạo SessionFactory
                sessionFactory = metadata.getSessionFactoryBuilder().build();
                
            } catch (Exception e) {
                e.printStackTrace();
                if (registry != null) {
                    StandardServiceRegistryBuilder.destroy(registry);
                }
                throw new RuntimeException("There was an error building the Hibernate session factory", e);
            }
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (registry != null) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
} 
