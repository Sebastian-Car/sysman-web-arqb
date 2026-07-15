/*
 * To change this license header, choose License Headers in Project
 * Properties. To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sysman.identificacion;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sysman.cache.enums.UrlServiceCache;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.CompaniaDao;
import com.sysman.dao.Registro;
import com.sysman.dao.UsuarioDao;
import com.sysman.ejb.report.ParameterProviderRemote;
import com.sysman.email.ApiRestClient;
import com.sysman.email.EmailPojo;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.clientwso2.exceptions.ClientWSO2Exception;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.RequestManager;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.logica.Compania;
import com.sysman.logica.Formulario;
import com.sysman.logica.Grupo;
import com.sysman.logica.Usuario;
import com.sysman.recursos.ejb.EjbAutorizacionRemote;
import com.sysman.recursos.ejb.EjbMenukRemote;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.FormContinuoService;
import com.sysman.session.SessionContainerSt;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.nio.charset.StandardCharsets;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.WordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author cmanrique
 */
@ManagedBean
@SessionScoped
public class LoginBean {

    private static final String SERVICIO_TODAS_COMPANIAS = "59003";
    private static final String SERVICIO_AUDITA_LOGUEO = "1667001";
    private static final String SERVICIO_AUDITA_MENU = "1668001";
    private static final String SERVICIO_SEGUNDA_CLAVE = "47025";
    private static final String SERVICIO_EMAIL = "1663004";
    private static final String SERVICIO_ANTIPICIPOS = "72119";
    /**
     * Constante que identifica el servicio que busca la URL y tipo de
     * conexiï¿½n
     */
    private static final String SERVICIO_AUTENTICACION = "1710001";

    private static final String SERVICIO_POLITICACONTRASENA = "47030";

    private static final String SERVICIO_VIGENCIACONTRASENIA = "1864002";
    
    private static final String SERVICIO_VIGENCIACERTIFICADO = "1851002"; 
    
    private static final String SERVICIO_VALIDA_ALERTA = "47035";  

    private static final String SERVICIO_COMPANIA_POR_USUARIO = "59030";
    
    private static final String SERVICIO_USUARIO_VIGENTE = "47036";
    
    private static final String SERVICIO_UPDATE_ESTADO = "47037";  
    
    private static final String SERVICIO_USUARIO = "47039";
    
    private ScheduledExecutorService scheduler;
    private static final AtomicBoolean isTaskScheduled = new AtomicBoolean(false);
    private ScheduledFuture<?> scheduledFuture;

    private String usuario;
    private String contrasenia;
    private String compania;
    private Compania companiaIngreso;
    private Usuario user;
    private String menu;
    private String menuActual;
    private String form;
    private String modulo;
    private String comando;
    private String formato;
    private int cantidadAlertas = 0;
    //VARIABLE DEL DESARRO DE MARCA BLANCA
    private String tituloSeccionLogin;

    private boolean contrasenaValida;

    private int iteracionesMenu;

    private String imagenCompania;
    
    protected ResourceBundle idioma;
    private Map<String, Object> variables;

    private List<Registro> companias;

    @EJB
    private SessionContainerSt sessionContainer;

    @EJB
    private EjbAutorizacionRemote ejAutorizacion;

    @EJB
    private ParameterProviderRemote ejbParameterProvider;

    @EJB
    private EjbMenukRemote ejbMenuk;

    /**
     * Implementaciï¿½n para realizar el consumo de conecciï¿½n por ldap o
     * directorio activo
     */
    private APIAutenticacion autenticacion;

    /**
     * Guarda la url consultada en la base de datos
     */
    private String urlConsulta;

    /**
     * Guarda el tipo de directorio consultado en la base de datos
     */
    private String tipoDirectorio;

    /**
     * Implementacion del EJB de SysmanUtil para acceder a funciones
     * y/o procedimientos definidos en el paquete PCK_SYSMAN_UTL
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    private String rutaIframe;
    private String sessionId;
    private boolean aceptaTerminos;
    private boolean visibleTerminos;
    private boolean disBtnInicio;
    private String nombreCompania = "";
    private String urlTratamiento = "";

    private FormContinuoService service;

    protected final Log logger = LogFactory.getLog(this.getClass());

    private MenuModel modeloMenu;
    /**
     * Atributo que valida si el boton y campo de segunda contrasena
     * se visualiza o no
     */
    private boolean visibleSegundaContrasena = false;
    /**
     * Atributo que almacena el valor del campo de segunda contrasena
     */
    private String contraseniaDos;

    /**
     * Creates a new instance of LoginBean
     */
    public LoginBean() {
        idioma = ResourceBundle.getBundle(SysmanConstantes.RUTA_IDIOMA);
        service = FormContinuoService.getInstance();
        variables = new HashMap<>();
        menu = "0";
    }

    @PostConstruct
    public void inicializar() {
        usuario = "";
        contrasenia = "";
        compania = "001";
        tituloSeccionLogin =  JsfUtil.obtenerParametroMarcaBlanca("TITULOLOGIN");
        RequestManager requestManager = new RequestManager();
        try {
            UrlBean todasCompanias = UrlServiceUtil
                            .getUrlBeanById(SERVICIO_TODAS_COMPANIAS);
            if (todasCompanias == null) {
                StringBuilder msg = new StringBuilder();
                msg.append("Error en <<inicializar>> ->> ");
                msg.append("No se pudo recuperar el recurso ->> ");
                msg.append(SERVICIO_TODAS_COMPANIAS);
                throw new SystemException(msg.toString());
            }
            List<Parameter> parameters = requestManager
                            .getList(todasCompanias.getUrl(), null);
            companias = RegistroConverter.toListRegistro(parameters);
            cambiarCompania();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeFatal(e.getMessage());
        }
    }

