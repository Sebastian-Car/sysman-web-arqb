package com.sysman.general;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.SolicitudOrdenCompraControladorEnum;
import com.sysman.general.enums.SolicitudOrdenCompraControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author jrodriguezr
 * @version 1, 06/01/2016
 *
 *
 * @author ybecerra
 * @version 2, 05/04/2017 Arreglos Visualizacion del formulario
 *
 */
@ManagedBean
@ViewScoped

public class SolicitudOrdenCompraControlador extends BeanBaseModal {

    private final String compania;
    private String opcion;
    private String seleccionada;
    private String numeroOrden;
    private String anio;
    private RegistroDataModelImpl listasolicitudSeleccionada;
    private String claseOrden;
    String tipoT;
    String claseT;
    String dependencia;

    /**
     * Creates a new instance of SolicitudOrdenCompraControlador
     */
    public SolicitudOrdenCompraControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.SOLICITUD_ORDEN_COMPRA_CONTROLADOR
                            .getCodigo();
            numeroOrden = JsfUtil.getParametros().get("numeroOrden");
            claseOrden = JsfUtil.getParametros().get("claseOrden");
            anio = JsfUtil.getParametros().get("anio");
            validarPermisos();
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        opcion = "1";
        cargarListasolicitudSeleccionada();
        abrirFormulario();
    }

    public void cargarModal() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListasolicitudSeleccionada() {
        UrlBean urlBean;
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASEORDEN.getName(), claseOrden);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), numeroOrden);
        if ("1".equals(opcion)) {
            urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SolicitudOrdenCompraControladorUrlEnum.URL2601
                                                            .getValue());
        }
        else {
            urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SolicitudOrdenCompraControladorUrlEnum.URL101
                                                            .getValue());
        }
        if (listasolicitudSeleccionada == null) {
            listasolicitudSeleccionada = new RegistroDataModelImpl(
                            urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, GeneralParameterEnum.CODIGO.getName());
        }
        else {
            listasolicitudSeleccionada.setUrl(urlBean.getUrl());
            listasolicitudSeleccionada
                            .setUrlConteo(urlBean.getUrlConteo().getUrl());
        }

    }

    public void cambiarMarco9() {
        // <CODIGO_DESARROLLADO>
        seleccionada = null;
        cargarListasolicitudSeleccionada();
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirAceptar() {
        if (seleccionada == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2116"));
        }
        else {
            if ("1".equals(opcion)) {
                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put(GeneralParameterEnum.NOVEDAD.getName(), seleccionada);
                // 'WTORRES'Se agregan las actividades segun la
                // solicitud seleccionada.
                List<Registro> regAux;
                try {
                    regAux = RegistroConverter
                                    .toListRegistro(requestManager.getList(
                                                    UrlServiceUtil.getInstance()
                                                                    .getUrlServiceByUrlByEnumID(
                                                                                    SolicitudOrdenCompraControladorUrlEnum.URL170
                                                                                                    .getValue())
                                                                    .getUrl(),
                                                    param));
                    insertarRegistro(regAux);

                    cargarListasolicitudSeleccionada();
                }
                catch (SystemException e) {
                    logger.error(e.getMessage(), e);
                    JsfUtil.agregarMensajeError(e.getMessage());
                }

            }
            else {
                // 'WTORRES'Se eliminan las actividades segun
                // la solicitud seleccionada.
                Map<String, Object> fields = new TreeMap<>();
                fields.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                fields.put(SolicitudOrdenCompraControladorEnum.PARAM0
                                .getValue(), tipoT);
                fields.put(SolicitudOrdenCompraControladorEnum.PARAM1
                                .getValue(), claseT);
                fields.put(GeneralParameterEnum.NOVEDAD.getName(),
                                seleccionada);
                fields.put(GeneralParameterEnum.DEPENDENCIA.getName(),
                                dependencia);

                UrlBean urlDelete = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                SolicitudOrdenCompraControladorUrlEnum.URL202
                                                                .getValue());
                try {
                    requestManager.delete(urlDelete.getUrl(),
                                    fields);
                }
                catch (SystemException e) {
                    logger.error(e.getMessage(), e);
                    JsfUtil.agregarMensajeError(e.getMessage());
                }
                JsfUtil.agregarMensajeInformativo(idioma
                                .getString("MSM_REGISTRO_ELIMINADO"));
            }
        }
    }

    public void insertarRegistro(List<Registro> regAux) {
        try {
            if (!regAux.isEmpty()) {
                if (definirSector(regAux)) {
                    return;
                }
                Map<String, Object> fields = new TreeMap<>();
                fields.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                fields.put(GeneralParameterEnum.CLASEORDEN.getName(),
                                claseOrden);
                fields.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                                numeroOrden);
                fields.put(GeneralParameterEnum.CREATED_BY.getName(),
                                SessionUtil.getUser().getCodigo());
                fields.put(GeneralParameterEnum.NOVEDAD.getName(),
                                seleccionada);
                Parameter parameter = new Parameter();
                parameter.setFields(fields);

                UrlBean urlCreate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                SolicitudOrdenCompraControladorUrlEnum.URL7206
                                                                .getValue());
                requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                                parameter);
                JsfUtil.agregarMensajeInformativo(idioma
                                .getString("MSM_REGISTRO_INGRESADO"));
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public boolean definirSector(List<Registro> regAux) {
        for (int i = 0; i < regAux.size(); i++) {
            if (regAux.get(i).getCampos()
                            .get("DESTINORECURSOS") == null) {
                JsfUtil.agregarMensajeAlerta(idioma
                                .getString("TB_TB2117")
                                .replace("$#auxiliar$#",
                                                regAux.get(i).getCampos()
                                                                .get("AUXILIAR")
                                                                .toString()));
                return true;
            }
        }
        return false;
    }

    public void oprimirCancelar() {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilasolicitudSeleccionada(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        seleccionada = registroAux.getCampos().get("CODIGO") == null ? ""
            : registroAux.getCampos().get("CODIGO").toString();
        tipoT = registroAux.getCampos().get("TIPOT") == null ? ""
            : registroAux.getCampos().get("TIPOT").toString();
        claseT = registroAux.getCampos().get("CLASET") == null ? ""
            : registroAux.getCampos().get("CLASET").toString();
        dependencia = registroAux.getCampos().get("DEPENDENCIA") == null ? ""
            : registroAux.getCampos().get("DEPENDENCIA").toString();

    }

    public String getOpcion() {
        return opcion;
    }

    public void setOpcion(String opcion) {
        this.opcion = opcion;
    }

    public String getSeleccionada() {
        return seleccionada;
    }

    public void setSeleccionada(String seleccionada) {
        this.seleccionada = seleccionada;
    }

    public String getNumeroOrden() {
        return numeroOrden;
    }

    public void setNumeroOrden(String numeroOrden) {
        this.numeroOrden = numeroOrden;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public RegistroDataModelImpl getListasolicitudSeleccionada() {
        return listasolicitudSeleccionada;
    }

    public void setListasolicitudSeleccionada(
        RegistroDataModelImpl listasolicitudSeleccionada) {
        this.listasolicitudSeleccionada = listasolicitudSeleccionada;
    }

    @Override
    public void abrirFormulario() {
        // METODO_NO_IMPLEMENTADO
    }

}