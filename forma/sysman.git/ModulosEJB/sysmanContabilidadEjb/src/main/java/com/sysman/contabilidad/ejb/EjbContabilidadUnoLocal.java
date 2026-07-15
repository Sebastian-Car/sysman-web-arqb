package com.sysman.contabilidad.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.ejb.Local;

@Local
public interface EjbContabilidadUnoLocal {

    int insertarCuentasRetenciones(
        String compania,
        int anioactual,
        int aniopreparar)
                    throws SystemException;

    BigDecimal calcularValoraGirar(
        String compania,
        int anio,
        String tipo,
        BigInteger numero,
        String clase,
        BigDecimal valoragirar)
                    throws SystemException;

    void eliminarComprobantePresupuestal(
        String compania,
        int anio,
        String tipo,
        BigInteger numero,
        String usuario) throws SystemException;

    void afectarOtroComprobantePresupuestal(
        String compania,
        String tipo0,
        String tipo,
        BigInteger numero,
        String cuenta,
        BigDecimal creditoa,
        BigDecimal contracreditoa,
        BigDecimal credito,
        BigDecimal contracredito,
        int consecutivo,
        String con,
        BigInteger numero0,
        String usuario)
                    throws SystemException;

    boolean revisarDesembolso(
        String compania,
        int anio,
        String cuenta,
        BigDecimal desembolso,
        BigDecimal valor)
                    throws SystemException;

    boolean descargarDesembolso(
        String compania,
        int anio,
        String cuenta,
        BigDecimal desembolso,
        String tipo,
        BigInteger numero,
        Date fecha,
        BigDecimal valor,
        BigDecimal tasadecambio,
        int consecutivo,
        String tercero,
        String sucursal,
        String centrocosto,
        String auxiliar,
        String tipoafect,
        BigInteger numeroafect,
        String descripcion,
        double porcretencion)
                    throws SystemException;

    void afectarDesembolso(
        String compania,
        int anio,
        String cuenta,
        BigDecimal desembolso,
        BigDecimal vdesem,
        BigDecimal vavdesem)
                    throws SystemException;

    void insertarDetallecomprobanteContable(
        String compania,
        int anio,
        String tipo,
        BigInteger numero,
        String cuenta,
        int consecutivo,
        String fecha,
        String descripcion,
        String tercero,
        String sucursal,
        String centrocosto,
        String auxiliar,
        BigDecimal valordebito,
        BigDecimal valorcredito,
        String tipoafect,
        BigInteger numeroafect,
        BigDecimal debitoequiv,
        BigDecimal creditoequiv,
        double porcentajeret)
                    throws SystemException;

    String porcentajeCuentaBancos(
        String compania,
        int anio,
        String tipo,
        BigInteger numero)
                    throws SystemException;

    String consolidarCompanias(
        String companiacon,
        int anio)
                    throws SystemException;

    BigDecimal consultarSaldoFinalDeCaja(
        String compania,
        Date fecha,
        String cuenta,
        String naturaleza)
                    throws SystemException;

    BigDecimal consultarSaldoFinalCajayBancos(
        String compania,
        Date fecha,
        String cuenta,
        String naturaleza)
                    throws SystemException;

    String generarLibroDiario(
        String compania,
        String tipoInicial,
        String tipoFinal,
        Date fechaInicial,
        Date fechaFinal)
                    throws SystemException;

    String prepararLibroDiario(
        String compania,
        String tipoInicial,
        String tipoFinal,
        Date fechaInicial,
        Date fechaFinal)
                    throws SystemException;

    String consultarConsecutivosFaltantes(
        String tipo,
        String compania,
        Date fechainicial,
        Date fechafinal)
                    throws SystemException;

    String revisarAfectacionesDeCartera(
        String compania,
        int ano,
        Date fechaCorte)
                    throws SystemException;

    String afectarComprobantePresupuestal(
        String compania,
        String tipoafec,
        BigInteger numeroafec,
        int anoafec,
        Date fecha,
        String tipo,
        BigInteger numero,
        String clase,
        int ano,
        String pacproporcionalgiro)
                    throws SystemException;

    void generarComprobantePresupuestal(String compania, int ano, String tipo,
        BigInteger numero, Date fecha, String tercero, String sucursal,
        String descripcion, String numerodoc, BigDecimal valordoc,
        String tipopptal, String cadenainsertar, int cantidad, String usuario)
                    throws SystemException;

	void generarMasivoComPptal(String compania, String tipo, String anio, String mes, String numeroIni,
			String numeroFin, String usuario) throws SystemException;
	
}