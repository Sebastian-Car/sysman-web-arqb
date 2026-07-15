package com.sysman.kernel.api.clientwso2.util.enums;

public enum UrlServiceUtilEnum {

    URL_RECURSOS("getReg", "urlServiceRecursos"),

    URLUPDATE("updtReg", "application/json"),

    URLID("URLWSO2", "URLWSO2"),

    URLDELET("deleteReg", "application/json");

    private final String key;
    private final String value;

    private UrlServiceUtilEnum(String key, String value)
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
