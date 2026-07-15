/*-
 * EjbPredialOchoLocal.java
 *
 * 1.0
 * 
 * 27/06/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.predial.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.ejb.Local;

@Local
public interface EjbPredialOchoLocal {

    boolean anularPrescripcion(
        String compania,
        String codigo,
        String numeroOrden,
        Date fechaanterior,
        String resolucionant,
        String usuario,
        String observacion,
        String resolucion,
        int prescripcion)
                    throws SystemException;

    boolean insertarRegistroIgacUno(String compania, String cadena,
        int anoFecha, String usuario, String codigoPais, String departamento,
        String municipio, boolean indTotal, String nombreCompania)
                    throws SystemException;

    int actualizarAporteRecibo(String compania, String usuario, String concepto,
        BigDecimal valorAporte, BigDecimal preval, BigInteger proyecto,
        String numeroOrden, String factura, boolean indAporte)
                    throws SystemException;

    boolean validarConfigTarifas(boolean indTipoEstrato, String tipoInicial,
        String tipoFinal, boolean indEstrato, String estratoInicial,
        String estratoFinal) throws SystemException;

    boolean aplicarTodasCfgTarifas(String compania, String usuario)
                    throws SystemException;

    boolean aplicarConfigTarifa(String compania, BigInteger id, String usuario)
                    throws SystemException;

    void exencionDeInteresPorDuplicidad(String compania, String numeroOrden,
        String predio, int anoInicial, int anoFinal, String resolucion,
        String elaboradoPor, String firmadoPor, String fechaResolucion,
        boolean tipoExencion,
        String usuario) throws SystemException;

    String registrarCertificado(String compania, String formulario,
        String predio, String direccion, String nombre, String cedula,
        String numOrden, int valor, String sucursal, String usuario,
        String recibo, String destino, String banco, Date fecha, int anio)
                    throws SystemException;

    int prepararPeriodo(String compania, String usuario, int ano, int mes,
        String periodoBase, String numeroOrden) throws SystemException;

    void registrarPagosWeb(String compania, String referencia, String predio,
        String numeroOrden, String pys, String inscrito, String nitInscrito,
        String usuario, String eliminar) throws SystemException;

    long actualizarIncrAutoavaluo(String compania, String numeroOrden,
        String usuario)
                    throws SystemException;

    boolean generarAutoavaluo(String compania, String numeroOrden,
        String usuario) throws SystemException;

    int registrarPagosDobles(String usuario, String compania,
        String numeroOrden, String codigo, String factura, String pagodBanco,
        String pagodPaquete, int pagodAno, Date pagodFecha)
                    throws SystemException;

    boolean actualizarIndicadorActivo(String compania, String tipo,
        boolean indActivo, String usuario) throws SystemException;

    boolean validarConsecutivo(String compania, BigInteger secuencia,
        String consecutivo, String tipo) throws SystemException;

    String obtenerEncabezadoColumna(String compania, String nit, int codigo,
        int anio) throws SystemException;

    String prepararListadoGeneral(String compania, boolean clave,
        String palabraClave, boolean nombres, String nombreInicial,
        String nombreFinal, String direccionInicial, String direccionFinal,
        boolean indicador, boolean soloPredios, int ordenado)
                    throws SystemException;

    String generaPlanoFacturacion(String compania, String codigoInicial,
        String codigoFinal, String nombreInicial, String nombreFinal,
        int anioInicial, int anioFinal, int hastaAnio, String nitInicial,
        String nitFinal, String numeroOrden, String orden,
        BigDecimal valorInferior, BigDecimal valorSuperior)
                    throws SystemException;

    boolean agregarNuevoCodigoRuta(String compania, BigInteger numero,
        String claseSolicitud, BigInteger numeroNuevo, String codigoRutaNuevo,
        String usuario) throws SystemException;

    BigDecimal obtenerValorDescuentoEspecial(String compania, String predio,
        String numeroOrden) throws SystemException;

    BigDecimal obtenerValorPorcDctoEspecial(String compania, String predio,
        Date fechaEv) throws SystemException;

    String obtenerMsjeDesctoEspecial(String compania, String predio,
        Date fechaEv) throws SystemException;

    String generarPlanoFacturacionAsoBanNoventaOcho(String compania,
        String codigoInicial, String codigoFinal) throws SystemException;

    String generarPlanoPuntos(String compania, String numeroOrden,
        String codigoInicial, String codigoFinal) throws SystemException;

    String consultarNombreCopropietario(String compania, String codigoPred,
        String numeroOrden) throws SystemException;

    String consultarAniosPagos(String compania, String codigo,
        String numeroOrden) throws SystemException;

    long calcularUsuariosFacturaEnLote(String compania, String numeroOrden,
        String codigoInicial, String codigoFinal, String direccionInicial,
        String direccionFinal, String nombreInicial, String nombreFinal,
        String nitInicial, String nitFinal, int anioInicial, int anioFinal,
        BigDecimal valorInferior, BigDecimal valorSuperior, int hastaAno,
        String tipoPredio) throws SystemException;

    String facturarEnLote(String compania, String nitcompania,
        String siglaCompania, String numeroOrden, String codigoInicial,
        String codigoFinal, String direccionInicial, String direccionFinal,
        String nombreInicial, String nombreFinal, String nitInicial,
        String nitFinal, int anioInicial, int anioFinal,
        BigDecimal valorInferior, BigDecimal valorSuperior, int hastaAno,
        Date fechaLimite,
        String tipoPredio,
        String usuario)
                    throws SystemException;

    boolean validaPago(String compania, String numeroOrden, String paquete,
        String pagoBanco, Date prefechaVar, String codigo, String numFactura,
        String usuario) throws SystemException;

    void actualizarAntesPagosDobles(String compania, String valor,
        String codigo, String numeroOrden, int anioExc) throws SystemException;
}