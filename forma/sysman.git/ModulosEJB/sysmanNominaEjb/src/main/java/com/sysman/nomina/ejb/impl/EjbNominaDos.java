package com.sysman.nomina.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.nomina.ejb.EjbNominaDosLocal;
import com.sysman.nomina.ejb.EjbNominaDosRemote;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;
import java.util.Optional;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class NominaDos
 */
@Stateless
@LocalBean
public class EjbNominaDos implements EjbNominaDosRemote, EjbNominaDosLocal {
    /**
     * Default constructor.
     */
    public EjbNominaDos() {
    }

    @Override
    public void putActualizarSueldos(
                    String compania,
                    int anoBase,
                    int anoActualizar,
                    double porcentajeIncremento,
                    double porcentajeMesada,
                    String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                        "UN_ANO_BASE          =>",
                        Integer.toString(anoBase), ", ",
                        "UN_ANO_ACTUALIZAR    =>",
                        Integer.toString(anoActualizar), ", ",
                        "UN_PORCENTAJE_INCREMENTO =>",
                        Double.toString(porcentajeIncremento), ", ",
                        "UN_PORCENTAJE_MESADA =>",
                        Double.toString(porcentajeMesada), ", ",
                        "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM2.PR_ACTUALIZARSUELDOS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public long getCategoriaAnio(
                    String compania,
                    int ano)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                        "UN_ANO               =>",
                        Integer.toString(ano)
        };
        return (long) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM2.FC_EXISTE_CATEGORIA_ANO",
                        SysmanFunciones.concatenar(parametros),
                        Types.BIGINT);
    }

    @Override
    public void eliminarCategoria(
                    int anoBase,
                    String compania)
                    throws SystemException {
        String[] parametros = { "UN_ANO_BASE          =>",
                        Integer.toString(anoBase), ", ",
                        "UN_COMPANIA          =>'", compania, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM2.PR_ELIMINARDECATEGORIA",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public long getIncluirNovedadEncargos(
                    int mes,
                    int periodo,
                    int ano,
                    int proceso,
                    String compania,
                    Date fechainicio,
                    Date fechafin,
                    String user,
                    int idEmpleado)
                    throws SystemException {
        try {
            String[] parametros = { "UN_MES               =>",
                            Integer.toString(mes), ", ",
                            "UN_PERIODO           =>",
                            Integer.toString(periodo), ", ",
                            "UN_ANO               =>",
                            Integer.toString(ano), ", ",
                            "UN_PROCESO           =>",
                            Integer.toString(proceso), ", ",
                            "UN_COMPANIA          =>'", compania, "', ",
                            "UN_FECHAINICIO       =>TO_DATE('",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechainicio),
                            "','DD/MM/YYYY'), ",
                            "UN_FECHAFIN          =>TO_DATE('",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechafin),
                            "','DD/MM/YYYY'), ",
                            "UN_USER              =>'", user, "',",
                            "UN_ID_EMPLEADO               =>",
                            Integer.toString(idEmpleado), " "
            };
            return (long) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_NOMINA_COM2.FC_INCLUIR_NOVEDAD_ENCARGOS",
                            SysmanFunciones.concatenar(parametros),
                            Types.BIGINT);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public long getIncluirNovedadLicencias(
                    int mes,
                    int periodo,
                    int ano,
                    int proceso,
                    String compania,
                    Date fechainicio,
                    Date fechafin,
                    String usuario)
                    throws SystemException {
        try {
            String[] parametros = { "UN_MES               =>",
                            Integer.toString(mes), ", ",
                            "UN_PERIODO           =>",
                            Integer.toString(periodo), ", ",
                            "UN_ANO               =>",
                            Integer.toString(ano), ", ",
                            "UN_PROCESO           =>",
                            Integer.toString(proceso), ", ",
                            "UN_COMPANIA          =>'", compania, "', ",
                            "UN_FECHAINICIO       =>TO_DATE('",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechainicio),
                            "','DD/MM/YYYY'), ",
                            "UN_FECHAFIN          =>TO_DATE('",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechafin),
                            "','DD/MM/YYYY'), ",
                            "UN_USUARIO           =>'", usuario, "'"
            };
            return (long) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_NOMINA_COM2.FC_INCLUIR_NOVEDAD_LICENCIAS",
                            SysmanFunciones.concatenar(parametros),
                            Types.BIGINT);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public boolean getExistePeriodo(
                    String compania,
                    int ano,
                    int periodo,
                    int mes,
                    int proceso)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                        "UN_ANO               =>",
                        Integer.toString(ano), ", ",
                        "UN_PERIODO           =>",
                        Integer.toString(periodo), ", ",
                        "UN_MES               =>",
                        Integer.toString(mes), ", ",
                        "UN_PROCESO           =>",
                        Integer.toString(proceso)
        };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM2.FC_CONTAR_PERIODOS",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }
    
    @Override
    public boolean getValidaAnioMesada(
                    String compania,
                    int ano,
                    int estado)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                        "UN_ANO               =>",
                        Integer.toString(ano), ", ",
                        "UN_ESTADO           =>",
                        Integer.toString(estado)
        };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM2.FC_VALIDAANIOMESADA",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }
    
    @Override
    public boolean reversaMesada(
                    String compania,
                    String usuario)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
        		 				"UN_USUARIO           =>'", usuario, "'"
        };
        
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM2.FC_REVMESADA",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public BigDecimal getValorConceptoNovedad(
                    String compania,
                    int ano,
                    int periodo,
                    int mes,
                    int concepto)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                        "UN_ANO               =>",
                        Integer.toString(ano), ", ",
                        "UN_PERIODO           =>",
                        Integer.toString(periodo), ", ",
                        "UN_MES               =>",
                        Integer.toString(mes), ", ",
                        "UN_CONCEPTO          =>",
                        Integer.toString(concepto)
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM2.FC_VALOR_CONCEPTO_NOVEDAD",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public void prepararFinanciables(
                    String compania,
                    int ano1,
                    int mes1,
                    int periodo1,
                    int ano2,
                    int mes2,
                    int periodo2,
                    String usuario,
                    int tipoEmpleado,
                    String igualODiferente)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                        "UN_ANO1              =>",
                        Integer.toString(ano1), ", ",
                        "UN_MES1              =>",
                        Integer.toString(mes1), ", ",
                        "UN_PERIODO1          =>",
                        Integer.toString(periodo1), ", ",
                        "UN_ANO2              =>",
                        Integer.toString(ano2), ", ",
                        "UN_MES2              =>",
                        Integer.toString(mes2), ", ",
                        "UN_PERIODO2          =>",
                        Integer.toString(periodo2), ", ",
                        "UN_USUARIO           =>'", usuario, "', ",
                        "UN_TIPO_EMPLEADO     =>",
                        Integer.toString(tipoEmpleado), ", ",
                        "UN_IGUAL_O_DIFERENTE =>'", igualODiferente, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM2.PR_PREPARARFINANCIABLES",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String siguientePeriodo(
                    String compania,
                    int ano,
                    int periodo,
                    int mes,
                    int proceso,
                    String opcion)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                        "UN_ANO               =>",
                        Integer.toString(ano), ", ",
                        "UN_PERIODO           =>",
                        Integer.toString(periodo), ", ",
                        "UN_MES               =>",
                        Integer.toString(mes), ", ",
                        "UN_PROCESO           =>",
                        Integer.toString(proceso), ", ",
                        "UN_OPCION            =>'", opcion, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM2.FC_SIGUIENTEPERIODO",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public void prepararEmbargos(
                    String compania,
                    int ano1,
                    int mes1,
                    int periodo1,
                    int ano2,
                    int mes2,
                    int periodo2,
                    String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                        "UN_ANO1              =>",
                        Integer.toString(ano1), ", ",
                        "UN_MES1              =>",
                        Integer.toString(mes1), ", ",
                        "UN_PERIODO1          =>",
                        Integer.toString(periodo1), ", ",
                        "UN_ANO2              =>",
                        Integer.toString(ano2), ", ",
                        "UN_MES2              =>",
                        Integer.toString(mes2), ", ",
                        "UN_PERIODO2          =>",
                        Integer.toString(periodo2), ", ",
                        "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM2.PR_PREPARAREMBARGOS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void retefuente(
                    String compania,
                    int anoNuevo,
                    int anoSession,
                    String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                        "UN_ANO_NUEVO         =>",
                        Integer.toString(anoNuevo), ", ",
                        "UN_ANO_SESSION       =>",
                        Integer.toString(anoSession), ", ",
                        "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM2.PR_RETEFUENTE",
                        SysmanFunciones.concatenar(parametros));
    }
    
    @Override
    public void duplicarHE(
                    String compania,
                    int anoNuevo,
                    int ano,
                    String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                        "UN_ANO_NUEVO         =>",
                        Integer.toString(anoNuevo), ", ",
                        "UN_ANO       =>",
                        Integer.toString(ano), ", ",
                        "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM2.PR_DUPLICARHE",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public BigDecimal getAusentismoEmpleadoTotal(
                    String compania,
                    int idEmpleado)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                        "UN_ID_EMPLEADO       =>",
                        Integer.toString(idEmpleado)
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM2.FC_AUSENTISMO",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public BigDecimal getAutorizarEnvioCorreo(
                    String compania,
                    int modulo)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                        "UN_MODULO            =>",
                        Integer.toString(modulo)
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM2.FC_AUTORIZARENVIOCORREO",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public boolean getDiferir(
                    String compania,
                    int proceso,
                    int anio,
                    int mes,
                    int periodo,
                    int idEmpleado,
                    int concepto,
                    int diferido,
                    String opcion,
                    int concepto1,
                    BigDecimal valor1,
                    int concepto2,
                    BigDecimal valor2,
                    Date fechainicio,
                    int incapacidad,
                    String usuario)
                    throws SystemException {
        byte salida;
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                            "UN_PROCESO           =>",
                            Integer.toString(proceso), ", ",
                            "UN_ANIO              =>",
                            Integer.toString(anio), ", ",
                            "UN_MES               =>",
                            Integer.toString(mes), ", ",
                            "UN_PERIODO           =>",
                            Integer.toString(periodo), ", ",
                            "UN_ID_EMPLEADO       =>",
                            Integer.toString(idEmpleado), ", ",
                            "UN_CONCEPTO          =>",
                            Integer.toString(concepto), ", ",
                            "UN_DIFERIDO          =>",
                            Integer.toString(diferido), ", ",
                            "UN_OPCION            =>'", opcion, "', ",
                            "UN_CONCEPTO1         =>",
                            Integer.toString(concepto1), ", ",
                            "UN_VALOR1            =>",
                            valor1.toString(), ", ",
                            "UN_CONCEPTO2         =>",
                            Integer.toString(concepto2), ", ",
                            "UN_VALOR2            =>",
                            valor2.toString(), ", ",
                            "UN_FECHAINICIO       =>TO_DATE('",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechainicio),
                            "','DD/MM/YYYY'), ",
                            "UN_INCAPACIDAD       =>",
                            Integer.toString(incapacidad), ", ",
                            "UN_USUARIO           =>'", usuario, "'"
            };
            salida = (byte) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_NOMINA_COM2.FC_DIFERIR",
                            SysmanFunciones.concatenar(parametros),
                            Types.TINYINT);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
        return salida == 0 ? false : true;
    }

    @Override
    public boolean getDiferirVac(
                    String compania,
                    int proceso,
                    int idEmpleado,
                    int concepto,
                    int diahabil,
                    int diadinero,
                    int diapendiente,
                    int dias,
                    Date iniciodisfrute,
                    Date fechapago,
                    int numperiodo,
                    boolean indbonificacion,
                    String opcion,
                    String usuario)
                    throws SystemException {
        byte salida;
        
        Optional<Date> valFechaPago = Optional.ofNullable(fechapago);
        
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                            "UN_PROCESO           =>",
                            Integer.toString(proceso), ", ",
                            "UN_ID_EMPLEADO       =>",
                            Integer.toString(idEmpleado), ", ",
                            "UN_CONCEPTO          =>",
                            Integer.toString(concepto), ", ",
                            "UN_DIAHABIL          =>",
                            Integer.toString(diahabil), ", ",
                            "UN_DIADINERO         =>",
                            Integer.toString(diadinero), ", ",
                            "UN_DIAPENDIENTE      =>",
                            Integer.toString(diapendiente), ", ",
                            "UN_DIAS              =>",
                            Integer.toString(dias), ", ",
                            "UN_INICIODISFRUTE    =>TO_DATE('",
                            SysmanFunciones.convertirAFechaCadena(
                                            iniciodisfrute),
                            "','DD/MM/YYYY'), ",
                            "UN_FECHAPAGO         =>", 
                            (String) (valFechaPago.isPresent() ? "TO_DATE('"+SysmanFunciones.convertirAFechaCadena(fechapago)+"','DD/MM/YYYY')" : fechapago),", ",
                            "UN_NUMPERIODO        =>",
                            Integer.toString(numperiodo), ", ",
                            "UN_INDBONIFICACION        =>",
                            indbonificacion ? "-1":"0", ", ",
                            "UN_OPCION            =>",
                            opcion != null ? "'" + opcion + "'"
                                            : opcion,
                            ", ",
                            "UN_USUARIO           =>'", usuario, "'"
            };
            salida = (byte) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_NOMINA_COM2.FC_DIFERIRVAC",
                            SysmanFunciones.concatenar(parametros),
                            Types.TINYINT);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
        return salida == 0 ? false : true;
    }

    @Override
    public boolean getActualizarVacaPeriodo(
                    String compania,
                    int idDeProceso,
                    int anio,
                    int mes,
                    int periodo,
                    int idDeEmpleado,
                    String usuario)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                        "UN_ID_DE_PROCESO     =>",
                        Integer.toString(idDeProceso), ", ",
                        "UN_ANIO              =>",
                        Integer.toString(anio), ", ",
                        "UN_MES               =>",
                        Integer.toString(mes), ", ",
                        "UN_PERIODO           =>",
                        Integer.toString(periodo), ", ",
                        "UN_ID_DE_EMPLEADO    =>",
                        Integer.toString(idDeEmpleado), ", ",
                        "UN_USUARIO           =>'", usuario, "'"
        };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM2.FC_ACTUALIZAR_VACA_PERIODO",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public boolean getDiferirMas(
                    String compania,
                    int proceso,
                    int anio,
                    int mes,
                    int periodo,
                    int idEmpleado,
                    int concepto,
                    int diferido,
                    String opcion,
                    int concepto1,
                    BigDecimal valor1,
                    int concepto2,
                    BigDecimal valor2,
                    Date fechainicio,
                    String usuario)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                        "UN_PROCESO           =>",
                        Integer.toString(proceso), ", ",
                        "UN_ANIO              =>",
                        Integer.toString(anio), ", ",
                        "UN_MES               =>",
                        Integer.toString(mes), ", ",
                        "UN_PERIODO           =>",
                        Integer.toString(periodo), ", ",
                        "UN_ID_EMPLEADO       =>",
                        Integer.toString(idEmpleado), ", ",
                        "UN_CONCEPTO          =>",
                        Integer.toString(concepto), ", ",
                        "UN_DIFERIDO          =>",
                        Integer.toString(diferido), ", ",
                        "UN_OPCION            =>",
                        opcion != null ? "'" + opcion + "'" : null,
                        ", ",
                        "UN_CONCEPTO1         =>",
                        Integer.toString(concepto1), ", ",
                        "UN_VALOR1            =>",
                        valor1.toString(), ", ",
                        "UN_CONCEPTO2         =>",
                        Integer.toString(concepto2), ", ",
                        "UN_VALOR2            =>",
                        valor2.toString(), ", ",
                        "UN_FECHAINICIO       =>",
                        SysmanFunciones.formatearFecha(fechainicio),
                        ", ",
                        "UN_USUARIO           =>'", usuario, "'"
        };
        salida = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM2.FC_DIFERIRMAS",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public boolean getDiferirMas(
                    String compania,
                    int proceso,
                    int anio,
                    int mes,
                    int periodo,
                    int idEmpleado,
                    int concepto,
                    int diferido,
                    String opcion,
                    Date fechainicio,
                    String usuario,
                    Date fechaFinalOrg
                    )
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                        "UN_PROCESO           =>",
                        Integer.toString(proceso), ", ",
                        "UN_ANIO              =>",
                        Integer.toString(anio), ", ",
                        "UN_MES               =>",
                        Integer.toString(mes), ", ",
                        "UN_PERIODO           =>",
                        Integer.toString(periodo), ", ",
                        "UN_ID_EMPLEADO       =>",
                        Integer.toString(idEmpleado), ", ",
                        "UN_CONCEPTO          =>",
                        Integer.toString(concepto), ", ",
                        "UN_DIFERIDO          =>",
                        Integer.toString(diferido), ", ",
                        "UN_OPCION            =>",
                        opcion != null ? "'" + opcion + "'" : null, ", ",
                        "UN_CONCEPTO1         => NULL,",
                        "UN_VALOR1            => NULL,",
                        "UN_CONCEPTO2         => NULL,",
                        "UN_VALOR2            => NULL,",
                        "UN_FECHAINICIO       =>",
                        SysmanFunciones.formatearFecha(fechainicio),
                        ", ",
                        "UN_USUARIO           =>'", usuario, "', ",
                        "UN_FECHAFIN           =>", SysmanFunciones.formatearFecha(fechaFinalOrg),
        };
        salida = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM2.FC_DIFERIRMAS",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public void crearCentrosDeCosto(
                    int anoCrear,
                    int anoSession,
                    String compania,
                    String usuario)
                    throws SystemException {
        String[] parametros = { "UN_ANO_CREAR         =>",
                        Integer.toString(anoCrear), ", ",
                        "UN_ANO_SESSION       =>",
                        Integer.toString(anoSession), ", ",
                        "UN_COMPANIA          =>'", compania, "', ",
                        "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM2.PR_CREARCENTROSDECOSTO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public boolean getDiferirQuin(
                    String compania,
                    int proceso,
                    int idEmpleado,
                    String opcion,
                    Date fechapago)
                    throws SystemException {
        byte salida;
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                            "UN_PROCESO           =>",
                            Integer.toString(proceso), ", ",
                            "UN_ID_EMPLEADO       =>",
                            Integer.toString(idEmpleado), ", ",
                            "UN_OPCION            =>'", opcion, "', ",
                            "UN_FECHAPAGO         =>TO_DATE('",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechapago),
                            "','DD/MM/YYYY')"
            };
            salida = (byte) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_NOMINA_COM2.FC_DIFERIR_QUIN",
                            SysmanFunciones.concatenar(parametros),
                            Types.TINYINT);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
        return salida == 0 ? false : true;
    }

    @Override
    public boolean getDiferirEnc(
                    String compania,
                    int proceso,
                    int idEmpleado,
                    int diferido,
                    String opcion,
                    Date fechainicio,
                    double porgasto,
                    BigDecimal salario,
                    String usuario)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                        "UN_PROCESO           =>",
                        Integer.toString(proceso), ", ",
                        "UN_ID_EMPLEADO       =>",
                        Integer.toString(idEmpleado), ", ",
                        "UN_DIFERIDO          =>",
                        Integer.toString(diferido), ", ",
                        "UN_OPCION            =>",
                        opcion != null ? "'" + opcion + "'" : null,
                        ", ",
                        "UN_FECHAINICIO       =>",
                        SysmanFunciones.formatearFecha(fechainicio),
                        ", ",
                        "UN_PORGASTO          =>",
                        Double.toString(porgasto), ", ",
                        "UN_SALARIO           =>",
                        salario.toString(), ", ",
                        "UN_USUARIO           =>'", usuario, "'"
        };
        salida = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM2.FC_DIFERIR_ENC",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public boolean getDiferirIntVac(
                    String compania,
                    int proceso,
                    int idEmpleado,
                    String opcion,
                    Date fechapago,
                    boolean endinero,
                    Date fechainterrupcion,
                    Date fechafinaldisfrute,
                    int diasinterrupcion,
                    String usuario)
                    throws SystemException {
        byte salida;
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                            "UN_PROCESO           =>",
                            Integer.toString(proceso), ", ",
                            "UN_ID_EMPLEADO       =>",
                            Integer.toString(idEmpleado), ", ",
                            "UN_OPCION            =>",
                            opcion, ", ",
                            "UN_FECHAPAGO         =>TO_DATE('",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechapago),
                            "','DD/MM/YYYY'), ",
                            "UN_ENDINERO          =>",
                            endinero ? "-1" : "0", ", ",
                            "UN_FECHAINTERRUPCION =>TO_DATE('",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechainterrupcion),
                            "','DD/MM/YYYY'), ",
                            "UN_FECHAFINALDISFRUTE =>TO_DATE('",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechafinaldisfrute),
                            "','DD/MM/YYYY'), ",
                            "UN_DIASINTERRUPCION  =>",
                            Integer.toString(diasinterrupcion), ", ",
                            "UN_USUARIO           =>'", usuario, "'"
            };
            salida = (byte) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_NOMINA_COM2.FC_DIFERIR_INTVAC",
                            SysmanFunciones.concatenar(parametros),
                            Types.TINYINT);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
        return salida == 0 ? false : true;
    }

    @Override
    public String getNombreConcepto(
                    String compania,
                    int idDeConcepto)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                        "UN_ID_DE_CONCEPTO    =>",
                        Integer.toString(idDeConcepto)
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM2.FC_NOMBRECONCEPTO",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public void crearDatos(
                    String compania,
                    String companiaBase,
                    String nombreCompania,
                    String siglaCompania,
                    String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                        "UN_COMPANIA_BASE     =>'", companiaBase, "', ",
                        "UN_NOMBRE_COMPANIA   =>'", nombreCompania,
                        "', ", "UN_SIGLA_COMPANIA    =>'",
                        siglaCompania, "', ",
                        "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM2.PR_CREARDATOS",
                        SysmanFunciones.concatenar(parametros));
    }
    @Override
    public  int  difereirEncMod(
    		String compania, 
    		String proceso, 
    		String idEmpleado, 
    		String opcion, 
			Date fechainicio, 
			Date fechafinal, 
			double porgasto, 
			double salario, 
			String usuario, 
			String periodo) 
                      throws SystemException {
         try {
         String[] parametros ={                   "UN_COMPANIA          =>'" , compania , "', "
                 , "UN_PROCESO           =>" , proceso , ", "
                 , "UN_ID_EMPLEADO       =>" , idEmpleado , ", "
                 , "UN_OPCION            =>" , opcion , ", "
                 , "UN_FECHAINICIO       =>TO_DATE('" , SysmanFunciones.convertirAFechaCadena(fechainicio) , "','DD/MM/YYYY'), "
                 , "UN_FECHAFINAL        =>TO_DATE('" , SysmanFunciones.convertirAFechaCadena(fechafinal) , "','DD/MM/YYYY'), "
                 , "UN_PORGASTO          =>" , Double.toString(porgasto)   ,", "
                 , "UN_SALARIO           =>" , Double.toString(salario)    ,", "
                 , "UN_USUARIO           =>'" , usuario , "', "
                 , "UN_PERIODO           =>" , periodo , ""
};
         return (int) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN, "PCK_NOMINA_COM2.FC_DIFERIR_ENC_MOD",
SysmanFunciones.concatenar(parametros),
                Types.INTEGER);
         }
         catch (ParseException e) {
             throw new SystemException(e);
          }
    }
    
    @Override
    public void insertBecp(
            String compania,
            int idEmpleado,
            int proceso,
            int anio,
            int mes,
            int periodo,
            String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA =>'",compania,"',",
				        		"UN_IDEMPLEADO =>",Integer.toString(idEmpleado),",",
				        		"UN_PROCESO =>", Integer.toString(proceso),",",
				        		"UN_ANIO =>", Integer.toString(anio),",",
				        		"UN_MES =>", Integer.toString(mes),",",
				        		"UN_PERIODO =>", Integer.toString(periodo),",",
				        		"UN_USUARIO =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM2.PR_INSERTINF_BECP",
                        SysmanFunciones.concatenar(parametros));
    }

}