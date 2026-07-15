/**
 * 
 */
package com.sysman.bancoproyectos.enums;

/**
 * @author avega
 *
 */
public enum FrmimpresionSolicitudcdpControladorUrlEnum {
    
     URL4001("FRMIMPRESIONSOLICITUDCDPCONTROLADORURL4001", "4001"),	 
     URL62007("FRMIMPRESIONSOLICITUDCDPCONTROLADORURL62072", "62007"),
     URL130058("FRMIMPRESIONSOLICITUDCDPCONTROLADORURL130058", "130058");

	
	private final String key;
    private final String value;

    private FrmimpresionSolicitudcdpControladorUrlEnum(String key, String value)
    {
        this.key = key;
        this.value = value;
    }

    public String getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }
}