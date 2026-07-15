package com.sysman.nomina.enums;

public enum FrmDistribucionFinalControladorUrlEnum {

	URL210012("FrmDistribucionFinalControladorUrlEnum","210027"),

	URL210014("FrmDistribucionFinalControladorUrlEnum","210014"),

	URL20013("FrmDistribucionFinalControladorUrlEnum","20013"),

	URL20015("FrmDistribucionFinalControladorUrlEnum","20015"),

	URL151001("FrmDistribucionFinalControladorUrlEnum","151001"),

	URL151005("FrmDistribucionFinalControladorUrlEnum","151005"),

	URL23010("FrmDistribucionFinalControladorUrlEnum","23010"),

	URL23019("FrmDistribucionFinalControladorUrlEnum","23019"),
	
	URL210166("FrmDistribucionFinalControladorUrlEnum","210166");


	private final String key;
	private final String value;

	private  FrmDistribucionFinalControladorUrlEnum(String key, String value) {
		this.key   = key; 
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
}