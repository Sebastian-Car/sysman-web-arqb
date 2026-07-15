/*-
 * ConfiguracionfuenterecursosControlador.java
 *
 * 1.0
 *
 * 23/03/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.chipfut;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.chipfut.enums.ConfiguracionfuenterecursosControladorUrlEnum;
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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Controlador para la vista del formulario Configuracion Fuentes
 * Recursos Fut Ruta de Accesso -->
 * UtilidadesChip\Archivo\Configuraci�n FUT\Configuracion Fuente Fut
 *
 * @version 1.0, 23/03/2017
 * @author ybecerra
 */
@ManagedBean
@ViewScoped
public class ConfiguracionfuenterecursosControlador
                extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida para almacenar la cadena "CODIGO"
     */
    private final String cCodigo;
    /**
     * Constante definida para almacenar la cadena "CODIGO_FUT"
     */
    private final String cCodigoFut;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el numero de ano seleccionado en el combo
     * ano
     */
    private String anoBase;
    /**
     * Atributo que almacena el numero de ano seleccionado en el combo
     * ano Destino
     */
    private String anoDestino;
    /**
     * variable definida para validar el mensaje a mostrar despues de
     * darle clic al boton preparar ano del formulario
     */
    String mensaje;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de registros que contiene la tabla Ano
     */
    private List<Registro> listaano;
    /**
     * Lista de registros que contiene la tabla Ano
     */
    private List<Registro> listaanodes;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de registros de los codigos fut
     */
    private RegistroDataModelImpl listatipoCodigoFut;
    /**
     * Lista de registros de los codigos fut
     */
    private RegistroDataModelImpl listatipoCodigoFutE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de
     * ConfiguracionfuenterecursosControlador
     */
    public ConfiguracionfuenterecursosControlador() {
        super();
        compania = SessionUtil.getCompania();
        cCodigo = "CODIGO";
        cCodigoFut = "CODIGO_FUT";
        try {

            // 1370
            numFormulario = GeneralCodigoFormaEnum.CONFIGURACIONFUENTERECURSOS_CONTROLADOR
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
        tabla = GenericUrlEnum.FUENTE_RECURSOS.getTable();
        buscarLlave();
        registro = new Registro();
        abrirFormulario();
        reasignarOrigen();
        // <CARGAR_LISTA>
        cargarListaano();
        cargarListaanodes();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>

        cargarListatipoCodigoFut();
        cargarListatipoCodigoFutE();
        // </CARGAR_LISTA_COMBO_GRANDE>

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
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), anoBase);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfiguracionfuenterecursosControladorUrlEnum.URL9781
                                                        .getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfiguracionfuenterecursosControladorUrlEnum.URL9782
                                                        .getValue());

        urlCreacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfiguracionfuenterecursosControladorUrlEnum.URL9783
                                                        .getValue());

        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfiguracionfuenterecursosControladorUrlEnum.URL9784
                                                        .getValue());

    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaano
     *
     */
    public void cargarListaano() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaano = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ConfiguracionfuenterecursosControladorUrlEnum.URL6331
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
     * Carga la lista listaanodes
     *
     */
    public void cargarListaanodes() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anoBase);

        try {
            listaanodes = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            ConfiguracionfuenterecursosControladorUrlEnum.URL6785
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
     * Carga la lista listatipoCodigoFut
     *
     */
    public void cargarListatipoCodigoFut() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfiguracionfuenterecursosControladorUrlEnum.URL7330
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anoBase);

        listatipoCodigoFut = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigoFut);
    }

    /**
     *
     * Carga la lista listatipoCodigoFut
     *
     */
    public void cargarListatipoCodigoFutE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfiguracionfuenterecursosControladorUrlEnum.URL7996
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anoBase);

        listatipoCodigoFutE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigoFut);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton preparar en la vista
     *
     *
     */
    public void oprimirpreparar() {
        // <CODIGO_DESARROLLADO>

        if (SysmanFunciones.validarVariableVacio(anoBase)
            || SysmanFunciones.validarVariableVacio(anoDestino)) {

            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB7"));
        }
        else {

            preparar();
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarano() {
        reasignarOrigen();
        anoDestino = null;
        cargarListaanodes();
        cargarListatipoCodigoFut();
        cargarListatipoCodigoFutE();

    }

    // </METODOS_CAMBIAR>
    private void preparar() {

        try {

            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anoBase);
            param.put("ANODESTINO", anoDestino);

            Parameter parameter = new Parameter();

            parameter.setFields(param);

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ConfiguracionfuenterecursosControladorUrlEnum.URL9780
                                                            .getValue());

            requestManager.save(urlCreate.getUrl(),
                            urlCreate.getMetodo(),
                            parameter);

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));

        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listafuenteRecurso
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilafuenteRecurso(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(cCodigo,
                        registroAux.getCampos().get(cCodigo));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listafuenteRecurso
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilafuenteRecursoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listatipoCodigoFut
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilatipoCodigoFut(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CODIGO_FUT_F3",
                        registroAux.getCampos().get(cCodigoFut));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listatipoCodigoFut
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilatipoCodigoFutE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cCodigoFut), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        int anoActual = SysmanFunciones
                        .ano(new Date());
        anoBase = String.valueOf(anoActual);
        anoDestino = String.valueOf(anoActual + 1);

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoActual);

        Parameter parameter = new Parameter();
        parameter.setFields(param);

        try {

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ConfiguracionfuenterecursosControladorUrlEnum.URL13469
                                                            .getValue());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            parameter);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
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
     * @return true
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(), anoBase);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     *
     * @return true
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
     *
     * @return true
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
     *
     * @return true
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
     * @return true
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
     *
     * @return true
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
     * Retorna la variable anoBase
     *
     * @return anoBase
     */
    public String getAnoBase() {
        return anoBase;
    }

    /**
     * Asigna la variable anoBase
     *
     * @param anoBase
     */
    public void setAnoBase(String anoBase) {
        this.anoBase = anoBase;
    }

    /**
     * Retorna la variable anoDestino
     *
     * @return anoDestino
     */
    public String getAnoDestino() {
        return anoDestino;
    }

    /**
     * Asigna la variable anoDestino
     *
     * @param anoDestino
     */
    public void setAnoDestino(String anoDestino) {
        this.anoDestino = anoDestino;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaano
     *
     * @return listaano
     */
    public List<Registro> getListaano() {
        return listaano;
    }

    /**
     * Asigna la lista listaano
     *
     * @param listaano
     * Variable a asignar en listaano
     */
    public void setListaano(List<Registro> listaano) {
        this.listaano = listaano;
    }

    /**
     * Retorna la lista listaanodes
     *
     * @return listaanodes
     */
    public List<Registro> getListaanodes() {
        return listaanodes;
    }

    /**
     * Asigna la lista listaanodes
     *
     * @param listaanodes
     * Variable a asignar en listaanodes
     */
    public void setListaanodes(List<Registro> listaanodes) {
        this.listaanodes = listaanodes;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    /**
     * Retorna la lista listatipoCodigoFut
     *
     * @return listatipoCodigoFut
     */
    public RegistroDataModelImpl getListatipoCodigoFut() {
        return listatipoCodigoFut;
    }

    /**
     * Asigna la lista listatipoCodigoFut
     *
     * @param listatipoCodigoFut
     * Variable a asignar en listatipoCodigoFut
     */
    public void setListatipoCodigoFut(
        RegistroDataModelImpl listatipoCodigoFut) {
        this.listatipoCodigoFut = listatipoCodigoFut;
    }

    /**
     * Retorna la lista listatipoCodigoFut
     *
     * @return listatipoCodigoFut
     */
    public RegistroDataModelImpl getListatipoCodigoFutE() {
        return listatipoCodigoFutE;
    }

    /**
     * Asigna la lista listatipoCodigoFut
     *
     * @param listatipoCodigoFut
     * Variable a asignar en listatipoCodigoFut
     */
    public void setListatipoCodigoFutE(
        RegistroDataModelImpl listatipoCodigoFutE) {
        this.listatipoCodigoFutE = listatipoCodigoFutE;
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
