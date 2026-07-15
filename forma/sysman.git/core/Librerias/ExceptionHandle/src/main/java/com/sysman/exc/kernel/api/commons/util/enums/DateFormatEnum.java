/*
* DateFormatEnum
*
* 1.0
*
* 12/08/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.exc.kernel.api.commons.util.enums;
 
/**
 * @author Erwin Jose Tirado Baldovino
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar formatos y expresiones regulares de fechas.
 */
public enum DateFormatEnum {
	 
	DEFAULT("dd/MM/yyyy HH:mm:ss", "^(\\d{2}/\\d{2}/\\d{4}\\s\\d{2}:\\d{2}:\\d{2}(\\s[a])?)$");
	
	private final String format;
	private final String regExp;
	
	private DateFormatEnum(String format, String regExp) {
		this.format = format;
		this.regExp = regExp;
	}

	public String getFormat() {
		return format;
	}

	public String getRegExp() {
		return regExp;
	}
}
