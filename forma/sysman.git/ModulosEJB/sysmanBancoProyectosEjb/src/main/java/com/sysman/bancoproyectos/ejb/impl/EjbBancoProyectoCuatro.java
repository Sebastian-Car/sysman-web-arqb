package com.sysman.bancoproyectos.ejb.impl;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectoCuatroLocal;
import com.sysman.bancoproyectos.ejb.EjbBancoProyectoCuatroRemote;
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
 * Session Bean implementation class BancoProyectoCuatro
 */
@Stateless
@LocalBean
public class EjbBancoProyectoCuatro implements EjbBancoProyectoCuatroRemote,
                EjbBancoProyectoCuatroLocal {
    /**
     * Default constructor.
     */
    public EjbBancoProyectoCuatro() {
    }

    @Override
    public String crearCadenaSumatoria(
        String periocidadrta,
        String periocidad,
        String strcampo)
                    throws SystemException {
        String[] parametros = { "UN_PERIOCIDADRTA     =>'", periocidadrta,
                                "', ", "UN_PERIOCIDAD        =>'", periocidad,
                                "', ", "UN_STRCAMPO          =>'", strcampo, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_BANCOS_PROY4.FC_CREARCCADENASUMATORIA",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String getCambiarPeriodicidad(
        String compania,
        String proyectoinicial,
        String periocidad,
        String periocidadaux,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PROYECTOINICIAL   =>'", proyectoinicial,
                                "', ", "UN_PERIOCIDAD        =>'", periocidad,
                                "', ", "UN_PERIOCIDADAUX     =>'",
                                periocidadaux, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_BANCOS_PROY4.FC_CAMBIARPERIODICIDAD",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String armarSumatoria(
        String periocidadrta,
        String periocidad,
        String strcampo)
                    throws SystemException {
        String[] parametros = { "UN_PERIOCIDADRTA     =>'", periocidadrta,
                                "', ", "UN_PERIOCIDAD        =>'", periocidad,
                                "', ", "UN_STRCAMPO          =>'", strcampo, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_BANCOS_PROY4.FC_CREARCCADENAVALORCERO",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String mayorizarPonderacion(
        String compania,
        int vigencia,
        boolean temp,
        String indicador,
        boolean generaReporte,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_VIGENCIA          =>",
                                Integer.toString(vigencia), ", ",
                                "UN_TEMP              =>", temp ? "-1" : "0",
                                ", ", "UN_INDICADOR         =>'", indicador,
                                "', ", "UN_GENERAREPORTE     =>",
                                (generaReporte ? "-1" : "0"), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        try {
            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_BANCOS_PROY4.FC_MAYORIZAR_POND",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String subirMGA(
        String compania,
        int ano,
        BigDecimal id,
        BigDecimal validarCod,
        String nombre,
        int modulo,
        String objetivognrl,
        String usuario)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_ANO               =>",
                                    Integer.toString(ano), ", ",
                                    "UN_ID                =>", id.toString(),
                                    ", ",
                                    "UN_VALIDAR_COD       =>",
                                    validarCod.toString(), ", ",
                                    "UN_NOMBRE            =>'", nombre, "', ",
                                    "UN_MODULO            =>",
                                    Integer.toString(modulo), ", ",
                                    "UN_OBJETIVOGNRL      =>'", objetivognrl,
                                    "', ",
                                    "UN_USUARIO           =>'", usuario, "'"
            };
            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_BANCOS_PROY4.FC_SUBIRPROYECTO_MGA",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String buscarInsertarActividad(
        String compania,
        String nombre,
        String user,
        String fecha,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_NOMBRE            =>'", nombre, "', ",
                                "UN_USER              =>'", user, "', ",
                                "UN_FECHA             =>'", fecha, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_BANCOS_PROY4.FC_BUSCARACTIVIDAD",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public void ingresarRubros(
        String compania,
        String rubro,
        int vigencia,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_RUBRO             =>'", rubro, "', ",
                                "UN_VIGENCIA          =>",
                                Integer.toString(vigencia), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_BANCOS_PROY4.PR_INGRESAR_RUBROS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String verificarNuevosRubros(
        String compania,
        String rubro,
        int vigencia,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_RUBRO             =>'", rubro, "', ",
                                "UN_VIGENCIA          =>",
                                Integer.toString(vigencia), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_BANCOS_PROY4.FC_VERIFICARNUEVOSRUBROS",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public boolean validarRubroInversion(
        String compania,
        int vigencia,
        String codrubro,
        String accion)
                    throws SystemException {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_VIGENCIA          =>",
                                Integer.toString(vigencia), ", ",
                                "UN_CODRUBRO          =>'", codrubro, "', ",
                                "UN_ACCION            =>'", accion, "'"
        };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_BANCOS_PROY4.FC_VALIDARRUBROINVERSION",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public void eliminarActividades(
        String compania,
        String proyecto,
        String actividad,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PROYECTO          =>'", proyecto, "', ",
                                "UN_ACTIVIDAD         =>'", actividad, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_BANCOS_PROY4.PR_ELIMINARACTIVIDADES",
                        SysmanFunciones.concatenar(parametros));
    }

}