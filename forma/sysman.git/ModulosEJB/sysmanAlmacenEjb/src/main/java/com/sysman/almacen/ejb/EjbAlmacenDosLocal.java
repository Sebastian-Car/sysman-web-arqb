package com.sysman.almacen.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.ejb.Local;

@Local
public interface EjbAlmacenDosLocal
{

    long generarConsecutivoMov(
        String tablauno,
        String tablados,
        String criterio,
        String campo)
                    throws SystemException;

    long generarConsecutivoDevolutivo(
        String tipomovimiento,
        String compania,
        String clase,
        int modulo)
                    throws SystemException;

    long cambiarTipoActivo(
        String compania,
        String tipomovimiento,
        BigInteger consecutivo,
        Date fecha,
        String usuario)
                    throws SystemException;

    boolean reversarDocumento(
        String compania,
        String tipomovasociado,
        long movasociado,
        String codigoelemento,
        BigDecimal cantidad)
                    throws SystemException;

    String consultarClaseBodega(
        String compania,
        String dependencia)
                    throws SystemException;

    String actualizarInventario(
        String strelement,
        BigDecimal dif,
        String nombrecampo,
        String strcompania)
                    throws SystemException;

    String consultarResponsableDep(
        String strdependencia,
        String compania)
                    throws SystemException;

    long actualizarDocAsociado(
        String compania,
        String tipomovimiento,
        long movimiento)
                    throws SystemException;

    void generarInventarioInicial(
        String compania,
        String tipoDocAsociado,
        long numeroDocAsociado,
        String tipoMovEntrada,
        String tipoMovSalida,
        String usuario) throws SystemException;

    String consultarVidaUtilPlaca(
        String compania,
        long placa)
                    throws SystemException;

    String generarComprobanteAlmacen(String compania, String claseOrden,
        long numero, String usuario, String dependencia,
        String recalcularDevolu) throws SystemException;

    boolean revisarPlacasGeneradas(String compania, String claseOrden,
        long numero, String usuario) throws SystemException;

}