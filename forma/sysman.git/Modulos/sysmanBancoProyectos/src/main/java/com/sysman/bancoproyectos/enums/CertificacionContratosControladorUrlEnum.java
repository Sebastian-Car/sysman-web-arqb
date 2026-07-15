/*
 * CertificacionContratosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.bancoproyectos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum CertificacionContratosControladorUrlEnum {

    URL8187("CERTIFICACIONCONTRATOSCONTROLADORURL8187",
                    " listaDatos = service.getListado(con, \"SELECT * FROM TEMP_F18_CERTCONTRATOS\");"),

    URL4620("CERTIFICACIONCONTRATOSCONTROLADORURL4620",
                    "34001"),

    URL4239("CERTIFICACIONCONTRATOSCONTROLADORURL4239",
                    "4016"),

    URL8317("CERTIFICACIONCONTRATOSCONTROLADORURL8317",
                    " listaTotales = service.getListado(con, \"SELECT NVL(Sum(OTRASFUENTES),0) OTRASFUENTES, \n\" + \"NVL(Sum(SGR),0) SGR, \n\" + \"NVL(Sum(SALDODISPONIBLE),0) SALDODISPONIBLE, \n\" + \"NVL(Sum(VALORDELOSPAGOS),0) VALORDELOSPAGOS, \n\" + \"NVL(Sum(VALORDIFERENTEANTICIPO),0) VALORDIFERENTEANTICIPO, \n\" + \"NVL(Sum(VALOR_ANTICIPO),0) VALOR_ANTICIPO, \n\" + \"NVL(Sum(VALORTOTAL),0) VALORTOTAL \n\" + \"FROM TEMP_F18_CERTCONTRATOS\");"),

    URL5749("CERTIFICACIONCONTRATOSCONTROLADORURL5749",
                    "34003");

    private final String key;
    private final String value;

    private CertificacionContratosControladorUrlEnum(String key, String value) {
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
