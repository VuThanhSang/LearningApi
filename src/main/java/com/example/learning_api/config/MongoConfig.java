package com.example.learning_api.config;

import com.mongodb.client.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

    private final MongoClient mongoClient;

    public MongoConfig(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @Bean
    public boolean mongoTestConnection() {
        try {
            mongoClient.listDatabaseNames().first();
            System.out.println("Connected to MongoDB successfully");
            return true;
        } catch (Exception e) {
            System.out.println("Failed to connect to MongoDB");
            e.printStackTrace();
            return false;
        }
    }
}