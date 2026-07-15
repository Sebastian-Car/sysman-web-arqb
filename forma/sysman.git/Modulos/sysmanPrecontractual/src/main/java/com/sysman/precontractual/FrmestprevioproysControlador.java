package com.sysman.precontractual;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
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
import com.sysman.precontractual.ejb.EjbPrecontractualUnoRemote;
import com.sysman.precontractual.enums.FrmestprevioproysControladorEnum;
import com.sysman.precontractual.enums.FrmestprevioproysControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
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

import org.primefaces.event.SelectEvent;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author acaceres
 * @version 1, 23/01/2016
 * 
 * @author eamaya
 * @version 2.0, 25/08/2017, Proceso de refactoring DSS, cambio numero
 * de formulario, cambio de redireccionamientos , correcciones
 * SonarLint , Manejo de EJBs y creacion de Textos en Bean
 * 
 */
@ManagedBean
@ViewScoped

public class FrmestprevioproysControlador extends BeanBaseDatosAcmeImpl {

	private final String compania;
	private String modulo;

	/** Constante a nivel de clase que aloja el valor FECHA_VOBO */
	private final String cFechaVobo;

	/** Constante a nivel de clase que aloja el valor ADJUDICACION */
	private final String cAdjudicacion;

	/** Constante a nivel de clase que aloja el valor CANTIDAD */
	private final String cCantidad;

	/** Constante a nivel de clase que aloja el valor CANT_PERSONAS */
	private final String cCantPersonas;

	/** Constante a nivel de clase que aloja el valor CARGO */
	private final String cCargo;

	/**
	 * Constante a nivel de clase que aloja el valor CARGO_INTERVENTOR
	 */
	private final String cCargoInterventor;

	/** Constante a nivel de clase que aloja el valor CODIGO */
	private final String cCodigo;

	/** Constante a nivel de clase que aloja el valor COD_ESTUDIO */
	private final String cCodEstudio;

	/** Constante a nivel de clase que aloja el valor codEstudio */
	private final String cCodEstudio1;

	/**
	 * Constante a nivel de clase que aloja el valor COD_T_CONTRATO
	 */
	private final String cCodTContrato;

	/** Constante a nivel de clase que aloja el valor COD_CONTRATO */
	private final String cCodContrato;

	/** Constante a nivel de clase que aloja el valor COSTOEST */
	private final String cCostoEst;

	/** Constante a nivel de clase que aloja el valor COSTOIVAEST */
	private final String cCostoIvaEst;

	/** Constante a nivel de clase que aloja el valor COSTOTOTALEST */
	private final String cCostoTotalEst;

	/** Constante a nivel de clase que aloja el valor CREATED_BY */
	private final String cCreatedBy;

	/** Constante a nivel de clase que aloja el valor CUANTIACONTR */
	private final String cCuantiaContr;

	/** Constante a nivel de clase que aloja el valor DEPENDENCIA */
	private final String cDependencia;

	/** Constante a nivel de clase que aloja el valor EMPLEODIRECTO */
	private final String cEmpleoDirecto;

	/**
	 * Constante a nivel de clase que aloja el valor EMPLEOINDIRECTO
	 */
	private final String cEmpleoIndirecto;

	/** Constante a nivel de clase que aloja el valor ESTADO */
	private final String cEstado;

	/** Constante a nivel de clase que aloja el valor FECHA */
	private final String cFecha;

	/**
	 * Constante a nivel de clase que aloja el valor FUND_JURIDICOS
	 */
	private final String cFundJuridicos;

	/** Constante a nivel de clase que aloja el valor IMPRESO */
	private final String cImpreso;

	/** Constante a nivel de clase que aloja el valor NOMBRE */
	private final String cNombre;

	/**
	 * Constante a nivel de clase que aloja el valor NOMBRE_ESTUDIO
	 */
	private final String cNombreEstudio;

	/**
	 * Constante a nivel de clase que aloja el valor PLAZO_EJECUCION
	 */
	private final String cPlazoEjecucion;

	/**
	 * Constante a nivel de clase que aloja el valor PYE_CONTRATISTA
	 */
	private final String cPyeContratista;

	/** Constante a nivel de clase que aloja el valor RESPONSABLE */
	private final String cResponsable;

	/** Constante a nivel de clase que aloja el valor SUBTOTAL */
	private final String cSubtotal;

	/** Constante a nivel de clase que aloja el valor SUCURSAL */
	private final String cSucursal;

	/** Constante a nivel de clase que aloja el valor SUPERVISION */
	private final String cSupervision;

	/** Constante a nivel de clase que aloja el valor TIPO_CONTRATO */
	private final String cTipoContrato;

	/** Constante a nivel de clase que aloja el valor TIPO_DIA */
	private final String cTipoDia;

	/** Constante a nivel de clase que aloja el valor VALORTOTAL */
	private final String cValorTotal;

	/** Constante a nivel de clase que aloja el valor VLRTOTAL */
	private final String cVlrTotal;

	/** Constante a nivel de clase que aloja el valor esCreador */
	private final String cEsCreador;

	/** Constante a nivel de clase que aloja el valor txtCodEstudio */
	private final String cTxtCodEstudio;

	/**
	 * Constante a nivel de clase que aloja el valor vigenciaPeriodo
	 */
	private final String cVigenciaPeriodo;

	/**
	 * Variable que almacena el valor del parámetro MUESTRA BOTON
	 * COPIAR DE EN ESTUDIOS PREVIOS / PROYECTO
	 */

	private String parametroCopiarDe;

	private Date auxFecha;

	private boolean verMensaje;

	private String mensajeDialogo;

	private boolean cargar;
	private List<Registro> listaCombEstudio;
	private List<Registro> listaEntidadProponente;
	private RegistroDataModelImpl listaCombTercero;
	private RegistroDataModelImpl listaCmbModeloEstudio;
	private RegistroDataModelImpl listaCmbTipoC;
	private List<Registro> listaCmbIntervalo;
	private RegistroDataModelImpl listaCmbAdjudicacion;
	private RegistroDataModelImpl listaDependencia;
	private RegistroDataModelImpl listaResponsable;
	private RegistroDataModelImpl listaInterventor;
	private List<Registro> listacertInexistencia;
	private String combTercero;
	private String combEstudio;
	private String listaModelos;
	private Boolean esCreador;
	private String usuario;

	/**
	 * Origen de control de el combo combEstudio.
	 */
	private String nombreDependecia;
	/**
	 * Almacenara el nombre de la dependencia en la pestaÃ±a datos
	 * generales.
	 */
	private String cmbIntervalo;
	private String dependenciaOrigen;
	private String codigoDependencia;
	private boolean bloqueaVoBo;
	private boolean copiarDeVisible;
	/**
	 * Muestra el boton copiar de en la pestana datos generales.
	 */
	private boolean actualizarFormulario;
	/**
	 * variable para controlar si el formulario se puede editar.
	 */
	private boolean eliminarFormulario;
	/**
	 * variable para controlar si el formulario se puede eliminar.
	 */
	private boolean insertarFormulario;
	/**
	 * variable para controlar si se puede insertar un nuevo registro.
	 */
	private String vigenciaPeriodo;
	/**
	 * Vigencia seleccionada en el formulario Periodo Estudio Proyecto
	 */
	private String nombreResponsable;
	/**
	 *
	 */
	private String filtroDep;
	private String txtTipCont;
	/**
	 * tipo de contrato seleccionado.
	 */
	private String cargoResponsable;
	/**
	 * Cargo de la persona responsable formulario principal.
	 */
	private String cargoInterventor;
	/**
	 * Cargo del interventor formulario principal.
	 */
	private String nombreSupervisor;
	/**
	 * Nombre del supervisor.
	 */
	private String sucursalSupervision;
	/**
	 * Sucursal del supervisor
	 */
	private String sucursalResponsable;
	/**
	 * Sucursal del responsable
	 */
	private String txtEstado;
	private String nombreTipoContrato;
	/**
	 * Variable para almacenar el nombre del tipo de contrato.
	 */
	private String nombreAdjudicacion;
	/**
	 * Variable para almacenar el nombre de la adjudicacion.
	 */
	private int anoVigencia;
	private String txtTipoCont;
	private boolean adjudicado;
	/**
	 * Cambia de estado el ckeck adjudicado.
	 */
	private boolean txtAvisoVisible;
	/**
	 * Muestra etiqueta de esetudio visible.
	 */
	private boolean cmbCombEstudioVisible;
	/**
	 * muestra el Combo commb_estudio.
	 */
	private boolean cmbCombTerceroVisible;

	/**
	 * Activa el botÃ³n cmb_localizacion.
	 */
	private boolean bloqueaTxtCodEstudio;

	/**
	 * Activa el campo de la hora de asiganacion del VOBO.
	 */
	private boolean bloqueaTxtCodEstudio11;

	/**
	 * Activa el campo txt_plazo.
	 */
	private boolean bloqueaTxtFechaInicial;
	/**
	 * Activa el campo fecha Inicial de la pestaÃ±a Datos basicos.
	 */
	private boolean bloqueaTxtFechaFinal;
	/**
	 * Activa el campo decha Inicial de la pestana Datos basicos.
	 */
	private boolean bloqueaCmbIntervalo;

	/**
	 * Activa el campo del costo total del estudio.
	 */
	private boolean bloqueaCmdCalcularProy;

	/**
	 * Activa el campo fecha_cne.
	 */
	private boolean bloqueaSobreEscribirFundamentos;
	/**
	 * Activa el boton sobre escribir fundamentos.
	 */
	private boolean bloqueaCopiarDe;

	/** Atributo que contiene el valor del check VoBo */
	private boolean vobo;

	/**
	 * Variable utilizada para controlar la visualizacion del botón
	 * Modalidad de contratacion
	 */
	private boolean verModalidad;

	/**
	 * Activa el boton copiar de.
	 */
	private StreamedContent archivoDescarga;

	/**
	 * Variable que Almacena el nombre de la plantilla seleccionada.
	 */
	private String nombreDocDescarga;

	/**
	 * Variable que almacena la fecha de la plantilla seleccionada.
	 */

	private String fechaPlantilla;

	/**
	 * variable que almacena el codigo de la plantilla seleccionada.
	 */
	private String codigoPlantilla;

	String codContratoM;

	private String tipoEstudio;
	private String modeloEstudio;
	private String nombreRubro;
	/**
	 * Esta variable determina si el campo "costo total estimado" de
	 * la pestana datos basico se puede editar o no
	 */
	private boolean editarCostoTotalEstimado;

	/**
	 * Variable que almacena el valor del parámetro PERMITE INGRESO
	 * DATOS DISPONIBILIDAD
	 */
	private String permiteIngresoDatosDisp;

	/**
	 * esta variable, almacena el valor del parametro MANEJA PLAN DE
	 * ACCION
	 */
	private String manejaPlanAccion;

	/**
	 * Constate que almacena el texto ridEstudio
	 */
	private String cridEstudio;
	/**
	 * variable que permite la visibilidad del combo Certificado Inexistencia
	 */
	private boolean visibleCert;
	
	/**
	 * variable que permite la visibilidad del botón Recursos Naturales
	 */
	private boolean manejaRecursosNat;

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtl;

	@EJB
	private EjbPrecontractualUnoRemote ejbPrecontractualUno;

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

	private Map<String,Object> parametroswf;
	private boolean verCerrar = true;
	
