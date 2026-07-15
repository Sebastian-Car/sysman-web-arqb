/*
 * BorrarpagosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum BorrarpagosControladorUrlEnum {

    URL1313("BORRARPAGOSCONTROLADORURL1313",
                    "228004"),
    
    URL6969("BORRARPAGOSCONTROLADORURL6969",
                    "228002"),
    
    URL24498("BORRARPAGOSCONTROLADORURL24498",
                    "276004"),

    URL12035("BORRARPAGOSCONTROLADORURL12035",
                    "276003"),

    URL10938("BORRARPAGOSCONTROLADORURL10938",
                    "Acciones.insertar(ConectorPool.ESQUEMA_SYSMAN, tablaPago,"),

    URL9147("BORRARPAGOSCONTROLADORURL9147",
                    "276001"),

    URL18629("BORRARPAGOSCONTROLADORURL18629",
                    " totalCupones = service.getRegistro(ConectorPool.ESQUEMA_SYSMAN, \" SELECT NVL(COUNT(*),"),

    URL21199("BORRARPAGOSCONTROLADORURL21199",
                    " totalCupones = service.getRegistro(ConectorPool.ESQUEMA_SYSMAN, \" SELECT NVL(COUNT(*),"),

    URL16235("BORRARPAGOSCONTROLADORURL16235",
                    " totalCupones = service.getRegistro(ConectorPool.ESQUEMA_SYSMAN, \" SELECT NVL(COUNT(*),");

    private final String key;
    private final String value;

    private BorrarpagosControladorUrlEnum(String key, String value) {
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
