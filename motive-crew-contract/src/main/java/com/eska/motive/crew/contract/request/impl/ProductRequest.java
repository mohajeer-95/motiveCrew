package com.eska.motive.crew.contract.request.impl;

import com.eska.motive.crew.contract.request.Request;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@ToString
public class ProductRequest extends Request {
	private int year;
	private String productId;
}
