/*-
 * EjbAlmacenCincoRemote.java
 *
 * 1.0
 * 
 * 19/07/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.almacen.ejb;

import com.sysman.exception.SystemException;

import java.math.BigInteger;
import java.util.Date;

import javax.ejb.Remote;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 19/07/2018
 * @author ybecerra
 *
 */
@Remote
public interface EjbAlmacenCincoRemote {
    String calcularDepreciacionHNiif(
        String compania,
        int anoInicial,
        int mesInicial,
        int anoFinal,
        int mesFinal,
        String strElementoInicial,
        String strElementoFinal,
        long placaInicial,
        long placaFinal) throws SystemException;

    String generarPlantComponentes(
        String compania,
        String estacion,
        String bodega,
        String responsable,
        boolean informe,
        String dependencia) throws SystemException;

    void clasificacionMaterialComponentes(
        String compania,
        String usuario) throws SystemException;

    void copiarDeMovimiento(
        String compania,
        String tipoMov,
        BigInteger numero,
        String tipoMovCop,
        BigInteger numeroCop,
        Date fecha,
        String tercero,
        String sucursal,
        String auxiliar,
        String usuario) throws SystemException;

    void insertInventarioFisico(String compania,
        Date fechaLectura,
        String dependencia,
        String responsable,
        String sucursal,
        String referencia,
        String usuario) throws SystemException;

    void eliminarLoteAlmacen(String compania,
        String tipomovimiento,
        long movimiento) throws SystemException;

    void copiarPlacasLote(String compania, String tipomovimiento,
        long movimiento, String dependencia, String responsable,
        String sucursal, String clase, Date fecha, String usuario)
                    throws SystemException;
    
    String plantillaExcelIDCBIS(
			String compania, 
			String tipomovimiento, 
			long numero) throws SystemException;
    
    void actCantidadAfectada(
	        String compania,
	        String tipoMov,
	        BigInteger numero,
	        Double cantidad,
	        String elemento,
	        long serie,
	        String consecutivoAfect,
	        Double cantAnterior,
	        String accion) throws SystemException;
    
    void saldosInventBodega(
    		String compania,
            String tipoMov,
            long movimiento,
            String elemento,
            String bodegaO,
            String bodegaD,
            String fuenteR,
            String referencia,
            String auxiliar,
            String proyecto,
            String centroCosto,
            String lote,
            Double cantidad,
            Double cantAnterior,
            String accion,
            String usuario)
	                    throws SystemException;
    
    String generarCausacion(
			String compania, 
			String tipomovimiento, 
			long numero,
            String usuario) throws SystemException;
    
	void depreciacionAcumulada(
			String compania, 
			String elemento, 
			String serie, 
			int mes, 
			int anio, 
			String accion,
			String usuario) throws SystemException;
	
	void depreciacionAcumuladaInicial(
			String compania, 
			String elemento, 
			String serie, 
			int mes, int 
			anio, String accion,
			String usuario) throws SystemException;
	
	String cargarDepreciacionInicial(
			String compania, 
			String cadena, 
			String usuario) throws SystemException;
    void cargarElemConsumoFisico(
            String compania,
            String bodega,
            int ano,
            Date fechaCorte,
            String usuario
    ) throws SystemException;

	String aplicarAjusteInventario(
			String compania, 
			int anio, 
			String bodegaOrigen, 
			String bodegaDestino,
			String tipoCredito, 
			String tipoDebito, 
			String dependencia, 
			String respOrigen, 
			String sucursalResponsable,
			String respDestino,
			String fuenteRecurso, 
			String referencia, 
			String auxiliar, 
			String centroCosto, 
			String observaciones,
			String fechaCorte, 
			String bodegaDebito, 
			String bodegaCredito, 
			String usuario
		) throws SystemException;
	
	void depreciacionInicial(
			String compania, 
			String elemento, 
			String serie, 
			int mes, int 
			anio, 
			String usuario) throws SystemException;
	
	String crearElementoCompania(
			String compania, 
			String elemento,
        	String usuario) throws SystemException;	
	
	void ubicacionFisica(
			String accion,
			int idHistorial,
			String compania, 
			String elemento, 
			String serie, 
			Date fecha, 
			String ubicacion,
			String observaciones,
			String usuario) throws SystemException;
	
	String cargarDepreciacionNoCalculada(
			String compania, 
			String cadena, 
			String usuario) throws SystemException;
	
	String crearMovimientoCompania(
			String compania,
			String tipoMov, 
			long movimiento,
			int anio,
        	String usuario) throws SystemException;
}
