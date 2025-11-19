package com.eska.motive.crew.contract.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Creating a "markable" request interface refer to an interface that defines a
 * standard way to handle requests
 * 
 * @author Ashraf.Matar
 */
@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class Request {
	private String token;

}
