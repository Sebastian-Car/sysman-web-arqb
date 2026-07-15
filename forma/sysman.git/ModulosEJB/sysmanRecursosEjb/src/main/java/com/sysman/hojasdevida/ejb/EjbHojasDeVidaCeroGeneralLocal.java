/*-
 * EjbHojasDeVidaCeroGeneralLocal.java
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

import javax.ejb.Local;

/**
 * 
 * @version 1.0, 6/02/2018
 * @author ybecerra
 *
 */
@Local
public interface EjbHojasDeVidaCeroGeneralLocal {

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
