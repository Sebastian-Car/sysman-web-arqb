/*-
 * EjbHojasDeVidaCeroGeneralRemote.java
 *
 * 1.0
 * 
 * 6/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.ejb;

import com.sysman.exception.SystemException;

import java.math.BigInteger;

import javax.ejb.Remote;

/**
 * 
 * @version 1.0, 6/02/2018
 * @author ybecerra
 *
 */
@Remote
public interface EjbHojasDeVidaCeroGeneralRemote {

    void calificarEvaluacion(
        String compania,
        int idEmpleado,
        BigInteger evaluacion,
        int claseEvaluacion,
        String usuario)
                        throws SystemException;
    
    void   heredarEvaluacion(
        String compania,
        BigInteger evaluacion,
        int clase,
        int ano, 
        String cedulaEvaluado,
        String sucursalEvaluado,
        String cedulaEvaluador,
        String sucursalEvaluador,
        String tipo,
        String usuario) 
                              throws SystemException;

}
