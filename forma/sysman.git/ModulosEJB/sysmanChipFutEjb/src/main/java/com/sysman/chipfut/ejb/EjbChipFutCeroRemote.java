package com.sysman.chipfut.ejb;

import com.sysman.exception.SystemException;

import javax.ejb.Remote;

@Remote
public interface EjbChipFutCeroRemote {

    void traerFuentesPresupuesto(
        String compania,
        int anio) throws SystemException;

    void traerCrearCodigosFut(
        String compania,
        int anio) throws SystemException;

    String generarPlanoSaldoMovimiento(
        String compania,
        int ano,
        int trimestre,
        String codigoentidad,
        int digitos,
        boolean excel,
        boolean miles,
        boolean centavos,
        boolean covid)
                    throws SystemException;

    String generarPlanoReciproco(
        String compania,
        int ano,
        int trimestre,
        String codigoEntidad,
        int digitos,
        boolean excel,
        boolean miles,
        boolean centavos,
        boolean cgn,
        boolean codEquiv) throws SystemException;

    String generarPlanoBalance(
        String compania,
        int ano,
        String codigoEntidad,
        String codigoInicial,
        String codigoFinal,
        int digitos,
        boolean excel,
        boolean miles,
        boolean centavos) throws SystemException;

    String generarPlanoGastosInv(
        String compania,
        int ano,
        boolean miles,
        int mesInicial,
        int mesFinal,
        int trimestre,
        String codigoEntidad,
        boolean excel,
        boolean sicep,
        String tipo) throws SystemException;

    void trasladarConfSiguienteAnio(
        String compania,
        int anio,
        String usuario) throws SystemException;

    String generarArchivoPlanoIngresosFut(
        String compania,
        int ano,
        String codigoEntidad,
        int mes,
        int trimestre,
        boolean transBancos,
        boolean miles,
        boolean excel) throws SystemException;

    int prepararAnio(
        String compania,
        int anio,
        int anioDestino) throws SystemException;

    String generarArchivoPlano2009BDME(String compania, int anio,
        String codigoentidad, int mes, String fecha) throws SystemException;
    
    String generarProcesoSiaSql(
    		String  strsql)
                        throws SystemException;
    String generarPlanoSaldoMovimientoMensual(
            String compania,
            int ano,
            int mes,
            String codigoentidad,
            int digitos,
            boolean excel,
            boolean miles,
            boolean centavos,
            boolean covid)
                        throws SystemException;
}