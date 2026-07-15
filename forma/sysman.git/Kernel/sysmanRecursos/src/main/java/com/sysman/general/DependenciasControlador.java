package com.sysman.general;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.DependenciasControladorUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.math.BigInteger;
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

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author cmanrique modified by jrodriguezr
 *
 * -- Modificado por lcortes 03/04/2017 12:05. Ajustes Refactoring.
 * @version 1 25/04/2018 sdaza validar parametroEntrada retorn en caso
 * de no traer valor
 */
@ManagedBean
@ViewScoped
public class DependenciasControlador extends BeanBaseDatosAcmeImpl {

	private final String compania;
	private final String menu;
	private final String cCodigo;
	private final String cResponsable;
	private final String cNomResponsable;
	private final String cSucursal;
	private final String cCargo;
	private final String cClaseBodega;
	private boolean condicion = false;
	private Registro registroSub;
	private RegistroDataModelImpl listaCentroDeCosto;
	private List<Registro> listaClaseBodega;
	private RegistroDataModelImpl listaIdentificacion;
	private RegistroDataModelImpl listaIdentificacionE;
	private RegistroDataModelImpl listaSupervisor;
	private RegistroDataModelImpl listaSupervisorE;
	private String auxiliar;
	private String dependencia;
	private String modulo;
	private boolean cargaResponsable = true;
	private boolean retorn;
	private Map<String, Object> ridProyecto;
	private boolean valida = false;
	private boolean bloqueaCargo = true;
	/**
	 * Lista de registros de la tabla CGR_DEPENDENCIAS
	 */
	private List<Registro> listaDependenciaCgr;

	/**
	 * Esta constante almacena la opcion de menu de banco de proyectos
	 * MVENEGAS
	 */
	static final String OPCION520119 = "520119";

	/**
	 * Esta variable me permite: 1. mostrar u ocultar la opcion de
	 * insertar y eliminar en el formulario principal de responsable
	 * dependencia. 2. la misma variable pero negada me bloquea o
	 * desbloque los campos de edicion en el formulario principal. 3.
	 * me muestra u oculta la opcion de eliminart en el subformulario.
	 */
	private boolean mostrarAgreEli;
	/**
	 * Variable que almacena el valor del parametro = ALMACEN INTERCOMPANIAS
	 */
	private boolean manInterCompania;
	private boolean manCompaniaDestino;
	private String companiaDestino;
	// <DECLARAR_LISTAS_SUBFORM>
	/**
	 * Listado de registros para el subformulario "Responsables por
	 * Dependencia"
	 */
	private RegistroDataModelImpl listaSubresponsable;
	private RegistroDataModelImpl listacompaniaDestino;
	private RegistroDataModelImpl listamovimientoDestino;
	// </DECLARAR_LISTAS_SUBFORM>

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	private int longitudPredecesor;
	private int longitudDependencia;
	private boolean manPredecesor;
	private String codigoDependencia;
	private boolean ocultarSupervisor;

	@SuppressWarnings("unchecked")
	public DependenciasControlador() {
		super();
		compania = SessionUtil.getCompania();
		menu = SessionUtil.getMenuActual();
		modulo = SessionUtil.getModulo();
		cCodigo = GeneralParameterEnum.CODIGO.getName();
		cResponsable = GeneralParameterEnum.RESPONSABLE.getName();
		cNomResponsable = "NOMRESPONSABLE";
		cSucursal = GeneralParameterEnum.SUCURSAL.getName();
		cCargo = GeneralParameterEnum.CARGO.getName();
		cClaseBodega = "CLASE_BODEGA";

		if (menu.equals("21070112")) {
			mostrarAgreEli = false;
		}
		else {
			mostrarAgreEli = true;
		}

		try {
			numFormulario = GeneralCodigoFormaEnum.DEPENDENCIAS_CONTROLADOR
					.getCodigo();
			registro = new Registro(new HashMap<String, Object>());
			registroSub = new Registro(new HashMap<String, Object>());
			validarPermisos();
			Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

			if ("670105".equals(SessionUtil.getMenuActual())) {
				parametrosEntrada = null;
			}
			if (parametrosEntrada != null) {
				valida = true;
				retorn = SysmanFunciones.validarVariableVacio(
						extraerString(parametrosEntrada
								.get("retorno"))) ? false
										: (boolean) parametrosEntrada
										.get("retorno");
				ridProyecto = (Map<String, Object>) parametrosEntrada
						.get("ridProyecto");
			}
		}
		catch (SysmanException ex) {
			Logger.getLogger(DependenciasControlador.class.getName())
			.log(Level.SEVERE, null, ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	@PostConstruct
	public void inicializar() {
		/**
		 * En esta seccion se esta validando que si se ingresa desde
		 * banco de BP-ARCHIVO-DEPENDENCIA RESPONSABLE llame
		 * diferentes servicion para la CRUD del formulario
		 */
		if (OPCION520119.equals(SessionUtil.getMenuActual())) {
			tabla = "DEPENDENCIA";

			urlListado = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							"6200G");

			urlCreacion = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							"6200C");

			urlActualizacion = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							"62086");

			urlEliminacion = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							"6200D");
			urlLectura = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							"6200R");

