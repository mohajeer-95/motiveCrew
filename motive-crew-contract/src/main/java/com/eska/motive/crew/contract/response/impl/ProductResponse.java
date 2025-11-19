package com.eska.motive.crew.contract.response.impl;

import java.util.List;

import com.eska.motive.crew.contract.dto.ProductDTO;
import com.eska.motive.crew.contract.response.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse extends Response {

	private List<ProductDTO> data;

}
