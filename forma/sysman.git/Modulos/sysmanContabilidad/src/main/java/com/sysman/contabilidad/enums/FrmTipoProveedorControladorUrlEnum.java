/**
 * 
 */
package com.sysman.contabilidad.enums;

/**
 * @author User
 *
 */
public enum FrmTipoProveedorControladorUrlEnum {
	
    URL16221("FRMTIPOPROVEEDORCONTROLADORURL16221", "16221"), 
    
    URL4001("FRMTIPOPROVEEDORCONTROLADORURL4001", "4001");

    private final String key;
    private final String value;

    private FrmTipoProveedorControladorUrlEnum(String key, String value) {
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
