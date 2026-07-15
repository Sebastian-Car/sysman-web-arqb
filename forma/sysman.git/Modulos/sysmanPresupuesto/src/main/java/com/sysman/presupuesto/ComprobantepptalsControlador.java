package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.contabilidad.reportes.ComprobantesContPresReporteador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.ejb.EjbPresupuestoCuatroRemote;
import com.sysman.presupuesto.ejb.EjbPresupuestoDosRemote;
import com.sysman.presupuesto.ejb.EjbPresupuestoTresRemote;
import com.sysman.presupuesto.enums.ComprobantepptalsControladorEnum;
import com.sysman.presupuesto.enums.ComprobantepptalsControladorUrlEnum;
import com.sysman.recursos.ejb.EjbGeneralesRemote;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
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
import javax.faces.context.FacesContext;

import org.primefaces.component.panel.Panel;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dmaldonado
 * @version 7, 05/08/2016 09:57:28 -- Modificado por dmaldonado
 * @version 8, 18/04/2017 -- Modificado por jcrodriguez descripcion:--depuracion
 *          del controlador --creacion de dss para las consultas quemadas
 *
 * @author jcrodriguez,se eliminan las concatenaciones y se adiciona llama al
 *         metodo de sysman funciones concatenar, se agrega el mensaje cuando un
 *         reporte no tiene informacino que mostrar,se adiciona dos valores en
 *         el dss lectura para que el dss de actualizar edite la informacion
 *         correctamente
 * @version 9, 18/09/2017
 *
 *
 * @author gfigueredo
 * @version 10, 20/05/2021 Se crea la funciï¿½n
 *          {@link #copiaImputacionPresupuestal()} Se depura el controlador
 *          conforme sugerencias de SonarLint
 * @see #seleccionarFilaCompACopiar(SelectEvent)
 * @see #validarValoresEnCero(Registro)
 * @see #copiaImputacionPresupuestal()
 * 
 * 
 * 
 */
@ManagedBean
@ViewScoped
public class ComprobantepptalsControlador extends BeanBaseDatosAcmeImpl {
	

	/**
	 * variable que almacena la compaï¿½ia
	 */
	private final String compania;
	/**
	 * variable que almacena el modulo
	 */
	private final String modulo;
	
	/**
	 * variable que almacena el reporte de afectaciones
	 */
	private String reporte;
	/**
	 * Codigo del usuario que ingresa a la aplicacion
	 */
	private final String usuario;
	/**
	 * variable que almacena el comp a copiar
	 */
	private String compACopiar;
	/**
	 * variable que almacena el anio
	 */
	private String ano;
	/**
	 * variable que almacena la dependencia del solicitante
	 */
	private String prDependencia;
	/**
	 * variable que almacena el cargo del solicitante
	 */
	private String prCargo;
	/**
	 * variable que almacena el tipo comprobante
	 */
	private String strTipoComprobante;
	/**
	 * variable que almacena el mes
	 */
	private String mes;
	/**
	 * variable que almacena el nombre comprobante
	 */
	private String nombreComprobante;
	/**
	 * variable que almacena la clase de comprobante
	 */
	private String claseComprobante;
	/**
	 * variable que almacena el formato comprobante
	 */
	private String formatoComprobante;
	/**
	 * variable que el almacena el pide tercero
	 */
	private String pideTercero;
	/**
	 * variable que almacena la clase a afectar
	 */
	private String claseAfectar;
	private static final String CTEVLR_DOCUMENTO = "VLR_DOCUMENTO";
	private static final String FORMULARIO_RETORNO = "formularioRetorno";
	/**
	 * Atributo que contiene el estado del atributo ano.
	 */
	private String estado;
	/**
	 * Atributo que controla si el formulario tiene opcion de insertar. variable que
	 * almacenan el estado
	 */
	private boolean insertar;
	private boolean visibleSolicitudDIS;
	private boolean visibleDocumentoAAfectar;
	private boolean visibleContractual;
	private boolean visiblePapeles;
	private boolean visibleGenerarRegistro;
	private boolean visibleCargo;
	private boolean visibleCopiar;
	private boolean visibleCompACopiar;
	private boolean bloqLiberar;
	private boolean visibleAfectaciones;
	private boolean editableNumero;
	private boolean permiteAcme;
	private boolean visibleNombreSolicitante;
	private boolean visibleCargoSol;
	private boolean bloqNombreSolicitante;
	private boolean bloqTxtCargoSol;
	private boolean tipoVigenciaFutura;
	private boolean visibleDane;
	private boolean visibleMEN;
	private boolean visibleMENVIATICOS;
	private boolean visiblePermanencia;
	private boolean visibleTarifa;
	private boolean visibleVigenciaFutura;
	private boolean visibleActualizarTercero;
	private boolean bloqImpreso;
	private boolean bloqTercero;
	private boolean bloqFecha;
	private boolean bloqDescripcion;
	private boolean bloqTexto;
	private boolean bloqVlrDocumento;
	private boolean bloqNroDocumento;
	private boolean bloqTipoCpteAfect;
	private boolean bloqTipoContrato;
	private boolean bloqCmpteAfectado;
	private boolean bloqNumeroContrato;
	private boolean bloqDestino;
	private boolean bloqFechaVcnDoc;
	private boolean bloqAsignacion;
	private boolean bloqFuente;
	private boolean bloqUbicacion;
	private boolean bloqDependencia;
	private boolean bloqCargo;
	private boolean bloqAuxiliar;
	private boolean bloqProyecto;
	private boolean varVolver;
	private boolean volverGrilla;
	private boolean terceroVarios;
	private String tipoDeCuentas;
	private StreamedContent archivoDescarga;
	private String columna;
	private String afectacion;
	private String estadoPeriodo;
	private boolean bloqCompACopiar;
	private boolean visibleDgProyecto;
	private boolean visibleDgLiberar;
	private boolean visibleDgFecha;
	private Map<String, Object> ridContable;
	private String validarAccion;
	private boolean paramManejaReportesAppui;
	private boolean paramManejaControlDeContratos;
	private boolean manejaReportesAppui;
	private String parObligaDocumento;
	private boolean mostrarTipoCompro;
	
	
	/**
	 * variable que almacena el nombre del tercero
	 */
	private String strNombreTercero;
	/**
	 * variable que almacen el numero
	 */
	private String strNumero;
	/**
	 * variable que lista los cargos
	 */
	private List<Registro> listaCargo;
	/**
	 * variable que lista las ubicaciones
	 */
	private List<Registro> listaubicacion;
	/**
	 * variable que lista las fuentes de financiacion
	 */
	private List<Registro> listafuenteFinanciacion;
	/**
	 * variable que lista las asignaciones
	 */
	private List<Registro> listaasignacion;
	/**
	 * variable que lista los tipos de contratos
	 */
	private List<Registro> listaTipoContrato;
	/**
	 * variable que lista las refenrecias
	 */
	private List<Registro> listaReferencia;
	/**
	 * lista auxiliar
	 */
	private List<Registro> listaAuxiliar;
	/**
	 * variable que lista las dependencias
	 */
	private RegistroDataModelImpl listaDependencia;
	/**
	 * variable que lista los nombres de los solicitantes
	 */
	private RegistroDataModelImpl listaListaNombreSolicitante;
	/**
	 * variable que lista los numeros de contrato
	 */
	private RegistroDataModelImpl listaNumeroContrato;
	/**
	 * variable que lista los numeros
	 */
	private RegistroDataModelImpl listaNumero;
	/**
	 * variable que lista los terceros
	 */
	private RegistroDataModelImpl listaTercero;
	/**
	 * variable que lista auxiliar
	 */
	private RegistroDataModelImpl listaCompACopiar;
	/**
	 * variable que lista los codigos de proyectos
	 */
	private RegistroDataModelImpl listaCODPROYECTO;
	
	
	private RegistroDataModelImpl listaListaPlantillas;
	
	private RegistroDataModelImpl listatipoCompromiso;
	
	private Boolean visibleListaPlantillas = false;
	/**
	 * variable que almacena las filas
	 */
	private int filas;
	/**
	 * variable que almacena el ultimo dia
	 */
	private int ultimoDia;
	/**
	 * variable que almacena los registros pre act
	 */
	private Registro registroPreAct;
	/**
	 * variable que almacena la fecha de liberacion
	 */
	private Date fechaLiberacion;
	/**
	 * variable que lista la liberacion
	 */
	private List<Registro> listaLiberacion;
	/**
	 * variable que almacena el estado
	 */
	private boolean visibleCrearNuevoComp;

	/**
	 * variable que almacena el nombre del proyecto
	 */
	private String nombreProyecto;
	/**
	 * Atrinbuto que almacena el valor del parametro tipoPptal recibida del boton
	 * Comprobante Ingresos del formulario comprobante cnt
	 */
	private String tipoCont;
	/**
	 * Atributo que valida si el formulario se abre desde el boton comprobante
	 * presupuestal o de comprobante de ingresos
	 */
	private boolean ingreso = false;
	/**
	 * Variable que almacena la claseT
	 */
	private String claseT;

	private int formularioRetorno;
	
	private boolean visiblePresentarPlantillas;
	
	private ComprobantesContPresReporteador comprobantesContPresReporteador;

	private static final String CAMPO_TIPOCONTRATO = "TIPOCONTRATO";
	private static final String CAMPO_NUMEROCONTRATO = "NUMEROCONTRATO";
	private static final String CAMPO_NUMERO = "NUMERO";
	private static final String CAMPO_TERCERO = "TERCERO";
	private static final String CAMPO_SUCURSAL = "SUCURSAL";
	
	/**
	 * variable que almacena el valor del parametro: CONTROLA LONGITUD DE CONSECUTIVO
	 */
	private boolean controlaLongitud;
	
	/**
	 * variable ejb
	 */
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	@EJB
	private EjbPresupuestoDosRemote ejbPresupuestoDos;
	@EJB
	private EjbGeneralesRemote ejbGenerales;
	@EJB
	private EjbPresupuestoTresRemote ejbPresupuestoTres;
	@EJB
	private EjbPresupuestoCuatroRemote ejbPresupuestoCuatro;
	
	private String  plantilla;
	private String nombrePlantilla;
	private Date fechaPlantilla;
	
	private Map<String,Object> parametroswf;
	private boolean verCerrar = true;
	private boolean manejaVarios;

	public ComprobantepptalsControlador() {
		super();
		compania = SessionUtil.getCompania();
		SessionUtil.setSessionVar("modulo", "3");
		modulo = SessionUtil.getModulo();
		usuario = SessionUtil.getUser().getCodigo();
		prCargo = "";
		prDependencia = "";
		formularioRetorno = -1;
		claseT = "P";
		try {
			numFormulario = GeneralCodigoFormaEnum.COMPROBANTEPPTALS_CONTROLADOR.getCodigo();

			validarPermisos();
			cargarFlash();

		} catch (Exception ex) {
			Logger.getLogger(ComprobantepptalsControlador.class.getName()).log(Level.SEVERE, null, ex);
			SessionUtil.redireccionarMenuPermisos();
		} finally {
			SessionUtil.cleanFlash();
		}
	}

	/**
	 * metodo que se llama al inicializar el formulario
	 */
	@Override
	public void iniciarListas() {
		cargarListaNumero();
		cargarListaTercero();
		cargarListaDependencia();
		cargarListaCODPROYECTO();
		cargarListaubicacion();
		cargarListafuenteFinanciacion();
		cargarListaasignacion();
		cargarListaAuxiliar();
		cargarListaCODPROYECTO();
		cargarListaReferencia();
		cargarListaListaPlantillas();
		cargarListatipoCompromiso();
	}

	/**
	 * metodo que se llama al inicial una lista sub
	 */
	@Override
	public void iniciarListasSub() {
		// <CARGAR_LISTAS_SUBFORM>
		cargarListaCompACopiar();
		cargarListaTipoContrato();
		cargarListaNumeroContrato();
		cargarListaListaNombreSolicitante();
		cargarListaCargo();
		// </CARGAR_LISTAS_SUBFORM>
	}

	/**
	 * metodo que se llama al iniciar la lista del sub nulo
	 */
	@Override
	public void iniciarListasSubNulo() {
		// <CARGAR_LISTAS_SUBFORM_NULL>
		// </CARGAR_LISTAS_SUBFORM_NULL>
	}

	private String validarCadenaCampos(Map<String, Object> campos, String var) {
		return SysmanFunciones.validarCampoVacio(campos, var) ? "" : campos.get(var).toString();
	}

	/**
	 * metodo que se llama al cargar flash
	 */
	@SuppressWarnings("unchecked")
	public void cargarFlash() {
		Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
		if (parametrosEntrada != null) {
			rid = (Map<String, Object>) parametrosEntrada.get("rid");
			ano = validarCadenaCampos(parametrosEntrada, GeneralParameterEnum.ANO.getName().toLowerCase());
			mes = validarCadenaCampos(parametrosEntrada, GeneralParameterEnum.MES.getName().toLowerCase());
			try {
				ultimoDia = SysmanFunciones.ultimoDiaInt(SysmanFunciones.convertirAFecha("01/" + mes + "/" + ano));
			} catch (ParseException e) {
				Logger.getLogger(ComprobantepptalsControlador.class.getName()).log(Level.SEVERE, null, e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
			ingreso = (boolean) parametrosEntrada.get("ingreso");

			if (ingreso) {
				strTipoComprobante = validarCadenaCampos(parametrosEntrada, "tipoPptal");
				tipoCont = validarCadenaCampos(parametrosEntrada,
						ComprobantepptalsControladorEnum.TIPOCOMPROBANTE.getValue());
				ridContable = (Map<String, Object>) parametrosEntrada.get("rid");
				rid = (Map<String, Object>) parametrosEntrada.get("ridPptal");
			} else {
				strTipoComprobante = validarCadenaCampos(parametrosEntrada,
						ComprobantepptalsControladorEnum.TIPOCOMPROBANTE.getValue());
			}

			nombreComprobante = validarCadenaCampos(parametrosEntrada,
					ComprobantepptalsControladorEnum.CTENOMBRECOMPROBANTE.getValue());
			claseComprobante = validarCadenaCampos(parametrosEntrada,
					ComprobantepptalsControladorEnum.CTECLASECOMPROBANTE.getValue());
			formatoComprobante = validarCadenaCampos(parametrosEntrada,
					ComprobantepptalsControladorEnum.CTEFORMATOCOMPROBANTE.getValue());
			pideTercero = validarCadenaCampos(parametrosEntrada,
					ComprobantepptalsControladorEnum.CTEPIDETERCERO.getValue());
			tipoVigenciaFutura = Boolean.parseBoolean(parametrosEntrada.get("tipoVigenciaFutura").toString());
			formularioRetorno = (int) (parametrosEntrada.get(FORMULARIO_RETORNO) != null
					? parametrosEntrada.get(FORMULARIO_RETORNO)
					: formularioRetorno);
			validarAccion = extraerString(parametrosEntrada.get("accion"));

			estado = validarCadenaCampos(parametrosEntrada, "estadoPeriodo");
			parametroswf = (Map<String,Object>) parametrosEntrada.get("parametroswf");
			if(parametroswf != null) {
				varVolver = false;
				verCerrar = false;
			}
			insertar = true;
			if ("C".equals(estado)) {
				insertar = false;
			}

			if ("DIS".equals(claseComprobante) || (formularioRetorno != -1)) {
				varVolver = true;
			}

			terceroVarios = "DIS".equals(claseComprobante) || "ADD".equals(claseComprobante)
					|| "DMD".equals(claseComprobante);
		} else {
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	/**
	 * metodo que se llama al inicializar el formulario
	 */
	@PostConstruct
	public void inicializar() {
		if(parametroswf != null) {
			JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').parent().parent().css('height','800px');");
			JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').parent().parent().css('width','1830px');");
			JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').parent().parent().css('top','45px');");
			JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').parent().parent().css('left','125px');");
			
			JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').css('width','1800px');");
			JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').css('height','600px');");
		}
		enumBase = GenericUrlEnum.COMPROBANTE_PPTAL;
		buscarLlave();
		asignarOrigenDatos();
		comprobantesContPresReporteador = new ComprobantesContPresReporteador(ejbSysmanUtil);
		
		try {
			manejaVarios = (SysmanFunciones.nvl(
					ejbSysmanUtil.consultarParametro(compania, "PERMITE CREAR COMPROBANTES CON TERCERO VARIOS", modulo, new Date(), true),
					"NO").toString().equals("SI")?true:false);
		} catch(SystemException e) {
			e.printStackTrace();
		}
	}

	/**
	 * metodo que se llama al asignar el origen de datos
	 */
	@Override
	public void asignarOrigenDatos() {
		buscarUrls();
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		parametrosListado.put(ComprobantepptalsControladorEnum.ANIO.getValue(), ano);
		parametrosListado.put(ComprobantepptalsControladorEnum.STRTIPOCOMPROBANTE.getValue(), strTipoComprobante);
		parametrosListado.put(ComprobantepptalsControladorEnum.MES.getValue(), mes);
		parametrosListado.put(ComprobantepptalsControladorEnum.MODULO.getValue(), modulo);
		parametrosListado.put(ComprobantepptalsControladorEnum.PROCESO.getValue(), 1);
	}

	public void cargarListaCargo() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.DEPENDENCIA.getName(),
				registro.getCampos().get(ComprobantepptalsControladorEnum.DEPENDENCIA.getValue()));

		try {
			listaCargo = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantepptalsControladorUrlEnum.URL13557.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}
	
	
	public void cargarListaListaPlantillas() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.MODULO.getName(), modulo);
		param.put(GeneralParameterEnum.TIPO.getName(), strTipoComprobante);
		
        if ("1".equals(SessionUtil.getModulo()) || "3".equals(SessionUtil.getModulo())) {
        	
        	UrlBean urlBean = UrlServiceUtil.getInstance()
    				.getUrlServiceByUrlByEnumID(ComprobantepptalsControladorUrlEnum.URL104080.getValue());
        	listaListaPlantillas = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
    				GeneralParameterEnum.CODIGO.getName());
        }else {
        	
			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(ComprobantepptalsControladorUrlEnum.URL104076.getValue());
			listaListaPlantillas = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
					GeneralParameterEnum.CODIGO.getName());
        }
		
		
		visibleListaPlantillas = listaListaPlantillas==null?false:true;
	}
	
	/**
	 * 
	 * Carga la lista listatipoCompromiso
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListatipoCompromiso(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ComprobantepptalsControladorUrlEnum.URL2001001.getValue());
		
		listatipoCompromiso = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * metodo que se llama al carga la lista de ubicacion
	 */
	public void cargarListaubicacion() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaubicacion = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantepptalsControladorUrlEnum.URL14078.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * metodo que se llama al carga la lista de financiacion
	 */
	public void cargarListafuenteFinanciacion() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);

		try {
			listafuenteFinanciacion = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantepptalsControladorUrlEnum.URL14368.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * metodo que se llama al carga la lista de asignacion
	 */
	public void cargarListaasignacion() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaasignacion = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantepptalsControladorUrlEnum.URL14840.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * metodo que se llama al carga la lista de tipo contrato
	 */
	public void cargarListaTipoContrato() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.TERCERO.getName(),
				registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()));
		param.put(GeneralParameterEnum.SUCURSAL.getName(),
				registro.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()));

		try {
			listaTipoContrato = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantepptalsControladorUrlEnum.URL15550.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * metodo que se llama al carga la lista de referencia
	 */
	public void cargarListaReferencia() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);

		try {
			listaReferencia = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantepptalsControladorUrlEnum.URL16093.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * metodo que se llama al carga la lista auxiliar
	 */
	public void cargarListaAuxiliar() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);

		try {
			listaAuxiliar = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantepptalsControladorUrlEnum.URL16505.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * metodo que se llama al carga la lista del codigo de proyecto
	 */
	public void cargarListaCODPROYECTO() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ComprobantepptalsControladorUrlEnum.URL17912.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);

		listaCODPROYECTO = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());

	}

	/**
	 * metodo que se llama al carga la lista de dependencia
	 */
	public void cargarListaDependencia() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ComprobantepptalsControladorUrlEnum.URL18639.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaDependencia = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());

	}

	/**
	 * metodo que se llama al carga la lista del numero de contrato
	 */
	public void cargarListaNumeroContrato() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ComprobantepptalsControladorUrlEnum.URL19359.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.TIPOCONTRATO.getName(),
				registro.getCampos().get(GeneralParameterEnum.TIPOCONTRATO.getName()));
		param.put(GeneralParameterEnum.TERCERO.getName(),
				registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()));
		param.put(GeneralParameterEnum.SUCURSAL.getName(),
				registro.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()));

		param.put(GeneralParameterEnum.FECHA.getName(), registro.getCampos().get(GeneralParameterEnum.FECHA.getName()));

		param.put(ComprobantepptalsControladorEnum.CONSTANTE_TERCERO.getValue(), SysmanConstantes.CONS_TERCERO);
		listaNumeroContrato = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.NUMERO.getName());

	}

	/**
	 * metodo que se llama al carga la lista del nombre del solicitante
	 */
	public void cargarListaListaNombreSolicitante() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ComprobantepptalsControladorUrlEnum.URL19624.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.DEPENDENCIA.getName(),
				registro.getCampos().get(GeneralParameterEnum.DEPENDENCIA.getName()));

		listaListaNombreSolicitante = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(),
				param, true, ComprobantepptalsControladorEnum.CEDULA.getValue());

	}

	/**
	 * metodo que se llama al carga la lista del numero
	 */
	public void cargarListaNumero() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ComprobantepptalsControladorUrlEnum.URL21372.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(ComprobantepptalsControladorEnum.STRTIPOCOMPROBANTE.getValue(), strTipoComprobante);
		param.put(ComprobantepptalsControladorEnum.MES.getValue(), mes);

		listaNumero = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.NUMERO.getName());

	}

	/**
	 * metodo que se llama al carga la lista de tercero
	 */
	public void cargarListaTercero() {
		String parametro = obtenerParametro("MANEJA TERCERO DIFERENTE A VARIOS EN DISPONIBILIDAD", true); 
		parametro = SysmanFunciones.nvlStr(parametro, "NO");

        if(manejaVarios && 
        		!("DIS".equals(claseComprobante)||"ADD".equals(claseComprobante)||"DMD".equals(claseComprobante))) {
        	UrlBean urlBean; 
        	
        	urlBean = UrlServiceUtil.getInstance()
    				.getUrlServiceByUrlByEnumID(ComprobantepptalsControladorUrlEnum.URL16566.getValue());
        	
        	Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaTercero = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
                            ComprobantepptalsControladorEnum.CODIGO.getValue());
        } else {
        	/* Se asigna condicional para que en las clases DIS, ADD y DMD cargue todos los terceros de acuerdo al parametro*/
            if("SI".equals(parametro)) {
            	if("DIS".equals(claseComprobante)||("ADD".equals(claseComprobante)||"DMD".equals(claseComprobante))) {
            		UrlBean urlBean;
            		
            		urlBean = UrlServiceUtil.getInstance()
            				.getUrlServiceByUrlByEnumID(ComprobantepptalsControladorUrlEnum.URL14199.getValue());
            		
            		Map<String, Object> param = new TreeMap<>();
                    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

                    listaTercero = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
                                    ComprobantepptalsControladorEnum.CODIGO.getValue());
            	} else {
            		UrlBean urlBean;
            		
            		if ("30203".equals(SessionUtil.getMenuActual())) {
            			urlBean = UrlServiceUtil.getInstance()
            					.getUrlServiceByUrlByEnumID(ComprobantepptalsControladorUrlEnum.URL16566.getValue());
            		} else {
            			urlBean = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(ComprobantepptalsControladorUrlEnum.URL16567.getValue());
            		}
            		
            		Map<String, Object> param = new TreeMap<>();
                    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

                    listaTercero = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
                                    ComprobantepptalsControladorEnum.CODIGO.getValue());
                }
            } else {
            	if(!terceroVarios) {
            		UrlBean urlBean;
            		
            		if("30203".equals(SessionUtil.getMenuActual())) {
                        urlBean = UrlServiceUtil.getInstance()
                        		.getUrlServiceByUrlByEnumID(ComprobantepptalsControladorUrlEnum.URL16566.getValue());
            		} else {
                        urlBean = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(ComprobantepptalsControladorUrlEnum.URL16567.getValue());
            		}
            		Map<String, Object> param = new TreeMap<>();
                    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

                    listaTercero = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
                                    ComprobantepptalsControladorEnum.CODIGO.getValue());
            	}
            }
        }
	}
	
	

	/**
	 * metodo que se llama al carga la lista comp a copiar
	 */
	public void cargarListaCompACopiar() {

		if (registro.getCampos().get(GeneralParameterEnum.FECHA.getName()) == null) {
			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1436"));
			return;
		}
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ComprobantepptalsControladorUrlEnum.URL23367.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(ComprobantepptalsControladorEnum.ANIO.getValue(), ano);

		param.put(ComprobantepptalsControladorEnum.STRTIPOCOMPROBANTE.getValue(), strTipoComprobante);
		param.put(ComprobantepptalsControladorEnum.NUMEROS.getValue(),
				registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));
		param.put(ComprobantepptalsControladorEnum.MES.getValue(), mes);
		try {
			Date fecha = SysmanFunciones.convertirAFecha(SysmanFunciones.concatenar(
					String.valueOf(
							SysmanFunciones.dia((Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName()))),
					"/", mes.length() == 1 ? "0" + mes : mes, "/", ano));
			param.put(ComprobantepptalsControladorEnum.FECHAS.getValue(), fecha);

			listaCompACopiar = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
					GeneralParameterEnum.NUMERO.getName());
		} catch (ParseException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);
		}

	}

	/**
	 * metodo que se llama al cambiar el numero
	 */
	public void cambiarNumero() {
		// <CODIGO_DESARROLLADO>
		cargarListaCompACopiar();
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * metodo que de un dialogo
	 */
	public void aceptarCrearNuevoComp() {
		visibleCrearNuevoComp = false;
		Object objNumero = registro.getCampos().get(GeneralParameterEnum.NUMERO.getName());
		cargarRegistro(null, "i");
		registro.getCampos().put(GeneralParameterEnum.NUMERO.getName(), objNumero);
	}

	/**
	 * metodo que de un dialogo
	 */
	public void cancelarCrearNuevoComp() {
		visibleCrearNuevoComp = false;
		registro.getCampos().put(GeneralParameterEnum.NUMERO.getName(),
				registroIni.get(GeneralParameterEnum.NUMERO.getName()));
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Aceptar del dialogo ActualizarTercero en
	 * la vista
	 *
	 */
	public void aceptarActualizarTercero() {

		UrlBean updateTercero = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ComprobantepptalsControladorUrlEnum.URL16568.getValue());
		Map<String, Object> parametros = new TreeMap<>();

		try {
			parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			parametros.put(GeneralParameterEnum.TERCERO.getName(),
					registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()));

			parametros.put(GeneralParameterEnum.SUCURSAL.getName(),
					registro.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()));

			parametros.put(GeneralParameterEnum.ANO.getName(), ano);

			parametros.put(GeneralParameterEnum.TIPO.getName(), strTipoComprobante);

			parametros.put(GeneralParameterEnum.COMPROBANTE.getName(),
					registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));

			Parameter parameter = new Parameter();
			parameter.setFields(parametros);

			requestManager.update(updateTercero.getUrl(), updateTercero.getMetodo(), parameter);
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		agregarRegistroNuevo(false);
		visibleActualizarTercero = false;
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Aceptar del dialogo ActualizarTercero en
	 * la vista
	 *
	 */
	public void cancelarActualizarTercero() {
		visibleActualizarTercero = false;
	}

	/**
	 * metodo que llama al cambiar el auxiliar
	 */
	public void cambiarAuxiliar() {
		// <CODIGO_DESARROLLADO>
		if ("i".equals(accion)) {
			return;
		}

		Object oldAuxiliar = registroIni.get(GeneralParameterEnum.AUXILIAR.getName());

		if (oldAuxiliar != registro.getCampos().get(GeneralParameterEnum.AUXILIAR.getName())) {
			agregarRegistroNuevo(false);

			cambiarAuxiliarEnDetalle(registro.getCampos().get(GeneralParameterEnum.AUXILIAR.getName()).toString());
			String mensaje = idioma.getString("TB_TB902");
			mensaje = mensaje.replace("s$filas$s", Integer.toString(filas));
			mensaje = mensaje.replace("s$numero$s", SysmanFunciones
					.concatenar(registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString(), ""));
			JsfUtil.agregarMensajeInformativo(mensaje);

		}

		// </CODIGO_DESARROLLADO>
	}

	/**
	 * metodo que llama al cambiar el auxiliar del detalle
	 */
	public void cambiarAuxiliarEnDetalle(String auxiliarN) {
		HashMap<String, Object> param = new HashMap<>();
		param.put(GeneralParameterEnum.AUXILIAR.getName(), auxiliarN);
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(ComprobantepptalsControladorEnum.STRTIPOCOMPROBANTE.getValue(), strTipoComprobante);
		param.put(GeneralParameterEnum.NUMERO.getName(),
				registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));
		Parameter parameter = new Parameter();
		parameter.setFields(param);
		UrlBean urlUpdate = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ComprobantepptalsControladorUrlEnum.URL16558.getValue());
		try {
			filas = requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), parameter);
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * metodo que llama al cambiar men viaticos
	 */
	public void cambiarMENVIATICOS() {
		if ((boolean) registro.getCampos().get(ComprobantepptalsControladorEnum.MEN_VIATICOS.getValue())) {
			visibleTarifa = true;
			visiblePermanencia = true;
		} else {
			visibleTarifa = false;
			visiblePermanencia = false;
		}
	}

	/**
	 * metodo que llama al cambiar el impreso check
	 */
	public void cambiarImpreso() {
		if ((boolean) registro.getCampos().get(ComprobantepptalsControladorEnum.IMPRESO.getValue())) {
			if (!bloqTercero) {
				bloqProyecto = true;
				bloqFecha = true;
				bloqTercero = true;
				bloqDescripcion = true;
				bloqTexto = true;
				bloqVlrDocumento = true;
				bloqNroDocumento = true;
				bloqTipoCpteAfect = true;
				bloqCmpteAfectado = true;
				bloqTipoContrato = true;
				bloqNumeroContrato = true;
				bloqFechaVcnDoc = true;
				bloqDestino = true;
				bloqAsignacion = true;
				bloqFuente = true;
				bloqUbicacion = true;
				bloqDependencia = true;
				bloqCargo = true;
				bloqTxtCargoSol = true;
				bloqNombreSolicitante = true;
				bloqAuxiliar = true;
			}
		} else {
			if (bloqTercero) {
				bloqProyecto = false;
				bloqFecha = false;
				bloqTercero = false;
				bloqDescripcion = false;
				bloqTexto = false;
				bloqVlrDocumento = false;
				bloqNroDocumento = false;
				bloqTipoCpteAfect = false;
				bloqCmpteAfectado = false;
				bloqTipoContrato = false;
				bloqNumeroContrato = false;
				bloqFechaVcnDoc = false;
				bloqDestino = false;
				bloqAsignacion = false;
				bloqFuente = false;
				bloqUbicacion = false;
				bloqDependencia = false;
				bloqCargo = false;
				bloqTxtCargoSol = false;
				bloqNombreSolicitante = false;
				bloqAuxiliar = false;
			}
		}
	}

	/**
	 * metodo que llama al cambiar el tipo de contrato
	 */
	public void cambiarTipoContrato() {
		cargarListaNumeroContrato();
		registro.getCampos().put(ComprobantepptalsControladorEnum.NUMEROCONTRATO.getValue(), "");
	}

	/**
	 * metodo que llama al cambiar la fecha a liberar
	 */
	public void cambiarFechaALiberar() {
		// <CODIGO_DESARROLLADO>
		try {
			Date primeroDeAno = SysmanFunciones.convertirAFecha("01/01/" + ano);
			Date ultimoDeAno = SysmanFunciones
					.convertirAFecha(ComprobantepptalsControladorEnum.CTE3112.getValue() + ano);
			if (primeroDeAno.after(fechaLiberacion)) {
				fechaLiberacion = (Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName());
				JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1194"));
			} else if (ultimoDeAno.before(fechaLiberacion)) {
				fechaLiberacion = ultimoDeAno;
				JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1194"));

			}
		} catch (ParseException e) {
			Logger.getLogger(ComprobantepptalsControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		if (fechaLiberacion.before((Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName()))) {
			fechaLiberacion = (Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName());
			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1193"));
		}
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * metodo que llama al cambiar la fecha
	 */
	public void cambiarFecha() {
		// <CODIGO_DESARROLLADO>

		if (registro.getCampos().get(GeneralParameterEnum.FECHA.getName()) == null) {
			registro.getCampos().put(ComprobantepptalsControladorEnum.FECHA_VCN_DOC.getValue(), null);
			return;
		}
		mensajeVerificarComprobantes();
		cargarListaNumeroContrato();
		registro.getCampos().put(ComprobantepptalsControladorEnum.NUMEROCONTRATO.getValue(), "");
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * metodo que llama al reemplazar el mensaje
	 */
	private void mensajeVerificarComprobantes() {
		try {
			if ((registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()) != null)
					&& ejbPresupuestoTres.configurarParametroDisRes(compania, Integer.parseInt(ano), strTipoComprobante,
							new BigInteger(registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString()),
							(Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName()), claseComprobante,
							Integer.parseInt(modulo))) {
				String mensaje = idioma.getString("TB_TB1100");
				mensaje = mensaje.replace("s$tipo$s",
						registro.getCampos().get(ComprobantepptalsControladorEnum.TIPO.getValue()).toString());

				mensaje = mensaje.replace("s$fecha$s", SysmanFunciones
						.convertirAFechaCadena((Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName())));
				JsfUtil.agregarMensajeError(mensaje);

				registro.getCampos().put(GeneralParameterEnum.FECHA.getName(),
						registroIni.get(GeneralParameterEnum.FECHA.getName()));

			}
		} catch (NumberFormatException | SystemException |

				ParseException e) {
			Logger.getLogger(ComprobantepptalsControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * Actualiza la fecha en los detalles del comprobante cuando se modifica en el
	 * Header
	 */
	public void actualizarFecha() {
	    
	    if (!"MOP".equals(claseComprobante)) {
	    
		try {
			UrlBean urlUpdate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(ComprobantepptalsControladorUrlEnum.URL22979.getValue());

			Map<String, Object> fields = new TreeMap<>();

			fields.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			fields.put(GeneralParameterEnum.ANO.getName(), ano);
			fields.put(GeneralParameterEnum.TIPO.getName(), strTipoComprobante);
			fields.put(GeneralParameterEnum.COMPROBANTE.getName(),
					registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));
			fields.put(GeneralParameterEnum.FECHA.getName(), SysmanFunciones
					.convertirAFechaCadena((Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName())));

			fields.put(GeneralParameterEnum.MODIFIED_BY.getName(), SessionUtil.getUser().getCodigo());
			fields.put(GeneralParameterEnum.DATE_MODIFIED.getName(), SysmanFunciones.convertirAFechaCadena(new Date()));
			Parameter parameter = new Parameter();
			parameter.setFields(fields);

			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), parameter);
		} catch (SystemException | ParseException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		
	   }

	}

	/**
	 * metodo que llama al seleccionar un registro de un combo grande
	 */
	public void seleccionarFilaNumeroContrato(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(ComprobantepptalsControladorEnum.NUMEROCONTRATO.getValue(),
				registroAux.getCampos().get(GeneralParameterEnum.NUMERO.getName()));
		if (SysmanFunciones.nvl(registro.getCampos().get(ComprobantepptalsControladorEnum.TEXTO.getValue()), "")
				.toString().trim().isEmpty()) {
			registro.getCampos().put(ComprobantepptalsControladorEnum.TEXTO.getValue(),
					registroAux.getCampos().get(ComprobantepptalsControladorEnum.OBJETOCONTRATO.getValue()));
		}
	}
	
	public void seleccionarFilaListaPlantillas(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		plantilla = SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
        nombrePlantilla =  SysmanFunciones.toString(registroAux.getCampos().get("NOMBRE"));
        fechaPlantilla = (Date) registroAux.getCampos().get("FECHA");
        
        visiblePresentarPlantillas = plantilla==null?false:true;
	}
	
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listatipoCompromiso
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilatipoCompromiso(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(ComprobantepptalsControladorEnum.TIPO_COMPROMISO.getValue(), 
				registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
		registro.getCampos().put(ComprobantepptalsControladorEnum.NOMBRE_COMPROMISO.getValue(), 
				registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
	}

	/**
	 * metodo que llama al seleccionar un registro de un combo grande
	 */
	public void seleccionarFilaNumero(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(GeneralParameterEnum.NUMERO.getName(),
				registroAux.getCampos().get(GeneralParameterEnum.NUMERO.getName()));
	}

	/**
	 * metodo que llama al seleccionar un registro de un combo grande
	 */
	public void seleccionarFilaTercero(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		if (!registroAux.getCampos().isEmpty()) {
			try {

				boolean miRpta = ejbPresupuestoTres.terceroRegistraEmbargo(compania, Integer.parseInt(modulo),
						registroAux.getCampos().get(ComprobantepptalsControladorEnum.NIT.getValue()).toString(),
						registroAux.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()).toString(),
						strTipoComprobante);
				if (miRpta) {
					JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1230"));
				}
			} catch (NumberFormatException | SystemException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
		}
		registro.getCampos().put(GeneralParameterEnum.TERCERO.getName(),
				registroAux.getCampos().get(ComprobantepptalsControladorEnum.NIT.getValue()));
		strNombreTercero = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()), "")
				.toString();
		registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
				registroAux.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()));
		if (!registroAux.getCampos().isEmpty()) {
			cargarListaTipoContrato();
		} else {
			listaTipoContrato = null;
		}

		if (accion.equals(ACCION_MODIFICAR)) {
			visibleActualizarTercero = true;
		}
	}

	/**
	 * metodo que llama para obtener el parametro
	 */
	private String obtenerParametro(String parametro, boolean mayMe) {
		try {
			return ejbSysmanUtil.consultarParametro(compania, parametro, modulo, new Date(), mayMe);
		} catch (SystemException e) {

			Logger.getLogger(ComprobantepptalsControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return null;
	}

	/**
	 * metodo que llama al seleccionar un registro de un combo grande
	 */
	public void seleccionarFilaCompACopiar(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		compACopiar = registroAux.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString();
		if ("i".equals(accion)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString(ComprobantepptalsControladorEnum.TB_TB876.getValue()));
			return;
		}
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ComprobantepptalsControladorUrlEnum.URL16544.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(ComprobantepptalsControladorEnum.STRTIPOCOMPROBANTE.getValue(), strTipoComprobante);
		param.put(GeneralParameterEnum.NUMERO.getName(),
				registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));
		int conteo;
		try {
			conteo = Integer.parseInt(requestManager.get(urlBean.getUrl(), param).getFields()
					.get(ComprobantepptalsControladorEnum.CONTEO.getValue()).toString());
			if (conteo > 0) {
				// El comprobante ya tiene imputacion presupuestal
				JsfUtil.agregarMensajeError(idioma.getString(ComprobantepptalsControladorEnum.TB_TB895.getValue()));
				return;
			}
			validarValoresEnCero(registroAux);
		} catch (NumberFormatException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * metodo que llama al validar los valores en cero
	 */
	private void validarValoresEnCero(Registro registroAux) {
		// El sistema realiza copia con valores en cero por
		// control.
		JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB896"));

		registro.getCampos().put(GeneralParameterEnum.DESCRIPCION.getName(),
				SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.DESCRIPCION.getName()), ""));
		registro.getCampos().put(ComprobantepptalsControladorEnum.TEXTO.getValue(), SysmanFunciones
				.nvl(registroAux.getCampos().get(ComprobantepptalsControladorEnum.TEXTO.getValue()), ""));
		registro.getCampos().put(ComprobantepptalsControladorEnum.NRO_DOCUMENTO.getValue(), SysmanFunciones
				.nvl(registroAux.getCampos().get(ComprobantepptalsControladorEnum.NRO_DOCUMENTO.getValue()), ""));
		registro.getCampos().put(CTEVLR_DOCUMENTO, 0);
		registro.getCampos().put(GeneralParameterEnum.REFERENCIA.getName(),
				SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.REFERENCIA.getName()), ""));
		registro.getCampos().put(GeneralParameterEnum.TIPOCONTRATO.getName(),
				SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.TIPOCONTRATO.getName()), ""));
		registro.getCampos().put(ComprobantepptalsControladorEnum.NUMEROCONTRATO.getValue(), SysmanFunciones
				.nvl(registroAux.getCampos().get(ComprobantepptalsControladorEnum.NUMEROCONTRATO.getValue()), ""));
		registro.getCampos().put(ComprobantepptalsControladorEnum.TIPO_DOCUMENTO.getValue(), SysmanFunciones
				.nvl(registroAux.getCampos().get(ComprobantepptalsControladorEnum.TIPO_DOCUMENTO.getValue()), ""));
		registro.getCampos().put(ComprobantepptalsControladorEnum.FECHA_VCN_DOC.getValue(), SysmanFunciones
				.nvl(registroAux.getCampos().get(ComprobantepptalsControladorEnum.FECHA_VCN_DOC.getValue()), ""));
		registro.getCampos().put(ComprobantepptalsControladorEnum.DESTINO.getValue(), SysmanFunciones
				.nvl(registroAux.getCampos().get(ComprobantepptalsControladorEnum.DESTINO.getValue()), ""));
		registro.getCampos().put(GeneralParameterEnum.DEPENDENCIA.getName(),
				SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.DEPENDENCIA.getName()), ""));
		registro.getCampos().put(GeneralParameterEnum.CARGO.getName(),
				SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CARGO.getName()), ""));

		if (SysmanFunciones.validarCampoVacio(registro.getCampos(), GeneralParameterEnum.TERCERO.getName())) {
			registro.getCampos().put(GeneralParameterEnum.TERCERO.getName(),
					SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.TERCERO.getName()), ""));
			registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
					SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()), ""));
		}

		copiaImputacionPresupuestal();

		agregarRegistroNuevo(false);
		compACopiar = "";
		bloqCompACopiar = true;
	}

	/**
	 * @author gfigueredo
	 * 
	 *         Mï¿½todo encargado de ejecutar el procedimiento
	 *         PCK_PRESUPUESTO2.PR_COMPROBANTEACOPIAR el cual realiza copia del
	 *         comprobante y la imputacion presupuestal (detalles comprobante)
	 */
	public void copiaImputacionPresupuestal() {

		try {
			ejbPresupuestoTres.comprobanteAcopiar(compania,
					Integer.parseInt(registro.getCampos().get("ANO").toString()), strTipoComprobante,
					BigInteger.valueOf(Integer.parseInt(registro.getCampos().get(CAMPO_NUMERO).toString())),
					registro.getCampos().get(CAMPO_TERCERO).toString(),
					registro.getCampos().get(CAMPO_SUCURSAL).toString(), (Date) registro.getCampos().get("FECHA"),
					BigInteger.valueOf(Integer.parseInt(compACopiar)), usuario);
		} catch (SystemException e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * metodo que llama al seleccionar un registro de un combo grande
	 */
	public void seleccionarFilaListaNombreSolicitante(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(ComprobantepptalsControladorEnum.CODSOLICITANTE.getValue(),
				registroAux.getCampos().get(ComprobantepptalsControladorEnum.CEDULA.getValue()));
		registro.getCampos().put(ComprobantepptalsControladorEnum.NOMBRESOLICITANTE.getValue(),
				registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
		registro.getCampos().put(ComprobantepptalsControladorEnum.SUCSOLICITANTE.getValue(),
				registroAux.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()));
		registro.getCampos().put(GeneralParameterEnum.CARGO.getName(),
				registroAux.getCampos().get(GeneralParameterEnum.CARGO.getName()));
	}

	/**
	 * metodo que llama al seleccionar un registro de un combo grande
	 */
	public void seleccionarFilaDependencia(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(GeneralParameterEnum.DEPENDENCIA.getName(),
				registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
		registro.getCampos().put(ComprobantepptalsControladorEnum.NOMBRESOLICITANTE.getValue(), null);
		registro.getCampos().put(ComprobantepptalsControladorEnum.CODSOLICITANTE.getValue(), null);
		registro.getCampos().put(ComprobantepptalsControladorEnum.SUCSOLICITANTE.getValue(), null);
		registro.getCampos().put(GeneralParameterEnum.CARGO.getName(), null);
		cargarListaListaNombreSolicitante();
		cargarListaCargo();
	}

	/**
	 * metodo que llama al seleccionar un registro de un combo grande
	 */
	public void seleccionarFilaCODPROYECTO(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(ComprobantepptalsControladorEnum.COD_PROYECTO_PPTAL.getValue(),
				registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
		nombreProyecto = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()), "")
				.toString();
		registro.getCampos().put(ComprobantepptalsControladorEnum.ANOPROYECTO.getValue(),
				registroAux.getCampos().get(GeneralParameterEnum.ANO.getName()));
	}

	/**
	 * metodo que llama al oprimir el boton pdf
	 */
	public void oprimirPresentar() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generaInforme(FORMATOS.PDF);
		// </CODIGO_DESARROLLADO>
	}
	
	
	private void generarPdfdesdeWord() {
//		 TODO Auto-generated method stub
		 Map<String, Object> param = new HashMap<>();
	        param.put("s$compania$s", compania);
	        param.put("s$usuario$s", SessionUtil.getUser().getCodigo());
	        String[] campos = new String[4];

	        String[] valores = new String[4];
	        campos[0] = "codigoPlantilla";
	        campos[1] = "fechaPlantilla";
	        campos[2] = "nombreDocDescarga";
	        campos[3] = "tipoCpte";

	        valores[0] = plantilla;
	        valores[1] = SysmanFunciones.formatearFecha(fechaPlantilla);
	        valores[2] = nombrePlantilla;
	        valores[3] = claseComprobante;

	        HashMap<String, String> variablesConsultaW = new HashMap<>();
	        variablesConsultaW.put("s$compania$s", "'" + compania + "'");
	        variablesConsultaW.put("s$ano$s", ano);
	        variablesConsultaW.put("s$tipo$s",  "'" +  strTipoComprobante + "'");
	        variablesConsultaW.put("s$numeroIni$s",registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString());
	        variablesConsultaW.put("s$numeroFin$s",registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString());
	        // variables por parametro para documento word
	        SessionUtil.setSessionVar("variablesConsultaWord", variablesConsultaW);

	        SessionUtil.cargarModalDatosFlash(
	                        Integer.toString(
	                                        GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
	                                                        .getCodigo()),
	                        SessionUtil.getModulo(),
	                        campos,
	                        valores);

	}

	/**
	 * metodo que llama al oprimir el boton pdf
	 */
	public void oprimirPresentarPlantillas() {
		// <CODIGO_DESARROLLADO>
		if (plantilla == null) {
			generaInforme(FORMATOS.PDF);
		}else {
			generarPdfdesdeWord();
		}
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * metodo que llama al oprimir el boton de la impresora.
	 */
	public void impresoraClick() {
		String parManejaDependencia;
		String dependencia;
		if ("DIS".equals(claseComprobante)) {

			parManejaDependencia = obtenerParametro("MANEJA DEPENDENCIA SOLICITANTE EN DIS", true);
			parManejaDependencia = SysmanFunciones.nvlStr(parManejaDependencia, "NO");
			dependencia = SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.DEPENDENCIA.getName()), "")
					.toString();
			if ("SI".equals(parManejaDependencia) && "".equals(dependencia)) {
				JsfUtil.agregarMensajeError(idioma.getString("TB_TB1119"));
				return;
			}

		}
		boolean impreso = (Boolean) registro.getCampos().get(ComprobantepptalsControladorEnum.IMPRESO.getValue());
		if (!impreso) {
			if (!"0".equals(SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()), "0")
					.toString())) {
				formatoComprobante = SysmanFunciones.nvlStr(formatoComprobante,
						ComprobantepptalsControladorEnum.REP000958CDP.getValue());
			}
		} else {
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB1120"));
		}

	}

	/**
	 * metodo que llama al oprimir el boton excel
	 */
	public void oprimirExcel() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generaInforme(FORMATOS.EXCEL97);
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * metodo que tiene la logica para generar un reporte en formato pdf o excel
	 */
	public void generaInformeCDP(FORMATOS formato) {
		if ("i".equals(accion)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString(ComprobantepptalsControladorEnum.TB_TB876.getValue()));
			return;
		}

		try {
			HashMap<String, Object> reemplazar = new HashMap<>();
			Map<String, Object> parametros = new HashMap<>();
			reemplazar.put("ano", ano);
			reemplazar.put("tipoCpte", strTipoComprobante);
			reemplazar.put("fecha", SysmanFunciones
					.formatearFecha((Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName())));

			reemplazar.put("numeroPptoInicial", registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));
			reemplazar.put("numeroPptoFinal", registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));

			String parametro;
			parametro = SysmanFunciones.nvlStr(obtenerParametro("CARGO PRESUPUESTO", true), " ");
			parametros.put(ComprobantepptalsControladorEnum.PR_CARGO_PRESUPUESTO.getValue(), parametro);

			parametro = SysmanFunciones.nvlStr(obtenerParametro("NOMBRE DE JEFE DE PRESUPUESTO", true), " ");

			parametros.put(ComprobantepptalsControladorEnum.PR_NOMJEFE_PRESUPUESTO.getValue(), parametro);
			
			parametros.put("PR_NOMBRE_DE_JEFE_DE_PRESUPUESTO", parametro);
			
			parametro = SysmanFunciones.nvlStr(obtenerParametro("OCULTAR DATOS ELABORADOR EN CDP", true), "NO");

			parametros.put("PR_ELABORO", "NO".equals(parametro) ? "Elaboro:" : " ");
			parametros.put("PR_NOMBREUSUARIO", "NO".equals(parametro) ? SessionUtil.getUser().getCodigo() : " ");

			parametro = SysmanFunciones.nvlStr(obtenerParametro("MOSTRAR CONCEPTO DEL REGISTRO AFECTADO", true), "NO");

			parametros.put("PR_MOSTRARCONCEPTO", "SI".equals(parametro));

			parametro = SysmanFunciones.nvlStr(obtenerParametro("MANEJA NOMBRE EN MOVIMIENTOS PRESUPUESTALES", true),
					"NO");

			parametros.put("PR_MANEJANOMBRE", "SI".equals(parametro));

			parametro = SysmanFunciones.nvlStr(obtenerParametro("MOSTRAR FIRMA DE SUBDIRECTORA EN FORMATO CDP", true),
					"NO");

			parametros.put("PR_MOSTRARFIRMA", "SI".equals(parametro));

			parametro = SysmanFunciones.nvlStr(obtenerParametro("TEXTO DE VENCIMIENTO EN FORMATO CDP", false), " ");

			parametros.put("PR_VISIBLEVENCIMIENTO", !parametro.isEmpty());

			parametros.put("PR_TEXTO_DE_VENCIMIENTO_EN_FORMATO_CDP", parametro);

			parametro = SysmanFunciones
					.nvlStr(obtenerParametro("NOMBRE SUBDIRECTOR ADMINISTRATIVO Y FINANCIERO", false), " ");

			parametros.put("PR_NOMBRE_SUBDIRECTOR_ADMINISTRATIVO_Y_FINANCIERO", parametro);

			parametro = SysmanFunciones.nvlStr(obtenerParametro("NOMBRE DEL CARGO SUBDIRECTOR ADMINISTRATIVO", true),
					" ");

			parametros.put("PR_NOMBRE_DEL_CARGO_SUBDIRECTOR_ADMINISTRATIVO", parametro);
			
			//1397_PRESUPUESTO
			parametro = SysmanFunciones.nvlStr(obtenerParametro("ELABORO EN PRESUPUESTO", true)," ");
			parametros.put("PR_ELABORO_EN_PRESUPUESTO", parametro);	
			
			parametro = SysmanFunciones.nvlStr(obtenerParametro("CARGO ELABORO EN PRESUPUESTO", true)," ");
			parametros.put("PR_CARGO_ELABORO_EN_PRESUPUESTO", parametro);		
			
			parametro = SysmanFunciones.nvlStr(obtenerParametro("APROBO EN PRESUPUESTO", true)," ");
			parametros.put("PR_APROBO_EN_PRESUPUESTO", parametro);	
			
			parametro = SysmanFunciones.nvlStr(obtenerParametro("CARGO APROBO EN PRESUPUESTO", true)," ");
			parametros.put("PR_CARGO_APROBO_EN_PRESUPUESTO", parametro);	
			
			parametro = SysmanFunciones.nvlStr(obtenerParametro("REVISO EN PRESUPUESTO", true)," ");
			parametros.put("PR_REVISO_EN_PRESUPUESTO", parametro);	
			
			parametro = SysmanFunciones.nvlStr(obtenerParametro("CARGO REVISO EN PRESUPUESTO", true)," ");
			parametros.put("PR_CARGO_REVISO_EN_PRESUPUESTO", parametro);	
			
			
			

			Reporteador.resuelveConsulta(ComprobantepptalsControladorEnum.REP000958CDP.getValue(),
					Integer.valueOf(modulo), reemplazar, parametros);

			Long contarRegistro = service.getConteoConsulta(
					parametros.get(ComprobantepptalsControladorEnum.PR_STRSQL.getValue()).toString());
			if (contarRegistro <= 0) {
				JsfUtil.agregarMensajeAlerta(
						idioma.getString(ComprobantepptalsControladorEnum.TG_NO_EXISTE.getValue()));
			} else {
				archivoDescarga = JsfUtil.exportarStreamed(ComprobantepptalsControladorEnum.REP000958CDP.getValue(),
						parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
				registro.getCampos().put(ComprobantepptalsControladorEnum.IMPRESO.getValue(), true);
			}
		} catch (FileNotFoundException ex) {
			JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
					idioma.getString(ComprobantepptalsControladorEnum.MSM_INFORME_NO_EXISTE.getValue()), " ",
					ex.getMessage(), " ", ComprobantepptalsControladorEnum.REP000958CDP.getValue()));
			Logger.getLogger(ComprobantepptalsControlador.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException | JRException e) {
			Logger.getLogger(ComprobantepptalsControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
					idioma.getString(ComprobantepptalsControladorEnum.MSM_TRANS_INTERRUMPIDA.getValue()), " - ",
					e.getMessage()));
		} catch (SystemException | SysmanException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * metodo que tiene la logica para generar un reporte
	 */
	public void generaInforme(FORMATOS formato) {
		if (ComprobantepptalsControladorEnum.REP000958CDP.getValue().equals(formatoComprobante)) {
			generaInformeCDP(formato);
			return;
		}

		HashMap<String, Object> reemplazar = new HashMap<>();
		Map<String, Object> parametros = new HashMap<>();

		reemplazar.put("ano", ano);
		reemplazar.put("tipoCpte", strTipoComprobante);
		reemplazar.put("fecha",
				SysmanFunciones.formatearFecha((Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName())));
		reemplazar.put("mes",
				SysmanFunciones.mes((Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName())));

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put("TIPO", strTipoComprobante);
		param.put(GeneralParameterEnum.NUMERO.getName(),
				registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));

		reemplazar.put("numeroPptoInicial", registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));
		reemplazar.put("numeroPptoFinal", registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));

		parametros.put("PR_ANO", ano);
		// 07/06/2018
		String parametro;

		parametro = SysmanFunciones.nvlStr(obtenerParametro("TEXTO EN FORMATO DIS_IN", true), " ");
		parametros.put("PR_TEXTO_EN_FORMATO_DIS_IN", parametro);
		
		parametro = SysmanFunciones.nvlStr(obtenerParametro("CARGO PRESUPUESTO DISPONIBILIDAD", true), " ");
		parametros.put("PR_CARGO_PRESUPUESTO_DISPONIBILIDAD", parametro);

		parametro = SysmanFunciones.nvlStr(obtenerParametro("TITULO COMPANIA", true), " ");
		parametros.put("PR_TITULO_COMPANIA", parametro);

		parametro = SysmanFunciones.nvlStr(obtenerParametro("RESPONSABLES DE PRESUPUESTO", true), " ");
		parametros.put("PR_RESPONSABLES_DE_PRESUPUESTO", parametro);

		parametro = SysmanFunciones.nvlStr(obtenerParametro("CARGO DE SECRETARIA DE HACIENDA", true), " ");

		parametros.put(ComprobantepptalsControladorEnum.PR_CARGO_SECRETARIA_HACIENDA.getValue(), parametro);

		parametro = SysmanFunciones.nvlStr(obtenerParametro("NOMBRE DE SECRETARIA DE HACIENDA", true), " ");

		parametros.put(ComprobantepptalsControladorEnum.PR_NOMBRE_SECRETARIA_HACIENDA.getValue(), parametro);
		
		parametro = SysmanFunciones.nvlStr(obtenerParametro("SECRETARIA DE HACIENDA", true), " ");

		parametros.put(ComprobantepptalsControladorEnum.PR_SECRETARIA_HACIENDA.getValue(), parametro);

		parametro = SysmanFunciones.nvlStr(obtenerParametro("NOMBRE COLUMNA FUENTE", true), " ");

		parametros.put(ComprobantepptalsControladorEnum.PR_NOMBRE_COLUMNA_FUENTE.getValue(), parametro);

		parametro = SysmanFunciones.nvlStr(obtenerParametro("NOMBRE COLUMNA CENTRO COSTO", true), " ");

		parametros.put(ComprobantepptalsControladorEnum.PR_NOMBRE_COLUMNA_CENTRO_COSTO.getValue(), parametro);

		parametro = SysmanFunciones.nvlStr(obtenerParametro("NOMBRE COLUMNA AUXILIAR GENERAL", true), " ");

		parametros.put(ComprobantepptalsControladorEnum.PR_NOMBRE_COLUMNA_AUXILIAR_GENERAL.getValue(), parametro);

		parametro = SysmanFunciones.nvlStr(obtenerParametro("NOMBRE COLUMNA REFERENCIA", true), " ");

		parametros.put(ComprobantepptalsControladorEnum.PR_NOMBRE_COLUMNA_REFERENCIA.getValue(), parametro);

		parametro = SysmanFunciones.nvlStr(obtenerParametro("NOMBRE COLUMNA COD PPTAL", true), " ");

		parametros.put(ComprobantepptalsControladorEnum.PR_NOMBRE_COLUMNA_COD_PPTAL.getValue(), parametro);

		parametro = SysmanFunciones.nvlStr(obtenerParametro("VISTO BUENO", false), " ");

		parametros.put(ComprobantepptalsControladorEnum.PR_VISTO_BUENO.getValue(), parametro);
		
		parametro = SysmanFunciones.nvlStr(obtenerParametro("TEXTO DE VENCIMIENTO EN FORMATO CDP", true)," ");
		parametros.put(ComprobantepptalsControladorEnum.PR_TEXTO_VENCIMIENTO_FORMATO_CDP.getValue(), parametro);
		
		parametro = SysmanFunciones.nvlStr(obtenerParametro("CARGO 1 EN PRESUPUESTO", true), " ");
		parametros.put("PR_CARGO_1_EN_PRESUPUESTO", parametro);
		
		parametro = SysmanFunciones.nvlStr(obtenerParametro("CARGO PRESUPUESTO EN FORMATO 002887DISCOS", true), " ");
		parametros.put("PR_CARGO_PRESUPUESTO0028", parametro);

		// 07/06/2018
		Map<String, Object> valores = new HashMap<>();
		valores.put("informe", formatoComprobante);
		valores.put("formato", formato);
		valores.put("lote", false);
		archivoDescarga = comprobantesContPresReporteador.generarInforme(valores, parametros, reemplazar);
		registro.getCampos().put(ComprobantepptalsControladorEnum.IMPRESO.getValue(), true);

	}

	/**
	 * metodo que valida el casteo a toString
	 *
	 * @param campos
	 * @param var
	 * @return
	 */
	private String cadenaVacia(Registro campos, String var) {
		return SysmanFunciones.validarCampoVacio(campos.getCampos(), var) ? null
				: campos.getCampos().get(var).toString();
	}

	/**
	 * Metodo que se ejecuta al hacer clic en el boton Imputacion presupuestal.
	 */
	public void oprimirVerDetalle() {
		// <CODIGO_DESARROLLADO>
		if ("i".equals(accion)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString(ComprobantepptalsControladorEnum.TB_TB876.getValue()));
			return;
		}
		String validarDependencia;

		validarDependencia = SysmanFunciones.nvlStr(obtenerParametro("MANEJA DEPENDENCIA SOLICITANTE EN DIS", true),
				"NO");

		if ("DIS".equals(claseComprobante) && "SI".equals(validarDependencia) && SysmanFunciones
				.validarCampoVacio(registro.getCampos(), GeneralParameterEnum.DEPENDENCIA.getName())) {
			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB877"));
			return;
		}

		validarAplazamiento();

		String[] campos = { "rid", "ano", "mes", ComprobantepptalsControladorEnum.TIPOCOMPROBANTE.getValue(),
				ComprobantepptalsControladorEnum.NUMEROCOMPROBANTE.getValue(),
				ComprobantepptalsControladorEnum.CTENOMBRECOMPROBANTE.getValue(),
				ComprobantepptalsControladorEnum.CTECLASECOMPROBANTE.getValue(),
				ComprobantepptalsControladorEnum.CTEFORMATOCOMPROBANTE.getValue(),
				ComprobantepptalsControladorEnum.CTEPIDETERCERO.getValue(), "tipoDeCuentas",
				ComprobantepptalsControladorEnum.CLASEAFECTAR.getValue(), "columna", "afectacion", "impreso",
				"estadoPeriodo", "visiblePapeles", "debitoComprobante", "creditoComprobante", "tipoContrato",
				"numeroContrato", ComprobantepptalsControladorEnum.FECHACOMPROBANTE.getValue(),
				ComprobantepptalsControladorEnum.TERCEROCOMPROBANTE.getValue(),
				ComprobantepptalsControladorEnum.SUCURSALCOMPROBANTE.getValue(), "nomTerComprobante",
				"papelesComprobante", "contractualComprobante",
				ComprobantepptalsControladorEnum.DESCRIPCIONCOMPROBANTE.getValue(), "tipoDocComprobante",
				"nroDocComprobante", "tipoVigenciaFutura", "tipoAfectComprobante", FORMULARIO_RETORNO,
				"obligaAfectacion", "ingreso", "ridContable", "tipoPptal", "tipoCont", "accion", "parametroswf" };
		Object[] valores = { ingreso ? rid : css, ano, mes, strTipoComprobante,
				cadenaVacia(registro, GeneralParameterEnum.NUMERO.getName()), nombreComprobante, claseComprobante,
				formatoComprobante, pideTercero, tipoDeCuentas, claseAfectar, columna, afectacion,
				registro.getCampos().get(ComprobantepptalsControladorEnum.IMPRESO.getValue()), estadoPeriodo,
				visiblePapeles,
				SysmanFunciones.nvlDbl(registro.getCampos().get(ComprobantepptalsControladorEnum.DEBITO.getValue()), 0),
				SysmanFunciones
						.nvlDbl(registro.getCampos().get(ComprobantepptalsControladorEnum.CREDITO.getValue()), 0),
				registro.getCampos().get(ComprobantepptalsControladorEnum.TIPOCONTRATO.getValue()),
				SysmanFunciones
						.nvlStr(cadenaVacia(registro, ComprobantepptalsControladorEnum.NUMEROCONTRATO.getValue()), "0")
						.trim(),
				SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.FECHA.getName()), ""),
				SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()), ""),
				SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()), "").toString(),
				SysmanFunciones.nvl(registro.getCampos().get(ComprobantepptalsControladorEnum.NOMBRETERCERO.getValue()),
						""),
				SysmanFunciones.nvl(registro.getCampos().get(ComprobantepptalsControladorEnum.PAPELES.getValue()),
						false),
				SysmanFunciones.nvl(registro.getCampos().get(ComprobantepptalsControladorEnum.CONTRACTUAL.getValue()),
						false),
				SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.DESCRIPCION.getName()), ""),
				SysmanFunciones
						.nvl(registro.getCampos().get(ComprobantepptalsControladorEnum.TIPO_DOCUMENTO.getValue()), ""),
				SysmanFunciones
						.nvl(registro.getCampos().get(ComprobantepptalsControladorEnum.NRO_DOCUMENTO.getValue()), ""),
				tipoVigenciaFutura,
				SysmanFunciones
						.nvl(registro.getCampos().get(ComprobantepptalsControladorEnum.TIPO_CPTE_AFECT.getValue()), ""),
				formularioRetorno,
				registro.getCampos().get(ComprobantepptalsControladorEnum.OBLIGAAFECTACION.getValue()), ingreso,
				ridContable, strTipoComprobante, tipoCont, accion, parametroswf };
		if (css != null && ACCION_MODIFICAR.equals(accion)) {
			agregarRegistroNuevo(false);
		}
		SessionUtil.redireccionar("/subdetallecomprobantepptal.sysman", campos, valores);

		// </CODIGO_DESARROLLADO>
	}

	private void validarAplazamiento() {
		if ("APL".equals(claseComprobante) || "A1L".equals(claseComprobante) || "D1L".equals(claseComprobante)) {
			tipoDeCuentas = "D";
		}

	}

	/**
	 * metodo que llama al oprimir el boton documento a afectar
	 */
	public void oprimirDocumentoAAfectar() {
		// <CODIGO_DESARROLLADO>
		if ("i".equals(accion)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString(ComprobantepptalsControladorEnum.TB_TB876.getValue()));
			return;
		}
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ComprobantepptalsControladorUrlEnum.URL16544.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(ComprobantepptalsControladorEnum.STRTIPOCOMPROBANTE.getValue(), strTipoComprobante);
		param.put(GeneralParameterEnum.NUMERO.getName(),
				registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));

		int conteo;

		try {
			conteo = Integer.parseInt(requestManager.get(urlBean.getUrl(), param).getFields()
					.get(ComprobantepptalsControladorEnum.CONTEO.getValue()).toString());
			if (conteo > 0) {
				JsfUtil.agregarMensajeInformativo(
						idioma.getString(ComprobantepptalsControladorEnum.TB_TB895.getValue()));
				return;
			}
			String[] campos = { "rid", "ano", "mes", ComprobantepptalsControladorEnum.TIPOCOMPROBANTE.getValue(),
					ComprobantepptalsControladorEnum.NUMEROCOMPROBANTE.getValue(),
					ComprobantepptalsControladorEnum.CTENOMBRECOMPROBANTE.getValue(),
					ComprobantepptalsControladorEnum.CTECLASECOMPROBANTE.getValue(),
					ComprobantepptalsControladorEnum.CTEFORMATOCOMPROBANTE.getValue(),
					ComprobantepptalsControladorEnum.CTEPIDETERCERO.getValue(), "claseAfectar",
					ComprobantepptalsControladorEnum.TERCEROCOMPROBANTE.getValue(),
					ComprobantepptalsControladorEnum.SUCURSALCOMPROBANTE.getValue(),
					ComprobantepptalsControladorEnum.FECHACOMPROBANTE.getValue(), "afectacion" };
			Object[] valores = { css, ano, mes, strTipoComprobante,
					registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString(), nombreComprobante,
					claseComprobante, formatoComprobante, pideTercero, claseAfectar,
					registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()).toString(),
					registro.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()).toString(),
					registro.getCampos().get(GeneralParameterEnum.FECHA.getName()), afectacion };
			SessionUtil.cargarModalDatosFlashCerrar(
					String.valueOf(GeneralCodigoFormaEnum.PEDIR_DOCUMENTO_AFECTAR_CONTROLADOR.getCodigo()), modulo,
					campos, valores);
		} catch (NumberFormatException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		// </CODIGO_DESARROLLADO>
	}

	/**
	 * metodo que llama al oprimir el boton generar registro
	 */
	public void oprimirGenerarRegistro() {
		// <CODIGO_DESARROLLADO>

		if ("i".equals(accion)) {
			JsfUtil.agregarMensajeError(idioma.getString(ComprobantepptalsControladorEnum.TB_TB876.getValue()));
			return;
		}

		if ((boolean) registro.getCampos().get(ComprobantepptalsControladorEnum.REGISTROAUTOMATICO.getValue())) {
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB903"));
			return;
		}

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.name(), compania);
		param.put(GeneralParameterEnum.ANO.name(), ano);
		param.put(GeneralParameterEnum.TIPO_CPTE.getName(), strTipoComprobante);
		param.put(GeneralParameterEnum.COMPROBANTE.name(),
				SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()), "").toString());
		UrlBean urlRs = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ComprobantepptalsControladorUrlEnum.URL16565.getValue());

		Registro rs;

		try {
			rs = RegistroConverter.toRegistro(requestManager.get(urlRs.getUrl(), param));
			if (Integer
					.valueOf(rs.getCampos().get(ComprobantepptalsControladorEnum.CONTEO.getValue()).toString()) > 0) {
				JsfUtil.agregarMensajeError(idioma.getString("TB_TB898"));
				return;
			}
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		agregarRegistroNuevo(false);
		String[] campos = { "ano", ComprobantepptalsControladorEnum.TIPOCOMPROBANTE.getValue(),
				ComprobantepptalsControladorEnum.NUMEROCOMPROBANTE.getValue(), "fechaComprobante",
				"descripcionComprobante", "documentoComprobante", "vlrDocumentoComprobante", "debitoComprobante",
				"creditoComprobante", "abonadoComprobante", "creadorComprobante", "cuentaComprobante",
				"destinoComprobante", "registroAutomatico" };

		String[] valores = { ano, strTipoComprobante,
				SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()), "").toString(),
				SysmanFunciones.formatearFecha((Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName())),
				SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.DESCRIPCION.getName()), "")
						.toString(),
				SysmanFunciones
						.nvl(registro.getCampos().get(ComprobantepptalsControladorEnum.NRO_DOCUMENTO.getValue()),
								"")
						.toString(),
				SysmanFunciones.nvl(registro.getCampos().get(CTEVLR_DOCUMENTO), "").toString(),
				String.valueOf(SysmanFunciones
						.nvl(registro.getCampos().get(ComprobantepptalsControladorEnum.DEBITO.getValue()), "0")),
				String.valueOf(SysmanFunciones
						.nvl(registro.getCampos().get(ComprobantepptalsControladorEnum.CREDITO.getValue()), "0")),
				SysmanFunciones.nvl(registro.getCampos().get(ComprobantepptalsControladorEnum.ABONADO.getValue()), "")
						.toString(),
				SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.CREATED_BY.getName()), "").toString(),
				SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.CUENTA.getName()), "").toString(),
				SysmanFunciones.nvl(registro.getCampos().get(ComprobantepptalsControladorEnum.DESTINO.getValue()), "")
						.toString(),
				((boolean) registro.getCampos().get(ComprobantepptalsControladorEnum.REGISTROAUTOMATICO.getValue()))
						? "SI"
						: "NO" };
		SessionUtil.cargarModalDatosFlashCerrar(
				String.valueOf(GeneralCodigoFormaEnum.PEDIR_TERCERO_CONTROLADOR.getCodigo()), modulo, campos, valores);
		// </CODIGO_DESARROLLADO>

	}

	/**
	 * metodo que llama al cambiar el check impreso
	 */
	public void ejecutarcambiarCheckImpreso() {
		if (ACCION_VER.equals(accion)) {
			
			try {
			UrlBean urlUpdate = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(ComprobantepptalsControladorUrlEnum.URL75061.getValue());
			
			Map<String, Object> param = new HashMap<>();
			param.put(ComprobantepptalsControladorEnum.IMPRESO.getValue(), true);
			param.put(GeneralParameterEnum.KEY_COMPANIA.getName(),compania);
			param.put("KEY_ANO", registro.getCampos().get(GeneralParameterEnum.ANO.getName()));
			param.put("KEY_TIPO", registro.getCampos().get(GeneralParameterEnum.TIPO.getName()));
			param.put("KEY_NUMERO", registro.getCampos().get(CAMPO_NUMERO));
			
			Parameter campos = new Parameter();
			campos.setFields(param);
			
			
				requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), campos);
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}else {
			registro.getCampos().put(ComprobantepptalsControladorEnum.IMPRESO.getValue(), true);
			cambiarImpreso();
			agregarRegistroNuevo(false);
		}
		
		
	}
	

	/**
	 * metodo que llama al oprimir el boton liberar
	 */
	public void oprimirLiberar() {
		// <CODIGO_DESARROLLADO>
		if ("i".equals(accion)) {
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB1190"));
		} else {
			visibleDgLiberar = true;
		}

		// </CODIGO_DESARROLLADO>
	}

	/**
	 * metodo que llama al oprimir el boton afectaciones
	 */
	public void oprimirafectaciones() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generaInformeAfectaciones(FORMATOS.PDF);
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton BtAfectadoPor en la vista
	 *
	 * Generacion del reporte <i>000926AquienAfectaEsteDocumento</i>, el cual lista
	 * los comprobantes que afecta el comprobante con el que se esta trabajando
	 *
	 */
	public void oprimirBtAfectadoPor() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		try {
			// PARAMETROS DE REEMPLAZO EN LA CONSULTA
			Map<String, Object> reemplazar = new HashMap<>();
			reemplazar.put("tipo", strTipoComprobante);
			reemplazar.put("numero", registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));
			// PARAMETROS PARA GENERACION DE INFORME
			Map<String, Object> parametros = new HashMap<>();
			Reporteador.resuelveConsulta("000926AquienAfectaEsteDocumento", Integer.valueOf(modulo), reemplazar,
					parametros);
			archivoDescarga = JsfUtil.exportarStreamed("000926AquienAfectaEsteDocumento", parametros,
					ConectorPool.ESQUEMA_SYSMAN, FORMATOS.PDF);
		} catch (JRException | IOException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * metodo que tiene la logica para generar el reporte en formato pdf de
	 * afectaciones
	 */
	public void generaInformeAfectaciones(FORMATOS formato) {
		if ("i".equals(accion)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString(ComprobantepptalsControladorEnum.TB_TB876.getValue()));
			return;
		}

		try {
			HashMap<String, Object> reemplazar = new HashMap<>();
			Map<String, Object> parametros = new HashMap<>();
			
			
			
          	reporte = null;
			try {
			reporte = ejbSysmanUtil.consultarParametro(compania,
			     "FORMATO AFECTACIONES DESDE COMPROBANTE",
			     modulo, new Date(),false);
			} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}  
			
			reemplazar.put("ano", ano);
			reemplazar.put(ComprobantepptalsControladorEnum.TIPOCOMPROBANTE.getValue(), strTipoComprobante);
			reemplazar.put(ComprobantepptalsControladorEnum.NUMEROCOMPROBANTE.getValue(),
					registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));
			
			parametros.put("PR_NUMEROCOMPROBANTE", registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString());
			parametros.put("PR_NOMBRECOMPROBANTE", nombreComprobante);
			parametros.put("PR_FECHA_COMPROBANTE", registro.getCampos().get(GeneralParameterEnum.FECHA.getName()));
			parametros.put("PR_VALOR_DOCUMENTO_COMPROBANTE",
					Double.valueOf(registro.getCampos().get(CTEVLR_DOCUMENTO).toString()));

			Reporteador.resuelveConsulta(reporte,
					Integer.valueOf(modulo), reemplazar, parametros);

			archivoDescarga = JsfUtil.exportarStreamed(reporte,
					parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
		} catch (FileNotFoundException ex) {
			JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(idioma.getString("MSM_INFORME_NO_EXISTE"), " ",
					ex.getMessage(), " ", reporte));
			Logger.getLogger(ComprobantepptalsControlador.class.getName()).log(Level.SEVERE, null, ex);
		} catch (JRException | IOException | SysmanException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}

	}

	/**
	 * metodo que llama al oprimir el boton copiar
	 */
	public void oprimirCopiar() {
		// <CODIGO_DESARROLLADO>
		if ((registro.getCampos().get(ComprobantepptalsControladorEnum.CTECOMPANIA.getValue()) != null)
				&& (registro.getCampos().get(ComprobantepptalsControladorEnum.TIPO.getValue()) != null)
				&& (registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()) != null)
				&& (registro.getCampos().get(GeneralParameterEnum.FECHA.getName()) != null)) {
			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(ComprobantepptalsControladorUrlEnum.URL16543.getValue());
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ANO.getName(), ano);
			param.put(ComprobantepptalsControladorEnum.STRTIPOCOMPROBANTE.getValue(), strTipoComprobante);
			param.put(GeneralParameterEnum.NUMERO.getName(),
					registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));

			int conteo;
			try {
				conteo = Integer.parseInt(requestManager.get(urlBean.getUrl(), param).getFields()
						.get(ComprobantepptalsControladorEnum.CONTEO.getValue()).toString());
				if (conteo > 0) {
					JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB897"));
					bloqCompACopiar = true;
				} else {
					agregarRegistroNuevo(false);
					bloqCompACopiar = false;
					compACopiar = "";
					cargarListaCompACopiar();
				}
			} catch (NumberFormatException | SystemException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}

		} else {
			bloqCompACopiar = false;
			compACopiar = "";
			cargarListaCompACopiar();
		}
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * metodo que llama al oprimir el boton solicitud DIS
	 */
	public void oprimirSolicitudDIS() {
		// <CODIGO_DESARROLLADO>
		if ("i".equals(accion)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString(ComprobantepptalsControladorEnum.TB_TB876.getValue()));
			return;
		}
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ComprobantepptalsControladorUrlEnum.URL16544.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(ComprobantepptalsControladorEnum.STRTIPOCOMPROBANTE.getValue(), strTipoComprobante);
		param.put(GeneralParameterEnum.NUMERO.getName(),
				registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));

		int conteo;
		try {
			conteo = Integer.parseInt(requestManager.get(urlBean.getUrl(), param).getFields()
					.get(ComprobantepptalsControladorEnum.CONTEO.getValue()).toString());
			if (conteo > 0) {
				// El comprobante ya tiene imputacion presupuestal
				JsfUtil.agregarMensajeError(idioma.getString(ComprobantepptalsControladorEnum.TB_TB895.getValue()));
				return;
			}
			String[] campos = { "rid", "ano", "mes", ComprobantepptalsControladorEnum.TIPOCOMPROBANTE.getValue(),
					ComprobantepptalsControladorEnum.NUMEROCOMPROBANTE.getValue(),
					ComprobantepptalsControladorEnum.CTENOMBRECOMPROBANTE.getValue(),
					ComprobantepptalsControladorEnum.CTECLASECOMPROBANTE.getValue(),
					ComprobantepptalsControladorEnum.CTEFORMATOCOMPROBANTE.getValue(),
					ComprobantepptalsControladorEnum.CTEPIDETERCERO.getValue(), "claseAfectar", "fechaComprobante",
					ComprobantepptalsControladorEnum.TERCEROCOMPROBANTE.getValue(),
					ComprobantepptalsControladorEnum.SUCURSALCOMPROBANTE.getValue(), "descripcionComprobante" };
			Object[] valores = { css, ano, mes, strTipoComprobante,
					registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString(), nombreComprobante,
					claseComprobante, formatoComprobante, pideTercero, claseAfectar,
					(Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName()),
					registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()),
					registro.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()).toString(),
					registro.getCampos().get(GeneralParameterEnum.DESCRIPCION.getName()) };
			SessionUtil.cargarModalDatosFlash(
					String.valueOf(GeneralCodigoFormaEnum.PEDIR_SOLICITUD_AFECTAR_CONTROLADOR.getCodigo()), modulo,
					campos, valores);
		} catch (NumberFormatException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * metodo que llama al retornar el formulario documento a afectar
	 */
	public void retornarFormularioDocumentoAAfectar(SelectEvent event) {
		boolean rta = (Boolean) event.getObject();
		if (rta) {
			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3186"));
		}
		cargarRegistro(css, accion);
	}

	/**
	 * metodo que llama al retornar el formulario solicitud dis
	 */
	@SuppressWarnings("unchecked")
	public void retornarFormularioSolicitudDIS() {
		Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
		String mensaje = null;
		if (parametrosEntrada != null) {
			mensaje = parametrosEntrada.get("mensaje") == null ? null : parametrosEntrada.get("mensaje").toString();
			cargarRegistro((Map<String, Object>) parametrosEntrada.get("rid"), "m");

		}
		if (mensaje != null) {
			JsfUtil.agregarMensajeInformativo(mensaje);
		}

	}

	/**
	 * metodo que llama al cargar el inicial del periodo
	 */
	public void cargaInicialPeriodo() {
		try {
			String manejaControl = obtenerParametro("MANEJA CONTROL DE SOLICITUD DE DISPONIBILIDAD", true);

			visibleSolicitudDIS = "SI".equals(SysmanFunciones.nvlStr(manejaControl, "NO"))
					&& "DIS".equals(claseComprobante);

			visibleDocumentoAAfectar = !SysmanFunciones.nvlStr(claseAfectar, "").isEmpty();

			actualizarCondicionesComprobantes();
		} catch (Exception e) {
			Logger.getLogger(ComprobantepptalsControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * metodo que compara el comprobante
	 */
	public boolean compararComprobantesOR(boolean valor1, boolean valor2, boolean valor3, boolean valor4) {
		return valor1 || valor2 || valor3 || valor4;
	}

	/**
	 * metodo que compara el comprobante
	 */
	public boolean compararComprobantesOR(boolean valor1, boolean valor2) {
		return valor1 || valor2;
	}

	/**
	 * metodo que compara el comprobante
	 */
	private void actualizarCondicionesComprobantes() {

		if (compararComprobantesOR("DIS".equals(claseComprobante), "ADD".equals(claseComprobante),
				"DMD".equals(claseComprobante), "RES".equals(claseComprobante))
				|| compararComprobantesOR("ADR".equals(claseComprobante), "DMR".equals(claseComprobante),
						"REO".equals(claseComprobante), "ARO".equals(claseComprobante))
				|| compararComprobantesOR("DRO".equals(claseComprobante), "EGR".equals(claseComprobante),
						"EJE".equals(claseComprobante), "REI".equals(claseComprobante))
				|| compararComprobantesOR("AEG".equals(claseComprobante), "DEG".equals(claseComprobante))) {
			tipoDeCuentas = "D";
		} else if (compararComprobantesOR("ING".equals(claseComprobante), "AIN".equals(claseComprobante),
				"DIC".equals(claseComprobante), "DIN".equals(claseComprobante))
				|| compararComprobantesOR("ICA".equals(claseComprobante), "MIC".equals(claseComprobante),
						"MIN".equals(claseComprobante), "MOI".equals(claseComprobante))

				|| "OIN".equals(claseComprobante)) {
			tipoDeCuentas = "C";
		} else {
			tipoDeCuentas = "T";
		}

		validarRESINGADD();
		activarDesactivarBtnComp();
	}

	/**
	 * metodo que se llama al validar
	 */
	private void validarRESINGADD() {
		visibleContractual = "RES".equals(claseComprobante) || "ADR".equals(claseComprobante)
				|| "DMR".equals(claseComprobante);

		visiblePapeles = "ING".equals(claseComprobante) || "DIN".equals(claseComprobante)
				|| "AIN".equals(claseComprobante);

		if ("DIS".equals(claseComprobante)) {
			visibleGenerarRegistro = true;
			visibleCargo = true;
			visibleCopiar = true;
			visibleCompACopiar = true;

		} else if ("ADD".equals(claseComprobante)) {
			visibleGenerarRegistro = false;
			visibleCargo = true;
		} else {
			visibleGenerarRegistro = false;
			visibleCargo = false;
		}
	}

	/**
	 * metodo que llama al activar desactivar el boton comp
	 */
	private void activarDesactivarBtnComp() {
		// Para activar o desactivar el boton de liberar
		// comprobante
		bloqLiberar = !("DIS".equals(claseComprobante) || "RES".equals(claseComprobante)
				|| "REO".equals(claseComprobante));

		if ("DIS".equals(claseComprobante) || "RES".equals(claseComprobante) || "REO".equals(claseComprobante)
				|| "EGR".equals(claseComprobante)) {
			// Activar boton informe de trazabilidad
			visibleAfectaciones = true;
		}

		String parametro = obtenerParametro("PERMITE MODIFICAR CONSECUTIVOS PRESUPUESTO", true);
		editableNumero = "SI".equals(SysmanFunciones.nvlStr(parametro, "NO"));
		try {
			estadoPeriodo = ejbSysmanUtil.verificarEstadoPeriodoMensual(compania, Integer.parseInt(ano),
					Integer.parseInt(mes), Integer.parseInt(modulo), 1);
		} catch (NumberFormatException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		if ("C".equals(estadoPeriodo)) {
			permiteAcme = false;
			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB878"));
		} else {
			permiteAcme = true;
		}
	}

	/**
	 * metodo que llama al retornar el formulario generar registro
	 */
	public void retornarFormularioGenerarRegistro(SelectEvent event) {
		@SuppressWarnings("unchecked")
		Map<String, Object> parametrosEntrada = (Map<String, Object>) event.getObject();
		String mensajeError = null;
		String valor = null;
		if (parametrosEntrada != null) {
			mensajeError = parametrosEntrada.get("mensajeError") == null ? null
					: parametrosEntrada.get("mensajeError").toString();
			valor = parametrosEntrada.get("valor") == null ? null : parametrosEntrada.get("valor").toString();
		}
		if (valor != null) {

			JsfUtil.agregarMensajeInformativo(valor);
			registro.getCampos().put(ComprobantepptalsControladorEnum.REGISTROAUTOMATICO.getValue(), true);
			agregarRegistroNuevo(false);
		}
		if (mensajeError != null) {
			JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
					idioma.getString(ComprobantepptalsControladorEnum.MSM_TRANS_INTERRUMPIDA.getValue()),
					mensajeError));
		}

	}

	/**
	 * metodo que actualiza los campos en detalle
	 */

	/**
	 * metodo que valida el casteo a toString
	 *
	 * @param campos
	 * @param var
	 * @return
	 */
	private String cadenaVaciaH(HashMap<String, Object> campos, String var) {
		return SysmanFunciones.validarCampoVacio(campos, var) ? "" : campos.get(var).toString();
	}

	/**
	 * metodo que se llama al abrir el formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		if (claseComprobante == null) {
			SessionUtil.redireccionarMenuPermisos();
		}
		try {
			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(ComprobantepptalsControladorUrlEnum.URL16545.getValue());
			Map<String, Object> param = new TreeMap<>();
			param.put(ComprobantepptalsControladorEnum.CLASECOMPROBANTE.getValue(), claseComprobante);

			HashMap<String, Object> regClaseCnt = (HashMap<String, Object>) requestManager.get(urlBean.getUrl(), param)
					.getFields();
			if (regClaseCnt == null) {
				columna = "A";
				afectacion = "N";
				claseAfectar = "";
			} else {
				columna = cadenaVaciaH(regClaseCnt, ComprobantepptalsControladorEnum.COLUMNA.getValue());
				afectacion = cadenaVaciaH(regClaseCnt, ComprobantepptalsControladorEnum.AFECTACION.getValue());
				claseAfectar = cadenaVaciaH(regClaseCnt, "CLASEAFECTAR");
			}
			cargaInicialPeriodo();
			visibleNombreSolicitante = true;
			visibleCargoSol = true;
			bloqNombreSolicitante = false;
			bloqTxtCargoSol = true;
			visibleCargo = false;
			if (!"TRA".equals(strTipoComprobante) && !"ADI".equals(strTipoComprobante)
					&& !"DSM".equals(strTipoComprobante) && "DIS".equals(claseComprobante)) {
				String parametro = obtenerParametro("MOSTRAR NOMBRE DE SOLICITANTE EN DISPONIBILIDAD", true);

				if ("SI".equals(SysmanFunciones.nvlStr(parametro, "NO"))) { // 26/09/2007
					visibleNombreSolicitante = true;
					visibleCargoSol = true;
					bloqNombreSolicitante = false;
					bloqTxtCargoSol = true;
					visibleCargo = false;
				} else {
					visibleNombreSolicitante = false;
					visibleCargoSol = false;
					bloqNombreSolicitante = true;
					bloqTxtCargoSol = true;
					visibleCargo = true;
				}

			}
			if ("3".equals(SessionUtil.getModulo())) {
				visibleVigenciaFutura = tipoVigenciaFutura;
				visibleDane = "RES".equals(claseComprobante);

			}
			
	        paramManejaReportesAppui = "SI".equals(SysmanFunciones
	            .nvl(ejbSysmanUtil.consultarParametro(compania, "MANEJA REPORTES APPUI",
	                "-1", new Date(), true), "NO"));
	        
	        paramManejaControlDeContratos = "SI".equals(SysmanFunciones
	            .nvl(ejbSysmanUtil.consultarParametro(compania, "MANEJA CONTROL DE CONTRATOS",
	                "-1", new Date(), true), "NO"));
	        
	        if(paramManejaReportesAppui && !paramManejaControlDeContratos) {
	        	manejaReportesAppui = true;
	        }else {
	        	manejaReportesAppui = false;
	        }
	        
	        controlaLongitud = "SI".equals(SysmanFunciones.nvlStr(
					ejbSysmanUtil.consultarParametro(compania, "CONTROLA LONGITUD DE CONSECUTIVO", "-1", new Date(), true),
					"NO"));
	        
			Map<String, Object> parametros = new TreeMap<>();
			parametros.put(GeneralParameterEnum.MODULO.getName(),SessionUtil.getModulo());

			
				Registro rsExiste = RegistroConverter.toRegistro(
						requestManager.get(UrlServiceUtil.getInstance()
								.getUrlServiceByUrlByEnumID(
										ComprobantepptalsControladorUrlEnum.URL104075
										.getValue())
								.getUrl(), param));

				visiblePresentarPlantillas = (!rsExiste.getCampos().get("TOTAL").toString().equals("0"))?true:false;
				
				mostrarTipoCompro = "SI".equals(obtenerParametro("MANEJA TIPO COMPROMISO EN SOLICITUD DE DISPONIBILIDAD", true)) ? true : false;
			
		} catch (SystemException e) {
			Logger.getLogger(ComprobantepptalsControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		// </CODIGO_DESARROLLADO>
	}

	/**
	 * metodo que se llama para cargar los registros
	 */
	@Override
	public void cargarRegistro() {
		// <CODIGO_DESARROLLADO>

		if (ACCION_VER.equals(validarAccion)) {
			accion = validarAccion;
		} else {

			strNombreTercero = "";
			nombreProyecto = "";
			FacesContext context = FacesContext.getCurrentInstance();
			Panel panelNuevo = (Panel) context.getViewRoot().findComponent(
					SysmanFunciones.concatenar(":FR", String.valueOf(numFormulario), "_nuevo:nuevoPanel"));
			Panel panelLista = (Panel) context.getViewRoot()
					.findComponent(SysmanFunciones.concatenar(":FR", String.valueOf(numFormulario), ":lista"));
			panelNuevo.setVisible(true);
			panelLista.setVisible(false);
			precargarRegistro();

			try {
				obtenerParametroYAsignarBooleanos();
			} catch (Exception e) {
				Logger.getLogger(ComprobantepptalsControlador.class.getName()).log(Level.SEVERE, null, e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}

			validaAccion();

			String parametro = obtenerParametro("MANEJA TERCERO DIFERENTE A VARIOS EN DISPONIBILIDAD", true); 
			parametro = SysmanFunciones.nvlStr(parametro, "NO");

			/* Se modifica condicional para que tambien se tengan en cuenta las adiciones y las reducciones presupuestales*/
			if ("SI".equals(parametro)) 
			{
				if("DIS".equals(claseComprobante)) {
					String codigoTercero = SysmanFunciones
							.nvl(registro.getCampos().get(ComprobantepptalsControladorEnum.TERCERO.getValue()), "")
							.toString();
					if ("999999999999999999".equals(codigoTercero)) {
						terceroVarios = true;
						cargarListaTercero();
					} else {
						terceroVarios = false;
						cargarListaTercero();
					}
				}
				else if("ADD".equals(claseComprobante)||"DMD".equals(claseComprobante)) 
				{
					terceroVarios = false;
					cargarListaTercero();
				}
			}
			
						
			strNombreTercero = SysmanFunciones
					.nvl(registro.getCampos().get(ComprobantepptalsControladorEnum.NOMBRETERCERO.getValue()), "")
					.toString();
			nombreProyecto = SysmanFunciones
					.nvl(registro.getCampos().get(ComprobantepptalsControladorEnum.NOMBRE_PROYECTO_PPTAL.getValue()),
							"")
					.toString();
			current();
			
		}

		validarAccion = null;

		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Mï¿½todo que evalua la acciï¿½n que se va a realizar (I-Insertar, D-Borrar,
	 * M-Actualizar)
	 */
	public void validaAccion() {
		if ("i".equals(accion)) {
			registro.getCampos().put(ComprobantepptalsControladorEnum.TIPO.getValue(), strTipoComprobante);
			Date fechaIns = new Date();
			Calendar c = Calendar.getInstance();
			c.setTime(fechaIns);
			int diaActual;
			diaActual = c.get(Calendar.DAY_OF_MONTH);
			c.set(Calendar.DAY_OF_MONTH, 1);
			c.set(Calendar.MONTH, Integer.valueOf(mes) - 1);
			c.set(Calendar.YEAR, Integer.valueOf(ano));
			if (c.getActualMaximum(Calendar.DAY_OF_MONTH) < diaActual) {
				c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
			} else {
				c.set(Calendar.DAY_OF_MONTH, diaActual);
			}
			registro.getCampos().put(GeneralParameterEnum.FECHA.getName(), c.getTime());
//7716297_PRESUPUESTO MROSERO
				try {
			String parametro = ejbSysmanUtil.consultarParametro(
	                    compania,
	                    "MANEJA VENCIMIENTO ULTIMO DIA DEL ANO",
	                    SessionUtil.getModulo(),
	                    new Date(), false);		
			
					if ("SI".equals(parametro)) 
				{
									
						registro.getCampos().put(ComprobantepptalsControladorEnum.FECHA_VCN_DOC.getValue(), SysmanFunciones
								.convertirAFecha(SysmanFunciones.concatenar("31/12", "/", ano, "dd/MM/yyyy")));
						
			
			} else {
				int a=SysmanFunciones.ano(SysmanFunciones.sumarRestarDiasFecha(
						(Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName()),
						Integer.valueOf((SysmanFunciones.nvl(obtenerParametro("DIAS VENCIMIENTO", false), 1))
								.toString()))); 
				if (a>Integer.parseInt(ano)) {
					try {
						registro.getCampos().put(ComprobantepptalsControladorEnum.FECHA_VCN_DOC.getValue(), SysmanFunciones
								.convertirAFecha(SysmanFunciones.concatenar("31/12", "/", ano, "dd/MM/yyyy")));
					} catch (ParseException e) {
						logger.error(e.getMessage(), e);
						JsfUtil.agregarMensajeError(e.getMessage());
					}
				}else {
					registro.getCampos().put(ComprobantepptalsControladorEnum.FECHA_VCN_DOC.getValue(),
							SysmanFunciones.sumarRestarDiasFecha(
									(Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName()),
									Integer.valueOf((SysmanFunciones.nvl(obtenerParametro("DIAS VENCIMIENTO", false), 1))
											.toString())));
				}
				
			}
				}
			      
		        catch (SystemException | ParseException e) {
		    logger.error(e.getMessage(), e);
		    JsfUtil.agregarMensajeError(e.getMessage());
		}
		

		} else {
			int conteo;

			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(ComprobantepptalsControladorUrlEnum.URL16544.getValue());
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ANO.getName(), ano);
			param.put(ComprobantepptalsControladorEnum.STRTIPOCOMPROBANTE.getValue(), strTipoComprobante);
			param.put(GeneralParameterEnum.NUMERO.getName(),
					registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));
			try {
				conteo = Integer.parseInt(requestManager.get(urlBean.getUrl(), param).getFields()
						.get(ComprobantepptalsControladorEnum.CONTEO.getValue()).toString());
				if (conteo > 0) {
					bloqAuxiliar = true;
				}

			} catch (NumberFormatException | SystemException e) {

				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
		}
	}

	/**
	 * metodo que se llama para obtener el parametro y asignacion
	 */
	private void obtenerParametroYAsignarBooleanos() {
		String parametro = obtenerParametro("REPORTA INFORMES AL MEN", true);
		if ("SI".equals(SysmanFunciones.nvlStr(parametro, "NO"))) {
			visibleMEN = true;
			if ("RES".equals(claseComprobante)) {
				visibleMENVIATICOS = true;
				if ((boolean) SysmanFunciones.nvl(
						registro.getCampos().get(ComprobantepptalsControladorEnum.MEN_VIATICOS.getValue()), false)) {
					visiblePermanencia = true;
					visibleTarifa = true;
				}
			}
		}
	}

	/**
	 * metodo curent
	 */
	public void current() {
		registro.getCampos().put(GeneralParameterEnum.ANO.getName(), ano);
		registro.getCampos().put(GeneralParameterEnum.CENTRO_COSTO.getName(), SysmanConstantes.CONS_CENTRO);
		registro.getCampos().put(GeneralParameterEnum.FUENTE_RECURSO.getName(), SysmanConstantes.CONS_FUENTE);
		bloqImpreso = true;
		bloqCompACopiar = true;
		if (!"i".equals(accion)) {
			bloqImpreso = "C".equals(estadoPeriodo) || !SessionUtil.getGrupo(modulo).isModificaComprobante();
			validaImpreso();
		} else {
			if (terceroVarios) {
				registro.getCampos().put(GeneralParameterEnum.TERCERO.getName(), SysmanConstantes.CONS_TERCERO);
				UrlBean urlBean = UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(ComprobantepptalsControladorUrlEnum.URL16564.getValue());
				Map<String, Object> param = new TreeMap<>();
				param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
				param.put(ComprobantepptalsControladorEnum.CONS_TERCERO.getValue(), SysmanConstantes.CONS_TERCERO);
				param.put(ComprobantepptalsControladorEnum.CONS_SUCURSAL.getValue(), SysmanConstantes.CONS_SUCURSAL);
				try {
					strNombreTercero = cadenaVaciaH(
							(HashMap<String, Object>) requestManager.get(urlBean.getUrl(), param).getFields(),
							GeneralParameterEnum.NOMBRE.getName());
				} catch (SystemException e) {
					logger.error(e.getMessage(), e);
					JsfUtil.agregarMensajeError(e.getMessage());
				}
				registro.getCampos().put(ComprobantepptalsControladorEnum.NOMBRETERCERO.getValue(), strNombreTercero);

				registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(), SysmanConstantes.CONS_SUCURSAL);
			}
			if (bloqTercero) {
				bloqProyecto = false;
				bloqFecha = false;
				bloqTercero = false;
				bloqDescripcion = false;
				bloqTexto = false;
				bloqVlrDocumento = false;
				bloqNroDocumento = false;
				bloqTipoCpteAfect = false;
				bloqCmpteAfectado = false;
				bloqTipoContrato = false;
				bloqNumeroContrato = false;
				bloqFechaVcnDoc = false;
				bloqDestino = false;
				bloqAsignacion = false;
				bloqFuente = false;
				bloqUbicacion = false;
				bloqDependencia = false;
				bloqCargo = false;
				bloqTxtCargoSol = false;
				bloqNombreSolicitante = false;
				bloqAuxiliar = false;
			} else {
				bloqAuxiliar = false;
			}
		}

		cargarListaTipoContrato();

	}

	/**
	 * Mï¿½todo encargado de validar el campo impreso y asignar valores a las
	 * variables de bloqueo.
	 */
	private void validaImpreso() {
		if (((boolean) registro.getCampos().get(ComprobantepptalsControladorEnum.IMPRESO.getValue()))
				|| "C".equals(estadoPeriodo)) {
			if (!bloqTercero) {
				bloqProyecto = true;
				bloqFecha = true;
				bloqTercero = true;
				bloqDescripcion = true;
				bloqTexto = true;
				bloqVlrDocumento = true;
				bloqNroDocumento = true;
				bloqTipoCpteAfect = true;
				bloqCmpteAfectado = true;
				bloqTipoContrato = true;
				bloqNumeroContrato = true;
				bloqFechaVcnDoc = true;
				bloqDestino = true;
				bloqAsignacion = true;
				bloqFuente = true;
				bloqUbicacion = true;
				bloqDependencia = true;
				bloqCargo = true;
				bloqTxtCargoSol = true;
				bloqNombreSolicitante = true;
				bloqAuxiliar = true;
			}
		} else {
			if (bloqTercero) {
				bloqProyecto = false;
				bloqFecha = false;
				bloqTercero = false;
				bloqDescripcion = false;
				bloqTexto = false;
				bloqVlrDocumento = false;
				bloqNroDocumento = false;
				bloqTipoCpteAfect = false;
				bloqCmpteAfectado = false;
				bloqTipoContrato = false;
				bloqNumeroContrato = false;
				bloqFechaVcnDoc = false;
				bloqDestino = false;
				bloqAsignacion = false;
				bloqFuente = false;
				bloqUbicacion = false;
				bloqDependencia = false;
				bloqCargo = false;
				bloqTxtCargoSol = false;
				bloqNombreSolicitante = false;
				bloqAuxiliar = false;
			}
		}

	}

	/**
	 * Form_Unload
	 */
	public void ejecutarrcVolver() {
		// <CODIGO_DESARROLLADO>
		try {

			if (formularioRetorno != -1) {

				String[] campos = { "rid", "ano", "mes", "tipoMov" };

				Object[] valores = { ingreso ? ridContable : rid, ano, mes, ingreso ? tipoCont : strTipoComprobante };

				SessionUtil.redireccionarPorFormulario(Integer.toString(SysmanConstantes.MODULO_CONTABILIDAD),
						Integer.toString(formularioRetorno), campos, valores, true);
				return;
			}

			String parametro = obtenerParametro("MANEJA INDICADOR DE PROYECTOS EN PRESUPUESTO", true);
			parametro = SysmanFunciones.nvlStr(parametro, "NO");

			if ("SI".equals(parametro)) {
				String codProy = SysmanFunciones
						.nvl(registro.getCampos().get(ComprobantepptalsControladorEnum.COD_PROYECTO_PPTAL.getValue()),
								"")
						.toString();
				if (codProy.isEmpty()) {
					UrlBean urlBean = UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(ComprobantepptalsControladorUrlEnum.URL16549.getValue());
					Map<String, Object> param = new TreeMap<>();
					param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
					param.put(GeneralParameterEnum.ANO.getName(), ano);
					param.put(ComprobantepptalsControladorEnum.STRTIPOCOMPROBANTE.getValue(), strTipoComprobante);
					param.put(GeneralParameterEnum.NUMERO.getName(),
							registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));

					int conteo = Integer
							.parseInt(RegistroConverter.toRegistro(requestManager.get(urlBean.getUrl(), param))
									.getCampos().get(ComprobantepptalsControladorEnum.CONTEO.getValue()).toString());
					decidirVolverAGrilla(conteo);
				} else {
					volverGrilla();
				}
			} else {
				volverGrilla();
			}
		} catch (NumberFormatException | SystemException e) {
			Logger.getLogger(ComprobantepptalsControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * metodo que se llama al volver a la grilla del formulario de datos
	 */
	private void decidirVolverAGrilla(int conteo) {
		if (conteo > 0) {
			visibleDgProyecto = true;
		} else {
			volverGrilla();
		}
	}

	/**
	 * metodo que se llama al volver a la grilla
	 */
	public void volverGrilla() {
		FacesContext context = FacesContext.getCurrentInstance();
		Panel panelNuevo = (Panel) context.getViewRoot()
				.findComponent(SysmanFunciones.concatenar(":FR", String.valueOf(numFormulario), "_nuevo:nuevoPanel"));
		Panel panelLista = (Panel) context.getViewRoot()
				.findComponent(SysmanFunciones.concatenar(":FR", String.valueOf(numFormulario), ":lista"));
		panelNuevo.setVisible(false);
		panelLista.setVisible(true);

	}

	/**
	 * metodo que se llama al cerrar
	 */
	public void ejecutarrcCerrar() {
		SessionUtil.redireccionarMenu();
	}

	/**
	 * metodos que se llaman para hacer validaciones sobre los registros
	 *
	 * @param registro
	 * @param var
	 * @return
	 */
	private String validarRegistroCadena(Registro registro, String var) {
		return SysmanFunciones.validarCampoVacio(registro.getCampos(), var) ? ""
				: registro.getCampos().get(var).toString();
	}

	private String validarRegistroBigIntegerDecimal(Registro registro, String var) {
		return SysmanFunciones.validarCampoVacio(registro.getCampos(), var) ? "0"
				: registro.getCampos().get(var).toString();
	}

	/**
	 * metodo que se llama al oprimir el boton aceptar de un dialogo
	 */
	public void aceptarFechaLiberacion() {
		if (fechaLiberacion == null) {
			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1198"));
			return;
		}
		visibleDgFecha = false;

		for (int i = 0; i < listaLiberacion.size(); i++) {
			try {
				String respuesta = ejbPresupuestoDos.liberarComprobantePresupuestal(listaLiberacion.get(i).getCampos()
						.get(ComprobantepptalsControladorEnum.CTECOMPANIA.getValue()).toString(),
						Integer.parseInt(modulo),
						Integer.parseInt(
								validarRegistroCadena(listaLiberacion.get(i), GeneralParameterEnum.ANO.getName())),
						validarRegistroCadena(listaLiberacion.get(i), GeneralParameterEnum.TIPO_CPTE.getName()),
						new BigInteger(validarRegistroBigIntegerDecimal(listaLiberacion.get(i),
								ComprobantepptalsControladorEnum.COMPROBANTE.getValue())),
						Integer.parseInt(validarRegistroCadena(listaLiberacion.get(i),
								GeneralParameterEnum.CONSECUTIVO.getName())),
						new BigDecimal(validarRegistroBigIntegerDecimal(listaLiberacion.get(i),
								ComprobantepptalsControladorEnum.NETO.getValue())),
						fechaLiberacion,
						validarRegistroCadena(listaLiberacion.get(i), GeneralParameterEnum.CUENTA.getName()),
						validarRegistroCadena(listaLiberacion.get(i), GeneralParameterEnum.TERCERO.getName()),
						validarRegistroCadena(listaLiberacion.get(i), GeneralParameterEnum.SUCURSAL.getName()),
						validarRegistroCadena(listaLiberacion.get(i), GeneralParameterEnum.CENTRO_COSTO.getName()),
						validarRegistroCadena(listaLiberacion.get(i), GeneralParameterEnum.AUXILIAR.getName()),
						validarRegistroCadena(listaLiberacion.get(i), GeneralParameterEnum.FUENTE_RECURSO.getName()),
						validarRegistroCadena(listaLiberacion.get(i), GeneralParameterEnum.REFERENCIA.getName()),
						validarRegistroCadena(listaLiberacion.get(i), ComprobantepptalsControladorEnum.NAT.getValue()),
						"", SessionUtil.getUser().getCodigo());

				compararYMostrarMensajeAlerta(respuesta, i);

				if (respuesta.contains("#")) {
					formatearRespuesta(respuesta);
				}
			} catch (NumberFormatException | SystemException e) {
				Logger.getLogger(ComprobantepptalsControlador.class.getName()).log(Level.SEVERE, null, e);
				JsfUtil.agregarMensajeError(idioma.getString("TB_TB1196"));
			}
		}
	}

	/**
	 * metodo que compara
	 */
	private void compararYMostrarMensajeAlerta(String respuesta, int i) {
		if (!respuesta.contains("-1")) {
			String mensaje;
			if (respuesta.contains("0")) {
				mensaje = idioma.getString("TB_TB1199");
				mensaje = mensaje.replace("s$tipo$s", listaLiberacion.get(i).getCampos().get("TIPO").toString());
				mensaje = mensaje.replace("s$numero$s",
						listaLiberacion.get(i).getCampos().get("COMPROBANTE").toString());
				mensaje = mensaje.replace("s$cuenta$s",
						listaLiberacion.get(i).getCampos().get(GeneralParameterEnum.CUENTA.getName()).toString());
				mensaje = mensaje.replace("s$consecutivo$s",
						listaLiberacion.get(i).getCampos().get(GeneralParameterEnum.CONSECUTIVO.getName()).toString());
				JsfUtil.agregarMensajeAlerta(mensaje);
			}
			for (int j = 1; j < 9; j++) {
				if (respuesta.contains(Integer.toString(j))) {
					mensaje = idioma.getString("TB_TB1201");
					mensaje = mensaje.replace("s$empresaparam$s", JsfUtil.getTituloPaginaEmpresaParametrizada());
					mensaje = mensaje.replace("s$numMensaje$s", "0" + j);
					JsfUtil.agregarMensajeAlerta(mensaje);
				}
			}
		} else {
			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1195"));
		}
	}

	/**
	 * Extrae la cadena que representa al objeto, solo si es diferente de nulo.
	 *
	 * @param object Un Objeto
	 * @return String que representa al objeto
	 */
	private String extraerString(Object object) {
		return object != null ? object.toString() : null;
	}

	/**
	 * metodo que formatea la respuesta
	 */
	private void formatearRespuesta(String respuesta) {
		try {
			String tipoACrear = respuesta.split("#")[1];
			String numeroACrear = respuesta.split("#")[2];
			JsfUtil.agregarMensajeInformativo(
					SysmanFunciones.concatenar(idioma.getString("TB_TB1210"), " ", tipoACrear, " ", numeroACrear));
		} catch (ArrayIndexOutOfBoundsException e) {
			Logger.getLogger(ComprobantepptalsControlador.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	/**
	 * metodo que se llama al oprimir el boton de dialogo cancelar
	 */
	public void cancelarFechaLiberacion() {
		visibleDgFecha = false;
	}

	/**
	 * metodo que se llama al oprimir el boton de dialogo aceptar
	 */
	public void aceptarLiberaComprobante() {
		visibleDgLiberar = false;
		agregarRegistroNuevo(false);
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ComprobantepptalsControladorUrlEnum.URL16550.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(ComprobantepptalsControladorEnum.STRTIPOCOMPROBANTE.getValue(), strTipoComprobante);
		param.put(GeneralParameterEnum.NUMERO.getName(),
				registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));

		try {
			listaLiberacion = RegistroConverter.toListRegistro(requestManager.getList(urlBean.getUrl(), param));
			if (listaLiberacion.isEmpty()) {
				JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1192"));
				return;
			}

			visibleDgFecha = true;
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * metodo que se llama al oprimir el boton de dialogo cancelar
	 */
	public void cancelarLiberaComprobante() {
		visibleDgLiberar = false;
	}

	/**
	 * metodo que se llama al oprimir el boton de dialogo aceptar
	 */
	public void aceptarConfigurarProyecto() {
		visibleDgProyecto = false;
	}

	/**
	 * metodo que se llama al oprimir el boton de dialogo cancelar
	 */
	public void cancelarConfigurarProyecto() {
		visibleDgProyecto = false;
	}

	/**
	 * Genera el consecutivo para el comprobante presupuestal segun el aï¿½o y
	 * consecutivo configurado para el tipo de comprobante.
	 */
	public void enumerar() {
		if ((registro.getCampos().get(ComprobantepptalsControladorEnum.TIPO.getValue()) != null)
				&& !"".equals(registro.getCampos().get(ComprobantepptalsControladorEnum.TIPO.getValue()))) {
			try {
				registro.getCampos().put(GeneralParameterEnum.NUMERO.getName(),
						ejbPresupuestoTres.enumerar(compania, Integer.parseInt(ano), strTipoComprobante));
			} catch (NumberFormatException | SystemException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
		}
	}

	private void ejecutarInsertarPpto() {

		try {
			ejbPresupuestoCuatro.insertaPpto(compania,
					SysmanFunciones.nvl(registro.getCampos().get(CAMPO_TIPOCONTRATO), "").toString(),
					SysmanFunciones.nvl(registro.getCampos().get(CAMPO_NUMEROCONTRATO), "0").toString(),
					strTipoComprobante,
					Long.parseLong(SysmanFunciones.nvl(registro.getCampos().get(CAMPO_NUMERO), 0).toString()),
					registro.getCampos().get(CAMPO_TERCERO).toString(),
					registro.getCampos().get(CAMPO_SUCURSAL).toString(), usuario);

		} catch (NumberFormatException | SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}
	}

	/**
	 * metodo heredado del bean base
	 */
	@Override
	public boolean insertarAntes() {
		if (SysmanFunciones.validarCampoVacio(registro.getCampos(), // valida
				// si
				// se
				// esta
				// tratando
				// de
				// guardar
				// el
				// cargo
				// antes
				// de
				// guardar
				// el
				// regsitro
				// por
				// primera
				// vez
				GeneralParameterEnum.CARGO.getName())) {
				try {
					parObligaDocumento = ejbSysmanUtil.consultarParametro(compania,	"TIPOS PPTALES CAMPOS OBLIGATORIOS NO Y TIPO DOCUMENTO", modulo, new Date(), true);
					if(parObligaDocumento != null && parObligaDocumento.contains(strTipoComprobante)) {
							if (SysmanFunciones.validarCampoVacio(registro.getCampos(),GeneralParameterEnum.TIPO_DOCUMENTO.getName()) || SysmanFunciones.validarCampoVacio(registro.getCampos(),GeneralParameterEnum.NRO_DOCUMENTO.getName())   ) {
				
									JsfUtil.agregarMensajeAlertaDialogo(
											"Por favor, Ingrese información en Tipo y No de documento");
									return false;
							}		
					
					}
				} catch (SystemException ex) {
					Logger.getLogger(ComprobantepptalsControlador.class.getName()).log(Level.SEVERE, null, ex);
				}
			if (ACCION_INSERTAR.equals(accion)) {
				registro.getCampos().put(ComprobantepptalsControladorEnum.CTECOMPANIA.getValue(), compania);
				registro.getCampos().put(GeneralParameterEnum.ANO.getName(), ano);
				registro.getCampos().put(ComprobantepptalsControladorEnum.TIPO.getValue(), strTipoComprobante);
				registro.getCampos().remove(ComprobantepptalsControladorEnum.TARIFA.getValue());
				registro.getCampos().remove(ComprobantepptalsControladorEnum.PERMANENCIA.getValue());
				registro.getCampos().remove(ComprobantepptalsControladorEnum.NOMBRETERCERO.getValue());
				registro.getCampos().remove(ComprobantepptalsControladorEnum.NOMBRESOLICITANTE.getValue());
				registro.getCampos().remove(ComprobantepptalsControladorEnum.NOMBRE_COMPROMISO.getValue());
				
				String descripcion = registro.getCampos().get(GeneralParameterEnum.DESCRIPCION.getName()).toString().replaceAll("\r\n", "");
		        descripcion = descripcion.replaceAll("\'", "");
		        descripcion = descripcion.replaceAll("\"", "");
		        descripcion = descripcion.replaceAll("\\s*$", ""); //JM CC_594 08/01/2025
		        registro.getCampos().put(GeneralParameterEnum.DESCRIPCION.getName(), descripcion);
		        
		        String texto = registro.getCampos().get(GeneralParameterEnum.TEXTO.getName()).toString().replaceAll("\r\n", "");
		        texto = texto.replaceAll("\'", "");
		        texto = texto.replaceAll("\"", "");
		        texto = texto.replaceAll("\\s*$", ""); //JM CC_594 08/01/2025
		        registro.getCampos().put(GeneralParameterEnum.TEXTO.getName(), texto);
			}

			if (SysmanFunciones.validarCampoVacio(registro.getCampos(), GeneralParameterEnum.REFERENCIA.getName())) {
				registro.getCampos().put(GeneralParameterEnum.REFERENCIA.getName(), "");
			}

			registro.getCampos().put(GeneralParameterEnum.DESCRIPCION.getName(),
					SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.DESCRIPCION.getName()), "")
							.toString().replace("\r\n", "\t"));

			// </CODIGO_DESARROLLADO>
			return true;
		} else {
			JsfUtil.agregarMensajeAlertaDialogo(
					"Por favor, guarde primero el registro con los datos:Fecha, Detalle Y Texto  antes de adicionar cualquier otro dato al resgistro");
			return false;
		}
	}

	/**
	 * metodo heredado del bean base
	 */
	@Override
	public boolean insertarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * metodo heredado del bean base
	 */
	@Override
	public boolean actualizarAntes() {
		// <CODIGO_DESARROLLADO>
		
		try {
			parObligaDocumento = ejbSysmanUtil.consultarParametro(compania,	"TIPOS PPTALES CAMPOS OBLIGATORIOS NO Y TIPO DOCUMENTO", modulo, new Date(), true);
			if(parObligaDocumento != null && parObligaDocumento.contains(strTipoComprobante)) {
					if (SysmanFunciones.validarCampoVacio(registro.getCampos(),GeneralParameterEnum.TIPO_DOCUMENTO.getName()) || SysmanFunciones.validarCampoVacio(registro.getCampos(),GeneralParameterEnum.NRO_DOCUMENTO.getName())   ) {
		
							JsfUtil.agregarMensajeAlertaDialogo(
									"Por favor, Ingrese información en Tipo y No de documento");
							return false;
					}		
			
			}
		} catch (SystemException ex) {
			Logger.getLogger(ComprobantepptalsControlador.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		if (accion.equals(ACCION_MODIFICAR)) {
			registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
			String descripcion = registro.getCampos().get(GeneralParameterEnum.DESCRIPCION.getName()).toString().replaceAll("\r\n", "");
	        descripcion = descripcion.replaceAll("\'", "");
	        descripcion = descripcion.replaceAll("\"", "");
	        descripcion = descripcion.replaceAll("\\s*$", ""); //JM CC_594 08/01/2025
	        registro.getCampos().put(GeneralParameterEnum.DESCRIPCION.getName(), descripcion);
	        
	        String texto = "";
	        if (registro.getCampos().get(GeneralParameterEnum.TEXTO.getName()) != null) {// Se valida que el campo TEXTO no sea nulo para evitar errores al procesar su contenido
	            
	            Object valorTexto = registro.getCampos().get(GeneralParameterEnum.TEXTO.getName());
	            if (valorTexto != null) {
	                texto = valorTexto.toString().replaceAll("\r\n", "");
	            }
	        }
	        texto = texto.replaceAll("\'", "");
	        texto = texto.replaceAll("\"", "");
	        texto = texto.replaceAll("\\s*$", ""); //JM CC_594 08/01/2025
	        registro.getCampos().put(GeneralParameterEnum.TEXTO.getName(), texto);
		}
		registro.getCampos().remove(ComprobantepptalsControladorEnum.OBLIGAAFECTACION.getValue());

		registroPreAct = new Registro();
		registroPreAct.setCampos(new HashMap<>(registro.getCampos()));

		Long numeroCom = Long.valueOf(
				SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()), "0").toString());

		validarAccion(numeroCom);

		registro.getCampos().remove(ComprobantepptalsControladorEnum.NOMBRETERCERO.getValue());
		registro.getCampos().remove(ComprobantepptalsControladorEnum.NOMBRESOLICITANTE.getValue());
		registro.getCampos().remove(GeneralParameterEnum.CLASE.getName());
		registro.getCampos().remove(ComprobantepptalsControladorEnum.NOMBRE_PROYECTO_PPTAL.getValue());
		registro.getCampos().remove(ComprobantepptalsControladorEnum.NOMBRE_COMPROMISO.getValue());

		if (!SysmanFunciones.nvl(registro.getCampos().get(ComprobantepptalsControladorEnum.TIPOCONTRATO.getValue()), "")
				.toString().isEmpty()
				&& !"0".equals(SysmanFunciones
						.nvl(registro.getCampos().get(ComprobantepptalsControladorEnum.NUMEROCONTRATO.getValue()), "0")
						.toString())
				&& (SysmanFunciones.nvl(
						registro.getCampos().get(ComprobantepptalsControladorEnum.NUMEROCONTRATO.getValue()),
						"0") != SysmanFunciones.nvl(
								registroIni.get(ComprobantepptalsControladorEnum.NUMEROCONTRATO.getValue()), "0"))) {

			try {
				ejbPresupuestoTres.eliminaNovedad(compania,
						registro.getCampos().get(ComprobantepptalsControladorEnum.TIPOCONTRATO.getValue()).toString(),
						new BigInteger(registro.getCampos()
								.get(ComprobantepptalsControladorEnum.NUMEROCONTRATO.getValue()).toString()),
						"P", strTipoComprobante,
						new BigInteger(registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString()));
			} catch (SystemException e) {

				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}

		}

		// if ("RES".equals(strTipoComprobante) && registro.getCampos().get(CAMPO_TIPOCONTRATO) != null) {
        if ("RES".equals(claseComprobante) && registro.getCampos().get(CAMPO_TIPOCONTRATO) != null)
        {
            ejecutarInsertarPpto();
        }

		agregarCamposAcutualizrAnter();
		
		if(controlaLongitud) {

			String largoConsecutivo = SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()), "").toString();
			if (largoConsecutivo.length() != 10) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4456"));
				return false;
			}

		}

		return true;
	}
	
	private void agregarCamposAcutualizrAnter() {
		if (SysmanFunciones.validarCampoVacio(registro.getCampos(), GeneralParameterEnum.REFERENCIA.getName()))

		{
			registro.getCampos().put(GeneralParameterEnum.REFERENCIA.getName(), SysmanConstantes.CONS_REFERENCIA);
			registroPreAct.getCampos().put(GeneralParameterEnum.REFERENCIA.getName(), SysmanConstantes.CONS_REFERENCIA);
		}
		if (SysmanFunciones.validarCampoVacio(registro.getCampos(), GeneralParameterEnum.CENTRO_COSTO.getName())) {
			registro.getCampos().remove(GeneralParameterEnum.CENTRO_COSTO.getName());
			registroPreAct.getCampos().put(GeneralParameterEnum.CENTRO_COSTO.getName(), SysmanConstantes.CONS_CENTRO);
		}
		if (SysmanFunciones.validarCampoVacio(registro.getCampos(), GeneralParameterEnum.AUXILIAR.getName())) {
			registro.getCampos().remove(GeneralParameterEnum.AUXILIAR.getName());
			registroPreAct.getCampos().put(GeneralParameterEnum.AUXILIAR.getName(), SysmanConstantes.CONS_AUXILIAR);
		}
		if (SysmanFunciones.validarCampoVacio(registro.getCampos(), GeneralParameterEnum.FUENTE_RECURSO.getName())) {
			registro.getCampos().remove(GeneralParameterEnum.FUENTE_RECURSO.getName());
			registroPreAct.getCampos().put(GeneralParameterEnum.FUENTE_RECURSO.getName(), SysmanConstantes.CONS_FUENTE);
		}
		if (SysmanFunciones.validarCampoVacio(registro.getCampos(), GeneralParameterEnum.TERCERO.getName())) {
			registro.getCampos().remove(GeneralParameterEnum.TERCERO.getName());
			registroPreAct.getCampos().put(GeneralParameterEnum.TERCERO.getName(), SysmanConstantes.CONS_TERCERO);
		}
		if (SysmanFunciones.validarCampoVacio(registro.getCampos(), GeneralParameterEnum.SUCURSAL.getName())) {
			registro.getCampos().remove(GeneralParameterEnum.SUCURSAL.getName());
			registroPreAct.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(), SysmanConstantes.CONS_SUCURSAL);
		}
		if (SysmanFunciones.validarCampoVacio(registro.getCampos(), GeneralParameterEnum.REFERENCIA.getName())) {
			registro.getCampos().put(GeneralParameterEnum.REFERENCIA.getName(), "");
		}
		// </CODIGO_DESARROLLADO>
		if ("m".equals(accion)) {
			registro.getCampos().remove("PERMANENCIA");
			registro.getCampos().remove("TARIFA");
			registro.getCampos().remove("COMPANIA");
		}

	}

	/**
	 * metodo que se llama al valdar la accion
	 */
	private void validarAccion(long numeroCom) {
		if ("i".equals(accion) && (numeroCom == 0L)) {

			enumerar();

		}
	}

	/**
	 * metodo heredado del bean base
	 */
	@Override
	public boolean actualizarDespues() {
		actualizarFecha();
		insertaNovedad();
		return true;

	}
	
	public void insertaNovedad() {
		if(registro.getCampos().get(CAMPO_TIPOCONTRATO) != null 
		   && SysmanFunciones.nvl(registro.getCampos().get(CAMPO_NUMEROCONTRATO),"0") != "0") 
		{
			Map<String, Object> param = new TreeMap<>();
		    param.put(ComprobantepptalsControladorEnum.TIPOT.getValue(), 
		    		registro.getCampos().get(ComprobantepptalsControladorEnum.TIPO.getValue()));
		    param.put(ComprobantepptalsControladorEnum.CLASET.getValue(), claseT);

		    Registro rs = null;
		    String novAuto = "";
		    
		    try {
		    	rs = RegistroConverter.toRegistro(requestManager.get(UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
		    			ComprobantepptalsControladorUrlEnum.URL437005.getValue()).getUrl(),param));
		    } catch (SystemException e) {
		    	logger.error(e.getMessage(), e);
		        JsfUtil.agregarMensajeError(e.getMessage());
		    }
		            
		    if(rs != null) {
		    	novAuto = rs.getCampos().get("TAUTOMATICA").toString();
		            	
		        if(novAuto.equals("A")) {
			        try {
			        	ejbGenerales.afectarNovedadEnContratacion(compania, claseT, 
			        			registro.getCampos().get(ComprobantepptalsControladorEnum.TIPO.getValue()).toString(), 
			        			Integer.parseInt(ano), Long.parseLong(registro.getCampos().get(CAMPO_NUMERO).toString()), 
			        			(Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName()), 
			        			(Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName()), 
			        			(Date) registro.getCampos().get(ComprobantepptalsControladorEnum.FECHA_VCN_DOC.getValue()), 
			        			new BigDecimal(registro.getCampos().get(CTEVLR_DOCUMENTO).toString()), 
			        			registro.getCampos().get(CAMPO_TIPOCONTRATO).toString(), 
			        			Long.parseLong(registro.getCampos().get(CAMPO_NUMEROCONTRATO).toString()),
			        			SessionUtil.getUser().getCodigo());
					} catch (NumberFormatException | SystemException e) {
						e.printStackTrace();
					}
		        }
		    }
		}
	}

	/**
	 * metodo heredado del bean base
	 */
	@Override
	public boolean eliminarAntes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * metodo heredado del bean base
	 */
	@Override
	public boolean eliminarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * metodo get y set
	 */
	public String getCompACopiar() {
		return compACopiar;
	}

	public boolean isInsertar() {
		return insertar;
	}

	public void setInsertar(boolean insertar) {
		this.insertar = insertar;
	}

	public void setCompACopiar(String compACopiar) {
		this.compACopiar = compACopiar;
	}

	public String getNombreComprobante() {
		return nombreComprobante;
	}

	public void setNombreComprobante(String nombreComprobante) {
		this.nombreComprobante = nombreComprobante;
	}

	public String getPideTercero() {
		return pideTercero;
	}

	public void setPideTercero(String pideTercero) {
		this.pideTercero = pideTercero;
	}

	public boolean isVisibleSolicitudDIS() {
		return visibleSolicitudDIS;
	}

	public void setVisibleSolicitudDIS(boolean visibleSolicitudDIS) {
		this.visibleSolicitudDIS = visibleSolicitudDIS;
	}

	public boolean isVisibleCrearNuevoComp() {
		return visibleCrearNuevoComp;
	}

	public void setVisibleCrearNuevoComp(boolean visibleCrearNuevoComp) {
		this.visibleCrearNuevoComp = visibleCrearNuevoComp;
	}

	public boolean isVisibleDocumentoAAfectar() {
		return visibleDocumentoAAfectar;
	}

	public void setVisibleDocumentoAAfectar(boolean visibleDocumentoAAfectar) {
		this.visibleDocumentoAAfectar = visibleDocumentoAAfectar;
	}

	public boolean isVisibleContractual() {
		return visibleContractual;
	}

	public void setVisibleContractual(boolean visibleContractual) {
		this.visibleContractual = visibleContractual;
	}

	public boolean isVisiblePapeles() {
		return visiblePapeles;
	}

	public void setVisiblePapeles(boolean visiblePapeles) {
		this.visiblePapeles = visiblePapeles;
	}

	public boolean isVisibleGenerarRegistro() {
		return visibleGenerarRegistro;
	}

	public void setVisibleGenerarRegistro(boolean visibleGenerarRegistro) {
		this.visibleGenerarRegistro = visibleGenerarRegistro;
	}

	public boolean isVisibleCargo() {
		return visibleCargo;
	}

	public void setVisibleCargo(boolean visibleCargo) {
		this.visibleCargo = visibleCargo;
	}

	public boolean isVisibleCopiar() {
		return visibleCopiar;
	}

	public void setVisibleCopiar(boolean visibleCopiar) {
		this.visibleCopiar = visibleCopiar;
	}

	public boolean isVisibleCompACopiar() {
		return visibleCompACopiar;
	}

	public void setVisibleCompACopiar(boolean visibleCompACopiar) {
		this.visibleCompACopiar = visibleCompACopiar;
	}

	public boolean isVisibleAfectaciones() {
		return visibleAfectaciones;
	}

	public void setVisibleAfectaciones(boolean visibleafectaciones) {
		this.visibleAfectaciones = visibleafectaciones;
	}

	public boolean isVisibleNombreSolicitante() {
		return visibleNombreSolicitante;
	}

	public void setVisibleNombreSolicitante(boolean visibleNombreSolicitante) {
		this.visibleNombreSolicitante = visibleNombreSolicitante;
	}

	public boolean isVisibleCargoSol() {
		return visibleCargoSol;
	}

	public void setVisibleCargoSol(boolean visibleCargoSol) {
		this.visibleCargoSol = visibleCargoSol;
	}

	public boolean isBloqNombreSolicitante() {
		return bloqNombreSolicitante;
	}

	public void setBloqNombreSolicitante(boolean bloqNombreSolicitante) {
		this.bloqNombreSolicitante = bloqNombreSolicitante;
	}

	public boolean isBloqTxtCargoSol() {
		return bloqTxtCargoSol;
	}

	public void setBloqTxtCargoSol(boolean bloqTxtCargoSol) {
		this.bloqTxtCargoSol = bloqTxtCargoSol;
	}

	public boolean isVisibleDane() {
		return visibleDane;
	}

	public void setVisibleDane(boolean visibleDane) {
		this.visibleDane = visibleDane;
	}

	public boolean isVisibleMEN() {
		return visibleMEN;
	}

	public void setVisibleMEN(boolean visibleMEN) {
		this.visibleMEN = visibleMEN;
	}

	public boolean isVisibleMENVIATICOS() {
		return visibleMENVIATICOS;
	}

	public void setVisibleMENVIATICOS(boolean visibleMENVIATICOS) {
		this.visibleMENVIATICOS = visibleMENVIATICOS;
	}

	public boolean isVisiblePermanencia() {
		return visiblePermanencia;
	}

	public void setVisiblePermanencia(boolean visiblePermanencia) {
		this.visiblePermanencia = visiblePermanencia;
	}

	public boolean isVisibleTarifa() {
		return visibleTarifa;
	}

	public void setVisibleTarifa(boolean visibleTarifa) {
		this.visibleTarifa = visibleTarifa;
	}

	public boolean isVisibleVigenciaFutura() {
		return visibleVigenciaFutura;
	}

	public void setVisibleVigenciaFutura(boolean visibleVigenciaFutura) {
		this.visibleVigenciaFutura = visibleVigenciaFutura;
	}

	public boolean isVisibleActualizarTercero() {
		return visibleActualizarTercero;
	}

	public void setVisibleActualizarTercero(boolean visibleActualizarTercero) {
		this.visibleActualizarTercero = visibleActualizarTercero;
	}

	public boolean isPermiteAcme() {
		return permiteAcme;
	}

	public void setPermiteAcme(boolean permiteAcme) {
		this.permiteAcme = permiteAcme;
	}

	public boolean isBloqLiberar() {
		return bloqLiberar;
	}

	public void setBloqLiberar(boolean bloqLiberar) {
		this.bloqLiberar = bloqLiberar;
	}

	public boolean isEditableNumero() {
		return editableNumero;
	}

	public void setEditableNumero(boolean editableNumero) {
		this.editableNumero = editableNumero;
	}

	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}

	public boolean isBloqImpreso() {
		return bloqImpreso;
	}

	public void setBloqImpreso(boolean bloqImpreso) {
		this.bloqImpreso = bloqImpreso;
	}

	public boolean isBloqTercero() {
		return bloqTercero;
	}

	public void setBloqTercero(boolean bloqTercero) {
		this.bloqTercero = bloqTercero;
	}

	public boolean isBloqFecha() {
		return bloqFecha;
	}

	public void setBloqFecha(boolean bloqFecha) {
		this.bloqFecha = bloqFecha;
	}

	public boolean isBloqDescripcion() {
		return bloqDescripcion;
	}

	public void setBloqDescripcion(boolean bloqDescripcion) {
		this.bloqDescripcion = bloqDescripcion;
	}

	public boolean isBloqTexto() {
		return bloqTexto;
	}

	public void setBloqTexto(boolean bloqTexto) {
		this.bloqTexto = bloqTexto;
	}

	public boolean isBloqVlrDocumento() {
		return bloqVlrDocumento;
	}

	public void setBloqVlrDocumento(boolean bloqVlrDocumento) {
		this.bloqVlrDocumento = bloqVlrDocumento;
	}

	public boolean isBloqNroDocumento() {
		return bloqNroDocumento;
	}

	public void setBloqNroDocumento(boolean bloqNroDocumento) {
		this.bloqNroDocumento = bloqNroDocumento;
	}

	public boolean isBloqTipoCpteAfect() {
		return bloqTipoCpteAfect;
	}

	public void setBloqTipoCpteAfect(boolean bloqTipoCpteAfect) {
		this.bloqTipoCpteAfect = bloqTipoCpteAfect;
	}

	public boolean isBloqTipoContrato() {
		return bloqTipoContrato;
	}

	public void setBloqTipoContrato(boolean bloqTipoContrato) {
		this.bloqTipoContrato = bloqTipoContrato;
	}

	public boolean isBloqCmpteAfectado() {
		return bloqCmpteAfectado;
	}

	public void setBloqCmpteAfectado(boolean bloqCmpteAfectado) {
		this.bloqCmpteAfectado = bloqCmpteAfectado;
	}

	public boolean isBloqNumeroContrato() {
		return bloqNumeroContrato;
	}

	public void setBloqNumeroContrato(boolean bloqNumeroContrato) {
		this.bloqNumeroContrato = bloqNumeroContrato;
	}

	public boolean isBloqDestino() {
		return bloqDestino;
	}

	public void setBloqDestino(boolean bloqDestino) {
		this.bloqDestino = bloqDestino;
	}

	public boolean isBloqFechaVcnDoc() {
		return bloqFechaVcnDoc;
	}

	public void setBloqFechaVcnDoc(boolean bloqFechaVcnDoc) {
		this.bloqFechaVcnDoc = bloqFechaVcnDoc;
	}

	public boolean isBloqAsignacion() {
		return bloqAsignacion;
	}

	public void setBloqAsignacion(boolean bloqAsignacion) {
		this.bloqAsignacion = bloqAsignacion;
	}

	public boolean isBloqFuente() {
		return bloqFuente;
	}

	public void setBloqFuente(boolean bloqFuente) {
		this.bloqFuente = bloqFuente;
	}

	public boolean isBloqUbicacion() {
		return bloqUbicacion;
	}

	public void setBloqUbicacion(boolean bloqUbicacion) {
		this.bloqUbicacion = bloqUbicacion;
	}

	public boolean isBloqDependencia() {
		return bloqDependencia;
	}

	public void setBloqDependencia(boolean bloqDependencia) {
		this.bloqDependencia = bloqDependencia;
	}

	public boolean isBloqCargo() {
		return bloqCargo;
	}

	public void setBloqCargo(boolean bloqCargo) {
		this.bloqCargo = bloqCargo;
	}

	public boolean isBloqAuxiliar() {
		return bloqAuxiliar;
	}

	public void setBloqAuxiliar(boolean bloqAuxiliar) {
		this.bloqAuxiliar = bloqAuxiliar;
	}

	public String getNumero() {
		return strNumero;
	}

	public void setNumero(String numero) {
		this.strNumero = numero;
	}

	public boolean isBloqProyecto() {
		return bloqProyecto;
	}

	public void setBloqProyecto(boolean bloqProyecto) {
		this.bloqProyecto = bloqProyecto;
	}

	public boolean isBloqCompACopiar() {
		return bloqCompACopiar;
	}

	public void setBloqCompACopiar(boolean bloqCompACopiar) {
		this.bloqCompACopiar = bloqCompACopiar;
	}

	public boolean isVarVolver() {
		return varVolver;
	}

	public void setVarVolver(boolean varVolver) {
		this.varVolver = varVolver;
	}

	public boolean isVolverGrilla() {
		return volverGrilla;
	}

	public void setVolverGrilla(boolean volverGrilla) {
		this.volverGrilla = volverGrilla;
	}

	public boolean isVisibleDgProyecto() {
		return visibleDgProyecto;
	}

	public void setVisibleDgProyecto(boolean visibleDgProyecto) {
		this.visibleDgProyecto = visibleDgProyecto;
	}

	public boolean isVisibleDgLiberar() {
		return visibleDgLiberar;
	}

	public void setVisibleDgLiberar(boolean visibleDgLiberar) {
		this.visibleDgLiberar = visibleDgLiberar;
	}

	public boolean isVisibleDgFecha() {
		return visibleDgFecha;
	}

	public void setVisibleDgFecha(boolean visibleDgFecha) {
		this.visibleDgFecha = visibleDgFecha;
	}

	public Date getFechaLiberacion() {
		return fechaLiberacion;
	}

	public void setFechaLiberacion(Date fechaLiberacion) {
		this.fechaLiberacion = fechaLiberacion;
	}

	public int getUltimoDia() {
		return ultimoDia;
	}

	public void setUltimoDia(int ultimoDia) {
		this.ultimoDia = ultimoDia;
	}

	public String getAno() {
		return ano;
	}

	public void setAno(String ano) {
		this.ano = ano;
	}

	public String getMes() {
		return mes;
	}

	public void setMes(String mes) {
		this.mes = mes;
	}

	public String getNombreProyecto() {
		return nombreProyecto;
	}

	public void setNombreProyecto(String nombreProyecto) {
		this.nombreProyecto = nombreProyecto;
	}

	public String getNombreTercero() {
		return strNombreTercero;
	}

	public void setNombreTercero(String nombreTercero) {
		this.strNombreTercero = nombreTercero;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	// </SET_GET_ATRIBUTOS>
	// <SET_GET_LISTAS>

	public List<Registro> getListaCargo() {
		return listaCargo;
	}

	public void setListaCargo(List<Registro> listaCargo) {
		this.listaCargo = listaCargo;
	}

	public List<Registro> getListaubicacion() {
		return listaubicacion;
	}

	public void setListaubicacion(List<Registro> listaubicacion) {
		this.listaubicacion = listaubicacion;
	}

	public List<Registro> getListafuenteFinanciacion() {
		return listafuenteFinanciacion;
	}

	public void setListafuenteFinanciacion(List<Registro> listafuenteFinanciacion) {
		this.listafuenteFinanciacion = listafuenteFinanciacion;
	}

	public List<Registro> getListaasignacion() {
		return listaasignacion;
	}

	public void setListaasignacion(List<Registro> listaasignacion) {
		this.listaasignacion = listaasignacion;
	}

	public List<Registro> getListaTipoContrato() {
		return listaTipoContrato;
	}

	public void setListaTipoContrato(List<Registro> listaTipoContrato) {
		this.listaTipoContrato = listaTipoContrato;
	}

	public List<Registro> getListaReferencia() {
		return listaReferencia;
	}

	public void setListaReferencia(List<Registro> listaReferencia) {
		this.listaReferencia = listaReferencia;
	}

	public List<Registro> getListaAuxiliar() {
		return listaAuxiliar;
	}

	public void setListaAuxiliar(List<Registro> listaAuxiliar) {
		this.listaAuxiliar = listaAuxiliar;
	}

	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	public RegistroDataModelImpl getListaDependencia() {
		return listaDependencia;
	}

	public void setListaDependencia(RegistroDataModelImpl listaDependencia) {
		this.listaDependencia = listaDependencia;
	}

	public RegistroDataModelImpl getListaListaNombreSolicitante() {
		return listaListaNombreSolicitante;
	}

	public void setListaListaNombreSolicitante(RegistroDataModelImpl listaListaNombreSolicitante) {
		this.listaListaNombreSolicitante = listaListaNombreSolicitante;
	}

	public RegistroDataModelImpl getListaNumeroContrato() {
		return listaNumeroContrato;
	}

	public void setListaNumeroContrato(RegistroDataModelImpl listaNumeroContrato) {
		this.listaNumeroContrato = listaNumeroContrato;
	}

	public RegistroDataModelImpl getListaNumero() {
		return listaNumero;
	}

	public void setListaNumero(RegistroDataModelImpl listaNumero) {
		this.listaNumero = listaNumero;
	}

	public RegistroDataModelImpl getListaTercero() {
		return listaTercero;
	}

	public void setListaTercero(RegistroDataModelImpl listaTercero) {
		this.listaTercero = listaTercero;
	}

	public RegistroDataModelImpl getListaCompACopiar() {
		return listaCompACopiar;
	}

	public String getPrDependencia() {
		return prDependencia;
	}

	public void setPrDependencia(String prDependencia) {
		this.prDependencia = prDependencia;
	}

	public String getPrCargo() {
		return prCargo;
	}

	public void setPrCargo(String prCargo) {
		this.prCargo = prCargo;
	}

	public void setListaCompACopiar(RegistroDataModelImpl listaCompACopiar) {
		this.listaCompACopiar = listaCompACopiar;
	}

	public RegistroDataModelImpl getListaCODPROYECTO() {
		return listaCODPROYECTO;
	}

	public void setListaCODPROYECTO(RegistroDataModelImpl listaCODPROYECTO) {
		this.listaCODPROYECTO = listaCODPROYECTO;
	}

	/**
	 * @return the terceroVarios
	 */
	public boolean isTerceroVarios() {
		return terceroVarios;
	}

	/**
	 * @param terceroVarios the terceroVarios to set
	 */
	public void setTerceroVarios(boolean terceroVarios) {
		this.terceroVarios = terceroVarios;
	}

	/**
	 * @return the manejaReportesAppui
	 */
	public boolean isManejaReportesAppui() {
		return manejaReportesAppui;
	}

	/**
	 * @param manejaReportesAppui the manejaReportesAppui to set
	 */
	public void setManejaReportesAppui(boolean manejaReportesAppui) {
		this.manejaReportesAppui = manejaReportesAppui;
	}

	/**
	 * @return the visiblePresentarPlantillas
	 */
	public boolean isVisiblePresentarPlantillas() {
		return visiblePresentarPlantillas;
	}

	/**
	 * @param visiblePresentarPlantillas the visiblePresentarPlantillas to set
	 */
	public void setVisiblePresentarPlantillas(boolean visiblePresentarPlantillas) {
		this.visiblePresentarPlantillas = visiblePresentarPlantillas;
	}

	/**
	 * @return the plantilla
	 */
	public String getPlantilla() {
		return plantilla;
	}

	/**
	 * @param plantilla the plantilla to set
	 */
	public void setplantilla(String plantilla) {
		plantilla = plantilla;
	}

	/**
	 * @return the listaListaPlantillas
	 */
	public RegistroDataModelImpl getListaListaPlantillas() {
		return listaListaPlantillas;
	}

	/**
	 * @param listaListaPlantillas the listaListaPlantillas to set
	 */
	public void setListaListaPlantillas(RegistroDataModelImpl listaListaPlantillas) {
		this.listaListaPlantillas = listaListaPlantillas;
	}
	
	/**
     * Retorna la lista listatipoCompromiso
     * 
     * @return listatipoCompromiso
     */
    public RegistroDataModelImpl getListatipoCompromiso() {
        return listatipoCompromiso;
    }
    /**
     * Asigna la lista listatipoCompromiso
     * 
     * @param listatipoCompromiso
     * Variable a asignar en  listatipoCompromiso
     */
    public void setListatipoCompromiso(RegistroDataModelImpl listatipoCompromiso) {
        this.listatipoCompromiso = listatipoCompromiso;
    }

	/**
	 * @return the visibleListaPlantillas
	 */
	public Boolean getVisibleListaPlantillas() {
		return visibleListaPlantillas;
	}

	/**
	 * @param visibleListaPlantillas the visibleListaPlantillas to set
	 */
	public void setVisibleListaPlantillas(Boolean visibleListaPlantillas) {
		this.visibleListaPlantillas = visibleListaPlantillas;
	}
	
	public boolean isVerCerrar() {
		return verCerrar;
	}

	public void setVerCerrar(boolean verCerrar) {
		this.verCerrar = verCerrar;
	}
	
	public boolean isMostrarTipoCompro() {
        return mostrarTipoCompro;
    }

    public void setMostrarTipoCompro(boolean mostrarTipoCompro) {
        this.mostrarTipoCompro = mostrarTipoCompro;
    }

}
