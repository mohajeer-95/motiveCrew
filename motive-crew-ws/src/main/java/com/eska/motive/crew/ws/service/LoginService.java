package com.eska.motive.crew.ws.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.eska.motive.crew.ws.entity.User;
import com.eska.motive.crew.ws.repository.UserRepository;
import com.eska.motive.crew.ws.util.JWTUtil;
import com.eska.motive.crew.contract.StatusCode;
import com.eska.motive.crew.contract.dto.AuthenticationDTO;
import com.eska.motive.crew.contract.request.impl.LoginRequest;
import com.eska.motive.crew.contract.response.Response;
import com.eska.motive.crew.contract.response.impl.LoginResponse;
import com.eska.motive.crew.ws.exception.InternalErrorException;
import com.eska.motive.crew.ws.exception.ResourceNotFoundException;
import com.eska.motive.crew.ws.exception.ValidationException;

import lombok.extern.log4j.Log4j2;

import java.util.Optional;

/**
 * @author Ashraf.Matar
 * 
 */
@Service
@Log4j2
public class LoginService {

	@Autowired
	private JWTUtil jwtUtil;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public ResponseEntity<Response> login(LoginRequest loginRequest)
			throws ResourceNotFoundException, ValidationException, InternalErrorException {

		try {
			// Validate required fields
			if (loginRequest.getUsername() == null || loginRequest.getUsername().isEmpty()) {
				throw new ValidationException(StatusCode.INVALID_USER_NAME_PASS);
			}
			
			if (loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
				throw new ValidationException(StatusCode.INVALID_USER_NAME_PASS);
			}
			
			// Find user by email (username)
			Optional<User> userOpt = userRepository.findByEmail(loginRequest.getUsername());
			
			if (userOpt.isEmpty() || !userOpt.get().getIsActive()) {
				throw new ResourceNotFoundException(StatusCode.USER_NOT_FOUND);
			}

			User user = userOpt.get();
			
			// Validate password
			if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
				throw new ValidationException(StatusCode.INVALID_USER_NAME_PASS);
			}
			
			// Generate JWT token
			String generatedToken = jwtUtil.generateToken(user.getEmail());
			
			// Build response
			AuthenticationDTO authenticationDTO = AuthenticationDTO.builder().token(generatedToken).build();
			LoginResponse loginResponse = new LoginResponse(authenticationDTO);
			loginResponse.setStatusCode(StatusCode.SUCCESS.getCode());
			loginResponse.setMessage(StatusCode.SUCCESS.getDescription());
			loginResponse.setError(false);
			return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
			
		} catch (ResourceNotFoundException | ValidationException e) {
			throw e;
		} catch (Exception exception) {
			log.error("Error while login with exception ", exception);
			throw new InternalErrorException(StatusCode.INTERNAL_ERROR);
		}
	}
}
