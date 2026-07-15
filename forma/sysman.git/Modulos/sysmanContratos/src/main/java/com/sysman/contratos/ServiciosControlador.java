package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.contratos.enums.ServiciosControladorEnum;
import com.sysman.contratos.enums.ServiciosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;

import java.util.HashMap;
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
 * @author ybecerra
 * @version 1, 18/09/2015
 * 
 * @author asana
 * @version 2, 11/08/2017 Se realiza refactoring controlador
 */
@ManagedBean
@ViewScoped
public class ServiciosControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;

    private RegistroDataModelImpl listacmbElemento;
    private RegistroDataModelImpl listacmbElementoE;
    private String auxiliar;
    private List<Registro> listaUnidad;

    /**
     * Creates a new instance of ServiciosControlador
     */
    public ServiciosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.SERVICIOS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (SysmanException ex) {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(ServiciosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    @PostConstruct
    public void inicializar() {
        tabla = GenericUrlEnum.INVENTARIO.getTable();
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        cargarListaUnidad();
        cargarListacmbElemento();
        cargarListacmbElementoE();
        abrirFormulario();
    }

    public List<Registro> getListaUnidad() {
        return listaUnidad;
    }

    public void setListaUnidad(List<Registro> listaUnidad) {
        this.listaUnidad = listaUnidad;
    }

    public RegistroDataModelImpl getListacmbElemento() {
        return listacmbElemento;
    }

    public void setListacmbElemento(RegistroDataModelImpl listacmbElemento) {
        this.listacmbElemento = listacmbElemento;
    }

    public RegistroDataModelImpl getListacmbElementoE() {
        return listacmbElementoE;
    }

    public void setListacmbElementoE(RegistroDataModelImpl listacmbElementoE) {
        this.listacmbElementoE = listacmbElementoE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public void cargarListaUnidad() {

        try {
            listaUnidad = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ServiciosControladorUrlEnum.URL3036
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListacmbElemento() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ServiciosControladorUrlEnum.URL3320
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listacmbElemento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, ServiciosControladorEnum.PARAM1.getValue());
    }

    public void cargarListacmbElementoE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ServiciosControladorUrlEnum.URL3320
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listacmbElementoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, ServiciosControladorEnum.PARAM1.getValue());
    }

    public void onRowSelectcmbElemento(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(ServiciosControladorEnum.PARAM1.getValue(),
                        registroAux.getCampos()
                                        .get(ServiciosControladorEnum.PARAM1
                                                        .getValue()));
    }

    public void onRowSelectcmbElementoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos()
                        .get(ServiciosControladorEnum.PARAM1.getValue());
    }

    public void cambiarNombreCortoC(int rowNum) {
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put("NOMBRELARGO", listaInicial.getDatasource()
                                        .get(rowNum % 10).getCampos()
                                        .get("NOMBRECORTO"));
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
        return true;
        // </CODIGO_DESARROLLADO>
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

        registro.getCampos().remove("CODIGOELEMENTO");
        registro.getCampos().remove("TIENEMOVIMIENTO");
        registro.getCampos().remove("MEDIDA");
        registro.getCampos().remove("NOMBRELARGO");
        registro.getCampos().remove("COMPANIA");
        registro.getCampos().remove("UNIDAD");
        registro.getCampos().remove("PREDECESOR");
        registro.getCampos().remove("CODIGOCUBS");
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>

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
        // METODO_NO_IMPLEMENTADO
    }

    @Override
    public void reasignarOrigen() {

        urlListado = UrlServiceUtil.getUrlBeanById(
                        ServiciosControladorUrlEnum.URL4390.getValue());

        urlActualizacion = UrlServiceUtil.getUrlBeanById(
                        ServiciosControladorUrlEnum.URL3037.getValue());

        urlEliminacion = UrlServiceUtil.getUrlBeanById(
                        ServiciosControladorUrlEnum.URL3038.getValue());

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

    }

    @Override
    public void asignarValoresRegistro() {
        // METODO_NO_IMPLEMENTADO
    }

}
