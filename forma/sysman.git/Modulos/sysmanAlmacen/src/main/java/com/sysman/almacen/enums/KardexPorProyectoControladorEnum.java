package com.sysman.almacen.enums;

public enum KardexPorProyectoControladorEnum {
	

	PROYECTOINI("CODIGOINICIAL"),
	
    TIPOELEMENTO("TIPOELEMENTO"),
	
	ELEMENTODESDE("ELEMENTODESDE");

    private final String value;

	
	private KardexPorProyectoControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    
}
