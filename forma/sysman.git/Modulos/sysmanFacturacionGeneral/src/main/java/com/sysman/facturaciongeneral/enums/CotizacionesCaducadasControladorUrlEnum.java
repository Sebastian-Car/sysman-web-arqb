package com.sysman.facturaciongeneral.enums;

public enum CotizacionesCaducadasControladorUrlEnum {
	
	URL678010("COTIZACIONESCADUCADASCONTROLADORURL001", "678010"),
	
	URL678012("COTIZACIONESCADUCADASCONTROLADORURL002", "678012"),
	
	URL678013("COTIZACIONESCADUCADASCONTROLADORURL003", "678013"),
	
	URL678014("COTIZACIONESCADUCADASCONTROLADORURL003", "678014");

	 private final String key;
    private final String value;

    private CotizacionesCaducadasControladorUrlEnum(String key,
        String value) {
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
