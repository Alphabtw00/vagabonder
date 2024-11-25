package com.vagabonder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(CorsConfigurationSource corsConfigurationSource) {
        this.corsConfigurationSource = corsConfigurationSource;
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource)) // Enable CORS
                .csrf(AbstractHttpConfigurer::disable)
                .headers(httpSecurityHeadersConfigurer ->
                        httpSecurityHeadersConfigurer
                                .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()  // Allow OPTIONS requests
                        .requestMatchers(
                                "/profile",
                                "/profile/**").authenticated()
                        .requestMatchers(
                                "/itinerary/create",
                                "/itinerary/update/**",
                                "/itinerary/update-status/**",
                                "/itinerary/past").authenticated()
                        .requestMatchers(
                                "/discover",
                                "/ignore/**",
                                "/ignore",
                                "/friends/**",
                                "/add-friend/**",
                                "/accept-friend/**",
                                "/reject-friend/**").authenticated()
                        .requestMatchers(
                                "/check-status/**",
                                "/conversations",
                                "/conversations/**",
                                "/send-message",
                                "/requests",
                                "/requests/**").authenticated()
                        .anyRequest().permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpStatus.OK.value());
                            response.getWriter().write("{\"message\": \"Logout successful\"}");
                        })
                )
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

}
