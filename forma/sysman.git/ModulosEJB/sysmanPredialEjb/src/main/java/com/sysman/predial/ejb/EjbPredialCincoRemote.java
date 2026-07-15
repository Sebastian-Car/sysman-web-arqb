package com.sysman.predial.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Remote;

@Remote
public interface EjbPredialCincoRemote {
    void actualizarAbonoPrioridad(
        String compania,
        String numeroOrden,
        String acuerdo,
        String predio,
        long cuota,
        long valorcuota,
        long valorabono,
        String strtipo,
        long numcuotas)
                    throws SystemException;

    void distribuirAbonoEnAcuerdo(
        String compania,
        String acuerdo,
        String predio,
        long cuota,
        String strtipo,
        long numcuotas)
                    throws SystemException;

    void totalizarAbonoEnAcuerdo(
        String compania,
        String acuerdo,
        String predio)
                    throws SystemException;

    String imprimirFactAbonoEnAcuerdo(
        String numeroOrden,
        String acuerdo,
        String predio,
        long cuota,
        String strtipo,
        boolean anulacion,
        String usuario)
                    throws SystemException;

    void facturarAbonoEnAcuerdo(
        String numeroOrden,
        String acuerdo,
        String predio,
        long cuota,
        BigDecimal valorCuota,
        BigDecimal valoRabono,
        String strtipo,
        boolean anulacion,
        boolean facturar,
        String usuario)
                    throws SystemException;

    void planoMoroso(
        String compania,
        BigDecimal valor,
        Date fechaCorte,
        boolean sinCedula,
        String nombreCompania)
                    throws SystemException;

    String importarAsobancaria(String compania, BigDecimal tamanio,
        String cadena, int rango, String usuario, String numeroOrden,
        String modulo, String codigoBanco, String nombreArchivo)
                    throws SystemException;

    String establecerTablaAsobancaria(String compania, String usuario,
        BigDecimal factMultiplicacion, int linea, String lineaArchivo)
                    throws SystemException;

    String validarFacturaMF(String compania, String noFactura, String fechaPago,
        String numeroOrden, String modulo, String usuario)
                    throws SystemException;
}
