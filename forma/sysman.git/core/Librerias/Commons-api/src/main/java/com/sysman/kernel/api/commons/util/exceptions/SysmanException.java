/*
* SysmanException
*
* 1.0
*
* 12/08/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.kernel.api.commons.util.exceptions;
 
/**
 * @author Erwin Jose Tirado Baldovino
 * 
 * @version 1.0
 * 
 * Clase principal de excepciones que controla cualquier anomalia del ERP.
 */
public class SysmanException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public SysmanException(String message) {
		super(message); 
	}
}
