package com.sysman.contabilidad.ejb;

import com.sysman.exception.SystemException;

import java.util.Date;

import javax.ejb.Remote;

@Remote
public interface EjbContabilidadSeisGeneralRemote {

    String generarRtaConciliadosXPlano(
        String compania,
        String cuenta,
        String cadena,
        boolean eliminarpartidas,
        boolean maningpartidasconc,
        boolean validadoc,
        int colvalingreso,
        int colvalcredito,
        int coldocumento,
        int colerror,
        int colfecha,
        Date fecha,
        String formatofecha,
        int filaini,
        String usuario) throws SystemException;

    String generarRtaConciliadosXTipo(
        String compania,
        String cuenta,
        String cadena,
        int colValDebito,
        int colValCredito,
        int colTipo,
        int colComprobante,
        int colConsecutivo,
        int colFecha,
        Date fecha,
        int filaini,
        String usuario) throws SystemException;
}
