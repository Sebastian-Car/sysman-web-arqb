/*
 * PedirTerceroControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum PedirTerceroControladorUrlEnum {
    URL7328("PEDIRTERCEROCONTROLADORURL7328", "25014"), URL8024(
                    "PEDIRTERCEROCONTROLADORURL8024", "14117"),

    URL10748("PEDIRTERCEROCONTROLADORURL10748", "38025"),

    URL21232("PEDIRTERCEROCONTROLADORURL21232", "25030"),

    URL16943("PEDIRTERCEROCONTROLADORURL16943", "25031"),

    URL25842("PEDIRTERCEROCONTROLADORURL25842", "38016"),

    URL30964("PEDIRTERCEROCONTROLADORURL30964",
                    " List<Registro> rsDetalles = service.getListado(conectorPool, \"SELECT CONSECUTIVO, \" + \" CUENTA, \" + \" FECHA, \" + \" NATURALEZA, \" + \" DESCRIPCION, \" + \" VALOR_DEBITO, \" + \" VALOR_CREDITO, \" + \" TIPO_DOCUMENTO, \" + \" NRO_DOCUMENTO, \" + \" CENTRO_COSTO, \" + \" AUXILIAR \" + \" FROM DETALLE_COMPROBANTE_PPTAL \" + \" WHERE COMPANIA='\" + compania + \"' \" + \" AND ANO=\" + anoComprobante + \" \" + \" AND TIPO_CPTE='\" + tipoComprobante + \"' \" + \" AND COMPROBANTE =\" + numeroComprobante + \" \" + \" ORDER BY COMPANIA,ANO,TIPO_CPTE,COMPROBANTE \");"),

    URL22229("PEDIRTERCEROCONTROLADORURL22229",
                    "Acciones.actualizar(conectorPool, cComprobantePptal,"),

    URL35466("PEDIRTERCEROCONTROLADORURL35466",
                    "Acciones.actualizar(conectorPool, cComprobantePptal,"),

    URL34503("PEDIRTERCEROCONTROLADORURL34503",
                    "Acciones.actualizar(conectorPool, cDetalleComprobante,");

    private final String key;
    private final String value;

    private PedirTerceroControladorUrlEnum(String key, String value) {
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
