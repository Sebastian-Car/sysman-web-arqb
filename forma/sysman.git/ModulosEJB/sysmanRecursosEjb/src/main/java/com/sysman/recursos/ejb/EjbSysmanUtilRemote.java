package com.sysman.recursos.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Remote;

@Remote
public interface EjbSysmanUtilRemote {

	String consultarParametro(
			String compania,
			String nombre,
			String modulo,
			Date fechaPar,
			boolean indMayus)
					throws SystemException;

	int generarDigitoDeVerificacion(
			String numero)
					throws SystemException;

	int calcularDiferenciaEnMeses(
			Date fecha1,
			Date fecha2)
					throws SystemException;

	int retornarDiasComerciales(
			Date fechain,
			Date fechafin)
					throws SystemException;

	int retornarDiaDeLaSemana(
			Date fecha,
			int arranca)
					throws SystemException;

	String convetirValorEnLetras(
			BigDecimal numt,
			boolean ctvs)
					throws SystemException;

	String calcularDiferenciaDeTiempo(
			Date tiempoi,
			Date tiempof)
					throws SystemException;

	long generarSiguienteConsecutivo(
			String tabla,
			String criterio,
			String campo)
					throws SystemException;

	long generarSiguienteConsecutivo(
			String tabla,
			String criterio,
			String campo,
			String nombreConexion)
					throws SystemException;

	long generarConsecutivoConValorInicial(
			String tabla,
			String criterio,
			String campo,
			String inicial)
					throws SystemException;

	String consultarNombreDeTercero(
			String compania,
			String nit,
			String sucursal)
					throws SystemException;

	String consultarNombreCuentaContable(
			String compania,
			int ano,
			String cuenta)
					throws SystemException;

	String consultarNombreCentroDeCosto(
			String compania,
			int ano,
			String codigo)
					throws SystemException;

	String consultarNombreAuxiliar(
			String compania,
			int ano,
			String codigo)
					throws SystemException;

	String mostrarNombreDeMes(
			int numeroMes)
					throws SystemException;

	Date retornarFechaMasDiasHabiles(
			String compania,
			Date fecha,
			int dias,
			boolean sabado)
					throws SystemException;

	String formatearNitEntidad(
			String compania,
			int opcion)
					throws SystemException;

	boolean consultarDiaFestivo(
			String compania,
			Date fecha)
					throws SystemException;

	int retornarDiasHabilesEntreFechas(
			String compania,
			Date fechaini,
			Date fechafin,
			boolean sabados)
					throws SystemException;

	String consutarNombreCompletoDeTerceroConParametro(
			String compania,
			String nombre1,
			String nombre2,
			String apellido1,
			String apellido2)
					throws SystemException;

	String crearCodigoPresupuestal(
			String compania,
			int ano,
			String cuenta,
			String centroCosto,
			String tercero,
			String sucursal,
			String auxiliar,
			String referencia,
			String fuente)
					throws SystemException;

	String crearCodigoContable(
			String compania,
			int ano,
			String cuenta,
			String centroCosto,
			String tercero,
			String sucursal,
			String auxiliar,
			String referencia,
			String fuente)
					throws SystemException;

	String calcularDiferenciaEntreFechas(
			Date fechaini,
			Date fechafin,
			int formato,
			int edadpersona)
					throws SystemException;

	int calcularDiferenciaEnDias(
			Date fechaini,
			Date fechafin)
					throws SystemException;

	int calcularEdadDelPersonal(
			Date fecha)
					throws SystemException;

	String generarValorDeCamposAuxiliaresPresupuestales(
			String compania,
			int ano,
			String cuenta,
			String campo,
			int valor)
					throws SystemException;

	String generarValorDeCamposAuxiliaresContables(
			String compania,
			int ano,
			String cuenta,
			String campo,
			int valor)
					throws SystemException;

	Date fechaFinalMasDiasComerciales(
			Date fechainicial,
			int dias,
			boolean mescomercial)
					throws SystemException;

	String consutarNombreCompletoDeTercero(
			String nombre1,
			String nombre2,
			String apellido1,
			String apellido2)
					throws SystemException;

	Date fechaFinalMasDiasComercialesTT(
			Date fechainicial,
			int dias,
			int mescomercial)
					throws SystemException;

	String verificarEstadoPeriodoAnual(
			String compania,
			int ano,
			int modulo,
			int proceso)
					throws SystemException;
	
	String verificarEstadoPeriodoAnual(
			String compania,
			String ano,
			String modulo,
			String proceso)
					throws SystemException;

	String verificarEstadoPeriodoMensual(
			String compania,
			int ano,
			int mes,
			int modulo,
			int proceso)
					throws SystemException;

	String verificarEstadoDiario(
			String compania,
			int ano,
			int mes,
			int dia,
			int modulo,
			int proceso)
					throws SystemException;

	String generarCuentaRecibiendoAuxiliares(
			String compania,
			int ano,
			String cuenta,
			String centroCosto,
			String tercero,
			String sucursal,
			String auxiliar,
			String referencia,
			String fuente)
					throws SystemException;

	String retornarNombreCardinal(
			int numero,
			int tipo)
					throws SystemException;

	Date sumarDiasFecha(
			Date fecha,
			int dias)
					throws SystemException;

	Boolean insertarDiasdelMes(
			String compania,
			int ano,
			int mes,
			String creador)
					throws SystemException;

	Boolean insertarMesesdelAno(
			String compania,
			int ano,
			String creador)
					throws SystemException;

	Boolean cambiarEstadoMes(
			String compania,
			int ano,
			int mes,
			int modulo,
			int proceso,
			String estado,
			String creador)
					throws SystemException;

	Boolean cambiarEstadoAno(
			String compania,
			int ano,
			int modulo,
			int proceso,
			String estado,
			String creador)
					throws SystemException;

	String verificarConsultaPlantilla(String consulta) throws SystemException;

	void anexarDatosEnTabla(String tabla, String excluidos, String valorNuevo,
			String baseExcluido, String condicion) throws SystemException;

	String generarCerosIzquierda(
			long numero,
			long longitud)
					throws SystemException;

	BigDecimal fix(
			BigDecimal valor)
					throws SystemException;

	int retornarDiasHabilesViaticos(
			String compania,
			Date fechaIni,
			Date fechaFin,
			boolean sabados,
			boolean domingos,
			boolean festivos)
					throws SystemException;

	BigDecimal retornarDiasPernoctando(
			String compania,
			boolean perNoctando,
			Date fechaInicio,
			Date fechaFin,
			boolean sabado,
			boolean domingo,
			boolean festivo) throws SystemException;

	String detectarCampos(
			String sql)
					throws SystemException;

	int consultarModeloAno(
			String compania,
			String ano
			)
					throws SystemException;
	
	int aplicacionCuenta(
			String compania,
			String ano,
			String codigo,
			String cuenta
			)
					throws SystemException;
	
	String cuentaClasificador(
			String compania,
			String ano,
			String cuenta,
			String clase
			)
					throws SystemException;

	String consultarParametroMarcaBlanca(String nombre) throws SystemException;
	
	String copiarPlantilla(
			String plantillaOrigen, 
			String plantillaDestino,  
			Date fecha,
			String nombre,
			int tipo,
			String nombrePlantilla) 
					throws SystemException;
	
	String calcularDiferenciaEntreFechasVac(
			Date fechaini,
			Date fechafin,
			int formato,
			int edadpersona)
					throws SystemException;
	
	int calcularDiferenciaEntreFechasInc(
			Date fechaini,
			Date fechafin,
			int formato)
					throws SystemException;
	

}