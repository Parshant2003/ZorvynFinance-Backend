package com.finance.FinanceDataProcessing.config;


import com.finance.FinanceDataProcessing.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    @Autowired
//    private JwtAuthenticationFilter jwtAuthenticationFilter;
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(auth -> auth
//
//                        .requestMatchers("/api/auth/**").permitAll()
//                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/api-docs/**").permitAll()
//                        .requestMatchers("/swagger-ui.html").permitAll()
//
//                        // User management - ADMIN only
//                        .requestMatchers(HttpMethod.GET, "/api/users").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.PUT, "/api/users/**").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")
//
//                        // Record management
//                        .requestMatchers(HttpMethod.POST, "/api/records").hasAnyRole("ANALYST", "ADMIN")
//                        .requestMatchers(HttpMethod.GET, "/api/records").hasAnyRole("ANALYST", "ADMIN", "VIEWER")
//                        .requestMatchers(HttpMethod.GET, "/api/records/**").hasAnyRole("ANALYST", "ADMIN", "VIEWER")
//                        .requestMatchers(HttpMethod.PUT, "/api/records/**").hasAnyRole("ANALYST", "ADMIN")
//                        .requestMatchers(HttpMethod.DELETE, "/api/records/**").hasRole("ADMIN")
//
//                        .requestMatchers("/api/dashboard/**").hasAnyRole("ANALYST", "ADMIN", "VIEWER")
//
//                        .anyRequest().authenticated()
//                )
//                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//}
//
//
//
//
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json");

            // Use exception message (jo tumhare service ne throw kiya)
            String message = accessDeniedException.getMessage();
            if (message == null || message.isBlank()) {
                message = "Access denied";
            }

            Map<String, Object> error = Map.of(
                    "code", 403,
                    "message", message,                            // ← yeh alag‑alag hoga
                    "path", request.getRequestURI()
            );

            ObjectMapper mapper = new ObjectMapper();
            response.getOutputStream().write(mapper.writeValueAsBytes(error));
        };
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/records").hasAnyRole("ANALYST", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/records").hasAnyRole("ANALYST", "ADMIN", "VIEWER")
                        .requestMatchers(HttpMethod.GET, "/api/records/**").hasAnyRole("ANALYST", "ADMIN", "VIEWER")
                        .requestMatchers(HttpMethod.PUT, "/api/records/**").hasAnyRole("ANALYST", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/records/**").hasRole("ADMIN")

                        .requestMatchers("/api/dashboard/**").hasAnyRole("ANALYST", "ADMIN", "VIEWER")

                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedHandler(accessDeniedHandler())   // ← yeh line add
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}