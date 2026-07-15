package com.sysman.presupuesto.enums;

public enum FrmSubirClasificadoresUrlEnum {

	 URL0001("FRMSUBIRCLASIFICADORESURLENUM0001", "59003"),
	 URL0002("FRMSUBIRCLASIFICADORESURLENUM0002", "1883004"),
	 //URL0003("FRMSUBIRCLASIFICADORESURLENUM0003", "15019"),
	 URL0003("FRMSUBIRCLASIFICADORESURLENUM0003", "25004"),
	 URL0004("FRMSUBIRCLASIFICADORESURLENUM0004", "62004"),
	 URL0005("FRMSUBIRCLASIFICADORESURLENUM0005", "1783001"),
;

	    private final String key;
	    private final String value;

	    private FrmSubirClasificadoresUrlEnum(String key, String value) {
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