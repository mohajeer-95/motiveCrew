package com.eska.motive.crew.ws.controller.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eska.motive.crew.contract.request.impl.LoginRequest;
import com.eska.motive.crew.contract.response.Response;
import com.eska.motive.crew.ws.exception.InternalErrorException;
import com.eska.motive.crew.ws.exception.ResourceNotFoundException;
import com.eska.motive.crew.ws.exception.ValidationException;
import com.eska.motive.crew.ws.service.LoginService;

import jakarta.validation.Valid;

/**
 * @author Ashraf.Matar
 */
@RestController
@RequestMapping("/public")
public class Login {

	@Autowired
	private LoginService loginService;

	@PostMapping("/login")
	public ResponseEntity<Response> login(@RequestBody LoginRequest loginRequest)
			throws ResourceNotFoundException, ValidationException, InternalErrorException {
		return loginService.login(loginRequest);
	}

}
