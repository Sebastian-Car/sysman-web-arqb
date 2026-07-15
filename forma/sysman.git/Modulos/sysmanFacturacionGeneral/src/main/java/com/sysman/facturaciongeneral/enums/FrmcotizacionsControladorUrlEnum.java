/*
 * FacturacionconceptosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.facturaciongeneral.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmcotizacionsControladorUrlEnum {

    URL001("FrmcotizacionsControlador001", "20059"), // Centro costo.
    URL002("FrmcotizacionsControlador002", "14001"), // Tercero.
    URL003("FrmcotizacionsControlador003", "23035"), // Auxiliar.
    URL004("FrmcotizacionsControlador004", "663001"), // Conceptos.
    URL005("FrmcotizacionsControlador005", "668002"), // Tarifa.
    URL006("FrmcotizacionsControlador006", "663029"), // ObtenerConceptos
    URL007("FrmcotizacionsControlador007", "1711001"), // TarifaBase
    URL008("FrmcotizacionsControlador008", "678007"),//Valor por concepto
    URL009("FrmcotizacionsControlador009", "1947001"),
    URL010("FrmcotizacionsControlador010", "663043"),
    URL011("FrmcotizacionsControlador010", "683002"),
    URL012("FrmcotizacionsControlador012", "683004"),
    ;

    private final String key;
    private final String value;

    private FrmcotizacionsControladorUrlEnum(String key, String value) {
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
