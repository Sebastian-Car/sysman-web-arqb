package com.sysman.general.enums;

public enum FrmInformesSecBogotaControladorUrlEnum {

	URL0001("MOVIMIENTOSCONTROLADORURLENUM6559", "34043"), // 1684001

	URL0002("MOVIMIENTOSCONTROLADORURLENUM0002", "34045"),
	
	URL0003("MOVIMIENTOSCONTROLADORURLENUM0003", "72105"),
	
	URL0004("MOVIMIENTOSCONTROLADORURLENUM0004", "72107"),
	
	URL0005("MOVIMIENTOSCONTROLADORURLENUM0005", "72109"),
	
	URL0006("MOVIMIENTOSCONTROLADORURLENUM0006", "72111");

	private final String key;
	private final String value;

	private FrmInformesSecBogotaControladorUrlEnum(String key,
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