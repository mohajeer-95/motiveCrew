package com.eska.motive.crew.contract.request.impl;

import com.eska.motive.crew.contract.request.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest extends Request {
	
	
	@NotBlank(message = "username is required")
	private String username;
	
	@NotBlank(message = "password is required")
	private String password;
	
	// Optional fields - not required for password-based authentication
	private String eskaCoreToken;
	
	private String sessionId;
	
	
	

}
