package com.sysman.presupuesto.enums;

public enum FrmLisantepptogastosControladorUrlEnum {

    URL001("AIMREGISTROEJECUCGASTOSCXPSCONTROLADORURL001", "4007"),

    URL002("AIMREGISTROEJECUCGASTOSCXPSCONTROLADORURL002", "94030"),

    URL003("AIMREGISTROEJECUCGASTOSCXPSCONTROLADORURL003", "94032");

    private final String key;
    private final String value;

    private FrmLisantepptogastosControladorUrlEnum(String key, String value)
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