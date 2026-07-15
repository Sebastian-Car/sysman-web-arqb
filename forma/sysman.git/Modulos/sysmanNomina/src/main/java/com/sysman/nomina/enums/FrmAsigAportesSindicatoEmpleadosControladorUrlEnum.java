/*
 * FrmAsigAportesSindicatoEmpleadosControladorUrlEnum
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
public enum FrmAsigAportesSindicatoEmpleadosControladorUrlEnum {

    URL5243("FRMASIGAPORTESSINDICATOEMPLEADOSCONTROLADORURL5243",
                    "210008"),

    URL6263("FRMASIGAPORTESSINDICATOEMPLEADOSCONTROLADORURL6263",
                    "1786001"),

    URL5749("FRMASIGAPORTESSINDICATOEMPLEADOSCONTROLADORURL5749",
                    "210004"),

    URL7204("FRMASIGAPORTESSINDICATOEMPLEADOSCONTROLADORURL7204",
                    " listaCodigoAporteE = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, \":FRFR2056:TBCB6980\", \"SELECT \" + \" CODIGO ,\" + \" CLASE_ID_DE_FONDO,\" + \" FORMA_DESCUENTO ,\" + \" CASE\" + \" WHEN FORMA_DESCUENTO = 'F'\" + \" THEN 'Fijo'\" + \" ELSE 'Porcentual'\" + \" END NOMBRE_FORMA,\" + \" VALOR \" + \" FROM CLASEFONDOAPORTE\" + \" WHERE COMPANIA = :COMPANIA\" + \" ORDER BY CODIGO\",");

    private final String key;
    private final String value;

    private FrmAsigAportesSindicatoEmpleadosControladorUrlEnum(String key,
        String value) {
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
