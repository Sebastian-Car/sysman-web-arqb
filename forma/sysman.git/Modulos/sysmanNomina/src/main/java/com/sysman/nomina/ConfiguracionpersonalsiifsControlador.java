/*-
 * ConfiguracionpersonalsiifsControlador.java
 *
 * 1.0
 * 
 * 21/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.ConfiguracionpersonalsiifsControladorEnum;

/**
 * Formulario para Configuración personal de nómina SIIF.
 *
 * @version 1.0, 21/02/2018
 * @author fperez
 */
@ManagedBean
@ViewScoped
public class ConfiguracionpersonalsiifsControlador extends BeanBaseContinuoAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la
	 * cual inicio sesion el usuario, el valor de esta constante es asignado en
	 * el constructor a la variable de sesion correspondiente
	 */
	private final String compania;

	// <DECLARAR_ATRIBUTOS>
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	// </DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de ConfiguracionpersonalsiifsControlador
	 */
	public ConfiguracionpersonalsiifsControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			/** Formulario no 1716. */
			numFormulario = GeneralCodigoFormaEnum.PERSONA_SIIF_CONTROLADOR.getCodigo();
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

		enumBase = GenericUrlEnum.PERSONAL;
		buscarLlave();
		reasignarOrigen();
		registro = new Registro();
		// <CARGAR_LISTA>
		// </CARGAR_LISTA>
		// <CARGAR_LISTA_COMBO_GRANDE>
		// </CARGAR_LISTA_COMBO_GRANDE>
		abrirFormulario();

	}

	/**
	 * En este metodo se asigna al atributo origenDatos del bean base el valor
	 * de la consulta del formulario. Tambien carga la lista del formulario por
	 * primera vez
	 */
	@Override
	public void reasignarOrigen() {
		buscarUrls();
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID("210120");
		urlActualizacion = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID("210122");
	}

	// <METODOS_CARGAR_LISTA>
	// </METODOS_CARGAR_LISTA>
	// <METODOS_BOTONES>
	// </METODOS_BOTONES>
	// <METODOS_CAMBIAR>
	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	// </METODOS_COMBOS_GRANDES>
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
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
	public boolean insertarAntes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion del registro
	 * 
	 */
	@Override
	public boolean insertarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado antes de realizar la insercion y actualizacion del
	 * registro
	 * 
	 */
	@Override
	public boolean actualizarAntes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion y actualizacion del
	 * registro
	 * 
	 */
	@Override
	public boolean actualizarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado antes de realizar la eliminacion del registro
	 * 
	 */
	@Override
	public boolean eliminarAntes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la eliminacion del registro
	 *
	 */
	@Override
	public boolean eliminarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Este metodo se ejecuta antes enviar la accion de actualizacion, en el se
	 * pueden remover valores auxiliares que no se desee o se deban enviar en el
	 * registro
	 */
	@Override
	public void removerCombos() {
		registro.getCampos().remove(ConfiguracionpersonalsiifsControladorEnum.COMPANIA.getValue());
		registro.getCampos().remove(ConfiguracionpersonalsiifsControladorEnum.NUMERO_DCTO.getValue());
		registro.getCampos().remove(ConfiguracionpersonalsiifsControladorEnum.ID_DE_EMPLEADO.getValue());
		registro.getCampos().remove(ConfiguracionpersonalsiifsControladorEnum.TIPOCUENTA.getValue());
		registro.getCampos().remove(ConfiguracionpersonalsiifsControladorEnum.BANCO.getValue());
		registro.getCampos().remove(ConfiguracionpersonalsiifsControladorEnum.NOMBRE.getValue());
		registro.getCampos().remove(ConfiguracionpersonalsiifsControladorEnum.CUENTA.getValue());
		registro.getCampos().remove(ConfiguracionpersonalsiifsControladorEnum.NOMBRE_MEDIO_PAGO_SIIF.getValue());
	}

	/**
	 * Metodo ejecutado desde un comando remoto cuando se cierra el formulario
	 * 
	 */
	public void ejecutarrcCerrar() {
		// <CODIGO_DESARROLLADO>
		if ("60506".equals(SessionUtil.getMenuActual()))
			SessionUtil.redireccionarMenuFormulario("60506");
		else
			SessionUtil.redireccionar("/menu.sysman");
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Este metodo es ejecutado despues de finalizar la insercion y edicion del
	 * registro se usa cuando se desean agregar valores al registro despues de
	 * dichas acciones
	 */
	@Override
	public void asignarValoresRegistro() {
		// No hay código aquí.
	}
	// <SET_GET_ATRIBUTOS>
	// </SET_GET_ATRIBUTOS>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_LISTAS>
	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	// </SET_GET_LISTAS_COMBO_GRANDE>
}
