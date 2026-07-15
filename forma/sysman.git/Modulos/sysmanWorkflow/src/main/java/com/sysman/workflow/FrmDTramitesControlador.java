/*-
 * FrmDTramitesControlador.java
 *
 * 1.0
 * 
 * 24/04/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman. Paipa, Boyaca. All rights
 * reserved.
 */
package com.sysman.workflow;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.email.ApiRestClient;
import com.sysman.email.EmailPojo;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import com.sysman.workflow.ejb.EjbWorkflowCeroRemote;
import com.sysman.workflow.enums.FrmDTramitesControladorEnum;
import com.sysman.workflow.enums.FrmDTramitesControladorUrlEnum;
import com.sysman.workflow.enums.FrmTramitesControladorUrlEnum;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.swing.JOptionPane;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 * Controlador de la forma: <code>frmdtramites</code>.
 *
 * @version 1.0, 24/04/2018
 * @author pespitia
 */
@ManagedBean
@ViewScoped
public class FrmDTramitesControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;

	/**
	 * Constante a nivel de clase que almacena el codigo del modulo desde el cual se
	 * accede a este formulario.
	 */
	private final String modulo = SessionUtil.getModulo();

	/**
	 * Constante a nivel de clase que aloja el codigo del usuario que inicio sesion.
	 */
	private final String usuarioSesion = SessionUtil.getUser().getCodigo();

	/** Constante a nivel de clase que aloja la cadena: COMPANIA. */
	private final String cCompania = GeneralParameterEnum.COMPANIA.getName();
	// <DECLARAR_ATRIBUTOS>
	/**
	 * Atributo a nivel de clase que recibe y almacena el codigo del proceso al
	 * abrir el formulario.
	 */
	private String proceso;

	/** Atributo que almacena el nombre del proceso. */
	private String procesoNom;

	/**
	 * Atributo que almacena el codigo del rol seleccionado en el combo Rol
	 * (CB5905).
	 */
	private String rol;

	/**
	 * Atributo que almacena el codigo del usuario seleccionado en el combo Usuario
	 * (CB5906).
	 */
	private String usuario;

	/**
	 * Atributo que almacena el codigo del nodo seleccionado en el combo Nodo
	 * Destino (CB5907).
	 */
	private String nodoDestino;

	/**
	 * Atributo que aloja el nombre del rol seleccionado en el combo Rol (CB5905).
	 */
	private String nombreRol;

	/** Atributo que contiene el nombre del nodo origen (CP55288). */
	private String nombreNodoOrigen;

	/** Atributo que contiene el codigo del nodo origen (CP55290). */
	private String nodoOrigen;

	/**
	 * Atributo que almacena el codigo del tipo de tramite redireccionado.
	 */
	private String tipoTramite;

	/** Atributo que almacena el nombre del tipo de tramite. */
	private String tipoTramiteNom;

	/** Variable que almacena el numero del tramite redireccionado. */
	private String tramite;

	/** Variable que establece la visibilidad del boton Informados. */
	private boolean indInformados;

	private String archivoCentral;

	private String devolutivo;
	private String descDevolucion;
	private String movDevolutivo;
	
	private boolean mostrarAlertaDep;


	private Date fechaReal;
	private Date fechaProrroga;

	private boolean mostrarAlerta;

	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	/** Lista que contiene los detalles del combo Rol (CB5905). */
	private List<Registro> listaCbRol;

	/**
	 * Lista que contiene los detalles del combo Nodo Destino (CB5907).
	 */
	private List<Registro> listaCbNodoDestino;

	private List<Registro> listaArchivoCentral;

	private List<Registro> listaMovDevolucion;
	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	/** Lista que contiene los detalles del combo Usuario (CB5906). */
	private RegistroDataModelImpl listaCbUsuario;
	// </DECLARAR_LISTAS_COMBO_GRANDE>
	// <DECLARAR_EJBs>
	/**
	 * Variable que permite acceder a las funciones y procedimientos del paquete:
	 * PCK_WORKFLOW.
	 */
	@EJB
	private EjbWorkflowCeroRemote ejbWorkflowCero;

	private boolean manArchivoCentral;

	private boolean visibleArchivoCentral;
	
	private boolean visibleLbArchivoCentral;

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

	private Registro rsProrroga;

	private Boolean prorroga;

	private boolean mostrarAlertaPrg;

	private String depUsuario;

	private String depTramite;


	// </DECLARAR_EJBs>

	/**
	 * Crea una nueva instancia de FrmDTramitesControlador
	 */
	public FrmDTramitesControlador() {
		super();

		compania = SessionUtil.getCompania();

		try {
			// 1767
			numFormulario = GeneralCodigoFormaEnum.FRM_D_TRAMITES_CONTROLADOR.getCodigo();

			validarPermisos();
			// <INI_ADICIONAL>
			Map<String, Object> paramIn = SessionUtil.getFlash();

			if (paramIn != null) {
				proceso = paramIn.get(FrmDTramitesControladorEnum.PR_PROCESO.getValue()).toString();

				procesoNom = paramIn.get(FrmDTramitesControladorEnum.PR_PROCESO_NOM.getValue()).toString();

				nodoOrigen = paramIn.get(FrmDTramitesControladorEnum.PR_NODO_ORIGEN.getValue()).toString();

				nombreNodoOrigen = paramIn.get(FrmDTramitesControladorEnum.PR_NODO_ORIGEN_NOM.getValue()).toString();

				tipoTramite = paramIn.get(FrmDTramitesControladorEnum.PR_TIPOTRAMITE.getValue()).toString();

				tipoTramiteNom = paramIn.get(FrmDTramitesControladorEnum.PR_TIPOTRAMITE_NOM.getValue()).toString();

				tramite = paramIn.get(FrmDTramitesControladorEnum.PR_TRAMITE.getValue()).toString();
				
				fechaReal = (Date) paramIn.get(FrmDTramitesControladorEnum.FECHA_REAL.getValue());

				fechaProrroga = (Date) paramIn.get(FrmDTramitesControladorEnum.FECHA_PRORROGA.getValue());

				prorroga = (boolean) paramIn.get(FrmDTramitesControladorEnum.PRORROGA.getValue());
				
				depTramite = paramIn.get(FrmDTramitesControladorEnum.DEPENDENCIA.getValue()).toString();
			}
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
		// <CARGAR_LISTA>
		cargarListaCbNodoDestino();

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
		/*- Si la lista de nodos destino solo tiene un item, preseleccionar. */
		if (listaCbNodoDestino.size() == 1) {
			nodoDestino = listaCbNodoDestino.get(0).getCampos().get("NODO_DESTINO").toString();

			manArchivoCentral = (boolean) listaCbNodoDestino.get(0).getCampos().get("IND_ARCHIVO_CENTRAL");
		}

		cargarListaCbRol();
		cargarListaArchivoCentral();
		mostrarArchivoCentral();


		/*- Si la lista de roles solo tiene un item, preseleccionar. */
		if (listaCbRol.size() == 1) {
			rol = listaCbRol.get(0).getCampos().get("CODIGO_ROL").toString();
		}

		cargarListaCbUsuario();

		indInformados = !SysmanFunciones.validarVariableVacio(nodoDestino) && !recuperarUsuariosTipoRaci(12).isEmpty();
		// </CODIGO_DESARROLLADO>

	}

	// <METODOS_CARGAR_LISTA>
	/**
	 * Carga la lista: <code>listaCbRol</code> asociada al combo Rol (CB5905).
	 */
	public void cargarListaCbRol() {
		Map<String, Object> param = new TreeMap<>();
		param.put(cCompania, compania);
		param.put(FrmDTramitesControladorEnum.PROCESO.getValue(), proceso);
		param.put(FrmDTramitesControladorEnum.NODO.getValue(), nodoDestino);
		param.put(FrmDTramitesControladorEnum.RACI.getValue(), "10"); // Ejecutor
		// RACI
		param.put(GeneralParameterEnum.ESTADO.getName(), 4); // Estado
		// Activo

		try {
			listaCbRol = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													FrmDTramitesControladorUrlEnum.URL4734.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * Carga la lista: <code>listaCbNodoDestino</code> asociada al combo Nodo
	 * Destino (CB5907).
	 */
	public void cargarListaCbNodoDestino() {
		Map<String, Object> param = new TreeMap<>();
		param.put(cCompania, compania);
		param.put(FrmDTramitesControladorEnum.PROCESO.getValue(), proceso);
		param.put("NODO_ORIGEN", nodoOrigen);
		param.put(GeneralParameterEnum.ESTADO.getName(), 4); // Activo

		try {
			listaCbNodoDestino = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													FrmDTramitesControladorUrlEnum.URL5123.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * Carga la lista: <code>listaCbUsuario</code> asociada al combo Usuario
	 * (CB5906).
	 */
	public void cargarListaCbUsuario() {
		Map<String, Object> param = new TreeMap<>();
		param.put(cCompania, compania);
		param.put(FrmDTramitesControladorEnum.ROL.getValue(), rol);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmDTramitesControladorUrlEnum.URL6021.getValue());

		listaCbUsuario = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.USUARIO.getName());
	}

	/**
	 * 
	 * Carga la lista listaArchivoCentral
	 *
	 * 
	 */
	public void cargarListaArchivoCentral() {
		// listaArchivoCentral = service.getListado(ConectorPool.ESQUEMA_SYSMAN,
		// "SELECT"+
		// " CODIGO"+
		// " FROM ARCHIVO_CENTRAL"+
		// " WHERE COMPANIA = :COMPANIA");
		Map<String, Object> param = new TreeMap<>();
		param.put(cCompania, compania);

		try {
			listaArchivoCentral = RegistroConverter
					.toListRegistro(
							requestManager
									.getList(
											UrlServiceUtil.getInstance()
													.getUrlServiceByUrlByEnumID(
															FrmDTramitesControladorUrlEnum.URL004.getValue())
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
	 * Metodo ejecutado al oprimir el boton Aceptar (BT3104) en la vista. Permite
	 * tramitar de un nodo a otro.
	 * @throws SystemException 
	 */
	public void oprimirBtAceptar() throws SystemException {
		// <CODIGO_DESARROLLADO>

					//dgPaola 7729163
					Map<String, Object> param = new TreeMap<>();
			        param.put("COMPANIA", compania);
			        param.put("CODIGO_PROCESO", proceso);
			        param.put("CODIGO", nodoOrigen);
				
			        boolean validaDep;
						
							List<Registro> resValidaDependencia = RegistroConverter
									.toListRegistro(
											requestManager
													.getList(
															UrlServiceUtil.getInstance()
																	.getUrlServiceByUrlByEnumID(
																			FrmDTramitesControladorUrlEnum.URL1035012.getValue())
																	.getUrl(),
															param));
						
						validaDep = (boolean) resValidaDependencia.get(0).getCampos().get("VALIDA_AL_TRAMITAR");


						if(validaDep) {
							if(depUsuario.equals(depTramite)) {
								mostarAlertaPrg();
							}else {
								mostrarAlertaDep= true;
							}
						}else{
							mostarAlertaPrg();
						}
		  							

					 /*catch (IOException e) {
					logger.error(e.getMessage(), e);
					JsfUtil.agregarMensajeError(e.getMessage());

					RequestContext.getCurrentInstance().closeDialog(null);
				}*/
				// </CODIGO_DESARROLLADO>
	}

	public void mostarAlertaPrg() {

		try {
			int totalDias = ejbSysmanUtil.retornarDiasHabilesEntreFechas(compania, fechaReal, new Date(), false);
			int totalDiasPrg = ejbSysmanUtil.retornarDiasHabilesEntreFechas(compania,(Date) SysmanFunciones.nvl(fechaProrroga, new Date()), new Date(), false);

			if(totalDias >= 10 && etapaProrroga()) {
				mostrarAlerta = true;
			}else if (totalDiasPrg >= 30 && prorroga){
				mostrarAlertaPrg = true;
			}else {
				tramitar();
				RequestContext.getCurrentInstance().closeDialog(null);
			}

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}


	

	/**
	 * Metodo ejecutado al oprimir el boton Informados (BT3594) en la vista.
	 * Redirecciona al formulario: <code>frmlistaritemsconsulta</code> (FR2077).
	 */
	public void oprimirBtInformados() {
		// <CODIGO_DESARROLLADO>
		String[] campos = new String[2];

		/*
		 * campos[0] = FrmListarItemsConsultaControladorEnum.PR_SQL_SELECT .getValue();
		 * 
		 * campos[1] = FrmListarItemsConsultaControladorEnum.PR_SQL_PARAMETROS
		 * .getValue();
		 */

		Map<String, Object> param = new TreeMap<>();
		param.put(cCompania, compania);
		param.put(FrmDTramitesControladorEnum.NODO.getValue(), nodoDestino);
		param.put(FrmDTramitesControladorEnum.PROCESO.getValue(), proceso);
		/*- El 12 es RACI de tipo I (Informados)*/
		param.put(FrmDTramitesControladorEnum.RACI.getValue(), 12);

		Object[] valores = new Object[2];
		valores[0] = FrmDTramitesControladorUrlEnum.URL003.getValue();
		valores[1] = param;

		SessionUtil.cargarModalDatosFlashCerrar(
				Integer.toString(GeneralCodigoFormaEnum.FRM_LISTAR_ITEMS_CONSULTA_CONTROLADOR.getCodigo()), modulo,
				campos, valores);
		// </CODIGO_DESARROLLADO>
	}

	// </METODOS_BOTONES>
	// <METODOS_CAMBIAR>

	/**
	 * Metodo ejecutado al seleccionar una fila del combo Rol (CB5905).
	 */
	public void cambiarCbRol() {
		// <CODIGO_DESARROLLADO>
		cargarListaCbUsuario();

		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al seleccionar un item el combo Nodo Destino (CB5907).
	 */
	public void cambiarCbNodoDestino() {
		// <CODIGO_DESARROLLADO>
		cargarListaCbRol();

		indInformados = !SysmanFunciones.validarVariableVacio(nodoDestino) && !recuperarUsuariosTipoRaci(12).isEmpty();
		// </CODIGO_DESARROLLADO>
	}

	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	/**
	 * Metodo ejecutado al seleccionar una fila de la lista:
	 * <code>listaCbUsuario</code> asociada al combo Usuario (CB5906).
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCbUsuario(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();

		usuario = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.USUARIO.getName()), "")
				.toString();
		
		depUsuario = SysmanFunciones.nvl(registroAux.getCampos().get(FrmDTramitesControladorEnum.DEPENDENCIA.getValue()), "")
				.toString();			
	}

	public void aceptardgValidarDep() {
		mostarAlertaPrg();
	}
	
	public void cancelardgValidarDep() {
		mostrarAlertaDep = false;
	}
	/**
	 * Metodo ejecutado al seleccionar una fila de la lista:
	 * <code>listaCbNodoDestino</code> asociada al combo Nodo Destino (CB5907).
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCbNodoDestino(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();

		nodoDestino = SysmanFunciones.nvl(registroAux.getCampos().get("NODO_DESTINO"), "").toString();

		manArchivoCentral = (boolean) SysmanFunciones.nvl(registroAux.getCampos().get("IND_ARCHIVO_CENTRAL"), false);
	}
	
	/**
	 * Metodo que permita ocultar o mostrar el campo de archivo central

	 */

	public void mostrarArchivoCentral() {

		try {

			String compara = SysmanFunciones.nvlStr(
					ejbSysmanUtil.consultarParametro(compania, "VISTA ARCHIVO CENTRAL", modulo, new Date(), false),
					"SI");
			if (compara.equals("SI") || compara.equals("si") || compara.equals("Si")) {
				visibleArchivoCentral = true;
				visibleLbArchivoCentral = true;
			} else {
				visibleArchivoCentral = false;
				visibleLbArchivoCentral = false;
			}

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	// </METODOS_COMBOS_GRANDES>
	// <METODOS_ARBOL>
	// </METODOS_ARBOL>

	/**
	 * Metodo que desencadena el proceso por el cual se informa mediante correo
	 * electronico el pase del tramite al ejecutor.
	 * 
	 * @param raci -> Tipo de usuarios a los que se debe informar.
	 *             <li>R: Responsable
	 *             <li>A: Ejecutor
	 *             <li>C: Consultor
	 *             <li>I: Informado
	 * @throws IOException
	 */
	private void informar(String raci) throws IOException {
		String emails = "";
		String asunto = "";
		String cuerpo = "";

		switch (raci) {
		case "A":
			emails = obtenerEmails(usuario);
			asunto = idioma.getString("TB_TB4107");

			cuerpo = idioma.getString("TB_TB4108").replace("#tipoTramiteNom#", tipoTramiteNom)
					.replace("#tipoTramite#", tipoTramite).replace("#procesoNom#", procesoNom)
					.replace("#proceso#", proceso).replace("#tramite#", tramite)
					.replace("#nodoOrigenNom#", nombreNodoOrigen).replace("#nodoOrigen#", nodoOrigen);
			break;
		case "I":
			emails = obtenerEmails(recuperarUsuariosTipoRaci(12));
			asunto = idioma.getString("TB_TB4129");

			cuerpo = idioma.getString("TB_TB4130").replace("#tipoTramiteNom#", tipoTramiteNom)
					.replace("#tipoTramite#", tipoTramite).replace("#procesoNom#", procesoNom)
					.replace("#proceso#", proceso).replace("#tramite#", tramite)
					.replace("#nodoOrigenNom#", nombreNodoOrigen).replace("#nodoOrigen#", nodoOrigen);
			break;
		default:
			break;
		}

		EmailPojo emailPojo = new EmailPojo();

		emailPojo.setFrom("WORKFLOW");
		emailPojo.setTo(emails);
		emailPojo.setSubject(asunto);
		emailPojo.setBody(cuerpo);

		ApiRestClient client = new ApiRestClient();
		client.postClient(emailPojo);
	}

	/**
	 * Metodo utilizado para obtener una secuencia de correos electronicos separados
	 * por coma (,) de un listado de usuarios.
	 * 
	 * @param usuarios -> Listado de usuarios separados por coma (,).
	 * @return Secuencia de correos electronicos separados por coma (,).
	 */
	private String obtenerEmails(String usuarios) {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.USUARIO.getName(), usuarios);

		StringBuilder emails = new StringBuilder();

		try {
			List<Registro> list = RegistroConverter
					.toListRegistro(
							requestManager
									.getList(
											UrlServiceUtil.getInstance()
													.getUrlServiceByUrlByEnumID(
															FrmDTramitesControladorUrlEnum.URL001.getValue())
													.getUrl(),
											param));

			for (int i = 0; i < list.size(); i++) {
				emails.append(list.get(i).getCampos().get("CORREOELECTRONICO").toString());

				if (i < list.size() - 1) {
					emails.append(',');
				}
			}
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		return emails.toString();
	}

	/**
	 * Metodo utilizado para recuperar los usuarios de un tipo de raci definido en
	 * el nodo actual.
	 * 
	 * @return Una cadena con los usuarios separados por coma.
	 */
	private String recuperarUsuariosTipoRaci(int raci) {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(FrmDTramitesControladorEnum.PROCESO.getValue(), proceso);
		param.put(FrmDTramitesControladorEnum.NODO.getValue(), nodoOrigen);
		param.put(FrmDTramitesControladorEnum.RACI.getValue(), raci); /*- Tipo RACI*/

		StringBuilder usuarios = new StringBuilder();

		try {
			List<Registro> listUsuarios = RegistroConverter
					.toListRegistro(
							requestManager
									.getList(
											UrlServiceUtil.getInstance()
													.getUrlServiceByUrlByEnumID(
															FrmDTramitesControladorUrlEnum.URL002.getValue())
													.getUrl(),
											param));

			for (Registro r : listUsuarios) {
				usuarios.append(r.getCampos().get(GeneralParameterEnum.USUARIO.getName()).toString().concat(","));
			}

			if (usuarios.length() > 0) {
				usuarios.deleteCharAt(usuarios.length() - 1);
			}
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		return usuarios.toString();
	}
	
	private boolean etapaProrroga() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(FrmDTramitesControladorEnum.PROCESO.getValue(), proceso);
		param.put(FrmDTramitesControladorEnum.NODO.getValue(), nodoDestino);

		boolean prorroga = false;

		try {
			Registro rsProrroga = RegistroConverter
					.toRegistro(
							requestManager
							.get(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmDTramitesControladorUrlEnum.URL1035010.getValue())
									.getUrl(),
									param));

			if(rsProrroga != null) {

				prorroga = (boolean) rsProrroga.getCampos().get(FrmDTramitesControladorEnum.PRORROGA.getValue());
			}

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		return prorroga;
	}
	
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Aceptar
	 * del dialogo dialogoProrrogaVenc en la vista
	 *
	 *
	 */
	public void aceptardialogoProrrogaVenc() {
		//<CODIGO_DESARROLLADO>
		tramitar();
		RequestContext.getCurrentInstance().closeDialog(null);
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Cancelar
	 * del dialogo dialogoProrrogaVenc en la vista
	 *
	 *
	 */
	public void cancelardialogoProrrogaVenc() {
		//<CODIGO_DESARROLLADO>
		RequestContext.getCurrentInstance().closeDialog(null);
		//</CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Aceptar
	 * del dialogo dialogoProrrogaEtapa en la vista
	 *
	 *
	 */
	public void aceptardialogoProrrogaEtapa() {
		//<CODIGO_DESARROLLADO>
		tramitar();
		RequestContext.getCurrentInstance().closeDialog(null);
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Cancelar
	 * del dialogo dialogoProrrogaEtapa en la vista
	 *
	 *
	 */
	public void cancelardialogoProrrogaEtapa() {
		//<CODIGO_DESARROLLADO>
		RequestContext.getCurrentInstance().closeDialog(null);
		//</CODIGO_DESARROLLADO>
	}
	
	public void tramitar() {
		try {
			ejbWorkflowCero.tramitar(compania, proceso, tipoTramite, new BigInteger(tramite), nodoOrigen, nodoDestino,
					false, usuario, archivoCentral, usuarioSesion);


			informar("A");
			informar("I");
			
			RequestContext.getCurrentInstance().closeDialog(null);

			
		} catch (SystemException | IOException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		
	}

	// <SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable rol
	 * 
	 * @return rol
	 */
	public String getRol() {
		return rol;
	}

	/**
	 * Asigna la variable rol
	 * 
	 * @param rol Variable a asignar en rol
	 */
	public void setRol(String rol) {
		this.rol = rol;
	}

	/**
	 * Retorna la variable usuario
	 * 
	 * @return usuario
	 */
	public String getUsuario() {
		return usuario;
	}

	/**
	 * Asigna la variable usuario
	 * 
	 * @param usuario Variable a asignar en usuario
	 */
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	/**
	 * Retorna la variable nodoDestino
	 * 
	 * @return nodoDestino
	 */
	public String getNodoDestino() {
		return nodoDestino;
	}

	/**
	 * Asigna la variable nodoDestino
	 * 
	 * @param nodoDestino Variable a asignar en nodoDestino
	 */
	public void setNodoDestino(String nodoDestino) {
		this.nodoDestino = nodoDestino;
	}

	/**
	 * Retorna la variable nombreRol
	 * 
	 * @return nombreRol
	 */
	public String getNombreRol() {
		return nombreRol;
	}

	/**
	 * Asigna la variable nombreRol
	 * 
	 * @param nombreRol Variable a asignar en nombreRol
	 */
	public void setNombreRol(String nombreRol) {
		this.nombreRol = nombreRol;
	}

	/**
	 * Retorna la variable nombreNodoOrigen
	 * 
	 * @return nombreNodoOrigen
	 */
	public String getNombreNodoOrigen() {
		return nombreNodoOrigen;
	}

	/**
	 * Asigna la variable nombreNodoOrigen
	 * 
	 * @param nombreNodoOrigen Variable a asignar en nombreNodoOrigen
	 */
	public void setNombreNodoOrigen(String nombreNodoOrigen) {
		this.nombreNodoOrigen = nombreNodoOrigen;
	}

	/**
	 * Retorna la variable nodoOrigen
	 * 
	 * @return nodoOrigen
	 */
	public String getNodoOrigen() {
		return nodoOrigen;
	}

	/**
	 * Asigna la variable nodoOrigen
	 * 
	 * @param nodoOrigen Variable a asignar en nodoOrigen
	 */
	public void setNodoOrigen(String nodoOrigen) {
		this.nodoOrigen = nodoOrigen;
	}

	// </SET_GET_ATRIBUTOS>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_LISTAS>
	/**
	 * Retorna la lista listaCbRol
	 * 
	 * @return listaCbRol
	 */
	public List<Registro> getListaCbRol() {
		return listaCbRol;
	}

	/**
	 * Asigna la lista listaCbRol
	 * 
	 * @param listaCbRol Variable a asignar en listaCbRol
	 */
	public void setListaCbRol(List<Registro> listaCbRol) {
		this.listaCbRol = listaCbRol;
	}

	public List<Registro> getListaCbNodoDestino() {
		return listaCbNodoDestino;
	}

	public void setListaCbNodoDestino(List<Registro> listaCbNodoDestino) {
		this.listaCbNodoDestino = listaCbNodoDestino;
	}

	public boolean isIndInformados() {
		return indInformados;
	}

	public void setIndInformados(boolean indInformados) {
		this.indInformados = indInformados;
	}

	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>

	public RegistroDataModelImpl getListaCbUsuario() {
		return listaCbUsuario;
	}

	public void setListaCbUsuario(RegistroDataModelImpl listaCbUsuario) {
		this.listaCbUsuario = listaCbUsuario;
	}

	public List<Registro> getListaArchivoCentral() {
		return listaArchivoCentral;
	}

	public void setListaArchivoCentral(List<Registro> listaArchivoCentral) {
		this.listaArchivoCentral = listaArchivoCentral;
	}

	public String getArchivoCentral() {
		return archivoCentral;
	}

	public void setArchivoCentral(String archivoCentral) {
		this.archivoCentral = archivoCentral;
	}

	public boolean isManArchivoCentral() {
		return manArchivoCentral;
	}

	public void setManArchivoCentral(boolean manArchivoCentral) {
		this.manArchivoCentral = manArchivoCentral;
	}

	/**
	 * @return the devolutivo
	 */
	public String getDevolutivo() {
		return devolutivo;
	}

	/**
	 * @param devolutivo the devolutivo to set
	 */
	public void setDevolutivo(String devolutivo) {
		this.devolutivo = devolutivo;
	}

	/**
	 * @return the descDevolucion
	 */
	public String getDescDevolucion() {
		return descDevolucion;
	}

	/**
	 * @param descDevolucion the descDevolucion to set
	 */
	public void setDescDevolucion(String descDevolucion) {
		this.descDevolucion = descDevolucion;
	}

	/**
	 * @return the listaMovDevolucion
	 */
	public List<Registro> getListaMovDevolucion() {
		return listaMovDevolucion;
	}

	/**
	 * @param listaMovDevolucion the listaMovDevolucion to set
	 */
	public void setListaMovDevolucion(List<Registro> listaMovDevolucion) {
		this.listaMovDevolucion = listaMovDevolucion;
	}

	/**
	 * @return the movDevolutivo
	 */
	public String getMovDevolutivo() {
		return movDevolutivo;
	}

	/**
	 * @param movDevolutivo the movDevolutivo to set
	 */
	public void setMovDevolutivo(String movDevolutivo) {
		this.movDevolutivo = movDevolutivo;
	}

	/**
	 * @return the visibleArchivoCentral
	 */
	public boolean isVisibleArchivoCentral() {
		return visibleArchivoCentral;
	}

	/**
	 * @param visibleArchivoCentral the visibleArchivoCentral to set
	 */
	public void setVisibleArchivoCentral(boolean visibleArchivoCentral) {
		this.visibleArchivoCentral = visibleArchivoCentral;
	}

	/**
	 * @return the visibleLbArchivoCentral
	 */
	public boolean isVisibleLbArchivoCentral() {
		return visibleLbArchivoCentral;
	}

	/**
	 * @param visibleLbArchivoCentral the visibleLbArchivoCentral to set
	 */
	public void setVisibleLbArchivoCentral(boolean visibleLbArchivoCentral) {
		this.visibleLbArchivoCentral = visibleLbArchivoCentral;
	}
	
	/**
	 * @return the fechaReal
	 */
	public Date getFechaReal() {
		return fechaReal;
	}

	/**
	 * @param fechaReal the fechaReal to set
	 */
	public void setFechaReal(Date fechaReal) {
		this.fechaReal = fechaReal;
	}

	/**
	 * @return the mostrarAlerta
	 */
	public boolean isMostrarAlerta() {
		return mostrarAlerta;
	}

	/**
	 * @param mostrarAlerta the mostrarAlerta to set
	 */
	public void setMostrarAlerta(boolean mostrarAlerta) {
		this.mostrarAlerta = mostrarAlerta;
	}

	/**
	 * @return the mostrarAlertaPrg
	 */
	public boolean isMostrarAlertaPrg() {
		return mostrarAlertaPrg;
	}

	/**
	 * @param mostrarAlertaPrg the mostrarAlertaPrg to set
	 */
	public void setMostrarAlertaPrg(boolean mostrarAlertaPrg) {
		this.mostrarAlertaPrg = mostrarAlertaPrg;
	}

	/**
	 * @return the mostrarAlertaDep
	 */
	public boolean isMostrarAlertaDep() {
		return mostrarAlertaDep;
	}

	/**
	 * @param mostrarAlertaDep the mostrarAlertaDep to set
	 */
	public void setMostrarAlertaDep(boolean mostrarAlertaDep) {
		this.mostrarAlertaDep = mostrarAlertaDep;
	}

	/**
	 * @return the depUsuario
	 */
	public String getDepUsuario() {
		return depUsuario;
	}

	/**
	 * @param depUsuario the depUsuario to set
	 */
	public void setDepUsuario(String depUsuario) {
		this.depUsuario = depUsuario;
	}

	/**
	 * @return the depTramite
	 */
	public String getDepTramite() {
		return depTramite;
	}

	/**
	 * @param depTramite the depTramite to set
	 */
	public void setDepTramite(String depTramite) {
		this.depTramite = depTramite;
	}



}
