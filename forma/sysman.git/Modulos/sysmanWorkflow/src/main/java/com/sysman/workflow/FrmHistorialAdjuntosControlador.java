/*-
 * FrmHistorialAdjuntosControlador.java
 *
 * 1.0
 * 
 * 05/02/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.workflow;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.RowEditEvent;
import org.apache.commons.io.FilenameUtils;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.util.SysmanFunciones;
import com.sysman.workflow.enums.DTramiteVariablesControladorEnum;
import com.sysman.workflow.enums.DTramiteVariablesControladorUrlEnum;
import com.sysman.workflow.enums.FrmHistorialAdjuntosControladorUrlEnum;

import net.sf.jasperreports.engine.JRException;

import org.primefaces.model.StreamedContent;
/**
 *
 * @version 1.0, 05/02/2021
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class  FrmHistorialAdjuntosControlador  extends BeanBaseContinuoAcmeImpl{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	private final String modulo;
	//<DECLARAR_ATRIBUTOS>
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;
	private String tramites;
	private String tipoTramites;
	private String proceso;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmHistorialAdjuntosControlador
	 */
	public FrmHistorialAdjuntosControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			numFormulario=2237;
			validarPermisos();

			Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
			if (parametrosEntrada != null) {

				tipoTramites = (String) parametrosEntrada.get("tipoTramite");
				proceso = (String) parametrosEntrada.get("proceso");
				tramites = (String) parametrosEntrada.get("tramite");
			}
			//<INI_ADICIONAL>
			//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			SessionUtil.redireccionarMenuPermisos();
		} finally {
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
		tabla = GenericUrlEnum.D_TRAMITE_VARIABLES.getTable();
		buscarLlave();
		reasignarOrigen();
		registro = new Registro();
		abrirFormulario();

	}
	/**
	 * En este metodo se asigna al atributo origenDatos del bean base
	 * el valor de la consulta del formulario. Tambien carga la lista
	 * del formulario por primera vez
	 */
	@Override
	public void reasignarOrigen(){

		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),compania);
		parametrosListado.put("TIPOTRAMITE", tipoTramites);
		parametrosListado.put("TRAMITE", tramites);
		parametrosListado.put("PROCESO", proceso);


		urlListado = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmHistorialAdjuntosControladorUrlEnum.URL0001 
						.getValue());
	}
	//<METODOS_CARGAR_LISTA>
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * Metodo ejecutado al oprimir el boton BtnAdjunto
	 * 
	 * 
	 * @param reg
	 * registro en el cual esta ubicado el boton oprimido dentro de la
	 * grilla
	 * @param indice
	 * indice en el cual esta ubicado el boton oprimido dentro de la
	 * grilla
	 */
	public void oprimirBtnAdjunto(Registro reg, int indice) {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;        
		
		String ruta = reg.getCampos().get(
				DTramiteVariablesControladorEnum.ADJUNTO.getValue())
				.toString();

		String rutaAbs = JsfUtil.generarRuta(modulo, "",
				FilenameUtils.getFullPath(ruta),
				FilenameUtils.getName(ruta));

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						DTramiteVariablesControladorUrlEnum.URL004
						.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(DTramiteVariablesControladorEnum.TIPO_MIME.getValue(), 37);
		param.put(DTramiteVariablesControladorEnum.ID.getValue(), 6);
		param.put(DTramiteVariablesControladorEnum.EXTENSION.getValue(),
				FilenameUtils.getExtension(ruta));

		Registro auxReg = null;

		try {
			auxReg = RegistroConverter.toRegistro(
					requestManager.get(urlBean.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		// Descargar archivo
		File adjunto = new File(rutaAbs);

		try (InputStream inputStream = new FileInputStream(adjunto)) {
			byte[] vec = new byte[(int) adjunto.length()];

			inputStream.read(vec, 0, vec.length);

			archivoDescarga = JsfUtil.getArchivoDescarga(
					new ByteArrayInputStream(vec), adjunto.getName());
		}
		catch (IOException | JRException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	//</METODOS_COMBOS_GRANDES>
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
	/**
	 * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
	 */
	@Override
	public void cancelarEdicion(RowEditEvent event) {
		getListaInicial().load();
	}
	/**
	 * Metodo ejecutado antes de realizar la insercion del registro
	 * 
	 */
	@Override
	public boolean insertarAntes(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la insercion del registro
	 * 
	 */
	@Override
	public boolean insertarDespues(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado antes de realizar la insercion y actualizacion
	 * del registro
	 * 
	 * 
	 */
	@Override
	public boolean actualizarAntes(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la insercion y actualizacion
	 * del registro
	 * 
	 * 
	 */
	@Override   
	public boolean actualizarDespues(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado antes de realizar la eliminacion del
	 * registro
	 * 
	 * 
	 */
	@Override    
	public boolean eliminarAntes(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la eliminacion del
	 * registro
	 * 
	 * 
	 */
	@Override   
	public boolean eliminarDespues(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Este metodo se ejecuta antes enviar la accion de actualizacion,
	 * en el se pueden remover valores auxiliares que no se desee o se
	 * deban enviar en el registro
	 */
	@Override
	public void removerCombos() {
	}
	/**
	 * Metodo ejecutado cuando se cierra el formulario
	 * 
	 */
	public void cerrarFormulario() {
		RequestContext.getCurrentInstance().closeDialog(null);
	}
	/**
	 * Metodo ejecutado desde un comando remoto cuando se cierra el formulario
	 * 
	 */
	public void ejecutarrcCerrar(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Este metodo es ejecutado despues de finalizar la insercion y
	 * edicion del registro se usa cuando se desean agregar valores
	 * al registro despues de dichas acciones
	 */
	@Override
	public void asignarValoresRegistro()
	{
		// TODO Auto-generated method stub
	}
	//<SET_GET_ATRIBUTOS>
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
