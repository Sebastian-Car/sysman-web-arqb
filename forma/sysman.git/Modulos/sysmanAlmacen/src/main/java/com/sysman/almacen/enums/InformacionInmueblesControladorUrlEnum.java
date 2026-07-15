package com.sysman.almacen.enums;

public enum InformacionInmueblesControladorUrlEnum {
	
	URL8996("INFORMACIONINMUEBLESURL8996","7001"),
	URL2977("INFORMACIONINMUEBLESURL2977","112166"),
	URL3765("INFORMACIONINMUEBLESURL3765","112170"),
	URL4578("INFORMACIONINMUEBLESURL4578","4001");
	
	private final String key;
    private final String value;

    private InformacionInmueblesControladorUrlEnum(String key, String value) {
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
