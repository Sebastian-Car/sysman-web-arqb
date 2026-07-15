package com.sysman.almacen.enums;

public enum FrmDepreciacionAcumuladaInicialControladorUrlEnum {

	 URL112006("FRMDEPRECIACIONACUMULADAINICIALCONTROLADORURL001", "112196"),
	 
	 URL141160("FRMDEPRECIACIONACUMULADAINICIALCONTROLADORURL002", "141160"),
	 
	 URL179008("FRMDEPRECIACIONACUMULADAINICIALCONTROLADORURL002", "179008");

    private final String key;
   private final String value;

   private FrmDepreciacionAcumuladaInicialControladorUrlEnum(String key,
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
