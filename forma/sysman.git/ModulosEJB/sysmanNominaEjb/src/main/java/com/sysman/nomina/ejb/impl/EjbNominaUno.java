package com.sysman.nomina.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.nomina.ejb.EjbNominaUnoLocal;
import com.sysman.nomina.ejb.EjbNominaUnoRemote;
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
import java.util.Optional;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class NominaUno
 */
@Stateless
@LocalBean
public class EjbNominaUno implements EjbNominaUnoRemote, EjbNominaUnoLocal {
    /**
     * Default constructor.
     */
    public EjbNominaUno() {
    }

    @Override
    public int getConceptoRelacionado(
        String compania,
        int concepto)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA            =>'", compania, "', ",
                                "UN_CONCEPTO            =>",
                                Integer.toString(concepto)
        };
        return (int) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM1.FC_RELACIONADO",
                        SysmanFunciones.concatenar(parametros),
                        Types.INTEGER);
    }

    @Override
    public BigDecimal getValorConcepto(
        String compania,
        int proceso,
        int ano,
        int mes,
        int periodo,
        int empleado,
        int concepto)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA         =>'", compania, "', ",
                                "UN_PROCESO          =>",
                                Integer.toString(proceso), ", ",
                                "UN_ANO              =>",
                                Integer.toString(ano), ", ",
                                "UN_MES              =>",
                                Integer.toString(mes), ", ",
                                "UN_PERIODO          =>",
                                Integer.toString(periodo), ", ",
                                "UN_EMPLEADO         =>",
                                Integer.toString(empleado), ", ",
                                "UN_CONCEPTO         =>",
                                Integer.toString(concepto)
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM1.FC_CONCEPTORELACIONADO",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public BigDecimal getSalarioBasico(
        String compania,
        int proceso,
        int ano,
        int mes,
        int periodo,
        int empleado)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA              =>'", compania, "', ",
                                "UN_PROCESO               =>",
                                Integer.toString(proceso), ", ",
                                "UN_ANO                   =>",
                                Integer.toString(ano), ", ",
                                "UN_MES                   =>",
                                Integer.toString(mes), ", ",
                                "UN_PERIODO               =>",
                                Integer.toString(periodo), ", ",
                                "UN_EMPLEADO              =>",
                                Integer.toString(empleado)
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM1.FC_SALARIOBASEHISTORICO",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public String getMiNovedad(
        String compania,
        int proceso,
        int ano,
        int mes,
        int empleado)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA       =>'", compania, "', ",
                                "UN_PROCESO        =>",
                                Integer.toString(proceso), ", ",
                                "UN_ANO            =>",
                                Integer.toString(ano), ", ",
                                "UN_MES            =>",
                                Integer.toString(mes), ", ",
                                "UN_EMPLEADO       =>",
                                Integer.toString(empleado)
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM1.FC_MINOVEDAD",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String getMisVacaciones(
        String compania,
        int proceso,
        int ano,
        int mes,
        int empleado,
        String opcion)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA              =>'", compania, "', ",
                                "UN_PROCESO               =>",
                                Integer.toString(proceso), ", ",
                                "UN_ANO                   =>",
                                Integer.toString(ano), ", ",
                                "UN_MES                   =>",
                                Integer.toString(mes), ", ",
                                "UN_EMPLEADO              =>",
                                Integer.toString(empleado), ", ",
                                "UN_OPCION            =>'", opcion, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM1.FC_MISVACACIONES",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public BigDecimal getSalarioBase(
        String compania,
        int empleado)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_EMPLEADO          =>",
                                Integer.toString(empleado)
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM1.FC_SALARIOBASE",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public String getParametroNomina(
        String compania,
        int opcion)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_OPCION            =>",
                                Integer.toString(opcion)
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM1.FC_PARAMETRO",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String getMiResolucion(
        String compania,
        int empleado,
        int opcion)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_EMPLEADO          =>",
                                Integer.toString(empleado), ", ",
                                "UN_OPCION            =>",
                                Integer.toString(opcion)
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM1.FC_MIRESOLUCION",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public void putCopiaPersona(
        String compania,
        int idempleado,
        int anio)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_IDEMPLEADO        =>",
                                Integer.toString(idempleado), ", ",
                                "UN_ANIO              =>",
                                Integer.toString(anio), ""

        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM1.FC_COPIAREMPLEADO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String getPivotKardexNomina(
        String compania,
        int ano,
        int empleadoini,
        int empleadofin,
        int nombremes,
        long mayorigual,
        boolean acumulable)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_EMPLEADOINI       =>",
                                Integer.toString(empleadoini), ", ",
                                "UN_EMPLEADOFIN       =>",
                                Integer.toString(empleadofin), ", ",
                                "UN_NOMBREMES         =>",
                                Integer.toString(nombremes), ", ",
                                "UN_MAYORIGUAL        =>",
                                Long.toString(mayorigual), ", ",
                                "UN_ACUMULABLE        =>",
                                (acumulable ? "-1" : "0"), ""
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM1.FC_PREPARARPIVOT_KARDEX",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String getKardexAcumNomina(
        String compania,
        String limiteinf,
        String limitesup,
        int idempleado)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_LIMITEINF         =>'", limiteinf, "', ",
                                "UN_LIMITESUP         =>'", limitesup, "', ",
                                "UN_IDEMPLEADO        =>",
                                Integer.toString(idempleado)
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM1.FC_PREPARARPIVOT_KARDEX_ACUM",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String getDatosEntidadesFondos(
        String compania,
        String codfondo,
        String tipofondo,
        int par)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CODFONDO          =>'", codfondo, "', ",
                                "UN_TIPOFONDO         =>'", tipofondo, "', ",
                                "UN_PAR               =>",
                                Integer.toString(par)
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM1.FC_TRAERDATOSENTIDADES",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public void putActualizarRetefuente(
        String compania,
        int ano,
        int mes,
        int periodo,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_MES      =>",
                                Integer.toString(mes), ", ",
                                "UN_PERIODO  =>",
                                Integer.toString(periodo), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM1.PR_ACTUALIZAR_RETEFUENTE",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public int getPrimerEmpleado(
        String compania,
        int registro)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_REGISTRO          =>",
                                Integer.toString(registro)
        };
        return (int) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM1.FC_PRIMERVALOR",
                        SysmanFunciones.concatenar(parametros),
                        Types.INTEGER);
    }

    @Override
    public String getNombrePeriodo(
        String compania,
        int proceso,
        int ano,
        int mes,
        int periodo)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PROCESO             =>",
                                Integer.toString(proceso), ", ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_MES               =>",
                                Integer.toString(mes), ", ",
                                "UN_PERIODO           =>",
                                Integer.toString(periodo)
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM1.FC_NOMBREPERIODO",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public BigDecimal getAcumConceptoValor(
        String compania,
        int concepto,
        int ano1,
        int mes1,
        int per1,
        int ano2,
        int mes2,
        int per2,
        int iddeempleado)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CONCEPTO          =>",
                                Integer.toString(concepto), ", ",
                                "UN_ANO1              =>",
                                Integer.toString(ano1), ", ",
                                "UN_MES1              =>",
                                Integer.toString(mes1), ", ",
                                "UN_PER1              =>",
                                Integer.toString(per1), ", ",
                                "UN_ANO2              =>",
                                Integer.toString(ano2), ", ",
                                "UN_MES2              =>",
                                Integer.toString(mes2), ", ",
                                "UN_PER2              =>",
                                Integer.toString(per2), ", ",
                                "UN_IDDEEMPLEADO       =>",
                                Integer.toString(iddeempleado)
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM1.FC_ACUMCONCEPTO_VALOR",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public BigDecimal getAcumNovedadConcValor(
        String compania,
        int concepto,
        int ano1,
        int mes1,
        int per1,
        int ano2,
        int mes2,
        int per2,
        int iddeempleado)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CONCEPTO          =>",
                                Integer.toString(concepto), ", ",
                                "UN_ANO1              =>",
                                Integer.toString(ano1), ", ",
                                "UN_MES1              =>",
                                Integer.toString(mes1), ", ",
                                "UN_PER1              =>",
                                Integer.toString(per1), ", ",
                                "UN_ANO2              =>",
                                Integer.toString(ano2), ", ",
                                "UN_MES2              =>",
                                Integer.toString(mes2), ", ",
                                "UN_PER2              =>",
                                Integer.toString(per2), ", ",
                                "UN_IDDEEMPLEADO    =>",
                                Integer.toString(iddeempleado)
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM1.FC_ACUMNOVEDADCONCEPTO_VALOR",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public BigDecimal getSalarioBaseCategoria(
        String compania,
        int iddeempleado)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_IDDEEMPLEADO      =>",
                                Integer.toString(iddeempleado), ""
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM1.FC_SALARIO_BASE_CATEG",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public BigDecimal getDeducible(
        String compania,
        int iddeempleado)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_IDDEEMPLEADO      =>",
                                Integer.toString(iddeempleado), ""
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM1.FC_DEDUCIBLE",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public String getPrepararPivotDaneEmpleado(
        String compania,
        String limiteinf,
        String limitesup)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_LIMITEINF         =>'", limiteinf, "', ",
                                "UN_LIMITESUP         =>'", limitesup, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM1.FC_PREPARARPIVOT_DANE_EMPLEADO",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public boolean getPeriodoActivadoFecha(
        String compania,
        int proceso,
        Date fecha,
        int periodo)
                    throws SystemException {
        byte salida;
        try {
        	
        	Optional<Date> valFecha = Optional.ofNullable(fecha);
        	
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_PROCESO           =>",
                                    Integer.toString(proceso), ", ",
                                    "UN_FECHA             =>",
                                    (String) (valFecha.isPresent() ? "TO_DATE('"+SysmanFunciones.convertirAFechaCadena(fecha)+"','DD/MM/YYYY')" : fecha),", ",
                                    "UN_PERIODO           =>",
                                    Integer.toString(periodo)
            };
            salida = (byte) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_NOMINA_COM1.FC_PERIODOACTIVADOFECHA",
                            SysmanFunciones.concatenar(parametros),
                            Types.TINYINT);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
        return salida == 0 ? false : true;
    }

    @Override
    public Date getUltimaFechaVacaciones(
        String compania,
        int empleado,
        boolean parametro)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_EMPLEADO          =>",
                                Integer.toString(empleado), ", ",
                                "UN_PARAMETRO         =>",
                                parametro ? "-1" : "0"
        };
        return (Date) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM1.FC_ULTIMAFECHAVAC",
                        SysmanFunciones.concatenar(parametros),
                        Types.DATE);
    }

    @Override
    public String getValidarFechasVacaciones(
        String compania,
        Date fecha,
        int empleado)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY'), ",
                                    "UN_EMPLEADO          =>",
                                    Integer.toString(empleado)
            };
            return (String) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_NOMINA_COM1.FC_VALIDARFECHASVACACIONES",
                            SysmanFunciones.concatenar(parametros),
                            Types.VARCHAR);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public BigDecimal getDiaPendienteVacacionesHistorico(
        String compania,
        int proceso,
        int empleado)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PROCESO           =>",
                                Integer.toString(proceso), ", ",
                                "UN_EMPLEADO          =>",
                                Integer.toString(empleado)
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM1.FC_DIAPENDIENTEVAC",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public String getAvisoVacaciones(
        String compania,
        int empleado,
        Date fechainicio)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_EMPLEADO          =>",
                                    Integer.toString(empleado), ", ",
                                    "UN_FECHAINICIO        =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechainicio),
                                    "','DD/MM/YYYY')"
            };
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_NOMINA_COM1.FC_AVISO_VACACIONES",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (ParseException | IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String getAvisoLicencias(
        String compania,
        int empleado,
        Date fechainicio,
        Date fechafinal)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_EMPLEADO          =>",
                                    Integer.toString(empleado), ", ",
                                    "UN_FECHAINICIO       =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechainicio),
                                    "','DD/MM/YYYY'), ",
                                    "UN_FECHAFINAL        =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechafinal),
                                    "','DD/MM/YYYY')"
            };
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_NOMINA_COM1.FC_AVISO_LICENCIAS",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (ParseException | IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public int getDiasLicCorrerVacaciones(
        String compania,
        int empleado,
        Date fechainicio,
        Date fechafinal)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_EMPLEADO          =>",
                                    Integer.toString(empleado), ", ",
                                    "UN_FECHAINICIO       =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechainicio),
                                    "','DD/MM/YYYY'), ",
                                    "UN_FECHAFINAL        =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechafinal),
                                    "','DD/MM/YYYY')"
            };
            return (int) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_NOMINA_COM1.FC_DIASLIC_CORRERVAC",
                            SysmanFunciones.concatenar(parametros),
                            Types.INTEGER);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public int getDiasHabilesVacaciones(
        String compania,
        int ano,
        int mes,
        int periodo,
        int empleado,
        int numeroper,
        int diaspendi)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_MES               =>",
                                Integer.toString(mes), ", ",
                                "UN_PERIODO           =>",
                                Integer.toString(periodo), ", ",
                                "UN_EMPLEADO          =>",
                                Integer.toString(empleado), ", ",
                                "UN_NUMEROPER         =>",
                                Integer.toString(numeroper), ", ",
                                "UN_DIASPENDI         =>",
                                Integer.toString(diaspendi)
        };
        return (int) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM1.FC_DIASHABILES_VACACIONES",
                        SysmanFunciones.concatenar(parametros),
                        Types.INTEGER);
    }

    @Override
    public Date getUltimoInterrupcion(
        String compania,
        int proceso,
        int ano,
        int mes,
        int periodo,
        int empleado,
        int parametro)
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
                                "UN_EMPLEADO          =>",
                                Integer.toString(empleado), ", ",
                                "UN_PARAMETRO         =>",
                                Integer.toString(parametro)
        };
        return (Date) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM1.FC_ULTIMAINTERRUPCION",
                        SysmanFunciones.concatenar(parametros),
                        Types.DATE);
    }

    @Override
    public Date getFechaFinalVacaciones(
        String compania,
        Date iniciodisfrute,
        int diashabiles,
        String sabadohabil,
        int modulo)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_INICIODISFRUTE    =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    iniciodisfrute),
                                    "','DD/MM/YYYY'), ",
                                    "UN_DIASHABILES       =>",
                                    Integer.toString(diashabiles), ", ",
                                    "UN_SABADOHABIL       =>'", sabadohabil,
                                    "', ", "UN_MODULO            =>",
                                    Integer.toString(modulo)
            };
            return (Date) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_NOMINA_COM1.FC_FECHAFINAL_VAC",
                            SysmanFunciones.concatenar(parametros),
                            Types.DATE);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String getPrepararPivotDevengosAnio(
        String compania,
        int ano)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano)
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM1.FC_PREPARARPIVOT_DEVENGOSANO",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String cierreNominaPreliminar(
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
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM1.FC_CIERRE_NOMINAPRELIMINAR",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }
    
    
    @Override
    public void guardahistoricoCune(
        String compania,
        int anio,
        int mes,
        String user) throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIO        =>",
                                Integer.toString(anio), ", ",
                                "UN_MES              =>",
                                Integer.toString(mes), ", ",
                                "UN_USUARIO              =>'",
                                user, "'"
                                

        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM1.PR_GUARDAHISTORICOCUNE",
                        SysmanFunciones.concatenar(parametros));
    }
    
    @Override
    public void actNominaCune(
        String compania,
        int anio,
        int mes,
        String tipoNom,
        int consec,
        Date fechaRpt,
        BigDecimal trm,
        String user,
        String empleado) throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA   =>'", compania, "', ",
                                    "UN_ANO        =>",
                                    Integer.toString(anio), ", ",
                                    "UN_MES        =>",
                                    Integer.toString(mes), ", ",
                                    "UN_TIPONOM    =>'", tipoNom, "', ",
                                    "UN_FECHARPT   =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaRpt),
                                    "','DD/MM/YYYY'), ",
                                    "UN_TRM        =>",
                                    trm.toString(), ", ",
                                    "UN_CONSEC     =>",
                                    Integer.toString(consec), ", ",
                                    "UN_USUARIO    =>'",
                                    user, "', ",
                                    "UN_EMPLEADO   =>'",
                                    empleado, "'"};
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_NOMINA_COM1.PR_ACTNOMINACUNE",
                            SysmanFunciones.concatenar(parametros));     
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
       
    }
    
    @Override
    public boolean actEstNominaCune(
            String compania,
            int anio,
            int mes,
            String tipoNom,
            int consec,
            String nroDoc,
            String ope
            ) throws SystemException {
        byte salida;
        
        String[] parametros = { "UN_COMPANIA   =>'", compania, "', ",
		         "UN_ANO        =>",
		         Integer.toString(anio), ", ",
		         "UN_MES        =>",
		         Integer.toString(mes), ", ",
		         "UN_TIPONOM    =>'", tipoNom, "', ",
		         "UN_CONSEC     =>",
		         Integer.toString(consec), ", ",
		         "UN_NRODOC     =>",
		         nroDoc, ", ",
		         "UN_OPERACION     =>'", ope, "'" 
		         };
		         
		 salida = (byte) AccionesImp.ejecutarFuncion(
		                ConectorPool.ESQUEMA_SYSMAN,
		                "PCK_NOMINA_COM1.FC_ACTESTNOMINACUNE",
		                SysmanFunciones.concatenar(parametros),
		                Types.TINYINT);
		 
         return salida == 0 ? false : true;
    }


    @Override
    public String revisionDatosCune(
        String compania,
        int anio,
        int mes) throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA   =>'", compania, "', ",
                                    "UN_ANO        =>",
                                    Integer.toString(anio), ", ",
                                    "UN_MES        =>",
                                    Integer.toString(mes), " "
                                     };
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_NOMINA_COM1.FC_REVISIONDATOSCUNE",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));   
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
       
    }
    
    @Override
    public String getPreparaPivotConsFact(
        String compania,
        int ano,
        int mes)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
				        		"UN_ANO              =>",
				                Integer.toString(ano), ", ",
				                "UN_MES              =>",
				                Integer.toString(mes), ""
	    };
        
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM1.FC_PREPARAPIVOT_CONSFACT",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }
    
    @Override
    public void porcentajesFsp(
        String compania,
        int anio,
        int salarioMinimo,
        String usuario)
            throws SystemException {
        
        String[] parametros = {
            "UN_COMPANIA       =>'", compania, "', ",
            "UN_ANIO           =>", Integer.toString(anio), ", ",
            "UN_SALARIOMINIMO  =>", Integer.toString(salarioMinimo), ", ",
            "UN_USUARIO        =>'", usuario, "'"
        };
        
        AccionesImp.ejecutarProcedimiento(
            ConectorPool.ESQUEMA_SYSMAN,
            "PCK_NOMINA_COM1.PR_PORCENTAJES_FSP_ADI",
            SysmanFunciones.concatenar(parametros)
        );
    }

}