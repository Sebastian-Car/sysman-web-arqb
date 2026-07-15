/*-
 * FrmTramitesControlador.java
 *
 * 1.0
 * 
 * 13/04/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman. Paipa, Boyaca. All rights
 * reserved.
 */
package com.sysman.workflow;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.ArmarDireccionesControladorEnum;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.services.ServidorCorreo;
import com.sysman.session.utl.ConstantesWorkflowEnum;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.rest.enums.APIAutoServicioEnum;
import com.sysman.workflow.ejb.EjbWorkflowCeroRemote;
import com.sysman.workflow.enums.DTramiteVariablesControladorEnum;
import com.sysman.workflow.enums.DTramiteVariablesControladorUrlEnum;
import com.sysman.workflow.enums.FrmAsignarTramitesControladorEnum;
import com.sysman.workflow.enums.FrmDTramitesControladorEnum;
import com.sysman.workflow.enums.FrmMonitorHistorialControladorEnum;
import com.sysman.workflow.enums.FrmProyeccionesTramitesControladorEnum;
import com.sysman.workflow.enums.FrmTramitesControladorEnum;
import com.sysman.workflow.enums.FrmTramitesControladorUrlEnum;
import com.sysman.workflow.enums.FrmmonitortramitesControladorUrlEnum;
import com.sysman.workflow.enums.FrmprocedenciatramitesControladorUrlEnum;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.jms.Session;
import javax.mail.MessagingException;
import javax.naming.NamingException;
import javax.swing.JOptionPane;

import org.apache.commons.io.FilenameUtils;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import net.sf.jasperreports.engine.JRException;
import sysman.util.consumo.APIJudicialDeudaC;
import sysman.util.consumo.enums.ServicioEnum;
import sysman.util.consumo.enums.VariableComercioEnum;
import sysman.util.consumo.enums.VariableDeudaEnum;
/**
 * Controlador de la forma: <code>frmtramites</code>.
 *
 * @version 1.0, 13/04/2018
 * @author pespitia
 */
