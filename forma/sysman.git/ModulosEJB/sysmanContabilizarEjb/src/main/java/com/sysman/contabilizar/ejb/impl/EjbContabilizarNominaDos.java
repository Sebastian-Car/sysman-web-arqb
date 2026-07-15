package com.sysman.contabilizar.ejb.impl;

import com.sysman.contabilizar.ejb.EjbContabilizarNominaDosGeneralRemote;
import com.sysman.contabilizar.ejb.EjbContabilizarNominaDosLocal;
import com.sysman.contabilizar.ejb.EjbContabilizarNominaDosRemote;
import com.sysman.exception.SystemException;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class ContabilizarNominaDos
 */
@Stateless
@LocalBean

public class EjbContabilizarNominaDos implements EjbContabilizarNominaDosRemote, EjbContabilizarNominaDosLocal
{
    @EJB
    private EjbContabilizarNominaDosGeneralRemote ejbContabilizarDosGeneral;

    /**
     * Default constructor.
     */
    public EjbContabilizarNominaDos()
    {
    }

    @Override
    public void revisarCuentasPlancontable(
        String companiaNomina,
        String companiaDs,
        int ano,
        int mes,
        int periodo,
        int proceso,
        String usuario)
                    throws SystemException
    {
        ejbContabilizarDosGeneral.revisarCuentasPlancontable(companiaNomina, companiaDs, ano, mes, periodo, proceso, usuario);
    }

    @Override
    public String contabilizarAlmcnH(
        String compania,
        int ano,
        int mes,
        Date fechaInterf,
        String tipo,
        BigDecimal numero,
        String usuario)
                    throws SystemException
    {
        try
        {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_ANO               =>", Integer.toString(ano), ", ",
                                    "UN_MES               =>", Integer.toString(mes), ", ",
                                    "UN_FECHINTERF        =>TO_DATE('", SysmanFunciones.convertirAFechaCadena(fechaInterf),
                                    "','DD/MM/YYYY'), ",
                                    "UN_TIPO              =>'", tipo, "', ",
                                    "UN_NUMERO            =>", numero.toString(), ", ",
                                    "UN_USUARIO           =>'", usuario, "'" };
            return Acciones.clobToStringSalto((Clob) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIZAR_NOMINA2.FC_CONTABILIZARALMCNH",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (ParseException | IOException | SQLException e)
        {
            throw new SystemException(e);
        }
    }

    @Override
    public String contabilizarHNiveles(
        String compania,
        int ano,
        int mes,
        Date fechaInterf,
        String tipo,
        BigDecimal numero,
        boolean niif,
        String usuario)
                    throws SystemException
    {
        try
        {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_ANO               =>", Integer.toString(ano), ", ",
                                    "UN_MES               =>", Integer.toString(mes), ", ",
                                    "UN_FECHINTERF        =>TO_DATE('", SysmanFunciones.convertirAFechaCadena(fechaInterf),
                                    "','DD/MM/YYYY'), ",
                                    "UN_TIPO              =>'", tipo, "', ",
                                    "UN_NUMERO            =>", numero.toString(), ", ",
                                    "UN_NIIF              =>", niif ? "-1" : "0", ", ",
                                    "UN_USUARIO           =>'", usuario, "'" };
            return Acciones.clobToStringSalto((Clob) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIZAR_NOMINA2.FC_CONTABILIZARHNIVELES",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (ParseException | IOException | SQLException e)
        {
            throw new SystemException(e);
        }
    }

    @Override
    public String contabilizarHNivelesCC(
        String compania,
        int ano,
        int mes,
        Date fechaInterf,
        String tipo,
        BigDecimal numero,
        boolean niif,
        String usuario)
                    throws SystemException
    {
        try
        {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_ANO               =>", Integer.toString(ano), ", ",
                                    "UN_MES               =>", Integer.toString(mes), ", ",
                                    "UN_FECHINTERF        =>TO_DATE('", SysmanFunciones.convertirAFechaCadena(fechaInterf),
                                    "','DD/MM/YYYY'), ",
                                    "UN_TIPO              =>'", tipo, "', ",
                                    "UN_NUMERO            =>", numero.toString(), ", ",
                                    "UN_NIIF              =>", niif ? "-1" : "0", ", ",
                                    "UN_USUARIO           =>'", usuario, "'"
            };
            return Acciones.clobToStringSalto((Clob) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIZAR_NOMINA2.FC_CONTABILIZARHNIVELESCC",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (ParseException | IOException | SQLException e)
        {
            throw new SystemException(e);
        }
    }

    @Override
    public String contabilizarArmConsltHNvles(
        String compania,
        Date fechaInterf,
        boolean niif)
                    throws SystemException
    {
        try
        {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_FECHINTERF        =>TO_DATE('", SysmanFunciones.convertirAFechaCadena(fechaInterf),
                                    "','DD/MM/YYYY'), ",
                                    "UN_NIIF              =>", niif ? "-1" : "0", ""
            };
            return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIZAR_NOMINA2.FC_CONTBLIZARARMCONSLTHNVLES",
                            SysmanFunciones.concatenar(parametros),
                            Types.VARCHAR);
        }
        catch (ParseException e)
        {
            throw new SystemException(e);
        }
    }

    @Override
    public String contabilizarArmConsltHNvlesCC(
        String compania,
        Date fechaInterf,
        boolean niif)
                    throws SystemException
    {
        try
        {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_FECHINTERF        =>TO_DATE('", SysmanFunciones.convertirAFechaCadena(fechaInterf),
                                    "','DD/MM/YYYY'), ",
                                    "UN_NIIF              =>", niif ? "-1" : "0", ""
            };
            return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTABILIZAR_NOMINA2.FC_CONTBLIZARARMCONSLTHNVLESCC",
                            SysmanFunciones.concatenar(parametros),
                            Types.VARCHAR);
        }
        catch (ParseException e)
        {
            throw new SystemException(e);
        }
    }

}