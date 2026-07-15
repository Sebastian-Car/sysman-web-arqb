package com.sysman.facturaciongeneral.enums;

public enum InmueblesControladorUrlEnum {
	
	URL18021("INMUEBLESCONTROLADORURLENUM18021", "663039"),
	URL18599("INMUEBLESCONTROLADORURLENUM18599", "5003"),
	URL18011("INMUEBLESCONTROLADORURLENUM18011", "2001"),
	URL1980001("INMUEBLESCONTROLADORURLENUM1980001","1980001"),
	URL1981001("INMUEBLESCONTROLADORURLENUM1981001","1981001"),
	URL1982001("INMUEBLESCONTROLADORURLENUM1982001","1982001");
	
	private final String key;
    private final String value;

    private InmueblesControladorUrlEnum(String key, String value) {
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
