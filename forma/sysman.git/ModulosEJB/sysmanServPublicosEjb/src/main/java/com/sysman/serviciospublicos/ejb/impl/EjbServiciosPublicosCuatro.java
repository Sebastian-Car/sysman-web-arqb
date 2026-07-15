package com.sysman.serviciospublicos.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosCuatroLocal;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosCuatroRemote;
import com.sysman.servpublicos.ejb.EjbServiciosPublicosCuatroGeneralRemote;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class ServiciosPublicosCuatro
 * 
 * @version 2.0 asana Refactorización de concatenados.
 */
@Stateless
@LocalBean
public class EjbServiciosPublicosCuatro
                implements EjbServiciosPublicosCuatroRemote,
                EjbServiciosPublicosCuatroLocal {

    @EJB
    private EjbServiciosPublicosCuatroGeneralRemote ejbServiciosPublicosCuatroGeneral;

    /**
     * Default constructor.
     */
    public EjbServiciosPublicosCuatro() {
        // No tiene sentencias
    }

    @Override
    public boolean autorizarFacturaPagada(
        String compania,
        String nit)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
                               "UN_NIT               =>'", nit, "'"
        };
        byte rta = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM4.FC_AUTORIZACION_FACTURAPAGADA",
                        SysmanFunciones.concatenar(parametro),
                        Types.TINYINT);
        return rta != 0;
    }

    @Override
    public boolean estarBloqueado(
        String compania,
        int ciclo)
                    throws SystemException {

        return ejbServiciosPublicosCuatroGeneral.estarBloqueado(compania,
                        ciclo);
    }

    @Override
    public boolean autorizarConvenios(
        String compania,
        String nit)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
                               "UN_NIT               =>'", nit, "'"
        };
        byte rta = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM4.FC_AUTORIZACION_CONVENIOS",
                        SysmanFunciones.concatenar(parametro),
                        Types.TINYINT);
        return rta != 0;
    }

    @Override
    public boolean permitirAccion(
        String compania,
        String accion,
        String usuario,
        String parametro)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ACCION            =>'", accion, "', ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_PARAMETRO         =>'", parametro, "'"
        };
        byte rta = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM4.FC_PERMISO_ACCION",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return rta != 0;
    }

    @Override
    public String obtenerComentariosPeriodo(
        String compania,
        String comentarios,
        int ano,
        String periodo)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
                               "UN_COMENTARIOS       =>'", comentarios, "', ",
                               "UN_ANO               =>", Integer.toString(ano),
                               ", ",
                               "UN_PERIODO           =>'", periodo, "'",
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM4.FC_OBTENER_COMENTARIOS_PERIODO",
                        SysmanFunciones.concatenar(parametro),
                        Types.VARCHAR);
    }

    @Override
    public String armarConsultaPlanoExp(
        String compania,
        int ciclo,
        String codigoinicial,
        String codigofinal)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
                               "UN_CICLO             =>",
                               Integer.toString(ciclo), ", ",
                               "UN_CODIGOINICIAL     =>'", codigoinicial, "', ",
                               "UN_CODIGOFINAL       =>'", codigofinal, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM4.FC_ARMACONSULTAPLANOSEXP",
                        SysmanFunciones.concatenar(parametro),
                        Types.VARCHAR);
    }

    @Override
    public String enviarPlanosAsobancaria(
        String compania,
        int ciclo,
        String codigoinicial,
        String codigofinal,
        String usuario,
        boolean checkath)
                    throws SystemException {
        try {
            String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
                                   "UN_CICLO             =>",
                                   Integer.toString(ciclo), ", ",
                                   "UN_CODIGOINICIAL     =>'", codigoinicial,
                                   "', ",
                                   "UN_CODIGOFINAL       =>'", codigofinal,
                                   "', ",
                                   "UN_USUARIO           =>'", usuario, "', ",
                                   "UN_CHECKATH          =>",
                                   (checkath ? "-1" : "0"), ""
            };
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_SERVICIOS_PUBLICOS_COM4.FC_PLANOS_ASOBANCARIA",
                                            SysmanFunciones.concatenar(
                                                            parametro),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String enviarFimmCabeza(
        String compania,
        int ciclo,
        int strdiasafacturar,
        int diasdesde,
        int diashasta,
        String codigoinicial,
        String codigofinal,
        Date fechaemision,
        String usuario)
                    throws SystemException {
        try {
            String[] parametro = { "UN_COMPANIA          =>'", compania,
                                   "', ",
                                   "UN_CICLO             =>",
                                   Integer.toString(ciclo),
                                   ", ",
                                   "UN_STRDIASAFACTURAR  =>",
                                   Integer.toString(strdiasafacturar),
                                   ", ",
                                   "UN_DIASDESDE         =>",
                                   Integer.toString(diasdesde),
                                   ", ",
                                   "UN_DIASHASTA         =>",
                                   Integer.toString(diashasta),
                                   ", ",
                                   "UN_CODIGOINICIAL     =>'", codigoinicial,
                                   "', ",
                                   "UN_CODIGOFINAL       =>'", codigofinal,
                                   "', ",
                                   "UN_FECHAEMISION      =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(
                                                   fechaemision),
                                   "','DD/MM/YYYY'), ",
                                   "UN_USUARIO           =>'", usuario, "'"
            };
            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_SERVICIOS_PUBLICOS_COM4.FC_FIMMCABEZA",
                            SysmanFunciones.concatenar(parametro),
                            Types.CLOB));
        }
        catch (ParseException | IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String enviarFimmCuerpo(
        String compania,
        int ciclo,
        int diasprimervenc,
        String codigoinicial,
        String codigofinal,
        Date fechaemision,
        Date fechavencimiento,
        boolean indemisionfija,
        boolean indvencimientofijo,
        String usuario)
                    throws SystemException {
        try {
            String[] parametro = { "UN_COMPANIA          =>'",
                                   compania, "', ",
                                   "UN_CICLO             =>",
                                   Integer.toString(ciclo), ", ",
                                   "UN_DIASPRIMERVENC    =>",
                                   Integer.toString(diasprimervenc), ", ",
                                   "UN_CODIGOINICIAL     =>'",
                                   codigoinicial, "', ",
                                   "UN_CODIGOFINAL       =>'",
                                   codigofinal, "', ",
                                   "UN_FECHAEMISION      =>TO_DATE('",
                                   SysmanFunciones
                                                   .convertirAFechaCadena(
                                                                   fechaemision),
                                   "','DD/MM/YYYY'), ",
                                   "UN_FECHAVENCIMIENTO  =>TO_DATE('",
                                   SysmanFunciones
                                                   .convertirAFechaCadena(
                                                                   fechavencimiento),
                                   "','DD/MM/YYYY'), ",
                                   "UN_INDEMISIONFIJA    =>",
                                   (indemisionfija ? "-1" : "0"), ", ",
                                   "UN_INDVENCIMIENTOFIJO =>",
                                   (indvencimientofijo ? "-1" : "0"),
                                   ", ", "UN_USUARIO           =>'",
                                   usuario, "'"
            };
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_SERVICIOS_PUBLICOS_COM4.FC_FIMMCUERPO",
                                            SysmanFunciones.concatenar(
                                                            parametro),
                                            Types.CLOB));
        }
        catch (ParseException | IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String obtenerNombrePeriodo(
        String compania,
        int ano,
        String periodo)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
                               "UN_ANO               =>", Integer.toString(ano),
                               ", ",
                               "UN_PERIODO           =>'", periodo, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM4.FC_NOMBREPER",
                        SysmanFunciones.concatenar(parametro),
                        Types.VARCHAR);
    }

    @Override
    public String enviarFimmSoloLecturaCabeza(
        String compania,
        int ciclo,
        int strdiasafacturar,
        int diasdesde,
        int diashasta,
        String codigoinicial,
        String codigofinal)
                    throws SystemException {
        try {
            String[] parametro = { "UN_COMPANIA          =>'", compania,
                                   "', ",
                                   "UN_CICLO             =>",
                                   Integer.toString(ciclo),
                                   ", ",
                                   "UN_STRDIASAFACTURAR  =>",
                                   Integer.toString(strdiasafacturar),
                                   ", ",
                                   "UN_DIASDESDE         =>",
                                   Integer.toString(diasdesde),
                                   ", ",
                                   "UN_DIASHASTA         =>",
                                   Integer.toString(diashasta),
                                   ", ",
                                   "UN_CODIGOINICIAL     =>'", codigoinicial,
                                   "', ",
                                   "UN_CODIGOFINAL       =>'", codigofinal,
                                   "'"
            };
            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_SERVICIOS_PUBLICOS_COM4.FC_FIMMSOLOLECTURACABEZA",
                            SysmanFunciones.concatenar(parametro),
                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String enviarFimmSoloLecturaCuerpo(
        String compania,
        int ciclo,
        String codigoinicial,
        String codigofinal)
                    throws SystemException {
        try {
            String[] parametro = { "UN_COMPANIA          =>'",
                                   compania, "', ",
                                   "UN_CICLO             =>",
                                   Integer.toString(ciclo), ", ",
                                   "UN_CODIGOINICIAL     =>'",
                                   codigoinicial,
                                   "', ",
                                   "UN_CODIGOFINAL       =>'",
                                   codigofinal,
                                   "'"
            };
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_SERVICIOS_PUBLICOS_COM4.FC_FIMMSOLOLECTURACUERPO",
                                            SysmanFunciones.concatenar(
                                                            parametro),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void registrarProceso(
        String compania,
        String proceso,
        String descripcion,
        String parametros,
        String resultados,
        String estado,
        String usuario,
        Date fecha)
                    throws SystemException {
        try {
            String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
                                   "UN_PROCESO           =>'", proceso, "', ",
                                   "UN_DESCRIPCION       =>'", descripcion,
                                   "', ",
                                   "UN_PARAMETROS        =>'", parametros,
                                   "', ",
                                   "UN_RESULTADOS        =>'", resultados,
                                   "', ",
                                   "UN_ESTADO            =>'", estado, "', ",
                                   "UN_USUARIO           =>'", usuario, "', ",
                                   "UN_FECHA             =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(fecha),
                                   "','DD/MM/YYYY')"
            };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_SERVICIOS_PUBLICOS_COM4.PR_REGISTRARPROCESO",
                            SysmanFunciones.concatenar(parametro));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }
}