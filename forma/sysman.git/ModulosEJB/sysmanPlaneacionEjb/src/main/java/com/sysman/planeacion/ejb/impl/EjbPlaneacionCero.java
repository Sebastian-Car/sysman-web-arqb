/*-
 * EjbPlaneacionUno.java
 *
 * 1.0
 * 
 * 7/09/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.planeacion.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.planeacion.ejb.EjbPlaneacionCeroLocal;
import com.sysman.planeacion.ejb.EjbPlaneacionCeroRemote;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Types;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class PlaneacionUno
 */
@Stateless
@LocalBean
public class EjbPlaneacionCero
                implements EjbPlaneacionCeroRemote, EjbPlaneacionCeroLocal {
    /**
     * Default constructor.
     */
    public EjbPlaneacionCero() {
    }

    @Override
    public boolean actualizarPlanCompras(
        String compania,
        int ano,
        String usuario)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PLANEACION.FC_ACTUALIZAREJECUCION",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public void registrarActualizacionPlanAdquisiciones(
        String compania,
        boolean realizada,
        BigInteger actualizacion,
        int ano,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_REALIZADA         =>",
                                (realizada ? "-1" : "0"), ", ",
                                "UN_ACTUALIZACION     =>",
                                actualizacion.toString(), ", ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PLANEACION.PR_REGISTRARACTPLANADQUI",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public BigDecimal calcularValorEjecutado(
        String compania,
        int anio,
        String codigo)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_CODIGO            =>'", codigo, "'"
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PLANEACION.FC_CALC_VLR_EJECUTADO",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public BigDecimal traerUltimoValorElemento(
        String compania,
        int anio,
        String elemento)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_ELEMENTO          =>'", elemento, "'"
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PLANEACION.FC_GET_ULTIMO_VALOR_ELEMENTO",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public BigDecimal calcularValorProgramado(
        String compania,
        int anio,
        String rubro, 
        String fuenteRecurso, 
        String referencia, 
        String centroCosto, 
        String auxiliar)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", "
                                , "UN_RUBRO             =>'" , rubro , "', "
                                , "UN_FUENTE_RECURSO    =>'" , fuenteRecurso , "', "
                                , "UN_REFERENCIA        =>'" , referencia , "', "
                                , "UN_CENTRO_COSTO      =>'" , centroCosto , "', "
                                , "UN_AUXILIAR          =>'" , auxiliar , "'"

        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PLANEACION.FC_CALCULAR_PROGRAMADO",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public boolean tieneDetallesPlanCompras(
        String compania,
        int anio,
        String rubro,
        String dependencia)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_RUBRO             =>'", rubro, "', ",
                                "UN_DEPENDENCIA       =>'", dependencia, "'"
        };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PLANEACION.FC_TIENE_DETALLES_PLAN_COMPRAS",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

}