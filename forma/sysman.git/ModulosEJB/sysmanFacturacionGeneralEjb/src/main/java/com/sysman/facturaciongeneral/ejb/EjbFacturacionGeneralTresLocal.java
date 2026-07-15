package com.sysman.facturaciongeneral.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.ejb.Local;

@Local
public interface EjbFacturacionGeneralTresLocal {

    boolean interfazarFactura(
        String compania,
        String tipofactura,
        BigInteger nofactura,
        Date fechapago,
        boolean vermensaje,
        boolean manejainventario,
        String usuario)
                    throws SystemException;

    boolean manejarDerConexion(
        String compania,
        int ano,
        String tipocobro)
                    throws SystemException;

    boolean manejarInterfazaCntDerConexion(
        String compania,
        String tipo,
        BigInteger numero,
        Date fecha,
        String tercero,
        String sucursal,
        String descripcion,
        String usuario)
                    throws SystemException;

    boolean manejarInterfazContable(
        String compania,
        String tipo,
        BigInteger numero,
        Date fecha,
        String tercero,
        String sucursal,
        String descripcion,
        boolean manejainventario,
        String usuario)
                    throws SystemException;

    BigDecimal extraerSalarioMinimo(
        String compania,
        int ano) throws SystemException;

    BigDecimal reemplazarFormula(
        String formula) throws SystemException;

    boolean verificarDescuentoCobro(
        String compania,
        String tipofactura,
        BigInteger numerofactura) throws SystemException;

    String recaudarFactura(
        String compania,
        String tipoFactura,
        BigInteger numeroFactura,
        String observacion,
        String cuenta,
        Date fecha,
        int anio,
        boolean diferida,
        String usuario,
        BigDecimal valorRecaudo) throws SystemException;
    
    void actualizarTotalFacturaxTRM(
            String compania,
            String tipoFactura,
            BigInteger numeroFactura,
            BigDecimal valorFactura,
            BigDecimal valorTRM,
            int anio) throws SystemException;
    
    void actualizarSaldoActualConcepto(
            String compania,
            String tipoFactura,
            String concepto,
            BigInteger numeroContrato,
            BigDecimal saldoActual)
                    throws SystemException;
    
    BigDecimal obtenerSaldoConcepto(
    		String compania,
            String tipoFactura,
            String concepto,
            BigInteger numeroContrato)
                    throws SystemException;
    
    BigDecimal obtenerValorNetoAnterior(
    		String compania,
    		int anio,
            String tipoCobro,
            String concepto,
            BigInteger codigoCobro)
                    throws SystemException;
}