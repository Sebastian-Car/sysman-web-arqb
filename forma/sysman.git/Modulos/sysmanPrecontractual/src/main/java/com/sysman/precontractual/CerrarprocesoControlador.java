package com.sysman.precontractual;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.precontractual.ejb.EjbPrecontractualCeroRemote;
import com.sysman.precontractual.enums.CerrarprocesoControladorEnum;
import com.sysman.precontractual.enums.CerrarprocesoControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.List;
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
 * @author lcortes
 * @version 1, 09/12/2015
 * 
 * @modifier amonroy
 * @version 2, 23/08/2017 Se realizan ajustes para aplicacion de
 * buenas practicas sugeridas por la herremienta SonarLint, Proceso de
 * Refactoring e implementacion de EJBs para las funciones y
 * procedimientos que son llamadas en el controlador
 */
@ManagedBean
@ViewScoped
public class CerrarprocesoControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida por el numero de veces que se llama la
     * palabra "CONSECUTIVO" en el controlador
     */
    private final String cConsecutivo;
    private String tipoContrato;
    private String consecutivo;
    private String estado;
    private String observacion;
    private List<Registro> listaTipoContrato;
    private RegistroDataModelImpl listaConsecutivo;
    private Registro registro;
    /**
     * Implementacion del EJB de EjbPrecontractualCeroRemote para
     * hacer el llamado a las funciones que se invocan dentro del
     * Controlador y se encuentran almacenadas en el paquete
     * PCK_PRECONTRACTUAL
     */
    @EJB
    private EjbPrecontractualCeroRemote ejbPrecontractualCero;

    /**
     * Crea una nueva instancia de CerrarprocesoControlador
     */
    public CerrarprocesoControlador() {
        super();
        compania = SessionUtil.getCompania();
        cConsecutivo = GeneralParameterEnum.CONSECUTIVO.getName();
        try {
            numFormulario = GeneralCodigoFormaEnum.CERRARPROCESO_CONTROLADOR
                            .getCodigo();
            registro = new Registro(new HashMap<String, Object>());
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(CerrarprocesoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        cargarListaTipoContrato();
        cargarListaConsecutivo();
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Carga la lista listaTipoContrato
     *
     */
    public void cargarListaTipoContrato() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaTipoContrato = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CerrarprocesoControladorUrlEnum.URL2854
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaConsecutivo
     *
     */
    public void cargarListaConsecutivo() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CerrarprocesoControladorUrlEnum.URL3357
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(CerrarprocesoControladorEnum.ESTADOPAR.getValue(), "AC");
        param.put(GeneralParameterEnum.TIPOCONTRATO.getName(), tipoContrato);

        listaConsecutivo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cConsecutivo);
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton BtnAceptar en la vista
     * 
     * Realiza la validacion de los campos vacios y ejecuta la funcion
     * que actualiza el estado de los contratos
     *
     */
    public void oprimirBtnAceptar() {
        // <CODIGO_DESARROLLADO>
        estado = registro.getCampos().get(GeneralParameterEnum.ESTADO.getName())
                        .toString();
        observacion = registro.getCampos()
                        .get(GeneralParameterEnum.OBSERVACION.getName())
                        .toString();
        if (validarVacio(tipoContrato) ||
            validarVacio(consecutivo) ||
            validarVacio(estado) ||
            validarVacio(observacion)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2186"));
            return;
        }
        try {

            String num = ejbPrecontractualCero.actualizarTransaccion(compania,
                            tipoContrato,
                            Integer.parseInt(consecutivo),
                            estado,
                            observacion,
                            SessionUtil.getUser().getCodigo());

            if (Integer.parseInt(num) > 0) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB1637"));
            }
            else {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB2187"));
            }
            // </CODIGO_DESARROLLADO>
        }
        catch (NumberFormatException | SystemException ex) {
            Logger.getLogger(CerrarprocesoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton BtnCancelar en la vista
     *
     * Cierra el formulario y redirecciona a las opciones de menu
     *
     */
    public void oprimirBtnCancelar() {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control TipoContrato
     * 
     * Recarga la lista de los contratos al seleccionar un nuevo tipo
     * de contrato
     * 
     */
    public void cambiarTipoContrato() {
        // <CODIGO_DESARROLLADO>
        tipoContrato = registro.getCampos()
                        .get(GeneralParameterEnum.TIPOCONTRATO.getName())
                        .toString();
        cargarListaConsecutivo();
        registro.getCampos().put(GeneralParameterEnum.CONSECUTIVO.getName(),
                        null);
        registro.getCampos().put(GeneralParameterEnum.ESTADO.getName(), null);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaConsecutivo
     *
     * Actualiza el valor del contrato a afectar en el registro
     * principal del formulario
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaConsecutivo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        consecutivo = registroAux.getCampos().get(cConsecutivo).toString();
        registro.getCampos().put(cConsecutivo, consecutivo);

    }

    /**
     * Valida si el valor que ingresa por parametro se encuentra vaio
     * o nulo
     * 
     * @param variable
     * Cadena a evaluar
     * @return Si el parametro esta vacio
     */
    private boolean validarVacio(String variable) {
        return SysmanFunciones.validarVariableVacio(variable);
    }

    public Registro getRegistro() {
        return registro;
    }

    public void setRegistro(Registro registro) {
        this.registro = registro;
    }

    public String getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    public String getConsecutivo() {
        return consecutivo;
    }

    public void setConsecutivo(String consecutivo) {
        this.consecutivo = consecutivo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public List<Registro> getListaTipoContrato() {
        return listaTipoContrato;
    }

    public void setListaTipoContrato(List<Registro> listaTipoContrato) {
        this.listaTipoContrato = listaTipoContrato;
    }

    public RegistroDataModelImpl getListaConsecutivo() {
        return listaConsecutivo;
    }

    public void setListaConsecutivo(RegistroDataModelImpl listaConsecutivo) {
        this.listaConsecutivo = listaConsecutivo;
    }

}
