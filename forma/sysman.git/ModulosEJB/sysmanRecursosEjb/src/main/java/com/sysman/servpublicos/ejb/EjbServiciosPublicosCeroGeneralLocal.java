package com.sysman.servpublicos.ejb;

import com.sysman.exception.SystemException;

import javax.ejb.Local;

@Local
public interface EjbServiciosPublicosCeroGeneralLocal {

    String prepararAnoPeriodoSiguiente(String compania, int ano, String periodo,
        String tipoRetorno, String frecuencia) throws SystemException;

    String prepararCritica(String compania, int modulo, int ciclo,
        String strcodigoini, String strcodigofin, String consumoMenor,
        int ano, int periodo, double porcMenor, double porcMayor,
        boolean normales, boolean manual,
        boolean iguales, boolean desviacion, String usuario, boolean reporte)
                    throws SystemException;

    boolean generarMicroconsumos(String compania, int ciclo, String strperiodo,
        int intano) throws SystemException;

    boolean autorizarMicromedicion(String compania, String nit)
                    throws SystemException;

}
