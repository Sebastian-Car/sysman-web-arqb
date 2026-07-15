package com.sysman.contabilidad.ejb;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Local;

import com.sysman.exception.SystemException;

@Local
public interface EjbContabilidadCeroLocal {

    void mayorizarContable(
        String compania,
        int anio,
        int mesIni,
        int mesFin)
                    throws SystemException;

    void corregirSaldosAuxiliaresContables(String compania, int anio,
        int mesIni, int mesFin, String codigoIni, String codigoFin)
                    throws SystemException;

    void actualizarIndicadorDeMovimientoContable(
        String compania,
        int anio)
                    throws SystemException;

    void cargarSaldosIniciales(
        String compania,
        int anio,
        String modificador)
                    throws SystemException;

    void prepararSaldosAnoSiguiente(
        String compania,
        int anoDestino)
                    throws SystemException;

    String corregirChequera(
        String compania,
        int ano,
        String cuenta)
                    throws SystemException;

    boolean revisarConciliacionPeriodo(
        int ano,
        int mes)
                    throws SystemException;

    BigDecimal consultarSaldosCaja(
        String compania,
        Date fecha,
        String cuenta,
        String naturaleza)
                    throws SystemException;

    void cambiarNitTerceros(
        String nueCompania,
        String nueNit,
        String nueSucursal,
        String antCompania,
        String antNit,
        String antSucursal, String usuario)
                    throws SystemException;

    boolean verificarPeriodo(
        String compania,
        int ano,
        int mes)
                    throws SystemException;

    String verificarInconsistencias(
        String compania,
        int ano)
                    throws SystemException;
    
     String validarCuentaUtilizar(
    		String compania,
            int ano,
            String cuenta,
            boolean validaBloqueo)
                        throws SystemException;
     
     void perpararDatosAjusteFiscal(String compania, 
    		 String anoFiscal, 
    		 String mesFiscal) 
    				 throws SystemException;
     
     
     public  void   eliminarComprobantesIngreso(
 			String compania, 
 			int ano, 
 			String tipo, 
 			String numero, 
 			String tercero) 
 			                      throws SystemException;
     
     
}