    public void iniciarMenu() throws IOException {
        String permisos = "permisos";
        if ((user == null)
            || ((sessionId != null)
                && !sessionContainer.existSession(sessionId))
            || (sessionId == null)) {
            variables.remove(permisos);
            ExternalContext ec = FacesContext.getCurrentInstance()
                            .getExternalContext();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_INICIE_SESION"));
            ec.redirect(ec.getRequestContextPath());
            return;
        }
        try {
            if ((SessionUtil.getSessionVarContainer(permisos) != null)
                && !(boolean) SessionUtil.getSessionVarContainer(permisos)) {
                SessionUtil.removeSessionVarContainer(permisos);
                JsfUtil.agregarMensajeFatal(
                                idioma.getString(
                                                Constantes.MSM_PERMISOS_ACCEDER));
            }

            String error = (String) SessionUtil
                            .getSessionVarContainer(
                                            SysmanConstantes.LLAVE_MENSAJE_ERROR);
            if (error != null) {
                SessionUtil.removeSessionVarContainer(
                                SysmanConstantes.LLAVE_MENSAJE_ERROR);
                JsfUtil.agregarMensajeError(error);
            }

            String abreForm = (String) SessionUtil
                            .getSessionVarContainer(
                                            SysmanConstantes.LLAVE_FORMULARIO_ABRIR);
            if (abreForm != null) {
                SessionUtil.removeSessionVarContainer(
                                SysmanConstantes.LLAVE_FORMULARIO_ABRIR);
                JsfUtil.ejecutarJavaScript(
                                "setTimeout(function(){ $('#" + abreForm
                                    + "').click(); },100)");
            }

            String modal = (String) SessionUtil
                            .getSessionVarContainer(
                                            SysmanConstantes.LLAVE_FORMULARIO_MODAL);
            if (modal != null) {
                String[] datos = modal.split(",");

                SessionUtil.removeSessionVarContainer(
                                SysmanConstantes.LLAVE_FORMULARIO_MODAL);
                JsfUtil.ejecutarJavaScript(
                                "setTimeout(function(){ verMenu('"
                                    + SessionUtil.getModulo() + "', '"
                                    + datos[0] + "', '" + datos[1]
                                    + "'); },100)");
            }

            String mensajeInicio = (String) SessionUtil
                            .getSessionVarContainer(
                                            SysmanConstantes.LLAVE_MENSAJE_ABRIR);

            if (mensajeInicio != null) {
                SessionUtil.removeSessionVarContainer(
                                SysmanConstantes.LLAVE_MENSAJE_ABRIR);

                String[] datos = mensajeInicio.split("#");

                String tipo = datos[0];
                String mensaje = datos[1];

                if (SysmanConstantes.MSJ_INFORMATIVO.equals(tipo)) {
                    JsfUtil.agregarMensajeInformativo(mensaje);
                }
                else if (SysmanConstantes.MSJ_ALERTA.equals(tipo)) {
                    JsfUtil.agregarMensajeAlerta(mensaje);
                }
                else if (SysmanConstantes.MSJ_ERROR.equals(tipo)) {
                    JsfUtil.agregarMensajeError(mensaje);
                }
                else if (SysmanConstantes.MSJ_FATAL.equals(tipo)) {
                    JsfUtil.agregarMensajeFatal(mensaje);
                }

            }

            String formRetornoIn = (String) SessionUtil
                            .getSessionVarContainer(
                                            SysmanConstantes.LLAVE_FORMULARIO_RETORNO_IN);

            String formRetornoOut = (String) SessionUtil
                            .getSessionVarContainer(
                                            SysmanConstantes.LLAVE_FORMULARIO_RETORNO_OUT);

            if (formRetornoIn != null || formRetornoOut != null) {
                iteracionesMenu++;

                // Se realizan 3 iteraciones
                if (iteracionesMenu == 3) {
                    String[] datos;
                    iteracionesMenu = 0;

                    if (formRetornoIn != null) {
                        datos = formRetornoIn.split(",");

                        SessionUtil.removeSessionVarContainer(
                                        SysmanConstantes.LLAVE_FORMULARIO_RETORNO_IN);

                        JsfUtil.ejecutarJavaScript(
                                        "setTimeout(function(){ verMenu('"
                                            + datos[2] + "', '"
                                            + datos[0] + "', '" + datos[1]
                                            + "'); },100)");
                    }
                    else {
                        datos = formRetornoOut.split(",");
                        SessionUtil.removeSessionVarContainer(
                                        SysmanConstantes.LLAVE_FORMULARIO_RETORNO_OUT);

                        JsfUtil.ejecutarJavaScript(
                                        "setTimeout(function(){ verMenu('"
                                            + datos[2] + "', '"
                                            + datos[0] + "', '" + datos[1]
                                            + "'); },100)");
                    }
                }
            }

        }
        catch (NamingException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        menu = SysmanFunciones.nvlStr((String) sessionContainer
                        .getSession(sessionId).get("menu"), "0");
    }
    /**
     * Carga las companias asociadas al usuario actual.
     *
     * Este metodo consulta el servicio correspondiente para obtener las companias
     * vinculadas al usuario.
     */
     private void cargarCompaniasPorUsuario() {
         RequestManager requestManager = new RequestManager();
         try {
             UrlBean companiasPorUsuario = UrlServiceUtil.getUrlBeanById(SERVICIO_COMPANIA_POR_USUARIO);
             
             if (companiasPorUsuario == null) {
             	StringBuilder msg = new StringBuilder();
                 msg.append("Error en <<inicializar>> ->> ");
                 msg.append("No se pudo recuperar el recurso ->> ");
                 msg.append(SERVICIO_TODAS_COMPANIAS);
                 throw new SystemException(msg.toString());
             }
             
             // Crear parametros para enviar el usuario
             Map<String, Object> parametros = new TreeMap<>();
             parametros.put("USUARIO", usuario);
             
             List<Parameter> parameters = requestManager.getList(
                 companiasPorUsuario.getUrl(), 
                 parametros
             );
             
             companias = RegistroConverter.toListRegistro(parameters);

        
             if (companias != null && !companias.isEmpty()) {
                 Object codigo = companias.get(0).getCampos().get("CODIGO");
                 this.compania = (codigo != null) ? codigo.toString() : null;
            }
             
         } catch (SystemException e) {
         	 logger.error(e.getMessage(), e);
              JsfUtil.agregarMensajeFatal(e.getMessage());
         }
     }

    public String login() {
    	
        RequestManager requestManager = new RequestManager();
        FacesContext faceContext = null;
        menu = "0";

        String paginaRetorno = "index";
        try {
            faceContext = FacesContext.getCurrentInstance();
            if (manejaDobleContrasena()) {
                if (SysmanFunciones.validarVariableVacio(contraseniaDos)) {
                    manejaError("Esta obligando doble contraseÃ±a y no se ha digitado",
                                    faceContext);
                    return paginaRetorno;
                }
                Registro rsUsuario = getDatosSegundaClave();
                if (rsUsuario == null) {
                    manejaError("El usuario no existe o la doble contraseÃ±a ya esta vencida",
                                    faceContext);
                    return paginaRetorno;
                }
                else if (!(contraseniaDos.equals(rsUsuario.getCampos()
                                .get("SEGUNDA_CLAVE")
                                .toString()))) {
                    manejaError("La doble contraseÃ±a esta errada", faceContext);
                    return paginaRetorno;
                }
            }

			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.CODIGO.getName(), usuario);

			Date fechaActual = new Date();
			try {
				Registro rsUsuario = RegistroConverter.toRegistro(requestManager.get(
						UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(SERVICIO_USUARIO_VIGENTE).getUrl(),
						param));

				if (rsUsuario != null) {
					if (rsUsuario.getCampos().get("VIGENTE").toString().equals("NO")) {
						
						UrlBean updateEstado = UrlServiceUtil.getInstance()
			                    .getUrlServiceByUrlByEnumID(SERVICIO_UPDATE_ESTADO);
			    	
			        	 Map<String, Object> parametros = new HashMap<>();
			        	 parametros.put(GeneralParameterEnum.KEY_CODIGO.getName(), usuario);
			        	 parametros.put(GeneralParameterEnum.MODIFIED_BY.getName(), usuario);
			        	 parametros.put(GeneralParameterEnum.DATE_MODIFIED.getName(), fechaActual);
			        	 parametros.put(GeneralParameterEnum.ESTADO.getName(), "R");
			        	 
			           		Parameter parameter = new Parameter();
			           		parameter.setFields(parametros);
			           		try {
			 					requestManager.update(updateEstado.getUrl(),
			 					        updateEstado.getMetodo(),
			 					        parameter);
			 				} catch (SystemException e) {
			 					logger.error(e.getMessage(), e);
			 				}
						
						 manejaError("Su usuario está en estado Retirado, por favor contacte al administrador del sistema",
                                 faceContext);
						return paginaRetorno;
					} 
				}

			} catch (SystemException e) {
				JsfUtil.agregarMensajeError(e.getMessage());
				logger.error(e.getMessage(), e);

			}
        
            UsuarioDao udao = new UsuarioDao();
            CompaniaDao cdao = new CompaniaDao();
            if (manejaAccesoPorLDAP()) {
                try {
                    if (!(servicioAuteticacion(compania, usuario,
                                    contrasenia))) {
                        manejaError("Inconsistencias al validar por ldap",
                                        faceContext);
                        return paginaRetorno;
                    }
                }
                catch (MalformedURLException e) {
                    manejaError("La URL de ldap no responde", faceContext);
                    return paginaRetorno;
                }
                catch (IOException e) {
                    manejaError("El servicio de ldap no responde", faceContext);
                    return paginaRetorno;
                }
                catch (SysmanException e) {
                    manejaError(e.getMessage(), faceContext);
                    return paginaRetorno;
                }
                catch (NullPointerException e) {
                    manejaError("El servicio de ldap no responde", faceContext);
                    return paginaRetorno;
                }
                catch (Exception e) {
                    manejaError("Error al autenticar a traves de LDAP",
                                    faceContext);
                    return paginaRetorno;
                }
                udao.validarUsuario(usuario, compania);
            }
            else {
                udao.validarUsuario(usuario, contrasenia, compania);
            }

            user = udao.getUsuario();

            if (user != null) {
                faceContext = FacesContext.getCurrentInstance();
                if (manejaPoliticaAcceso()
                    && !(ejbMenuk.autorizarAccesoUsuario(compania,
                                    user.getCodigo()))) {
                    String mensaje = "No puede acceder al sistema de acuerdo con la polÃ­tica de acceso.";
                    manejaError(mensaje, faceContext,
                                    FacesMessage.SEVERITY_INFO);
                    return paginaRetorno;
                }
                autorizarUsuario(user);
                if (user.getOpcionesMenu().isEmpty()) {
                    FacesMessage facesMessage = new FacesMessage(
                                    FacesMessage.SEVERITY_ERROR,
                                    idioma.getString(
                                                    "MSM_USUSARIO_SIN_PERMISOS"),
                                    null);
                    faceContext.addMessage(null, facesMessage);
                    return paginaRetorno;
                }

                SessionUtil.setSessionVar("usuario", user);
                SessionUtil.setSessionVar("compania", compania);
                companiaIngreso = cdao.validarCompania(compania);

                SessionUtil.setSessionVar("excelPlano", consultarParametro(
                                "GENERAR REPORTES EN EXCEL DESDE CONSULTA"));

                cargarImagenCompania();

                SessionUtil.setSessionVar("companiaIngreso", companiaIngreso);

                variables.put("usuario", user);
                variables.put("compania", compania);
                variables.put("companiaIngreso", companiaIngreso);
                variables.put("excelPlano", SessionUtil.getExcePlano());

                // if (!validarVigenciaContrasena()) {

                // }

                // else {

                //
                generarCookie();
                generarBitacora();
                
                if(consultarParametro("MANEJA ALERTAS EN ANTICIPO").equals("SI")) {

                	startAlertSchedule();
                }
//               	else {
//                	isTaskScheduled.set(true);
//                	stopAlertSchedule();
//                }

                return "menu.sysman?faces-redirect=true";

            }
            else {

                faceContext = FacesContext.getCurrentInstance();
                FacesMessage facesMessage = new FacesMessage(
                                FacesMessage.SEVERITY_ERROR,
                                idioma.getString(
                                                "MSM_USUARIO_CONTRASENIA_INCORRECTO"),
                                null);
                faceContext.addMessage(null, facesMessage);

                return paginaRetorno;

            }

        }
        catch (SysmanException | SystemException e) {
            logger.error(e.getMessage(), e);
            faceContext = FacesContext.getCurrentInstance();
            FacesMessage facesMessage = new FacesMessage(
                            FacesMessage.SEVERITY_FATAL, e.getMessage(), null);
            faceContext.addMessage(null, facesMessage);
            return paginaRetorno;
        }

    }

