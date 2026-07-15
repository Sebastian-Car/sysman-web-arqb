/*-
 * CambioCajaCompensacionControlador.java
 *
 * 1.0
 * 
 * 02/08/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.CambioCajaCompensacionControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
/**
 *
 * @version 1.0, 02/08/2023
 * @author User 1
 */
@ManagedBean
@ViewScoped
public class  CambioCajaCompensacionControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	//<DECLARAR_ATRIBUTOS>
	private boolean ckEmpleado;
	private boolean ckTodos;
	private String cajaCompensacion;
	private String empleado;
	private Date fechaCambio;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	private RegistroDataModelImpl listaCajaCompensacion;
	private RegistroDataModelImpl listaEmpleado;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de CambioCajaCompensacionControlador
	 */
	public CambioCajaCompensacionControlador() {
		super();
		compania = SessionUtil.getCompania();
		fechaCambio = new Date();
		ckTodos = true;
		empleado = "0";
		try {
			//2420
			numFormulario = GeneralCodigoFormaEnum.CAMBIO_CAJA_COMPENSACION_CONTROLADOR.getCodigo();
			validarPermisos();
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
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListaCajaCompensacion(); cargarListaEmpleado();
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
	 * Carga la lista listaCajaCompensacion
	 *
	 */
	public void cargarListaCajaCompensacion(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						CambioCajaCompensacionControladorUrlEnum.URL644004
						.getValue());

		listaCajaCompensacion = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true,
				"CAJA_COMPENSACION");
	}
	/**
	 * 
	 * Carga la lista listaEmpleado
	 *
	 */
	public void cargarListaEmpleado(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						CambioCajaCompensacionControladorUrlEnum.URL210012
						.getValue());

		listaEmpleado = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true,
				GeneralParameterEnum.ID_DE_EMPLEADO.getName());

	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Actualizar
	 * en la vista
	 *
	 *
	 */
	public void oprimirActualizar() {
		//<CODIGO_DESARROLLADO>
		try {
			UrlBean urlUpdate = null;

			Map<String, Object> param = new TreeMap<>();

			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

			param.put("CAJA_COMPENSACION", cajaCompensacion);

			param.put("FECHACAJACOMPENSACION", fechaCambio);

			param.put(GeneralParameterEnum.MODIFIED_BY.getName(),
					SessionUtil.getUser().getCodigo());

			param.put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());



			if(ckTodos) {

				urlUpdate = UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(CambioCajaCompensacionControladorUrlEnum.URL210161.getValue());

			}else {

				urlUpdate = UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(CambioCajaCompensacionControladorUrlEnum.URL210162.getValue());

				param.put(GeneralParameterEnum.ID_DE_EMPLEADO.getName(), empleado);				

			}

			Parameter parameter = new Parameter();

			parameter.setFields(param);

			String a = Integer
					.toString(requestManager.update(urlUpdate.getUrl(),
							urlUpdate.getMetodo(),
							parameter));

			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4438")
					.replace("#$rta$#", a));

		} catch (SystemException ex) {
			Logger.getLogger(CambioCajaCompensacionControlador.class.getName())
			.log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
					idioma.getString("MSM_TRANS_INTERRUMPIDA"), " ",
					ex.getMessage()));
		}

		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control ckEmpleado
	 * 
	 * 
	 */
	public void cambiarckEmpleado() {
		//<CODIGO_DESARROLLADO>
		if(ckEmpleado) {
			ckTodos = false;
			empleado = null;
		}
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control ckTodos
	 * 
	 * 
	 */
	public void cambiarckTodos() {
		//<CODIGO_DESARROLLADO>
		if(ckTodos) {
			ckEmpleado = false;
			empleado = "0";
			
		}
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCajaCompensacion
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCajaCompensacion(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cajaCompensacion= registroAux.getCampos().get("CAJA_COMPENSACION").toString();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaEmpleado
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaEmpleado(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		empleado = registroAux.getCampos().get(GeneralParameterEnum.ID_DE_EMPLEADO.getName()).toString();
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable ckEmpleado
	 * 
	 * @return  ckEmpleado
	 */
	public boolean getCkEmpleado() {
		return ckEmpleado;
	}
	/**
	 * Asigna la variable  ckEmpleado
	 * 
	 * @param  ckEmpleado
	 * Variable a asignar en  ckEmpleado
	 */
	public void setCkEmpleado(boolean ckEmpleado) {
		this.ckEmpleado = ckEmpleado;
	}
	/**
	 * Retorna la variable ckTodos
	 * 
	 * @return  ckTodos
	 */
	public boolean getCkTodos() {
		return ckTodos;
	}
	/**
	 * Asigna la variable  ckTodos
	 * 
	 * @param  ckTodos
	 * Variable a asignar en  ckTodos
	 */
	public void setCkTodos(boolean ckTodos) {
		this.ckTodos = ckTodos;
	}
	/**
	 * Retorna la variable cajaCompensacion
	 * 
	 * @return  cajaCompensacion
	 */
	public String getCajaCompensacion() {
		return cajaCompensacion;
	}
	/**
	 * Asigna la variable  cajaCompensacion
	 * 
	 * @param  cajaCompensacion
	 * Variable a asignar en  cajaCompensacion
	 */
	public void setCajaCompensacion(String cajaCompensacion) {
		this.cajaCompensacion = cajaCompensacion;
	}
	/**
	 * Retorna la variable empleado
	 * 
	 * @return  empleado
	 */
	public String getEmpleado() {
		return empleado;
	}
	/**
	 * Asigna la variable  empleado
	 * 
	 * @param  empleado
	 * Variable a asignar en  empleado
	 */
	public void setEmpleado(String empleado) {
		this.empleado = empleado;
	}
	/**
	 * Retorna la variable fechaCambio
	 * 
	 * @return  fechaCambio
	 */
	public Date getFechaCambio() {
		return fechaCambio;
	}
	/**
	 * Asigna la variable  fechaCambio
	 * 
	 * @param  fechaCambio
	 * Variable a asignar en  fechaCambio
	 */
	public void setFechaCambio(Date fechaCambio) {
		this.fechaCambio = fechaCambio;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaCajaCompensacion
	 * 
	 * @return listaCajaCompensacion
	 */
	public RegistroDataModelImpl getListaCajaCompensacion() {
		return listaCajaCompensacion;
	}
	/**
	 * Asigna la lista listaCajaCompensacion
	 * 
	 * @param listaCajaCompensacion
	 * Variable a asignar en  listaCajaCompensacion
	 */
	public void setListaCajaCompensacion(RegistroDataModelImpl listaCajaCompensacion) {
		this.listaCajaCompensacion = listaCajaCompensacion;
	}
	/**
	 * Retorna la lista listaEmpleado
	 * 
	 * @return listaEmpleado
	 */
	public RegistroDataModelImpl getListaEmpleado() {
		return listaEmpleado;
	}
	/**
	 * Asigna la lista listaEmpleado
	 * 
	 * @param listaEmpleado
	 * Variable a asignar en  listaEmpleado
	 */
	public void setListaEmpleado(RegistroDataModelImpl listaEmpleado) {
		this.listaEmpleado = listaEmpleado;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
