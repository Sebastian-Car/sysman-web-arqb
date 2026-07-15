package com.sysman.predial.ejb;

import com.sysman.exception.SystemException;

import javax.ejb.Remote;

@Remote
public interface EjbPredialSieteRemote {

    boolean importarIgacMeses(
        String compania,
        int anio,
        String usuario,
        String numeroorden)
                    throws SystemException;

    String importarIgacTipoDos(
        String compania,
        String usuario)
                    throws SystemException;

    void prepararTipoMes(
        String compania)
                    throws SystemException;

    void prepararTipoDos(
        String compania)
                    throws SystemException;

    String actualizarNotificaPredial(
        String compania,
        String codigoinicial,
        String codigofinal,
        String orden,
        String usuario,
        String aniosdeuda,
        int deudas)
                    throws SystemException;

    String eliminarAcuerdoDePago(
        String compania,
        String codigoacuerdo,
        String usuario,
        String codpredio,
        int preanioi,
        int preanio,
        String numeroorden,
        String concuotascanc,
        String eliminarcuotcanc,
        String aplicadscesp,
        String leydescesp,
        int vigenciasaldo)
                    throws SystemException;

    void insertarResolucionesIgacMes(String compania, String usuario,
        int factorDeMultiplicacion, long lineas, String lineaArchivo,
        int codigoPais) throws SystemException;

    boolean actualizarIgacMes(String compania, String plano,
        long longitudArchivo, int anio, String usuario, int codigoPais)
                    throws SystemException;

    void anularCertificadoCatastral(String compania, String certificado,
        String usuario) throws SystemException;

    int prepararConceptoAnio(String compania, int anioFinal,
        int anioInicial, String usuario) throws SystemException;

    String actualizarIgacDos(String compania, String plano,
        long longitudArchivo, String departamento, String pais,
        String municipio, String usuario) throws SystemException;
}
