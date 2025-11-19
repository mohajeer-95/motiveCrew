package com.eska.motive.crew.ws.validation;

import org.springframework.stereotype.Service;

import com.eska.motive.crew.contract.request.impl.LoginRequest;
import com.eska.motive.crew.ws.exception.ResourceNotFoundException;
import com.eska.motive.crew.ws.exception.ValidationException;

/**
 * @author A.Juhaini
 */

@Service
public class LoginRequestValidator implements Validator<LoginRequest> {

	@Override
	public void validate(LoginRequest loginRequest) throws ResourceNotFoundException, ValidationException {

		/**
		 * TODO : validate EskaCore token using Eska core validation
		 * 
		 */

	}
}
