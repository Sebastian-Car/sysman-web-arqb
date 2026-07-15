package com.sysman.serviciospublicos.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Local;

@Local
public interface EjbServiciosPublicosTresLocal {

    void actualizarEstado(String compania, int ciclo, String codigoRuta,
        String estadoNuevo, String usuario, String codigoAuditoria)
                    throws SystemException;

    String auditoriaGeneral(
        String compania,
        String usuario,
        String macroproceso,
        String subproceso,
        int anio,
        String periodo,
        String codinterno,
        String descripcion)
                    throws SystemException;

    boolean validarConceptoFacturado(
        String compania,
        String codigoruta,
        int ciclo,
        int concepto,
        int ano,
        String periodo)
                    throws SystemException;

    void auditarModif(
        String compania,
        String formorigen,
        int intTipo,
        String campo,
        String usuario)
                    throws SystemException;

    void auditarRegistroComparar(
        String compania,
        String formorigen,
        String strcampo,
        String camposmodif,
        String usuario,
        int ciclo,
        String codigoruta,
        int anio,
        String periodo,
        int cont)
                    throws SystemException;

    void preparaExcluirCartera(
        String compania,
        int cicloinicial,
        int ciclofinal,
        String usuario)
                    throws SystemException;

    void precargarPromedios(
        String compania,
        String codigointerno,
        int ciclo,
        String codigoruta)
                    throws SystemException;

    void operarConsumoManual(
        String compania,
        int ciclo,
        String codigointerno,
        int opcion,
        BigDecimal consumo,
        String usuario)
                    throws SystemException;

    Date ultimoDia(
        Date fecha)
                    throws SystemException;

    void anularFinanciabledeDeuda(
        String compania,
        int ciclo,
        String codigoruta,
        int ano,
        String periodo,
        BigDecimal vlrafinanciar)
                    throws SystemException;

    boolean existePeriodo(
        String compania,
        int ano,
        String periodo)
                    throws SystemException;

    void discriminarFinanciacion(
        String compania,
        int ciclo,
        String codigoruta,
        int ano,
        String periodo,
        BigDecimal valorabono,
        int nrocuotas,
        BigDecimal vrafinanciar,
        String usuario,
        long consecutivo)
                    throws SystemException;

    boolean actualizaFinanciabledeDeuda(
        String compania,
        int ciclo,
        String codigoruta,
        int ano,
        String periodo,
        BigDecimal valorabono,
        BigDecimal vrafinanciar,
        int nrocuotas,
        String usuario,
        boolean sinabonoin,
        int pernocobro,
        long consecutivo)
                    throws SystemException;
}