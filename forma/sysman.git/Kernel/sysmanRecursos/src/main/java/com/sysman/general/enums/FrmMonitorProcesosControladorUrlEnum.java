package com.sysman.general.enums;

public enum FrmMonitorProcesosControladorUrlEnum {

	URL0001("MOVIMIENTOSCONTROLADORURLENUM0006", "198400G");

	private final String key;
	private final String value;

	private FrmMonitorProcesosControladorUrlEnum(String key,
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