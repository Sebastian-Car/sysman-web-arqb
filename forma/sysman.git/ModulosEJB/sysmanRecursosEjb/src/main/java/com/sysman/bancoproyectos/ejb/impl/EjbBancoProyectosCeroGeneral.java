package com.sysman.bancoproyectos.ejb.impl;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectosCeroGeneralLocal;
import com.sysman.bancoproyectos.ejb.EjbBancoProyectosCeroGeneralRemote;
import com.sysman.exception.SystemException;
import com.sysman.interceptor.BusinessInterceptor;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.sql.Types;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 * Session Bean implementation class BancoProyectosCeroGeneral
 */
@Stateless
@LocalBean
@Interceptors(BusinessInterceptor.class)
public class EjbBancoProyectosCeroGeneral
                implements EjbBancoProyectosCeroGeneralRemote,
                EjbBancoProyectosCeroGeneralLocal {
    /**
     * Default constructor.
     */
    public EjbBancoProyectosCeroGeneral() {
    }

    @Override
    public BigDecimal insertarVigencias(
        String compania,
        int anioIni,
        int anioFin,
        String opcion,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO_INI          =>",
                                Integer.toString(anioIni), ", ",
                                "UN_ANIO_FIN          =>",
                                Integer.toString(anioFin), ", ",
                                "UN_OPCION            =>'", opcion, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_BANCOS_PROY.FC_INSERTARVIGENCIANUEVA",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

}