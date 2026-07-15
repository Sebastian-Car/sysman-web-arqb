package com.sysman.almacen;

import com.sysman.almacen.enums.FrmtipoeventodevolutivosControladorEnum;
import com.sysman.almacen.enums.FrmtipoeventodevolutivosControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author vmolano
 * @version 1, 07/04/2016
 *
 * @author ybecerra
 * @version 2, 03/05/2017 Refactoring
 *
 */
@ManagedBean
@ViewScoped

public class FrmtipoeventodevolutivosControlador
                extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private final String serieCons;

    private String devolutivoActual;
    private String elementoActual;
    private String serieActual;
    private RegistroDataModelImpl listacmbDevolutivo;
    private RegistroDataModelImpl listacmbDevolutivoE;
    private String auxiliar;
    private List<Registro> listacmbElemento;
    private boolean verNuevo;

    /**
     * Creates a new instance of FrmtipoeventodevolutivosControlador
     */
    public FrmtipoeventodevolutivosControlador() {
        super();
        compania = SessionUtil.getCompania();
        serieCons = GeneralParameterEnum.SERIE.getName();
        try {

            numFormulario = GeneralCodigoFormaEnum.FRMTIPOEVENTODEVOLUTIVOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(
                            FrmtipoeventodevolutivosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.TIPO_EVENTO_DEVOLUTIVO;
        buscarLlave();
        registro = new Registro();
        cargarListacmbElemento();
        cargarListacmbDevolutivo();
        cargarListacmbDevolutivoE();
        abrirFormulario();

    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ELEMENTO.getName(),
                        elementoActual);
        parametrosListado.put(GeneralParameterEnum.SERIE.getName(),
                        serieActual);
    }

    public List<Registro> getListacmbElemento() {
        return listacmbElemento;
    }

    public void setListacmbElemento(List<Registro> listacmbElemento) {
        this.listacmbElemento = listacmbElemento;
    }

    public RegistroDataModelImpl getListacmbDevolutivo() {
        return listacmbDevolutivo;
    }

    public void setListacmbDevolutivo(
        RegistroDataModelImpl listacmbDevolutivo) {
        this.listacmbDevolutivo = listacmbDevolutivo;
    }

    public RegistroDataModelImpl getListacmbDevolutivoE() {
        return listacmbDevolutivoE;
    }

    public void setListacmbDevolutivoE(
        RegistroDataModelImpl listacmbDevolutivoE) {
        this.listacmbDevolutivoE = listacmbDevolutivoE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String getDevolutivoActual() {
        return devolutivoActual;
    }

    public void setDevolutivoActual(String devolutivoActual) {
        this.devolutivoActual = devolutivoActual;
    }

    public String getElementoActual() {
        return elementoActual;
    }

    public void setElementoActual(String elementoActual) {
        this.elementoActual = elementoActual;
    }

    public String getSerieActual() {
        return serieActual;
    }

    public void setSerieActual(String serieActual) {
        this.serieActual = serieActual;
    }

    public boolean isVerNuevo() {
        return verNuevo;
    }

    public void setVerNuevo(boolean verNuevo) {
        this.verNuevo = verNuevo;
    }

    public void cargarListacmbElemento() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listacmbElemento = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmtipoeventodevolutivosControladorUrlEnum.URL4619
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListacmbDevolutivo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmtipoeventodevolutivosControladorUrlEnum.URL5190
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacmbDevolutivo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, serieCons);

    }

    public void cargarListacmbDevolutivoE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmtipoeventodevolutivosControladorUrlEnum.URL5807
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacmbDevolutivoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, serieCons);
    }

    public void seleccionarFilacmbDevolutivo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        devolutivoActual = registroAux.getCampos().get("DESCRIPCION") == null
            ? "" : registroAux.getCampos().get("DESCRIPCION").toString();
        elementoActual = registroAux.getCampos().get("ELEMENTO") == null ? ""
            : registroAux.getCampos().get("ELEMENTO").toString();
        serieActual = registroAux.getCampos().get(serieCons) == null ? ""
            : registroAux.getCampos().get(serieCons).toString();
        verNuevo = false;
        if (SysmanFunciones.validarVariableVacio(elementoActual)) {
            verNuevo = true;
            registro.getCampos()
                            .put(FrmtipoeventodevolutivosControladorEnum.PARAM0
                                            .getValue(), elementoActual);
            registro.getCampos()
                            .put(FrmtipoeventodevolutivosControladorEnum.PARAM1
                                            .getValue(), serieActual);

        }
        reasignarOrigen();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public void removerCombos() {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos()
                        .remove(FrmtipoeventodevolutivosControladorEnum.PARAM0
                                        .getValue());
        registro.getCampos()
                        .remove(FrmtipoeventodevolutivosControladorEnum.PARAM1
                                        .getValue());
        registro.getCampos().remove("NOM_TIPO_EVENTO");

    }

    @Override
    public void asignarValoresRegistro() {
        registro.getCampos().put(FrmtipoeventodevolutivosControladorEnum.PARAM0
                        .getValue(), elementoActual);
        registro.getCampos().put(FrmtipoeventodevolutivosControladorEnum.PARAM1
                        .getValue(), serieActual);

    }
}
