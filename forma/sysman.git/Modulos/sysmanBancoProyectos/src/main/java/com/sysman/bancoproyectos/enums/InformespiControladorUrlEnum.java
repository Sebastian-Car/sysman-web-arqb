package com.sysman.bancoproyectos.enums;

public enum InformespiControladorUrlEnum {
	
	URL001("InformespiControladorUrlEnum001", "4002"),
	URL002("InformespiControladorUrlEnum001", "7045"),
	URL003("InformespiControladorUrlEnum001", "7046");

    private final String key;
    private final String value;

    private InformespiControladorUrlEnum(String key, String value)
    {
        this.key = key;
        this.value = value;
    }

    public String getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }
}
