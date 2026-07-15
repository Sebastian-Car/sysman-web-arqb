/*-
 * EjbPlanDesarrolloCeroGeneralRemote.java
 *
 * 1.0
 * 
 * 26/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.plandesarrollo.ejb;

import com.sysman.exception.SystemException;

import javax.ejb.Remote;

@Remote
public interface EjbPlanDesarrolloCeroGeneralRemote {

    long cargarNivel(
        String compania,
        int vigencia,
        String codigo)
                    throws SystemException;

    int obtenerDigitosMetaProduccion()
                    throws SystemException;

    int obtenerDigitosMetaResultado()
                    throws SystemException;

    int obtenerDigitosAccion()
                    throws SystemException;

    void cuadrarSaldos(
        String compania,
        int vigencia) throws SystemException;
}