package com.sysman.nomina.enums;

public enum FrmCalculoDistribucionControladorUrlEnum {

	URL7836("INFRESOLUCIONESCONTROLADORURL7836", "104050"),

	URL4541("INFRESOLUCIONESCONTROLADORURL4541", "471039"),

	URL7027("INFRESOLUCIONESCONTROLADORURL7027", "210045"),

	URL3927("INFRESOLUCIONESCONTROLADORURL3927", "471032"),

	URL3928("INFRESOLUCIONESCONTROLADORURL3928", "471026"),

	URL3929("INFRESOLUCIONESCONTROLADORURL3929", "537004"),
	
	URL3930("INFRESOLUCIONESCONTROLADORURL3928", "471035"),;

	private final String key;
	private final String value;

	private FrmCalculoDistribucionControladorUrlEnum(String key, String value) {
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
