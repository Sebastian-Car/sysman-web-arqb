/*-
 * FrmArmonizarPdControlador.java
 *
 * 1.0
 * 
 * 12/03/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.bancoproyectos;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.sysman.bancoproyectos.ejb.impl.EjbBancoProyectoCero;
import com.sysman.bancoproyectos.enums.FrmArmonizarPdControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;

/**
 * Migración del formulario frm_armonizarpd de access a web con el controlador
 * FrmArmonizarPdControlador
 *
 * @version 1.0, 12/03/2018
 * @author jhernandez
 */
@ManagedBean
@ViewScoped
public class FrmArmonizarPdControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la
	 * cual inicio sesion el usuario, el valor de esta constante es asignado en
	 * el constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	/**
	 * Variable de clase para manejar la seleccion de una vigencia que se toma
	 * desde el combo lista vigencia.
	 */
	private int vigencia;
	@EJB
	private EjbBancoProyectoCero ejbBancoproyectos;
	// <DECLARAR_ATRIBUTOS>
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	/**
	 * Variable que permite manejar una lista de vigencias.
	 */
	private List<Registro> listaVigenciaInicial;

	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	// </DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmArmonizarPdControlador
	 */
	public FrmArmonizarPdControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario = 1743;
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la clase del
	 * Bean ha sido creado, en este se realizan las asignaciones iniciales
	 * necesarias para la visualizacion del formulario, como son tablas,
	 * origenes de datos, inicializacion de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar() {
		// <CARGAR_LISTA>
		cargarListaVigenciaInicial();
		// </CARGAR_LISTA>
		// <CARGAR_LISTA_COMBO_GRANDE>
		// </CARGAR_LISTA_COMBO_GRANDE>
		// <CREAR_ARBOLES>
		// </CREAR_ARBOLES>
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

	// <METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaVigenciaInicial
	 *
	 */
	public void cargarListaVigenciaInicial() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaVigenciaInicial = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													FrmArmonizarPdControladorUrlEnum.URL001.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Aceptar en la vista
	 *
	 *
	 */
	public void oprimirAceptar() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		armonizarPd();
	}

	/**
	 * Llamado al paquete que contiene el procedimiento para armonizar plan de
	 * desarrollo
	 */
	private void armonizarPd() {
		// <CODIGO_DESARROLLADO>
		if (vigencia != 0) {
			try {
				ejbBancoproyectos.armonizarPd(compania, vigencia);
				JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));
			} catch (SystemException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
		}
		// </CODIGO_DESARROLLADO>
	}

	// </METODOS_BOTONES>
	// <METODOS_CAMBIAR>
	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	// </METODOS_COMBOS_GRANDES>
	// <METODOS_ARBOL>
	// </METODOS_ARBOL>
	// <SET_GET_ATRIBUTOS>
	// </SET_GET_ATRIBUTOS>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_LISTAS>
	/**
	 * Retorna la lista listaVigenciaInicial
	 * 
	 * @return listaVigenciaInicial
	 */
	public List<Registro> getListaVigenciaInicial() {
		return listaVigenciaInicial;
	}

	/**
	 * Asigna la lista listaVigenciaInicial
	 * 
	 * @param listaVigenciaInicial
	 *            Variable a asignar en listaVigenciaInicial
	 */
	public void setListaVigenciaInicial(List<Registro> listaVigenciaInicial) {
		this.listaVigenciaInicial = listaVigenciaInicial;
	}

	/**
	 * @return the vigencia
	 */
	public int getVigencia() {
		return vigencia;
	}

	/**
	 * @param vigencia
	 *            the vigencia to set
	 */
	public void setVigencia(int vigencia) {
		this.vigencia = vigencia;
	}
	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	// </SET_GET_LISTAS_COMBO_GRANDE>
}
