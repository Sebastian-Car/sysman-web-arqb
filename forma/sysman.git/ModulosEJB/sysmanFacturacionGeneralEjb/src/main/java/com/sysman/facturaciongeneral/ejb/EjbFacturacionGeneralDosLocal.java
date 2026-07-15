/*-
 * EjbFacturacionGeneralDosLocal.java
 *
 * 1.0
 * 
 * 7/12/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.facturaciongeneral.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Local;

@Local
public interface EjbFacturacionGeneralDosLocal {

    Date calcularFechaVencimiento(
        String compania,
        String tipoCobro,
        boolean aplicaRel,
        String codigoCobro)
                    throws SystemException;

    void validarConcInteresesFin(
        String compania,
        int ano)
                    throws SystemException;

    String verificarFactura(
        String compania,
        String tipoFactura,
        long noFactura,
        BigDecimal tasa,
        int cuotas,
        BigDecimal efectivo,
        String tipoCobro,
        int ano,
        String usuario)
                    throws SystemException;

    void facturarConceptos(
        String compania,
        String tipoCobro,
        long codigoCobro,
        long nroFactura,
        int anio,
        String usuario)
                    throws SystemException;

    String cargarArchivoAsobancaria(
        String compania,
        String banco,
        String usuario,
        String cadena) throws SystemException;

    BigDecimal hallarConceptoFormula(
        String compania,
        String formula,
        int ano) throws SystemException;

    BigDecimal retornarValorAnual(
        String compania,
        int anio, String campo,
        boolean salarioMen) throws SystemException;

    void calculoFacturacionCorabastos(String compania, int anofacturar,
        int mesfacturar, String tipofactura, Date fechafacturacion,
        Date fechalimite, Date fechalimite1, String cci, String ccf,
        String descripcion, String strusuario, long facturaciontotal)
                    throws SystemException;
    
    public void calculoFacturacionSinContrato(String compania,
	        int anofacturar,
	        int mesfacturar,
	        String tipofactura,
	        Date fechafacturacion,
	        Date fechalimite,
	        Date fechalimite1,
	        String ubicacionInicial,
	        String ubicacionFinal,
	        String terceroInicial,
	        String terceroFinal,
	        String inmuebleInicial,
	        String inmuebleFinal,
	        String descripcion,
	        String strusuario,
	        long facturaciontotal) throws SystemException;

}