	@SuppressWarnings("unchecked")
	public FrmestprevioproysControlador() {
		super();

		compania = SessionUtil.getCompania();
		SessionUtil.setSessionVar("modulo", "19");
		modulo = SessionUtil.getModulo();
		cFechaVobo = "FECHA_VOBO";
		cAdjudicacion = "ADJUDICACION";
		cCantidad = "CANTIDAD";
		cCantPersonas = "CANT_PERSONAS";
		cCargo = "CARGO";
		cCargoInterventor = "CARGO_INTERVENTOR";
		cCodigo = "CODIGO";
		cCodEstudio = "COD_ESTUDIO";
		cCodEstudio1 = "codEstudio";
		cCodTContrato = "COD_T_CONTRATO";
		cCodContrato = "COD_CONTRATO";
		cCostoEst = "COSTOEST";
		cCostoIvaEst = "COSTOIVAEST";
		cCostoTotalEst = "COSTOTOTALEST";
		cCreatedBy = "CREATED_BY";
		cCuantiaContr = "CUANTIACONTR";
		cDependencia = "DEPENDENCIA";
		cEmpleoDirecto = "EMPLEODIRECTO";
		cEmpleoIndirecto = "EMPLEOINDIRECTO";
		cEstado = "ESTADO";
		cFecha = "FECHA";
		cFundJuridicos = "FUND_JURIDICOS";
		cImpreso = "IMPRESO";
		cNombre = "NOMBRE";
		cNombreEstudio = "NOMBRE_ESTUDIO";
		cPlazoEjecucion = "PLAZO_EJECUCION";
		cPyeContratista = "PYE_CONTRATISTA";
		cResponsable = "RESPONSABLE";
		cSubtotal = "SUBTOTAL";
		cSucursal = "SUCURSAL";
		cSupervision = "SUPERVISION";
		cTipoContrato = "TIPO_CONTRATO";
		cTipoDia = "TIPO_DIA";
		cValorTotal = "VALORTOTAL";
		cVlrTotal = "VLRTOTAL";
		cEsCreador = "esCreador";
		cTxtCodEstudio = "txtCodEstudio";
		cVigenciaPeriodo = "vigenciaPeriodo";
		editarCostoTotalEstimado = false;
		manejaPlanAccion = "";
		cridEstudio = "ridEstudio";

		try {
			numFormulario = GeneralCodigoFormaEnum.FRMESTPREVIOPROYS_CONTROLADOR
					.getCodigo();
			validarPermisos();
			bloqueaVoBo = false;
			insertarFormulario = true;
			eliminarFormulario = true;
			actualizarFormulario = true;
			registro = new Registro(new HashMap<String, Object>());
			dependenciaOrigen = SessionUtil.getUser().getDependencia()
					.getCodigo();
			usuario = SessionUtil.getUser().getCodigo();

			// SessionUtil.getMenuActual().equals(nuevo)
			Map<String, Object> parametros = SessionUtil.getFlash();
			if (parametros != null) {
				vigenciaPeriodo = SysmanFunciones
						.nvl(parametros.get(cVigenciaPeriodo), "0")
						.toString();
				rid = (Map<String, Object>) parametros.get("ridEstPrevios");
				
				parametroswf = (Map<String,Object>) parametros.get("parametroswf");
				if(parametroswf != null) {
					verCerrar = false;
				}
			}
		}
		catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());

