package com.sysman.predial;

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
import com.sysman.predial.enums.TarifasespecialesControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
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
 * @author acaceres
 * @version 1, 24/05/2016
 *
 * @author lcortes
 * @version 2, 19,24/07/2017. Refactorizacion para usar dss en el
 * controlador.
 */
@ManagedBean
@ViewScoped
public class TarifasespecialesControlador extends BeanBaseContinuoAcmeImpl {
    private final String compania;
    private final String codigoCons;
    private final String conceptoCons;
    private int anio;
    private int indice;
    private List<Registro> listaAno;
    private RegistroDataModelImpl listaConcepto;
    private RegistroDataModelImpl listaConceptoE;
    private RegistroDataModelImpl listaPredio;
    private RegistroDataModelImpl listaPredioE;
    private String auxiliar;

    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of TarifasespecialesControlador
     */
    public TarifasespecialesControlador() {
        super();
        compania = SessionUtil.getCompania();
        codigoCons = GeneralParameterEnum.CODIGO.getName();
        conceptoCons = GeneralParameterEnum.CONCEPTO.getName();
        try {
            numFormulario = GeneralCodigoFormaEnum.TARIFASESPECIALES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(TarifasespecialesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.IP_TARIFAS_ESPECIALES;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        abrirFormulario();
        cargarListaAno();
        cargarListaConcepto();
        cargarListaConceptoE();
        cargarListaPredio();
        cargarListaPredioE();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaAno() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TarifasespecialesControladorUrlEnum.URL3144
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaConcepto() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TarifasespecialesControladorUrlEnum.URL3483
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaConcepto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    public void cargarListaConceptoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TarifasespecialesControladorUrlEnum.URL4236
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaConceptoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    public void cargarListaPredio() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TarifasespecialesControladorUrlEnum.URL5037
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaPredio = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    public void cargarListaPredioE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TarifasespecialesControladorUrlEnum.URL5929
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaPredioE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarAno() {
        anio = Integer.valueOf(registro.getCampos()
                        .get(GeneralParameterEnum.ANO.getName()).toString());
        cargarListaConcepto();
    }

    public void cambiarAnoC(int rowNum) {
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(conceptoCons, null);
        anio = Integer.valueOf(listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos().get(GeneralParameterEnum.ANO.getName())
                        .toString());
        cargarListaConceptoE();
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>

    public void seleccionarFilaConcepto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(conceptoCons,
                        registroAux.getCampos().get(codigoCons));
    }

    public void seleccionarFilaConceptoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(codigoCons).toString();
    }

    public void seleccionarFilaPredio(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.PREDIO.getName(),
                        registroAux.getCampos().get(codigoCons));
    }

    public void seleccionarFilaPredioE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(codigoCons).toString();
    }

    // </METODOS_COMBOS_GRANDES>
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
        registro.getCampos().put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);

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
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        if (SysmanFunciones.validarVariableVacio(registro.getCampos()
                        .get(GeneralParameterEnum.ANO.getName()).toString())
            || SysmanFunciones.validarVariableVacio(
                            registro.getCampos().get("PORCENTAJE").toString())
            || SysmanFunciones.validarVariableVacio(
                            registro.getCampos().get(conceptoCons)
                                            .toString())) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB724"));
            return false;

        }

        if (SysmanFunciones.validarVariableVacio(
                        registro.getCampos().get(
                                        GeneralParameterEnum.PREDIO.getName())
                                        .toString())
            || SysmanFunciones.validarVariableVacio(registro.getCampos()
                            .get("FECHALIMITE").toString())) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB724"));
            return false;
        }
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

    public void activarEdicion(Registro reg) {
        indice = listaInicial.getRowIndex();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TarifasespecialesControladorUrlEnum.URL11200
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        listaConceptoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    @Override
    public void removerCombos() {
        // NO ESTA IMPLEMENTADO
    }

    @Override
    public void asignarValoresRegistro() {
        // NO ESTA IMPLEMENTADO
    }

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    public RegistroDataModelImpl getListaConcepto() {
        return listaConcepto;
    }

    public void setListaConcepto(RegistroDataModelImpl listaConcepto) {
        this.listaConcepto = listaConcepto;
    }

    public RegistroDataModelImpl getListaConceptoE() {
        return listaConceptoE;
    }

    public void setListaConceptoE(RegistroDataModelImpl listaConceptoE) {
        this.listaConceptoE = listaConceptoE;
    }

    public RegistroDataModelImpl getListaPredio() {
        return listaPredio;
    }

    public void setListaPredio(RegistroDataModelImpl listaPredio) {
        this.listaPredio = listaPredio;
    }

    public RegistroDataModelImpl getListaPredioE() {
        return listaPredioE;
    }

    public void setListaPredioE(RegistroDataModelImpl listaPredioE) {
        this.listaPredioE = listaPredioE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }
}
