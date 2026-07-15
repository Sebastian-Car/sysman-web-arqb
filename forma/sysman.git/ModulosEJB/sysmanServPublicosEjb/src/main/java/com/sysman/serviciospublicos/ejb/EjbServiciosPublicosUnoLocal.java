package com.sysman.serviciospublicos.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Local;

@Local
public interface EjbServiciosPublicosUnoLocal {

    int obtenerMedidoresDudosos(
        String compania,
        int ciclo)
                    throws SystemException;

    boolean actualizarMedidores(
        String compania,
        int ciclo,
        String usuario)
                    throws SystemException;

    String validarNit(
        String compania,
        String nit)
                    throws SystemException;

    BigDecimal obtenerAutorizaciónPersuasivo(
        String compania,
        String nit)
                    throws SystemException;

    void cambiarRuta(
        String nueCompania,
        String antCompania,
        int nueCiclo,
        int antCiclo,
        String nueCodigoruta,
        String antCodigoruta,
        String usuario)
                    throws SystemException;

    String cambiarCiclo(
        String compania,
        int nueCiclo,
        String nueCodigoruta,
        int ano,
        String periodo,
        String antCompania,
        int antCiclo,
        String antCodigoruta, String usuario)
                    throws SystemException;

    void actualizarRangos(
        String compania,
        int ciclo)
                    throws SystemException;

    void reversarPorPaquete(
        String compania,
        Date fecha,
        String banco,
        String paquete)
                    throws SystemException;

    void reversarPorPaqueteConvenio(
        String compania,
        Date fecha,
        String banco,
        String paquete)
                    throws SystemException;

    void eliminarRecProdPaquete(
        String compania,
        Date fecha,
        String banco,
        String paquete)
                    throws SystemException;

    String borrarRecPagos(
        String compania,
        Date fecha,
        String banco,
        String paquete)
                    throws SystemException;

    boolean consultarPeriodosCerrados(
        String compania,
        Date fecha,
        String banco,
        String paquete)
                    throws SystemException;

    String realizarDevolucionDeCaja(
        String compania,
        String tipo,
        String clase,
        Date fecha,
        BigDecimal dblvalor,
        String usuario,
        String tipoanular)
                    throws SystemException;

    String borrarPagos(
        String compania,
        Date fecha,
        String banco,
        String numeropaquete,
        int ciclo,
        String codigoruta,
        String operacion,
        long consecutivo,
        String grupo,
        BigDecimal valorpago,
        String usuario)
                    throws SystemException;

    void eliminarRecProd(
        String compania,
        int ciclo,
        String usuario,
        Date fecha,
        String banco,
        String paquete,
        String operacion)
                    throws SystemException;
}