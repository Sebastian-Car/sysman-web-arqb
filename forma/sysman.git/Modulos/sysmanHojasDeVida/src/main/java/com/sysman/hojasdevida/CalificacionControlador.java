/*-
 * CalificacionControlador.java
 *
 * 1.0
 * 
 * 27/12/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.enums.CalificacionControladorEnum;
import com.sysman.hojasdevida.enums.CalificacionControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * Formulario que administra las calificaciones de la selección de
 * personal.
 *
 * @version 1.0, 27/12/2017
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class CalificacionControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private final String modulo;

    private final String cedula;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el numero de convocatoria seleccionada en
     * la vista
     */
    private String numeroConvocatoria;
    /**
     * Atributo que almacena la clase de prueba seleccionada en la
     * vista
     */
    private String clasePrueba;
    /**
     * Atributo que almacena la fecha del numero de convocatoria
     * seleccionada en la vista
     */
    private Date fechaConvocatoria;
    /**
     * Atributo que almacena el cargo de convocatoria seleccionada en
     * la vista
     */
    private String cargo;

    private boolean ver;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que almacena los numeros de convocatoria
     */
    private RegistroDataModelImpl listaNumeroConvocatoria;
    /**
     * Lista que almacena las clases de pruebas
     */
    private RegistroDataModelImpl listaClaseDePrueba;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de CalificacionControlador
     */
    public CalificacionControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cedula = SessionUtil.getUser()
                        .getCedula();
        try {
            numFormulario = GeneralCodigoFormaEnum.CALIFICACION_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
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
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaNumeroConvocatoria();

        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
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
        mensajesInicioModal();
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaNumeroConvocatoria
     *
     */
    public void cargarListaNumeroConvocatoria() {

        Map<String, Object> param = new TreeMap<>();

        UrlBean urlBean;
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        if ("21090104".equals(SessionUtil.getMenuActual())) {
            param.put(GeneralParameterEnum.NUMERO_DCTO.getName(),
                            cedula);

            urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            CalificacionControladorUrlEnum.URL5166
                                                            .getValue());
        }
        else {
            urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            CalificacionControladorUrlEnum.URL5181
                                                            .getValue());
        }

        listaNumeroConvocatoria = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NRO_CONVOCATORIA");
    }

    public void mensajesInicioModal() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_DCTO.getName(),
                        cedula);

        try {
            Registro registro = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CalificacionControladorUrlEnum.URL6196
                                                                            .getValue())
                                            .getUrl(), param));

            if ("0".equals(registro.getCampos().get("CANTIDAD").toString())
                && "21090104".equals(SessionUtil.getMenuActual())) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB3956")
                                .replace("s$cedula$s", cedula));
            }
        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listaClaseDePrueba
     *
     */
    public void cargarListaClaseDePrueba() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CalificacionControladorUrlEnum.URL6227
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(CalificacionControladorEnum.CONVOCATORIA.getValue(),
                        numeroConvocatoria);

        listaClaseDePrueba = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "PRUEBA");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton CalificacionPruebas en la
     * vista
     *
     *
     */
    public void oprimirCalificacionPruebas() {
        Map<String, Object> param = new TreeMap<>();

        param.put("numeroConvocatoria", numeroConvocatoria);
        param.put("prueba", clasePrueba);
        param.put("permiteVer", ver);

        Direccionador direccionador = new Direccionador();
        direccionador.setParametros(param);
        direccionador.setNumForm("1551");

        SessionUtil.redireccionarDeModalAModal(direccionador, modulo);

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaNumeroConvocatoria
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNumeroConvocatoria(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        ver = (boolean) registroAux.getCampos().get("CERRADA");
        try {
            numeroConvocatoria = SysmanFunciones
                            .nvl(registroAux.getCampos()
                                            .get("NRO_CONVOCATORIA"),
                                            "")
                            .toString();

            fechaConvocatoria = SysmanFunciones
                            .convertirAFecha(SysmanFunciones
                                            .nvl(registroAux.getCampos()
                                                            .get("FECHA_CONVOCATORIA"),
                                                            "")
                                            .toString());

            cargo = SysmanFunciones
                            .nvl(registroAux.getCampos().get("DENOMINACION"),
                                            "")
                            .toString();

            clasePrueba = "";
            cargarListaClaseDePrueba();

        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaClaseDePrueba
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaClaseDePrueba(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        clasePrueba = SysmanFunciones
                        .nvl(registroAux.getCampos().get("PRUEBA"), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable numeroConvocatoria
     * 
     * @return numeroConvocatoria
     */
    public String getNumeroConvocatoria() {
        return numeroConvocatoria;
    }

    /**
     * Asigna la variable numeroConvocatoria
     * 
     * @param numeroConvocatoria
     * Variable a asignar en numeroConvocatoria
     */
    public void setNumeroConvocatoria(String numeroConvocatoria) {
        this.numeroConvocatoria = numeroConvocatoria;
    }

    /**
     * Retorna la variable clasePrueba
     * 
     * @return clasePrueba
     */
    public String getClasePrueba() {
        return clasePrueba;
    }

    /**
     * Asigna la variable clasePrueba
     * 
     * @param clasePrueba
     * Variable a asignar en clasePrueba
     */
    public void setClasePrueba(String clasePrueba) {
        this.clasePrueba = clasePrueba;
    }

    /**
     * Retorna la variable fechaConvocatoria
     * 
     * @return fechaConvocatoria
     */
    public Date getFechaConvocatoria() {
        return fechaConvocatoria;
    }

    /**
     * Asigna la variable fechaConvocatoria
     * 
     * @param fechaConvocatoria
     * Variable a asignar en fechaConvocatoria
     */
    public void setFechaConvocatoria(Date fechaConvocatoria) {
        this.fechaConvocatoria = fechaConvocatoria;
    }

    /**
     * Retorna la variable cargo
     * 
     * @return cargo
     */
    public String getCargo() {
        return cargo;
    }

    /**
     * Asigna la variable cargo
     * 
     * @param cargo
     * Variable a asignar en cargo
     */
    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaNumeroConvocatoria
     * 
     * @return listaNumeroConvocatoria
     */
    public RegistroDataModelImpl getListaNumeroConvocatoria() {
        return listaNumeroConvocatoria;
    }

    /**
     * Asigna la lista listaNumeroConvocatoria
     * 
     * @param listaNumeroConvocatoria
     * Variable a asignar en listaNumeroConvocatoria
     */
    public void setListaNumeroConvocatoria(
        RegistroDataModelImpl listaNumeroConvocatoria) {
        this.listaNumeroConvocatoria = listaNumeroConvocatoria;
    }

    /**
     * Retorna la lista listaClaseDePrueba
     * 
     * @return listaClaseDePrueba
     */
    public RegistroDataModelImpl getListaClaseDePrueba() {
        return listaClaseDePrueba;
    }

    /**
     * Asigna la lista listaClaseDePrueba
     * 
     * @param listaClaseDePrueba
     * Variable a asignar en listaClaseDePrueba
     */
    public void setListaClaseDePrueba(
        RegistroDataModelImpl listaClaseDePrueba) {
        this.listaClaseDePrueba = listaClaseDePrueba;
    }

    /**
     * @return the ver
     */
    public boolean isVer() {
        return ver;
    }

    /**
     * @param ver
     * the ver to set
     */
    public void setVer(boolean ver) {
        this.ver = ver;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
