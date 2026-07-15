package com.sysman.serviciospublicos.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;

import javax.ejb.Remote;

@Remote
public interface EjbServiciosPublicosSeisRemote {

    int generarActa(
        String compania,
        String codigoinicial,
        String codigofinal,
        int ciclo,
        String problemainicial,
        String problemafinal,
        int acta)
                    throws SystemException;

    boolean autorizarDescuentoTotal(
        String compania,
        String nit)
                    throws SystemException;

    boolean autorizarLimpiaDeshabilitados(
        String compania,
        String nit)
                    throws SystemException;

    String prepararSiguientePeriodo(
        String compania,
        int ciclo,
        String usuario,
        String nit,
        int anio,
        String periodo)
                    throws SystemException;

    String llamarPrepararSigPeriodo(
        String compania,
        int ciclo,
        int anio,
        String periodo,
        String sobreproduct,
        String sobredescuento,
        String nit,
        String usuario)
                    throws SystemException;

    void guardarHistoria(
        String compania,
        int ciclo,
        int anio,
        String periodo)
                    throws SystemException;

    void cerrarConsumosMicro(
        String compania,
        int ciclo,
        String usuario)
                    throws SystemException;

    void realizarCierrePersuasivo(
        String compania,
        int ciclo,
        String usuario)
                    throws SystemException;

    void guardarHistoricosFactura(
        String compania,
        int ciclo,
        int anio,
        String periodo)
                    throws SystemException;

    void auditarDesvio(
        String compania,
        String usuario,
        String proceso,
        int anio,
        String periodo,
        int ciclo,
        String codinterno,
        String descripcion)
                    throws SystemException;

    void sumarPesoAseo(
        String compania,
        int ciclo,
        int anio,
        String periodo)
                    throws SystemException;

    void revisarFacturadosAbonos(
        String compania,
        int ciclo,
        int anio,
        String periodo,
        String usuario)
                    throws SystemException;

    void verificarFacturadosNulos(
        String compania,
        int ciclo,
        int anio,
        String periodo,
        String usuario)
                    throws SystemException;

    void registrarEstadisticasTrifarias(
        String compania,
        int ciclo,
        int anio,
        String periodo, String usuario)
                    throws SystemException;

    void cuadrarEstratos(
        String compania,
        int ciclo,
        int anio,
        String periodo,
        String usuario)
                    throws SystemException;

    void discriminarRes688(
        String compania,
        int ciclo,
        String periodo,
        int anio,
        String usuario)
                    throws SystemException;

    void actualizarEstadisticaFacturacion(
        String compania,
        int ciclo,
        String usuario)
                    throws SystemException;

    void actualizarEstadisticasRecaudo(
        String compania,
        int ciclo,
        String usuario)
                    throws SystemException;

    void verificarFacturadosNulos(
        String compania,
        int ciclo,
        String servicio,
        String rango,
        BigDecimal limiteinferior,
        BigDecimal limitesuperior,
        String condicion,
        String condicion1,
        String calsuspendidos,
        boolean conmanuales,
        String sitio,
        BigDecimal pesoaseoestad,
        boolean aseores720,
        boolean parexcluirpnocobro)
                    throws SystemException;

    void actualizarEstadisticasConsumo(
        String compania,
        int ciclo, String usuario)
                    throws SystemException;

    void cerrarDescuentos(
        String compania,
        int ciclo,
        int anio,
        String periodo,
        String nit,
        String sobreescribir,
        String usuario)
                    throws SystemException;

    void cerrarProductividad(
        String compania,
        int ciclo,
        int anio,
        String periodo,
        String sobreescribir,
        String usuario)
                    throws SystemException;

    void insertarEstadistica(String compania, int ciclo, String servicio,
        String rango, BigDecimal limiteInferior, BigDecimal limiteSuperior,
        String condicion, String condicion1, String calSuspendidos,
        boolean conManuales, String sitio, BigDecimal pesoAseoEstado,
        boolean aseores720, boolean parExcluirPNoCobro, String usuario)
                    throws SystemException;
}