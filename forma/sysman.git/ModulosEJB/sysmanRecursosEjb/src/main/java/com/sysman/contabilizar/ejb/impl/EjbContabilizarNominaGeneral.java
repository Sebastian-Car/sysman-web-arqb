package com.sysman.contabilizar.ejb.impl;

import com.sysman.contabilizar.ejb.EjbContabilizarNominaGeneralLocal;
import com.sysman.contabilizar.ejb.EjbContabilizarNominaGeneralRemote;
import com.sysman.exception.SystemException;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class ContabilizarNominaGeneral
 */
@Stateless
@LocalBean

public class EjbContabilizarNominaGeneral implements EjbContabilizarNominaGeneralRemote, EjbContabilizarNominaGeneralLocal
{
    /**
     * Default constructor.
     */
    public EjbContabilizarNominaGeneral()
    {
    }

    @Override
    public String contabilizarNomina(
        String compania,
        String companiaNomina,
        int ano,
        int mes,
        int periodo,
        int proceso,
        Date fechaInter,
        String tipoComprobante,
        boolean compporEmpleado,
        boolean compporTercero,
        boolean compporCentroCosto,
        String empleado,
        boolean patronoT,
        boolean empleadoT,
        boolean provisionesT,
        String centroCostoUnico,
        String usuario)
                    throws SystemException
    {
        try
        {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_COMPANIANOMINA    =>'", companiaNomina, "', ",
                                    "UN_ANO               =>", Integer.toString(ano), ", ",
                                    "UN_MES               =>",
                                    Integer.toString(mes), ", ",
                                    "UN_PERIODO           =>", Integer.toString(periodo), ", ",
                                    "UN_PROCESO           =>", Integer.toString(proceso), ", ",
                                    "UN_FECHAINTER        =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(fechaInter), "','DD/MM/YYYY'), ", "UN_TIPOCOMPROBANTE   =>'",
                                    tipoComprobante, "', ",
                                    "UN_COMPPOREMPLEADO   =>", (compporEmpleado ? "-1" : "0"), ", ",
                                    "UN_COMPPORTERCERO    =>", (compporTercero ? "-1" : "0"), ", ", "UN_COMPPORCENTROCOST =>",
                                    (compporCentroCosto ? "-1" : "0"), ", ",
                                    "UN_EMPLEADO          =>'", empleado, "', ",
                                    "UN_PATRONO_T         =>", (patronoT ? "-1" : "0"), ", ",
                                    "UN_EMPLEADO_T        =>",
                                    (empleadoT ? "-1" : "0"), ", ",
                                    "UN_PROVISIONES_T     =>", (provisionesT ? "-1" : "0"), ", ",
                                    "UN_CENTROCOSTOUNICO  =>'", centroCostoUnico, "', ",
                                    "UN_USUARIO           =>'", usuario, "'"
            };
            return Acciones.clobToStringSalto((Clob) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIZAR_NOMINA.FC_CONTABILIZARNOMINA",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (ParseException | IOException | SQLException e)
        {
            throw new SystemException(e);
        }

    }
}