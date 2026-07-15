package com.sysman.bancoproyectos.ejb.impl;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectoDosLocal;
import com.sysman.bancoproyectos.ejb.EjbBancoProyectoDosRemote;
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

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class BancoProyectoDos
 */
@Stateless
@LocalBean
public class EjbBancoProyectoDos
                implements EjbBancoProyectoDosRemote, EjbBancoProyectoDosLocal {
    /**
     * Default constructor.
     */
    public EjbBancoProyectoDos() {
    }

    @Override
    public String actualizarProyectoMante(
        String compania,
        String proyectoInicial,
        String proyectoFinal,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PROYECTO_INICIAL  =>'", proyectoInicial,
                                "', ", "UN_PROYECTO_FINAL    =>'",
                                proyectoFinal, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_BANCOS_PROY2.FC_MANTENIMIENTO_ACT_PROYECTO",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public BigDecimal getCantidadPorActividad(
        String compania,
        int vigencia,
        String proyecto,
        String componente,
        String tipoComponente,
        String actividad)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA      =>'", compania, "', ",
                                "UN_VIGENCIA         =>",
                                Integer.toString(vigencia), ", ",
                                "UN_PROYECTO         =>'", proyecto, "', ",
                                "UN_COMPONENTE        =>'", componente, "', ",
                                "UN_TIPO_COMPONENTE   =>'", tipoComponente,
                                "', ", "UN_ACTIVIDAD         =>'", actividad,
                                "'"
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_BANCOS_PROY2.FC_CANT_ACTIVIDAD",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public BigDecimal getCantidadDisponible(
        String compania,
        String estado,
        int vigencia,
        String proyecto,
        String componente,
        String tipoComponente,
        String actividad,
        int valNuevo,
        int valViejo)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA           =>'", compania, "', ",
                                "UN_ESTADO            =>'", estado, "', ",
                                "UN_VIGENCIA          =>",
                                Integer.toString(vigencia), ", ",
                                "UN_PROYECTO          =>'", proyecto, "', ",
                                "UN_COMPONENTE        =>'", componente, "', ",
                                "UN_TIPO_COMPONENTE   =>'", tipoComponente,
                                "', ", "UN_ACTIVIDAD         =>'", actividad,
                                "', ", "UN_VAL_NUEVO         =>",
                                Integer.toString(valNuevo), ", ",
                                "UN_VAL_VIEJO         =>",
                                Integer.toString(valViejo), ""
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_BANCOS_PROY2.FC_CANT_DISPONIBLE",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public boolean actualizarProgramacion(
        String compania,
        String tipo,
        String clase,
        long novedad,
        String dependencia,
        int vigencia,
        String proyecto,
        int actPlanind,
        String tipoNovedad,
        String usuario)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA         =>'", compania, "', ",
                                "UN_TIPO             =>'", tipo, "', ",
                                "UN_CLASE            =>'", clase, "', ",
                                "UN_NOVEDAD          =>",
                                Long.toString(novedad), ", ",
                                "UN_DEPENDENCIA      =>'", dependencia, "', ",
                                "UN_VIGENCIA          =>",
                                Integer.toString(vigencia), ", ",
                                "UN_PROYECTO          =>'", proyecto, "', ",
                                "UN_ACT_PLANIND       =>",
                                Integer.toString(actPlanind), ", ",
                                "UN_TIPO_NOVEDAD      =>'", tipoNovedad, "', ",
                                "UN_USUARIO            =>'", usuario, "'"
        };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_BANCOS_PROY2.FC_ACT_PROGRAMACION",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public boolean actualizarProgramado(
        String compania,
        String proyectoInicial,
        String proyectoFinal,
        String usuario)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA           =>'", compania, "', ",
                                "UN_PROYECTO_INICIAL  =>'", proyectoInicial,
                                "', ", "UN_PROYECTO_FINAL    =>'",
                                proyectoFinal, "', ",
                                "UN_USUARIO             =>'", usuario, "'"
        };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_BANCOS_PROY2.FC_ACT_PROGRAMADO",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public int insertaInconsistenciaNovedad(
        String compania,
        String tipo,
        String clase,
        long novedad,
        String dependencia,
        long rscodigo,
        String rsproyecto,
        String rscomponentein,
        String rstipocomponentein,
        String rsactividadin,
        int cantidad,
        String inconsistencia,
        int rsvigencia,
        String estado,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA         =>'", compania, "', ",
                                "UN_TIPO              =>'", tipo, "', ",
                                "UN_CLASE             =>'", clase, "', ",
                                "UN_NOVEDAD           =>",
                                Long.toString(novedad), ", ",
                                "UN_DEPENDENCIA       =>'", dependencia, "', ",
                                "UN_RSCODIGO          =>",
                                Long.toString(rscodigo), ", ",
                                "UN_RSPROYECTO        =>'", rsproyecto, "', ",
                                "UN_RSCOMPONENTEIN    =>'", rscomponentein,
                                "', ", "UN_RSTIPOCOMPONENTEI =>'",
                                rstipocomponentein, "', ",
                                "UN_RSACTIVIDADIN     =>'", rsactividadin,
                                "', ", "UN_CANTIDAD          =>",
                                Integer.toString(cantidad), ", ",
                                "UN_INCONSISTENCIA    =>'", inconsistencia,
                                "', ", "UN_RSVIGENCIA        =>",
                                Integer.toString(rsvigencia), ", ",
                                "UN_ESTADO            =>'", estado, "', ",
                                "UN_USUARIO          =>'", usuario, "'"
        };
        return (int) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_BANCOS_PROY2.FC_REG_INCONSISTENCIA_NOVE",
                        SysmanFunciones.concatenar(parametros),
                        Types.INTEGER);
    }

    @Override
    public int actualizaIndicadoresNovedad(
        String compania,
        String tipo,
        String clase,
        int novedad,
        String dependencia,
        long rscodigo,
        String rsproyecto,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_TIPO              =>'", tipo, "', ",
                                "UN_CLASE             =>'", clase, "', ",
                                "UN_NOVEDAD           =>",
                                Integer.toString(novedad), ", ",
                                "UN_DEPENDENCIA       =>'", dependencia, "', ",
                                "UN_RSCODIGO          =>",
                                Long.toString(rscodigo), ", ",
                                "UN_RSPROYECTO        =>'", rsproyecto, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        return (int) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_BANCOS_PROY2.FC_ACT_INDICADOR_NOVEDAD",
                        SysmanFunciones.concatenar(parametros),
                        Types.INTEGER);
    }

    @Override
    public String actulizaTotalesProyecto(
        String compania,
        String proyectoInicial,
        String proyectoFinal,
        int actTotal,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PROYECTO_INICIAL  =>'", proyectoInicial,
                                "', ", "UN_PROYECTO_FINAL    =>'",
                                proyectoFinal, "', ", "UN_ACT_TOTAL         =>",
                                Integer.toString(actTotal), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        try {
            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_BANCOS_PROY2.FC_REVISAR_TOTALES",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String validarProcesos(
        String compania,
        int proceso,
        String proyectoInicial,
        String proyectoFinal,
        boolean actTotal,
        String usuario,
        int modulo)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PROCESO           =>",
                                Integer.toString(proceso), ", ",
                                "UN_PROYECTO_INICIAL  =>'", proyectoInicial,
                                "', ", "UN_PROYECTO_FINAL    =>'",
                                proyectoFinal, "', ", "UN_ACT_TOTAL         =>",
                                actTotal ? "-1" : "0", ", ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_MODULO            =>",
                                Integer.toString(modulo), ""
        };
        try {
            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_BANCOS_PROY2.FC_VALIDARPROCESOS",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void verificarSaldoProyecto(
        String compania,
        String proyecto,
        BigDecimal valor,
        BigDecimal valorAnt,
        BigDecimal valorproyecto)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PROYECTO          =>'", proyecto, "', ",
                                "UN_VALOR             =>", valor.toString(),
                                ", ", "UN_VALOR_ANT         =>",
                                valorAnt.toString(), ", ",
                                "UN_VALORPROYECTO     =>",
                                valorproyecto.toString()
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_BANCOS_PROY2.PR_VERIFICARSALDOPROYECTO",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void validarVOBO(
        String compania,
        String tipot,
        String claset,
        String novedad,
        String dependencia,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_TIPOT             =>'", tipot, "', ",
                                "UN_CLASET            =>'", claset, "', ",
                                "UN_NOVEDAD           =>'", novedad, "', ",
                                "UN_DEPENDENCIA       =>'", dependencia, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN, "PCK_BANCOS_PROY2.PR_VOBO_AFTER",
                        SysmanFunciones.concatenar(parametros));
    }

}