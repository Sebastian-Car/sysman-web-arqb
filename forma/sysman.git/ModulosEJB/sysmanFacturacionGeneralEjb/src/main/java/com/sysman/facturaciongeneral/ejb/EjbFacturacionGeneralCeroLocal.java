/*-
 * EjbFacturacionGeneralCeroLocal.java
 *
 * 1.0
 * 
 * 10/11/2017
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

import javax.ejb.Local;

@Local
public interface EjbFacturacionGeneralCeroLocal {

    boolean validarConceptoAfectaInventario(
        String compania,
        int ano,
        String tipocobro,
        String concepto)
                    throws SystemException;

    boolean validarManejaInventario(
        String compania,
        int ano,
        String tipocobro)
                    throws SystemException;

    long afectarInventario(
        String compania,
        String tipoasociado,
        BigInteger numeroasociado,
        String tipomovimiento,
        Date fechamov,
        String dependencia,
        String tipocobro,
        String bodegaorigen,
        String bodegadestino,
        String usuario)
                    throws SystemException;

    void eliminarRecaudo(
        String compania,
        String tipofactura,
        BigInteger numeroFactura,
        String usuario) throws SystemException;

    void reversarFacturacion(
        String compania,
        int anio,
        String tipoCobro,
        BigInteger factura,
        BigInteger codCobro,
        String usuario) throws SystemException;

    String consultarAbono(
        String compania,
        String tipoAbono,
        BigInteger abono) throws SystemException;

    void actualizarPagosAbono(String compania, String tipoabono,
        BigInteger abono, String tipofactura, BigInteger nofactura,
        BigInteger codigocobro, Date fechapago, String tipocptpago,
        BigInteger cptepago, String bancopago, String usuario)
                    throws SystemException;

    long insertarElementoConcepto(
        String compania,
        int anio,
        String tipoCobro,
        String elemento,
        double porcUtilidad,
        String usuario)
                    throws SystemException;

    void facturarEnSerie(String compania, int anio, String tipocobro,
        BigInteger contratoinicial, BigInteger contratofinal, String lugar,
        Date fechafactura,
        String usuario) throws SystemException;

    void financiarPredial(String compania, int anio, String tipocobro,
        String codigopredio, String aniosfinanciar, String tercero,
        String sucursal, String usuario) throws SystemException;
    
    BigDecimal retonarTasaDiaria(String compania, int anio, String tipocobro, Date fechacorte) throws SystemException;

}