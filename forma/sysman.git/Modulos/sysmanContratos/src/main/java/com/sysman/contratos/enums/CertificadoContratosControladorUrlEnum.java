package com.sysman.contratos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum CertificadoContratosControladorUrlEnum {

    URL4313("CERTIFICADOCONTRATOSCONTROLADORURL4313", "14038"),

    URL3642("CERTIFICADOCONTRATOSCONTROLADORURL3642", "14036"),

    URL5270("CERTIFICADOCONTRATOSCONTROLADORURL5270", "73012"),

    URL6095("CERTIFICADOCONTRATOSCONTROLADORURL6095", "73014"),

    URL104078("CERTIFICADOCONTRATOSCONTROLADORURL104076", "104078");

    private final String key;
    private final String value;

    private CertificadoContratosControladorUrlEnum(String key, String value) {
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
