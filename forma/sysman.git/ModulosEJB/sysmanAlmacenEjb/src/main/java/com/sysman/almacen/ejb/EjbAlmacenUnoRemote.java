package com.sysman.almacen.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.ejb.Remote;

@Remote
public interface EjbAlmacenUnoRemote {
    String prepararPivotDevolutivo(
        String compania,
        String codigoinicial,
        String codigofinal,
        String elementodesde,
        String elementohasta,
        String agrupacion)
                    throws SystemException;

    BigDecimal calcularAcumuladoInventario(
        String compania,
        String elemento,
        Date fecha,
        int opcion,
        int tipokardex,
        long serie)
                    throws SystemException;

    BigDecimal calcularDepreciacionAcumulado(
        String compania,
        String elementoinicialin,
        String elementofinal,
        int anio,
        int mes,
        String grupo,
        int par)
                    throws SystemException;

    void calcularValorPromedioMovimiento(
        String compania,
        String elementorect)
                    throws SystemException;

    int rectificarPromedios(
        String compania,
        String elementorect)
                    throws SystemException;

    String cargarMovimientoDocAsociado(
        String compania,
        int modulo,
        String clasedocasoc,
        String tipodocasoc,
        long nrodocasoc,
        String tipomov,
        BigInteger nromov,
        int frmmov,
        String proyecto,
        String fuenteR,
        String referencia,
        String auxiliar,
        String centroCosto,
        String bodega)
                    throws SystemException;

    String afectarMovimientoDocAsociado(
        String compania,
        int modulo,
        String clasedocasoc,
        String tipodocasoc,
        long nrodocasoc,
        String tipomov,
        long nromov,
        String usuario,
        String proyMov,
        String operMov,
        String auxMov,
        String ruedMov,
        String fuenteR,
        String referencia,
        String auxiliar,
        String centroCosto)
                    throws SystemException;

    long generarConsecutivoAlmacen(
        String compania,
        String tipomov,
        String clasemov,
        boolean invinicial,
        long nroinicial,
        int modulo,
        String tipoElemento)
                    throws SystemException;

	String cargarReexpVidaUtil(
			String compania, 
			String elemento, 
			long serie, 
			Date fecha, 
			String vidaUtil,
			String usuario) throws SystemException;
	
	String actualizarVlrSaldo(
	        String compania
	    )throws SystemException;
	
	void eliminarMovimientos(
			String compania, 
			int anio, 
			String tipomovimiento, 
			String movimiento)
			throws SystemException;
}
