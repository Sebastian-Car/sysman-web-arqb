package com.sysman.serviciospublicos.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosDosLocal;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosDosRemote;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class ServiciosPublicosDos
 * 
 * @modified jguerrero
 * @version 2. 07/06/2017
 */
@Stateless
@LocalBean
public class EjbServiciosPublicosDos implements EjbServiciosPublicosDosRemote,
                EjbServiciosPublicosDosLocal {
    /**
     * Default constructor.
     */
    public EjbServiciosPublicosDos() {
    }

    @Override
    public String registrarPersuasivo(
        String compania,
        String nit,
        String consecutivo,
        String ciclo,
        String codigoinicial,
        String codigofinal,
        String peratrasoini,
        String peratrasofin,
        BigDecimal dedudaini,
        BigDecimal deudafin,
        String usuario)
                    throws SystemException {

        String[] parametos = { "UN_COMPANIA        =>'", compania, "', ",
                               "UN_NIT             =>'", nit, "', ",
                               "UN_CONSECUTIVO     =>'", consecutivo, "', ",
                               "UN_CICLO             =>'", ciclo, "', ",
                               "UN_CODIGOINICIAL   =>'", codigoinicial, "', ",
                               "UN_CODIGOFINAL     =>'", codigofinal, "', ",
                               "UN_PERATRASOINI    =>'", peratrasoini, "', ",
                               "UN_PERATRASOFIN      =>'", peratrasofin, "', ",
                               "UN_DEDUDAINI         =>", dedudaini.toString(),
                               ", ", "UN_DEUDAFIN        =>",
                               deudafin.toString(), ", ",
                               "UN_USUARIO         =>'", usuario, "' " };

        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM2.FC_REGISTRARPERSUASIVO",
                        SysmanFunciones.concatenar(parametos),
                        Types.VARCHAR);
    }

    @Override
    public String registrarCoactivo(
        String compania,
        String consecutivo,
        int ciclo,
        String codigoinicial,
        String codigofinal,
        String peratrasoini,
        String peratrasofin,
        BigDecimal deudaini,
        BigDecimal deudafin, String usuario)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA =>'", compania, "', ",
                                "UN_CONSECUTIVO       =>'", consecutivo, "', ",
                                "UN_CICLO =>",
                                Integer.toString(ciclo), ", ",
                                "UN_CODIGOINICIAL     =>'", codigoinicial,
                                "', ", "UN_CODIGOFINAL       =>'", codigofinal,
                                "', ", "UN_PERATRASOINI      =>'", peratrasoini,
                                "', ", "UN_PERATRASOFIN      =>'", peratrasofin,
                                "', ", "UN_DEUDAINI          =>",
                                deudaini.toString(), ", ",
                                "UN_DEUDAFIN          =>", deudafin.toString(),
                                ", ", "UN_USUARIO =>'", usuario,
                                "' " };

        return (String) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM2.FC_REGISTRARCOACTIVO",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public boolean registrarActa(
        String compania,
        String consecutivo,
        int ciclo,
        String codigoinicial,
        String codigofinal,
        int peratrasoini,
        int peratrasofin,
        BigDecimal deudaini,
        BigDecimal deudafin,
        String estado,
        boolean conabonos,
        int abonos,
        int chapetas,
        int pqr,
        String fechaemision,
        String superint,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA    =>'", compania, "', ",
                                "UN_CONSECUTIVO  =>'", consecutivo, "', ",
                                "UN_CICLO       =>",
                                Integer.toString(ciclo), ", ",
                                "UN_CODIGOINICIAL     =>'", codigoinicial,
                                "', ", "UN_CODIGOFINAL       =>'", codigofinal,
                                "', ", "UN_PERATRASOINI      =>",
                                Integer.toString(peratrasoini), ", ",
                                "UN_PERATRASOFIN      =>",
                                Integer.toString(peratrasofin), ", ",
                                "UN_DEUDAINI          =>", deudaini.toString(),
                                ", ", "UN_DEUDAFIN          =>",
                                deudafin.toString(), ", ",
                                "UN_ESTADO            =>'", estado, "', ",
                                "UN_CONABONOS         =>",
                                conabonos ? "-1" : "0", ", ",
                                "UN_ABONOS            =>",
                                Integer.toString(abonos), ", ",
                                "UN_CHAPETAS          =>",
                                Integer.toString(chapetas), ", ",
                                "UN_PQR               =>",
                                Integer.toString(pqr), ", ",
                                "UN_FECHAEMISION      =>'", fechaemision, "', ",
                                "UN_SUPERINT          =>'", superint, "', ",
                                "UN_USUARIO   =>'", usuario, "'" };

        byte rta = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM2.FC_REGISTRARACTA",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return rta != 0;
    }

    @Override
    public String asignarNombrePeriodoM(
        String compania,
        String periodo)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA   =>'", compania, "', ",
                                "UN_PERIODO    =>'", periodo, "'", };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM2.FC_NOMBREPERIODOM",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public boolean registrarActaOperacion(
        String compania,
        String tipoop,
        int ciclo,
        String codigoruta,
        String interno,
        int ano,
        String periodo,
        Date fechaejecucion,
        Date horaejecucion,
        String aforador,
        String descripcion,
        String peratraso,
        String usuario,
        String claseoperacion)
                    throws SystemException {

        try {
            String[] parametros = { "UN_COMPANIA      =>'", compania, "', ",
                                    "UN_TIPOOP        =>'", tipoop, "', ",
                                    "UN_CICLO         =>",
                                    Integer.toString(ciclo), ", ",
                                    "UN_CODIGORUTA    =>'", codigoruta,
                                    "', ", "UN_INTERNO           =>'", interno,
                                    "', ", "UN_ANO    =>",
                                    Integer.toString(ano), ", ",
                                    "UN_PERIODO       =>'", periodo, "', ",
                                    "UN_FECHAEJECUCION    =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaejecucion),
                                    "','DD/MM/YYYY'),",
                                    "UN_HORAEJECUCION     =>",
                                    SysmanFunciones.formatearFecha(
                                                    horaejecucion),
                                    ",",
                                    "UN_AFORADOR          =>'", aforador,
                                    "', ", "UN_DESCRIPCION       =>'",
                                    descripcion, "', ",
                                    "UN_PERATRASO         =>'", peratraso,
                                    "', ", "UN_USUARIO  =>'", usuario,
                                    "', ", "UN_CLASEOPERACION    =>'",
                                    claseoperacion, "'" };
            byte rta = (byte) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_SERVICIOS_PUBLICOS_COM2.FC_REGISTRARACTAOPERACION",
                            SysmanFunciones.concatenar(parametros),
                            Types.TINYINT);
            return rta != 0;
        }
        catch (ParseException e) {
            throw new SystemException(e.getMessage(), e);
        }

    }

    @Override
    public String armarDocumentoSolicitud(
        String compania,
        String clasesolicitud,
        BigInteger solicitud)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA =>'", compania, "', ",
                                "UN_CLASESOLICITUD    =>'", clasesolicitud,
                                "', ", "UN_SOLICITUD         =>",
                                solicitud.toString(),
                                "" };

        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM2.FC_ARMADOCSOLICITUD",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String asignarNombrePeriodoDe(
        String compania,
        int ano,
        String periodo,
        String frecuencia)
                    throws SystemException {
        StringBuilder frec = new StringBuilder();

        if (frecuencia != null) {
            frec.append("'");
            frec.append(frecuencia);
            frec.append("'");
        }
        else {
            frec.append("null");
        }
        String[] parametos = { "UN_COMPANIA=>'", compania, "', ",
                               "UN_ANO=>", Integer.toString(ano),
                               ", ",
                               "UN_PERIODO=>'", periodo, "', ",
                               "UN_FRECUENCIA        =>", frec.toString()
        };

        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM2.FC_NOMBREPERIODODE",
                        SysmanFunciones.concatenar(parametos),
                        Types.VARCHAR);
    }

    @Override
    public String asignarNombrePeriodoSigDe(
        String compania,
        int ano,
        String periodo,
        String frecuencia)
                    throws SystemException {
        StringBuilder frec = new StringBuilder();
        if (frecuencia != null) {
            frec.append("'");
            frec.append(frecuencia);
            frec.append("'");
        }
        else {
            frec.append("null");
        }

        String[] parametros = { "UN_COMPANIA     =>'", compania, "', ",
                                "UN_ANO          =>",
                                Integer.toString(ano), ", ",
                                "UN_PERIODO      =>'", periodo, "', ",
                                "UN_FRECUENCIA        =>", frec.toString()
        };

        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM2.FC_NOMBREPERIODOSIGDE",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public boolean autorizarDesviacion(
        String compania,
        String nit)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA       =>'", compania, "', ",
                                "UN_NIT            =>'", nit, "'"
        };

        byte rta = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM2.FC_AUTORIZACION_DESVIACION",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return rta != 0;
    }

    @Override
    public boolean autorizarFraudes(
        String compania,
        String nit)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA     =>'", compania, "', ",
                                "UN_NIT               =>'", nit, "'" };
        byte rta = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM2.FC_AUTORIZACION_FRAUDES",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return rta != 0;
    }

    @Override
    public String asignarNombrePeriodoCorto(
        String compania,
        int ano,
        String periodo,
        String frecuencia)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA   =>'", compania, "', ",
                                "UN_ANO         =>",
                                Integer.toString(ano), ", ",
                                "UN_PERIODO   =>'", periodo, "', ",
                                "UN_FRECUENCIA       =>'", frecuencia, "'" };

        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM2.FC_NOMBREPERIODOCORTO",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public int generarAnoSiguiente(
        String compania,
        int ano,
        String periodo,
        String frecuencia)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA               =>'", compania,
                                "', ",
                                "UN_ANO                   =>",
                                Integer.toString(ano), ", ",
                                "UN_PERIODO               =>'", periodo, "', ",
                                "UN_FRECUENCIA        =>'", frecuencia, "'" };
        return (int) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM2.FC_ANOSIGUIENTE",
                        SysmanFunciones.concatenar(parametros),
                        Types.INTEGER);
    }

    @Override
    public String generarPeriodoSiguiente(
        String compania,
        int ano,
        String periodo,
        String frecuencia)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA           =>'", compania, "', ",
                                "UN_ANO                 =>",
                                Integer.toString(ano), ", ",
                                "UN_PERIODO             =>'", periodo, "', ",
                                "UN_FRECUENCIA        =>'", frecuencia, "'" };

        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM2.FC_PERIODOSIGUIENTE",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public void actualizarAbonoPrioridad(
        String compania,
        int ciclo,
        String codigoruta,
        String periodo,
        long consecutivo,
        int ano,
        BigDecimal dblvalor,
        BigDecimal dbldeuda)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA                  =>'", compania,
                                "', ",
                                "UN_CICLO               =>",
                                Integer.toString(ciclo), ", ",
                                "UN_CODIGORUTA           =>'", codigoruta,
                                "', ",
                                "UN_PERIODO              =>'", periodo, "', ",
                                "UN_CONSECUTIVO          =>",
                                Long.toString(consecutivo), ", ",
                                "UN_ANO                   =>",
                                Integer.toString(ano), ", ",
                                "UN_DBLVALOR          =>", dblvalor.toString(),
                                ", ",
                                "UN_DBLDEUDA          =>",
                                dbldeuda.toString() };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM2.PR_ABONOPRIORIDAD",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public BigDecimal consultarValorPagoConvenios(
        String compania,
        int ciclo,
        String codigoruta,
        int ano,
        String periodo,
        String convenio)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA           =>'", compania, "', ",
                                "UN_CICLO              =>",
                                Integer.toString(ciclo), ", ",
                                "UN_CODIGORUTA         =>'", codigoruta, "', ",
                                "UN_ANO                =>",
                                Integer.toString(ano), ", ",
                                "UN_PERIODO            =>'", periodo, "', ",
                                "UN_CONVENIO          =>'", convenio, "'" };

        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM2.FC_VALORPAGOCONVENIOS",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public BigDecimal consultarValorPagoTercerizado(
        String compania,
        int ciclo,
        String codigoruta,
        int ano,
        String periodo,
        String tercerizado)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA            =>'", compania, "', ",
                                "UN_CICLO               =>",
                                Integer.toString(ciclo), ", ",
                                "UN_CODIGORUTA          =>'", codigoruta, "', ",
                                "UN_ANO                =>",
                                Integer.toString(ano), ", ",
                                "UN_PERIODO            =>'", periodo, "', ",
                                "UN_TERCERIZADO       =>'", tercerizado, "'" };

        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM2.FC_VALORPAGOTERCERIZADO",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public void charlesPesoAB(
        String compania,
        int ciclo,
        String codigoruta,
        int ano,
        String periodo,
        long consecutivo)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA            =>'", compania, "', ",
                                "UN_CICLO               =>",
                                Integer.toString(ciclo), ", ",
                                "UN_CODIGORUTA          =>'", codigoruta, "', ",
                                "UN_ANO                 =>",
                                Integer.toString(ano), ", ",
                                "UN_PERIODO             =>'", periodo, "', ",
                                "UN_CONSECUTIVO       =>",
                                Long.toString(consecutivo) };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM2.PR_CHARLESPESOAB",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void discriminarAbonos(
        String compania,
        String codigoruta)
                    throws SystemException {

        String[] parametro = { "UN_COMPANIA              =>'", compania, "', ",
                               "UN_CODIGORUTA            =>'", codigoruta,
                               "'" };

        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM2.PR_DISCRIMINARABONOS",
                        SysmanFunciones.concatenar(parametro));
    }

    @Override
    public void actualizarAbonoRecaudos(
        String compania,
        Date fechaInicial,
        Date fechaFinal,
        String strbanco)
                    throws SystemException {
        try {

            String[] parametros = { "UN_COMPANIA              =>'", compania,
                                    "', ",
                                    "UN_FECHA_INICIAL     =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaInicial),
                                    "','DD/MM/YYYY'),",
                                    "UN_FECHA_FINAL       =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaFinal),
                                    "','DD/MM/YYYY'), ",
                                    "UN_STRBANCO          =>'", strbanco, "'" };

            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_SERVICIOS_PUBLICOS_COM2.PR_ABORECAUDOS",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void pasarAbonosRecaudos(
        String compania,
        Date strfecha,
        String strbanco,
        String usuario)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA =>'", compania, "', ",
                                    "UN_STRFECHA          =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    strfecha),
                                    "','DD/MM/YYYY'), ",
                                    "UN_STRBANCO          =>'", strbanco, "', ",
                                    "UN_USUARIO  =>'", usuario, "'" };

            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_SERVICIOS_PUBLICOS_COM2.PR_PASAABONOSRECAUDOS",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String registrarAbono(
        String compania,
        String codigoruta,
        String periodo,
        Date fecha,
        String banco,
        String usuario,
        int ano,
        int ciclo,
        long consecutivo,
        BigDecimal vlrabono)
                    throws SystemException {
        try {

            String[] parametros = { "UN_COMPANIA   =>'", compania, "', ",
                                    "UN_CODIGORUTA =>'", codigoruta, "', ",
                                    "UN_PERIODO    =>'", periodo, "', ",
                                    "UN_FECHA      =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY'), ",
                                    "UN_BANCO    =>'", banco, "', ",
                                    "UN_USUARIO           =>'", usuario, "', ",
                                    "UN_ANO      =>", Integer.toString(ano),
                                    ", ",
                                    "UN_CICLO    =>", Integer.toString(ciclo),
                                    ", ",
                                    "UN_CONSECUTIVO       =>",
                                    Long.toString(consecutivo), ", ",
                                    "UN_VLRABONO          =>",
                                    vlrabono.toString()
            };

            return (String) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_SERVICIOS_PUBLICOS_COM2.FC_REGISTRARABONO",
                            SysmanFunciones.concatenar(parametros),
                            Types.VARCHAR);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public boolean actualizarPagoTercerizados(
        String compania,
        int ciclo,
        String codigoruta,
        int ano,
        String periodo,
        Date fecha,
        String banco,
        String paquete,
        boolean reversa,
        boolean pagodoble,
        String tercer)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA   =>'", compania, "', ",
                                    "UN_CICLO      =>",
                                    Integer.toString(ciclo), ", ",
                                    "UN_CODIGORUTA        =>'", codigoruta,
                                    "', ", "UN_ANO               =>",
                                    Integer.toString(ano), ", ",
                                    "UN_PERIODO           =>'", periodo, "', ",
                                    "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY'), ",
                                    "UN_BANCO             =>'", banco, "', ",
                                    "UN_PAQUETE           =>'", paquete, "', ",
                                    "UN_REVERSA           =>",
                                    reversa ? "-1" : "0", ", ",
                                    "UN_PAGODOBLE         =>",
                                    pagodoble ? "-1" : "0", ", ",
                                    "UN_TERCER            =>'", tercer, "'" };

            byte rta = (byte) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_SERVICIOS_PUBLICOS_COM2.FC_ACTUALIZAPAGOTERCERIZADOS",
                            SysmanFunciones.concatenar(parametros),
                            Types.TINYINT);
            return rta != 0;
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public boolean actualizarPagoConvenios(
        String compania,
        int ciclo,
        String codigoruta,
        int ano,
        String periodo,
        Date fecha,
        String banco,
        String paquete,
        boolean reversa,
        boolean pagodoble,
        String convenio)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_CICLO             =>",
                                    Integer.toString(ciclo), ", ",
                                    "UN_CODIGORUTA        =>'", codigoruta,
                                    "', ", "UN_ANO               =>",
                                    Integer.toString(ano), ", ",
                                    "UN_PERIODO           =>'", periodo, "', ",
                                    "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY'), ",
                                    "UN_BANCO             =>'", banco, "', ",
                                    "UN_PAQUETE           =>'", paquete, "', ",
                                    "UN_REVERSA           =>",
                                    reversa ? "-1" : "0", ", ",
                                    "UN_PAGODOBLE         =>",
                                    pagodoble ? "-1" : "0", ", ",
                                    "UN_CONVENIO          =>'", convenio, "'" };

            byte rta = (byte) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_SERVICIOS_PUBLICOS_COM2.FC_ACTUALIZARPAGOCONVENIOS",
                            SysmanFunciones.concatenar(parametros),
                            Types.TINYINT);
            return rta != 0;
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void calcularProducRec(
        String compania,
        int ciclo,
        int ano,
        String periodo,
        Date fechaini,
        Date fechafin,
        String usuarioini,
        String usuariofin,
        String aplica,
        int cnproductividad)
                    throws SystemException {
        try {

            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_CICLO             =>",
                                    Integer.toString(ciclo), ", ",
                                    "UN_ANO               =>",
                                    Integer.toString(ano), ", ",
                                    "UN_PERIODO           =>'", periodo, "', ",
                                    "UN_FECHAINI          =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaini),
                                    "','DD/MM/YYYY'), ",
                                    "UN_FECHAFIN          =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechafin),
                                    "','DD/MM/YYYY'), ",
                                    "UN_USUARIOINI        =>'", usuarioini,
                                    "', ", "UN_USUARIOFIN        =>'",
                                    usuariofin, "', ",
                                    "UN_APLICA            =>'", aplica, "', ",
                                    "UN_CNPRODUCTIVIDAD   =>",
                                    Integer.toString(cnproductividad) };

            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_SERVICIOS_PUBLICOS_COM2.PR_CALCULAPRODUCREC",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String eliminarFinanciable(
        String compania,
        int ciclo,
        int ano,
        String periodo,
        String codigoruta,
        int concepto,
        String usuario,
        BigDecimal montofinanciar,
        BigDecimal totalmonto,
        BigDecimal saldofinanciable,
        BigDecimal numerocuotas,
        int nrocuota,
        BigDecimal valorcuota,
        String bancoperproceso,
        String codigointerno)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CICLO             =>",
                                Integer.toString(ciclo), ", ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_PERIODO           =>'", periodo, "', ",
                                "UN_CODIGORUTA        =>'", codigoruta, "', ",
                                "UN_CONCEPTO          =>",
                                Integer.toString(concepto), ", ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_MONTOFINANCIAR    =>",
                                montofinanciar.toString(), ", ",
                                "UN_TOTALMONTO        =>",
                                totalmonto.toString(), ", ",
                                "UN_SALDOFINANCIABLE  =>",
                                saldofinanciable.toString(), ", ",
                                "UN_NUMEROCUOTAS      =>",
                                numerocuotas.toString(), ", ",
                                "UN_NROCUOTA          =>",
                                Integer.toString(nrocuota), ", ",
                                "UN_VALORCUOTA        =>",
                                valorcuota.toString(), ", ",
                                "UN_BANCOPERPROCESO   =>'", bancoperproceso,
                                "', ", "UN_CODIGOINTERNO     =>'",
                                codigointerno, "'" };

        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM2.FC_ELIMINARFINANCIABLE",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public void calcularProductAbo(
        String compania,
        int ciclo,
        int ano,
        String periodo,
        Date fechaini,
        Date fechafin,
        String usuarioini,
        String usuariofin)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_CICLO             =>",
                                    Integer.toString(ciclo), ", ",
                                    "UN_ANO               =>",
                                    Integer.toString(ano), ", ",
                                    "UN_PERIODO           =>'", periodo, "', ",
                                    "UN_FECHAINI          =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaini),
                                    "','DD/MM/YYYY'), ",
                                    "UN_FECHAFIN          =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechafin),
                                    "','DD/MM/YYYY'), ",
                                    "UN_USUARIOINI        =>'", usuarioini,
                                    "', ", "UN_USUARIOFIN        =>'",
                                    usuariofin, "'" };

            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_SERVICIOS_PUBLICOS_COM2.PR_CALCULAPRODUCTABO",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String asignarFechaTexto(
        String compania,
        String dato,
        String tipo)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_DATO              =>'", dato, "', ",
                                "UN_TIPO              =>'", tipo, "'" };

        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM2.FC_GETFECHATEXTO",
                        SysmanFunciones.concatenar(parametros), Types.VARCHAR);
    }

    @Override
    public boolean validarManejoCartas(
        String compania,
        String nit)
                    throws SystemException {

        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_NIT               =>'", nit, "'" };

        byte rta = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM2.FC_AUTORIZACION_CARTA",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return rta != 0;
    }

    @Override
    public String agregarFinanciablePerSig(
        String compania,
        int anioIni,
        String periodoIni)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANIOINI           =>",
                                Integer.toString(anioIni), ", ",
                                "UN_PERIODOINI        =>'", periodoIni, "'" };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM2.FC_AGREGARFINANCIABLE_PERSIG",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public void verificarAdicionFinanciable(
        String compania,
        int ciclo,
        String codigoRuta,
        Date fechaCreacion)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA        =>'", compania, "', ",
                                "UN_CICLO           =>",
                                Integer.toString(ciclo), ",",
                                "UN_CODIGORUTA      =>'", codigoRuta, "',",
                                "UN_FECHACREACION   =>",
                                SysmanFunciones.formatearFecha(fechaCreacion) };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM2.PR_VERIFICA_ADIFINANCIABLE",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String actualizarConceptoAntes(
        String compania,
        int ciclo,
        String codigoRuta,
        int anioIni,
        String periodoIni,
        int concepto)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CICLO             =>",
                                Integer.toString(ciclo), ", ",
                                "UN_CODIGORUTA        =>'", codigoRuta, "', ",
                                "UN_ANIOINI           =>",
                                Integer.toString(anioIni), ", ",
                                "UN_PERIODOINI        =>'", periodoIni, "', ",
                                "UN_CONCEPTO          =>",
                                Integer.toString(concepto), ""
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_SERVICIOS_PUBLICOS_COM2.FC_ACTUALIZARCONCEPTO_ANTES",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

}