/*-
 * TransaccionModelosControlador.java
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

 /**
  * TODO Ingrese una descripcion para la clase.
  * 
  * @version 1.0, 18/09/2018
  * @author asana
  *
  */
public enum TransaccionModelosControladorUrlEnum {

    URL11959("TRANSACCIONMODELOSCONTROLADORURL11959","1719001"),//tipoTransaccion
    
    URL12000("TRANSACCIONMODELOSCONTROLADORURL12000","4001"), //año
    
    URL12012("TRANSACCIONMODELOSCONTROLADORURL12012","14040"), //tercero
    
    URL1812("TRANSACCIONMODELOSCONTROLADORURL1812","20003"), //centrocosto
    
    URL1814("TRANSACCIONMODELOSCONTROLADORURL1814","13001"), //referencia
    
    URL1815("TRANSACCIONMODELOSCONTROLADORURL1815","34001"), ////fuenterecurso
    
    URL1816("TRANSACCIONMODELOSCONTROLADORURL1816","23040"), //auxiliar
    
    URL1817("TRANSACCIONMODELOSCONTROLADORURL1817","1719003"), //tiposComprobantes
    
    URL1818("TRANSACCIONMODELOSCONTROLADORURL1818","1720001"),
    
    URL1819("TRANSACCIONMODELOSCONTROLADORURL1819","1720003"),
    
    URL2143("TRANSACCIONMODELOSCONTROLADORURL2143","1721001"),
    
    URL2144("TRANSACCIONMODELOSCONTROLADORURL2144","30002"), //tipoGasto 1743001
    
    URL2145("TRANSACCIONMODELOSCONTROLADORURL2145","49005"), //ConceptoDian
    
    URL2146("TRANSACCIONMODELOSCONTROLADORURL2146","1744001"),  //Medios de Pago
    
    URL20("DTRANSACCIONMODELOSCONTROLADORURL","20071"), // centro costo nombre constante
    
    URL14("DTRANSACCIONMODELOSCONTROLADORURL2144","14175"), //tercero nombre constante

    URL23("DTRANSACCIONMODELOSCONTROLADORURL2144","23042"), //auxiliar nombre constante

    URL34("DTRANSACCIONMODELOSCONTROLADORURL2144","34042"), //fuente recurso nombre constante

    URL13("DTRANSACCIONMODELOSCONTROLADORURL2144","13034"), //referencia nombre constante
    
    URL002("DTRANSACCIONMODELOSCONTROLADORURL2144","1723002"), //carga datos de la tabla transacciones
    
    ; 
            
    private final String key;
    private final String value;
    
    private  TransaccionModelosControladorUrlEnum(String key, String value) {
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
