/*-
 * FrmTotalesNominaControlador.java
 *
 * 1.0
 * 
 * 06/12/2024
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.model.StreamedContent;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.FrmTotalesNominaControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
/**
 *
 * @version 1.0, 06/12/2024
 * @author lvega
 */
@ManagedBean
@ViewScoped
public class  FrmTotalesNominaControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;

	private String proceso;

	private String anio;

	private String mes;

	private String periodo;

	private List<Registro> listaProceso;

	private List<Registro> listaAno;
	
	private List<Registro> listaMes;
	
	private List<Registro> listaPeriodo;

	private StreamedContent archivoDescarga;
	
	private String nombreConsulta;

	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmTotalesNominaControlador
	 */
	public FrmTotalesNominaControlador() {
		super();
		compania = SessionUtil.getCompania();
		String modulo =  SessionUtil.getModulo();
		try {
			numFormulario = GeneralCodigoFormaEnum.FRM_TOTALES_NOMINA_CONTROLADOR.getCodigo();
			validarPermisos();
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			SessionUtil.redireccionarMenuPermisos();
		}finally{
		}
	}
	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la
	 * clase del Bean ha sido creado, en este se realizan las
	 * asignaciones iniciales necesarias para la visualizacion del
	 * formulario, como son tablas, origenes de datos, inicializacion
	 * de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar(){
		//<CARGAR_LISTA>
		cargarListaProceso();
		cargarListaAno();
		cargarListaMes();
		cargarListaPeriodo();
		abrirFormulario();
	}
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario(){
		proceso = "1";
		cargarListaAno();
		anio = Integer.toString(SysmanFunciones.ano(new Date()));
		cargarListaMes();
		mes = Integer.toString(SysmanFunciones.mes(new Date()));
		cargarListaPeriodo();
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaProceso
	 *
	 */
	public void cargarListaProceso(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaProceso = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmTotalesNominaControladorUrlEnum.URL537004
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}
	}
	/**
	 * 
	 * Carga la lista listaAno
	 *
	 */
	public void cargarListaAno(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);

		try {
			listaAno = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmTotalesNominaControladorUrlEnum.URL471008
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}
	}
	/**
	 * 
	 * Carga la lista listaMes
	 *
	 */
	public void cargarListaMes(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		try {
			listaMes = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmTotalesNominaControladorUrlEnum.URL471049
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}
	}
	/**
	 * 
	 * Carga la lista listaPeriodo
	 *
	 */
	public void cargarListaPeriodo(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(GeneralParameterEnum.MES.getName(), mes);

		try {
			listaPeriodo = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmTotalesNominaControladorUrlEnum.URL471050
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Aceptar
	 * en la vista
	 *
	 *
	 */
	public void oprimirAceptar() {
		generarInforme(FORMATOS.EXCEL);
	}


	public void oprimirPlano() {
		generarInforme(FORMATOS.TXT);
	}

	private void generarInforme(FORMATOS formato) {
		// TODO Auto-generated method stub
		//8000575TotalesNomina
		nombreConsulta = "8000575TotalesNomina";
		Map<String, Object> reemplazar = new HashMap<>();

		reemplazar.put("anioNomina",anio);
		reemplazar.put("mesNomina",mes);
		reemplazar.put("periodoNomina",periodo);
		reemplazar.put("compania",compania);
		reemplazar.put("procesoNomina",proceso);

		String strSql = Reporteador.resuelveConsulta(nombreConsulta, Integer.parseInt(SessionUtil.getModulo()), reemplazar);

		if(formato.equals(FORMATOS.EXCEL)){
			try {

				archivoDescarga = JsfUtil.exportarHojaDatosStreamed(
						strSql, ConectorPool.ESQUEMA_SYSMAN,
						formato,
						"TotalesNomina");
			}
			catch (JRException | IOException | SQLException | DRException  | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
		}else {
			try {

				List<Registro> rs = service.getListado(ConectorPool.ESQUEMA_SYSMAN,
						strSql);

				StringBuilder textoTxt = new StringBuilder();
				textoTxt.append(SysmanFunciones.padl("TOTALES DE NOMINA", 90, " ")
						+ "\r\n");

				textoTxt.append(" " + "\r\n");
				textoTxt.append(SysmanFunciones.padr("CEDULA", 20, " ")
						+ SysmanFunciones.padr("NOMBRE_EMPLEADO", 45, " ")
						+ SysmanFunciones.padr("CARGO", 50, " ")
						+ SysmanFunciones.padr("DEVENGOS", 20, " ")
						+  SysmanFunciones.padr("DEDUCCIONES", 20, " ")
						+  SysmanFunciones.padr("NETO_A_PAGAR", 20, " ")
						+ "SALARIO");
				textoTxt.append("\r\n");
				if (!rs.isEmpty()) {

					for (int j = 0; j < rs.size(); j++) {

						textoTxt.append(SysmanFunciones
								.padr(rs.get(j).getCampos()
										.get("CEDULA")
										.toString(), 20, " "));

						if (!SysmanFunciones.validarCampoVacio(
								rs.get(j).getCampos(), "NOMBRE_EMPLEADO")) {
							textoTxt.append(SysmanFunciones
									.padr(rs.get(j).getCampos()
											.get("NOMBRE_EMPLEADO")
											.toString(), 45, " "));
						}

						if (!SysmanFunciones.validarCampoVacio(
								rs.get(j).getCampos(), "CARGO")) {
							textoTxt.append(SysmanFunciones
									.padr(rs.get(j).getCampos()
											.get("CARGO")
											.toString(),50, " "));
						}

						if (!SysmanFunciones.validarCampoVacio(
								rs.get(j).getCampos(), "DEVENGOS")) {
							textoTxt.append(SysmanFunciones
									.padr(rs.get(j).getCampos()
											.get("DEVENGOS")
											.toString(),20, " "));
						}

						if (!SysmanFunciones.validarCampoVacio(
								rs.get(j).getCampos(), "DEDUCCIONES")) {
							textoTxt.append(SysmanFunciones
									.padr(rs.get(j).getCampos()
											.get("DEDUCCIONES")
											.toString(),20, " "));
						}
						if (!SysmanFunciones.validarCampoVacio(
								rs.get(j).getCampos(), "NETO_A_PAGAR")) {
							textoTxt.append(SysmanFunciones
									.padr(rs.get(j).getCampos()
											.get("NETO_A_PAGAR")
											.toString(),
											20, " "));
						}

						if (!SysmanFunciones.validarCampoVacio(
								rs.get(j).getCampos(), "SALARIO")) {
							textoTxt.append(rs.get(j).getCampos().get(
									"SALARIO").toString()
									+ "\r\n");
						}

					}
				}

				archivoDescarga = JsfUtil.getArchivoDescarga(
						JsfUtil.serializarPlano(textoTxt.toString()),
						"TOTALES_NOMINA.txt");

			}
			catch (JRException | IOException  e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
		}

	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control Proceso
	 * 
	 * 
	 */
	public void cambiarProceso() {
		cargarListaAno();
		anio = null;
		mes = null;
		periodo = null;
		listaMes = null;
		listaPeriodo = null;
		cambiarAno();
		cambiarMes();
	}
	/**
	 * Metodo ejecutado al cambiar el control Ano
	 * 
	 * 
	 */
	public void cambiarAno() {
		cargarListaMes();

		mes = null;
		periodo = null;
		listaPeriodo = null;
		cambiarMes();
	}
	/**
	 * Metodo ejecutado al cambiar el control Mes
	 * 
	 * 
	 */
	public void cambiarMes() {
		cargarListaPeriodo();
		periodo = null;
	}
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable proceso
	 * 
	 * @return  proceso
	 */
	public String getProceso() {
		return proceso;
	}
	/**
	 * Asigna la variable  proceso
	 * 
	 * @param  proceso
	 * Variable a asignar en  proceso
	 */
	public void setProceso(String proceso) {
		this.proceso = proceso;
	}
	/**
	 * Retorna la variable anio
	 * 
	 * @return  anio
	 */
	public String getAnio() {
		return anio;
	}
	/**
	 * Asigna la variable  anio
	 * 
	 * @param  anio
	 * Variable a asignar en  anio
	 */
	public void setAnio(String anio) {
		this.anio = anio;
	}
	/**
	 * Retorna la variable mes
	 * 
	 * @return  mes
	 */
	public String getMes() {
		return mes;
	}
	/**
	 * Asigna la variable  mes
	 * 
	 * @param  mes
	 * Variable a asignar en  mes
	 */
	public void setMes(String mes) {
		this.mes = mes;
	}
	/**
	 * Retorna la variable periodo
	 * 
	 * @return  periodo
	 */
	public String getPeriodo() {
		return periodo;
	}
	/**
	 * Asigna la variable  periodo
	 * 
	 * @param  periodo
	 * Variable a asignar en  periodo
	 */
	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listaProceso
	 * 
	 * @return listaProceso
	 */
	public List<Registro> getListaProceso() {
		return listaProceso;
	}
	/**
	 * Asigna la lista listaProceso
	 * 
	 * @param listaProceso
	 * Variable a asignar en  listaProceso
	 */
	public void setListaProceso(List<Registro> listaProceso) {
		this.listaProceso = listaProceso;
	}
	/**
	 * Retorna la lista listaAno
	 * 
	 * @return listaAno
	 */
	public List<Registro> getListaAno() {
		return listaAno;
	}
	/**
	 * Asigna la lista listaAno
	 * 
	 * @param listaAno
	 * Variable a asignar en  listaAno
	 */
	public void setListaAno(List<Registro> listaAno) {
		this.listaAno = listaAno;
	}
	/**
	 * Retorna la lista listaMes
	 * 
	 * @return listaMes
	 */
	public List<Registro> getListaMes() {
		return listaMes;
	}
	/**
	 * Asigna la lista listaMes
	 * 
	 * @param listaMes
	 * Variable a asignar en  listaMes
	 */
	public void setListaMes(List<Registro> listaMes) {
		this.listaMes = listaMes;
	}
	/**
	 * Retorna la lista listaPeriodo
	 * 
	 * @return listaPeriodo
	 */
	public List<Registro> getListaPeriodo() {
		return listaPeriodo;
	}
	/**
	 * Asigna la lista listaPeriodo
	 * 
	 * @param listaPeriodo
	 * Variable a asignar en  listaPeriodo
	 */
	public void setListaPeriodo(List<Registro> listaPeriodo) {
		this.listaPeriodo = listaPeriodo;
	}
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}


	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
