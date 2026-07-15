package com.sysman.contabilidad.enums;

public enum FrmGenerarFlujoEfectivoControladorUrlEnum {
	 URL0001("FRMGENERARFLUJOEFECTIVOCONTROLADORURL0001",
             "4001"),

     URL0002("FRMGENERARFLUJOEFECTIVOCONTROLADORURL0002",
             "7001");

private final String key;
private final String value;

private FrmGenerarFlujoEfectivoControladorUrlEnum(String key, String value) {
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