			buscarLlave();
		}
		else {
			enumBase = GenericUrlEnum.DEPENDENCIA;
			buscarLlave();
		}

		asignarOrigenDatos();

	}

	@Override
	public void asignarOrigenDatos() {
		if (!OPCION520119.equals(SessionUtil.getMenuActual())) {
			buscarUrls();
		}

		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);
		parametrosListado.put(GeneralParameterEnum.ANO.getName(),
				SysmanFunciones.ano(new Date()));

	}

	@Override
	public void iniciarListas() {
		cargarListaIdentificacion();
		cargarListaIdentificacionE();
		cargarListaSupervisor(); 
		cargarListaSupervisorE();
		cargarListaCentroDeCosto();
		cargarListaClaseBodega();
		cargarListaDependenciaCgr();
		cargarListacompaniaDestino();
	}

	@Override
	public void iniciarListasSub() {
		cargarListaSubresponsable();
	}

	@Override
	public void iniciarListasSubNulo() {
		listaSubresponsable = null;
	}

	public void cargarListaSubresponsable() {

		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.DEPENDENCIA.getName(),
					registro.getCampos().get(cCodigo));

			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							DependenciasControladorUrlEnum.URL4255
							.getValue());
			listaSubresponsable = new RegistroDataModelImpl(
					urlBean.getUrl(),
					urlBean.getUrlConteo().getUrl(),
					param,
					CacheUtil.getLlaveServicio(
							urlConexionCache,
							GenericUrlEnum.DEPENDENCIA_RESPONSABLE
							.getTable()));

		}
		catch (SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		try {
			dependencia = "";

			dependencia = ejbSysmanUtil.consultarParametro(
					compania,
					"DEPENDENCIA PARA RECEPCION DE PROYECTOS",
					modulo,
					new Date(), true);
			if (dependencia != null) {
				if (dependencia.equals(registro.getCampos().get(cCodigo))) {
					condicion = true;
				}
				else {
					condicion = false;
				}
			}
		}
		catch (SystemException ex) {
			Logger.getLogger(DependenciasControlador.class.getName())
			.log(Level.SEVERE, null, ex);
		}
	}

	public void cargarListaCentroDeCosto() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						DependenciasControladorUrlEnum.URL6046
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), SysmanFunciones
				.ano(new Date()));

		listaCentroDeCosto = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, cCodigo);
	}

	public void cargarListaClaseBodega() {
		try {
			listaClaseBodega = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									DependenciasControladorUrlEnum.URL6679
									.getValue())
							.getUrl(), null));
		}
		catch (SystemException e) {
			Logger.getLogger(DependenciasControlador.class.getName())
			.log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}
	
	/**
	 * 
	 * Carga la lista listacompaniaDestino
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListacompaniaDestino(){
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						DependenciasControladorUrlEnum.URL59031
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listacompaniaDestino = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, cCodigo);
	}
	/**
	 * 
	 * Carga la lista listamovimientoDestino
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListamovimientoDestino(){
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						DependenciasControladorUrlEnum.URL139029
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), companiaDestino);
		param.put(GeneralParameterEnum.CLASE.getName(), "E");
		param.put(GeneralParameterEnum.TIPO.getName(), "C");

		listamovimientoDestino = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, cCodigo);
	}

	/**
	 * 
	 * Carga la lista listaDependenciaCgr
	 *
	 */
	public void cargarListaDependenciaCgr() {
		try {
			listaDependenciaCgr = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									DependenciasControladorUrlEnum.URL239
									.getValue())
							.getUrl(), null));
		}
		catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}
	}

	public void cargarListaIdentificacion() {
		UrlBean urlBean;
		if ("60108".equals(SessionUtil.getMenuActual())) {
			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							DependenciasControladorUrlEnum.URL8188
							.getValue());
		}
		else {
			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							DependenciasControladorUrlEnum.URL7319
							.getValue());
		}

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaIdentificacion = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, cResponsable);
	}

	public void cargarListaSupervisor(){

		UrlBean urlBean;
		if ("60108".equals(SessionUtil.getMenuActual())) {
			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							DependenciasControladorUrlEnum.URL8188
							.getValue());
		}
		else {
			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							DependenciasControladorUrlEnum.URL7319
							.getValue());
		}

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaSupervisor = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "NOMRESPONSABLE");
	}

	public void  cargarListaSupervisorE(){

		UrlBean urlBean;
		if ("60108".equals(SessionUtil.getMenuActual())) {
			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							DependenciasControladorUrlEnum.URL8188
							.getValue());
		}
		else {
			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							DependenciasControladorUrlEnum.URL7319
							.getValue());
		}

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaSupervisorE = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "NOMRESPONSABLE");
	}

	public void cargarListaIdentificacionE() {
		UrlBean urlBean;
		if ("60108".equals(SessionUtil.getMenuActual())) {
			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							DependenciasControladorUrlEnum.URL8188
							.getValue());
		}
		else {
			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							DependenciasControladorUrlEnum.URL7319
							.getValue());
		}

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaIdentificacionE = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, cResponsable);
	}

	public void agregarRegistroSubSubresponsable() {
		try {
			registroSub.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
					compania);
			registroSub.getCampos().put(
					GeneralParameterEnum.DEPENDENCIA.getName(),
					registro.getCampos().get(cCodigo));
			registroSub.getCampos().put(
					GeneralParameterEnum.CREATED_BY.getName(),
					SessionUtil.getUser().getCodigo());
			registroSub.getCampos().put(
					GeneralParameterEnum.DATE_CREATED.getName(),
					new Date());
			registroSub.getCampos().remove(cNomResponsable);
			registroSub.getCampos().remove("NOMBRESUPERVISOR");

			UrlBean urlCreate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							GenericUrlEnum.DEPENDENCIA_RESPONSABLE
							.getCreateKey());
			requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
					registroSub.getCampos());
			cargarListaSubresponsable();
			JsfUtil.agregarMensajeInformativo(
					idioma.getString("MSM_REGISTRO_INGRESADO"));
		}
		catch (SystemException ex) {
			Logger.getLogger(DependenciasControlador.class.getName())
			.log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
		finally {
			registroSub = new Registro(new HashMap<String, Object>());
		}
	}

	public void editarRegSubSubresponsable(RowEditEvent event) {
		Registro reg = (Registro) event.getObject();
		try {
			if (!SysmanFunciones.validarCampoVacio(reg.getCampos(),
					cResponsable)) {
				reg.getCampos().remove(cNomResponsable);
				reg.getCampos().remove("NOMBRESUPERVISOR");
				reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
						SessionUtil.getUser().getCodigo());
				reg.getCampos().put(
						GeneralParameterEnum.DATE_MODIFIED.getName(),
						new Date());
				if (registroSub.getCampos().get(cSucursal) != null) {
					reg.getCampos().put(cCargo,
							reg.getCampos().get(cCargo));
					reg.getCampos().put(cSucursal,
							registroSub.getCampos().get(cSucursal));
				}
				UrlBean urlUpdate = UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(
								GenericUrlEnum.DEPENDENCIA_RESPONSABLE
								.getUpdateKey());
				requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
						reg.getCampos(),
						reg.getLlave());
				JsfUtil.agregarMensajeInformativo(
						idioma.getString("MSM_REGISTRO_MODIFICADO"));
			}
		}
		catch (SystemException ex) {
			Logger.getLogger(DependenciasControlador.class.getName())
			.log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
		finally {
			cargarListaSubresponsable();
			registroSub.getCampos().put(cResponsable, "");
			registroSub.getCampos().put(cCargo, "");
		}
	}

	public void eliminarRegSubSubresponsable(Registro reg) {
		try {
			UrlBean urlDelete = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							GenericUrlEnum.DEPENDENCIA_RESPONSABLE
							.getDeleteKey());
			requestManager.delete(urlDelete.getUrl(), reg.getLlave());
			JsfUtil.agregarMensajeInformativo(
					idioma.getString("MSM_REGISTRO_ELIMINADO"));
			cargarListaSubresponsable();
		}
		catch (SystemException ex) {
			Logger.getLogger(DependenciasControlador.class.getName())
			.log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
		finally {
			cargarListaSubresponsable();
			registroSub.getCampos().put(cResponsable, "");
			registroSub.getCampos().put(cCargo, "");
		}
	}

	public void ejecutarrcCerrar() {
		if (valida) {
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("retorno", retorn);
			parametros.put("ridProy", ridProyecto);
			parametros.put("valida", valida);
			Direccionador direccionador = new Direccionador();
			direccionador.setParametros(parametros);
			direccionador.setNumForm(
					String.valueOf(GeneralCodigoFormaEnum.FRMPROYECTOS_CONTROLADOR
							.getCodigo()));
			SessionUtil.redireccionarForma(direccionador,
					SessionUtil.getModulo());
		}
		else {
			SessionUtil.redireccionarMenu();
		}
	}

	public void cancelarEdicionSubresponsable() {
		cargarListaSubresponsable();
		registro.getCampos().put(cResponsable, "");
		registro.getCampos().put(cNomResponsable, "");
		registro.getCampos().put(cCargo, "");
		registroSub.getCampos().put("RESPONSABLEALMACEN", "");
	}

	public void cambiarIdentificacionC(int rowNum) {
		// <CODIGO_DESARROLLADO>
		if (SysmanFunciones.validarCampoVacio(
				listaSubresponsable.getDatasource().get(rowNum)
				.getCampos(),
				cResponsable)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3081"));
			listaSubresponsable.getDatasource().get(rowNum).getCampos()
			.put(cResponsable, "");
			listaSubresponsable.getDatasource().get(rowNum).getCampos().put(
					cNomResponsable,
					"");
			listaSubresponsable.getDatasource().get(rowNum).getCampos()
			.put(cCargo, "");
			return;
		}
		listaSubresponsable.getDatasource().get(rowNum).getCampos().put(
				cResponsable,
				registroSub.getCampos().get(cResponsable));
		listaSubresponsable.getDatasource().get(rowNum).getCampos().put(
				cNomResponsable,
				registroSub.getCampos().get(cNomResponsable));
		listaSubresponsable.getDatasource().get(rowNum).getCampos().put(cCargo,
				registroSub.getCampos().get(cCargo));
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarSupervisorC(int rowNum){


		listaSubresponsable.getDatasource().get(rowNum).getCampos().put(
				"SUPERVISOR",
				registroSub.getCampos().get("SUPERVISOR"));

		listaSubresponsable.getDatasource().get(rowNum).getCampos().put(
				"SUCURSAL_SUPERVISOR",
				registroSub.getCampos().get("SUCURSAL_SUPERVISOR"));

		listaSubresponsable.getDatasource().get(rowNum).getCampos().put(
				"NOMBRESUPERVISOR",
				registroSub.getCampos().get("NOMBRESUPERVISOR"));
	}

	public void cambiarNombre() {
		// <CODIGO_DESARROLLADO>
		registro.getCampos()
		.put(GeneralParameterEnum.NOMBRE.getName(),
				SysmanFunciones.nvl(
						registro.getCampos()
						.get(GeneralParameterEnum.NOMBRE
								.getName()),
						"").toString()
				.toUpperCase());
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control ckPredecesor
	 * 
	 * 
	 */
	public void cambiarckPredecesor() {
		//<CODIGO_DESARROLLADO>
		calcularPredecesor();
		//</CODIGO_DESARROLLADO>
	}
	
	/**
	 * Metodo ejecutado al cambiar el control manInterCompania
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarmanInterCompania() {
		//<CODIGO_DESARROLLADO>
		boolean manCompanias = (boolean) registro.getCampos().get("MAN_COMPANIADES");
		if (manCompanias) {
			manCompaniaDestino = true;
			cargarListacompaniaDestino();
		}else {
			manCompaniaDestino = false;
			registro.getCampos().put("COMP_DESTINO", "");
			registro.getCampos().put("MOV_DESTINO", "");
		}
		//</CODIGO_DESARROLLADO>
	}

	public void seleccionarFilaIdentificacion(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSub.getCampos().put(cResponsable,
				registroAux.getCampos().get(cResponsable));
		registroSub.getCampos().put(cSucursal,
				registroAux.getCampos().get(cSucursal));
		registroSub.getCampos().put(cCargo,
				registroAux.getCampos().get(cCargo));
		registroSub.getCampos().put(cNomResponsable,
				registroAux.getCampos().get(cNomResponsable));
	}

	public void seleccionarFilaIdentificacionE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		if (SysmanFunciones.validarCampoVacio(registroAux.getCampos(),
				cResponsable)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3081"));
			registroSub.getCampos().put(cResponsable, "");
			registroSub.getCampos().put(cSucursal, "");
			registroSub.getCampos().put(cCargo, "");
			registroSub.getCampos().put(cNomResponsable, "");
			return;
		}
		auxiliar = registroAux.getCampos().get(cResponsable).toString();
		registroSub.getCampos().put(cResponsable, auxiliar);
		registroSub.getCampos().put(cSucursal,
				registroAux.getCampos().get(cSucursal));
		registroSub.getCampos().put(cCargo,
				registroAux.getCampos().get(cCargo));
		registroSub.getCampos().put(cNomResponsable,
				registroAux.getCampos().get(cNomResponsable));
	}


	public void seleccionarFilaSupervisor(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();

		registroSub.getCampos().put("NOMBRESUPERVISOR",
				registroAux.getCampos().get(cNomResponsable));
		registroSub.getCampos().put("SUCURSAL_SUPERVISOR",
				registroAux.getCampos().get(cSucursal));
		registroSub.getCampos().put("SUPERVISOR",
				registroAux.getCampos().get(cResponsable));
	}


	public void seleccionarFilaSupervisorE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  (String) registroAux.getCampos().get(cNomResponsable);

		registroSub.getCampos().put("NOMBRESUPERVISOR", auxiliar);
		registroSub.getCampos().put("SUCURSAL_SUPERVISOR",
				registroAux.getCampos().get(cSucursal));
		registroSub.getCampos().put("SUPERVISOR",
				registroAux.getCampos().get(cResponsable));
	}

	public void seleccionarFilaCentroDeCosto(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(GeneralParameterEnum.CENTRODECOSTO.getName(),
				registroAux.getCampos().get(cCodigo));
	}

	public void seleccionarFilaClaseBodega(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(cClaseBodega,
				registroAux.getCampos().get(cClaseBodega));
	}
	
	public void seleccionarFilacompaniaDestino(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("COMP_DESTINO", registroAux.getCampos().get("CODIGO"));
		companiaDestino = registroAux.getCampos().get("CODIGO").toString();
		cargarListamovimientoDestino();
	}

	public void seleccionarFilamovimientoDestino(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("MOV_DESTINO", registroAux.getCampos().get("CODIGO"));
	}

	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		if ("190124".equals(menu) || "30112".equals(menu)) {
			cargaResponsable = false;
		}		
		// </CODIGO_DESARROLLADO>
	}

	@Override
	public void cargarRegistro() {
		// <CODIGO_DESARROLLADO>
		precargarRegistro();
		manInterCompania = false;
		manCompaniaDestino = false;
        try {
		String validaSupervisor = SysmanFunciones.nvl(
				ejbSysmanUtil.consultarParametro(compania, "MANEJA SUPERVISOR EN DEPENDENCIA RESPONSABLE", modulo, new Date(), false).toString(),"NO").toString();
			
		 ocultarSupervisor = validaSupervisor.equals("SI")?true:false;
		 
		 bloqueaCargo = SysmanFunciones.nvl(
					ejbSysmanUtil.consultarParametro(compania, "PERMITE EDITAR CARGO EN DEPENDENCIA RESPONSABLE", modulo, new Date(), false).toString(),"NO").equals("SI")? false:true;
		 
		 manInterCompania = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania, "ALMACEN INTERCOMPANIAS", "-1", new Date(), true),"NO").toString().equals("SI")?true:false;
		 
		 boolean manCompanias = (boolean) registro.getCampos().get("MAN_COMPANIADES");
			if (manInterCompania && manCompanias) {
				 manCompaniaDestino= true;
			 }
		 			
		} catch (SystemException e) {
			e.printStackTrace();
		}
		// </CODIGO_DESARROLLADO>
	}

	private void calcularPredecesor() {
		// <CODIGO_DESARROLLADO>
		try {

			manPredecesor = (boolean) registro.getCampos()
					.get("MAN_PREDECESOR");
			longitudDependencia = registro.getCampos()
					.get(GeneralParameterEnum.CODIGO.getName())
					.toString().length();
			codigoDependencia = registro.getCampos()
					.get(GeneralParameterEnum.CODIGO.getName())
					.toString();

			longitudPredecesor = Integer.parseInt(SysmanFunciones
					.nvl(ejbSysmanUtil.consultarParametro(compania,
							"LONGITUD PREDECESOR DEPENDENCIAS ALMACEN",
							modulo,
							new Date(), false), "0")
					.toString());

			if (manPredecesor) {
				if (longitudDependencia >= longitudPredecesor) {

					registro.getCampos().put("PREDECESOR", codigoDependencia
							.substring(0, longitudPredecesor));

				}
				else {
					registro.getCampos().put("PREDECESOR", "");
					registro.getCampos().put("MAN_PREDECESOR", false);
					JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4343"));
				}
			} else {
				registro.getCampos().put("PREDECESOR", null);
				registro.getCampos().put("MAN_PREDECESOR", false);
			}
			registro.getCampos().remove("CREATED_BY");
			registro.getCampos().remove("DATE_CREATED");
			UrlBean urlUpdate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							GenericUrlEnum.DEPENDENCIA
							.getUpdateKey());
			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
					registro.getCampos(), registro.getLlave());

			JsfUtil.agregarMensajeInformativo(
					idioma.getString("MSM_REGISTRO_MODIFICADO"));

		}
		catch (SystemException ex) {
			Logger.getLogger(DependenciasControlador.class.getName())
			.log(Level.SEVERE, null, ex);
		}
		// </CODIGO_DESARROLLADO>
	}

	private String extraerString(Object object) {
		return object != null ? object.toString() : null;
	}

	@Override
	public boolean insertarAntes() {
		// <CODIGO_DESARROLLADO>
		registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
				compania);
		// </CODIGO_DESARROLLADO>
		return true;
	}

	@Override
	public boolean insertarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	@Override
	public boolean actualizarAntes() {
		// <CODIGO_DESARROLLADO>
		if (OPCION520119.equals(SessionUtil.getMenuActual())) {
			registro.getCampos().remove("CLASE_BODEGA");
			registro.getCampos().remove("MAN_PREDECESOR");
			registro.getCampos().remove("PREDECESOR");
			registro.getCampos().remove("CODIGO_EQUIVALENTE");
		}
		// </CODIGO_DESARROLLADO>
		return true;
	}

	@Override
	public boolean actualizarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	@Override
	public boolean eliminarAntes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	@Override
	public boolean eliminarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	public void cerrarFormulario() {
		RequestContext.getCurrentInstance().closeDialog(null);
	}

	public boolean isCargaResponsable() {
		return cargaResponsable;
	}

	public void setCargaResponsable(boolean cargaResponsable) {
		this.cargaResponsable = cargaResponsable;
	}

	public RegistroDataModelImpl getListaCentroDeCosto() {
		return listaCentroDeCosto;
	}

	public void setListaCentroDeCosto(
			RegistroDataModelImpl listaCentroDeCosto) {
		this.listaCentroDeCosto = listaCentroDeCosto;
	}

	public List<Registro> getListaClaseBodega() {
		return listaClaseBodega;
	}

	public void setListaClaseBodega(List<Registro> listaClaseBodega) {
		this.listaClaseBodega = listaClaseBodega;
	}

	public RegistroDataModelImpl getListaSubresponsable() {
		return listaSubresponsable;
	}

	public void setListaSubresponsable(
			RegistroDataModelImpl listaSubresponsable) {
		this.listaSubresponsable = listaSubresponsable;
	}

	public RegistroDataModelImpl getListaIdentificacion() {
		return listaIdentificacion;
	}

	public void setListaIdentificacion(
			RegistroDataModelImpl listaIdentificacion) {
		this.listaIdentificacion = listaIdentificacion;
	}

	public RegistroDataModelImpl getListaIdentificacionE() {
		return listaIdentificacionE;
	}

	public void setListaIdentificacionE(
			RegistroDataModelImpl listaIdentificacionE) {
		this.listaIdentificacionE = listaIdentificacionE;
	}

	/**
	 * Retorna la lista listaDependenciaCgr
	 * 
	 * @return listaDependenciaCgr
	 */
	public List<Registro> getListaDependenciaCgr() {
		return listaDependenciaCgr;
	}

	/**
	 * Asigna la lista listaDependenciaCgr
	 * 
	 * @param listaDependenciaCgr
	 * Variable a asignar en listaDependenciaCgr
	 */
	public void setListaDependenciaCgr(List<Registro> listaDependenciaCgr) {
		this.listaDependenciaCgr = listaDependenciaCgr;
	}

	public String getAuxiliar() {
		return auxiliar;
	}

	public void setAuxiliar(String auxiliar) {
		this.auxiliar = auxiliar;
	}

	public boolean isCondicion() {
		return condicion;
	}

	public void setCondicion(boolean condicion) {
		this.condicion = condicion;
	}

	public String getDependencia() {
		return dependencia;
	}

	public void setDependencia(String dependencia) {
		this.dependencia = dependencia;
	}

	public Registro getRegistroSub() {
		return registroSub;
	}

	public void setRegistroSub(Registro registroSub) {
		this.registroSub = registroSub;
	}

	public String getModulo() {
		return modulo;
	}

	public void setModulo(String modulo) {
		this.modulo = modulo;
	}

	public boolean isMostrarAgreEli() {
		return mostrarAgreEli;
	}

	public void setMostrarAgreEli(boolean mostrarAgreEli) {
		this.mostrarAgreEli = mostrarAgreEli;
	}

	public RegistroDataModelImpl getListaSupervisor() {
		return listaSupervisor;
	}

	public void setListaSupervisor(RegistroDataModelImpl listaSupervisor) {
		this.listaSupervisor = listaSupervisor;
	}

	public RegistroDataModelImpl getListaSupervisorE() {
		return listaSupervisorE;
	}

	public void setListaSupervisorE(RegistroDataModelImpl listaSupervisorE) {
		this.listaSupervisorE = listaSupervisorE;
	}

	public boolean isOcultarSupervisor() {
		return ocultarSupervisor;
	}

	public void setOcultarSupervisor(boolean ocultarSupervisor) {
		this.ocultarSupervisor = ocultarSupervisor;
	}

	public boolean getBloqueaCargo() {
		return bloqueaCargo;
	}

	public void setBloqueaCargo(boolean bloqueaCargo) {
		this.bloqueaCargo = bloqueaCargo;
	}
	
	public boolean isManInterCompania() {
        return manInterCompania;
    }

    public void setManInterCompania(boolean manInterCompania) {
        this.manInterCompania = manInterCompania;
    }
    
    public RegistroDataModelImpl getListacompaniaDestino() {
        return listacompaniaDestino;
    }
    
    public void setListacompaniaDestino(RegistroDataModelImpl listacompaniaDestino) {
        this.listacompaniaDestino = listacompaniaDestino;
    }
    
    public RegistroDataModelImpl getListamovimientoDestino() {
        return listamovimientoDestino;
    }
    
    public void setListamovimientoDestino(RegistroDataModelImpl listamovimientoDestino) {
        this.listamovimientoDestino = listamovimientoDestino;
    }

    public boolean isManCompaniaDestino() {
        return manCompaniaDestino;
    }

    public void setManCompaniaDestino(boolean manCompaniaDestino) {
        this.manCompaniaDestino = manCompaniaDestino;
    }

}
