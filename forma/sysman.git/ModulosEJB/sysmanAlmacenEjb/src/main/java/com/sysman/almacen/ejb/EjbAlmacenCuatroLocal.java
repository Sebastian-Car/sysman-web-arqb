package com.sysman.almacen.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.ejb.Local;

@Local
public interface EjbAlmacenCuatroLocal {

    long actualizarResponsable(
        String compania,
        String id,
        long seriePlaca,
        String codigoInventario,
        boolean predio)
                    throws SystemException;

    BigDecimal calcularVidaUtilRestante(
        String compania,
        long placa)
                    throws SystemException;

    String calcularDepreciacionInicial(
        String compania,
        String strelementoinicial,
        String strelementofinal,
        long placainicial,
        long placafinal)
                    throws SystemException;

    String calcularDepreciacionInicialNiif(
        String compania,
        String strelementoinicial,
        String strelementofinal,
        long placainicial,
        long placafinal)
                    throws SystemException;

    String verificarCuentaAlmacen(
        String compania,
        Date fechaInicial,
        Date fechaFinal)
                    throws SystemException;

    String calcularDepreciacionMensual(
        String compania,
        int anoinicial,
        int mesinicial,
        int anofinal,
        int mesfinal,
        String strelementoinicial,
        String strelementofinal,
        long placainicial,
        long placafinal)
                    throws SystemException;

    String calcularDepreciacionMensualNiif(
        String compania,
        int anoinicial,
        int mesinicial,
        int anofinal,
        int mesfinal,
        String strelementoinicial,
        String strelementofinal,
        long placainicial,
        long placafinal)
                    throws SystemException;

    boolean evaluarDepreciacionesPosteriores(
        String compania,
        int ano,
        int mes,
        String elementoinicial,
        String elementofinal,
        long placainicial,
        long placafinal)
                    throws SystemException;

    long actResponsablePredioVias(
        String compania,
        boolean predio)
                    throws SystemException;

    void registrarAdicionPredial(
        String compania,
        String seleccion,
        BigInteger serie,
        String elemento,
        Date fechamovimiento,
        BigInteger numeromovimiento,
        String tipomovimiento,
        String tipoinmueble,
        String valortotal,
        String ordena,
        int anioconstruccion,
        String sucursal,
        String descripcion,
        String usuario)
                    throws SystemException;

    BigDecimal actualizarDesdeExcel(String compania, String plano,
        String usuario)
                    throws SystemException;

    void cambiarNumeroMovimiento(String compania, String usuario,
        String tipoMovimiento, long numeroMovimiento, long numeroNuevo)
                    throws SystemException;

    boolean tieneMovimientosSinRegistrar(String compania, String tipoMovimiento,
        long numeroMovimiento) throws SystemException;

    String traerTipoFormato(String compania, String tipoMovimiento)
                    throws SystemException;

    String traerNombreDocumentoAsociado(String compania, String codDocumento)
                    throws SystemException;

    int actualizarResponsablePredioVias(String compania, boolean predio,
        String usuario) throws SystemException;

    int actualizarVidaUtilR(String compania, boolean predio, String usuario)
                    throws SystemException;

    String generarNumeroRequisiciones(
        String compania,
        String tipoMovimiento,
        long movimientoInicial,
        long movimientoFinal) throws SystemException;

    void subirInventarioInicial(
        String compania,
        String inventarios,
        String dependencias,
        String responsables,
        String ordenescompra,
        String claseorden,
        String usuario) throws SystemException;

    String generarMovInventarioInicial(
        String compania,
        String claseorden,
        String usuario) throws SystemException;

    int validarRequisicion(
        String compania,
        String dependencia,
        String tercero,
        int ano,
        String sucursal,
        String accion,
        String nuevoValorEstimado,
        String numero) throws SystemException;

    void actualizarIdentificadoresPlaca(
        String compania,
        String usuario) throws SystemException;

    void subirCambioPlaca(
        String compania,
        String cambios,
        String usuario,
        long cambio) throws SystemException;

    void cambiarPlacas(
        String compania,
        String usuario,
        long cambio,
        int opcion) throws SystemException;

    String validarInventarioInicial(
        String compania,
        String inventarios,
        String dependencias,
        String responsables,
        String ordenescompra) throws SystemException;

}