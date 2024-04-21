package com.example.learning_api.model;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "spring.security.oauth2.resourceserver.opaque-token")
public class OauthProperties {

    private String clientId;
    private String clientSecret;
    private String introspectionUri;

    // getters and setters
}