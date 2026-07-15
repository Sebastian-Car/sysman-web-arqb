package com.sysman.serviciospublicos.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosCincoLocal;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosCincoRemote;
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
 * Session Bean implementation class ServiciosPublicosCinco
 * 
 * @author ybecerra
 * @version 2, 10/06/2017, Implementacion metodo concatenar de la
 * clase SysmanFunciones para el envio de parametros a los diferentes
 * procedimientos y funciones
 */
@Stateless
@LocalBean
public class EjbServiciosPublicosCinco
                implements EjbServiciosPublicosCincoRemote,
                EjbServiciosPublicosCincoLocal {
    /**
     * Default constructor.
     */
    public EjbServiciosPublicosCinco() {
    }

    @Override
    public int obtenerMicromedicion(
        String compania,
        int ciclo,
        String codigoruta,
        int anio,
        String periodo,
        String momento,
        String usuario)
                    throws SystemException {

        String[] parametro = { "UN_COMPANIA   =>'", compania, "', ",
                               "UN_CICLO         =>",
                               Integer.toString(ciclo), ", ",
                               "UN_CODIGORUTA    =>'", codigoruta, "', ",
                               "UN_ANIO          =>",
                               Integer.toString(anio), ", ",
                               "UN_PERIODO       =>'", periodo, "',",
                               "UN_MOMENTO       =>'", momento, "',",
                               "UN_USUARIO       =>'", usuario, "'" };
        return (int) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM5.FC_MICROMEDICIONSITIO",
                        SysmanFunciones.concatenar(parametro),
                        Types.INTEGER);
    }

    @Override
    public String armarPlanoMultiusuarios(
        String compania,
        String codigoruta,
        int anio,
        String periodo,
        int numeroMultiusuarios)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA           =>'", compania, "', ",
                               "UN_CODIGORUTA         =>'", codigoruta, "', ",
                               "UN_ANIO               =>",
                               Integer.toString(anio), ", ",
                               "UN_PERIODO            =>'", periodo, "', ",
                               "UN_NUMERO_MULTIUSUAR  =>",
                               Integer.toString(numeroMultiusuarios), ""

        };

        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM5.FC_PLANOMULTIUSUARIOS",
                        SysmanFunciones.concatenar(parametro),
                        Types.VARCHAR);
    }

    @Override
    public String armarPlanoMultiusuarios720(
        String compania,
        String codigoruta,
        int anio,
        String periodo)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA           =>'", compania, "', ",
                               "UN_CODIGORUTA         =>'", codigoruta, "', ",
                               "UN_ANIO               =>",
                               Integer.toString(anio), ", ",
                               "UN_PERIODO            =>'", periodo, "'"

        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM5.FC_PLANOMULTIUSUARIOS720",
                        SysmanFunciones.concatenar(parametro),
                        Types.VARCHAR);
    }

    @Override
    public int obtenerNumeroMultiusuarios(
        String compania,
        String codigoruta)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA       =>'", compania, "', ",
                               "UN_CODIGORUTA     =>'", codigoruta, "'"

        };
        return (int) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM5.FC_NUMERO_MULTIUSUARIOS",
                        SysmanFunciones.concatenar(parametro),
                        Types.INTEGER);
    }

    @Override
    public String armarParametrosPlanoFacSitio(
        String compania,
        String codigoinicial,
        String codigofinal,
        int ciclo,
        String observaciones,
        String bancodepago)
                    throws SystemException {
        try {
            String[] parametro = { "UN_COMPANIA =>'", compania, "', ",
                                   "UN_CODIGOINICIAL =>'", codigoinicial, "', ",
                                   "UN_CODIGOFINAL    =>'", codigofinal, "', ",
                                   "UN_CICLO  =>", Integer.toString(ciclo),
                                   ", ",
                                   "UN_OBSERVACIONES     =>'", observaciones,
                                   "', ", "UN_BANCODEPAGO       =>'",
                                   bancodepago, "'"

            };
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_SERVICIOS_PUBLICOS_COM5.FC_PLANOPARAMETROS_FACSITIO",
                                            SysmanFunciones.concatenar(
                                                            parametro),
                                            Types.CLOB));
        }
        catch (IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String armarDetallesPlanoFacSitio(
        String compania,
        String codigoinicial,
        String codigofinal,
        int ciclo,
        Date fechavencimiento,
        String aforador,
        int critica,
        String publicidad,
        String condicionadicional,
        int limiteinferior,
        int limitesuperior,
        String usuario)
                    throws SystemException {
        try {
            String[] parametro = { "UN_COMPANIA =>'", compania, "', ",
                                   "UN_CODIGOINICIAL     =>'", codigoinicial,
                                   "', ",
                                   "UN_CODIGOFINAL       =>'", codigofinal,
                                   "', ",
                                   "UN_CICLO             =>",
                                   Integer.toString(ciclo), ", ",
                                   "UN_FECHAVENCIMIENTO  =>TO_DATE('",
                                   SysmanFunciones
                                                   .convertirAFechaCadena(
                                                                   fechavencimiento),
                                   "','DD/MM/YYYY'), ",
                                   "UN_AFORADOR          =>'", aforador, "', ",
                                   "UN_CRITICA           =>",
                                   Integer.toString(critica), ", ",
                                   "UN_PUBLICIDAD        =>'", publicidad,
                                   "', ",
                                   "UN_CONDICIONADICIONAL =>'",
                                   condicionadicional, "', ",
                                   "UN_LIMITEINFERIOR    =>",
                                   Integer.toString(limiteinferior),
                                   ", ", "UN_LIMITESUPERIOR    =>",
                                   Integer.toString(limitesuperior), ", ",
                                   "UN_USUARIO  =>'", usuario, "'"

            };
            return Acciones.clobToStringSalto(
                            (Clob) AccionesImp.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_SERVICIOS_PUBLICOS_COM5.FC_PLANOFACSITIO",
                                            SysmanFunciones.concatenar(
                                                            parametro),
                                            Types.CLOB));
        }
        catch (ParseException | IOException | SQLException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public boolean calcularProgresividad(
        String compania,
        int anio,
        String periodo,
        boolean solometa,
        String uso,
        String estrato)
                    throws SystemException {

        String[] parametro = { "UN_COMPANIA  =>'", compania, "', ",
                               "UN_ANIO      =>", Integer.toString(anio), ", ",
                               "UN_PERIODO   =>'", periodo, "', ",
                               "UN_SOLOMETA   =>", solometa ? "-1" : "0",
                               ", ", "UN_USO  =>'", uso, "', ",
                               "UN_ESTRATO    =>'", estrato, "'"

        };
        byte rta = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM5.FC_PROGRESIVIDAD",
                        SysmanFunciones.concatenar(parametro),
                        Types.TINYINT);
        return rta != 0;
    }

    @Override
    public boolean calcularTarifas720(
        String compania,
        int anio,
        String periodo)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA    =>'", compania, "', ",
                               "UN_ANIO              =>",
                               Integer.toString(anio), ", ",
                               "UN_PERIODO   =>'", periodo, "'"

        };
        byte rta = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM5.FC_CALCULOTARIFAS720",
                        SysmanFunciones.concatenar(parametro),
                        Types.TINYINT);
        return rta != 0;
    }

    @Override
    public void actualizacionDesvioSignificativo(
        String compania,
        int ciclo,
        String codigoinicial,
        String codigofinal,
        String usuario,
        int anio,
        String periodo, String nitCompania)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA      =>'", compania, "', ",
                               "UN_CICLO             =>",
                               Integer.toString(ciclo), ", ",
                               "UN_CODIGOINICIAL     =>'", codigoinicial, "', ",
                               "UN_CODIGOFINAL       =>'", codigofinal, "', ",
                               "UN_USUARIO           =>'", usuario, "', ",
                               "UN_ANIO              =>",
                               Integer.toString(anio), ", ",
                               "UN_PERIODO           =>'", periodo, "', ",
                               "UN_NITCOMPANIA       =>'", nitCompania, "' "

        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM5.PR_ACTUALIZARDESVIO",
                        SysmanFunciones.concatenar(parametro));
    }

    @Override
    public void incrementarTarifas(
        String compania,
        int anosig,
        String periodosig,
        int ano,
        String periodo,
        boolean reescribir,
        String usuario)
                    throws SystemException {
        String[] parametro = { "UN_COMPANIA      =>'", compania, "', ",
                               "UN_ANOSIG            =>",
                               Integer.toString(anosig), ", ",
                               "UN_PERIODOSIG        =>'", periodosig, "', ",
                               "UN_ANO               =>", Integer.toString(ano),
                               ", ", "UN_PERIODO           =>'", periodo, "', ",
                               "UN_REESCRIBIR        =>",
                               reescribir ? "-1" : "0", ", ",
                               "UN_USUARIO           =>'", usuario, "'"

        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM5.PR_INCREMENTARTARIFAS",
                        SysmanFunciones.concatenar(parametro));
    }
}