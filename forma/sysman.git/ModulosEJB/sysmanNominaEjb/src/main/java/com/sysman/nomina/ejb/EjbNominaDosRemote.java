package com.sysman.nomina.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Remote;

@Remote
public interface EjbNominaDosRemote {

	void putActualizarSueldos(
			String compania,
			int anoBase,
			int anoActualizar,
			double porcentajeIncremento,
			double porcentajeMesada,
			String usuario)
					throws SystemException;

	long getCategoriaAnio(
			String compania,
			int ano)
					throws SystemException;

	void eliminarCategoria(
			int anoBase,
			String compania)
					throws SystemException;

	long getIncluirNovedadEncargos(
			int mes,
			int periodo,
			int ano,
			int proceso,
			String compania,
			Date fechainicio,
			Date fechafin,
			String user,
			int idEmpleado)
					throws SystemException;

	long getIncluirNovedadLicencias(
			int mes,
			int periodo,
			int ano,
			int proceso,
			String compania,
			Date fechainicio,
			Date fechafin,
			String usuario)
					throws SystemException;

	boolean getExistePeriodo(
			String compania,
			int ano,
			int periodo,
			int mes,
			int proceso)
					throws SystemException;
	
	boolean getValidaAnioMesada(
			String compania,
			int ano,
			int estado)
					throws SystemException;
	
	boolean reversaMesada(
			String compania,
			String usuario)
					throws SystemException;

	BigDecimal getValorConceptoNovedad(
			String compania,
			int ano,
			int periodo,
			int mes,
			int concepto)
					throws SystemException;

	void prepararFinanciables(
			String compania,
			int ano1,
			int mes1,
			int periodo1,
			int ano2,
			int mes2,
			int periodo2,
			String usuario,
			int tipoEmpleado,
			String igualODiferente)
					throws SystemException;

	String siguientePeriodo(
			String compania,
			int ano,
			int periodo,
			int mes,
			int proceso,
			String opcion)
					throws SystemException;

	void prepararEmbargos(
			String compania,
			int ano1,
			int mes1,
			int periodo1,
			int ano2,
			int mes2,
			int periodo2,
			String usuario)
					throws SystemException;

	void retefuente(
			String compania,
			int anoNuevo,
			int anoSession,
			String usuario)
					throws SystemException;
	
	void duplicarHE(
            String compania,
            int anoNuevo,
            int ano,
            String usuario)
            throws SystemException;

	BigDecimal getAusentismoEmpleadoTotal(
			String compania,
			int idEmpleado)
					throws SystemException;

	BigDecimal getAutorizarEnvioCorreo(
			String compania,
			int modulo)
					throws SystemException;

	boolean getDiferir(
			String compania,
			int proceso,
			int anio,
			int mes,
			int periodo,
			int idEmpleado,
			int concepto,
			int diferido,
			String opcion,
			int concepto1,
			BigDecimal valor1,
			int concepto2,
			BigDecimal valor2,
			Date fechainicio,
			int incapacidad,
			String usuario)
					throws SystemException;

	boolean getDiferirVac(
			String compania,
			int proceso,
			int idEmpleado,
			int concepto,
			int diahabil,
			int diadinero,
			int diapendiente,
			int dias,
			Date iniciodisfrute,
			Date fechapago,
			int numperiodo,
			boolean indbonificacion,
			String opcion,
			String usuario)
					throws SystemException;

	boolean getActualizarVacaPeriodo(
			String compania,
			int idDeProceso,
			int anio,
			int mes,
			int periodo,
			int idDeEmpleado,
			String usuario)
					throws SystemException;

	boolean getDiferirMas(
			String compania,
			int proceso,
			int anio,
			int mes,
			int periodo,
			int idEmpleado,
			int concepto,
			int diferido,
			String opcion,
			int concepto1,
			BigDecimal valor1,
			int concepto2,
			BigDecimal valor2,
			Date fechainicio,
			String usuario)
					throws SystemException;

	boolean getDiferirMas(String compania, int proceso, int anio, int mes,
			int periodo, int idEmpleado, int concepto, int diferido, String opcion,
			Date fechainicio, String usuario, Date fechaFinalOrg) throws SystemException;

	void crearCentrosDeCosto(
			int anoCrear,
			int anoSession,
			String compania,
			String usuario)
					throws SystemException;

	boolean getDiferirQuin(
			String compania,
			int proceso,
			int idEmpleado,
			String opcion,
			Date fechapago)
					throws SystemException;

	boolean getDiferirEnc(
			String compania,
			int proceso,
			int idEmpleado,
			int diferido,
			String opcion,
			Date fechainicio,
			double porgasto,
			BigDecimal salario,
			String usuario)
					throws SystemException;

	boolean getDiferirIntVac(
			String compania,
			int proceso,
			int idEmpleado,
			String opcion,
			Date fechapago,
			boolean endinero,
			Date fechainterrupcion,
			Date fechafinaldisfrute,
			int diasinterrupcion,
			String usuario)
					throws SystemException;

	String getNombreConcepto(
			String compania,
			int idDeConcepto)
					throws SystemException;

	void crearDatos(String compania, 
			String companiaBase, 
			String nombreCompania,
			String siglaCompania, 
			String usuario) 
					throws SystemException;

	int difereirEncMod(
			String compania, 
			String proceso, 
			String idEmpleado, 
			String opcion, 
			Date fechainicio,
			Date fechafinal, 
			double porgasto, 
			double salario, 
			String usuario, 
			String periodo)
					throws SystemException;
	
	void insertBecp(
            String compania,
            int idEmpleado,
            int proceso,
            int anio,
            int mes,
            int periodo,
            String usuario)
                    throws SystemException;
}