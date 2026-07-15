package com.sysman.nomina.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.nomina.ejb.EjbNominaTresLocal;
import com.sysman.nomina.ejb.EjbNominaTresRemote;
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

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class NominaTres
 */
@Stateless
@LocalBean
public class EjbNominaTres implements EjbNominaTresRemote, EjbNominaTresLocal
{
    /**
     * Default constructor.
     */
    public EjbNominaTres()
    {
    }

    @Override
    public void incluirConcepto(
        String nombre,
        int codigo,
        int tipoc,
        String unidad,
        String usuario)
                    throws SystemException
    {
        String[] parametros = { "UN_NOMBRE            =>'", nombre, "', ",
                                "UN_CODIGO            =>",
                                Integer.toString(codigo), ", ",
                                "UN_TIPOC             =>",
                                Integer.toString(tipoc), ", ",
                                "UN_UNIDAD            =>'", unidad, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM3.PR_INCLUIRCONCEPTO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String nombreConcepto(
        String compania,
        int codigo)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CODIGO            =>",
                                Integer.toString(codigo)
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM3.FC_CONCEPTO",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public BigDecimal deducibleDependientes(
        String compania,
        int idempleado)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_IDEMPLEADO        =>",
                                Integer.toString(idempleado)
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM3.FC_DEDUCIBLE_DEPENDIENTES",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public boolean deduciblePendiente(
        String compania,
        String parametro,
        int idempleado)
                    throws SystemException
    {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PARAMETRO         =>'", parametro, "', ",
                                "UN_IDEMPLEADO        =>",
                                Integer.toString(idempleado)
        };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM3.FC_MIDEPENDIENTES",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public BigDecimal deducibleValor300(
        String compania,
        int idempleado)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_IDEMPLEADO        =>",
                                Integer.toString(idempleado)
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM3.FC_DEDUCIBLEV",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public BigDecimal miSueldo(
        String compania,
        int idempleado,
        int ano)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_IDEMPLEADO        =>",
                                Integer.toString(idempleado), ", ",
                                "UN_ANO               =>",
                                Integer.toString(ano)
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM3.FC_MISUELDO",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public void actualizarNovedad304(
        String compania,
        int anio,
        String usuario)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM3.PR_ACTUALIZARNOVEDADES304",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public int mesesLaborados(
        String compania,
        int anio,
        int idEmpleado,
        int concepto)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_ID_EMPLEADO       =>",
                                Integer.toString(idEmpleado), ", ",
                                "UN_CONCEPTO          =>",
                                Integer.toString(concepto)
        };
        return (int) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM3.FC_NUMERO_MESES_LABORADOS",
                        SysmanFunciones.concatenar(parametros),
                        Types.INTEGER);
    }

    @Override
    public void calcularDiferenciaRetroactivo(
        String compania,
        int idempleado,
        int proceso,
        int anio,
        int mes,
        int periodo,
        String usuario)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_IDEMPLEADO        =>",
                                Integer.toString(idempleado), ", ",
                                "UN_PROCESO           =>",
                                Integer.toString(proceso), ", ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_MES               =>",
                                Integer.toString(mes), ", ",
                                "UN_PERIODO           =>",
                                Integer.toString(periodo), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM3.PR_CALCULARDIFRETROACTIVOS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void reteFteRetroActivos(
        String compania,
        int idempleado,
        int proceso,
        int anio,
        int mes,
        int periodo,
        String usuario)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_IDEMPLEADO        =>",
                                Integer.toString(idempleado), ", ",
                                "UN_PROCESO           =>",
                                Integer.toString(proceso), ", ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_MES               =>",
                                Integer.toString(mes), ", ",
                                "UN_PERIODO           =>",
                                Integer.toString(periodo), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM3.PR_RETEFTERETROACTIVOS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void netosRetroActivo(
        String compania,
        int proceso,
        int mes,
        int anio,
        int periodo,
        String usuario)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PROCESO           =>",
                                Integer.toString(proceso), ", ",
                                "UN_MES               =>",
                                Integer.toString(mes), ", ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_PERIODO           =>",
                                Integer.toString(periodo), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM3.PR_NETOSRETROACTIVO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void calcRetencion(
        String compania,
        Date fechaCombo2,
        Date ingreso,
        BigDecimal vporcentaje,
        String documento,
        String usuario)
                    throws SystemException
    {
        try
        {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_FECHA_COMBO2      =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaCombo2),
                                    "','DD/MM/YYYY'), ",
                                    "UN_INGRESO           =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    ingreso),
                                    "','DD/MM/YYYY'), ",
                                    "UN_VPORCENTAJE       =>",
                                    vporcentaje.toString(), ", ",
                                    "UN_DOCUMENTO         =>'", documento,
                                    "', ", "UN_USUARIO           =>'", usuario,
                                    "'"
            };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_NOMINA_COM3.PR_CALCRETENCION",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e)
        {
            throw new SystemException(e);
        }
    }

    @Override
    public BigDecimal deduciblePrepagada(
        String compania,
        int iddeempleado)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_IDDEEMPLEADO      =>",
                                Integer.toString(iddeempleado)
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM3.FC_DEDUCIBLEPREPAGADA",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public void cargarParCalCret(
        String compania,
        String usuario)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM3.PR_CARGARPARCALCRET",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String calcularRtfParUno(
        String compania,
        int ano,
        int proceso,
        String fechaInicial,
        String fechaFinal,
        boolean cn309,
        int  ckAnioHasta,
        String usuario)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_PROCESO           =>",
                                Integer.toString(proceso), ", ",
                                "UN_FECHA_INICIAL     =>'", fechaInicial, "', ",
                                "UN_FECHA_FINAL       =>'", fechaFinal, "', ",
                                "UN_CN309             =>", (cn309 ? "-1" : "0"),
                                ", ", "UN_ANIOHASTADEDSALUD =>", Integer.toString(ckAnioHasta) ," " ,
                                ", ", "UN_USUARIO     =>'", usuario, "'"
        };
        try
        {
            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_NOMINA_COM3.FC_CALCULARRTFPARUNO",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (IOException | SQLException e)
        {
            throw new SystemException(e);
        }
    }

    @Override
    public String calcularRtfParDos(
        String compania,
        String documento,
        BigDecimal promedio,
        Date fechaCombo1,
        Date fechaCombo2,
        BigDecimal imeses,
        BigDecimal promedioc22,
        boolean asignarPorReten,
        String usuario)

                    throws SystemException
    {
        try
        {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_DOCUMENTO         =>'", documento,
                                    "', ", "UN_PROMEDIO          =>",
                                    promedio.toString(), ", ",
                                    "UN_FECHA_COMBO1      =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaCombo1),
                                    "','DD/MM/YYYY'), ",
                                    "UN_FECHA_COMBO2      =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaCombo2),
                                    "','DD/MM/YYYY'), ",
                                    "UN_IMESES            =>",
                                    imeses.toString(), ", ",
                                    "UN_PROMEDIOC22       =>",
                                    promedioc22.toString(), ", ",
                                    "UN_ASIGNARPORRETEN   =>",
                                    asignarPorReten ? "-1" : "0", ", ",
                                    "UN_USUARIO           =>'", usuario, "'"
            };
            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_NOMINA_COM3.FC_CALCULARRTFPARDOS",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (ParseException | IOException | SQLException e)
        {
            throw new SystemException(e);
        }
    }

    @Override
    public String calcularRtfParTres(
        String compania,
        int ano,
        String fechaInicial,
        String fechaFinal,
        String usuario)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_FECHA_INICIAL     =>'", fechaInicial, "', ",
                                "UN_FECHA_FINAL       =>'", fechaFinal, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        try
        {
            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_NOMINA_COM3.FC_CALCULARRTFPARTRES",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (IOException | SQLException e)
        {
            throw new SystemException(e);
        }
    }

    @Override
    public String generarPlanoIncapacidadSiif(
        String compania,
        int proceso,
        int ano,
        int mes,
        int periodo,
        int archivo,
        String salida)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PROCESO         =>",
                                Integer.toString(proceso), ", ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_MES               =>",
                                Integer.toString(mes), ", ",
                                "UN_PERIODO         =>",
                                Integer.toString(periodo), ", ",
                                "UN_ARCHIVO           =>",
                                Integer.toString(archivo), ", ",
                                "UN_SALIDA            =>'", salida, "'"
        };
        try
        {
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_NOMINA_COM3.FC_PLANOINCAPACIDADESSIIF",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e)
        {

            throw new SystemException(e);
        }
    }

    @Override
    public String generarPlanoRetefuenteSiif(
        String compania,
        int proceso,
        int ano,
        int mes,
        int periodo,
        int archivo,
        String salida)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PROCESO         =>",
                                Integer.toString(proceso), ", ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_MES               =>",
                                Integer.toString(mes), ", ",
                                "UN_PERIODO         =>",
                                Integer.toString(periodo), ", ",
                                "UN_ARCHIVO           =>",
                                Integer.toString(archivo), ", ",
                                "UN_SALIDA            =>'", salida, "'"
        };
        try
        {
            return Acciones.clobToStringSalto((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_NOMINA_COM3.FC_PLANORETEFUENTESIIF",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (IOException | SQLException e)
        {
            throw new SystemException(e);
        }
    }

    @Override
    public String generarPlanoBeneficiosSiif(
        String compania,
        int proceso,
        int ano,
        int mes,
        int periodo,
        int archivo,
        String salida)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PROCESO         =>",
                                Integer.toString(proceso), ", ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_MES               =>",
                                Integer.toString(mes), ", ",
                                "UN_PERIODO         =>",
                                Integer.toString(periodo), ", ",
                                "UN_ARCHIVO           =>",
                                Integer.toString(archivo), ", ",
                                "UN_SALIDA            =>'", salida, "'"
        };
        try
        {
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_NOMINA_COM3.FC_PLANOBENEFICIOSSIIF",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e)
        {
            throw new SystemException(e);
        }
    }
    
    @Override
    public boolean validarPeriodoActivoNominaH(
        String compania,
        int proceso,
        int anio,
        int mes,
        String fecha)
        		throws SystemException
        	    {
        	        byte salida;
        	        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
        	                                "UN_PROCESO         =>", Integer.toString(proceso), ", ",
        	                                "UN_ANIO         =>", Integer.toString(anio), ", ",
        	                                "UN_MES         =>", Integer.toString(mes), ", ",
        	                                "UN_FECHA         =>'", fecha, "' "
        	        };
        	        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
        	                        "PCK_NOMINA_COM3.FC_PERIODOACTIVADONOMINAH",
        	                        SysmanFunciones.concatenar(parametros),
        	                        Types.TINYINT);
        	        return salida == 0 ? false : true;
        	    }

}