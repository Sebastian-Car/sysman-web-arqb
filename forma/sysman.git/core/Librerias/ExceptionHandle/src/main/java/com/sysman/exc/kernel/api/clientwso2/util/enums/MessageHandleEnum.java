package com.sysman.exc.kernel.api.clientwso2.util.enums;
/**
 * Enum MessageHandleEnum
 * @author erick
 *
 * @version 1.0, 26/02/2017
 * @since 1.0
 */
public enum MessageHandleEnum {

	GENERAL_ERROR("Error en el sistema, por favor intentelo m&aacute;s tarde. "),
	MODULE("Module: "),
	CLASS("Class: "),
	METHOD("Method: "),
	CODE("Code: "),
	LINE("Line: ");
	
	private final String message; 
	
	private MessageHandleEnum(String message){
		this.message = message;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
}
