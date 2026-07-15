package com.sysman.nomina.enums;

public enum FrmFactoresliquidacionprimanavidadControladorUrlEnum {

	    URL007("FACTORESLIQUIDACIONPRIMANAVIDAD007","471008"),  
	    URL008("FACTORESLIQUIDACIONPRIMANAVIDAD008","471028"),  
	    URL009("FACTORESLIQUIDACIONPRIMANAVIDAD009","471010");

	    private final String key;
	    private final String value;

	    private  FrmFactoresliquidacionprimanavidadControladorUrlEnum(String key, String value) {
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