package com.sysman.sysmanauditoriacuentasmedicas.enums;

public enum FrmdevolucionfacradicadasControladorUrlEnum {

    URL4391("FRMDEVOLUCIONFACRADICADASCONTROLADORURL4391",
             "14001"),
	URL4392("FRMDEVOLUCIONFACRADICADASCONTROLADORURL4392",
			"1823001"),
	URL4393("FRMDEVOLUCIONFACRADICADASCONTROLADORURL4393",
			"1823002"),
	URL4394("FRMDEVOLUCIONFACRADICADASCONTROLADORURL4394",
			"1886003"),
	URL4395("FRMDEVOLUCIONFACRADICADASCONTROLADORURL4395",
			"1885001"),
	
	URL1885003("FRMDEVOLUCIONFACRADICADASCONTROLADORURL1885003","1885003"),
	
	URL1823008("FRMDEVOLUCIONFACRADICADASCONTROLADORURLURL1823008","1823008");

	private final String key;
	private final String value;

	private FrmdevolucionfacradicadasControladorUrlEnum(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

}
