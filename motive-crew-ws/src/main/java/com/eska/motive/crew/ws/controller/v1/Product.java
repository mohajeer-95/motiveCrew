package com.eska.motive.crew.ws.controller.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eska.motive.crew.contract.request.impl.ProductRequest;
import com.eska.motive.crew.ws.service.ProductService;

import io.micrometer.core.ipc.http.HttpSender.Response;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

/**
 * @author Ashraf.Matar
 */
@RestController
@RequestMapping("/v1/alignment")
public class Product {

	@Autowired
	private ProductService productService;

	/**
	 * @author Ashraf.Matar
	 */
	@GetMapping("/all")
	public ResponseEntity<Response> getAlignment(
			@Min(message = "Invalid view type. The value should be greater than 1", value = 1) @RequestParam(name = "viewType") Long viewType,
			@Min(message = "Invalid type. The value should be greater than 1 ", value = 1) @RequestParam(name = "type") Long type,
			@RequestParam(name = "productId", required = false) String productId) {
//		FindAlignmentRequest request = FindAlignmentRequest.builder().viewType(viewType).type(type).productId(productId)
//				.build();
//		return productService.getAlignment(request);
		return null;
	}

	@PostMapping("/add")
	public ResponseEntity<Response> addAlignment(@RequestBody @Valid ProductRequest request) {
//		return productService.addAlignment(request);
		return null;
	}

	@PutMapping("/edit")
	public ResponseEntity<Response> editAlignment(@RequestBody @Valid ProductRequest request) {
//		return productService.editAlignment(request);
		return null;
	}

	@DeleteMapping("/delete")
	public ResponseEntity<Response> deleteAlignment(@RequestParam(name = "productId") Long productId) {
		return null;
//		return productService.deleteAlignment(productId);
	}

}
