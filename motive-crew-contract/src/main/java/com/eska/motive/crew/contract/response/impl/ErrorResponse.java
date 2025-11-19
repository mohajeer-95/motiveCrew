package com.eska.motive.crew.contract.response.impl;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
	private String statusCode;
	private String message;
	private boolean isError;
}
