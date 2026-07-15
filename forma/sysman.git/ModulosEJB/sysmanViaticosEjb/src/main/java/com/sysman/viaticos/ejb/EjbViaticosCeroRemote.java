package com.sysman.viaticos.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.ejb.Remote;

@Remote
public interface EjbViaticosCeroRemote {

    void crearDetalleLegalizaViaticos(
        String compania,
        int ano,
        String tercero,
        String codSolicitud,
        String numero,
        String usuario)
                        throws SystemException;

    BigDecimal retornarTotalViaticos(
        String compania,
        int ano,
        String codPersonal,
        BigInteger codigoSolicitud,
        String funcionario,
        String concepto,
        String usuario) throws SystemException;

    void actualizarDetalleLegalizaViaticos(
        String compania,
        int ano,
        String numero,
        String numeroafectado,
        String concepto,
        String usuario,
        BigDecimal valor,
        BigDecimal valorAnterior) throws SystemException;
}