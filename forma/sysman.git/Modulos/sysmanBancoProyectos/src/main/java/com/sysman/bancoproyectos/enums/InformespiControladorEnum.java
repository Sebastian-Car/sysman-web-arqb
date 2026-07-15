package com.sysman.bancoproyectos.enums;

public enum InformespiControladorEnum {
	 PARAM0("MESINICIAL");

    private final String value;

    private InformespiControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
