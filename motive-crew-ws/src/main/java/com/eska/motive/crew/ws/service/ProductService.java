package com.eska.motive.crew.ws.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.eska.motive.crew.contract.StatusCode;
import com.eska.motive.crew.contract.request.impl.ProductRequest;
import com.eska.motive.crew.contract.response.Response;
import com.eska.motive.crew.contract.response.impl.ProductResponse;

import lombok.extern.log4j.Log4j2;

/**
 * @author Ashraf.Matar
 * 
 */
@Service
@Log4j2
public class ProductService {

//	@Autowired
//	private ProductDBService productDBService;
//	@Autowired
//	private Utility utility;

	/**
	 * This method responsible for :
	 * 
	 * 1 - Get Retention Rate for specific productId
	 * 
	 * Updated by Ashraf.Matar
	 */
	public ResponseEntity<Response> getRetentionRate(ProductRequest request) {
		log.debug(String.format("ProductService - getRetentionRate for product %s in %s ", request.getProductId(),
				request.getYear()));
		ProductResponse response = new ProductResponse();
		response.setError(false);
		response.setStatusCode(StatusCode.SUCCESS.getCode());
		response.setMessage(StatusCode.SUCCESS.getDescription());
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

}