@ManagedBean
@ViewScoped
public class FrmTramitesControlador extends BeanBaseDatosAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	/**
	 * Constante a nivel de clase que aloja el codigo del modulo desde
	 * el cual el usuario inicio sesion.
	 */
	private final String modulo = SessionUtil.getModulo();
	/**
	 * Constante a nivel de clase que aloja el codigo del menu desde
	 * el cual se accedio al formulario.
	 */
	private final String menuActual = SessionUtil.getMenuActual();
	/**
	 * Constante a nivel de clase que aloja el codigo del usuario que
	 * inicio sesion.
	 */
	private final String usuario = SessionUtil.getUser().getCodigo();
	/** Constante a nivel de clase que aloja la cadena: COMPANIA. */
	private final String cCompania = GeneralParameterEnum.COMPANIA.getName();
	/** Constante a nivel de clase que aloja la cadena: CODIGO. */
	private final String cCodigo = GeneralParameterEnum.CODIGO.getName();
	/**
	 * Constante a nivel de clase que aloja la cadena:
	 * DIRECCION_PROCEDENCIA.
	 */
	private final String cDireccionProcedencia = FrmTramitesControladorEnum.DIRECCION_PROCEDENCIA
			.getValue();
	/**
	 * Constante a nivel de clase que aloja la cadena: NODO_ACTUAL.
	 */
	private final String cNodoActual = FrmTramitesControladorEnum.NODO_ACTUAL
			.getValue();
	/** Constante a nivel de clase que aloja la cadena: NODO_A_NOM. */
	private final String cNodoANom = FrmTramitesControladorEnum.NODO_A_NOM
			.getValue();
	/**
	 * Constante a nivel de clase que aloja la cadena: NODO_ORIGEN.
	 */
	private final String cNodoOrigen = FrmTramitesControladorEnum.NODO_ORIGEN
			.getValue();
	/** Constante a nivel de clase que aloja la cadena: NODO_O_NOM. */
	private final String cNodoONom = FrmTramitesControladorEnum.NODO_O_NOM
			.getValue();
	/** Constante a nivel de clase que aloja la cadena: NOMBRE. */
	private final String cNombre = GeneralParameterEnum.NOMBRE.getName();
	/** Constante a nivel de clase que aloja la cadena: NUMERO. */
	private final String cNumero = GeneralParameterEnum.NUMERO.getName();
	/** Constante a nivel de clase que aloja la cadena: PROCESO. */
	private final String cProceso = FrmTramitesControladorEnum.PROCESO
			.getValue();
	/** Constante a nivel de clase que aloja la cadena: PROCESOS. */
	private final String cProcesos = FrmTramitesControladorEnum.PROCESOS
			.getValue();
	/**
	 * Constante a nivel de clase que aloja la cadena: TIPO_TRAMITE.
	 */
	private final String cTipoTramite = FrmTramitesControladorEnum.TIPO_TRAMITE
			.getValue();
	// <DECLARAR_ATRIBUTOS>
	/**
	 * Atributo que contiene el codigo del pais seleccionado en el
	 * combo Pais Origen (CB5880).
	 */
	private String codigoPais;
	/**
	 * Atributo que contiene el codigo del departamento seleccionado
	 * en el combo Departamento Origen (CB5881).
	 */
	private String codigoDepartamento;
	/**
	 * Atributo que contiene el codigo de la ciudad seleccionada
	 * en el combo Ciudad Origen (CB5874).
	 */
	private String codigoCiudad;
	/**
	 * Atributo que contiene el codigo del proceso seleccionado en el
	 * combo Proceso (CB5878).
	 */
	private String codigoProceso;
	/**
	 * Atributo que almacena el nombre del proceso seleccionado en el
	 * combo (CB5878).
	 */
	private String procesoNom;
	/**
	 * Atributo que almacena el nombre del tipo de tramite
	 * seleccionado en el combo (CB5877).
	 */
	private String tipoTramiteNom;
	/**
	 * Atributo que almacena el codigo del formato configurado para el
	 * nodo actual en el tramite.
	 */
	private String formatoCod;
	/**
	 * Atributo que almacena la fecha que identifica el formato
	 * configurado para el nodo actual en el tramite.
	 */
	private Date formatoFecha;
	/**
	 * Variable que almacena el codigo de la opcion de menu
	 * configurada para el nodo actual.
	 */
	private String menuNodoActual;
	/**
	 * Variable que almacena el nombre de la opcion de menu
	 * configurada para el nodo actual.
	 */
	private String menuNodoActualNom;
	/**
	 * Variable que almacena el codigo del modulo asociado a la opcion
	 * de menu configurada para el nodo actual.
	 */
	private String moduloNodoActual;
	/**
	 * Variable que almacena el codigo calidad.
	 */
	private String codigoCalidad;
	/**
	 * Variable que almacena la procedencia.
	 */
	private String procedencia;
	/**
	 * Registro que almacena la informacion del formulario asociado a
	 * la opcion de menu configurada para el nodo actual.
	 */
	private Registro regFormNodoActual;
	/**
	 * Indicador que establece el formulario a que debe retornar la
	 * opciï¿½n "volver"
	 */
	private boolean varVolver;
	/** Atributo que indica si el proceso maneja nodo intermedio. */
	private boolean indNodoIntermedio;
	/**
	 * Indicador que establece si el usuario que edita el registro es
	 * de tipo RACI Responsable.
	 */
	private boolean indUsuarioResponsable;
	/**
	 * objeto del tipo registro para la informacion de e-mail config_email
	 */
	private Registro rsEmail;
	/**
	 * objeto del tipo registro para la procedencia
	 */
	private Registro rsProcedencia;
	/**
	 * variable que almacena el nombre del dominio para el caso del envio por correo
	 */ 
	private String hostName;
	/**
	 * variable que almacenara el usuario del correo que enviara la notificacion
	 */
	private String user;
	/**
	 * variable que almacena la contraseï¿½a para aplicaciones obtenida del correo configurado
	 */
	private String contrasena;
	/**
	 * varibale que enviara el correo de notificacion
	 */
	private ServidorCorreo correo;
	
	/**
	 * variabale que almacena el valor retornado del DSS:1035013
	 */
     private int manejaCheckDependencia;
     /**
 	 * variabale que controla si el campo Depencia es obligatorio
 	 */
     private String obligaCampos1;
     /**
  	 * variabale que indica si de ebe bloquear el campo Dependencia
  	 */
     private boolean obligaCampoDependencia;
     /**
  	 * Mapa que indica si se debe bloquear el campo Dependencia.
  	 */
     private Map<String, String>  camposBordeDependencia;
	
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_LISTAS>
	/**
	 * Lista que contiene los detalles del combo Procedencia (CB5872).
	 */
	private RegistroDataModelImpl listaProcedencia;
	/** Lista que contiene los detalles del combo Estado (CB5873). */
	private List<Registro> listaEstado;
	/**
	 * Lista que contiene los detalles del combo Ciudad Origen
	 * (CB5874).
	 */
	private List<Registro> listaCiudadOrigen;
	/**
	 * lLista que contiene los detalles del combo Tipo Referencia
	 * (CB5875).
	 */
	private List<Registro> listaTipoReferencia;
	/**
	 * Lista que contiene los detalles del combo Tipo Referencia 1
	 * (CB5876).
	 */
	private List<Registro> listaTipoReferenciaUno;
	/**
	 * Lista que contiene los detalles del combo Tipo Tramite
	 * (CB5877).
	 */
	private List<Registro> listaTipoTramite;
	/** Lista que contiene los detalles del combo Proceso (CB5878). */
	private List<Registro> listaProcesos;
	/** Lista que contiene los detalles del combo Modulo (CB5879). */
	private List<Registro> listaModulo;
	/**
	 * Lista que contiene los detalles del combo Pais Origen (CB5880).
	 */
	private List<Registro> listaPaisOrigen;
	/**
	 * Lista que contiene los detalles del combo Departmaneto Origen
	 * (CB5881).
	 */
	private List<Registro> listaDepartamentoOrigen;
	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Lista que contiene los detalles del combo Etapa Origen
	 * (CB5901).
	 */
	private List<Registro> listaVulnerabilidad;
	/**
	 * Lista que contiene los detalles del combo vulnerabilidad
	 */
	private List<Registro> listaOcupacion;
	/**
	 * Lista que contiene los detalles del combo ocupacion
	 */
	private List<Registro> listaEscolaridad;
	/**
	 * Lista que contiene los detalles del combo escolaridad
	 */
	private List<Registro> listaRangoEdad;
	/**
	 * Lista que contiene los detalles del combo rango edad
	 */
	private List<Registro> listaTipoPersona;
	/**
	 * Lista que contiene los detalles del combo tipo persona
	 */
	
	private RegistroDataModelImpl listaNodoOrigen;
	/**
	 * Lista que contiene los detalles del combo Serie Documental
	 * (CB5904).
	 */
	private RegistroDataModelImpl listaCbSerieDocumental;
	/**
	 * Lista que contiene los detalles del combo dependencias
	 ** (CB7432).
	 */
	private RegistroDataModelImpl listacmbDependencia;
	private List<Registro> listaTipoPoblacion;
	private List<Registro> listaTipomedio;
	private List<Registro> listaMotivoDevolucion;
	private boolean visibleAlerta;
	private boolean visiblePoblacion;
	private boolean visibleOcupacion; 
	/**
	 * variable almacena valor por defecto de caracteres del campo Descripcion
	 ** (CB1671).
	 */
	private int maxCaracteresDescripcion = 255;  
	/**
	 * variable almacena valor por defecto de caracteres del campo Clase anexo
	 ** (CB1671).
	 */
	private int maxCaracteresClaseAnexo = 500; 	
	// </DECLARAR_LISTAS_COMBO_GRANDE>
	// <DECLARAR_LISTAS_SUBFORM>
	// </DECLARAR_LISTAS_SUBFORM>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_ADICIONALES>
	// </DECLARAR_ADICIONALES>
	// <DECLARAR_EJBs>
	/**
	 * Variable que permite acceder a las funciones y procedimientos
	 * del paquete: <code>PCK_WORKFLOW</code>.
	 */
	@EJB
	private EjbWorkflowCeroRemote ejbWorkflowCero;
	/**
	 * Variable que permite acceder a las funciones y procedimientos
	 * del paquete: <code>PCK_SYSMAN_UTL</code>.
	 */
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	/**
	 * Variable que permite habilitar el boton de imprimirSticker
	 */
	private boolean imprimirSticker=false;
	/**
	 * Variable que permite hablitar un mensaje popup para confirmar si desea o no enviar el correo.
	 */
	private boolean enviarCorreoConfirmacion = false;
	private StreamedContent archivoDescarga;
	/**
	 * Variable que permite identificar si se muestran los botones de deuda
	 */
	private boolean verDeuda = false;
	/**
	 * C&oacute;digo del servicio que trae la deuda de modulos auxiliares
	 */
	private Integer codServicio;
	/**
	 * registro del nivel de usuario
	 */
	private Registro nivelUsuario;
	private String correspondencia;
	private boolean envioCorrespondencia;
	private boolean recepcionCorres;
	private String dependencia;
	// </DECLARAR_EJBs>
	/**
	 * Crea una nueva instancia de FrmTramitesControlador
	 */
	@SuppressWarnings("unchecked")
	public FrmTramitesControlador() {
		super();
		compania = SessionUtil.getCompania();
		visiblePoblacion = false;
		visibleOcupacion= false; 
		
		try {
			// 1763
			numFormulario = GeneralCodigoFormaEnum.FRM_TRAMITES_CONTROLADOR
					.getCodigo();
			validarPermisos();
			//            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
			//            if (parametrosEntrada != null) {
			//                rid = (Map<String, Object>) parametrosEntrada.get("rid");
			//            }
			// <INI_ADICIONAL>
			Map<String, Object> paramIn = SessionUtil.getFlash();
			if (paramIn != null) {
				// rid = (Map<String, Object>) parametrosEntrada.get("rid");
				rid = (Map<String, Object>) paramIn
						.get(FrmTramitesControladorEnum.PR_ROWKEY
								.getValue());
			}
			if (rid == null) {
				Map<String, Object> ridTramite = (Map<String, Object>) SessionUtil
						.getSessionVar(ConstantesWorkflowEnum.PR_RID_TRAMITE
								.getValue());
				if (ridTramite != null) {
					rid = (Map<String, Object>) ridTramite
							.get(FrmTramitesControladorEnum.PR_ROWKEY
									.getValue());
				}
			}
			// </INI_ADICIONAL>
		}
		catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		}
		finally {
			SessionUtil.cleanFlash();
			try {
				SessionUtil.removeSessionVarContainer(
						ConstantesWorkflowEnum.PR_RID_TRAMITE
						.getValue());
				SessionUtil.removeSessionVarContainer("parametroswf");
			}
			catch (NamingException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
		}
	}
	/**
	 * En este metodo se hace la invocacion de lo metodos de carga de
	 * todas las listas, menos las que son de subformularios
	 */
	@Override
	public void iniciarListas() {
		// <CARGAR_LISTA_COMBO_GRANDE>
		cargarListaCbSerieDocumental();
		cargarListacmbDependencia();
		cargarListaTipoPoblacion();
		// </CARGAR_LISTA_COMBO_GRANDE>
		// <CARGAR_LISTA>
		cargarListaVulnerabilidad();
		cargarListaOcupacion();
		cargarListaEscolaridad();
		cargarListaRangoEdad();
		cargarListaTipoPersona();
		cargarListaProcedencia();
		cargarListaEstado();
		cargarListaTipoReferencia();
		cargarListaTipoReferenciaUno();
		cargarListaModulo();
		cargarListaPaisOrigen();
		cargarListaTipomedio();
		cargarListaMotivoDevolucion();
		cargarLimitesCaracteres();
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
		enumBase = GenericUrlEnum.TRAMITES;
		buscarLlave();
		asignarOrigenDatos();
	}
	/**
	 * Se asignan las URLs y parametros asociados a las operaciones
	 * CRUD del formulario.
	 */
	@Override
	public void asignarOrigenDatos() {
		buscarUrls();
		String nivel =nivelU();
		parametrosListado.put(cCompania, compania);		
		parametrosListado.put(GeneralParameterEnum.ESTADO.getName(), "4,5"); // Activo e Inactivo
		if(nivel.equals("1")) {
			parametrosListado.put(GeneralParameterEnum.ESTADO.getName(), 4);
			urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
					FrmTramitesControladorUrlEnum.URL001.getValue());
		}else{
			parametrosListado.put(
					FrmTramitesControladorEnum.USUARIO_INTERNO.getValue(),
					usuario);			
			urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(GenericUrlEnum.TRAMITES.getGridKey());
		}
	}
	// <METODOS_CARGAR_LISTA>
	/**
	 * Carga la lista: <code>listaProcedencia</code> asociada al combo
	 * Procedencia (CB5872).
	 */
	public void cargarListaProcedencia() {
		Map<String, Object> param = new TreeMap<>();
		param.put(cCompania, compania);
		//		try {
		//			listaProcedencia = RegistroConverter.toListRegistro(
		//					requestManager.getList(UrlServiceUtil.getInstance()
		//							.getUrlServiceByUrlByEnumID(
		//									FrmTramitesControladorUrlEnum.URL7008
		//									.getValue())
		//							.getUrl(), param));
		//		}
		//		catch (SystemException e) {
		//			logger.error(e.getMessage(), e);
		//			JsfUtil.agregarMensajeError(e.getMessage());
		//		}
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmTramitesControladorUrlEnum.URL01848 // Se reemplazo URL01842
						.getValue());
		listaProcedencia = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, cCodigo);
	}
	/**
	 * 
	 * Carga la lista listaTipomedio
	 *
	 */
	public void cargarListaTipomedio(){
		Map<String, Object> param = new TreeMap<>();
		param.put(cCompania, compania);
		try {
			listaTipomedio = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmTramitesControladorUrlEnum.URL10094
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
	 * Carga la lista listaMotivoDevolucion
	 *
	 */
	public void cargarListaMotivoDevolucion(){
		//listaMotivoDevolucion = service.getListado(conectorPool, "SELECT"+
		//"     CODIGO,NOMBRE"+
		//" FROM MOTIVO_DEVOLUTIVO"+
		//" WHERE COMPANIA = :COMPANI");
	}
	/**
	 * Carga la lista: <code>listaEstado</code> asociada al combo
	 * Estado (CB5873).
	 */
	public void cargarListaEstado() {
		Map<String, Object> param = new TreeMap<>();
		param.put("CATEGORIA", 4);
		try {
			listaEstado = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmTramitesControladorUrlEnum.URL7377
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * Carga la lista: <code>listaCiudadOrigen</code> asociada al
	 * combo Ciudad Origen (CB5874).
	 */
	public void cargarListaCiudadOrigen() {
		Map<String, Object> param = new TreeMap<>();
		param.put("PAIS", codigoPais);
		param.put("DEPARTAMENTO", codigoDepartamento);
		try {
			listaCiudadOrigen = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmTramitesControladorUrlEnum.URL7736
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * Carga la lista: <code>listaTipoReferencia</code> asociada al
	 * combo Tipo Referencia (CB5875).
	 */
	public void cargarListaTipoReferencia() {
		Map<String, Object> param = new TreeMap<>();
		param.put("CATEGORIA", 7);
		try {
			listaTipoReferencia = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmTramitesControladorUrlEnum.URL7377
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * Carga la lista: <code>listaTipoReferenciaUno</code> asociada al
	 * combo Tipo Referencia 1 (CB5876).
	 */
	public void cargarListaTipoReferenciaUno() {
		listaTipoReferenciaUno = listaTipoReferencia;
	}
	/**
	 * Carga la lista: <code>listaTipoTramite</code> asociada al combo
	 * Tipo Tramite (CB5877).
	 */
	public void cargarListaTipoTramite() {
		Map<String, Object> param = new TreeMap<>();
		param.put(cCompania, compania);
		param.put(cProceso, codigoProceso);
		try {
			listaTipoTramite = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmTramitesControladorUrlEnum.URL8946
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * Carga la lista: <code>listaProcesos</code> asociada al combo
	 * Procesos (CB5878).
	 */
	public void cargarListaProcesos() {
		Map<String, Object> param = new TreeMap<>();
		param.put(cCompania, compania);
		try {
			String urlEnum;	
			String mostrarProceso = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania, "MOSTRAR PROCESOS WORKFLOW", modulo, new Date(), false),"SI");
			if(mostrarProceso.equals("SI")) {
				urlEnum = FrmTramitesControladorUrlEnum.URL9368.getValue();
			}else {
				urlEnum = FrmTramitesControladorUrlEnum.URL01843.getValue();
				param.put("USUARIO", SessionUtil.getUser().getCodigo());
			}
			listaProcesos = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(urlEnum)
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		
		cargarBorde();
	}
	/**
	 * Carga la lista: <code>listaModulo</code> asociada al combo
	 * Modulo (CB5879).
	 */
	public void cargarListaModulo() {
		try {
			listaModulo = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmTramitesControladorUrlEnum.URL9725
									.getValue())
							.getUrl(), null));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * Carga la lista: <code>listaPaisOrigen</code> asociada al combo
	 * Pais Origen (CB5880).
	 */
	public void cargarListaPaisOrigen() {
		try {
			listaPaisOrigen = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmTramitesControladorUrlEnum.URL10091
									.getValue())
							.getUrl(), null));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * Carga la lista: <code>listaDepartamentoOrigen</code> asociada
	 * al combo Departamento Origen (CB5881).
	 */
	public void cargarListaDepartamentoOrigen() {
		Map<String, Object> param = new TreeMap<>();
		param.put("PAIS", codigoPais);
		try {
			listaDepartamentoOrigen = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmTramitesControladorUrlEnum.URL10460
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * Carga la lista: <code>listaNodoOrigen</code> asociada al combo
	 * Etapa Origen (CB5901).
	 */
	public void cargarListaNodoOrigen() {
		Map<String, Object> param = new TreeMap<>();
		param.put(cCompania, compania);
		param.put(cProceso, codigoProceso);
		param.put(GeneralParameterEnum.ESTADO.getName(), 4); // Estado
		// Activo
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmTramitesControladorUrlEnum.URL0001
						.getValue());
		listaNodoOrigen = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, cCodigo);
	}
	/**
	 * Carga la lista: <code>listaCbSerieDocumental</code> asociada al
	 * combo Serie Documental (CB5904).
	 */
	public void cargarListaCbSerieDocumental() {
		Map<String, Object> param = new TreeMap<>();
		param.put(cCompania, compania);
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmTramitesControladorUrlEnum.URL0002
						.getValue());
		listaCbSerieDocumental = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, cCodigo);
	}
	/**
	 * Carga la lista: <code>listacmbDependencia</code> asociada al
	 * combo Dependevias (CB7432).
	 */
	public void cargarListacmbDependencia(){
		Map<String, Object> param = new TreeMap<>();
		param.put(cCompania, compania);
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmTramitesControladorUrlEnum.URL01844
						.getValue());
		listacmbDependencia= new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, cCodigo);
	}
	/**
	 * 
	 * Carga la lista listaTipoPoblacion
	 *
	 */
	public void cargarListaTipoPoblacion(){
		Map<String, Object> param = new TreeMap<>();
		param.put("CATEGORIA", "20");
		try {
			listaTipoPoblacion = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmTramitesControladorUrlEnum.URL01841
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	
	public void cargarListaVulnerabilidad(){
		Map<String, Object> param = new TreeMap<>();
		param.put("CATEGORIA", "25");
		try {
			listaVulnerabilidad = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmTramitesControladorUrlEnum.URL01841
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	
	public void cargarListaOcupacion(){
		Map<String, Object> param = new TreeMap<>();
		param.put("CATEGORIA", "26");
		try {
			listaOcupacion = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmTramitesControladorUrlEnum.URL01841
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	
	public void cargarListaEscolaridad(){
		Map<String, Object> param = new TreeMap<>();
		param.put("CATEGORIA", "24");
		try {
			listaEscolaridad = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmTramitesControladorUrlEnum.URL01841
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	
	public void cargarListaRangoEdad(){
		Map<String, Object> param = new TreeMap<>();
		param.put("CATEGORIA", "23");
		try {
			listaRangoEdad = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmTramitesControladorUrlEnum.URL01841
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	
	public void cargarListaTipoPersona(){
		Map<String, Object> param = new TreeMap<>();
		param.put("CATEGORIA", "22");
		try {
			listaTipoPersona = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmTramitesControladorUrlEnum.URL01841
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	// </METODOS_CARGAR_LISTA>
	// <METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el valor del combo Tipo Tramite
	 * (CB5877). Asigna el consecutivo del numero del tramite.
	 */
	public void cambiarTipoTramite() {
		// <CODIGO_DESARROLLADO>
		registro.getCampos().put(cNumero, "");
		String tipoTramite = SysmanFunciones
				.nvl(registro.getCampos().get(cTipoTramite), "")
				.toString();
		if (!tipoTramite.isEmpty()) {
			registro.getCampos().put(cNumero, generarNumeroTramite());
			tipoTramiteNom = service.buscarEnLista(tipoTramite, "TIPOTRAMITE",
					cNombre, listaTipoTramite);
		}
		// </CODIGO_DESARROLLADO>
	}
	/**
	 * Carga y define los límites de caracteres para los campos de descripción
	 * y clase de anexo según el parámetro NUMERO DE CARACTERES CAMPO CLASE DE ANEXO. 
	 * CC 1671
	 */
	private void cargarLimitesCaracteres() {
	    try {
	        String valor = SysmanFunciones.nvl(
	                ejbSysmanUtil.consultarParametro(compania, "LIMITE CARACTERES CAMPO CLASE DE ANEXO Y DESCRIPCION", modulo, new Date(), true),
	                "").toString();

	        if (!valor.isEmpty()) {
	            try {
	                int limite = Integer.parseInt(valor);

	                maxCaracteresDescripcion = Math.min(limite, 255);
	                maxCaracteresClaseAnexo = Math.min(limite, 500);
	            } catch (NumberFormatException e) {
	            	maxCaracteresDescripcion = 255;
	            	maxCaracteresClaseAnexo = 500;
	            }
	        } else {
	        	maxCaracteresDescripcion = 255;
            	maxCaracteresClaseAnexo = 500;
	        }

	    } catch (SystemException e) {
	        logger.error(e.getMessage(), e);
	        JsfUtil.agregarMensajeError(e.getMessage());
	    }
	}
	/**
	 * Metodo ejecutado al seleccionar un item del combo Proceso
	 * (CB5878).
	 */
	public void cambiarProcesos() {
		// <CODIGO_DESARROLLADO>
 		registro.getCampos().put(cNumero, "");
		registro.getCampos().put(FrmTramitesControladorEnum.NODO_ORIGEN.getValue(), "");
		registro.getCampos().put(FrmTramitesControladorEnum.NODO_ACTUAL.getValue(), "");
		registro.getCampos().remove("NODO_O_NOM");
		registro.getCampos().remove("NODO_A_NOM");
		codigoProceso = SysmanFunciones
				.nvl(registro.getCampos().get(cProcesos), "")
				.toString();
		procesoNom = service.buscarEnLista(codigoProceso, "CODIGO", cNombre,
				listaProcesos);
		//	url = service.buscarEnLista(codigoProceso, "CODIGO", "URL", listaProcesos);
		consultarProceso();
		cargarListaNodoOrigen();
		// Asignar etapa origen si el indicador NO esta marcado.
		if (!indNodoIntermedio) {
			asignarEtapaOrigen();
		}
		cargarListaTipoTramite();

		cargarBorde();
		// </CODIGO_DESARROLLADO>
		
		if(codigoProceso.contains("00000")) {
			//se hace la validaciï¿½n para que en el proceso PQRSDF se selecciones el tipo medio ELECTRONICO, codigo:2
			registro.getCampos().put("TIPO_MEDIO","2");
			
		}else {
			registro.getCampos().put("TIPO_MEDIO",null);
		}
	}
	/**
	 * Metodo ejecutado al seleccionar un item del combo Pais Origen
	 * (CB5880).
	 */
	public void cambiarPaisOrigen() {
		// <CODIGO_DESARROLLADO>
		codigoPais = SysmanFunciones
				.nvl(registro.getCampos().get(FrmTramitesControladorEnum.PAIS_ORIGEN
						.getValue()), "")
				.toString();
		cargarListaDepartamentoOrigen();
		cargarListaCiudadOrigen();
		// </CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al seleccionar un item del combo Departamento
	 * Origen (CB5881).
	 */
	public void cambiarDepartamentoOrigen() {
		// <CODIGO_DESARROLLADO>
		codigoDepartamento = SysmanFunciones
				.nvl(registro.getCampos().get(FrmTramitesControladorEnum.DEPARTAMENTO_ORIGEN
						.getValue()),
						"")
				.toString();
		cargarListaCiudadOrigen();
		// </CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al seleccionar un item del combo Procedencia
	 * (CB5872). Asigna la direccion de la procedencia al campo
	 * Direccion Procedencia (CP55035).
	 */
	public void cambiarProcedencia() {
		// <CODIGO_DESARROLLADO>
		registro.getCampos().put("DIRECCION_PROCEDENCIA", "");
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.CODIGO.getName(),
				registro.getCampos().get("PROCEDENCIA"));
		Registro regAux = null;
		try {
			regAux = RegistroConverter.toRegistro(
					requestManager.get(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmTramitesControladorUrlEnum.URL0007
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		if (regAux != null) {
			registro.getCampos().put("DIRECCION_PROCEDENCIA",
					regAux.getCampos().get("DIRECCION"));
		}
		// </CODIGO_DESARROLLADO>
	}
	
	/**
	 * Metodo ejecutado al cambiar el control TipoPoblacion
	 */
	public void cambiarTipoPoblacion() {
		visiblePoblacion = SysmanFunciones.toString(SysmanFunciones.nvl(registro.getCampos().get("TIPO_POBLACION"),"0")).equals("102"); 
		if(!visiblePoblacion) {
			registro.getCampos().put("DESC_TIPO_POBLACION", null);
		}
	}
	/**
	 * Metodo ejecutado al cambiar el control Ocupacion
	 */
	public void cambiarOcupacion() {
		visibleOcupacion = SysmanFunciones.toString(SysmanFunciones.nvl(registro.getCampos().get("OCUPACION"),"0")).equals("101");
		if(!visibleOcupacion) {
			registro.getCampos().put("DESC_OCUPACION", null);
		}
	}
		
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaProcedencia
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaProcedencia(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("PROCEDENCIA", registroAux.getCampos().get("CODIGO"));
		registro.getCampos().put("NOMBRE_PROCEDENCIA", registroAux.getCampos().get("NOMBRE"));
		registro.getCampos().put("NIT_PROCEDENCIA", registroAux.getCampos().get("NIT"));
		registro.getCampos().put(cDireccionProcedencia, registroAux.getCampos().get("DIRECCION"));
	}
	/**
	 * Metodo ejecutado al seleccionar una fila de la lista Etapa
	 * Origen (CB5901).
	 * 
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaNodoOrigen(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(cNodoOrigen,
				registroAux.getCampos().get(cCodigo));
		registro.getCampos().put(cNodoONom,
				registroAux.getCampos().get(cNombre));
		/*- Al crear un tramite el nodo actual es igual al origen. */
		registro.getCampos().put(cNodoActual,
				registroAux.getCampos().get(cCodigo));
		registro.getCampos().put(cNodoANom,
				registroAux.getCampos().get(cNombre));
	}
	/**
	 * Metodo ejecutado al seleccionar una fila de la lista:
	 * <code>listaCbSerieDocumental</code> asociada al combo (CB5904).
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCbSerieDocumental(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("SERIE_DOCUMENTAL",
				registroAux.getCampos().get(cCodigo));
		registro.getCampos().put("SERIE_DOCUMENTAL_NOM",
				registroAux.getCampos().get(cNombre));
	}
	/**
	 * Metodo ejecutado al seleccionar una fila de la lista:
	 * <code>listacmDependecnias</code> asociada al combo (CB7432).
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacmbDependencia(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("DEPENDENCIA",
				registroAux.getCampos().get(cCodigo));
		registro.getCampos().put("NOMBRE_DEPENDENCIA",
				registroAux.getCampos().get(cNombre));
	}
	
	 /**
     * Metodo ejecutado al cambiar el control mostrarAlerta
     * 
     * 
     */
public void cambiarmostrarAlerta() {
         //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
    }
	/**
	 * Metodo ejecutado al abrir el formulario (529) desde el boton
	 * Editar (BT3092).
	 */
	public void retornarFormularioBtEditarDirProcedencia(SelectEvent event) {
		// <CODIGO_DESARROLLADO>
		Map<String, Object> param = SessionUtil.getFlash();
		if (param != null
				&& !SysmanFunciones.validarCampoVacio(param,
						ArmarDireccionesControladorEnum.PR_DIRECCION
						.getValue())) {
			registro.getCampos().put(cDireccionProcedencia,
					param.get(ArmarDireccionesControladorEnum.PR_DIRECCION
							.getValue()));
		}
		// </CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al abrir el formulario (529) desde el boton
	 * Editar (BT3093).
	 */
	public void retornarFormularioBtEditarDirDestino(SelectEvent event) {
		// <CODIGO_DESARROLLADO>
		Map<String, Object> param = SessionUtil.getFlash();
		if (param != null
				&& !SysmanFunciones.validarCampoVacio(param,
						ArmarDireccionesControladorEnum.PR_DIRECCION
						.getValue())) {
			registro.getCampos().put("DIRECCION_DESTINO",
					param.get(ArmarDireccionesControladorEnum.PR_DIRECCION
							.getValue()));
		}
		// </CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cerrar el formulario que abre el boton
	 * (BT3099). Redirecciona a la grilla del formulario.
	 */
	public void retornarFormularioBtTramitar(SelectEvent event) {
		// <CODIGO_DESARROLLADO>
		Map<String, Object> param = new TreeMap<>();
		Direccionador dir = new Direccionador();
		dir.setNumForm(Integer.toString(retornarFormularioIngreso()));
		dir.setParametros(param);
		SessionUtil.redireccionarForma(dir, modulo);
		// </CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al finalizar el proceso ejecutado por el boton
	 * Acceder (BT3179).
	 * 
	 * @param event
	 * -> Evento que encapsula un objeto.
	 */
	public void retornarFormularioBtAcceder(SelectEvent event) {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cerrar el formulario desplegado al oprimir
	 * el boton Cambiar Ejecutor (BT3177).
	 */
	public void retornarFormularioBtAsignarA(SelectEvent event) {
		// <CODIGO_DESARROLLADO>
		String usuarioInterno = SysmanFunciones.nvl(event.getObject(), "")
				.toString();
		registro.getCampos().put(
				FrmTramitesControladorEnum.USUARIO_INTERNO.getValue(),
				usuarioInterno);
		// </CODIGO_DESARROLLADO>
	}
	
	
	/**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar
     * del dialogo mostrarAlerta en la vista
     *
     *
     */
public void aceptarmostrarAlerta() {
         //<CODIGO_DESARROLLADO>
	visibleAlerta = false;
	archivoDescarga = null;
	generaInforme(ReportesBean.FORMATOS.PDF);

        //</CODIGO_DESARROLLADO>
    }
    /**
     * 
     * Metodo ejecutado al oprimir el boton Cancelar
     * del dialogo mostrarAlerta en la vista
     *
     *
     */
public void cancelarmostrarAlerta() {
         //<CODIGO_DESARROLLADO>
	     visibleAlerta = false;
        //</CODIGO_DESARROLLADO>
    }
	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	// </METODOS_COMBOS_GRANDES>
	// <METODOS_ARBOL>
	// </METODOS_ARBOL>
	// <METODOS_BOTONES>
	/**
	 * Metodo ejecutado al oprimir el boton Acceder (BT3179) en la
	 * vista.
	 */
	public void oprimirBtAcceder() {
		agregarRegistroNuevo(false);
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(FrmTramitesControladorEnum.PR_ROWKEY.getValue(),css);
			param.put("PR_D_TRAMITE",recuperarConsecutivoDTramite());
			param.put("PR_NODO_ACTUAL",registro.getCampos().get(cNodoActual));
			param.put("PR_TIPO_TRAMITE",registro.getCampos().get(cTipoTramite));
			param.put("PR_TRAMITE",registro.getCampos().get(cNumero));
			param.put("PR_PROCESO",codigoProceso);
			param.put("vigenciaPeriodo",registro.getCampos().get(cNumero).toString().substring(0,4));
			param.put("menu",menuNodoActual);
			SessionUtil.setSessionVarContainer("parametroswf",param);	
		} catch(NamingException e) {
			logger.error(e.getMessage(),e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		Map<String, Object> parametros = new TreeMap<>();
		Direccionador direccionador = new Direccionador();
		direccionador.setNumForm(regFormNodoActual.getCampos()
				.get(FrmTramitesControladorEnum.CODIGO_FORMULARIO.getValue()).toString());
		direccionador.setParametros(parametros);
		SessionUtil.redireccionarForma(direccionador,moduloNodoActual);
	}
	/**
	 * Metodo ejecutado al oprimir el boton Editar (BT3092) en la
	 * vista.
	 */
	public void oprimirBtEditarDirProcedencia() {
		// <CODIGO_DESARROLLADO>
		SessionUtil.cargarModalDatosFlashCerrar(Integer
				.toString(GeneralCodigoFormaEnum.ARMAR_DIRECCIONES_CONTROLADOR
						.getCodigo()),
				modulo, new String[1], new Object[1]);
		// </CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al oprimir el boton Editar (BT3093) en la
	 * vista.
	 */
	public void oprimirBtEditarDirDestino() {
		// <CODIGO_DESARROLLADO>
		SessionUtil.cargarModalDatosFlashCerrar(Integer
				.toString(GeneralCodigoFormaEnum.ARMAR_DIRECCIONES_CONTROLADOR
						.getCodigo()),
				modulo, new String[1], new Object[1]);
		// </CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al oprimir el boton Tramitar (BT3099) en la
	 * vista.
	 */
	public void oprimirBtTramitar() {
		// <CODIGO_DESARROLLADO>
		agregarRegistroNuevo(false);
		if (validarInformacionDetallada()) {
			return;
		}
		String[] campos = new String[11];
		campos[0] = FrmDTramitesControladorEnum.PR_PROCESO.getValue();
		campos[1] = FrmDTramitesControladorEnum.PR_NODO_ORIGEN.getValue();
		campos[2] = FrmDTramitesControladorEnum.PR_NODO_ORIGEN_NOM.getValue();
		campos[3] = FrmDTramitesControladorEnum.PR_TIPOTRAMITE.getValue();
		campos[4] = FrmDTramitesControladorEnum.PR_TRAMITE.getValue();
		campos[5] = FrmDTramitesControladorEnum.PR_PROCESO_NOM.getValue();
		campos[6] = FrmDTramitesControladorEnum.PR_TIPOTRAMITE_NOM.getValue();
		campos[7] = FrmDTramitesControladorEnum.FECHA_REAL.getValue();
		campos[8] = FrmDTramitesControladorEnum.FECHA_PRORROGA.getValue();
		campos[9] = FrmDTramitesControladorEnum.PRORROGA.getValue();
		campos[10] = FrmDTramitesControladorEnum.DEPENDENCIA.getValue();

		Object[] valores = new Object[11];
		valores[0] = registro.getCampos().get(cProcesos);
		valores[1] = registro.getCampos().get(cNodoActual);
		valores[2] = registro.getCampos().get(cNodoANom);
		valores[3] = registro.getCampos().get(cTipoTramite);
		valores[4] = registro.getCampos().get(cNumero);
		valores[5] = procesoNom;
		valores[6] = tipoTramiteNom;
		valores[7] = registro.getCampos().get(FrmDTramitesControladorEnum.FECHA_REAL.getValue());
		valores[8] = registro.getCampos().get(FrmDTramitesControladorEnum.FECHA_PRORROGA.getValue());
		valores[9] = registro.getCampos().get(FrmDTramitesControladorEnum.PRORROGA.getValue());
		valores[10] = registro.getCampos().get("DEPENDENCIA");

		SessionUtil.cargarModalDatosFlashCerrar(
				Integer.toString(
						GeneralCodigoFormaEnum.FRM_D_TRAMITES_CONTROLADOR
						.getCodigo()),
				modulo, campos, valores);
		// </CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al oprimir el boton Proyectar (BT3282) en la
	 * vista.
	 */
	public void oprimirBtProyectar() {
		// <CODIGO_DESARROLLADO>
		agregarRegistroNuevo(false);
		Map<String, Object> param = new TreeMap<>();
		param.put(FrmProyeccionesTramitesControladorEnum.PR_TIPO_TRAMITE
				.getValue(), registro.getCampos().get(cTipoTramite));
		param.put(FrmProyeccionesTramitesControladorEnum.PR_PROCESO.getValue(),
				codigoProceso);
		param.put(FrmProyeccionesTramitesControladorEnum.PR_TRAMITE.getValue(),
				registro.getCampos().get(cNumero));
		param.put(FrmTramitesControladorEnum.PR_ROWKEY.getValue(), css);
		Direccionador dir = new Direccionador();
		dir.setNumForm(Integer.toString(
				GeneralCodigoFormaEnum.FRM_PROYECCIONES_TRAMITES_CONTROLADOR
				.getCodigo()));
		dir.setParametros(param);
		SessionUtil.redireccionarForma(dir, modulo);
		// </CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al oprimir el boton Informacion Detallada
	 * (BT3103) en la vista.
	 */
	public void oprimirBtInfDetallada() {
		// <CODIGO_DESARROLLADO>
		//   agregarRegistroNuevo(false);
		boolean enviaCorreo = Boolean.parseBoolean(SysmanFunciones.nvl(registro.getCampos().get(DTramiteVariablesControladorEnum.ENVIA_CORREO.getValue()),"0").toString());
		boolean procendenciaAut = Boolean.parseBoolean(SysmanFunciones.nvl(registro.getCampos().get(DTramiteVariablesControladorEnum.PROCEDENCIA_AUT.getValue()),"0").toString());
		try {
			if(procendenciaAut) {
				ejbWorkflowCero.actualizarProcedencia(compania, 
						registro.getCampos().get(cProcesos).toString(), 
						registro.getCampos().get(cNodoActual).toString(), 
						registro.getCampos().get(cTipoTramite).toString(),
						registro.getCampos().get(cNumero).toString(), 
						registro.getCampos().get(cDireccionProcedencia).toString(),
						registro.getCampos().get("PROCEDENCIA").toString(), 
						usuario);
			}
		} catch (SystemException e1) {
			logger.error(e1.getMessage(), e1);
			JsfUtil.agregarMensajeError(e1.getMessage());
		}
		Map<String, Object> params = new HashMap<>();
		params.put("COMPANIA", compania);
		params.put("PROCESO",  registro.getCampos().get(cProcesos));
		params.put("TIPO_TRAMITE",  registro.getCampos().get(cTipoTramite));
		params.put("NUMERO",  registro.getCampos().get(cNumero));
		Parameter parameter = new Parameter();
		parameter.setFields(params);
		UrlBean urlBean = UrlServiceUtil.getUrlBeanById(
				FrmTramitesControladorUrlEnum.URL10093.getValue());
		try {
			requestManager.update(urlBean.getUrl(), urlBean.getMetodo(), parameter);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		Map<String, Object> param = new TreeMap<>();
		param.put(DTramiteVariablesControladorEnum.PR_PROCESO.getValue(),
				registro.getCampos().get(cProcesos));
		param.put(DTramiteVariablesControladorEnum.PR_TIPO_TRAMITE
				.getValue(), registro.getCampos().get(cTipoTramite));
		param.put(DTramiteVariablesControladorEnum.PR_TRAMITE.getValue(),
				registro.getCampos().get(cNumero));
		param.put(DTramiteVariablesControladorEnum.PR_D_TRAMITE.getValue(),
				recuperarConsecutivoDTramite());
		param.put(DTramiteVariablesControladorEnum.PR_NODO.getValue(),
				registro.getCampos().get(cNodoActual));
		param.put(DTramiteVariablesControladorEnum.PR_COD_FORM.getValue(),
				GeneralCodigoFormaEnum.FRM_TRAMITES_CONTROLADOR
				.getCodigo());
		param.put(DTramiteVariablesControladorEnum.ENVIA_CORREO.getValue(), enviaCorreo);
		param.put(DTramiteVariablesControladorEnum.PROCEDENCIA_AUT.getValue(), procendenciaAut);
		param.put(FrmTramitesControladorEnum.PR_ROWKEY.getValue(), css);
		Direccionador dir = new Direccionador();
		dir.setNumForm(Integer
				.toString(GeneralCodigoFormaEnum.D_TRAMITE_VARIABLES_CONTROLADOR
						.getCodigo()));
		dir.setParametros(param);
		SessionUtil.redireccionarForma(dir, modulo);
		// </CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al oprimir el boton Informacion General en la
	 * vista.
	 */
	public void oprimirInfGeneral(){
		try {
			ejbWorkflowCero.prepararVariablesProceso(compania, 
					codigoProceso, 
					registro.getCampos().get(cTipoTramite).toString(),
					registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString(), usuario);
			JsfUtil.agregarMensajeInformativo(
					idioma.getString("MSM_PROCESO_EJECUTADO"));
			Map<String, Object> param = new TreeMap<>();
			param.put(DTramiteVariablesControladorEnum.PR_PROCESO.getValue(),
					registro.getCampos().get(cProcesos));
			param.put(DTramiteVariablesControladorEnum.PR_TIPO_TRAMITE
					.getValue(), registro.getCampos().get(cTipoTramite));
			param.put(DTramiteVariablesControladorEnum.PR_TRAMITE.getValue(),
					registro.getCampos().get(cNumero));
			param.put(DTramiteVariablesControladorEnum.PR_D_TRAMITE.getValue(),
					recuperarConsecutivoDTramite());
			param.put(DTramiteVariablesControladorEnum.PR_NODO.getValue(),
					registro.getCampos().get(cNodoActual));
			param.put(DTramiteVariablesControladorEnum.PR_COD_FORM.getValue(),
					GeneralCodigoFormaEnum.FRM_TRAMITES_CONTROLADOR
					.getCodigo());
			param.put(FrmTramitesControladorEnum.PR_ROWKEY.getValue(), css);
			Direccionador dir = new Direccionador();
			dir.setNumForm(Integer
					.toString(2214));
			dir.setParametros(param);
			SessionUtil.redireccionarForma(dir, modulo);
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	public void oprimirBtVerDeuda() {
		Map<String, Object> param = new TreeMap<>();
		param.put(DTramiteVariablesControladorEnum.PR_PROCESO.getValue(),
				registro.getCampos().get(cProcesos));
		param.put(DTramiteVariablesControladorEnum.PR_TIPO_TRAMITE
				.getValue(), registro.getCampos().get(cTipoTramite));
		param.put(DTramiteVariablesControladorEnum.PR_TRAMITE.getValue(),
				registro.getCampos().get(cNumero));
		param.put(DTramiteVariablesControladorEnum.PR_D_TRAMITE.getValue(),
				recuperarConsecutivoDTramite());
		param.put(DTramiteVariablesControladorEnum.PR_NODO.getValue(),
				registro.getCampos().get(cNodoActual));
		param.put(DTramiteVariablesControladorEnum.PR_COD_FORM.getValue(),
				GeneralCodigoFormaEnum.FRM_TRAMITES_CONTROLADOR
				.getCodigo());
		param.put(FrmTramitesControladorEnum.PR_ROWKEY.getValue(), css);
		Direccionador dir = new Direccionador();
		dir.setNumForm(Integer
				.toString(GeneralCodigoFormaEnum.FRM_WF_TRAER_DEUDADE_CONTROLADOR.getCodigo()));
		dir.setParametros(param);
		SessionUtil.redireccionarForma(dir, modulo);
	}
	public void oprimirBtCargarDeuda() {
		String tipoTramite = (registro.getCampos().get(cTipoTramite)).toString();
		String tramite = (registro.getCampos().get(cNumero)).toString();
		ServicioEnum servicio = ServicioEnum.VACIO;
		servicio = servicio.buscarPorUrlServicio(codServicio);
		APIJudicialDeudaC actDeuda = new APIJudicialDeudaC(compania, codigoProceso, tipoTramite, tramite, usuario,
				servicio);
		Map<String, Object> para = paramDeuda(servicio);
		try {
			actDeuda.consultaDeuda(para);
			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));
		} catch (com.sysman.util.SysmanException | SystemException | NumberFormatException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * Metodo ejecutado al oprimir el boton Historial (BT3128) en la
	 * vista.
	 */
	public void oprimirBtHistorial() {
		// <CODIGO_DESARROLLADO>
		agregarRegistroNuevo(false);
		// Parametros
		Map<String, Object> param = new TreeMap<>();
		param.put(FrmMonitorHistorialControladorEnum.PR_PROCESO.getValue(),
				registro.getCampos().get(cProcesos));
		param.put(FrmMonitorHistorialControladorEnum.PR_TIPO_TRAMITE.getValue(),
				registro.getCampos().get(cTipoTramite));
		param.put(FrmMonitorHistorialControladorEnum.PR_TRAMITE.getValue(),
				registro.getCampos().get(cNumero));
		param.put(FrmTramitesControladorEnum.PR_ROWKEY.getValue(), css);
		// Direccionador
		Direccionador dir = new Direccionador();
		dir.setNumForm(Integer
				.toString(GeneralCodigoFormaEnum.FRM_MONITOR_HISTORIAL_CONTROLADOR
						.getCodigo()));
		dir.setParametros(param);
		SessionUtil.redireccionarForma(dir, modulo);
		// </CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al oprimir el boton Cerrar (BT3134) en la
	 * vista.
	 */
	public void oprimirBtCerrar() {
		// <CODIGO_DESARROLLADO>
		try {
			ejbWorkflowCero.cerrarTramite(compania,
					registro.getCampos().get(cProcesos).toString(),
					registro.getCampos().get(cTipoTramite).toString(),
					new BigInteger(registro.getCampos().get(cNumero)
							.toString()),
					registro.getCampos().get(cNodoActual).toString(),
					usuario);
			JsfUtil.agregarMensajeInformativo(
					idioma.getString("MSM_PROCESO_EJECUTADO"));
			Map<String, Object> param = new TreeMap<>();
			Direccionador dir = new Direccionador();
			dir.setNumForm(Integer
					.toString(GeneralCodigoFormaEnum.FRM_TRAMITES_CONTROLADOR
							.getCodigo()));
			dir.setParametros(param);
			SessionUtil.redireccionarForma(dir, modulo);
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		// </CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al oprimir el boton Generar Plantilla
	 * (BT3153). Genera la plantilla configurada para el nodo.
	 */
	public void oprimirBtPlantilla() {
		// <CODIGO_DESARROLLADO>
		agregarRegistroNuevo(false);
		generarPlantilla();
		// </CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al oprimir el boton Asignar A (BT3177). Abre
	 * el formulario
	 * {@link com.sysman.workflow.FrmAsignarTramitesControlador}
	 */
	public void oprimirBtAsignarA() {
		// <CODIGO_DESARROLLADO>
		agregarRegistroNuevo(false);
		String[] claves = new String[8];
		claves[0] = FrmAsignarTramitesControladorEnum.PR_PROCESO.getValue();
		claves[1] = FrmAsignarTramitesControladorEnum.PR_TIPO_TRAMITE
				.getValue();
		claves[2] = FrmAsignarTramitesControladorEnum.PR_TRAMITE.getValue();
		claves[3] = FrmAsignarTramitesControladorEnum.PR_D_TRAMITE.getValue();
		claves[4] = FrmAsignarTramitesControladorEnum.PR_NODO_ACTUAL.getValue();
		claves[5] = FrmAsignarTramitesControladorEnum.PR_USUARIO_INT_TRAMITE
				.getValue();
		claves[6] = FrmAsignarTramitesControladorEnum.PR_PROCESO_NOM.getValue();
		claves[7] = FrmAsignarTramitesControladorEnum.PR_TIPO_TRAMITE_NOM
				.getValue();
		Object[] valores = new Object[8];
		valores[0] = registro.getCampos().get(cProcesos);
		valores[1] = registro.getCampos().get(cTipoTramite);
		valores[2] = registro.getCampos().get(cNumero);
		valores[3] = recuperarConsecutivoDTramite();
		valores[4] = registro.getCampos().get(cNodoActual);
		valores[5] = registro.getCampos().get("USUARIO_INTERNO");
		valores[6] = procesoNom;
		valores[7] = tipoTramiteNom;
		SessionUtil.cargarModalDatosFlashCerrar(
				Integer.toString(
						GeneralCodigoFormaEnum.FRM_ASIGNAR_TRAMITES_CONTROLADOR
						.getCodigo()),
				modulo, claves, valores);
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
		// <CODIGO_DESARROLLADO>
		regFormNodoActual = new Registro();
		if ((rid != null) && !rid.isEmpty()) {
			varVolver = true;
			cargarRegistro(rid, ACCION_MODIFICAR);
		}
		// </CODIGO_DESARROLLADO>
	}
	/** Metodo ejecutado despues de cargar el registro. */
	@Override
	public void cargarRegistro() {
		// <CODIGO_DESARROLLADO>
		precargarRegistro();
		cargarListaProcesos();
		/*- Asignar valores a variables al modificar*/
		if (css != null) {
			codigoPais = registro.getCampos().get(FrmTramitesControladorEnum.PAIS_ORIGEN
					.getValue()).toString();
			codigoDepartamento = registro.getCampos().get(FrmTramitesControladorEnum.DEPARTAMENTO_ORIGEN
					.getValue())
					.toString();
			codigoProceso = registro.getCampos().get(cProcesos).toString();
			consultarDTramiteActual();
			consultarMenuNodoActual();
			consultarFormNodoActual();
			validarIsUsuarioResponsable();
			procesoNom = SysmanFunciones
					.nvl(service.buscarEnLista(codigoProceso, "CODIGO",
							cNombre, listaProcesos), "")
					.toString();
			cargarListaTipoTramite();
			tipoTramiteNom = service.buscarEnLista(
					registro.getCampos().get(cTipoTramite).toString(),
					"TIPOTRAMITE", cNombre, listaTipoTramite);
			
			 alertaProrroga();
		}
		else {
			cargarValoresIniciales();
			registro.getCampos().put(GeneralParameterEnum.ESTADO.getName(),
					4); /*-Estado Activo*/
			registro.getCampos().put("HORA", recuperarHoraActualEstandar());
			registro.getCampos().put("FECHA", new Date());
			registro.getCampos().put(FrmTramitesControladorEnum.USUARIO_INTERNO
					.getValue(), usuario);
			registro.getCampos().put("USUARIO_EXTERNO", usuario);
			registro.getCampos().put("FECHA_REAL", new Date());
			registro.getCampos().put("USUARIO_EXTERNO", usuario);
			registro.getCampos().put(FrmTramitesControladorEnum.CODIGO_CALIDAD
					.getValue(), codigoCalidad);
			registro.getCampos().put(FrmTramitesControladorEnum.PAIS_ORIGEN
					.getValue(),codigoPais );
			registro.getCampos().put(FrmTramitesControladorEnum.DEPARTAMENTO_ORIGEN
					.getValue(),codigoDepartamento );
			registro.getCampos().put(FrmTramitesControladorEnum.CIUDAD_ORIGEN
					.getValue(),codigoCiudad );
			registro.getCampos().put(FrmTramitesControladorEnum.PROCEDENCIA
					.getValue(),procedencia );
			cargarListaProcedencia();
		}
		cargarListaTipoTramite();
		cargarListaDepartamentoOrigen();
		cargarListaCiudadOrigen();
		cambiarOcupacion();
		cambiarTipoPoblacion();
		// </CODIGO_DESARROLLADO>
		/**
		 * Manejo de botones para traer deuda de otros modulos
		 */
		String cod1= service.buscarEnLista(codigoProceso, 
				FrmTramitesControladorEnum.CODIGO.getValue(), 
				FrmTramitesControladorEnum.CODIGO_URL.getValue(), listaProcesos);		
		codServicio = (cod1 == null) ? null : Integer.parseInt(cod1);		 
		verDeuda= (cod1 != null);
	}
	/**
	 * Metodo ejecutado antes de realizar la insercion del registro.
	 * 
	 * @return true -> Permite realizar la insercion.
	 */
	@Override
	public boolean insertarAntes() {
		// <CODIGO_DESARROLLADO>
		String procedencia = registro.getCampos().get("PROCEDENCIA").toString();
		if(procedencia == null || procedencia.isEmpty()) {
			registro.getCampos().put("PROCEDENCIA", "00");
		}
		registro.getCampos().put(cCompania, compania);	
		registro.getCampos().remove("DEVOLUCION");
		registro.getCampos().remove("DESCRIPCION_DEVOLUCION");
		registro.getCampos().remove("MOTIVO_DEVOLUCION");
		registro.getCampos().remove("NIT_PROCEDENCIA");
		registro.getCampos().remove("NOMBRE_PROCEDENCIA");
		registro.getCampos().remove("NOMBRE_DEPENDENCIA");
		registro.getCampos().remove("RECEPCION_CORRES");
		registro.getCampos().remove("ENVIO_CORRES");
		registro.getCampos().remove("URL");
		registro.getCampos().remove("TIPO_MEDIO_NOM");
		registro.getCampos().remove("PRORROGA");
		registro.getCampos().remove("FECHA_PRORROGA");
		registro.getCampos().remove(DTramiteVariablesControladorEnum.ENVIA_CORREO.getValue());
		registro.getCampos().remove(DTramiteVariablesControladorEnum.PROCEDENCIA_AUT.getValue());
		registro.getCampos().put(cNumero, generarNumeroTramite());
		// </CODIGO_DESARROLLADO>

		if (cambiarBorde()) {
			JsfUtil.agregarMensajeAlerta(
					"Por favor, verifique que todos los campos requeridos estén completos en cada pestaña del formulario.");//--(CC:2155_)
			return false;
		}
        
        return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la insercion del registro.
	 * Desencadena el proceso para crear el detalle inicial del
	 * tramite.
	 * 
	 * @return true
	 */
	@Override
	public boolean insertarDespues() {
		// <CODIGO_DESARROLLADO>
		cargarDTramiteInicial();
		/**
		 * inicio de la solucion del ticket 7707490
		 *  @author ljdiaz
		 *  @description: este codigo realiza el llamado al metodo enviarCorreoRadicacion teniendo en cuenta dos condiciones previas de auerdo al requisito, no se va a enviar en todos los casos.
		 */
		try {
			if(FrmTramitesControladorEnum.DEPARTAMENTO_EMAIL_RADICA_TRAMITE_SI.getValue().equals(codigoDepartamento) && FrmTramitesControladorEnum.COMPANIA_EMAIL_RADICA_TRAMITE_SI.getValue().equals(compania)) {
				if(!registro.getCampos().get(cProcesos).toString().equals(FrmTramitesControladorEnum.PROCESO_EMAIL_RADICA_TRAMITE_NO.getValue().toString())) {
					enviarCorreoRadicacion();
				}
			}			
		} catch (IOException | MessagingException |NamingException | SQLException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		} 
				
		imprimirSticker=true;
		// </CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado antes de realizar la insercion o actualizacion
	 * del registro.
	 * 
	 * @return true -> Permite realizar la insercion o actualizacion
	 * del registro.
	 */
	@Override
	public boolean actualizarAntes() {
		// <CODIGO_DESARROLLADO>
		registro.getCampos().remove(cNodoONom);
		registro.getCampos().remove(cNodoANom);
		registro.getCampos().remove("SERIE_DOCUMENTAL_NOM");
		registro.getCampos().remove("FECHA_LIMITE");
		registro.getCampos().remove("DEVOLUCION");
		registro.getCampos().remove("DESCRIPCION_DEVOLUCION");
		registro.getCampos().remove("MOTIVO_DEVOLUCION");
		registro.getCampos().remove("NIT_PROCEDENCIA");
		registro.getCampos().remove("NOMBRE_PROCEDENCIA");
		registro.getCampos().remove("NOMBRE_DEPENDENCIA");
		registro.getCampos().remove("RECEPCION_CORRES");
		registro.getCampos().remove("ENVIO_CORRES");
		registro.getCampos().remove("URL");
		registro.getCampos().remove("TIPO_MEDIO_NOM");
		registro.getCampos().remove("PRORROGA");
		registro.getCampos().remove("FECHA_PRORROGA");
		registro.getCampos().remove(DTramiteVariablesControladorEnum.ENVIA_CORREO.getValue());
		registro.getCampos().remove(DTramiteVariablesControladorEnum.PROCEDENCIA_AUT.getValue());
		if (css != null) {
			registro.getCampos().remove(cCompania);
			registro.getCampos().remove(cNodoOrigen);
			registro.getCampos().remove("NODO_FINAL");
		}

		if (cambiarBorde()) {
			JsfUtil.agregarMensajeAlerta(
					"Por favor, verifique que todos los campos requeridos estén completos en cada pestaña del formulario.");
			return false;
		}
		// </CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la insercion y
	 * actualizacion del registro.
	 * 
	 * @return true
	 */
	@Override
	public boolean actualizarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado antes de realizar la eliminacion del registro.
	 * 
	 * @return true -> Permite eliminar el registro.
	 */
	@Override
	public boolean eliminarAntes() {
		// <CODIGO_DESARROLLADO>
		try {
			ejbWorkflowCero.eliminarTramite(compania,
					registro.getCampos().get(cProcesos).toString(),
					registro.getCampos().get(cTipoTramite).toString(),
					new BigInteger(registro.getCampos().get(cNumero)
							.toString()));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
			return false;
		}
		// </CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la eliminacion del
	 * registro.
	 * 
	 * @return true
	 */
	@Override
	public boolean eliminarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado desde un comando remoto en el boton volver del
	 * formulario
	 * 
	 */
	public void cargarBorde() {
        obligaCampos1 = "#000000 solid 1px";
    }
    
    
	public boolean cambiarBorde() {
	    // Siempre valida antes de revisar el borde
		obligaCampoDependencia = false;
	    camposBordeDependencia = null;
	    validarCheckDependencia();

	    boolean rta = false;
	    if (obligaCampoDependencia && camposBordeDependencia != null) {
	        if (cambiaBordeCampos(camposBordeDependencia)) {
	            rta = true;
	        }
	    }
	    return rta;
	}
    
	
	private boolean cambiaBordeCampos(Map<String, String> campos) {
	    boolean rta = false;
	    if (campos != null) {
	        for (Map.Entry<String, String> entry : campos.entrySet()) {
	            String campo = entry.getKey();
	            String variable = entry.getValue();

	            // Siempre resetea el borde a negro antes de validar
	            asignarEstilo(variable, "#000000 solid 1px");

	            // Solo pone rojo si falta el campo y la alerta se va a mostrar
	            if (SysmanFunciones.validarCampoVacio(registro.getCampos(), campo) && obligaCampoDependencia) {
	                asignarEstilo(variable, "#FF0000 solid 1px");
	                rta = true;
	            }
	        }
	    }
	    return rta;
	}
    
    private void asignarEstilo(String variable, String estilo) {
        switch (variable) {
            case "obligaCampos1": obligaCampos1 = estilo; break;
                
        }
    }
    /**
     * CFBARRERA CCC: 2155
     * Valida si se debe obligar el campo Dependencia según el proceso seleccionado.
     */
    private void validarCheckDependencia() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        Object nodoActual = regFormNodoActual.getCampos().get(FrmTramitesControladorEnum.NODO_ACTUAL.getValue());
        if (nodoActual == null || nodoActual.toString().isEmpty()) {
           
            nodoActual = registro.getCampos().get(FrmTramitesControladorEnum.NODO_ACTUAL.getValue());
            regFormNodoActual.getCampos().put(FrmTramitesControladorEnum.NODO_ACTUAL.getValue(), nodoActual);
        }
        
        param.put("CODIGOPROCESO", codigoProceso); 

        param.put("NODO_ACTUAL", nodoActual);

        try {
            Registro regAux = RegistroConverter.toRegistro(
                requestManager.get(UrlServiceUtil.getInstance()
                    .getUrlServiceByUrlByEnumID(
                        FrmTramitesControladorUrlEnum.URL1035013.getValue())
                    .getUrl(), param));
            
            if (regAux != null) {
            	
            	System.out.println("Campos recibidos: " + regAux.getCampos());
                Object resultadoObj = regAux.getCampos().get("RESULTADO");
                System.out.println("Valor de RESULTADO recibido: " + resultadoObj);
                manejaCheckDependencia = Integer.parseInt(SysmanFunciones.toString(resultadoObj));
            	
            	
            	manejaCheckDependencia = Integer.parseInt(SysmanFunciones.toString(regAux.getCampos().get("RESULTADO")));
                
                if (manejaCheckDependencia == 0) {
                	obligaCampoDependencia = true;
                    
                	camposBordeDependencia = new HashMap<>();
                	camposBordeDependencia.put("DEPENDENCIA", "obligaCampos1");
                }
            }

            
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

	public void ejecutarrcVolver() {
		// <CODIGO_DESARROLLADO>
		Direccionador direccionador = new Direccionador();
		int numForm = retornarFormularioIngreso();
		direccionador.setNumForm(Integer.toString(numForm));
		SessionUtil.redireccionarForma(direccionador, modulo);
		// </CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo utilizado para ejecutar el proceso por el que se crea el
	 * primer detalle del tramite, en donde el nodo final es igual al
	 * inicial.
	 */
	private void cargarDTramiteInicial() {
		try {
			/* Crear primer detalle tramite. */
			ejbWorkflowCero.tramitar(compania,
					registro.getCampos().get(cProcesos).toString(),
					registro.getCampos().get(cTipoTramite).toString(),
					new BigInteger(registro.getCampos().get(cNumero)
							.toString()),
					registro.getCampos().get(cNodoOrigen).toString(),
					registro.getCampos().get(cNodoActual).toString(),
					true, usuario," ", usuario);
		}
		catch (SystemException e1) {
			logger.error(e1.getMessage(), e1);
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4082"));
		}
	}
	/**
	 * @return La hora actual estandar.
	 */
	private Date recuperarHoraActualEstandar() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(1899, 12, 30);
		return calendar.getTime();
	}
	/**
	 * Consulta el consecutivo mas reciente generado para el tramite.
	 * 
	 * @return El consecutivo mas reciente generado para el tramite.
	 */
	private String recuperarConsecutivoDTramite() {
		Map<String, Object> param = new TreeMap<>();
		param.put(cCompania, compania);
		param.put(cProceso, registro.getCampos().get(cProcesos));
		param.put(cTipoTramite, registro.getCampos().get(cTipoTramite));
		param.put(FrmTramitesControladorEnum.TRAMITE.getValue(),
				registro.getCampos().get(cNumero));
		/*-Recuperar el ultimo consecutivo generado para el tramite.*/
		Registro miReg = null;
		try {
			miReg = RegistroConverter.toRegistro(
					requestManager.get(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmTramitesControladorUrlEnum.URL0003
									.getValue())
							.getUrl(),
							param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return miReg != null ? miReg.getCampos().get("CONSECUTIVO").toString()
				: "";
	}
	/**
	 * Valida que la informacion del nodo actual sea diligenciada
	 * antes de realizar el pase de nodo en el tramite.
	 * 
	 * @return true -> El usuario no diligencio alguna variable
	 * obligatoria o que maneja adjunto.
	 */
	private boolean validarInformacionDetallada() {
		try {
			ejbWorkflowCero.validarInfDetallada(compania,
					registro.getCampos().get(cProcesos).toString(),
					registro.getCampos().get(cTipoTramite).toString(),
					new BigInteger(registro.getCampos().get(cNumero)
							.toString()),
					new BigInteger(recuperarConsecutivoDTramite()),
					registro.getCampos().get(cNodoActual).toString());
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
			return true;
		}
		return false;
	}
	/**
	 * Metodo que genera el numero consecutivo del tramite antes de
	 * ser creado.
	 * 
	 * @return El numero consecutivo del tramite.
	 */
	private long generarNumeroTramite() {
		Map<String, Object> param = new TreeMap<>();
		param.put(cCompania, compania);
		param.put(FrmTramitesControladorEnum.PROCESOS.getValue(), registro.getCampos().get(cProcesos));
		param.put(FrmTramitesControladorEnum.TIPOTRAMITE.getValue(), registro.getCampos().get(FrmTramitesControladorEnum.TIPO_TRAMITE.getValue()));
		long numero = 0;
		try {
			Registro regAux = RegistroConverter.toRegistro(
					requestManager.get(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmTramitesControladorUrlEnum.URL8947
									.getValue())
							.getUrl(), param));
			String criterio = "";
			String inicial = "";
			String tabla = SysmanFunciones.concatenar(GenericUrlEnum.TRAMITES.getTable(), " T INNER JOIN ",
					GenericUrlEnum.TIPOTRAMITES.getTable()," TT ON T.TIPO_TRAMITE=TT.TIPOTRAMITE AND T.COMPANIA=TT.COMPANIA");
			String campo = SysmanFunciones.concatenar("T.",cNumero);
			if((boolean) regAux.getCampos().get(FrmTramitesControladorEnum.NUMERACION_UNICA.getValue())) {
				criterio = SysmanFunciones.concatenar("T.COMPANIA = ''", compania, "'' AND TT.NUMERACION_UNICA NOT IN (0) ",
						//" AND T.PROCESOS = ''",	registro.getCampos().get(cProcesos).toString(),"''"
						"AND LENGTH(T.NUMERO) = 9 ",
						"AND T.NUMERO LIKE ''",String.valueOf(SysmanFunciones.ano(new Date())),"%''");
				inicial = String.valueOf(SysmanFunciones.ano(new Date()))+ "00001";
			}else {
				String anoActual = String.valueOf(SysmanFunciones.ano(new Date()));
				criterio = SysmanFunciones.concatenar(
						"T.COMPANIA = ''", compania,
						"'' AND SUBSTR(T.NUMERO,1,4) = ''", anoActual,
						"'' AND LENGTH(T.NUMERO) = 9 AND T.PROCESOS = ''",
						registro.getCampos().get(cProcesos).toString(),
						"'' AND T.TIPO_TRAMITE = ''",
						registro.getCampos().get("TIPO_TRAMITE").toString(),
						"''"
						);
				inicial = String.valueOf(SysmanFunciones.ano(new Date()))+ "00001";
			}
			numero = ejbSysmanUtil.generarConsecutivoConValorInicial(
					tabla, criterio,
					cNumero,inicial);
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return numero;
	}
	/**
	 * Metodo que asigna el nodo origen al tramite cuando el proceso
	 * tiene el indicador de nodo intermedio desactivado.
	 */
	private void asignarEtapaOrigen() {
		Map<String, Object> param = new TreeMap<>();
		param.put(cCompania, compania);
		param.put(cProceso, codigoProceso);
		try {
			Registro regAux = RegistroConverter.toRegistro(
					requestManager.get(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmTramitesControladorUrlEnum.URL0004
									.getValue())
							.getUrl(), param));
			registro.getCampos().put(FrmTramitesControladorEnum.NODO_ACTUAL.getValue(),
					regAux.getCampos().get("CODIGO"));
			registro.getCampos().put(FrmTramitesControladorEnum.NODO_ORIGEN.getValue(),
					regAux.getCampos().get("CODIGO"));
			registro.getCampos().put(FrmTramitesControladorEnum.NODO_A_NOM.getValue(),
					regAux.getCampos().get("NOMBRE"));
			registro.getCampos().put(FrmTramitesControladorEnum.NODO_O_NOM.getValue(),
					regAux.getCampos().get("NOMBRE"));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * Metodo utilizado para recuperar valores propios del proceso,
	 * como:
	 * <li>NODO_INTERMEDIO
	 */
	private void consultarProceso() {
		Map<String, Object> param = new TreeMap<>();
		param.put(cCompania, compania);
		param.put(cProceso, codigoProceso);
		try {
			Registro regAux = RegistroConverter.toRegistro(
					requestManager.get(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmTramitesControladorUrlEnum.URL0005
									.getValue())
							.getUrl(), param));
			if (regAux != null) {
				indNodoIntermedio = (Boolean) regAux.getCampos()
						.get("NODO_INTERMEDIO");
			}
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * Metodo utilizado para recuperar valores propios del detalle del
	 * tramite actual, como:
	 * <li>FORMATO
	 * <li>FORMATO_FECHA
	 * <li>MENU
	 */
	private void consultarDTramiteActual() {
		Map<String, Object> param = new TreeMap<>();
		param.put(cCompania, compania);
		param.put(cProceso, codigoProceso);
		param.put(cTipoTramite, registro.getCampos().get(cTipoTramite));
		param.put(FrmTramitesControladorEnum.TRAMITE.getValue(),
				registro.getCampos().get(cNumero));
		param.put("DTRAMITE", recuperarConsecutivoDTramite());
		try {
			Registro regAux = RegistroConverter.toRegistro(
					requestManager.get(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmTramitesControladorUrlEnum.URL0006
									.getValue())
							.getUrl(), param));
			if (regAux != null) {
				formatoCod = SysmanFunciones
						.nvl(regAux.getCampos().get("FORMATO"), "")
						.toString();
				formatoFecha = (Date) regAux.getCampos().get("FORMATO_FECHA");
				menuNodoActual = SysmanFunciones
						.nvl(regAux.getCampos().get("MENU"), "")
						.toString();
			}
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * Metodo utilizado para recuperar valores propios del menu
	 * configurado para el nodo actual, como:
	 * <li>NOMBRE
	 * <li>PARAMETRO
	 * <li>APLICACION
	 */
	private void consultarMenuNodoActual() {
		Map<String, Object> param = new TreeMap<>();
		param.put(cCodigo, menuNodoActual);
		try {
			Registro regAux = RegistroConverter.toRegistro(
					requestManager.get(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmTramitesControladorUrlEnum.URL0009
									.getValue())
							.getUrl(), param));
			if (regAux != null) {
				menuNodoActualNom = SysmanFunciones
						.nvl(regAux.getCampos()
								.get(GeneralParameterEnum.NOMBRE
										.getName()),
								"")
						.toString();
				regFormNodoActual.getCampos()
				.put(FrmTramitesControladorEnum.CODIGO_FORMULARIO
						.getValue(),
						regAux.getCampos().get(
								"PARAMETRO"));
				moduloNodoActual = SysmanFunciones
						.nvl(regAux.getCampos().get("APLICACION"), "")
						.toString();
			}
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * Metodo utilizado para recuperar valores propios del formulario
	 * asociado al menu configurado para el nodo actual, como:
	 * <li>CODIGO_FORMULARIO
	 * <li>MODAL
	 */
	private void consultarFormNodoActual() {
		Map<String, Object> param = new TreeMap<>();
		param.put(cCodigo,
				regFormNodoActual.getCampos()
				.get(FrmTramitesControladorEnum.CODIGO_FORMULARIO
						.getValue()));
		try {
			Registro regAux = RegistroConverter.toRegistro(
					requestManager.get(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmTramitesControladorUrlEnum.URL0010
									.getValue())
							.getUrl(), param));
			if (regAux != null) {
				regFormNodoActual = regAux;
			}
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/** Metodo que ejecuta el proceso para generar la plantilla. */
	private void generarPlantilla() {
		/*-Parametros a reemplazar en la consulta de la plantilla */
		String[] campos = new String[7];
		campos[0] = FrmDTramitesControladorEnum.PR_PROCESO.getValue();
		campos[1] = FrmDTramitesControladorEnum.PR_NODO_ORIGEN.getValue();
		campos[2] = FrmDTramitesControladorEnum.PR_NODO_ORIGEN_NOM.getValue();
		campos[3] = FrmDTramitesControladorEnum.PR_TIPOTRAMITE.getValue();
		campos[4] = FrmDTramitesControladorEnum.PR_TRAMITE.getValue();
		campos[5] = FrmDTramitesControladorEnum.PR_PROCESO_NOM.getValue();
		campos[6] = FrmDTramitesControladorEnum.PR_TIPOTRAMITE_NOM.getValue();
		Object[] valores = new Object[7];
		valores[0] = registro.getCampos().get(cProcesos);
		valores[1] = registro.getCampos().get(cNodoActual);
		valores[2] = registro.getCampos().get(cNodoANom);
		valores[3] = registro.getCampos().get(cTipoTramite);
		valores[4] = registro.getCampos().get(cNumero);
		valores[5] = procesoNom;
		valores[6] = tipoTramiteNom;
		SessionUtil.cargarModalDatosFlashCerrar(
				Integer.toString(
						GeneralCodigoFormaEnum.PREPARARPLANTILLA
						.getCodigo()),
				SessionUtil.getModulo(), campos, valores);
	}
	/**
	 * Recupera la lista de variables generales del tr&aacute;mite
	 * @return La lista de variables con su respectivo valor, en donde
	 */
	private List<Registro> recuperarVariableGeneral() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.TIPO_TRAMITE.getName(), registro
				.getCampos()
				.get(GeneralParameterEnum.TIPO_TRAMITE.getName()));
		param.put(cProceso, registro.getCampos().get(cProcesos));
		param.put(FrmTramitesControladorEnum.TRAMITE.getValue(),
				registro.getCampos().get(cNumero));
		List<Registro> list = null;
		try {
			list = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmTramitesControladorUrlEnum.URL01847
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return list;
	}
	private Map<String, Object> paramDeuda(ServicioEnum servicio){
		Map<String, Object> para = new TreeMap<>();
		List<Registro> variables = recuperarVariableGeneral();
		para.put(VariableDeudaEnum.IDCOMPANIA.getName(), compania);
		for (Registro reg : variables) {
			switch (servicio) {
			case PREDIAL:
				if (reg.getCampos().get("VARIABLE").toString().equals(VariableDeudaEnum.CODCATASTRAL.getName())) {
					para.put(VariableDeudaEnum.CODCATASTRAL.getName(), reg.getCampos().get("VALOR_TEXTO").toString());
				}
				if (reg.getCampos().get("VARIABLE").toString().equals(VariableDeudaEnum.VIGENCIAINICIAL.getName())) {
					para.put(VariableDeudaEnum.INICIAL.getName(), reg.getCampos().get("VALOR").toString());
				}
				if (reg.getCampos().get("VARIABLE").toString().equals(VariableDeudaEnum.VIGENCIAFINAL.getName())) {
					para.put(VariableDeudaEnum.FINAL.getName(), reg.getCampos().get("VALOR").toString());
				}
				break;
			case COMERCIO:
				if (reg.getCampos().get("VARIABLE").toString().equals(VariableComercioEnum.IDENTIFICACION.getName())) {
					para.put(VariableComercioEnum.IDENTIFICACION.getName(), reg.getCampos().get("VALOR_TEXTO").toString());
				}
				if (reg.getCampos().get("VARIABLE").toString().equals(VariableComercioEnum.VIGENCIAINICIAL.getName())) {
					para.put(VariableComercioEnum.INICIAL.getName(), reg.getCampos().get("VALOR").toString());
				}
				if (reg.getCampos().get("VARIABLE").toString().equals(VariableComercioEnum.VIGENCIAFINAL.getName())) {
					para.put(VariableComercioEnum.FINAL.getName(), reg.getCampos().get("VALOR").toString());
				}
				if (reg.getCampos().get("VARIABLE").toString().equals(VariableComercioEnum.RECALCULA.getName())) {
					para.put(VariableComercioEnum.RECALCULA.getName(), reg.getCampos().get("VALOR_TEXTO").toString().equals("Si")?true:false);
				}
			default:
				break;
			}			
		}
		return para;
	}
	/**
	 * Recupera la lista de variables diligenciadas en cada una de las
	 * etapas del tramite.
	 * 
	 * @return La lista de variables con su respectivo valor, en donde
	 * cada variable esta separada por coma(,).<br>
	 * <strong>Ejemplo:</strong>
	 * <li>'VALOR_VAR_1' VAR1, 'VALOR_VAR_2' VAR2
	 */
	private String recuperarVariablesTramite() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.TIPO_TRAMITE.getName(), registro
				.getCampos()
				.get(GeneralParameterEnum.TIPO_TRAMITE.getName()));
		param.put(cProceso, registro.getCampos().get(cProcesos));
		param.put(FrmTramitesControladorEnum.TRAMITE.getValue(),
				registro.getCampos().get(cNumero));
		List<Registro> list = null;
		try {
			list = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmTramitesControladorUrlEnum.URL0008
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		String variables = "";
		for (Registro r : list) {
			variables = SysmanFunciones.concatenar(",",
					r.getCampos().get(cCodigo).toString(), variables);
		}
		return variables;
	}
	/**
	 * Metodo que consulta el tipo de RACI asignado al usuario que
	 * ingreso al formulario.
	 */
	private void validarIsUsuarioResponsable() {
		Map<String, Object> param = new TreeMap<>();
		param.put(cCompania, compania);
		param.put(cProceso, codigoProceso);
		param.put("NODO", registro.getCampos().get(cNodoActual));
		param.put(GeneralParameterEnum.USUARIO.getName(), usuario);
		param.put("RACI", 9); // RACI Responsable
		try {
			Registro regAux = RegistroConverter.toRegistro(
					requestManager.get(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmTramitesControladorUrlEnum.URL0011
									.getValue())
							.getUrl(), param));
			if (regAux != null) {
				indUsuarioResponsable = !SysmanFunciones.validarCampoVacio(
						regAux.getCampos(), "EXISTE");
			}
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * Retorna el numero del formulario asociado a la opcion de menu
	 * desde la cual se accedio al formulario de tramites.
	 * 
	 * @return Numero del formulario.
	 */
	private int retornarFormularioIngreso() {
		return "350202".equals(menuActual)
				? GeneralCodigoFormaEnum.FRMMONITORTRAMITES_CONTROLADOR.getCodigo()
						: GeneralCodigoFormaEnum.FRM_TRAMITES_CONTROLADOR.getCodigo();
	}
	private void cargarValoresIniciales() {
		codigoPais=SessionUtil.getCompaniaIngreso().getCodigoPais();
		codigoDepartamento = SessionUtil.getCompaniaIngreso().getCodigoDepartamento();
		codigoCiudad=SessionUtil.getCompaniaIngreso().getCodigoCiudad();
		codigoCalidad="00";
		procedencia = "";
	}
	public void oprimirprocedencia() {
		Map<String, Object> param = new TreeMap<>();
		param.put(FrmTramitesControladorEnum.PR_ROWKEY.getValue(), css);
		Direccionador dir = new Direccionador();
		dir.setNumForm(Integer.toString(
				GeneralCodigoFormaEnum.FRM_PROCEDENCIA_TRAMITE_CONTROLADOR
				.getCodigo()));
		dir.setParametros(param);
		SessionUtil.redireccionarForma(dir, modulo);
	}
	/**
	 * Metodo ejecutado al oprimir el boton sticker en la
	 * vista.
	 */
	public void oprimirsticker() {
		archivoDescarga = null;		
		correspondencia = SysmanFunciones.nvl(registro.getCampos().get("CORRESPONDENCIA"), "0").toString();
		envioCorrespondencia = (boolean) registro.getCampos().get("ENVIO_CORRES");	
		recepcionCorres = (boolean) registro.getCampos().get("RECEPCION_CORRES");
		
		if(!recepcionCorres && !envioCorrespondencia) {
			
			visibleAlerta = true;
			
		}else if(recepcionCorres && correspondencia.equals("0") || 
				  envioCorrespondencia && correspondencia.equals("0")) {
			JsfUtil.agregarMensajeAlerta("Debe selecionar el indicador de Externo o Interno");
			
		}else {
			
		generaInforme(ReportesBean.FORMATOS.PDF);
		
		}
	}
	/**
	 * Metodo que retorna el nombre del reporte a generar
	 */
	public String getReporte()
	{
		String reporte="";
		try {

			if(!recepcionCorres && !envioCorrespondencia) {
				//"llamar al dialogo"
				reporte= ejbSysmanUtil.consultarParametro(compania, "STICKER TRAMITE", modulo, new Date(), false);
				
			}else {	
				
			if(recepcionCorres && correspondencia.equals("2")) {

				reporte= ejbSysmanUtil.consultarParametro(compania, "STICKER TRAMITE", modulo, new Date(), false);

			}else if(recepcionCorres && correspondencia.equals("1")) {

				reporte= ejbSysmanUtil.consultarParametro(compania, "STICKER INTERNA ENVIADA", modulo, new Date(), false);

			}else if(envioCorrespondencia && (correspondencia.equals("1") || correspondencia.equals("2"))){

				reporte= ejbSysmanUtil.consultarParametro(compania, "STICKER EXTERNA ENVIADA", modulo, new Date(), false);

			} 
			}

				

		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		// reporte="002051StickerTramites"; 
		return reporte;
	}
	/**
	 * Metodo que genera el reporte fisico 
	 * @param ReportesBean.FORMATOS Formato del reporte a generar
	 */
	private void generaInforme(ReportesBean.FORMATOS formato)
	{
		String reporte = getReporte();
		try
		{
			HashMap<String, Object> reemplazar = new HashMap<>();
			reemplazar.put("procesos", registro.getCampos().get(FrmTramitesControladorEnum.PROCESOS.getValue()));
			reemplazar.put("numero", registro.getCampos().get(FrmTramitesControladorEnum.NUMERO.getValue()));
			reemplazar.put("tipotramites", registro.getCampos().get(FrmTramitesControladorEnum.TIPO_TRAMITE.getValue()));
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("PR_PARA", "");
			parametros.put("PR_ESCUDO",
					ejbSysmanUtil.consultarParametro(compania,
							"IMAGEN ESCUDO IDIPRON", SessionUtil.getModulo(),
							new Date(), 
							false));
			parametros.put("PR_BOGOTA",
					ejbSysmanUtil.consultarParametro(compania,
							"IMAGEN BOGOTA IDIPRON", SessionUtil.getModulo(),
							new Date(), 
							false));
			Reporteador.resuelveConsulta(reporte,
					Integer.parseInt(SessionUtil.getModulo()),
					reemplazar, parametros);
			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
					ConectorPool.ESQUEMA_SYSMAN, formato);
		}
		catch (FileNotFoundException ex)
		{
			JsfUtil.agregarMensajeInformativo(
					idioma.getString(Constantes.MSM_INFORME_NO_EXISTE)
					+ ex.getMessage() + " " + reporte);
		}
		catch (JRException | IOException ex)
		{
			JsfUtil.agregarMensajeError(
					idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA)
					+ ex.getMessage());
		}
		catch (SysmanException e)
		{
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Metodo encargado armar el mensaje de radicacion del tramite y enviarlo por el correo configurado al correo del destinatario
	 * @throws NamingException
	 * @throws SQLException
	 * @throws MessagingException
	 * @throws IOException
	 */
	private void enviarCorreoRadicacion() throws NamingException, SQLException, MessagingException, IOException {
		//declaraciones a nivel de metodo solo se usaran en este metodo
		String nombreRadica = null;
		String direccionRadica = null;
		String telefonoRadica = null;
		Object telefonoRadicaTemp = null;
		String emailRadica = null;
		boolean validoEamil = false;
		correo = new ServidorCorreo();
		// aqui logica de envio verificar si esta el registros que debe estar
		// Inicia envio de E-mail
		String tramite = registro.getCampos().get("NUMERO").toString();
		try {
			// consulta la procedencia de la cual obtendra los datos de la presona quien radica, de acuerdo a la compaï¿½ia y el codigo de la procedencia
			String procedencia = registro.getCampos().get("PROCEDENCIA").toString();
			if(procedencia == null || procedencia.isEmpty()) {
				registro.getCampos().put("PROCEDENCIA", "00");
			}
			Map<String, Object> paramProcedencia = new HashMap<>();
			paramProcedencia.put(GeneralParameterEnum.KEY_COMPANIA.getName(), compania);
			paramProcedencia.put(FrmTramitesControladorEnum.KEY_CODIGO.getValue(), procedencia);
			rsProcedencia = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(FrmTramitesControladorUrlEnum.URL104000R.getValue())
							.getUrl(),
							paramProcedencia));
			//desglosa los datos consultados de la procedencia del tramite
			nombreRadica = rsProcedencia.getCampos().get("NOMBRE").toString();
		    direccionRadica = rsProcedencia.getCampos().get("DIRECCION").toString();
		    telefonoRadicaTemp = rsProcedencia.getCampos().get("TELEFONO");
			if(telefonoRadicaTemp == null) {
				telefonoRadica = "0";
			} else
				telefonoRadica = rsProcedencia.getCampos().get("TELEFONO").toString();
			emailRadica = rsProcedencia.getCampos().get("DIRECCIONE_MAIL").toString();
			validoEamil = ValidarMail(emailRadica);
			if(!validoEamil) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("Email Incorrecto"));
			}
			// consulta los datos del email configurado por la empresa o compaï¿½ia, con el cual se enviaran los correos de radicacion del tramite
			// nota este correo debe tener cofigurado la salida de correo por aplicaciones.
			Map<String, Object> paramEmail = new HashMap<>();
			paramEmail.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			paramEmail.put("APLICACION", SessionUtil.getModulo());

			rsEmail = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(FrmTramitesControladorUrlEnum.URL002.getValue())
							.getUrl(),
							paramEmail));
			if(rsEmail!= null) {
				/**
				 * desglosza la informacion necesaria para el envio del correo, esta informacion esta alamacenada en la base de datos de datos.
				 * @hostName: nombre del host smtp al que pertenence el correo encargado de enviar las notificaciones, por ejemplo: gmail (stmp.gmail.com)
				 * @user: el correo que sera designado para el envio de los correos de notificaciones, por ejemplo: compania@gmial.com, 
				 * Este correo debera tener configurado la opcion contraseï¿½a para aplicaciones.
				 * @constrasena: esta contraseï¿½a la ingresa la compaï¿½ia que esta configurando el correo para el envio de notificaciones, esa contraseï¿½a es
				 * de aplicacion, por medio de ella el servidor tiene acceso al correo para enviar la notificacion.
				 */
				hostName = rsEmail.getCampos().get("DOMINIO").toString();
				user = rsEmail.getCampos().get("EMAIL").toString();
				contrasena = rsEmail.getCampos().get("PASSWORD").toString();
			
				correo.setSmtpHostName(hostName);
				correo.setSmtpHostPort(587);
				correo.setSmtpAuthUser(user);
				correo.setSmtpAuthName(user);
				correo.setSmtpAuthPwd(contrasena);
				System.setProperty("mail.mime.charset","Cp1252");
				// se crea el asunto y cuerpo del mensaje que se enviara notificando la radicacion.
				String strAsunto = "Respuesta electrÃ³nica radicado No. "+ tramite;
				String strMensaje = idioma.getString("TB_TB4397").replace("#nombreRadica#", nombreRadica)
						.replace("#direccionRadica#", direccionRadica)
						.replace("#telefonoRadica#", telefonoRadica)
						.replace("#emailRadica#", emailRadica)
						.replace("#tramiteRadica#", tramite);
							
						String[] direcciones = new String[] {emailRadica}; 
						correo.enviar(direcciones, strAsunto, strMensaje);
						JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4393"));					
				}else {	
					JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4389"));						
				}
		} catch ( /*| SysmanException*/  SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	
	public void alertaProrroga() {
		try {
			
			String mostrarMsj = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania, "MOSTRAR MENSAJE DE ETAPA DE PRORROGA", modulo, new Date(), false),"NO");
			boolean prorroga = (boolean) registro.getCampos().get("PRORROGA");
			
			if(mostrarMsj.equals("SI") && prorroga ) {

				JsfUtil.agregarMensajeInformativo("No olvide diligenciar el motivo de prorroga en la opciÃ³n \"InformaciÃ³n Detallada \". ");
			}
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());

		}
	}
	
	public void cambiarNombreDependencia() {
		//JM CC1558 se agrega funcion que no hace nada por los momentos
		//pero es para que no se rompa el formulario 
	}

	// <SET_GET_ATRIBUTOS>
	/**
	 * @return the archivoDescarga
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	/**
	 * @param archivoDescarga the archivoDescarga to set
	 */
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}
	public boolean isIndNodoIntermedio() {
		return indNodoIntermedio;
	}
	public void setIndNodoIntermedio(boolean indNodoIntermedio) {
		this.indNodoIntermedio = indNodoIntermedio;
	}
	public String getFormatoCod() {
		return formatoCod;
	}
	public void setFormatoCod(String formatoCod) {
		this.formatoCod = formatoCod;
	}
	public String getMenuNodoActualNom() {
		return menuNodoActualNom;
	}
	public void setMenuNodoActualNom(String menuNodoActualNom) {
		this.menuNodoActualNom = menuNodoActualNom;
	}
	public boolean isIndUsuarioResponsable() {
		return indUsuarioResponsable;
	}
	public void setIndUsuarioResponsable(boolean indUsuarioResponsable) {
		this.indUsuarioResponsable = indUsuarioResponsable;
	}
	// </SET_GET_ATRIBUTOS>
	// <SET_GET_LISTAS>
	/**
	 * Retorna la lista listaProcedencia
	 * 
	 * @return listaProcedencia
	 */
	public RegistroDataModelImpl getListaProcedencia() {
		return listaProcedencia;
	}
	/**
	 * Asigna la lista listaProcedencia
	 * 
	 * @param listaProcedencia
	 * Variable a asignar en  listaProcedencia
	 */
	public void setListaProcedencia(RegistroDataModelImpl listaProcedencia) {
		this.listaProcedencia = listaProcedencia;
	}
	/**
	 * Retorna la lista listaEstado
	 * 
	 * @return listaEstado
	 */
	public List<Registro> getListaEstado() {
		return listaEstado;
	}
	/**
	 * Asigna la lista listaEstado
	 * 
	 * @param listaEstado
	 * Variable a asignar en listaEstado
	 */
	public void setListaEstado(List<Registro> listaEstado) {
		this.listaEstado = listaEstado;
	}
	/**
	 * Retorna la lista listaCiudadOrigen
	 * 
	 * @return listaCiudadOrigen
	 */
	public List<Registro> getListaCiudadOrigen() {
		return listaCiudadOrigen;
	}
	/**
	 * Asigna la lista listaCiudadOrigen
	 * 
	 * @param listaCiudadOrigen
	 * Variable a asignar en listaCiudadOrigen
	 */
	public void setListaCiudadOrigen(List<Registro> listaCiudadOrigen) {
		this.listaCiudadOrigen = listaCiudadOrigen;
	}
	/**
	 * Retorna la lista listaTipoReferencia
	 * 
	 * @return listaTipoReferencia
	 */
	public List<Registro> getListaTipoReferencia() {
		return listaTipoReferencia;
	}
	/**
	 * Asigna la lista listaTipoReferencia
	 * 
	 * @param listaTipoReferencia
	 * Variable a asignar en listaTipoReferencia
	 */
	public void setListaTipoReferencia(List<Registro> listaTipoReferencia) {
		this.listaTipoReferencia = listaTipoReferencia;
	}
	/**
	 * Retorna la lista listaTipoReferenciaUno
	 * 
	 * @return listaTipoReferenciaUno
	 */
	public List<Registro> getListaTipoReferenciaUno() {
		return listaTipoReferenciaUno;
	}
	/**
	 * Asigna la lista listaTipoReferenciaUno
	 * 
	 * @param listaTipoReferenciaUno
	 * Variable a asignar en listaTipoReferenciaUno
	 */
	public void setListaTipoReferenciaUno(
			List<Registro> listaTipoReferenciaUno) {
		this.listaTipoReferenciaUno = listaTipoReferenciaUno;
	}
	/**
	 * Retorna la lista listaTipoTramite
	 * 
	 * @return listaTipoTramite
	 */
	public List<Registro> getListaTipoTramite() {
		return listaTipoTramite;
	}
	/**
	 * Asigna la lista listaTipoTramite
	 * 
	 * @param listaTipoTramite
	 * Variable a asignar en listaTipoTramite
	 */
	public void setListaTipoTramite(List<Registro> listaTipoTramite) {
		this.listaTipoTramite = listaTipoTramite;
	}
	/**
	 * Retorna la lista listaProcesos
	 * 
	 * @return listaProcesos
	 */
	public List<Registro> getListaProcesos() {
		return listaProcesos;
	}
	/**
	 * Asigna la lista listaProcesos
	 * 
	 * @param listaProcesos
	 * Variable a asignar en listaProcesos
	 */
	public void setListaProcesos(List<Registro> listaProcesos) {
		this.listaProcesos = listaProcesos;
	}
	/**
	 * Retorna la lista listaModulo
	 * 
	 * @return listaModulo
	 */
	public List<Registro> getListaModulo() {
		return listaModulo;
	}
	/**
	 * Asigna la lista listaModulo
	 * 
	 * @param listaModulo
	 * Variable a asignar en listaModulo
	 */
	public void setListaModulo(List<Registro> listaModulo) {
		this.listaModulo = listaModulo;
	}
	/**
	 * Retorna la lista listaPaisOrigen
	 * 
	 * @return listaPaisOrigen
	 */
	public List<Registro> getListaPaisOrigen() {
		return listaPaisOrigen;
	}
	/**
	 * Asigna la lista listaPaisOrigen
	 * 
	 * @param listaPaisOrigen
	 * Variable a asignar en listaPaisOrigen
	 */
	public void setListaPaisOrigen(List<Registro> listaPaisOrigen) {
		this.listaPaisOrigen = listaPaisOrigen;
	}
	/**
	 * Retorna la lista listaDepartamentoOrigen
	 * 
	 * @return listaDepartamentoOrigen
	 */
	public List<Registro> getListaDepartamentoOrigen() {
		return listaDepartamentoOrigen;
	}
	/**
	 * Asigna la lista listaDepartamentoOrigen
	 * 
	 * @param listaDepartamentoOrigen
	 * Variable a asignar en listaDepartamentoOrigen
	 */
	public void setListaDepartamentoOrigen(
			List<Registro> listaDepartamentoOrigen) {
		this.listaDepartamentoOrigen = listaDepartamentoOrigen;
	}
	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	public RegistroDataModelImpl getListaNodoOrigen() {
		return listaNodoOrigen;
	}
	public void setListaNodoOrigen(RegistroDataModelImpl listaNodoOrigen) {
		this.listaNodoOrigen = listaNodoOrigen;
	}
	public RegistroDataModelImpl getListaCbSerieDocumental() {
		return listaCbSerieDocumental;
	}
	public void setListaCbSerieDocumental(
			RegistroDataModelImpl listaCbSerieDocumental) {
		this.listaCbSerieDocumental = listaCbSerieDocumental;
	}
	// </SET_GET_LISTAS_COMBO_GRANDE>
	// <SET_GET_LISTAS_SUBFORM>
	// </SET_GET_LISTAS_SUBFORM>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_ADICIONALES>
	/**
	 * @return the varVolver
	 */
	public boolean isVarVolver() {
		return varVolver;
	}
	/**
	 * @param varVolver
	 * the varVolver to set
	 */
	public void setVarVolver(boolean varVolver) {
		this.varVolver = varVolver;
	}
	/**
	 * @return the imprimirSticker
	 */
	public boolean isImprimirSticker() {
		return imprimirSticker;
	}
	/**
	 * @param imprimirSticker
	 * the imprimirSticker to set
	 */
	public void setImprimirSticker(boolean imprimirSticker) {
		this.imprimirSticker = imprimirSticker;
	}
	/**
	 * @return enviarCorreoConfirmacion
	 */
	public boolean isEnviarCorreoConfirmacion() {
		return enviarCorreoConfirmacion;
	}
	/**
	 * @param enviarCorreoConfirmacion
	 * el enviarCorreoConfirmacion a settear
	 */
	public void setEnviarCorreoConfirmacion(boolean enviarCorreoConfirmacion) {
		this.enviarCorreoConfirmacion = enviarCorreoConfirmacion;
	}
	public List<Registro> getListaTipomedio() {
		return listaTipomedio;
	}
	public void setListaTipomedio(List<Registro> listaTipomedio) {
		this.listaTipomedio = listaTipomedio;
	}
	public List<Registro> getListaMotivoDevolucion() {
		return listaMotivoDevolucion;
	}
	public void setListaMotivoDevolucion(List<Registro> listaMotivoDevolucion) {
		this.listaMotivoDevolucion = listaMotivoDevolucion;
	}
	public List<Registro> getListaTipoPoblacion() {
		return listaTipoPoblacion;
	}
	public void setListaTipoPoblacion(List<Registro> listaTipoPoblacion) {
		this.listaTipoPoblacion = listaTipoPoblacion;
	}
	public List<Registro> getListaVulnerabilidad() {
        return listaVulnerabilidad;
    }
	public void setListaVulnerabilidad(List<Registro> listaVulnerabilidad) {
        this.listaVulnerabilidad = listaVulnerabilidad;
    }
	public List<Registro> getListaOcupacion() {
        return listaOcupacion;
    }
	public void setListaOcupacion(List<Registro> listaOcupacion) {
        this.listaOcupacion = listaOcupacion;
    }
	public List<Registro> getListaEscolaridad() {
        return listaEscolaridad;
    }
	public void setListaEscolaridad(List<Registro> listaEscolaridad) {
        this.listaEscolaridad = listaEscolaridad;
    }
	public List<Registro> getListaRangoEdad() {
        return listaRangoEdad;
    }
	public void setListaRangoEdad(List<Registro> listaRangoEdad) {
        this.listaRangoEdad = listaRangoEdad;
    }
	public List<Registro> getListaTipoPersona() {
        return listaTipoPersona;
    }
	public void setListaTipoPersona(List<Registro> listaTipoPersona) {
        this.listaTipoPersona = listaTipoPersona;
    }	
	
	public RegistroDataModelImpl getListacmbDependencia() {
		return listacmbDependencia;
	}
	/**
	 * Asigna la lista listacmbDependencia
	 * 
	 * @param listacmbDependencia
	 * Variable a asignar en  listacmbDependencia
	 */
	public void setListacmbDependencia(RegistroDataModelImpl listacmbDependencia) {
		this.listacmbDependencia = listacmbDependencia;
	}
	/**
	 * @return the verDeuda
	 */
	public boolean isVerDeuda() {
		return verDeuda;
	}
	public String nivelU() {
		Map<String, Object> param = new HashMap<>();
		param.put(
				FrmTramitesControladorEnum.USUARIO_INTERNO.getValue(),
				usuario);
		String nivel="9";
		try {
			nivelUsuario = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmmonitortramitesControladorUrlEnum.URL002.getValue())
							.getUrl(),
							param));
			if (nivelUsuario != null) {
				nivel = nivelUsuario.getCampos().get("NIVEL_USUARIO").toString();
			}
			else {
				nivel="9";
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		if(nivel=="true") {
			nivel="1";
		}
		return nivel;
	}
	public static boolean ValidarMail(String email) {
        // Patron para validar el email
        Pattern pattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
 
        Matcher mather = pattern.matcher(email);
        return mather.find();
    }
	// </SET_GET_ADICIONALES>
	/**
	 * @return the visibleAlerta
	 */
	public boolean isVisibleAlerta() {
		return visibleAlerta;
	}
	/**
	 * @param visibleAlerta the visibleAlerta to set
	 */
	public void setVisibleAlerta(boolean visibleAlerta) {
		this.visibleAlerta = visibleAlerta;
	}
	public boolean isVisiblePoblacion() {
		return visiblePoblacion;
	}
	public void setVisiblePoblacion(boolean visiblePoblacion) {
		this.visiblePoblacion = visiblePoblacion;
	}
	public boolean isVisibleOcupacion() {
		return visibleOcupacion;
	}
	public void setVisibleOcupacion(boolean visibleOcupacion) {
		this.visibleOcupacion = visibleOcupacion;
	}
	/**
	 * @return the maxCaracteresDescripcion
	 */
	public int getMaxCaracteresDescripcion() {
		return maxCaracteresDescripcion;
	}
	/**
	 * @param maxCaracteresDescripcion the maxCaracteresDescripcion to set
	 */
	public void setMaxCaracteresDescripcion(int maxCaracteresDescripcion) {
		this.maxCaracteresDescripcion = maxCaracteresDescripcion;
	}
	/**
	 * @return the maxCaracteresClaseAnexo
	 */
	public int getMaxCaracteresClaseAnexo() {
		return maxCaracteresClaseAnexo;
	}
	/**
	 * @param maxCaracteresClaseAnexo the maxCaracteresClaseAnexo to set
	 */
	public void setMaxCaracteresClaseAnexo(int maxCaracteresClaseAnexo) {
		this.maxCaracteresClaseAnexo = maxCaracteresClaseAnexo;
	}
	
	/**
	 * @return the obligaCampos1
	 */
	public String getObligaCampos1() {
		return obligaCampos1;
	}
	/**
	 * @param obligaCampos1 the obligaCampos1 to set
	 */
	public void setObligaCampos1(String obligaCampos1) {
		this.obligaCampos1 = obligaCampos1;
	}
	/**
	 * @return the obligaCampoDependencia
	 */
	public boolean isObligaCampoDependencia() {
		return obligaCampoDependencia;
	}
	/**
	 * @param obligaCampoDependencia the obligaCampoDependencia to set
	 */
	public void setObligaCampoDependencia(boolean obligaCampoDependencia) {
		this.obligaCampoDependencia = obligaCampoDependencia;
	}

	
}