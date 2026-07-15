/*-
 * ConfigurarFuenteFutsControlador.java
 *
 * 1.0
 * 
 * 27/03/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.chipfut;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.chipfut.ejb.EjbChipFutUnoGeneralRemote;
import com.sysman.chipfut.enums.ConfigurarFuenteFutsControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Esta clase es el controlador para el formulario Configuracion
 * Fuentes FUT en Access "Conf_Fuente", el cual es llamado desde Entes
 * de Control\Chip - Fut\Archivo\Configuraci�n FUT\Fuentes
 * Excedentes y Cierre Fiscal
 *
 * 
 * @version 1.0, 27/03/2017
 * @author amonroy
 */
@ManagedBean
@ViewScoped
public class ConfigurarFuenteFutsControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo CODIGO en el formulario, almacena el texto
     * CODIGO el cual es un campo del registro
     */
    private final String cCodigo;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo CODIGO_FUT en el formulario, almacena el texto
     * CODIGO_FUT el cual es un campo del registro
     */
    private final String cCodigoFut;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo CODIGO_FUTCF en el formulario, almacena el
     * texto CODIGO_FUTCF el cual es un campo del registro
     */
    private final String cCodigoFutCf;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el anio en el que se desea trabajar la
     * configuracion de fuentes FUT
     */
    private int anio;
    /**
     * Atributo que almacena el anio que se desea configurar con la
     * informacion del anio en el que se esta trabajando
     */
    private int anioDes;
    /**
     * Atributo que almacena el tipo de movimiento del codigo Fut con
     * Excedentes de Liquidez que ha sido seleccionado cuando se esta
     * editando un registro de la grilla
     */
    private String movimientoTipocE;
    /**
     * Atributo que almacena el tipo de movimiento del codigo Fut
     * Cierre Fiscal que ha sido seleccionado cuando se esta editando
     * un registro de la grilla
     */
    private String movimientoFutCfE;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Listado de registros para el comboBox de Anio
     */
    private List<Registro> listaAnio;
    /**
     * Listado de registros para el comboBox de Anio Destino
     */
    private List<Registro> listaAnioDes;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Listado de registros para el comboBox de C�digo FUT
     * Excedentes de Liquidez
     */
    private RegistroDataModelImpl listaTipoc;
    /**
     * Listado de registros para el comboBox de C�digo FUT
     * Excedentes de Liquidez cuando se edita un registro
     */
    private RegistroDataModelImpl listaTipocE;
    /**
     * Listado de registros para el comboBox de C�digo FUT Cierre
     * Fiscal
     */
    private RegistroDataModelImpl listaFutcf;
    /**
     * Listado de registros para el comboBox de C�digo FUT Cierre
     * Fiscal cuando se edita un registro
     */
    private RegistroDataModelImpl listaFutcfE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbChipFutUnoGeneralRemote ejbChipGeneral;

    /**
     * Crea una nueva instancia de ConfigurarFuenteFutsControlador
     */
    public ConfigurarFuenteFutsControlador() {
        super();
        compania = SessionUtil.getCompania();
        cCodigo = "CODIGO";
        cCodigoFut = "CODIGO_FUT";
        cCodigoFutCf = "CODIGO_FUTCF";
        try {
            // 1385
            numFormulario = GeneralCodigoFormaEnum.CONFIGURAR_FUENTE_FUTS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            anioDes = anio = SysmanFunciones.ano(new Date());
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
        tabla = GenericUrlEnum.FUENTE_RECURSOS.getTable();
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        cargarListaAnio();
        cargarListaAnioDes();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaTipoc();
        cargarListaTipocE();
        cargarListaFutcf();
        cargarListaFutcfE();
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
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), anio);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarFuenteFutsControladorUrlEnum.URL11542
                                                        .getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarFuenteFutsControladorUrlEnum.URL11543
                                                        .getValue());

        urlCreacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarFuenteFutsControladorUrlEnum.URL11544
                                                        .getValue());

        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarFuenteFutsControladorUrlEnum.URL11545
                                                        .getValue());
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAnio
     *
     */
    public void cargarListaAnio() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaAnio = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ConfigurarFuenteFutsControladorUrlEnum.URL7768
                                                                            .getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaAnioDes
     *
     */
    public void cargarListaAnioDes() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaAnioDes = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            ConfigurarFuenteFutsControladorUrlEnum.URL8185
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaTipoc
     *
     */
    public void cargarListaTipoc() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.TIPO.getName(), "EX");

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarFuenteFutsControladorUrlEnum.URL8601
                                                        .getValue());
        listaTipoc = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    /**
     * 
     * Carga la lista listaTipoc
     *
     */
    public void cargarListaTipocE() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.TIPO.getName(), "EX");

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarFuenteFutsControladorUrlEnum.URL9585
                                                        .getValue());
        listaTipocE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    /**
     * 
     * Carga la lista listaFutcf
     *
     */
    public void cargarListaFutcf() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.TIPO.getName(), "CF");

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarFuenteFutsControladorUrlEnum.URL10568
                                                        .getValue());
        listaFutcf = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    /**
     * 
     * Carga la lista listaFutcf
     *
     */
    public void cargarListaFutcfE() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.TIPO.getName(), "CF");

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfigurarFuenteFutsControladorUrlEnum.URL11541
                                                        .getValue());
        listaFutcfE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Preparar en la vista
     *
     * Realiza el llamado al procedimiento PR_COPIAR_FUENTE_RECURSO
     * ubicado en el paquete PCK_PREPARAR_ANO, la cual pasa la
     * configuracion de las fuentes FUT de un anio Origen a uno
     * Destino
     *
     * 25/04/2017 Se realiza el cambio de llamado a la funcion
     * PCK_PREPARAR_ANO.PR_COPIAR_FUENTE_RECURSO, previamente se
     * realizaba el llamado a la funcion PCK_CHIPFUT.FC_PREPARARFUENTE
     */
    public void oprimirPreparar() {
        // <CODIGO_DESARROLLADO>
        try {

            ejbChipGeneral.copiarFuenteRecurso(compania, anioDes, anio,
                            compania);

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Anio
     * 
     * Al cambiar el anio se actualizam los valores que visualizan en
     * la grilla
     * 
     */
    public void cambiarAnio() {
        // <CODIGO_DESARROLLADO>
        buscarLlave();
        reasignarOrigen();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Tipoc en la fila
     * seleccionada dentro de la grilla
     * 
     * Se valida el tipo de movimiento del codigo que ha sido
     * seleccionado, de acuerdo a su valor se asigna el valor
     * seleccionado o nulo
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarTipocC(int rowNum) {
        // Para el cambio en una fila selecciona (PARA FORMULARIOS
        // CONTINUOS) se realiza como lo muestra la siguiente linea
        String valor = evaluarMovimiento(movimientoTipocE, cCodigoFut)
            ? auxiliar
            : null;
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(cCodigoFut, valor);
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Futcf en la fila
     * seleccionada dentro de la grilla
     * 
     * Se valida el tipo de movimiento del codigo que ha sido
     * seleccionado, de acuerdo a su valor se asigna el valor
     * seleccionado o nulo
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarFutcfC(int rowNum) {
        // Para el cambio en una fila selecciona (PARA FORMULARIOS
        // CONTINUOS) se realiza como lo muestra la siguiente linea
        String valorAux = evaluarMovimiento(movimientoFutCfE, cCodigoFutCf)
            ? auxiliar
            : null;
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(cCodigoFutCf, valorAux);
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuenter
     *
     * Asigna el valor seleccionado en el combo al campo CODIGO del
     * registro que se va a insertar
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuenter(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(cCodigo,
                        registroAux.getCampos().get(cCodigo));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuenter
     *
     * Obtiene el valor del codigo seleccionado en el combo y lo
     * almacena en el atributo "auxiliar "
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuenterE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaTipoc
     *
     * Asigna el valor seleccionado en el combo al campo CODIGO_FUT
     * del registro que se va a insertar
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoc(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        if (evaluarMovimiento(registroAux.getCampos().get("MOV").toString(),
                        cCodigoFut)) {
            registro.getCampos().put(cCodigoFut,
                            registroAux.getCampos().get(cCodigo));
        }
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaTipoc
     *
     * Obtiene el valor del codigo seleccionado en el combo y lo
     * almacena en el atributo "auxiliar" adicionalmente almacena en
     * el atributo "movimientoTipocE" el indicador de movimiento para
     * el valor seleccionado
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipocE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();
        movimientoTipocE = SysmanFunciones
                        .nvl(registroAux.getCampos().get("MOV"), "").toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaFutcf
     *
     * Asigna el valor seleccionado en el combo al campo CODIGO_FUTCF
     * del registro que se va a insertar
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFutcf(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        if (evaluarMovimiento(registroAux.getCampos().get("MOV").toString(),
                        cCodigoFutCf)) {
            registro.getCampos().put(cCodigoFutCf,
                            registroAux.getCampos().get(cCodigo));
        }
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaFutcf
     *
     * Obtiene el valor del codigo seleccionado en el combo y lo
     * almacena en el atributo "auxiliar" adicionalmente almacena en
     * el atributo "movimientoFutCfE" el indicador de movimiento para
     * el valor seleccionado
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFutcfE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();
        movimientoFutCfE = SysmanFunciones
                        .nvl(registroAux.getCampos().get("MOV"), "").toString();
    }

    /**
     * Evalua si el codigo FUT seleccionado posee indicador de
     * movimiento
     * 
     * @param movimiento
     * el valor del indicador de movimiento
     * @return Verdadero si el indicador de movimiento esta en si
     */
    private boolean evaluarMovimiento(String movimiento, String campo) {
        boolean respuesta = true;
        if (!"S".equalsIgnoreCase(movimiento)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3020"));
            registro.getCampos().put(campo, null);
            respuesta = false;
        }
        return respuesta;
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
        try {

            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anio);

            Parameter parameter = new Parameter();

            parameter.setFields(param);

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ConfigurarFuenteFutsControladorUrlEnum.URL18944
                                                            .getValue());

            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            parameter);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
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
     * Agrega en el registro los valores para los campos COMPANIA y
     * ANO
     * 
     * @return Si el proceso previo a la insercion fue exitoso
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(), anio);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return Si el proceso de insercion fue exitoso
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
     * @return Si el proceso previo a la actualizacion fue exitoso
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * @return Si el proceso de actualizacion fue exitoso
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
     * @return Si el proceso previo a la eliminacion fue exitoso
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
     * @return Si el proceso de eliminacion fue exitoso
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
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable anio
     * 
     * @return anio
     */
    public int getAnio() {
        return anio;
    }

    /**
     * Asigna la variable anio
     * 
     * @param anio
     * Variable a asignar en anio
     */
    public void setAnio(int anio) {
        this.anio = anio;
    }

    /**
     * Retorna la variable anioDes
     * 
     * @return anioDes
     */
    public int getAnioDes() {
        return anioDes;
    }

    /**
     * Asigna la variable anioDes
     * 
     * @param anioDes
     * Variable a asignar en anioDes
     */
    public void setAnioDes(int anioDes) {
        this.anioDes = anioDes;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaAnio
     * 
     * @return listaAnio
     */
    public List<Registro> getListaAnio() {
        return listaAnio;
    }

    /**
     * Asigna la lista listaAnio
     * 
     * @param listaAnio
     * Variable a asignar en listaAnio
     */
    public void setListaAnio(List<Registro> listaAnio) {
        this.listaAnio = listaAnio;
    }

    /**
     * Retorna la lista listaAnioDes
     * 
     * @return listaAnioDes
     */
    public List<Registro> getListaAnioDes() {
        return listaAnioDes;
    }

    /**
     * Asigna la lista listaAnioDes
     * 
     * @param listaAnioDes
     * Variable a asignar en listaAnioDes
     */
    public void setListaAnioDes(List<Registro> listaAnioDes) {
        this.listaAnioDes = listaAnioDes;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaTipoc
     * 
     * @return listaTipoc
     */
    public RegistroDataModelImpl getListaTipoc() {
        return listaTipoc;
    }

    /**
     * Asigna la lista listaTipoc
     * 
     * @param listaTipoc
     * Variable a asignar en listaTipoc
     */
    public void setListaTipoc(RegistroDataModelImpl listaTipoc) {
        this.listaTipoc = listaTipoc;
    }

    /**
     * Retorna la lista listaTipoc
     * 
     * @return listaTipoc
     */
    public RegistroDataModelImpl getListaTipocE() {
        return listaTipocE;
    }

    /**
     * Asigna la lista listaTipoc
     * 
     * @param listaTipoc
     * Variable a asignar en listaTipoc
     */
    public void setListaTipocE(RegistroDataModelImpl listaTipocE) {
        this.listaTipocE = listaTipocE;
    }

    /**
     * Retorna la lista listaFutcf
     * 
     * @return listaFutcf
     */
    public RegistroDataModelImpl getListaFutcf() {
        return listaFutcf;
    }

    /**
     * Asigna la lista listaFutcf
     * 
     * @param listaFutcf
     * Variable a asignar en listaFutcf
     */
    public void setListaFutcf(RegistroDataModelImpl listaFutcf) {
        this.listaFutcf = listaFutcf;
    }

    /**
     * Retorna la lista listaFutcf
     * 
     * @return listaFutcf
     */
    public RegistroDataModelImpl getListaFutcfE() {
        return listaFutcfE;
    }

    /**
     * Asigna la lista listaFutcf
     * 
     * @param listaFutcf
     * Variable a asignar en listaFutcf
     */
    public void setListaFutcfE(RegistroDataModelImpl listaFutcfE) {
        this.listaFutcfE = listaFutcfE;
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
