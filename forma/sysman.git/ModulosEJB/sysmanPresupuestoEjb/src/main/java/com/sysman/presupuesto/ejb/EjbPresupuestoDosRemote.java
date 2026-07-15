package com.sysman.presupuesto.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.ejb.Remote;

@Remote
public interface EjbPresupuestoDosRemote {

    boolean actualizarRubrosAsociadosEnContratacion(
        String compania,
        int ano,
        String tipocomprobante,
        BigInteger numerocomprobante,
        String tipocontrato,
        long numerocontrato)
                    throws SystemException;

    void afectarOtroComprobantePresupuestal(
        String compania,
        int modulo,
        int ano,
        int ano0,
        String tipo0,
        String tipo,
        BigInteger numero,
        String cuenta,
        BigDecimal creditoa,
        BigDecimal contracreditoa,
        BigDecimal credito,
        BigDecimal contracredito,
        int consecutivo,
        int consecutivoppto,
        String con,
        BigInteger numero0)
                    throws SystemException;

    String liberarComprobantePresupuestal(
        String compania,
        int modulo,
        int ano,
        String tipo,
        BigInteger numero,
        int consecutivo,
        BigDecimal valorneto,
        Date fecha,
        String cuenta,
        String tercero,
        String sucursal,
        String centroCosto,
        String auxiliar,
        String fuenteRecursos,
        String referencia,
        String naturaleza,
        String descripcionUsuario,
        String usuario)
                    throws SystemException;

    boolean modificarAuxiliaresEnDetallesPresupuestales(
        String compania,
        int modulo,
        int ano,
        String tipo,
        BigInteger comprobante,
        String terceroa,
        String terceron,
        String sucursala,
        String sucursaln,
        String descripciona,
        String descripcionn,
        String numerodoca,
        String numerodocn,
        String referenciaa,
        String referencian,
        String auxiliara,
        String auxiliarn)
                    throws SystemException;

    void cuadrarSaldosPpto(
        String compania,
        int mesinicial,
        int mesfinal,
        int anio)
                    throws SystemException;

    void mayorizarPacApropiado(
        String compania,
        int anio,
        String codigo)
                    throws SystemException;

    String verificarSaldoDisponible(
        String clase,
        String compania,
        int anio,
        Date fecha,
        String codigo,
        String tercero,
        String sucursal,
        String auxiliar,
        String centro,
        String referencia,
        String fuenterecurso,
        BigDecimal debitoant,
        BigDecimal creditoant,
        BigDecimal debito,
        BigDecimal credito,
        BigDecimal valor)
                    throws SystemException;

    void agregarRubrosInferiores(
        String compania,
        int anio,
        String codigo,
        String cuentaFinal)
                    throws SystemException;

    long generarRegistroConTipo(String compania,
        int anio,
        String tipo,
        Date fechaComprobante,
        String descripcionComprobante,
        String objeto,
        String documentoComprobante,
        String vlrDocumentoComprobante,
        String tercero,
        String sucursal,
        BigDecimal debitoComprobante,
        BigDecimal creditoComprobante,
        BigDecimal abonadoComprobante,
        String creadorComprobante,
        String destinoComprobante,
        String tipoComprobante,
        long numeroComprobante)
                    throws SystemException;

    boolean diferenciaValidaVA(
        String compania,
        int anio,
        String tipoCpte,
        BigInteger comprobante,
        String claseComprobante)
                    throws SystemException;

    String afectarCptesDesdeSolicitudes(
        String compania,
        int ano,
        Date fecha,
        String tipot,
        String usuario,
        String tipoCpteAfect,
        BigInteger cmpteAfectado,
        String dependencia,
        BigInteger novedad,
        String claset,
        String vlrDocumento,
        String debito,
        String cargo,
        String codProyecto,
        String descripcion,
        String tipoCpte,
        BigInteger numeroCpte,
        String destino)
                    throws SystemException;

}
