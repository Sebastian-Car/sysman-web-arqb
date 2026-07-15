package com.sysman.contabilizar.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Remote;

@Remote
public interface EjbContabilizarNominaDosRemote
{

    void revisarCuentasPlancontable(
        String companiaNomina,
        String companiaDs,
        int ano,
        int mes,
        int periodo,
        int proceso,
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
        Date fechaInterf,
        String tipo,
        BigDecimal numero,
        boolean niif,
        String usuario) throws SystemException;

    String contabilizarHNivelesCC(
        String compania,
        int ano,
        int mes,
        Date fechaInterf,
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

}