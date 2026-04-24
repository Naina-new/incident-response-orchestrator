package com.internship.tool.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private com.internship.tool.security.JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF since we are using Tokens instead of session cookies
                .csrf(AbstractHttpConfigurer::disable)

                // Set up the routing rules
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll() // Allow everyone to login/register
                        .anyRequest().authenticated()            // Require token for everything else
                )

                // Tell Spring to be stateless (don't save session state)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Put our custom JwtAuthFilter in front of the default username/password filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    // 1. Give Spring Boot an Authentication Manager
    @Bean
    public org.springframework.security.authentication.AuthenticationManager authenticationManager(
            org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // 2. Create a temporary hardcoded user for testing
    @Bean
    public org.springframework.security.core.userdetails.UserDetailsService userDetailsService() {
        org.springframework.security.core.userdetails.UserDetails admin = org.springframework.security.core.userdetails.User.builder()
                .username("admin")
                .password(passwordEncoder().encode("password123"))
                .roles("ADMIN")
                .build();
        return new org.springframework.security.provisioning.InMemoryUserDetailsManager(admin);
    }

    // 3. Set up the password encoder
    @Bean
    public org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }
}