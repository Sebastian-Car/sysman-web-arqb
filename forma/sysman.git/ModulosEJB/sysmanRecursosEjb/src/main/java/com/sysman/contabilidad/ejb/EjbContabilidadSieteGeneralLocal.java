package com.sysman.contabilidad.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.Local;

@Local
public interface EjbContabilidadSieteGeneralLocal {

    String generarPlanoH(String compania, String fechainicial,
        String fechafinal, String fuenteinicial, String fuentefinal,
        String chequeInicial,String chequeFinal, String comprobanteInicial,String comprobanteFinal)
                    throws SystemException;

    String generarPlanoH2018(String compania, String fechainicial,
        String fechafinal, String fuenteinicial, String fuentefinal)
                    throws SystemException;

    String generarPlanoHAdi(String compania, Date fechainicial, Date fechafinal,
        String fuenteinicial, String fuentefinal) throws SystemException;

    String generarPlanoHNomina(String compania, String fechainicial,
        String fechafinal, String fuenteinicial, String fuentefinal)
                    throws SystemException;
    
    void actualizarAbono(String compania, int ano, String tipoCpte,
            BigInteger comprobante, int consecutivo, BigDecimal abono,
            Date fechaabono, String usuario) throws SystemException, ParseException;

}
