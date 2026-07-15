package com.sysman.contabilizar.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Local;

@Local
public interface EjbContabilizarAlmacenCeroLocal {

    String contabilizarAlmcnH(
        String companiaorigen,
        String companiadestino,
        Date fechinterf,
        String tipo,
        int numero,
        String tercero,
        String sucursal,
        String centrocosto,
        String usuario)
                    throws SystemException;

    String contabilizarAlmcnH(
        String compania,
        int ano,
        int mes,
        Date fechaInterf,
        String tipo,
        BigDecimal numero,
        String usuario)
                    throws SystemException;

    String contabilizarHNiveles(
        String compania,
        int ano,
        int mes,
        Date fechinterf,
        String tipo,
        BigDecimal numero,
        boolean niif,
        String usuario) throws SystemException;

    String contabilizarHNivelesCC(
        String compania,
        int ano,
        int mes,
        Date fechinterf,
        String tipo,
        BigDecimal numero,
        boolean niif,
        String usuario) throws SystemException;

    String contabilizarArmConsltHNvles(
        String compania,
        Date fechaInterf,
        boolean niif) throws SystemException;

    String contabilizarArmConsltHNvlesCC(
        String compania,
        Date fechaInterf,
        boolean niif) throws SystemException;

    String contabilizarRetiroActivos(
        String compania,
        int ano,
        int mes,
        Date fechinterf,
        String tipo,
        int numero,
        String usuario)
                    throws SystemException;

    void actualizarBodegaTipoActivo(
        String compania,
        String elemento,
        int ano,
        String centrocosto,
        String usuario)
                    throws SystemException;

    public String insertarComprobTransicion(
        String compania,
        int ano,
        int mes,
        String tipo,
        Date fecha,
        String usuario)
                    throws SystemException;

    void insertaAlmacenContabilidadCC(
    		String compania,
    		String codigoelemento,
    		String tipo,
    		String centrocosto,
    		String fuenterecurso,
    		int ano)
    			 throws SystemException;

    void insertaAlmacenContabilidad(
    		String compania,
    		String codigoelemento,
    		String tipo,
    		int ano)
    			 throws SystemException;
    
    void insertaAlmacenContabilidadFR(
			String compania, 
			String codigoelemento, 
			String tipo, 
			String fuenterecurso,
			int ano) throws SystemException;

}