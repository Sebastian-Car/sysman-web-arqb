package com.sysman.facturaciongeneral.enums;

public enum InformesfactarrendamientoControladorUrlEnum {
	
	URL3639("INFORMESFACTARRENDAMIENTOCONTROLADORURLENUM3639", "14087"),
	
	URL3640("INFORMESFACTARRENDAMIENTOCONTROLADORURLENUM3640", "1947003"),
	
	URL3641("INFORMESFACTARRENDAMIENTOCONTROLADORURLENUM3641", "1947001"),
	
	URL3642("INFORMESFACTARRENDAMIENTOCONTROLADORURLENUM3642", "14075");
	
	
	private final String key;
    private final String value;

    private InformesfactarrendamientoControladorUrlEnum(String key, String value) {
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
