/**
 * 
 */
package com.sysman.nomina.enums;


/**
 * @author avega
 *
 */
public enum FrmNominaDianControladorUrlEnum {

	URL1902001("FRMNOMINADIANCONTROLADOR1902001","1902001"),

	URL1902002("FRMNOMINADIANCONTROLADOR1902002","1902002"),

	URL1902006("FRMNOMINADIANCONTROLADOR1902006","1902006"),
	
	URL1902008("FRMNOMINADIANCONTROLADOR1902008","1902008"),

	URL1902010("FRMNOMINADIANCONTROLADOR1902010","1902010"),
	
	//Borra la tabla ESTADO_NOMINA_DIAN
	URL1903001("FRMNOMINADIANCONTROLADOR1903001","1903001"),
	//inserta los datos en la tabla ESTADO_NOMINA_DIAN
	URL1903002("FRMNOMINADIANCONTROLADOR1903002","1903002"),
	//Trae el registro de la tabla ESTADO_NOMINA_DIAN
	URL1903003("FRMNOMINADIANCONTROLADOR1903003","1903003"), 
	//Consulta el certificado
	URL1882001("FRMNOMINADIANCONTROLADOR1882001","1882001"), 
	
	URL1902012("FRMNOMINADIANCONTROLADOR1902012","1902012"), 
	
	URL1902013("FRMNOMINADIANCONTROLADOR1902013","1902013"), 

	;
	
	private final String key;
	private final String value;

	private FrmNominaDianControladorUrlEnum(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
}

