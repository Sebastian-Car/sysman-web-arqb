/*-
 * SeguimientoMensualControlador.java
 *
 * 1.0
 * 
 * 26/04/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.bancoproyectos;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;
import com.sysman.bancoproyectos.enums.SeguimientoMensualControladorEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.sesion.SessionBean;
import com.sysman.util.SysmanFunciones;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.model.StreamedContent;

/**
 *
 * @version 1.0, 26/04/2021
 * @author gfigueredo
 */
@ManagedBean
@ViewScoped
public class SeguimientoMensualControlador extends BeanBaseModal {
	
	@EJB
	private EjbSysmanUtilRemote sysmanUtil;
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	//<DECLARAR_ATRIBUTOS>
	
	/**
	 * Constante usada para realizar busqueda de proyectos por codigo.
	 */
	private static final String CODIGO = "CODIGO";
	/**
	 * Variable usada para almacenar el valor del radiobutton (selección del reporte)
	 */
	private String opcion;

	/**
	 * Variable que almacena el valor del módulo
	 */
	private final String modulo;
	/**
	 * Variable que almacena el valor del proyectoInicial
	 */
	private String proyectoInicial;
	/**
	 * Varible que almacena el valor del proyectoFinal
	 */
	private String proyectoFinal;
	/**
	 * Varible que almacena el valor de la vigencia
	 */
	private String vigencia;
	/**
	 * Varible que almacena el valor del mes
	 */
	private String mes;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;
	/**
	 * Lista que alamcena los datos de las vigencias
	 */
	private List<Registro> listaVigencia;
	/**
	 * Lista que almacena los datos del mes
	 */
	private List<Registro> listaMes;
	/**
	 * Lista que almacena los datos de los proyectos
	 */
	private RegistroDataModelImpl listaProyectoinicial;
	/**
	 * Lista que almacena los datos de los proyectos
	 */
	private RegistroDataModelImpl listaProyectofinal;

	@EJB
	private EjbSysmanUtilRemote ejSysmanUtil;

	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de SeguimientoMensualControlador
	 */
	public SeguimientoMensualControlador() {
		super();
		compania = SessionUtil.getCompania();

		modulo = SessionUtil.getModulo();
		vigencia = String.valueOf(SysmanFunciones
				.ano(new Date()));
		mes = String.valueOf(SysmanFunciones
				.mes(new Date()));
		opcion = "1";
		proyectoInicial = "0";
		proyectoFinal = "99999999";

		try {
			numFormulario = 2267;
			validarPermisos();
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		} 
	}

	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha
	 * sido creado, en este se realizan las asignaciones iniciales necesarias para
	 * la visualizacion del formulario, como son tablas, origenes de datos,
	 * inicializacion de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar() {
		//<CARGAR_LISTA>
		cargarListaVigencia();
		cargarListaMes();
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListaProyectoinicial();
		cargarListaProyectofinal();
		//</CARGAR_LISTA_COMBO_GRANDE>
		//<CREAR_ARBOLES>
		//</CREAR_ARBOLES>
		abrirFormulario();
	}

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaVigencia
	 *
	 */

