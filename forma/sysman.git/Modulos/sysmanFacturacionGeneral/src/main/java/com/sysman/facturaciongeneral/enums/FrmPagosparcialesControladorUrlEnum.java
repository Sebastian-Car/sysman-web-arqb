/**
 * 
 */
package com.sysman.facturaciongeneral.enums;

/**
 * @author kmartinez
 *
 */
public enum FrmPagosparcialesControladorUrlEnum {
	
	URL23221("FRMPAGOSPARCIALESCONTROLADORURL", "661066"), //cargar factura con tercero
	
	URL23222("FRMPAGOSPARCIALESCONTROLADORURL", "669004"),
	
	URL23223("FRMPAGOSPARCIALESCONTROLADORURL", "669007"),
	
	URL104019("FRMPAGOSPARCIALESCONTROLADORURL", "104028"),
	
	URL669006("FRMPAGOSPARCIALESCONTROLADORURL", "669006");
	
	private final String key;
    private final String value;
    
    FrmPagosparcialesControladorUrlEnum(String key, String value) {
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
