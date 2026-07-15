package com.sysman.almacen;

import com.sysman.almacen.enums.SdentdevolutivoactivosControladorEnum;
import com.sysman.almacen.enums.SdentdevolutivoactivosControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
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
import com.sysman.services.FormContinuoService;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author dmaldonado
 * @version 1, 05/02/2016
 * @version 2, 09/05/2017 modificado por jcrodriguez
 * DESCRIPCION:*Depuracion del controlador *generacion de
 * refactoring=>creacion de dss
 * 
 */
@ManagedBean
@ViewScoped
public class SdentdevolutivoactivosControlador
                extends BeanBaseContinuoAcmeImpl {
    /**
     * variable que alamcena la compa�ia
     */
    private final String compania;
    /**
     * variable que almacena un indice
     */
    private int indice;
    /**
     * variable que almacena la lista de placas
     */
    private RegistroDataModelImpl listaPlaca;
    /**
     * variable que almacena la lista de placas
     */
    private RegistroDataModelImpl listaPlacaE;
    /**
     * variable de tipo cadena auxiliar
     */
    private String auxiliar;
    /**
     * variable que almacena el listadi de activos niff final
     */
    private List<Registro> listaActivoNIIFFinal;
    /**
     * variable que alamcena el consecutivo
     */
    private String consecutivoCTA;
    /**
     * variable que alamcena el parametro tipo movimiento
     */
    private String parametroTipoMov;
    /**
     * variable que alamcena el tipo movimiento cuenta
     */
    private String tipoMovimientoCTA;
    /**
     * variable que alamcena el elemento aux
     */
    private String elementoAux;
    /**
     * variable que alamcena el nombre
     */
    private String nombreAux;
    /**
     * variable que alamcena la descripcion
     */
    private String descripcionAux;
    /**
     * variable que alamcena el nif tipo aux
     */
    private String niifTipoAux;
    /**
     * variable que alamcena el nombre tipo activo
     */
    private Object nombreTipoActivo;
    /**
     * variable que alamcena el nif valor base
     */
    private String niifValorBase;
    /**
     * variable que alamcena el nif valor total
     */
    private String niifValorTotal;

    /**
     * Creates a new instance of SdentdevolutivoactivosControlador
     */
    public SdentdevolutivoactivosControlador() {
        super();
        compania = SessionUtil.getCompania();

        try {
            numFormulario = GeneralCodigoFormaEnum.SDENTDEVOLUTIVOACTIVOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                consecutivoCTA = SysmanFunciones.nvl(parametrosEntrada
                                .get("consecutivoCTA"), "").toString();
                parametroTipoMov = SysmanFunciones.nvl(parametrosEntrada
                                .get("parametroTipoMov"), "").toString();
                tipoMovimientoCTA = SysmanFunciones.nvl(parametrosEntrada
                                .get("tipoMovimientoCTA"), "").toString();
            }
            else {
                SessionUtil.redireccionarMenuPermisos();
            }
            SessionUtil.cleanFlash();
        }
        catch (SysmanException ex) {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(SdentdevolutivoactivosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    /**
     * metodo, el cual se llama al inicializar el formulario
     */
    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.D_CAMBIOS_TIPOACTIVO;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        cargarListaActivoNIIFFinal();
        cargarListaPlaca();
        cargarListaPlacaE();
        abrirFormulario();
    }

    /**
     * metodo que se llama para cargar los datos en la grilla del
     * formulario continuo
     */
    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado
                        .put(SdentdevolutivoactivosControladorEnum.CONSECUTIVOCTA
                                        .getValue(), consecutivoCTA);
        parametrosListado
                        .put(SdentdevolutivoactivosControladorEnum.PARAMETROTIPOMOV
                                        .getValue(), parametroTipoMov);
        parametrosListado
                        .put(SdentdevolutivoactivosControladorEnum.TIPOMOVIMIENTOCTA
                                        .getValue(), tipoMovimientoCTA);

    }

    /**
     * metodo que se llama al abrir el formulario
     */
    @Override
    public void abrirFormulario() {
        // heredado del bean padre
    }

    /**
     * metodo que se llama para cargar la lista de nif final
     */
    public void cargarListaActivoNIIFFinal() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaActivoNIIFFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SdentdevolutivoactivosControladorUrlEnum.URL5842
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * metodo,el cual se llama para cargar la lista de placas
     */
    public void cargarListaPlaca() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SdentdevolutivoactivosControladorUrlEnum.URL6418
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaPlaca = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.SERIE.getName());

    }

    /**
     * metodo, el cual se llama para cargar la lista de placas
     */
    public void cargarListaPlacaE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SdentdevolutivoactivosControladorUrlEnum.URL6418
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaPlacaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.SERIE.getName());
    }

    /**
     * metodo que se llama al cambiar la placa
     * 
     * @param rowNum
     */
    public void cambiarPlacaC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(GeneralParameterEnum.SERIE.getName(), auxiliar);
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(GeneralParameterEnum.ELEMENTO.getName(),
                                        elementoAux);
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(SdentdevolutivoactivosControladorEnum.NOMBRELARGO
                                        .getValue(), nombreAux);
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(GeneralParameterEnum.DESCRIPCION.getName(),
                                        descripcionAux);
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(SdentdevolutivoactivosControladorEnum.TIPOACTIVO_ANT
                                        .getValue(), niifTipoAux);
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(SdentdevolutivoactivosControladorEnum.NOMBRETIPOACT
                                        .getValue(), nombreTipoActivo);
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(SdentdevolutivoactivosControladorEnum.VALORBASE
                                        .getValue(), niifValorBase);
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(SdentdevolutivoactivosControladorEnum.VALORTOTAL
                                        .getValue(), niifValorTotal);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * metodo que se llama al seleccionar una fila en el combo grande
     * 
     * @param rowNum
     */
    public void seleccionarFilaPlaca(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.SERIE.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.SERIE.getName()));
        registro.getCampos().put(GeneralParameterEnum.ELEMENTO.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.ELEMENTO
                                                        .getName()));
        registro.getCampos().put(GeneralParameterEnum.DESCRIPCION.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.DESCRIPCION
                                                        .getName()));
        registro.getCampos()
                        .put(SdentdevolutivoactivosControladorEnum.NOMBRELARGO
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get(SdentdevolutivoactivosControladorEnum.NOMBRELARGO
                                                                        .getValue()));
        registro.getCampos().put(GeneralParameterEnum.FECHA.getName(),
                        new Date());
        registro.getCampos().put(
                        SdentdevolutivoactivosControladorEnum.HORA.getValue(),
                        new Date());
        registro.getCampos()
                        .put(SdentdevolutivoactivosControladorEnum.NOMBRETIPOACT
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get(SdentdevolutivoactivosControladorEnum.NOMBRE_TIPOACTIVO
                                                                        .getValue()));
        registro.getCampos()
                        .put(SdentdevolutivoactivosControladorEnum.TIPOACTIVO_ANT
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get(SdentdevolutivoactivosControladorEnum.TIPOACTIVO
                                                                        .getValue()));
        registro.getCampos().put(SdentdevolutivoactivosControladorEnum.VALORBASE
                        .getValue(),
                        registroAux.getCampos()
                                        .get(SdentdevolutivoactivosControladorEnum.VALORBASE
                                                        .getValue()));
        registro.getCampos()
                        .put(SdentdevolutivoactivosControladorEnum.VALORTOTAL
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get(SdentdevolutivoactivosControladorEnum.VALORTOTAL
                                                                        .getValue()));
    }

    /**
     * metodo que se llama al seleccionar una fila en el combo grande
     * 
     * @param rowNum
     */
    public void seleccionarFilaPlacaE(SelectEvent event) {

        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.SERIE
                                                        .getName()),
                                        "")
                        .toString();
        elementoAux = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.ELEMENTO
                                                        .getName()),
                                        "")
                        .toString();
        nombreAux = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(SdentdevolutivoactivosControladorEnum.NOMBRELARGO
                                                        .getValue()),
                                        "")
                        .toString();
        descripcionAux = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.DESCRIPCION
                                                        .getName()),
                                        "")
                        .toString();
        nombreTipoActivo = registroAux.getCampos()
                        .get(SdentdevolutivoactivosControladorEnum.NOMBRE_TIPOACTIVO
                                        .getValue());
        niifTipoAux = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(SdentdevolutivoactivosControladorEnum.TIPOACTIVO
                                                        .getValue()),
                                        "")
                        .toString();
        niifValorBase = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(SdentdevolutivoactivosControladorEnum.VALORBASE
                                        .getValue()),
                        "")
                        .toString();
        niifValorTotal = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(SdentdevolutivoactivosControladorEnum.VALORTOTAL
                                        .getValue()),
                        "")
                        .toString();
    }

    /**
     * metodo que cancela la edicon de un registro o fila
     * 
     * @param event
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    /**
     * metodo heredado del bean padre
     * 
     * @return
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        UrlBean url = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        SdentdevolutivoactivosControladorUrlEnum.URL6426
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(SdentdevolutivoactivosControladorEnum.TIPOMOVIMIENTOCTA
                        .getValue(), tipoMovimientoCTA);
        param.put(SdentdevolutivoactivosControladorEnum.CONSECUTIVOCTA
                        .getValue(), consecutivoCTA);
        Registro reg;
        try {
            reg = RegistroConverter.toRegistro(
                            requestManager.get(url.getUrl(), param));
            if (reg != null) {
                JsfUtil.agregarMensajeError(idioma
                                .getString(SdentdevolutivoactivosControladorEnum.TB_TB1928
                                                .getValue()));
                return false;
            }
            registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registro.getCampos()
                            .put(SdentdevolutivoactivosControladorEnum.INDREG
                                            .getValue(),
                                            SysmanFunciones.validarCampoVacio(
                                                            registro.getCampos(),
                                                            SdentdevolutivoactivosControladorEnum.INDREG
                                                                            .getValue())
                                                                                ? false
                                                                                : registro.getCampos()
                                                                                                .get(SdentdevolutivoactivosControladorEnum.INDREG
                                                                                                                .getValue()));

            registro.getCampos().put(
                            GeneralParameterEnum.TIPOMOVIMIENTO.getName(),
                            tipoMovimientoCTA);
            registro.getCampos().put(GeneralParameterEnum.NUMERO.getName(),
                            consecutivoCTA);
            removerRegistro();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return true;
    }

    /**
     * metodo heredado del bean padre
     * 
     * @return
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * metodo heredado del bean padre
     * 
     * @return
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        if (registro.getCampos()
                        .get(SdentdevolutivoactivosControladorEnum.INDREG
                                        .getValue()) != null) {
            boolean aux = (boolean) registro.getCampos()
                            .get(SdentdevolutivoactivosControladorEnum.INDREG
                                            .getValue());

            if (aux) {
                JsfUtil.agregarMensajeError(idioma
                                .getString(SdentdevolutivoactivosControladorEnum.TB_TB1929
                                                .getValue()));
                return false;
            }
        }

        registro.getCampos().put(GeneralParameterEnum.TIPOMOVIMIENTO.getName(),
                        tipoMovimientoCTA);
        registro.getCampos().put(GeneralParameterEnum.NUMERO.getName(),
                        consecutivoCTA);
        registro.getCampos().put(SdentdevolutivoactivosControladorEnum.VALORBASE
                        .getValue(),
                        registro.getCampos()
                                        .get(SdentdevolutivoactivosControladorEnum.VALORBASED
                                                        .getValue()));
        registro.getCampos()
                        .put(SdentdevolutivoactivosControladorEnum.VALORTOTAL
                                        .getValue(),
                                        registro.getCampos()
                                                        .get(SdentdevolutivoactivosControladorEnum.VALORTOTALD
                                                                        .getValue()));
        removerRegistro();

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * metodo que se llama para remover los registros antes de
     * actualiza e insertar
     */
    private void removerRegistro() {
        registro.getCampos()
                        .remove(SdentdevolutivoactivosControladorEnum.VALORBASED
                                        .getValue());
        registro.getCampos()
                        .remove(SdentdevolutivoactivosControladorEnum.VALORTOTALD
                                        .getValue());
        registro.getCampos()
                        .remove(SdentdevolutivoactivosControladorEnum.CODIGO_TIPOACTIVO
                                        .getValue());
        registro.getCampos()
                        .remove(SdentdevolutivoactivosControladorEnum.NOMBRETIPOACT
                                        .getValue());
        registro.getCampos()
                        .remove(SdentdevolutivoactivosControladorEnum.NOMBRETIPOACTIVOFIN
                                        .getValue());
        registro.getCampos()
                        .remove(SdentdevolutivoactivosControladorEnum.NOMBRELARGO
                                        .getValue());
        registro.getCampos().remove(GeneralParameterEnum.DESCRIPCION.getName());
    }

    /**
     * metodo heredado del bean padre
     * 
     * @return
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * metodo heredado del bean padre
     * 
     * @return
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        if ((boolean) registro.getCampos()
                        .get(SdentdevolutivoactivosControladorEnum.INDREG
                                        .getValue())) {
            JsfUtil.agregarMensajeError(idioma
                            .getString(SdentdevolutivoactivosControladorEnum.TB_TB1930
                                            .getValue()));
            return false;
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * metodo heredado del bean padre
     * 
     * @return
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * metodo que activa la edicion
     * 
     * @param reg
     */
    public void activarEdicion(Registro reg) {
        indice = listaInicial.getRowIndex();
    }

    /**
     * metodo que cierra el formualrio continuo
     */
    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    /**
     * metodo que se llama para remover los registro
     */
    @Override
    public void removerCombos() {
        registro.getCampos()
                        .remove(GeneralParameterEnum.DATE_MODIFIED.getName());
        registro.getCampos().remove(GeneralParameterEnum.MODIFIED_BY.getName());
    }

    @Override
    public void asignarValoresRegistro() {
        // NO SE IMPLEMENTA
    }

    @Override
    public FormContinuoService getService() {
        return service;
    }

    @Override
    public void setService(FormContinuoService service) {
        this.service = service;
    }

    /**
     * metodos get y set
     * 
     * @return
     */
    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public List<Registro> getListaActivoNIIFFinal() {
        return listaActivoNIIFFinal;
    }

    public void setListaActivoNIIFFinal(List<Registro> listaActivoNIIFFinal) {
        this.listaActivoNIIFFinal = listaActivoNIIFFinal;
    }

    public RegistroDataModelImpl getListaPlaca() {
        return listaPlaca;
    }

    public void setListaPlaca(RegistroDataModelImpl listaPlaca) {
        this.listaPlaca = listaPlaca;
    }

    public RegistroDataModelImpl getListaPlacaE() {
        return listaPlacaE;
    }

    public void setListaPlacaE(RegistroDataModelImpl listaPlacaE) {
        this.listaPlacaE = listaPlacaE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }
}
