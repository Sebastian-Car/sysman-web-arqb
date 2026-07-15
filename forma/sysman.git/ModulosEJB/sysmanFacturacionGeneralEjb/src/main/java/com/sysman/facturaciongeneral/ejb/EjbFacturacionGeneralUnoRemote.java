/*-
 * EjbFacturacionGeneralUnoRemote.java
 *
 * 1.0
 * 
 * 1/12/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.facturaciongeneral.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.ejb.Remote;

@Remote
public interface EjbFacturacionGeneralUnoRemote
{

    void cargarConceptos(
        String compania,
        int anio,
        String tipocobro,
        String codigoCobro,
        int grupoConceptos,
        Date fechaSolicitud,
        int cantidadGrupo,
        String usuario)
                    throws SystemException;

    void ejecutarAnulacionFacturas(
        String compania,
        String tipocobro,
        int anio,
        BigInteger facturainicial,
        BigInteger facturafinal,
        String usuario)
                    throws SystemException;

    BigDecimal cargarValorConceptoCant(
        double valor,
        int cantidad,
        int digito) throws SystemException;

    BigDecimal cargarValorConceptoIndica(
        boolean indicador,
        BigDecimal valor,
        int digito) throws SystemException;

	String cambiarFechaFactura(
			String compania, 
			String anio, 
			String tipoFactura, 
			String solicitud,
			String FacturaInicial, 
			String FacturaFinal, 
			Date fecha, 
			String usuario) throws SystemException;
}
