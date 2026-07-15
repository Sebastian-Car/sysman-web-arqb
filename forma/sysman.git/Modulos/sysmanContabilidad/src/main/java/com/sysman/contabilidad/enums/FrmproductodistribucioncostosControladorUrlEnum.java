package com.sysman.contabilidad.enums;

public enum FrmproductodistribucioncostosControladorUrlEnum {
	
	URL4001("FRMPRODUCTODISTRIBUCIONCOSTOSCONTROLADORURL4001", "4001"),
	
	URL1938001("FRMPRODUCTODISTRIBUCIONCOSTOSCONTROLADORURL1938001", "1938001");
	
	private final String key;
    private final String value;

    private FrmproductodistribucioncostosControladorUrlEnum(String key,
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

