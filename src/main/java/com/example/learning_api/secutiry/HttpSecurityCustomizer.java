package com.example.learning_api.secutiry;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.stereotype.Component;

@Component
public class HttpSecurityCustomizer implements Customizer<HttpSecurity> {
    @Override
    public void customize(HttpSecurity http) {
        try {
            http.oauth2Login(oauth2 ->
                    oauth2.authorizationEndpoint(
                            authorization -> authorization.authorizationRequestRepository(
                                    new HttpSessionOAuth2AuthorizationRequestRepository()
                            )
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}