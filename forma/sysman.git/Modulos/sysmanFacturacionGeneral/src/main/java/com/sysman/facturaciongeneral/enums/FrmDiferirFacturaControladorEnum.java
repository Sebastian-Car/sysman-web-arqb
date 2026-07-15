/*-
 * FrmDiferirFacturaControladorEnum.java
 *
 * 1.0
 * 
 * 9/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.facturaciongeneral.enums;

/**
 * @version 1.0, 9/11/2017
 * @author jcrodriguez
 *
 */
public enum FrmDiferirFacturaControladorEnum {
    DIFERIDA("DIFERIDA"),

    ANOCOBRO("ANOCOBRO"),

    PR_VISIBLE("PR_VISIBLE"),

    NORES_FACTURACION("NORES_FACTURACION"),

    FECRES_FACTURACION("FECRES_FACTURACION"),

    NOINICIAL_FACTURACION("NOINICIAL_FACTURACION"),

    NOFINAL_FACTURACION("NOFINAL_FACTURACION"),

    MANEJA_RESFACTURACION("MANEJA_RESFACTURACION"),

    LEYENDA_FACTURA("LEYENDA_FACTURA"),

    INFORME001489("001489INFFACSTD02"),

    FORMATO_FACTURA("FORMATO_FACTURA"),

    TIPOCOBRO("TIPOCOBRO"),

    TIPO_FACTURA("TIPO_FACTURA"),

    TERCERO("TERCERO"),

    SUCURSAL("SUCURSAL"),

    CENTRO_COSTO("CENTRO_COSTO"),

    AUXILIAR("AUXILIAR"),

    VALOR_TOTAL("VALOR_TOTAL"),

    TIPO_ABONO("TIPO_ABONO"),

    NRO_ABONO("NRO_ABONO"),

    TASA_INTERES("TASA_INTERES"),

    VLR_EFECTIVO("VLR_EFECTIVO"),

    VLR_CREDITO("VLR_CREDITO"),

    CUOTAS_DIFERIDAS("CUOTAS_DIFERIDAS"),

    VLR_PROM_CUOTA("VLR_PROM_CUOTA"),

    FECHA_EXPEDICION("FECHA_EXPEDICION"),

    OBSERVACIONES("OBSERVACIONES"),

    TIPOFACTURA("TIPOFACTURA"),

    CODIGORUTA("CODIGORUTA"),

    NUMERO_FACTURA("NUMERO_FACTURA");

    private final String value;

    private FrmDiferirFacturaControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
