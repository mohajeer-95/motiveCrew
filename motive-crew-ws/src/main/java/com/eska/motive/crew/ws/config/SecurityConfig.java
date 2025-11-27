package com.eska.motive.crew.ws.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.eska.motive.crew.ws.filter.JwtAuthenticationFilter;

/**
 * Security configuration for JWT-based authentication
 * 
 * @author Motive Crew Team
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	/**
	 * Security filter chain configuration
	 * - Public endpoints: /public/**, /api/v1/health/**, /api/v1/auth/signup
	 * - All other endpoints require JWT authentication
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			// Disable CSRF for stateless JWT authentication
			.csrf(AbstractHttpConfigurer::disable)
			
			// Disable form login and HTTP basic auth (using JWT instead)
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			
			// Stateless session management (JWT tokens)
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			
			// Configure authorization rules
			.authorizeHttpRequests(auth -> auth
				// Public endpoints - no authentication required
				.requestMatchers(
					"/public/**",                    // Public login endpoint
					"/api/v1/health/**",             // Health check endpoints
					"/api/v1/auth/login",            // User login endpoint
					"/api/v1/auth/signup",           // User registration
					"/uploads/**",                   // Uploaded files (avatars, etc.) - static serving
					"/api/v1/uploads/**",            // Uploaded files via API endpoint (avatars, announcements)
					"/actuator/**",                  // Spring Boot Actuator
					"/error",                        // Error pages
					"/swagger-ui/**",                // Swagger UI (if enabled)
					"/v3/api-docs/**"               // OpenAPI docs (if enabled)
				).permitAll()
				
				// All other endpoints require authentication
				.anyRequest().authenticated()
			)
			
			// Add JWT authentication filter before UsernamePasswordAuthenticationFilter
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	/**
	 * Password encoder bean for hashing passwords
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
