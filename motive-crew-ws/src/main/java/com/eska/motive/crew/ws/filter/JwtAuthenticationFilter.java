package com.eska.motive.crew.ws.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.eska.motive.crew.contract.StatusCode;
import com.eska.motive.crew.ws.entity.User;
import com.eska.motive.crew.ws.repository.UserRepository;
import com.eska.motive.crew.ws.util.JWTUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

/**
 * JWT Authentication Filter
 * 
 * This filter validates JWT tokens from the Authorization header and sets
 * the authentication context for Spring Security.
 * 
 * @author Motive Crew Team
 */
@Component
@Log4j2
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JWTUtil jwtUtil;

	@Autowired
	private UserRepository userRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		// Skip JWT processing for public endpoints
		String path = request.getRequestURI();
		if (path != null && (
			path.startsWith("/public/") ||
			path.startsWith("/api/v1/health/") ||
			path.startsWith("/api/v1/auth/login") ||
			path.startsWith("/api/v1/auth/signup") ||
			path.startsWith("/uploads/") ||
			path.startsWith("/api/v1/uploads/") ||
			path.startsWith("/actuator/") ||
			path.startsWith("/error") ||
			path.startsWith("/swagger-ui/") ||
			path.startsWith("/v3/api-docs/")
		)) {
			filterChain.doFilter(request, response);
			return;
		}
		
		try {
			// Extract token from Authorization header
			String authHeader = request.getHeader("Authorization");
			if (authHeader == null) {
				authHeader = request.getHeader("authorization");
			}

			if (authHeader != null && authHeader.startsWith("Bearer ")) {
				String token = authHeader.substring(7); // Remove "Bearer " prefix

				// Validate token
				if (jwtUtil.validateToken(token)) {
					// Extract username (email) from token
					String email = jwtUtil.getUsernameFromToken(token);
					
					if (email != null) {
						// Load user from database
						userRepository.findByEmail(email).ifPresent(user -> {
							// Check if user is active
							if (user.getIsActive()) {
								// Create authorities based on user role
								Collection<GrantedAuthority> authorities = new ArrayList<>();
								String role = "ROLE_" + user.getRole().name();
								authorities.add(new SimpleGrantedAuthority(role));

								// Create authentication token with user details
								Authentication authentication = new UsernamePasswordAuthenticationToken(
										user, // Principal (user object)
										null, // Credentials (not needed for JWT)
										authorities // User roles/authorities
								);

								// Set authentication in security context
								SecurityContextHolder.getContext().setAuthentication(authentication);
								
								log.debug("User authenticated: {}", email);
							} else {
								log.warn("Inactive user attempted to access: {}", email);
								SecurityContextHolder.clearContext();
							}
						});
					} else {
						log.warn("Could not extract username from token");
						SecurityContextHolder.clearContext();
					}
				} else {
					log.warn("Invalid JWT token");
					SecurityContextHolder.clearContext();
				}
			} else {
				// No token provided - will be handled by Spring Security authorization rules
				SecurityContextHolder.clearContext();
			}
		} catch (Exception e) {
			log.error("Error processing JWT authentication", e);
			SecurityContextHolder.clearContext();
		}

		// Continue with the filter chain
		filterChain.doFilter(request, response);
	}
}
