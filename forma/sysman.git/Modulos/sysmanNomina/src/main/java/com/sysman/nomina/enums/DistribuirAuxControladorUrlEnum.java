package com.sysman.nomina.enums;

public enum DistribuirAuxControladorUrlEnum {
	
	URL0001("DISTRIBUIRAUXCONTROLADORURLENUM001", "210027"),
	
	URL0002("DISTRIBUIRAUXCONTROLADORURLENUM002", "20003"),
	
    URL0003("DISTRIBUIRAUXCONTROLADORURLENUM003", "23006"),
    
    URL0004("DISTRIBUIRAUXCONTROLADORURLENUM004", "1944002"),
    
    URL0005("DISTRIBUIRAUXCONTROLADORURLENUM004", "1944003"),
    
    URL0006("DISTRIBUIRAUXCONTROLADORURLENUM004", "1944004"),
    
    URL0007("DISTRIBUIRAUXCONTROLADORURLENUM004", "1945002"),;
	
	private final String key;
    private final String value;

    private DistribuirAuxControladorUrlEnum(String key, String value) {
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
