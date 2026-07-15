package com.sysman.nomina.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Local;

@Local
public interface EjbNominaCeroLocal {

    void desactivarPeriodo(
        String compania,
        int anio,
        int mes,
        String user)
                    throws SystemException;

    void actualizarPeriodAcumulable(
        String compania,
        int proceso,
        int anio,
        int mes,
        int periodo,
        boolean parametro,
        String user)
                    throws SystemException;

    BigDecimal getDiasPeriodo(
        String compania,
        int proceso,
        int anio,
        int mes,
        int periodo)
                    throws SystemException;

    Date getFechaAnioAnterior(
        Date fecha)
                    throws SystemException;

    String getUltimasVacaciones(
        String compania,
        int empleado,
        int parametro,
        int proceso,
        int ano,
        int mes,
        int periodo)
                    throws SystemException;

    Date getFechaIncapacidad(
        String compania,
        int empleado,
        int parametro,
        Date fechafin)
                    throws SystemException;

    Date getFechaIncapacidadAmbul(
        String compania,
        int empleado,
        int parametro,
        Date fechaini,
        Date fechafin)
                    throws SystemException;

    BigDecimal getPeriodosDeVacaciones(
        String compania,
        int empleado,
        int parametro,
        Date fechaini,
        Date fechafin)
                    throws SystemException;

    Date getFechaLicencia(
        String compania,
        int empleado,
        int parametro)
                    throws SystemException;

    Date getInicioFechaPeriodo(
        String compania,
        int proceso,
        int ano,
        int mes,
        int periodo)
                    throws SystemException;

    void duplicarFinanciable(
        String compania,
        int empleado,
        int factor,
        String user)
                    throws SystemException;

    Date getUltimaIncapacidad(
        String compania,
        int empleado,
        int parametro,
        Date fechafin)
                    throws SystemException;

    BigDecimal getCampoDeCesantias(
        String compania,
        int empleado,
        String parametro)
                    throws SystemException;

    void insertarNovedad(
        String compania,
        int proceso,
        int anio,
        int mes,
        int periodo,
        int idempleado,
        int idconcepto,
        BigDecimal valor,
        String accion,
        String user)
                    throws SystemException;

    Date fechaVacacionesPagas(
        String compania,
        int empleado,
        int parametro)
                    throws SystemException;

    Date fechaPeriodo(
        String compania,
        int proceso,
        int ano,
        int mes,
        int periodoin)
                    throws SystemException;

    BigDecimal cesantiasAcumuladas(
        String compania,
        int empleado,
        Date fecha1,
        Date fecha2)
                    throws SystemException;

    BigDecimal getDeducibleSalud(
        String compania,
        int iddeempleado,
        int idproceso)
                    throws SystemException;

    BigDecimal getPorcentateRetefuente(
        String compania,
        int iddeempleado,
        int idproceso)
                    throws SystemException;

    Date fechaPeriodoAnterior(
        String compania,
        int proceso,
        int ano,
        int mes,
        int periodoin,
        String parametro)
                    throws SystemException;

    void actualizarVacaciones(
        String compania,
        int empleado,
        int periodo,
        Date fechafinper,
        String usuario)
                    throws SystemException;
    
    void actualizarVacacionesEliminar(
            String compania,
            int empleado,
            int periodo,
            Date fechafinper,
            String usuario)
                        throws SystemException;

    void cargarFinanciables(
        String compania,
        int procesoin,
        int ano,
        int mes,
        int periodoin,
        int empleado,
        String usuario)
                    throws SystemException;

    Date getFechaPeriodoIniFin(
        String compania,
        int procesoin,
        int anio,
        int mes,
        int periodoin,
        boolean fechainicio,
        boolean total)
                    throws SystemException;

    void actualizarTraslados(
        String compania,
        int proceso,
        int ano,
        int mes,
        int periodo,
        String usuario)
                    throws SystemException;

    boolean validarPeriodoActivoNomina(
        String compania,
        int proceso,
        int anio,
        int mes,
        int periodo)
                    throws SystemException;

    void borrarHistoricoEmpleado(
        String compania,
        int proceso,
        int ano,
        int mes,
        int periodo,
        int idempleado,
        String usuario)
                    throws SystemException;

    void borrarHistoricoPeriodo(
        String compania,
        int proceso,
        int ano,
        int mes,
        int periodo,
        String usuario)
                    throws SystemException;

    String getDatoEmpresa(
        String compania,
        int par)
                    throws SystemException;

    BigDecimal novedadSiempreunEmpleado(
        String compania,
        int idempleado,
        int concepto)
                    throws SystemException;

    BigDecimal diasPendientesVacaciones(
        String compania,
        int idempleado,
        Date fechaf)
                    throws SystemException;

    BigDecimal ausentismoEmpleado(
        String compania,
        int idempleado,
        Date fechainicio,
        Date fechafinal)
                    throws SystemException;

    String getNombreCargo(
        String compania,
        String idcargo,
        String idescalafon)
                    throws SystemException;

    BigDecimal getSalarioCargo(
        String compania,
        String idescalafon,
        String idcategoria,
        int ano)
                    throws SystemException;

    String getNombreProceso(
        String compania,
        int proceso)
                    throws SystemException;

    void eliminarNovedadEmpleado(
        String compania,
        int procesoin,
        int anio,
        int mes,
        int periodoin,
        int idempleado,
        int idconcepto)
                    throws SystemException;

    void eliminaActualizaNovedadEmpleado(
        String compania,
        int procesoin,
        int anio,
        int mes,
        int periodoin,
        int idempleado,
        int idconcepto,
        BigDecimal cantidad,
        String usuario)
                    throws SystemException;

    void incrementarIbc(
        String compania,
        String usuario)
                    throws SystemException;
}