package com.sysman.servpublicos.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.interceptor.BusinessInterceptor;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.servpublicos.ejb.EjbServiciosPublicosCuatroGeneralLocal;
import com.sysman.servpublicos.ejb.EjbServiciosPublicosCuatroGeneralRemote;
import com.sysman.util.SysmanFunciones;

import java.sql.Types;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 * Session Bean implementation class
 * EjbServiciosPublicosCuatroGeneralRemote
 */
@Stateless
@LocalBean
@Interceptors(BusinessInterceptor.class)
public class EjbServiciosPublicosCuatroGeneral
                implements EjbServiciosPublicosCuatroGeneralRemote,
                EjbServiciosPublicosCuatroGeneralLocal {

    /**
     * Default constructor.
     */
    public EjbServiciosPublicosCuatroGeneral() {
        //
    }

    @Override
    public boolean estarBloqueado(
        String compania,
        int ciclo)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
                               "UN_CICLO             =>",
                               Integer.toString(ciclo), ""
        };
        byte rta = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM4.FC_ESTA_BLOQUEADO",
                        SysmanFunciones.concatenar(parametro),
                        Types.TINYINT);

        return rta != 0;
    }

}
