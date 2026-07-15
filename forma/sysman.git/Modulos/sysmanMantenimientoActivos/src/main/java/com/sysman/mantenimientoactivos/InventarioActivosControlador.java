/*-
 * InventarioActivosControlador.java
 *
 * 1.0
 * 
 * 23/11/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.mantenimientoactivos;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
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
import javax.faces.event.ActionEvent;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.cache.enums.UrlServiceCache;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.mantenimientoactivos.ejb.EjbMantenimientoActivosCeroRemote;
import com.sysman.mantenimientoactivos.enums.InventarioActivosControladorEnum;
import com.sysman.mantenimientoactivos.enums.InventarioActivosControladorUrlEnum;
import com.sysman.mantenimientoactivos.enums.InventarioparqueautomotorControladorEnum;
import com.sysman.mantenimientoactivos.enums.InventarioparqueautomotorControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que permite hacer un crud sobre el inventario de activos.
 * 
 * @version 1.0, 23/11/2018
 * @author jgomezp
 * 
 * @version 2.0, 01/12/2018
 * @author jrojas
 * 
 */
@ManagedBean
@ViewScoped
public class InventarioActivosControlador extends BeanBaseDatosAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	private final String modulo;
	private final String usuario;
	private StreamedContent archivoDescarga;
	private List<Registro> listaPais;
	private List<Registro> listaDepartamento;
	private List<Registro> listaMunicipio;
	private List<Registro> listaTipo;
	private List<Registro> listaMarca;
	private List<Registro> listaLugarUbicacion;
	private List<Registro> listaSerieElemento;
	private List<Registro> listaPlacasAnteriores;
	private List<Registro> listaCodProcedencia;
	private List<Registro> listaParteFuncional;
	private List<Registro> listaVehiculoseguro;
	private List<Registro> listaSubvehiculopartes;
	private List<Registro> listaImagenvehiculos;
	private List<Registro> listaSecundario160;
	private List<Registro> listaFotosaccidentes;
	private List<Registro> listaSecundario141;
	private RegistroDataModelImpl listaNIT;
	private RegistroDataModelImpl listaNITE;
	private RegistroDataModelImpl listaAseguradora;
	private RegistroDataModelImpl listaNumeroPoliza;
	private RegistroDataModelImpl listaNumeroPolizaE;
	private RegistroDataModelImpl listaAseguradoraE;
	private RegistroDataModelImpl listaCodigoElemento;
	private RegistroDataModelImpl listaEntidadcomodataria;
	private String auxiliar;
	private String opcion;
	private String registroauxSucursalGases;
	private String registroauxNombreGases;
	private String codigoElemento;
	private String registroauxSucursalAseguradora;
	private String registroauxNombreSeguro;
	private String registroauxSucursalSeguro;
	private String nombreConductorAux;
	private String nombreComodatariaAux;
	private String parametroVehiculo = "RUTA DIGITALIZADO VEHICULOS";
	private String parametroAccidentes = "RUTA DIGITALIZADO ACCIDENTES";
	private Registro registroSubVehiculoSeguro;
	private Registro registroSubSubVehiculoPartes;
	private Registro registroSubImagenVehiculos;
	private Registro registroSubSecundario160;
	private Registro registroSubSecundario141;
	private Registro registroSubfotosaccidentes;
	// </DECLARAR_ADICIONALES>
	/**
	 * Crea una nueva instancia de InventarioActivosControlador
	 */
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

	@EJB
	private EjbMantenimientoActivosCeroRemote ejbMantenimientoActivosCero;

	@SuppressWarnings("unchecked")
	public InventarioActivosControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		usuario = SessionUtil.getUser().getCodigo();
		try {
			numFormulario = GeneralCodigoFormaEnum.INVENTARIO_ACTIVOS_CONTROLADOR.getCodigo();

			registroSubVehiculoSeguro = new Registro(new HashMap<String, Object>());
			registroSubSubVehiculoPartes = new Registro(new HashMap<String, Object>());
			registroSubImagenVehiculos = new Registro(new HashMap<String, Object>());
			registroSubSecundario160 = new Registro(new HashMap<String, Object>());
			registroSubfotosaccidentes = new Registro(new HashMap<String, Object>());
			registroSubSecundario141 = new Registro(new HashMap<String, Object>());

			Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
			SessionUtil.cleanFlash();
			if (parametrosEntrada != null) {
				rid = (Map<String, Object>) parametrosEntrada.get("rid");
				opcion = SysmanFunciones.nvl(parametrosEntrada.get("opcion"), "1").toString();
			}
			validarPermisos();
			// </INI_ADICIONAL>
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
		tabla = InventarioActivosControladorEnum.INVENTARIO_PARQUE_AUTOMOTOR.getValue();
		buscarLlave();
		asignarOrigenDatos();

	}

	/**
	 * Se realiza la asignacion de la variable origenDatos por la consulta
	 * correspondiente del formulario
	 * 
	 */
	@Override
	public void asignarOrigenDatos() {
		parametrosListado.put(InventarioActivosControladorEnum.PARESTADO.getValue(), opcion);
		parametrosListado.put("ESAUTOMOTOR", 0);
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		urlListado = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(InventarioActivosControladorUrlEnum.URL507004.getValue());
		urlLectura = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(InventarioActivosControladorUrlEnum.URL507006.getValue());
		urlCreacion = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(InventarioActivosControladorUrlEnum.URL507009.getValue());
		urlActualizacion = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(InventarioActivosControladorUrlEnum.URL507010.getValue());
		urlEliminacion = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID("50700D");

	}

	/**
	 * En este metodo se hace la invocacion de lo metodos de carga de todas las
	 * listas, menos las que son de subformularios
	 */
	@Override
	public void iniciarListas() {
		// <CARGAR_LISTA_COMBO_GRANDE>
		cargarListaNIT();
		cargarListaNITE();
		cargarListaAseguradora();
		cargarListaAseguradoraE();
		cargarListaEntidadcomodataria();
		cargarListaNumeroPoliza();
		cargarListaNumeroPolizaE();

		cargarListaTipo();
		cargarListaMarca();
		cargarListaPais();

		cargarListaLugarUbicacion();
		cargarListaCodigoElemento();
		cargarListaCodProcedencia();
		cargarListaPlacasAnteriores();
		cargarListaParteFuncional();

		// </CARGAR_LISTA>
	}

	/**
	 * En este metodo se hace la invocacion de lo metodos de carga de todas las
	 * listas que son de subformularios
	 */
	@Override
	public void iniciarListasSub() {
		// <CARGAR_LISTAS_SUBFORM>
		cargarListaVehiculoseguro();
		cargarListaSubvehiculopartes();
		cargarListaImagenvehiculos();
		cargarListaSecundario160();
		cargarListaFotosaccidentes();
		// </CARGAR_LISTAS_SUBFORM>
		// <CREAR_ARBOLES>
		// </CREAR_ARBOLES>
	}

	/**
	 * En este metodo se iguala a null todas las listas de los subformularios
	 */
	@Override
	public void iniciarListasSubNulo() {
		// <CARGAR_LISTAS_SUBFORM_NULL>
		listaVehiculoseguro = null;
		listaSubvehiculopartes = null;
		listaImagenvehiculos = null;
		listaSecundario160 = null;
		listaFotosaccidentes = null;
		// </CARGAR_LISTAS_SUBFORM_NULL>
	}

	/**
	 * Se realiza la asignacion de la variable origenGrilla por la consulta
	 * correspondiente de la grilla del formulario, se hace la asignacion de dicha
	 * consulta a los objetos listaInicial y listaInicialF
	 */

	/**
	 * 
	 * Carga la lista listaVehiculoseguro @throws
	 *
	 */
	public void cargarListaVehiculoseguro() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.NUMERO.getName(),
				registro.getCampos().get(GeneralParameterEnum.PLACA.getName()));
		try {
			listaVehiculoseguro = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													InventarioActivosControladorUrlEnum.URL1001.getValue())
											.getUrl(),
									param),
							CacheUtil.getLlaveServicio(UrlServiceCache.SYSMANDSUNIST,
									InventarioActivosControladorEnum.VEHICULOSEGURO.getValue()));
		} catch (SystemException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaSubvehiculopartes
	 *
	 */
	public void cargarListaSubvehiculopartes() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.PLACA.getName(), registro.getCampos().get(GeneralParameterEnum.PLACA.getName()));
		try {
			listaSubvehiculopartes = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													InventarioActivosControladorUrlEnum.URL1004.getValue())
											.getUrl(),
									param),
							CacheUtil.getLlaveServicio(UrlServiceCache.SYSMANDSUNIST,
									InventarioActivosControladorEnum.VEHICULO_PARTES.getValue()));

		} catch (SystemException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaImagenvehiculos
	 *
	 */
	public void cargarListaImagenvehiculos() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.PLACA.getName(), registro.getCampos().get(GeneralParameterEnum.PLACA.getName()));
		param.put(InventarioActivosControladorEnum.SEPARADOR.getValue(), File.separator);
		try {
			listaImagenvehiculos = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													InventarioActivosControladorUrlEnum.URL1005.getValue())
											.getUrl(),
									param),
							CacheUtil.getLlaveServicio(UrlServiceCache.SYSMANDSUNIST,
									InventarioActivosControladorEnum.FOTOSVEHICULOS.getValue()));
		} catch (SystemException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaSecundario160
	 *
	 */
	public void cargarListaSecundario160() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.PLACA.getName(), registro.getCampos().get(GeneralParameterEnum.PLACA.getName()));
		try {
			listaSecundario160 = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													InventarioActivosControladorUrlEnum.URL1006.getValue())
											.getUrl(),
									param),
							CacheUtil.getLlaveServicio(UrlServiceCache.SYSMANDSUNIST,
									InventarioActivosControladorEnum.ACCIDENTALIDAD.getValue()));

		} catch (SystemException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaFotosaccidentes
	 *
	 */
	public void cargarListaFotosaccidentes() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.PLACA.getName(), registro.getCampos().get(GeneralParameterEnum.PLACA.getName()));
		param.put(InventarioActivosControladorEnum.SEPARADOR.getValue(), File.separator);
		try {
			listaFotosaccidentes = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													InventarioActivosControladorUrlEnum.URL1007.getValue())
											.getUrl(),
									param),
							CacheUtil.getLlaveServicio(UrlServiceCache.SYSMANDSUNIST,
									InventarioActivosControladorEnum.FOTOSACCIDENTES.getValue()));
		} catch (SystemException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	// <METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaPais
	 *
	 */
	public void cargarListaPais() {
		try {
			listaPais = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													InventarioActivosControladorUrlEnum.URL1015.getValue())
											.getUrl(),
									null));

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaDepartamento
	 *
	 */
	public void cargarListaDepartamento() {
		Map<String, Object> param = new TreeMap<>();
		param.put(InventarioActivosControladorEnum.PAIS.getValue(), registro.getCampos().get("PAIS"));
		try {
			listaDepartamento = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													InventarioActivosControladorUrlEnum.URL1016.getValue())
											.getUrl(),
									param));

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaMunicipio
	 *
	 */
	public void cargarListaMunicipio() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.DEPARTAMENTO.getName(),
				registro.getCampos().get(GeneralParameterEnum.DEPARTAMENTO.getName()));
		param.put(InventarioActivosControladorEnum.PAIS.getValue(),
				registro.getCampos().get(InventarioActivosControladorEnum.PAIS.getValue()));
		try {
			listaMunicipio = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													InventarioActivosControladorUrlEnum.URL1008.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaTipo
	 *
	 */
	public void cargarListaTipo() {

		try {
			listaTipo = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													InventarioActivosControladorUrlEnum.URL1009.getValue())
											.getUrl(),
									null));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaMarca
	 *
	 */
	public void cargarListaMarca() {
		try {
			listaMarca = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													InventarioActivosControladorUrlEnum.URL1010.getValue())
											.getUrl(),
									null));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaLugarUbicacion
	 *
	 */
	public void cargarListaLugarUbicacion() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		try {
			listaLugarUbicacion = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													InventarioActivosControladorUrlEnum.URL1012.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaSerieElemento
	 *
	 */
	public void cargarListaSerieElemento() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ELEMENTO.getName(), registro.getCampos().get("CODIGO_ELEMENTO"));

		try {
			listaSerieElemento = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													InventarioActivosControladorUrlEnum.URL1014.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cambiarSerieElemento() {
		// <METODOS_CARGAR_LISTA>
	}

	public void cambiarNumeroPoliza() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarNumeroPolizaC(int rowNum) {
		// <CODIGO_DESARROLLADO>
		listaSecundario141.get(rowNum).getCampos().put("NOMBRECENTRO", registroauxNombreGases);
		listaSecundario141.get(rowNum).getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
				registroauxSucursalGases);
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Carga la lista listaPlacasAnteriores
	 *
	 */
	public void cargarListaPlacasAnteriores() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		try {
			listaPlacasAnteriores = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													InventarioActivosControladorUrlEnum.URL1018.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * 
	 * Carga la lista listaCodProcedencia
	 *
	 */
	public void cargarListaCodProcedencia() {
		try {
			listaCodProcedencia = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													InventarioActivosControladorUrlEnum.URL1017.getValue())
											.getUrl(),
									null));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaParteFuncional
	 *
	 */
	public void cargarListaParteFuncional() {
		try {
			listaParteFuncional = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													InventarioActivosControladorUrlEnum.URL1020.getValue())
											.getUrl(),
									null));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaNIT
	 *
	 */
	public void cargarListaNIT() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(InventarioActivosControladorUrlEnum.URL1021.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaNIT = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				InventarioActivosControladorEnum.NIT.getValue());
	}

	public void cargarListaNITE() {
		listaNITE = listaNIT;
	}

	/**
	 * 
	 * Carga la lista listaAseguradora
	 *
	 */
	public void cargarListaNumeroPoliza() {
		listaNumeroPoliza = listaNIT;
	}

	public void cargarListaNumeroPolizaE() {
		listaNumeroPolizaE = listaNumeroPoliza;
	}

	public void cargarListaAseguradora() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(InventarioActivosControladorUrlEnum.URL1000.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaAseguradora = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				InventarioActivosControladorEnum.NITASEGURADORA.getValue());
	}

	public void cargarListaSecundario141() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.PLACA.getName(), registro.getCampos().get(GeneralParameterEnum.PLACA.getName()));
		try {
			listaSecundario141 = RegistroConverter.toListRegistro(
					requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											InventarioparqueautomotorControladorUrlEnum.URL17915.getValue())
									.getUrl(),
							param),
					CacheUtil.getLlaveServicio(UrlServiceCache.SYSMANDSUNIST,
							InventarioparqueautomotorControladorEnum.VEHICULOGASES.getValue()));

		} catch (SysmanException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaAseguradora
	 *
	 */
	public void cargarListaAseguradoraE() {
		listaAseguradoraE = listaAseguradora;
	}

	/**
	 * 
	 * Carga la lista listaCodigoElemento
	 *
	 */
	public void cargarListaCodigoElemento() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(InventarioActivosControladorUrlEnum.URL1024.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaCodigoElemento = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGOELEMENTO.getName());
	}

	/**
	 * 
	 * Carga la lista listaEntidadcomodataria
	 *
	 */
	public void cargarListaEntidadcomodataria() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(InventarioActivosControladorUrlEnum.URL1025.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		listaEntidadcomodataria = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, InventarioActivosControladorEnum.NIT.getValue());
	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_CAMBIAR>
	/**
	 * 
	 * Metodo ejecutado al cargar un archivo desde el control Lector
	 *
	 *
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 *
	 */
	public void cargarArchivoLector(FileUploadEvent event) {
		// <CODIGO_DESARROLLADO>
		try {
			String ruta = generarRuta(parametroVehiculo,
					String.valueOf(registro.getCampos().get(GeneralParameterEnum.PLACA.getName())));
			if (ruta != null) {
				String nombreArch = event.getFile().getFileName();
				nombreArch = nombreArch.contains(File.separator)
						? nombreArch.substring(nombreArch.lastIndexOf(File.separator) + 1, nombreArch.length())
						: nombreArch;
				registroSubImagenVehiculos.getCampos().put(InventarioActivosControladorEnum.ARCHIVO.getValue(),
						SysmanFunciones.concatenar(ruta, nombreArch));
				agregarRegistroSubImagenvehiculos();
				JsfUtil.upload(event.getFile().getInputstream(), nombreArch, ruta);
			} else {
				JsfUtil.agregarMensajeAlerta(
						idioma.getString("TB_TB2492").replace("#$parametrovehiculo$#", parametroVehiculo));
			}
		} catch (IOException ex) {
			Logger.getLogger(InventarioActivosControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al cargar un archivo desde el control Lector2
	 *
	 *
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 *
	 */
	public void cargarArchivoLector2(FileUploadEvent event) {
		// <CODIGO_DESARROLLADO>
		try {
			String ruta = generarRuta(parametroAccidentes,
					String.valueOf(registro.getCampos().get(GeneralParameterEnum.PLACA.getName())));

			if (ruta != null) {
				String nombreArch = event.getFile().getFileName();
				nombreArch = nombreArch.contains(File.separator)
						? nombreArch.substring(nombreArch.lastIndexOf(File.separator) + 1, nombreArch.length())
						: nombreArch;
				registroSubfotosaccidentes.getCampos().put(InventarioActivosControladorEnum.ARCHIVO.getValue(),
						SysmanFunciones.concatenar(ruta, nombreArch));
				agregarRegistroSubFotosaccidentes();
				JsfUtil.upload(event.getFile().getInputstream(), nombreArch, ruta);
			} else {
				JsfUtil.agregarMensajeAlerta(
						idioma.getString("TB_TB2493").replace("#$parametroaccidentes$#", parametroAccidentes));
			}
		} catch (IOException ex) {
			Logger.getLogger(InventarioActivosControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
		// </CODIGO_DESARROLLADO>
	}

	public String generarRuta(String parametro, String documento) {
		String strRuta;
		String placa = documento.replace(" ", "");
		String aux = null;
		try {
			aux = ejbSysmanUtil.consultarParametro(compania, parametro, modulo, new Date(), true);
		} catch (SystemException ex) {
			Logger.getLogger(InventarioActivosControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
		if (aux != null) {
			if (!aux.substring(aux.length() - 1, aux.length()).equals(File.separator)) {
				aux = SysmanFunciones.concatenar(aux, File.separator);
			}
			strRuta = SysmanFunciones.concatenar(String.valueOf(aux), placa, File.separator);

			File folder = new File(strRuta);
			folder.mkdirs();
			return strRuta;
		} else {
			return null;
		}
	}

	/**
	 * Metodo ejecutado al cambiar el control Pais
	 * 
	 * 
	 */
	public void cambiarPais() {
		// <CODIGO_DESARROLLADO>
		cargarListaDepartamento();
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control Departamento
	 * 
	 * 
	 */
	public void cambiarDepartamento() {
		// <CODIGO_DESARROLLADO>
		cargarListaMunicipio();
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control Aseguradora y el control NIT en la
	 * fila seleccionada dentro de la grilla
	 * 
	 * 
	 * @param rowNum
	 *            indice de la fila seleccionada
	 */

	public void cambiarAseguradoraC(int rowNum) {
		listaSecundario160.get(rowNum).getCampos().put("SUCURSAL_ASEGURADORA", registroauxSucursalAseguradora);
	}

	public void cambiarLugarUbicacion(int rowNum) {
		registro.getCampos().put("SUCURSAL_PARQUEO",
				service.buscarEnLista(registro.getCampos().get("LUGAR_PARQUEO").toString(),
						GeneralParameterEnum.CODIGO.getName(), "SUCURSAL_CODIGO", listaLugarUbicacion));
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control CodigoElemento en la fila seleccionada
	 * dentro de la grilla
	 * 
	 * 
	 * @param rowNum
	 *            indice de la fila seleccionada
	 */
	public void cambiarCodigoElementoC(int rowNum) {
		listaSerieElemento.get(rowNum).getCampos().put("SERIE_ELEMENTO", codigoElemento);
		// </CODIGO_DESARROLLADO>
	}

	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaNIT
	 *
	 *
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaNIT(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSubVehiculoSeguro.getCampos().put("NIT", registroAux.getCampos().get("NIT"));

		registroSubVehiculoSeguro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
				registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
		registroSubVehiculoSeguro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
				registroAux.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaNIT
	 *
	 *
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaNITE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = (String) registroAux.getCampos().get("NIT");
		registroauxNombreSeguro = SysmanFunciones
				.nvl(registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()), "").toString();
		registroauxSucursalSeguro = SysmanFunciones
				.nvl(registroAux.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()), "").toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaAseguradora
	 *
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaAseguradora(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSubSecundario160.getCampos().put(InventarioActivosControladorEnum.NITASEGURADORA.getValue(),
				registroAux.getCampos().get("NITASEGURADORA"));

		registroSubSecundario160.getCampos().put(InventarioActivosControladorEnum.SUCURSAL_ASEGURADORA.getValue(),
				registroAux.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaAseguradora
	 *
	 *
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaAseguradoraE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = (String) registroAux.getCampos().get(InventarioActivosControladorEnum.NITASEGURADORA.getValue());
		registroauxSucursalAseguradora = SysmanFunciones
				.nvl(registroAux.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()), "").toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCodigoElemento
	 *
	 *
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoElemento(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(InventarioActivosControladorEnum.CODIGO_ELEMENTO.getValue(),
				registroAux.getCampos().get(GeneralParameterEnum.CODIGOELEMENTO.getName()));
		codigoElemento = GeneralParameterEnum.CODIGOELEMENTO.getName();
		cargarListaSerieElemento();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaEntidadcomodataria
	 *
	 *
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaEntidadcomodataria(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(InventarioActivosControladorEnum.ENTIDAD_COMODATARIA.getValue(),
				registroAux.getCampos().get(InventarioActivosControladorEnum.NIT.getValue()));

		registro.getCampos().put(InventarioActivosControladorEnum.NOMBRECOMODATARIA.getValue(),
				registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
		registro.getCampos().put(InventarioActivosControladorEnum.SUCURSAL_COMODATARIA.getValue(),
				registroAux.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()));
	}

	public void seleccionarFilaNumeroPoliza(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSubSecundario141.getCampos().put(InventarioActivosControladorEnum.NUMERODOCUMENTO.getValue(),
				SysmanFunciones.nvl(registroAux.getCampos().get("NIT"), " "));
		registroSubSecundario141.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
				registroAux.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()));
		registroSubSecundario141.getCampos().put(InventarioActivosControladorEnum.NOMBRECENTRO.getValue(),
				registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
	}

	public void seleccionarFilaNumeroPolizaE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones
				.nvl(registroAux.getCampos().get(InventarioparqueautomotorControladorEnum.NIT.getValue()), "")
				.toString();
		registroauxNombreGases = SysmanFunciones
				.nvl(registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()), "").toString();
		registroauxSucursalGases = SysmanFunciones
				.nvl(registroAux.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()), "").toString();
	}

	// </METODOS_COMBOS_GRANDES>
	// <METODOS_ARBOL>
	// </METODOS_ARBOL>
	// <METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton CmdCopiarModelo en la vista
	 *
	 *
	 */
	public void oprimirCmdCopiarModelo(ActionEvent ac) {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al oprimir el boton BT741
	 * 
	 * 
	 * @param reg
	 *            registro en el cual esta ubicado el boton oprimido dentro de la
	 *            grilla
	 * @param indice
	 *            indice en el cual esta ubicado el boton oprimido dentro de la
	 *            grilla
	 */
	public void oprimirDescargar(Registro reg, int indice) {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		File file = new File(SysmanFunciones
				.nvl(reg.getCampos().get(InventarioActivosControladorEnum.RUTA.getValue()), "").toString());

		try (FileInputStream filein = new FileInputStream(file)) {
			byte[] vec = new byte[(int) file.length()];
			filein.read(vec, 0, vec.length);
			archivoDescarga = JsfUtil.getArchivoDescarga(new ByteArrayInputStream(vec), SysmanFunciones
					.nvl(reg.getCampos().get(InventarioActivosControladorEnum.ARCHIVO.getValue()), "").toString());
		} catch (IOException | JRException ex) {
			Logger.getLogger(InventarioActivosControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeAlerta(ex.getMessage());
		}
		// </CODIGO_DESARROLLADO>
	}

	public void agregarRegistroSubSecundario141() {
		try {
			registroSubSecundario141.getCampos()
					.remove(InventarioparqueautomotorControladorEnum.FECHAINICIALM.getValue());
			registroSubSecundario141.getCampos()
					.remove(InventarioparqueautomotorControladorEnum.FECHAFINALM.getValue());
			registroSubSecundario141.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
			registroSubSecundario141.getCampos().put(GeneralParameterEnum.PLACA.getName(),
					registro.getCampos().get(GeneralParameterEnum.PLACA.getName()));
			registroSubSecundario141.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(), usuario);
			registroSubSecundario141.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());

			if ((registroSubSecundario141.getCampos().get(GeneralParameterEnum.FECHAFINAL.getName()) != null)
					&& (registroSubSecundario141.getCampos().get(GeneralParameterEnum.FECHAINICIAL.getName()) != null)
					&& ((Date) registroSubSecundario141.getCampos().get(GeneralParameterEnum.FECHAFINAL.getName()))
							.before((Date) registroSubSecundario141.getCampos()
									.get(GeneralParameterEnum.FECHAINICIAL.getName()))) {
				cargarListaSecundario141();
				JsfUtil.agregarMensajeAlerta(
						idioma.getString(InventarioparqueautomotorControladorEnum.TB_TB49.getValue()));
				return;
			}

			UrlBean urlCreate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(InventarioparqueautomotorControladorUrlEnum.URL17944.getValue());
			requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(), registroSubSecundario141.getCampos());
			JsfUtil.agregarMensajeInformativo(idioma.getString(Constantes.MSM_REGISTRO_INGRESADO));
		} catch (SystemException ex) {
			Logger.getLogger(InventarioparqueautomotorControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
		registroSubSecundario141.setCampos(new HashMap<String, Object>());
		cargarListaSecundario141();
	}

	public void editarRegSubSecundario141(RowEditEvent event) {
		Registro reg = (Registro) event.getObject();
		try {
			reg.getCampos().remove(InventarioparqueautomotorControladorEnum.FECHAINICIALM.getValue());
			reg.getCampos().remove(InventarioparqueautomotorControladorEnum.FECHAFINALM.getValue());
			reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(), usuario);
			reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());

			if ((reg.getCampos().get(GeneralParameterEnum.FECHAFINAL.getName()) != null)
					&& (reg.getCampos().get(GeneralParameterEnum.FECHAINICIAL.getName()) != null)
					&& ((Date) reg.getCampos().get(GeneralParameterEnum.FECHAFINAL.getName()))
							.before((Date) reg.getCampos().get(GeneralParameterEnum.FECHAINICIAL.getName()))) {
				JsfUtil.agregarMensajeAlerta(
						idioma.getString(InventarioparqueautomotorControladorEnum.TB_TB49.getValue()));
				cargarListaSecundario141();
				return;
			}

			UrlBean urlUpdate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(InventarioparqueautomotorControladorUrlEnum.URL17945.getValue());
			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), reg.getCampos(), reg.getLlave());

			JsfUtil.agregarMensajeInformativo(idioma.getString(Constantes.MSM_REGISTRO_MODIFICADO));
		} catch (SystemException ex) {
			Logger.getLogger(InventarioparqueautomotorControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}

		cargarListaSecundario141();
	}

	public void eliminarRegSubSecundario141(Registro reg) {
		try {

			UrlBean urlDelete = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(InventarioparqueautomotorControladorUrlEnum.URL17946.getValue());
			requestManager.delete(urlDelete.getUrl(), reg.getLlave());

			JsfUtil.agregarMensajeInformativo(idioma.getString(Constantes.MSM_REGISTRO_ELIMINADO));
		} catch (SystemException ex) {
			Logger.getLogger(InventarioparqueautomotorControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
		cargarListaSecundario141();
	}

	public void cancelarEdicionSecundario141() {
		cargarListaSecundario141();
		cargarListaSubvehiculopartes();
		cargarListaImagenvehiculos();
		cargarListaSecundario160();
		cargarListaFotosaccidentes();
	}

	/**
	 * Metodo ejecutado al oprimir el boton Descargar2
	 * 
	 * 
	 * @param reg
	 *            registro en el cual esta ubicado el boton oprimido dentro de la
	 *            grilla
	 * @param indice
	 *            indice en el cual esta ubicado el boton oprimido dentro de la
	 *            grilla
	 */
	public void oprimirDescargar2(Registro reg, int indice) {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		File file = new File(SysmanFunciones
				.nvl(reg.getCampos().get(InventarioActivosControladorEnum.RUTA.getValue()), "").toString());
		try (FileInputStream filein = new FileInputStream(file)) {
			byte[] vec = new byte[(int) file.length()];
			filein.read(vec, 0, vec.length);
			archivoDescarga = JsfUtil.getArchivoDescarga(new ByteArrayInputStream(vec), SysmanFunciones
					.nvl(reg.getCampos().get(InventarioActivosControladorEnum.ARCHIVO.getValue()), "").toString());
		} catch (IOException | JRException ex) {
			Logger.getLogger(InventarioActivosControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
		// </CODIGO_DESARROLLADO>
	}

	// </METODOS_BOTONES>
	// <METODOS_SUBFORM>
	/**
	 * Metodo de insercion del formulario Vehiculoseguro
	 * 
	 */
	public void agregarRegistroSubVehiculoseguro() {
		try {
			if ((registroSubVehiculoSeguro.getCampos().get(GeneralParameterEnum.FECHAFINAL.getName()) != null)
					&& (registroSubVehiculoSeguro.getCampos().get(GeneralParameterEnum.FECHAINICIAL.getName()) != null)
					&& ((Date) registroSubVehiculoSeguro.getCampos().get(GeneralParameterEnum.FECHAFINAL.getName()))
							.before((Date) registroSubVehiculoSeguro.getCampos()
									.get(GeneralParameterEnum.FECHAINICIAL.getName()))) {
				JsfUtil.agregarMensajeAlerta(idioma.getString(InventarioActivosControladorEnum.TB_TB49.getValue()));
				cargarListaVehiculoseguro();
				return;
			}

			registroSubVehiculoSeguro.getCampos().remove(InventarioActivosControladorEnum.FECHAINICIALM.getValue());
			registroSubVehiculoSeguro.getCampos().remove(InventarioActivosControladorEnum.FECHAFINALM.getValue());
			registroSubVehiculoSeguro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
			registroSubVehiculoSeguro.getCampos().put(GeneralParameterEnum.PLACA.getName(),
					registro.getCampos().get(GeneralParameterEnum.PLACA.getName()));

			registroSubVehiculoSeguro.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(), usuario);
			registroSubVehiculoSeguro.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());

			UrlBean urlCreate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(InventarioActivosControladorUrlEnum.URL0001.getValue());
			requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(), registroSubVehiculoSeguro.getCampos());
			JsfUtil.agregarMensajeInformativo(idioma.getString(Constantes.MSM_REGISTRO_INGRESADO));

		} catch (SystemException ex) {
			Logger.getLogger(InventarioActivosControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
		registroSubVehiculoSeguro.setCampos(new HashMap<String, Object>());
		cargarListaVehiculoseguro();
	}

	/**
	 * Metodo de edicion del formulario Vehiculoseguro
	 * 
	 * 
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */
	public void editarRegSubVehiculoseguro(RowEditEvent event) {
		Registro reg = (Registro) event.getObject();
		try {
			reg.getCampos().remove(InventarioActivosControladorEnum.FECHAINICIALM.getValue());
			reg.getCampos().remove(InventarioActivosControladorEnum.FECHAFINALM.getValue());
			reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
			reg.getCampos().remove(GeneralParameterEnum.PLACA.getName());
			reg.getCampos().remove(GeneralParameterEnum.SUCURSAL.getName());
			reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(), usuario);
			reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());

			if ((reg.getCampos().get(GeneralParameterEnum.FECHAFINAL.getName()) != null)
					&& (reg.getCampos().get(GeneralParameterEnum.FECHAINICIAL.getName()) != null)
					&& ((Date) reg.getCampos().get(GeneralParameterEnum.FECHAFINAL.getName()))
							.before((Date) reg.getCampos().get(GeneralParameterEnum.FECHAINICIAL.getName()))) {
				cargarListaVehiculoseguro();
				JsfUtil.agregarMensajeAlerta(idioma.getString(InventarioActivosControladorEnum.TB_TB49.getValue()));
				return;

			}

			UrlBean urlUpdate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(InventarioActivosControladorUrlEnum.URL0002.getValue());

			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), reg.getCampos(), reg.getLlave());

			JsfUtil.agregarMensajeInformativo(idioma.getString(Constantes.MSM_REGISTRO_MODIFICADO));
		} catch (SystemException ex) {
			Logger.getLogger(InventarioActivosControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
		cargarListaVehiculoseguro();
	}

	/**
	 * Metodo de eliminacion del formulario Vehiculoseguro
	 * 
	 * 
	 * @param reg
	 *            registro seleccionado en el subformulario
	 */
	public void eliminarRegSubVehiculoseguro(Registro reg) {
		try {
			UrlBean urlDelete = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(InventarioActivosControladorUrlEnum.URL0003.getValue());
			requestManager.delete(urlDelete.getUrl(), reg.getLlave());

			JsfUtil.agregarMensajeInformativo(idioma.getString(Constantes.MSM_REGISTRO_ELIMINADO));
		} catch (SystemException ex) {
			Logger.getLogger(InventarioActivosControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
		cargarListaVehiculoseguro();
	}

	/**
	 * Metodo ejecutado cuando se cancela la edicion del registro seleccionado para
	 * el subformulario Vehiculoseguro
	 *
	 */
	public void cancelarEdicionVehiculoseguro() {
		cargarListaVehiculoseguro();
		cargarListaSecundario141();
		cargarListaSubvehiculopartes();
		cargarListaImagenvehiculos();
		cargarListaSecundario160();
		cargarListaFotosaccidentes();
	}

	/**
	 * Metodo de insercion del formulario Subvehiculopartes
	 * 
	 */
	public void agregarRegistroSubSubvehiculopartes() {
		try {
			registroSubSubVehiculoPartes.getCampos().remove(InventarioActivosControladorEnum.NOMBRESTADO.getValue());
			registroSubSubVehiculoPartes.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
			registroSubSubVehiculoPartes.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
			registroSubSubVehiculoPartes.getCampos().put(GeneralParameterEnum.PLACA.getName(),
					registro.getCampos().get(GeneralParameterEnum.PLACA.getName()));
			registroSubSubVehiculoPartes.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(), usuario);
			registroSubSubVehiculoPartes.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());

			UrlBean urlCreate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(InventarioActivosControladorUrlEnum.URL0010.getValue());
			requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(), registroSubSubVehiculoPartes.getCampos());
			JsfUtil.agregarMensajeInformativo(idioma.getString(Constantes.MSM_REGISTRO_INGRESADO));
		} catch (SystemException ex) {
			Logger.getLogger(InventarioActivosControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
		registroSubSubVehiculoPartes.setCampos(new HashMap<String, Object>());
		cargarListaSubvehiculopartes();
	}

	/**
	 * Metodo de edicion del formulario Subvehiculopartes
	 * 
	 * 
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */
	public void editarRegSubSubvehiculopartes(RowEditEvent event) {
		Registro reg = (Registro) event.getObject();
		try {
			reg.getCampos().remove(InventarioActivosControladorEnum.NOMBRESTADO.getValue());
			reg.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
			reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
			reg.getCampos().remove(GeneralParameterEnum.PLACA.getName());
			reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(), usuario);
			reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());

			UrlBean urlUpdate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(InventarioActivosControladorUrlEnum.URL0011.getValue());
			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), reg.getCampos(), reg.getLlave());

			JsfUtil.agregarMensajeInformativo(idioma.getString(Constantes.MSM_REGISTRO_MODIFICADO));
		} catch (SystemException ex) {
			Logger.getLogger(InventarioActivosControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
		cargarListaSubvehiculopartes();
	}

	/**
	 * Metodo de eliminacion del formulario Subvehiculopartes
	 * 
	 * 
	 * @param reg
	 *            registro seleccionado en el subformulario
	 */
	public void eliminarRegSubSubvehiculopartes(Registro reg) {
		try {
			UrlBean urlDelete = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(InventarioActivosControladorUrlEnum.URL0012.getValue());
			requestManager.delete(urlDelete.getUrl(), reg.getLlave());

			JsfUtil.agregarMensajeInformativo(idioma.getString(Constantes.MSM_REGISTRO_ELIMINADO));
		} catch (SystemException ex) {
			Logger.getLogger(InventarioActivosControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
		cargarListaSubvehiculopartes();
	}

	/**
	 * Metodo ejecutado cuando se cancela la edicion del registro seleccionado para
	 * el subformulario Subvehiculopartes
	 *
	 */
	public void cancelarEdicionSubvehiculopartes() {
		cargarListaSubvehiculopartes();
		cargarListaImagenvehiculos();
		cargarListaSecundario160();
		cargarListaFotosaccidentes();
	}

	/**
	 * Metodo de insercion del formulario Imagenvehiculos @throws
	 * 
	 */
	public void agregarRegistroSubImagenvehiculos() {
		try {
			registroSubImagenVehiculos.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
			registroSubImagenVehiculos.getCampos().put(InventarioActivosControladorEnum.REGISTRO.getValue(),
					registro.getCampos().get(GeneralParameterEnum.PLACA.getName()));
			registroSubImagenVehiculos.getCampos().put(GeneralParameterEnum.VIGENCIA.getName(),
					SysmanFunciones.getParteFecha(new Date(), Calendar.YEAR));
			registroSubImagenVehiculos.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(), usuario);
			registroSubImagenVehiculos.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());

			UrlBean urlCreate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(InventarioActivosControladorUrlEnum.URL0013.getValue());
			requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(), registroSubImagenVehiculos.getCampos());

			JsfUtil.agregarMensajeInformativo(idioma.getString(Constantes.MSM_REGISTRO_INGRESADO));
			registroSubImagenVehiculos.setCampos(new HashMap<String, Object>());
			cargarListaImagenvehiculos();
		} catch (SystemException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
	}

	/**
	 * Metodo de edicion del formulario Imagenvehiculos
	 * 
	 * 
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */
	public void editarRegSubImagenvehiculos(RowEditEvent event) {
		/* Metodo no implementado */
	}

	/**
	 * Metodo de eliminacion del formulario Imagenvehiculos
	 * 
	 * 
	 * @param reg
	 *            registro seleccionado en el subformulario
	 */
	public void eliminarRegSubImagenvehiculos(Registro reg) {
		try {
			UrlBean urlDelete = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(InventarioActivosControladorUrlEnum.URL0014.getValue());
			reg.getLlave().put(InventarioActivosControladorEnum.KEY_ARCHIVO.getValue(),
					reg.getCampos().get(InventarioActivosControladorEnum.RUTA.getValue()));
			requestManager.delete(urlDelete.getUrl(), reg.getLlave());

			File aux = new File(SysmanFunciones
					.nvl(reg.getCampos().get(InventarioActivosControladorEnum.RUTA.getValue()), "").toString());
			if (aux.delete()) {
				JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2490"));
			} else {
				JsfUtil.agregarMensajeError(idioma.getString("TB_TB2491"));
			}
		} catch (SystemException ex) {
			Logger.getLogger(InventarioActivosControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
		cargarListaImagenvehiculos();
	}

	/**
	 * Metodo ejecutado cuando se cancela la edicion del registro seleccionado para
	 * el subformulario Imagenvehiculos
	 */
	public void cancelarEdicionImagenvehiculos() {
		cargarListaImagenvehiculos();
		cargarListaSecundario160();
		cargarListaFotosaccidentes();
	}

	/**
	 * Metodo de insercion del formulario Secundario160
	 * 
	 */
	public void agregarRegistroSubSecundario160() {
		try {
			registroSubSecundario160.getCampos().remove(InventarioActivosControladorEnum.FECHA_ACCIDENTEM.getValue());
			registroSubSecundario160.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
			registroSubSecundario160.getCampos().put(GeneralParameterEnum.PLACA.getName(),
					registro.getCampos().get(GeneralParameterEnum.PLACA.getName()));
			registroSubSecundario160.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(), usuario);
			registroSubSecundario160.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());

			UrlBean urlCreate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(InventarioActivosControladorUrlEnum.URL0015.getValue());
			requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(), registroSubSecundario160.getCampos());

			JsfUtil.agregarMensajeInformativo(idioma.getString(Constantes.MSM_REGISTRO_INGRESADO));
		} catch (SystemException ex) {
			Logger.getLogger(InventarioparqueautomotorControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
		registroSubSecundario160.setCampos(new HashMap<String, Object>());
		cargarListaSecundario160();
	}

	/**
	 * Metodo de edicion del formulario Secundario160
	 * 
	 * 
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */
	public void editarRegSubSecundario160(RowEditEvent event) {
		Registro reg = (Registro) event.getObject();
		try {
			reg.getCampos().remove(InventarioActivosControladorEnum.FECHA_ACCIDENTEM.getValue());
			reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(), usuario);
			reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());

			UrlBean urlUpdate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(InventarioActivosControladorUrlEnum.URL0016.getValue());

			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), reg.getCampos(), reg.getLlave());
			JsfUtil.agregarMensajeInformativo(idioma.getString(Constantes.MSM_REGISTRO_MODIFICADO));
		} catch (SystemException ex) {
			Logger.getLogger(InventarioparqueautomotorControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
		cargarListaSecundario160();
	}

	/**
	 * Metodo de eliminacion del formulario Secundario160
	 * 
	 * 
	 * @param reg
	 *            registro seleccionado en el subformulario
	 */
	public void eliminarRegSubSecundario160(Registro reg) {
		try {
			UrlBean urlDelete = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(InventarioActivosControladorUrlEnum.URL0017.getValue());
			requestManager.delete(urlDelete.getUrl(), reg.getLlave());

			JsfUtil.agregarMensajeInformativo(idioma.getString(Constantes.MSM_REGISTRO_ELIMINADO));
		} catch (SystemException ex) {
			Logger.getLogger(InventarioparqueautomotorControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
		cargarListaSecundario160();
	}

	/**
	 * Metodo ejecutado cuando se cancela la edicion del registro seleccionado para
	 * el subformulario Secundario160
	 *
	 */
	public void cancelarEdicionSecundario160() {
		cargarListaSecundario160();
		cargarListaFotosaccidentes();
	}

	/**
	 * Metodo de insercion del formulario Fotosaccidentes
	 * 
	 */
	public void agregarRegistroSubFotosaccidentes() {
		try {
			registroSubfotosaccidentes.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
			registroSubfotosaccidentes.getCampos().put(InventarioActivosControladorEnum.REGISTRO.getValue(),
					registro.getCampos().get(GeneralParameterEnum.PLACA.getName()));
			registroSubfotosaccidentes.getCampos().put(GeneralParameterEnum.VIGENCIA.getName(),
					SysmanFunciones.getParteFecha(new Date(), Calendar.YEAR));
			registroSubfotosaccidentes.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(), usuario);
			registroSubfotosaccidentes.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());

			UrlBean urlCreate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(InventarioActivosControladorUrlEnum.URL0018.getValue());
			requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(), registroSubfotosaccidentes.getCampos());
			registroSubfotosaccidentes.setCampos(new HashMap<String, Object>());
			cargarListaFotosaccidentes();
		} catch (SystemException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
	}

	/**
	 * Metodo de edicion del formulario Fotosaccidentes
	 * 
	 * 
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */
	public void editarRegSubFotosaccidentes(RowEditEvent event) {
		/* Metodo no implementado */
	}

	/**
	 * Metodo de eliminacion del formulario Fotosaccidentes
	 * 
	 * 
	 * @param reg
	 *            registro seleccionado en el subformulario
	 */
	public void eliminarRegSubFotosaccidentes(Registro reg) {
		try {

			UrlBean urlDelete = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(InventarioActivosControladorUrlEnum.URL0019.getValue());

			reg.getLlave().put(InventarioActivosControladorEnum.KEY_ARCHIVO.getValue(),
					reg.getCampos().get(InventarioActivosControladorEnum.RUTA.getValue()));

			requestManager.delete(urlDelete.getUrl(), reg.getLlave());

			File aux = new File(SysmanFunciones
					.nvl(reg.getCampos().get(InventarioActivosControladorEnum.RUTA.getValue()), "").toString());
			if (aux.delete()) {
				JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2490"));
			} else {
				JsfUtil.agregarMensajeError(idioma.getString("TB_TB2491"));
			}
		} catch (SystemException ex) {
			Logger.getLogger(InventarioActivosControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
		cargarListaFotosaccidentes();
	}

	/**
	 * Metodo ejecutado cuando se cancela la edicion del registro seleccionado para
	 * el subformulario Fotosaccidentes
	 *
	 */
	public void cancelarEdicionFotosaccidentes() {
		cargarListaFotosaccidentes();
	}

	// </METODOS_SUBFORM>
	// <METODOS_ADICIONALES>
	// </METODOS_ADICIONALES>
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
	 * Metodo ejecutado en el momento despues de cargar el registro
	 * 
	 */
	@Override
	public void cargarRegistro() {
		// <CODIGO_DESARROLLADO>
		precargarRegistro();
		if (accion.equals(ACCION_MODIFICAR)) {
			cargarListaDepartamento();
			cargarListaSerieElemento();
			cargarListaMunicipio();

		}
		// </CODIGO_DESARROLLADO>

	}

	/**
	 * Metodo ejecutado antes de realizar la insercion del registro
	 */
	@Override
	public boolean insertarAntes() {

		registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
		registro.getCampos().put(InventarioActivosControladorEnum.ES_AUTOMOTOR.getValue(), 0);

		return actualizarAntes();
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion del registro
	 */
	@Override
	public boolean insertarDespues() {
		// try {
		// ejbMantenimientoActivosCero.insertarParteFuncional(compania,
		// SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.PLACA.getName()),
		// "").toString(),
		// usuario);
		// } catch (SystemException e) {
		// logger.error(e.getMessage(), e);
		// JsfUtil.agregarMensajeError(e.getMessage());
		// }

		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado antes de realizar la insercion y actualizacion
	 */
	@Override
	public boolean actualizarAntes() {

		if (ACCION_MODIFICAR.equals(accion)) {
			registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
			registro.getCampos().remove(GeneralParameterEnum.PLACA.getName());
			registro.getCampos().remove(InventarioActivosControladorEnum.ES_AUTOMOTOR.getValue());

		} else {

			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(InventarioparqueautomotorControladorEnum.SERIE_ELEMENTO.getValue(),
					registro.getCampos().get(InventarioparqueautomotorControladorEnum.SERIE_ELEMENTO.getValue()));
			param.put(InventarioActivosControladorEnum.ES_AUTOMOTOR.getValue(), -1);

			try {
				Registro reg = RegistroConverter.toRegistro(requestManager.get(
						UrlServiceUtil.getInstance()
								.getUrlServiceByUrlByEnumID(
										InventarioparqueautomotorControladorUrlEnum.URL17961.getValue())
								.getUrl(),
						param));

				if (reg != null) {
					JsfUtil.agregarMensajeError(idioma.getString("TB_TB3861")
							.replace("s$serie$s", registro.getCampos()
									.get(InventarioparqueautomotorControladorEnum.SERIE_ELEMENTO.getValue()).toString())
							.replace("s$placa$s",
									reg.getCampos().get(GeneralParameterEnum.PLACA.getName()).toString()));
					return false;
				}
			} catch (SystemException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
			registro.getCampos().put(GeneralParameterEnum.ESTADO.getName(),
					SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.ESTADO.getName()), "A"));

		}

		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion y actualizacion
	 */
	@Override
	public boolean actualizarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado antes de realizar la eliminacion del registro
	 */
	@Override
	public boolean eliminarAntes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la eliminacion del registro
	 */
	@Override
	public boolean eliminarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	// <SET_GET_ATRIBUTOS>
	// </SET_GET_ATRIBUTOS>
	// <SET_GET_LISTAS>
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	/**
	 * Retorna la lista listaPais
	 * 
	 * @return listaPais
	 */
	public List<Registro> getListaPais() {
		return listaPais;
	}

	/**
	 * Asigna la lista listaPais
	 * 
	 * @param listaPais
	 *            Variable a asignar en listaPais
	 */
	public void setListaPais(List<Registro> listaPais) {
		this.listaPais = listaPais;
	}

	/**
	 * Retorna la lista listaDepartamento
	 * 
	 * @return listaDepartamento
	 */
	public List<Registro> getListaDepartamento() {
		return listaDepartamento;
	}

	/**
	 * Asigna la lista listaDepartamento
	 * 
	 * @param listaDepartamento
	 *            Variable a asignar en listaDepartamento
	 */
	public void setListaDepartamento(List<Registro> listaDepartamento) {
		this.listaDepartamento = listaDepartamento;
	}

	/**
	 * Retorna la lista listaMunicipio
	 * 
	 * @return listaMunicipio
	 */
	public List<Registro> getListaMunicipio() {
		return listaMunicipio;
	}

	/**
	 * Asigna la lista listaMunicipio
	 * 
	 * @param listaMunicipio
	 *            Variable a asignar en listaMunicipio
	 */
	public void setListaMunicipio(List<Registro> listaMunicipio) {
		this.listaMunicipio = listaMunicipio;
	}

	/**
	 * Retorna la lista listaTipo
	 * 
	 * @return listaTipo
	 */
	public List<Registro> getListaTipo() {
		return listaTipo;
	}

	/**
	 * Asigna la lista listaTipo
	 * 
	 * @param listaTipo
	 *            Variable a asignar en listaTipo
	 */
	public void setListaTipo(List<Registro> listaTipo) {
		this.listaTipo = listaTipo;
	}

	/**
	 * Retorna la lista listaMarca
	 * 
	 * @return listaMarca
	 */
	public List<Registro> getListaMarca() {
		return listaMarca;
	}

	/**
	 * Asigna la lista listaMarca
	 * 
	 * @param listaMarca
	 *            Variable a asignar en listaMarca
	 */
	public void setListaMarca(List<Registro> listaMarca) {
		this.listaMarca = listaMarca;
	}

	/**
	 * Retorna la lista listaLugarUbicacion
	 * 
	 * @return listaLugarUbicacion
	 */
	public List<Registro> getListaLugarUbicacion() {
		return listaLugarUbicacion;
	}

	/**
	 * Asigna la lista listaLugarUbicacion
	 * 
	 * @param listaLugarUbicacion
	 *            Variable a asignar en listaLugarUbicacion
	 */
	public void setListaLugarUbicacion(List<Registro> listaLugarUbicacion) {
		this.listaLugarUbicacion = listaLugarUbicacion;
	}

	/**
	 * Retorna la lista listaSerieElemento
	 * 
	 * @return listaSerieElemento
	 */
	public List<Registro> getListaSerieElemento() {
		return listaSerieElemento;
	}

	/**
	 * Asigna la lista listaSerieElemento
	 * 
	 * @param listaSerieElemento
	 *            Variable a asignar en listaSerieElemento
	 */
	public void setListaSerieElemento(List<Registro> listaSerieElemento) {
		this.listaSerieElemento = listaSerieElemento;
	}

	/**
	 * Retorna la lista listaPlacasAnteriores
	 * 
	 * @return listaPlacasAnteriores
	 */
	public List<Registro> getListaPlacasAnteriores() {
		return listaPlacasAnteriores;
	}

	/**
	 * Asigna la lista listaPlacasAnteriores
	 * 
	 * @param listaPlacasAnteriores
	 *            Variable a asignar en listaPlacasAnteriores
	 */
	public void setListaPlacasAnteriores(List<Registro> listaPlacasAnteriores) {
		this.listaPlacasAnteriores = listaPlacasAnteriores;
	}

	/**
	 * Retorna la lista listaCodProcedencia
	 * 
	 * @return listaCodProcedencia
	 */
	public List<Registro> getListaCodProcedencia() {
		return listaCodProcedencia;
	}

	/**
	 * Asigna la lista listaCodProcedencia
	 * 
	 * @param listaCodProcedencia
	 *            Variable a asignar en listaCodProcedencia
	 */
	public void setListaCodProcedencia(List<Registro> listaCodProcedencia) {
		this.listaCodProcedencia = listaCodProcedencia;
	}

	/**
	 * Retorna la lista listaParteFuncional
	 * 
	 * @return listaParteFuncional
	 */
	public List<Registro> getListaParteFuncional() {
		return listaParteFuncional;
	}

	/**
	 * Asigna la lista listaParteFuncional
	 * 
	 * @param listaParteFuncional
	 *            Variable a asignar en listaParteFuncional
	 */
	public void setListaParteFuncional(List<Registro> listaParteFuncional) {
		this.listaParteFuncional = listaParteFuncional;
	}

	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	/**
	 * Retorna la lista listaNIT
	 * 
	 * @return listaNIT
	 */
	public RegistroDataModelImpl getListaNIT() {
		return listaNIT;
	}

	/**
	 * Asigna la lista listaNIT
	 * 
	 * @param listaNIT
	 *            Variable a asignar en listaNIT
	 */
	public void setListaNIT(RegistroDataModelImpl listaNIT) {
		this.listaNIT = listaNIT;
	}

	/**
	 * Retorna la lista listaNIT
	 * 
	 * @return listaNIT
	 */
	public RegistroDataModelImpl getListaNITE() {
		return listaNITE;
	}

	/**
	 * Asigna la lista listaNIT
	 * 
	 * @param listaNIT
	 *            Variable a asignar en listaNIT
	 */
	public void setListaNITE(RegistroDataModelImpl listaNITE) {
		this.listaNITE = listaNITE;
	}

	/**
	 * Retorna la variable auxiliar
	 * 
	 * @return auxiliar
	 */
	public String getAuxiliar() {
		return auxiliar;
	}

	/**
	 * Asigna la variable auxiliar
	 * 
	 * @param auxiliar
	 *            Variable a asignar en auxiliar
	 */
	public void setAuxiliar(String auxiliar) {
		this.auxiliar = auxiliar;
	}

	/**
	 * Retorna la lista listaAseguradora
	 * 
	 * @return listaAseguradora
	 */
	public RegistroDataModelImpl getListaAseguradora() {
		return listaAseguradora;
	}

	/**
	 * Asigna la lista listaAseguradora
	 * 
	 * @param listaAseguradora
	 *            Variable a asignar en listaAseguradora
	 */
	public void setListaAseguradora(RegistroDataModelImpl listaAseguradora) {
		this.listaAseguradora = listaAseguradora;
	}

	/**
	 * Retorna la lista listaAseguradora
	 * 
	 * @return listaAseguradora
	 */
	public RegistroDataModelImpl getListaAseguradoraE() {
		return listaAseguradoraE;
	}

	/**
	 * Asigna la lista listaAseguradora
	 * 
	 * @param listaAseguradora
	 *            Variable a asignar en listaAseguradora
	 */
	public void setListaAseguradoraE(RegistroDataModelImpl listaAseguradoraE) {
		this.listaAseguradoraE = listaAseguradoraE;
	}

	/**
	 * Retorna la lista listaCodigoElemento
	 * 
	 * @return listaCodigoElemento
	 */
	public RegistroDataModelImpl getListaCodigoElemento() {
		return listaCodigoElemento;
	}

	/**
	 * Asigna la lista listaCodigoElemento
	 * 
	 * @param listaCodigoElemento
	 *            Variable a asignar en listaCodigoElemento
	 */
	public void setListaCodigoElemento(RegistroDataModelImpl listaCodigoElemento) {
		this.listaCodigoElemento = listaCodigoElemento;
	}

	/**
	 * Retorna la lista listaEntidadcomodataria
	 * 
	 * @return listaEntidadcomodataria
	 */
	public RegistroDataModelImpl getListaEntidadcomodataria() {
		return listaEntidadcomodataria;
	}

	/**
	 * Asigna la lista listaEntidadcomodataria
	 * 
	 * @param listaEntidadcomodataria
	 *            Variable a asignar en listaEntidadcomodataria
	 */
	public void setListaEntidadcomodataria(RegistroDataModelImpl listaEntidadcomodataria) {
		this.listaEntidadcomodataria = listaEntidadcomodataria;
	}

	// </SET_GET_LISTAS_COMBO_GRANDE>
	// <SET_GET_LISTAS_SUBFORM>
	/**
	 * Retorna la lista listaVehiculoseguro
	 * 
	 * @return listaVehiculoseguro
	 */
	public List<Registro> getListaVehiculoseguro() {
		return listaVehiculoseguro;
	}

	/**
	 * Asigna la lista listaVehiculoseguro
	 * 
	 * @param listaVehiculoseguro
	 *            Variable a asignar en listaVehiculoseguro
	 */
	public void setListaVehiculoseguro(List<Registro> listaVehiculoseguro) {
		this.listaVehiculoseguro = listaVehiculoseguro;
	}

	/**
	 * Retorna la lista listaSubvehiculopartes
	 * 
	 * @return listaSubvehiculopartes
	 */
	public List<Registro> getListaSubvehiculopartes() {
		return listaSubvehiculopartes;
	}

	/**
	 * Asigna la lista listaSubvehiculopartes
	 * 
	 * @param listaSubvehiculopartes
	 *            Variable a asignar en listaSubvehiculopartes
	 */
	public void setListaSubvehiculopartes(List<Registro> listaSubvehiculopartes) {
		this.listaSubvehiculopartes = listaSubvehiculopartes;
	}

	/**
	 * Retorna la lista listaImagenvehiculos
	 * 
	 * @return listaImagenvehiculos
	 */
	public List<Registro> getListaImagenvehiculos() {
		return listaImagenvehiculos;
	}

	/**
	 * Asigna la lista listaImagenvehiculos
	 * 
	 * @param listaImagenvehiculos
	 *            Variable a asignar en listaImagenvehiculos
	 */
	public void setListaImagenvehiculos(List<Registro> listaImagenvehiculos) {
		this.listaImagenvehiculos = listaImagenvehiculos;
	}

	/**
	 * Retorna la lista listaSecundario160
	 * 
	 * @return listaSecundario160
	 */
	public List<Registro> getListaSecundario160() {
		return listaSecundario160;
	}

	/**
	 * Asigna la lista listaSecundario160
	 * 
	 * @param listaSecundario160
	 *            Variable a asignar en listaSecundario160
	 */
	public void setListaSecundario160(List<Registro> listaSecundario160) {
		this.listaSecundario160 = listaSecundario160;
	}

	/**
	 * Retorna la lista listaFotosaccidentes
	 * 
	 * @return listaFotosaccidentes
	 */
	public List<Registro> getListaFotosaccidentes() {
		return listaFotosaccidentes;
	}

	/**
	 * Asigna la lista listaFotosaccidentes
	 * 
	 * @param listaFotosaccidentes
	 *            Variable a asignar en listaFotosaccidentes
	 */
	public void setListaFotosaccidentes(List<Registro> listaFotosaccidentes) {
		this.listaFotosaccidentes = listaFotosaccidentes;
	}

	// </SET_GET_LISTAS_SUBFORM>
	// <SET_GET_PARAMETROS>
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
	 * @param opcion
	 *            Variable a asignar en opcion
	 */
	public void setOpcion(String opcion) {
		this.opcion = opcion;
	}

	// </SET_GET_PARAMETROS>
	// <SET_GET_ADICIONALES>
	/**
	 * Retorna el objeto registroSubVehiculoSeguro
	 * 
	 * @return registroSubVehiculoSeguro
	 */
	public Registro getRegistroSubVehiculoSeguro() {
		return registroSubVehiculoSeguro;
	}

	/**
	 * Asigna el objeto registroSubVehiculoSeguro
	 * 
	 * @param registroSubVehiculoSeguro
	 *            Variable a asignar en registroSubVehiculoSeguro
	 */
	public void setRegistroSubVehiculoSeguro(Registro registroSubVehiculoSeguro) {
		this.registroSubVehiculoSeguro = registroSubVehiculoSeguro;
	}

	/**
	 * Retorna el objeto registroSubSubVehiculoPartes
	 * 
	 * @return registroSubSubVehiculoPartes
	 */
	public Registro getRegistroSubSubVehiculoPartes() {
		return registroSubSubVehiculoPartes;
	}

	/**
	 * Asigna el objeto registroSubSubVehiculoPartes
	 * 
	 * @param registroSubSubVehiculoPartes
	 *            Variable a asignar en registroSubSubVehiculoPartes
	 */
	public void setRegistroSubSubVehiculoPartes(Registro registroSubSubVehiculoPartes) {
		this.registroSubSubVehiculoPartes = registroSubSubVehiculoPartes;
	}

	/**
	 * Retorna el objeto registroSubImagenVehiculos
	 * 
	 * @return registroSubImagenVehiculos
	 */
	public Registro getRegistroSubImagenVehiculos() {
		return registroSubImagenVehiculos;
	}

	/**
	 * Asigna el objeto registroSubImagenVehiculos
	 * 
	 * @param registroSubImagenVehiculos
	 *            Variable a asignar en registroSubImagenVehiculos
	 */
	public void setRegistroSubImagenVehiculos(Registro registroSubImagenVehiculos) {
		this.registroSubImagenVehiculos = registroSubImagenVehiculos;
	}

	/**
	 * Retorna el objeto registroSubSecundario160
	 * 
	 * @return registroSubSecundario160
	 */
	public Registro getRegistroSubSecundario160() {
		return registroSubSecundario160;
	}

	/**
	 * Asigna el objeto registroSubSecundario160
	 * 
	 * @param registroSubSecundario160
	 *            Variable a asignar en registroSubSecundario160
	 */
	public void setRegistroSubSecundario160(Registro registroSubSecundario160) {
		this.registroSubSecundario160 = registroSubSecundario160;
	}

	/**
	 * Retorna el objeto registroSubfotosaccidentes
	 * 
	 * @return registroSubfotosaccidentes
	 */
	public Registro getRegistroSubfotosaccidentes() {
		return registroSubfotosaccidentes;
	}

	/**
	 * Asigna el objeto registroSubfotosaccidentes
	 * 
	 * @param registroSubfotosaccidentes
	 *            Variable a asignar en registroSubfotosaccidentes
	 */
	public void setRegistroSubfotosaccidentes(Registro registroSubfotosaccidentes) {
		this.registroSubfotosaccidentes = registroSubfotosaccidentes;
	}

	public EjbSysmanUtilRemote getEjbSysmanUtil() {
		return ejbSysmanUtil;
	}

	public void setEjbSysmanUtil(EjbSysmanUtilRemote ejbSysmanUtil) {
		this.ejbSysmanUtil = ejbSysmanUtil;
	}

	public EjbMantenimientoActivosCeroRemote getEjbMantenimientoActivosCero() {
		return ejbMantenimientoActivosCero;
	}

	public void setEjbMantenimientoActivosCero(EjbMantenimientoActivosCeroRemote ejbMantenimientoActivosCero) {
		this.ejbMantenimientoActivosCero = ejbMantenimientoActivosCero;
	}

	public String getCompania() {
		return compania;
	}

	public String getModulo() {
		return modulo;
	}

	public String getUsuario() {
		return usuario;
	}

	public String getParametroVehiculo() {
		return parametroVehiculo;
	}

	public void setParametroVehiculo(String parametroVehiculo) {
		this.parametroVehiculo = parametroVehiculo;
	}

	public String getParametroAccidentes() {
		return parametroAccidentes;
	}

	public void setParametroAccidentes(String parametroAccidentes) {
		this.parametroAccidentes = parametroAccidentes;
	}

	public String getRegistroauxSucursalAseguradora() {
		return registroauxSucursalAseguradora;
	}

	public void setRegistroauxSucursalAseguradora(String registroauxSucursalAseguradora) {
		this.registroauxSucursalAseguradora = registroauxSucursalAseguradora;
	}

	public String getCodigoElemento() {
		return codigoElemento;
	}

	public void setCodigoElemento(String codigoElemento) {
		this.codigoElemento = codigoElemento;
	}

	public String getRegistroauxNombreSeguro() {
		return registroauxNombreSeguro;
	}

	public void setRegistroauxNombreSeguro(String registroauxNombreSeguro) {
		this.registroauxNombreSeguro = registroauxNombreSeguro;
	}

	public String getRegistroauxSucursalSeguro() {
		return registroauxSucursalSeguro;
	}

	public void setRegistroauxSucursalSeguro(String registroauxSucursalSeguro) {
		this.registroauxSucursalSeguro = registroauxSucursalSeguro;
	}

	public String getNombreConductorAux() {
		return nombreConductorAux;
	}

	public void setNombreConductorAux(String nombreConductorAux) {
		this.nombreConductorAux = nombreConductorAux;
	}

	public String getNombreComodatariaAux() {
		return nombreComodatariaAux;
	}

	public void setNombreComodatariaAux(String nombreComodatariaAux) {
		this.nombreComodatariaAux = nombreComodatariaAux;
	}

	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}

	public List<Registro> getListaSecundario141() {
		return listaSecundario141;
	}

	public void setListaSecundario141(List<Registro> listaSecundario141) {
		this.listaSecundario141 = listaSecundario141;
	}

	public String getRegistroauxNombreGases() {
		return registroauxNombreGases;
	}

	public void setRegistroauxNombreGases(String registroauxNombreGases) {
		this.registroauxNombreGases = registroauxNombreGases;
	}

	public Registro getRegistroSubSecundario141() {
		return registroSubSecundario141;
	}

	public void setRegistroSubSecundario141(Registro registroSubSecundario141) {
		this.registroSubSecundario141 = registroSubSecundario141;
	}

	public String getRegistroauxSucursalGases() {
		return registroauxSucursalGases;
	}

	public void setRegistroauxSucursalGases(String registroauxSucursalGases) {
		this.registroauxSucursalGases = registroauxSucursalGases;
	}

	public RegistroDataModelImpl getListaNumeroPoliza() {
		return listaNumeroPoliza;
	}

	public void setListaNumeroPoliza(RegistroDataModelImpl listaNumeroPoliza) {
		this.listaNumeroPoliza = listaNumeroPoliza;
	}

	public RegistroDataModelImpl getListaNumeroPolizaE() {
		return listaNumeroPolizaE;
	}

	public void setListaNumeroPolizaE(RegistroDataModelImpl listaNumeroPolizaE) {
		this.listaNumeroPolizaE = listaNumeroPolizaE;
	}

	// </SET_GET_ADICIONALES>
}
