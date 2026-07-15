package com.sysman.precontractual;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.precontractual.enums.EtapaPreDocControladorUrlEnum;
import com.sysman.precontractual.enums.EtapaPreDocControladorEnum;

import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Map;
import java.util.TreeMap;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author esarmiento
 * @version 1, 19/02/2016
 * 
 * @author eamaya
 * @version 2.0, Proceso de Refactoring DSS, cambio de numero de
 * formulario por enum y correcciones SonarLint
 * 
 */
@ManagedBean
@ViewScoped

public class EtapaPreDocControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private final String modulo;
    private String tipoContrato;
    private String idEtapa;
    private String descripcion;
    Map<String, Object> parametrosEntrada = new TreeMap<>();

    /**
     * Constante que almacena el valor de la cadena IDETAPA
     */

    private final String cIdEtapa;

    private RegistroDataModel listaidEtapa;
    private RegistroDataModel listaidEtapaE;
    private RegistroDataModelImpl listaModelo;
    private RegistroDataModelImpl listaModeloE;
    private String auxiliar;

    /**
     * Creates a new instance of EtapaPreDocControlador
     */
    public EtapaPreDocControlador() {
        super();
        numFormulario = GeneralCodigoFormaEnum.ETAPA_PRE_DOC_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cIdEtapa = "IDETAPA";
        parametrosEntrada = SessionUtil.getFlash();
        tipoContrato = parametrosEntrada.get("tipoContrato").toString();
        idEtapa = parametrosEntrada.get("idEtapa").toString();

        if (parametrosEntrada.get("descripcion") != null) {
            descripcion = parametrosEntrada.get("descripcion").toString();
        }
        else {
            descripcion = "";
        }
        SessionUtil.cleanFlash();

        try {
            validarPermisos();
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.ETAPA_PR_DOC;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        registro.getCampos().put(cIdEtapa, idEtapa);
        registro.getCampos().put(GeneralParameterEnum.DESCRIPCION.getName(),
                        descripcion);
        cargarListaModelo();
        cargarListaModeloE();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        parametrosListado.put(EtapaPreDocControladorEnum.TIPO.getValue(),
                        tipoContrato);

        parametrosListado.put(EtapaPreDocControladorEnum.ETAPA.getValue(),
                        idEtapa);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EtapaPreDocControladorUrlEnum.URL0001
                                                        .getValue());

        urlCreacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EtapaPreDocControladorUrlEnum.URL0002
                                                        .getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EtapaPreDocControladorUrlEnum.URL0003
                                                        .getValue());

        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EtapaPreDocControladorUrlEnum.URL0004
                                                        .getValue());

    }

    public void cargarListaModelo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EtapaPreDocControladorUrlEnum.URL0005
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(EtapaPreDocControladorEnum.MODULO.getValue(),
                        modulo);

        listaModelo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaModeloE() {
        listaModeloE = listaModelo;

    }

    public void seleccionarFilaModelo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("MODELO_DOC",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    public void seleccionarFilaModeloE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
    }

    public String getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    public String getIdEtapa() {
        return idEtapa;
    }

    public void setIdEtapa(String idEtapa) {
        this.idEtapa = idEtapa;
    }

    public RegistroDataModel getListaidEtapa() {
        return listaidEtapa;
    }

    public void setListaidEtapa(RegistroDataModel listaidEtapa) {
        this.listaidEtapa = listaidEtapa;
    }

    public RegistroDataModel getListaidEtapaE() {
        return listaidEtapaE;
    }

    public void setListaidEtapaE(RegistroDataModel listaidEtapaE) {
        this.listaidEtapaE = listaidEtapaE;
    }

    public RegistroDataModelImpl getListaModelo() {
        return listaModelo;
    }

    public void setListaModelo(RegistroDataModelImpl listaModelo) {
        this.listaModelo = listaModelo;
    }

    public RegistroDataModelImpl getListaModeloE() {
        return listaModeloE;
    }

    public void setListaModeloE(RegistroDataModelImpl listaModeloE) {
        this.listaModeloE = listaModeloE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().remove(GeneralParameterEnum.DESCRIPCION.getName());
        registro.getCampos().put("TIPOCONTRATO", tipoContrato);
        // </CODIGO_DESARROLLADO>
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
        registro.getCampos().remove("RNUM");
        registro.getCampos().remove("RID");
        registro.getCampos().remove(GeneralParameterEnum.DESCRIPCION.getName());
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
        registro.getCampos().put(GeneralParameterEnum.DESCRIPCION.getName(),
                        descripcion);
        registro.getCampos().put(cIdEtapa, idEtapa);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public int getNumFormulario() {
        return numFormulario;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public void removerCombos() {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());

    }

    @Override
    public void asignarValoresRegistro() {
        registro.getCampos().put(GeneralParameterEnum.DESCRIPCION.getName(),
                        descripcion);
        registro.getCampos().put(cIdEtapa, idEtapa);
    }

}
