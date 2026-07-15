package com.sysman.contabilidad.enums;

public enum ActPagosFacturacionControladorUrlEnum {
	
	URL661075("ACTPAGOSFACTURACIONCONTROLADORURL661075", "661075"), 
	
	URL665031("ACTPAGOSFACTURACIONCONTROLADORURL665031","665031");
	
	
	
	private final String key;
    private final String value;

    private ActPagosFacturacionControladorUrlEnum(String key, String value) {
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
