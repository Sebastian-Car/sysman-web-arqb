/*-
 * DatosTransaccionControlador.java
 *
 * 1.0
 * 
 * 18/09/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */	

package com.sysman.transautomaticas.enums;

public enum DatosTransaccionControladorUrlEnum {

    URL1719("DTRANSACCIONMODELOSCONTROLADORURL2144","1719001"),
    
    URL4("DTRANSACCIONMODELOSCONTROLADORURL2144","4001"), 
    
    URL140("DTRANSACCIONMODELOSCONTROLADORURL2144","14040"), 
    
    URL200("DTRANSACCIONMODELOSCONTROLADORURL2144","20003"), 
    
    URL130("DTRANSACCIONMODELOSCONTROLADORURL2144","13001"),
    
    URL340("DTRANSACCIONMODELOSCONTROLADORURL2144","34001"),
    
    URL230("DTRANSACCIONMODELOSCONTROLADORURL2144","23040") ,
    
    URL172("DTRANSACCIONMODELOSCONTROLADORURL2144","1720003"),
    
    URL20("DTRANSACCIONMODELOSCONTROLADORURL","20071"), // centro costo nombre constante
     
	URL14("DTRANSACCIONMODELOSCONTROLADORURL2144","14175"), //tercero nombre constante
	
	URL23("DTRANSACCIONMODELOSCONTROLADORURL2144","23042"), //auxiliar nombre constante
	
	URL34("DTRANSACCIONMODELOSCONTROLADORURL2144","34042"), //fuente recurso nombre constante
	
	URL13("DTRANSACCIONMODELOSCONTROLADORURL2144","13034"), //referencia nombre constante
	
    URL2144("DTRANSACCIONMODELOSCONTROLADORURL2144","30002"), //tipoGasto 1743001
    
    URL2145("DTRANSACCIONMODELOSCONTROLADORURL2144","49005"), //ConceptoDian
    
    URL2146("DTRANSACCIONMODELOSCONTROLADORURL2144","1744001"),//Medios de Pago 
    
    URL161("DTRANSACCIONMODELOSCONTROLADORURL2144","16162"), //cuenta bancos
    
    URL1720("DTRANSACCIONMODELOSCONTROLADORURL2144","1720006"),
    
    URL1721("DTRANSACCIONMODELOSCONTROLADORURL2144","1723001"), //comprobante creado
    //reporte
    URL001("DTRANSACCIONMODELOSCONTROLADORURL2144", "59008"),
    
    URL002("DTRANSACCIONMODELOSCONTROLADORURL2144", "15023"),
    
    URL003("DTRANSACCIONMODELOSCONTROLADORURL003", "1723003"),
    
    URL004("DTRANSACCIONMODELOSCONTROLADORURL004", "25008"), //Tipo Comprobante Pptal
    
    URL005("DTRANSACCIONMODELOSCONTROLADORURL005", "75001") // Numero Comprobante Pptal
    ;
            
    private final String key;
    private final String value;
    
    private  DatosTransaccionControladorUrlEnum(String key, String value) {
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
