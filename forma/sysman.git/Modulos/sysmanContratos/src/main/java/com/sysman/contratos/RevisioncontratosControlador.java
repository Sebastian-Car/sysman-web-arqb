package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.contratos.enums.RevisioncontratosControladorEnum;
import com.sysman.contratos.enums.RevisioncontratosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.kernel.templates.annotations.Refactoring;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

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
 * @author dcastro
 * @version 1, 20/11/2015
 * @version 2, 10/08/2017 jrodriguezr Se refactoriza el código SQL de
 * las listas para utilizar DSS. También los llamados a funciones,
 * procedimientos y métodos de la clase Acciones a llamados a EJB.
 * Textos al archivo properties. Cambio el numero del formulario al
 * enumerado.
 */
@ManagedBean
@ViewScoped
@Refactoring
public class RevisioncontratosControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private String contrato;
    private String contratos;
    private String numeroAfectado;
    private RegistroDataModelImpl listaContrato;
    private RegistroDataModelImpl listaContratoE;
    private String auxiliar;

    /**
     * Creates a new instance of RevisioncontratosControlador
     */
    public RevisioncontratosControlador() {
        super();
        numFormulario = GeneralCodigoFormaEnum.REVISIONCONTRATOS_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        try {
            validarPermisos();
        }
        catch (SysmanException ex) {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(RevisioncontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void inicializar() {
        // 82085
        tabla = "ORDENDECOMPRA";
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        cargarListaContrato();
        cargarListaContratoE();
        abrirFormulario();
    }

    public void seleccionarFilaContrato(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        contrato = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CONTRATO"), "")
                        .toString();
        contratos = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CLASEORDEN"), "")
                        .toString();
        numeroAfectado = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NUMERO"), "")
                        .toString();
        reasignarOrigen();
    }

    public RegistroDataModelImpl getListaContrato() {
        return listaContrato;
    }

    public void setListaContrato(RegistroDataModelImpl listaContrato) {
        this.listaContrato = listaContrato;
    }

    public RegistroDataModelImpl getListaContratoE() {
        return listaContratoE;
    }

    public void setListaContratoE(RegistroDataModelImpl listaContratoE) {
        this.listaContratoE = listaContratoE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String getContrato() {
        return contrato;
    }

    public void setContrato(String contrato) {
        this.contrato = contrato;
    }

    public void cargarListaContrato() {
        // 82083
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RevisioncontratosControladorUrlEnum.URL3799
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaContrato = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "UNICO");
    }

    public void cargarListaContratoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RevisioncontratosControladorUrlEnum.URL3799
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaContratoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "UNICO");
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
    public void asignarValoresRegistro() {
        // Metodo heredado
    }

    @Override
    public void removerCombos() {
        // Metodo heredado
    }

    @Override
    public void reasignarOrigen() {
        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RevisioncontratosControladorUrlEnum.URL0001
                                                        .getValue());
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(
                        RevisioncontratosControladorEnum.CONTRATO.getValue(),
                        contratos);
        parametrosListado.put(RevisioncontratosControladorEnum.NUMEROAFECTADO
                        .getValue(),
                        numeroAfectado);
    }
}
