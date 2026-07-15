package com.sysman.plusvalia.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.ejb.Local;

@Local
public interface EjbPlusvaliaCeroLocal {

    void plusvaliaCargarPlantilla(
        String cargarPlantilla,
        String compania,
        int proyecto,
        String usuario)
                    throws SystemException;

    String procesoFacturacionCopia(
        String compania,
        long idProyecto,
        long beneficiarioInicial,
        long beneficiarioFinal,
        BigInteger numeroActo,
        int reclasificar,
        long proceso,
        String usuario)
                    throws SystemException;

    String calculoPlusvalia(
        String compania,
        long idProyecto,
        long beneficiario,
        int reclasificar,
        long proceso,
        String usuario) throws SystemException;

    BigDecimal calcularAcuerdos(
        String compania,
        long idProyecto,
        long beneficiario,
        int acuerdo) throws SystemException;

    String procesoFacturacion(String compania,
        long idProyecto,
        long beneficiarioInicial,
        long beneficiarioFinal,
        int etapa,
        String usuario) throws SystemException;

    String crearAcuerdo(
        String compania,
        long idProyecto,
        long idFactura,
        long idBeneficiario,
        BigDecimal cuotainicial,
        int numerocuota,
        BigDecimal interesacuerdo,
        String resolucion,
        String modelointeresdeuda,
        String usuario,
        boolean preliquidar)
                    throws SystemException;

    BigDecimal prorrateoGeneral(
        long acuerdo,
        int cuotaFinal,
        String usuario)
                    throws SystemException;

}