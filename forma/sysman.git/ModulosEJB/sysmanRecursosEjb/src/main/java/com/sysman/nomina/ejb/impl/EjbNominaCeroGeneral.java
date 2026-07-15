/*-
 * EjbNominaCeroGeneral.java
 *
 * 1.0
 * 
 * 19/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.nomina.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.nomina.ejb.EjbNominaCeroGeneralLocal;
import com.sysman.nomina.ejb.EjbNominaCeroGeneralRemote;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.sql.Types;
import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * 
 * @version 1.0, 19/01/2018
 * @author ybecerra
 *
 */
@Stateless
@LocalBean
public class EjbNominaCeroGeneral implements EjbNominaCeroGeneralRemote,
                EjbNominaCeroGeneralLocal {

    /**
     * Default constructor.
     */
    public EjbNominaCeroGeneral() {
    }

    @Override
    public Date getFechaPeriodoIniFin(
        String compania,
        int proceso,
        int anio,
        int mes,
        int periodo,
        boolean fechainicio,
        boolean total)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PROCESO           =>",
                                Integer.toString(proceso), ", ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_MES               =>",
                                Integer.toString(mes), ", ",
                                "UN_PERIODO           =>",
                                Integer.toString(periodo), ", ",
                                "UN_FECHAINICIO       =>",
                                fechainicio ? "-1" : "0", ", ",
                                "UN_TOTAL             =>", total ? "-1" : "0"
        };
        return (Date) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA.FC_FECHAINIFINPERIODO",
                        SysmanFunciones.concatenar(parametros),
                        Types.DATE);
    }

    @Override
    public String getDatoEmpresa(
        String compania,
        int par)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PAR               =>",
                                Integer.toString(par), ""
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA.FC_NOMBREEMPRESA",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public boolean validarPeriodoActivoNomina(
        String compania,
        int proceso,
        int anio,
        int mes,
        int periodo)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PROCESO           =>",
                                Integer.toString(proceso), ", ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_MES               =>",
                                Integer.toString(mes), ", ",
                                "UN_PERIODO           =>",
                                Integer.toString(periodo), ""
        };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA.FC_PERIODOACTIVADONOMINA",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }
}
