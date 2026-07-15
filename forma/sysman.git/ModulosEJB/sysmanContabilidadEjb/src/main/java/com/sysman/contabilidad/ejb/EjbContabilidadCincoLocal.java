package com.sysman.contabilidad.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.ejb.Local;

@Local
public interface EjbContabilidadCincoLocal
{

    boolean verificarSaldoRegistro(
        String compania,
        int ano,
        String tipoCpte,
        BigInteger comprobante,
        BigDecimal valordocumento)
                    throws SystemException;

    void generarComprobantePresupuestalVarios(String compania, int ano,
        String tipo, BigInteger numero, boolean siafecta,
        String listanumeroafectar, String listaterceroafectar, String usuario)
                    throws SystemException;
    
    void generarComprobantePresupuestalVarios(String compania, int ano,
            String tipo, BigInteger numero, boolean siafecta,
            String listanumeroafectar, String listaterceroafectar, String usuario, String listaterceroafectarcauto, int variable)
                        throws SystemException;

    void afectarComprobantePptalvarios(
        String compania,
        int anoafec,
        String tipoafec,
        BigInteger numeroafec,
        int ano,
        String tipo,
        BigInteger numero,
        String clase,
        Date fecha)
                    throws SystemException;

    void crearDetallesPptalesVarios(
        String compania,
        int ano,
        String tipo,
        BigInteger numero,
        String clase)
                    throws SystemException;

    void actualizarDebitosCreditosAfectados(
        String compania,
        int ano,
        String tipo,
        BigInteger numero,
        String valorDebito,
        String valorCredito,
        int consecutivo)
                    throws SystemException;

    void eliminarComprobantesCNT(
        String compania,
        int anio,
        String tipo,
        BigInteger numero, 
        String usuario)
                    throws SystemException;

    void corregirOrdenes(
        String compania)
                    throws SystemException;

    void revisaArAfectacionesCnth(
        String compania,
        int ano)
                    throws SystemException;

    boolean LimiteInferiorRetencion(
        String compania,
        String tipo,
        int ano,
        String codigo,
        BigDecimal valorbase)
                    throws SystemException;

    boolean permiteCambiarValor(
        String compania,
        String tipo,
        int ano,
        String codigo)
                    throws SystemException;

    boolean permiteCambiarValorBase(
        String compania,
        String tipo,
        int ano,
        String codigo)
                    throws SystemException;

    void actualizarConfSiguienteAnio(
        String compania,
        int anioFuente,
        int anioDestino,
        String usuario) throws SystemException;
    
    String cargarTerceros(
		String compania, 
		String cadenaplan, 
		String usuario) throws SystemException;

	void generarEstadoCambiosPatrimonio(
			String compania, 
			int anioIni, 
			int anioFin, 
			int mesIni, 
			int mesFin,
			String codigoIni, String codigoFin) throws SystemException;
	
	void generarCausacionIng(
	        String compania,
	        String anio,
	        String tipo,
	        String numero, 
	        String usuario)
	                    throws SystemException;
}