package com.sysman.contabilidad.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.ejb.Remote;

@Remote
public interface EjbContabilidadSeisRemote {

    int generarEgresos(
        String compania,
        int ano,
        String tipoCpte,
        long comprobante,
        String listanumeroafectar,
        String listaterceroafectar,
        boolean terceroegreso,
        Date fecha,
        String clase,
        String tercero,
        String sucursal,
        BigDecimal valorapagar,
        String usuario, int cantidad)
                    throws SystemException;

    BigDecimal calcularValorAGirar(
        String compania,
        int anio,
        String tipo,
        BigInteger numero,
        String clase,
        BigDecimal valoragirar,
        BigDecimal vlrdocumento,
        BigDecimal totaldebito,
        BigDecimal vlrgirardg)
                    throws SystemException;

    String generarRtaConciliadosXPlano(
        String compania,
        String cuenta,
        String cadena,
        boolean eliminarpartidas,
        boolean maningpartidasconc,
        boolean validadoc,
        int colvalingreso,
        int colvalcredito,
        int coldocumento,
        int colerror,
        int colfecha,
        Date fecha,
        String formatofecha,
        int filaini,
        String usuario)
                    throws SystemException;

    BigDecimal crearTotalListaAfec(
        String compania,
        String listanumeroafectar,
        String clase)
                    throws SystemException;

    BigDecimal crearTotalListaAfecTer(
        String compania,
        String listanumeroafectar,
        String tercero,
        String sucursal,
        String clase)
                    throws SystemException;

    boolean traerSaldoCuenta(
        String compania, int ano, String tipo, BigInteger numero,
        BigDecimal vlrgirar) throws SystemException;

    void generarIngreso(String compania, int ano, String tipoCpte,
        BigInteger comprobante, String tercero, String sucursal,
        String listanumeroafectar, Date fecha, String clase, String cuenta,
        String usuario) throws SystemException;
    
  //INI_7741561_CONTABILIDAD (mrosero)
    void generarIngresoNotasCliente(String compania, int ano, String tipoCpte,
            BigInteger comprobante, String tercero, String sucursal,
            String listanumeroafectar, Date fecha, String clase,
            String usuario) throws SystemException;   
   //FIN_7741561_CONTABILIDAD (mrosero)    
    
    void validarChequera(
        String compania,
        int ano,
        String cuenta,
        long chequeini,
        long chequefin)
                    throws SystemException;

    void reflejarSaldos(String compania, int anio) throws SystemException;

    boolean retencionPorTercero(
        String compania,
        int ano,
        String tipoComprobro,
        BigInteger numeroComprob,
        String tercero,
        String sucursal,
        String valorBase,
        String valor,
        String usuario) throws SystemException;

    boolean actualizarEstadoC(
        String compania,
        int anio,
        int mes,
        String cuenta,
        Date ultimodia,
        String usuario,
        String clases,
        boolean seleccion)
                    throws SystemException;

    void generarAnulacion(
        String compania,
        int ano,
        String tipoCpte,
        BigInteger comprobante,
        Date fecha,
        String clase,
        String listanumeroafectar,
        String usuario)
                    throws SystemException;
    
	String getPivotPlanContable(    		  String compania
    		, Date fechaInicial
    		, Date fechaFinal
    	    , String parametro
    	    , String tipo)
                        throws SystemException;
	
	BigDecimal obtenerValorFacturado(
			String compania, 
			String anioCpte, 
			String tipoCpte, 
			String nroCpte)
			throws SystemException;
	
	void cargarmovcontables(
	          String compania, String cadenah,
	          String cadenad, int proceso, String usuario
	                      )throws SystemException;
	
	void generarIngresoOST(
	        String compania,
	        int ano,
	        String tipoCpte,
	        BigInteger comprobante,
	        String tercero,
	        String sucursal,
	        String listanumeroafectar,
	        Date fecha,
	        String usuario)
	                    throws SystemException;
	
	
    void calcularValorNeto(
            String compania,
            int ano,
            String tipoComp,
            BigInteger numeroComp,
            int valoragirar,
            int vlrdocumento
            )
                        throws SystemException;
    
    void calcularValorNeto(
            String compania,
            String ano,
            String tipoComp,
            String numeroComp,
            String valoragirar,
            String vlrdocumento
            )
                        throws SystemException;
    
    String validarAuxiliaresEgresos(
			String compania, 
			int ano, 
			String tipoCpte, 
			long comprobante, 
			double valorBanco,
			String referencia, 
			String centroCosto, 
			String listanumeroafectar) 
					throws SystemException;

    BigDecimal  validarcsaldoafectado(
    		String compania,
    		String anioCpte,
	        String tipoCpte,
	        String nroCpte,
	        String cvalorcredito,
	        String cvalordebito,
	        String numeroComp,
	        String tipoComp,
	        String anio,
	        String cuenta)
    		throws SystemException;
    
    void  actualizarsaldoafect(
    		String compania,
    		String anioCpte,
	        String tipoCpte,
	        String nroCpte,
	        String cvalorcredito,
	        String cvalordebito,
	        String numeroComp,
	        String tipoComp,
	        String anio,
	        String cuenta,
	        String accion,
	        String consecutivo)
    		throws SystemException;
    
    void actualizarsaldopj(
      		 String compania,
      	     String cuenta,
      	     String valordebito,
      	     String numeroProceso)
      	     throws SystemException;
    
    String validarcuentas(String compania, int ano , String listanumeroafectar)
			throws SystemException; 
    
    void generarAnticipo(
			String compania, 
			int ano, 
			String tipoCpte, 
			BigInteger comprobante, 
			String tercero,
			String sucursal, 
			String listaFacturas, 
			String listaAnticipos, 
			Date fecha, 
			String clase, 
			BigDecimal valor,
			String usuario) throws SystemException;
    
    void generarADC(
			String compania, 
			int ano, 
			String tipoCpte, 
			BigInteger comprobante, 
			Date fecha, 
			String clase, 
			String listaAfectar,
			String usuario) throws SystemException;
    
    BigDecimal validarSaldoAuxCxp(
			String compania, 
			int ano, 
			String tipoCpte, 
			long comprobante,
			String referencia, 
			String centroCosto, 
			String listanumeroafectar) 
					throws SystemException;
}

