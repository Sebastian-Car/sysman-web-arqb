/*-
 * ListadoParafiscalescentrocostodependenciaControladorUrlEnum.java
 *
 * 1.0
 * 
 * 15/09/2020
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */	

package com.sysman.nomina.enums;

 /**
  * Enumeracion que permite clasificar cada uno de los identificadores 
  * geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
  * 
  * @version 1.0, 15/09/2020
  * @author dcastiblanco
  *
  */
public enum ListadoParafiscalescentrocostodependenciaControladorUrlEnum {
    URL40081("LISTADOPARAFISCALESCENTROCOSTODEPENDENCIACONTROLADORURL40081", "471050"),

    URL40082("LISTADOPARAFISCALESCENTROCOSTODEPENDENCIACONTROLADORURL40082", "537004"),

    URL40083("LISTADOPARAFISCALESCENTROCOSTODEPENDENCIACONTROLADORURL40083", "471008"),
    
    URL40084("LISTADOPARAFISCALESCENTROCOSTODEPENDENCIACONTROLADORURL40084", "471049");

    private final String key;
    private final String value;

    private ListadoParafiscalescentrocostodependenciaControladorUrlEnum(String key, String value)
    {
        this.key = key;
        this.value = value;
    }

    public String getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }

}
