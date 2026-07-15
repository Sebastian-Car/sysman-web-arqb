/*
 * DevolutivoPrestamoControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.almacen.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map<String,String>
 * y disponibles en dicha enumeración.
 */ 
public enum DevolutivoPrestamoControladorEnum {
   ELEMENTO_PADRE("ELEMENTO_PADRE"),
   DESCRIPCION("DESCRIPCION"),
   DEPENDENCIA("DEPENDENCIA"),
   NOMBRELARGO("NOMBRELARGO"),
   SERIE_PADRE("SERIE_PADRE"),
   PLACA("PLACA"),
   ELEMENTO("ELEMENTO"),
    TABLA("DEVOLUTIVO");  


    private final String value;

    private  DevolutivoPrestamoControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
