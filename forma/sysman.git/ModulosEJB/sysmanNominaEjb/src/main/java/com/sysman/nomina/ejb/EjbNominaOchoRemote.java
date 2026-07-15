package com.sysman.nomina.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.ejb.Remote;

@Remote
public interface EjbNominaOchoRemote
{

    void identificarNovedadesAntesDeLiquidar(
        String compania,
        int proceso,
        int ano,
        int mes,
        int periodo,
        String ini,
        String fin,
        String usuario)
                    throws SystemException;

    boolean calcularNetos(
        String compania,
        int proceso,
        int anioFinal,
        int mesFinal,
        int periodoFin,
        boolean indicador,
        String usuario)
                    throws SystemException;

    String generarInformacionSiif(
        String compania,
        int anioInicial,
        int mesInicial,
        int periodoInicial,
        int anioFinal,
        int mesFinal,
        int periodoFinal,
        Date fecha,
        String tipoDoc,
        String numeroDoc,
        String codigoExp,
        String texto,
        String nivel,
        int esBeneficio) throws SystemException;

    String informacionProvisiones(
        String compania,
        int anioIni,
        int mesIni,
        int periodoIni,
        int anioFin,
        int mesFin,
        int periodoFin,
        String nivel,
        Date fechaSolicit,
        String docSoporte,
        String numSoporte,
        String codExpedidor,
        String txtJustif) throws SystemException;

    String generarInformacionSiifPatronal(
        String compania,
        int anioInicial,
        int mesInicial,
        int periodoInicial,
        int anioFinal,
        int mesFinal,
        int periodoFinal,
        Date fecha,
        String tipoDoc,
        String numeroDoc,
        String codigoExp,
        String texto,
        String nivel,
        String usuario)
                    throws SystemException;

    String cargarExcelOccred(
        String compania,
        int proceso,
        int anio,
        int mes,
        int periodo,
        String banco,
        String consecutivo,
        Date fechareporte,
        boolean version,
        int informe,
        String comprobante) throws SystemException;

    String planoContabilizarNomina(
        String compania,
        int opcionPlano,
        int proceso,
        int ano,
        int mes,
        int periodo,
        Date fechaInter,
        String tipoComprobante,
        BigInteger numeroComprobante,
        boolean manejaCentroCosto,
        boolean auxTercero,
        BigDecimal porContribucion,
        String cuentadbt,
        String cuentacrd,
        boolean unicocomprobante,
        boolean porPeriodo,
        String usuario) throws SystemException;
    
    public String actualizaActividadesCiiu(
            String compania,
            String actividadesCIIU,
            String usuario
            ) throws SystemException;
}