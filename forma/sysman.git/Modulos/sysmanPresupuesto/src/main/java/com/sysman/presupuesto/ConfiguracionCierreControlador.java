/*-
 * ConfiguracionCierreControlador.java
 *
 * 1.0
 * 
 * 22/01/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.presupuesto.ejb.EjbPresupuestoCierreRemote;
import com.sysman.presupuesto.enums.ConfiguracionCierreControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Formulario que permite configurar el cierre contable.
 *
 * @version 1.0, 22/01/2019
 * @author jreina
 */
@ManagedBean
@ViewScoped
public class ConfiguracionCierreControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaTipoDis;
    private RegistroDataModelImpl listaTipoDisE;
    private RegistroDataModelImpl listaTipoReo;
    private RegistroDataModelImpl listaTipoReoE;
    private RegistroDataModelImpl listaTipoRes;
    private RegistroDataModelImpl listaTipoResE;
    private RegistroDataModelImpl listaTipoAdi;
    private RegistroDataModelImpl listaTipoAdiE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    @EJB
    EjbPresupuestoCierreRemote ejbPresupuestoCierre;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ConfiguracionCierreControlador
     */
    public ConfiguracionCierreControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.CONFIGURACION_CIERRE_CONTROLADOR
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
        tabla = "CONFIG_CIERRE_PPTAL";
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaTipoDis();
        cargarListaTipoReo();
        cargarListaTipoRes();
        cargarListaTipoAdi();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();

    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        ConfiguracionCierreControladorUrlEnum.URL12000
                                        .getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfiguracionCierreControladorUrlEnum.URL12002
                                                        .getValue());

        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfiguracionCierreControladorUrlEnum.URL12003
                                                        .getValue());
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Metodo que permite cargar los combos de tipos de comprobantes
     * por clase
     * 
     * @author jgomez
     * @param clase:
     * Clase de la cual se quiere listar los comprobantes
     * presupuestales
     * @return Registro Data Model para cargar combos de tipo de
     * comprobantes presupuestales
     */
    private RegistroDataModelImpl cargarListaTipo(String clase) {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASE.getName(), clase);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfiguracionCierreControladorUrlEnum.URL12001
                                                        .getValue());
        return new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaTipoDis
     *
     */
    public void cargarListaTipoDis() {
        listaTipoDis = cargarListaTipo("DIS");
        listaTipoDisE = listaTipoDis;
    }

    /**
     * 
     * Carga la lista listaTipoReo
     *
     */
    public void cargarListaTipoReo() {
        listaTipoReo = cargarListaTipo("REO");
        listaTipoReoE = listaTipoReo;
    }

    /**
     * 
     * Carga la lista listaTipoRes
     *
     */
    public void cargarListaTipoRes() {
        listaTipoRes = cargarListaTipo("RES");
        listaTipoResE = listaTipoRes;
    }

    /**
     * 
     * Carga la lista listaTipoAdi
     *
     */
    public void cargarListaTipoAdi() {
        listaTipoAdi = cargarListaTipo("ADC");
        listaTipoAdiE = listaTipoAdi;
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirCrearTipos() {
        try {
            ejbPresupuestoCierre.crearTipoDefectoCierre(compania,
                            SessionUtil.getUser().getCodigo());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo ejecutado al oprimir el boton Reclasificacion
     * 
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirReclasificacion(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        // <CODIGO_DESARROLLADO>
        Map<String, Object> param = new HashMap<>();

        param.put("compania", compania);
        param.put("clase", reg.getCampos().get("CLASERESERVA"));
        param.put("tipoVigencia", reg.getCampos().get("TIPOVIGENCIAINICI"));
        param.put("tipoCierre", reg.getCampos().get("TIPOCIERRE"));

        Direccionador direccionador = new Direccionador();
        direccionador.setParametros(param);
        direccionador.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.RECLASIFICACION_CIERRE_PPTAL_CONTROLADOR
                                        .getCodigo()));
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoDis
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoDis(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TIPO_DIS",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoDis
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoDisE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoReo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoReo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TIPO_REO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoReo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoReoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoRes
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoRes(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TIPO_RES",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoRes
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoResE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoAdi
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoAdi(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TIPO_ADI",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoRes
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoAdiE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_COMBOS_GRANDES>
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
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove("TIPOVIGENCIAFINAL");
        registro.getCampos().remove("CLASERESERVA");
        registro.getCampos().remove("TIPOVIGENCIAINICI");
        registro.getCampos().remove("TIPOCIERRE");
        registro.getCampos().remove("NOMBREVINI");
        registro.getCampos().remove("NOMBRE_DIS");
        registro.getCampos().remove("NOMBREVFIN");
        registro.getCampos().remove("NOMBRE_RES");
        registro.getCampos().remove("NOMBRE_REO");
        registro.getCampos().remove("NOMBRE_ADI");
        registro.getCampos().remove("NOMBRETIPOCIERRE");
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        // METODO NO IMPLEMENTADO
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // METODO NO IMPLEMENTADO
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaTipoDis
     * 
     * @return listaTipoDis
     */
    public RegistroDataModelImpl getListaTipoDis() {
        return listaTipoDis;
    }

    /**
     * Asigna la lista listaTipoDis
     * 
     * @param listaTipoDis
     * Variable a asignar en listaTipoDis
     */
    public void setListaTipoDis(RegistroDataModelImpl listaTipoDis) {
        this.listaTipoDis = listaTipoDis;
    }

    /**
     * Retorna la lista listaTipoDis
     * 
     * @return listaTipoDis
     */
    public RegistroDataModelImpl getListaTipoDisE() {
        return listaTipoDisE;
    }

    /**
     * Asigna la lista listaTipoDis
     * 
     * @param listaTipoDis
     * Variable a asignar en listaTipoDis
     */
    public void setListaTipoDisE(RegistroDataModelImpl listaTipoDisE) {
        this.listaTipoDisE = listaTipoDisE;
    }

    /**
     * Retorna la lista listaTipoReo
     * 
     * @return listaTipoReo
     */
    public RegistroDataModelImpl getListaTipoReo() {
        return listaTipoReo;
    }

    /**
     * Asigna la lista listaTipoReo
     * 
     * @param listaTipoReo
     * Variable a asignar en listaTipoReo
     */
    public void setListaTipoReo(RegistroDataModelImpl listaTipoReo) {
        this.listaTipoReo = listaTipoReo;
    }

    /**
     * Retorna la lista listaTipoReo
     * 
     * @return listaTipoReo
     */
    public RegistroDataModelImpl getListaTipoReoE() {
        return listaTipoReoE;
    }

    /**
     * Asigna la lista listaTipoReo
     * 
     * @param listaTipoReo
     * Variable a asignar en listaTipoReo
     */
    public void setListaTipoReoE(RegistroDataModelImpl listaTipoReoE) {
        this.listaTipoReoE = listaTipoReoE;
    }

    /**
     * Retorna la lista listaTipoRes
     * 
     * @return listaTipoRes
     */
    public RegistroDataModelImpl getListaTipoRes() {
        return listaTipoRes;
    }

    /**
     * Asigna la lista listaTipoRes
     * 
     * @param listaTipoRes
     * Variable a asignar en listaTipoRes
     */
    public void setListaTipoRes(RegistroDataModelImpl listaTipoRes) {
        this.listaTipoRes = listaTipoRes;
    }

    /**
     * Retorna la lista listaTipoRes
     * 
     * @return listaTipoRes
     */
    public RegistroDataModelImpl getListaTipoResE() {
        return listaTipoResE;
    }

    /**
     * Asigna la lista listaTipoRes
     * 
     * @param listaTipoRes
     * Variable a asignar en listaTipoRes
     */
    public void setListaTipoResE(RegistroDataModelImpl listaTipoResE) {
        this.listaTipoResE = listaTipoResE;
    }

    /**
     * Retorna la lista listaTipoAdi
     * 
     * @return listaTipoRes
     */
    public RegistroDataModelImpl getListaTipoAdiE() {
        return listaTipoAdiE;
    }

    /**
     * Asigna la lista listaTipoAdi
     * 
     * @param listaTipoRes
     * Variable a asignar en listaTipoAdi
     */
    public void setListaTipoAdiE(RegistroDataModelImpl listaTipoAdiE) {
        this.listaTipoAdiE = listaTipoAdiE;
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
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
