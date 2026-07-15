package com.sysman.general.enums;

public enum FrmconfigsycControladorUrlEnum {

	URL665006("FRMCONFIGSYCCONTROLADORURL665006", "665006"),
	
	URL662001("FRMCONFIGSYCCONTROLADORURL662001", "662001"),
	
	URL2006001("FRMCONFIGSYCCONTROLADORURL2006001", "2006001");

	private final String key;
	private final String value;

	private FrmconfigsycControladorUrlEnum(String key, String value)
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
