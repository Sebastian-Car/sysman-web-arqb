/*
 * To change this license header, choose License Headers in Project
 * Properties. To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sysman.controladores;

import com.sysman.componentes.Direccionador;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.ParametrosConstantes;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.connectors.RequestManager;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.logica.Compania;
import com.sysman.logica.Formulario;
import com.sysman.logica.Grupo;
import com.sysman.logica.Usuario;
import com.sysman.reportes.Reporteador;
import com.sysman.session.SessionContainerRemoteSt;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
import com.sysman.session.utl.ConstantesHojasDeVidaEnum;
import com.sysman.session.utl.ConstantesWorkflowEnum;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.primefaces.context.RequestContext;

/**
 *
 * @author cmanrique
 */
public class SessionUtil {

    public static final String CADENA_CONEXION = "java:global/sysmanAplicaciones/sysmanSession-ejb/SessionContainerSt!com.sysman.session.SessionContainerSt";
    /**
     * 187004 getFormulariosPorCodigoQuery
     */
    private static final String URL_GET_FORMULARIO_POR_CODIGO = "187004";

    private SessionUtil() {

    }

    public static Cookie getCookie(String name) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) facesContext
                        .getExternalContext().getRequest();
        Cookie cookie;
        Cookie[] userCookies = request.getCookies();
        if ((userCookies != null) && (userCookies.length > 0)) {
            for (int i = 0; i < userCookies.length; i++) {
                if (userCookies[i].getName().equals(name)) {
                    cookie = userCookies[i];
                    return cookie;
                }
            }
        }
        return null;
    }

    public static void eliminarSession() throws NamingException {
        Context ctx = new InitialContext();
        SessionContainerRemoteSt sr = (SessionContainerRemoteSt) ctx
                        .lookup(CADENA_CONEXION);
        sr.removeSession(getCookie("sysmanCookieGeneral").getValue());
        getCookie("sysmanCookieGeneral").setMaxAge(0);
        getSession().invalidate();
        redireccionarIdentificacion();
    }

    public static void cargarSessionPrincipal()
                    throws SysmanException {

        SessionContainerRemoteSt sr;
        try {
            Context ctx = new InitialContext();
            sr = (SessionContainerRemoteSt) ctx
                            .lookup(CADENA_CONEXION);
        }
        catch (NamingException e) {
            throw new SysmanException(e, e.getMessage());
        }
        HashMap<String, Object> variables = sr.getSession(
                        getCookie("sysmanCookieGeneral").getValue());
        Object[] datosCarga;
        if (variables == null) {
            getSession().invalidate();
            throw new SysmanException("No hay sesión");
        }
        if ((getSessionVar("sessionID") != null) && !getSessionVar("sessionID")
                        .equals(getCookie("sysmanCookieGeneral").getValue())) {
            getSession().invalidate();
        }

        if (getUser() == null) {
            setSessionVar("sessionID",
                            getCookie("sysmanCookieGeneral").getValue());
            setSessionVar("usuario", variables.get("usuario"));
            setSessionVar("compania", variables.get("compania"));
            setSessionVar("companiaIngreso", variables.get("companiaIngreso"));
            setSessionVar("modulo", variables.get("modulo"));
            setSessionVar("menu", variables.get("menu"));
            setSessionVar("menuActual", variables.get("menuActual"));
            setSessionVar("excelPlano", variables.get("excelPlano"));
        }
        else {
            datosCarga = sr.getDataLoad(
                            getCookie("sysmanCookieGeneral").getValue());
            setSessionVar("modulo", datosCarga[0]);
            setSessionVar("menu", datosCarga[1]);
            setSessionVar("menuActual", datosCarga[2]);
        }
        if ("6".equals(variables.get("modulo"))) {
            datosCarga = sr.getDataNomina(
                            getCookie("sysmanCookieGeneral").getValue());
            setSessionVar("procesoNomina", datosCarga[0]);
            setSessionVar("anioNomina", datosCarga[1]);
            setSessionVar("mesNomina", datosCarga[2]);
            setSessionVar("periodoNomina", datosCarga[3]);
            setSessionVar("nombreMesNomina", datosCarga[4]);
            setSessionVar("nombrePeriodoNomina", datosCarga[5]);
            setSessionVar("nombreProcesoNomina", datosCarga[6]);
            setSessionVar("periodoActivo", datosCarga[7]);
        }

        // Modulo de Facturacion General
        if (Integer.toString(SysmanConstantes.MODULO_FACTURACION_GENERAL)
                        .equals(variables.get("modulo"))) {
            datosCarga = sr.getDataFacturacionGeneral(
                            getCookie("sysmanCookieGeneral").getValue());

            setSessionVar(ConstantesFacturacionGenEnum.ANIO.getValue(),
                            datosCarga[0]);

            setSessionVar(ConstantesFacturacionGenEnum.TIPOCOBRO.getValue(),
                            datosCarga[1]);

            setSessionVar(ConstantesFacturacionGenEnum.NOMBRETIPOCOBRO
                            .getValue(), datosCarga[2]);

            setSessionVar(ConstantesFacturacionGenEnum.MANEJA_INVENTARIO
                            .getValue(), datosCarga[3]);

            setSessionVar(ConstantesFacturacionGenEnum.INTERFAZ_RECAUDO
                            .getValue(), datosCarga[4]);

            setSessionVar(ConstantesFacturacionGenEnum.TIPOCOBRO_NOFACTURADO
                            .getValue(), datosCarga[5]);

            setSessionVar(ConstantesFacturacionGenEnum.CPTE_RECAUDO
                            .getValue(), datosCarga[6]);

            setSessionVar(ConstantesFacturacionGenEnum.MANEJA_RECAUDOTERCEROS
                            .getValue(), datosCarga[7]);

            setSessionVar(ConstantesFacturacionGenEnum.CLASE_CUENTASRECAUDO
                            .getValue(), datosCarga[8]);

            setSessionVar(ConstantesFacturacionGenEnum.INDPRELIQUIDACION
                            .getValue(), datosCarga[9]);
        }

        // Modulo de Workflow (35)
        if (Integer.toString(SysmanConstantes.MODULO_WORKFLOW)
                        .equals(variables.get("modulo"))) {
            datosCarga = sr.getDataTramite(
                            getCookie("sysmanCookieGeneral").getValue());

            setSessionVar(ConstantesWorkflowEnum.PR_RID_TRAMITE.getValue(),
                            datosCarga[0]);
        }

        // Modulo hojas de vida
        if (Integer.toString(SysmanConstantes.MODULO_HOJAS_DE_VIDA)
                        .equals(variables.get("modulo"))) {
            datosCarga = sr.getDataClaseEvaluacion(
                            getCookie("sysmanCookieGeneral").getValue());
            setSessionVar(ConstantesHojasDeVidaEnum.CLASE_EVALUACION.getValue(),
                            datosCarga[0]);
        }

        if (Integer.toString(SysmanConstantes.MODULO_GENERAL)
                        .equals(variables.get("modulo"))) {
            datosCarga = sr.getDataGeneradorReportes(
                            getCookie("sysmanCookieGeneral").getValue());
            setSessionVar("aplicacion",
                            datosCarga[0]);
        }
        if (Integer.toString(SysmanConstantes.MODULO_CONTROL_Y_REGISTRO)
                .equals(variables.get("modulo"))) {
        	datosCarga = sr.getDataGeneradorReportes(
                    getCookie("sysmanCookieGeneral").getValue());
        	setSessionVar("aplicacion",
                    datosCarga[0]);
        }
        
        /*
         * if (Integer.toString(SysmanConstantes.MODULO_PLUSVALIA)
         * .equals(variables.get("modulo"))) { datosCarga =
         * sr.getDataPlusvalia(
         * getCookie("sysmanCookieGeneral").getValue());
         * setSessionVar("claseVp", datosCarga[0]);
         * setSessionVar("nombreClase", datosCarga[1]);
         * 
         * }
         */
    }

    public static void iniciarSessionPrincipal() {
        if (getSessionVar("usuario") == null) {
            try {
                cargarSessionPrincipal();
            }
            catch (SysmanException e) {
                Logger.getLogger(SessionUtil.class.getName()).log(Level.SEVERE,
                                null, e);
            }
        }
    }

    public static Map<String, Object> getSessionRemote() {
        Context ctx;
        try {
            ctx = new InitialContext();
            SessionContainerRemoteSt sr = (SessionContainerRemoteSt) ctx
                            .lookup(CADENA_CONEXION);
            return sr.getSession(
                            getCookie("sysmanCookieGeneral").getValue());
        }
        catch (NamingException e) {
            Logger.getLogger(SessionUtil.class.getName()).log(Level.SEVERE,
                            null, e);
            return null;
        }

    }

    public static void setSessionVarContainer(String nombre, Object valor)
                    throws NamingException {

        Context ctx = new InitialContext();
        SessionContainerRemoteSt sr = (SessionContainerRemoteSt) ctx
                        .lookup(CADENA_CONEXION);
        sr.setSessionVar(getCookie("sysmanCookieGeneral").getValue(), nombre,
                        valor);

    }

    public static void agregarMensajeErrorMenu(String mensaje) {
        try {
            setSessionVarContainer(SysmanConstantes.LLAVE_MENSAJE_ERROR,
                            mensaje);
        }
        catch (NamingException e) {
            Logger.getLogger(SessionUtil.class.getName()).log(Level.SEVERE,
                            null, e);
        }
    }

    /**
     * Permite redireccionar al menu principal e inmediatamente abrir
     * un formulario.
     * 
     * Ejemplo de uso: se abre un modal y se redirecciona a un
     * formulario continuo luego al cerrar el continuo se requiere
     * volver al modal.
     * 
     * @param menu
     * - código de la opcion de menu que se debe abrir. Debe estar
     * visible dicha opción.
     */
    public static void redireccionarMenuFormulario(String menu) {
        try {
            SessionUtil.setSessionVarContainer(
                            SysmanConstantes.LLAVE_FORMULARIO_ABRIR,
                            "menu" + menu);
            redireccionarMenu();
        }
        catch (NamingException e) {
            Logger.getLogger(SessionUtil.class.getName()).log(Level.SEVERE,
                            null, e);
        }
    }

    public static void redireccionarMenuFormulario(String menu,
        boolean esModal) {
        try {
            SessionUtil.setSessionVarContainer(
                            SysmanConstantes.LLAVE_FORMULARIO_ABRIR,
                            "menu" + menu);
            if (esModal) {
                RequestContext.getCurrentInstance().closeDialog(null);
            }
            else {
                redireccionarMenu();
            }

        }
        catch (NamingException e) {
            Logger.getLogger(SessionUtil.class.getName()).log(Level.SEVERE,
                            null, e);
        }
    }

    /**
     * Permite redireccionar al menu principal e inmediatamente abrir
     * un formulario destino.
     * 
     * @param menuIn
     * -> Codigo del menu asociado al formulario destino.
     * @param formIn
     * -> Codigo del formulario destino.
     * @param moduloIn
     * -> Codigo del modulo en el que se encuentra el formulario
     * destino.
     * @param menuOut
     * ->Codigo del menu asociado al formulario origen.
     * @param formOut
     * -> Codigo del formulario origen.
     * @param moduloOut
     * -> Codigo del modulo en el que se encuentra el formulario
     * origen.
     * @param esModal
     * -> Indicador que establece si el formulario es modal.
     * <li>true: Es modal.
     */
    public static void redireccionarFormularioRetorno(String menuIn,
        String formIn,
        String moduloIn, String menuOut, String formOut,
        String moduloOut,
        boolean esModal) {
        try {
            SessionUtil.setSessionVarContainer(
                            SysmanConstantes.LLAVE_FORMULARIO_RETORNO_IN,
                            formIn + "," + menuIn + "," + moduloIn);

            SessionUtil.setSessionVarContainer(
                            SysmanConstantes.LLAVE_FORMULARIO_RETORNO_OUT,
                            formOut + "," + menuOut + "," + moduloOut);
            if (esModal) {
                RequestContext.getCurrentInstance().closeDialog(null);
            }
            else {
                redireccionarMenu();
            }

        }
        catch (NamingException e) {
            Logger.getLogger(SessionUtil.class.getName()).log(Level.SEVERE,
                            null, e);
        }
    }

    public static Object getSessionVarContainer(String nombre)
                    throws NamingException {

        Context ctx = new InitialContext();
        SessionContainerRemoteSt sr = (SessionContainerRemoteSt) ctx
                        .lookup(CADENA_CONEXION);
        return sr.getSessionVar(getCookie("sysmanCookieGeneral").getValue(),
                        nombre);

    }

    public static void setApplicationVarContainer(String nombre, Object valor)
                    throws NamingException {

        Context ctx = new InitialContext();
        SessionContainerRemoteSt sr = (SessionContainerRemoteSt) ctx
                        .lookup(CADENA_CONEXION);
        sr.setApplicationVar(nombre, valor);

    }

    public static Object getApplicationVarContainer(String nombre)
                    throws NamingException {

        Context ctx = new InitialContext();
        SessionContainerRemoteSt sr = (SessionContainerRemoteSt) ctx
                        .lookup(CADENA_CONEXION);
        return sr.getApplicationVar(nombre);

    }

    public static void removeSessionVarContainer(String nombre)
                    throws NamingException {

        Context ctx = new InitialContext();
        SessionContainerRemoteSt sr = (SessionContainerRemoteSt) ctx
                        .lookup(CADENA_CONEXION);
        sr.removeSessionVar(getCookie("sysmanCookieGeneral").getValue(),
                        nombre);

    }

    public static HttpSession getSession() {
        return getRequest().getSession(true);
    }

    public static HttpServletRequest getRequest() {
        return (HttpServletRequest) FacesContext.getCurrentInstance()
                        .getExternalContext().getRequest();
    }

    public static String getCompania() {
        iniciarSessionPrincipal();
        return (String) getSessionVar("compania");
    }

    public static Compania getCompaniaIngreso() {
        iniciarSessionPrincipal();
        return (Compania) getSessionVar("companiaIngreso");
    }

    public static String getTipoEntidad() {
        Compania compania = getCompaniaIngreso();
        return compania.getCodigosChip() == null
            ? Integer.toString(compania.getTipoEntidad())
            : compania.getCodigosChip();
    }

    public static Usuario getUser() {
        return (Usuario) getSessionVar("usuario");
    }

    public static int getNivelUsuario(int modulo) {
        int rta = -1;
        iniciarSessionPrincipal();
        try {
            rta = getUser().getGrupos().get(Integer.toString(modulo))
                            .getNivelUsuario();
        }
        catch (NullPointerException e) {
            Logger.getLogger(SessionUtil.class.getName()).log(Level.SEVERE,
                            null, e);
        }
        return rta;
    }

    public static int getNivelGrupo(int modulo) {
        int rta = -1;
        iniciarSessionPrincipal();
        try {
            rta = getUser().getGrupos().get(Integer.toString(modulo))
                            .getNivelGrupo();
        }
        catch (NullPointerException e) {
            Logger.getLogger(SessionUtil.class.getName()).log(Level.SEVERE,
                            null, e);
        }
        return rta;
    }

    public static int getNivelUsuario(String modulo) {
        int rta = -1;
        iniciarSessionPrincipal();
        try {
            rta = getUser().getGrupos().get(modulo).getNivelUsuario();
        }
        catch (NullPointerException e) {
            Logger.getLogger(SessionUtil.class.getName()).log(Level.SEVERE,
                            null, e);
        }
        return rta;
    }

    public static int getNivelGrupo(String modulo) {
        iniciarSessionPrincipal();
        int rta = -1;
        try {
            rta = getUser().getGrupos().get(modulo).getNivelGrupo();
        }
        catch (NullPointerException e) {
            Logger.getLogger(SessionUtil.class.getName()).log(Level.SEVERE,
                            null, e);
        }
        return rta;
    }

    public static Grupo getGrupo(int modulo) {
        iniciarSessionPrincipal();
        return getUser().getGrupos().get(Integer.toString(modulo));
    }

    public static Grupo getGrupo(String modulo) {
        iniciarSessionPrincipal();
        return getUser().getGrupos().get(modulo);
    }

    public static String getRuta(String modulo) {
        iniciarSessionPrincipal();
        if ("-1".equals(modulo)) {
            return getUser().getAplicacionGeneral().getRutaArchivos();
        }
        else {
            return getGrupo(modulo).getAplicacionGrupo().getRutaArchivos();
        }
    }

    public static String getRuta(int modulo) {
        return getRuta(Integer.toString(modulo));
    }

    public static Formulario cargarFormulario(String id) {
        Formulario form = null;
        Usuario user = getUser();
        if (user != null) {
            form = user.getPermisos().get(id);
        }
        else {
            redireccionarMenu();
        }
        return form;
    }

    public static String getRutaDocumentos(String modulo) {
        iniciarSessionPrincipal();
        if ("-1".equals(modulo)) {
            return getUser().getAplicacionGeneral().getRutaDocumentos();
        }
        else {
            return getGrupo(modulo).getAplicacionGrupo().getRutaDocumentos();
        }
    }

    public static void redireccionarMenuPermisos() {
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext ec = context.getExternalContext();
        try {
            setSessionVarContainer("permisos", false);
            ec.redirect(SysmanConstantes.RETORNO_MENU);
        }
        catch (IOException | NamingException ex) {
            Logger.getLogger(SessionUtil.class.getName()).log(Level.SEVERE,
                            null, ex);
        }

    }

    public static void redireccionarMenu() {
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext ec = context.getExternalContext();
        try {
            ec.redirect(SysmanConstantes.RETORNO_MENU);
        }
        catch (IOException ex) {
            Logger.getLogger(SessionUtil.class.getName()).log(Level.SEVERE,
                            null, ex);
        }

    }

    public static void redireccionarIdentificacion() {
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext ec = context.getExternalContext();
        try {
            ec.redirect(SysmanConstantes.RETORNO_IDENTIFICACION);
        }
        catch (IOException ex) {
            Logger.getLogger(SessionUtil.class.getName()).log(Level.SEVERE,
                            null, ex);
        }

    }

    public static Object getSessionVar(String nombre) {
        return getSession().getAttribute(nombre);

    }

    public static void setSessionVar(String nombre, Object valor) {
        getSession().setAttribute(nombre, valor);
    }

    public static void removeSessionVar(String nombre) {
        getSession().removeAttribute(nombre);
    }

    public static void redireccionar(String ruta) {
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext ec = context.getExternalContext();
        try {
            ec.redirect(ec.getRequestContextPath() + ruta);
        }
        catch (IOException ex) {
            Logger.getLogger(SessionUtil.class.getName()).log(Level.SEVERE,
                            null, ex);
        }

    }

    public static void redireccionar(Direccionador direccionador) {
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext ec = context.getExternalContext();
        try {
            setFlash(direccionador.getParametros());
            ec.redirect(ec.getRequestContextPath() + direccionador.getRuta());
        }
        catch (IOException ex) {
            Logger.getLogger(SessionUtil.class.getName()).log(Level.SEVERE,
                            null, ex);
        }
    }

    public static void redireccionarLocal(Direccionador direccionador) {
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext ec = context.getExternalContext();
        try {
            setFlashLocal(direccionador.getParametros());
            ec.redirect(ec.getRequestContextPath() + direccionador.getRuta());
        }
        catch (IOException ex) {
            Logger.getLogger(SessionUtil.class.getName()).log(Level.SEVERE,
                            null, ex);
        }
    }

    public static void redireccionarForma(Direccionador direccionador,
        String modulo) {
        Formulario forma = SessionUtil.cargarFormulario(
                        direccionador.getNumForm() + "," + modulo);
        if (forma == null) {
            JsfUtil.agregarMensajeError(
                            "Operación interrumpida, No tiene permisos para acceder a este recurso");
            return;
        }
        boolean[] permisos = forma.getPermisos();
        if ((permisos == null) || !permisos[3]) {
            FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_FATAL,
                                            "Operación interrumpida",
                                            "No tiene permisos para acceder a este recurso"));
        }
        else {
            if (forma.isModal()) {
                Map<String, Object> options = new HashMap<>();
                options.put("modal", true);
                options.put("draggable", false);
                options.put("resizable", false);
                options.put("closable", true);
                options.put("contentWidth", forma.getAnchoModal());
                options.put("contentHeight", forma.getAltoModal());
                setFlash(direccionador.getParametros());
                RequestContext.getCurrentInstance().openDialog(forma.getRuta(),
                                options, null);
            }
            else {
                try {
                    ExternalContext ec = FacesContext.getCurrentInstance()
                                    .getExternalContext();
                    setFlash(direccionador.getParametros());
                    ec.redirect(forma.getRuta());
                }
                catch (IOException ex) {
                    Logger.getLogger(SessionUtil.class.getName())
                                    .log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public static void redireccionar(Direccionador direccionador,
        String nombre) {
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext ec = context.getExternalContext();
        try {
            setFlash(direccionador.getParametros());
            ec.redirect(nombre + direccionador.getRuta());
        }
        catch (IOException ex) {
            Logger.getLogger(SessionUtil.class.getName()).log(Level.SEVERE,
                            null, ex);
        }
    }

    public static void redireccionarDeModalAModal(Direccionador direccionador,
        String modulo) {
        Formulario forma = SessionUtil.cargarFormulario(
                        direccionador.getNumForm() + "," + modulo);
        if (forma == null) {
            JsfUtil.agregarMensajeError(
                            "Operación interrumpida, No tiene permisos para acceder a este recurso");
            return;
        }
        boolean[] permisos = forma.getPermisos();
        if ((permisos == null) || !permisos[3]) {
            FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_FATAL,
                                            "Operación interrumpida",
                                            "No tiene permisos para acceder a este recurso"));
        }
        try {
            ExternalContext ec = FacesContext.getCurrentInstance()
                            .getExternalContext();
            setFlash(direccionador.getParametros());
            ec.redirect(forma.getRuta());
        }
        catch (IOException ex) {
            Logger.getLogger(SessionUtil.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    public static void redireccionar(String ruta, String[] campos,
        String[] valores) {
        try {

            setFlash(armarRutaMapa(campos, valores));
            FacesContext context = FacesContext.getCurrentInstance();
            ExternalContext ec = context.getExternalContext();
            ec.redirect(ec.getRequestContextPath() + ruta);
        }
        catch (IOException ex) {
            Logger.getLogger(SessionUtil.class.getName()).log(Level.SEVERE,
                            null, ex);
        }
    }

    public static void redireccionar(String ruta, String[] campos,
        Object[] valores) {
        try {

            setFlash(armarRutaMapa(campos, valores));
            FacesContext context = FacesContext.getCurrentInstance();
            ExternalContext ec = context.getExternalContext();
            ec.redirect(ec.getRequestContextPath() + ruta);
        }
        catch (IOException ex) {
            Logger.getLogger(SessionUtil.class.getName()).log(Level.SEVERE,
                            null, ex);
        }
    }

    public static void redireccionarPorFormulario(String modulo,
        String numFormulario, String[] campos,
        Object[] valores, boolean externo) {
        try {

            setFlash(armarRutaMapa(campos, valores));
            FacesContext context = FacesContext.getCurrentInstance();
            ExternalContext ec = context.getExternalContext();
            ec.redirect(ec.getRequestContextPath()
                + (externo ? "/" : "")
                + SessionUtil.getUser().getPermisos()
                                .get(numFormulario + "," + modulo).getRuta());
        }
        catch (IOException ex) {
            Logger.getLogger(SessionUtil.class.getName()).log(Level.SEVERE,
                            null, ex);
        }
    }
    
    /**
     * Solo aplica para los formularios de datos que invocan un formulario modal
     * y se requiere invocar  formulario de datos adicional.
     *
     * @param modulo
     * @param numFormulario
     * @param campos
     * @param valores
     * * @param externo
     */
    public static void redireccionarFormularioModalFormulario(String modulo,
    		String numFormulario, String[] campos,
    		Object[] valores, boolean externo) {
    	try {
    		setFlash(armarRutaMapa(campos, valores));

    		FacesContext context = FacesContext.getCurrentInstance();
    		ExternalContext externalContext = context.getExternalContext();

    		String url = externalContext.getRequestContextPath()
    				+ (externo ? "/" : "")
    				+ SessionUtil.getUser()
    				.getPermisos()
    				.get(numFormulario + "," + modulo)
    				.getRuta();

    		String script = "window.top.location.href='" + url + "';";
    		JsfUtil.ejecutarJavaScript(script);

    	} catch (Exception ex) {
    		Logger.getLogger(SessionUtil.class.getName()).log(Level.SEVERE, null, ex);
    	}
    }

    /**
     * Solo aplica para los formularios que tienen formulario modal
     * adicional y se requiere invocar el formulario modal adicional
     * en esos casos al nombre del formulario se le agrega la palabra
     * modal al final del nombre
     *
     * @param form
     * @param modulo
     * @param campos
     * @param valores
     */
    public static void cargarModalCerrarModal(String form, String modulo) {

        Formulario forma = SessionUtil.cargarFormulario(form + "," + modulo);
        if (forma == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_FATAL,
                                            "Operación interrumpida",
                                            "No tiene permisos para acceder a este recurso"));
            return;
        }
        boolean[] permisos = forma.getPermisos();
        if ((permisos == null) || !permisos[3]) {
            FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_FATAL,
                                            "Operación interrumpida",
                                            "No tiene permisos para acceder a este recurso"));
        }
        else {
            Map<String, Object> options = new HashMap<>();
            options.put("modal", true);
            options.put("draggable", false);
            options.put("resizable", false);
            options.put("closable", true);
            options.put("contentWidth", forma.getAnchoModal());
            options.put("contentHeight", forma.getAltoModal());
            String ruta = forma.getRuta();

            ruta = ruta.contains("/") ? ruta.substring(ruta.lastIndexOf('/'))
                : ruta;
            ruta = ruta.substring(0, ruta.lastIndexOf('.')) + "Modal.sysman";
            RequestContext.getCurrentInstance().openDialog(ruta, options, null);
        }
    }

    /**
     * Solo aplica para los formularios que tienen formulario modal
     * adicional y se requiere invocar el formulario modal adicional
     * en esos casos al nombre del formulario se le agrega la palabra
     * modal al final del nombre
     *
     * @param form
     * @param modulo
     * @param campos
     * @param valores
     */
    public static void cargarModal(String form, String modulo) {

        Formulario forma = SessionUtil.cargarFormulario(form + "," + modulo);
        if (forma == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_FATAL,
                                            "Operación interrumpida",
                                            "No tiene permisos para acceder a este recurso"));
            return;
        }
        boolean[] permisos = forma.getPermisos();
        if ((permisos == null) || !permisos[3]) {
            FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_FATAL,
                                            "Operación interrumpida",
                                            "No tiene permisos para acceder a este recurso"));
        }
        else {
            Map<String, Object> options = new HashMap<>();
            options.put("modal", true);
            options.put("draggable", false);
            options.put("resizable", false);
            options.put("closable", true);
            options.put("contentWidth", forma.getAnchoModal());
            options.put("contentHeight", forma.getAltoModal());
            String ruta = forma.getRuta();
            ruta = ruta.contains("/") ? ruta.substring(ruta.lastIndexOf('/'))
                : ruta;
            RequestContext.getCurrentInstance().openDialog(ruta, options, null);
        }
    }

    /**
     * Solo aplica para los formularios que tienen formulario modal
     * adicional y se requiere invocar el formulario modal adicional
     * en esos casos al nombre del formulario se le agrega la palabra
     * modal al final del nombre
     *
     * @param form
     * @param modulo
     * @param campos
     * @param valores
     */
    public static void cargarModalDatosModal(String form, String modulo) {

        Formulario forma = SessionUtil.cargarFormulario(form + "," + modulo);
        if (forma == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_FATAL,
                                            "Operación interrumpida",
                                            "No tiene permisos para acceder a este recurso"));
            return;
        }
        boolean[] permisos = forma.getPermisos();
        if ((permisos == null) || !permisos[3]) {
            FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_FATAL,
                                            "Operación interrumpida",
                                            "No tiene permisos para acceder a este recurso"));
        }
        else {
            Map<String, Object> options = new HashMap<>();
            options.put("modal", true);
            options.put("draggable", true);
            options.put("resizable", false);
            options.put("closable", false);
            options.put("contentWidth", forma.getAnchoModal());
            options.put("contentHeight", forma.getAltoModal());
            String ruta = forma.getRuta();
            ruta = ruta.contains("/") ? ruta.substring(ruta.lastIndexOf('/'))
                : ruta;
            ruta = ruta.substring(0, ruta.lastIndexOf('.')) + "Modal.sysman";
            RequestContext.getCurrentInstance().openDialog(ruta, options, null);
        }
    }

    public static void cargarModalDatos(String form, String modulo) {

        Formulario forma = SessionUtil.cargarFormulario(form + "," + modulo);
        if (forma == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_FATAL,
                                            "Operación interrumpida",
                                            "No tiene permisos para acceder a este recurso"));
            return;
        }
        boolean[] permisos = forma.getPermisos();
        if ((permisos == null) || !permisos[3]) {
            FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_FATAL,
                                            "Operación interrumpida",
                                            "No tiene permisos para acceder a este recurso"));
        }
        else {
            Map<String, Object> options = new HashMap<>();
            options.put("modal", true);
            options.put("draggable", true);
            options.put("resizable", false);
            options.put("closable", false);
            options.put("contentWidth", forma.getAnchoModal());
            options.put("contentHeight", forma.getAltoModal());
            String ruta = forma.getRuta();
            ruta = ruta.contains("/") ? ruta.substring(ruta.lastIndexOf('/'))
                : ruta;
            RequestContext.getCurrentInstance().openDialog(ruta, options, null);
        }
    }

    public static void cargarModal(String form, String modulo, String[] campos,
        String[] valores) {
        try {
            Formulario forma = SessionUtil
                            .cargarFormulario(form + "," + modulo);
            if (forma == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_FATAL,
                                                "Operación interrumpida",
                                                "No tiene permisos para acceder a este recurso"));
                return;
            }
            boolean[] permisos = forma.getPermisos();
            if ((permisos == null) || !permisos[3]) {
                FacesContext.getCurrentInstance().addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_FATAL,
                                                "Operación interrumpida",
                                                "No tiene permisos para acceder a este recurso"));
            }
            else {
                Map<String, Object> options = new HashMap<>();
                options.put("modal", true);
                options.put("draggable", false);
                options.put("resizable", false);
                options.put("closable", true);
                options.put("contentWidth", forma.getAnchoModal());
                options.put("contentHeight", forma.getAltoModal());

                String ruta = forma.getRuta();
                ruta = ruta.contains("/")
                    ? ruta.substring(ruta.lastIndexOf('/'))
                    : ruta;
                RequestContext.getCurrentInstance().openDialog(ruta, options,
                                getParametroModal(campos, valores));
            }
        }
        catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SessionUtil.class.getName()).log(Level.SEVERE,
                            null, ex);
        }
    }

    public static void cargarModalDatos(String form, String modulo,
        String[] campos, String[] valores) {
        try {
            Formulario forma = SessionUtil
                            .cargarFormulario(form + "," + modulo);
            if (forma == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_FATAL,
                                                "Operación interrumpida",
                                                "No tiene permisos para acceder a este recurso"));
                return;
            }
            boolean[] permisos = forma.getPermisos();

            if ((permisos == null) || !permisos[3]) {
                FacesContext.getCurrentInstance().addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_FATAL,
                                                "Operación interrumpida",
                                                "No tiene permisos para acceder a este recurso"));
            }
            else {
                Map<String, Object> options = new HashMap<>();
                options.put("modal", true);
                options.put("draggable", true);
                options.put("resizable", false);
                options.put("closable", false);
                options.put("contentWidth", forma.getAnchoModal());
                options.put("contentHeight", forma.getAltoModal());

                String ruta = forma.getRuta();
                ruta = ruta.contains("/")
                    ? ruta.substring(ruta.lastIndexOf('/'))
                    : ruta;
                RequestContext.getCurrentInstance().openDialog(ruta, options,
                                getParametroModal(campos, valores));
            }
        }
        catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SessionUtil.class.getName()).log(Level.SEVERE,
                            null, ex);
        }
    }

    /**
     * Solo aplica para los formularios que tienen formulario modal
     * adicional y se requiere invocar el formulario modal adicional
     * en esos casos al nombre del formulario se le agrega la palabra
     * modal al final del nombre
     *
     * @param form
     * @param modulo
     * @param campos
     * @param valores
     */
    public static void cargarModalDatosModal(String form, String modulo,
        String[] campos, String[] valores) {
        try {
            Formulario forma = SessionUtil
                            .cargarFormulario(form + "," + modulo);
            if (forma == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_FATAL,
                                                "Operación interrumpida",
                                                "No tiene permisos para acceder a este recurso"));
                return;
            }
            boolean[] permisos = forma.getPermisos();

            if ((permisos == null) || !permisos[3]) {
                FacesContext.getCurrentInstance().addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_FATAL,
                                                "Operación interrumpida",
                                                "No tiene permisos para acceder a este recurso"));
            }
            else {
                Map<String, Object> options = new HashMap<>();
                options.put("modal", true);
                options.put("draggable", true);
                options.put("resizable", false);
                options.put("closable", false);
                options.put("contentWidth", forma.getAnchoModal());
                options.put("contentHeight", forma.getAltoModal());

                String ruta = forma.getRuta();
                ruta = ruta.contains("/")
                    ? ruta.substring(ruta.lastIndexOf('/'))
                    : ruta;
                ruta = ruta.substring(0, ruta.lastIndexOf('.'))
                    + "Modal.sysman";
                RequestContext.getCurrentInstance().openDialog(ruta, options,
                                getParametroModal(campos, valores));
            }
        }
        catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SessionUtil.class.getName()).log(Level.SEVERE,
                            null, ex);
        }
    }

    public static void cargarModalDatosFlashAdicional(String form,
        String modulo, String[] campos,
        Object[] valores) {
        cargarModalDatosFlashAdicional(form, modulo, campos, valores, false);

    }

    /**
     * Metodo utilizado para abrir un formulario modal en el cual se
     * debe visualizar el archivo de la ruta especificada.
     * 
     * @param ruta
     * -> Ruta absoluta del archivo.
     * @param titulo
     * -> Titulo que se va a mostrar en el encabezado del modal.
     */
    public static void cargarModalVisor(String ruta, String titulo) {
        String tipoArchivo;

        switch (FilenameUtils.getExtension(ruta).toUpperCase()) {
        case SysmanConstantes.TIPO_PDF:
            tipoArchivo = SysmanConstantes.TIPO_PDF;
            break;
        default:
            tipoArchivo = SysmanConstantes.TIPO_IMAGEN;
            break;
        }

        String[] claves = { "PR_RUTA", "PR_TIPO_ARCHIVO", "PR_TITULO" };
        Object[] valores = { ruta, tipoArchivo, titulo };

        SessionUtil.cargarModalDatosFlashCerrar(Integer.toString(
                        GeneralCodigoFormaEnum.FRM_VISUALIZAR_ARCHIVOS_CONTROLADOR
                                        .getCodigo()),
                        getModulo(), claves, valores);
    }

    public static void cargarModalDatosFlash(String form, String modulo,
        String[] campos,
        Object[] valores) {
        cargarModalDatosFlashGeneral(form, modulo, campos, valores, false);

    }

    public static void cargarModalDatosFlashCerrar(String form, String modulo,
        String[] campos,
        Object[] valores) {
        cargarModalDatosFlashGeneral(form, modulo, campos, valores, true);

    }

    private static void cargarModalDatosFlashGeneral(String form, String modulo,
        String[] campos,
        Object[] valores, boolean cerrar) {
        setFlash(armarRutaMapa(campos, valores));
        Formulario forma = SessionUtil.cargarFormulario(form + "," + modulo);
        if (forma == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_FATAL,
                                            "Operación interrumpida",
                                            "No tiene permisos para acceder a este recurso"));
            return;
        }
        boolean[] permisos = forma.getPermisos();
        if ((permisos == null) || !permisos[3]) {
            FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_FATAL,
                                            "Operación interrumpida",
                                            "No tiene permisos para acceder a este recurso"));
        }
        else {
            Map<String, Object> options = new HashMap<>();
            options.put("modal", true);
            options.put("draggable", true);
            options.put("resizable", false);
            options.put("closable", cerrar);
            options.put("contentWidth", forma.getAnchoModal());
            options.put("contentHeight", forma.getAltoModal());

            String ruta = forma.getRuta();
            ruta = ruta.contains("/") ? ruta.substring(ruta.lastIndexOf('/'))
                : ruta;
            RequestContext.getCurrentInstance().openDialog(ruta, options, null);
        }
    }

    private static void cargarModalDatosFlashAdicional(String form,
        String modulo, String[] campos,
        Object[] valores, boolean cerrar) {
        setFlash(armarRutaMapa(campos, valores));
        Formulario forma = SessionUtil.cargarFormulario(form + "," + modulo);
        if (forma == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_FATAL,
                                            "Operación interrumpida",
                                            "No tiene permisos para acceder a este recurso"));
            return;
        }
        boolean[] permisos = forma.getPermisos();
        if ((permisos == null) || !permisos[3]) {
            FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_FATAL,
                                            "Operación interrumpida",
                                            "No tiene permisos para acceder a este recurso"));
        }
        else {
            Map<String, Object> options = new HashMap<>();
            options.put("modal", true);
            options.put("draggable", true);
            options.put("resizable", false);
            options.put("closable", cerrar);
            options.put("contentWidth", forma.getAnchoModal());
            options.put("contentHeight", forma.getAltoModal());

            String ruta = forma.getRuta();
            ruta = ruta.contains("/") ? ruta.substring(ruta.lastIndexOf('/'))
                : ruta;
            ruta = ruta.substring(0, ruta.lastIndexOf('.')) + "Modal"
                + ruta.substring(ruta.lastIndexOf('.'));
            RequestContext.getCurrentInstance().openDialog(ruta, options, null);
        }
    }

    public static String armarRuta(String ruta, String[] campos,
        String[] valores) throws UnsupportedEncodingException {
        StringBuilder rutaB = new StringBuilder(ruta + "?");
        for (int i = 0; i < campos.length; i++) {
            rutaB.append(campos[i]).append("=")
                            .append(URLEncoder.encode(valores[i], "UTF-8"))
                            .append("&");
        }
        return rutaB.toString();
    }

    public static Map<String, Object> armarRutaMapa(String[] campos,
        String[] valores) {
        if (campos.length == 0) {
            return null;
        }
        Map<String, Object> respuesta = new HashMap<>();
        for (int i = 0; i < campos.length; i++) {
            respuesta.put(campos[i], valores[i]);
        }
        return respuesta;
    }

    public static Map<String, Object> armarRutaMapa(String[] campos,
        Object[] valores) {
        if (campos.length == 0) {
            return null;
        }
        Map<String, Object> respuesta = new HashMap<>();
        for (int i = 0; i < campos.length; i++) {
            respuesta.put(campos[i], valores[i]);
        }
        return respuesta;
    }

    public static Map<String, List<String>> getParametroModal(String[] campos,
        String[] valores)
                    throws UnsupportedEncodingException {
        Map<String, List<String>> par = new HashMap<>();
        List aux;
        for (int i = 0; i < campos.length; i++) {
            aux = new ArrayList<>();
            aux.add(valores[i]);
            par.put(campos[i], aux);
        }
        return par;
    }

    public static String getModulo() {
        String rta = (String) getSessionVar("modulo");
        rta = rta == null ? (String) getSessionRemote()
                        .get("modulo")
            : rta;
        return rta;
    }

    public static String getMenu() {
        return (String) getSessionVar("menu");
    }

    public static String getMenuActual() {
        return (String) getSessionVar("menuActual");
    }

    /**
     * Para ajustar los reportes de excel
     * 
     * @return String de SI o NO que identifica si los archivos de
     * axcel se manejan planos o no
     */
    public static String getExcePlano() {
        return (String) getSessionVar("excelPlano");
    }

    /**
     * 
     * @return returna los milisegundos que la aplicación espera
     * inactiva antes de cerrar la session
     */
    public static int getMinutosBloqueo() {
        return getUser().getMinutosBloqueo() * 60 * 1000;
    }

    public static void generarReporte(int modulo, String nombreReporte,
        ReportesBean.FORMATOS formato, String comando,
        Map<String, Object> parametros, Map<String, Object> reemplazos) {
        Reporteador.generaReporte(nombreReporte, formato, comando, modulo,
                        parametros, reemplazos);
    }

    public static void setFlash(Map<String, Object> parametros) {
        try {
            setSessionVarContainer("parametros", parametros);
        }
        catch (NamingException e) {
            Logger.getLogger(SessionUtil.class.getName()).log(Level.SEVERE,
                            null, e);
        }
    }

    public static void setFlashLocal(Map<String, Object> parametros) {
        setSessionVar("parametros", parametros);
    }

    public static Map<String, Object> getFlash() {
        try {
            return (Map<String, Object>) getSessionVarContainer(
                            "parametros");
        }
        catch (NamingException e) {
            Logger.getLogger(SessionUtil.class.getName()).log(Level.SEVERE,
                            null, e);
        }
        return null;
    }

    public static Map<String, Object> getFlashLocal() {
        return (Map<String, Object>) getSessionVar("parametros");
    }

    public static void cleanFlash() {
        try {
            removeSessionVarContainer("parametros");
        }
        catch (NamingException e) {
            Logger.getLogger(SessionUtil.class.getName()).log(Level.SEVERE,
                            null, e);
        }
    }

    public static void redireccionarModalAModalMenu(String numeroFormulario,
        String codigoMenu) {

        try {
            SessionUtil.setSessionVarContainer(
                            SysmanConstantes.LLAVE_FORMULARIO_MODAL,
                            numeroFormulario + "," + codigoMenu);
            RequestContext.getCurrentInstance().closeDialog(null);
        }
        catch (NamingException e) {
            Logger.getLogger(SessionUtil.class.getName()).log(Level.SEVERE,
                            null, e);
        }
    }

    /**
     * Este metodo permite que al ingresar a una opcion se menu se
     * redireccione al menu anterior y muestre un mensaje
     * personalizado. ya sea informativo, alerta, error o fatal.
     * 
     * @param tipoMensaje
     * @param mensaje
     * @param esModal
     */
    public static void redireccionarMenuMensaje(String tipoMensaje,
        String mensaje, boolean esModal) {

        try {
            SessionUtil.setSessionVarContainer(
                            SysmanConstantes.LLAVE_MENSAJE_ABRIR,
                            tipoMensaje + "#" + mensaje);

            if (esModal) {
                RequestContext.getCurrentInstance().closeDialog(null);
            }
            else {
                redireccionarMenu();
            }

        }
        catch (NamingException e) {
            Logger.getLogger(SessionUtil.class.getName()).log(Level.SEVERE,
                            null, e);
        }
    }

    /**
     * Abre un di&aacute;logo cargando el formulario empleado para
     * visualizar archivos PDF e im&aacute;genes en el m&oacute;dulo
     * Workflow; y carga el archivo pasado por par&aacute;metro.
     * 
     * @param ubicacionManual
     * ruta que apunta al documento PDF que se va a cargar en el
     * formulario.
     * @param tituloVentana
     * t&iacute;tulo de la ventana de ayuda.
     * @throws SystemException
     */
    public static void verAyuda(String ubicacionManual, String tituloVentana)
                    throws SystemException {
        String[] claves = { "PR_RUTA", "PR_TIPO_ARCHIVO", "PR_TITULO" };
        Object[] valores = { ubicacionManual, SysmanConstantes.TIPO_PDF,
                             tituloVentana };
        setFlash(armarRutaMapa(claves, valores));

        int idLectorPDF = GeneralCodigoFormaEnum.FRM_VISUALIZAR_ARCHIVOS_CONTROLADOR
                        .getCodigo();
        Map<String, Object> datosForma = traerDatosFormulario(idLectorPDF);

        Map<String, Object> options = new HashMap<>();
        options.put("minimizable", true);
        options.put("maximizable", true);
        options.put("modal", false);
        options.put("draggable", true);
        options.put("resizable", false);
        options.put("closable", true);
        options.put("contentWidth", datosForma.get("ANCHO_MODAL"));
        options.put("contentHeight", datosForma.get("ALTO_MODAL"));
        String ruta = SysmanFunciones.toString(datosForma.get("RUTA"));
        ruta = ruta.contains("/")
            ? ruta.substring(ruta.lastIndexOf('/'))
            : ruta;
        RequestContext.getCurrentInstance().openDialog(ruta, options,
                        null);
    }

    /**
     * Trae los datos de un formulario determinado.
     * 
     * @param codigo
     * c&oacute;digo de formulario
     * @return registro con los datos del formulario
     * @throws SystemException
     * en caso de que se presenten problemas al realizar la consulta
     */
    private static Map<String, Object> traerDatosFormulario(int codigo)
                    throws SystemException {
        RequestManager requestManager = new RequestManager();
        Map<String, Object> params = new HashMap<>();
        params.put(ParametrosConstantes.CODIGO.getValue(), codigo);
        String url = UrlServiceUtil
                        .getUrlBeanById(URL_GET_FORMULARIO_POR_CODIGO).getUrl();
        Parameter parameter = requestManager.get(url, params);
        if (parameter == null) {
            throw new SystemException(
                            "No se pudo recuperar el servicio identificado con el código: "
                                + URL_GET_FORMULARIO_POR_CODIGO);
        }
        return parameter.getFields();
    }

}
