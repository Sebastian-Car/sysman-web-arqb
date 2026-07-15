package com.sysman.contabilidad.enums;

public enum ComprobantecntbancosControladorEnum {
	
	PARAM0("COMPANIA"), PARAM1("ANO"), PARAM2("CLASE"), PARAM3("TIPO"),
	
	ESCENTRO("ESCENTRO"),
	
	LISTAAFECTAR("LISTAAFECTAR");

    private final String value;

    private ComprobantecntbancosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
