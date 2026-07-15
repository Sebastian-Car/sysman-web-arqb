/*-
 * DTramiteVariablesControlador.java
 *
 * 1.0
 * 
 * 27/04/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.workflow;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.RequestManager;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.logica.Formulario;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.ServidorCorreo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import com.sysman.workflow.enums.DTramiteVariablesControladorEnum;
import com.sysman.workflow.enums.DTramiteVariablesControladorUrlEnum;
import com.sysman.workflow.enums.FrmTramitesControladorEnum;
import com.sysman.workflow.enums.FrmWFInformaciongeneralControladorEnum;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.Date;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.json.Json;
import javax.mail.MessagingException;
import javax.naming.NamingException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador de la forma: <code>dtramitevariables</code>.
 *
 * @version 1.0, 27/04/2018
 * @author pespitia
 * 
 * @version 2.0, 17/09/2021
 * @author gfigueredo
 * Se ajustan las funciones oprimirVerOcr y oprimirEscanear para configurar
 * las rutas en donde se cargaran y se cosultar los archivos escaneados. La
 * ruta contendrá información de la compañia y la entidad.
 * @see #oprimirverOcr(Registro, int)
 * @see #oprimirEscanear(Registro, int)
 */
@ManagedBean
@ViewScoped
public class DTramiteVariablesControlador extends BeanBaseContinuoAcmeImpl {
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	/**
	 * Constante a nivel de clase que aloja el caracter delimitador de la ruta en el
	 * sistema operativo.
	 */
	private static final String FILE_SEPARADOR = File.separator;

	/**
	 * Constante para almacenar el formato de escaneo pdf
	 */
	//private static final String FORMATO_PDF = ".pdf";
	/**
	 * Constante a nivel de clase que aloja el codigo del tipo mime Application e
	 * Image
	 */
	private final String tipoMimeAppIma;

	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;

	/**
	 * Constante a nivel de clase que aloja el codigo del modulo desde el cual el
	 * usuario abre el formulario.
	 */
	private final String modulo = SessionUtil.getModulo();

	/**
	 * Constante a nivel de clase que aloja el codigo del usuario que inicio sesion.
	 */
	private final String usuario = SessionUtil.getUser().getCodigo();

	/** Constante a nivel de clase que aloja la cadena: COMPANIA. */
	private final String cCompania = GeneralParameterEnum.COMPANIA.getName();

	// <DECLARAR_ATRIBUTOS>
	/**
	 * Atributo auxliar el cual es asiganado en el momento que se activa la edicion
	 * de un registro. Toma el valor del indice dentro de la grilla del registro
	 * seleccionado para editar
	 */
	private int indice;

	/** Variable que recibe y almacena el codigo del proceso. */
	private String proceso;

	/**
	 * Variable que recibe y almacena el codigo del tipo de tramite.
	 */
	private String tipoTramite;

	/** Variable que recibe y almacena numero de tramite. */
	private String tramite;

	/**
	 * Variable que recibe y almacena el codigo del detalle del tramite.
	 */
	private String detalleTramite;

	/** Variable que recibe y almacena el codigo del nodo. */
	private String nodo;

	/**
	 * Atributo que contiene el tipo de dato de la etiqueta que esta siendo editada
	 * en la grilla.
	 */
	private String tipoDato;

	/**
	 * Este atributo se usa como auxiliar del componente referencia de archivos
	 * BuscarAdjunto (RA7) y funciona como contenedor del archivo que se desea
	 * cargar
	 */
	private UploadedFile archivoCargaBuscarAdjunto;

	/**
	 * Coleccion que contiene las llaves del registro de Tramite desde el cual se
	 * accede a este formulario.
	 */
	private Map<String, Object> ridTramite;

	/**
	 * Numero del formulario desde el cual se accede a este formulario.
	 */
	private int codFormRedireccion;

	/**
	 * Atributo que almacena las posibles extenciones de los archivos que se pueden
	 * adjuntar.
	 */
	private String extension;

	/**
	 * Indicador que establece si el formulario permite realizar modificaciones
	 * sobre los registros.
	 */
	private boolean permiteModificar;

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;

	private String nameFile;

	private boolean mostrarBtn;

	private String  rutaServidorEscaner;

	private ServidorCorreo correo;

	private Registro rsVariables;

	private List<Registro> rsAdjuntos;

	private ByteArrayInputStream[] salidas;

	private String[] nombresArchivos;

	private String[] tipos;

	private boolean enviaCorreo;

	private boolean procedenciaAut;

	private Registro rsEmail;

	private String hostName;

	private String user;

