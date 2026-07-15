package com.sysman.almacen.enums;

public enum KardexPorProyectoControladorUrlEnum {

    URL7809("KARDEXPORPROYECTOCONTROLADORURL7809", "112046"),

    URL11488("KARDEXPORPROYECTOCONTROLADORURLL11488", "112044"),
    
    URL32016("KARDEXPORPROYECTOCONTROLADORURL32016","32016"),
    
    URL32018("KARDEXPORPROYECTOCONTROLADORURL32018","32018");
	
	
	private final String key;
    private final String value;

    private KardexPorProyectoControladorUrlEnum(String key, String value) {
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
