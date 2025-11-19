package com.eska.motive.crew.contract;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Using an enum for status codes in a REST API can help improve code
 * readability and maintainability. It provides a centralized way to manage
 * status codes and ensures consistency throughout your application
 * 
 * @author Ashraf.Matar
 */

@AllArgsConstructor
public enum StatusCode {

	SUCCESS("MOTIVE-CREW-0000", "Operation done Successfully"), USER_NOT_FOUND("MOTIVE-CREW-0001", "User not found "),
	INVALID_USER_NAME_PASS("MOTIVE-CREW-0002", "invalid User Credentials"),
	GENERAL_FIELD_VALIDATION_ERROR("MOTIVE-CREW-003", "field Validation error"),
	USER_ACCESS_DENIED("MOTIVE-CREW-004", "User access denied , make sure you have a valid token"),
	INTERNAL_ERROR("MOTIVE-CREW-0005", "Internal server error while processing your request , please call system admin"),
	NOT_FOUND("MOTIVE-CREW-0006", "No data found"),
	PRODUCT_NOT_MAPPED_TO_ALIGNMENT("MOTIVE-CREW-0007", "Product not mapped to alignment"),
	NO_ALIGNMENT_DEFINED_FOR_PRODUCT("MOTIVE-CREW-0008", "No alignment defined for product"),
	CAN_NOT_BE_DELETED("MOTIVE-CREW-0009", "Unable to delete action. Status should be Closed"),
	CAN_NOT_BE_DELETED_ARRAY("MOTIVE-CREW-0010", "Unable to delete actions. Status of all should be Closed"),
	SERVICE_UNAVAILABLE("MOTIVE-CREW-0011", "Web service is down or not running");
	/*
	 * Add your status codes
	 */

	@Getter
	private String code;
	@Getter
	String description;

	public static StatusCode fromCode(String code) {
		for (StatusCode statusCode : StatusCode.values()) {
			if (statusCode.getCode().equals(code)) {
				return statusCode;
			}
		}
		return INTERNAL_ERROR;
	}

}
