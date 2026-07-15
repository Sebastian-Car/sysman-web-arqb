package com.sysman.general;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contratos.ejb.EjbContratosUnoGeneralRemote;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.ImportarItemsControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author jrodriguezr
 * @version 1, 15/01/2016
 * @version 2, 04/04/2017 Se refactoriza el codigo SQL de las listas
 * para utilizar dss. Se pasa a PLSQL el procedimiento en el botón
 * Importar.
 * 
 * @version 3, 12/06/2017, <strong>pespitia</strong>:<br>
 * Reemplazar numero del formulario por enumerado.
 */
@ManagedBean
@ViewScoped
public class ImportarItemsControlador extends BeanBaseModal {

    private final String compania;
    private final String usuario;
    private final String tipoContratoCons;
    private final String mensajeAlertCons;

    private String confirmacionpre;
    private String tipo;
    private String tipoContrato;
    private String objetoContrato;
    private String estudioPrevio;
    private String numProceso;
    private RegistroDataModelImpl listaConfirmacionpre;
    private RegistroDataModelImpl listaTIpo;
    private String claseOrden;
    private String numeroOrden;
    private String confirmar;
    private boolean porEstudio;
    private boolean bloqueado;

    private RegistroDataModelImpl listaEstudioPrevio;

    @EJB
    private EjbContratosUnoGeneralRemote ejbContratosUnoGeneral;
	private Map<String, Object> parametroswf;
	private String modulo;

