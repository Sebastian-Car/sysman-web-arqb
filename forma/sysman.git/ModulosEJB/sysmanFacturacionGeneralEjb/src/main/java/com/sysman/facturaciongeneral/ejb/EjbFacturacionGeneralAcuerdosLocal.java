/*-
 * EjbFacturacionGeneralAcuerdosLocal.java
 *
 * 1.0
 * 
 * 28/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.facturaciongeneral.ejb;

import com.sysman.exception.SystemException;

import java.math.BigInteger;

import javax.ejb.Local;

@Local
public interface EjbFacturacionGeneralAcuerdosLocal {

    String prepararDeudaAcuerdosPago(
        String compania,
        String acuerdonro,
        String seleccion,
        String tipocobro)
                        throws SystemException;

    String generarAcuerdoPreliminar(
        String compania,
        String tipoCobro,
        int anio,
        String tercero,
        String sucursal,
        String deudaCapital,
        String deudaInteres,
        String deudaTotal,
        String cuotaInicial,
        boolean indCondonacion,
        String obsCondonacion,
        String seleccionFact,
        String acuerdoNro,
        double tasa,
        int ncuotas,
        String valorCondonado,
        boolean indSimple,
        double gradiante,
        String usuario)
                        throws SystemException;

    void aprobarAcuerdo(
        String compania,
        String tipoCobro,
        BigInteger acuerdoNro,
        String seleccion,
        String usuario)
                        throws SystemException;

    void eliminarAcuerdoPagoPorTercero(
        String compania,
        String tipoCobro,
        String tercero,
        String sucursal)
                        throws SystemException;

    void facturarAcuerdo(
        String compania,
        int aniocobro,
        String tipocobro,
        String tipoacuerdo,
        BigInteger nroacuerdo,
        int cuota,
        String usuario)
                        throws SystemException;
}
