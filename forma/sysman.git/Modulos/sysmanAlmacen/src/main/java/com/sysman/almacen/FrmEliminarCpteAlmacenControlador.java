/*-
 * FrmEliminarCpteAlmacenControlador.java
 *
 * 1.0
 * 
 * 03/10/2024
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;

import com.sysman.almacen.ejb.EjbAlmacenUnoRemote;
import com.sysman.almacen.enums.FrmEliminarCpteAlmacenControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;


/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 03/10/2024
 * @author User
 */
@ManagedBean
@ViewScoped
public class  FrmEliminarCpteAlmacenControlador extends BeanBaseModal{

	private final String compania ;

	private int anio;

	private String tipo;

	private String numero;

	private String msjError;

	private List<Registro> listaAno;

	private RegistroDataModelImpl listaTipo;

	private RegistroDataModelImpl listaNumero;

	@EJB
	private EjbAlmacenUnoRemote ejbAlmacenUno;
	/**
	 * Crea una nueva instancia de FrmEliminarCpteAlmacenControlador
	 */
	public FrmEliminarCpteAlmacenControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario = GeneralCodigoFormaEnum.ELIMINAR_MOV_ALMACEN_CONTROLADOR.getCodigo();           
			validarPermisos();
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			SessionUtil.redireccionarMenuPermisos();
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
		cargarListaAno();
		cargarListaTipo(); 
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
	/**
	 * 
	 * Carga la lista listaAno
	 *
	 */
	public void cargarListaAno(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaAno = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmEliminarCpteAlmacenControladorUrlEnum.URL4001
											.getValue())
									.getUrl(),
									param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listaTipo
	 *
	 */
	public void cargarListaTipo(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmEliminarCpteAlmacenControladorUrlEnum.URL139003
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaTipo = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}
	/**
	 * 
	 * Carga la lista listaNumero
	 *
	 */
	public void cargarListaNumero(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmEliminarCpteAlmacenControladorUrlEnum.URL41002
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.TIPO.getName(), tipo);

		listaNumero = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.NUMERO.getName());
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Eliminar
	 * en la vista
	 *
	 *
	 */
	public void oprimirEliminar() { 
		
	Map<String, Object> param = new TreeMap<>();
    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    param.put(GeneralParameterEnum.TIPOMOVIMIENTO.getName(), tipo);
    param.put(GeneralParameterEnum.MOVIMIENTO.getName(), numero);

		try {
			

	        Registro rsExiste = RegistroConverter.toRegistro(
	                        requestManager.get(UrlServiceUtil.getInstance()
	                                        .getUrlServiceByUrlByEnumID(
	                                        		FrmEliminarCpteAlmacenControladorUrlEnum.URL119014
	                                                                        .getValue())
	                                        .getUrl(), param));

	        if (rsExiste != null)
	        {
	        	ejbAlmacenUno.eliminarMovimientos(compania, 
	        			anio, tipo, numero);
	        	JsfUtil.agregarMensajeInformativo(
	        			idioma.getString("MSM_PROCESO_EJECUTADO"));
	        	
	        }else {	 
	        	
	        	 JsfUtil.agregarMensajeError("No existen detalles en el movimiento");
	        	
	        }
	        
		}
		catch (NumberFormatException | SystemException e) {
			msjError=e.getMessage(); 
			msjError = extractErrorMessage(msjError);
			JsfUtil.agregarMensajeError(msjError);

		}
	}
	/**
	 * Metodo ejecutado al cambiar el control Ano
	 * 
	 */
	public void cambiarAno() {
		numero = null;
		cargarListaNumero();
	}

	public static String extractErrorMessage(String input) {
		int startIndex = input.indexOf("@#INI#@Log:")  + "@#INI#@".length();
		int endIndex = input.indexOf("@#FIN#@");
		String msj = "";

		if (startIndex != -1 && endIndex != -1) {
			String logContent = input.substring(startIndex, endIndex);
			String[] lines = logContent.split("\n");
			for (String line : lines) {
				msj += line.trim() + "\n ";

				if (msj.endsWith("\n")) {
					msj = msj.substring(0, msj.length() - 1);
				}

			}
		}else {
			msj = input;
			
		}
		return msj;
	}
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaTipo
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTipo(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		tipo = SysmanFunciones.nvl(
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()),
				" ").toString();
		numero = null;
		cargarListaNumero();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaNumero
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaNumero(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		numero = !("").equals(registroAux.getCampos()
				.get(GeneralParameterEnum.NUMERO.getName()))
				? (registroAux.getCampos()
						.get(GeneralParameterEnum.NUMERO
								.getName())).toString()
						: "";
	}
	/**
	 * Retorna la variable anio
	 * 
	 * @return  anio
	 */
	public int getAnio() {
		return anio;
	}
	/**
	 * Asigna la variable  anio
	 * 
	 * @param  anio
	 * Variable a asignar en  anio
	 */
	public void setAnio(int anio) {
		this.anio = anio;
	}
	/**
	 * Retorna la variable tipo
	 * 
	 * @return  tipo
	 */
	public String getTipo() {
		return tipo;
	}
	/**
	 * Asigna la variable  tipo
	 * 
	 * @param  tipo
	 * Variable a asignar en  tipo
	 */
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	/**
	 * Retorna la variable numero
	 * 
	 * @return  numero
	 */
	public String getNumero() {
		return numero;
	}
	/**
	 * Asigna la variable  numero
	 * 
	 * @param  numero
	 * Variable a asignar en  numero
	 */
	public void setNumero(String numero) {
		this.numero = numero;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
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
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaTipo
	 * 
	 * @return listaTipo
	 */
	public RegistroDataModelImpl getListaTipo() {
		return listaTipo;
	}
	/**
	 * Asigna la lista listaTipo
	 * 
	 * @param listaTipo
	 * Variable a asignar en  listaTipo
	 */
	public void setListaTipo(RegistroDataModelImpl listaTipo) {
		this.listaTipo = listaTipo;
	}
	/**
	 * Retorna la lista listaNumero
	 * 
	 * @return listaNumero
	 */
	public RegistroDataModelImpl getListaNumero() {
		return listaNumero;
	}
	/**
	 * Asigna la lista listaNumero
	 * 
	 * @param listaNumero
	 * Variable a asignar en  listaNumero
	 */
	public void setListaNumero(RegistroDataModelImpl listaNumero) {
		this.listaNumero = listaNumero;
	}
}
