package com.polarbookshop.catalogservice;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInfoLogger {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @PostConstruct
    public void logDatabaseInfo() {
        System.out.println("Database URL: " + dbUrl);
        System.out.println("Database Username: " + dbUsername);
        System.out.println("Database Password: " + dbPassword);
    }
}