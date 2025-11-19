package com.eska.motive.crew.contract.response.impl;

import com.eska.motive.crew.contract.dto.AuthenticationDTO;
import com.eska.motive.crew.contract.response.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse extends Response {

	private AuthenticationDTO authenticationData;

}