    /**
     * Creates a new instance of ImportarItemsControlador
     */
    public ImportarItemsControlador() {
        super();
        compania = SessionUtil.getCompania();
        usuario = SessionUtil.getUser().getCodigo();
        tipoContratoCons = "TIPOCONTRATO";
        mensajeAlertCons = "MSM_TRANS_INTERRUMPIDA";

        try {
        	
        	parametroswf = (Map<String,Object>) SessionUtil.getSessionVarContainer("parametroswf");
        	if(parametroswf != null) {
        		SessionUtil.setSessionVar("modulo", "9");
        	}
        	modulo = SessionUtil.getModulo();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                claseOrden = parametrosEntrada.get("claseOrden").toString();
                numeroOrden = parametrosEntrada.get("numeroOrden").toString();
                confirmar = parametrosEntrada.get("confirmar").toString();
            }

            // 372
            numFormulario = GeneralCodigoFormaEnum.IMPORTAR_ITEMS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(ImportarItemsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaTIpo();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaconfirmacionpre() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ImportarItemsControladorUrlEnum.URL3293
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.TIPOCONTRATO.getName(), tipo);

        listaConfirmacionpre = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CONSECUTIVO.getName());
    }

    public void cargarListaTIpo() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ImportarItemsControladorUrlEnum.URL5417
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTIpo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, tipoContratoCons);
    }

    /**
     * 
     * Carga la lista listaEstudioPrevio
     *
     */
    public void cargarListaEstudioPrevio() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ImportarItemsControladorUrlEnum.URL7548
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.TIPOCONTRATO.getName(), tipo);

        listaEstudioPrevio = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CONSECUTIVO.getName());
    }

    public void oprimirComando11() {
        // <CODIGO_DESARROLLADO>
        try {
            if (Boolean.parseBoolean(confirmar)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2066"));
                return;
            }
            confirmacionpre = SysmanFunciones
                            .validarVariableVacio(confirmacionpre)
                                ? "0"
                                : confirmacionpre;
            if ("0".equals(confirmacionpre)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2067"));
                return;
            }

            ejbContratosUnoGeneral.importarPrecontractual(compania,
                            Long.parseLong(numeroOrden),
                            claseOrden, Long.parseLong(estudioPrevio), usuario,
                            numProceso);

            ejbContratosUnoGeneral.actualizarDetallesActividades(compania,
                            Long.parseLong(estudioPrevio),
                            Long.parseLong(numeroOrden), claseOrden, usuario);

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));
        }
        catch (NumberFormatException | SystemException ex) {
            Logger.getLogger(ImportarItemsControlador.class
                            .getName()).log(Level.SEVERE, null, ex);

            JsfUtil.agregarMensajeAlerta(idioma.getString(mensajeAlertCons)
                + ex.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirSalir() {
        RequestContext.getCurrentInstance().closeDialog(this);
    }

    /**
     * Metodo ejecutado al cambiar el control PorEstudio
     * 
     */
    public void cambiarPorEstudio() {
        // <CODIGO_DESARROLLADO>
        bloqueado = porEstudio;
        cargarListaEstudioPrevio();
        cargarListaconfirmacionpre();
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaConfirmacionpre(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        confirmacionpre = registroAux.getCampos()
                        .get(GeneralParameterEnum.CONSECUTIVO.getName())
                        .toString();
        tipoContrato = SysmanFunciones
                        .nvl(registroAux.getCampos().get(tipoContratoCons), "")
                        .toString();
        objetoContrato = SysmanFunciones
                        .nvl(registroAux.getCampos().get("OBJETO_CONTRATO"), "")
                        .toString();
        estudioPrevio = SysmanFunciones
                        .nvl(registroAux.getCampos().get("ESTUDIOPREVIO"), "")
                        .toString();
        numProceso = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NUM_PROCESO"), "")
                        .toString();
    }

    public void seleccionarFilaTIpo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipo = SysmanFunciones
                        .nvl(registroAux.getCampos().get(tipoContratoCons), "")
                        .toString();
        cargarListaconfirmacionpre();
        cargarListaEstudioPrevio();
        confirmacionpre = null;
        tipoContrato = null;
        objetoContrato = null;
        estudioPrevio = null;
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaEstudioPrevio
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaEstudioPrevio(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        confirmacionpre = registroAux.getCampos()
                        .get(GeneralParameterEnum.CONSECUTIVO.getName())
                        .toString();
        tipoContrato = SysmanFunciones
                        .nvl(registroAux.getCampos().get(tipoContratoCons), "")
                        .toString();
        objetoContrato = SysmanFunciones
                        .nvl(registroAux.getCampos().get("OBJETO_CONTRATO"), "")
                        .toString();
        estudioPrevio = SysmanFunciones
                        .nvl(registroAux.getCampos().get("COD_ESTUDIO"), "")
                        .toString();
        numProceso = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NUM_PROCESO"), "")
                        .toString();
    }

    public String getConfirmacionpre() {
        return confirmacionpre;
    }

    public void setConfirmacionpre(String confirmacionpre) {
        this.confirmacionpre = confirmacionpre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    public String getObjetoContrato() {
        return objetoContrato;
    }

    public void setObjetoContrato(String objetoContrato) {
        this.objetoContrato = objetoContrato;
    }

    public String getEstudioPrevio() {
        return estudioPrevio;
    }

    public void setEstudioPrevio(String estudioPrevio) {
        this.estudioPrevio = estudioPrevio;
    }

    public RegistroDataModelImpl getListaConfirmacionpre() {
        return listaConfirmacionpre;
    }

    public void setListaConfirmacionpre(
        RegistroDataModelImpl listaConfirmacionpre) {
        this.listaConfirmacionpre = listaConfirmacionpre;
    }

    public String getConfirmar() {
        return confirmar;
    }

    public void setConfirmar(String confirmar) {
        this.confirmar = confirmar;
    }

    public String getClaseOrden() {
        return claseOrden;
    }

    public void setClaseOrden(String claseOrden) {
        this.claseOrden = claseOrden;
    }

    public String getNumeroOrden() {
        return numeroOrden;
    }

    public void setNumeroOrden(String numeroOrden) {
        this.numeroOrden = numeroOrden;
    }

    public RegistroDataModelImpl getListaTIpo() {
        return listaTIpo;
    }

    public void setListaTIpo(RegistroDataModelImpl listaTIpo) {
        this.listaTIpo = listaTIpo;
    }

    public String getNumProceso() {
        return numProceso;
    }

    public void setNumProceso(String numProceso) {
        this.numProceso = numProceso;
    }

    @Override
    public int getNumFormulario() {
        return numFormulario;
    }

    public boolean isPorEstudio() {
        return porEstudio;
    }

    public void setPorEstudio(boolean porEstudio) {
        this.porEstudio = porEstudio;
    }

    public RegistroDataModelImpl getListaEstudioPrevio() {
        return listaEstudioPrevio;
    }

    public void setListaEstudioPrevio(
        RegistroDataModelImpl listaEstudioPrevio) {
        this.listaEstudioPrevio = listaEstudioPrevio;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

}
