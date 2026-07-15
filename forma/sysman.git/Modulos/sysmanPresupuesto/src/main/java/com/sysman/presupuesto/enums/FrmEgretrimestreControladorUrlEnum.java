package com.sysman.presupuesto.enums;

public enum FrmEgretrimestreControladorUrlEnum {
    URL001("AIMREGISTROEJECUCGASTOSCXPSCONTROLADORURL4395", "4007"),

    URL002("AIMREGISTROEJECUCGASTOSCXPSCONTROLADORURL5616", "94030"),

    URL003("AIMREGISTROEJECUCGASTOSCXPSCONTROLADORURL6657", "94032");

    private final String key;
    private final String value;

    private FrmEgretrimestreControladorUrlEnum(String key, String value)
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
