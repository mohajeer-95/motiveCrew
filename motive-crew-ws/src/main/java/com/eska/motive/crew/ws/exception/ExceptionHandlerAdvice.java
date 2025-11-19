package com.eska.motive.crew.ws.exception;

import java.nio.file.AccessDeniedException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.eska.motive.crew.contract.StatusCode;
import com.eska.motive.crew.contract.response.impl.ErrorResponse;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;

@ControllerAdvice
@Log4j2
/**
 * Centralized Exception handler
 * 
 * @author Ashraf.Matar
 */
public class ExceptionHandlerAdvice {

	/**
	 * This handler is invoked when a request is sent with an invalid HTTP method
	 * for the given endpoint.
	 * 
	 * <p>
	 * For example, if an endpoint is configured to handle only {@code GET}
	 * requests, but the client sends a {@code POST} request, this handler will be
	 * triggered to handle the method mismatch.
	 * </p>
	 * 
	 * 
	 * @return
	 */

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<Object> methodNotSuported() {
		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(null);
	}

	/**
	 * This handler is invoked when a request argument has an invalid type, meaning
	 * the client sends a value that cannot be converted to the expected type for
	 * the parameter.
	 * 
	 * <p>
	 * For example, if the parameter {@code x} is expected to be an {@code int}, but
	 * the client sends a {@code String} value (e.g., "abc"), this handler will
	 * handle the type mismatch and return a response indicating the error.
	 * </p>
	 * 
	 * @return
	 */

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(
			MethodArgumentTypeMismatchException typeMismatchException) {
		return ResponseEntity.badRequest().body(null);
	}

	/***
	 * 
	 * 
	 * This handler is invoked when a request argument fails validation after being
	 * annotated with {@code @Valid}.
	 * 
	 * <p>
	 * For example, if a method parameter is annotated with {@code @Valid} and the
	 * parameter does not meet the validation constraints (such as {@code @NotNull},
	 * {@code @Size}, etc.), this handler will be triggered to handle the validation
	 * failure and provide an appropriate response.
	 * </p>
	 * 
	 * <p>
	 * Such validation failures can occur if the client sends a request with invalid
	 * data, such as a missing required field or a field that does not meet the
	 * defined constraints (e.g., an empty string when a non-empty value is
	 * required).
	 * </p>
	 * 
	 * used by get method
	 * 
	 * @param handlerMethodValidationException
	 * @return
	 */

	@ExceptionHandler(HandlerMethodValidationException.class)
	public ResponseEntity<Object> handleMethodValidationException(
			HandlerMethodValidationException handlerMethodValidationException) {
		List<String> errors = handlerMethodValidationException.getAllErrors().stream().map(p -> p.getDefaultMessage())
				.collect(Collectors.toList());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorResponse(StatusCode.GENERAL_FIELD_VALIDATION_ERROR.getCode(), errors.toString(), true));
	}

	/**
	 * This handler is invoked when the request body is not readable or cannot be
	 * deserialized into the expected Java object.
	 * 
	 * <p>
	 * This typically happens when the client sends an improperly formatted request
	 * body, such as invalid JSON or malformed XML, or when the content type does
	 * not match the expected format.
	 * </p>
	 * 
	 * <p>
	 * For example, if the client sends an invalid JSON payload that cannot be
	 * converted to a Java object (e.g., missing closing braces, incorrect syntax),
	 * this exception will be triggered.
	 * </p>
	 **/

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<Object> handleMessageNotReadableException(
			HttpMessageNotReadableException httpMessageNotReadableException) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorResponse(StatusCode.GENERAL_FIELD_VALIDATION_ERROR.getCode(),
						StatusCode.GENERAL_FIELD_VALIDATION_ERROR.getDescription() + ": "
								+ httpMessageNotReadableException.getMessage(),
						true));
	}

	/***
	 * 
	 * 
	 * This handler is invoked when a request argument fails validation after being
	 * annotated with {@code @Valid}.
	 * 
	 * <p>
	 * For example, if a method parameter is annotated with {@code @Valid} and the
	 * parameter does not meet the validation constraints (such as {@code @NotNull},
	 * {@code @Size}, etc.), this handler will be triggered to handle the validation
	 * failure and provide an appropriate response.
	 * </p>
	 * 
	 * <p>
	 * Such validation failures can occur if the client sends a request with invalid
	 * data, such as a missing required field or a field that does not meet the
	 * defined constraints (e.g., an empty string when a non-empty value is
	 * required).
	 * </p>
	 * 
	 * used by post method
	 * 
	 * @param handlerMethodValidationException
	 * @return
	 */

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> handleMethodNotSuportedd(MethodArgumentNotValidException argumentNotValidException) {
		List<String> errors = argumentNotValidException.getBindingResult().getFieldErrors().stream()
				.map(FieldError::getDefaultMessage).collect(Collectors.toList());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorResponse(StatusCode.GENERAL_FIELD_VALIDATION_ERROR.getCode(),
						StatusCode.GENERAL_FIELD_VALIDATION_ERROR.getDescription() + ": " + errors.toString(), true));
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
			ResourceNotFoundException resourceNotFoundException) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ErrorResponse(resourceNotFoundException.getStatusCode().getCode(),
						resourceNotFoundException.getStatusCode().getDescription(), true));
	}

	/**
	 * This handler is invoked when a request have invalid business validation
	 * exception that throws from {@code Validator}
	 * 
	 * @param validationException
	 * @return
	 */

	@ExceptionHandler(ValidationException.class)
	public ResponseEntity<ErrorResponse> handleValidationException(ValidationException validationException) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorResponse(validationException.getStatusCode().getCode(),
						validationException.getStatusCode().getDescription(), true));
	}

	@ExceptionHandler(InternalErrorException.class)
	public ResponseEntity<ErrorResponse> handleInternalErrorException(InternalErrorException internalErrorException) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ErrorResponse(internalErrorException.getStatusCode().getCode(),
						internalErrorException.getStatusCode().getDescription(), true));
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ErrorResponse> handleMissingRequiredPram(MissingServletRequestParameterException exception) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
				new ErrorResponse(StatusCode.GENERAL_FIELD_VALIDATION_ERROR.getCode(), exception.getMessage(), true));

	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException exception) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(new ErrorResponse(StatusCode.USER_ACCESS_DENIED.getCode(), exception.getMessage(), true));

	}

	@ExceptionHandler({ PersistenceException.class, SQLException.class, JpaSystemException.class,
			ConstraintViolationException.class, EntityNotFoundException.class })
	public ResponseEntity<ErrorResponse> handleDBException(Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(
				StatusCode.INTERNAL_ERROR.getCode(), StatusCode.INTERNAL_ERROR.getDescription(), true));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGeneralException(Exception exception) {
		log.error("Unhandled exception caught: ", exception);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(
				StatusCode.INTERNAL_ERROR.getCode(), StatusCode.INTERNAL_ERROR.getDescription(), true));
	}

}
