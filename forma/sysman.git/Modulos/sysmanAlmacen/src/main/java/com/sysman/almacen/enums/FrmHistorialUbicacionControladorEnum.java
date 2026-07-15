package com.sysman.almacen.enums;


public enum FrmHistorialUbicacionControladorEnum {
	
	CODIGOELEMENTO("CODIGOELEMENTO"),
	
	NOMBRELARGO("NOMBRELARGO"),
	
	NOMBREELEMENTO("NOMBREELEMENTO"),
	
	ID_HISTORIAL("ID_HISTORIAL");
	
	 private final String value;

    private FrmHistorialUbicacionControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
