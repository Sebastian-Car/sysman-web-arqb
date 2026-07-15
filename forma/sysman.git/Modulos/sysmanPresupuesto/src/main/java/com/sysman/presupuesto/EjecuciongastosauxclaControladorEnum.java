package com.sysman.presupuesto;

public enum EjecuciongastosauxclaControladorEnum {
	
	SECTOR("SECTOR"),

    CUENTAINICIAL("CUENTAINICIAL"),

    CENTRO_COSTO("CENTRO_COSTO"),

    NIT("NIT"),

    NITINICIAL("NITINICIAL"),

    FUENTEINICIAL("FUENTEINICIAL"),

    ANIO("ANIO"),
    
    ANO("ANO"),
    
    CODIGOINICIAL("CODIGOINICIAL"),

    CODIGOFINAL("CODIGOFINAL"),

    REFERENCIAINICIAL("REFERENCIAINICIAL"),
    
    CLASECLASIFICADOR("CLASECLASIFICADOR");

    private final String value;

    private EjecuciongastosauxclaControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
