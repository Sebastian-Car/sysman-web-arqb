/**
 * 
 */
package com.sysman.nomina.enums;

/**
 * @author avega
 *
 */
public enum FrmNominaDianControladorEnum {

	CUNE("CUNE"),
	
	TIPO_NOMINA("TIPO_NOMINA"), 
	
	MENSAJE("MENSAJE"), 
	
	CODIGO_TRABAJADOR("CODIGO_TRABAJADOR");
	
	
	private final String value;

    private FrmNominaDianControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
