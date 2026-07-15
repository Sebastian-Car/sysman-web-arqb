package com.sysman.nomina.ejb;

import com.sysman.exception.SystemException;

import java.util.Date;

import javax.ejb.Local;

@Local
public interface EjbNominaSieteLocal
{

    String liquidarNomina(
        String compania,
        int proceso,
        String inicial,
        String finali,
        String rutina,
        int ano,
        int mes,
        int periodo,
        String usuario)
                    throws SystemException;

    void eliminarQuinquenio(
        String compania,
        int procesoNomina,
        Date fechaPagoQuin,
        int periodoNomina,
        int idEmpleado,
        String opcion,
        String usuario)
                    throws SystemException;

    void revisarConceptos(
        String compania,
        String usuario)
                    throws SystemException;

    boolean actParametroCertDian(
        String compania,
        int modulo,
        String usuario)
                    throws SystemException;

    String getPrepararPivotTodosDescuentos(
        String compania,
        int ano,
        int mes,
        int proceso,
        String periodo)
                    throws SystemException;

    void generarErroresCuentasContables(
        String compania,
        int anio,
        int mes,
        String periodo,
        String proceso,
        String usuario)
                    throws SystemException;

    String retiroMasivo(
        String compania,
        String tipo,
        Date fechaRetiro,
        Date fechaTermina,
        long estado,
        String usuario)
                    throws SystemException;

    void actualiarConcepUgpp(
        String compania,
        String usuario)
                    throws SystemException;

    void migrarACesantias(
        String compania,
        int proceso,
        int anio,
        int mes,
        int periodo,
        int conceptoCesantias,
        int conceptoInteres,
        String usuario)
                    throws SystemException;

    void migrarAHistoricos(
        String compania,
        int proceso,
        int anioInicial,
        int mesInicial,
        int periodoInicial,
        int anioFinal,
        int mesFinal,
        int periodoFinal,
        int conceptoCesantias,
        int conceptoInteres,
        int codigoCesantias,
        String usuario)
                    throws SystemException;

    void programarVacaciones(
        String compania,
        int anio,
        String usuario) throws SystemException;

}