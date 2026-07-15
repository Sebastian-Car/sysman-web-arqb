package com.sysman.nomina;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Map;
import java.util.TreeMap;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.TipoEmbargosControladorEnum;
import com.sysman.nomina.enums.TipoEmbargosControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ngomez
 * @version 1, 28/07/2015
 * 
 * @author eamaya
 * @version 2.0,30/10/2017, Proceso de Refactoring DSS,cambio de
 * numero de formulario por enum,cambio de consulta de origen de datos
 * y adicion de columnas a la grilla de datos
 */
@ManagedBean
@ViewScoped

public class TipoEmbargosControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private final String consNomConceptoD;
    private final String consNomConceptoP;

    private RegistroDataModelImpl listaconceptoDescuento;

    private RegistroDataModelImpl listaconceptoDescuentoE;

    private RegistroDataModelImpl listaconceptoPorcentaje;

    private RegistroDataModelImpl listaconceptoPorcentajeE;
    private HashMap<String, Object> rid;

    private String auxiliar;

    /**
     * Creates a new instance of TipoEmbargosControlador
     */
    public TipoEmbargosControlador() {
        super();
        compania = SessionUtil.getCompania();
        consNomConceptoD = "NOMCONCEPTOD";
        consNomConceptoP = "NOMCONCEPTOP";
        try {
            numFormulario = GeneralCodigoFormaEnum.TIPO_EMBARGOS_CONTROLADOR
                            .getCodigo();
            rid = (HashMap<String, Object>) SessionUtil.getFlash().get("rid");
            validarPermisos();
        }
        catch (SysmanException ex) {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(TipoEmbargosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.TIPO_EMBARGO;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        cargarListaconceptoDescuento();
        cargarListaconceptoDescuentoE();
        cargarListaconceptoPorcentaje();
        cargarListaconceptoPorcentajeE();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {

        buscarUrls();

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

    }

    /**
     * 
     * Carga la lista listaconceptoDescuento
     *
     */
    public void cargarListaconceptoDescuento() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TipoEmbargosControladorUrlEnum.URL3430
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(TipoEmbargosControladorEnum.CONCEPTOUNO.getValue(), "700");

        param.put(TipoEmbargosControladorEnum.CONCEPTODOS.getValue(), "747");

        listaconceptoDescuento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.ID_DE_CONCEPTO.getName());
    }

    /**
     * 
     * Carga la lista listaconceptoDescuento
     *
     */
    public void cargarListaconceptoDescuentoE() {
        listaconceptoDescuentoE = listaconceptoDescuento;

    }

    /**
     * 
     * Carga la lista listaconceptoPorcentaje
     *
     */
    public void cargarListaconceptoPorcentaje() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TipoEmbargosControladorUrlEnum.URL5121
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaconceptoPorcentaje = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.ID_DE_CONCEPTO.getName());
    }

    /**
     * 
     * Carga la lista listaconceptoPorcentaje
     *
     */
    public void cargarListaconceptoPorcentajeE() {
        listaconceptoPorcentajeE = listaconceptoPorcentaje;

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaconceptoDescuento
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaconceptoDescuento(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CONCEPTO_DESCUENTO",
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.ID_DE_CONCEPTO
                                                        .getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaconceptoDescuento
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaconceptoDescuentoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.ID_DE_CONCEPTO
                                                        .getName()),
                                        "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaconceptoPorcentaje
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaconceptoPorcentaje(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CONCEPTO_PORCENTAJE",
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.ID_DE_CONCEPTO
                                                        .getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaconceptoPorcentaje
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaconceptoPorcentajeE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.ID_DE_CONCEPTO
                                                        .getName()),
                                        "")
                        .toString();
    }

    /**
     * Retorna la lista listaconceptoDescuento
     * 
     * @return listaconceptoDescuento
     */
    public RegistroDataModelImpl getListaconceptoDescuento() {
        return listaconceptoDescuento;
    }

    /**
     * Asigna la lista listaconceptoDescuento
     * 
     * @param listaconceptoDescuento
     * Variable a asignar en listaconceptoDescuento
     */
    public void setListaconceptoDescuento(
        RegistroDataModelImpl listaconceptoDescuento) {
        this.listaconceptoDescuento = listaconceptoDescuento;
    }

    /**
     * Retorna la lista listaconceptoDescuento
     * 
     * @return listaconceptoDescuento
     */
    public RegistroDataModelImpl getListaconceptoDescuentoE() {
        return listaconceptoDescuentoE;
    }

    /**
     * Asigna la lista listaconceptoDescuento
     * 
     * @param listaconceptoDescuento
     * Variable a asignar en listaconceptoDescuento
     */
    public void setListaconceptoDescuentoE(
        RegistroDataModelImpl listaconceptoDescuentoE) {
        this.listaconceptoDescuentoE = listaconceptoDescuentoE;
    }

    /**
     * Retorna la lista listaconceptoPorcentaje
     * 
     * @return listaconceptoPorcentaje
     */
    public RegistroDataModelImpl getListaconceptoPorcentaje() {
        return listaconceptoPorcentaje;
    }

    /**
     * Asigna la lista listaconceptoPorcentaje
     * 
     * @param listaconceptoPorcentaje
     * Variable a asignar en listaconceptoPorcentaje
     */
    public void setListaconceptoPorcentaje(
        RegistroDataModelImpl listaconceptoPorcentaje) {
        this.listaconceptoPorcentaje = listaconceptoPorcentaje;
    }

    /**
     * Retorna la lista listaconceptoPorcentaje
     * 
     * @return listaconceptoPorcentaje
     */
    public RegistroDataModelImpl getListaconceptoPorcentajeE() {
        return listaconceptoPorcentajeE;
    }

    /**
     * Asigna la lista listaconceptoPorcentaje
     * 
     * @param listaconceptoPorcentaje
     * Variable a asignar en listaconceptoPorcentaje
     */
    public void setListaconceptoPorcentajeE(
        RegistroDataModelImpl listaconceptoPorcentajeE) {
        this.listaconceptoPorcentajeE = listaconceptoPorcentajeE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public Map<String, Object> getRid() {
        return rid;
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
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().remove(consNomConceptoD);
        registro.getCampos().remove(consNomConceptoP);
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(consNomConceptoD);
        registro.getCampos().remove(consNomConceptoP);
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(consNomConceptoD);
        registro.getCampos().remove(consNomConceptoP);
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(consNomConceptoD);
        registro.getCampos().remove("NOMCONCEPTOP");
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(consNomConceptoD);
        registro.getCampos().remove(consNomConceptoP);
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(consNomConceptoD);
        registro.getCampos().remove(consNomConceptoP);
        return true;
        // </CODIGO_DESARROLLADO>
    }

    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { "rid" };
        Object[] valores = { rid };
        SessionUtil.redireccionar("/embargos.sysman", campos, valores);
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void removerCombos() {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());

    }

    @Override
    public void asignarValoresRegistro() {
        // METODO NO IMPLEMENTADO
    }
}
