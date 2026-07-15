/*-
 * EjbPlanDesarrolloCeroGeneral.java
 *
 * 1.0
 * 
 * 26/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.plandesarrollo.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.interceptor.BusinessInterceptor;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.plandesarrollo.ejb.EjbPlanDesarrolloCeroGeneralLocal;
import com.sysman.plandesarrollo.ejb.EjbPlanDesarrolloCeroGeneralRemote;
import com.sysman.util.SysmanFunciones;

import java.sql.Types;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 * Session Bean implementation class PlanDesarrolloCeroGeneral
 */
@Stateless
@LocalBean
@Interceptors(BusinessInterceptor.class)
public class EjbPlanDesarrolloCeroGeneral
                implements EjbPlanDesarrolloCeroGeneralRemote,
                EjbPlanDesarrolloCeroGeneralLocal {
    /**
     * Default constructor.
     */
    public EjbPlanDesarrolloCeroGeneral() {
    }

    @Override
    public long cargarNivel(
        String compania,
        int vigencia,
        String codigo)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_VIGENCIA          =>",
                                Integer.toString(vigencia), ", ",
                                "UN_CODIGO            =>'", codigo, "'"
        };
        return (long) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PLAN_DESARROLLO.FC_CARGAR_NIVEL",
                        SysmanFunciones.concatenar(parametros),
                        Types.BIGINT);
    }

    @Override
    public int obtenerDigitosMetaProduccion()
                    throws SystemException {
        String[] parametros = {
        };
        return (int) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PLAN_DESARROLLO.FC_GETM_PRO",
                        SysmanFunciones.concatenar(parametros),
                        Types.INTEGER);
    }

    @Override
    public int obtenerDigitosMetaResultado()
                    throws SystemException {
        String[] parametros = {
        };
        return (int) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PLAN_DESARROLLO.FC_GETM_RES",
                        SysmanFunciones.concatenar(parametros),
                        Types.INTEGER);
    }

    @Override
    public int obtenerDigitosAccion()
                    throws SystemException {
        String[] parametros = {
        };
        return (int) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PLAN_DESARROLLO.FC_GET_ACCION",
                        SysmanFunciones.concatenar(parametros),
                        Types.INTEGER);
    }

    @Override
    public void cuadrarSaldos(
        String compania,
        int vigencia)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_VIGENCIA          =>",
                                Integer.toString(vigencia), ""
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PLAN_DESARROLLO.PR_MANTENIMIENTOCUADRESALDOS",
                        SysmanFunciones.concatenar(parametros));
    }

}