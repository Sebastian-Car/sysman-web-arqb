package com.sysman.serviciospublicos.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosUnoLocal;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosUnoRemote;
import com.sysman.servpublicos.ejb.EjbServiciosPublicosUnoGeneralRemote;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class ServiciosPublicosUno
 * 
 * @author eamaya
 * @version 2.0 , Refactoring concatenacion
 */
@Stateless
@LocalBean
public class EjbServiciosPublicosUno implements EjbServiciosPublicosUnoRemote,
                EjbServiciosPublicosUnoLocal {

    @EJB
    private EjbServiciosPublicosUnoGeneralRemote ejbServiciosPublicosUnoGeneral;

    /**
     * Default constructor.
     */
    public EjbServiciosPublicosUno() {
    }

    @Override
    public int obtenerMedidoresDudosos(
        String compania,
        int ciclo)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CICLO             =>",
                                Integer.toString(ciclo), "" };

        return (int) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM1.FC_MEDIDORDUDOSO",
                        SysmanFunciones.concatenar(parametros),
                        Types.INTEGER);
    }

    @Override
    public boolean actualizarMedidores(
        String compania,
        int ciclo,
        String usuario)
                    throws SystemException {

        return ejbServiciosPublicosUnoGeneral.actualizarMedidores(compania,
                        ciclo, usuario);
    }

    @Override
    public String validarNit(
        String compania,
        String nit)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_NIT               =>'", nit, "'" };

        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM1.FC_NITVALIDAR",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public BigDecimal obtenerAutorizaciónPersuasivo(
        String compania,
        String nit)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_NIT               =>'", nit, "'" };

        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM1.FC_AUTORIZACION_PERSUASIVO",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public void cambiarRuta(
        String nueCompania,
        String antCompania,
        int nueCiclo,
        int antCiclo,
        String nueCodigoruta,
        String antCodigoruta,
        String usuario)
                    throws SystemException {

        String[] parametros = { "UN_NUE_COMPANIA      =>'", nueCompania, "', ",
                                "UN_ANT_COMPANIA      =>'", antCompania, "', ",
                                "UN_NUE_CICLO         =>",
                                Integer.toString(nueCiclo), ", ",
                                "UN_ANT_CICLO         =>",
                                Integer.toString(antCiclo), ", ",
                                "UN_NUE_CODIGORUTA    =>'", nueCodigoruta,
                                "', ", "UN_ANT_CODIGORUTA    =>'",
                                antCodigoruta, "', ",
                                "UN_USUARIO           =>'", usuario, "' " };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM1.PR_CAMBIORUTA",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String cambiarCiclo(
        String compania,
        int nueCiclo,
        String nueCodigoruta,
        int ano,
        String periodo,
        String antCompania,
        int antCiclo,
        String antCodigoruta, String usuario)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_NUE_CICLO         =>",
                                Integer.toString(nueCiclo), ", ",
                                "UN_NUE_CODIGORUTA    =>'", nueCodigoruta,
                                "', ", "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_PERIODO           =>'", periodo, "', ",
                                "UN_ANT_COMPANIA      =>'", antCompania, "', ",
                                "UN_ANT_CICLO         =>",
                                Integer.toString(antCiclo), ", ",
                                "UN_ANT_CODIGORUTA    =>'", antCodigoruta,
                                "', ", "UN_USUARIO           =>'", usuario,
                                "'" };

        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM1.FC_CAMBIOCICLO",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public void actualizarRangos(
        String compania,
        int ciclo)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CICLO             =>",
                                Integer.toString(ciclo), "" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM1.PR_ACTUALIZARRANGOS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void reversarPorPaquete(
        String compania,
        Date fecha,
        String banco,
        String paquete)
                    throws SystemException {
        try {

            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY'), ",
                                    "UN_BANCO             =>'", banco, "', ",
                                    "UN_PAQUETE           =>'", paquete, "'" };

            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_SERVICIOS_PUBLICOS_COM1.PR_REVERSARPORPAQUETE",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void reversarPorPaqueteConvenio(
        String compania,
        Date fecha,
        String banco,
        String paquete)
                    throws SystemException {
        try {

            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY'), ",
                                    "UN_BANCO             =>'", banco, "', ",
                                    "UN_PAQUETE           =>'", paquete, "'" };

            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_SERVICIOS_PUBLICOS_COM1.PR_REVERSARPORPAQUETECONV",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void eliminarRecProdPaquete(
        String compania,
        Date fecha,
        String banco,
        String paquete)
                    throws SystemException {
        try {

            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY'), ",
                                    "UN_BANCO             =>'", banco, "', ",
                                    "UN_PAQUETE           =>'", paquete, "'" };

            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_SERVICIOS_PUBLICOS_COM1.PR_ELIMINARECPRODPAQUETE",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String borrarRecPagos(
        String compania,
        Date fecha,
        String banco,
        String paquete)
                    throws SystemException {
        try {

            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY'), ",
                                    "UN_BANCO             =>'", banco, "', ",
                                    "UN_PAQUETE           =>'", paquete, "'" };

            return (String) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_SERVICIOS_PUBLICOS_COM1.FC_BORRAREGPAGOS",
                            SysmanFunciones.concatenar(parametros),
                            Types.VARCHAR);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public boolean consultarPeriodosCerrados(
        String compania,
        Date fecha,
        String banco,
        String paquete)
                    throws SystemException {
        try {

            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY'), ",
                                    "UN_BANCO             =>'", banco, "', ",
                                    "UN_PAQUETE           =>'", paquete, "'" };

            byte rta = (byte) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_SERVICIOS_PUBLICOS_COM1.FC_HAYPERIODOSCERRADOS",
                            SysmanFunciones.concatenar(parametros),
                            Types.TINYINT);
            return rta != 0;
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String realizarDevolucionDeCaja(
        String compania,
        String tipo,
        String clase,
        Date fecha,
        BigDecimal dblvalor,
        String usuario,
        String tipoanular)
                    throws SystemException {
        try {

            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_TIPO              =>'", tipo, "', ",
                                    "UN_CLASE             =>'", clase, "', ",
                                    "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY'), ",
                                    "UN_DBLVALOR          =>",
                                    dblvalor.toString(), ", ",
                                    "UN_USUARIO           =>'", usuario, "', ",
                                    "UN_TIPOANULAR        =>'", tipoanular,
                                    "'" };

            return (String) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_SERVICIOS_PUBLICOS_COM1.FC_DEVOLUCIONDECAJA",
                            SysmanFunciones.concatenar(parametros),
                            Types.VARCHAR);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String borrarPagos(
        String compania,
        Date fecha,
        String banco,
        String numeropaquete,
        int ciclo,
        String codigoruta,
        String operacion,
        long consecutivo,
        String grupo,
        BigDecimal valorpago,
        String usuario)
                    throws SystemException {
        try {

            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY'), ",
                                    "UN_BANCO             =>'", banco, "', ",
                                    "UN_NUMEROPAQUETE     =>'", numeropaquete,
                                    "', ", "UN_CICLO             =>",
                                    Integer.toString(ciclo),
                                    ", ", "UN_CODIGORUTA        =>'",
                                    codigoruta, "', ",
                                    "UN_OPERACION         =>'", operacion,
                                    "', ", "UN_CONSECUTIVO       =>",
                                    Long.toString(consecutivo), ", ",
                                    "UN_GRUPO             =>'", grupo, "', ",
                                    "UN_VALORPAGO         =>",
                                    valorpago.toString(), ", ",
                                    "UN_USUARIO           =>'", usuario, "'" };

            return (String) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_SERVICIOS_PUBLICOS_COM1.FC_BORRARPAGO",
                            SysmanFunciones.concatenar(parametros),
                            Types.VARCHAR);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void eliminarRecProd(
        String compania,
        int ciclo,
        String usuario,
        Date fecha,
        String banco,
        String paquete,
        String operacion)
                    throws SystemException {
        try {

            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_CICLO             =>",
                                    Integer.toString(ciclo), ", ",
                                    "UN_USUARIO           =>'", usuario, "', ",
                                    "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY'), ",
                                    "UN_BANCO             =>'", banco, "', ",
                                    "UN_PAQUETE           =>'", paquete, "', ",
                                    "UN_OPERACION         =>'", operacion,
                                    "'" };

            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_SERVICIOS_PUBLICOS_COM1.PR_ELIMINARECPROD",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }
}