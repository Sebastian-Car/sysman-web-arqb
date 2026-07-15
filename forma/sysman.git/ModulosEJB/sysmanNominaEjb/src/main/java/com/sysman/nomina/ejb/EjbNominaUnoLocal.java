package com.sysman.nomina.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Local;

@Local
public interface EjbNominaUnoLocal {

    int getConceptoRelacionado(
        String compania,
        int concepto)
                    throws SystemException;

    BigDecimal getValorConcepto(
        String compania,
        int proceso,
        int ano,
        int mes,
        int periodo,
        int empleado,
        int concepto)
                    throws SystemException;

    BigDecimal getSalarioBasico(
        String compania,
        int proceso,
        int ano,
        int mes,
        int periodo,
        int empleado)
                    throws SystemException;

    String getMiNovedad(
        String compania,
        int proceso,
        int ano,
        int mes,
        int empleado)
                    throws SystemException;

    String getMisVacaciones(
        String compania,
        int proceso,
        int ano,
        int mes,
        int empleado,
        String opcion)
                    throws SystemException;

    BigDecimal getSalarioBase(
        String compania,
        int empleado)
                    throws SystemException;

    String getParametroNomina(
        String compania,
        int opcion)
                    throws SystemException;

    String getMiResolucion(
        String compania,
        int empleado,
        int opcion)
                    throws SystemException;

    void putCopiaPersona(
        String compania,
        int idempleado,
        int anio)
                    throws SystemException;

    String getPivotKardexNomina(
        String compania,
        int ano,
        int empleadoini,
        int empleadofin,
        int nombremes,
        long mayorigual,
        boolean acumulable)
                    throws SystemException;

    String getKardexAcumNomina(
        String compania,
        String limiteinf,
        String limitesup,
        int idempleado)
                    throws SystemException;

    String getDatosEntidadesFondos(
        String compania,
        String codfondo,
        String tipofondo,
        int par)
                    throws SystemException;

    void putActualizarRetefuente(
        String compania,
        int ano,
        int mes,
        int periodo,
        String usuario)
                    throws SystemException;

    int getPrimerEmpleado(
        String compania,
        int registro)
                    throws SystemException;

    String getNombrePeriodo(
        String compania,
        int proceso,
        int ano,
        int mes,
        int periodo)
                    throws SystemException;

    BigDecimal getAcumConceptoValor(
        String compania,
        int concepto,
        int ano1,
        int mes1,
        int per1,
        int ano2,
        int mes2,
        int per2,
        int iddeempleado)
                    throws SystemException;

    BigDecimal getAcumNovedadConcValor(
        String compania,
        int concepto,
        int ano1,
        int mes1,
        int per1,
        int ano2,
        int mes2,
        int per2,
        int iddeempleado)
                    throws SystemException;

    BigDecimal getSalarioBaseCategoria(
        String compania,
        int iddeempleado)
                    throws SystemException;

    BigDecimal getDeducible(
        String compania,
        int iddeempleado)
                    throws SystemException;

    String getPrepararPivotDaneEmpleado(
        String compania,
        String limiteinf,
        String limitesup)
                    throws SystemException;

    boolean getPeriodoActivadoFecha(
        String compania,
        int proceso,
        Date fecha,
        int periodo)
                    throws SystemException;

    Date getUltimaFechaVacaciones(
        String compania,
        int empleado,
        boolean parametro)
                    throws SystemException;

    String getValidarFechasVacaciones(
        String compania,
        Date fecha,
        int empleado)
                    throws SystemException;

    BigDecimal getDiaPendienteVacacionesHistorico(
        String compania,
        int proceso,
        int empleado)
                    throws SystemException;

    String getAvisoVacaciones(
        String compania,
        int empleado,
        Date fechainicio)
                    throws SystemException;

    String getAvisoLicencias(
        String compania,
        int empleado,
        Date fechainicio,
        Date fechafinal)
                    throws SystemException;

    int getDiasLicCorrerVacaciones(
        String compania,
        int empleado,
        Date fechainicio,
        Date fechafinal)
                    throws SystemException;

    int getDiasHabilesVacaciones(
        String compania,
        int ano,
        int mes,
        int periodo,
        int empleado,
        int numeroper,
        int diaspendi)
                    throws SystemException;

    Date getUltimoInterrupcion(
        String compania,
        int proceso,
        int ano,
        int mes,
        int periodo,
        int empleado,
        int parametro)
                    throws SystemException;

    Date getFechaFinalVacaciones(
        String compania,
        Date iniciodisfrute,
        int diashabiles,
        String sabadohabil,
        int modulo)
                    throws SystemException;

    String getPrepararPivotDevengosAnio(
        String compania,
        int ano)
                    throws SystemException;

    String cierreNominaPreliminar(
        String compania,
        int proceso,
        int ano,
        int mes,
        int periodo,
        String usuario) throws SystemException;
    
    void guardahistoricoCune(
        String compania,
        int anio,
        int mes,
        String user) throws SystemException;
    
    void actNominaCune(
        String compania,
        int anio,
        int mes,
        String tipoNom,
        int consec,
        Date fechaRpt,
        BigDecimal trm,
        String user,
        String empleado) throws SystemException;
    
    boolean actEstNominaCune(
            String compania,
            int anio,
            int mes,
            String tipoNom,
            int consec,
            String nroDoc,
            String ope
            ) throws SystemException;
    
    String revisionDatosCune(
        String compania,
        int anio,
        int mes) throws SystemException;
    
    String getPreparaPivotConsFact(
            String compania,
            int ano,
            int mes)
                        throws SystemException;

	void porcentajesFsp(
			String compania, 
			int anio, 
			int salarioMinimo, 
			String usuario) throws SystemException;

}