package com.sysman.bancoproyectos.ejb.impl;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectoTresLocal;
import com.sysman.bancoproyectos.ejb.EjbBancoProyectoTresRemote;
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
 * Session Bean implementation class BancoProyectoTres
 */
@Stateless
@LocalBean
public class EjbBancoProyectoTres implements EjbBancoProyectoTresRemote,
                EjbBancoProyectoTresLocal {
    /**
     * Default constructor.
     */
    public EjbBancoProyectoTres() {
    }

    @Override
    public String calculcarSegumientoPlanIndicativo(
        String compania,
        int vigenciaGubernamental,
        int informe,
        String vigencia,
        int cantidadNiveles)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_VIGENCIA_GUBERNAMENTAL =>",
                                Integer.toString(vigenciaGubernamental), ", ",
                                "UN_INFORME           =>",
                                Integer.toString(informe), ", ",
                                "UN_VIGENCIA          =>'", vigencia, "', ",
                                "UN_CANTIDADNIVELES   =>",
                                Integer.toString(cantidadNiveles), ""
        };
        try {
            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_BANCOS_PROY3.FC_SEGUIMIENTO_PLAN",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String crearAuxiliarDesdeProyecto(
        String compania,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA           =>'", compania, "', ",
                                "UN_USUARIO            =>'", usuario, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_BANCOS_PROY3.FC_CREARAUXILIARES",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String eliminarProyecto(
        String compania,
        String proyecto,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PROYECTO          =>'", proyecto, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_BANCOS_PROY3.FC_ELIMINARPROYECTO",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public boolean generarPredecesorIndicativo(
        String compania,
        int vigencia,
        String usuario)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA         =>'", compania, "', ",
                                "UN_VIGENCIA         =>",
                                Integer.toString(vigencia), ", ",
                                "UN_USUARIO          =>'", usuario, "' "
        };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_BANCOS_PROY3.FC_GENERAR_PREDECESOR",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public String prepararDatos(
        String compania,
        String vigenciaInicial,
        String proyectoInicial,
        String proyectoFinal)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_VIGENCIAINICIAL   =>'", vigenciaInicial,
                                "', ", "UN_PROYECTOINICIAL   =>'",
                                proyectoInicial, "', ",
                                "UN_PROYECTOFINAL     =>'", proyectoFinal, "'"
        };
        try {
            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_BANCOS_PROY3.FC_PREPARARDATOS",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String nombrePeriodicidadBancoProy(
        int periocidad,
        int periodo)
                    throws SystemException {
        String[] parametros = { "UN_PERIOCIDAD        =>",
                                Integer.toString(periocidad), ", ",
                                "UN_PERIODO           =>",
                                Integer.toString(periodo), ""
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_BANCOS_PROY3.FC_NOMBREPERIODO_BANC",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String prepararDatosSCV17(
        String compania,
        int vigencia,
        Date fechaInicio,
        Date fechaFin,
        String fuenteInicio,
        String fuenteFin,
        boolean todasFuentes)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_VIGENCIA          =>",
                                    Integer.toString(vigencia), ", ",
                                    "UN_FECHA_INICIO      =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaInicio),
                                    "','DD/MM/YYYY'), ",
                                    "UN_FECHA_FIN         =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaFin),
                                    "','DD/MM/YYYY'), ",
                                    "UN_FUENTE_INICIO     =>",
                                    fuenteInicio != null
                                        ? "'" + fuenteInicio + "'"
                                        : null,
                                    ", ", "UN_FUENTE_FIN        =>",
                                    fuenteFin != null ? "'" + fuenteFin + "'"
                                        : null,
                                    ", ", "UN_TODAS_FUENTES     =>",
                                    todasFuentes ? "-1" : "0", ""
            };
            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_BANCOS_PROY3.FC_F_SCV_17",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (ParseException | IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String prepararDatosSCV18(
        String compania,
        int vigencia,
        Date fechaIni,
        Date fechaFin,
        String fuenteIni,
        String fuenteFin,
        int fuente)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_VIGENCIA          =>",
                                    Integer.toString(vigencia), ", ",
                                    "UN_FECHAINI          =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaIni),
                                    "','DD/MM/YYYY'), ",
                                    "UN_FECHAFIN          =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaFin),
                                    "','DD/MM/YYYY'), ",
                                    "UN_FUENTEINI         =>'", fuenteIni,
                                    "', ", "UN_FUENTEFIN         =>'",
                                    fuenteFin, "', ", "UN_FUENTE            =>",
                                    Integer.toString(fuente), ""
            };
            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_BANCOS_PROY3.FC_F_SCV_18",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (ParseException | IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

}