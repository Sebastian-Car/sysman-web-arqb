package com.sysman.nomina.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Local;

@Local
public interface EjbNominaBancosLocal {

    String getDiscoDavivienda(
        String compania,
        int consecutivo,
        String codigoBanco,
        String codigoOficina,
        int proceso,
        int anio,
        int mes,
        int periodo,
        Date fecha)
                    throws SystemException;

    String getDiscoDaviviendaAuditoria(
        String compania,
        String codbanco,
        int proceso,
        int anio,
        int mes,
        int periodo)
                    throws SystemException;

    String getDiscoAuditoriaNuevo(
        String compania,
        String codbanco,
        int proceso,
        int anio,
        int mes,
        int periodo)
                    throws SystemException;

    String getMediosMagneticosDian(
        String compania,
        int idproceso,
        int ano1,
        int mes1,
        int periodo1,
        int ano2,
        int mes2,
        int periodo2,
        BigDecimal tope)
                    throws SystemException;

    String generarDiscoAvVillas(
        String compania,
        int proceso,
        int ano1,
        int mes1,
        String periodo1,
        String banco,
        Date fecha)
                    throws SystemException;

    String generarPlanoSudameris(
        String compania,
        int ano,
        int mes,
        String periodo,
        String banco,
        String descripciondeta,
        String descripcionpago,
        String descripcionampl)
                    throws SystemException;

    String generarPlanoBBogota(
        String compania,
        int ano,
        int mes,
        String periodo,
        Date fecha,
        String banco,
        String codigociudad,
        String conceptopago,
        String centrocosto)
                    throws SystemException;

    String generarPlanoPagoTerSudameris(String compania, int ano, int mes,
        String periodo, int proceso, String descripciondeta,
        String descripcionpago, String descripcionampl, int tipoPlano,
        String centroDeCosto) throws SystemException;

}