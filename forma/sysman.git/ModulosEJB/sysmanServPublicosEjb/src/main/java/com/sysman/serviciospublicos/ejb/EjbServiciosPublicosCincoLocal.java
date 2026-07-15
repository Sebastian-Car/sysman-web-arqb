package com.sysman.serviciospublicos.ejb;

import com.sysman.exception.SystemException;

import java.util.Date;

import javax.ejb.Local;

@Local
public interface EjbServiciosPublicosCincoLocal {

    int obtenerMicromedicion(
        String compania,
        int ciclo,
        String codigoruta,
        int anio,
        String periodo,
        String momento,
        String usuario)
                    throws SystemException;

    String armarPlanoMultiusuarios(
        String compania,
        String codigoruta,
        int anio,
        String periodo,
        int numeroMultiusuarios)
                    throws SystemException;

    String armarPlanoMultiusuarios720(
        String compania,
        String codigoruta,
        int anio,
        String periodo)
                    throws SystemException;

    int obtenerNumeroMultiusuarios(
        String compania,
        String codigoruta)
                    throws SystemException;

    String armarParametrosPlanoFacSitio(
        String compania,
        String codigoinicial,
        String codigofinal,
        int ciclo,
        String observaciones,
        String bancodepago)
                    throws SystemException;

    String armarDetallesPlanoFacSitio(
        String compania,
        String codigoinicial,
        String codigofinal,
        int ciclo,
        Date fechavencimiento,
        String aforador,
        int critica,
        String publicidad,
        String condicionadicional,
        int limiteinferior,
        int limitesuperior,
        String usuario)
                    throws SystemException;

    boolean calcularProgresividad(
        String compania,
        int anio,
        String periodo,
        boolean solometa,
        String uso,
        String estrato)
                    throws SystemException;

    boolean calcularTarifas720(
        String compania,
        int anio,
        String periodo)
                    throws SystemException;

    void actualizacionDesvioSignificativo(
        String compania,
        int ciclo,
        String codigoinicial,
        String codigofinal,
        String usuario,
        int anio,
        String periodo, String nitCompania)
                    throws SystemException;

    void incrementarTarifas(
        String compania,
        int anosig,
        String periodosig,
        int ano,
        String periodo,
        boolean reescribir,
        String usuario)
                    throws SystemException;
}