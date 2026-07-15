/*-
 * SubirPlanoPilaControlador.java
 *
 * 1.0
 * 
 * 30/06/2025
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaCincoRemote;
import com.sysman.nomina.enums.SubirPlanoPilaControladorUrlEnum;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;
/**
 *
 * @version 1.0, 30/06/2025
 * @author User 1
 */
@ManagedBean
@ViewScoped
public class  SubirPlanoPilaControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	//<DECLARAR_ATRIBUTOS>
	private String anio = SessionUtil.getSessionVar("anioNomina").toString();
	private String mes = SessionUtil.getSessionVar("mesNomina").toString();
	private String periodo = SessionUtil.getSessionVar("periodoNomina").toString();
	private String proceso = SessionUtil.getSessionVar("procesoNomina").toString();
	private String cbCompania;
	private String tipoEmpleado;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;

	/**
	 * Este atributo se usa como auxiliar del componente selector de
	 * archivos datosPila y funciona como contenedor del archivo que se
	 * debe guardar
	 */
	private ContenedorArchivo contArchivodatosPila;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	private List<Registro> listaAnio;
	private List<Registro> listaMes;
	private List<Registro> listaPeriodo;
	private List<Registro> listaCompania;
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>

	@EJB
	private EjbNominaCincoRemote cincoRemote; 
	/**
	 * Crea una nueva instancia de SubirPlanoPilaControlador
	 */
	public SubirPlanoPilaControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario=2519;
			validarPermisos();
			cbCompania = compania;
			tipoEmpleado = "A";
			contArchivodatosPila = new ContenedorArchivo();
			//<INI_ADICIONAL>
			//</INI_ADICIONAL>
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
		cargarListaAnio();
		cargarListaMes();
		cargarListaPeriodo();
		cargarListaCompania();
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		//</CARGAR_LISTA_COMBO_GRANDE>
		//<CREAR_ARBOLES>
		//</CREAR_ARBOLES>
		abrirFormulario();
	}
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaAnio
	 *
	 */
	public void cargarListaAnio(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				String.valueOf(compania));
		param.put("PROCESO", proceso);
		try
		{
			listaAnio = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									SubirPlanoPilaControladorUrlEnum.URL4440.getValue())
							.getUrl(), param));
		}
		catch (SystemException e)
		{
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
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
									SubirPlanoPilaControladorUrlEnum.URL5723
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
									SubirPlanoPilaControladorUrlEnum.URL7274
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
	 * Carga la lista listaCompania
	 *
	 */
	public void cargarListaCompania(){
		try {
			listaCompania = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									SubirPlanoPilaControladorUrlEnum.URL59003
									.getValue())
							.getUrl(), null));
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
	 * Metodo ejecutado al oprimir el boton Cargar
	 * en la vista
	 *
	 *
	 */
	public void oprimirCargar() {
		//<CODIGO_DESARROLLADO>
		String respuesta = null;
		archivoDescarga = null;
		try {
			File archivo = contArchivodatosPila.getArchivo(); // <- debería ser java.io.File

			if (archivo == null || !archivo.exists() || archivo.length() == 0) {
				JsfUtil.agregarMensajeAlerta("El archivo no existe o está vacío.");
				return;
			}

			System.out.println("Leyendo archivo: " + archivo.getAbsolutePath());

			StringBuilder cadena = new StringBuilder();
			ArrayList<String> archivos = new ArrayList<>();
			int num = 0;

			cadena.append("TO_CLOB('");

			try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
				String linea;

				while ((linea = reader.readLine()) != null) {
					if (num >= 10000) {
						cadena.append("') || TO_CLOB('");
						num = 0;
					}

					String[] columnas = linea.split(",");

					if (columnas.length >= 4) {
						archivos.add(SysmanFunciones.concatenar(columnas[2], ",", columnas[3]));
					}

					cadena.append(linea.replace(",", SysmanConstantes.SEPARADOR_COL));
					cadena.append(SysmanConstantes.SEPARADOR_REG);

					num += linea.length();
				}

				cadena.append("')");

			}
			
			respuesta = cincoRemote.cargarBasesNovedades(cbCompania, anio, mes, SysmanFunciones.toString(cadena), SessionUtil.getUser().getCodigo());

			if (respuesta != null)
			{
				ByteArrayInputStream inconsitencia = JsfUtil
						.serializarPlano(respuesta);
				archivoDescarga = JsfUtil.getArchivoDescarga(inconsitencia,
						"inconsistencias.txt");
			}
			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));

		} catch (IOException | JRException | SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);
		}		

		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control Anio
	 * 
	 * 
	 */
	public void cambiarAnio() {
		//<CODIGO_DESARROLLADO>
		mes = null;
		periodo = null;
		cambiarMes();
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control Mes
	 * 
	 * 
	 */
	public void cambiarMes() {
		//<CODIGO_DESARROLLADO>
		periodo = null;
		cargarListaPeriodo();
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable cbCompania
	 * 
	 * @return  cbCompania
	 */
	public String getCbCompania() {
		return cbCompania;
	}
	/**
	 * Asigna la variable  cbCompania
	 * 
	 * @param  cbCompania
	 * Variable a asignar en  cbCompania
	 */
	public void setCbCompania(String cbCompania) {
		this.cbCompania = cbCompania;
	}
	/**
	 * @return the anio
	 */
	public String getAnio() {
		return anio;
	}
	/**
	 * @param anio the anio to set
	 */
	public void setAnio(String anio) {
		this.anio = anio;
	}
	/**
	 * @return the mes
	 */
	public String getMes() {
		return mes;
	}
	/**
	 * @param mes the mes to set
	 */
	public void setMes(String mes) {
		this.mes = mes;
	}
	/**
	 * @return the periodo
	 */
	public String getPeriodo() {
		return periodo;
	}
	/**
	 * @param periodo the periodo to set
	 */
	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}
	/**
	 * @return the proceso
	 */
	public String getProceso() {
		return proceso;
	}
	/**
	 * @param proceso the proceso to set
	 */
	public void setProceso(String proceso) {
		this.proceso = proceso;
	}
	/**
	 * @return the compania
	 */
	public String getCompania() {
		return compania;
	}
	/**
	 * Retorna la variable tipoEmpleado
	 * 
	 * @return  tipoEmpleado
	 */
	public String getTipoEmpleado() {
		return tipoEmpleado;
	}
	/**
	 * Asigna la variable  tipoEmpleado
	 * 
	 * @param  tipoEmpleado
	 * Variable a asignar en  tipoEmpleado
	 */
	public void setTipoEmpleado(String tipoEmpleado) {
		this.tipoEmpleado = tipoEmpleado;
	}
	/**
	 * Retorna el objeto contArchivodatosPila
	 * 
	 * @return contArchivodatosPila
	 */
	public ContenedorArchivo getContArchivodatosPila() {
		return contArchivodatosPila;
	}
	/**
	 * Asigna el objeto contArchivodatosPila
	 * 
	 * @param contArchivodatosPila
	 * Variable a asignar en contArchivodatosPila
	 */
	public void setContArchivodatosPila(ContenedorArchivo contArchivodatosPila) {
		this.contArchivodatosPila = contArchivodatosPila;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listaAnio
	 * 
	 * @return listaAnio
	 */
	public List<Registro> getListaAnio() {
		return listaAnio;
	}
	/**
	 * Asigna la lista listaAnio
	 * 
	 * @param listaAnio
	 * Variable a asignar en  listaAnio
	 */
	public void setListaAnio(List<Registro> listaAnio) {
		this.listaAnio = listaAnio;
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
	/**
	 * Retorna la lista listaCompania
	 * 
	 * @return listaCompania
	 */
	public List<Registro> getListaCompania() {
		return listaCompania;
	}
	/**
	 * Asigna la lista listaCompania
	 * 
	 * @param listaCompania
	 * Variable a asignar en  listaCompania
	 */
	public void setListaCompania(List<Registro> listaCompania) {
		this.listaCompania = listaCompania;
	}
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
	/**
	 * @return the archivoDescarga
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	/**
	 * @param archivoDescarga the archivoDescarga to set
	 */
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}
}
