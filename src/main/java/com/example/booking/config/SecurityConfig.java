package com.example.booking.config;

import com.example.booking.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import com.example.booking.filter.MdcLoggingFilter;
import com.example.booking.security.RateLimitFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.Customizer;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final RateLimitFilter rateLimitFilter;
        private final MdcLoggingFilter mdcLoggingFilter;

        @Value("${app.cors.allowed-origins}")
        private String allowedOrigins;

        public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                        RateLimitFilter rateLimitFilter,
                        MdcLoggingFilter mdcLoggingFilter) {
                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
                this.rateLimitFilter = rateLimitFilter;
                this.mdcLoggingFilter = mdcLoggingFilter;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                // CSRF is disabled because we are using stateless JWT authentication.
                                // CSRF attacks typically rely on session cookies identifying the user.
                                // Since we validate every request with a JWT in the Authorization header,
                                // and the Refresh Token is in a HttpOnly SameSite=Strict cookie (which is not
                                // automatically sent for cross-site requests in a way that allows exploitation
                                // without the Access Token), we are safe.
                                .csrf(AbstractHttpConfigurer::disable)
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .headers(headers -> headers
                                                .contentSecurityPolicy(csp -> csp
                                                                .policyDirectives(
                                                                                "default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'; img-src 'self' data:; connect-src 'self'; font-src 'self'; frame-ancestors 'self'"))
                                                .frameOptions(frameOptions -> frameOptions.deny())
                                                .contentTypeOptions(Customizer.withDefaults())
                                                .httpStrictTransportSecurity(hsts -> hsts
                                                                .includeSubDomains(true)
                                                                .maxAgeInSeconds(31536000))
                                                .referrerPolicy(referrer -> referrer
                                                                .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)))
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .addFilterBefore(mdcLoggingFilter, SecurityContextHolderFilter.class)
                                .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/api/v1/auth/**", "/api-docs/**", "/v3/api-docs/**",
                                                                "/swagger-ui/**",
                                                                "/swagger-ui.html",
                                                                "/api/v1/health", "/actuator/**")
                                                .permitAll()
                                                .requestMatchers("/api/v1/partnerships/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/v1/movies/**",
                                                                "/api/v1/cinemas/**", "/api/v1/hotels/**")
                                                .permitAll()
                                                .requestMatchers("/api/v1/movies/book", "/api/v1/movies/bookings/**",
                                                                "/api/v1/hotels/book",
                                                                "/api/v1/hotels/bookings/**")
                                                .authenticated()
                                                .requestMatchers("/api/v1/payments/**").authenticated()
                                                .anyRequest().authenticated());
                return http.build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(List.of("*"));
                configuration.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }

        @Bean
        public UserDetailsService userDetailsService(UserRepository userRepository) {
                return username -> userRepository.findByEmail(username)
                                .map(user -> org.springframework.security.core.userdetails.User.builder()
                                                .username(user.getEmail())
                                                .password(user.getPassword())
                                                .authorities(new ArrayList<>())
                                                .build())
                                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}