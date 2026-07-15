/*-
 * EjbplaneacionUno.java
 *
 * 1.0
 * 
 * 13/09/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.planeacion.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.planeacion.ejb.EjbPlaneacionUnoLocal;
import com.sysman.planeacion.ejb.EjbPlaneacionUnoRemote;
import com.sysman.util.SysmanFunciones;

import java.math.BigInteger;
import java.sql.Types;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class planeacionUno
 */
@Stateless
@LocalBean
public class EjbPlaneacionUno
                implements EjbPlaneacionUnoRemote, EjbPlaneacionUnoLocal {
    /**
     * Default constructor.
     */
    public EjbPlaneacionUno() {
        // constructor
    }

    @Override
    public void registrarCertPlanCompras(
        String compania,
        int anio,
        BigInteger numero,
        String dependencia,
        String responsable,
        int mes)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_NUMERO            =>", numero.toString(),
                                ", ", "UN_DEPENDENCIA       =>'", dependencia,
                                "', ", "UN_RESPONSABLE       =>'", responsable,
                                "', ", "UN_MES               =>",
                                Integer.toString(mes), ""
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PLANEACION_COM1.PR_REG_CERTPLANCOMPRAS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public int responderPropuesta(
        String compania,
        String usuario,
        long codRequisicion,
        long codPropuesta,
        boolean respondio)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_COD_REQUISICION   =>",
                                Long.toString(codRequisicion), ", ",
                                "UN_COD_PROPUESTA     =>",
                                Long.toString(codPropuesta), ", ",
                                "UN_RESPONDIO         =>",
                                respondio ? "-1" : "0"
        };
        return (int) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PLANEACION_COM1.FC_RESPONDERPROPUESTA",
                        SysmanFunciones.concatenar(parametros),
                        Types.INTEGER);
    }

    @Override
    public boolean actualizarEstadoRespondio(
        String compania,
        BigInteger codPropuesta,
        BigInteger codRequisicion,
        boolean respondio,
        String usuario)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_COD_PROPUESTA     =>",
                                codPropuesta.toString(), ", ",
                                "UN_COD_REQUISICION   =>",
                                codRequisicion.toString(), ", ",
                                "UN_RESPONDIO         =>",
                                respondio ? "-1" : "0", ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PLANEACION_COM1.FC_RESPONDERCOTIZACION",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

}
