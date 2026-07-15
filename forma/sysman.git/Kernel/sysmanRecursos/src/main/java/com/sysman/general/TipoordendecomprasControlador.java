package com.sysman.general;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.TipoordendecomprasControladorEnum;
import com.sysman.general.enums.TipoordendecomprasControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;

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

/**
 *
 * @author ngomez
 * @version 1, 20/10/2015
 * 
 * @author jlramirez
 * @version 2, 05/04/2017, proceso de Refactoring y modificaciones
 * segUn especificaciones de SONARLINT
 */
@ManagedBean
@ViewScoped
public class TipoordendecomprasControlador extends BeanBaseDatosAcmeImpl {

    private final String compania;
    private final String modulo;
    private boolean sgcVisible;
    private boolean contratoVisible;
    private boolean bloquearCodigo;
    private final String nombreCons;
    private final String codigoSgcCons;
    /**
     * Implementacion del EJB de SysmanUtil para obtener el valor de
     * un parametro
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    public TipoordendecomprasControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        nombreCons = "NOMBRE";
        codigoSgcCons = "CODIGO_SGC";
        try {
            numFormulario = GeneralCodigoFormaEnum.TIPOORDENDECOMPRAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            contratoVisible = "400108".equals(SessionUtil.getMenuActual());
        }
        catch (Exception ex) {
            Logger.getLogger(TipoordendecomprasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @Override
    public void iniciarListas() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void iniciarListasSub() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void iniciarListasSubNulo() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.TIPOORDENDECOMPRA;
        buscarLlave();
        asignarOrigenDatos();
    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    public void cambiarNombre() {
        registro.getCampos()
                        .put(nombreCons, registro.getCampos()
                                        .get(nombreCons) == null ? " "
                                            : registro.getCampos()
                                                            .get(nombreCons)
                                                            .toString()
                                                            .toUpperCase());
    }

    public void cambiarNumeroInicial() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CODIGO.getName(),
                        registro.getCampos().get(
                                        GeneralParameterEnum.CODIGO
                                                        .getName()) == null
                                                            ? " "
                                                            : registro.getCampos()
                                                                            .get(
                                                                                            GeneralParameterEnum.CODIGO
                                                                                                            .getName())
                                                                            .toString());
        List<Registro> aux;
        try {
            aux = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TipoordendecomprasControladorUrlEnum.URL2743
                                                                            .getValue())
                                            .getUrl(), param));
            String aux2 = aux.get(0).getCampos()
                            .get(GeneralParameterEnum.CUENTA.getName()) == null
                                ? " "
                                : aux.get(0).getCampos()
                                                .get(GeneralParameterEnum.CUENTA
                                                                .getName())
                                                .toString();
            if (!"0".equals(aux2)) {
                registro.getCampos().put("NUMEROINICIAL",
                                registroIni.get("NUMEROINICIAL"));
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2250"));
            }
        }
        catch (SystemException e) {
            Logger.getLogger(TipoordendecomprasControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cambiarCODIGOSGC() {
        registro.getCampos().put(codigoSgcCons, registro.getCampos()
                        .get(codigoSgcCons) == null ? " " : registro.getCampos()
                                        .get(codigoSgcCons).toString()
                                        .toUpperCase());

    }

    public void cambiarTexto59() {
        registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(), registro
                        .getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()) == null
                            ? " "
                            : registro.getCampos()
                                            .get(GeneralParameterEnum.CODIGO
                                                            .getName())
                                            .toString().toUpperCase());
    }

    @Override
    public void abrirFormulario() {
        sgcVisible = true;
        try {

            Object aux = ejbSysmanUtil.consultarParametro(compania,
                            "MANEJA CONFIGURACION FORMATOS CALIDAD", modulo,
                            new Date(), true);
            if (aux != null) {
                if ("NO".equals(aux)) {
                    sgcVisible = false;
                }
            }
            else {
                sgcVisible = false;
            }
        }
        catch (SystemException ex) {
            Logger.getLogger(TipoordendecomprasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void cargarRegistro() {

        if (css != null) {

            bloquearCodigo = true;
        }
        else {
            bloquearCodigo = false;
        }

        precargarRegistro();
    }

    @Override
    public boolean insertarAntes() {

        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(TipoordendecomprasControladorEnum.TIPO_MODELO.getValue(),"C");
        registro.getCampos().put(TipoordendecomprasControladorEnum.CLASE.getValue(),"C");
        registro.getCampos().put(TipoordendecomprasControladorEnum.MANEJA.getValue(),"E");
        actualizarAntes();

        return true;
    }

    @Override
    public boolean insertarDespues() {
        Map<String, Object> parametros = new HashMap<>();
        parametros.put(GeneralParameterEnum.CODIGO.getName(),
                        registro.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        parametros.put(GeneralParameterEnum.NOMBRE.getName(),
                        registro.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
        parametros.put(TipoordendecomprasControladorEnum.PARAM0.getValue(),
                        new Date());
        parametros.put(TipoordendecomprasControladorEnum.PARAM1.getValue(),
                        SessionUtil.getUser().getCodigo());

        UrlBean urlCreate = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TipoordendecomprasControladorUrlEnum.URL2744
                                                        .getValue());
        try {
            requestManager.saveCount(urlCreate.getUrl(),
                            urlCreate.getMetodo(), parametros);
        }
        catch (SystemException ex) {
            Logger.getLogger(TipoordendecomprasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

        return true;
    }

    @Override
    public boolean actualizarAntes() {

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

    public boolean isSgcVisible() {
        return sgcVisible;
    }

    public void setSgcVisible(boolean sgcVisible) {
        this.sgcVisible = sgcVisible;
    }

    public boolean isContratoVisible() {
        return contratoVisible;
    }

    public void setContratoVisible(boolean contratoVisible) {
        this.contratoVisible = contratoVisible;
    }

    public boolean isBloquearCodigo() {
        return bloquearCodigo;
    }

    public void setBloquearCodigo(boolean bloquearCodigo) {
        this.bloquearCodigo = bloquearCodigo;
    }

}
