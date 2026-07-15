/*-
 * PlusvaliaConceptosControlador.java
 *
 * 1.0
 * 
 * 11/03/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.plusvalia;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.plusvalia.enums.PlusvaliaConceptosControladorEnum;
import com.sysman.plusvalia.enums.PlusvaliaConceptosControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 11/03/2019
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class PlusvaliaConceptosControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    private Map<String, Object> parametrosEntrada;
    private BigInteger idProyecto;
    private String codigoProyecto;
    private String claseProyecto;
    private String claseVP;
    private Map<String, Object> ridProyecto;

    private int anio;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     *
     */
    private RegistroDataModelImpl listaCuentaCCausacion;
    private RegistroDataModelImpl listaCuentaDRecaudo;
    private RegistroDataModelImpl listaCuentaDCausacion;
    private RegistroDataModelImpl listaCuentaCRecuado;
    private RegistroDataModelImpl listaClase;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de PlusvaliaConceptosControlador
     */
    public PlusvaliaConceptosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        anio = SysmanFunciones.ano(new Date());

        parametrosEntrada = SessionUtil.getFlash();
        if (parametrosEntrada != null) {
            idProyecto = (BigInteger) parametrosEntrada
                            .get("idProyecto");
            codigoProyecto = (String) parametrosEntrada
                            .get("codigoProyecto");
            claseProyecto = (String) parametrosEntrada.get("claseProyecto");
            ridProyecto = (Map<String, Object>) parametrosEntrada.get("rid");
        }
        try {
            // 2040
            numFormulario = GeneralCodigoFormaEnum.PLUSVALIA_CONCEPTOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
        }
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCuentaCCausacion();
        cargarListaCuentaDRecaudo();
        cargarListaCuentaDCausacion();
        cargarListaCuentaCRecuado();
        cargarListaClase();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
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

        enumBase = GenericUrlEnum.VP_CONCEPTOS;
        claseVP = "44";
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.PROYECTO.getName(),
                        idProyecto);
    }
    
    
    /**
     * 
     * Carga la lista listaClase
     * 
     */
    public void cargarListaClase() {

        Map<String, Object> param = new HashMap<>();

        param.put("CATEGORIA", "18");

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlusvaliaConceptosControladorUrlEnum.URL1032
                                                        .getValue());

        listaClase = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "ID");
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaCuentaCCausacion
     *
     * 
     */
    public void cargarListaCuentaCCausacion() {

        Map<String, Object> param = new HashMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlusvaliaConceptosControladorUrlEnum.URL16132
                                                        .getValue());

        listaCuentaCCausacion = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaCuentaDRecaudo
     *
     * 
     */
    public void cargarListaCuentaDRecaudo() {

        Map<String, Object> param = new HashMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlusvaliaConceptosControladorUrlEnum.URL16132
                                                        .getValue());

        listaCuentaDRecaudo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaCuentaDCausacion
     *
     * 
     */
    public void cargarListaCuentaDCausacion() {

        Map<String, Object> param = new HashMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlusvaliaConceptosControladorUrlEnum.URL16132
                                                        .getValue());

        listaCuentaDCausacion = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaCuentaCRecuado
     *
     * 
     */
    public void cargarListaCuentaCRecuado() {

        Map<String, Object> param = new HashMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlusvaliaConceptosControladorUrlEnum.URL16132
                                                        .getValue());

        listaCuentaCRecuado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaCCausacion
     *
     * 
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaCCausacion(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(
                        PlusvaliaConceptosControladorEnum.CUENTA_CREDITO_CAUSACION
                                        .getValue(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));

        registro.getCampos().put(
                        PlusvaliaConceptosControladorEnum.NOMBRECREDITOCAUSACION
                                        .getValue(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaDRecaudo
     *
     * 
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaDRecaudo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(
                        PlusvaliaConceptosControladorEnum.CUENTA_DEBITO_RECAUDO
                                        .getValue(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));

        registro.getCampos().put(
                        PlusvaliaConceptosControladorEnum.NOMBREDEBITORECAUDO
                                        .getValue(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaDCausacion
     *
     * 
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaDCausacion(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(
                        PlusvaliaConceptosControladorEnum.CUENTA_DEBITO_CAUSACION
                                        .getValue(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));

        registro.getCampos().put(
                        PlusvaliaConceptosControladorEnum.NOMBREDEBITOCAUSACION
                                        .getValue(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaCRecuado
     *
     * 
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaCRecuado(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(
                        PlusvaliaConceptosControladorEnum.CUENTA_CREDITO_RECAUDO
                                        .getValue(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));

        registro.getCampos().put(
                        PlusvaliaConceptosControladorEnum.NOMBRECREDITORECAUDO
                                        .getValue(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
    }
    
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaClase
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaClase(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("NOMBRE_CONCEPTO",
                        registroAux.getCampos().get("NOMBRE"));

        registro.getCampos().put("CLASE_CONCEPTO",
                        registroAux.getCampos().get("ID"));
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
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
     * Metodo ejecutado en el momento despues de cargar el registro
     * 
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * 
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        registro.getCampos().put(PlusvaliaConceptosControladorEnum.ID_PROYECTO
                        .getValue(), idProyecto);

        registro.getCampos()
                        .put(PlusvaliaConceptosControladorEnum.CODIGO_PROYECTO
                                        .getValue(), codigoProyecto);

        registro.getCampos().put(GeneralParameterEnum.CLASE.getName(),
                        claseVP);

        registro.getCampos().remove(
                        PlusvaliaConceptosControladorEnum.NOMBRECREDITOCAUSACION
                                        .getValue());
        registro.getCampos().remove(
                        PlusvaliaConceptosControladorEnum.NOMBRECREDITORECAUDO
                                        .getValue());
        registro.getCampos().remove(
                        PlusvaliaConceptosControladorEnum.NOMBREDEBITOCAUSACION
                                        .getValue());
        registro.getCampos().remove(
                        PlusvaliaConceptosControladorEnum.NOMBREDEBITORECAUDO
                                        .getValue());
        registro.getCampos().remove("PRODESC");
        registro.getCampos().remove("ID");
        registro.getCampos().remove("NOMBRE_CONCEPTO");
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
     * 
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>

        registro.getCampos().remove(
                        PlusvaliaConceptosControladorEnum.NOMBRECREDITOCAUSACION
                                        .getValue());
        registro.getCampos().remove(
                        PlusvaliaConceptosControladorEnum.NOMBRECREDITORECAUDO
                                        .getValue());
        registro.getCampos().remove(
                        PlusvaliaConceptosControladorEnum.NOMBREDEBITOCAUSACION
                                        .getValue());
        registro.getCampos().remove(
                        PlusvaliaConceptosControladorEnum.NOMBREDEBITORECAUDO
                                        .getValue());
        registro.getCampos().remove("PRODESC");
        registro.getCampos().remove("ID");
        registro.getCampos().remove("NOMBRE_CONCEPTO");
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
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
     * 
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     * 
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("idProyecto", idProyecto);
        parametros.put("codigoProyecto", codigoProyecto);
        parametros.put("claseProyecto", claseProyecto);
        parametros.put("rid", ridProyecto);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(String.valueOf(
                        GeneralCodigoFormaEnum.PLUSVALIA_ACUERDO_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);

        SessionUtil.redireccionarForma(direccionador,
                        modulo);
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    
    /**
     * Retorna la lista listaClase
     * 
     * @return listaClase
     */
    public RegistroDataModelImpl getListaClase() {
        return listaClase;
    }

    /**
     * Asigna la lista listaClase
     * 
     * @param listaClase
     * Variable a asignar en listaClase
     */
    public void setListaClase(RegistroDataModelImpl listaClase) {
        this.listaClase = listaClase;
    }

    /**
     * Retorna la lista listaCuentaCCausacion
     * 
     * @return listaCuentaCCausacion
     */
    public RegistroDataModelImpl getListaCuentaCCausacion() {
        return listaCuentaCCausacion;
    }

    /**
     * Asigna la lista listaCuentaCCausacion
     * 
     * @param listaCuentaCCausacion
     * Variable a asignar en listaCuentaCCausacion
     */
    public void setListaCuentaCCausacion(
        RegistroDataModelImpl listaCuentaCCausacion) {
        this.listaCuentaCCausacion = listaCuentaCCausacion;
    }

    /**
     * Retorna la lista listaCuentaDRecaudo
     * 
     * @return listaCuentaDRecaudo
     */
    public RegistroDataModelImpl getListaCuentaDRecaudo() {
        return listaCuentaDRecaudo;
    }

    /**
     * Asigna la lista listaCuentaDRecaudo
     * 
     * @param listaCuentaDRecaudo
     * Variable a asignar en listaCuentaDRecaudo
     */
    public void setListaCuentaDRecaudo(
        RegistroDataModelImpl listaCuentaDRecaudo) {
        this.listaCuentaDRecaudo = listaCuentaDRecaudo;
    }

    /**
     * Retorna la lista listaCuentaDCausacion
     * 
     * @return listaCuentaDCausacion
     */
    public RegistroDataModelImpl getListaCuentaDCausacion() {
        return listaCuentaDCausacion;
    }

    /**
     * Asigna la lista listaCuentaDCausacion
     * 
     * @param listaCuentaDCausacion
     * Variable a asignar en listaCuentaDCausacion
     */
    public void setListaCuentaDCausacion(
        RegistroDataModelImpl listaCuentaDCausacion) {
        this.listaCuentaDCausacion = listaCuentaDCausacion;
    }

    /**
     * Retorna la lista listaCuentaCRecuado
     * 
     * @return listaCuentaCRecuado
     */
    public RegistroDataModelImpl getListaCuentaCRecuado() {
        return listaCuentaCRecuado;
    }

    /**
     * Asigna la lista listaCuentaCRecuado
     * 
     * @param listaCuentaCRecuado
     * Variable a asignar en listaCuentaCRecuado
     */
    public void setListaCuentaCRecuado(
        RegistroDataModelImpl listaCuentaCRecuado) {
        this.listaCuentaCRecuado = listaCuentaCRecuado;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
