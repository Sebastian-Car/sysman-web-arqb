package com.sysman.precontractual.ejb;

import com.sysman.exception.SystemException;

import java.math.BigInteger;
import java.util.Date;

import javax.ejb.Local;

@Local
public interface EjbPrecontractualUnoLocal {

    String actualizarFormulas(
        String compania,
        String campo,
        String valor,
        String tipocontrato,
        BigInteger consecutivo,
        String usuario)
                    throws SystemException;

    void copiarDatosEstudioPrevio(String compania, long codEstudio,
        String usuario, long consecutivo) throws SystemException;

    void insertProponentesEtapas(
        String compania, String tipoContrato, String transaccion,
        long consecutivo, String proponente, String sucursal,
        boolean cotizaInventario) throws SystemException;

    boolean cambiarEstudioPrevio(String compania, String tipoContrato,
        long transaccion, BigInteger estudioPrevio, String usuario)
                    throws SystemException;

    void actualizarEstudioPrevio(String compania, String tipoContrato,
        long transaccion, BigInteger estudioPrevio, String usuario)
                    throws SystemException;

    void actualizarInfoProponentes(String compania, String tipoContrato,
        long transaccion, int consecActual, int consecSiguiente, String usuario)
                    throws SystemException;

    void crearDetallesProceso(
        String compania,
        String tipoContrato,
        long transaccion,
        Date fechaInicio,
        String usuario)
                    throws SystemException;

    void subirCodigosUnspsc(
        String compania,
        String cambios,
        String usuario) throws SystemException;

    void insertarAcuerdos(
        String compania,
        long nroEstudio,
        String tipoContrato,
        String usuario) throws SystemException;

    void crearRiesgoPorDefecto(
        String compania,
        int tRiesgo,
        String usuario)
                    throws SystemException;
    
    void cambiarNumeroCertificado(
    		String compania, 
    		String usuario,
            long numeroCertificado, 
            long numeroNuevo)
                        throws SystemException;
}
