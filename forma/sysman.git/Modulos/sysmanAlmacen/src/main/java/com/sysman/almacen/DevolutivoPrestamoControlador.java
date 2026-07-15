package com.sysman.almacen;

import com.sysman.almacen.enums.DevolutivoPrestamoControladorEnum;
import com.sysman.almacen.enums.DevolutivoPrestamoControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.FormContinuoService;
import com.sysman.services.RegistroDataModelImpl;

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author OTORRES
 * @version 1, 08/02/2016
 * @version 2, 27/02/2017 modificado por jcrodriguez
 * descripcion:--depuracion del controlador --creacion de dss
 */
@ManagedBean
@ViewScoped
public class DevolutivoPrestamoControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * variable que almacena la compa�ia
     */
    private final String compania;
    /**
     * variable que almacena el indice
     */
    private int indice;
    /**
     * variable que almacena la lista serie padre
     */
    private RegistroDataModelImpl listaSeriePadre;
    /**
     * variable que almacena la lista serie padre
     */
    private RegistroDataModelImpl listaSeriePadreE;
    /**
     * variable auxilizar que almacena una cadena
     */
    private String auxiliar;
    /**
     * variable que almacena un elemento
     **/
    private String elemento;

    /**
     * Creates a new instance of DevolutivoPrestamoControlador
     */
    public DevolutivoPrestamoControlador() {
        super();
        numFormulario = GeneralCodigoFormaEnum.DEVOLUTIVO_PRESTAMO_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        try {
            validarPermisos();
        }
        catch (SysmanException ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * metodo que se llama al inicializar el formulario
     */
    @PostConstruct
    public void inicializar() {
        tabla = DevolutivoPrestamoControladorEnum.TABLA.getValue();
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        cargarListaSeriePadre();
        cargarListaSeriePadreE();
        abrirFormulario();
    }

    /***
     * metodo reaisgnarOrigen
     */
    @Override
    public void reasignarOrigen() {
        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        DevolutivoPrestamoControladorUrlEnum.URL4625
                                        .getValue());
        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DevolutivoPrestamoControladorUrlEnum.URL4626
                                                        .getValue());
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    /**
     * metodo que carga la lista serie
     */
    public void cargarListaSeriePadre() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DevolutivoPrestamoControladorUrlEnum.URL4001
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaSeriePadre = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.SERIE.getName());
    }

    /**
     * metodo que carga la lista serie
     */
    public void cargarListaSeriePadreE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DevolutivoPrestamoControladorUrlEnum.URL4682
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaSeriePadreE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.SERIE.getName());
    }

    /**
     * metodo que se llama al cambiar la seria
     * 
     * @param rowNum
     */
    public void cambiarSeriePadreC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(DevolutivoPrestamoControladorEnum.ELEMENTO_PADRE
                                        .getValue(), elemento);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * metodo que se llama al seleccionar una fila o registro de un
     * combo grande
     * 
     * @param event
     */
    public void seleccionarFilaSeriePadre(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(DevolutivoPrestamoControladorEnum.SERIE_PADRE
                        .getValue(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.SERIE.getName()));
    }

    /**
     * metodo que se llama al seleccionar una fila o registro de un
     * combo grande
     * 
     * @param event
     */
    public void seleccionarFilaSeriePadreE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = String.valueOf(registroAux.getCampos()
                        .get(GeneralParameterEnum.SERIE.getName()));
        elemento = String.valueOf(registroAux.getCampos().get(
                        DevolutivoPrestamoControladorEnum.ELEMENTO.getValue()));
    }

    /**
     * metodo que se llama al abrir el formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * metodo que cancela la edicion de la grilla
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    /**
     * metodo heredado del bean padre
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * metodo heredado del bean padre
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * metodo heredado del bean padre
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(
                        DevolutivoPrestamoControladorEnum.ELEMENTO.getValue());
        registro.getCampos().remove(GeneralParameterEnum.SERIE.getName());
        registro.getCampos().remove(
                        DevolutivoPrestamoControladorEnum.PLACA.getValue());
        registro.getCampos()
                        .remove(DevolutivoPrestamoControladorEnum.DESCRIPCION
                                        .getValue());
        registro.getCampos()
                        .remove(DevolutivoPrestamoControladorEnum.NOMBRELARGO
                                        .getValue());
        registro.getCampos()
                        .remove(DevolutivoPrestamoControladorEnum.DEPENDENCIA
                                        .getValue());
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * metodo heredado del bean padre
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * metodo heredado del bean padre
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * metodo heredado del bean padre
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    public void activarEdicion(Registro registro) {
        /*
         * registro es el que se selecciona al activar la edicion
         * viene de la forma
         */
        indice = listaInicial.getRowIndex();
    }

    @Override
    public void removerCombos() {
        // heredado del bean base
    }

    @Override
    public void asignarValoresRegistro() {
        // heredado del bean base
    }

    /**
     * metodos get y set
     */
    @Override
    public FormContinuoService getService() {
        return service;
    }

    @Override
    public void setService(FormContinuoService service) {
        this.service = service;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public RegistroDataModelImpl getListaSeriePadre() {
        return listaSeriePadre;
    }

    public void setListaSeriePadre(RegistroDataModelImpl listaSeriePadre) {
        this.listaSeriePadre = listaSeriePadre;
    }

    public RegistroDataModelImpl getListaSeriePadreE() {
        return listaSeriePadreE;
    }

    public void setListaSeriePadreE(RegistroDataModelImpl listaSeriePadreE) {
        this.listaSeriePadreE = listaSeriePadreE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

}
