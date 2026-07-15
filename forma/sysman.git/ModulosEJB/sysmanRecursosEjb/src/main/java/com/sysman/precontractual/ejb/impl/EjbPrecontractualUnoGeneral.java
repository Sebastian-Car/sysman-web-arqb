package com.sysman.precontractual.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.interceptor.BusinessInterceptor;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.precontractual.ejb.EjbPrecontractualUnoGeneralLocal;
import com.sysman.precontractual.ejb.EjbPrecontractualUnoGeneralRemote;
import com.sysman.util.SysmanFunciones;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 * Session Bean implementation class PrecontractualUnoGeneral
 */
@Stateless
@LocalBean
@Interceptors(BusinessInterceptor.class)
public class EjbPrecontractualUnoGeneral implements EjbPrecontractualUnoGeneralRemote, EjbPrecontractualUnoGeneralLocal
{
    /**
     * Default constructor.
     */
    public EjbPrecontractualUnoGeneral()
    {
    }

    @Override
    public void subirCodigosUnspsc(
        String compania,
        String cambios,
        String usuario)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CAMBIOS           =>", cambios, ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN, "PCK_PRECONTRACTUAL1.PR_CARGAR_CODIGO_UNSPSC",
                        SysmanFunciones.concatenar(parametros));
    }
}