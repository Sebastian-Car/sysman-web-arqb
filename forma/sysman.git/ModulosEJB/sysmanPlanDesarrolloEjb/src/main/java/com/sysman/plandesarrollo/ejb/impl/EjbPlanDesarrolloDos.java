/*-
 * EjbPlanDesarrolloDos.java
 *
 * 1.0
 * 
 * 11/04/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.plandesarrollo.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.plandesarrollo.ejb.EjbPlanDesarrolloDosLocal;
import com.sysman.plandesarrollo.ejb.EjbPlanDesarrolloDosRemote;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Types;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class PlanDesarrolloDos
 */
@Stateless
@LocalBean
public class EjbPlanDesarrolloDos implements EjbPlanDesarrolloDosRemote,
                EjbPlanDesarrolloDosLocal {
    /**
     * Default constructor.
     */
    public EjbPlanDesarrolloDos() {
    }

    @Override
    public boolean verificarPlanAdquisiciones(
        String compania,
        int ano,
        String valor)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_VALOR             =>'", valor, "'"
        };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PLAN_DESARROLLO2.FC_PLANADQUICERRADO",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public void eliminarPlanIndicativo(
        String compania,
        int ano,
        String tipo,
        Long numero,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_TIPO              =>'", tipo, "', ",
                                "UN_NUMERO            =>", numero.toString(),
                                ", ", "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PLAN_DESARROLLO2.PR_ELIMINARPLAN",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public boolean validarEliminacionPlanIndicativo(
        String compania,
        int vigenciainicial,
        String tipo,
        BigInteger numero,
        String idplan,
        BigInteger digitosaccion)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_VIGENCIAINICIAL   =>",
                                Integer.toString(vigenciainicial), ", ",
                                "UN_TIPO              =>'", tipo, "', ",
                                "UN_NUMERO            =>", numero.toString(),
                                ", ", "UN_IDPLAN            =>'", idplan, "', ",
                                "UN_DIGITOSACCION     =>",
                                digitosaccion.toString()
        };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PLAN_DESARROLLO2.FC_VALIDARELIMINACION",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public void verificarSaldoDisponible(
        String compania,
        String id,
        String idPlan,
        int vigenciaPlan,
        int vigenciaMeta,
        int vigenciaInicial,
        String tipo,
        String numero,
        String fuente,
        BigDecimal valorfuente)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ID                =>'", id, "', ",
                                "UN_ID_PLAN           =>'", idPlan, "', ",
                                "UN_VIGENCIA_PLAN     =>",
                                Integer.toString(vigenciaPlan), ", ",
                                "UN_VIGENCIA_META     =>",
                                Integer.toString(vigenciaMeta), ", ",
                                "UN_VIGENCIA_INICIAL  =>",
                                Integer.toString(vigenciaInicial), ", ",
                                "UN_TIPO              =>'", tipo, "', ",
                                "UN_NUMERO            =>'", numero, "', ",
                                "UN_FUENTE            =>'", fuente, "', ",
                                "UN_VALORFUENTE       =>",
                                valorfuente.toString()
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PLAN_DESARROLLO2.PR_SALDODISPONIBLE",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public boolean mayorizarMetasyFuentes(
        String compania,
        int vigencia,
        String tipo,
        String numero,
        String usuario)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_VIGENCIA          =>",
                                Integer.toString(vigencia), ", ",
                                "UN_TIPO              =>'", tipo, "', ",
                                "UN_NUMERO            =>'", numero, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PLAN_DESARROLLO2.FC_MAYORIZAMETAS",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public void actualizarPresupuestoPlanAccion(
        String compania,
        int anio,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PLAN_DESARROLLO2.PR_MANTENIMIENTOPRESUPUESTAL",
                        SysmanFunciones.concatenar(parametros));
    }
    
    @Override
    public void actualizarPresupuestoPlanDesarrollo(
        String compania,
        int anio,
        String usuario,
        int mes)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_MES              =>",
                                Integer.toString(mes)
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PLAN_DESARROLLO2.PR_MANTENIMIENTO_PLAN_DES",
                        SysmanFunciones.concatenar(parametros));
    }

}