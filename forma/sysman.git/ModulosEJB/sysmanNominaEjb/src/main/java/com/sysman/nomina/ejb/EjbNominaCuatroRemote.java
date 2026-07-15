package com.sysman.nomina.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Remote;

@Remote
public interface EjbNominaCuatroRemote {

    boolean borrarEmbargos(
        int proceso,
        int mes,
        int anio,
        int periodo,
        String compania)
                    throws SystemException;

    String nombreJuzgado(
        String compania,
        String idjuzgado)
                    throws SystemException;

    int conceptoEquivalente(
        String compania,
        String idDeConcepto)
                    throws SystemException;

    boolean subirNovedadExcel(
        String compania,
        int proceso,
        int ano,
        int mes,
        int concepto,
        int codigo,
        BigDecimal valor,
        int periodo,
        String tipo,
        String usuario,
		int nsiaue)
                    throws SystemException;

    void crearNovedadesActuales(
        String compania,
        String usuario)
                    throws SystemException;

    String discoBancoAgrario(
        String compania,
        int proceso,
        int anio,
        int mes,
        int periodo,
        String banco,
        Date fechaReporte,
        int oficinaOrigen) throws SystemException;

String generarDiscoBancoColombia(
        String compania,
        int proceso,
        int anio,
        int mes,
        int periodo,
        String banco,
        Date fechareporte,
        boolean todoslosbancos,
        String observacion,
        String lote,
        int informe,
        String tcuentabanorigen,
        String cuentabanorigen)
                    throws SystemException;

String generarDiscoBancoColombiaGn(
        String compania,
        int proceso,
        int anio,
        int mes,
        int periodo,
        String banco,
        Date fechareporte,
        boolean todoslosbancos,
        String observacion,
        String lote,
        String desPago,
		String referencia,
        int informe,
        String tcuentabanorigen,
        String cuentabanorigen)
                    throws SystemException;

    String generarDiscoBancoPopular(
        String compania,
        int proceso,
        int anio,
        int mes,
        int periodo,
        String banco,
        boolean todoslosbancos,
        Date fechaReporte,
        String observacion) throws SystemException;
    
   String generarDiscoTodosLosBancos(
            String compania,
            int proceso,
            int anio,
            int mes,
            int periodo,
            String banco,
            boolean todoslosbancos,
            Date fechaReporte,
            String observacion,
            String tcuentabanorigen,
            String cuentabanorigen) throws SystemException;
    
    String generarDiscoBancolombiaIdipron(
			String compania, 
			int proceso, 
			int periodo, 
			int mes, 
			int ano,
			Date fechaemision, 
			Date fechatransaccion, 
			String observacion, 
			String secuencialote, 
			String banco,
			boolean todoslosbancos) throws SystemException;
    
    String generarDiscoDaviviendaIdi(
			String compania, 
			int proceso, 
			int periodo, 
			int mes, 
			int ano, 
			Date fechaproceso,
			Date fechasistema,
			String banco,
			boolean todosLosBancos)
			throws SystemException;
	
    String generarDiscobbva_Cash(
    		String compania, 
    		int proceso, 
    		int periodo, 
    		int mes, 
    		int ano, 
    		Date fechaReporte, 
    		String banco) throws SystemException;
    
	String generarDiscoCajaSocialDuitama(
			String compania, 
			int proceso, 
			int periodo, 
			int mes, 
			int ano,  
			Date fechaReporte, 
			String concpago, 
			String banco) throws SystemException;
    
    String discoBancoAgrarioGobNarino(
			String compania, 
			int proceso, 
			int anio, 
			int mes, 
			int periodo, 
			String banco,
			Date fechareporte, 
			int oficinaorigen) throws SystemException;
    
   
    String generarDaviviendaExcel(
			String compania, 
			int proceso, 
			int periodo, 
			int mes, 
			int ano) throws SystemException;
    
    String cargarDatosReforma(
			String compania, 
			String cadena, 
			String usuario) throws SystemException;

}