package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.general.enums.TipoincapacidadsControladorEnum;
import com.sysman.general.enums.TipoincapacidadsControladorUrlEnum;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
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
 * @author cmanrique
 * 
 * @author jcrodriguez,Refactoring y depuracion
 * @version 2,30/10/2017
 */
@ManagedBean
@ViewScoped

public class TipoincapacidadsControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private RegistroDataModelImpl listaIdConcepto;
    private RegistroDataModelImpl listaIdConceptoE;
    private String auxiliar;
    private String idConcepto;
    private int indice;

    // Indica si muestra el campo NIE 126
    private boolean mostarCune;

    /**
     * Creates a new instance of TipoincapacidadsControlador
     */
    public TipoincapacidadsControlador() {
        super();
        compania = SessionUtil.getCompania();

        try {
            numFormulario = GeneralCodigoFormaEnum.TIPOINCAPACIDADS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (SysmanException ex) {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(TipoincapacidadsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.TIPO_INCAPACIDAD;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        cargarListaIdConcepto();
        cargarListaIdConceptoE();
        abrirFormulario();
        mostarCune = TipoincapacidadsControladorEnum.MENUNIE.getValue()
                        .equals(SessionUtil.getMenuActual());
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    public void cargarListaIdConcepto() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TipoincapacidadsControladorUrlEnum.URL4923
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaIdConcepto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        TipoincapacidadsControladorEnum.ID_DE_CONCEPTO
                                        .getValue());

    }

    public void cargarListaIdConceptoE() {

        listaIdConceptoE = listaIdConcepto;

    }

    public void seleccionarFilaIdConcepto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(TipoincapacidadsControladorEnum.ID_DE_CONCEPTO
                        .getValue(),
                        registroAux.getCampos()
                                        .get(TipoincapacidadsControladorEnum.ID_DE_CONCEPTO
                                                        .getValue()));
        registro.getCampos()
                        .put(TipoincapacidadsControladorEnum.NOMBRECONCEPTO
                                        .getValue(),
                                        registroAux.getCampos().get(
                                                        TipoincapacidadsControladorEnum.NOMBRE_CONCEPTO
                                                                        .getValue()));
    }

    public void seleccionarFilaIdConceptoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(TipoincapacidadsControladorEnum.NOMBRE_CONCEPTO
                                        .getValue()),
                        "")
                        .toString();
        idConcepto = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(TipoincapacidadsControladorEnum.ID_DE_CONCEPTO
                                        .getValue()),
                        "")
                        .toString();
    }

    @Override
    public void abrirFormulario() {
        // heredado del bean base
    }

    @Override
    public void removerCombos() {
        // METODO NO IMPLEMENTADO
        registro.getCampos().put(TipoincapacidadsControladorEnum.ID_DE_CONCEPTO
                        .getValue(),
                        SysmanFunciones.validarVariableVacio(idConcepto)
                            ? registro.getCampos()
                                            .get(TipoincapacidadsControladorEnum.ID_DE_CONCEPTO
                                                            .getValue())
                            : idConcepto);
    }

    @Override
    public boolean insertarAntes() {
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        return true;
    }

    @Override
    public boolean insertarDespues() {
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        registro.getCampos()
                        .remove(TipoincapacidadsControladorEnum.NOMBRECONCEPTO
                                        .getValue());
        registro.getCampos()
                        .remove(TipoincapacidadsControladorEnum.NOMBRE_CONCEPTO
                                        .getValue());

        registro.getCampos()
                        .remove(TipoincapacidadsControladorEnum.DESCNIE126
                                        .getValue());
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

    @Override
    public void asignarValoresRegistro() {
        // METODO NO IMPLEMENTADO
    }

    public RegistroDataModelImpl getListaIdConcepto() {
        return listaIdConcepto;
    }

    public void setListaIdConcepto(RegistroDataModelImpl listaIdConcepto) {
        this.listaIdConcepto = listaIdConcepto;
    }

    public RegistroDataModelImpl getListaIdConceptoE() {
        return listaIdConceptoE;
    }

    public void setListaIdConceptoE(RegistroDataModelImpl listaIdConceptoE) {
        this.listaIdConceptoE = listaIdConceptoE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String getIdConcepto() {
        return idConcepto;
    }

    public void setIdConcepto(String idConcepto) {
        this.idConcepto = idConcepto;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public boolean isMostarCune() {
        return mostarCune;
    }

    public void setMostarCune(boolean mostarCune) {
        this.mostarCune = mostarCune;
    }

}
