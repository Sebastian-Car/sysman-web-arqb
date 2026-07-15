/*-
 * EjbServiciosPublicosUtl.java
 *
 * 1.0
 * 
 * 26/07/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.serviciospublicos.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosUtlLocal;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosUtlRemote;
import com.sysman.util.SysmanFunciones;

import java.sql.Types;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class ServiciosPublicosUtl
 * 
 * @version 1.0, 26/07/2017
 * @author jrodrigueza
 */
@Stateless
@LocalBean
public class EjbServiciosPublicosUtl implements EjbServiciosPublicosUtlRemote,
                EjbServiciosPublicosUtlLocal {
    /**
     * Default constructor.
     */
    public EjbServiciosPublicosUtl() {
    }

    @Override
    public boolean obtenerCicloCalculado(
        String compania,
        int ciclo)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CICLO             =>",
                                Integer.toString(ciclo), "" };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_UTL.FC_CICLOCAL",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public boolean validarEmpresaExterna(
        String compania,
        int ciclo,
        String codigoRuta,
        String empresaAseoExt,
        String codigoExterno)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CICLO             =>",
                                Integer.toString(ciclo), ", ",
                                "UN_CODIGORUTA        =>'", codigoRuta, "', ",
                                "UN_EMPRESAASEOEXT    =>'", empresaAseoExt,
                                "', ", "UN_CODIGO_EXTERNO    =>'",
                                codigoExterno, "'" };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_UTL.FC_VALIDAREMPRESAEXTERNA",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public void cargarNovedadesConvenio(
        String compania,
        String codigoInterno,
        int ciclo,
        int ano,
        String periodo,
        String nit,
        String total,
        String cuotasPactadas,
        String cuotaAPagar,
        String capital,
        String interes,
        String otros,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CODIGOINTERNO     =>'", codigoInterno,
                                "', ", "UN_CICLO             =>",
                                Integer.toString(ciclo), ", ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_PERIODO           =>'", periodo, "', ",
                                "UN_NIT               =>'", nit, "', ",
                                "UN_TOTAL             =>'", total, "', ",
                                "UN_CUOTASPACTADAS    =>'", cuotasPactadas,
                                "', ", "UN_CUOTAAPAGAR       =>'", cuotaAPagar,
                                "', ", "UN_CAPITAL           =>'", capital,
                                "', ", "UN_INTERES           =>'", interes,
                                "', ", "UN_OTROS             =>'", otros, "', ",
                                "UN_USUARIO           =>'", usuario, "'" };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_UTL.PR_CARGARPLANO_NOV_CONVENIO",
                        SysmanFunciones.concatenar(parametros));
    }

}