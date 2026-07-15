package com.sysman.contabilidad.enums;


public enum ActualizarFechaComprobantesControladorUrlEnum {
	
	URL0001("ACTUALIZARFECHACOMPROBANTE0001",
            "59003"),
	URL0002("ACTUALIZARFECHACOMPROBANTE0002",
            "4002"),
	URL0003("ACTUALIZARFECHACOMPROBANTE0003",
            "15007"),
	URL0004("ACTUALIZARFECHACOMPROBANTE0004",
            "72113");
	
	private final String key;
    private final String value;

    private ActualizarFechaComprobantesControladorUrlEnum(String key, String value) {
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

    
