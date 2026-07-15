package com.sysman.presupuesto.enums;

public enum EjecucionporfuenteControladorEnumUrl {
	
	URL3655("EJECUCIONPORFUENTECONTROLADORENUMURL3655",  "4001"),
	
	URL58412("EJECUCIONPORFUENTECONTROLADORENUMURL58412", "34001"),
	
	URL5723("EJECUCIONPORFUENTECONTROLADORENUMURL5723", "471049"),
	
	URL1766001("EJECUCIONPORFUENTECONTROLADORENUMURL1766001", "1766001");
	
	 private final String key;
	    private final String value;

	    private EjecucionporfuenteControladorEnumUrl(String key,
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
