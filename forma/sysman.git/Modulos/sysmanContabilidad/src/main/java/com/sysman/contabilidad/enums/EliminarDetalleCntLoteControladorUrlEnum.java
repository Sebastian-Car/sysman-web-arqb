package com.sysman.contabilidad.enums;

public enum EliminarDetalleCntLoteControladorUrlEnum {
	URL39126("ELIMINARDETALLECNTLOTECONTROLADORURL39126",
            "39126"),
URL1914001("ELIMINARDETALLECNTLOTECONTROLADORURL1914002","1914001");

private final String key;
private final String value;



private EliminarDetalleCntLoteControladorUrlEnum(String key,
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
