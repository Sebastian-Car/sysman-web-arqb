package com.sysman.contabilidad.ejb.impl;

import com.sysman.contabilidad.ejb.EjbContabilidadCeroGeneralLocal;
import com.sysman.contabilidad.ejb.EjbContabilidadCeroGeneralRemote;
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
 * Session Bean implementation class EjbContabilidadCeroGeneral
 */
@Stateless
@LocalBean
@Interceptors(BusinessInterceptor.class)
public class EjbContabilidadCeroGeneral
                implements EjbContabilidadCeroGeneralRemote,
                EjbContabilidadCeroGeneralLocal {

    /**
     * Default constructor.
     */
    public EjbContabilidadCeroGeneral() {
    }

    @Override
    public String verificarInconsistencias(
        String compania,
        int ano)
                    throws SystemException {
        try {
            String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
                                   "UN_ANO               =>",
                                   Integer.toString(ano)
            };

            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD.FC_VERIFICAINCONSISTENCIAS",
                            SysmanFunciones.concatenar(parametro),
                            Types.CLOB));
        }
        catch (SQLException | IOException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void cambiarNitTerceros(
        String nueCompania,
        String nueNit,
        String nueSucursal,
        String antCompania,
        String antNit,
        String antSucursal, String usuario)
                    throws SystemException {

        String[] parametro = { "UN_NUE_COMPANIA      =>'", nueCompania, "', ",
                               "UN_NUE_NIT           =>'", nueNit, "', ",
                               "UN_NUE_SUCURSAL      =>'", nueSucursal, "', ",
                               "UN_ANT_COMPANIA      =>'", antCompania, "', ",
                               "UN_ANT_NIT           =>'", antNit, "', ",
                               "UN_ANT_SUCURSAL      =>'", antSucursal, "', ",
                               "UN_USUARIO           =>'", usuario, "' "
        };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTABILIDAD.PR_CAMBIOSDENITATERCERO",
                        SysmanFunciones.concatenar(parametro));
    }
    
    @Override
    public String validarCuentaUtilizar(
    		String compania,
            int ano,
            String cuenta,
            boolean validaBloqueo)
                        throws SystemException {
            String[] parametro = { "UN_COMPANIA  =>'", compania, "' ",
                                   ", UN_ANO     =>", Integer.toString(ano),
                                   ", UN_CUENTA  =>'", cuenta, "'",
                                   ", UN_VALIDABLOQUEADO  =>", validaBloqueo?"-1":"0"
            };
            return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIDAD.FC_VALIDARCUENTAUTILIZAR",
                            SysmanFunciones.concatenar(parametro),
                            Types.VARCHAR);

    }
}
