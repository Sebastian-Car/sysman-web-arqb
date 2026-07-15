package com.sysman.precontractual.ejb;

import java.util.Date;

import javax.ejb.Local;

import com.sysman.exception.SystemException;

@Local
public interface EjbPrecontractualCeroLocal {

    void actualizarVariablesPrponentes(
        String compania,
        String usuario)
                    throws SystemException;

    void actualizarVariables(
        String compania,
        String usuario)
                    throws SystemException;

    String actualizarTransaccion(
        String compania,
        String tipocontrato,
        long consecutivo,
        String estado,
        String observacion,
        String usuario)
                    throws SystemException;

    void actualizarEtapas(
        String compania,
        String tipocontrato,
        String usuario)
                    throws SystemException;

    Date adicionarDias(
        String compania,
        Date fechainicial,
        int numdias,
        boolean tipo)
                    throws SystemException;

    boolean evaluarFestivo(
        String compania,
        Date fecha)
                    throws SystemException;

    void actualizarVariables(
        String compania,
        String tipocontrato,
        String usuario)
                    throws SystemException;

    String convertirVariables(
        String variable)
                    throws SystemException;
}