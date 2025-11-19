package com.eska.motive.crew.ws.validation;

import com.eska.motive.crew.contract.request.Request;
import com.eska.motive.crew.ws.exception.ResourceNotFoundException;
import com.eska.motive.crew.ws.exception.ValidationException;

/**
 * @author Ashraf.Matar
 */

public interface Validator<R extends Request> {

	void validate(R r) throws ValidationException, ResourceNotFoundException;
}
