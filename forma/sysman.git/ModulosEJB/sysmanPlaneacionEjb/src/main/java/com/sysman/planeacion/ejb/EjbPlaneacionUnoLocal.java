/*-
 * EjbplaneacionUnoLocal.java
 *
 * 1.0
 * 
 * 13/09/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.planeacion.ejb;

import com.sysman.exception.SystemException;

import java.math.BigInteger;

import javax.ejb.Local;

@Local
public interface EjbPlaneacionUnoLocal {

    void registrarCertPlanCompras(String compania, int anio, BigInteger numero,
        String dependencia, String responsable, int mes) throws SystemException;

    int responderPropuesta(
        String compania,
        String usuario,
        long codRequisicion,
        long codPropuesta,
        boolean respondio)
                    throws SystemException;

    boolean actualizarEstadoRespondio(
        String compania,
        BigInteger codPropuesta,
        BigInteger codRequisicion,
        boolean respondio,
        String usuario)
                    throws SystemException;

}