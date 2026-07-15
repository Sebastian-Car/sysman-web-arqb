package com.sysman.almacen.enums;

public enum FrmsaldosconsolidadosidiControladorUrlEnum {
	
	URL112158("INFORMACIONGENERALDEVOLUTIVOSCONTROLADORURL112011", "112158"),

    URL112160("INFORMACIONGENERALDEVOLUTIVOSCONTROLADORURL112013", "112160"),
    
    URL59031("INFORMACIONGENERALDEVOLUTIVOSCONTROLADORURL59031", "59031");
    

    private final String key;
    private final String value;

    private FrmsaldosconsolidadosidiControladorUrlEnum(String key,
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
