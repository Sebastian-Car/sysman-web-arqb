package com.sysman.servpublicos.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.interceptor.BusinessInterceptor;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.servpublicos.ejb.EjbServiciosPublicosUnoGeneralLocal;
import com.sysman.servpublicos.ejb.EjbServiciosPublicosUnoGeneralRemote;
import com.sysman.util.SysmanFunciones;

import java.sql.Types;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 * Session Bean implementation class EjbServiciosPublicosUnoGeneral
 */
@Stateless
@LocalBean
@Interceptors(BusinessInterceptor.class)
public class EjbServiciosPublicosUnoGeneral
                implements EjbServiciosPublicosUnoGeneralRemote,
                EjbServiciosPublicosUnoGeneralLocal {

    /**
     * Default constructor.
     */
    public EjbServiciosPublicosUnoGeneral() {
        //
    }

    @Override
    public boolean actualizarMedidores(
        String compania,
        int ciclo,
        String usuario)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CICLO             =>",
                                Integer.toString(ciclo), ", ",
                                "UN_USUARIO           =>'", usuario, "' " };

        byte rta = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM1.FC_ACTUALIZAMEDIDORSINESTFEC",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return rta != 0;
    }

}
