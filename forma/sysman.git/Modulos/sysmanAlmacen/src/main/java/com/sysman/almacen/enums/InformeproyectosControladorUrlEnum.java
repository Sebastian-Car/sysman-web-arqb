package com.sysman.almacen.enums;

public enum InformeproyectosControladorUrlEnum {
	
	URL32016("INFORMEPROYECTOSCONTROLADORURL32016","32016"),
	URL32018("INFORMEPROYECTOSCONTROLADORURL32018","32018"),
	URL135009("INFORMEPROYECTOSCONTROLADORURL135009","135009"),
	URL135011("INFORMEPROYECTOSCONTROLADORURL135011","135011"),
	URL112183("INFORMEPROYECTOSCONTROLADORURL112183","112183"),
	URL112185("INFORMEPROYECTOSCONTROLADORURL112185","112185");

    private final String key;
    private final String value;

    private InformeproyectosControladorUrlEnum(String key, String value) {
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