			Logger.getLogger(FrmestprevioproysControlador.class.getName())
			.log(Level.SEVERE, null, ex);
			SessionUtil.redireccionarMenuPermisos();
		}
		finally {
			SessionUtil.cleanFlash();
		}
	}

	@PostConstruct
	public void inicializar() {
		if(parametroswf != null) {
			JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').parent().parent().css('height','770px');");
			JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').parent().parent().css('width','1830px');");
			JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').parent().parent().css('top','45px');");
			JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').parent().parent().css('left','125px');");
			
			JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').css('width','1800px');");
			JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').css('height','550px');");
		}
		enumBase = GenericUrlEnum.ES_ESTPREVIO;
		buscarLlave();
		asignarOrigenDatos();
	}

	@Override
	public void asignarOrigenDatos() {

		buscarUrls();
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);

		parametrosListado.put("TIPODIA", "I");

		parametrosListado.put(GeneralParameterEnum.VIGENCIA.getName(),
				vigenciaPeriodo);

	}

	@Override
	public void abrirFormulario() {

		verMensaje = false;
		mensajeDialogo = idioma.getString("TB_TB3495");

		try {
			manejaPlanAccion = SysmanFunciones
					.nvlStr(ejbSysmanUtl.consultarParametro(compania,
							"MANEJA PLAN DE ACCION",
							modulo,
							new Date(), false), "NO");
			
			// CC3345 mperez - Permite verificar si se debe mostrar el boton Recursos Naturales			         			
 			manejaRecursosNat = "SI".equals(SysmanFunciones
 					.nvl(ejbSysmanUtil.consultarParametro(compania, "ENTIDAD MANEJA CUENCAS Y RECURSOS",
 							SessionUtil.getModulo(), new Date(), true), "NO"));

			if("SI".equals(SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
					"VISIBLE COMBO CERTIFICADO INEXISTENCIA",
					modulo,new Date(),false), "NO")))
			{
				visibleCert = true;
			}
			else
			{
				visibleCert = false;
			}
		}
		catch (SystemException e1) {
			JsfUtil.agregarMensajeAlerta(
					"Falta configurar el parametro MANEJA PLAN DE ACCION");
		}

		registro.getCampos().put(cFecha, new Date());

		try {


			String parametro1 = ejbSysmanUtl.consultarParametro(compania,
					"CONSECUTIVO INICIAL ESTUDIOS PREVIOS", modulo,
					new Date(), false);

			String parametro2 = ejbSysmanUtl.consultarParametro(compania,
					"PERMITE DIGITAR VALORES DE ESTUDIOS PREVIOS", modulo,
					new Date(), false);


			validarParametros(parametro1,parametro2);


			cargarListaCmbModeloEstudio();

			parametroCopiarDe = ejbSysmanUtl.consultarParametro(compania,
					"MUESTRA BOTON COPIAR DE EN ESTUDIOS PREVIOS / PROYECTO",
					modulo, new Date(), false);

			permiteIngresoDatosDisp = ejbSysmanUtl.consultarParametro(compania,
					"PERMITE INGRESO DATOS DISPONIBILIDAD", modulo,
					new Date(), false);

			permiteIngresoDatosDisp = permiteIngresoDatosDisp == null ? "NO"
					: permiteIngresoDatosDisp;

		}
		catch (SystemException e) {
			Logger.getLogger(FrmestprevioproysControlador.class.getName())
			.log(Level.SEVERE, null, e);
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());

		}
	}

	private void validarParametros(String param1, String param2) {
		try {
			if (param1 == null) {
				JsfUtil.agregarMensajeError(
						idioma.getString("TB_TB2196"));
			}
			if (param2 == null) {
				editarCostoTotalEstimado = false;
				JsfUtil.agregarMensajeError(
						idioma.getString("TB_TB4416"));
			}else{
				editarCostoTotalEstimado = true;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}		
	}

	@Override
	public void iniciarListas() {
		cargarListaCombTercero();
		cargarListaCmbTipoC();
		cargarListaCmbAdjudicacion();
		cargarListaDependencia();
		cargarListaResponsable();
		cargarListaInterventor();
		cargarListaCombEstudio();
		cargarListaEntidadProponente();
		cargarListacertInexistencia();
	}

	@Override
	public void iniciarListasSub() {
		try {
			Map<String, Object> parametro = new TreeMap<>();

			parametro.put(GeneralParameterEnum.COMPANIA.getName(), compania);

			parametro.put(GeneralParameterEnum.DEPENDENCIA.getName(),
					registro.getCampos().get(cDependencia));

			Registro regNombreDependencia = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmestprevioproysControladorUrlEnum.URL109314
									.getValue())
							.getUrl(),
							parametro));

			nombreDependecia = regNombreDependencia != null
					? regNombreDependencia.getCampos().get(cNombre)
							.toString()
							: "";

							parametro.put(GeneralParameterEnum.RESPONSABLE.getName(),
									registro.getCampos().get(cResponsable));

							Registro recNombreResponsable = RegistroConverter
									.toRegistro(requestManager.get(
											UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													FrmestprevioproysControladorUrlEnum.URL26551
													.getValue())
											.getUrl(),
											parametro));

							nombreResponsable = recNombreResponsable != null
									? recNombreResponsable.getCampos()
											.get(cNombre).toString()
											: "";

											Map<String, Object> parameter = new TreeMap<>();

											parameter.put(GeneralParameterEnum.COMPANIA.getName(),
													compania);

											parameter.put(FrmestprevioproysControladorEnum.CEDULA
													.getValue(),
													registro.getCampos().get(cSupervision));

											Registro regSupervisor = RegistroConverter
													.toRegistro(requestManager.get(
															UrlServiceUtil.getInstance()
															.getUrlServiceByUrlByEnumID(
																	FrmestprevioproysControladorUrlEnum.URL103407
																	.getValue())
															.getUrl(),
															parameter));

											nombreSupervisor = regSupervisor != null
													? regSupervisor.getCampos().get(cNombre).toString()
															: "";

													sucursalSupervision = regSupervisor != null
															? regSupervisor.getCampos().get(cSucursal).toString()
																	: "";

		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	@Override
	public void iniciarListasSubNulo() {
		// METODO_NO_IMPLEMENTADO
	}

	public void cargarListaCmbModeloEstudio() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmestprevioproysControladorUrlEnum.URL22090
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(FrmestprevioproysControladorEnum.MODULO.getValue(),
				modulo);
		param.put(FrmestprevioproysControladorEnum.LISTAMODELOS.getValue(),
				listaModelos);

		listaCmbModeloEstudio = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, cCodigo);
	}

	public void cargarListaCombEstudio() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);

		param.put(FrmestprevioproysControladorEnum.TIPO.getValue(), "I");

		try {
			listaCombEstudio = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmestprevioproysControladorUrlEnum.URL22667
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaEntidadProponente() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		try {
			listaEntidadProponente = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmestprevioproysControladorUrlEnum.URL23510
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	public void cargarListaCombTercero() {

		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmestprevioproysControladorUrlEnum.URL24197
						.getValue());
		listaCombTercero = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, cNombre);
	}

	public void cargarListaCmbTipoC() {

		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmestprevioproysControladorUrlEnum.URL24611
						.getValue());
		listaCmbTipoC = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, cCodTContrato);
	}

	public void cargarListaCmbAdjudicacion() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmestprevioproysControladorUrlEnum.URL25113
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);
		param.put(FrmestprevioproysControladorEnum.TIPOCONTRATO.getValue(),
				registro.getCampos().get(cTipoContrato));

		listaCmbAdjudicacion = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, cCodContrato);

	}

	public void cargarListaDependencia() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmestprevioproysControladorUrlEnum.URL25897
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);

		listaDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, cCodigo);
	}

	public void cargarListaResponsable() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmestprevioproysControladorUrlEnum.URL26550
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);
		param.put(GeneralParameterEnum.DEPENDENCIA.getName(),
				registro.getCampos().get(cDependencia));

		listaResponsable = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, cResponsable);
	}

	public void cargarListaInterventor() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmestprevioproysControladorUrlEnum.URL28436
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);

		listaInterventor = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "CEDULA");

	}

	public void cargarListacertInexistencia(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);

		try
		{
			listacertInexistencia = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmestprevioproysControladorUrlEnum.URL1893001
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e)
		{
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cambiarCmbIntervalo() {
		// <CODIGO_DESARROLLADO>
		int rango = 0;

		Date plazoInicial = (Date) registro.getCampos().get("PLAZOINICIAL");
		Date plazoFinal = (Date) registro.getCampos().get("PLAZOFINAL");
		if (plazoInicial.compareTo(plazoFinal) > 0) {
			JsfUtil.agregarMensajeError(
					idioma.getString("TB_TB2189"));
		}

		try {
			int plazo1 = SysmanFunciones.calcularDiferenciaDias(plazoInicial,
					plazoFinal);

			if (plazo1 == 0) {
				JsfUtil.agregarMensajeError(
						idioma.getString("TB_TB2190"));

			}
			else {
				registro.getCampos().put(cPlazoEjecucion,
						plazo1 + " " + rango);
			}
			// </CODIGO_DESARROLLADO>
		}
		catch (ParseException ex) {
			Logger.getLogger(FrmestprevioproysControlador.class.getName())
			.log(Level.SEVERE, null, ex);
		}

	}

	public void oprimirbtnPlanAccion() {

		String codEstudio = String
				.valueOf(registro.getCampos().get(cCodEstudio));

		Map<String, Object> parametros = new TreeMap<>();
		parametros.put("rid", css);
		parametros.put("numeroEstudio", codEstudio);
		parametros.put("vigenciaPeriodo", vigenciaPeriodo);

		Direccionador direccionador = new Direccionador();

		direccionador.setNumForm(Integer.toString(1956));
		direccionador.setParametros(parametros);

		SessionUtil.redireccionarForma(direccionador, modulo);
	}

	public void oprimirbtnAcuerdos() {

		try {
			ejbPrecontractualUno.insertarAcuerdos(compania,
					Long.parseLong(registro.getCampos().get(cCodEstudio)
							.toString()),
					registro.getCampos().get(cTipoContrato).toString(),
					SessionUtil.getUser().getCodigo());

			String[] campos = { "numeroEstudio", "tipoContrato" };

			Object[] valores = { registro.getCampos().get(cCodEstudio)
					.toString(),
					registro.getCampos().get(cTipoContrato)
					.toString() };

			SessionUtil.cargarModalDatosFlash(
					String.valueOf(GeneralCodigoFormaEnum.FRM_ACUCOMERCIALESPREVIOPROY_CONTROLADOR
							.getCodigo()),
					SessionUtil.getModulo(), campos, valores);
		}
		catch (NumberFormatException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	public void oprimirCmdProyecto() {
		try {
			String codEstudio = registro.getCampos().get(cCodEstudio)
					.toString();

			String dependencia = registro.getCampos()
					.get(cDependencia).toString();

			String parametro = ejbSysmanUtl.consultarParametro(compania,
					"ASOCIA SOLICITUDES DE VIABILIDAD EN ESTUDIOS PREVIOS",
					modulo, new Date(), false);

			Direccionador direccionador = new Direccionador();

			Map<String, Object> parametros = new TreeMap<>();
			parametros.put(FrmestprevioproysControladorEnum.PR_FRM_ORIGEN
					.getValue(), numFormulario);

			if ("SI".equals(parametro)) {

				parametros.put("rid", css);

				parametros.put(cTxtCodEstudio, codEstudio);

				parametros.put(cVigenciaPeriodo, vigenciaPeriodo);

				parametros.put("voBo", vobo);

				parametros.put(cEsCreador, esCreador.toString());

				direccionador.setNumForm(Integer
						.toString(GeneralCodigoFormaEnum.FRMESTPROYPASTOS_CONTROLADOR
								.getCodigo()));

				direccionador.setParametros(parametros);

				SessionUtil.redireccionarForma(direccionador, modulo);

			}
			else {

				parametros.put("rid", css);

				parametros.put(cTxtCodEstudio, codEstudio);

				parametros.put(cVigenciaPeriodo, vigenciaPeriodo);

				parametros.put("voBo", vobo);

				parametros.put(cEsCreador, esCreador.toString());

				parametros.put("dependencia", dependencia);

				direccionador.setNumForm(Integer
						.toString(GeneralCodigoFormaEnum.FRMESTPROY_CONTROLADOR
								.getCodigo()));

				direccionador.setParametros(parametros);

				SessionUtil.redireccionarForma(direccionador, modulo);

			}

		}
		catch (SystemException ex) {
			Logger.getLogger(FrmestprevioproysControlador.class.getName())
			.log(Level.SEVERE, null, ex);
		}

	}

	public void oprimirCmdItemsEstudio() {
		String codEstudio = String
				.valueOf(registro.getCampos().get(cCodEstudio));

		Map<String, Object> parametros = new TreeMap<>();

		parametros.put(cTxtCodEstudio, codEstudio);

		parametros.put(cVigenciaPeriodo, vigenciaPeriodo);

		parametros.put("rid", css);

		parametros.put(cEsCreador, vobo);

		Direccionador direccionador = new Direccionador();

		direccionador.setNumForm(Integer
				.toString(GeneralCodigoFormaEnum.EPITEMSESTPROYS_CONTROLADOR
						.getCodigo()));

		direccionador.setParametros(parametros);

		SessionUtil.redireccionarForma(direccionador, modulo);

	}

	public void oprimirComPersonal() {

		Map<String, Object> parametros = new TreeMap<>();

		parametros.put("rid", css);

		parametros.put(cTxtCodEstudio, String
				.valueOf(registro.getCampos().get(cCodEstudio)));

		parametros.put(cVigenciaPeriodo, vigenciaPeriodo);

		parametros.put(cEsCreador, vobo);

		Direccionador direccionador = new Direccionador();

		direccionador.setParametros(parametros);

		direccionador.setNumForm(Integer
				.toString(GeneralCodigoFormaEnum.ESCOSTOPER_CONTROLADOR
						.getCodigo()));

		SessionUtil.redireccionarForma(direccionador, modulo);

	}

	public void oprimirCmbRubros() {
		/*
		 * Se deshabilita validaciï¿½n que impide ingresar al rubro si
		 * no tiene un proyecto relacionado. Segï¿½n TAR 3000000438
		 * este control ya no aplica.
		 */
		String codEstudio = String
				.valueOf(registro.getCampos().get(cCodEstudio));
		String tipoDia = String.valueOf(registro.getCampos().get(cTipoDia));

		Map<String, Object> parametros = new TreeMap<>();

		parametros.put(cTxtCodEstudio, codEstudio);

		parametros.put("txtTipoDia", tipoDia);

		parametros.put(cEsCreador, String.valueOf(vobo));

		parametros.put("costoTotal", registro.getCampos().get(cCostoTotalEst));

		Direccionador direccionador = new Direccionador();

		direccionador.setParametros(parametros);

		direccionador.setNumForm(Integer
				.toString(GeneralCodigoFormaEnum.FRMRUBROSPROY_CONTROLADOR
						.getCodigo()));

		SessionUtil.redireccionarForma(direccionador, modulo);
	}

	public void oprimirDisponibilidad() {

		Direccionador direccionador = new Direccionador();
		Map<String, Object> parametros = new TreeMap<>();

		String codEstudio = SysmanFunciones
				.nvl(registro.getCampos().get(cCodEstudio), "")
				.toString();

		if ("NO".equals(permiteIngresoDatosDisp)) {

			if ((codEstudio == null) || codEstudio.isEmpty()) {
				JsfUtil.agregarMensajeInformativo(
						idioma.getString("TB_TB2191"));
				return;
			}

			String tipoDia = String.valueOf(registro.getCampos().get(cTipoDia));

			parametros.put(cCodEstudio1, codEstudio);
			parametros.put("tipoDia", tipoDia);
			parametros.put(cridEstudio, css);
			parametros.put(cEsCreador, vobo);
			parametros.put(cVigenciaPeriodo, vigenciaPeriodo);
			direccionador.setParametros(parametros);

			direccionador.setNumForm(Integer
					.toString(GeneralCodigoFormaEnum.ESRUBROSESTDISPROYS_CONTROLADOR
							.getCodigo()));
		}

		else {

			parametros.put(cCodEstudio1, codEstudio);
			parametros.put(cridEstudio, css);
			parametros.put(cVigenciaPeriodo, vigenciaPeriodo);

			direccionador.setParametros(parametros);

			direccionador.setNumForm(Integer
					.toString(GeneralCodigoFormaEnum.RUBROS_DISPONIBILIDAD_MANUAL_CONTROLADOR
							.getCodigo()));

		}

		SessionUtil.redireccionarForma(direccionador, modulo);

	}

	public void oprimirCmbRiesgos() {
		String codEstudio = SysmanFunciones
				.nvl(registro.getCampos().get(cCodEstudio), "")
				.toString();

		if ((codEstudio == null) || codEstudio.isEmpty()) {
			JsfUtil.agregarMensajeInformativo(
					idioma.getString("TB_TB2191"));
			return;
		}

		Map<String, Object> parametros = new TreeMap<>();

		parametros.put(cCodEstudio1, codEstudio);

		parametros.put(cridEstudio, css);

		parametros.put(cVigenciaPeriodo, vigenciaPeriodo);

		parametros.put(cEsCreador, vobo);

		Direccionador direccionador = new Direccionador();

		direccionador.setParametros(parametros);

		direccionador.setNumForm(Integer
				.toString(GeneralCodigoFormaEnum.FRMRIESGOSPROYS_CONTROLADOR
						.getCodigo()));

		SessionUtil.redireccionarForma(direccionador, modulo);

	}

	public void oprimirCmbCumplimiento() {

		String codEstudio = String
				.valueOf(registro.getCampos().get(cCodEstudio));

		String voBo = String.valueOf(registro.getCampos().get("VOBO"));

		Map<String, Object> parametros = new TreeMap<>();

		parametros.put("txtTipCont", registro.getCampos().get(cTipoContrato));

		parametros.put(cCodEstudio1, codEstudio);

		parametros.put("voBo", voBo);

		parametros.put("rid", css);

		parametros.put(cEsCreador, esCreador.toString());

		parametros.put(cVigenciaPeriodo, vigenciaPeriodo);

		Direccionador direccionador = new Direccionador();

		direccionador.setParametros(parametros);

		direccionador.setNumForm(Integer
				.toString(GeneralCodigoFormaEnum.FRMCUMPLIMIENTOPROYS_CONTROLADOR
						.getCodigo()));

		SessionUtil.redireccionarForma(direccionador, modulo);

	}

	public void oprimirCmbSoporte() {

		String codEstudio = String
				.valueOf(registro.getCampos().get(cCodEstudio));

		Map<String, Object> parametros = new TreeMap<>();

		parametros.put("rid", css);

		parametros.put(cCodEstudio1, codEstudio);

		parametros.put(cEsCreador, vobo);

		parametros.put(cVigenciaPeriodo, vigenciaPeriodo);
		Direccionador direccionador = new Direccionador();

		direccionador.setNumForm(Integer
				.toString(GeneralCodigoFormaEnum.FRMSOPORTEPROYS_CONTROLADOR
						.getCodigo()));

		direccionador.setParametros(parametros);

		SessionUtil.redireccionarForma(direccionador, modulo);

	}

	public void oprimirCmbPolizas() {

		String codEstudio = String
				.valueOf(registro.getCampos().get(cCodEstudio));

		Map<String, Object> parametros = new TreeMap<>();

		parametros.put(cCodEstudio1, codEstudio);

		parametros.put("rid", css);

		parametros.put(cEsCreador, vobo);

		parametros.put(cVigenciaPeriodo, vigenciaPeriodo);

		Direccionador direccionador = new Direccionador();

		direccionador.setParametros(parametros);

		direccionador.setNumForm(Integer
				.toString(GeneralCodigoFormaEnum.FRMPOLIZASPROYS_CONTROLADOR
						.getCodigo()));

		SessionUtil.redireccionarForma(direccionador, modulo);

	}

	public void oprimirCmdLocaliza() {

		String codEstudio = String
				.valueOf(registro.getCampos().get(cCodEstudio));

		Map<String, Object> parametros = new TreeMap<>();

		parametros.put(cCodEstudio1, codEstudio);
		parametros.put("vigenciaPeriodo", vigenciaPeriodo);
		parametros.put("rid", css);
		parametros.put(cEsCreador, vobo);

		Direccionador direccionador = new Direccionador();

		direccionador.setParametros(parametros);

		direccionador.setNumForm(Integer
				.toString(GeneralCodigoFormaEnum.FRMSUBESTUDIOLOCALIZACIONPROYS_CONTROLADOR
						.getCodigo()));

		SessionUtil.redireccionarForma(direccionador, modulo);

	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton BtnDetalleActividades en
	 * la vista
	 *
	 *
	 */
	public void oprimirBtnDetalleActividades() {
		// <CODIGO_DESARROLLADO>

		String codEstudio = String
				.valueOf(registro.getCampos().get(cCodEstudio));

		Map<String, Object> parametros = new TreeMap<>();

		parametros.put(cCodEstudio1, codEstudio);

		parametros.put("rid", css);

		Direccionador direccionador = new Direccionador();

		direccionador.setParametros(parametros);

		direccionador.setNumForm(Integer
				.toString(1934));

		SessionUtil.redireccionarForma(direccionador, modulo);
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirCopiarDe() {
		combEstudio = null;
		cargarListaCombEstudio();

		if ("i".equals(accion)) {
			cmbCombEstudioVisible = true;
			if (adjudicado) {
				cmbCombTerceroVisible = true;
			}
			else {
				cmbCombTerceroVisible = false;
			}
		}
		else {
			JsfUtil.agregarMensajeInformativo(
					idioma.getString("TB_TB2192"));
		}

	}

	public void oprimirModalidadDeContratacion() {

		Map<String, Object> parametros = new TreeMap<>();

		parametros.put("rid", css);

		parametros.put(cEsCreador, esCreador.toString());

		parametros.put(cVigenciaPeriodo, vigenciaPeriodo);

		Direccionador direccionador = new Direccionador();

		direccionador.setParametros(parametros);

		direccionador.setNumForm(Integer
				.toString(GeneralCodigoFormaEnum.FRMMODALIDADPRECONTRATOS_CONTROLADOR
						.getCodigo()));

		SessionUtil.redireccionarForma(direccionador, modulo);

		cargarListaCmbTipoC();
	}

	public void oprimirCmdImprimir() {
		// <CODIGO_DESARROLLADO>

		List<Registro> rs;
		List<Registro> rsNrubro;
		try {

			Map<String, Object> param = new TreeMap<>();

			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

			param.put(GeneralParameterEnum.COD_ESTUDIO.getName(),
					registro.getCampos().get(cCodEstudio));

			rs = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmestprevioproysControladorUrlEnum.URL37363
									.getValue())
							.getUrl(), param));

			if (!rs.isEmpty()) {
				for (int i = 0; i < rs.size(); i++) {
					String rsRubroCDP = SysmanFunciones
							.nvl(rs.get(i).getCampos()
									.get("RUBRO_CDP"), "")
							.toString();

					String rsAnioEstPrev = SysmanFunciones
							.nvl(rs.get(i).getCampos()
									.get("ANIOESTPREV"), "")
							.toString();
					if (!(rsRubroCDP.isEmpty() && rsAnioEstPrev.isEmpty())) {

						Map<String, Object> parametro = new TreeMap<>();

						parametro.put(FrmestprevioproysControladorEnum.ID
								.getValue(), rsRubroCDP);

						parametro.put(GeneralParameterEnum.ANO.getName(),
								rsAnioEstPrev);

						rsNrubro = RegistroConverter
								.toListRegistro(requestManager.getList(
										UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												FrmestprevioproysControladorUrlEnum.URL38098
												.getValue())
										.getUrl(),
										parametro));

						asignarNombreRubro(rsNrubro);
					}

				}
			}

			Map<String, Object> parametros = new HashMap<>();

			parametros.put("codigoPlantilla", codigoPlantilla);

			parametros.put("fechaPlantilla", SysmanFunciones.formatearFecha(
					SysmanFunciones.convertirAFecha(fechaPlantilla)));

			parametros.put("nombreDocDescarga", nombreDocDescarga);

			HashMap<String, String> variablesConsultaW = new HashMap<>();

			variablesConsultaW.put("s$fechaSistemas$s",
					SysmanFunciones
					.convertirAFechaCadena(new Date()));
			variablesConsultaW.put("s$horaSistema$s",
					SysmanFunciones
					.convertirAHoraCadena(new Date()));
			variablesConsultaW.put("s$nitCompania$s",
					SessionUtil.getCompaniaIngreso().getNit());
			variablesConsultaW.put("s$nombreCompania$s",
					SessionUtil.getCompaniaIngreso().getNombre());
			variablesConsultaW.put("s$nombreRubroCdp$s",
					SysmanFunciones.nvlStr(nombreRubro, ""));
			variablesConsultaW.put("s$codEstudio$s",
					SysmanFunciones.nvl(registro.getCampos()
							.get(cCodEstudio), "").toString());

			variablesConsultaW.put("s$compania$s", compania);

			SessionUtil.setSessionVar("variablesConsultaWord",
					variablesConsultaW);

			Direccionador direccionador = new Direccionador();

			direccionador.setNumForm(Integer
					.toString(GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
							.getCodigo()));
			direccionador.setParametros(parametros);
			SessionUtil.redireccionarForma(direccionador, modulo);

		}
		catch (ParseException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton oprimirBtnProgramarPagos
	 * en la vista
	 *
	 */
	public void oprimirBtnProgramarPagos() {
		// <CODIGO_DESARROLLADO>
		String[] campos = { "valorTotalEstudio",
		"codigoEstudio" };
		String[] valores = { String
				.valueOf(registro.getCampos().get(cCostoTotalEst)),
				String.valueOf(registro.getCampos()
						.get(cCodEstudio)) };
		SessionUtil.cargarModalDatosFlash(
				String.valueOf(GeneralCodigoFormaEnum.FORMADEPAGOS_CONTROLADOR
						.getCodigo()),
				modulo, campos,
				valores);
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Asigna un valor al nombre rubro cdp.
	 *
	 * @param rsNrubro
	 * Lista que contiene el valor
	 */
	private void asignarNombreRubro(List<Registro> rsNrubro) {
		if (!rsNrubro.isEmpty()) {
			for (int j = 0; j < rsNrubro.size(); j++) {
				nombreRubro = SysmanFunciones.nvl(rsNrubro.get(j).getCampos()
						.get("NOMBRE_RUBRO_CDP"), "").toString();
			}
		}
	}

	/**
	 * Realiza el calculo de los valores necesarios en el estudio
	 * previo, tomando los datos de costos de personal y de items
	 * estudio.
	 *
	 * @param ac
	 */
	public void oprimirCmdCalcularProy() {
		double directos = 0;
		double indirectos = 0;
		double total1;
		double total2;
		double granTotal;
		double valorIva = 0;
		String valorConFormato;
		List<Registro> rs;
		try {

			Map<String, Object> param = new TreeMap<>();

			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

			param.put(GeneralParameterEnum.COD_ESTUDIO.getName(),
					registro.getCampos().get(cCodEstudio));

			rs = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmestprevioproysControladorUrlEnum.URL41629
									.getValue())
							.getUrl(), param));

			int aux = rs.size();

			/* Mientras exista personal requerido */
			while (aux > 0) {
				aux--;

				double cantidad = Double.parseDouble(rs.get(aux).getCampos()
						.get(cCantidad).toString());

				if ("true".equals(rs.get(aux).getCampos().get("CONTRATO")
						.toString())) {
					directos = directos + cantidad;
				}
				else {
					indirectos = indirectos + cantidad;
				}
			}

			registro.getCampos().put(cEmpleoDirecto, directos);
			registro.getCampos().put(cEmpleoIndirecto, indirectos);
			total1 = 0;

			rs = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmestprevioproysControladorUrlEnum.URL42723
									.getValue())
							.getUrl(), param));

			int aux1 = rs.size();

			while (aux1 > 0) {
				aux1--;

				total1 = total1
						+ Double.parseDouble(SysmanFunciones
								.nvl(rs.get(aux1).getCampos()
										.get(cValorTotal), "0")
								.toString());
			}

			total2 = 0;

			rs = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmestprevioproysControladorUrlEnum.URL43446
									.getValue())
							.getUrl(), param));

			int aux2 = rs.size();

			while (aux2 > 0) {
				aux2--;

				total2 = total2
						+ Double.parseDouble(SysmanFunciones.nvl(
								rs.get(aux2).getCampos().get(cVlrTotal),
								"0")
								.toString());
				valorIva = valorIva
						+ Double.parseDouble(SysmanFunciones.nvl(
								rs.get(aux2).getCampos().get(cVlrTotal),
								"0")
								.toString())
						- Double.parseDouble(SysmanFunciones.nvl(
								rs.get(aux2).getCampos().get(cSubtotal),
								"0")
								.toString());
			}

			granTotal = total1 + total2;
			total1 = granTotal - valorIva;
			registro.getCampos().put(cCostoEst, total1);
			registro.getCampos().put(cCostoIvaEst, valorIva);
			registro.getCampos().put(cCostoTotalEst, granTotal);
			DecimalFormat dblDF = new DecimalFormat("###,###,###,###.00");
			valorConFormato = dblDF.format(granTotal);
			SysmanFunciones.moneda(granTotal, 0);
			registro.getCampos()
			.put(cCuantiaContr,
					SysmanFunciones.moneda(granTotal, 0)
					+ " " + "($ " + " "
							+ valorConFormato + ")");

		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void oprimirSobreEscribirFundamentos() {
		List<Registro> rs;
		try {
			JsfUtil.agregarMensajeAlerta(
					idioma.getString("TB_TB2193"));

			Map<String, Object> param = new TreeMap<>();

			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

			param.put(FrmestprevioproysControladorEnum.COD_T_CONTRATO
					.getValue(),
					registro.getCampos().get(cTipoContrato));

			rs = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmestprevioproysControladorUrlEnum.URL45316
									.getValue())
							.getUrl(), param));

			int aux = rs.size();
			if (aux > 0) {
				aux--;

				registro.getCampos().put(cFundJuridicos,
						rs.get(aux).getCampos().get("FUNDAMENTACION"));
			}
			else {
				registro.getCampos().put(cFundJuridicos, "");
			}
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void oprimirSupervisores() {
		JSONObject llaves = new JSONObject();
		try {
			llaves.put("NUMERO_ESTUDIO", registro.getCampos().get(cCodEstudio));

			String tabla = "ES_SUPERVISORES";
			String numero = String
					.valueOf(registro.getCampos().get(cCodEstudio));

			Map<String, Object> parametros = new TreeMap<>();

			parametros.put("tabla", tabla);

			parametros.put("llaves", llaves.toString());

			parametros.put("numero", numero);

			parametros.put("vobo", String.valueOf(vobo));

			Direccionador direccionador = new Direccionador();

			direccionador.setNumForm(Integer
					.toString(GeneralCodigoFormaEnum.DSUPERVISORES_CONTROLADOR
							.getCodigo()));

			direccionador.setParametros(parametros);
			SessionUtil.redireccionarForma(direccionador,
					modulo);

		}
		catch (JSONException e) {
			Logger.getLogger(FrmestprevioproysControlador.class.getName())
			.log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());

		}

	}
	
	public void oprimirbtnRecursosNat() {					
		String numero = String.valueOf(registro.getCampos().get(cCodEstudio));

		Map<String, Object> parametros = new TreeMap<>();
			
		parametros.put(cCodEstudio1, numero);

		Direccionador direccionador = new Direccionador();

		direccionador.setNumForm(Integer
					.toString(GeneralCodigoFormaEnum.FRM_ESTPREVRECURSOSNAT_CONTROLADOR
							.getCodigo()));

		direccionador.setParametros(parametros);
			SessionUtil.redireccionarForma(direccionador,
					modulo);
	}

	public void oprimirAnexos() {
		String titulo = "ANEXOS DE ESTUDIO PREVIO";
		String tablaParametro = "ARCHIVOS_ES_ESTPREVIO";
		String condicion = SysmanFunciones.concatenar(" WHERE COMPANIA = '",
				compania, "' ", " AND COD_ESTUDIO = '",
				registro.getCampos().get(cCodEstudio).toString(), "'",
				"   AND VIGENCIA = ", vigenciaPeriodo, "");

		String parametroRuta = "RUTA DIGITALIZADO ANEXOS ESTUDIOS PREVIOS";
		String codigo = String.valueOf(registro.getCampos().get(cCodEstudio));

		Map<String, Object> parametros = new TreeMap<>();

		parametros.put("titulo", titulo);

		parametros.put("tabla", tablaParametro);

		parametros.put("condicion", condicion);

		parametros.put("parametroRuta", parametroRuta);

		parametros.put("codigo", codigo);

		parametros.put("vigencia", vigenciaPeriodo);

		parametros.put(cEsCreador, String.valueOf(vobo));

		Direccionador direccionador = new Direccionador();

		direccionador.setNumForm(Integer
				.toString(GeneralCodigoFormaEnum.ANEXOSESTUDIOSPREVIOS_CONTROLADOR
						.getCodigo()));

		direccionador.setParametros(parametros);
		SessionUtil.redireccionarForma(direccionador, modulo);
	}

	public void oprimirCriteriosEv() {

		Map<String, Object> parametros = new TreeMap<>();

		parametros.put("ridEstPrevios", css);

		parametros.put(cTxtCodEstudio, String.valueOf(registro.getCampos()
				.get(cCodEstudio)));

		parametros.put("tipoContratacion", String.valueOf(registro.getCampos()
				.get(cTipoContrato)));

		parametros.put("visualizar", vobo);

		parametros.put(cVigenciaPeriodo, vigenciaPeriodo);

		Direccionador direccionador = new Direccionador();

		direccionador.setNumForm(Integer
				.toString(GeneralCodigoFormaEnum.ESCRITERIOS_FAC_PROY_CONTROLADOR
						.getCodigo()));

		direccionador.setParametros(parametros);

		SessionUtil.redireccionarForma(direccionador, modulo);

	}

	public void oprimirBtUNSPSC() {
		String codEstudio = String
				.valueOf(registro.getCampos().get(cCodEstudio));

		String titulo = SysmanFunciones.concatenar(
				idioma.getString("TB_TB3499"), "  ",
				registro.getCampos().get(cNombreEstudio).toString());

		Map<String, Object> parametros = new TreeMap<>();

		parametros.put(cTxtCodEstudio, codEstudio);

		parametros.put("titulo", titulo);

		parametros.put("visualizar", String.valueOf(vobo));

		Direccionador direccionador = new Direccionador();

		direccionador.setNumForm(Integer
				.toString(GeneralCodigoFormaEnum.FRMESTPREVIOUNSPSCS_CONTROLADOR
						.getCodigo()));

		direccionador.setParametros(parametros);

		SessionUtil.redireccionarForma(direccionador, modulo);
	}

	public void oprimirBtExperiencia() {
		String codEstudio = String
				.valueOf(registro.getCampos().get(cCodEstudio));

		String perfil = recuperarPerfil(codEstudio);
		String voBo = String.valueOf(registro.getCampos().get("VOBO"));
		Map<String, Object> codRID = registro.getLlave();

		Map<String, Object> parametros = new HashMap<>();
		parametros.put("perfil", perfil);
		parametros.put("vobo", voBo);
		// Parametros de regreso
		parametros.put("RID", codRID);
		parametros.put(cCodEstudio1, codEstudio);
		parametros.put(cVigenciaPeriodo, vigenciaPeriodo);

		Direccionador direccionador = new Direccionador();
		direccionador.setNumForm(Integer
				.toString(GeneralCodigoFormaEnum.FRMESTPREVIOEXPERIENCIAS_CONTROLADOR
						.getCodigo()));
		direccionador.setParametros(parametros);

		SessionUtil.redireccionarForma(direccionador, modulo);
	}
	
	public void oprimirAdjuntos() {

		String codEstudio = String
				.valueOf(registro.getCampos().get(cCodEstudio));

		Map<String, Object> parametros = new TreeMap<>();
		
		parametros.put("rid", css);
		parametros.put(cCodEstudio1, codEstudio);
		parametros.put(cVigenciaPeriodo, vigenciaPeriodo);

		Direccionador direccionador = new Direccionador();

		direccionador.setParametros(parametros);

		direccionador.setNumForm(Integer
				.toString(GeneralCodigoFormaEnum.FRM_ADJUNTOS_ESTUDIOS_PREVIOS_CONTROLADOR
						.getCodigo()));

		SessionUtil.redireccionarForma(direccionador, modulo);

	}

	/**
	 * Recupera la descripcion del perfil.
	 *
	 * @return Descripcion del perfil.
	 */
	public String recuperarPerfil(String codEstudio) {

		String descripcion = null;

		Map<String, Object> parametro = new TreeMap<>();

		parametro.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		parametro.put(GeneralParameterEnum.COD_ESTUDIO.getName(), codEstudio);

		Registro registro1;
		try {
			registro1 = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmestprevioproysControladorUrlEnum.URL122879
									.getValue())
							.getUrl(),
							parametro));
			if (registro1 != null) {

				descripcion = registro1.getCampos().get(cPyeContratista) != null
						? registro1.getCampos().get(cPyeContratista).toString()
								: "";
			}

		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		return descripcion;
	}

	public void cambiarCombEstudio() {
		// <CODIGO_DESARROLLADO>
		List<Registro> rsCopiaEst;
		try {
			copiarDeVisible = true;

			Map<String, Object> param = new TreeMap<>();

			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

			param.put(GeneralParameterEnum.COD_ESTUDIO.getName(), combEstudio);

			rsCopiaEst = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmestprevioproysControladorUrlEnum.URL54361
									.getValue())
							.getUrl(), param));

			for (Registro registro1 : rsCopiaEst) {
				registro.getCampos().put(cNombreEstudio,
						registro1.getCampos().get(cNombreEstudio));
				registro.getCampos().put(cTipoContrato,
						registro1.getCampos().get(cTipoContrato));
				nombreTipoContrato = SysmanFunciones.nvl(registro1.getCampos()
						.get(cTipoContrato), "").toString();

				cargarListaCmbTipoC();

				txtTipoCont = SysmanFunciones
						.nvl(registro1.getCampos().get(cTipoContrato),
								"")
						.toString();

				registro.getCampos().put(cAdjudicacion,
						registro1.getCampos().get(cAdjudicacion));

				nombreAdjudicacion = SysmanFunciones.nvl(registro1.getCampos()
						.get(cAdjudicacion), "").toString();

				cargarListaCmbAdjudicacion();

				registro.getCampos().put(cDependencia,
						registro1.getCampos().get(cDependencia));
				registro.getCampos().put(cResponsable,
						registro1.getCampos().get(cResponsable));
				cargarListaResponsable();
				registro.getCampos().put(cCargo,
						registro1.getCampos().get(cCargo));
				registro.getCampos().put("PROPONENTE",
						registro1.getCampos().get("PROPONENTE"));
				registro.getCampos().put(cSupervision,
						registro1.getCampos().get(cSupervision));
				cargarListaInterventor();
				registro.getCampos().put(cCargoInterventor,
						registro1.getCampos().get(cCargoInterventor));
				registro.getCampos().put("OBJETO_CONTRATO",
						registro1.getCampos().get("OBJETO_CONTRATO"));
				registro.getCampos().put("ALCANCE",
						registro1.getCampos().get("ALCANCE"));
				registro.getCampos().put("DEF_NECESIDAD",
						registro1.getCampos().get("DEF_NECESIDAD"));
				registro.getCampos().put(cPlazoEjecucion,
						registro1.getCampos().get(cPlazoEjecucion));
				registro.getCampos().put(cCuantiaContr,
						registro1.getCampos().get(cCuantiaContr));
				registro.getCampos().put("FORMA_PAGOS",
						registro1.getCampos().get("FORMA_PAGOS"));
				registro.getCampos().put(cFundJuridicos,
						registro1.getCampos().get(cFundJuridicos));
				registro.getCampos().put("OBL_CONTRATISTA",
						registro1.getCampos().get("OBL_CONTRATISTA"));
				registro.getCampos().put("OBL_ENTIDAD",
						registro1.getCampos().get("OBL_ENTIDAD"));
				registro.getCampos().put(cPyeContratista,
						registro1.getCampos().get(cPyeContratista));
				registro.getCampos().put("AN_CONVENIENCIA",
						registro1.getCampos().get("AN_CONVENIENCIA"));
				registro.getCampos().put("AN_OPORTUNIDAD",
						registro1.getCampos().get("AN_OPORTUNIDAD"));
				registro.getCampos().put("POBLACION_BENEF",
						registro1.getCampos().get("POBLACION_BENEF"));
				registro.getCampos().put(cCantPersonas,
						registro1.getCampos().get(cCantPersonas));
				registro.getCampos().put(cEmpleoDirecto,
						registro1.getCampos().get(cEmpleoDirecto));
				registro.getCampos().put(cEmpleoIndirecto,
						registro1.getCampos().get(cEmpleoIndirecto));
				registro.getCampos().put(cCostoEst,
						registro1.getCampos().get(cCostoEst));
				registro.getCampos().put(cCostoIvaEst,
						registro1.getCampos().get(cCostoIvaEst));
				registro.getCampos().put(cCostoTotalEst,
						registro1.getCampos().get(cCostoTotalEst));
				registro.getCampos().put("PRODUCTO_ENTREGAR",
						registro1.getCampos().get("PRODUCTO_ENTREGAR"));
				registro.getCampos().put("RESULTADOS_ESP",
						registro1.getCampos().get("RESULTADOS_ESP"));
				registro.getCampos().put("NOSOLICITUD_CNE",
						registro1.getCampos().get("NOSOLICITUD_CNE"));
				registro.getCampos().put("NUM_CNE",
						registro1.getCampos().get("NUM_CNE"));
				registro.getCampos().put("FECHA_CNE",
						registro1.getCampos().get("FECHA_CNE"));
				registro.getCampos().put("FECHA_SOLICITUD",
						registro1.getCampos().get("FECHA_SOLICITUD"));
				registro.getCampos()
				.put(FrmestprevioproysControladorEnum.NUM_PROCESO
						.getValue(),
						registro1.getCampos()
						.get(FrmestprevioproysControladorEnum.NUM_PROCESO
								.getValue()));
				registro.getCampos().put(cTipoDia,
						registro1.getCampos().get(cTipoDia));
				txtEstado = String.valueOf(registro1.getCampos().get(cEstado));
				registro.getCampos().put("ANULADOPOR",
						registro1.getCampos().get("ANULADOPOR"));
				registro.getCampos().put("TIPO_EST_PREVIO",
						registro1.getCampos().get(cTipoDia));
				registro.getCampos().put(cTipoContrato,
						registro1.getCampos().get(cTipoContrato));
				registro.getCampos().put("COMPANIA", compania);

				Map<String, Object> parametro = new TreeMap<>();

				parametro.put(GeneralParameterEnum.COMPANIA.getName(),
						compania);

				parametro.put(GeneralParameterEnum.DEPENDENCIA.getName(),
						registro.getCampos().get(cDependencia));

				Registro regNombreDependencia = RegistroConverter
						.toRegistro(requestManager.get(
								UrlServiceUtil.getInstance()
								.getUrlServiceByUrlByEnumID(
										FrmestprevioproysControladorUrlEnum.URL109314
										.getValue())
								.getUrl(),
								parametro));

				nombreDependecia = regNombreDependencia != null
						? regNombreDependencia.getCampos().get(cNombre)
								.toString()
								: "";

								parametro.put(GeneralParameterEnum.RESPONSABLE.getName(),
										registro.getCampos().get(cResponsable));

								Registro recNombreResponsable = RegistroConverter
										.toRegistro(requestManager.get(
												UrlServiceUtil.getInstance()
												.getUrlServiceByUrlByEnumID(
														FrmestprevioproysControladorUrlEnum.URL26551
														.getValue())
												.getUrl(),
												parametro));

								nombreResponsable = recNombreResponsable != null
										? recNombreResponsable.getCampos()
												.get(cNombre).toString()
												: "";

												sucursalResponsable = recNombreResponsable != null
														? recNombreResponsable.getCampos()
																.get(cSucursal).toString()
																: "";

																Map<String, Object> parameter = new TreeMap<>();

																parameter.put(GeneralParameterEnum.COMPANIA.getName(),
																		compania);

																parameter.put(FrmestprevioproysControladorEnum.CEDULA
																		.getValue(),
																		registro.getCampos().get(cSupervision));

																Registro regSupervisor = RegistroConverter
																		.toRegistro(requestManager.get(
																				UrlServiceUtil.getInstance()
																				.getUrlServiceByUrlByEnumID(
																						FrmestprevioproysControladorUrlEnum.URL103407
																						.getValue())
																				.getUrl(),
																				parameter));

																nombreSupervisor = regSupervisor != null
																		? regSupervisor.getCampos()
																				.get(cNombre).toString()
																				: "";

																				sucursalSupervision = regSupervisor != null
																						? regSupervisor.getCampos()
																								.get(cSucursal).toString()
																								: "";

				registro.getCampos().put("SECTOR_ASP_GENERAL",
                		registro1.getCampos().get("SECTOR_ASP_GENERAL"));
                registro.getCampos().put("SECTOR_ASP_ECONOMICO",
                		registro1.getCampos().get("SECTOR_ASP_ECONOMICO"));
                registro.getCampos().put("SECTOR_ASP_TECNICO",
                		registro1.getCampos().get("SECTOR_ASP_TECNICO"));
                registro.getCampos().put("SECTOR_ASP_REGULATORIO",
                		registro1.getCampos().get("SECTOR_ASP_REGULATORIO"));
                registro.getCampos().put("SECTOR_ESTUDIO_OFERTA",
                		registro1.getCampos().get("SECTOR_ESTUDIO_OFERTA"));
                registro.getCampos().put("SECTOR_ESTUDIO_DEMANDA",
                		registro1.getCampos().get("SECTOR_ESTUDIO_DEMANDA"));
                registro.getCampos().put("SECTOR_ANALISIS_ESTADISTICO",
                		registro1.getCampos().get("SECTOR_ANALISIS_ESTADISTICO"));
                registro.getCampos().put("SECTOR_PERSPECTIVA_LEGAL",
                		registro1.getCampos().get("SECTOR_PERSPECTIVA_LEGAL"));
                registro.getCampos().put("SECTOR_CONCLUSIONES",
                		registro1.getCampos().get("SECTOR_CONCLUSIONES"));
                registro.getCampos().put("ESPECIFICACION_TEC",
                		registro1.getCampos().get("ESPECIFICACION_TEC"));
                registro.getCampos().put("OBL_GARANTIAS",
                		registro1.getCampos().get("OBL_GARANTIAS"));
                
			}

			registro.getCampos().put(GeneralParameterEnum.FECHA.getName(),
					new Date());

			registro.getCampos().put(
					GeneralParameterEnum.DATE_CREATED.getName(),
					new Date());

			registro.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(),
					usuario);

			agregarRegistroNuevo(false);

			cargarNombreContratoAdjudicacion();

			verMensaje = true;

			JsfUtil.agregarMensajeInformativo(
					idioma.getString("TB_TB2194"));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	public void cambiarTxtCodEstudio() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarTxtFechaRegistro() {

		int anioFecha = SysmanFunciones.ano((Date) registro.getCampos()
				.get(GeneralParameterEnum.FECHA
						.getName()));

		if (anioFecha != Integer.parseInt(vigenciaPeriodo)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2293"));

			registro.getCampos().put(GeneralParameterEnum.FECHA.getName(),
					auxFecha);

			/**
			 * return;
			 */
		}

	}

	private void cargarNombreContratoAdjudicacion() {
		try {
			Map<String, Object> param = new TreeMap<>();

			param.put(cCodTContrato,
					registro.getCampos().get(cTipoContrato));

			nombreTipoContrato = listaCmbTipoC
					.getRegistroUnico(param)
					.getCampos().get(cNombre).toString();

			cargarListaCmbAdjudicacion();

			nombreAdjudicacion = "";

			Map<String, Object> parametro = new TreeMap<>();

			parametro.put(cCodContrato,
					registro.getCampos().get(cAdjudicacion));

			Registro rNom;

			rNom = listaCmbAdjudicacion
					.getRegistroUnico(parametro);
			if (rNom != null) {
				nombreAdjudicacion = rNom.getCampos().get(cNombre)
						.toString();
			}

		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * Metodo que trae la lista de los modelos de acuerdo al tipo de
	 * contrato seleccionado
	 */
	public void consultarModelos() {

		cargarListaCmbTipoC();

		Map<String, Object> parametro = new TreeMap<>();

		parametro.put(FrmestprevioproysControladorEnum.CODCONTRATO.getValue(),
				codContratoM);

		Registro regAux;
		try {
			regAux = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmestprevioproysControladorUrlEnum.URL92561
									.getValue())
							.getUrl(),
							parametro));

			listaModelos = SysmanFunciones
					.nvl(regAux.getCampos().get("LISTADO"), "")
					.toString();

		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	public void seleccionarFilaCmbModeloEstudio(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		codigoPlantilla = SysmanFunciones
				.nvl(registroAux.getCampos().get(cCodigo), "")
				.toString();

		nombreDocDescarga = SysmanFunciones
				.nvl(registroAux.getCampos().get(cNombre), "")
				.toString();

		fechaPlantilla = SysmanFunciones
				.nvl(registroAux.getCampos().get(cFecha), "")
				.toString();
	}

	public void seleccionarFilaCombTercero(SelectEvent event) {

		Registro registroAux = (Registro) event.getObject();

		combTercero = SysmanFunciones
				.nvl(registroAux.getCampos().get(cNombre), "")
				.toString();

		SessionUtil.getNivelUsuario(modulo);

		cargarListaCombEstudio();
	}

	public void seleccionarFilaCmbTipoC(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(cTipoContrato,
				registroAux.getCampos().get(cCodTContrato));
		txtTipCont = SysmanFunciones
				.nvl(registroAux.getCampos().get(cCodTContrato), "")
				.toString();
		nombreTipoContrato = SysmanFunciones
				.nvl(registroAux.getCampos().get(cNombre), "")
				.toString();

		nombreAdjudicacion = null;

		cargarListaCmbAdjudicacion();
	}

	public void seleccionarFilaCmbAdjudicacion(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(cAdjudicacion,
				registroAux.getCampos().get(cCodContrato));
		nombreAdjudicacion = SysmanFunciones
				.nvl(registroAux.getCampos().get(cNombre), "")
				.toString();
	}

	public void seleccionarFilaDependencia(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(cDependencia,
				registroAux.getCampos().get(cCodigo));
		nombreDependecia = SysmanFunciones
				.nvl(registroAux.getCampos().get(cNombre), "")
				.toString();
		cargarListaResponsable();
	}

	public void seleccionarFilaResponsable(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(cResponsable,
				registroAux.getCampos().get(cResponsable));
		nombreResponsable = SysmanFunciones
				.nvl(registroAux.getCampos().get(cNombre), "")
				.toString();
		cargoResponsable = SysmanFunciones
				.nvl(registroAux.getCampos().get(cCargo), "")
				.toString();
		sucursalResponsable = SysmanFunciones
				.nvl(registroAux.getCampos().get(cSucursal), "")
				.toString();
		registro.getCampos().put(cCargo, cargoResponsable);
	}

	public void seleccionarFilaInterventor(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(cSupervision,
				registroAux.getCampos().get("CEDULA"));
		cargoInterventor = SysmanFunciones
				.nvl(registroAux.getCampos().get(cCargo), "")
				.toString();
		registro.getCampos().put(cCargoInterventor, cargoInterventor);
		nombreSupervisor = SysmanFunciones
				.nvl(registroAux.getCampos().get(cNombre), "")
				.toString();
		sucursalSupervision = SysmanFunciones
				.nvl(registroAux.getCampos().get(cSucursal), "")
				.toString();
	}

	public void alAbrirFormulario() {
		// Codigo
	}

	@Override
	public void cargarRegistro() {
		precargarRegistro();

		vobo = Boolean.parseBoolean(SysmanFunciones.nvl(
				registro.getCampos().get("VOBO"), "false")
				.toString());

		if ("i".equals(accion)) {
			txtAvisoVisible = false;

			registro.getCampos().put("combEstudio", "");
			registro.getCampos().put("combTercero", "");
			registro.getCampos().put("adjudicado", false);
			registro.getCampos().put(cCantPersonas, 0);
			registro.getCampos().put(cEmpleoDirecto, 0);
			registro.getCampos().put(cEmpleoIndirecto, 0);
			registro.getCampos().put(cCostoEst, 0);
			registro.getCampos().put(cCostoIvaEst, 0);
			registro.getCampos().put(cCostoTotalEst, 0);
			registro.getCampos().put(cTipoContrato, " ");
			registro.getCampos().put(cAdjudicacion, " ");
			registro.getCampos().put("VOBO", false);
			auxFecha = null;
			verModalidad = true;
			nombreDependecia = "";
			nombreResponsable = "";
			nombreSupervisor = "";
			cmbCombEstudioVisible = false;
			cmbCombTerceroVisible = false;
			nombreDependecia = "";
			nombreResponsable = "";
			nombreSupervisor = "";
			nombreTipoContrato = "";
			nombreAdjudicacion = "";
			nombreTipoContrato = "";
			nombreAdjudicacion = "";

			if ("SI".equals(parametroCopiarDe)) {
				copiarDeVisible = true;
			}
			else {
				copiarDeVisible = false;
			}

			bloqueaCopiarDe = false;

		}
		else {
			codContratoM = registro.getCampos().get(cTipoContrato)
					.toString();
			consultarModelos();

			auxFecha = (Date) registro.getCampos()
					.get(GeneralParameterEnum.FECHA.getName());
			cargarNombreContratoAdjudicacion();

			txtAvisoVisible = false;
			copiarDeVisible = false;

			vobo = false;
			verModalidad = false;

		}

		cmbCombEstudioVisible = false;

		cargarListaResponsable();
		cargarListaCmbModeloEstudio();

		bloqueaCamposVoBo();

		if ((Boolean) registro.getCampos().get("VOBO")) {
			insertarFormulario = true;
			bloqueaTxtCodEstudio = false;

		}
		else {
			insertarFormulario = true;
			eliminarFormulario = false;
			actualizarFormulario = true;
		}

		bloqueaTxtCodEstudio = true;

		esCreador = SysmanFunciones
				.nvl(registro.getCampos().get(cCreatedBy), "")
				.toString()
				.equals(SessionUtil.getUser().getCodigo());

	}

	public void bloqueaCamposVoBo() {
		if ((Boolean) registro.getCampos().get("VOBO")) {
			bloqueaTxtCodEstudio = true;
			bloqueaTxtCodEstudio11 = true;
			bloqueaTxtFechaInicial = true;
			bloqueaTxtFechaFinal = true;
			bloqueaCmbIntervalo = true;
			bloqueaCmdCalcularProy = true;
			bloqueaSobreEscribirFundamentos = true;
			bloqueaCopiarDe = true;

		}
		else {
			bloqueaTxtCodEstudio11 = false;
			bloqueaTxtFechaInicial = false;
			bloqueaTxtFechaFinal = false;
			bloqueaCmbIntervalo = false;
			bloqueaCmdCalcularProy = false;
			bloqueaSobreEscribirFundamentos = false;
			bloqueaCopiarDe = false;

		}
	}

	@Override
	public boolean insertarAntes() {
		/**
		 * mvenegas,validacion solo aplica cuando el parametro MANEJA
		 * PLAN DE ACCION esta en SI, de estar en NO, el valor del
		 * cotos del estudio puede ser el que se desee
		 */
		// asana, 18/01/2019, se asigna consecutivo para que en método
		// (validarSaldoEstudios) el campo cCodEstudio no llegue null
		registro.getCampos().put(cCodEstudio, generarConsecutivo());

		if (!validarSaldoEstudios()) {
			return false;
		}
		registro.getCampos().remove("adjudicado");
		registro.getCampos().put(cSupervision,
				registro.getCampos().get(cSupervision));
		registro.getCampos().put("SUCURSAL_SUPERVISION", sucursalSupervision);
		registro.getCampos().put("SUCURSAL_RESPONSABLE", sucursalResponsable);
		registro.getCampos().put("COMPANIA", compania);
		registro.getCampos().put(cTipoDia, "I");
		registro.getCampos().put("NOSOLICITUD_CNE", "0");

		return true;
	}

	private long generarConsecutivo() {
		long consecutivo = 0;

		try {
			consecutivo = ejbSysmanUtl.generarConsecutivoConValorInicial(
					"ES_ESTPREVIO",
					SysmanFunciones.concatenar("COMPANIA = ''",
							compania,
							"''",
							"AND TO_CHAR(FECHA, ''YYYY'') = ",
							vigenciaPeriodo),
					cCodEstudio,
					ejbSysmanUtl.consultarParametro(compania,
							"CONSECUTIVO INICIAL ESTUDIOS PREVIOS",
							modulo, new Date(), false));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		return consecutivo;
	}

	@Override
	public boolean insertarDespues() {
		return true;
	}

	/**
	 * Este metodo valida si el parametro MANEJA PLAN DE ACCION esta
	 * en SI, suma los valores de los items y valida que el saldo
	 * digitado no sobrepase este saldo, en caso de que no se hayan
	 * insertado items asociados al estudio, se muestra un mensaje que
	 * lo indica
	 */
	public boolean validarSaldoEstudios() {
		boolean salida = true;

		if (manejaPlanAccion.equals("SI")) {

			@SuppressWarnings("unused")
			BigDecimal saldoItems = new BigDecimal("0");
			Map<String, Object> parametrosSaldo = new TreeMap<>();
			parametrosSaldo.put(GeneralParameterEnum.COMPANIA.getName(),
					compania);
			parametrosSaldo.put("ESTUDIO",
					registro.getCampos().get(cCodEstudio));

			Registro saldoActual;
			try {
				saldoActual = RegistroConverter.toRegistro(
						requestManager.get(UrlServiceUtil.getInstance()
								.getUrlServiceByUrlByEnumID(
										"1021002")
								.getUrl(), parametrosSaldo));
				saldoItems = SysmanFunciones
						.toString(saldoActual.getCampos()
								.get("SALDO_DISPONIBLE")) == null
								? new BigDecimal("0")
										: new BigDecimal(
												SysmanFunciones
												.toString(saldoActual
														.getCampos()
														.get("SALDO_DISPONIBLE")));

								int respuesta = saldoItems.compareTo(new BigDecimal(registro
										.getCampos().get("COSTOTOTALEST").toString()));
								/**
								 * El resultado -1, indica que el segundo valor es
								 * mayor
								 */
								if (respuesta < 0) {
									JsfUtil.agregarMensajeError(
											"El Costo Total Estimado no puede ser mayor a la suma del saldo de los items PAA asociados al estudio.");
									salida = false;
								}
								else {
									salida = true;
								}
			}
			catch (SystemException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}

		}
		return salida;
	}

	@Override
	public boolean actualizarAntes() {

		if (accion.equals(ACCION_MODIFICAR)) {

			/**
			 * mvenegas,validacion solo aplica cuando el parametro
			 * MANEJA PLAN DE ACCION esta en SI, de estar en NO, el
			 * valor del cotos del estudio puede ser el que se desee
			 */
			if (!validarSaldoEstudios()) {
				cargarRegistro();
				return false;
			}

			registro.getCampos()
			.remove(GeneralParameterEnum.COMPANIA.getName());

			registro.getCampos()
			.remove("FECHAMODIFICADOR");

			String numeroProceso = SysmanFunciones
					.nvl(registro.getCampos()
							.get(FrmestprevioproysControladorEnum.NUM_PROCESO
									.getValue()),
							"")
					.toString();

			if (SysmanFunciones.validarVariableVacio(numeroProceso)) {
				registro.getCampos()
				.put(FrmestprevioproysControladorEnum.NUM_PROCESO
						.getValue(), "0");
			}

		}

		return true;
	}

	@Override
	public boolean actualizarDespues() {
		return true;
	}

	@Override
	public boolean eliminarAntes() {
		return true;
	}

	@Override
	public boolean eliminarDespues() {
		return true;
	}

	/**
	 * Metodo ejecutado al cambiar el control Vobo
	 * 
	 */
	public void cambiarVobo() {
		// <CODIGO_DESARROLLADO>
		vobo = Boolean.parseBoolean(
				registro.getCampos().get("VOBO").toString());

		if (vobo) {
			verModalidad = true;
			bloqueaTxtCodEstudio = true;
			bloqueaCamposVoBo();
			registro.getCampos().put(cFechaVobo, new Date());
			registro.getCampos().put("HORA_VOBO", new Date());
			Calendar c = Calendar.getInstance();
			c.setTime((Date) registro.getCampos().get(cFechaVobo));
			c.get(Calendar.HOUR);
		}
		else {
			registro.getCampos().put(cFechaVobo, null);
			registro.getCampos().put("HORA_VOBO", null);
			bloqueaCamposVoBo();
			verModalidad = false;
		}
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarImpreso() {
		// <CODIGO_DESARROLLADO>
		if (Boolean.parseBoolean(
				registro.getCampos().get(cImpreso).toString())) {
			int nivelUsuario = SessionUtil.getNivelUsuario(modulo);
			if (nivelUsuario == 9) {
				registro.getCampos().put(cImpreso, 0);
			}
			else {
				registro.getCampos().put(cImpreso, -1);
			}
		}
		else {
			registro.getCampos().put(cImpreso, 0);
		}

		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Aceptar del dialogo
	 * copiarDatosAsociados en la vista
	 *
	 */
	public void aceptarcopiarDatosAsociados() {

		verMensaje = false;

		try {
			ejbPrecontractualUno.copiarDatosEstudioPrevio(compania,
					Long.parseLong(combEstudio),
					usuario, Long.parseLong(registro.getCampos()
							.get(cCodEstudio).toString()));

			JsfUtil.agregarMensajeInformativo(
					idioma.getString("TB_TB2195"));
		}
		catch (NumberFormatException | SystemException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Cancelar del dialogo
	 * copiarDatosAsociados en la vista
	 *
	 */
	public void cancelarcopiarDatosAsociados() {
		verMensaje = false;
	}

	public void validaUsuarioProy() {
		// Codigo
	}

	public List<Registro> getListaCombEstudio() {
		return listaCombEstudio;
	}

	public void setListaCombEstudio(List<Registro> listaCombEstudio) {
		this.listaCombEstudio = listaCombEstudio;
	}

	public List<Registro> getListaEntidadProponente() {
		return listaEntidadProponente;
	}

	public void setListaEntidadProponente(
			List<Registro> listaEntidadProponente) {
		this.listaEntidadProponente = listaEntidadProponente;
	}

	public RegistroDataModelImpl getListaCombTercero() {
		return listaCombTercero;
	}

	public void setListaCombTercero(RegistroDataModelImpl listaCombTercero) {
		this.listaCombTercero = listaCombTercero;
	}

	public RegistroDataModelImpl getListaCmbTipoC() {
		return listaCmbTipoC;
	}

	public void setListaCmbTipoC(RegistroDataModelImpl listaCmbTipoC) {
		this.listaCmbTipoC = listaCmbTipoC;
	}

	public RegistroDataModelImpl getListaCmbAdjudicacion() {
		return listaCmbAdjudicacion;
	}

	public void setListaCmbAdjudicacion(
			RegistroDataModelImpl listaCmbAdjudicacion) {
		this.listaCmbAdjudicacion = listaCmbAdjudicacion;
	}

	public RegistroDataModelImpl getListaDependencia() {
		return listaDependencia;
	}

	public void setListaDependencia(RegistroDataModelImpl listaDependencia) {
		this.listaDependencia = listaDependencia;
	}

	public RegistroDataModelImpl getListaResponsable() {
		return listaResponsable;
	}

	public void setListaResponsable(RegistroDataModelImpl listaResponsable) {
		this.listaResponsable = listaResponsable;
	}

	public RegistroDataModelImpl getListaInterventor() {
		return listaInterventor;
	}

	public void setListaInterventor(RegistroDataModelImpl listaInterventor) {
		this.listaInterventor = listaInterventor;
	}

	public List<Registro> getListacertInexistencia() {
		return listacertInexistencia;
	}

	public void setListacertInexistencia(List<Registro> listacertInexistencia) {
		this.listacertInexistencia = listacertInexistencia;
	}
	public String getCombTercero() {
		return combTercero;
	}

	public void setCombTercero(String combTercero) {
		this.combTercero = combTercero;
	}

	public String getCombEstudio() {
		return combEstudio;
	}

	public void setCombEstudio(String combEstudio) {
		this.combEstudio = combEstudio;
	}

	public String getNombreDependecia() {
		return nombreDependecia;
	}

	public void setNombreDependecia(String nombreDependecia) {
		this.nombreDependecia = nombreDependecia;
	}

	public String getCmbIntervalo() {
		return cmbIntervalo;
	}

	public void setCmbIntervalo(String cmbIntervalo) {
		this.cmbIntervalo = cmbIntervalo;
	}

	public String getNombreResponsable() {
		return nombreResponsable;
	}

	public void setNombreResponsable(String nombreResponsable) {
		this.nombreResponsable = nombreResponsable;
	}

	public String getFiltroDep() {
		return filtroDep;
	}

	public void setFiltroDep(String filtroDep) {
		this.filtroDep = filtroDep;
	}

	public boolean isBloqueaVoBo() {
		return bloqueaVoBo;
	}

	public void setBloqueaVoBo(boolean bloqueaVoBo) {
		this.bloqueaVoBo = bloqueaVoBo;
	}

	public boolean isCopiarDeVisible() {
		return copiarDeVisible;
	}

	public void setCopiarDeVisible(boolean copiarDeVisible) {
		this.copiarDeVisible = copiarDeVisible;
	}

	public boolean isActualizarFormulario() {
		return actualizarFormulario;
	}

	public void setActualizarFormulario(boolean actualizarFormulario) {
		this.actualizarFormulario = actualizarFormulario;
	}

	public boolean isEliminarFormulario() {
		return eliminarFormulario;
	}

	public void setEliminarFormulario(boolean eliminarFormulario) {
		this.eliminarFormulario = eliminarFormulario;
	}

	public boolean isInsertarFormulario() {
		return insertarFormulario;
	}

	public void setInsertarFormulario(boolean insertarFormulario) {
		this.insertarFormulario = insertarFormulario;
	}

	public String getTxtTipCont() {
		return txtTipCont;
	}

	public void setTxtTipCont(String txtTipCont) {
		this.txtTipCont = txtTipCont;
	}

	public String getCodigoDependencia() {
		return codigoDependencia;
	}

	public void setCodigoDependencia(String codigoDependencia) {
		this.codigoDependencia = codigoDependencia;
	}

	public String getCargoResponsable() {
		return cargoResponsable;
	}

	public void setCargoResponsable(String cargoResponsable) {
		this.cargoResponsable = cargoResponsable;
	}

	public String getCargoInterventor() {
		return cargoInterventor;
	}

	public void setCargoInterventor(String cargoInterventor) {
		this.cargoInterventor = cargoInterventor;
	}

	public String getNombreSupervisor() {
		return nombreSupervisor;
	}

	public void setNombreSupervisor(String nombreSupervisor) {
		this.nombreSupervisor = nombreSupervisor;
	}

	public String getDependenciaOrigen() {
		return dependenciaOrigen;
	}

	public void setDependenciaOrigen(String dependenciaOrigen) {
		this.dependenciaOrigen = dependenciaOrigen;
	}

	public int getAnoVigencia() {
		return anoVigencia;
	}

	public void setAnoVigencia(int anoVigencia) {
		this.anoVigencia = anoVigencia;
	}

	public boolean isAdjudicado() {
		return adjudicado;
	}

	public void setAdjudicado(boolean adjudicado) {
		this.adjudicado = adjudicado;
	}

	public String getTxtTipoCont() {
		return txtTipoCont;
	}

	public void setTxtTipoCont(String txtTipoCont) {
		this.txtTipoCont = txtTipoCont;
	}

	public String getSucursalSupervision() {
		return sucursalSupervision;
	}

	public void setSucursalSupervision(String sucursalSupervision) {
		this.sucursalSupervision = sucursalSupervision;
	}

	public String getVigenciaPeriodo() {
		return vigenciaPeriodo;
	}

	public void setVigenciaPeriodo(String vigenciaPeriodo) {
		this.vigenciaPeriodo = vigenciaPeriodo;
	}

	public boolean isCargar() {
		return cargar;
	}

	public void setCargar(boolean cargar) {
		this.cargar = cargar;
	}

	public boolean isVerMensaje() {
		return verMensaje;
	}

	public void setVerMensaje(boolean verMensaje) {
		this.verMensaje = verMensaje;
	}

	public String getMensajeDialogo() {
		return mensajeDialogo;
	}

	public void setMensajeDialogo(String mensajeDialogo) {
		this.mensajeDialogo = mensajeDialogo;
	}

	public List<Registro> getListaCmbIntervalo() {
		return listaCmbIntervalo;
	}

	public void setListaCmbIntervalo(List<Registro> listaCmbIntervalo) {
		this.listaCmbIntervalo = listaCmbIntervalo;
	}

	public String getSucursalResponsable() {
		return sucursalResponsable;
	}

	public void setSucursalResponsable(String sucursalResponsable) {
		this.sucursalResponsable = sucursalResponsable;
	}

	public String getTxtEstado() {
		return txtEstado;
	}

	public void setTxtEstado(String txtEstado) {
		this.txtEstado = txtEstado;
	}

	public boolean isTxtAvisoVisible() {
		return txtAvisoVisible;
	}

	public void setTxtAvisoVisible(boolean txtAvisoVisible) {
		this.txtAvisoVisible = txtAvisoVisible;
	}

	public boolean isCmbCombEstudioVisible() {
		return cmbCombEstudioVisible;
	}

	public void setCmbCombEstudioVisible(boolean cmbCombEstudioVisible) {
		this.cmbCombEstudioVisible = cmbCombEstudioVisible;
	}

	public boolean isCmbCombTerceroVisible() {
		return cmbCombTerceroVisible;
	}

	public void setCmbCombTerceroVisible(boolean cmbCombTerceroVisible) {
		this.cmbCombTerceroVisible = cmbCombTerceroVisible;
	}

	public boolean isBloqueaTxtCodEstudio() {
		return bloqueaTxtCodEstudio;
	}

	public void setBloqueaTxtCodEstudio(boolean bloqueaTxtCodEstudio) {
		this.bloqueaTxtCodEstudio = bloqueaTxtCodEstudio;
	}

	public boolean isBloqueaTxtCodEstudio11() {
		return bloqueaTxtCodEstudio11;
	}

	public void setBloqueaTxtCodEstudio11(boolean bloqueaTxtCodEstudio11) {
		this.bloqueaTxtCodEstudio11 = bloqueaTxtCodEstudio11;
	}

	public boolean isBloqueaTxtFechaInicial() {
		return bloqueaTxtFechaInicial;
	}

	public void setBloqueaTxtFechaInicial(boolean bloqueaTxtFechaInicial) {
		this.bloqueaTxtFechaInicial = bloqueaTxtFechaInicial;
	}

	public boolean isBloqueaTxtFechaFinal() {
		return bloqueaTxtFechaFinal;
	}

	public void setBloqueaTxtFechaFinal(boolean bloqueaTxtFechaFinal) {
		this.bloqueaTxtFechaFinal = bloqueaTxtFechaFinal;
	}

	public boolean isBloqueaCmbIntervalo() {
		return bloqueaCmbIntervalo;
	}

	public void setBloqueaCmbIntervalo(boolean bloqueaCmbIntervalo) {
		this.bloqueaCmbIntervalo = bloqueaCmbIntervalo;
	}

	public boolean isBloqueaCmdCalcularProy() {
		return bloqueaCmdCalcularProy;
	}

	public void setBloqueaCmdCalcularProy(boolean bloqueaCmdCalcularProy) {
		this.bloqueaCmdCalcularProy = bloqueaCmdCalcularProy;
	}

	public boolean isBloqueaSobreEscribirFundamentos() {
		return bloqueaSobreEscribirFundamentos;
	}

	public void setBloqueaSobreEscribirFundamentos(
			boolean bloqueaSobreEscribirFundamentos) {
		this.bloqueaSobreEscribirFundamentos = bloqueaSobreEscribirFundamentos;
	}

	public boolean isBloqueaCopiarDe() {
		return bloqueaCopiarDe;
	}

	public void setBloqueaCopiarDe(boolean bloqueaCopiarDe) {
		this.bloqueaCopiarDe = bloqueaCopiarDe;
	}

	public String getNombreTipoContrato() {
		return nombreTipoContrato;
	}

	public void setNombreTipoContrato(String nombreTipoContrato) {
		this.nombreTipoContrato = nombreTipoContrato;
	}

	public String getNombreAdjudicacion() {
		return nombreAdjudicacion;
	}

	public void setNombreAdjudicacion(String nombreAdjudicacion) {
		this.nombreAdjudicacion = nombreAdjudicacion;
	}

	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}

	public String getTipoEstudio() {
		return tipoEstudio;
	}

	public void setTipoEstudio(String tipoEstudio) {
		this.tipoEstudio = tipoEstudio;
	}

	public RegistroDataModelImpl getListaCmbModeloEstudio() {
		return listaCmbModeloEstudio;
	}

	public void setListaCmbModeloEstudio(
			RegistroDataModelImpl listaCmbModeloEstudio) {
		this.listaCmbModeloEstudio = listaCmbModeloEstudio;
	}

	public String getModeloEstudio() {
		return modeloEstudio;
	}

	public void setModeloEstudio(String modeloEstudio) {
		this.modeloEstudio = modeloEstudio;
	}

	public String getNombreDocDescarga() {
		return nombreDocDescarga;
	}

	public void setNombreDocDescarga(String nombreDocDescarga) {
		this.nombreDocDescarga = nombreDocDescarga;
	}

	public String getFechaPlantilla() {
		return fechaPlantilla;
	}

	public void setFechaPlantilla(String fechaPlantilla) {
		this.fechaPlantilla = fechaPlantilla;
	}

	public String getCodigoPlantilla() {
		return codigoPlantilla;
	}

	public void setCodigoPlantilla(String codigoPlantilla) {
		this.codigoPlantilla = codigoPlantilla;
	}

	public Boolean getEsCreador() {
		return esCreador;
	}

	public void setEsCreador(Boolean esCreador) {
		this.esCreador = esCreador;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public boolean isVobo() {
		return vobo;
	}

	public void setVobo(boolean vobo) {
		this.vobo = vobo;
	}

	public boolean isVerModalidad() {
		return verModalidad;
	}

	public void setVerModalidad(boolean verModalidad) {
		this.verModalidad = verModalidad;
	}

	public boolean isEditarCostoTotalEstimado() {
		return editarCostoTotalEstimado;
	}

	public void setEditarCostoTotalEstimado(boolean editarCostoTotalEstimado) {
		this.editarCostoTotalEstimado = editarCostoTotalEstimado;
	}

	public String getManejaPlanAccion() {
		return manejaPlanAccion;
	}

	public void setManejaPlanAccion(String manejaPlanAccion) {
		this.manejaPlanAccion = manejaPlanAccion;
	}

	public boolean getVisibleCert() {
		return visibleCert;
	}

	public void setVisibleCert(boolean visibleCert) {
		this.visibleCert = visibleCert;
	}
	
	public boolean isVerCerrar() {
		return verCerrar;
	}

	public void setVerCerrar(boolean verCerrar) {
		this.verCerrar = verCerrar;
	}

	public boolean isManejaRecursosNat() {
		return manejaRecursosNat;
	}

	public void setManejaRecursosNat(boolean manejaRecursosNat) {
		this.manejaRecursosNat = manejaRecursosNat;
	}
}