	public void cargarListaVigencia() {
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			listaVigencia = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											SeguimientoMensualControladorEnum.URL0003.getValue())
									.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaMes
	 *
	 */
	public void cargarListaMes() {
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ANO.getName(), vigencia);
			listaMes = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											SeguimientoMensualControladorEnum.URL0004.getValue())
									.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaProyectoinicial
	 *
	 */
	public void cargarListaProyectoinicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(SeguimientoMensualControladorEnum.URL0001.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaProyectoinicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				CODIGO);
	}

	/**
	 * 
	 * Carga la lista listaProyectofinal
	 *
	 */
	public void cargarListaProyectofinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(SeguimientoMensualControladorEnum.URL0002.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("NUMERO", proyectoInicial);

		listaProyectofinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				CODIGO);
	}

	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton PDF en la vista
	 *
	 *
	 */
	public void oprimirPDF() {
		generarReporte(FORMATOS.PDF);
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton EXCEL en la vista
	 *
	 *
	 */
	public void oprimirEXCEL() {
		generarReporte(FORMATOS.EXCEL);
	}

	public void generarReporte(FORMATOS formatos) {

		try {
			archivoDescarga = null;
			String reporte = "";
			HashMap<String, Object> reemplazar = new HashMap<>();
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("PR_NOMBREEMPRESA", SessionUtil.getCompaniaIngreso().getNombre());
			switch (opcion)
			{
			case "1":
				reporte = "002257InformePorVigenciaSeguimientoMensual";
				reemplazar.put("proyectoI", proyectoInicial);
				reemplazar.put("proyectoF", proyectoFinal);
				if (vigencia.equals("TODAS"))
					vigencia = "null";
				reemplazar.put("vigencia", vigencia);
				reemplazar.put("mes", mes);
				String parametro = getParametro("NUMERO DE DIGITOS META-PRODUCTO", "12");
				reemplazar.put("valorParam", parametro);
				break;
			case "2":
				reporte = "002258InformePorProyectoSeguimientoMensual";
				reemplazar.put("proyectoI", proyectoInicial);
				reemplazar.put("proyectoF", proyectoFinal);
				parametros.put("PR_VIGENCIA", "Vigencia: " + vigencia);
				if (vigencia.equals("TODAS"))
					vigencia = "null";
				reemplazar.put("vigencia", vigencia);
				parametros.put("PR_TITULO_1", "Entre el proyecto :" + proyectoInicial + " y el " + proyectoFinal);
				break;
			default:
				break;
			}



			parametros.put("PR_AHORA", new Date());

			// IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz muñoz)
            parametros.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
            // FIN IMPLEMENTACION MARCA_BLANCA

			Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo), reemplazar, parametros);

			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formatos);

                } catch (JRException | IOException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			Logger.getLogger(SeguimientoMensualControladorEnum.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	
	private String getParametro(String nombreParametro, String valorDefault) {
		String parametro = null;
		try {
			parametro = sysmanUtil.consultarParametro(compania, nombreParametro, SessionUtil.getModulo(), new Date(),
					true);
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return parametro != null ? parametro : valorDefault;
	}

	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control Vigencia
	 * 
	 * 
	 */
	public void cambiarVigencia() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control opciones
	 * 
	 * 
	 */
	public void cambiaropciones() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaProyectoinicial
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaProyectoinicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		proyectoInicial = SysmanFunciones.nvl(registroAux.getCampos().get(CODIGO), "").toString();

		cargarListaProyectofinal();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaProyectofinal
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaProyectofinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		proyectoFinal = SysmanFunciones.nvl(registroAux.getCampos().get(CODIGO), "").toString();
	}

	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable opcion
	 * 
	 * @return opcion
	 */
	public String getOpcion() {
		return opcion;
	}

	/**
	 * Asigna la variable opcion
	 * 
	 * @param opcion Variable a asignar en opcion
	 */
	public void setOpcion(String opcion) {
		this.opcion = opcion;
	}

	/**
	 * Retorna la variable proyectoInicial
	 * 
	 * @return proyectoInicial
	 */
	public String getProyectoInicial() {
		return proyectoInicial;
	}

	/**
	 * Asigna la variable proyectoInicial
	 * 
	 * @param proyectoInicial Variable a asignar en proyectoInicial
	 */
	public void setProyectoInicial(String proyectoInicial) {
		this.proyectoInicial = proyectoInicial;
	}

	/**
	 * Retorna la variable proyectoFinal
	 * 
	 * @return proyectoFinal
	 */
	public String getProyectoFinal() {
		return proyectoFinal;
	}

	/**
	 * Asigna la variable proyectoFinal
	 * 
	 * @param proyectoFinal Variable a asignar en proyectoFinal
	 */
	public void setProyectoFinal(String proyectoFinal) {
		this.proyectoFinal = proyectoFinal;
	}

	/**
	 * Retorna la variable vigencia
	 * 
	 * @return vigencia
	 */
	public String getVigencia() {
		return vigencia;
	}

	/**
	 * Asigna la variable vigencia
	 * 
	 * @param vigencia Variable a asignar en vigencia
	 */
	public void setVigencia(String vigencia) {
		this.vigencia = vigencia;
	}

	/**
	 * Retorna la variable mes
	 * 
	 * @return mes
	 */
	public String getMes() {
		return mes;
	}

	/**
	 * Asigna la variable mes
	 * 
	 * @param mes Variable a asignar en mes
	 */
	public void setMes(String mes) {
		this.mes = mes;
	}

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listaVigencia
	 * 
	 * @return listaVigencia
	 */
	public List<Registro> getListaVigencia() {
		return listaVigencia;
	}

	/**
	 * Asigna la lista listaVigencia
	 * 
	 * @param listaVigencia Variable a asignar en listaVigencia
	 */
	public void setListaVigencia(List<Registro> listaVigencia) {
		this.listaVigencia = listaVigencia;
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
	 * @param listaMes Variable a asignar en listaMes
	 */
	public void setListaMes(List<Registro> listaMes) {
		this.listaMes = listaMes;
	}

	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaProyectoinicial
	 * 
	 * @return listaProyectoinicial
	 */
	public RegistroDataModelImpl getListaProyectoinicial() {
		return listaProyectoinicial;
	}

	/**
	 * Asigna la lista listaProyectoinicial
	 * 
	 * @param listaProyectoinicial Variable a asignar en listaProyectoinicial
	 */
	public void setListaProyectoinicial(RegistroDataModelImpl listaProyectoinicial) {
		this.listaProyectoinicial = listaProyectoinicial;
	}

	/**
	 * Retorna la lista listaProyectofinal
	 * 
	 * @return listaProyectofinal
	 */
	public RegistroDataModelImpl getListaProyectofinal() {
		return listaProyectofinal;
	}

	/**
	 * Asigna la lista listaProyectofinal
	 * 
	 * @param listaProyectofinal Variable a asignar en listaProyectofinal
	 */
	public void setListaProyectofinal(RegistroDataModelImpl listaProyectofinal) {
		this.listaProyectofinal = listaProyectofinal;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
