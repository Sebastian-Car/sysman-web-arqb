/*-
 * EstratosfgControlador.java
 *
 * 1.0
 * 
 * 14/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.facturaciongeneral;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.enums.EstratosfgControladorEnum;
import com.sysman.facturaciongeneral.enums.EstratosfgControladorUrlEnum;
import com.sysman.facturaciongeneral.enums.TarifasfgControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

/**
 * Formulario que contiene los datos del estrato, además de inserción,
 * modificación y eliminación.
 *
 * @version 1.0, 14/11/2017
 * @author fperez
 */
@ManagedBean
@ViewScoped
public class EstratosfgControlador extends BeanBaseContinuoAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el código de la compañía en la cual
	 * inicio sesión el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesión correspondiente.
	 */
	private final String compania;

	// <DECLARAR_ATRIBUTOS>

	/**
	 * Año seleccionado en la lista Año.
	 */
	private int anio;

	/**
	 * Código del concepto relacionado seleccioando del combo Concepto Relacionado.
	 */
	private String concRelacionado;

	/**
	 * Nombre del concepto relacionado seleccioando del combo Concepto Relacionado.
	 */
	private String nombreConcRelacionado;

	/**
	 * Código del tipo de cobro seleccionado previamente en el modal Seleccionar
	 * tipo de cobro.
	 */
	private final String tipoCobro;

	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	/**
	 * Listado del combo año.
	 */
	private List<Registro> listaAno;
	/**
	 * Listado del combo concepto relacionado según el año seleccionado. (lista no
	 * visible actualmente - oculta en el formulario)
	 */
	private RegistroDataModelImpl listaConcRelacionado;

	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	// </DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de EstratosfgControlador
	 */
	public EstratosfgControlador() {
		super();
		compania = SessionUtil.getCompania();
		tipoCobro = (String) SessionUtil.getSessionVar("tipoCobro");
		try {
			numFormulario = GeneralCodigoFormaEnum.ESTRATOS_FG_CONTROLADOR.getCodigo();
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	/**
	 * Este método se ejecuta justo después de que el objeto de la clase del Bean ha
	 * sido creado. En este se realizan las asignaciones iniciales necesarias para
	 * la visualización del formulario, como son tablas, origenes de datos,
	 * inicialización de listas y demás necesarios.
	 */
	@PostConstruct
	public void inicializar() {
		enumBase = GenericUrlEnum.SF_ESTRATO;
		anio = SysmanFunciones.ano(new Date());

		buscarLlave();
		reasignarOrigen();
		registro = new Registro();
		// <CARGAR_LISTA>
		cargarListaAno();
		// </CARGAR_LISTA>
		// <CARGAR_LISTA_COMBO_GRANDE>
		cargarListaConcRelacionado();
		// </CARGAR_LISTA_COMBO_GRANDE>
		abrirFormulario();

	}

	/**
	 * En este método se asigna al atributo origenDatos del bean base el valor de la
	 * consulta del formulario. También carga la lista del formulario por primera
	 * vez.
	 */
	@Override
	public void reasignarOrigen() {
		buscarUrls();

		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		parametrosListado.put("ANO", anio);
		parametrosListado.put(EstratosfgControladorEnum.TIPOCOBRO.getValue(), tipoCobro);

	}

	// <METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaAno
	 *
	 * Carga la lista del combo año.
	 */
	public void cargarListaAno() {

		Map<String, Object> param = new TreeMap<>();
		param.put(EstratosfgControladorEnum.COMPANIA.getValue(), String.valueOf(compania));

		try {
			listaAno = RegistroConverter
					.toListRegistro(
							requestManager
									.getList(
											UrlServiceUtil.getInstance()
													.getUrlServiceByUrlByEnumID(
															EstratosfgControladorUrlEnum.URL3608.getValue())
													.getUrl(),
											param));

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * 
	 * Carga la lista listaConcRelacionado Carga la lista del combo concepto
	 * relacionado. (lista no visible actualmente - oculta en el formulario)
	 *
	 */
	public void cargarListaConcRelacionado() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(TarifasfgControladorUrlEnum.URL4507.getValue());

		Map<String, Object> param = new TreeMap<>();

		param.put(EstratosfgControladorEnum.COMPANIA.getValue(), String.valueOf(compania));

		param.put(EstratosfgControladorEnum.ANO.getValue(), anio);

		param.put(EstratosfgControladorEnum.TIPOCOBRO.getValue(), tipoCobro);

		listaConcRelacionado = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());

	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_BOTONES>
	// </METODOS_BOTONES>
	// <METODOS_CAMBIAR>

	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	// </METODOS_COMBOS_GRANDES>
	/**
	 * Este método es invocado al inicializar, se ejecutan las acciones a tener en
	 * cuenta en el momento de apertura del formulario.
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		/**
		 * No hay código aquí.
		 */
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Método ejecutado cuando se cancela la edición del registro seleccionado.
	 */
	@Override
	public void cancelarEdicion(RowEditEvent event) {
		getListaInicial().load();
	}

	public void cambiarAno() {
		concRelacionado = null;
		reasignarOrigen();
	}

	/**
	 * Método ejecutado al cambiar el control TOMAR_VLR.
	 * 
	 */
	public void cambiarTOMAR_VLR() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaConcRelacionado
	 * (lista no visible actualmente - oculta en el formulario)
	 *
	 * @param event
	 *            Objeto que encapsula la acción proveniente de la vista.
	 */
	public void seleccionarFilaConcRelacionado(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		concRelacionado = SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));

		nombreConcRelacionado = SysmanFunciones.toString(registroAux.getCampos().get("NOMBRE"));

		reasignarOrigen();

	}

	// </METODOS_COMBOS_GRANDES>

	/**
	 * Método ejecutado antes de realizar la inserción del registro.
	 * 
	 * @return Retorna verdadero antes de insertar el registro. Acá se crean
	 *         parámetros para la consulta insert.
	 */
	@Override
	public boolean insertarAntes() {
		// <CODIGO_DESARROLLADO>
		if (!(boolean) registro.getCampos().get("TOMAR_VLR")) {
			if (Double.parseDouble(registro.getCampos().get("TARIFA").toString()) > 100) {
				JsfUtil.agregarMensajeError(idioma.getString("TB_TB4142"));
				return false;
			}
		}
		registro.getCampos().put("COMPANIA", compania);
		registro.getCampos().put("ANO", anio);
		registro.getCampos().put("TIPOCOBRO", tipoCobro);
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Método ejecutado después de realizar la inserción del registro.
	 * 
	 * @return Retorna verdadero en caso de un evento después de insertar.
	 */
	@Override
	public boolean insertarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Método ejecutado antes de realizar la inserción y actualización del registro.
	 * 
	 * @return Retorna verdadero en caso de un evento después de actualizar.
	 */
	@Override
	public boolean actualizarAntes() {
		// <CODIGO_DESARROLLADO>
		if (!(boolean) registro.getCampos().get("TOMAR_VLR")) {
			if (Double.parseDouble(registro.getCampos().get("TARIFA").toString()) > 100) {
				JsfUtil.agregarMensajeError(idioma.getString("TB_TB4142"));
				return false;
			}
		}
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Método ejecutado despues de realizar la inserción y actualización del
	 * registro.
	 * 
	 * @return Retorna verdadero en caso de un evento después de actualizar.
	 */
	@Override
	public boolean actualizarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Método ejecutado antes de realizar la eliminación del registro.
	 * 
	 * 
	 * @return Retorna verdadero en caso de un evento antes de eliminar.
	 */
	@Override
	public boolean eliminarAntes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Método ejecutado después de realizar la eliminación del registro.
	 * 
	 * 
	 * @return Retorna verdadero en caso de un evento después de eliminar.
	 */
	@Override
	public boolean eliminarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Este método se ejecuta antes enviar la acción de actualización, en él se
	 * pueden remover valores auxiliares que no se desee o se deban enviar en el
	 * registro.
	 */
	@Override
	public void removerCombos() {
		registro.getCampos().remove("TIPOCOBRO");
		registro.getCampos().remove("CODIGO");
		registro.getCampos().remove("COMPANIA");
		registro.getCampos().remove("ANO");
	}

	/**
	 * Este método es ejecutado después de finalizar la inserción y edición del
	 * registro. Se usa cuando se desean agregar valores al registro después de
	 * dichas acciones.
	 */
	@Override
	public void asignarValoresRegistro() {
		// No se requiere agregar valores al registro.
	}

	// <SET_GET_ATRIBUTOS>
	// </SET_GET_ATRIBUTOS>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_LISTAS>
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
	 *            Variable a asignar en listaAno
	 */
	public void setListaAno(List<Registro> listaAno) {
		this.listaAno = listaAno;
	}

	/**
	 * Retorna la lista listaConcRelacionado
	 * 
	 * @return listaConcRelacionado
	 */
	public RegistroDataModelImpl getListaConcRelacionado() {
		return listaConcRelacionado;
	}

	/**
	 * Asigna la lista listaConcRelacionado
	 * 
	 * @param listaConcRelacionado
	 *            Variable a asignar en listaConcRelacionado
	 */
	public void setListaConcRelacionado(RegistroDataModelImpl listaConcRelacionado) {
		this.listaConcRelacionado = listaConcRelacionado;
	}
	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	// </SET_GET_LISTAS_COMBO_GRANDE>

	public int getAnio() {
		return anio;
	}

	public void setAnio(int anio) {
		this.anio = anio;
	}

	/**
	 * Retorna la variable concRelacionado
	 * 
	 * @return concRelacionado
	 */
	public String getConcRelacionado() {
		return concRelacionado;
	}

	/**
	 * Asigna la variable concRelacionado
	 * 
	 * @param concRelacionado
	 *            Variable a asignar en concRelacionado
	 */
	public void setConcRelacionado(String concRelacionado) {
		this.concRelacionado = concRelacionado;
	}

	/**
	 * Retorna la variable nombreConcRelacionado
	 * 
	 * @return nombreConcRelacionado
	 */
	public String getNombreConcRelacionado() {
		return nombreConcRelacionado;
	}

	/**
	 * Asigna la variable nombreConcRelacionado
	 * 
	 * @param nombreConcRelacionado
	 *            Variable a asignar en nombreConcRelacionado
	 */
	public void setNombreConcRelacionado(String nombreConcRelacionado) {
		this.nombreConcRelacionado = nombreConcRelacionado;
	}

}
