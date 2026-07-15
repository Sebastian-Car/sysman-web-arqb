package com.sysman.nomina.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.nomina.ejb.EjbNominaCeroGeneralRemote;
import com.sysman.nomina.ejb.EjbNominaCeroLocal;
import com.sysman.nomina.ejb.EjbNominaCeroRemote;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class nominaCero
 */
@Stateless
@LocalBean
public class EjbNominaCero implements EjbNominaCeroRemote, EjbNominaCeroLocal {

    @EJB
    private EjbNominaCeroGeneralRemote ejbNominaCeroGeneral;

    /**
     * Default constructor.
     */
    public EjbNominaCero() {
    }

    @Override
    public void desactivarPeriodo(
        String compania,
        int anio,
        int mes,
        String user)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA       =>'", compania, "', ",
                                "UN_ANIO           =>",
                                Integer.toString(anio), ", ",
                                "UN_MES            =>",
                                Integer.toString(mes), ", ",
                                "UN_USER           =>'", user, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA.PR_DESACTIVAR",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void actualizarPeriodAcumulable(
        String compania,
        int proceso,
        int anio,
        int mes,
        int periodo,
        boolean parametro,
        String user)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA            =>'", compania, "', ",
                                "UN_PROCESO             =>",
                                Integer.toString(proceso), ", ",
                                "UN_ANIO                =>",
                                Integer.toString(anio), ", ",
                                "UN_MES                 =>",
                                Integer.toString(mes), ", ",
                                "UN_PERIODO             =>",
                                Integer.toString(periodo), ", ",
                                "UN_PARAMETRO           =>",
                                parametro ? "-1" : "0", ", ",
                                "UN_USER                =>'", user, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA.PR_PERIODOACUMULABLE",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public BigDecimal getDiasPeriodo(
        String compania,
        int proceso,
        int anio,
        int mes,
        int periodo)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA            =>'", compania, "', ",
                                "UN_PROCESO             =>",
                                Integer.toString(proceso), ", ",
                                "UN_ANIO                =>",
                                Integer.toString(anio), ", ",
                                "UN_MES                 =>",
                                Integer.toString(mes), ", ",
                                "UN_PERIODO             =>",
                                Integer.toString(periodo)
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA.FC_DIASPERIODO",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public Date getFechaAnioAnterior(
        Date fecha)
                    throws SystemException {
        try {
            String[] parametros = { "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY')"
            };
            return (Date) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_NOMINA.FC_FECHAANOATRAS",
                            SysmanFunciones.concatenar(parametros),
                            Types.DATE);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String getUltimasVacaciones(
        String compania,
        int empleado,
        int parametro,
        int proceso,
        int ano,
        int mes,
        int periodo)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA             =>'", compania, "', ",
                                "UN_EMPLEADO             =>",
                                Integer.toString(empleado), ", ",
                                "UN_PARAMETRO            =>",
                                Integer.toString(parametro), ", ",
                                "UN_PROCESO              =>",
                                Integer.toString(proceso), ", ",
                                "UN_ANO                  =>",
                                Integer.toString(ano), ", ",
                                "UN_MES                  =>",
                                Integer.toString(mes), ", ",
                                "UN_PERIODO              =>",
                                Integer.toString(periodo)
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA.FC_ULTIMASVACACIONES",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public Date getFechaIncapacidad(
        String compania,
        int empleado,
        int parametro,
        Date fechafin)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA   =>'", compania, "', ",
                                    "UN_EMPLEADO   =>",
                                    Integer.toString(empleado), ", ",
                                    "UN_PARAMETRO  =>",
                                    Integer.toString(parametro), ", ",
                                    "UN_FECHAFIN   =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechafin),
                                    "','DD/MM/YYYY')"
            };
            return (Date) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_NOMINA.FC_INIFININCAPACIDAD",
                            SysmanFunciones.concatenar(parametros),
                            Types.DATE);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public Date getFechaIncapacidadAmbul(
        String compania,
        int empleado,
        int parametro,
        Date fechaini,
        Date fechafin)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA               =>'", compania,
                                    "', ",
                                    "UN_EMPLEADO               =>",
                                    Integer.toString(empleado), ", ",
                                    "UN_PARAMETRO              =>",
                                    Integer.toString(parametro), ", ",
                                    "UN_FECHAINI          =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaini),
                                    "','DD/MM/YYYY'), ",
                                    "UN_FECHAFIN               =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechafin),
                                    "','DD/MM/YYYY')"
            };
            return (Date) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_NOMINA.FC_INIFININCAPACIDADAMBUL",
                            SysmanFunciones.concatenar(parametros),
                            Types.DATE);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public BigDecimal getPeriodosDeVacaciones(
        String compania,
        int empleado,
        int parametro,
        Date fechaini,
        Date fechafin)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA               =>'", compania,
                                    "', ",
                                    "UN_EMPLEADO               =>",
                                    Integer.toString(empleado), ", ",
                                    "UN_PARAMETRO              =>",
                                    Integer.toString(parametro), ", ",
                                    "UN_FECHAINI          =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaini),
                                    "','DD/MM/YYYY'), ",
                                    "UN_FECHAFIN          =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechafin),
                                    "','DD/MM/YYYY')"
            };
            return (BigDecimal) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_NOMINA.FC_PERIODOSVACACIONES",
                            SysmanFunciones.concatenar(parametros),
                            Types.DECIMAL);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public Date getFechaLicencia(
        String compania,
        int empleado,
        int parametro)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_EMPLEADO          =>",
                                Integer.toString(empleado), ", ",
                                "UN_PARAMETRO         =>",
                                Integer.toString(parametro), ""
        };
        return (Date) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA.FC_INIFINLICENCIA",
                        SysmanFunciones.concatenar(parametros),
                        Types.DATE);
    }

    @Override
    public Date getInicioFechaPeriodo(
        String compania,
        int proceso,
        int ano,
        int mes,
        int periodo)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA     =>'", compania, "', ",
                                "UN_PROCESO      =>",
                                Integer.toString(proceso), ", ",
                                "UN_ANO          =>",
                                Integer.toString(ano), ", ",
                                "UN_MES          =>",
                                Integer.toString(mes), ", ",
                                "UN_PERIODO      =>",
                                Integer.toString(periodo)
        };
        return (Date) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA.FC_FECHAINICIOPERIODO",
                        SysmanFunciones.concatenar(parametros),
                        Types.DATE);
    }

    @Override
    public void duplicarFinanciable(
        String compania,
        int empleado,
        int factor,
        String user)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_EMPLEADO          =>",
                                Integer.toString(empleado), ", ",
                                "UN_FACTOR            =>",
                                Integer.toString(factor), ", ",
                                "UN_USER              =>'", user, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA.PR_DUPLICARFINAN",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public Date getUltimaIncapacidad(
        String compania,
        int empleado,
        int parametro,
        Date fechafin)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_EMPLEADO          =>",
                                    Integer.toString(empleado), ", ",
                                    "UN_PARAMETRO         =>",
                                    Integer.toString(parametro), ", ",
                                    "UN_FECHAFIN          =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechafin),
                                    "','DD/MM/YYYY')"
            };
            return (Date) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_NOMINA.FC_FECHAULTIMAINCAPACIDAD",
                            SysmanFunciones.concatenar(parametros),
                            Types.DATE);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public BigDecimal getCampoDeCesantias(
        String compania,
        int empleado,
        String parametro)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_EMPLEADO          =>",
                                Integer.toString(empleado), ", ",
                                "UN_PARAMETRO         =>'", parametro, "'"
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN, "PCK_NOMINA.FC_CESANTIA",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public void insertarNovedad(
        String compania,
        int proceso,
        int anio,
        int mes,
        int periodo,
        int idempleado,
        int idconcepto,
        BigDecimal valor,
        String accion,
        String user)
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
                                "UN_IDEMPLEADO        =>",
                                Integer.toString(idempleado), ", ",
                                "UN_IDCONCEPTO        =>",
                                Integer.toString(idconcepto), ", ",
                                "UN_VALOR             =>", valor.toString(),
                                ", ", "UN_ACCION            =>'", accion, "', ",
                                "UN_USER              =>'", user, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA.PR_INCLUIRNOVEDAD",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public Date fechaVacacionesPagas(
        String compania,
        int empleado,
        int parametro)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_EMPLEADO          =>",
                                Integer.toString(empleado), ", ",
                                "UN_PARAMETRO         =>",
                                Integer.toString(parametro), ""
        };
        return (Date) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA.FC_FECHAVACACIONESENPAGADAS",
                        SysmanFunciones.concatenar(parametros),
                        Types.DATE);
    }

    @Override
    public Date fechaPeriodo(
        String compania,
        int proceso,
        int ano,
        int mes,
        int periodoin)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PROCESO           =>",
                                Integer.toString(proceso), ", ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_MES               =>",
                                Integer.toString(mes), ", ",
                                "UN_PERIODOIN         =>",
                                Integer.toString(periodoin), ""
        };
        return (Date) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA.FC_FECHAPERIODO",
                        SysmanFunciones.concatenar(parametros),
                        Types.DATE);
    }

    @Override
    public BigDecimal cesantiasAcumuladas(
        String compania,
        int empleado,
        Date fecha1,
        Date fecha2)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_EMPLEADO          =>",
                                    Integer.toString(empleado), ", ",
                                    "UN_FECHA1            =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha1),
                                    "','DD/MM/YYYY'), ",
                                    "UN_FECHA2            =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha2),
                                    "','DD/MM/YYYY')"
            };
            return (BigDecimal) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_NOMINA.FC_ACUMCESANTIA",
                            SysmanFunciones.concatenar(parametros),
                            Types.DECIMAL);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public BigDecimal getDeducibleSalud(
        String compania,
        int iddeempleado,
        int idproceso)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_IDDEEMPLEADO      =>",
                                Integer.toString(iddeempleado), ", ",
                                "UN_IDPROCESO         =>",
                                Integer.toString(idproceso), ""
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA.FC_DEDUCIBLESALUD",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public BigDecimal getPorcentateRetefuente(
        String compania,
        int iddeempleado,
        int idproceso)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_IDDEEMPLEADO      =>",
                                Integer.toString(iddeempleado), ", ",
                                "UN_IDPROCESO         =>",
                                Integer.toString(idproceso), ""
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA.FC_PORC_RTEFTE_EMPLEADO",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public Date fechaPeriodoAnterior(
        String compania,
        int proceso,
        int ano,
        int mes,
        int periodoin,
        String parametro)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PROCESO           =>",
                                Integer.toString(proceso), ", ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_MES               =>",
                                Integer.toString(mes), ", ",
                                "UN_PERIODOIN         =>",
                                Integer.toString(periodoin), ", ",
                                "UN_PARAMETRO         =>'", parametro, "'"
        };
        return (Date) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA.FC_ANTERIORPERIODO",
                        SysmanFunciones.concatenar(parametros),
                        Types.DATE);
    }

    @Override
    public void actualizarVacaciones(
        String compania,
        int empleado,
        int periodo,
        Date fechafinper,
        String usuario)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_EMPLEADO          =>",
                                    Integer.toString(empleado), ", ",
                                    "UN_PERIODO           =>",
                                    Integer.toString(periodo), ", ",
                                    "UN_FECHAFINPER       =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechafinper),
                                    "','DD/MM/YYYY'), ",
                                    "UN_USUARIO           =>'", usuario, "'"
            };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_NOMINA.PR_ACTUALIZARVACACIONES",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void actualizarVacacionesEliminar(
        String compania,
        int empleado,
        int periodo,
        Date fechafinper,
        String usuario)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_EMPLEADO          =>",
                                    Integer.toString(empleado), ", ",
                                    "UN_PERIODO           =>",
                                    Integer.toString(periodo), ", ",
                                    "UN_FECHAFINPER       =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechafinper),
                                    "','DD/MM/YYYY'), ",
                                    "UN_USUARIO           =>'", usuario, "'"
            };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_NOMINA.PR_ACTUALIZARVACACIONES_ELIM",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }
    @Override
    public void cargarFinanciables(
        String compania,
        int procesoin,
        int ano,
        int mes,
        int periodoin,
        int empleado,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PROCESOIN         =>",
                                Integer.toString(procesoin), ", ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_MES               =>",
                                Integer.toString(mes), ", ",
                                "UN_PERIODOIN         =>",
                                Integer.toString(periodoin), ", ",
                                "UN_EMPLEADO          =>",
                                Integer.toString(empleado), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA.PR_CARGARFINANCIABLES",
                        SysmanFunciones.concatenar(parametros));
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

        return ejbNominaCeroGeneral.getFechaPeriodoIniFin(compania, proceso,
                        anio, mes, periodo, fechainicio, total);

    }

    @Override
    public void actualizarTraslados(
        String compania,
        int proceso,
        int ano,
        int mes,
        int periodo,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PROCESO           =>",
                                Integer.toString(proceso), ", ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_MES               =>",
                                Integer.toString(mes), ", ",
                                "UN_PERIODO           =>",
                                Integer.toString(periodo), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA.PR_ACTUALIZARTRASLADOS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public boolean validarPeriodoActivoNomina(
        String compania,
        int proceso,
        int anio,
        int mes,
        int periodo)
                    throws SystemException {
        return ejbNominaCeroGeneral.validarPeriodoActivoNomina(compania,
                        proceso, anio, mes, periodo);
    }

    @Override
    public void borrarHistoricoEmpleado(
        String compania,
        int proceso,
        int ano,
        int mes,
        int periodo,
        int idempleado,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PROCESO           =>",
                                Integer.toString(proceso), ", ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_MES               =>",
                                Integer.toString(mes), ", ",
                                "UN_PERIODO           =>",
                                Integer.toString(periodo), ", ",
                                "UN_IDEMPLEADO        =>",
                                Integer.toString(idempleado), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA.PR_BORRARHISTORICOEMPLEADO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void borrarHistoricoPeriodo(
        String compania,
        int proceso,
        int ano,
        int mes,
        int periodo,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PROCESO           =>",
                                Integer.toString(proceso), ", ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_MES               =>",
                                Integer.toString(mes), ", ",
                                "UN_PERIODO           =>",
                                Integer.toString(periodo), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA.PR_BORRARHISTORICO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String getDatoEmpresa(
        String compania,
        int par)
                    throws SystemException {

        return ejbNominaCeroGeneral.getDatoEmpresa(compania, par);

    }

    @Override
    public BigDecimal novedadSiempreunEmpleado(
        String compania,
        int idempleado,
        int concepto)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_IDEMPLEADO        =>",
                                Integer.toString(idempleado), ", ",
                                "UN_CONCEPTO          =>",
                                Integer.toString(concepto), ""
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA.FC_VLRCONCEPTONOVEDADEMPLEADO",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public BigDecimal diasPendientesVacaciones(
        String compania,
        int idempleado,
        Date fechaf)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_IDEMPLEADO        =>",
                                    Integer.toString(idempleado), ", ",
                                    "UN_FECHAF            =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaf),
                                    "','DD/MM/YYYY')"
            };
            return (BigDecimal) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_NOMINA.FC_DIASPENDVACACIONES",
                            SysmanFunciones.concatenar(parametros),
                            Types.DECIMAL);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public BigDecimal ausentismoEmpleado(
        String compania,
        int idempleado,
        Date fechainicio,
        Date fechafinal)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_IDEMPLEADO        =>",
                                    Integer.toString(idempleado), ", ",
                                    "UN_FECHAINICIO       =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechainicio),
                                    "','DD/MM/YYYY'), ",
                                    "UN_FECHAFINAL        =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechafinal),
                                    "','DD/MM/YYYY')"
            };
            return (BigDecimal) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_NOMINA.FC_AUSENTISMOEMPLEADO",
                            SysmanFunciones.concatenar(parametros),
                            Types.DECIMAL);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String getNombreCargo(
        String compania,
        String idcargo,
        String idescalafon)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_IDCARGO           =>'", idcargo, "', ",
                                "UN_IDESCALAFON       =>'", idescalafon, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA.FC_NOMBRECARGO",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public BigDecimal getSalarioCargo(
        String compania,
        String idescalafon,
        String idcategoria,
        int ano)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_IDESCALAFON       =>'", idescalafon, "', ",
                                "UN_IDCATEGORIA       =>'", idcategoria, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ""
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA.FC_SALARIOCARGO",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public String getNombreProceso(
        String compania,
        int proceso)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PROCESO           =>",
                                Integer.toString(proceso), ""
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA.FC_NOMBREPROCESO",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public void eliminarNovedadEmpleado(
        String compania,
        int procesoin,
        int anio,
        int mes,
        int periodoin,
        int idempleado,
        int idconcepto)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PROCESOIN         =>",
                                Integer.toString(procesoin), ", ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_MES               =>",
                                Integer.toString(mes), ", ",
                                "UN_PERIODOIN         =>",
                                Integer.toString(periodoin), ", ",
                                "UN_IDEMPLEADO        =>",
                                Integer.toString(idempleado), ", ",
                                "UN_IDCONCEPTO        =>",
                                Integer.toString(idconcepto), ""
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA.PR_BORRARNOVEDAD",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void eliminaActualizaNovedadEmpleado(
        String compania,
        int procesoin,
        int anio,
        int mes,
        int periodoin,
        int idempleado,
        int idconcepto,
        BigDecimal cantidad,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PROCESOIN         =>",
                                Integer.toString(procesoin), ", ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ", ",
                                "UN_MES               =>",
                                Integer.toString(mes), ", ",
                                "UN_PERIODOIN         =>",
                                Integer.toString(periodoin), ", ",
                                "UN_IDEMPLEADO        =>",
                                Integer.toString(idempleado), ", ",
                                "UN_IDCONCEPTO        =>",
                                Integer.toString(idconcepto), ", ",
                                "UN_CANTIDAD          =>", cantidad.toString(),
                                ", ", "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA.PR_BORRARNOVEDADX",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void incrementarIbc(
        String compania,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA.PR_INCREMENTARIBC",
                        SysmanFunciones.concatenar(parametros));
    }
}