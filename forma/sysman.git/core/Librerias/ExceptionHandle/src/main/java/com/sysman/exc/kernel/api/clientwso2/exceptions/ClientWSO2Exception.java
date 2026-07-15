/*
* ClientWSO2Exception
*
* 1.0
*
* 12/08/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.exc.kernel.api.clientwso2.exceptions;

import com.sysman.exception.SystemException;

/**
 * @author Erwin Jose Tirado Baldovino
 * 
 * @version 1.0
 * 
 * Clase de excepciones que controla cualquier anomalia en la libreria.
 */
@SuppressWarnings("serial")
public class ClientWSO2Exception extends SystemException {
	 
	public ClientWSO2Exception(String message) {
		super(message);
	} 
}
