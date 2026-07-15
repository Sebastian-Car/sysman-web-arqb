package com.sysman.predial.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.ejb.EjbPredialTresLocal;
import com.sysman.predial.ejb.EjbPredialTresRemote;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class PredialTres
 *
 * @version 2, 10/06/2017 jrodriguezr Se realiza refactorización de
 * concatenados
 */
@Stateless
@LocalBean
public class EjbPredialTres
                implements EjbPredialTresRemote, EjbPredialTresLocal {
    /**
     * Default constructor.
     */
    public EjbPredialTres() {
        // Sin Sentencias
    }

    @Override
    public int repartirDeuda(
        String compania,
        String codpredio,
        int anoabono,
        BigDecimal abono,
        String numrecibo,
        boolean indacuerdo,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CODPREDIO         =>'", codpredio, "', ",
                                "UN_ANOABONO          =>",
                                Integer.toString(anoabono), ", ",
                                "UN_ABONO             =>", abono.toString(),
                                ", ", "UN_NUMRECIBO         =>'", numrecibo,
                                "', ", "UN_INDACUERDO        =>",
                                (indacuerdo ? "-1" : "0"), ", ",
                                "UN_USUARIO           =>'", usuario, "'" };
        return (int) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM3.FC_REPARTIRDEUDA",
                        SysmanFunciones.concatenar(parametros),
                        Types.INTEGER);
    }

    @Override
    public void repartirDeudaProratiada(
        String compania,
        String codpredio,
        int anoabono,
        BigDecimal abono,
        String numrecibo,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CODPREDIO         =>'", codpredio, "', ",
                                "UN_ANOABONO          =>",
                                Integer.toString(anoabono), ", ",
                                "UN_ABONO             =>", abono.toString(),
                                ", ",
                                "UN_NUMRECIBO         =>'", numrecibo, "', ",
                                "UN_USUARIO           =>'", usuario, "'" };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM3.PR_REPARTIRDEUDAPRORATEADA",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public String impresoraAbono(
        String compania,
        Date fechacorte,
        int anoabono,
        String codpredio,
        BigDecimal abono,
        String usuario)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_FECHACORTE        =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechacorte),
                                    "','DD/MM/YYYY'), ",
                                    "UN_ANOABONO          =>",
                                    Integer.toString(anoabono), ", ",
                                    "UN_CODPREDIO         =>'", codpredio,
                                    "', ", "UN_ABONO             =>",
                                    abono.toString(),
                                    ", ", "UN_USUARIO           =>'", usuario,
                                    "'" };
            return (String) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PREDIAL_COM3.PR_IMPRESORAABONO",
                            SysmanFunciones.concatenar(parametros),
                            Types.VARCHAR);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public BigDecimal consultarCodigoConcepto(
        int concepto)
                    throws SystemException {
        String[] parametros = { "UN_CONCEPTO          =>",
                                Integer.toString(concepto), "" };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN, "PCK_PREDIAL_COM3.FC_CPTO",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public void asignarPorcReserva(
        String compania,
        Date fechacorte,
        String usuario,
        String codpredio,
        boolean aplicadesc,
        boolean indreserva,
        double porcreserva,
        int pagoano,
        boolean calculoserie,
        boolean ley1066,
        boolean ley1175)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_FECHACORTE        =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechacorte),
                                    "','DD/MM/YYYY'), ",
                                    "UN_USUARIO           =>'", usuario, "', ",
                                    "UN_CODPREDIO         =>'", codpredio,
                                    "', ", "UN_APLICADESC        =>",
                                    aplicadesc ? "-1" : "0", ", ",
                                    "UN_INDRESERVA        =>",
                                    indreserva ? "-1" : "0", ", ",
                                    "UN_PORCRESERVA       =>",
                                    Double.toString(porcreserva),
                                    ", ", "UN_PAGOANO           =>",
                                    Integer.toString(pagoano),
                                    ", ", "UN_CALCULOSERIE      =>",
                                    calculoserie ? "-1" : "0", ", ",
                                    "UN_LEY1066           =>",
                                    ley1066 ? "-1" : "0", ", ",
                                    "UN_LEY1175           =>",
                                    ley1175 ? "-1" : "0", "" };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PREDIAL_COM3.PR_ASIGNAPORC_RESERVA",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public BigDecimal avaluoInicialRes(
        int vigencia)
                    throws SystemException {
        String[] parametros = { "UN_VIGENCIA          =>",
                                Integer.toString(vigencia), "" };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM3.FC_AVALUOINICIALRES",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public BigDecimal retornarPorcetajeDescuento(
        String compania,
        int ano,
        Date fechacorte,
        String clasepredio,
        String codpredio)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_ANO               =>",
                                    Integer.toString(ano), ", ",
                                    "UN_FECHACORTE        =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechacorte),
                                    "','DD/MM/YYYY'), ",
                                    "UN_CLASEPREDIO       =>'", clasepredio,
                                    "', ", "UN_CODPREDIO         =>'",
                                    codpredio, "'" };
            return (BigDecimal) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PREDIAL_COM3.FC_DESCUENTOMES",
                            SysmanFunciones.concatenar(parametros),
                            Types.DECIMAL);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public BigDecimal retornarValorConceptoAbono(
        String compania,
        String codpredio,
        int concepto)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CODPREDIO         =>'", codpredio, "', ",
                                "UN_CONCEPTO          =>",
                                Integer.toString(concepto), "" };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM3.FC_DESCABONOS",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public int retornarMesesEnMora(
        String compania,
        int ano,
        Date fechacorte,
        boolean indExeint,
        Date fechainicialExeint,
        Date fechafinalExeint)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_ANO               =>",
                                    Integer.toString(ano), ", ",
                                    "UN_FECHACORTE        =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechacorte),
                                    "','DD/MM/YYYY'), ",
                                    "UN_IND_EXEINT        =>",
                                    (indExeint ? "-1" : "0"), ", ",
                                    "UN_FECHAINICIAL_EXEI =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechainicialExeint),
                                    "','DD/MM/YYYY'), ",
                                    "UN_FECHAFINAL_EXEINT =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechafinalExeint),
                                    "','DD/MM/YYYY')" };
            return (int) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PREDIAL_COM3.FC_MESESMORA",
                            SysmanFunciones.concatenar(parametros),
                            Types.INTEGER);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public int retornarMenorVigenciaAdeudada(
        String compania,
        String codpredio)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CODPREDIO         =>'", codpredio, "'" };
        return (int) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM3.FC_MENORVIGENCIA_ADEUDADA",
                        SysmanFunciones.concatenar(parametros),
                        Types.INTEGER);
    }

    @Override
    public int retornarMenorVigenciaAdeudadaSinAcuerdo(
        String compania,
        String codpredio)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CODPREDIO         =>'", codpredio, "'" };
        return (int) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM3.FC_MENORVIGENCIA_SINACUERDO",
                        SysmanFunciones.concatenar(parametros),
                        Types.INTEGER);
    }

    @Override
    public BigDecimal calcularIncrementoAvaluo(
        String compania,
        String codpredio)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CODPREDIO         =>'", codpredio, "'" };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM3.FC_INCREMENTOAVALUO",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public boolean validarPagaDescEspecial(
        String compania,
        String codpredio,
        int anofin)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CODPREDIO         =>'", codpredio, "', ",
                                "UN_ANOFIN            =>",
                                Integer.toString(anofin), "" };
        byte rta = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM3.FC_PAGADESCESP",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return rta != 0;
    }

    @Override
    public boolean validarPagaTodo(
        String compania,
        String codpredio,
        int anofin)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CODPREDIO         =>'", codpredio, "', ",
                                "UN_ANOFIN            =>",
                                Integer.toString(anofin), "" };
        byte rta = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM3.FC_PAGATODO",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
        return rta != 0;
    }

    @Override
    public BigDecimal calcularDescuentosRegistrados(
        String compania,
        String codpredio,
        int anoinicial,
        int anofinal)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CODPREDIO         =>'", codpredio, "', ",
                                "UN_ANOINICIAL        =>",
                                Integer.toString(anoinicial), ", ",
                                "UN_ANOFINAL          =>",
                                Integer.toString(anofinal), "" };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM3.FC_DESC_REGISTRADOS",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public String retornarFormulaConcepto(
        String compania,
        int ano,
        int concepto)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_CONCEPTO          =>",
                                Integer.toString(concepto), "" };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM3.FC_RETORNAR_FORMULACPTO",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public int retornarMesesAmnistia(
        String compania,
        int ano)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), "" };
        return (int) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM3.FC_AMNISTIA",
                        SysmanFunciones.concatenar(parametros),
                        Types.INTEGER);
    }

    @Override
    public double evaluarPorcentajeDescuento(
        String compania,
        String nitcompania,
        int ano,
        int anomindesc,
        double porcentaje,
        double porcentajegr,
        String codtarifa)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_NITCOMPANIA       =>'", nitcompania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_ANOMINDESC        =>",
                                Integer.toString(anomindesc), ", ",
                                "UN_PORCENTAJE        =>",
                                Double.toString(porcentaje), ", ",
                                "UN_PORCENTAJEGR      =>",
                                Double.toString(porcentajegr), ", ",
                                "UN_CODTARIFA         =>'", codtarifa, "'" };
        return (double) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM3.FC_EVALUARPORCDESCUENTO",
                        SysmanFunciones.concatenar(parametros),
                        Types.DOUBLE);
    }

    @Override
    public BigDecimal retornarTasaInteresParametro()
                    throws SystemException {
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN, "PCK_PREDIAL_COM3.FC_TASA",
                        "",
                        Types.DECIMAL);
    }

    @Override
    public String facturarVigencia(
        String compania,
        String nitcompania,
        String siglacompania,
        String codigopredio,
        int pagoAno,
        boolean unicoAno,
        Date fechacorte,
        Date fechalimite,
        String nombrePropietario,
        String numeroordenPropietario,
        String nitPropietario,
        boolean facturaCero,
        String usuario,
        int anioinicial,
        int aniofin,
        String avaluoAno,
        boolean aplicadesc)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_NITCOMPANIA       =>'", nitcompania,
                                    "', ", "UN_SIGLACOMPANIA     =>'",
                                    siglacompania, "', ",
                                    "UN_CODIGOPREDIO      =>'", codigopredio,
                                    "', ", "UN_PAGO_ANO          =>",
                                    Integer.toString(pagoAno),
                                    ", ", "UN_UNICO_ANO           =>",
                                    unicoAno ? "-1" : "0", ", ",
                                    "UN_FECHACORTE        =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechacorte),
                                    "','DD/MM/YYYY'), ",
                                    "UN_FECHALIMITE       =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechalimite),
                                    "','DD/MM/YYYY'), ",
                                    "UN_NOMBRE_PROPIETARIO =>'",
                                    nombrePropietario, "', ",
                                    "UN_NUMEROORDEN_PROPIETARIO =>'",
                                    numeroordenPropietario, "', ",
                                    "UN_NIT_PROPIETARIO   =>'", nitPropietario,
                                    "', ", "UN_FACTURA_CERO      =>",
                                    facturaCero ? "-1" : "0", ", ",
                                    "UN_USUARIO           =>'", usuario, "', ",
                                    "UN_ANIOINICIAL       =>",
                                    Integer.toString(anioinicial),
                                    ", ", "UN_ANIOFIN           =>",
                                    Integer.toString(aniofin),
                                    ", ", "UN_AVALUO_ANO        =>'", avaluoAno,
                                    "', ", "UN_APLICADESC        =>",
                                    aplicadesc ? "-1" : "0", "" };
            return (String) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PREDIAL_COM3.FC_FACTURAR",
                            SysmanFunciones.concatenar(parametros),
                            Types.VARCHAR);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void facturarMultifechas(
        String compania,
        String docnum,
        String usuario,
        boolean unicoAno,
        boolean aplicaDsctoEspecial,
        int anoInicial,
        int anoFinal)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_DOCNUM            =>'", docnum, "', ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_UNICO_ANO         =>",
                                (unicoAno ? "-1" : "0"), ", ",
                                "UN_APLICA_DSCTO_ESPE =>",
                                (aplicaDsctoEspecial ? "-1" : "0"), ", ",
                                "UN_ANO_INICIAL       =>",
                                Integer.toString(anoInicial), ", ",
                                "UN_ANO_FINAL         =>",
                                Integer.toString(anoFinal), "" };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM3.PR_FACTURAR_MULTIFECHAS",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void recibosMultifecha(
        String compania,
        String docnum,
        String usuario,
        String codPredio,
        String numeroOrden,
        String condicion,
        boolean aplicaDsctoEspecial,
        boolean unicoAno,
        int anoInicial,
        int anoFinal)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_DOCNUM            =>'", docnum, "', ",
                                "UN_USUARIO           =>'", usuario, "', ",
                                "UN_COD_PREDIO        =>'", codPredio, "', ",
                                "UN_NUMERO_ORDEN      =>'", numeroOrden, "', ",
                                "UN_CONDICION         =>'", condicion, "', ",
                                "UN_APLICA_DSCTO_ESPE =>",
                                (aplicaDsctoEspecial ? "-1" : "0"), ", ",
                                "UN_UNICO_ANO         =>",
                                (unicoAno ? "-1" : "0"), ", ",
                                "UN_ANO_INICIAL       =>",
                                Integer.toString(anoInicial), ", ",
                                "UN_ANO_FINAL         =>",
                                Integer.toString(anoFinal), "" };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM3.PR_RECIBOS_MULTIFECHA",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public BigDecimal getDescuentoFactura(
        String compania,
        String codpredio,
        int anoInicial,
        int anoFinal)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CODPREDIO         =>'", codpredio, "', ",
                                "UN_ANO_INICIAL       =>",
                                Integer.toString(anoInicial), ", ",
                                "UN_ANO_FINAL         =>",
                                Integer.toString(anoFinal), "" };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM3.FC_DESC_FAC",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public BigDecimal getValorConcepto(
        String compania,
        int concepto,
        int ano,
        String codigo)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CONCEPTO          =>",
                                Integer.toString(concepto), ", ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_CODIGO            =>'", codigo, "'" };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM3.FC_CONCEPTO",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public BigDecimal getMoraAcumuladaMensual(
        String compania,
        String codigo,
        int ano,
        Date fechaCorte)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_CODIGO            =>'", codigo, "', ",
                                    "UN_ANO               =>",
                                    Integer.toString(ano), ", ",
                                    "UN_FECHA_CORTE       =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaCorte),
                                    "','DD/MM/YYYY')" };
            return (BigDecimal) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PREDIAL_COM3.FC_MORAACUMULADAMENSUAL",
                            SysmanFunciones.concatenar(parametros),
                            Types.DECIMAL);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public BigDecimal getTasaInteresTarifa(
        String compania,
        String codigotarifa,
        int anotarifa,
        BigDecimal avaluotarifa,
        String rangotarifa)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CODIGOTARIFA      =>'", codigotarifa, "', ",
                                "UN_ANOTARIFA         =>",
                                Integer.toString(anotarifa), ", ",
                                "UN_AVALUOTARIFA      =>",
                                avaluotarifa.toString(), ", ",
                                "UN_RANGOTARIFA       =>'", rangotarifa, "'" };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM3.FC_TASAINTERESTARIFA",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public int getMesesMoraFecha(
        String compania,
        Date fechaAlDia,
        Date fechaCorte)
                    throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_FECHA_AL_DIA      =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaAlDia),
                                    "','DD/MM/YYYY'), ",
                                    "UN_FECHA_CORTE       =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaCorte),
                                    "','DD/MM/YYYY')" };
            return (int) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PREDIAL_COM3.FC_MESESMORA_FECHA",
                            SysmanFunciones.concatenar(parametros),
                            Types.INTEGER);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public BigDecimal getDescuentoMesFactura(
        String compania,
        int mes)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_MES               =>",
                                Integer.toString(mes), "" };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM3.FC_DESCUENTOMES_FACTURA",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public BigDecimal getValorAvaluo(
        String compania,
        int ano,
        String codigo)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_CODIGO            =>'", codigo, "'" };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM3.FC_AVALUO",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public BigDecimal getTarifa(
        String compania,
        int ano,
        String codigo)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_CODIGO            =>'", codigo, "'" };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM3.FC_TARIFA",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public BigDecimal getValorConceptoPeriodoAnterior(
        String compania,
        int concepto,
        int ano,
        String codigo)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CONCEPTO          =>",
                                Integer.toString(concepto), ", ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_CODIGO            =>'", codigo, "'" };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM3.FC_CONCEPTOPERIODOANTERIOR",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public BigDecimal getSumaConcepto(
        String compania,
        int concepto,
        int ano,
        String codigo)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CONCEPTO          =>",
                                Integer.toString(concepto), ", ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_CODIGO            =>'", codigo, "'" };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN, "PCK_PREDIAL_COM3.FC_SUMA",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public BigDecimal getValorExcedentes(
        String compania,
        int ano,
        String codigo)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_CODIGO            =>'", codigo, "'" };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM3.FC_EXCEDENTES",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public BigDecimal getValorSaldos(
        String compania,
        int ano,
        String codigo)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_CODIGO            =>'", codigo, "'" };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM3.FC_SALDOS",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public String evaluarConceptos(
        String formula,
        String compania,
        int ano,
        String codigo,
        BigDecimal digitosRedondeo)
                    throws SystemException {
        String[] parametros = { "UN_FORMULA           =>'", formula, "', ",
                                "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_CODIGO            =>'", codigo, "', ",
                                "UN_DIGITOS_REDONDEO  =>",
                                digitosRedondeo.toString(),
                                "" };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_PREDIAL_COM3.FC_EVALUAR_CONCEPTOS",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }
}