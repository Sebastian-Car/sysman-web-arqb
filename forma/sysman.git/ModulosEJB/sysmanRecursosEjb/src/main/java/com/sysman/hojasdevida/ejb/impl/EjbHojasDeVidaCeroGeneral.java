/*-
 * EjbHojasDeVidaCeroGeneral.java
 *
 * 1.0
 * 
 * 19/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.ejb.EjbHojasDeVidaCeroGeneralLocal;
import com.sysman.hojasdevida.ejb.EjbHojasDeVidaCeroGeneralRemote;
import com.sysman.interceptor.BusinessInterceptor;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.math.BigInteger;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 * 
 * @version 1.0, 06/02/2018
 * @author ybecerra
 *
 */
@Stateless
@LocalBean
@Interceptors(BusinessInterceptor.class)
public class EjbHojasDeVidaCeroGeneral
                implements EjbHojasDeVidaCeroGeneralRemote,
                EjbHojasDeVidaCeroGeneralLocal {

    /**
     * Default constructor.
     */
    public EjbHojasDeVidaCeroGeneral() {
    }

    @Override
    public void calificarEvaluacion(
        String compania,
        int idEmpleado,
        BigInteger evaluacion,
        int claseEvaluacion,
        String usuario)
                        throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ID_EMPLEADO       =>",
                                Integer.toString(idEmpleado), ", ",
                                "UN_EVALUACION        =>",
                                evaluacion.toString(), ", ",
                                "UN_CLASE_EVALUACION  =>",
                                Integer.toString(claseEvaluacion), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_HOJAS_DE_VIDA.PR_CALIFICAR_EVALUACION",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void heredarEvaluacion(
        String compania, 
        BigInteger evaluacion,
        int clase, 
        int ano, 
        String cedulaEvaluado, 
        String sucursalEvaluado,
        String cedulaEvaluador, 
        String sucursalEvaluador, 
        String tipo,
        String usuario) throws SystemException {
        
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_EVALUACION          =>",
                                evaluacion.toString(), ", ",
                                "UN_CLASE          =>",
                                Integer.toString(clase), ", ",
                                "UN_ANO           =>",
                                Integer.toString(ano), ", ",
                                "UN_EVALUADO             =>'",
                                cedulaEvaluado, "', ",
                                "UN_SUCURSALEVALUADO             =>'",
                                sucursalEvaluado, "', ",
                                "UN_EVALUADOR               =>'",
                                cedulaEvaluador, "', ",
                                "UN_SUCURSALEVALUADOR             =>'",
                                sucursalEvaluador, "', ",
                                "UN_TIPO  =>'", tipo, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_HOJAS_DE_VIDA.PR_HEREDAREVALUACION",
                        SysmanFunciones.concatenar(parametros));
        

    }

}
