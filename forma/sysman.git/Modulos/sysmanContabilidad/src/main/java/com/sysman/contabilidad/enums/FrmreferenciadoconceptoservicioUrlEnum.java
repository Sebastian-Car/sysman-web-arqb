/**
 * 
 */
package com.sysman.contabilidad.enums;

/**
 * @author User
 *
 */
public enum FrmreferenciadoconceptoservicioUrlEnum {
	
	URL1930002("FrmreferenciadoconceptoservicioURL1930002","1930002"),	
    URL4001("FrmreferenciadoconceptoservicioURL4001", "4001"),    
    URL16221("FrmreferenciadoconceptoservicioURL16221", "16221"), 
    URL15627("FrmreferenciadoconceptoservicioURL13001", "13001");
	
    private final String key;
    private final String value;

    private FrmreferenciadoconceptoservicioUrlEnum(String key, String value) {
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




