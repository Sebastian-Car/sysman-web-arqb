package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.general.enums.TipolicenciasControladorUrlEnum;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
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
 * @author cmanrique
 * 
 * @author eamaya
 * @version 2.0, 30/10/2017, Proceso de Refactoring DSS y cambio de
 * numero de formulario por enum
 * 
 */
@ManagedBean
@ViewScoped
public class TipolicenciasControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private final String strCodigo;

    private RegistroDataModelImpl listaIdConcepto;
    private RegistroDataModelImpl listaIdConceptoE;
    private String auxiliar;

    /**
     * Creates a new instance of TipolicenciasControlador
     */
    public TipolicenciasControlador() {
        super();
        compania = SessionUtil.getCompania();
        strCodigo = "ID_DE_CONCEPTO";
        try {
            numFormulario = GeneralCodigoFormaEnum.TIPOLICENCIAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (SysmanException ex) {
            Logger.getLogger(TipolicenciasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.TIPOS_LICENCIA;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        cargarListaIdConcepto();
        cargarListaIdConceptoE();
        abrirFormulario();

    }

    @Override
    public void reasignarOrigen() {

        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

    }

    public void cargarListaIdConcepto() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TipolicenciasControladorUrlEnum.URL3316
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaIdConcepto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigo);
    }

    public void cargarListaIdConceptoE() {
        listaIdConceptoE = listaIdConcepto;

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

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    public void seleccionarFilaIdConcepto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(strCodigo,
                        registroAux.getCampos().get(strCodigo));
    }

    public void seleccionarFilaIdConceptoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(strCodigo), "")
                        .toString();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void removerCombos() {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());

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
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        registro.getCampos().remove("NOMBRECONCEPTO");
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
        // NO ESTA IMPLEMENTADO
    }

}
