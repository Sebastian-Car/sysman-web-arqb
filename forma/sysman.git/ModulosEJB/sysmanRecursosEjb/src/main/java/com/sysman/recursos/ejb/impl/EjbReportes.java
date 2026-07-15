package com.sysman.recursos.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbReportesLocal;
import com.sysman.recursos.ejb.EjbReportesRemote;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class EjbSysmanUtil
 */
@Stateless
@LocalBean
public class EjbReportes implements EjbReportesRemote, EjbReportesLocal {
    /**
     * Default constructor.
     */
    public EjbReportes() {
        // METODO NO IMPLEMENTADO
    }

    @Override
    public String resolverConsulta(
        String compania,
        String reporte,
        String usuario)
                    throws SystemException {
        try {
            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMANK,
                            "PCK_REPORTES.FC_RESULVECONSULTA",
                            "UN_COMPANIA          =>'" + compania + "', "
                                + "UN_REPORTE            =>'" + reporte + "', "
                                + "UN_USUARIO            =>'" + usuario + "'",
                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void configurarParametros(String compania, String reporte,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA   =>'", compania, "', ",
                                "UN_REPORTE    =>'", reporte, "', ",
                                "UN_USUARIO    =>'", usuario,
                                "'" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMANK,
                        "PCK_REPORTES.PR_CONFIGURARPARAMETROS",
                        SysmanFunciones.concatenar(parametros));

    }

}