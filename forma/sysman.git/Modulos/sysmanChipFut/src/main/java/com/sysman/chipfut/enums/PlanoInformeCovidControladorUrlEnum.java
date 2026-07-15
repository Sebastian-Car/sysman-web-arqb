package com.sysman.chipfut.enums;

public enum PlanoInformeCovidControladorUrlEnum {
	URL7828("PLANOINFORMECOVIDCONTROLADORURL7828", "4001");

    private final String key;
    private final String value;

    private PlanoInformeCovidControladorUrlEnum(String key,
        String value)
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