	private String contrasena;
	
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DTramiteVariablesControlador.class);
	
	/**
	 * Para manejar el texto de error del procesador
	 */
	private static final String ERROR = "Error de: <<DTramiteVariablesControlador>> parametros / message  ->> {} ";
	
	private static final String GET = "GET";
	
	private static final String METHOD = "Method";
	
	private static final String KEY_LOG_URL ="URL-> {}";
	
	private String adjuntosExternos;
	/**
	 * Variable que permite acceder a la url remota del servidor de archivos externos
	 */
	private String url;

	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	// </DECLARAR_LISTAS_COMBO_GRANDE>
	// <DECLARAR_EJBs>
	// </DECLARAR_EJBs>
	/**
	 * Crea una nueva instancia de DTramiteVariablesControlador
	 */
	@SuppressWarnings("unchecked")
	public DTramiteVariablesControlador() {
		super();

		compania = SessionUtil.getCompania();
		tipoMimeAppIma = "33,37";
		correo = new ServidorCorreo();

		try {
			// 1780
			numFormulario = GeneralCodigoFormaEnum.D_TRAMITE_VARIABLES_CONTROLADOR.getCodigo();

			Map<String, Object> paramIn = SessionUtil.getFlash();

			if (paramIn != null) {
				proceso = paramIn.get(DTramiteVariablesControladorEnum.PR_PROCESO.getValue()).toString();

				tipoTramite = paramIn.get(DTramiteVariablesControladorEnum.PR_TIPO_TRAMITE.getValue()).toString();

				tramite = paramIn.get(DTramiteVariablesControladorEnum.PR_TRAMITE.getValue()).toString();

				detalleTramite = paramIn.get(DTramiteVariablesControladorEnum.PR_D_TRAMITE.getValue()).toString();

				nodo = paramIn.get(DTramiteVariablesControladorEnum.PR_NODO.getValue()).toString();

				codFormRedireccion = (int) paramIn.get(DTramiteVariablesControladorEnum.PR_COD_FORM.getValue());

				ridTramite = (Map<String, Object>) paramIn.get(FrmTramitesControladorEnum.PR_ROWKEY.getValue());
			}

			validarPermisos();
			
			Registro rs;
			RequestManager requestManager = new RequestManager();
			Map<String, Object> paraServicio = new HashMap<>();
			paraServicio.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			paraServicio.put(GeneralParameterEnum.CODIGO.getName(), 103);
		
			rs = RegistroConverter.toRegistro(requestManager
					.get(UrlServiceUtil.getUrlBeanById(SysmanConstantes.SERVICIO_API).getUrl(), paraServicio));
			
			if (rs != null)
            {
			url = rs.getCampos().get(GeneralParameterEnum.URL.getName()).toString();
            }
			// <INI_ADICIONAL>
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
		enumBase = GenericUrlEnum.D_TRAMITE_VARIABLES;
		registro = new Registro();

		reasignarOrigen();
		buscarLlave();

		// <CARGAR_LISTA>
		// </CARGAR_LISTA>
		// <CARGAR_LISTA_COMBO_GRANDE>
		// </CARGAR_LISTA_COMBO_GRANDE>
		abrirFormulario();
	}

	/**
	 * En este metodo se asigna al atributo origenDatos del bean base el valor de la
	 * consulta del formulario. Tambien carga la lista del formulario por primera
	 * vez
	 */
	@Override
	public void reasignarOrigen() {
		buscarUrls();

		parametrosListado.put(cCompania, compania);

		parametrosListado.put(DTramiteVariablesControladorEnum.PROCESO.getValue(), proceso);

		parametrosListado.put(DTramiteVariablesControladorEnum.NODO.getValue(), nodo);

		parametrosListado.put(DTramiteVariablesControladorEnum.TIPO_TRAMITE.getValue(), tipoTramite);

		parametrosListado.put(DTramiteVariablesControladorEnum.TRAMITE.getValue(), tramite);
		parametrosListado.put(DTramiteVariablesControladorEnum.D_TRAMITE.getValue(), detalleTramite);
		parametrosListado.put("SEPARADOR", FILE_SEPARADOR);
	}

	// <METODOS_CARGAR_LISTA>
	// </METODOS_CARGAR_LISTA>
	// <METODOS_BOTONES>}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton HistorialAdjunto en la vista
	 *
	 *
	 */
	public void oprimirHistorialAdjunto() {
		// <CODIGO_DESARROLLADO>

		String[] campos = { "tramite", "tipoTramite", "proceso" };
		String[] valores = { tramite, tipoTramite, proceso };
		SessionUtil.cargarModalDatosFlash("2237", modulo, campos, valores);
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al oprimir el boton Escanear
	 * 
	 * 
	 * @param reg    registro en el cual esta ubicado el boton oprimido dentro de la
	 *               grilla
	 * @param indice indice en el cual esta ubicado el boton oprimido dentro de la
	 *               grilla
	 * @throws SystemException 
	 */
	public void oprimirEscanear(Registro reg, int indice) throws SystemException {
		// <CODIGO_DESARROLLADO>

		try {

			String ruta;
			String variable = SysmanFunciones
					.nvl(reg.getCampos().get(DTramiteVariablesControladorEnum.CODIGO_NODO_VARIABLE.getValue()), "")
					.toString();
			String rutaArchvivo = reg.getCampos().get("ADJUNTO").toString();
			String rutaAdjunto = recuperarRutaAdjunto(variable);

			String entidad = SessionUtil.getCompaniaIngreso().getNombre();
			String rutaAdjunto1 = "//" + entidad.replace(" ", "_") + "//" + compania + "//" + rutaAdjunto;

			//			ruta = JsfUtil.generarRuta(modulo, "", FilenameUtils.getFullPath(rutaAdjunto1),
			//					FilenameUtils.getName(rutaArchvivo));
			ruta =  rutaArchvivo;

			String rutaGeneral = FilenameUtils.getFullPath(ruta);
			String[] nombreArchivo = FilenameUtils.getName(ruta).split("." + FilenameUtils.getExtension(ruta));
			rutaArchvivo = nombreArchivo[0];
			//Utilizar / para linux y \ para windows
			rutaGeneral = rutaGeneral.replace("\\", "/");
			rutaGeneral = rutaGeneral.replace("//", "/");


			ruta = rutaAdjunto1.concat(FilenameUtils.getName(ruta));
			
			ruta = ruta.replace("///", "/");
			ruta = ruta.replace("//", "/");
			
			rutaGeneral = rutaServidorEscaner + rutaGeneral;

			actualizarAdjuntoVarTramite(variable, ruta);

			Direccionador direccionador = new Direccionador();
			direccionador.setNumForm(String.valueOf(2130));
			Formulario forma = SessionUtil.cargarFormulario(direccionador.getNumForm() + "," + modulo);
			if (forma == null) {
				JsfUtil.agregarMensajeError("Operación interrumpida, No tiene permisos para acceder a este recurso");
				return;
			}

			String urlFinal = forma.getRuta() + "?fichero=" + rutaArchvivo + "&destino=" + rutaGeneral;

			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();

			ec.redirect(urlFinal);

		} catch (IOException e) {
			e.printStackTrace();
		}

		// Direccionador direccionador = new Direccionador();
		// direccionador.setParametros(param);
		//
		// direccionador.setNumForm(
		// String.valueOf(GeneralCodigoFormaEnum.FRM_ESCANEAR
		// .getCodigo()));
		// SessionUtil.redireccionarForma(direccionador, modulo);

		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al oprimir el boton Ocr
	 * 
	 *
	 * 
	 * @param reg    registro en el cual esta ubicado el boton oprimido dentro de la
	 *               grilla
	 * @param indice indice en el cual esta ubicado el boton oprimido dentro de la
	 *               grilla
	 */
	public void oprimirOcr(Registro reg, int indice) {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al oprimir el boton verOcr
	 * 
	 *
	 * 
	 * @param reg    registro en el cual esta ubicado el boton oprimido dentro de la
	 *               grilla
	 * @param indice indice en el cual esta ubicado el boton oprimido dentro de la
	 *               grilla
	 * @throws SystemException 
	 */
	public void oprimirverOcr(Registro reg, int indice) throws SystemException {
		// <CODIGO_DESARROLLADO>

		String ocr = reg.getCampos().get("OCR") + "";
		String nodoVar = reg.getCampos().get("CODIGO_NODO_VARIABLE") + "";
		String consTramite =  reg.getCampos().get("CONSECUTIVO_TRAMITE") + "";
		String codNodo = reg.getCampos().get("CODIGO_NODO") + "";


		String ruta;
		String variable = SysmanFunciones
				.nvl(reg.getCampos().get(DTramiteVariablesControladorEnum.CODIGO_NODO_VARIABLE.getValue()), "")
				.toString();
		String rutaArchvivo = reg.getCampos().get("ADJUNTO").toString();

		String[] nombreArchivo = FilenameUtils.getName(rutaArchvivo)
				.split("." + FilenameUtils.getExtension(rutaArchvivo));
		rutaArchvivo =  nombreArchivo[0] + ".txt";
		String rutaAdjunto = recuperarRutaAdjunto(variable);
		//rutaAdjunto = rutaAdjunto.replace("'\'", "/");
		String entidad = SessionUtil.getCompaniaIngreso().getNombre();
		String rutaAdjunto1 = "\\" + entidad.replace(" ", "_") + "\\" + compania  + rutaAdjunto + rutaArchvivo ;
		//ruta = JsfUtil.generarRuta(modulo, "", FilenameUtils.getFullPath(rutaAdjunto1), rutaArchvivo);
		//ruta = "\\\\192.168.1.253\\documentos\\WORKFLOW" + rutaAdjunto1;
		ruta = rutaServidorEscaner + rutaAdjunto1;

		String[] campos = { "tabla", "ocr", "ruta", "tipoTramite", "proceso", "tramite", "nodoVar", "nodo", "consTramite" };

		Object[] valores = { "DTramite",ocr, ruta, tipoTramite, proceso, tramite, nodoVar, codNodo, consTramite };

		SessionUtil.cargarModalDatosFlashCerrar(String.valueOf(2132), SessionUtil.getModulo(), campos, valores);
		// </CODIGO_DESARROLLADO>
	}
	public void oprimirEnviarCorreo() {
		try {

			Map<String, Object> paramEmail = new HashMap<>();
			paramEmail.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			paramEmail.put("APLICACION", SessionUtil.getModulo());

			rsEmail = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(DTramiteVariablesControladorUrlEnum.URL009.getValue())
							.getUrl(),
							paramEmail));
			if(rsEmail!= null) {

				hostName = rsEmail.getCampos().get("DOMINIO").toString();
				user = rsEmail.getCampos().get("EMAIL").toString();
				contrasena = rsEmail.getCampos().get("PASSWORD").toString();

				correo.setSmtpHostName(hostName);
				correo.setSmtpHostPort(587);
				correo.setSmtpAuthUser(user);
				correo.setSmtpAuthName(user);
				correo.setSmtpAuthPwd(contrasena);

				String nombre = null;
				String email = null;
				String direccion = null;
				String telefono = null;
				String observaciones = null;
				String cargoFun = null;
				String userInterno = null;

				String dia = String.valueOf(SysmanFunciones.dia(new Date()));
				String mes = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[SysmanFunciones.mes(new Date())];
				String anio = String.valueOf(SysmanFunciones.ano(new Date()));

				Map<String, Object> paramVar = new HashMap<>();
				paramVar.put(GeneralParameterEnum.COMPANIA.getName(), compania);
				paramVar.put("PROCESO", proceso);
				paramVar.put("NODO", nodo);
				paramVar.put("TIPO_TRAMITE", tipoTramite);
				paramVar.put("TRAMITE", tramite);

				rsVariables = RegistroConverter
						.toRegistro(requestManager.get(
								UrlServiceUtil.getInstance()
								.getUrlServiceByUrlByEnumID(DTramiteVariablesControladorUrlEnum.URL007.getValue())
								.getUrl(),
								paramVar));
				if (rsVariables != null)
				{

					nombre = rsVariables.getCampos().get("NOMBRE").toString();
					email = rsVariables.getCampos().get("EMAIL").toString();
					direccion = rsVariables.getCampos().get("DIRECCION").toString();
					telefono = rsVariables.getCampos().get("TELEFONO").toString();
					observaciones = rsVariables.getCampos().get("OBSERVACIONES").toString();
					cargoFun = rsVariables.getCampos().get("CARGO_FUNCIONARIO").toString();
					userInterno = rsVariables.getCampos().get("USUARIO_INTERNO").toString();
				}


				paramVar.put("SEPARADOR", FILE_SEPARADOR);

				rsAdjuntos = RegistroConverter.toListRegistro(
						requestManager.getList(UrlServiceUtil.getInstance()
								.getUrlServiceByUrlByEnumID(
										DTramiteVariablesControladorUrlEnum.URL008.getValue())
								.getUrl(), paramVar));
				if (!rsAdjuntos.isEmpty())
				{
					int it = rsAdjuntos.size();

					salidas = new ByteArrayInputStream[it];
					nombresArchivos = new String[it];
					tipos = new String[it];

					for (int j = 0; j < it; j++) {

						String archivo = rsAdjuntos.get(j).getCampos().get("ADJUNTO").toString();


						String rutaAbs = JsfUtil.generarRuta(modulo, "",
								FilenameUtils.getFullPath(archivo),
								FilenameUtils.getName(archivo));

						File adjunto = new File(rutaAbs);

						InputStream inputStream;

						inputStream = new FileInputStream(adjunto);

						byte[] vec = new byte[(int) adjunto.length()];

						inputStream.read(vec, 0, vec.length);

						salidas[j] = new ByteArrayInputStream(vec);


						nombresArchivos[j] = FilenameUtils.getName(archivo);

						tipos[j] = "application/"+ FilenameUtils.getExtension(archivo);

					}

					String strAsunto = "Respuesta electrónica radicado No. "+ tramite;
					String strMensaje  = idioma.getString("TB_TB4388").replace("#dia#", dia)
							.replace("#mes#", mes)
							.replace("#anio#", anio)
							.replace("#proceso#", proceso)
							.replace("#tramite#", tramite)
							.replace("#nombreProc#", nombre)
							.replace("#entidad#", SessionUtil.getCompaniaIngreso().getNombre())
							.replace("#telefonoProc#", telefono)
							.replace("#direccionProc#", direccion)
							.replace("#emailProc#", email)
							.replace("#observaciones#", observaciones)
							.replace("#cargoFunc#", cargoFun)
							.replace("#nombreFunc#", userInterno);

					correo.enviarAdjunto(email,
							strAsunto, strMensaje,
							nombresArchivos,
							salidas,
							tipos);

					JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4393"));

				}else {

					JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4390"));

				}

			}else {

				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4389"));

			}

		} catch ( /*| SysmanException*/  SystemException | IOException | MessagingException | SQLException | NamingException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * Metodo ejecutado al oprimir el boton Adjuntar (BT3118) ubicado en la grilla.
	 * 
	 * @param reg    registro en el cual esta ubicado el boton oprimido dentro de la
	 *               grilla
	 * @param indice indice en el cual esta ubicado el boton oprimido dentro de la
	 *               grilla
	 */
	public void oprimirBtAdjuntar(Registro reg, int indice) {
		// <CODIGO_DESARROLLADO>
		String archivo = archivoCargaBuscarAdjunto.getFileName();

		if (SysmanFunciones.validarVariableVacio(archivo)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4079"));
			return;
		}

		String variable = SysmanFunciones
				.nvl(reg.getCampos().get(DTramiteVariablesControladorEnum.CODIGO_NODO_VARIABLE.getValue()), "")
				.toString();

		/* Nombre original del archivo sin extension. */
		nameFile = FilenameUtils.getBaseName(archivo);

		Map<String, Object> keyAdjunto = new LinkedHashMap<>();
		keyAdjunto.put("KEY_TIPO_TRAMITE", tipoTramite);
		keyAdjunto.put("KEY_NUMERO_TRAMITE", tramite);
		keyAdjunto.put("KEY_CONSECUTIVO_TRAMITE", detalleTramite);
		keyAdjunto.put("KEY_CODIGO_NODO_VARIABLE", variable);
		keyAdjunto.put("KEY_NAME_FILE", nameFile);

		String miExtension = FilenameUtils.getExtension(archivo);

		try {
			String rutaAdjunto = recuperarRutaAdjunto(variable);

			//String ruta = JsfUtil.generarNombreArchivo(modulo, keyAdjunto, rutaAdjunto, miExtension, "");
			String rutaArchvivo = reg.getCampos().get("ADJUNTO").toString();



			String entidad = SessionUtil.getCompaniaIngreso().getNombre();
			String rutaAdjunto1 = "/" + entidad.replace(" ", "_") + "/" + compania  + rutaAdjunto;
			rutaAdjunto = "//" + entidad.replace(" ", "_") + "//" + compania  + rutaAdjunto;

			//String ruta = "\\\\192.168.1.253\\documentos\\WORKFLOW" + rutaAdjunto1;
			String ruta = rutaServidorEscaner + rutaAdjunto1;
			//Utilizar / para linux y \ para windows
			ruta = ruta.replace("\\", "/");
			//			JsfUtil.generarNombreArchivo(modulo, keyAdjunto,
			//					rutaAdjunto, miExtension, "");
			
			ruta = generarNombreArchivo(rutaServidorEscaner, keyAdjunto,
					miExtension, rutaAdjunto);
			
			rutaAdjunto1 = rutaAdjunto1 + FilenameUtils.getName(ruta);
			// Validar extension
			if (validarExtAdjunto(ruta)) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4123"));
				return;
			}

			// Validar mime
 			if (validarMimeAdjunto(ruta)) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4216"));
				return;
			}

			/* Truncar ruta y adicionar extension */
			if (ruta.length() >= 254) {
				Map<String, Object> keyAdjunto1 = new LinkedHashMap<>();
				keyAdjunto1.put("KEY_TIPO_TRAMITE", tipoTramite);
				keyAdjunto1.put("KEY_NUMERO_TRAMITE", tramite);
				keyAdjunto1.put("KEY_CONSECUTIVO_TRAMITE", detalleTramite);
				keyAdjunto1.put("KEY_CODIGO_NODO_VARIABLE", variable);
				keyAdjunto1.put("KEY_NAME_FILE", nameFile.substring(0, 130));
				
				ruta = generarNombreArchivo(rutaServidorEscaner, keyAdjunto1,
						miExtension, rutaAdjunto);
				rutaAdjunto = recuperarRutaAdjunto(variable);
				rutaAdjunto1 = "/" + entidad.replace(" ", "_") + "/" + compania  + rutaAdjunto;
				rutaAdjunto1 = rutaAdjunto1 + FilenameUtils.getName(ruta);
			}
	
			File adjunto = JsfUtil.upload(archivoCargaBuscarAdjunto.getInputstream(), ruta);
			
			if(adjuntosExternos.equals("SI")) {
				
				
				if(!validarConexion(url)) {
				return;
				}
				String urlCargar = url + "/cargar/"+rutaAdjunto.replaceAll("/", "-");
				HashMap<String, Object> body = new HashMap<>();
				body.put("file", adjunto);
				String respuesta = SysmanFunciones.peticionMultipart(urlCargar, null, body);
		        JSONObject json = new JSONObject(respuesta);
		        if(json.getInt("codigo") == 0) {
		        adjunto.delete();
		        }else {
		       JsfUtil.agregarMensajeAlerta(json.getString("mensaje"));
		       return;
		        }
				LOG.info("Respuesta Subir Adjunto <<DTramiteVariablesControlador>> "+respuesta);
				
			}

			//ruta = rutaAdjunto.concat(FilenameUtils.getName(ruta));

			actualizarAdjuntoVarTramite(variable, rutaAdjunto1);

			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4083"));
		} catch (IOException | SystemException | SysmanException | JSONException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al oprimir el boton Eliminar (BT3131) ubicado en la grilla.
	 * Actualiza el adjunto de la variable asociada al nodo actual del tramite.
	 * 
	 * @param reg    registro en el cual esta ubicado el boton oprimido dentro de la
	 *               grilla
	 * @param indice indice en el cual esta ubicado el boton oprimido dentro de la
	 *               grilla
	 */
	public void oprimirBtEliminar(Registro reg, int indice) {
		// <CODIGO_DESARROLLADO>
		try {
			actualizarAdjuntoVarTramite(
					reg.getCampos().get(DTramiteVariablesControladorEnum.CODIGO_NODO_VARIABLE.getValue()).toString(),
					"");

			listaInicial.load();

			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4088"));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al oprimir el boton Ver (BT3132). Redirecciona al formulario
	 * visor de archivos (1824).
	 * 
	 * @param reg    registro en el cual esta ubicado el boton oprimido dentro de la
	 *               grilla
	 * @param indice indice en el cual esta ubicado el boton oprimido dentro de la
	 *               grilla
	 */
	public void oprimirBtDescargar(Registro reg, int indice) {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		try {
			String ruta = reg.getCampos().get(DTramiteVariablesControladorEnum.ADJUNTO.getValue()).toString();
			//String rutaAbs = "\\\\192.168.1.253\\documentos\\WORKFLOW" + ruta;
			String carpeta = FilenameUtils.getFullPath(ruta);
			String archivo = FilenameUtils.getName(ruta);
			String rutaAbs = rutaServidorEscaner + ruta;
			//String rutaAbs = JsfUtil.generarRuta(modulo, "", FilenameUtils.getFullPath(ruta),
			//		FilenameUtils.getName(ruta));

			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(DTramiteVariablesControladorUrlEnum.URL004.getValue());

			Map<String, Object> param = new TreeMap<>();
			param.put(DTramiteVariablesControladorEnum.TIPO_MIME.getValue(), 37);
			param.put(DTramiteVariablesControladorEnum.ID.getValue(), 6);
			param.put(DTramiteVariablesControladorEnum.EXTENSION.getValue(), FilenameUtils.getExtension(ruta));

			Registro auxReg = null;

			auxReg = RegistroConverter.toRegistro(requestManager.get(urlBean.getUrl(), param));
			
			if(adjuntosExternos.equals("NO")) {
			// Mostrar en visor si es una extension valida: PDF o Imagen
			if (auxReg != null && !SysmanFunciones.validarCampoVacio(auxReg.getCampos(), "EXISTE")) {

				File adjunto = new File(rutaAbs);

				InputStream inputStream = new FileInputStream(adjunto);
				if (adjunto.length() > 1502898) {
					byte[] vec = new byte[(int) adjunto.length()];

					inputStream.read(vec, 0, vec.length);

					archivoDescarga = JsfUtil.getArchivoDescarga(new ByteArrayInputStream(vec), adjunto.getName());
					
					if(adjuntosExternos.equals("SI")) {

						inputStream.close();
						adjunto.delete();
					}
					return;
			

				} else {

					SessionUtil.cargarModalVisor(rutaAbs, reg.getCampos()
							.get(FrmWFInformaciongeneralControladorEnum.ADJUNTO_NOM.getValue()).toString());
					return;
				}
			}
			}

			// Descargar archivo
			File adjunto = new File(rutaAbs);
			
			if(adjuntosExternos.equals("SI")) {
				if(!validarConexion(url)) {
					return;
				}
				adjunto = descargarArchivoExterno(carpeta,archivo,adjunto);
			}

			try (InputStream inputStream = new FileInputStream(adjunto)) {
				byte[] vec = new byte[(int) adjunto.length()];

				inputStream.read(vec, 0, vec.length);

				archivoDescarga = JsfUtil.getArchivoDescarga(new ByteArrayInputStream(vec), adjunto.getName());

			     if(adjuntosExternos.equals("SI")) {
			        	inputStream.close();
						adjunto.delete();
					}
			} catch (IOException | JRException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
		} catch (SystemException | IOException | JRException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		// </CODIGO_DESARROLLADO>
	}

	public void mostrarBtnHistorial() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(DTramiteVariablesControladorUrlEnum.URL006.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("TIPOTRAMITE", tipoTramite);
		param.put("PROCESO", proceso);
		param.put("TRAMITE", tramite);

		Registro auxReg = null;

		try {
			auxReg = RegistroConverter.toRegistro(requestManager.get(urlBean.getUrl(), param));

			if (auxReg != null && !SysmanFunciones.validarCampoVacio(auxReg.getCampos(), "EXISTE")) {

				mostrarBtn = true;

			} else {

				mostrarBtn = false;

			}
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

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
		try {
		/*-Solo permite modificar si se accede desde el formulario de tramites*/
		permiteModificar = codFormRedireccion == GeneralCodigoFormaEnum.FRM_TRAMITES_CONTROLADOR.getCodigo();
		cargarParametros();
		cargarListaExtensiones();
		mostrarBtnHistorial();
		
		adjuntosExternos = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania, 
		           "MANEJA ARCHIVOS ADJUNTOS EXTERNOS", 
		            modulo, new Date(), false),"NO");
		
	} catch (SystemException e) {
		logger.error(e.getMessage(), e);
		JsfUtil.agregarMensajeError(e.getMessage());
	}
		// </CODIGO_DESARROLLADO>
	}

	private void cargarParametros() {
		try {
			rutaServidorEscaner = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
					"RUTA SERVIDOR ESCANER WORKFLOW",
					SessionUtil.getModulo(), new Date(), false),"/opt/sysman/data/documentos/WORKFLOW");
		} catch (SystemException e) {
			e.printStackTrace();
		}

	}
	
	//metodo para arma el nombre y ruta del archivo
	public static String generarNombreArchivo(String rutaIni,
			Map<String, Object> llave,
			String extension,String carpetas) {

		StringBuilder nombre = new StringBuilder();

		for (Object valor : llave.values()) {
			nombre.append(valor);
			nombre.append("_");
		}
		nombre.append(".");
		nombre.append(extension);

		StringBuilder ruta = new StringBuilder();

		ruta.append(rutaIni);
		ruta.append(carpetas);

		File verificar = new File(ruta.toString());
		if (!verificar.isDirectory()) {
			verificar.mkdirs();
		}

		ruta.append(nombre);

		return ruta.toString();
	}



	/**
	 * Metodo ejecutado cuando se cancela la edicion del registro seleccionado.
	 */
	@Override
	public void cancelarEdicion(RowEditEvent event) {
		getListaInicial().load();
	}

	/**
	 * Metodo ejecutado antes de realizar la insercion del registro.
	 * 
	 * @return true -> Permite realizar la insercion del registro.
	 */
	@Override
	public boolean insertarAntes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion del registro.
	 * 
	 * @return true
	 */
	@Override
	public boolean insertarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado antes de realizar la insercion o actualizacion del registro.
	 * 
	 * @return true -> Permite realizar la insercion o actualizacion del registro.
	 */
	@Override
	public boolean actualizarAntes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion y actualizacion del
	 * registro.
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
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la eliminacion del registro.
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
	 * Este metodo se ejecuta antes enviar la accion de actualizacion, en el se
	 * pueden remover valores auxiliares que no se desee o se deban enviar en el
	 * registro
	 */
	@Override
	public void removerCombos() {
		// <CODIGO_DESARROLLADO>
		registro.getCampos().remove(DTramiteVariablesControladorEnum.OBLIGATORIO.getValue());

		registro.getCampos().remove(DTramiteVariablesControladorEnum.ETIQUETA.getValue());

		registro.getCampos().remove(DTramiteVariablesControladorEnum.CODIGO_PROCESO.getValue());

		registro.getCampos().remove("TIPO_DATO");
		registro.getCampos().remove("CODIGO_NODO");
		registro.getCampos().remove("NUMERO_TRAMITE");

		registro.getCampos().remove(DTramiteVariablesControladorEnum.ADJUNTO_NOM.getValue());

		registro.getCampos().remove("VALOR_FECHA_STR");

		registro.getCampos().remove(DTramiteVariablesControladorEnum.ADJUNTO.getValue());

		registro.getCampos().remove(DTramiteVariablesControladorEnum.CODIGO_NODO_VARIABLE.getValue());

		registro.getCampos().remove(cCompania);
		registro.getCampos().remove("MANEJA_ADJUNTO");
		registro.getCampos().remove("ADJUNTO_OBLIGATORIO");

		registro.getCampos().remove(DTramiteVariablesControladorEnum.TIPO_TRAMITE.getValue());

		registro.getCampos().remove("CONSECUTIVO_TRAMITE");

		registro.getCampos().remove("OCR");
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado cuando se activa la edicion de un registro del formulario.
	 * Determina el tipo de dato que se puede actualizar segun la etiqueta
	 * seleccionada.
	 *
	 * @param registro registro del cual se activo la edicion.
	 */
	public void activarEdicion(Registro registro) {
		indice = listaInicial.getRowIndex();

		tipoDato = registro.getCampos().get("TIPO_DATO").toString();

	}

	/**
	 * Metodo ejecutado desde un comando remoto cuando se cierra el formulario.
	 * Retorna al formulario de tramites (1763).
	 */
	public void ejecutarrcCerrar() {
		// <CODIGO_DESARROLLADO>
		Map<String, Object> param = new TreeMap<>();
		param.put(FrmTramitesControladorEnum.PR_ROWKEY.getValue(), ridTramite);

		Direccionador dir = new Direccionador();
		dir.setNumForm(Integer.toString(codFormRedireccion));

		if (GeneralCodigoFormaEnum.FRM_MONITOR_HISTORIAL_CONTROLADOR.getCodigo() == codFormRedireccion) {
			param.put(DTramiteVariablesControladorEnum.PR_PROCESO.getValue(), proceso);

			param.put(DTramiteVariablesControladorEnum.PR_TIPO_TRAMITE.getValue(), tipoTramite);

			param.put(DTramiteVariablesControladorEnum.PR_TRAMITE.getValue(), tramite);

			param.put(DTramiteVariablesControladorEnum.PR_D_TRAMITE.getValue(), detalleTramite);

			param.put(DTramiteVariablesControladorEnum.PR_NODO.getValue(), nodo);
		}

		dir.setParametros(param);

		SessionUtil.redireccionarForma(dir, modulo);
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Este metodo es ejecutado despues de finalizar la insercion y edicion del
	 * registro se usa cuando se desean agregar valores al registro despues de
	 * dichas acciones
	 */
	@Override
	public void asignarValoresRegistro() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>

	}

	/**
	 * Metodo utilizado para recuperar la ruta donde se deben ubicar los archivos
	 * para el nodo actual.
	 * 
	 * @param variable -> Codigo de la variable.
	 * @return
	 *         <li>Ruta donde se deben ubicar los archivos para el nodo actual.
	 *         <li>En caso de no encontrar la ruta retorna vacio.
	 * @throws SystemException
	 */
	private String recuperarRutaAdjunto(String variable) throws SystemException {
		Map<String, Object> param = new TreeMap<>();
		param.put(cCompania, compania);
		param.put(DTramiteVariablesControladorEnum.PROCESO.getValue(), proceso);
		param.put(DTramiteVariablesControladorEnum.NODO.getValue(), nodo);
		param.put(DTramiteVariablesControladorEnum.VARIABLE.getValue(), variable);

		Registro auxReg = RegistroConverter.toRegistro(requestManager.get(
				UrlServiceUtil.getUrlBeanById(DTramiteVariablesControladorUrlEnum.URL0001.getValue()).getUrl(), param));

		if (auxReg != null) {
			return auxReg.getCampos().get(DTramiteVariablesControladorEnum.ADJUNTO.getValue()).toString();
		}

		return "";
	}

	/**
	 * Actualiza el adjunto de la variable en el nodo actual del tramite.
	 * 
	 * @param variable    -> Codigo de la variable.
	 * @param rutaAdjunto -> Ruta absoluta del adjunto.
	 * @throws SystemException
	 */
	private void actualizarAdjuntoVarTramite(String variable, String rutaAdjunto) throws SystemException {
		Map<String, Object> paramSet = new TreeMap<>();
		paramSet.put(cCompania, compania);

		paramSet.put(DTramiteVariablesControladorEnum.PROCESO.getValue(), proceso);

		paramSet.put(DTramiteVariablesControladorEnum.TIPO_TRAMITE.getValue(), tipoTramite);

		paramSet.put(DTramiteVariablesControladorEnum.TRAMITE.getValue(), tramite);

		paramSet.put(DTramiteVariablesControladorEnum.D_TRAMITE.getValue(), detalleTramite);

		paramSet.put(DTramiteVariablesControladorEnum.NODO.getValue(), nodo);

		paramSet.put(DTramiteVariablesControladorEnum.VARIABLE.getValue(), variable);

		paramSet.put(DTramiteVariablesControladorEnum.ADJUNTO.getValue(), rutaAdjunto);

		paramSet.put(GeneralParameterEnum.MODIFIED_BY.getName(), usuario);

		Parameter parameter = new Parameter();
		parameter.setFields(paramSet);

		UrlBean urlBean = UrlServiceUtil.getUrlBeanById(DTramiteVariablesControladorUrlEnum.URL0002.getValue());

		requestManager.update(urlBean.getUrl(), urlBean.getMetodo(), parameter);
	}

	/**
	 * Determina si el archivo de la ruta indicada es una imagen, office, comprimido
	 * o PDF.
	 * 
	 * @param ruta -> Ruta donde se encuentra ubicado el archivo.
	 * @return
	 *         <li>true -> Cuando es otro tipo de archivo.
	 *         <li>false -> Cuando el archivo es una imagen, office, comprimido o
	 *         PDF.
	 */
	private boolean validarExtAdjunto(String ruta) {
		Map<String, Object> param = new TreeMap<>();

		param.put(DTramiteVariablesControladorEnum.TIPO_MIME.getValue(), tipoMimeAppIma);

		param.put(DTramiteVariablesControladorEnum.EXTENSION.getValue(), FilenameUtils.getExtension(ruta));

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(DTramiteVariablesControladorUrlEnum.URL001.getValue());
		Registro auxReg = null;

		try {
			auxReg = RegistroConverter.toRegistro(requestManager.get(urlBean.getUrl(), param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		return auxReg == null || SysmanFunciones.validarCampoVacio(auxReg.getCampos(),
				DTramiteVariablesControladorEnum.EXISTE.getValue());
	}

	/**
	 * Valida que la extension corresponda al contenido del archivo mediante el
	 * MIME.
	 * 
	 * @param ruta -> Ruta donde se encuentra ubicado el archivo.
	 * @return
	 *         <li>true</li> -> Cuando la extension del archivo no correponde a su
	 *         contenido.
	 *         <li>false</li> -> Cuando la extension correponde al tipo de contenido
	 *         del archivo.
	 */
	private boolean validarMimeAdjunto(String ruta) {
		String mimeFile = "";

		Map<String, Object> param = new TreeMap<>();

		param.put(DTramiteVariablesControladorEnum.TIPO_MIME.getValue(), tipoMimeAppIma);

		param.put(DTramiteVariablesControladorEnum.EXTENSION.getValue(), FilenameUtils.getExtension(ruta));

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(DTramiteVariablesControladorUrlEnum.URL002.getValue());
		Registro auxReg = null;

		try {
			URL url = new File(ruta).toURI().toURL();

			mimeFile = url.openConnection().getContentType();

			/*-Subir archivos con formato desconocido, mientras tanto.*/
			if ("content/unknown".equals(mimeFile)) {
				return false;
			}

			param.put(GeneralParameterEnum.CODIGO.getName(), mimeFile);

			auxReg = RegistroConverter.toRegistro(requestManager.get(urlBean.getUrl(), param));
		} catch (IOException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		return auxReg == null || SysmanFunciones.validarCampoVacio(auxReg.getCampos(),
				DTramiteVariablesControladorEnum.EXISTE.getValue());
	}

	/**
	 * Lista las extenciones con tipo de mime:
	 * <li>33 -> APPLICATION
	 * <li>37 -> IMAGE
	 */
	private void cargarListaExtensiones() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(DTramiteVariablesControladorUrlEnum.URL003.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(DTramiteVariablesControladorEnum.TIPO_MIME.getValue(), tipoMimeAppIma);

		Registro auxReg = null;

		try {
			auxReg = RegistroConverter.toRegistro(requestManager.get(urlBean.getUrl(), param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		if (auxReg != null) {
			extension = SysmanFunciones.nvl(auxReg.getCampos().get("EXTENSIONES"), "").toString();
		}
	}
	
public boolean validarConexion(String url) throws SysmanException {

		
		HttpURLConnection connection = null;
		String msgError;	
		boolean rta = false;

		try {
			LOG.info(KEY_LOG_URL, url);
			//validando conexion 
			connection = (HttpURLConnection) new URL(url).openConnection();
		} catch (IOException e1) {
			msgError = "No se pudo establecer conexion con el servicio: "+ url ;
			LOG.error(ERROR, msgError);
		}
		try {
			connection.setDoInput(true);
			connection.setDoOutput(false);
			connection.setRequestProperty(METHOD, GET);
		} catch (Exception e) {
			if (connection != null) {
				connection.disconnect();
			}
			msgError = "Error al configurar la petición de conexión al servidor";
			LOG.error(ERROR, msgError);
		}

		try {
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				
				rta = true;
				
			}
			
		} catch (IOException e) {
			if (connection != null) {
				connection.disconnect();
			}
			JsfUtil.agregarMensajeAlerta("El servidor de archivos no se encuentra disponible en este momento.");
		}
		
	return rta;
	}
	
	
	public File descargarArchivoExterno(String carpeta, String archivo,File data) {
		File adjunto = data;
		String urlDescargar = url + "/descargar/"+carpeta.replaceAll("/", "-") +"/"+ archivo.replaceAll(" ", "%20");
		
		HttpURLConnection connection = null;
		String msgError;	

		try {
			LOG.info(KEY_LOG_URL, urlDescargar);
			//validando conexion 
			connection = (HttpURLConnection) new URL(urlDescargar).openConnection();
		} catch (IOException e1) {
			msgError = "No se pudo establecer conexion con el servicio: "+ urlDescargar ;
			LOG.error(ERROR, msgError);
		}
		try {
			connection.setDoInput(true);
			connection.setDoOutput(false);
			connection.setRequestProperty(METHOD, GET);
		} catch (Exception e) {
			if (connection != null) {
				connection.disconnect();
			}
			msgError = "Error al configurar la petición para descargar el adjunto";
			LOG.error(ERROR, msgError);

		}

		try {
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				//				BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
				//				String output;
				//				StringBuffer response = new StringBuffer();
				//				while ((output = br.readLine()) != null) {
				//					response.append(output);
				//				}	
			Files.copy(connection.getInputStream(), adjunto.toPath(), StandardCopyOption.REPLACE_EXISTING);
				
			}
			
		} catch (IOException e) {
			if (connection != null) {
				connection.disconnect();
			}
			//JsfUtil.agregarMensajeAlerta("Servidor de archivos no se encuentra disponible en este momento");
		}
		return adjunto;

	}
	// <SET_GET_ATRIBUTOS>

	public UploadedFile getArchivoCargaBuscarAdjunto() {
		return archivoCargaBuscarAdjunto;
	}

	public void setArchivoCargaBuscarAdjunto(UploadedFile archivoCargaBuscarAdjunto) {
		this.archivoCargaBuscarAdjunto = archivoCargaBuscarAdjunto;
	}

	public int getIndice() {
		return indice;
	}

	public void setIndice(int indice) {
		this.indice = indice;
	}

	public String getTipoDato() {
		return tipoDato;
	}

	public void setTipoDato(String tipoDato) {
		this.tipoDato = tipoDato;
	}

	public boolean isPermiteModificar() {
		return permiteModificar;
	}

	public void setPermiteModificar(boolean permiteModificar) {
		this.permiteModificar = permiteModificar;
	}

	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	/**
	 * @return the mostrarBtn
	 */
	public boolean isMostrarBtn() {
		return mostrarBtn;
	}

	/**
	 * @param mostrarBtn the mostrarBtn to set
	 */
	public void setMostrarBtn(boolean mostrarBtn) {
		this.mostrarBtn = mostrarBtn;
	}

	/**
	 * @return the enviaCorreo
	 */
	public boolean isEnviaCorreo() {
		return enviaCorreo;
	}

	/**
	 * @param enviaCorreo the enviaCorreo to set
	 */
	public void setEnviaCorreo(boolean enviaCorreo) {
		this.enviaCorreo = enviaCorreo;
	}

	/**
	 * @return the procedenciaAut
	 */
	public boolean isProcedenciaAut() {
		return procedenciaAut;
	}

	/**
	 * @param procedenciaAut the procedenciaAut to set
	 */
	public void setProcedenciaAut(boolean procedenciaAut) {
		this.procedenciaAut = procedenciaAut;
	}

	// </SET_GET_ATRIBUTOS>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_LISTAS>
	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	// </SET_GET_LISTAS_COMBO_GRANDE>
}
