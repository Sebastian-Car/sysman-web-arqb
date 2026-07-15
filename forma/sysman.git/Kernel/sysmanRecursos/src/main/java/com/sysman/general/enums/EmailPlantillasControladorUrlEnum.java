/*
 * EmailPlantillasControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.general.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum EmailPlantillasControladorUrlEnum {

    URL7903("EMAILPLANTILLASCONTROLADORURL7903",
                    "1032005"),

    URL7177("EMAILPLANTILLASCONTROLADORURL7177",
                    " listaEmaildestino = service.getListado(ConectorPool.ESQUEMA_SYSMAN, \"SELECT \" + \" COMPANIA , \" + \" PLANTILLA , \" + \" NOMBRE_DESTINATARIO , \" + \" CORREO_DESTINATARIO , \" + \" CREATED_BY , \" + \" MODIFIED_BY , \" + \" DATE_CREATED , \" + \" DATE_MODIFIED \" + \" FROM \" + \" EMAIL_DESTINO\","),

    URL11516("EMAILPLANTILLASCONTROLADORURL11516",
                    "Acciones.eliminar(ConectorPool.ESQUEMA_SYSMAN, \"EMAIL_DESTINO\","),

    URL10221("EMAILPLANTILLASCONTROLADORURL10221",
                    "Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN, \"EMAIL_DESTINO\","),

    URL9021("EMAILPLANTILLASCONTROLADORURL9021",
                    "Acciones.insertar(ConectorPool.ESQUEMA_SYSMAN, \"EMAIL_DESTINO\",");

    private final String key;
    private final String value;

    private EmailPlantillasControladorUrlEnum(String key, String value) {
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
