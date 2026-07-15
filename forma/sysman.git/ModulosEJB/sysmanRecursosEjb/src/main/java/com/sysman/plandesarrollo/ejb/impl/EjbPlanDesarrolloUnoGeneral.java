package com.sysman.plandesarrollo.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.interceptor.BusinessInterceptor;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.plandesarrollo.ejb.EjbPlanDesarrolloUnoGeneralLocal;
import com.sysman.plandesarrollo.ejb.EjbPlanDesarrolloUnoGeneralRemote;
import com.sysman.util.SysmanFunciones;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 * Session Bean implementation class PlanDesarrolloUnoGeneral
 */
@Stateless
@LocalBean
@Interceptors(BusinessInterceptor.class)
public class EjbPlanDesarrolloUnoGeneral implements EjbPlanDesarrolloUnoGeneralRemote, EjbPlanDesarrolloUnoGeneralLocal
{
    /**
     * Default constructor.
     */
    public EjbPlanDesarrolloUnoGeneral()
    {
    }

    @Override
    public void generarMantenimientoPlan(
        String compania,
        int vigencia,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_VIGENCIA          =>", Integer.toString(vigencia), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN, "PCK_PLAN_DESARROLLO1.PR_MANTENIMIENTOPLAN",
                        SysmanFunciones.concatenar(parametros));
    }
}