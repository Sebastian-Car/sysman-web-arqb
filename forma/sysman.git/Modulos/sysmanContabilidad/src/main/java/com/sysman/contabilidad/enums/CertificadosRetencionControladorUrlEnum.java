/*
 * CertificadosRetencionControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum CertificadosRetencionControladorUrlEnum {

    URL7567("CERTIFICADOSRETENCIONCONTROLADORURL7567",
                    "14033"),

    URL18801("CERTIFICADOSRETENCIONCONTROLADORURL18801",
                    " String claseRet = service.buscarEnLista(tipoRetencion, \"NOMBRE\", \"CLASE\", listaTipoRetencion) == null ? \"\" : service.buscarEnLista(tipoRetencion, \"NOMBRE\", \"CLASE\","),

    URL9217("CERTIFICADOSRETENCIONCONTROLADORURL9217",
                    "16078"),

    URL10434("CERTIFICADOSRETENCIONCONTROLADORURL10434",
                    "14001"),

    URL5278("CERTIFICADOSRETENCIONCONTROLADORURL5278",
                    "8006"),

    URL6522("CERTIFICADOSRETENCIONCONTROLADORURL6522",
                    "59012"),

    URL6985("CERTIFICADOSRETENCIONCONTROLADORURL6985",
                    "14001"),

    URL11046("CERTIFICADOSRETENCIONCONTROLADORURL11046",
                    "14055"),

    URL6678("CERTIFICADOSRETENCIONCONTROLADORURL6678",
                    "4001"),

    URL8231("CERTIFICADOSRETENCIONCONTROLADORURL8231",
                    "16074"),
    
    URL2292("CERTIFICADOSRETENCIONCONTROLADORURL2292",
                    "59020");

    private final String key;
    private final String value;

    private CertificadosRetencionControladorUrlEnum(String key, String value) {
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