    public void validarDiasNotificacion() {

        int diasNotificacion = 0;
        int cantidadDias = 0;
        int diasExpiracion = 0;

        RequestManager requestManager = new RequestManager();
        Registro rs;
        Registro rs2;

        try {
            rs = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            "1864001")
                                                            .getUrl(),
                                            null));

            if (rs != null) {
                diasNotificacion = Integer.parseInt(SysmanFunciones
                                .nvl(rs.getCampos().get("DIAS_NOTIFICACION"),
                                                "0")
                                .toString());

            }

            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.CODIGO.getName(), usuario);

            rs = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            SERVICIO_POLITICACONTRASENA)
                                                            .getUrl(),
                                            param));

            if (rs != null) {
                cantidadDias = Integer.parseInt(SysmanFunciones
                                .nvl(rs.getCampos().get("DIAS_VIGENCIA"),
                                                "0")
                                .toString());

            }

            rs2 = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            SERVICIO_VIGENCIACONTRASENIA)
                                                            .getUrl(),
                                            null));

            if (rs2 != null) {
                diasExpiracion = Integer.parseInt(SysmanFunciones
                                .nvl(rs2.getCampos().get("TIEMPO_EXPIRACION"),
                                                "0")
                                .toString());

            }

            int dias = diasExpiracion - cantidadDias;

            if (dias > 0 && dias <= diasNotificacion) {

                JsfUtil.agregarMensajeAlertaDialogo(
                                "Su contraseÃ±a vence en " + dias
                                    + " dÃ­as, por favor actualizarla");

            }

        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }
    
    public void validarVigenciaCertificado() {
    	int diasNotificacion = 0;
    	int cantidadDias = 0;
    	boolean alertaCertificado = false;
    	String fechaVencimiento = "";


    	RequestManager requestManager = new RequestManager();
    	Registro rs;
    	Registro rs2;

    	if(cantidadAlertas == 0) {
    		try {
    			Map<String, Object> parametro = new TreeMap<>();

    			parametro.put(GeneralParameterEnum.CODIGO.getName(), usuario);

    			rs2 = RegistroConverter
    					.toRegistro(requestManager.get(
    							UrlServiceUtil.getInstance()
    							.getUrlServiceByUrlByEnumID(
    									SERVICIO_VALIDA_ALERTA)
    							.getUrl(),
    							parametro));

    			alertaCertificado = (boolean) rs2.getCampos().get("ALER_CERTIFICADO");

    			if(alertaCertificado) {
    				rs = RegistroConverter
    						.toRegistro(requestManager.get(
    								UrlServiceUtil.getInstance()
    								.getUrlServiceByUrlByEnumID(
    										"1851001")
    								.getUrl(),
    								null));

    				if (rs != null) {
    					diasNotificacion = Integer.parseInt(SysmanFunciones
    							.nvl(rs.getCampos().get("DIAS_NOTIFICA"),
    									"0")
    							.toString());

    					SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
    					fechaVencimiento = formato.format(rs.getCampos().get("FECHA_VENCIMIENTO"));

    				}

    				Map<String, Object> param = new TreeMap<>();

    				param.put(GeneralParameterEnum.CODIGO.getName(), usuario);

    				rs = RegistroConverter
    						.toRegistro(requestManager.get(
    								UrlServiceUtil.getInstance()
    								.getUrlServiceByUrlByEnumID(
    										SERVICIO_VIGENCIACERTIFICADO)
    								.getUrl(),
    								param));

    				if (rs != null) {
    					cantidadDias = Integer.parseInt(SysmanFunciones
    							.nvl(rs.getCampos().get("DIAS_VIGENCIA"),
    									"0")
    							.toString());

    				}

    				if (cantidadDias > 0 && cantidadDias <= diasNotificacion) {

    					String msg = idioma.getString("TB_TB4439");
    							
    							msg = msg.replace("s$cantidadDias$s", SysmanFunciones.toString(cantidadDias));
    							msg = msg.replace("s$fechaVencimiento$s", fechaVencimiento);

    					JsfUtil.agregarMensajeAlertaDialogo(msg);
    					cantidadAlertas = cantidadAlertas + 1;

    				}
    			}           

    		}
    		catch (SystemException e) {

    			logger.error(e.getMessage(), e);
    			JsfUtil.agregarMensajeError(e.getMessage());
    		}
    	}
    }

    private boolean validarVigenciaContrasena() {

        int cantidadDias = 0;
        int diasExpiracion = 0;
        UsuarioDao udao = new UsuarioDao();

        try {
            String llaveDiasNotificacion = (String) SessionUtil
                            .getSessionVarContainer(
                                            SysmanConstantes.LLAVE_DIAS_NOTIFICACION);

            boolean datoEnSesionValido = false;

            if (llaveDiasNotificacion != null) {

                String[] variablesDiasNotificacion = llaveDiasNotificacion
                                .split(";");

                int diferencia = SysmanFunciones.calcularDiferenciaDias(
                                SysmanFunciones.convertirAFecha(
                                                variablesDiasNotificacion[2]),
                                new Date());
                if (diferencia < 1) {

                    datoEnSesionValido = true;
                    cantidadDias = Integer
                                    .parseInt(variablesDiasNotificacion[0]);
                    diasExpiracion = Integer
                                    .parseInt(variablesDiasNotificacion[1]);
                }

            }

            if (!datoEnSesionValido) {

                cantidadDias = calculcarDiferenciaFechasActualizacion();

                RequestManager requestManager = new RequestManager();

                Registro rs2;

                rs2 = RegistroConverter
                                .toRegistro(requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                SERVICIO_VIGENCIACONTRASENIA)
                                                                .getUrl(),
                                                null));

                if (rs2 != null) {
                    diasExpiracion = Integer.parseInt(SysmanFunciones
                                    .nvl(rs2.getCampos()
                                                    .get("TIEMPO_EXPIRACION"),
                                                    "0")
                                    .toString());

                }

                SessionUtil.setSessionVarContainer(
                                SysmanConstantes.LLAVE_DIAS_NOTIFICACION,
                                cantidadDias + ";" + diasExpiracion + ";"
                                    + SysmanFunciones.convertirAFechaCadena(
                                                    new Date(), "DD/MM/YYYY"));

            }
            else {
                boolean debeActualizar = (boolean) SysmanFunciones.nvl(
                                SessionUtil
                                                .getSessionVarContainer(
                                                                "ACTUALIZAR_FECHA_ACTCONTRASENA"),
                                false);

                if (debeActualizar) {

                    SessionUtil.removeSessionVarContainer(
                                    "ACTUALIZAR_FECHA_ACTCONTRASENA");
                    cantidadDias = calculcarDiferenciaFechasActualizacion();
                }
            }

            if (diasExpiracion <= cantidadDias) {

                JsfUtil.agregarMensajeAlertaDialogo(
                                "La contraseÃ±a no esta vigente. Se debe cambiar");

                return false;

            }

            if (!validarPoliticaContrasena()) {

                JsfUtil.agregarMensajeAlertaDialogo(
                                "La contraseÃ±a ingresada no cumple con las politicas de privacidad");

                return false;
            }

        }

        catch (SystemException | NamingException | ParseException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return true;
    }

    private int calculcarDiferenciaFechasActualizacion() {
        int cantidadDias = 0;
        RequestManager requestManager = new RequestManager();

        Registro rs;

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.CODIGO.getName(), usuario);

        try {
            rs = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            SERVICIO_POLITICACONTRASENA)
                                                            .getUrl(),
                                            param));

            if (rs != null) {
                cantidadDias = Integer.parseInt(SysmanFunciones
                                .nvl(rs.getCampos().get("DIAS_VIGENCIA"),
                                                "0")
                                .toString());

            }

        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return cantidadDias;
    }

    private boolean validarPoliticaContrasena() {

        int cantidadNumeros = 0;
        int cantidadLetras = 0;
        int cantidadSimbolos = 0;

        try {

            String llavePolitcaContrasena = (String) SessionUtil
                            .getSessionVarContainer(
                                            SysmanConstantes.LLAVE_POLITICA_CONTRASENA);

            boolean datoEnSesionValido = false;

            if (llavePolitcaContrasena != null) {

                String[] variablesContrasena = llavePolitcaContrasena
                                .split(";");

                int diferencia = SysmanFunciones.calcularDiferenciaDias(
                                SysmanFunciones.convertirAFecha(
                                                variablesContrasena[3]),
                                new Date());
                if (diferencia < 1) {

                    cantidadNumeros = Integer.parseInt(variablesContrasena[0]);
                    cantidadLetras = Integer.parseInt(variablesContrasena[1]);
                    cantidadSimbolos = Integer.parseInt(variablesContrasena[2]);

                    datoEnSesionValido = true;
                }

            }
            if (!datoEnSesionValido) {

                RequestManager requestManager = new RequestManager();
                Registro rs;

                rs = RegistroConverter
                                .toRegistro(requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                "1864001")
                                                                .getUrl(),
                                                null));

                if (rs != null) {

                    cantidadNumeros = Integer.parseInt(SysmanFunciones
                                    .nvl(rs.getCampos().get("CANTIDAD_NUMEROS"),
                                                    "0")
                                    .toString());
                    cantidadLetras = Integer.parseInt(SysmanFunciones
                                    .nvl(rs.getCampos().get("CANTIDAD_LETRAS"),
                                                    "0")
                                    .toString());
                    cantidadSimbolos = Integer.parseInt(SysmanFunciones
                                    .nvl(rs.getCampos()
                                                    .get("CANTIDAD_SIMBOLOS"),
                                                    "0")
                                    .toString());

                    SessionUtil.setSessionVarContainer(
                                    SysmanConstantes.LLAVE_POLITICA_CONTRASENA,
                                    cantidadNumeros + ";" + cantidadLetras + ";"
                                        + cantidadSimbolos + ";"
                                        + SysmanFunciones.convertirAFechaCadena(
                                                        new Date(),
                                                        "DD/MM/YYYY"));
                }
                String contrasenaValidar = (String) SessionUtil
                                .getSessionVarContainer("PASSWORD");

                contrasenaValidar = contrasenaValidar != null
                    ? contrasenaValidar
                    : contrasenia;

                if (!SysmanFunciones.validarContrasena(contrasenaValidar,
                                cantidadNumeros,
                                cantidadLetras, cantidadSimbolos)) {

                    return false;
                }

            }
        }
        catch (SystemException | NamingException | ParseException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return true;
    }

    /**
     * manejo de mensajes de error
     * 
     * @param mensaje
     * @param faceContext
     */
    private void manejaError(String mensaje, FacesContext faceContext) {
        manejaError(mensaje, faceContext, FacesMessage.SEVERITY_ERROR);
    }

    /**
     * manejo de errores, especificando la severidad del error
     * 
     * @param mensaje
     * @param faceContext
     * @param severityError
     */
    private void manejaError(String mensaje, FacesContext faceContext,
        Severity severityError) {
        FacesMessage facesMessage = new FacesMessage(severityError, mensaje,
                        null);
        faceContext.addMessage(null, facesMessage);
        logger.error(mensaje);
    }

    /**
     * Identifica si el acceso a la aplicaci&oacute;n se debe
     * gestionar por medio de LDAP o Active Directory.
     * 
     * @return verdadero si estï¿½ habilitado el acceso por LDAP
     * @throws SystemException
     * en caso de que se presenten problemas al consultar el
     * parametro.
     */
    private boolean manejaAccesoPorLDAP() throws SystemException {
        return "SI".equals(consultarParametro("MANEJA ACCESO POR LDAP"));
    }

    /**
     * Identifica si el acceso a la aplicaci&oacute;n se debe
     * gestionar con una doble contraseï¿½a
     * 
     * @return verdadero si se obliga a doble contraseï¿½a
     * @throws SystemException
     * en caso de que se presenten problemas al consultar el
     * parametro.
     */
    private boolean manejaDobleContrasena() throws SystemException {
        return "SI".equals(consultarParametro("MANEJA DOBLE CONTRASENA"));
    }

    /**
     * Identifica si el acceso a la aplicaci&oacute;n est&aacute;
     * restringida por pol&iacute;tica de acceso.
     * 
     * @return verdadero si restringe por pol&iacute;tica de acceso
     * @throws SystemException
     * en caso de que se presenten problemas al consultar el
     * parametro.
     */
    private boolean manejaPoliticaAcceso() throws SystemException {
        return "SI".equals(consultarParametro("MANEJA POLITICA DE ACCESO"));
    }

    private void cargarImagenCompania() {
        try {
            imagenCompania = "data:image/png;base64,"
                + JsfUtil.encodeImage(companiaIngreso.getRutaImagen());
        }
        catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    

    public void generarCookie() {
        FacesContext faceContext = FacesContext.getCurrentInstance();
        try {
            String value = SysmanFunciones
                            .getMD5Code(new Date().getTime()
                                + user.getCodigo());
            Map<String, Object> properties = new HashMap<>();
            properties.put("maxAge", -1);
            properties.put("path", "/");
            sessionId = URLEncoder.encode(value, "UTF-8");
            faceContext.getExternalContext().addResponseCookie(
                            "sysmanCookieGeneral", sessionId, properties);
            sessionContainer.setSession(value,
                            (HashMap<String, Object>) variables);
        }
        catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cerrarSesion() {
        try {
            SessionUtil.eliminarSession();
        }
        catch (NamingException e) {
            Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null,
                            e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void ir() {
        variables.put("modulo", modulo);
        variables.put("menu", menu);
        variables.put("menuActual", menuActual);
        SessionUtil.setSessionVar("modulo", modulo);
        SessionUtil.setSessionVar("menu", menu);
        SessionUtil.setSessionVar("menuActual", menuActual);

        if ("RP".equals(comando) || comando.endsWith("QR")) {
            Map<String, Object> parametros = null;
            Map<String, Object> reemplazos = null;
            try {
                parametros = ejbParameterProvider
                                .getParametrosModulo(Integer.parseInt(modulo),
                                                SessionUtil.getUser(),
                                                SessionUtil.getCompaniaIngreso(),
                                                SessionUtil.getSessionRemote(),
                                                form);
                reemplazos = ejbParameterProvider
                                .getReemplazosModulo(Integer.parseInt(modulo),
                                                SessionUtil.getUser(),
                                                SessionUtil.getCompaniaIngreso(),
                                                SessionUtil.getSessionRemote(),
                                                form, menu);

                formato = (formato == null) || formato.isEmpty()
                    ? "PDF"
                    : formato;
                SessionUtil.generarReporte(Integer.parseInt(modulo), form,
                                ReportesBean.FORMATOS.valueOf(formato),
                                comando, parametros, reemplazos);
            }
            catch (IllegalArgumentException e) {
                logger.error(e.getMessage(), e);
                SessionUtil.generarReporte(Integer.parseInt(modulo), form,
                                ReportesBean.FORMATOS.PDF, comando, parametros,
                                reemplazos);
            }
            catch (SysmanException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
            finally {
                formato = null;
                comando = null;
            }
        }
        //Ticket#7737557 GROJAS: Se crea validaciï¿½n para cuando se configura con LK la opciï¿½n de menï¿½ se redireccione al link configurado.
        else if ("LK".equals(comando)) {
            FacesContext context = FacesContext.getCurrentInstance();
            ExternalContext ec = context.getExternalContext();
            
            try {
                String url ="";
            	if (menuActual.equals("99702")) {
            		url = obtenerUrlSSO(form);
            	} else {
            		url = form;
            	}
            	
                ec.redirect(url);
            
            } catch (SysmanException | IOException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
            
        }
        
        else{
            cargarForma();
        }

    }

    private void cargarForma() {
        Formulario forma = SessionUtil
                        .cargarFormulario(form + "," + modulo);
        if (forma == null) {
            JsfUtil.agregarMensajeFatal(
                            idioma.getString(Constantes.MSM_PERMISOS_ACCEDER));
            return;
        }
        boolean[] permisos = forma.getPermisos();
        if ((permisos == null) || !permisos[3]) {
            JsfUtil.agregarMensajeFatal(
                            idioma.getString(Constantes.MSM_PERMISOS_ACCEDER));

        }
        else if (forma.isModal()) {
            Map<String, Object> options = new HashMap<>();
            options.put("modal", true);
            options.put("draggable", false);
            options.put("resizable", false);
            options.put("closable", true);
            if (forma.getRuta().startsWith("../")) {
                options.put("contentWidth", forma.getAnchoModal());
                options.put("contentHeight", forma.getAltoModal());
                rutaIframe = forma.getRuta();
                RequestContext.getCurrentInstance().openDialog(
                                "modalRemote.sysman", options, null);
            }
            else {
                options.put("contentWidth", forma.getAnchoModal());
                options.put("contentHeight", forma.getAltoModal());
                RequestContext.getCurrentInstance().openDialog(
                                forma.getRuta(), options, null);

            }
            JsfUtil.ejecutarJavaScript(
                            "setTimeout(function(){posicionModalArriba('opsMenu:menu-10_dlg', '37px')}, 300);");
        }
        else {
            ExternalContext ec = FacesContext.getCurrentInstance()
                            .getExternalContext();
            try {
                ec.redirect(forma.getRuta());
            }
            catch (IOException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
    }

    public void redirecionarModal(SelectEvent event) {
        if (event.getObject() == null) {
            return;
        }

        Direccionador direccionador = (Direccionador) event.getObject();
        String ruta = direccionador.getRuta();
        if (direccionador.getNumForm() != null) {
            SessionUtil.redireccionarForma(direccionador, modulo);
        }
        else if ((ruta != null) && (direccionador.getParametros() != null)) {
            SessionUtil.redireccionar(direccionador);
        }
        else if (ruta != null) {
            SessionUtil.redireccionar(ruta);
        }

    }

    public String serializar(Object objeto) throws IOException {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(bs);
        os.writeObject(objeto); // this es de tipo DatoUdp
        os.close();

        return bs.toString();
    }

    public void validarIdentificacion() {
        if (sessionId != null) {
            if (!sessionContainer.existSession(sessionId)) {
                SessionUtil.getSession().invalidate();
            }
            else {
                SessionUtil.redireccionarMenu();
            }
        }
    }

    private void autorizarUsuario(Usuario usuario) throws SysmanException {
        Map<String, Grupo> grupos = ejAutorizacion.getGruposAutorizacion(
                        usuario.getCodigo(), compania);
        usuario.setGrupos(grupos);

        Object[] aux = ejAutorizacion.getXMLMenus(compania,
                        usuario);
        usuario.setOpcionesMenu((String) aux[0]);
        List<String> excluidos = (List<String>) aux[1];
        if (!usuario.getOpcionesMenu().isEmpty()
            && !usuario.getGrupos().isEmpty()) {
            usuario.setPermisos(ejAutorizacion.getPermisos(compania,
                            usuario.getCodigo()));
            for (String excluido : excluidos) {
                usuario.getPermisos().remove(excluido);
            }

        }

    }

    /**
     * Permite obtener los datos de la segunda contraseï¿½a del usuario
     * 
     * @author jgomez
     * @return Registro
     */
    private Registro getDatosSegundaClave() {
        Map<String, Object> parametrosEnvio = new TreeMap<>();
        parametrosEnvio.put(GeneralParameterEnum.USUARIO.getName(),
                        usuario);
        Registro rsUsuario = new Registro();
        try {
            RequestManager requestManager = new RequestManager();
            rsUsuario = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil
                                            .getUrlBeanById(SERVICIO_SEGUNDA_CLAVE)
                                            .getUrl(),
                                            parametrosEnvio));
        }
        catch (SystemException | NullPointerException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }
        return rsUsuario;

    }

    /**
     * Metodo que se ejecuta al oprimir el boton se segunda contrasena
     */
    public void enviarContrasena() {
        try {
            boolean verificarContrasena = ejbMenuk.generarClaveUsuario(usuario,
                            20, 6);
            if (verificarContrasena) {
                RequestManager requestManager = new RequestManager();
                Map<String, Object> remplazosDescripcion = new TreeMap<>();
                Map<String, Object> parametrosDescripcion = new TreeMap<>();
                Registro rsUsuario = getDatosSegundaClave();
                remplazosDescripcion.put("Usuario", rsUsuario.getCampos()
                                .get(GeneralParameterEnum.NOMBRE.getName())
                                .toString());
                remplazosDescripcion.put("segundaClave", rsUsuario.getCampos()
                                .get("SEGUNDA_CLAVE")
                                .toString());

                parametrosDescripcion.put(
                                GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                parametrosDescripcion.put(
                                GeneralParameterEnum.CONSECUTIVO.getName(),
                                "16");

                Registro rsEmail = RegistroConverter.toRegistro(
                                requestManager.get(
                                                UrlServiceUtil.getUrlBeanById(
                                                                SERVICIO_EMAIL)
                                                                .getUrl(),
                                                parametrosDescripcion));

                String descripcionFinal = remplazarVariable(
                                rsEmail.getCampos().get(
                                                GeneralParameterEnum.DESCRIPCION
                                                                .getName())
                                                .toString(),
                                remplazosDescripcion);

                /*
                 * Se agrega la implementacion del envio de correo
                 * haciendo uso del API
                 */
                EmailPojo email = new EmailPojo();
                email.setFrom(rsEmail.getCampos().get("ORIGEN").toString());
                email.setTo(rsUsuario.getCampos().get("CORREOELECTRONICO")
                                .toString());
                email.setSubject(rsEmail.getCampos().get("ASUNTO").toString());
                email.setBody(descripcionFinal);

                ApiRestClient client = new ApiRestClient();
                client.postClient(email, compania);

                JsfUtil.ejecutarJavaScript(
                                "PF('alert').renderMessage({'summary': document.getElementById('empresaParametrizada').outerText+' Software','detail': 'Por favor revise su correo y copie la segunda contraseÃ±a para ingresar', 'severity': 'info'});");
            }
        }
        catch (SystemException | NullPointerException | IOException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }

    }

    /**
     * Metodo que remplaza las variables de la descripcion del correo
     */
    public String remplazarVariable(String descripcion,
        Map<String, Object> parametros) {
        String salida = descripcion;

        for (Map.Entry<String, Object> entry : parametros.entrySet()) {
            salida = salida.replace(
                            SysmanFunciones.concatenar("s$", entry.getKey(),
                                            "$s"),
                            SysmanFunciones.toString(entry.getValue()));
        }

        return salida;
    }

    public void generaMenuBarra() {

        Map<String, String> params = FacesContext.getCurrentInstance()
                        .getExternalContext().getRequestParameterMap();
        String moduloActual = params.get("menu");

        if (!"0".equals(moduloActual)) {

            String xml = user.getOpcionesMenu();

            try {
                DocumentBuilder newDocumentBuilder = DocumentBuilderFactory
                                .newInstance().newDocumentBuilder();
                Document parse = newDocumentBuilder
                                .parse(new ByteArrayInputStream(
                                                xml.getBytes("UTF-8")));
                NodeList listado = parse.getElementsByTagName("M");

                Node moduloInicial = null;

                for (int i = 0; i < listado.getLength(); i++) {
                    Node item = listado.item(i);

                    if (item.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) item;

                        String idAct = eElement.getAttribute("id");
                        String padreAct = eElement.getAttribute("class");

                        if (idAct.equals(moduloActual)
                            && "0".equals(padreAct)) {
                            moduloInicial = item;
                            break;
                        }
                    }
                }

                if (moduloInicial != null) {
                    modeloMenu = new DefaultMenuModel();
                    pintarHijos(moduloInicial, null, null);

                    // DefaultMenuItem itemSalir = new
                    // DefaultMenuItem("Salir");
                    // itemSalir.setOnclick("PF('men').jq[0].value='0';pintarMenus(0);");
                    // itemSalir.setStyleClass("menuBarraSalir");
                    // modeloMenu.addElement(itemSalir);
                }

            }
            catch (ParserConfigurationException | IOException
                            | SAXException e1) {
                e1.printStackTrace();
            }
        }
        else {
            modeloMenu = new DefaultMenuModel();
        }

    }

    public void validarCambioContrasena() {

        if (!validarVigenciaContrasena()) {

            contrasenaValida = false;

            JsfUtil.ejecutarJavaScript(
                            "setTimeout(function(){ verMenu('999', '" + "422" +
                                "', '" + "99906" + "'); },1000)");

        }

        else {

            contrasenaValida = true;

        }

    }
    
    public void validarNotificacionInicio() {

    	List<Registro> lista = null;
        RequestManager requestManager = new RequestManager();

		try {
			lista = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID("531005").getUrl(), null),
							CacheUtil.getLlaveServicio(UrlServiceCache.SYSMANIRISST, "EVENTOS_CALENDARIO"));
		} catch (SystemException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		
		if(!(lista == null || lista.isEmpty())) {
			JsfUtil.ejecutarJavaScript(
					"setTimeout(function(){ verMenu('999', '" + "2545" +
							"', '" + "99933" + "'); },10000)");
		}
    	
        	
    }

    public void pintarHijos(Node padre, DefaultSubMenu menuAct,
        DefaultSubMenu padre2) {

        NodeList menus = padre.getChildNodes();

        for (int i = 0; i < menus.getLength(); i++) {
            Node menuActual = menus.item(i);
            if (menuActual.getNodeType() == Node.ELEMENT_NODE) {
                String nombre = ((Element) menuActual).getAttribute("N");

                // if (padre2 !=null) {
                nombre = WordUtils.capitalize(nombre.toLowerCase());
                // }

                String tipo = ((Element) menuActual).getTagName();

                if ("M".equals(tipo)) {
                    menuAct = new DefaultSubMenu(nombre);
                    pintarHijos(menuActual, menuAct, menuAct);
                    if (padre2 != null) {
                        menuAct.setStyleClass("subMenuBarra");
                        padre2.addElement(menuAct);
                        menuAct = padre2;
                    }
                    else {
                        menuAct.setStyleClass("menuRaizBarra");
                        modeloMenu.addElement(menuAct);
                    }
                }
                else {
                    String moduloAct = ((Element) menuActual).getAttribute("A");
                    String comandoAct = ((Element) menuActual)
                                    .getAttribute("CO");
                    String formularioAct = ((Element) menuActual)
                                    .getAttribute("P");
                    String opcionMActual = ((Element) menuActual)
                                    .getAttribute("id");

                    String filActual = ((Element) menuActual).getAttribute("F");

                    DefaultMenuItem item = new DefaultMenuItem(nombre);
                    String datosClick = "'" + moduloAct + "'," +
                        "'" + formularioAct + "'," +
                        "'" + opcionMActual + "'," +
                        "'" + comandoAct + "'," +
                        "'" + filActual + "'";

                    item.setOnclick("verMenu(" + datosClick + ")");

                    menuAct.addElement(item);
                }
            }
        }

    }

    /**
     * Este metodo permite guardar a nivel de base de datos, los datos
     * del usuario que se a logueado
     * 
     * @author jgomez
     * @throws SystemException
     * @serialData 13/06/2018
     */
    public void generarBitacora() {
        HttpServletRequest datoLocal = ((HttpServletRequest) FacesContext
                        .getCurrentInstance().getExternalContext()
                        .getRequest());

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("NIT", SysmanFunciones.validarVariableVacio(
                        SessionUtil.getUser().getCedula())
                            ? SessionUtil.getUser().getCodigo()
                            : SessionUtil.getUser().getCedula());
        parametros.put("USUARIO", SessionUtil.getUser().getCodigo());
        parametros.put("SUCURSAL", SysmanFunciones
                        .nvl(SessionUtil.getUser().getSucursal(), "001"));

        parametros.put("COMPANIA", SessionUtil.getCompania());
        parametros.put("REMOTE_ADDR",
                        SysmanFunciones.validarVariableVacio(
                                        datoLocal.getRemoteAddr()) ? ""
                                            : datoLocal.getRemoteAddr());
        parametros.put("HTTP_USER_AGENT", SysmanFunciones.validarVariableVacio(
                        datoLocal.getHeader("User-Agent")) ? ""
                            : datoLocal.getHeader("User-Agent"));
        parametros.put("SERVER_PROTOCOL", SysmanFunciones.validarVariableVacio(
                        datoLocal.getProtocol()) ? ""
                            : datoLocal.getProtocol());
        parametros.put("SERVER_NAME", SysmanFunciones.validarVariableVacio(
                        datoLocal.getServerName()) ? ""
                            : datoLocal.getServerName());
        parametros.put("SERVER_PORT", SysmanFunciones.validarVariableVacio(
                        Integer.toString(datoLocal.getServerPort())) ? ""
                            : datoLocal.getServerPort());

        Parameter parameter = new Parameter();
        parameter.setFields(parametros);

        UrlBean urlCreacion = UrlServiceUtil
                        .getUrlBeanById(SERVICIO_AUDITA_LOGUEO);
        RequestManager re = new RequestManager();
        try {
            re.save(urlCreacion.getUrl(),
                            urlCreacion.getMetodo(), parameter);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
        }

    }

    public void generarBitacoraMenu() {
        Map<String, String> params = FacesContext.getCurrentInstance()
                        .getExternalContext().getRequestParameterMap();
        String menuClic = params.get("menu");
        String menuClicRuta = params.get("ruta");

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("USUARIO", SessionUtil.getUser().getCodigo());
        parametros.put("NIT", SysmanFunciones.validarVariableVacio(
                        SessionUtil.getUser().getCedula())
                            ? SessionUtil.getUser().getCodigo()
                            : SessionUtil.getUser().getCedula());
        parametros.put("SUCURSAL", SysmanFunciones
                        .nvl(SessionUtil.getUser().getSucursal(), "001"));
        parametros.put("MENU", menuClic);
        parametros.put("RUTA", menuClicRuta);
        parametros.put("APLICACION",
                        SysmanFunciones.validarVariableVacio(
                                        SessionUtil.getModulo()) ? "-1"
                                            : SessionUtil.getModulo());
        parametros.put("COMPANIA", SessionUtil.getCompania());

        Parameter parameter = new Parameter();
        parameter.setFields(parametros);

        UrlBean urlCreacion = UrlServiceUtil
                        .getUrlBeanById(SERVICIO_AUDITA_MENU);
        RequestManager re = new RequestManager();
        try {
            re.save(urlCreacion.getUrl(),
                            urlCreacion.getMetodo(), parameter);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
        }
        
        

    }

    /**
     * Metodo llamado en cambiarUsuario, cambiarContrasena y
     * cambiarCompania
     * 
     * @throws SystemException
     */
    public void habilitarSegundaContrasena() {
        try {
            if (!SysmanFunciones.validarVariableVacio(usuario)
                && !SysmanFunciones.validarVariableVacio(compania)
                && !SysmanFunciones.validarVariableVacio(contrasenia)
                && manejaDobleContrasena()) {
                visibleSegundaContrasena = true;
            }
            else {
                visibleSegundaContrasena = false;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Consulta de parametros a la base de datos.
     * 
     * @param nombreParametro
     * @return valor configurado para el parametro, en caso de que no
     * estï¿½ configurado retorna la cadena NO.
     * @throws SystemException
     */
    private String consultarParametro(String nombreParametro)
                    throws SystemException {
        String string = null;
        string = ejbSysmanUtil.consultarParametro(compania, nombreParametro,
                        "-1", new Date(), true);
        return SysmanFunciones.nvlStr(string, "NO");
    }
    
	    private String consultarParametros(String nombreParametro)
	            throws SystemException {
	String string = null;
	string = ejbSysmanUtil.consultarParametro(compania, nombreParametro,
	                "-1", new Date(), false);
	return SysmanFunciones.nvlStr(string, "NO");
	}

    /**
     * Permite resolver la conecciï¿½n a directorio Activo o ldap
     * 
     * @author jgomez
     * 
     * @param compania
     * @param nombreUsuario
     * @param password
     * @return
     * @throws SysmanException
     * @throws NullPointerException
     * @throws SystemException
     * @throws MalformedURLException
     * @throws IOException
     */
    private boolean servicioAuteticacion(String compania, String nombreUsuario,
        String password)
                    throws SysmanException, NullPointerException,
                    SystemException, MalformedURLException, IOException {
        Map<String, Object> parametros = new TreeMap<>();
        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametros.put(GeneralParameterEnum.CODIGO.getName(), "99");
        Registro rs = new Registro();

        RequestManager requestManager = new RequestManager();
        try {
            rs = RegistroConverter.toRegistro(requestManager.get(
                            UrlServiceUtil.getUrlBeanById(
                                            SERVICIO_AUTENTICACION).getUrl(),
                            parametros));
        }
        catch (ClientWSO2Exception | NullPointerException e) {
            throw new SysmanException(
                            "El servicio no esta configurado en la base de datos");
        }
        if (rs == null) {
            throw new SysmanException("No existe el servicio");
        }
        else if (rs.getCampos().get("TIPO_DIRECTORIO").toString() == null
            || rs.getCampos().get("URL").toString() == null) {
            throw new SysmanException(
                            "El servicio no esta configurado correctamente");
        }

        urlConsulta = rs.getCampos().get("URL").toString();
        tipoDirectorio = rs.getCampos().get("TIPO_DIRECTORIO").toString();

        autenticacion = new APIAutenticacion();
        return autenticacion.autenticacion(nombreUsuario, password, urlConsulta,
                        tipoDirectorio);
    }
    
    public void startAlertSchedule() {
    	try {
        String tiempo = consultarParametro("TIEMPO PARA ALERTAS EN ANTICIPO");
        scheduler = Executors.newScheduledThreadPool(1);

        if (tiempo != null && tiempo.contains(":")) {
            String[] partesTiempo = tiempo.split(":");
            String horas = partesTiempo[0];
            String minutos = partesTiempo[1];

            try {
                int horasInt = Integer.parseInt(horas);
                int minutosInt = Integer.parseInt(minutos);

                // Convertir horas y minutos a milisegundos
                long period = TimeUnit.HOURS.toMinutes(horasInt) + minutosInt;

                // Convertir el perï¿½odo de minutos a milisegundos
                period = TimeUnit.MINUTES.toMillis(period);

                if (isTaskScheduled.compareAndSet(false, true)) {
                    // Define la tarea que deseas ejecutar cada 5 minutos
                    Runnable alertTask = new Runnable() {
                        @Override
                        public void run() {
                            sendAlert();
                        }
                    };

                    // Programa la tarea para que se ejecute cada 'period' milisegundos
                    scheduledFuture = scheduler.scheduleAtFixedRate(alertTask, 0, period, TimeUnit.MILLISECONDS);
                } 

            } catch (NumberFormatException e) {
                System.out.println("Error al convertir horas o minutos a enteros: " + e.getMessage());
            }
        } else {
            System.out.println("El parÃ¡metro 'TIEMPO PARA ALERTAS EN ANTICIPO' no es vÃ¡lido.");
        }
    	} catch (com.sysman.exception.SystemException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    }
    
    private void sendAlert() {
    	try {
    	if(consultarParametro("MANEJA ALERTAS EN ANTICIPO").equals("SI")) {

        RequestManager requestManager = new RequestManager();
        
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), 2024);

        
			List<Registro> aux = RegistroConverter.toListRegistro(
			                requestManager.getList(
			                                UrlServiceUtil.getInstance()
			                                                .getUrlServiceByUrlByEnumID(SERVICIO_ANTIPICIPOS)
			                                                .getUrl(),
			                                param));
			if(aux != null) {

				for (Registro aux1 : aux) {
					//validar la fecha de vencimiento 
					Date fechaVenc = SysmanFunciones.convertirAFecha(SysmanFunciones.toString(aux1.getCampos().get("FECHA_VCN_DOC")));

					if(fechaVenc.equals(new Date()) || SysmanFunciones.comparaFechas(fechaVenc, new Date()) ) {				
						if (SysmanFunciones.toString(aux1.getCampos().get("DIRECCIONEMAIL")).contains("@")) {
							String comprobante = SysmanFunciones.toString(aux1.getCampos().get("NUMERO"));
							String descripcion = SysmanFunciones.toString(aux1.getCampos().get("DESCRIPCION"));
							String fecha = SysmanFunciones.toString(aux1.getCampos().get("FECHA"));
							String fechaV = SysmanFunciones.toString(aux1.getCampos().get("FECHA_VCN_DOC"));
							String valor = SysmanFunciones.toString(aux1.getCampos().get("VLR_DOCUMENTO"));
							String tercero = SysmanFunciones.toString(aux1.getCampos().get("NOMBRE"));

							System.out.println("Alerta: Inicio de Notificaciones.");

							Map<String, Object> params = new TreeMap<>();
							params.put("nombreTercero", convertToCamelCase(tercero).trim());
							params.put("comprobante", comprobante);
							params.put("valor", valor);
							params.put("fecha", fecha);
							params.put("fechaVencimiento", fechaV);
							params.put("descripcion", descripcion);
							
							String strAsunto = "Vencimiento de Comprobante de Anticipo";
							String correoDescrip = consultarParametros("CUERPO DEL CORREO VENCIMIENTO ANTICIPOS");
							
							if(!correoDescrip.equals("NO")) {
								
							String correoDescripFinal =   SysmanFunciones.remplazarVariableCorreo( correoDescrip,
									params);
							
							String correo = SysmanFunciones.toString(aux1.getCampos().get("DIRECCIONEMAIL"));

							EmailPojo email = new EmailPojo();
							email.setFrom("CONTABILIDAD");
							email.setSubject(strAsunto);
							email.setBody(correoDescripFinal);
							email.setTo(correo);
							ApiRestClient client = new ApiRestClient();
							try {
								client.postClient(email,compania);
							}catch (IOException  e) {

								logger.error(e.getMessage(), e);
							}
							}else {
								
								System.out.println("Alerta: El valor del parametro CUERPO DEL CORREO CREACION ANTICIPOS se encuentra vacio.");
								
							}

						}else {
							System.out.println("Alerta: No esta configurado un correo electronico.");
						}
					}
				}

			}

            }else {
            	isTaskScheduled.set(true);
            	stopAlertSchedule();
            }
			
		} catch (SystemException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // Aquï¿½ puedes agregar el cï¿½digo para enviar una alerta
    }
    
    public void stopAlertSchedule() {
        if (isTaskScheduled.compareAndSet(true, false)) {
        	boolean cancelSuccess = scheduledFuture.cancel(false); // o true si quieres interrumpir la tarea si estï¿½ en ejecuciï¿½n
            if (cancelSuccess) {
                System.out.println("Tarea cancelada con Ã©xito.");
            } else {
                System.out.println("No se pudo cancelar la tarea.");
            }

            // Detener el scheduler
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
            }

            System.out.println("Scheduler detenido.");
        }
    }
    
    
    public static String convertToCamelCase(String input) {
        // Dividimos la cadena por espacios y/o guiones
        String[] words = input.split("[\\s-]+");

        // Construimos el resultado en camelCase
        StringBuilder camelCaseString = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            // Capitalizamos la primera letra de cada palabra
            camelCaseString.append(Character.toUpperCase(word.charAt(0)));
            if (word.length() > 1) {
            	
                camelCaseString.append(word.substring(1).toLowerCase());
                camelCaseString.append(" ");
            }
            
        }

        return camelCaseString.toString();
    }
    
    public String obtenerUrlSSO(String urlBase) throws IOException, SysmanException {

    	String endpoint = urlBase;
    	Gson gson = new Gson();

    	String usuario = SessionUtil.getUser().getCodigo();

    	try {

    		String apikey = SysmanFunciones.nvlStr(
    				ejbSysmanUtil.consultarParametro(
    						compania, "KEY LOGIN APP EXTERNA", "-1", new Date(), false),"");

    		RequestManager requestManager = new RequestManager();

    		Map<String, Object> param = new TreeMap<>();
    		param.put(GeneralParameterEnum.CODIGO.getName(), usuario);

    		List<Registro> lista = RegistroConverter.toListRegistro(
    				requestManager.getList(
    						UrlServiceUtil.getInstance()
    						.getUrlServiceByUrlByEnumID(SERVICIO_USUARIO)
    						.getUrl(),param));

    		if (lista == null || lista.isEmpty()) {
    			throw new SysmanException("No se encontró información del usuario");
    		}

    		Registro reg = lista.get(0);

    		String email = SysmanFunciones.nvl(reg.getCampos().get("CORREOELECTRONICO"), "").toString();
    		String documento = SysmanFunciones.nvl(reg.getCampos().get("CEDULA"), "").toString();

    		if (email.isEmpty() || documento.isEmpty()) {
    			throw new SysmanException("El usuario no tiene email o documento configurado");
    		}

    		JsonObject jsonBody = new JsonObject();
    		jsonBody.addProperty("apikey", apikey);
    		jsonBody.addProperty("email", email);
    		jsonBody.addProperty("document", documento);

    		String json = gson.toJson(jsonBody);

    		HttpURLConnection connection = (HttpURLConnection) new URL(endpoint).openConnection();
    		connection.setRequestMethod("POST");
    		connection.setDoOutput(true);
    		connection.setConnectTimeout(15000);
    		connection.setReadTimeout(15000);

    		connection.setRequestProperty("Content-Type", "application/json");
    		connection.setRequestProperty("Accept", "application/json");

    		try (OutputStream os = connection.getOutputStream()) {
    			os.write(json.getBytes(StandardCharsets.UTF_8));
    		}

    		int responseCode = connection.getResponseCode();

    		String respuestaStr;
    		try (BufferedReader br = new BufferedReader(
    				new InputStreamReader(
    						(responseCode >= 200 && responseCode < 300)
    						? connection.getInputStream()
    								: connection.getErrorStream(),
    								StandardCharsets.UTF_8))) {

    			respuestaStr = br.lines().collect(java.util.stream.Collectors.joining());
    		}

    		if (respuestaStr.isEmpty()) {
    			throw new SysmanException("Respuesta vacía del servicio SSO");
    		}

    		JsonObject respuestaJson = gson.fromJson(respuestaStr, JsonObject.class);

    		switch (responseCode) {

    		case 201:
    			return respuestaJson.get("redirectUrl").getAsString();

    		case 401:
    			throw new SysmanException("Credenciales inválidas: "
    					+ respuestaJson.get("message").getAsString());

    		case 403:
    			throw new SysmanException("API Key inválida: "
    					+ respuestaJson.get("message").getAsString());

    		default:
    			throw new SysmanException("Error en servicio SSO. Código: "
    					+ responseCode + " Respuesta: " + respuestaStr);
    		}

    	} catch (SystemException e) {
    		throw new SysmanException(e);
    	}
    }
    

    /**
     * Metodo que se ejecuta al cambiar el usuario
     */
    public void cambiarUsuario() {
        habilitarSegundaContrasena();
        cargarCompaniasPorUsuario();
    }

    public void cambiarContrasena() {
        habilitarSegundaContrasena();
    }

    /**
     * Metodo que se ejecuta al cambiar la compania
     */
    public void cambiarCompania() {
        habilitarSegundaContrasena();
        this.aceptaTerminos = false;
        try {
			if(consultarParametro("HABILITA TRATAMIENTO DATOS EN INICIO DE SESION").equals("SI")) {				

	            Registro companiaSeleccionada = null;
	            
	            for (Registro reg : this.companias) {
	                if (reg.getCampos().get("CODIGO").toString().equals(this.compania)) {
	                    companiaSeleccionada = reg;
	                    break;
	                }
	            }

	            if (companiaSeleccionada != null) {
	                if (companiaSeleccionada.getCampos().containsKey("URL_TRATAMIENTO")) {
	                    this.urlTratamiento = SysmanFunciones.nvlStr(companiaSeleccionada.getCampos().get("URL_TRATAMIENTO").toString(),"");
	                } else {
	                    this.urlTratamiento = "";
	                }
	                
	                if(!this.urlTratamiento.equalsIgnoreCase("")) {
		            	this.visibleTerminos = true;
	    				this.disBtnInicio = true;
	    				this.nombreCompania = companiaSeleccionada.getCampos().get("NOMBRE").toString();
		            }
	            }
 		
				
			}else {
				this.visibleTerminos = false;
				this.disBtnInicio = false;
			}
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void cambiarAceptaTerminos() {
    	
    	this.disBtnInicio = !this.aceptaTerminos;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getCompania() {
        return compania;
    }

    public void setCompania(String compania) {
        this.compania = compania;
    }

    public List<Registro> getCompanias() {
        return companias;
    }

    public void setCompanias(List<Registro> companias) {
        this.companias = companias;
    }

    public FormContinuoService getService() {
        return service;
    }

    public void setService(FormContinuoService service) {
        this.service = service;
    }

    public Usuario getUser() {
        return user;
    }

    public void setUser(Usuario user) {
        this.user = user;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
        SessionUtil.setSessionVar("menu", menu);
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
        SessionUtil.setSessionVar("modulo", modulo);
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public Compania getCompaniaIngreso() {
        return companiaIngreso;
    }

    public void setCompaniaIngreso(Compania companiaIngreso) {
        this.companiaIngreso = companiaIngreso;
    }

    public String getComando() {
        return comando;
    }

    public void setComando(String comando) {
        this.comando = comando;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public String getMenuActual() {
        return menuActual;
    }

    public void setMenuActual(String menuActual) {
        this.menuActual = menuActual;
        SessionUtil.setSessionVar("menuActual", menuActual);
    }

    public String getRutaIframe() {
        return rutaIframe;
    }

    public void setRutaIframe(String rutaIframe) {
        this.rutaIframe = rutaIframe;
    }

    public MenuModel getModeloMenu() {
        return modeloMenu;
    }

    public void setModeloMenu(MenuModel modeloMenu) {
        this.modeloMenu = modeloMenu;
    }

    public String getImagenCompania() {
        return imagenCompania;
    }

    public void setImagenCompania(String imagenCompania) {
        this.imagenCompania = imagenCompania;
    }

    /**
     * Retorna el objeto visibleSegundaContrasena
     * 
     * @return visibleSegundaContrasena
     */
    public boolean isVisibleSegundaContrasena() {
        return visibleSegundaContrasena;
    }

    /**
     * Asigna el objeto visibleSegundaContrasena
     * 
     * @param visibleSegundaContrasena
     * Variable a asignar en visibleSegundaContrasena
     */
    public void setVisibleSegundaContrasena(boolean visibleSegundaContrasena) {
        this.visibleSegundaContrasena = visibleSegundaContrasena;
    }

    /**
     * Retorna el objeto contraseniaDos
     * 
     * @return contraseniaDos
     */
    public String getContraseniaDos() {
        return contraseniaDos;
    }

    /**
     * Asigna el objeto contraseniaDos
     * 
     * @param contraseniaDos
     * Variable a asignar en contraseniaDos
     */
    public void setContraseniaDos(String contraseniaDos) {
        this.contraseniaDos = contraseniaDos;
    }

    public boolean isContrasenaValida() {
        return contrasenaValida;
    }

    public void setContrasenaValida(boolean contrasenaValida) {
        this.contrasenaValida = contrasenaValida;
    }

	public String getTituloSeccionLogin() {
		return tituloSeccionLogin;
	}

	public void setTituloSeccionLogin(String tituloSeccionLogin) {
		this.tituloSeccionLogin = tituloSeccionLogin;
	}
	
	public boolean isVisibleTerminos() { 
		return visibleTerminos; 
	}
	public void setVisibleTerminos(boolean visibleTerminos) { 
		this.visibleTerminos = visibleTerminos; 
	}
	
	public boolean isAceptaTerminos() { 
		return aceptaTerminos; 
	}
	public void setAceptaTerminos(boolean aceptaTerminos) { 
		this.aceptaTerminos = aceptaTerminos; 
	}
	
	public boolean isDisBtnInicio() { 
		return disBtnInicio; 
	}
	public void setDisBtnInicio(boolean disBtnInicio) { 
		this.disBtnInicio = disBtnInicio; 
	}
	
	public String getNombreCompania() {
	    return nombreCompania;
	}

	public void setNombreCompania(String nombreCompania) {
	    this.nombreCompania = nombreCompania;
	}
	
	public String getUrlTratamiento() {
	    return urlTratamiento;
	}

	public void setUrlTratamiento(String urlTratamiento) {
	    this.urlTratamiento = urlTratamiento;
	}

	
}
