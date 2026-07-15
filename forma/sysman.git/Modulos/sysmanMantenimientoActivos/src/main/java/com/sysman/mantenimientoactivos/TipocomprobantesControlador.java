package com.sysman.mantenimientoactivos;

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
import com.sysman.mantenimientoactivos.enums.TipocomprobantesControladorUrlEnum;
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
 * @author ngomez
 * @version 1, 15/09/2015
 *
 * @author lcortes
 * @version 2, 17,18/08/2017. Refactorizacion de codigo para usar dss.
 * 
 * @author eamaya
 * @version 2.1,26/10/2017 Cambio de origen de datos del formulario
 * por la tabla CONSECUTIVOTC
 */
@ManagedBean
@ViewScoped
public class TipocomprobantesControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;

    /**
     * Lista que almacena los tipos de de comprobante
     */
    private RegistroDataModelImpl listatipoComprobante;
    /**
     * Lista que almacena los tipos de de comprobante
     */
    private RegistroDataModelImpl listatipoComprobanteE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    private boolean insertando;

    /**
     * Creates a new instance of TipocomprobantesControlador
     */
    public TipocomprobantesControlador() {

        super();
        compania = SessionUtil.getCompania();
        try {

            numFormulario = GeneralCodigoFormaEnum.TIPOCOMPROBANTES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (SysmanException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.CONSECUTIVOTC;
        insertando = false;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        cargarListatipoComprobante();
        cargarListatipoComprobanteE();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void reasignarOrigen() {

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TipocomprobantesControladorUrlEnum.URL0001
                                                        .getValue());

        urlCreacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TipocomprobantesControladorUrlEnum.URL0002
                                                        .getValue());
        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TipocomprobantesControladorUrlEnum.URL0003
                                                        .getValue());
        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TipocomprobantesControladorUrlEnum.URL0004
                                                        .getValue());

    }

    /**
     * 
     * Carga la lista listatipoComprobante
     *
     */
    public void cargarListatipoComprobante() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TipocomprobantesControladorUrlEnum.URL2886
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(GeneralParameterEnum.CLASE.getName(), "M");

        listatipoComprobante = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listatipoComprobante
     *
     */
    public void cargarListatipoComprobanteE() {
        listatipoComprobanteE = listatipoComprobante;
    }

    public void seleccionarFilatipoComprobante(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TIPOCOMPROBANTE",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));

        registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listatipoComprobante
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilatipoComprobanteE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName());
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    private boolean validarConsecutivos() {
        Registro registroMantenimiento;

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(GeneralParameterEnum.TIPO_CPTE.getName(),
                        registro.getCampos().get("TIPOCOMPROBANTE"));

        param.put(GeneralParameterEnum.ANO.getName(),
                        registro.getCampos().get(
                                        GeneralParameterEnum.ANO.getName()));

        try {
            registroMantenimiento = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TipocomprobantesControladorUrlEnum.URL21259
                                                                            .getValue())
                                            .getUrl(), param));

            if (!"0".equals(registroMantenimiento.getCampos().get("EXISTE")
                            .toString())) {

                JsfUtil.agregarMensajeAlerta(
                                "No se puede cambiar el consecutivo porque ya existen registros con la vigencia"
                                    + " y con el comprobante seleccionado");
                return false;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return true;
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        insertando = true;
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        if (!insertando && !validarConsecutivos()) {
            return false;

        }

        insertando = false;
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
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void removerCombos() {
        // Metodo heredado

        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
    }

    @Override
    public void asignarValoresRegistro() {
        // Metodo heredado
    }

    /**
     * Retorna la lista listatipoComprobante
     * 
     * @return listatipoComprobante
     */
    public RegistroDataModelImpl getListatipoComprobante() {
        return listatipoComprobante;
    }

    /**
     * Asigna la lista listatipoComprobante
     * 
     * @param listatipoComprobante
     * Variable a asignar en listatipoComprobante
     */
    public void setListatipoComprobante(
        RegistroDataModelImpl listatipoComprobante) {
        this.listatipoComprobante = listatipoComprobante;
    }

    /**
     * Retorna la lista listatipoComprobante
     * 
     * @return listatipoComprobante
     */
    public RegistroDataModelImpl getListatipoComprobanteE() {
        return listatipoComprobanteE;
    }

    /**
     * Asigna la lista listatipoComprobante
     * 
     * @param listatipoComprobante
     * Variable a asignar en listatipoComprobante
     */
    public void setListatipoComprobanteE(
        RegistroDataModelImpl listatipoComprobanteE) {
        this.listatipoComprobanteE = listatipoComprobanteE;
    }

    /**
     * Retorna la variable auxiliar
     * 
     * @return auxiliar
     */
    public String getAuxiliar() {
        return auxiliar;
    }

    /**
     * Asigna la variable auxiliar
     * 
     * @param auxiliar
     * Variable a asignar en auxiliar
     */
    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }
}
