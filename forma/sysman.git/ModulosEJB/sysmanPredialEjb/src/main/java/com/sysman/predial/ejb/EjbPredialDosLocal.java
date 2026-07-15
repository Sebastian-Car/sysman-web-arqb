package com.sysman.predial.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.ejb.Local;

@Local
public interface EjbPredialDosLocal {

    String modificarRecibos(
        String compania,
        String modificacion,
        String tiporecibo,
        String tipomodificacion,
        Date fechamodificacion,
        String codigo,
        String factura,
        Date fecha,
        String banco,
        String paquete,
        BigDecimal valor,
        String anular,
        String activar,
        String campoanterior,
        String usuario)
                    throws SystemException;

    void corregirCupones(
        String compania,
        Date fecha,
        String banco,
        String paquete,
        String usuario)
                    throws SystemException;

    String modificarValores(
        String compania,
        String tiporecibo,
        String tipomodificacion,
        String banco,
        String codigo,
        String factura,
        Date fecha,
        BigDecimal valor,
        String paquete,
        Date prefec,
        String pagBan,
        String paqueters,
        String activar,
        String acuerdo,
        String ncAcuerdo,
        String usuario)
                    throws SystemException;

    String actualizarUltimaVigenciaCancelada(
        String compania,
        String codinicial,
        String codfinal, String modcod, String usuario, String descripcion)
                    throws SystemException;

    void actualizarCodigoAnterior(
        String compania,
        String codigoAnterior,
        String codigoNuevo)
                    throws SystemException;

    void realizarTrasladoNuevoCodigo(
        String compania,
        String codigoAnterior,
        String codigoNuevo,
        String usuario,
        boolean opcionRegistro)
                    throws SystemException;

    void registroDePagoVigenciaAnterior(
        String compania,
        String v50,
        String vDamnificados,
        String fechacorte,
        String codpredio,
        String codigobanco,
        int anofin,
        String nrorecibo,
        BigDecimal totalpagadoin,
        String tarifaap,
        String trpcod,
        String observacionesin,
        String usuario)
                    throws SystemException;

    void actualizarAvaluoAnterior(
        String compania,
        String resolucion,
        int preano,
        String codigo,
        int ultimoAnioin,
        BigDecimal avaluo,
        String trpcod)
                    throws SystemException;

    String insertarCopropietarios(
        String compania,
        String pais,
        String departamento,
        String municipio,
        String resolucion,
        BigInteger consecutivo,
        int ano,
        String codigo,
        String usuario)
                    throws SystemException;

    long insertarUsuariosResolucionIgac(
        String resolucion,
        String pais,
        String departamento,
        String municipio,
        int ano,
        String codigo,
        String usuario,
        String compania)
                    throws SystemException;

    String actualizarNumeroDeOrdenPredios(
        String compania,
        String codigo,
        String usuario)
                    throws SystemException;

    String insertarUsuariosDesdeResoluciones(
        String departamento,
        String municipio,
        String resolucion,
        String radicacion,
        String compania,
        String usuario,
        int pagoAno,
        BigDecimal pagVal,
        String pagBan,
        String numCom,
        String codpadre,
        String pagfec,
        BigDecimal trppor)
                    throws SystemException;

    void incrementarAvaluos(
        String compania,
        String codigo,
        String numeroOrden,
        BigDecimal avaluo,
        BigDecimal avaluoAno,
        int ano)
                    throws SystemException;

    String armaConsultaEstadoCuenta(String compania, String nit,
        String codPredio, String propietario, boolean porUsuario,
        boolean porPredio, String numOrden, String nombreCompania)
                    throws SystemException;
}