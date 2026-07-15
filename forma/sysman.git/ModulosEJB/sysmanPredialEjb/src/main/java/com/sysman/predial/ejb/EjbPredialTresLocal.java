package com.sysman.predial.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Local;

@Local
public interface EjbPredialTresLocal {

    int repartirDeuda(
        String compania,
        String codpredio,
        int anoabono,
        BigDecimal abono,
        String numrecibo,
        boolean indacuerdo,
        String usuario)
                        throws SystemException;

    void repartirDeudaProratiada(
        String compania,
        String codpredio,
        int anoabono,
        BigDecimal abono,
        String numrecibo,
        String usuario)
                        throws SystemException;

    String impresoraAbono(
        String compania,
        Date fechacorte,
        int anoabono,
        String codpredio,
        BigDecimal abono,
        String usuario)
                        throws SystemException;

    BigDecimal consultarCodigoConcepto(
        int concepto)
                        throws SystemException;

    void asignarPorcReserva(
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
                        throws SystemException;

    BigDecimal avaluoInicialRes(
        int vigencia)
                        throws SystemException;

    BigDecimal retornarPorcetajeDescuento(
        String compania,
        int ano,
        Date fechacorte,
        String clasepredio,
        String codpredio)
                        throws SystemException;

    BigDecimal retornarValorConceptoAbono(
        String compania,
        String codpredio,
        int concepto)
                        throws SystemException;

    int retornarMesesEnMora(
        String compania,
        int ano,
        Date fechacorte,
        boolean indExeint,
        Date fechainicialExeint,
        Date fechafinalExeint)
                        throws SystemException;

    int retornarMenorVigenciaAdeudada(
        String compania,
        String codpredio)
                        throws SystemException;

    int retornarMenorVigenciaAdeudadaSinAcuerdo(
        String compania,
        String codpredio)
                        throws SystemException;

    BigDecimal calcularIncrementoAvaluo(
        String compania,
        String codpredio)
                        throws SystemException;

    boolean validarPagaDescEspecial(
        String compania,
        String codpredio,
        int anofin)
                        throws SystemException;

    boolean validarPagaTodo(
        String compania,
        String codpredio,
        int anofin)
                        throws SystemException;

    BigDecimal calcularDescuentosRegistrados(
        String compania,
        String codpredio,
        int anoinicial,
        int anofinal)
                        throws SystemException;

    String retornarFormulaConcepto(
        String compania,
        int ano,
        int concepto)
                        throws SystemException;

    int retornarMesesAmnistia(
        String compania,
        int ano)
                        throws SystemException;

    double evaluarPorcentajeDescuento(
        String compania,
        String nitcompania,
        int ano,
        int anomindesc,
        double porcentaje,
        double porcentajegr,
        String codtarifa)
                        throws SystemException;

    BigDecimal retornarTasaInteresParametro()
                    throws SystemException;

    String facturarVigencia(
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
                        throws SystemException;

    void facturarMultifechas(
        String compania,
        String docnum,
        String usuario,
        boolean unicoAno,
        boolean aplicaDsctoEspecial,
        int anoInicial,
        int anoFinal)
                        throws SystemException;

    void recibosMultifecha(
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
                        throws SystemException;

    BigDecimal getDescuentoFactura(
        String compania,
        String codpredio,
        int anoInicial,
        int anoFinal)
                        throws SystemException;

    BigDecimal getValorConcepto(
        String compania,
        int concepto,
        int ano,
        String codigo)
                        throws SystemException;

    BigDecimal getMoraAcumuladaMensual(
        String compania,
        String codigo,
        int ano,
        Date fechaCorte)
                        throws SystemException;

    BigDecimal getTasaInteresTarifa(
        String compania,
        String codigotarifa,
        int anotarifa,
        BigDecimal avaluotarifa,
        String rangotarifa)
                        throws SystemException;

    int getMesesMoraFecha(
        String compania,
        Date fechaAlDia,
        Date fechaCorte)
                        throws SystemException;

    BigDecimal getDescuentoMesFactura(
        String compania,
        int mes)
                        throws SystemException;

    BigDecimal getValorAvaluo(
        String compania,
        int ano,
        String codigo)
                        throws SystemException;

    BigDecimal getTarifa(
        String compania,
        int ano,
        String codigo)
                        throws SystemException;

    BigDecimal getValorConceptoPeriodoAnterior(
        String compania,
        int concepto,
        int ano,
        String codigo)
                        throws SystemException;

    BigDecimal getSumaConcepto(
        String compania,
        int concepto,
        int ano,
        String codigo)
                        throws SystemException;

    BigDecimal getValorExcedentes(
        String compania,
        int ano,
        String codigo)
                        throws SystemException;

    BigDecimal getValorSaldos(
        String compania,
        int ano,
        String codigo)
                        throws SystemException;

    String evaluarConceptos(
        String formula,
        String compania,
        int ano,
        String codigo,
        BigDecimal digitosRedondeo)
                        throws SystemException;
}