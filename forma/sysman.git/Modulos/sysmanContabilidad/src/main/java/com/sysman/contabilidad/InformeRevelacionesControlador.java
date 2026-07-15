/*-
 * InformeRevelacionesControlador.java
 *
 * 1.0
 * 
 * 14/12/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.contabilidad.enums.InformeRevelacionesControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
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

import org.primefaces.event.SelectEvent;

/**
 * Formulario que permite imprimir un informe de las revelaciones.
 *
 * @version 1.0, 14/12/2018
 * @author jreina
 */
@ManagedBean
@ViewScoped
public class InformeRevelacionesControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>

    private String anioInicial;
    private String mesInicial;
    private String cuentaInicial;
    private String cuentaFinal;
    private String plantilla;
    private String mesFinal;

    private String codPlantilla;
    private String nombrePlantilla;
    private Date fechaPlantilla;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>

    private List<Registro> listaAnioInicial;
    private List<Registro> listaMesInicial;
    private List<Registro> listaMesFinal;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;
    private RegistroDataModelImpl listaPlantilla;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de InformeRevelacionesControlador
     */
    public InformeRevelacionesControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.INFORME_REVELACIONES_CONTROLADOR
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
        cargarListaAnioInicial();
        cargarListaMesInicial();
        cargarListaMesFinal();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
        cargarListaPlantilla();
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
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAnioInicial
     *
     */
    public void cargarListaAnioInicial() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaAnioInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformeRevelacionesControladorUrlEnum.URL4621
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(SubdetallecomprobantecntsControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listaMesInicial
     *
     */
    public void cargarListaMesInicial() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anioInicial);

            listaMesInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformeRevelacionesControladorUrlEnum.URL7451
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(SubdetallecomprobantecntsControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listaMesFinal
     *
     */
    public void cargarListaMesFinal() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anioInicial);
            param.put(GeneralParameterEnum.NUMERO.getName(), mesInicial);

            listaMesFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformeRevelacionesControladorUrlEnum.URL3521
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(SubdetallecomprobantecntsControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaCuentaInicial
     *
     */
    public void cargarListaCuentaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InformeRevelacionesControladorUrlEnum.URL6428
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anioInicial);

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaCuentaFinal
     *
     */
    public void cargarListaCuentaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InformeRevelacionesControladorUrlEnum.URL7684
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anioInicial);
        param.put("CODIGOINICIAL", cuentaInicial);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaPlantilla
     *
     */
    public void cargarListaPlantilla() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InformeRevelacionesControladorUrlEnum.URL3785
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.TIPO.getName(), 59);

        listaPlantilla = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Imprimir en la vista
     *
     */
    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>

        Map<String, Object> param = new HashMap<>();
        param.put("codigoPlantilla", codPlantilla);
        param.put("fechaPlantilla",
                        SysmanFunciones.formatearFecha(
                                        fechaPlantilla));
        param.put("nombreDocDescarga", nombrePlantilla);

        HashMap<String, String> variablesConsultaW = new HashMap<>();
        variablesConsultaW.put("s$compania$s", compania);
        variablesConsultaW.put("s$anio$s", anioInicial);
        variablesConsultaW.put("s$mesInicial$s", mesInicial);
        variablesConsultaW.put("s$mesFinal$s", mesFinal);
        variablesConsultaW.put("s$cuentaInicial$s", cuentaInicial);
        variablesConsultaW.put("s$cuentaFinal$s", cuentaFinal);

        SessionUtil.setSessionVar("variablesConsultaWord",
                        variablesConsultaW);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(param);

        SessionUtil.redireccionarForma(direccionador,
                        SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>

    public void cambiarAnioInicial() {
        cargarListaMesInicial();
        cargarListaMesFinal();
        cargarListaCuentaInicial();
        cargarListaCuentaInicial();
    }

    public void cambiarMesInicial() {
        cargarListaMesFinal();
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        cargarListaCuentaFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaPlantilla
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaPlantilla(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codPlantilla = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()),
                                        "")
                        .toString();
        plantilla = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()),
                                        "")
                        .toString();
        nombrePlantilla = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.NOMBRE
                                                        .getName()),
                                        "")
                        .toString();
        fechaPlantilla = (Date) registroAux.getCampos().get("FECHA");
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable anioInicial
     * 
     * @return anioInicial
     */
    public String getAnioInicial() {
        return anioInicial;
    }

    /**
     * Asigna la variable anioInicial
     * 
     * @param anioInicial
     * Variable a asignar en anioInicial
     */
    public void setAnioInicial(String anioInicial) {
        this.anioInicial = anioInicial;
    }

    /**
     * Retorna la variable mesInicial
     * 
     * @return mesInicial
     */
    public String getMesInicial() {
        return mesInicial;
    }

    /**
     * Asigna la variable mesInicial
     * 
     * @param mesInicial
     * Variable a asignar en mesInicial
     */
    public void setMesInicial(String mesInicial) {
        this.mesInicial = mesInicial;
    }

    /**
     * Retorna la variable cuentaInicial
     * 
     * @return cuentaInicial
     */
    public String getCuentaInicial() {
        return cuentaInicial;
    }

    /**
     * Asigna la variable cuentaInicial
     * 
     * @param cuentaInicial
     * Variable a asignar en cuentaInicial
     */
    public void setCuentaInicial(String cuentaInicial) {
        this.cuentaInicial = cuentaInicial;
    }

    /**
     * Retorna la variable cuentaFinal
     * 
     * @return cuentaFinal
     */
    public String getCuentaFinal() {
        return cuentaFinal;
    }

    /**
     * Asigna la variable cuentaFinal
     * 
     * @param cuentaFinal
     * Variable a asignar en cuentaFinal
     */
    public void setCuentaFinal(String cuentaFinal) {
        this.cuentaFinal = cuentaFinal;
    }

    /**
     * Retorna la variable plantilla
     * 
     * @return plantilla
     */
    public String getPlantilla() {
        return plantilla;
    }

    /**
     * Asigna la variable plantilla
     * 
     * @param plantilla
     * Variable a asignar en plantilla
     */
    public void setPlantilla(String plantilla) {
        this.plantilla = plantilla;
    }

    /**
     * Retorna la variable mesFinal
     * 
     * @return mesFinal
     */
    public String getMesFinal() {
        return mesFinal;
    }

    /**
     * Asigna la variable mesFinal
     * 
     * @param mesFinal
     * Variable a asignar en mesFinal
     */
    public void setMesFinal(String mesFinal) {
        this.mesFinal = mesFinal;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaAnioInicial
     * 
     * @return listaAnioInicial
     */
    public List<Registro> getListaAnioInicial() {
        return listaAnioInicial;
    }

    /**
     * Asigna la lista listaAnioInicial
     * 
     * @param listaAnioInicial
     * Variable a asignar en listaAnioInicial
     */
    public void setListaAnioInicial(List<Registro> listaAnioInicial) {
        this.listaAnioInicial = listaAnioInicial;
    }

    /**
     * Retorna la lista listaMesInicial
     * 
     * @return listaMesInicial
     */
    public List<Registro> getListaMesInicial() {
        return listaMesInicial;
    }

    /**
     * Asigna la lista listaMesInicial
     * 
     * @param listaMesInicial
     * Variable a asignar en listaMesInicial
     */
    public void setListaMesInicial(List<Registro> listaMesInicial) {
        this.listaMesInicial = listaMesInicial;
    }

    /**
     * Retorna la lista listaMesFinal
     * 
     * @return listaMesFinal
     */
    public List<Registro> getListaMesFinal() {
        return listaMesFinal;
    }

    /**
     * Asigna la lista listaMesFinal
     * 
     * @param listaMesFinal
     * Variable a asignar en listaMesFinal
     */
    public void setListaMesFinal(List<Registro> listaMesFinal) {
        this.listaMesFinal = listaMesFinal;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCuentaInicial
     * 
     * @return listaCuentaInicial
     */
    public RegistroDataModelImpl getListaCuentaInicial() {
        return listaCuentaInicial;
    }

    /**
     * Asigna la lista listaCuentaInicial
     * 
     * @param listaCuentaInicial
     * Variable a asignar en listaCuentaInicial
     */
    public void setListaCuentaInicial(
        RegistroDataModelImpl listaCuentaInicial) {
        this.listaCuentaInicial = listaCuentaInicial;
    }

    /**
     * Retorna la lista listaCuentaFinal
     * 
     * @return listaCuentaFinal
     */
    public RegistroDataModelImpl getListaCuentaFinal() {
        return listaCuentaFinal;
    }

    /**
     * Asigna la lista listaCuentaFinal
     * 
     * @param listaCuentaFinal
     * Variable a asignar en listaCuentaFinal
     */
    public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal) {
        this.listaCuentaFinal = listaCuentaFinal;
    }

    /**
     * Retorna la lista listaPlantilla
     * 
     * @return listaPlantilla
     */
    public RegistroDataModelImpl getListaPlantilla() {
        return listaPlantilla;
    }

    /**
     * Asigna la lista listaPlantilla
     * 
     * @param listaPlantilla
     * Variable a asignar en listaPlantilla
     */
    public void setListaPlantilla(RegistroDataModelImpl listaPlantilla) {
        this.listaPlantilla = listaPlantilla;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
