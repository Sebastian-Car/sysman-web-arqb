package com.sysman.nomina.enums;

public enum DiscoCesantiasFnaCbyControladorUrlEnum {


	URL3933("ACUMULADOSUNOCONTROLADORURL3933","471002"),  
	URL5973("ACUMULADOSUNOCONTROLADORURL5973","471009"),  
	URL4509("ACUMULADOSUNOCONTROLADORURL4509","471008"),  
	URL5057("ACUMULADOSUNOCONTROLADORURL5057","7027"),  
	URL8762("ACUMULADOSUNOCONTROLADORURL8762","537003"),
	URL8763("ACUMULADOSUNOCONTROLADORURL8763","629005");

	private final String key;
	private final String value;

	private  DiscoCesantiasFnaCbyControladorUrlEnum(String key, String value) {
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
