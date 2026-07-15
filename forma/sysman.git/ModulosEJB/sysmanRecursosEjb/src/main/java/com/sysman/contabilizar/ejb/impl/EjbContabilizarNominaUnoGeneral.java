package com.sysman.contabilizar.ejb.impl;

import com.sysman.contabilizar.ejb.EjbContabilizarNominaUnoGeneralLocal;
import com.sysman.contabilizar.ejb.EjbContabilizarNominaUnoGeneralRemote;
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
 * Session Bean implementation class ContabilizarNominaUnoGeneral
 */
@Stateless
@LocalBean

public class EjbContabilizarNominaUnoGeneral implements EjbContabilizarNominaUnoGeneralRemote, EjbContabilizarNominaUnoGeneralLocal
{
    /**
     * Default constructor.
     */
    public EjbContabilizarNominaUnoGeneral()
    {
    }

    @Override
    public String contabilizarRetenciones(
        String companiaDs,
        String companiaNomina,
        int proceso,
        int ano,
        int mes,
        String centroCostoUnico,
        Date fechaInter,
        String usuario)
                    throws SystemException
    {
        try
        {
            String[] parametros = { "UN_COMPANIADS        =>'", companiaDs, "', ",
                                    "UN_COMPANIANOMINA    =>'", companiaNomina, "', ",
                                    "UN_PROCESO           =>", Integer.toString(proceso), ", ",
                                    "UN_ANO               =>", Integer.toString(ano), ", ",
                                    "UN_MES               =>", Integer.toString(mes), ", ",
                                    "UN_CENTROCOSTOUNICO  =>'", centroCostoUnico, "', ",
                                    "UN_FECHAINTER        =>TO_DATE('", SysmanFunciones.convertirAFechaCadena(fechaInter),
                                    "','DD/MM/YYYY'), ",
                                    "UN_USUARIO           =>'", usuario, "'"
            };
            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIZAR_NOMINA1.FC_CONTABILIZARRETENCIONES",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (ParseException | SQLException | IOException e)
        {
            throw new SystemException(e);
        }
    }

    @Override
    public String contabilizarCuentasporpagar(
        String companiaNomina,
        String companiaDs,
        int proceso,
        int ano,
        int mes,
        Date fechaInter,
        String usuario)
                    throws SystemException
    {
        try
        {
            String[] parametros = { "UN_COMPANIANOMINA    =>'", companiaNomina, "', ",
                                    "UN_COMPANIADS        =>'", companiaDs, "', ",
                                    "UN_PROCESO           =>", Integer.toString(proceso), ", ",
                                    "UN_ANO               =>", Integer.toString(ano), ", ",
                                    "UN_MES               =>", Integer.toString(mes), ", ",
                                    "UN_FECHAINTER        =>TO_DATE('", SysmanFunciones.convertirAFechaCadena(fechaInter),
                                    "','DD/MM/YYYY'), ",
                                    "UN_USUARIO           =>'", usuario, "'"
            };
            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIZAR_NOMINA1.FC_CONTACUENTASPORPAGAR",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (ParseException | SQLException | IOException e)
        {
            throw new SystemException(e);
        }
    }
}