package com.sysman.contabilizar.ejb.impl;

import com.sysman.contabilizar.ejb.EjbContabilizarNominaTresLocal;
import com.sysman.contabilizar.ejb.EjbContabilizarNominaTresRemote;
import com.sysman.exception.SystemException;
import com.sysman.interceptor.BusinessInterceptor;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 * Session Bean implementation class EjbContabilizarNominaTres
 */
@Stateless
@LocalBean
@Interceptors(BusinessInterceptor.class)
public class EjbContabilizarNominaTres
                implements EjbContabilizarNominaTresRemote,
                EjbContabilizarNominaTresLocal {
    /**
     * Default constructor.
     */
    public EjbContabilizarNominaTres() {
    }

    @Override
    public String contabilizarNominaHBucarama(
        String companianomina,
        int proceso,
        int ano,
        int mes,
        int periodo,
        String usuario)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIANOMINA    =>'", companianomina,
                                    "', ", "UN_PROCESO           =>",
                                    Integer.toString(proceso), ", ",
                                    "UN_ANO               =>",
                                    Integer.toString(ano), ", ",
                                    "UN_MES               =>",
                                    Integer.toString(mes), ", ",
                                    "UN_PERIODO           =>",
                                    Integer.toString(periodo), ", ",
                                    "UN_USUARIO           =>'", usuario, "'"
            };

            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_CONTABILIZAR_NOMINA3.FC_CONTABILIZARNOMINAHBUCARAMA",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }
}