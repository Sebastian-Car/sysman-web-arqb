package com.sysman.serviciospublicos.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.ejb.Remote;

@Remote
public interface EjbServiciosPublicosDosRemote {

    String registrarPersuasivo(
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
                    throws SystemException;

    String registrarCoactivo(
        String compania,
        String consecutivo,
        int ciclo,
        String codigoinicial,
        String codigofinal,
        String peratrasoini,
        String peratrasofin,
        BigDecimal deudaini,
        BigDecimal deudafin, String usuario)
                    throws SystemException;

    boolean registrarActa(
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
                    throws SystemException;

    String asignarNombrePeriodoM(
        String compania,
        String periodo)
                    throws SystemException;

    boolean registrarActaOperacion(
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
                    throws SystemException;

    String armarDocumentoSolicitud(
        String compania,
        String clasesolicitud,
        BigInteger solicitud)
                    throws SystemException;

    String asignarNombrePeriodoDe(
        String compania,
        int ano,
        String periodo,
        String frecuencia)
                    throws SystemException;

    String asignarNombrePeriodoSigDe(
        String compania,
        int ano,
        String periodo,
        String frecuencia)
                    throws SystemException;

    boolean autorizarDesviacion(
        String compania,
        String nit)
                    throws SystemException;

    boolean autorizarFraudes(
        String compania,
        String nit)
                    throws SystemException;

    String asignarNombrePeriodoCorto(
        String compania,
        int ano,
        String periodo,
        String frecuencia)
                    throws SystemException;

    int generarAnoSiguiente(
        String compania,
        int ano,
        String periodo,
        String frecuencia)
                    throws SystemException;

    String generarPeriodoSiguiente(
        String compania,
        int ano,
        String periodo,
        String frecuencia)
                    throws SystemException;

    void actualizarAbonoPrioridad(
        String compania,
        int ciclo,
        String codigoruta,
        String periodo,
        long consecutivo,
        int ano,
        BigDecimal dblvalor,
        BigDecimal dbldeuda)
                    throws SystemException;

    BigDecimal consultarValorPagoConvenios(
        String compania,
        int ciclo,
        String codigoruta,
        int ano,
        String periodo,
        String convenio)
                    throws SystemException;

    BigDecimal consultarValorPagoTercerizado(
        String compania,
        int ciclo,
        String codigoruta,
        int ano,
        String periodo,
        String tercerizado)
                    throws SystemException;

    void charlesPesoAB(
        String compania,
        int ciclo,
        String codigoruta,
        int ano,
        String periodo,
        long consecutivo)
                    throws SystemException;

    void discriminarAbonos(
        String compania,
        String codigoruta)
                    throws SystemException;

    void actualizarAbonoRecaudos(
        String compania,
        Date fechaInicial,
        Date fechaFinal,
        String strbanco)
                    throws SystemException;

    void pasarAbonosRecaudos(
        String compania,
        Date strfecha,
        String strbanco,
        String usuario)
                    throws SystemException;

    String registrarAbono(
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
                    throws SystemException;

    boolean actualizarPagoTercerizados(
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
                    throws SystemException;

    boolean actualizarPagoConvenios(
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
                    throws SystemException;

    void calcularProducRec(
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
                    throws SystemException;

    String eliminarFinanciable(
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
                    throws SystemException;

    void calcularProductAbo(
        String compania,
        int ciclo,
        int ano,
        String periodo,
        Date fechaini,
        Date fechafin,
        String usuarioini,
        String usuariofin)
                    throws SystemException;

    String asignarFechaTexto(
        String compania,
        String dato,
        String tipo)
                    throws SystemException;

    boolean validarManejoCartas(
        String compania,
        String nit)
                    throws SystemException;

    String agregarFinanciablePerSig(String compania, int anioIni,
        String periodoIni) throws SystemException;

    void verificarAdicionFinanciable(String compania, int ciclo,
        String codigoRuta, Date fechaCreacion) throws SystemException;

    String actualizarConceptoAntes(String compania, int ciclo,
        String codigoRuta, int anioIni, String periodoIni, int concepto)
                    throws SystemException;
}