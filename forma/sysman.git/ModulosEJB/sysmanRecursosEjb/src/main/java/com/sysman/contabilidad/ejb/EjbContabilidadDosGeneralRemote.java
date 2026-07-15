package com.sysman.contabilidad.ejb;

import com.sysman.exception.SystemException;

import javax.ejb.Remote;

@Remote
public interface EjbContabilidadDosGeneralRemote {

    String crearCompaniaNiifLotes(String compania, String companiaNiif)
                    throws SystemException;

    String contabilizarComprobantesContablesNiif(String compania,
        int mesInicial, int mesFinal, String tipoInicial, String tipoFinal,
        int anio, String usuario) throws SystemException;
}
