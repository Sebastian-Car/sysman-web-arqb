/*-
 * EliminardetallecntlotesControlador.java
 *
 * 1.0
 * 
 * 11/03/2026
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.EliminarDetalleCntLoteControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 * Formulario que permite eliminar los detalles de comprobante
 * CONTABILIDAD en lote
 *
 * @version 1.0, 11/03/2026
 * @author NCARDENAS
 */
@ManagedBean
@ViewScoped
public class  EliminardetallecntlotesControlador extends BeanBaseModal{

	private final String compania ;
	private String anioComprobante;
	private String numeroComprobante;
	private String tipoComprobante;
	//<DECLARAR_ATRIBUTOS>
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>

	private RegistroDataModelImpl listaDetalle;
	//</DECLARAR_LISTAS_COMBO_GRANDE>


	public EliminardetallecntlotesControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario=GeneralCodigoFormaEnum.ELIMINAR_DETALLE_CNT_LOTE_CONTROLADOR
					.getCodigo();

			Map<String, Object> parametros = SessionUtil.getFlash();
			if (parametros != null) {

				numeroComprobante = parametros.get("comprobante").toString();
				anioComprobante = parametros.get("anio").toString();
				tipoComprobante = parametros.get("tipo").toString();
			}
			validarPermisos();

		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			SessionUtil.redireccionarMenuPermisos();
		}finally{
		}
	}

	@PostConstruct
	public void inicializar(){
		cargarListaDetalle();
		abrirFormulario();
	}

	@Override
	public void abrirFormulario(){

	}

	public void cargarListaDetalle(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						EliminarDetalleCntLoteControladorUrlEnum.URL39126
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		try {
			param.put(GeneralParameterEnum.COMPANIA.getName(),
					compania);
			param.put(GeneralParameterEnum.ANO.getName(),
					anioComprobante);
			param.put(GeneralParameterEnum.TIPO.getName(),
					tipoComprobante);
			param.put(GeneralParameterEnum.NUMERO.getName(),
					numeroComprobante);

			listaDetalle = new RegistroDataModelImpl(urlBean.getUrl(),
					urlBean.getUrlConteo().getUrl(), param, false,
					CacheUtil.getLlaveServicio(urlConexionCache,
							"DETALLE_COMPROBANTE_CNT"),
					true);
		}
		catch (SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void oprimirCerrar() {
		RequestContext.getCurrentInstance().closeDialog(null);
	}


	public void oprimirAceptar() {
		
		List<Registro> listaSeleccionados;
		if (listaDetalle.getSeleccionados().isEmpty()) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB686"));
		}
		else {

			listaSeleccionados = listaDetalle.getSeleccionados();

			for (Registro reg : listaSeleccionados) {

				UrlBean urlDelete = UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(
								GenericUrlEnum.DETALLECOMPROBANTECNT
								.getDeleteKey());
				try {
					requestManager.delete(urlDelete.getUrl(), reg.getLlave());
				}
				catch (SystemException e) {
					logger.error(e.getMessage(), e);
					JsfUtil.agregarMensajeError(e.getMessage());
				}

			}

			listaDetalle.getSeleccionados().clear();
			cargarListaDetalle();
			eliminarCpteWs();
			
		//  forzar refresh de ambas tablas desde Java
	        RequestContext.getCurrentInstance().execute(
	            "PF('LM35').clearFilters();" +
	            "PF('LM35s').clearFilters();" +
	            "PF('LM35').filter();" +
	            "PF('LM35s').filter();"
	        );
		}

	}

	private void eliminarCpteWs() {
		// se elimina el detalle del comprobante pero de la tabla detalle_cpte_afect_ws
		try {
			int delete = 0;

			Map<String, Object> param2 = new TreeMap<>();
			param2.put(GeneralParameterEnum.KEY_COMPANIA.getName(), compania);
			param2.put(GeneralParameterEnum.KEY_ANO.getName(), anioComprobante);
			param2.put(GeneralParameterEnum.KEY_COMPROBANTE.getName(), numeroComprobante);
			param2.put(GeneralParameterEnum.KEY_TIPO_CPTE.getName(), tipoComprobante);

			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(EliminarDetalleCntLoteControladorUrlEnum.URL1914001.getValue());


			delete = requestManager.delete(urlBean.getUrl(), param2);
		} catch (SystemException e) {
			Logger.getLogger(EliminardetallecntlotesControlador.class
					.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void oprimirSeleccionarTodo() {
	    
	    listaDetalle.setSeleccionados(new HashMap<>(listaDetalle.getParams()));

	   
	    for (Registro reg : listaDetalle.getSeleccionados()) {
	        reg.getCampos().put("SELECCIONADO_EN_PARTE_GRAFICA", true);
	    }

	    RequestContext.getCurrentInstance().execute(
	        "PF('LM35').clearFilters();PF('LM35s').clearFilters();" +
	        "PF('LM35').filter();PF('LM35s').filter();"
	    );
	}

	
	public void oprimirBorrarSeleccion () {
		// limpiar listas internas
	    listaDetalle.getSeleccionados().clear();
	    listaDetalle.getLlavesSeleccionadas().clear();

	    // limpiar indicador visual en los registros cargados
	    for (Object obj : (List<?>) listaDetalle.getWrappedData()) {
	        Registro reg = (Registro) obj;
	        reg.getCampos().put("SELECCIONADO_EN_PARTE_GRAFICA", false);
	    }

	}
	public void seleccionarFilaDetalle(SelectEvent event) {
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaDetalle
	 * 
	 * @return listaDetalle
	 */
	public RegistroDataModelImpl getListaDetalle() {
		return listaDetalle;
	}

	public void setListaDetalle(RegistroDataModelImpl listaDetalle) {
		this.listaDetalle = listaDetalle;
	}

	//</SET_GET_LISTAS_COMBO_GRANDE>
}
