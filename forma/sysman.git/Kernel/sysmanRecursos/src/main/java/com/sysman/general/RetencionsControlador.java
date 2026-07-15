package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.RetencionsControladorEnum;
import com.sysman.general.enums.RetencionsControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author cmanrique
 * 
 * @author jlramirez
 * @version 2, 03/04/2017, proceso de Refactoring y modificaciones
 * segUn especificaciones de SONARLINT
 */
@ManagedBean
@ViewScoped
public class RetencionsControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private List<Registro> listaAnoOrigen;
    private List<Registro> listaAnoDestino;
    private List<Registro> listaAnio;
    private String anoOrigen;
    private String anoDestino;

    /**
     * Creates a new instance of RetencionsControlador
     */
    public RetencionsControlador() {
        super();
        compania = SessionUtil.getCompania();
        numFormulario = GeneralCodigoFormaEnum.RETENCIONS_CONTROLADOR
                        .getCodigo();
        try {
            validarPermisos();
        }
        catch (SysmanException ex) {
            Logger.getLogger(RetencionsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.RETEFUENTE;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        cargarListaAnoOrigen();
        cargarListaAnoDestino();
        cargarListaAnio();
        abrirFormulario();

    }

    public void cargarListaAnoOrigen() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        try {
            listaAnoOrigen = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RetencionsControladorUrlEnum.URL2312
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(RetencionsControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAnoDestino() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaAnoDestino = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RetencionsControladorUrlEnum.URL2703
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(RetencionsControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaAnio() {
        listaAnio = listaAnoDestino;
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

    }

    public List<Registro> getListaAnoOrigen() {
        return listaAnoOrigen;
    }

    public void setListaAnoOrigen(List<Registro> listaAnoOrigen) {
        this.listaAnoOrigen = listaAnoOrigen;
    }

    public List<Registro> getListaAnoDestino() {
        return listaAnoDestino;
    }

    public void setListaAnoDestino(List<Registro> listaAnoDestino) {
        this.listaAnoDestino = listaAnoDestino;
    }

    /**
     * Retorna la lista listaAnio
     * 
     * @return listaAnio
     */
    public List<Registro> getListaAnio() {
        return listaAnio;
    }

    /**
     * Asigna la lista listaAnio
     * 
     * @param listaAnio
     * Variable a asignar en listaAnio
     */
    public void setListaAnio(List<Registro> listaAnio) {
        this.listaAnio = listaAnio;
    }

    public String getAnoOrigen() {
        return anoOrigen;
    }

    public void setAnoOrigen(String anoOrigen) {
        this.anoOrigen = anoOrigen;
    }

    public String getAnoDestino() {
        return anoDestino;
    }

    public void setAnoDestino(String anoDestino) {
        this.anoDestino = anoDestino;
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    public void oprimirRetefuente() {
        // <CODIGO_DESARROLLADO>

        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            parametros.put(RetencionsControladorEnum.PARAM0.getValue(),
                            anoDestino);
            parametros.put(RetencionsControladorEnum.PARAM1.getValue(),
                            anoOrigen);
            parametros.put(RetencionsControladorEnum.PARAM2.getValue(),
                            new Date());
            parametros.put(RetencionsControladorEnum.PARAM3.getValue(),
                            SessionUtil.getUser().getCodigo());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            RetencionsControladorUrlEnum.URL5727
                                                            .getValue());

            requestManager.save(urlCreate.getUrl(),
                            urlCreate.getMetodo(), parametros);
            anoOrigen = "";
            anoDestino = "";
            cargarListaAnoOrigen();
            cargarListaAnoDestino();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>

    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        cargarListaAnoOrigen();
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos()
                        .remove(RetencionsControladorEnum.PARAM4.getValue());
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        anoOrigen = "";
        anoDestino = "";
        cargarListaAnoOrigen();
        cargarListaAnoDestino();
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void removerCombos() {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
    }

    @Override
    public void asignarValoresRegistro() {
        // NO SE IMPLEMENTA

    }

}
