package com.sysman.almacen.ejb;

import com.sysman.exception.SystemException;

import java.util.Date;

import javax.ejb.Remote;

@Remote
public interface EjbPrestamosCeroRemote {
    void generarExclusion(
        String compania,
        Date fechaini,
        Date fechafin,
        String elemento,
        int serie,
        int limpia)
                        throws SystemException;

    void generarUnicos(
        String compania,
        Date fechaini,
        Date fechafin,
        String elemento,
        int serie,
        String elementopad,
        int seriepad,
        int estodos)
                        throws SystemException;

    void generarFestivos(
        String compania,
        Date fechaini,
        Date fechafin,
        String elemento,
        int serie,
        String elementopad,
        int seriepad,
        int estodos)
                        throws SystemException;

    void generarUnDia(
        String compania,
        Date fechaini,
        Date fechafin,
        String elemento,
        int serie,
        String elementopad,
        int seriepad,
        int estodos)
                        throws SystemException;

    void generarTodosDias(
        String compania,
        Date fechaini,
        Date fechafin,
        String elemento,
        int serie,
        String elementopad,
        int seriepad,
        int estodos)
                        throws SystemException;

    int hacerPase(
        String compania,
        String tipoorigen,
        int codigoorigen,
        String tipodestino,
        Date fechapase)
                        throws SystemException;

    void generarPrestamos(
        String compania,
        Date fechaini,
        Date fechafin,
        String elemento,
        int serie,
        String elementopad,
        int seriepad)
                        throws SystemException;
}
