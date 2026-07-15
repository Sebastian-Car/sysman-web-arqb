package com.sysman.plandesarrollo.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.plandesarrollo.ejb.EjbPlanDesarrolloUnoGeneralRemote;
import com.sysman.plandesarrollo.ejb.EjbPlanDesarrolloUnoLocal;
import com.sysman.plandesarrollo.ejb.EjbPlanDesarrolloUnoRemote;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class PlanDesarrolloUno
 */
@Stateless
@LocalBean

public class EjbPlanDesarrolloUno implements EjbPlanDesarrolloUnoRemote,
                EjbPlanDesarrolloUnoLocal
{
    @EJB
    private EjbPlanDesarrolloUnoGeneralRemote ejbPlanDesarrolloUnoGeneral;

    /**
     * Default constructor.
     */
    public EjbPlanDesarrolloUno()
    {
    }

    @Override
    public String actualizarPlanIndicativo(
        String compania,
        int vigencia,
        String tipot,
        long numerot,
        String nombredependencia,
        int accion,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_VIGENCIA          =>",
                                Integer.toString(vigencia), ", ",
                                "UN_TIPOT             =>'", tipot, "', ",
                                "UN_NUMEROT           =>",
                                Long.toString(numerot),
                                ", ", "UN_NOMBREDEPENDENCIA =>'",
                                nombredependencia, "', ",
                                "UN_ACCION            =>",
                                Integer.toString(accion), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        try
        {
            return Acciones.clobToStringSalto((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PLAN_DESARROLLO1.FC_ACTUALIZARPLANINDICATIVO",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (IOException | SQLException e)
        {
            throw new SystemException(e);
        }
    }

    @Override
    public void calcularAvanceIndicadores(
        String compania,
        int vigencia,
        String tipot,
        BigInteger numerot,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_VIGENCIA          =>", Integer.toString(vigencia), ", ",
                                "UN_TIPOT             =>'", tipot, "', ",
                                "UN_NUMEROT           =>", numerot.toString(), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN, "PCK_PLAN_DESARROLLO1.PR_CALAVANCEINDICADORES",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void generarMantenimientoPlan(
        String compania,
        int vigencia,
        String usuario)
                    throws SystemException {
        ejbPlanDesarrolloUnoGeneral.generarMantenimientoPlan(compania, vigencia, usuario);
    }

}