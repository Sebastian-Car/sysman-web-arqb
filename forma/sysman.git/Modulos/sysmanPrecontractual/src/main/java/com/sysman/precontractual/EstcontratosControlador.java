package com.sysman.precontractual;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.precontractual.enums.EstcontratosControladorEnum;
import com.sysman.precontractual.enums.EstcontratosControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author dcastro
 * @version 1, 01/12/2015
 * @modified jguerrero
 * @version 2. 24/08/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar
 */
@ManagedBean
@ViewScoped

public class EstcontratosControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;

    private RegistroDataModelImpl listaCodigoContrato;
    private RegistroDataModelImpl listaCodigoContratoE;
    private String auxiliar;

    /**
     * Creates a new instance of EstcontratosControlador
     */
    public EstcontratosControlador() {

        super();
        compania = SessionUtil.getCompania();
        try {

            numFormulario = GeneralCodigoFormaEnum.ESTCONTRATOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    @PostConstruct
    public void inicializar() {

        enumBase = GenericUrlEnum.ES_TIPOCONTRATO;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        cargarlistaCodigoContrato();
        cargarlistaCodigoContratoE();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    public RegistroDataModelImpl getlistaCodigoContrato() {
        return listaCodigoContrato;
    }

    public void setlistaCodigoContrato(
        RegistroDataModelImpl listaCodigoContrato) {
        this.listaCodigoContrato = listaCodigoContrato;
    }

    public RegistroDataModelImpl getlistaCodigoContratoE() {
        return listaCodigoContratoE;
    }

    public void setlistaCodigoContratoE(
        RegistroDataModelImpl listaCodigoContratoE) {
        this.listaCodigoContratoE = listaCodigoContratoE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public void cargarlistaCodigoContrato() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EstcontratosControladorUrlEnum.URL2978
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCodigoContrato = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.TIPOCONTRATO.getName());

    }

    public void cargarlistaCodigoContratoE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EstcontratosControladorUrlEnum.URL2978
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCodigoContratoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.TIPOCONTRATO.getName());

    }

    public void seleccionarFilaCodigoContrato(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(
                        EstcontratosControladorEnum.CODIGOCONTRATO.getValue(),
                        retornarString(registroAux,
                                        GeneralParameterEnum.TIPOCONTRATO
                                                        .getName()));
    }

    public void seleccionarFilaCodigoContratoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = retornarString(registroAux,
                        GeneralParameterEnum.TIPOCONTRATO.getName());
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
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
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
    }

    @Override
    public void asignarValoresRegistro() {
        // Metodo heredado del bean base

    }

    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();

    }
}
