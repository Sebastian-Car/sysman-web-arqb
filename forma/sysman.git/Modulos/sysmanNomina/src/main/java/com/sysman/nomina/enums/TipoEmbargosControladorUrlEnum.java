/*
 * TipoEmbargosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.nomina.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum TipoEmbargosControladorUrlEnum {

    URL4256("TIPOEMBARGOSCONTROLADORURL4256",
                    " listaconceptoDescuentoE = new RegistroDataModel( ConectorPool.ESQUEMA_SYSMAN, \":FRFR78:TBCB4645\", \"SELECT Conceptos.ID_de_Concepto,\" + \" Conceptos.Nombre_Concepto\" + \" FROM Conceptos\" + \" WHERE Conceptos.ID_de_Concepto >= '700'\" + \" AND Conceptos.ID_de_Concepto <='747'\" + \" AND Conceptos.Compania = '\" + compania + \"'\" + \" ORDER BY Conceptos.Nombre_Concepto\","),

    URL5121("TIPOEMBARGOSCONTROLADORURL5121",
                    "151001"),

    URL5790("TIPOEMBARGOSCONTROLADORURL5790",
                    " listaconceptoPorcentajeE = new RegistroDataModel( ConectorPool.ESQUEMA_SYSMAN, \":FRFR78:TBCB4646\", \"SELECT Conceptos.ID_de_Concepto,\" + \" Conceptos.Nombre_Concepto\" + \" FROM Conceptos\" + \" WHERE Conceptos.Compania = '001'\" + \" ORDER BY Conceptos.ID_de_Concepto\","),

    URL3430("TIPOEMBARGOSCONTROLADORURL3430",
                    "151026");

    private final String key;
    private final String value;

    private TipoEmbargosControladorUrlEnum(String key, String value) {
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
