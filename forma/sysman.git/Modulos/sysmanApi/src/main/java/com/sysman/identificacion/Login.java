/*
 * To change this license header, choose License Headers in Project
 * Properties. To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sysman.identificacion;

import com.sysman.controladores.SessionUtil;
import com.sysman.dao.CompaniaDao;
import com.sysman.dao.Registro;
import com.sysman.dao.UsuarioDao;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.logica.Compania;
import com.sysman.logica.Usuario;
import com.sysman.services.FormContinuoService;
import com.sysman.session.SessionContainerSt;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.menu.MenuModel;

/**
 *
 * @author cmanrique
 */
@ManagedBean
@SessionScoped
public class Login {

    private static final String SERVICIO_TODAS_COMPANIAS = "59003";

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

    private String imagenCompania;

    protected ResourceBundle idioma;
    private Map<String, Object> variables;

    private List<Registro> companias;

    @EJB
    private SessionContainerSt sessionContainer;

    private String rutaIframe;

    private FormContinuoService service;

    protected final Log logger = LogFactory.getLog(this.getClass());

    private MenuModel modeloMenu;

    /**
     * Creates a new instance of LoginBean
     */
    public Login() {
        idioma = ResourceBundle.getBundle(SysmanConstantes.RUTA_IDIOMA);
        service = FormContinuoService.getInstance();
        variables = new HashMap<>();
        menu = "0";
    }

    public void login() {
        menu = "0";
        try {
            UsuarioDao udao = new UsuarioDao();
            CompaniaDao cdao = new CompaniaDao();
            udao.validarUsuario(usuario, contrasenia, compania);
            user = udao.getUsuario();
            if (user != null) {

                SessionUtil.setSessionVar("usuario", user);
                SessionUtil.setSessionVar("compania", compania);
                companiaIngreso = cdao.validarCompania(compania);
                SessionUtil.setSessionVar("companiaIngreso", companiaIngreso);

                variables.put("usuario", user);
                variables.put("compania", compania);
                variables.put("companiaIngreso", companiaIngreso);
                generarCookie();
            }
            else {
                logger.error(idioma.getString(
                                "MSM_USUARIO_CONTRASENIA_INCORRECTO"));
            }
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
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
            String sessionId = URLEncoder.encode(value, "UTF-8");
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

    public String serializar(Object objeto) throws IOException {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(bs);
        os.writeObject(objeto); // this es de tipo DatoUdp
        os.close();
        return bs.toString();
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
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
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

}
