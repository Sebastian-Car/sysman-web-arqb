package com.sysman.contabilidad.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.ejb.Remote;

@Remote
public interface EjbContabilidadTresRemote
{

    String verificarInconsistenciasCuentasContables(
                    String compania,
                    int anio)
                    throws SystemException;

    String generarCuentasContablesNiif(
                    String compania,
                    int anio,
                    String codigo)
                    throws SystemException;

    String contabilizarComprobantesContablesNiif(
                    String compania,
                    int mesInicial,
                    int mesFinal,
                    String tipoInicial,
                    String tipoFinal,
                    int anio,
                    String usuario)
                    throws SystemException;

    String consultarPredecesorCuentaContable(
                    String compania,
                    int anio,
                    String codigo)
                    throws SystemException;

    boolean insertarCuentaContableEnNiif(
                    String companianiif,
                    String compania,
                    int anoFuente,
                    int anoDestino,
                    String codigo,
                    String usuario)
                    throws SystemException;

    void copiarConfiguracionEquivalenteNIIF(
                    String compania,
                    int anoFuente,
                    int anoDestino)
                    throws SystemException;

    void subirSaldosInicialesNiif(
                    String compania,
                    String companianiif,
                    int anoFuente,
                    int anoDestino,
                    boolean clave,
                    String usuario)
                    throws SystemException;

    void reclasificarniif(
                    String compania,
                    int ano,
                    String tipo,
                    int numeroregistro,
                    long consecutivo,
                    Date fecha,
                    String usuario)
                    throws SystemException;

    String inconsistenciasPlanoBancolombia(
                    String compania,
                    int ano,
                    String tipo,
                    String cuentaInicial,
                    String cuentaFinal,
                    String numeroInicial,
                    String numeroFinal,
                    String usuario)
                    throws SystemException;
    
    String  asobancariaImportarCT(
    		String compania, 
    		String usuario, 
    		String cadena) 
    		throws SystemException;
    
    void reclasificarNiifMensual(
            String compania,
            int ano,
            String tipo,
            int numeroregistro,
            long consecutivo,
            Date fecha,
            String usuario)
            throws SystemException;

    void actPagosFacturacion(
    		String compania,
    		Date fechaIni,
    		Date fechaFin,
    		String tipoCobroIni,
    		String tipoCobroFin,
    		String facturaIni,
    		String facturaFin,
    		String general)
    		throws SystemException;
    
    boolean causacionAutomatica(
            String compania,
            int ano,            
            String tipo,
            BigInteger numero,        
            String usuario,
            int varCausar)
            throws SystemException;
    
    void validacionCausacionAutomatica(
    		String compania,
            int ano,            
            String tipo,
            BigInteger numero,        
            String usuario)
    		throws SystemException;    

    int pasarTasasInteres(
    		int anioIni,
	        int anioFin,
	        String usuario)
	        throws SystemException;
    
    String generarPlanoSudameris(
            String compania, 
            int ano, 
            String egresoInicial, 
            String egresoFinal, 
            String fechaPago,
            String cuentaDebitar, 
            String cuentaPrincipalAfiliada, 
            String codigoBanco, 
            String tipoEgreso,
            String cuentaInicial, 
            String cuentaFinal, 
            String identificador, 
            String codigoVerificacion,
            String tipoCuentaCliente, 
            String claseTransaccion) 
            throws SystemException;
            
    String generarPlanoItau(
    	    String compania,
    	    int ano,
    	    String egresoInicial,
    	    String egresoFinal,
    	    String fechaPago,
    	    String cuentaDebitar,
    	    String cuentaPrincipalAfiliada,
    	    String codigoBanco,
    	    String tipoEgreso,
    	    String cuentaInicial,
    	    String cuentaFinal,
    	    String identificador,
    	    String codigoVerificacion,
    	    String tipoCuentaCliente,
    	    String nitCliente
    	) throws SystemException;
    
    String pasarProvedores(
			String compania,
			int anoOrigen,
			int anoDestino) throws SystemException;
    
    String pasarReferenciados(
			String compania,
			int anoOrigen,
			int anoDestino) throws SystemException;

    String cargarMasivoRetencion(
    		String compania,
    		String cadena,
    		int mes,
    		int dia,
    		String codigo) throws SystemException;
	

}