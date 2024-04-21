package com.example.learning_api.secutiry.jwt;

import com.example.learning_api.repository.database.UserRepository;
import com.example.learning_api.service.common.JwtService;
import com.example.learning_api.service.common.UserDetailsImp;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsImp userService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            final String authHeader = request.getHeader("Authorization");
            final String Jwt ;
            final String userEmail;

            if (StringUtils.isEmpty(authHeader)|| !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }
            Jwt = authHeader.substring(7);
            userEmail = jwtService.extractUsername(Jwt);
            UserDetails userDetails = userService.loadUserByUsername(userEmail);
            if (jwtService.isValid(Jwt, userDetails)) {
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                UsernamePasswordAuthenticationToken token =
                        new UsernamePasswordAuthenticationToken
                                (userDetails, null, userDetails.getAuthorities());
                token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                securityContext.setAuthentication(token);
                SecurityContextHolder.setContext(securityContext);

            }
            filterChain.doFilter(request, response);
//            extractTokenFromRequest(request)
//                    .map(jwtService::decodeAccessToken) // str -> jwtUtils.decodeAccessToken(str)  jwtUtils::decodeAccessToken
//                    .map(jwtService::convert)
//                    .map(UserPrincipalAuthenticationToken::new)
//                    .ifPresent(authentication -> SecurityContextHolder.getContext().setAuthentication(authentication));
//            filterChain.doFilter(request, response);
        }
        catch (RuntimeException   e) {
            e.printStackTrace();
        }
    }
    private Optional<String> extractTokenFromRequest(HttpServletRequest request) {
        var token = request.getHeader("Authorization");
        log.warn("Token", token);
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return Optional.of(token.substring(7));
        }
        return Optional.empty();
    }
    private boolean isBypassToken(@NonNull HttpServletRequest request) {
        String requestPath = request.getServletPath();
        return requestPath.equals("/login/oauth2/code/google") || requestPath.startsWith("/api/auth/user/") || requestPath.startsWith("/oauth2/");
    }
}