package com.sysman.presupuesto.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.interceptor.BusinessInterceptor;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.ejb.EjbPresupuestoCeroGeneralLocal;
import com.sysman.presupuesto.ejb.EjbPresupuestoCeroGeneralRemote;
import com.sysman.util.SysmanFunciones;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 * Session Bean implementation class PresupuestoCeroGeneral
 */
@Stateless
@LocalBean
@Interceptors(BusinessInterceptor.class)
public class EjbPresupuestoCeroGeneral
                implements EjbPresupuestoCeroGeneralRemote,
                EjbPresupuestoCeroGeneralLocal {
    /**
     * Default constructor.
     */
    public EjbPresupuestoCeroGeneral() {
    }

    @Override
    public void insertarAuxiliarenPresupuesto(
        String compania,
        int ano,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PRESUPUESTO.PR_GENERAR_AUXILIAR",
                        SysmanFunciones.concatenar(parametros));
    }

}