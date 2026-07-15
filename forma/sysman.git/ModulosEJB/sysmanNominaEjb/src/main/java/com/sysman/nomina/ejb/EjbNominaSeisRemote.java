package com.sysman.nomina.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Remote;

@Remote
public interface EjbNominaSeisRemote
{

    BigDecimal getSalarioBase(
        String compania,
        int idDeEmpleado)
                    throws SystemException;

    BigDecimal getCupoDeuda(
        String compania,
        int empleado,
        Date fecha)
                    throws SystemException;

    BigDecimal getCupoAsigando(
        String compania,
        BigDecimal cualsalario)
                    throws SystemException;

    void prepararPeriodoFinan(
        String compania,
        int anio,
        int mes,
        int periodo,
        int anio2,
        int mes2,
        int periodo2,
        String usuario)
                    throws SystemException;

    void prepararFinanciableNuevo(
        String compania,
        int anio1,
        int mes1,
        int periodo1,
        int anio2,
        int mes2,
        int periodo2,
        String usuario,
        String tipoempleado,
        String igualODiferente)
                    throws SystemException;

    int borrarFinaciables(
        String compania,
        int proceso,
        int anio,
        int mes,
        int periodo,
        String usuario)
                    throws SystemException;

    int borrarHistoricos(
        String compania,
        int proceso,
        int anio,
        int mes,
        int periodo,
        String empleado,
        String usuario)
                    throws SystemException;

    BigDecimal inconsistenciaSOI(
        String compania,
        int anio,
        int mes,
        int periodo,
        int proceso,
        String usuario)
                    throws SystemException;

    boolean factorParafiscal(
        String compania,
        int concepto)
                    throws SystemException;

    Date fechaCambioFondoRiesgo(
        String compania,
        int empleado)
                    throws SystemException;

    String getCampoPersonal(
        String compania,
        int idempleado,
        String campo)
                    throws SystemException;

    String sucursalRiesgo(
        String compania,
        int empleado,
        boolean par)
                    throws SystemException;

    String getDatoCajaCompensacion(
        String compania,
        int empleado,
        int par)
                    throws SystemException;

    String traerDatosFondoEmpleado(
        String compania,
        int empleado,
        String fondoactual,
        String tipofondo,
        int par)
                    throws SystemException;

    boolean cerrarPeriodo(
        String compania,
        int proceso,
        int anio,
        int mes,
        int periodo,
        String usuario)
                    throws SystemException;

    boolean cerrarNomina(
        String compania,
        int proceso,
        int anio,
        int mes,
        int periodo,
        int par,
        String usuario)
                    throws SystemException;

    String calcularPrenomina(
        String compania,
        int periodo,
        int proceso,
        int anio,
        int mes,
        String opcion,
        String inicial,
        String fin,
        String rutina,
        String usuario)
                    throws SystemException;

    void pasarConfiguracionDian(
        String compania,
        int anoAct,
        int anoConf,
        String usuario) throws SystemException;
    
    String generarDiscofna(
    		String compania, 
    		int proceso, 
    		int periodo, 
    		int mes, 
    		int anio, 
    		String fondo,
    		boolean mes13) throws SystemException;
    
    void revisaLey2277_2022_2(
            String compania,
            int proceso,
            int ano,
            int mes,
            int periodo,
            int empleado,
            boolean todos,
            String usuario)
                        throws SystemException;
    
    String subirCnHistoricos(
			String compania, 
			int proceso, 
			int anio, 
			int mes, 
			int periodo, 
			int idempleado,
			int idconcepto, 
			double valor, 
			Date fechac, 
			String obs,
			String usuario) throws SystemException;
    
    String crearNovedadesPensionado(
            String compania,
            Date  fechaInicial,
            Date  fechaFinal,
            String numeroDocumento,
            String idDeEmpleado,
            String tipoNovedad
            ) throws SystemException;
    
    String generarPlanoPeriodico(
  		  String compania,
            Date  fechaInicial,
            Date  fechaFinal
          ) throws SystemException;
    
    String generarPlanosPensionados(
  		  String compania,
            Date  fechaInicial,
            Date  fechaFinal
          ) throws SystemException;

}