package com.sysman.presupuesto.enums;

public enum FrmdistribucionicldControladorUrlEnum {
	URL21760("FRMDISTRIBUCIONICLDCONTROLADORURLENUM21760", "4001"),
	
	URL1904001("FRMDISTRIBUCIONICLDCONTROLADORURLENUM1904001", "1904001"),
	
	URL1904003("FRMDISTRIBUCIONICLDCONTROLADORURLENUM1904003", "1904003"),
	
	URL1904004("FRMDISTRIBUCIONICLDCONTROLADORURLENUM1904004", "1904004"),
	
	URL34008("FRMDISTRIBUCIONICLDCONTROLADORURLENUM34008", "34008");
	 private final String key;
	    private final String value;

	    private FrmdistribucionicldControladorUrlEnum(String key,
	        String value) {
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
