/*-
 * EjbServiciosPublicosOchoRemote.java
 *
 * 1.0
 *
 * 15/05/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.serviciospublicos.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.ejb.Remote;

@Remote
public interface EjbServiciosPublicosOchoRemote {

    boolean registrarActaFinanciable(
        String compania,
        String idacta,
        String clase,
        int ciclo,
        String codigoruta,
        int conceptoinicial,
        int conceptofinal,
        boolean impreso,
        String periodo,
        String usuario)
                    throws SystemException;

    void ajustarEstadoUsuario(String compania, String usuario)
                    throws SystemException;

    boolean borrarPaquetePago(String compania, String banco, Date fecha,
        String paquete) throws SystemException;

    void actualizarMetrosDesviacion(String compania, int ciclo,
        String codigoruta, int metros, boolean periodocobro, int ano,
        String periodo, String usuario) throws SystemException;

    String realizarAbonoCuotasFinanciable(String compania, int cuotas,
        int ciclo, String codigoruta, String periodo, int anio, String usuario,
        String consecutivo) throws SystemException;

    void reconstruccionDeRecaudosPorConcepto(String compania, Date fecha,
        String usuario) throws SystemException;

    boolean validarFinanciable(String compania,
        int ciclo, int ano, String periodo,
        boolean extra, String fimm, long lectura, String bancoperproceso,
        String periodosnocobrofac, Date fecha) throws SystemException;

    int asignarConceptosGrupoUsuarios(String compania, int ciclo,
        String codigoInicial, String codigoFinal, String periodo, int anio,
        String concepto, BigDecimal nuevoValorFact, String condicionUso,
        String condicionEst, String usuario) throws SystemException;

    void actualizaConsecutivos(String compania, int ciclo, String marca,
        String codigoInicial, String codigoFinal, Date fechaLimite1,
        Date fechaLimite2, String usuario) throws SystemException;

    boolean validarCodigoRuta(String compania, int ciclo, String codigoRuta)
                    throws SystemException;

    String crearParametroFacturacion(String compania, int ciclo, int anio,
        String periodo, int marca, String usuario) throws SystemException;

    int eliminarFinanciable(String compania, int ciclo, String codigoRuta,
        int ano, String periodo, String bancoPerProceso, int concepto,
        String usuario, String codigoInterno) throws SystemException;

    int eliminarFacturado(String compania, int ciclo, String codigoRuta,
        int ano, String periodo, String bancoPerproceso, int concepto,
        String usuario, String codigoInterno) throws SystemException;

    void actualizarConcepto(String compania, int ciclo, int anio,
        String periodo, String codigoRuta, String codigoInterno, int concepto,
        String usuario, BigDecimal deudaAnterior, BigDecimal deudaNueva,
        BigDecimal facturadoAnterior, BigDecimal facturadoNuevo)
                    throws SystemException;

    String prepararInformeSuspensiones(String compania, int cicloInicial,
        int cicloFinal, int abonos, int chapetas, int pqr, int periodoAtrasoIni,
        int periodoAtrasoFin, int condicion, BigDecimal valorSuperior,
        int ordenadoPor) throws SystemException;

    void actualizarDatosMultiusuarios(String compania, int ciclo,
        String codigoRuta, String usuario) throws SystemException;

    void generarOrdenDeTrabajo(String compania, BigInteger consecutivo,
        BigInteger numOrden, String usuario) throws SystemException;

    String cargarConsumosManYProm(String compania, int ciclo, String datosExcel,
        int ano, String periodo, String usuario, String strsql, String strlog,
        int numUsuError, int numUsuOk, boolean tipoConsumo)
                    throws SystemException;

    boolean actualizarMedidor(String compania, String auxiliar,
        String codigoRuta,
        int ciclo, String usuario)
                    throws SystemException;

    boolean agregarNuevoCodigoRuta(String compania, BigInteger numero,
        String claseSolicitud, BigInteger numeroNuevo, String codigoRutaNuevo,
        String usuario)
                    throws SystemException;

    String actualizarSubTotalElementos(String compania, int modulo,
        String usuario, String claseSolicitud, BigInteger solicitud)
                    throws SystemException;

    boolean actualizarMedidorEstado(String compania, String estado,
        String marca, String codigo, String usuario, int clase,
        int localizacion, int ciclo, String codigoRuta, int digitos)
                    throws SystemException;

    String obtenerAnioPeriodoActual(String compania, int ciclo)
                    throws SystemException;

}
