package com.sysman.almacen.enums;

public enum FrmDepreciacionAcumuladaControladorEnum {
	
	CODIGOELEMENTO("CODIGOELEMENTO"),
	
	TIPOELEMENTO("TIPOELEMENTO"),
	
	NOMBRELARGO("NOMBRELARGO"),
	
	NOMBREELEMENTO("NOMBREELEMENTO"),
	
	VALOR_HISTORICO("VALOR_HISTORICO"),
	
	VIDA_UTIL("VIDA_UTIL"),
	
	VALOR_ACUMULADO("VALOR_ACUMULADO"),
	
	VALOR_LIBROS("VALOR_LIBROS"),
	
	NIIF_VLRLIBROS("NIIF_VLRLIBROS");
	
	 private final String value;

    private FrmDepreciacionAcumuladaControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
