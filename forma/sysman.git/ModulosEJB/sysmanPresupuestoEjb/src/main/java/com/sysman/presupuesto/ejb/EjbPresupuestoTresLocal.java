/*-
 * EjbPresupuestoTresLocal.java
 *
 * 1.0
 *
 * 26/05/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.presupuesto.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.ejb.Local;

@Local
public interface EjbPresupuestoTresLocal {

    long enumerar(
        String compania,
        int ano,
        String tipo)
                    throws SystemException;

    boolean terceroRegistraEmbargo(String compania, int modulo, String tercero,
        String sucursal, String tipocomprobante) throws SystemException;

    boolean configurarParametroDisRes(String compania, int ano,
        String tipocomprobante, BigInteger numero, Date fecha, String clase,
        int modulo) throws SystemException;

    BigDecimal calcularSaldoNeto(String compania, int anio, String tipo,
        BigInteger comprobante, int consecutivo, String cuenta,
        BigDecimal valorDebito, BigDecimal valorCredito)
                    throws SystemException;

    void eliminaNovedad(
        String compania,
        String claseOrden,
        BigInteger ordenDeCompra,
        String claseT,
        String tipoT,
        BigInteger numero) throws SystemException;

    void comprobanteAcopiar(String compania, int ano, String tipo,
        BigInteger numero, String tercero, String sucursal, Date fecha,
        BigInteger numerocopiar, String usuario) throws SystemException;

    BigInteger generarRegistroConTipo(String compania, int ano, String tipo,
        String tercero, String sucursal, String tipoComprobante,
        BigInteger numeroComprobante, String objeto, String usuario)
                    throws SystemException;

    BigDecimal consolidarCompaniasPptales(
        String compania,
        int ano,
        String usuario,
        int nivel) throws SystemException;

    void eliminarCuentaPresupuestal(
        String compania,
        int ano,
        String codigo)
                    throws SystemException;

    void afectarSolicitud(
        String compania,
        long solicitudAfectada,
        long solicitudNueva,
        int tipoSolicitudNueva) throws SystemException;

    void congelarSaldoDetalle(
        String compania,
        int anio,
        String tipo,
        long comprobanteinicial,
        long comprobantefinal) throws SystemException;

    int actualizarValorSolicitado(
        String compania,
        String itemAfectado,
        int vigenciaItemAfectado,
        BigDecimal valorDigitado,
        int codigoItem,
        int tipoSolicitud,
        String accion,
        BigDecimal valorAntiguo,
        String codigo,
        int ano,
        BigDecimal valorRubro,
        String fuente,
        String centroCosto,
        String referencia
        ,int solicitudAfect)
                    throws SystemException;

    void actualizarTerDeoSolicitud(
        String compania,
        long numero,
        String tercero,
        String dependencia) throws SystemException;

    boolean esOrdenador(
        String compania,
        String cedula) throws SystemException;

    boolean actualizarSolicitudesNoAprobadas(
        String compania,
        long solicitud,
        String aprobacion)
                    throws SystemException;

    void revisarAfectacionesPpto(
        String compania,
        int ano) throws SystemException;

    String cargarPlanVigencia(
        String planVig,
        String usuario,
        int opcion) throws SystemException;

    void registrarPrSiif(
        String compania,
        String plano,
        int ano,
        int plantilla,
        String usuario)
                    throws SystemException;
    
    int validarAprociacion(
            String compania,
            String codigo,
            int ano) 
            		 throws SystemException;

}