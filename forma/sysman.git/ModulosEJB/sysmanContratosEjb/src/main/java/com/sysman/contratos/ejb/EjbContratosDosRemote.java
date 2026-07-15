/*-
 * EjbContratosDosRemote.java
 *
 * 1.0
 * 
 * 11/08/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contratos.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Remote;

@Remote
public interface EjbContratosDosRemote {

    void registrarCesion(
        String compania,
        long numero,
        String tipocontratoin,
        long numeroafectado,
        String tipoafectado,
        String usuario,
        String mensaje,
        String nittercero,
        String sucursalCesion)
                    throws SystemException;

    void afectarItem(String compania, long numero, String tipoContrato,
        long numeroAfectado, String tipoAfectado, String usuario)
                    throws SystemException;

    boolean enviarNominaCesion(String compania, long numeroAfectado,
        String tipoAfectado, String nitCesion, Date fechaInicial,
        Date fechaFinal, BigDecimal valorTotal, String nombreCesion,
        String usuario) throws SystemException;

    boolean actDespNovedadContr(String compania, String strTipoT,
        String claseOrden, long numero,long novedad, String nvlPEjecucion, Date fechaInicial,
        Date fechaFinal, Date fechaVencimiento, String valorTotal, String diasContrato)
                    throws SystemException;

    boolean eliminarNovedadContrato(String compania, String cTipoT,
        String cClaseT, String claseOrden, long numero, long novedad,
        long ordenCompra) throws SystemException;

    BigDecimal actualizarPSubcontrato(String compania, long ordendeSuministro,
        long codigo, long cantidadAnterior, long cantidad,
        long ordenDeCompra, long codigoOrden, String tipoAfectado,
        long numeroAfectado, String claseOrden, String claseOrdenAnt,
        long numeroOrdenAnt, String usuario) throws SystemException;

    void modificarCantidadContrato(String compania, String claseOrden,
        long ordenDeCompra, long codigo, String campo, String valorAnterior,
        String valorAfectar, String usuario) throws SystemException;

    void insertarRubrosOrdenPpto(String compania, String clase, long numero,
        String tipoPpto, long numeroPpto, String fechaSelec, String usuario,
        int contador, String tercero, String sucursal) throws SystemException;

    void actualizarCumplimientoActividades(String compania, long codContrato,
        String tipoContrato, long codigoNovedad, String tipoNovedad,
        long codigoActa, String usuario) throws SystemException;
}