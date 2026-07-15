/*-
 * CAlmacenContabilidadsControlador.java
 *
 * 1.0
 * 
 * 1/12/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contabilizar;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.contabilizar.ejb.EjbContabilizarCeroRemote;
import com.sysman.contabilizar.enums.CAlmacenContabilidadsControladorEnum;
import com.sysman.contabilizar.enums.CAlmacenContabilidadsControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * Formulario que cambia la interfaz de almacen a contabilidad
 *
 * @version 1.0, 04/12/2017
 * @author eamaya
 */
@ManagedBean
@ViewScoped

public class CAlmacenContabilidadsControlador extends BeanBaseDatosAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;

	/**
	 * Constante a nivel de clase que almacena el modulo de la en el
	 * cual inicio sesion el usuario, el valor de esta constante es
	 * asignado en el constructor a la variable de sesion
	 * correspondiente
	 */

	private final String modulo;

	/**
	 * Atributo utilizado para almacenar el boton pulsado para
	 * gestionar la apertura de formularios
	 */
	private int botonSeleccionado;

	/**
	 * Atributo utilizado para almacenar el anio seleccionado en el
	 * combo del dialogo
	 */
	private String anio;

	/**
	 * Atributo utilizado para guardar el valor del indicador de
	 * centro de costo de la vista
	 */
	private boolean centroCosto;

	/**
	 * Atributo utilizado para guardar el valor del indicador de
	 * centro de costo en la pesaña niif de la vista
	 */

	private boolean centroCostoNiif;

	/**
	 * Atributo que almacena el valor del parametro MANEJA NIIF EN
	 * ALMACEN
	 */
	private String aplicaNIIF;

	/**
	 * Variable que almacena el valor del parametro PERMITE CONFIGURAR
	 * INTERFASE ALMACEN SIN NIVEL
	 */
	private String permiteInterfase;

	/**
	 * Variable que almacena el valor del parametro MANEJA INTERFACE
	 * ALMACEN MENSUAL POR CENTRO COSTO
	 */
	private String manejaInterfaceCC;

	/**
	 * Variable que almacena el valor del parametro MANEJA INTERFACE
	 * MENSUAL NIIF POR CENTRO DE COSTO
	 */

	private String manejaInterfaceCCNiif;

	/**
	 * Variable que almacena el valor del parametro MANEJA ESTRUCTURA
	 * ESTANDAR
	 */
	private String manejaEstructuraEstandar;

	/**
	 * Variable que almacena el valor del parametro MANEJA
	 * CONFIGURACION ESPECIAL DE TRASLADOS EN ALMACEN
	 */
	private String manejaConfgEspecial;

	/**
	 * Variable que almacena el valor del parametro DIGITOS AGRUPACION
	 * INVENTARIO
	 */
	private String digitosAgurpacionInv;

	/**
	 * Atributo que administra la visibilidad de algunos componentes
	 * de la vista dependiendo del campo TIPO del registro
	 */
	private boolean verTipoC;

	/**
	 * Atributo que administra la visibilidad del Por Retiro de
	 * Activos de la vista
	 */

	private boolean verRetiro;
	/**
	 * Atributo que administra la visibilidad de la pestaña Interfaz a
	 * NIIF de la vista
	 */
	private boolean verInterfazNIIF;
	/**
	 * Atributo que administra la visibilidad de la pestaña Interfaz a
	 * Contabilidad de la vista
	 */
	private boolean verInterfazContabilidad;

	/**
	 * Atributo que administra la visibilidad del indicador de centro
	 * de costo de la vista
	 */
	private boolean verCentroCosto;

	/**
	 * Atributo que administra la visibilidad del indicador de centro
	 * de costo de la pestaña Niif
	 */

	private boolean verCentroCostoNiif;
	
	private boolean fuenteRecursos;

	private final String anioActual;

	/**
	 * Atributo que guarda el nombre de la cuenta de bodega
	 */
	private String nombreCuentaActivo;
	/**
	 * Atributo que guarda el nombre de la cuenta de inservibles
	 */
	private String nombreCuentaActivoI;
	/**
	 * Atributo que guarda el nombre de la cuenta de servicio
	 */
	private String nombreCuentaActivoS;
	/**
	 * Atributo que guarda el nombre de la cuenta de responsabilidades
	 */
	private String nombreCuentaActivoR;
	/**
	 * Atributo que guarda el nombre de la cuenta de comodato
	 */
	private String nombreCuentaActivoC;
	/**
	 * Atributo que guarda el nombre de la cuenta niif de bodega
	 */
	private String niifNombreCuentaActivo;
	/**
	 * Atributo que guarda el nombre de la cuenta niif de inservibles
	 */
	private String niifNombreCuentaActivoI;
	/**
	 * Atributo que guarda el nombre de la cuenta niif de servicio
	 */
	private String niifNombreCuentaActivoS;

	private String niifNombreCuentaActivoMan;
	private String niifNombreCuentaActivoNexp;
	private String nombreCuentaActivoMan;
	private String nombreCuentaActivoNexp;
	/**
	 * Atributo que guarda el nombre de la cuenta niif de
	 * responsabilidades
	 */
	private String niifNombreCuentaActivoR;

	private String textoMensaje;

	private boolean verDialogo;

	private boolean verActualizarTodos;

	private boolean visiblePreparar;

	private int anioInicial;

	private int anioFinal;

	private final String consCodigoElemento;

	private final String consNombre;

	@EJB
	private EjbContabilizarCeroRemote ejbContabilizar;
	// <DECLARAR_ATRIBUTOS>
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_LISTAS>
	/**
	 * Lista que almacena los anios del combo del dialogo
	 */
	private List<Registro> listaanio;

	private List<Registro> listaAnioInicial;

	private List<Registro> listaAnioFinal;

	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>

	/**
	 * Lista que guarda las cuentas de bodega
	 */
	private RegistroDataModelImpl listacuentaActivo;
	/**
	 * Lista que guarda las cuentas de inservibles
	 */
	private RegistroDataModelImpl listaCuentaActivoI;
	/**
	 * Lista que guarda las cuentas de servicio
	 */
	private RegistroDataModelImpl listaCuentaActivoS;
	/**
	 * Lista que guarda las cuentas de responsabilidades
	 */
	private RegistroDataModelImpl listaCuentaActivoR;
	/**
	 * Lista que guarda las cuentas de comodato
	 */
	private RegistroDataModelImpl listaCuentaActivoC;
	/**
	 * Lista que guarda las cuentas niif de bodega
	 */
	private RegistroDataModelImpl listaNiifCuentaActivo;
	/**
	 * Lista que guarda las cuentas niif de inservibles
	 */
	private RegistroDataModelImpl listaNiifCuentaActivoI;
	/**
	 * Lista que guarda las cuentas niif de Serivicio
	 */
	private RegistroDataModelImpl listaNiifCuentaActivoS;
	/**
	 * Lista que guarda las cuentas niif de responsabilidades
	 */
	private RegistroDataModelImpl listaNiifCuentaActivoR;

	private RegistroDataModelImpl listaNiifCuentaActivoMan;
	private RegistroDataModelImpl listaNiifCuentaActivoNexp;
	private RegistroDataModelImpl listaCuentaActivoMan;
	private RegistroDataModelImpl listaCuentaActivoNexp;

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

	private boolean visibleFuente;
	private boolean fuenteRecursoNiif;

	// </DECLARAR_LISTAS_COMBO_GRANDE>
	// <DECLARAR_LISTAS_SUBFORM>
	// </DECLARAR_LISTAS_SUBFORM>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_ADICIONALES>
	// </DECLARAR_ADICIONALES>
	/**
	 * Crea una nueva instancia de CAlmacenContabilidadsControlador
	 */
	public CAlmacenContabilidadsControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		anioActual = Integer.toString(SysmanFunciones.ano(new Date()));
		verDialogo = false;
		consCodigoElemento = "codigoElemento";
		consNombre = "nombre";
		try {

			numFormulario = GeneralCodigoFormaEnum.CALMACEN_CONTABILIDADS_CONTROLADOR
					.getCodigo();
			validarPermisos();

			Map<String, Object> parametros = SessionUtil.getFlash();
			if (parametros != null) {
				rid = (Map<String, Object>) parametros.get("rid");
			}
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		}
		catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		}

	}

	/**
	 * En este metodo se hace la invocacion de lo metodos de carga de
	 * todas las listas, menos las que son de subformularios
	 */
	@Override
	public void iniciarListas() {
		// <CARGAR_LISTA_COMBO_GRANDE>

		cargarListacuentaActivo();
		cargarListaCuentaActivoI();
		cargarListaCuentaActivoS();
		cargarListaCuentaActivoR();
		cargarListaCuentaActivoC();
		cargarListaNiifCuentaActivo();
		cargarListaNiifCuentaActivoI();
		cargarListaNiifCuentaActivoS();
		cargarListaNiifCuentaActivoR();
		cargarListaNiifCuentaActivoMan(); 
		cargarListaNiifCuentaActivoNexp();
		cargarListaCuentaActivoMan(); 
		cargarListaCuentaActivoNexp();
		// </CARGAR_LISTA_COMBO_GRANDE>
		// <CARGAR_LISTA>
		cargarListaanio();
		cargarListaAnioInicial();
		cargarListaAnioFinal();
		// </CARGAR_LISTA>
	}

	/**
	 * En este metodo se hace la invocacion de lo metodos de carga de
	 * todas las listas que son de subformularios
	 */
	@Override
	public void iniciarListasSub() {
		// <CARGAR_LISTAS_SUBFORM>
		// </CARGAR_LISTAS_SUBFORM>
		// <CREAR_ARBOLES>
		// </CREAR_ARBOLES>
	}

	/**
	 * En este metodo se iguala a null todas las listas de los
	 * subformularios
	 */
	@Override
	public void iniciarListasSubNulo() {
		// <CARGAR_LISTAS_SUBFORM_NULL>
		// </CARGAR_LISTAS_SUBFORM_NULL>
	}

	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la
	 * clase del Bean ha sido creado, en este se realizan las
	 * asignaciones iniciales necesarias para la visualizacion del
	 * formulario, como son tablas, origenes de datos, inicializacion
	 * de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar() {
		tabla = GenericUrlEnum.INVENTARIO.getTable();
		buscarLlave();
		asignarOrigenDatos();
	}

	/**
	 * Se realiza la asignacion de la variable origenDatos por la
	 * consulta correspondiente del formulario
	 * 
	 * 
	 */
	@Override
	public void asignarOrigenDatos() {

		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);

		urlListado = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						CAlmacenContabilidadsControladorUrlEnum.URL6969
						.getValue());

		urlLectura = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						CAlmacenContabilidadsControladorUrlEnum.URL1313
						.getValue());

		urlActualizacion = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						CAlmacenContabilidadsControladorUrlEnum.URL2525
						.getValue());

	}

	// <METODOS_CARGAR_LISTA>

	/**
	 * 
	 * Carga la lista listaCuentaActivoMan
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCuentaActivoMan(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						CAlmacenContabilidadsControladorUrlEnum.URL20029
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anioActual);

		listaCuentaActivoMan = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}
	/**
	 * 
	 * Carga la lista listaCuentaActivoNexp
	 *
	 */
	public void cargarListaCuentaActivoNexp(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						CAlmacenContabilidadsControladorUrlEnum.URL20029
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anioActual);

		listaCuentaActivoNexp = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaNiifCuentaActivoMan
	 *
	 */
	public void cargarListaNiifCuentaActivoMan(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						CAlmacenContabilidadsControladorUrlEnum.URL20029
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anioActual);

		listaNiifCuentaActivoMan = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}
	/**
	 * 
	 * Carga la lista listaNiifCuentaActivoNexp
	 *
	 */
	public void cargarListaNiifCuentaActivoNexp(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						CAlmacenContabilidadsControladorUrlEnum.URL20029
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anioActual);

		listaNiifCuentaActivoNexp = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}



	/**
	 * 
	 * Carga la lista listaanio
	 *
	 */
	public void cargarListaanio() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		try {
			listaanio = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									CAlmacenContabilidadsControladorUrlEnum.URL3837
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaAnioInicial
	 *
	 */
	public void cargarListaAnioInicial() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		try {
			listaAnioInicial = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									CAlmacenContabilidadsControladorUrlEnum.URL3837
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaAnioFinal
	 *
	 */
	public void cargarListaAnioFinal() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anioInicial);
		try {
			listaAnioFinal = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									CAlmacenContabilidadsControladorUrlEnum.URL6475
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listacuentaActivo
	 *
	 */
	public void cargarListacuentaActivo() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						CAlmacenContabilidadsControladorUrlEnum.URL10311
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anioActual);

		listacuentaActivo = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaCuentaActivoI
	 *
	 */
	public void cargarListaCuentaActivoI() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						CAlmacenContabilidadsControladorUrlEnum.URL11510
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anioActual);

		listaCuentaActivoI = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaCuentaActivoS
	 *
	 */
	public void cargarListaCuentaActivoS() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						CAlmacenContabilidadsControladorUrlEnum.URL12710
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anioActual);

		listaCuentaActivoS = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaCuentaActivoR
	 *
	 */
	public void cargarListaCuentaActivoR() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						CAlmacenContabilidadsControladorUrlEnum.URL13910
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anioActual);

		listaCuentaActivoR = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaCuentaActivoC
	 *
	 */
	public void cargarListaCuentaActivoC() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						CAlmacenContabilidadsControladorUrlEnum.URL15110
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anioActual);

		listaCuentaActivoC = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaNiifCuentaActivo
	 *
	 */
	public void cargarListaNiifCuentaActivo() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						CAlmacenContabilidadsControladorUrlEnum.URL16316
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anioActual);

		listaNiifCuentaActivo = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaNiifCuentaActivoI
	 *
	 */
	public void cargarListaNiifCuentaActivoI() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						CAlmacenContabilidadsControladorUrlEnum.URL17553
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anioActual);

		listaNiifCuentaActivoI = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaNiifCuentaActivoS
	 *
	 */
	public void cargarListaNiifCuentaActivoS() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						CAlmacenContabilidadsControladorUrlEnum.URL18791
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anioActual);

		listaNiifCuentaActivoS = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaNiifCuentaActivoR
	 *
	 */
	public void cargarListaNiifCuentaActivoR() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						CAlmacenContabilidadsControladorUrlEnum.URL20029
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anioActual);

		listaNiifCuentaActivoR = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_CAMBIAR>

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Aceptar del dialogo
	 * cargarAnio en la vista
	 *
	 */
	public void aceptarcargarAnio() {

		if (SysmanFunciones.validarVariableVacio(anio)) {
			JsfUtil.agregarMensajeError("Inserte el año por favor");

		}
		else {

			switch (botonSeleccionado) {
			case 1:
				redireccionarTransaccion();
				break;
			case 2:
				redireccionarDepreciacion();
				break;
			case 3:
				redireccionarRetiroActivos();
				break;
			case 4:
				redireccionarTransaccionCC();
				break;
			case 6:
				redireccionarTransaccionNiif();
				break;
			case 7:
				redireccionarTransaccionCCNiif();
				break;
			case 8:
				redireccionarDepreciacionNiifCC();
				break;
			case 9:
				redireccionarRetiroActivosniif();
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Aceptar del dialogo
	 * actualizarTodosElem en la vista
	 *
	 */
	public void aceptaractualizarTodosElem() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Aceptar del dialogo
	 * PrepararInterfaz en la vista
	 *
	 *
	 */
	public void aceptarPrepararInterfaz() {
		// <CODIGO_DESARROLLADO>
		try {
			ejbContabilizar.prepararContabilizacionSiguienteAnio(compania,
					anioInicial,
					anioFinal, SessionUtil.getUser().getCodigo());
			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3044"));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		// </CODIGO_DESARROLLADO>
	}

	private void redireccionarTransaccion() {
		Direccionador direccionador = new Direccionador();

		Map<String, Object> param = new TreeMap<>();

		param.put("rid", css);

		param.put("anio", anio);

		param.put(consCodigoElemento, registro.getCampos()
				.get(GeneralParameterEnum.CODIGOELEMENTO.getName()));

		param.put("tipo", registro.getCampos()
				.get(CAlmacenContabilidadsControladorEnum.TIPO
						.getValue()));

		param.put(consNombre, registro.getCampos()
				.get(CAlmacenContabilidadsControladorEnum.NOMBRELARGO
						.getValue()));

		direccionador.setParametros(param);
		direccionador.setNumForm("1493");

		SessionUtil.redireccionarForma(direccionador, modulo);

	}

	private void redireccionarTransaccionNiif() {

		Direccionador direccionador = new Direccionador();

		Map<String, Object> param = new TreeMap<>();

		param.put("rid", css);

		param.put("anio", anio);

		param.put(consCodigoElemento, registro.getCampos()
				.get(GeneralParameterEnum.CODIGOELEMENTO.getName()));

		param.put("tipo", registro.getCampos().get(
				CAlmacenContabilidadsControladorEnum.TIPO.getValue()));

		param.put(consNombre, registro.getCampos()
				.get(CAlmacenContabilidadsControladorEnum.NOMBRELARGO
						.getValue()));

		direccionador.setParametros(param);
		direccionador.setNumForm("1917");

		SessionUtil.redireccionarForma(direccionador, modulo);

	}

	private void redireccionarDepreciacion() {
		Direccionador direccionador = new Direccionador();

		Map<String, Object> param = new TreeMap<>();

		param.put("rid", css);

		param.put("anio", anio);

		param.put(consCodigoElemento, registro.getCampos()
				.get(GeneralParameterEnum.CODIGOELEMENTO.getName()));

		param.put(consNombre, registro.getCampos()
				.get(CAlmacenContabilidadsControladorEnum.NOMBRELARGO
						.getValue()));

		direccionador.setParametros(param);
		direccionador.setNumForm("1498");

		SessionUtil.redireccionarForma(direccionador, modulo);

	}

	private void redireccionarRetiroActivos() {
		Direccionador direccionador = new Direccionador();

		Map<String, Object> param = new TreeMap<>();

		param.put("rid", css);

		param.put("anio", anio);

		param.put(consCodigoElemento, registro.getCampos()
				.get(GeneralParameterEnum.CODIGOELEMENTO.getName()));

		param.put(consNombre, registro.getCampos()
				.get(CAlmacenContabilidadsControladorEnum.NOMBRELARGO
						.getValue()));

		param.put("tipo", registro.getCampos()
				.get(CAlmacenContabilidadsControladorEnum.TIPO
						.getValue()));

		direccionador.setParametros(param);
		direccionador.setNumForm("1548");

		SessionUtil.redireccionarForma(direccionador, modulo);

	}
	
	private void redireccionarRetiroActivosniif() {
		Direccionador direccionador = new Direccionador();

		Map<String, Object> param = new TreeMap<>();

		param.put("rid", css);

		param.put("anio", anio);

		param.put(consCodigoElemento, registro.getCampos()
				.get(GeneralParameterEnum.CODIGOELEMENTO.getName()));

		param.put("tipo", registro.getCampos().get(
				CAlmacenContabilidadsControladorEnum.TIPO.getValue()));

		param.put(consNombre, registro.getCampos()
				.get(CAlmacenContabilidadsControladorEnum.NOMBRELARGO
						.getValue()));

		direccionador.setParametros(param);
		direccionador.setNumForm("2162");

		SessionUtil.redireccionarForma(direccionador, modulo);
	}

	private void redireccionarTransaccionCC() {

		try {
			ejbContabilizar.cargarInterfazAlmacenCC(compania,
					registro.getCampos()
					.get(GeneralParameterEnum.CODIGOELEMENTO
							.getName())
					.toString(),
					Integer.parseInt(anio));

			Direccionador direccionador = new Direccionador();

			Map<String, Object> param = new TreeMap<>();

			param.put("rid", css);

			param.put("anio", anio);

			param.put(consCodigoElemento, registro.getCampos()
					.get(GeneralParameterEnum.CODIGOELEMENTO
							.getName()));

			param.put("tipo", registro.getCampos()
					.get(CAlmacenContabilidadsControladorEnum.TIPO
							.getValue()));

			param.put(consNombre, registro.getCampos()
					.get(CAlmacenContabilidadsControladorEnum.NOMBRELARGO
							.getValue()));
			
			param.put("fuenteRecursos", fuenteRecursos);
			
			param.put("centroCosto", centroCosto);




			direccionador.setParametros(param);
			direccionador.setNumForm("1576");

			SessionUtil.redireccionarForma(direccionador, modulo);
		}
		catch (NumberFormatException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	private void redireccionarTransaccionCCNiif() {

		try {
			ejbContabilizar.cargarInterfazAlmacenCCNIIF(compania,
					registro.getCampos()
					.get(GeneralParameterEnum.CODIGOELEMENTO
							.getName())
					.toString(),
					Integer.parseInt(anio));

			Direccionador direccionador = new Direccionador();

			Map<String, Object> param = new TreeMap<>();

			param.put("rid", css);

			param.put("anio", anio);

			param.put(consCodigoElemento, registro.getCampos()
					.get(GeneralParameterEnum.CODIGOELEMENTO
							.getName()));

			param.put("tipo", registro.getCampos()
					.get(CAlmacenContabilidadsControladorEnum.TIPO
							.getValue()));

			param.put(consNombre, registro.getCampos()
					.get(CAlmacenContabilidadsControladorEnum.NOMBRELARGO
							.getValue()));
			
			param.put("centroCostoNiif", centroCostoNiif);
			
			param.put("fuenteRecursoNiif", fuenteRecursoNiif);

			direccionador.setParametros(param);
			direccionador.setNumForm("1918");

			SessionUtil.redireccionarForma(direccionador, modulo);
		}
		catch (NumberFormatException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	private void redireccionarDepreciacionNiifCC() {
		Direccionador direccionador = new Direccionador();

		Map<String, Object> param = new TreeMap<>();

		param.put("rid", css);

		param.put("anio", anio);

		param.put(consCodigoElemento, registro.getCampos()
				.get(GeneralParameterEnum.CODIGOELEMENTO
						.getName()));

		param.put("tipo", registro.getCampos()
				.get(CAlmacenContabilidadsControladorEnum.TIPO
						.getValue()));

		param.put(consNombre, registro.getCampos()
				.get(CAlmacenContabilidadsControladorEnum.NOMBRELARGO
						.getValue()));

		param.put("centroCostoNiif", centroCostoNiif);

		direccionador.setParametros(param);
		direccionador.setNumForm("1943");

		SessionUtil.redireccionarForma(direccionador, modulo);
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Cancelar del dialogo
	 * cargarAnio en la vista
	 *
	 */
	public void cancelarcargarAnio() {
		verDialogo = false;
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Cancelar del dialogo
	 * cargarAnio en la vista
	 *
	 */
	public void cambiarAnioInicial() {
		cargarListaAnioFinal();
	}

	// <METODOS_COMBOS_GRANDES>

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCuentaActivoMan
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCuentaActivoMan(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CUENTAACTIVOMAN", registroAux.getCampos().get("CODIGO"));

		nombreCuentaActivoMan = SysmanFunciones.nvl(registroAux.getCampos()
				.get(GeneralParameterEnum.NOMBRE.getName()), "")
				.toString();
		
		registro.getCampos().put("NIIF_CUENTAACTIVOMAN", registroAux.getCampos().get("CODIGO"));
		
		niifNombreCuentaActivoMan = nombreCuentaActivoMan;
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCuentaActivoNexp
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCuentaActivoNexp(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CUENTAACTIVONEXP", registroAux.getCampos().get("CODIGO"));

		nombreCuentaActivoNexp = SysmanFunciones.nvl(registroAux.getCampos()
				.get(GeneralParameterEnum.NOMBRE.getName()), "")
				.toString();
		
		registro.getCampos().put("NIIF_CUENTAACTIVONEXP", registroAux.getCampos().get("CODIGO"));
		
		niifNombreCuentaActivoNexp = nombreCuentaActivoNexp;
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaNiifCuentaActivoMan
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaNiifCuentaActivoMan(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("NIIF_CUENTAACTIVOMAN", registroAux.getCampos().get("CODIGO"));

		niifNombreCuentaActivoMan = SysmanFunciones.nvl(registroAux.getCampos()
				.get(GeneralParameterEnum.NOMBRE.getName()), "")
				.toString();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaNiifCuentaActivoNexp
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaNiifCuentaActivoNexp(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("NIIF_CUENTAACTIVONEXP", registroAux.getCampos().get("CODIGO"));


		niifNombreCuentaActivoNexp = SysmanFunciones.nvl(registroAux.getCampos()
				.get(GeneralParameterEnum.NOMBRE.getName()), "")
				.toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaBuscarElemento
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaBuscarElemento(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(),
				registroAux.getCampos().get("CODIGOELEMENTO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaUnidad
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaUnidad(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("UNIDAD",
				registroAux.getCampos().get("UNIDAD"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listacuentaActivo
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacuentaActivo(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CUENTAACTIVO",
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()));

		nombreCuentaActivo = SysmanFunciones.nvl(registroAux.getCampos()
				.get(GeneralParameterEnum.NOMBRE.getName()), "")
				.toString();
		
		registro.getCampos().put("NIIF_CUENTAACTIVO", registroAux.getCampos().get(
				GeneralParameterEnum.CODIGO.getName()));
		
		niifNombreCuentaActivo = nombreCuentaActivo;
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCuentaActivoI
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCuentaActivoI(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CUENTAACTIVOI",
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()));

		nombreCuentaActivoI = SysmanFunciones.nvl(registroAux.getCampos()
				.get(GeneralParameterEnum.NOMBRE.getName()), "")
				.toString();
		
		registro.getCampos().put("NIIF_CUENTAACTIVOI",
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()));
		
		niifNombreCuentaActivoI = nombreCuentaActivoI; 

	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCuentaActivoS
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCuentaActivoS(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CUENTAACTIVOS",
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()));

		nombreCuentaActivoS = SysmanFunciones.nvl(registroAux.getCampos()
				.get(GeneralParameterEnum.NOMBRE.getName()), "")
				.toString();
		
		registro.getCampos().put("NIIF_CUENTAACTIVOS",
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()));
		
		niifNombreCuentaActivoS = nombreCuentaActivoS;

	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCuentaActivoR
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCuentaActivoR(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CUENTAACTIVOR",
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()));

		nombreCuentaActivoR = SysmanFunciones.nvl(registroAux.getCampos()
				.get(GeneralParameterEnum.NOMBRE.getName()), "")
				.toString();
		
		registro.getCampos().put("NIIF_CUENTAACTIVOR",
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()));
		
		niifNombreCuentaActivoR = nombreCuentaActivoR;
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCuentaActivoC
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCuentaActivoC(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CUENTAACTIVOC",
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()));

		nombreCuentaActivoC = SysmanFunciones.nvl(registroAux.getCampos()
				.get(GeneralParameterEnum.NOMBRE.getName()), "")
				.toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaNiifCuentaActivo
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaNiifCuentaActivo(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("NIIF_CUENTAACTIVO",
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()));

		niifNombreCuentaActivo = SysmanFunciones.nvl(registroAux.getCampos()
				.get(GeneralParameterEnum.NOMBRE.getName()), "")
				.toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaNiifCuentaActivoI
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaNiifCuentaActivoI(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("NIIF_CUENTAACTIVOI",
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()));

		niifNombreCuentaActivoI = SysmanFunciones.nvl(registroAux.getCampos()
				.get(GeneralParameterEnum.NOMBRE.getName()), "")
				.toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaNiifCuentaActivoS
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaNiifCuentaActivoS(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("NIIF_CUENTAACTIVOS",
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()));

		niifNombreCuentaActivoS = SysmanFunciones.nvl(registroAux.getCampos()
				.get(GeneralParameterEnum.NOMBRE.getName()), "")
				.toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaNiifCuentaActivoR
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaNiifCuentaActivoR(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("NIIF_CUENTAACTIVOR",
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()));

		niifNombreCuentaActivoR = SysmanFunciones.nvl(registroAux.getCampos()
				.get(GeneralParameterEnum.NOMBRE.getName()), "")
				.toString();
	}

	// </METODOS_COMBOS_GRANDES>
	// <METODOS_ARBOL>
	// </METODOS_ARBOL>
	// <METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Comando280 en la vista
	 *
	 */
	public void oprimirComando280() {

		Map<String, Object> param = new TreeMap<>();

		param.put("rid", css);

		Direccionador direccionador = new Direccionador();
		direccionador.setParametros(param);
		direccionador.setNumForm("1489");

		SessionUtil.redireccionarForma(direccionador, modulo);
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Transaccion en la vista
	 *
	 */
	public void oprimirTransaccion() {
		if (!centroCosto && !fuenteRecursos) {
			botonSeleccionado = 1;
			verDialogo = true;
		}
		else {
			botonSeleccionado = 4;
			verDialogo = true;
		}
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Depreciacion en la vista
	 *
	 *
	 */
	public void oprimirDepreciacion() {
		if (!centroCosto) {
			botonSeleccionado = 2;
		}
		else
			botonSeleccionado = 5;
		verDialogo = true;
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton CmdRetiro en la vista
	 *
	 *
	 */
	public void oprimirCmdRetiro() {
		botonSeleccionado = 3;
		verDialogo = true;
	}
	
	public void oprimirNIIFCmdRetiro() {
		botonSeleccionado = 9;
		verDialogo = true;
   }

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton CmdRetiro en la vista
	 *
	 *
	 */
	public void oprimirPrepararInterfaz() {
		visiblePreparar = true;
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton NIIF_Transaccion en la
	 * vista
	 *
	 *
	 */
	public void oprimirNIIFTransaccion() {
		// <CODIGO_DESARROLLADO>

		if (!centroCostoNiif && !fuenteRecursoNiif) {
			botonSeleccionado = 6;
			verDialogo = true;
		}
		else {
			botonSeleccionado = 7;
			verDialogo = true;
		}
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton NIIF_Depreciacion en la
	 * vista
	 *
	 *
	 */
	public void oprimirNIIFDepreciacion() {
		// <CODIGO_DESARROLLADO>

		botonSeleccionado = 8;
		verDialogo = true;

		// </CODIGO_DESARROLLADO>
	}

	// </METODOS_BOTONES>
	// <METODOS_SUBFORM>
	// </METODOS_SUBFORM>
	// <METODOS_ADICIONALES>
	// </METODOS_ADICIONALES>
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario() {
		try {
			aplicaNIIF = ejbSysmanUtil.consultarParametro(compania,
					"MANEJA NIIF EN ALMACEN",
					modulo, new Date(), false);

			manejaEstructuraEstandar = ejbSysmanUtil.consultarParametro(
					compania,
					"MANEJA ESTRUCTURA ESTANDAR", modulo, new Date(),
					false);

			manejaInterfaceCC = ejbSysmanUtil.consultarParametro(compania,
					"MANEJA INTERFACE ALMACEN MENSUAL POR CENTRO COSTO",
					modulo, new Date(),
					false);

			manejaInterfaceCCNiif = ejbSysmanUtil.consultarParametro(compania,
					"MANEJA INTERFACE MENSUAL NIIF POR CENTRO DE COSTO",
					modulo, new Date(),
					false);

			permiteInterfase = ejbSysmanUtil.consultarParametro(compania,
					"PERMITE CONFIGURAR INTERFASE ALMACEN SIN NIVEL",
					modulo, new Date(),
					false);

			manejaConfgEspecial = ejbSysmanUtil.consultarParametro(compania,
					"MANEJA CONFIGURACION ESPECIAL DE TRASLADOS EN ALMACEN",
					modulo, new Date(),
					false);

			digitosAgurpacionInv = ejbSysmanUtil.consultarParametro(compania,
					"DIGITOS AGRUPACION INVENTARIO",
					modulo, new Date(),
					false);
			
			visibleFuente = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					"MANEJA FUENTE DE RECURSOS PARA INTERFAZ ALMACEN",
					modulo, new Date(),
					false),"NO").equals("SI");

			textoMensaje = "Por favor seleccione el año en el que desea configurar la interfaz";

		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * Metodo ejecutado en el momento despues de cargar el registro
	 * 
	 */
	@Override
	public void cargarRegistro() {
		// <CODIGO_DESARROLLADO>
		precargarRegistro();
		visiblePreparar = false;

		validarVisibilidadPEstanas();

		nombreCuentaActivo = "";
		nombreCuentaActivoC = "";
		nombreCuentaActivoI = "";
		nombreCuentaActivoR = "";
		nombreCuentaActivoS = "";
		nombreCuentaActivoMan = "";
		nombreCuentaActivoNexp = "";
		niifNombreCuentaActivo = "";
		niifNombreCuentaActivoI = "";
		niifNombreCuentaActivoS = "";
		niifNombreCuentaActivoR = "";
		niifNombreCuentaActivoNexp = "";
		niifNombreCuentaActivoMan = "";

		if (accion.equals(ACCION_MODIFICAR)) {

			nombreCuentaActivo = consultarNombreCuenta(SysmanFunciones
					.nvl(registro.getCampos().get("CUENTAACTIVO"), "")
					.toString());

			nombreCuentaActivoC = consultarNombreCuenta(SysmanFunciones
					.nvl(registro.getCampos().get("CUENTAACTIVOC"), "")
					.toString());

			nombreCuentaActivoI = consultarNombreCuenta(SysmanFunciones
					.nvl(registro.getCampos().get("CUENTAACTIVOI"), "")
					.toString());

			nombreCuentaActivoR = consultarNombreCuenta(SysmanFunciones
					.nvl(registro.getCampos().get("CUENTAACTIVOR"), "")
					.toString());

			nombreCuentaActivoS = consultarNombreCuenta(SysmanFunciones
					.nvl(registro.getCampos().get("CUENTAACTIVOS"), "")
					.toString());

			/**/


			nombreCuentaActivoMan = consultarNombreCuenta(SysmanFunciones
					.nvl(registro.getCampos().get("CUENTAACTIVOMAN"), "")
					.toString());

			nombreCuentaActivoNexp = consultarNombreCuenta(SysmanFunciones
					.nvl(registro.getCampos().get("CUENTAACTIVONEXP"), "")
					.toString());

			/**/

			niifNombreCuentaActivo = consultarNombreCuenta(SysmanFunciones
					.nvl(registro.getCampos().get("NIIF_CUENTAACTIVO"),
							"")
					.toString());

			niifNombreCuentaActivoI = consultarNombreCuenta(SysmanFunciones
					.nvl(registro.getCampos().get("NIIF_CUENTAACTIVOI"),
							"")
					.toString());

			niifNombreCuentaActivoR = consultarNombreCuenta(SysmanFunciones
					.nvl(registro.getCampos().get("NIIF_CUENTAACTIVOR"),
							"")
					.toString());
			niifNombreCuentaActivoS = consultarNombreCuenta(SysmanFunciones
					.nvl(registro.getCampos().get("NIIF_CUENTAACTIVOS"),
							"")
					.toString());

			/**/

			niifNombreCuentaActivoMan = consultarNombreCuenta(SysmanFunciones
					.nvl(registro.getCampos().get("NIIF_CUENTAACTIVOMAN"),
							"")
					.toString());

			niifNombreCuentaActivoNexp = consultarNombreCuenta(SysmanFunciones
					.nvl(registro.getCampos().get("NIIF_CUENTAACTIVONEXP"),
							"")
					.toString());

			/**/

		}

		// </CODIGO_DESARROLLADO>
	}

	private String consultarNombreCuenta(String cuenta) {

		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		param.put(GeneralParameterEnum.ANO.getName(), anioActual);

		param.put(GeneralParameterEnum.CUENTA.getName(), cuenta);
		Registro registro;

		try {
			registro = RegistroConverter.toRegistro(
					requestManager.get(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									CAlmacenContabilidadsControladorUrlEnum.URL8935
									.getValue())
							.getUrl(), param));

			if (registro != null) {
				return registro.getCampos()
						.get(GeneralParameterEnum.NOMBRE.getName())
						.toString();
			}
			else {
				return "";
			}
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		return "";
	}

	private void validarVisibilidadPEstanas() {

		verCentroCosto = "SI".equals(manejaInterfaceCC);

		verCentroCostoNiif = "SI".equals(manejaInterfaceCCNiif);

		verRetiro = "SI".equals(manejaConfgEspecial);

		verTipoC = !"C".equals(SysmanFunciones.nvl(registro.getCampos().get(
				CAlmacenContabilidadsControladorEnum.TIPO.getValue()),
				"")
				.toString());

		if ("SI".equals(manejaEstructuraEstandar)) {
			if (registro.getCampos()
					.get("CODIGOELEMENTO").toString()
					.length() == Integer
					.parseInt(digitosAgurpacionInv)
					|| "SI".equals(permiteInterfase)) {

				verInterfazContabilidad = true;
				verInterfazNIIF = "SI".equals(aplicaNIIF);
			}
			else {
				verInterfazContabilidad = false;
				verInterfazNIIF = !"SI".equals(aplicaNIIF);
			}
		}
		else {
			verInterfazContabilidad = true;
			verInterfazNIIF = "SI".equals(aplicaNIIF);
		}
	}

	public void validarVacios() {

		if(registro.getCampos().get("CUENTAACTIVOMAN") == null) {
			registro.getCampos().put("CUENTAACTIVOMAN", null);	
		}
		if(registro.getCampos().get("CUENTAACTIVONEXP") == null) {
			registro.getCampos().put("CUENTAACTIVONEXP", null);
		}
		if( registro.getCampos().get("NIIF_CUENTAACTIVONEXP") == null) {
			registro.getCampos().put("NIIF_CUENTAACTIVONEXP", null);
		}
		if(registro.getCampos().get("NIIF_CUENTAACTIVOMAN") == null) {
			registro.getCampos().put("NIIF_CUENTAACTIVOMAN", null);
		}

	}

	/**
	 * Metodo ejecutado antes de realizar la insercion del registro
	 *
	 */
	@Override
	public boolean insertarAntes() {
		// <CODIGO_DESARROLLADO>
		registro.getCampos().put("COMPANIA", compania);
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
	 * Metodo ejecutado antes de realizar la insercion y actualizacion
	 * del registro
	 * 
	 */
	@Override
	public boolean actualizarAntes() {
		// <CODIGO_DESARROLLADO>

		if (accion.equals(ACCION_MODIFICAR)) {
			registro.getCampos()
			.remove(GeneralParameterEnum.COMPANIA.getName());

			registro.getCampos()
			.remove(CAlmacenContabilidadsControladorEnum.TIPO
					.getValue());
		}

		validarVacios();

		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion y
	 * actualizacion del registro
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
	 * Metodo ejecutado despues de realizar la eliminacion del
	 * registro
	 * 
	 */
	@Override
	public boolean eliminarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	// <SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable anio
	 * 
	 * @return anio
	 */
	public String getAnio() {
		return anio;
	}

	/**
	 * Asigna la variable anio
	 * 
	 * @param anio
	 * Variable a asignar en anio
	 */
	public void setAnio(String anio) {
		this.anio = anio;
	}

	/**
	 * Retorna la variable nombreCuentaActivo
	 * 
	 * @return nombreCuentaActivo
	 */
	public String getNombreCuentaActivo() {
		return nombreCuentaActivo;
	}

	/**
	 * Asigna la variable nombreCuentaActivo
	 * 
	 * @param nombreCuentaActivo
	 * Variable a asignar en nombreCuentaActivo
	 */
	public void setNombreCuentaActivo(String nombreCuentaActivo) {
		this.nombreCuentaActivo = nombreCuentaActivo;
	}

	/**
	 * Retorna la variable nombreCuentaActivoI
	 * 
	 * @return nombreCuentaActivoI
	 */
	public String getNombreCuentaActivoI() {
		return nombreCuentaActivoI;
	}

	/**
	 * Asigna la variable nombreCuentaActivoI
	 * 
	 * @param nombreCuentaActivoI
	 * Variable a asignar en nombreCuentaActivoI
	 */
	public void setNombreCuentaActivoI(String nombreCuentaActivoI) {
		this.nombreCuentaActivoI = nombreCuentaActivoI;
	}

	/**
	 * Retorna la variable nombreCuentaActivoS
	 * 
	 * @return nombreCuentaActivoS
	 */
	public String getNombreCuentaActivoS() {
		return nombreCuentaActivoS;
	}

	public boolean isCentroCosto() {
		return centroCosto;
	}

	public void setCentroCosto(boolean centroCosto) {
		this.centroCosto = centroCosto;
	}

	/**
	 * Asigna la variable nombreCuentaActivoS
	 * 
	 * @param nombreCuentaActivoS
	 * Variable a asignar en nombreCuentaActivoS
	 */
	public void setNombreCuentaActivoS(String nombreCuentaActivoS) {
		this.nombreCuentaActivoS = nombreCuentaActivoS;
	}

	/**
	 * Retorna la variable nombreCuentaActivoR
	 * 
	 * @return nombreCuentaActivoR
	 */
	public String getNombreCuentaActivoR() {
		return nombreCuentaActivoR;
	}

	/**
	 * Asigna la variable nombreCuentaActivoR
	 * 
	 * @param nombreCuentaActivoR
	 * Variable a asignar en nombreCuentaActivoR
	 */
	public void setNombreCuentaActivoR(String nombreCuentaActivoR) {
		this.nombreCuentaActivoR = nombreCuentaActivoR;
	}

	/**
	 * Retorna la variable nombreCuentaActivoC
	 * 
	 * @return nombreCuentaActivoC
	 */
	public String getNombreCuentaActivoC() {
		return nombreCuentaActivoC;
	}

	/**
	 * Asigna la variable nombreCuentaActivoC
	 * 
	 * @param nombreCuentaActivoC
	 * Variable a asignar en nombreCuentaActivoC
	 */
	public void setNombreCuentaActivoC(String nombreCuentaActivoC) {
		this.nombreCuentaActivoC = nombreCuentaActivoC;
	}

	/**
	 * Retorna la variable niifNombreCuentaActivo
	 * 
	 * @return niifNombreCuentaActivo
	 */
	public String getNiifNombreCuentaActivo() {
		return niifNombreCuentaActivo;
	}

	/**
	 * Asigna la variable niifNombreCuentaActivo
	 * 
	 * @param niifNombreCuentaActivo
	 * Variable a asignar en niifNombreCuentaActivo
	 */
	public void setNiifNombreCuentaActivo(String niifNombreCuentaActivo) {
		this.niifNombreCuentaActivo = niifNombreCuentaActivo;
	}

	/**
	 * Retorna la variable niifNombreCuentaActivoI
	 * 
	 * @return niifNombreCuentaActivoI
	 */
	public String getNiifNombreCuentaActivoI() {
		return niifNombreCuentaActivoI;
	}

	/**
	 * Asigna la variable niifNombreCuentaActivoI
	 * 
	 * @param niifNombreCuentaActivoI
	 * Variable a asignar en niifNombreCuentaActivoI
	 */
	public void setNiifNombreCuentaActivoI(String niifNombreCuentaActivoI) {
		this.niifNombreCuentaActivoI = niifNombreCuentaActivoI;
	}

	/**
	 * Retorna la variable niifNombreCuentaActivoS
	 * 
	 * @return niifNombreCuentaActivoS
	 */
	public String getNiifNombreCuentaActivoS() {
		return niifNombreCuentaActivoS;
	}

	/**
	 * Asigna la variable niifNombreCuentaActivoS
	 * 
	 * @param niifNombreCuentaActivoS
	 * Variable a asignar en niifNombreCuentaActivoS
	 */
	public void setNiifNombreCuentaActivoS(String niifNombreCuentaActivoS) {
		this.niifNombreCuentaActivoS = niifNombreCuentaActivoS;
	}

	/**
	 * Retorna la variable niifNombreCuentaActivoR
	 * 
	 * @return niifNombreCuentaActivoR
	 */
	public String getNiifNombreCuentaActivoR() {
		return niifNombreCuentaActivoR;
	}

	/**
	 * Asigna la variable niifNombreCuentaActivoR
	 * 
	 * @param niifNombreCuentaActivoR
	 * Variable a asignar en niifNombreCuentaActivoR
	 */
	public void setNiifNombreCuentaActivoR(String niifNombreCuentaActivoR) {
		this.niifNombreCuentaActivoR = niifNombreCuentaActivoR;
	}

	public boolean isVerTipoC() {
		return verTipoC;
	}

	public void setVerTipoC(boolean verTipoC) {
		this.verTipoC = verTipoC;
	}

	public String getAplicaNIIF() {
		return aplicaNIIF;
	}

	public void setAplicaNIIF(String aplicaNIIF) {
		this.aplicaNIIF = aplicaNIIF;
	}

	public boolean isVerInterfazNIIF() {
		return verInterfazNIIF;
	}

	public void setVerInterfazNIIF(boolean verInterfazNIIF) {
		this.verInterfazNIIF = verInterfazNIIF;
	}

	public boolean isVerCentroCosto() {
		return verCentroCosto;
	}

	public void setVerCentroCosto(boolean verCentroCosto) {
		this.verCentroCosto = verCentroCosto;
	}

	public boolean isVerRetiro() {
		return verRetiro;
	}

	public void setVerRetiro(boolean verRetiro) {
		this.verRetiro = verRetiro;
	}

	public boolean isVerInterfazContabilidad() {
		return verInterfazContabilidad;
	}

	public void setVerInterfazContabilidad(boolean verInterfazContabilidad) {
		this.verInterfazContabilidad = verInterfazContabilidad;
	}

	public boolean isVerDialogo() {
		return verDialogo;
	}

	public void setVerDialogo(boolean verDialogo) {
		this.verDialogo = verDialogo;
	}

	public String getTextoMensaje() {
		return textoMensaje;
	}

	public void setTextoMensaje(String textoMensaje) {
		this.textoMensaje = textoMensaje;
	}

	// </SET_GET_ATRIBUTOS>
	// <SET_GET_LISTAS>

	/**
	 * Retorna la lista listaanio
	 * 
	 * @return listaanio
	 */
	public List<Registro> getListaanio() {
		return listaanio;
	}

	/**
	 * Asigna la lista listaanio
	 * 
	 * @param listaanio
	 * Variable a asignar en listaanio
	 */
	public void setListaanio(List<Registro> listaanio) {
		this.listaanio = listaanio;
	}
	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>

	/**
	 * Retorna la lista listacuentaActivo
	 * 
	 * @return listacuentaActivo
	 */
	public RegistroDataModelImpl getListacuentaActivo() {
		return listacuentaActivo;
	}

	/**
	 * Asigna la lista listacuentaActivo
	 * 
	 * @param listacuentaActivo
	 * Variable a asignar en listacuentaActivo
	 */
	public void setListacuentaActivo(RegistroDataModelImpl listacuentaActivo) {
		this.listacuentaActivo = listacuentaActivo;
	}

	/**
	 * Retorna la lista listaCuentaActivoI
	 * 
	 * @return listaCuentaActivoI
	 */
	public RegistroDataModelImpl getListaCuentaActivoI() {
		return listaCuentaActivoI;
	}

	/**
	 * Asigna la lista listaCuentaActivoI
	 * 
	 * @param listaCuentaActivoI
	 * Variable a asignar en listaCuentaActivoI
	 */
	public void setListaCuentaActivoI(
			RegistroDataModelImpl listaCuentaActivoI) {
		this.listaCuentaActivoI = listaCuentaActivoI;
	}

	/**
	 * Retorna la lista listaCuentaActivoS
	 * 
	 * @return listaCuentaActivoS
	 */
	public RegistroDataModelImpl getListaCuentaActivoS() {
		return listaCuentaActivoS;
	}

	/**
	 * Asigna la lista listaCuentaActivoS
	 * 
	 * @param listaCuentaActivoS
	 * Variable a asignar en listaCuentaActivoS
	 */
	public void setListaCuentaActivoS(
			RegistroDataModelImpl listaCuentaActivoS) {
		this.listaCuentaActivoS = listaCuentaActivoS;
	}

	/**
	 * Retorna la lista listaCuentaActivoR
	 * 
	 * @return listaCuentaActivoR
	 */
	public RegistroDataModelImpl getListaCuentaActivoR() {
		return listaCuentaActivoR;
	}

	/**
	 * Asigna la lista listaCuentaActivoR
	 * 
	 * @param listaCuentaActivoR
	 * Variable a asignar en listaCuentaActivoR
	 */
	public void setListaCuentaActivoR(
			RegistroDataModelImpl listaCuentaActivoR) {
		this.listaCuentaActivoR = listaCuentaActivoR;
	}

	/**
	 * Retorna la lista listaCuentaActivoC
	 * 
	 * @return listaCuentaActivoC
	 */
	public RegistroDataModelImpl getListaCuentaActivoC() {
		return listaCuentaActivoC;
	}

	/**
	 * Asigna la lista listaCuentaActivoC
	 * 
	 * @param listaCuentaActivoC
	 * Variable a asignar en listaCuentaActivoC
	 */
	public void setListaCuentaActivoC(
			RegistroDataModelImpl listaCuentaActivoC) {
		this.listaCuentaActivoC = listaCuentaActivoC;
	}

	/**
	 * Retorna la lista listaNiifCuentaActivo
	 * 
	 * @return listaNiifCuentaActivo
	 */
	public RegistroDataModelImpl getListaNiifCuentaActivo() {
		return listaNiifCuentaActivo;
	}

	/**
	 * Asigna la lista listaNiifCuentaActivo
	 * 
	 * @param listaNiifCuentaActivo
	 * Variable a asignar en listaNiifCuentaActivo
	 */
	public void setListaNiifCuentaActivo(
			RegistroDataModelImpl listaNiifCuentaActivo) {
		this.listaNiifCuentaActivo = listaNiifCuentaActivo;
	}

	/**
	 * Retorna la lista listaNiifCuentaActivoI
	 * 
	 * @return listaNiifCuentaActivoI
	 */
	public RegistroDataModelImpl getListaNiifCuentaActivoI() {
		return listaNiifCuentaActivoI;
	}

	/**
	 * Asigna la lista listaNiifCuentaActivoI
	 * 
	 * @param listaNiifCuentaActivoI
	 * Variable a asignar en listaNiifCuentaActivoI
	 */
	public void setListaNiifCuentaActivoI(
			RegistroDataModelImpl listaNiifCuentaActivoI) {
		this.listaNiifCuentaActivoI = listaNiifCuentaActivoI;
	}

	/**
	 * Retorna la lista listaNiifCuentaActivoS
	 * 
	 * @return listaNiifCuentaActivoS
	 */
	public RegistroDataModelImpl getListaNiifCuentaActivoS() {
		return listaNiifCuentaActivoS;
	}

	/**
	 * Asigna la lista listaNiifCuentaActivoS
	 * 
	 * @param listaNiifCuentaActivoS
	 * Variable a asignar en listaNiifCuentaActivoS
	 */
	public void setListaNiifCuentaActivoS(
			RegistroDataModelImpl listaNiifCuentaActivoS) {
		this.listaNiifCuentaActivoS = listaNiifCuentaActivoS;
	}

	/**
	 * Retorna la lista listaNiifCuentaActivoR
	 * 
	 * @return listaNiifCuentaActivoR
	 */
	public RegistroDataModelImpl getListaNiifCuentaActivoR() {
		return listaNiifCuentaActivoR;
	}

	/**
	 * Asigna la lista listaNiifCuentaActivoR
	 * 
	 * @param listaNiifCuentaActivoR
	 * Variable a asignar en listaNiifCuentaActivoR
	 */
	public void setListaNiifCuentaActivoR(
			RegistroDataModelImpl listaNiifCuentaActivoR) {
		this.listaNiifCuentaActivoR = listaNiifCuentaActivoR;
	}

	/**
	 * @return the centroCostoNiif
	 */
	public boolean isCentroCostoNiif() {
		return centroCostoNiif;
	}

	/**
	 * @param centroCostoNiif
	 * the centroCostoNiif to set
	 */
	public void setCentroCostoNiif(boolean centroCostoNiif) {
		this.centroCostoNiif = centroCostoNiif;
	}

	/**
	 * @return the verCentroCostoNiif
	 */
	public boolean isVerCentroCostoNiif() {
		return verCentroCostoNiif;
	}

	/**
	 * @param verCentroCostoNiif
	 * the verCentroCostoNiif to set
	 */
	public void setVerCentroCostoNiif(boolean verCentroCostoNiif) {
		this.verCentroCostoNiif = verCentroCostoNiif;
	}

	public boolean isVerActualizarTodos() {
		return verActualizarTodos;
	}

	public void setVerActualizarTodos(boolean verActualizarTodos) {
		this.verActualizarTodos = verActualizarTodos;
	}

	public boolean isVisiblePreparar() {
		return visiblePreparar;
	}

	public void setVisiblePreparar(boolean visiblePreparar) {
		this.visiblePreparar = visiblePreparar;
	}

	public int getAnioInicial() {
		return anioInicial;
	}

	public void setAnioInicial(int anioInicial) {
		this.anioInicial = anioInicial;
	}

	public int getAnioFinal() {
		return anioFinal;
	}

	public void setAnioFinal(int anioFinal) {
		this.anioFinal = anioFinal;
	}

	public List<Registro> getListaAnioInicial() {
		return listaAnioInicial;
	}

	public void setListaAnioInicial(List<Registro> listaAnioInicial) {
		this.listaAnioInicial = listaAnioInicial;
	}

	public List<Registro> getListaAnioFinal() {
		return listaAnioFinal;
	}

	public void setListaAnioFinal(List<Registro> listaAnioFinal) {
		this.listaAnioFinal = listaAnioFinal;
	}

	public RegistroDataModelImpl getListaNiifCuentaActivoMan() {
		return listaNiifCuentaActivoMan;
	}

	public void setListaNiifCuentaActivoMan(RegistroDataModelImpl listaNiifCuentaActivoMan) {
		this.listaNiifCuentaActivoMan = listaNiifCuentaActivoMan;
	}

	public RegistroDataModelImpl getListaNiifCuentaActivoNexp() {
		return listaNiifCuentaActivoNexp;
	}

	public void setListaNiifCuentaActivoNexp(RegistroDataModelImpl listaNiifCuentaActivoNexp) {
		this.listaNiifCuentaActivoNexp = listaNiifCuentaActivoNexp;
	}

	public String getNiifNombreCuentaActivoMan() {
		return niifNombreCuentaActivoMan;
	}

	public void setNiifNombreCuentaActivoMan(String niifNombreCuentaActivoMan) {
		this.niifNombreCuentaActivoMan = niifNombreCuentaActivoMan;
	}

	public String getNiifNombreCuentaActivoNexp() {
		return niifNombreCuentaActivoNexp;
	}

	public void setNiifNombreCuentaActivoNexp(String niifNombreCuentaActivoNexp) {
		this.niifNombreCuentaActivoNexp = niifNombreCuentaActivoNexp;
	}

	public String getNombreCuentaActivoMan() {
		return nombreCuentaActivoMan;
	}

	public void setNombreCuentaActivoMan(String nombreCuentaActivoMan) {
		this.nombreCuentaActivoMan = nombreCuentaActivoMan;
	}

	public String getNombreCuentaActivoNexp() {
		return nombreCuentaActivoNexp;
	}

	public void setNombreCuentaActivoNexp(String nombreCuentaActivoNexp) {
		this.nombreCuentaActivoNexp = nombreCuentaActivoNexp;
	}

	public RegistroDataModelImpl getListaCuentaActivoMan() {
		return listaCuentaActivoMan;
	}

	public void setListaCuentaActivoMan(RegistroDataModelImpl listaCuentaActivoMan) {
		this.listaCuentaActivoMan = listaCuentaActivoMan;
	}

	public RegistroDataModelImpl getListaCuentaActivoNexp() {
		return listaCuentaActivoNexp;
	}

	public void setListaCuentaActivoNexp(RegistroDataModelImpl listaCuentaActivoNexp) {
		this.listaCuentaActivoNexp = listaCuentaActivoNexp;
	}

	/**
	 * @return the fuenteRecursos
	 */
	public boolean isFuenteRecursos() {
		return fuenteRecursos;
	}

	/**
	 * @param fuenteRecursos the fuenteRecursos to set
	 */
	public void setFuenteRecursos(boolean fuenteRecursos) {
		this.fuenteRecursos = fuenteRecursos;
	}

	/**
	 * @return the visibleFuente
	 */
	public boolean isVisibleFuente() {
		return visibleFuente;
	}

	/**
	 * @param visibleFuente the visibleFuente to set
	 */
	public void setVisibleFuente(boolean visibleFuente) {
		this.visibleFuente = visibleFuente;
	}

	/**
	 * @return the fuenteRecursoNiif
	 */
	public boolean isFuenteRecursoNiif() {
		return fuenteRecursoNiif;
	}

	/**
	 * @param fuenteRecursoNiif the fuenteRecursoNiif to set
	 */
	public void setFuenteRecursoNiif(boolean fuenteRecursoNiif) {
		this.fuenteRecursoNiif = fuenteRecursoNiif;
	}
	
	

	// </SET_GET_LISTAS_COMBO_GRANDE>
	// <SET_GET_LISTAS_SUBFORM>
	// </SET_GET_LISTAS_SUBFORM>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_ADICIONALES>
	// </SET_GET_ADICIONALES>
}
