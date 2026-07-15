/*
 * FrmRegistroPagosControladorUrlEnum
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
public enum FrmRegistroPagosControladorUrlEnum {

    URL9195("FRMREGISTROPAGOSCONTROLADORURL9195",
                    "16149"),

    URL11561("FRMREGISTROPAGOSCONTROLADORURL11561", "665020"),

    URL12283("FRMREGISTROPAGOSCONTROLADORURL12283",
                    " listaTercero = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, \":FR1463_nuevo:TBCB4800\", \"SELECT \" + \" FACTURA_SF.TERCERO, \" + \" FACTURA_SF.SUCURSAL, \" + \" TERCERO.NOMBRE, \" + \" NUMERO_FACTURA, \" + \" VALOR_TOTAL, \" + \" OBSERVACIONES, \" + \" CENTRO_COSTO, \" + \" AUXILIAR, \" + \" TIPO_AP, \" + \" CODIGO_AP, \" + \" CUOTA_ACUERDO, \" + \" NZ(ESACUERDO, 0), \" + \" NZ(INTERFAZADA, 0), \" + \" ANO_CPTE, \" + \" TIPO_CPTE, \" + \" NRO_CPTE, \" + \" NZ(AFECTO_INVENTARIO, 0), \" + \" NZ(DIFERIDA, 0), \" + \" TIPO_ABONO, \" + \" NRO_ABONO, \" + \" CUENTA_RECAUDO, \" + \" FECHA_EXPEDICION, \" + \" FECHA_VENCIMIENTO \" + \" FROM \" + \" FACTURA_SF \" + \" INNER JOIN TERCERO \" + \" ON \" + \" (FACTURA_SF.SUCURSAL = TERCERO.SUCURSAL) \" + \" AND \" + \" (FACTURA_SF.TERCERO = TERCERO.NIT) \" + \" AND \" + \" (FACTURA_SF.COMPANIA = TERCERO.COMPANIA) \" + \" WHERE \" + \" FACTURA_SF.COMPANIA = '001' \" + \" AND \" + \" TIPO_FACTURA = 'FAL' \" + \" AND \" + \" FECHA_PAGO IS NULL \" + \" AND \" + \" NZ(ANULADA, 0) = 0 \" + \" ORDER BY \" + \" NUMERO_FACTURA\","),

    URL15013("FRMREGISTROPAGOSCONTROLADORURL15013", "661035"),

    URL15014("FRMREGISTROPAGOSCONTROLADORURL15014", "661037"),

    URL10617("FRMREGISTROPAGOSCONTROLADORURL10617",
                    " listaCompRecaudo = service.getListado(ConectorPool.ESQUEMA_SYSMAN, \"SELECT \" + \" TIPO_COMPROBANTE.CODIGO, \" + \" TIPO_COMPROBANTE.NOMBRE \" + \" FROM \" + \" TIPO_COMPROBANTE \" + \" WHERE \" + \" (\" + \" ((TIPO_COMPROBANTE.COMPANIA) = '\" + compania + \"') \" + \" AND \" + \" ((TIPO_COMPROBANTE.CLASE_CONTABLE) = 'I')\" + \" ) \" + \" \");"),

    URL10618("FRMREGISTROPAGOSCONTROLADORURL10618", "15053"),

    URL10619("FRMREGISTROPAGOSCONTROLADORURL10619", "16117"),
    
    URL4444("FRMREGISTROPAGOSCONTROLADORURL4444", "665021"), 
    
    URL1928003("FRMREGISTROPAGOSCONTROLADORURL1928003", "1928003"), 
    
    URL666020("FRMREGISTROPAGOSCONTROLADORURL666020", "666020"),
    
    URL665034("FRMREGISTROPAGOSCONTROLADORURL665034","665034");

    private final String key;
    private final String value;

    private FrmRegistroPagosControladorUrlEnum(String key, String value) {
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
