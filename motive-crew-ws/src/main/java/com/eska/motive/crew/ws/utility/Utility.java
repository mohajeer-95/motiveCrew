/**
 * copyright (C) 2023 ESKADENIA Software 
 * This document contains trade secret data which is the property of
 * ESKADENIA Software. Information contained herein may not be used, copied or
 * disclosed in whole or part except as permitted by written
 * agreement from ESKADENIA Software.
*/
package com.eska.motive.crew.ws.utility;

import java.time.LocalDate;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

/**
 * The Class Utility.
 *
 * @author ashraf.matar
 * @created January 13, 2025
 */
@Component
@Log4j2
public class Utility {

	/**
	 * This method responsible for :
	 * 
	 * Handle optional parameter by converting it to long if not empty
	 * 
	 * Added by Ashraf.Matar
	 */
	public Long handleOptionalParam(String param) {
		return param == null || param.equals("null") || param.isEmpty() ? null : Long.valueOf(param);
	}

	/**
	 * This method responsible for :
	 * 
	 * Handle optional parameter by converting it to long if not empty
	 * 
	 * Added by Ashraf.Matar
	 */
	public boolean isParameterEmpty(String param) {
		return param == null || param.equals("null") || param.isEmpty();
	}

	/**
	 * This method responsible for :
	 * 
	 * Validate the base64 code
	 * 
	 * Added by Ashraf.Matar
	 */
	public byte[] validateImage(byte[] image) {
		byte[] validatedImage;
		try {
			validatedImage = Base64.getDecoder().decode(image);
		} catch (IllegalArgumentException e) {
			log.error("Failed to decode Base64 data: " + e.getMessage());
			validatedImage = new byte[0];
		}
		return validatedImage;
	}

	/**
	 * This method responsible for :
	 * 
	 * 1.Initialize a map with months 01 to 12, all set to 0.0
	 * 
	 * 2.Ensure 01, 02, ..., 12 format
	 * 
	 * Added by Ashraf.Matar
	 * @param year 
	 * 
	 */
	public Map<String, Double> initializeMonthMap(int year) {
		Map<String, Double> monthMap = new HashMap<>();
		if (LocalDate.now().getYear() == year) {
			for (int i = 1; i <= LocalDate.now().getMonthValue(); i++) {
				monthMap.put(String.format("%02d", i), 0.0);
			}
		} else {
			for (int i = 1; i <= 12; i++) {
				monthMap.put(String.format("%02d", i), 0.0);
			}
		}
		return monthMap;
	}
		
		/**
		 * This method responsible for :
		 * 
		 * 1.Initialize a map with months 01 to 12, all set to 0
		 * 
		 * 2.Ensure 01, 02, ... for current month of current year
		 * 
		 * Added by Dana.Sawalha
		 * @param year 
		 * 
		 */
		public Map<String, Integer> initializeIntergerMonthMap(int year) {
			Map<String, Integer> monthMap = new HashMap<>();
			if (LocalDate.now().getYear() == year) {
				for (int i = 1; i <= LocalDate.now().getMonthValue(); i++) {
					monthMap.put(String.format("%02d", i), 0);
				}
			} else {
				for (int i = 1; i <= 12; i++) {
					monthMap.put(String.format("%02d", i), 0);
				}
			}
			return monthMap;
	}

	public Long handleOptionalParam(Long geoAreaId) {
		if (geoAreaId == null || geoAreaId == 0) {
			return null;
		}
		return geoAreaId;
	}

}
