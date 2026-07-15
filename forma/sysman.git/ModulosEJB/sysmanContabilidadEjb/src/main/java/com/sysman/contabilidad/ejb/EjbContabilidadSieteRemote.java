package com.sysman.contabilidad.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.Remote;

@Remote
public interface EjbContabilidadSieteRemote {

    String generarBancosPlanos(
        String compania,
        int ano,
        BigInteger egresoInicial,
        BigInteger egresoFinal,
        Date fechapago,
        long cuentaCliente,
        long cuentaPrincipal,
        String numerobanco,
        String tipoegreso,
        String cuentainicial,
        String cuentafinal,
        int identificador,
        boolean digitoVerificacion,
        String tipoCuentaCliente,
        String claseTransaccion) throws SystemException;

    void depurarPagoRetenciones(
        String compania,
        int anio,
        int mes) throws SystemException;

    BigDecimal actualizarEstadoConsol(
        String compania,
        int ano,
        int mesinicial,
        int mesfinal,
        String usuario,
        int modulo,
        String estado) throws SystemException;

    void configurarCuentasDesc(
        String compania,
        int anio,
        String usuario)
                    throws SystemException;

    String diferenciasPorMes(
        String compania,
        int ano) throws SystemException;

    void actualizarFechaPago(
        String compania,
        int ano,
        int mes,
        Date fecha,
        String usuario,
        boolean seleccion) throws SystemException;

    String generarArchivoVerde(
        String compania,
        int ano,
        int mesInicial,
        int mesFinal) throws SystemException;

    void validarCuentasEquivalentes(
        String compania,
        int anio,
        String tipoCpte,
        long comprobante) throws SystemException;

    String validarCuentasEquivalentesEgr(
        String compania,
        int ano,
        String listaNumeroAfectar) throws SystemException;

    String carteraCuenta(String compania,
        int modulo,
        String cuenta)
                    throws SystemException;

    void cargarPlanContable(String compania, String cadenaplan, int opcion,
        String usuario) throws SystemException;

    void causarHeredandoConciliacion(String compania, int anio,
        BigInteger numero, String tipomovimiento, BigInteger numeroacopiar,
        String usuario) throws SystemException;

    void actualizarAbono(String compania, int ano, String tipoCpte,
        BigInteger comprobante, int consecutivo, BigDecimal abono,
        Date fechaabono, String usuario) throws SystemException, ParseException;
    
    public String actualizarValoresTotalesComp(String compania, String ano, String comprobante) throws SystemException;
    
    String generarPlanoCnt(
    		String compania,
            int ano,
            String tipoCpte, 
            String comprobante)
                        throws SystemException;
    
	void copiarConfigConceptos(
			String compania, 
			int anoDestino, 
			int anoOrigen)
			throws SystemException;
	
	void copiarConfigPlanContable(
			String compania, 
			int anoDestino, 
			int anoOrigen) throws SystemException;
	
	String cargarConciliar(
			String tabla, 
			String cadena, 
			String usuario,
			String compania,
			int ano
			) throws SystemException;

}