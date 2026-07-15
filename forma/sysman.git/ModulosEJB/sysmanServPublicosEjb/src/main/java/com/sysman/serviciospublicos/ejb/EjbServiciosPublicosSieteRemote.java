package com.sysman.serviciospublicos.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;

import javax.ejb.Remote;

@Remote
public interface EjbServiciosPublicosSieteRemote {

    String calcularFacturacion(
        String compania,
        int intciclo,
        String strcodigoinicial,
        String strcodigofinal,
        boolean enserie,
        boolean finall,
        String usuario)
                    throws SystemException;

    BigDecimal obtenerAjustePeso(
        String valor,
        BigDecimal ajustedecena,
        String redondeoporencima)
                    throws SystemException;

    BigDecimal obtenerCalculoDescuentoConcepto(
        String compania,
        String strusuario,
        int intciclo,
        int intano,
        String strperiodo,
        String totaseo)
                    throws SystemException;

    BigDecimal obtenerModificacionesFacturado(
        String compania,
        String codigoruta,
        int anio,
        String periodo,
        int intciclo)
                    throws SystemException;

    void distribuirFinanciableDeuda(
        String compania,
        String codigoruta,
        int anio,
        String periodo,
        int ciclo)
                    throws SystemException;

    void distribuirDeudaFinanciable12(
        String compania,
        String codigoruta,
        int anio,
        String periodo,
        int ciclo)
                    throws SystemException;

    BigDecimal calcularFacturacion(
        String compania,
        int ciclo,
        String codigoruta,
        int ano,
        String periodo,
        String notacredito,
        boolean facturado)
                    throws SystemException;

    void calcularFacturacion(
        String compania,
        int ano,
        String periodo,
        String codigoruta,
        int ciclo,
        String totfact)
                    throws SystemException;

    void registrarError(
        String compania,
        String codigoruta,
        int ciclo,
        int codigoerrorinterno,
        String mensaje)
                    throws SystemException;

    void actualizarRangos(String compania) throws SystemException;

    void actualizarEstadoMedidores(String compania, int ciclo,
        String codigoRuta, long medidor, String usuario) throws SystemException;
}
