package com.sysman.facturaciongeneral.enums;

public enum FrmfacturacionlotesControladorUrlEnum {
	
	URL666022("FRMFACTURACIONLOTESCONTROLADOR666022", "666022"), 
	
	URL666024("FRMFACTURACIONLOTESCONTROLADOR666024", "666024"), 
	
	URL661073("FRMFACTURACIONLOTESCONTROLADOR666024", "661073"),

    URL665010("FRMFACTURACIONLOTESCONTROLADOR665010", "665010"),

    URL665023("FRMFACTURACIONLOTESCONTROLADOR665023", "665023");

	private final String key;
	private final String value;

	private FrmfacturacionlotesControladorUrlEnum(String key, String value) {
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
