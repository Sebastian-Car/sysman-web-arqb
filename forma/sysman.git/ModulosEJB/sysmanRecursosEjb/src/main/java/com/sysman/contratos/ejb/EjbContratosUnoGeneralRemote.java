/*-
 * EjbContratosUnoRemote.java
 *
 * 1.0
 * 
 * 14/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contratos.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;

import javax.ejb.Remote;

/**
 * 
 * @version 1.0, 14/11/2017
 * @author cmanrique
 *
 */
@Remote
public interface EjbContratosUnoGeneralRemote {

    void insertarSectoresDefault(String compania, String usuario)
                    throws SystemException;

    void actualizarDetallesActividades(String compania, long codEstudio,
        long codContrato, String tipoContrato, String usuario)
                    throws SystemException;

    String extraerValores(
        String compania,
        String claseorden,
        long numero,
        BigDecimal valorfinal,
        String usuario)
                    throws SystemException;

    boolean eliminarOrdendeCompra(
        String compania,
        String claseOrden,
        long numero) throws SystemException;

    void seleccionarRequisiciones(
        String compania,
        String usuario) throws SystemException;

    void insertaPpto(
        String compania,
        String claseOrden,
        long numero,
        String claseDisp,
        long numeroDispSel,
        String fechaSelec,
        String tercero,
        String sucursal,
        String usuario) throws SystemException;

    BigDecimal calculartotalpagos(
        String compania,
        String claseOrden,
        long numero,
        String claseContable)
                    throws SystemException;

    String copiarContrato(
        String compania,
        String claseOrden,
        long copiarDe,
        int vigencia,
        long numero,
        String usuario)
                    throws SystemException;

    BigDecimal ConsecContrato(
        String compania,
        String claseContrato,
        int anioVigencia,
        String usuario)
                    throws SystemException;

    boolean actualizaIvaDetalle(
        String compania,
        String claseOrden,
        long numero,
        BigDecimal porcIvaGlobal,
        String roundValorIvaoc,
        String roundVlrTotaloc,
        String roundValorUnioc,
        BigDecimal digRedoVluniIva,
        BigDecimal digRoundVlrIva,
        BigDecimal digRedonTotal,
        String usuario) throws SystemException;

    BigDecimal getTotalValorNovedad(
        String compania,
        String claseOrden,
        long ordendeCompra,
        String claseNovedad)
                    throws SystemException;

    void importarPrecontractual(String compania, long numeroOrden,
        String claseOrden, long estudioPrevio, String usuario,
        String numProceso) throws SystemException;

}
