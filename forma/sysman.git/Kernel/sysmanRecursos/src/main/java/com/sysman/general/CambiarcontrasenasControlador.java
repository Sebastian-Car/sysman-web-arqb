package com.sysman.general;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.CambiarcontrasenasControladorEnum;
import com.sysman.general.enums.CambiarcontrasenasControladorUrlEnum;
import com.sysman.general.enums.CuentasControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.context.RequestContext;

/**
 *
 * @author ybecerra
 * @version 1, 13/01/2016
 * @modified jsforero
 * @version 2. 05/04/2017 Se realizo el refactory. Ademas se hicieron
 * las respectivas Correcciones del sonar.
 */
@ManagedBean
@ViewScoped
public class CambiarcontrasenasControlador extends BeanBaseModal {

    private String anterior;
    private String nueva;
    private String confirmar;
    private String usuario;

    private String politicaContrasena;
    private int cantidadNumeros = 0;
    private int cantidadLetras = 0;
    private int cantidadSimbolos = 0;

    /**
     * Creates a new instance of CambiarcontrasenasControlador
     */
    public CambiarcontrasenasControlador() {
        super();
        try {
            numFormulario = GeneralCodigoFormaEnum.CAMBIARCONTRASENAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(CambiarcontrasenasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void init() {
        usuario = SessionUtil.getUser().getCodigo();
        abrirFormulario();
    }

    public void oprimirAceptar()

    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.USUARIO.getName(), usuario);

        try {
            UrlBean urlListMeI = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            CambiarcontrasenasControladorUrlEnum.URL4379
                                                            .getValue());
            Registro registro = RegistroConverter.toRegistro(
                            requestManager.get(urlListMeI.getUrl(), param));
            if ("0".equals(registro.getCampos().get("CUENTA").toString())) {
                JsfUtil.agregarMensajeError("El usuario no existe");
                return;
            }
        }
        catch (SystemException ex) {
            Logger.getLogger(CambiarcontrasenasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }

        if (anterior.equals(nueva)) {
            JsfUtil.agregarMensajeAlerta(
                            "La contraseña nueva es igual a la anterior");
        }
        else if (nueva.equals(confirmar)) {

            if (!SysmanFunciones.validarContrasena(nueva, cantidadNumeros,
                            cantidadLetras, cantidadSimbolos)) {

                JsfUtil.agregarMensajeAlerta(
                                "La contraseña ingresada no cumple con las politicas de privacidad");
            }
            else {

                try {

                    SessionUtil.setSessionVarContainer("PASSWORD", nueva);

                    Map<String, Object> parametros = new HashMap<>();
                    parametros.put(GeneralParameterEnum.USUARIO.getName(),
                                    usuario);
                    parametros.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                                    usuario);
                    parametros.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                                    new Date());
                    parametros.put(CambiarcontrasenasControladorEnum.NUEVA
                                    .getValue(), nueva);
                    parametros.put(CambiarcontrasenasControladorEnum.ANTERIOR
                                    .getValue(), anterior);

                    parametros.put("FECHA_ACTCONTRASENA", new Date());

                    Parameter parameter = new Parameter();

                    parameter.setFields(parametros);
                    UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                                    CambiarcontrasenasControladorUrlEnum.URL2395
                                                                    .getValue());
                    int contrasenaAnterior = requestManager.update(
                                    urlUpdate.getUrl(), urlUpdate.getMetodo(),
                                    parameter);

                    if (contrasenaAnterior == 1) {

                        SessionUtil.setSessionVarContainer(
                                        "ACTUALIZAR_FECHA_ACTCONTRASENA", true);

                        JsfUtil.agregarMensajeInformativo(
                                        idioma.getString("TB_TB19"));
                    }
                    else {
                        JsfUtil.agregarMensajeError(
                                        idioma.getString("TB_TB20"));
                    }
                }
                catch (SystemException | NamingException ex) {
                    Logger.getLogger(CambiarcontrasenasControlador.class
                                    .getName())
                                    .log(Level.SEVERE, null, ex);
                    JsfUtil.agregarMensajeError(
                                    idioma.getString("MSM_TRANS_INTERRUMPIDA")
                                        + ex.getMessage());
                }
            }
        }
        else {
            JsfUtil.agregarMensajeError("Las contraseñas no coinciden");
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCancelar() {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }

    public String getAnterior() {
        return anterior;
    }

    public void setAnterior(String anterior) {
        this.anterior = anterior;
    }

    public String getNueva() {
        return nueva;
    }

    public void setNueva(String nueva) {
        this.nueva = nueva;
    }

    public String getConfirmar() {
        return confirmar;
    }

    public void setConfirmar(String confirmar) {
        this.confirmar = confirmar;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    @Override
    public void abrirFormulario() {
        consultarPoliticaContrasena();

    }

    public String getPoliticaContrasena() {
        return politicaContrasena;
    }

    public void setPoliticaContrasena(String politicaContrasena) {
        this.politicaContrasena = politicaContrasena;
    }

    private void consultarPoliticaContrasena() {

        try {
            Registro rs = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            CuentasControladorUrlEnum.URL1864001
                                                                                            .getValue())
                                                            .getUrl(),
                                            null));

            if (rs != null) {
                cantidadNumeros = Integer.parseInt(SysmanFunciones
                                .nvl(rs.getCampos().get("CANTIDAD_NUMEROS"),
                                                "0")
                                .toString());
                cantidadLetras = Integer.parseInt(SysmanFunciones
                                .nvl(rs.getCampos().get("CANTIDAD_LETRAS"), "0")
                                .toString());
                cantidadSimbolos = Integer.parseInt(SysmanFunciones
                                .nvl(rs.getCampos().get("CANTIDAD_SIMBOLOS"),
                                                "0")
                                .toString());
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        politicaContrasena = "Señor usuario la contraseña debe contener "
            + cantidadNumeros + " numeros, " + cantidadLetras + " letras y "
            + cantidadSimbolos + " simbolos";

    }

}
