/*-
 * FrmpasarplancntControlador.java
 *
 * 1.0
 * 
 * 01/11/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.FrmpasarplancntControladorUrlEnum;
import com.sysman.jsfutil.ArchivosBean;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbPrepararAnoRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 01/11/2023
 * @author avega
 */
@ManagedBean
@ViewScoped
public class  FrmpasarplancntControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;

	private String anioOrigen;

	private String companiaOrigen;

	private String anoDestino;

	private String companiaDestino;

	private List<Registro> listaanoOrigen;

	private List<Registro> listaanoDestino;

	private RegistroDataModelImpl listacompaniaDestino;

	private RegistroDataModelImpl listacompaniaOrigen;

	
	@EJB
    private EjbPrepararAnoRemote ejbPrepararAno;
	
	public FrmpasarplancntControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario=GeneralCodigoFormaEnum.FRM_PASAR_PLAN_CNT_CONTROLADOR
                    .getCodigo();
			validarPermisos();
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			SessionUtil.redireccionarMenuPermisos();
		}finally{
		}
	}

	@PostConstruct
	public void inicializar(){		
		cargarListaanoOrigen();
		cargarListaanoDestino();
		cargarListacompaniaDestino();
		cargarListacompaniaOrigen();
		abrirFormulario();
	}

	@Override
	public void abrirFormulario(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}

	public void cargarListaanoOrigen(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaanoOrigen = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmpasarplancntControladorUrlEnum.URL4001
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaanoDestino(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaanoDestino = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmpasarplancntControladorUrlEnum.URL4001
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListacompaniaDestino(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmpasarplancntControladorUrlEnum.URL59021
						.getValue());
		listacompaniaDestino = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), null,
				true, "CODIGO");



	}

	public void cargarListacompaniaOrigen(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmpasarplancntControladorUrlEnum.URL59021
						.getValue());
		listacompaniaOrigen = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), null,
				true, "CODIGO");
	}

	public void oprimirbtnIniciar() {
		try {			

			int anoD = Integer.parseInt(anoDestino);
			int anoO = Integer.parseInt(anioOrigen);
			
			ejbPrepararAno.copiarPlanContable(companiaOrigen, anoD , anoO ,
					companiaDestino);	
		
			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));	
		}

		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			
			Map<String, Object> param = new HashMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), companiaDestino);
			param.put("PREPARAANO",anoDestino);
			Registro pAnio;

			try {
				pAnio = RegistroConverter.toRegistro(
						requestManager.get(UrlServiceUtil.getInstance()
								.getUrlServiceByUrlByEnumID(
										FrmpasarplancntControladorUrlEnum.URL4014
										.getValue())
								.getUrl(), param));
				int existeAno = Integer.parseInt(
						"" + pAnio.getCampos().get("NUMERO") + "");
				if (0 == existeAno) {
					JsfUtil.agregarMensajeAlerta(idioma.getString("MSM_VIGENCIA_NO_CONFIGURADA"));
				}
			} catch (SystemException e1) {
				e1.printStackTrace();
			}
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void seleccionarFilacompaniaDestino(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		companiaDestino= SysmanFunciones
				.nvl(registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()),
						"")
				.toString();
	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listacompaniaOrigen
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacompaniaOrigen(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		companiaOrigen= SysmanFunciones
				.nvl(registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()),
						"")
				.toString();
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>

	public String getAnioOrigen() {
		return anioOrigen;
	}

	public void setAnioOrigen(String anioOrigen) {
		this.anioOrigen = anioOrigen;
	}

	public String getCompaniaOrigen() {
		return companiaOrigen;
	}

	public void setCompaniaOrigen(String companiaOrigen) {
		this.companiaOrigen = companiaOrigen;
	}

	public String getAnoDestino() {
		return anoDestino;
	}

	public void setAnoDestino(String anoDestino) {
		this.anoDestino = anoDestino;
	}

	public String getCompaniaDestino() {
		return companiaDestino;
	}

	public void setCompaniaDestino(String companiaDestino) {
		this.companiaDestino = companiaDestino;
	}

	public String getCompania() {
		return compania;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listaanoOrigen
	 * 
	 * @return listaanoOrigen
	 */
	public List<Registro> getListaanoOrigen() {
		return listaanoOrigen;
	}
	/**
	 * Asigna la lista listaanoOrigen
	 * 
	 * @param listaanoOrigen
	 * Variable a asignar en  listaanoOrigen
	 */
	public void setListaanoOrigen(List<Registro> listaanoOrigen) {
		this.listaanoOrigen = listaanoOrigen;
	}
	/**
	 * Retorna la lista listaanoDestino
	 * 
	 * @return listaanoDestino
	 */
	public List<Registro> getListaanoDestino() {
		return listaanoDestino;
	}
	/**
	 * Asigna la lista listaanoDestino
	 * 
	 * @param listaanoDestino
	 * Variable a asignar en  listaanoDestino
	 */
	public void setListaanoDestino(List<Registro> listaanoDestino) {
		this.listaanoDestino = listaanoDestino;
	}
	/**
	 * Retorna la lista listacompaniaDestino
	 * 
	 * @return listacompaniaDestino
	 */
	public RegistroDataModelImpl getListacompaniaDestino() {
		return listacompaniaDestino;
	}
	/**
	 * Asigna la lista listacompaniaDestino
	 * 
	 * @param listacompaniaDestino
	 * Variable a asignar en  listacompaniaDestino
	 */
	public void setListacompaniaDestino(RegistroDataModelImpl listacompaniaDestino) {
		this.listacompaniaDestino = listacompaniaDestino;
	}
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listacompaniaOrigen
	 * 
	 * @return listacompaniaOrigen
	 */
	public RegistroDataModelImpl getListacompaniaOrigen() {
		return listacompaniaOrigen;
	}
	/**
	 * Asigna la lista listacompaniaOrigen
	 * 
	 * @param listacompaniaOrigen
	 * Variable a asignar en  listacompaniaOrigen
	 */
	public void setListacompaniaOrigen(RegistroDataModelImpl listacompaniaOrigen) {
		this.listacompaniaOrigen = listacompaniaOrigen;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
