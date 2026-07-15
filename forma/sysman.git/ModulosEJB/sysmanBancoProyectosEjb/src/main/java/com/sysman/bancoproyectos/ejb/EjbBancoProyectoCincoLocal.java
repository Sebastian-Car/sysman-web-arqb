package com.sysman.bancoproyectos.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.ejb.Local;

@Local
public interface EjbBancoProyectoCincoLocal {

	BigDecimal mayorizarAvance(
			String compania,
			int vigencia,
			String metaProdIni,
			String metaProdFin,
			BigDecimal total,
			String usuario)
					throws SystemException;

	BigDecimal actualizarPlanIndicativo(
			String compania,
			int modulo,
			long novedadInicial,
			long novedadFinal,
			String tipo,
			String usuario)
					throws SystemException;

	String genConsecutivoCDP(
			String nombretabla,
			String condicion,
			String nombrecampo,
			String inicial)
					throws SystemException;

	String getNombreTipoNovedad(
			String compania,
			String strtipot,
			String strclaset)
					throws SystemException;

	void afectarNovedad(
			String compania,
			String clase,
			String tipo,
			String dependencia,
			long codigo,
			long novedadanterior,
			long novedadafectar,
			String usuario)
					throws SystemException;

	BigDecimal saldoPlanIndicativoMeta(String compania, String meta,
			BigDecimal vigenciaPlan, BigDecimal vigenciaMeta)
					throws SystemException;

	void crearFichaTecnica(String compania, String proyecto, String sector,
			String usuario) throws SystemException;

	String generarConsultaCriticos(String compania, int ano)
			throws SystemException;

	String generarMetaBruta(
			String compania,
			String idPlan,
			int vigenciaPlan,
			int vigenciaMeta,
			BigDecimal aProgramar)
					throws SystemException;

	void actualizarMetaBruta(
			String compania,
			String idPlan,
			int vigenciaPlan,
			int vigenciaMeta,
			String usuario)
					throws SystemException;

	void importarXml(
			String compania,
			String proyectos,
			String productos,
			String actividades,
			boolean asignar,
			String codigoProy,
			String dependencia,
			int vigenciaInicial,
			int vigenciaFinal,
			String usuario,
			String codigoProyBpin)
					throws SystemException;

	long crearProgramacionProy(
			String compania,
			String vigencia,
			String codigo,
			String usuario) throws SystemException;

	String generarProyectosInversion(
			String compania,
			int vigenciaI,
			int vigenciaF) throws SystemException;

	String definirOrden(
			String nombretabla,
			String nombrecampo,
			String campoorden,
			String condicion,
			boolean conAlias)
					throws SystemException;

	String generarInformeEficacia(
			String compania,
			int vigencia)
					throws SystemException;

	void heredarSolicitudScd(
			String compania,
			String tipotHijo,
			String clasetHijo,
			long novedadHijo,
			String dependenciaHijo,
			String tipot,
			String claset,
			long novedad,
			String dependencia,
			String usuario)
					throws SystemException;

	void consultadDcumentoAfectar(
			String compania,
			String tipot,
			String claset,
			long documento,
			String dependencia,
			String usuario)
					throws SystemException;

	long crearMante(
			String compania,
			String vigencia,
			String codigo,
			String usuario)
					throws SystemException;

	String verificarSaldoRubro(
			String compania,
			String claset,
			long novedad,
			String dependencia,
			String rubroPresupuestal,
			String proyecto,
			String fuenteRecursos,
			String idMetaProducto,
			Double valorSolicitado,
			long valorDisminuido,
			String centroCosto,
			String referencia,
			String auxiliar)
					throws SystemException;

	String prepararDatosF202ProyectosDestinados(String compania,
			Date vigenciainicial, Date vigenciafinal) throws SystemException;

	String validarVoBo(
			String compania, 
			BigInteger novedad, 
			String tipo, 
			String clase, 
			boolean vobo)
			throws SystemException;
	
	void actualizarMetaProducto(
			String compania,
			int vigenciaPlan,
			String usuario)
					throws SystemException;

	String cargarPlanIndicativo(
			String compania, 
			String cadena, 
			String usuario) throws SystemException;

}